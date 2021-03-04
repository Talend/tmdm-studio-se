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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

public class XSDDeleteIdentityConstraintAction extends UndoAction {

    private static Log log = LogFactory.getLog(XSDDeleteIdentityConstraintAction.class);

    private XSDIdentityConstraintDefinition xsdIdenty = null;

    public XSDDeleteIdentityConstraintAction(DataModelMainPage page) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.DELETE_OBJ.getPath()));
        setText(Messages.XSDDeleteXX_Text);
        setToolTipText(Messages.XSDDeleteXX_ActionTip);
    }

    public void run(Object toDel) {
        if (!(toDel instanceof XSDIdentityConstraintDefinition)) {
            return;
        }
        xsdIdenty = (XSDIdentityConstraintDefinition) toDel;
        run();
    }

    public IStatus doAction() {
        try {

            // xsdIdenty is to support the multiple delete action on key press,
            // which each delete action on identity must be explicit passed a xsd key to
            // delete
            XSDIdentityConstraintDefinition constraint = xsdIdenty;
            XSDElementDeclaration decl = null;
            if (constraint != null) {
                decl = (XSDElementDeclaration) constraint.getContainer();
                if (decl == null)
                    return Status.CANCEL_STATUS;
            }

            if (decl == null) {
                IStructuredSelection selection = (IStructuredSelection) page.getTreeViewer().getSelection();
                constraint = (XSDIdentityConstraintDefinition) selection.getFirstElement();
                decl = (XSDElementDeclaration) constraint.getContainer();
            }
            /*
             * REMOVE so that simple elements can be made if (
             * (constraint.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL)) &&
             * (decl.getContainer().equals(decl.getSchema())) ) { MessageDialog.openError(
             * this.page.getSite().getShell(), "Error", "Entities must have an unique key" ); return; }
             */

            decl.getIdentityConstraintDefinitions().remove(constraint);
            decl.updateElement();
            xsdIdenty = null;
            page.refresh();
            page.markDirty();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.XSDDeleteXX_ErrorMsg, e.getLocalizedMessage()));
            return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }

    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

    public void setXSDTODel(XSDIdentityConstraintDefinition elem) {
        xsdIdenty = elem;
    }

}
