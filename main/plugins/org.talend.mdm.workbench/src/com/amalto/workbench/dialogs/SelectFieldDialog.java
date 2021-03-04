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

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.amalto.workbench.i18n.Messages;

public class SelectFieldDialog extends Dialog {

    String title;

    private CCombo fieldNameCombo;

    private List<String> fields;

    private String field;

    private String defualtField;

    public SelectFieldDialog(Shell parentShell, String title, List<String> fields, String defualtField) {
        super(parentShell);
        this.title = title;
        this.fields = fields;
        this.defualtField = defualtField;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        parent.getShell().setText(this.title);

        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout layout = (GridLayout) composite.getLayout();
        layout.numColumns = 2;

        Label serverLabel = new Label(composite, SWT.NONE);
        serverLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        serverLabel.setText(Messages.SelectFieldDialog_FieldName);

        fieldNameCombo = new CCombo(composite, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
        fieldNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        fieldNameCombo.setItems(fields.toArray(new String[fields.size()]));
        if (fields.size() > 0) {
            if (defualtField == null)
                fieldNameCombo.select(0);
            else
                fieldNameCombo.setText(defualtField);
        }
        return composite;
    }

    public String getField() {
        return field;
    }

    @Override
    protected void okPressed() {

        if (!validateInput())
            return;

        field = fieldNameCombo.getText().trim();
        super.okPressed();
    }

    private boolean validateInput() {

        if ("".equals(fieldNameCombo.getText().trim())) {//$NON-NLS-1$
            MessageDialog.openError(getShell(), Messages._Error, Messages.SelectFieldDialog_FileNameCannotbeEmpty);
            return false;
        }

        if (Pattern.compile("^\\s+\\w+\\s*").matcher(fieldNameCombo.getText()).matches()//$NON-NLS-1$
                || fieldNameCombo.getText().trim().replaceAll("\\s", "").length() != fieldNameCombo.getText().trim().length()) {//$NON-NLS-1$//$NON-NLS-2$

            MessageDialog.openError(getShell(), Messages._Error, Messages.SelectFieldDialog_FileNameCannotContainEmpty);
            return false;
        }

        return true;
    }
}
