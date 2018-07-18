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
