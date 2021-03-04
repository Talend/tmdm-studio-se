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

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPartSite;
import org.talend.mdm.repository.i18n.Messages;

public class RenameViewDialog2 extends RenameViewDialog {

    private Text entityText;

    /**
     * Create the dialog.
     * @param parentShell
     */
    public RenameViewDialog2(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue,
            IInputValidator validator, IWorkbenchPartSite site) {
        super(parentShell, dialogTitle, dialogMessage, initialValue, validator , site);
    }

    @Override
    protected void createMainPart(Composite container) {
        GridLayout gridLayout = (GridLayout) container.getLayout();
        gridLayout.numColumns = 3;

        Label label2 = new Label(container, SWT.NONE);
        label2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        label2.setText(Messages.RenameViewDialog2_Name);
        entityText = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.WRAP);
        GridData gd_text = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd_text.widthHint = 400;
        entityText.setLayoutData(gd_text);
        new Label(container, SWT.NONE);

        final Label internalLabel = new Label(container, SWT.NONE | SWT.WRAP);
        internalLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3,1));
        internalLabel.setText(Messages.RenameViewDialog2_InternalName);
        internalLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));

        if(value != null) {
            entityText.setText(value);
            internalLabel.setText(Messages.bind(Messages.RenameViewDialog2_InternalNameX, value));
        }

        entityText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                updateOkButtonState();

                internalLabel.setText(Messages.bind(Messages.RenameViewDialog2_InternalNameX, entityText.getText()));
            }
        });
    }

    private void updateOkButtonState() {
        if(okBtn != null) {
            if(validInput())
                okBtn.setEnabled(true);
            else {
                okBtn.setEnabled(false);
            }
        }
    }

    protected boolean validInput() {
        String entityName = entityText.getText().trim();

        if(entityName.equals(value))
            return false;

        if(validator != null)
        {
            String validMsg = validator.isValid(entityName);
            if(validMsg != null) {
                errorMessageText.setText(validMsg);
                return false;
            }

            errorMessageText.setText(""); //$NON-NLS-1$
        }

        return true;
    }

    @Override
    protected void saveDatas() {
        value = entityText.getText();
    }
}
