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
package org.talend.mdm.repository.core.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.mdm.repository.plugin.RepositoryPlugin;

/**
 * created by HHB on 2013-10-18 Detailled comment
 *
 */
public class RepositoryViewObjectResourceChangeManager implements PropertyChangeListener {

    private static final String EXTENSION_POINT_TEMPLATE = "repositoryResourceChangeListener"; //$NON-NLS-1$

    private static final String PROP_CLASS = "class"; //$NON-NLS-1$

    private RepositoryViewObjectResourceChangeManager() {

    }

    private boolean isWorkInUI() {
        try {
            return PlatformUI.getWorkbench() != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static RepositoryViewObjectResourceChangeManager instance = new RepositoryViewObjectResourceChangeManager();

    public static void startListening() {
        ProxyRepositoryFactory.getInstance().addPropertyChangeListener(instance);
    }

    public static void stopListening() {
        ProxyRepositoryFactory.getInstance().removePropertyChangeListener(instance);
    }

    static Logger log = Logger.getLogger(RepositoryViewObjectResourceChangeManager.class);

    public void propertyChange(PropertyChangeEvent event) {
        if (isWorkInUI() && ProxyRepositoryFactory.getInstance().isFullLogonFinished()) {
            String propertyName = event.getPropertyName();
            Object newValue = event.getNewValue();
            if (newValue != null) {
                if (newValue instanceof IRepositoryViewObject) {
                    newValue = ((IRepositoryViewObject) newValue).getProperty().getItem();
                }
                if (newValue instanceof Property) {
                    newValue = ((Property) newValue).getItem();
                }
                if (newValue instanceof Item) {
                    Item item = (Item) newValue;
                    for (AbstractRepositoryResourceChangeListener listener : getListeners()) {
                        if (listener.isHandleProperty(propertyName) && listener.isHandleItem(item)) {
                            try {
                                listener.run(propertyName, item);
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        }
    }

    private List<AbstractRepositoryResourceChangeListener> listeners = null;

    private List<AbstractRepositoryResourceChangeListener> getListeners() {
        if (listeners == null) {
            initListenerDefine();
        }
        return listeners;
    }

    private void initListenerDefine() {
        listeners = new ArrayList<AbstractRepositoryResourceChangeListener>();
        IExtensionRegistry registry = Platform.getExtensionRegistry();

        //
        IExtensionPoint extensionPoint = registry.getExtensionPoint(RepositoryPlugin.PLUGIN_ID, EXTENSION_POINT_TEMPLATE);
        if (extensionPoint != null && extensionPoint.isValid()) {
            IExtension[] extensions = extensionPoint.getExtensions();
            for (IExtension s : extensions) {
                IConfigurationElement[] elements = s.getConfigurationElements();
                for (IConfigurationElement element : elements) {
                    if (element.getAttribute(PROP_CLASS) != null) {
                        try {
                            AbstractRepositoryResourceChangeListener configuration = (AbstractRepositoryResourceChangeListener) element
                                    .createExecutableExtension(PROP_CLASS);
                            listeners.add(configuration);

                        } catch (CoreException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }

}
