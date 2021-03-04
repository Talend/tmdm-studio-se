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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.XSDXPathVariety;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

import com.amalto.workbench.dialogs.SelectFieldDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

public class XSDNewXPathAction extends XSDAbstractNewXPathAction {

    private static Log log = LogFactory.getLog(XSDNewXPathAction.class);

    private XSDIdentityConstraintDefinition icd = null;

    public XSDNewXPathAction(DataModelMainPage page) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.ADD_NEWXPATH.getPath()));
        setText(Messages.XSDNewXPathAction_Text);
        setToolTipText(Messages.XSDNewXPathAction_ActionTip);
    }

    @Override
    public IStatus doAction() {
        try {
            int index = 0;
            IStructuredSelection selection = (IStructuredSelection) page.getTreeViewer().getSelection();
            if (selection.getFirstElement() instanceof XSDIdentityConstraintDefinition) {
                icd = (XSDIdentityConstraintDefinition) selection.getFirstElement();
            } else if (selection.getFirstElement() instanceof XSDXPathDefinition) {
                XSDXPathDefinition xpath = (XSDXPathDefinition) selection.getFirstElement();
                icd = (XSDIdentityConstraintDefinition) xpath.getContainer();
                if (xpath.getVariety().equals(XSDXPathVariety.FIELD_LITERAL))
                    index = icd.getFields().indexOf(xpath) + 1;
                else
                    index = 0;
            } else {
                MessageDialog.openError(this.page.getSite().getShell(), Messages._Error, Messages.XSDNewXPathAction_Huhhh
                        + selection.getFirstElement().getClass().getName());
                return Status.CANCEL_STATUS;
            }

            // InputDialog id = new InputDialog(
            // page.getSite().getShell(),
            // "New XPath",
            // "Enter a new XPath to the field",
            // null,
            // new IInputValidator() {
            // public String isValid(String newText) {
            // if ((newText==null) || "".equals(newText)) return "The XPath cannot be empty";
            // return null;
            // };
            // }
            // );

            List<String> childNames = Util.getChildElementNames("", (XSDElementDeclaration) icd.getContainer()); //$NON-NLS-1$
            // filter the non top level fields
            List<String> topChilds = new ArrayList<String>();
            for (String child : childNames) {
                if (child.indexOf('/') == -1) {
                    topChilds.add(child);
                }
            }
            // forbid to add already exists field
            EList<XSDXPathDefinition> fields = icd.getFields();
            for (XSDXPathDefinition fd : fields) {
                if (topChilds.contains(fd.getValue()))
                    topChilds.remove(fd.getValue());
            }

            SelectFieldDialog id = new SelectFieldDialog(page.getSite().getShell(), Messages.XSDNewXPathAction_SelectOnField, topChilds, null);
            id.create();
            id.setBlockOnOpen(true);
            int ret = id.open();
            if (ret == Dialog.CANCEL) {
                return Status.CANCEL_STATUS;
            }
            String field = id.getField();
            if (field.length() == 0)
                return Status.CANCEL_STATUS;

            XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();

            XSDXPathDefinition xpath = factory.createXSDXPathDefinition();
            xpath.setValue(field);
            xpath.setVariety(XSDXPathVariety.FIELD_LITERAL);

            icd.getFields().add(index, xpath);
            icd.updateElement();

            updateElementForAddedfield(icd, field);

            page.refresh();
            page.getTreeViewer().setSelection(new StructuredSelection(xpath), true);
            page.markDirty();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.ErrorMsg, e.getLocalizedMessage()));
            return Status.CANCEL_STATUS;
        }

        return Status.OK_STATUS;
    }

    @Override
    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

}
