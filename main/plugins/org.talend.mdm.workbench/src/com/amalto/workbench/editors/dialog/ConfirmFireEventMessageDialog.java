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
package com.amalto.workbench.editors.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * created by liusongbo on Oct 20, 2014
 */
public class ConfirmFireEventMessageDialog extends MessageDialog {

    private FireEventComposite customArea;

    // private Group customArea;

    private boolean isFireDeletingEvent;

    private String source;

    private boolean isInvokeBeforeDelting;

    private ConfirmFireEventMessageDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage,
            int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
    }

    @Override
    protected Control createCustomArea(Composite parent) {
        customArea = new FireEventComposite(parent, SWT.NONE);
        customArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

        return customArea;
    }

    public static ConfirmFireEventMessageDialog createConfirmDialog(Shell parent, String title, String message) {
        ConfirmFireEventMessageDialog dialog = new ConfirmFireEventMessageDialog(parent, title, null, message, CONFIRM, getButtonLabels(CONFIRM),
                0);
        dialog.setShellStyle(dialog.getShellStyle() | SWT.SHEET);
        return dialog;
    }

    static String[] getButtonLabels(int kind) {
        String[] dialogButtonLabels;
        switch (kind) {
        case ERROR:
        case INFORMATION:
        case WARNING: {
            dialogButtonLabels = new String[] { IDialogConstants.OK_LABEL };
            break;
        }
        case CONFIRM: {
            dialogButtonLabels = new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL };
            break;
        }
        case QUESTION: {
            dialogButtonLabels = new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL };
            break;
        }
        case QUESTION_WITH_CANCEL: {
            dialogButtonLabels = new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL,
                    IDialogConstants.CANCEL_LABEL };
            break;
        }
        default: {
            throw new IllegalArgumentException("Illegal value for kind in MessageDialog.open()"); //$NON-NLS-1$
        }
        }
        return dialogButtonLabels;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == 0) {
            isFireDeletingEvent = customArea.isFireEvent();
            source = customArea.getSource();
            isInvokeBeforeDelting = customArea.isInvokeBeforeProcess();
        }
        super.buttonPressed(buttonId);
    }

    public boolean isFireEvent() {
        return isFireDeletingEvent;
    }

    public boolean isInvokeBeforeProcess() {
        return isInvokeBeforeDelting;
    }

    public String getSource() {
        return source;
    }

}
