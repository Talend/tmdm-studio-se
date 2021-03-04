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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IRepositoryViewFilter;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.utils.PreferenceUtil;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class LastServerViewFilter extends ViewerFilter implements IRepositoryViewFilter {

    public static final String PROP_ENABLE = "ENABLE_LAST_SERVER_FILTER"; //$NON-NLS-1$

    public static final String LAST_SERVER_NAME = "LAST_SERVER_NAME"; //$NON-NLS-1$

    private String lastServerName;

    public boolean isEnable() {
        return PreferenceUtil.getBoolean(PROP_ENABLE);
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof IRepositoryViewObject) {
            Item item = ((IRepositoryViewObject) element).getProperty().getItem();
            if (item != null) {
                if (item instanceof FolderItem) {
                    return true;
                } else {
                    MDMServerDef lastServerDef = RepositoryResourceUtil.getLastServerDef(item);
                    return isEnableServerObject(lastServerDef);
                }
            }
        }
        return false;
    }

    private boolean isEnableServerObject(MDMServerDef serverDef) {
        if (serverDef == null)
            return false;
        if (lastServerName == null) {
            updateLastServerName();
        }
        if (lastServerName == null || lastServerName.length() == 0)
            return false;
        else {
            return lastServerName.equalsIgnoreCase(serverDef.getName());
        }
    }

    private void updateLastServerName() {
        lastServerName = PreferenceUtil.getString(LAST_SERVER_NAME);
    }

    public void updatePreference() {
        updateLastServerName();
    }

}
