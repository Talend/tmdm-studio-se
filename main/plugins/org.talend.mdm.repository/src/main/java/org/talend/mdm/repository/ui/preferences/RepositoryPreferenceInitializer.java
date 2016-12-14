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
package org.talend.mdm.repository.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.talend.mdm.repository.core.service.ConsistencyService;
import org.talend.mdm.repository.plugin.RepositoryPlugin;

public class RepositoryPreferenceInitializer extends AbstractPreferenceInitializer implements PreferenceConstants {

    public RepositoryPreferenceInitializer() {
    }

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = RepositoryPlugin.getDefault().getPreferenceStore();
        initConsistencyConflictPreference(store);

    }

    void initConsistencyConflictPreference(IPreferenceStore store) {
        store.setDefault(PreferenceConstants.P_WARN_USER_HAS_CONFLICT, true);
        store.setDefault(PreferenceConstants.P_CONFLICT_STRATEGY, ConsistencyService.CONFLICT_STRATEGY_DEFAULT);
    }

}
