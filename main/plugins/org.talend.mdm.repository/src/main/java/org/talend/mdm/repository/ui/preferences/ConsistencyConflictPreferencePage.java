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
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.talend.mdm.repository.core.service.ConsistencyService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.plugin.RepositoryPlugin;

/**
 * created by HHB on 2013-7-25 Detailled comment
 *
 */
public class ConsistencyConflictPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private Button warnUserBun;

    private Button defaultBun;

    private Button overrideBun;

    private Button skipBun;

    private ConsistencyService service;

    private IPreferenceStore store;

    /**
     * Create the preference page.
     */
    public ConsistencyConflictPreferencePage() {
    }

    /**
     * Create contents of the preference page.
     *
     * @param parent
     */
    @Override
    public Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        container.setLayout(new GridLayout(1, false));

        warnUserBun = new Button(container, SWT.CHECK);
        warnUserBun.setText(Messages.ConsistencyConflictPreferencePage_warnUserWhenHasConflict);

        Group grpConflictStr = new Group(container, SWT.NONE);
        grpConflictStr.setText(Messages.ConsistencyConflict_conflictStrategy);
        grpConflictStr.setLayout(new GridLayout(1, false));
        grpConflictStr.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        defaultBun = new Button(grpConflictStr, SWT.RADIO);
        defaultBun.setSelection(true);
        defaultBun.setText(Messages.ConsistencyConflict_defaultStrategyLabel);

        overrideBun = new Button(grpConflictStr, SWT.RADIO);
        overrideBun.setText(Messages.ConsistencyConflict_overrideStrategyLabel);

        skipBun = new Button(grpConflictStr, SWT.RADIO);
        skipBun.setText(Messages.ConsistencyConflict_skipStrategyLabel);
        initUI();
        return container;
    }

    /**
     * DOC HHB Comment method "initUI".
     */
    private void initUI() {
        warnUserBun.setSelection(service.isWarnUserWhenConflict());
        int strategy = service.getConflictStrategy();
        defaultBun.setSelection(ConsistencyService.CONFLICT_STRATEGY_DEFAULT == strategy);
        overrideBun.setSelection(ConsistencyService.CONFLICT_STRATEGY_OVERWRITE == strategy);
        skipBun.setSelection(ConsistencyService.CONFLICT_STRATEGY_SKIP_DIFFERENCE == strategy);
    }

    @Override
    public boolean performOk() {
        service.setWarnUserWhenConflict(warnUserBun.getSelection());
        int stragegy = 0;
        if (defaultBun.getSelection()) {
            stragegy = ConsistencyService.CONFLICT_STRATEGY_DEFAULT;
        } else if (overrideBun.getSelection()) {
            stragegy = ConsistencyService.CONFLICT_STRATEGY_OVERWRITE;
        } else if (skipBun.getSelection()) {
            stragegy = ConsistencyService.CONFLICT_STRATEGY_SKIP_DIFFERENCE;
        }
        service.setConflictStrategy(stragegy);
        return super.performOk();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        store.setValue(PreferenceConstants.P_WARN_USER_HAS_CONFLICT, true);
        store.setValue(PreferenceConstants.P_CONFLICT_STRATEGY, ConsistencyService.CONFLICT_STRATEGY_DEFAULT);
        initUI();

        super.performDefaults();
    }

    /**
     * Initialize the preference page.
     */
    public void init(IWorkbench workbench) {
        store = RepositoryPlugin.getDefault().getPreferenceStore();
        setPreferenceStore(store);
        this.service = ConsistencyService.getInstance();
    }
}
