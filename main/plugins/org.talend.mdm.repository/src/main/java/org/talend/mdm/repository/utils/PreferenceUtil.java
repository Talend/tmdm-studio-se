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
package org.talend.mdm.repository.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.talend.mdm.repository.plugin.RepositoryPlugin;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class PreferenceUtil {

    static Logger log = Logger.getLogger(PreferenceUtil.class);

    private static final String DIVIDER = ","; //$NON-NLS-1$

    private static IPreferenceStore store = RepositoryPlugin.getDefault().getPreferenceStore();

    public static boolean getBoolean(String key) {
        return store.getBoolean(key);
    }

    public static void setBoolean(String key, boolean value) {
        store.setValue(key, value);
    }

    public static String getString(String key) {
        return store.getString(key);
    }

    public static void setString(String key, String value) {
        store.setValue(key, value);
    }

    public static Set<String> getStringSet(String key) {
        Set<String> result = new HashSet<String>();
        String values = store.getString(key);
        if (values != null) {
            for (String value : values.split(DIVIDER)) {
                if (value.length() > 0)
                    result.add(value);
            }
        }
        return result;
    }

    public static void setStringSet(String key, Set<String> values) {
        if (values != null) {
            StringBuffer buf = new StringBuffer();
            for (String value : values) {
                if (value.length() > 0) {
                    buf.append(value).append(DIVIDER);
                }
            }
            store.setValue(key, buf.toString());
        }
    }

    public static void flipBooleanKey(String key) {
            boolean result = !store.getBoolean(key);
            store.setValue(key, result);
    }

    public static void save() {
        try {
            ((IPersistentPreferenceStore) store).save();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
