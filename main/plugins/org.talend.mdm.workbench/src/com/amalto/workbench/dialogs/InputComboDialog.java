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
package com.amalto.workbench.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class InputComboDialog extends Dialog {

    private String title;

    private String message;

    private String value = "";//$NON-NLS-1$

    private int type;

    public String getValue() {
        return value;
    }

    public int getType() {
        return type;
    }

    private Button okButton;

    private Combo inputCombo;

    private Text errorMessageText;

    private String[] values;

    private String txtValue;

    private final boolean isAppendBlankItem;

    public InputComboDialog(Shell parentShell, String dialogTitle, String dialogMessage, String[] dialogValues,
            String initialValue, String txtValue) {
        this(parentShell, dialogTitle, dialogMessage, dialogValues, initialValue, txtValue, true);
    }

    public InputComboDialog(Shell parentShell, String dialogTitle, String dialogMessage, String[] dialogValues,
            String initialValue, String txtValue, boolean isAppendBlankItem) {
        super(parentShell);
        // TODO Auto-generated constructor stub
        this.message = dialogMessage;
        this.title = dialogTitle;
        this.message = dialogMessage;
        this.values = dialogValues;
        this.value = initialValue;
        this.txtValue = txtValue;
        this.isAppendBlankItem = isAppendBlankItem;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        // create composite
        Composite composite = (Composite) super.createDialogArea(parent);
        // create message
        if (message != null) {
            Label label = new Label(composite, SWT.WRAP);
            label.setText(message);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                    | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }

        inputCombo = new Combo(composite, SWT.READ_ONLY);
        inputCombo.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        inputCombo.setItems(values);
        if (isAppendBlankItem) {
            inputCombo.add("");//$NON-NLS-1$
        }
        for (String pro : values) {
            if (pro.equals(value)) {
                inputCombo.setText(pro);
                break;
            }
        }
        if (txtValue != null) {
            errorMessageText = new Text(composite, SWT.BORDER);
            errorMessageText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
            errorMessageText.setBackground(errorMessageText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            errorMessageText.setText(txtValue);
            errorMessageText.setEditable(false);
        }

        // applyDialogFont(composite);
        return composite;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            value = inputCombo.getText();
            type = inputCombo.getSelectionIndex();
        } else {
            type = -1;
            value = null;
        }
        super.buttonPressed(buttonId);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (title != null) {
            shell.setText(title);
        }
    }

    public Combo getCombo() {
        return inputCombo;
    }

    public Text getText() {
        return errorMessageText;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        // create OK and Cancel buttons by default
        okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        // do this here because setting the text will set enablement on the ok
        // button
        if (value != null) {
            inputCombo.setText(value);
        }
    }

}
