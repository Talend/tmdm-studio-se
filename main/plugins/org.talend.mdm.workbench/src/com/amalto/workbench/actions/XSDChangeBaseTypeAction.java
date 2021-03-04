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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDConstrainingFacet;
import org.eclipse.xsd.XSDPatternFacet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

import com.amalto.workbench.dialogs.SimpleTypeInputDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.XSDUtil;

public class XSDChangeBaseTypeAction extends UndoAction implements SelectionListener {

    private static Log log = LogFactory.getLog(XSDChangeBaseTypeAction.class);

    private SimpleTypeInputDialog dialog = null;

    private String typeName = null;

    private boolean builtIn = false;

    public XSDChangeBaseTypeAction(DataModelMainPage page) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.CHANGE_TO_SIMPLE.getPath()));
        setText(Messages.XSDChangeBaseTypeAction_Text);
        setToolTipText(Messages.XSDChangeBaseTypeAction_ActionTip);
        setDescription(getToolTipText());
    }

    @Override
    public IStatus doAction() {
        try {
            IStructuredSelection selection = (IStructuredSelection) page.getTreeViewer().getSelection();
            XSDSimpleTypeDefinition typedef = (XSDSimpleTypeDefinition) selection.getFirstElement();

            // Cannot change the simple type definition of built in type
            // if (schema.getSchemaForSchemaNamespace().equals(typedef.getTargetNamespace())) return
            // Status.CANCEL_STATUS;

            // build list of custom types and built in types
            ArrayList customTypes = new ArrayList();
            for (Object element : schema.getTypeDefinitions()) {
                XSDTypeDefinition type = (XSDTypeDefinition) element;
                if (type instanceof XSDSimpleTypeDefinition) {
                    if (type.getTargetNamespace() != null
                            && !type.getTargetNamespace().equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001)
                            || type.getTargetNamespace() == null) {
                        customTypes.add(type.getName());
                    }
                }
            }
            List builtInTypes = XSDUtil.getBuiltInTypes();
            // can't change builtin's base type
            if (builtInTypes.contains(typedef.getName())) {
                return Status.CANCEL_STATUS;
            }
            dialog = new SimpleTypeInputDialog(this, page.getSite().getShell(), schema,
                    Messages.XSDChangeBaseTypeAction_DialogTitle, customTypes, builtInTypes, typedef.getBaseTypeDefinition()
                            .getName());

            dialog.setBlockOnOpen(true);
            int ret = dialog.open();
            if (ret == Window.CANCEL) {
                return Status.CANCEL_STATUS;
            }

            // backup current Base Type
            XSDTypeDefinition current = typedef.getBaseTypeDefinition();

            // set new one
            if (builtIn) {
                typedef.setBaseTypeDefinition(schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(), typeName));
            } else {
                // check if simple type definition already exists
                XSDSimpleTypeDefinition std = schema.resolveSimpleTypeDefinition(typeName);
                if (!schema.getTypeDefinitions().contains(std)) {
                    std.setBaseTypeDefinition(schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(), "string"));//$NON-NLS-1$
                    schema.getContents().add(std);
                }
                typedef.setBaseTypeDefinition(std);
            }

            // remove current facets
            typedef.getFacetContents().removeAll(typedef.getFacetContents());

            typedef.updateElement();

            if (builtIn) {
                EList<XSDConstrainingFacet> constrainFacts = typedef.getBaseTypeDefinition().getFacetContents();
                for (XSDConstrainingFacet fact : constrainFacts) {
                    if (fact instanceof XSDPatternFacet) {
                        XSDPatternFacet newFact = XSDSchemaBuildingTools.getXSDFactory().createXSDPatternFacet();
                        newFact.setLexicalValue(((XSDPatternFacet) fact).getLexicalValue());
                        typedef.getFacetContents().add(newFact);
                    }
                }
                typedef.updateElement();
            }

            // remove current if no more in use
            // if ( (current.getName()!=null) && //anonymous type
            // (!schema.getSchemaForSchemaNamespace().equals(current.getTargetNamespace()))
            // ){
            // List eut =Util.findElementsUsingType(schema, current.getTargetNamespace(), current.getName());
            // if (eut.size()==0)
            // schema.getContents().remove(current);
            // }

            page.refresh();
            page.markDirty();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.XSDChangeBaseTypeAction_ErrorMsg1, e.getLocalizedMessage()));
            return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }

    @Override
    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

    /********************************
     * Listener to input dialog
     */
    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {
        if (dialog.getReturnCode() == -1) {
            return;
        }
        typeName = dialog.getTypeName();
        builtIn = dialog.isBuiltIn();

        // if built in, check that the type actually exists
        if (builtIn) {
            boolean found = false;
            for (Object element : schema.getSchemaForSchema().getTypeDefinitions()) {
                XSDTypeDefinition type = (XSDTypeDefinition) element;
                if (type instanceof XSDSimpleTypeDefinition) {
                    if (type.getName().equals(typeName)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                        Messages.bind(Messages.XSDChangeBaseTypeAction_ErrorMsg2, typeName));
                return;
            }
        }
        dialog.close();
    }

}
