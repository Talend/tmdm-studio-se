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
package org.talend.mdm.repository.ui.markers;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.talend.mdm.repository.validate.plugin.ValidatePlugin;

/**
 * created by HHB on 2014-3-31 Detailled comment
 *
 */
public class OpenMarkerHandlerRegister {

    private static List<IOpenMarkerHandler> handlers = null;

    private static final String EXTENSION_POINT_TEMPLATE = "openMarkerHandler"; //$NON-NLS-1$

    private static final String PROP_CLASS = "class"; //$NON-NLS-1$

    static Logger log = Logger.getLogger(OpenMarkerHandlerRegister.class);

    public static List<IOpenMarkerHandler> getHandlers() {
        if (handlers == null) {
            handlers = new LinkedList<IOpenMarkerHandler>();
            IExtensionRegistry registry = Platform.getExtensionRegistry();

            //
            IExtensionPoint extensionPoint = registry.getExtensionPoint(ValidatePlugin.PLUGIN_ID, EXTENSION_POINT_TEMPLATE);
            if (extensionPoint != null && extensionPoint.isValid()) {
                IExtension[] extensions = extensionPoint.getExtensions();
                for (IExtension s : extensions) {
                    IConfigurationElement[] elements = s.getConfigurationElements();
                    for (IConfigurationElement element : elements) {
                        if (element.getAttribute(PROP_CLASS) != null) {
                            try {
                                IOpenMarkerHandler handler = (IOpenMarkerHandler) element.createExecutableExtension(PROP_CLASS);
                                handlers.add(handler);

                            } catch (CoreException e) {
                                log.error(e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        }
        return handlers;
    }
}
