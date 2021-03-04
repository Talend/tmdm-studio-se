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
package org.talend.mdm.repository.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class FileInputDialog extends Dialog {

    private final String dialogTitle;

    private final String message;

    private final IInputValidator validator;

    private Text text;

    private Text errorMessageText;

    private String errorMessage;

    private Button selFileBun;

    private String[] fileExtensions;

    private Button okButton;

    private String filePath = ""; //$NON-NLS-1$

    public String getFilePath() {
        return this.filePath;
    }

    /**
     * DOC hbhong FileInputDialog constructor comment.
     *
     * @param parentShell
     * @param dialogTitle
     * @param dialogMessage
     * @param initialValue
     * @param validator
     */
    public FileInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, String[] fileExtensions,
            IInputValidator validator) {
        super(parentShell);
        this.dialogTitle = dialogTitle;
        this.message = dialogMessage;
        this.validator = validator;
        this.fileExtensions = fileExtensions;
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (dialogTitle != null) {
            shell.setText(dialogTitle);
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        // create composite
        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) composite.getLayout();
        gridLayout.numColumns = 2;
        // create message
        if (message != null) {
            Label label = new Label(composite, SWT.WRAP);
            label.setText(message);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                    | GridData.VERTICAL_ALIGN_CENTER);
            data.horizontalSpan = 2;
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }
        text = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
        text.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        text.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                validateInput();
            }
        });

        selFileBun = new Button(composite, SWT.NONE);
        selFileBun.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog fileDialog = new FileDialog(getShell());
                fileDialog.setFilterExtensions(fileExtensions);
                String filePath = fileDialog.open();
                if (filePath != null) {
                    text.setText(filePath);
                }
            }
        });
        selFileBun.setText("..."); //$NON-NLS-1$
        errorMessageText = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
        GridData gd_errorMessageText = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
        gd_errorMessageText.horizontalSpan = 2;
        errorMessageText.setLayoutData(gd_errorMessageText);
        errorMessageText.setBackground(errorMessageText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        // Set the error message text

        applyDialogFont(composite);
        return composite;
    }

    protected void validateInput() {
        String errorMessage = null;
        if (validator != null) {
            errorMessage = validator.isValid(text.getText());
        }

        setErrorMessage(errorMessage);
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            filePath = text.getText();
        } else {
            filePath = null;
        }
        super.buttonPressed(buttonId);
    }

    protected void createButtonsForButtonBar(Composite parent) {
        // create OK and Cancel buttons by default
        okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        okButton.setEnabled(false);

    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        if (errorMessageText != null && !errorMessageText.isDisposed()) {
            errorMessageText.setText(errorMessage == null ? " \n " : errorMessage); //$NON-NLS-1$
            // Disable the error message text control if there is no error, or
            // no error text (empty or whitespace only). Hide it also to avoid
            // color change.
            boolean hasError = errorMessage != null && (StringConverter.removeWhiteSpaces(errorMessage)).length() > 0;
            errorMessageText.setEnabled(hasError);
            errorMessageText.setVisible(hasError);
            errorMessageText.getParent().update();

            Control button = getButton(IDialogConstants.OK_ID);
            if (button != null) {
                button.setEnabled(errorMessage == null);
            }
        }
    }
}
