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
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDComponent;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class XSDSetAnnotationDocumentationAction extends UndoAction {

    private static Log log = LogFactory.getLog(XSDSetAnnotationDocumentationAction.class);

    public XSDSetAnnotationDocumentationAction(DataModelMainPage page) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.DOCUMENTATION.getPath()));
        setText(Messages.XSDSetXX_Text);
        setToolTipText(Messages.XSDSetXX_ActionTip);
    }

    public IStatus doAction() {
        try {

            IStructuredSelection selection = (IStructuredSelection) page.getTreeViewer().getSelection();
            XSDAnnotationsStructure struc = new XSDAnnotationsStructure((XSDComponent) selection.getFirstElement());
            if (struc.getAnnotation() == null) {
                throw new RuntimeException(Messages.bind(Messages.XSDSetXX_ExceptionInfo, selection.getFirstElement().getClass().getName()));
            }

            InputDialog id = new InputDialog(page.getSite().getShell(), Messages.XSDSetXX_DialogTitle,
                    Messages.XSDSetXX_DialogTip, struc.getDocumentation(),
                    new IInputValidator() {

                        public String isValid(String newText) {
                            return null;
                        };
                    });

            id.setBlockOnOpen(true);
            int ret = id.open();
            if (ret == Window.CANCEL) {
                return Status.CANCEL_STATUS;
            }

            struc.setDocumentation("".equals(id.getValue()) ? null : id.getValue());//$NON-NLS-1$

            if (struc.hasChanged()) {
                page.refresh();
                page.getTreeViewer().expandToLevel(selection.getFirstElement(), 2);
                page.markDirty();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.XSDSetXX_ErrorMsg, e.getLocalizedMessage()));
            return Status.CANCEL_STATUS;
        }

        return Status.OK_STATUS;
    }

    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

}
