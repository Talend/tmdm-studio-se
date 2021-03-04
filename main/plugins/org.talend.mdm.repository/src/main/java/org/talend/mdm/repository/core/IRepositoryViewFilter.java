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
package org.talend.mdm.repository.core;

import org.eclipse.jface.viewers.Viewer;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public interface IRepositoryViewFilter {

    public static final String PROP_REFRESH = "REFRESH"; //$NON-NLS-1$

    public boolean select(Viewer viewer, Object parentElement, Object element);

    public boolean isEnable();

    public void updatePreference();
}
