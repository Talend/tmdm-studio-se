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
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.XSDXPathVariety;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

public class XSDDeleteXPathAction extends UndoAction {

    private static Log log = LogFactory.getLog(XSDDeleteXPathAction.class);

    private XSDXPathDefinition xsdPath = null;

    public XSDDeleteXPathAction(DataModelMainPage page) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.DELETE_OBJ.getPath()));
        setText(Messages.XSDDeleteXPathAction_DelField);
        setToolTipText(Messages.XSDDeleteXPathAction_DelAField);
    }

    public void run(Object toDel) {
        if (!(toDel instanceof XSDXPathDefinition)) {
            return;
        }
        xsdPath = (XSDXPathDefinition) toDel;
        run();
    }

    public IStatus doAction() {
        try {

            // xsdPath is to support the multiple delete action on key press,
            // which each delete action on xpath must be explicit passed a xsd path to
            // delete
            XSDXPathDefinition xpath = xsdPath;
            if (xpath == null) {
                ISelection selection = page.getTreeViewer().getSelection();
                xpath = (XSDXPathDefinition) ((IStructuredSelection) selection).getFirstElement();
            }
            XSDIdentityConstraintDefinition icd = (XSDIdentityConstraintDefinition) xpath.getContainer();
            if (icd == null)
                return Status.CANCEL_STATUS;

            if (xpath.getVariety().equals(XSDXPathVariety.SELECTOR_LITERAL)) {
                MessageDialog.openError(page.getSite().getShell(), Messages._Error, Messages.XSDDeleteXPathAction_SelectorCannotDel);
                return Status.CANCEL_STATUS;
            }

            if (icd.getFields().size() == 1) {
                MessageDialog.openError(page.getSite().getShell(), Messages._Error, Messages.XSDDeleteXPathAction_KeyMustContainOne);
                return Status.CANCEL_STATUS;
            }

            icd.getFields().remove(xpath);
            icd.updateElement();
            xsdPath = null;
            page.refresh();
            page.markDirty();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.XSDDeleteXPathAction_ErrorRemoveAField, e.getLocalizedMessage()));

            return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }

    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

    public void setXSDTODel(XSDXPathDefinition elem) {
        xsdPath = elem;
    }

}
