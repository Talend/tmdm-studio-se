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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class SwitchPerspectivePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    public SwitchPerspectivePreferencePage() {
    }

    private Button askUserForJobBun;

    // private Button askUserForWorkflowBun;

    public void init(IWorkbench workbench) {
        IPreferenceStore store = PlatformUI.getPreferenceStore();
        setPreferenceStore(store);

    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        composite.setLayoutData(gridData);
        composite.setLayout(new GridLayout(2, false));
        askUserForJobBun = new Button(composite, SWT.CHECK);

        askUserForJobBun.setText("Automatically open the Integration perspective without asking user, When open a Job object.");
        askUserForJobBun.setSelection(getPreferenceStore().getBoolean(PreferenceConstants.P_NOT_ASK_AUTO_SWITCH_TO_DI));
        gridData = new GridData();
        gridData.horizontalSpan = 2;
        askUserForJobBun.setLayoutData(gridData);
        //
        // if (Util.IsEnterPrise()) {
        // askUserForWorkflowBun = new Button(composite, SWT.CHECK);
        //
        // askUserForWorkflowBun
        // .setText("Automatically open the BPM perspective without asking user, When open a Workflow object.");
        // askUserForWorkflowBun.setSelection(getPreferenceStore().getBoolean(
        // PreferenceConstants.P_NOT_ASK_AUTO_SWITCH_TO_BONITA));
        // gridData = new GridData();
        // gridData.horizontalSpan = 2;
        // askUserForWorkflowBun.setLayoutData(gridData);
        // }
        initCheckedBuns();
        return composite;
    }

    public boolean performOk() {
        IPreferenceStore store = getPreferenceStore();
        store.setValue(PreferenceConstants.P_NOT_ASK_AUTO_SWITCH_TO_DI, askUserForJobBun.getSelection());
        // if (Util.IsEnterPrise()) {
        // store.setValue(PreferenceConstants.P_NOT_ASK_AUTO_SWITCH_TO_BONITA, askUserForWorkflowBun.getSelection());
        // }

        return true;
    }

    private void initCheckedBuns() {
        IPreferenceStore store = getPreferenceStore();
        askUserForJobBun.setSelection(store.getBoolean(PreferenceConstants.P_NOT_ASK_AUTO_SWITCH_TO_DI));
        // if (Util.IsEnterPrise()) {
        // askUserForWorkflowBun.setSelection(store.getBoolean(PreferenceConstants.P_NOT_ASK_AUTO_SWITCH_TO_BONITA));
        // }
    }

    protected void performDefaults() {
        IPreferenceStore store = getPreferenceStore();
        store.setValue(PreferenceConstants.P_NOT_ASK_AUTO_SWITCH_TO_DI, false);
        // if (Util.IsEnterPrise()) {
        // store.setValue(PreferenceConstants.P_NOT_ASK_AUTO_SWITCH_TO_BONITA, false);
        // }
        initCheckedBuns();
    }

}
