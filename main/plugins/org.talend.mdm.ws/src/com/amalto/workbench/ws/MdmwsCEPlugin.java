package com.amalto.workbench.ws;
import org.eclipse.core.runtime.Plugin;
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
