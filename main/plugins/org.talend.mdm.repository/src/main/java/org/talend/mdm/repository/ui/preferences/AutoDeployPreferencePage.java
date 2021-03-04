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
package org.talend.mdm.repository.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.talend.mdm.repository.i18n.Messages;

import com.amalto.workbench.exadapter.ExAdapterManager;

public class AutoDeployPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    public AutoDeployPreferencePage() {
    }

    private Button bAutoDeploy;

    private Button warnUserBun;

    IAutoDeployPreferencePageExAdapter exAdapter = null;

    public void init(IWorkbench workbench) {
        IPreferenceStore store = PlatformUI.getPreferenceStore();
        setPreferenceStore(store);
        exAdapter = ExAdapterManager.getAdapter(this, IAutoDeployPreferencePageExAdapter.class);
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        composite.setLayoutData(gridData);
        composite.setLayout(new GridLayout(2, false));
        bAutoDeploy = new Button(composite, SWT.CHECK);
        bAutoDeploy.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateWarnUserBun();
            }
        });
        bAutoDeploy.setText(Messages.AutoDeploy_saving_text);
        bAutoDeploy.setSelection(getPreferenceStore().getBoolean(PreferenceConstants.P_AUTO_DEPLOY));
        gridData = new GridData();
        gridData.horizontalSpan = 2;
        bAutoDeploy.setLayoutData(gridData);

        warnUserBun = new Button(composite, SWT.CHECK);
        GridData gd_btnCheckButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnCheckButton.horizontalIndent = 20;
        warnUserBun.setLayoutData(gd_btnCheckButton);
        warnUserBun.setText(Messages.AutoDeployPreferencePage_btnCheckButton_text);
        if (exAdapter != null) {
            exAdapter.createContents(composite);
        }
        //
        initCheckedBuns();
        return composite;
    }

    private void updateWarnUserBun() {
        warnUserBun.setEnabled(bAutoDeploy.getSelection());
    }

    @Override
    public boolean performOk() {
        IPreferenceStore store = getPreferenceStore();
        store.setValue(PreferenceConstants.P_AUTO_DEPLOY, bAutoDeploy.getSelection());
        store.setValue(PreferenceConstants.P_WARN_USER_AUTO_DEPLOY, warnUserBun.getSelection());
        if (exAdapter != null) {
            exAdapter.performOk();
        }
        return true;
    }

    private void initCheckedBuns() {
        IPreferenceStore store = getPreferenceStore();
        bAutoDeploy.setSelection(store.getBoolean(PreferenceConstants.P_AUTO_DEPLOY));
        warnUserBun.setSelection(store.getBoolean(PreferenceConstants.P_WARN_USER_AUTO_DEPLOY));
        if (exAdapter != null) {
            exAdapter.initCheckedBuns();
        }
        updateWarnUserBun();
    }

    @Override
    protected void performDefaults() {
        IPreferenceStore store = getPreferenceStore();
        store.setValue(PreferenceConstants.P_AUTO_DEPLOY, false);
        store.setValue(PreferenceConstants.P_WARN_USER_AUTO_DEPLOY, true);
        if (exAdapter != null) {
            exAdapter.performDefaults();
        }
        initCheckedBuns();
    }

}
