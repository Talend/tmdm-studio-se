// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.ui.wizards.process;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.mdm.repository.i18n.Messages;

import com.amalto.workbench.dialogs.AnnotationLanguageLabelsDialog;
import com.amalto.workbench.widgets.MultilingualDescParser;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class ConfigReturnMessagePage extends WizardPage {

    public static final String PAGE_ID = "org.talend.mdm.repository.ui.wizards.process.ConfigReturnMessagePage"; //$NON-NLS-1$

    private Text messageText;

    String[] messageParams = new String[2];

    public String[] getMessageParams() {
        return this.messageParams;
    }

    private Combo typeCombo;

    /**
     * Create the wizard.
     */
    public ConfigReturnMessagePage() {
        super(PAGE_ID);
        setTitle(Messages.NewProcessWizard_title);
        setDescription(Messages.ConfigReturnMessagePage_description);
    }

    /**
     * Create contents of the wizard.
     * 
     * @param parent
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
        container.setLayout(new GridLayout(3, false));

        Label lblNewLabel = new Label(container, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblNewLabel.setText(Messages.ConfigReturnMessagePage_type);

        typeCombo = new Combo(container, SWT.READ_ONLY);

        typeCombo.setItems(new String[] { "error", "info" }); //$NON-NLS-1$ //$NON-NLS-2$ 
        typeCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        typeCombo.select(0);
        typeCombo.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                getWizard().getContainer().updateButtons();
            }
        });
        Label lblMessage = new Label(container, SWT.NONE);
        lblMessage.setText(Messages.ConfigReturnMessagePage_message);

        Button btnNewButton = new Button(container, SWT.NONE);
        btnNewButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Map<String, String> descriptionsMap = new LinkedHashMap<String, String>();
                MultilingualDescParser.parseMultiLanguageString(messageText.getText().trim(), descriptionsMap);
                AnnotationLanguageLabelsDialog dialog = new AnnotationLanguageLabelsDialog(descriptionsMap, null, getShell(),
                        Messages.ConfigReturnMessagePage_setMessage);
                if (dialog.open() == IDialogConstants.OK_ID) {
                    StringBuffer output = new StringBuffer();
                    for (Map.Entry<String, String> m : dialog.getDescriptionsMap().entrySet()) {
                        output.append("[").append(m.getKey().toUpperCase()).append(":").append(m.getValue()).append("]");//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ 
                    }
                    messageText.setText(output.toString());
                }
            }
        });
        btnNewButton.setText(Messages.ConfigReturnMessagePage_selectMessage);

        messageText = new Text(container, SWT.BORDER);
        messageText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                getWizard().getContainer().updateButtons();
            }
        });
        messageText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        setPageComplete(false);
    }

    private boolean validateMessage() {

        boolean result = false;
        messageParams[0] = typeCombo.getText().trim();
        messageParams[1] = messageText.getText().trim();
        if (messageParams[1].length() == 0) {
            setErrorMessage(Messages.ConfigReturnMessagePage_pleaseInputMessage);
        } else {
            setErrorMessage(null);
            result = true;
        }
        return result;
    }

    @Override
    public boolean isPageComplete() {
        return validateMessage();
    }

}
