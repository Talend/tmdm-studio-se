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
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;


/**
 * created by liusongbo on Oct 20, 2014
 */
public class ConfirmFireEventWithInputDialog extends CustomInputDialog {

    private FireEventComposite fireEventComp;

    private boolean isFireDeletingEvent;

    private String source;

    private boolean isInvokeBeforeDelting;


    public ConfirmFireEventWithInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue,
            IInputValidator validator) {
        super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
    }

    @Override
    protected void createInputWidget(Composite composite) {
        super.createInputWidget(composite);

        fireEventComp = new FireEventComposite(composite, SWT.NONE);
        GridData layoutData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL
                | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
        layoutData.horizontalIndent = 0;
        fireEventComp.setLayoutData(layoutData);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            isFireDeletingEvent = fireEventComp.isFireEvent();
            source = fireEventComp.getSource();
            isInvokeBeforeDelting = fireEventComp.isInvokeBeforeProcess();
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
