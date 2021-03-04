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
package org.talend.mdm.engines.client.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.talend.mdm.engines.client.Activator;

/**@deprecated
 * Preference Initializer for the designer.
 *
 * $Id: PreferenceInitializer.java 5392 2007-08-29 06:24:20Z yzhang $
 *
 */
public class MDMPreferenceInitializer extends AbstractPreferenceInitializer {

    //    public static final String MDM_STATUS = "MDMPreferencePage.spagoBiCheckButton"; //$NON-NLS-1$

    public static final String MDM_SERVER = "org.talend.repository.properties.mdmserver"; //$NON-NLS-1$

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        // store.setDefault(MDM_STATUS, false);
    }

}
