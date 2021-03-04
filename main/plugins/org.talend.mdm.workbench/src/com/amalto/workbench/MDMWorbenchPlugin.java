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
package com.amalto.workbench;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MDMWorbenchPlugin extends AbstractUIPlugin {

    public static final String ID = "org.talend.mdm.workbench";//$NON-NLS-1$

    // The shared instance.
    private static MDMWorbenchPlugin plugin;

    /**
     * The constructor.
     */
    public MDMWorbenchPlugin() {
        plugin = this;
    }

    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }

    /**
     * Returns the shared instance.
     */
    public static MDMWorbenchPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
    }

    public String getVersion() {
        String version = System.getProperty("talend.studio.version"); //$NON-NLS-1$
        if (version == null || "".equals(version.trim())) { //$NON-NLS-1$
            version = (String) getDefault().getBundle().getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
        }
        return version;
    }
}
