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
package org.talend.mdm.repository.ui.wizards.process;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.mdm.repository.i18n.Messages;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class ConfigRedirectURLPage extends WizardPage {

    public static final String PAGE_ID = "org.talend.mdm.repository.ui.wizards.process.ConfigRedirectURLPage"; //$NON-NLS-1$

    private Text urlText;

    private Label lblNewLabel;

    private Button enableButton;

    private String url;

    public String getUrl() {
        if(enableButton.getSelection())
            return this.url;
        else
            return "";//$NON-NLS-1$
    }

    private boolean enableRedirect;

    public boolean isEnableRedirect() {
        return this.enableRedirect;
    }

    /**
     * Create the wizard.
     */
    public ConfigRedirectURLPage() {
        super(PAGE_ID);
        setTitle(Messages.NewProcessWizard_title);
        setDescription(Messages.ConfigRedirectURLPage_description);
    }

    /**
     * Create contents of the wizard.
     *
     * @param parent
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
        container.setLayout(new GridLayout(2, false));

        enableButton = new Button(container, SWT.CHECK);

        enableButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        enableButton.setText(Messages.ConfigRedirectURLPage_enableRedirect);

        lblNewLabel = new Label(container, SWT.NONE);
        GridData gd_lblNewLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        gd_lblNewLabel.horizontalIndent = 20;
        lblNewLabel.setLayoutData(gd_lblNewLabel);
        lblNewLabel.setText(Messages.ConfigRedirectURLPage_url);

        urlText = new Text(container, SWT.BORDER);
        urlText.setText("http://www.talend.com"); //$NON-NLS-1$
        urlText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                getWizard().getContainer().updateButtons();
            }
        });
        urlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        enableButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                enableURL(enableButton.getSelection());
                getWizard().getContainer().updateButtons();
            }
        });
        enableURL(false);

    }

    private boolean validateURL() {
        url = urlText.getText().trim();
        boolean result = false;
        enableRedirect = enableButton.getSelection();
        if (enableRedirect && url.length() == 0) {
            setErrorMessage(Messages.ConfigRedirectURLPage_pleaseInputUrl);
        } else {
            setErrorMessage(null);
            result = true;
        }
        return result;
    }

    private void enableURL(boolean enable) {
        lblNewLabel.setEnabled(enable);
        urlText.setEnabled(enable);
    }

    @Override
    public boolean isPageComplete() {
        return validateURL();
    }

    @Override
    public IWizardPage getNextPage() {
        //get jobtemplate page
        return getWizard().getPage("org.talend.mdm.repository.enterprise.ui.wizards.jobtemplate.CreateJobTemplateWizardPage");//$NON-NLS-1$
    }
}
