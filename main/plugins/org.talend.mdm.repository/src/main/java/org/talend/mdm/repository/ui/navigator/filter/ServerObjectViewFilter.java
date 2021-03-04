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
package org.talend.mdm.repository.ui.navigator.filter;

import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.IRepositoryViewFilter;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.mdm.repository.utils.PreferenceUtil;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class ServerObjectViewFilter extends ViewerFilter implements IRepositoryViewFilter {

    public static final String PROP_ENABLE = "ENABLE_SERVER_OBJECT_FILTER"; //$NON-NLS-1$

    public static final String PROP_ENABLE_LIST = "Enable_SERVER_OBJECTS"; //$NON-NLS-1$

    private Set<String> enabledServerObjects;

    public boolean isEnable() {
        return PreferenceUtil.getBoolean(PROP_ENABLE);
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof FolderRepositoryObject) {
            IRepositoryNodeConfiguration conf = RepositoryNodeConfigurationManager
                    .getConfiguration((IRepositoryViewObject) element);
            if (conf != null) {
                String label = conf.getLabelProvider().getCategoryLabel(null);
                return isEnableServerObject(label);
            }
        }
        return true;
    }

    private boolean isEnableServerObject(String label) {
        if (enabledServerObjects == null) {
            updateEnableServerObjects();
        }
        return enabledServerObjects.contains(label);
    }

    private void updateEnableServerObjects() {
        enabledServerObjects = PreferenceUtil.getStringSet(PROP_ENABLE_LIST);
    }

    public void updatePreference() {
        updateEnableServerObjects();
    }

}
