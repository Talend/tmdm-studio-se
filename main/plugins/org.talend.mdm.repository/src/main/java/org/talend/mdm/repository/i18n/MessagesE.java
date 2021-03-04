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
package org.talend.mdm.repository.i18n;

import java.util.ResourceBundle;

import org.talend.mdm.repository.plugin.RepositoryPlugin;

/**
 * Default implementation of MessageCore from org.talend.commons plug-in.<br/>
 *
 * Developpers can copy this class in their plug-in and change :
 * <ul>
 * <li>the BUNDLE_NAME constant</li>
 * </ul>
 *
 * $Id: Messages.java 23594 2009-04-13 10:33:00Z nrousseau $
 *
 */
public class MessagesE extends MessagesCoreE {

    private static final String BUNDLE_NAME = "org.talend.mdm.repository.ui.starting.messages"; //$NON-NLS-1$

    private static final String PLUGIN_ID = RepositoryPlugin.PLUGIN_ID;

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);

    /**
     * Returns the i18n formatted message for <i>key</i> in the class bundle.
     *
     * @param key - the key for the desired string
     * @return the string for the given key in the class resource bundle
     * @see MessagesCoreE#getString(String, ResourceBundle)
     */
    public static String getString(String key) {
        return getString(key, PLUGIN_ID, resourceBundle);
    }

    /**
     * Returns the i18n formatted message for <i>key</i> and <i>args</i> in the specified bundle.
     *
     * @param key - the key for the desired string
     * @param args - arg to include in the string
     * @return the string for the given key in the given resource bundle
     * @see MessagesCoreE#getString(String, ResourceBundle, Object[])
     */
    public static String getString(String key, Object... args) {
        return getString(key, PLUGIN_ID, resourceBundle, args);
    }
}
