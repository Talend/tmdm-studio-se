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
package com.amalto.workbench.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.amalto.workbench.i18n.Messages;

/**
 * DOC qian class global comment. A global service register provides the service registration and acquirement. <br/>
 *
 * $Id: talend-code-templates.xml 1 2006-09-29 17:06:40 +0000 () nrousseau $
 *
 */
public class GlobalServiceRegister {

    private static Log log = LogFactory.getLog(GlobalServiceRegister.class);

    // The shared instance
    private static GlobalServiceRegister instance = new GlobalServiceRegister();

    private static IConfigurationElement[] configurationElements;

    public static GlobalServiceRegister getDefault() {
        return instance;
    }

    private Map<Class, IService> services = new HashMap<Class, IService>();

    static {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        configurationElements = registry.getConfigurationElementsFor("org.talend.mdm.workbench.service"); //$NON-NLS-1$
    }

    /**
     * DOC qian Comment method "getService".Gets the specific IService.
     *
     * @param klass the Service type you want to get
     * @return IService IService
     */
    public IService getService(Class klass) {
        IService service = services.get(klass);
        if (service == null) {
            service = findService(klass);
            if (service == null) {
                throw new RuntimeException(Messages.bind(Messages.GlobalServiceRegister_ServiceNotRegistered, klass.getName()));
            }
            services.put(klass, service);
        }
        return service;
    }

    /**
     * DOC qian Comment method "findService".Finds the specific service from the list.
     *
     * @param klass the interface type want to find.
     * @return IService
     */
    private IService findService(Class klass) {
        String key = klass.getName();
        for (int i = 0; i < configurationElements.length; i++) {
            IConfigurationElement element = configurationElements[i];
            String id = element.getAttribute("serviceId"); //$NON-NLS-1$
            element.getAttribute("class");//$NON-NLS-1$
            if (!key.endsWith(id)) {
                continue;
            }
            try {
                Object service = element.createExecutableExtension("class"); //$NON-NLS-1$
                if (klass.isInstance(service)) {
                    return (IService) service;
                }
            } catch (CoreException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }
}
