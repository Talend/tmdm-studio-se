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
package org.talend.mdm.workbench.serverexplorer.console;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.talend.mdm.workbench.serverexplorer.i18n.Messages;

/**
 *
 * created by Karelun Huang on Mar 20, 2013 Detailled comment
 *
 */
public class MDMServerConsolePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private static final int MIN_BUFFER_SIZE = 1000;

    private Spinner freqSpinner = null;

    private Text bufferText = null;

    public void init(IWorkbench workbench) {
        // TODO:
    }

    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        return PlatformUI.getPreferenceStore();
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        doCreateContent(composite);
        initControls();
        return composite;
    }

    private void doCreateContent(Composite parent) {
        Composite composite1 = new Composite(parent, SWT.NONE);
        composite1.setLayout(new GridLayout(3, false));
        composite1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        new Label(composite1, SWT.NONE).setText(Messages.MDMServerConsolePrefsPage_RefreshLabel_1);
        freqSpinner = new Spinner(composite1, SWT.BORDER | SWT.READ_ONLY);
        freqSpinner.setMaximum(5);
        freqSpinner.setMinimum(1);
        GridDataFactory.fillDefaults().hint(10, SWT.DEFAULT).applyTo(freqSpinner);
        new Label(composite1, SWT.NONE).setText(Messages.MDMServerConsolePrefsPage_RefreshLabel_2);

        Composite composite2 = new Composite(parent, SWT.NONE);
        composite2.setLayout(new GridLayout(2, false));
        composite2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        new Label(composite2, SWT.NONE).setText(Messages.MDMServerConsolePrefsPage_Display_Buffer_Text);
        bufferText = new Text(composite2, SWT.BORDER);
        GridDataFactory.fillDefaults().hint(100, SWT.DEFAULT).applyTo(bufferText);
        bufferText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                String text = bufferText.getText().trim();
                try {
                    int value = Integer.parseInt(text);
                    boolean isValid = value >= MIN_BUFFER_SIZE;
                    String errorMsg = isValid ? null : NLS.bind(Messages.MDMServerConsolePrefsPage_Error_Message1,
                            MIN_BUFFER_SIZE);
                    setErrorMessage(errorMsg);
                    setValid(isValid);
                } catch (NumberFormatException ex) {
                    setErrorMessage(Messages.MDMServerConsolePrefsPage_Error_Message);
                    setValid(false);
                }
            }
        });
    }

    private void initControls() {
        int frequency = MDMServerPreferenceService.getRefrehFrequency();
        freqSpinner.setSelection(frequency);

        int bufferSize = MDMServerPreferenceService.getDisplayedBuffer();
        bufferText.setText(String.valueOf(bufferSize));
    }

    @Override
    public boolean performOk() {
        MDMServerPreferenceService.setRefreshFrequency(freqSpinner.getSelection());
        String value = bufferText.getText().trim();
        MDMServerPreferenceService.setDisplayedBuffer(Integer.parseInt(value));
        return super.performOk();
    }

    @Override
    protected void performDefaults() {
        int frequency = MDMServerPreferenceService.getDefaultRefreshFrequency();
        freqSpinner.setSelection(frequency);

        int bufferSize = MDMServerPreferenceService.getDefaultDisplayedBuffer();
        bufferText.setText(String.valueOf(bufferSize));
        super.performDefaults();
    }
}
