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
package com.amalto.workbench.utils;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;


/**
 * This class can check whether some specific plugins are loaded or not. <br/>
 *
 */
public class PluginChecker {

    /**
     * Check if specific plug-in is loaded.
     *
     * @return isLoaded
     */
    public static boolean isPluginLoaded(String pluginID) {
        boolean isLoaded = true;
        Bundle bundle = Platform.getBundle(pluginID);
        if (bundle == null || (bundle != null && bundle.getState() == Bundle.UNINSTALLED)) {
            isLoaded = false;
        }
        return isLoaded;
    }

}
