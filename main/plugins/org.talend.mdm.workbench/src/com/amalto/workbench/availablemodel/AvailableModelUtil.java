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
package com.amalto.workbench.availablemodel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class AvailableModelUtil {

    /**
     *
     */
    private static final String CLASS = "class"; //$NON-NLS-1$

    private static final String MODEL_TYPE = "modelType"; //$NON-NLS-1$

    private static final String TYPE_ALL = "All"; //$NON-NLS-1$

    private static final String TYPE_LEGACY = "Legacy"; //$NON-NLS-1$

    private static final String TYPE_REPOSITORY = "Repository"; //$NON-NLS-1$

    /**
     *
     */
    private static final String EXTENSION_POINT = "org.talend.mdm.workbench.AvailableModel"; //$NON-NLS-1$

    private static Log log = LogFactory.getLog(AvailableModelUtil.class);

    public static List<IAvailableModel> getAvailableModels() {
        try {
            IExtensionRegistry registry = Platform.getExtensionRegistry();
            IConfigurationElement[] configurationElements = registry.getConfigurationElementsFor(EXTENSION_POINT);
            List<IAvailableModel> models = new ArrayList<IAvailableModel>();
            for (int i = 0; i < configurationElements.length; i++) {
                IConfigurationElement element = configurationElements[i];

                IAvailableModel modelcalss = (IAvailableModel) element.createExecutableExtension(CLASS);
                models.add(modelcalss);
            }

            return models;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new ArrayList<IAvailableModel>();
    }

    public static List<IAvailableModel> getAvailableModels(boolean inRepository) {
        try {
            IExtensionRegistry registry = Platform.getExtensionRegistry();
            IConfigurationElement[] configurationElements = registry.getConfigurationElementsFor(EXTENSION_POINT);
            List<IAvailableModel> models = new ArrayList<IAvailableModel>();
            for (int i = 0; i < configurationElements.length; i++) {
                IConfigurationElement element = configurationElements[i];
                String modelType = element.getAttribute(MODEL_TYPE);
                boolean toAdd = false;

                if (modelType == null || modelType.equals(TYPE_ALL)) {
                    toAdd = true;
                } else if ((inRepository && modelType.equals(TYPE_REPOSITORY))
                        || (!inRepository && modelType.equals(TYPE_LEGACY))) {
                    toAdd = true;
                }
                if (toAdd) {
                    IAvailableModel modelClass = (IAvailableModel) element.createExecutableExtension(CLASS);
                    models.add(modelClass);
                }

            }

            return models;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new ArrayList<IAvailableModel>();
    }
}
