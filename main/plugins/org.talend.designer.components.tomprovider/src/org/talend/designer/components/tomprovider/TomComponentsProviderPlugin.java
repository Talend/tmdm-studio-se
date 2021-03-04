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
package org.talend.designer.components.tomprovider;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class TomComponentsProviderPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "org.talend.designer.components.tomprovider"; //$NON-NLS-1$

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

    private static TomComponentsProviderPlugin plugin;

    public TomComponentsProviderPlugin() {
        plugin = this;
    }

	@Override
    public void start(BundleContext bundleContext) throws Exception {
		TomComponentsProviderPlugin.context = bundleContext;
	}

	@Override
    public void stop(BundleContext bundleContext) throws Exception {
        plugin = null;
		TomComponentsProviderPlugin.context = null;
	}

    public static TomComponentsProviderPlugin getDefault() {
        return plugin;
    }

}
