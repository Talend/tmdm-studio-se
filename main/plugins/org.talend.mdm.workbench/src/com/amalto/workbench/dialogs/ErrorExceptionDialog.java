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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ErrorExceptionDialog extends IconAndMessageDialog {

    private Image image = null;

    private String title;

    private static Image titleImage;

    private String message;

    private Text text;

    public ErrorExceptionDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage) {

        super(parentShell);
        this.title = dialogTitle;
        this.message = dialogMessage;
        this.image = getErrorImage();
    }

    protected void configureShell(Shell shell) {

        super.configureShell(shell);

        if (title != null) {
            shell.setText(title);
        }
        if (titleImage != null) {
            shell.setImage(titleImage);
        }
    }

    protected Control createDialogArea(Composite parent) {
        createMessageArea(parent);
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.widthHint = 400;
        data.horizontalAlignment = SWT.CENTER;
        composite.setLayoutData(data);
        text = new Text(composite, SWT.MULTI | SWT.WRAP);
        text.setText(message);
        text.setEditable(false);
        text.setLayoutData(data);
        return composite;
    }

    protected Control createButtonBar(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1) // this is incremented
                // by createButton
                .equalWidth(true).applyTo(composite);

        GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).span(2, 1).applyTo(composite);
        composite.setFont(parent.getFont());
        createButton(composite, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        return composite;
    }

    @Override
    public Image getImage() {
        return image;
    }

    protected int getShellStyle() {
        return super.getShellStyle() | SWT.RESIZE;
    }

    public static void openError(Shell parent, String title, String message) {
        ErrorExceptionDialog dialog = new ErrorExceptionDialog(parent, title, titleImage, message);

        dialog.open();
        return;
    }
}
