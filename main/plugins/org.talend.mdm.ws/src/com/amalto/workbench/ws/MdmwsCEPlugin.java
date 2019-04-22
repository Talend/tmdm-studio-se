package com.amalto.workbench.ws;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class MdmwsCEPlugin extends Plugin {

    public static final String ID = "org.talend.mdm.ws";//$NON-NLS-1$

    // The shared instance.
    private static MdmwsCEPlugin plugin;

    /**
     * The constructor.
     */
    public MdmwsCEPlugin() {
        plugin = this;
    }

    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        String jdkversion = System.getProperty("java.version");
        if (!jdkversion.startsWith("11")) {
            Bundle bundle = Platform.getBundle("org.talend.mdm.ws.jars");
            if (bundle != null && (bundle.getState() == Bundle.ACTIVE || bundle.getState() == Bundle.RESOLVED
                    || bundle.getState() == Bundle.STARTING)) {
                bundle.uninstall();
            }
        }
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
    public static MdmwsCEPlugin getDefault() {
        return plugin;
    }

}
