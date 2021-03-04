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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

/**
 * created by liusongbo on 2013-9-30
 */
public class XSDDeleteAttributeAction extends UndoAction {

    private static Log log = LogFactory.getLog(XSDDeleteAttributeAction.class);

    private XSDAttributeUse attributeUse;

    private XSDAttributeDeclaration attributeDeclaration;

    public XSDDeleteAttributeAction(DataModelMainPage page) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.DELETE_OBJ.getPath()));
        setText(Messages.XSDDeleteAttributeAction_label);
        setToolTipText(Messages.XSDDeleteAttributeAction_label);
    }

    @Override
    protected IStatus doAction() {
        try {
            XSDAttributeUse attriUse = attributeUse;
            XSDAttributeDeclaration attriDec = attributeDeclaration;
            if (attriUse == null || attriDec == null) {
                ISelection selection = page.getTreeViewer().getSelection();
                Object firstElement = ((IStructuredSelection) selection).getFirstElement();
                if (firstElement instanceof XSDAttributeUse) {
                    attriUse = (XSDAttributeUse) firstElement;
                } else if (firstElement instanceof XSDAttributeDeclaration) {
                    attriDec = (XSDAttributeDeclaration) firstElement;
                }
            }

            if (attriUse != null) {
                if (attriUse.getContainer() == null) {
                    return Status.CANCEL_STATUS;
                }

                XSDConcreteComponent container = attriUse.getContainer();
                if (container instanceof XSDComplexTypeDefinition) {
                    XSDComplexTypeDefinition cType = (XSDComplexTypeDefinition) container;
                    cType.getAttributeUses().remove(attriUse);
                    cType.getAttributeContents().remove(attriUse);
                    cType.updateElement();
                }
            } else if (attriDec != null) {
                if(attriDec.getContainer() == null){
                    return Status.CANCEL_STATUS;
                }

                XSDConcreteComponent container = attriDec.getContainer();
                if(container instanceof XSDSchema) {
                    XSDSchema xsdschema = (XSDSchema) container;
                    xsdschema.getContents().remove(attriDec);
                }
            }

            schema.update();

            attributeUse = null;
            attributeDeclaration = null;

            page.refresh();
            page.markDirtyWithoutCommit();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.XSDDeleteElementAction_ErrorMsg, e.getLocalizedMessage()));
            return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }

    public void setXSDAttributeUse(XSDAttributeUse attribute) {
        this.attributeUse = attribute;
    }

    public void setXSDAttribute(XSDAttributeDeclaration attribute) {
        this.attributeDeclaration = attribute;
    }
}
