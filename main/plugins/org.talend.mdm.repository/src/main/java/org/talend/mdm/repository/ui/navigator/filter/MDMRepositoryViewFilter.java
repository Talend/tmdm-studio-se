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
import org.talend.mdm.repository.core.IRepositoryViewFilter;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class MDMRepositoryViewFilter extends ViewerFilter {

    IRepositoryViewFilter[] filters;

    public MDMRepositoryViewFilter() {
        filters = new IRepositoryViewFilter[] { new ServerObjectViewFilter(), new NamePatternViewFilter(),
                new LastServerViewFilter() };
    }

    public boolean select(Viewer viewer, Object parentElement, Object element) {
        for (IRepositoryViewFilter filter : filters) {
            if (filter.isEnable()) {
                if (!filter.select(viewer, parentElement, element)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void updatePreferences() {
        for (IRepositoryViewFilter filter : filters) {
            filter.updatePreference();
        }
    }
}
