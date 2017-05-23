// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintCategory;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.inputvalidator.NewXSDIndentityConstraintValidator;

public class IdentityConstraintInputDialog extends Dialog {

    private CLabel typeCLabel = null;

    private CCombo fieldNameCombo = null;

    private String keyName = "";//$NON-NLS-1$

    private String fieldName = "";//$NON-NLS-1$

    private XSDIdentityConstraintCategory type;

    private List<String> deElements;

    // private SelectionListener caller = null;
    private String title = "";//$NON-NLS-1$

    private Text keyNameText;

    private XSDElementDeclaration keyContainer;

    private NewXSDIndentityConstraintValidator inputValidator;

    /**
     * @param parentShell
     */
    // public IdentityConstraintInputDialog(SelectionListener caller, Shell parentShell, String title,List<String>
    // deElements, String keyName) {
    // this(caller,parentShell,title,keyName,XSDIdentityConstraintCategory.KEY_LITERAL,deElements);
    // }
    public IdentityConstraintInputDialog(XSDElementDeclaration keyContainer, Shell parentShell, String title,
            List<String> deElements, String keyName) {
        this(keyContainer, parentShell, title, keyName, XSDIdentityConstraintCategory.KEY_LITERAL, deElements);
    }

    /**
     * @param parentShell
     */
    // public IdentityConstraintInputDialog(
    // SelectionListener caller,
    // Shell parentShell,
    // String title,
    // String keyName,
    // XSDIdentityConstraintCategory type,
    // List<String> deElements
    // ) {
    // super(parentShell);
    // // this.caller = caller;
    // this.title = title;
    // this.keyName = keyName;
    // this.type = type;
    // this.deElements = deElements;
    // }

    public IdentityConstraintInputDialog(XSDElementDeclaration keyContainer, Shell parentShell, String title, String keyName,
            XSDIdentityConstraintCategory type, List<String> deElements) {
        super(parentShell);
        // this.caller = caller;
        this.keyContainer = keyContainer;
        this.title = title;
        this.keyName = keyName;
        this.type = type;
        this.deElements = deElements;

        inputValidator = new NewXSDIndentityConstraintValidator(keyContainer.getSchema());
    }

    protected Control createDialogArea(Composite parent) {
        // Should not really be here but well,....
        parent.getShell().setText(this.title);

        Composite composite = (Composite) super.createDialogArea(parent);

        GridLayout layout = (GridLayout) composite.getLayout();
        layout.numColumns = 2;
        // layout.verticalSpacing = 10;
        Label Label = new Label(composite, SWT.NONE);
        Label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        Label.setText(Messages.IdentityConstraintInputDialog_KeyName);
        keyNameText = new Text(composite, SWT.BORDER);
        keyNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        keyNameText.setText(keyName);

        Label serverLabel = new Label(composite, SWT.NONE);
        serverLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        serverLabel.setText(Messages.IdentityConstraintInputDialog_FieldName);

        fieldNameCombo = new CCombo(composite, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
        fieldNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        fieldNameCombo.setItems(deElements.toArray(new String[deElements.size()]));
        if (deElements.size() > 0)
            fieldNameCombo.select(0);
        Label typeLabel = new Label(composite, SWT.NONE);
        typeLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        typeLabel.setText(Messages.IdentityConstraintInputDialog_Type);

        typeCLabel = new CLabel(composite, SWT.BORDER);
        typeCLabel.setText("Unique Key");//$NON-NLS-1$
        typeCLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        keyNameText.setEditable(false);

        return composite;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        // super.createButtonsForButtonBar(parent);
        // getButton(IDialogConstants.OK_ID).addSelectionListener(this.caller);
        // /*
        // createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
        // true);
        // createButton(parent, IDialogConstants.CANCEL_ID,
        // IDialogConstants.CANCEL_LABEL, false);
        // */

        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

    }

    protected void okPressed() {
        // keyName = keyNameText.getText();
        fieldName = fieldNameCombo.getText().trim();
        keyName = keyNameText.getText().trim();
        if ((keyName == null) || ("".equals(keyName))) {//$NON-NLS-1$
            MessageDialog.openError(this.getShell(), Messages._Error, Messages.IdentityConstraintInputDialog_KeyNameCannotbeEmpty);
            setReturnCode(-1);
            keyNameText.setFocus();
            // fieldNameCombo.setFocus();
            return;
        }

        if (keyName.replaceAll("\\s", "").length() != keyName.length()) {//$NON-NLS-1$//$NON-NLS-2$
            MessageDialog.openError(this.getShell(), Messages._Error, Messages.IdentityConstraintInputDialog_KeyNameCannotContainEmpty);
            setReturnCode(-1);
            keyNameText.setFocus();
            return;
        }

        if (fieldName.replaceAll("\\s", "").length() != fieldName.length()) {//$NON-NLS-1$//$NON-NLS-2$
            MessageDialog.openError(this.getShell(), Messages._Error, Messages.IdentityConstraintInputDialog_FieldNameCannotbeEmpty);
            setReturnCode(-1);
            fieldNameCombo.setFocus();
            return;
        }

        // if ((fieldName==null) || ("".equals(fieldName))) {
        // MessageDialog.openError(
        // this.getShell(),
        // "Error", "The Field Name cannot be empty"
        // );
        // setReturnCode(-1);
        // //keyNameText.setFocus();
        // fieldNameCombo.setFocus();
        // return;
        // }
        // String selection = (typeCombo.getText()).toUpperCase();
        // if (selection.indexOf("UNIQUE")>=0)
        // type = XSDIdentityConstraintCategory.UNIQUE_LITERAL;
        // else if (selection.indexOf("FOREIGN")>=0)
        // type = XSDIdentityConstraintCategory.KEYREF_LITERAL;
        // else
        // type = XSDIdentityConstraintCategory.KEY_LITERAL;

        type = getTypeFromUI();

        // setReturnCode(OK);
        // //no close let Action Handler handle it

        super.okPressed();
    }

    private XSDIdentityConstraintCategory getTypeFromUI() {
        return XSDIdentityConstraintCategory.UNIQUE_LITERAL;
    }

    public String getKeyName() {
        return keyName;
    }

    public XSDIdentityConstraintCategory getType() {
        return type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setInputValidator(NewXSDIndentityConstraintValidator inputValidator) {
        this.inputValidator = inputValidator;
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (!validInput())
                return;
        }
        super.buttonPressed(buttonId);
    }

    private boolean validInput() {

        if ("".equals(keyNameText.getText().trim())) {//$NON-NLS-1$
            MessageDialog.openError(this.getShell(), Messages._Error, Messages.IdentityConstraintInputDialog_KeyNameCannotbeEmpty);
            keyNameText.setFocus();
            return false;
        }

        if (inputValidator != null) {
            String errMsg = inputValidator.isValid(keyNameText.getText().trim(), getTypeFromUI(), this.keyContainer);

            if (errMsg != null) {
                MessageDialog.openError(this.getShell(), Messages._Error, errMsg);
                return false;
            }
        }

        return true;
    }
}
