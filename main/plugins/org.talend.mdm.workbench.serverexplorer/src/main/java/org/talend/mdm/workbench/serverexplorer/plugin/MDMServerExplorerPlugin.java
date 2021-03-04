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
package org.talend.mdm.workbench.serverexplorer.plugin;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.talend.mdm.workbench.serverexplorer.console.MDMServerMessageConsole;

/**
 * The activator class controls the plug-in life cycle
 */
public class MDMServerExplorerPlugin extends AbstractUIPlugin {

    private static final Log log = LogFactory.getLog(MDMServerExplorerPlugin.class);

    // The plug-in ID
    public static final String PLUGIN_ID = "org.talend.mdm.workbench.serverexplorer"; //$NON-NLS-1$

    // The shared instance
    private static MDMServerExplorerPlugin plugin;

    private Map<String, MDMServerMessageConsole> serverToConsole = null;

    /**
     * The constructor
     */
    public MDMServerExplorerPlugin() {
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        serverToConsole = new HashMap<String, MDMServerMessageConsole>();

        activateEEBundleIfExist();
    }

    private void activateEEBundleIfExist() {
        new Thread(new Runnable() {

            public void run() {
                activate();
            }
        }).run();
    }

    private void activate() {
        try {
            Bundle bundle = Platform.getBundle(PLUGIN_ID + ".enterprise"); //$NON-NLS-1$
            if (bundle != null && bundle.getState() != Bundle.ACTIVE) {
                bundle.start();
            }
        } catch (BundleException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (serverToConsole != null) {
            serverToConsole.clear();
            serverToConsole = null;
        }

        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static MDMServerExplorerPlugin getDefault() {
        return plugin;
    }

    public Map<String, MDMServerMessageConsole> getServerToConsole() {
        return this.serverToConsole;
    }
}
