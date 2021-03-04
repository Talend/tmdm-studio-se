// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.XSDXPathVariety;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

import com.amalto.workbench.dialogs.IdentityConstraintInputDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

public class XSDNewIdentityConstraintAction extends XSDAbstractNewXPathAction { // implements SelectionListener{

    private static Log log = LogFactory.getLog(XSDNewIdentityConstraintAction.class);

    private IdentityConstraintInputDialog dialog = null;

    private String keyName;

    private String fieldName;

    private XSDIdentityConstraintCategory type;

    private XSDElementDeclaration decl = null;

    public XSDNewIdentityConstraintAction(DataModelMainPage page) {
        super(page);
        setImageDescriptor(ImageCache.getImage("icons/add_obj.gif"));//$NON-NLS-1$
        setText(Messages.XSDNewIdentityConstraintAction_AddKey);
        setToolTipText(Messages.XSDNewIdentityConstraintAction_AddANewKey);
    }

    @Override
    public IStatus doAction() {
        try {

            int index = -1;
            // EList list = schema.getIdentityConstraintDefinitions();

            // schema.getElement();
            IStructuredSelection selection = (IStructuredSelection) page.getTreeViewer().getSelection();
            List<String> childNames = new ArrayList<String>();
            if (selection.getFirstElement() instanceof XSDElementDeclaration) {
                decl = (XSDElementDeclaration) selection.getFirstElement();
                // childNames = Util.getChildElementNames(decl.getElement());
            }

            else if (selection.getFirstElement() instanceof XSDIdentityConstraintDefinition) {
                XSDIdentityConstraintDefinition selIcd = (XSDIdentityConstraintDefinition) selection.getFirstElement();
                decl = (XSDElementDeclaration) (selIcd.getContainer());

                // get position of the selected Identity Constraint in the container (Element Declaration)
                int i = 0;
                for (Iterator iter = decl.getIdentityConstraintDefinitions().iterator(); iter.hasNext();) {
                    XSDIdentityConstraintDefinition ic = (XSDIdentityConstraintDefinition) iter.next();
                    if (ic.equals(selIcd)) {
                        index = i;
                        break;
                    }
                    i++;
                }

            } else if (selection.getFirstElement() instanceof XSDParticle) {
                XSDParticle selParticle = (XSDParticle) selection.getFirstElement();
                if (!(selParticle.getTerm() instanceof XSDElementDeclaration))
                    return Status.CANCEL_STATUS;
                decl = (XSDElementDeclaration) selParticle.getTerm();
                // childNames.add(decl.getName());

            } else {
                MessageDialog.openError(this.page.getSite().getShell(), Messages._Error, Messages.bind(Messages.XSDNewIdentityConstraintAction_ErrorMsg
                        , selection.getFirstElement().getClass().getName()));
                return Status.CANCEL_STATUS;
            }
            childNames = Util.getChildElementNames("", decl); //$NON-NLS-1$
            // filter the non top level fields
            List<String> topChilds = new ArrayList<String>();
            for (String child : childNames) {
                if (child.indexOf('/') == -1) {
                    topChilds.add(child);
                }
            }
            dialog = new IdentityConstraintInputDialog(decl, page.getSite().getShell(), Messages.XSDNewIdentityConstraintAction_AddANewKey, topChilds,
                    decl.getName());
            dialog.setBlockOnOpen(true);
            int ret = dialog.open();
            if (ret == Window.CANCEL) {
                return Status.CANCEL_STATUS;
            }

            keyName = dialog.getKeyName();
            fieldName = dialog.getFieldName();
            type = dialog.getType();

            XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();

            XSDIdentityConstraintDefinition icd = factory.createXSDIdentityConstraintDefinition();
            icd.setName(keyName);
            icd.setIdentityConstraintCategory(type);
            XSDXPathDefinition selector = factory.createXSDXPathDefinition();
            selector.setVariety(XSDXPathVariety.SELECTOR_LITERAL);
            selector.setValue("."); //$NON-NLS-1$
            icd.setSelector(selector);
            XSDXPathDefinition field = factory.createXSDXPathDefinition();
            field.setVariety(XSDXPathVariety.FIELD_LITERAL);
            field.setValue("."); //$NON-NLS-1$
            // if complex content set name of first field
            if (fieldName == null || fieldName.trim().length() == 0) {
                if (decl.getTypeDefinition() instanceof XSDComplexTypeDefinition) {
                    XSDComplexTypeContent ctc = ((XSDComplexTypeDefinition) decl.getTypeDefinition()).getContent();
                    if (ctc instanceof XSDParticle) {
                        if (((XSDParticle) ctc).getTerm() instanceof XSDModelGroup) {
                            XSDModelGroup mg = (XSDModelGroup) ((XSDParticle) ctc).getTerm();
                            if (mg.getContents().size() > 0)
                                if (mg.getContents().get(0).getTerm() instanceof XSDElementDeclaration)
                                    field.setValue(((XSDElementDeclaration) (mg.getContents().get(0).getTerm()))
                                            .getName());
                        }
                    }
                }
            } else {
                field.setValue(fieldName);
            }
            icd.getFields().add(field);

            decl.getIdentityConstraintDefinitions().add(index + 1, icd);
            decl.updateElement();

            updateElementForAddedfield(icd, fieldName);

            page.refresh();
            page.getTreeViewer().setSelection(new StructuredSelection(icd), true);
            page.markDirty();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.XSDNewIdentityConstraintAction_ErrorMsg2, e.getLocalizedMessage()));
            return Status.CANCEL_STATUS;
        }

        return Status.OK_STATUS;
    }

    @Override
    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

    // /********************************
    // * Listener to input dialog
    // */
    // public void widgetDefaultSelected(SelectionEvent e) {
    // }
    //
    // public void widgetSelected(SelectionEvent e) {
    // if (dialog.getReturnCode() == -1) return; //there was a validation error
    //
    // keyName = dialog.getKeyName();
    // fieldName=dialog.getFieldName();
    // type = dialog.getType();
    //
    // //check if key does not already exist
    // EList list = schema.getIdentityConstraintDefinitions();
    // for (Iterator iter = list.iterator(); iter.hasNext(); ) {
    // XSDIdentityConstraintDefinition icd = (XSDIdentityConstraintDefinition) iter.next();
    // if (
    // (type.equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL)) &&
    // (icd.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL)) &&
    // (icd.getContainer().equals(decl))
    // ){
    // MessageDialog.openError(
    // page.getSite().getShell(),
    // "Error",
    // "The Business Element already has an unique key"
    // );
    // return;
    // }
    // if (icd.getName().equals(keyName)) {
    // MessageDialog.openError(
    // page.getSite().getShell(),
    // "Error",
    // "The Key "+keyName+" already exist"
    // );
    // return;
    // }
    // }
    //
    // dialog.close();
    // }

}
