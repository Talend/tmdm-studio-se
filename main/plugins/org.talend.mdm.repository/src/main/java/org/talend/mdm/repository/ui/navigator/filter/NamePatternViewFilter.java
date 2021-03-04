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

import org.eclipse.ui.dialogs.PatternFilter;
import org.talend.mdm.repository.core.IRepositoryViewFilter;
import org.talend.mdm.repository.utils.PreferenceUtil;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class NamePatternViewFilter extends PatternFilter implements IRepositoryViewFilter {

    public static final String PROP_ENABLE = "ENABLE_NAME_PATTERN_FILTER"; //$NON-NLS-1$

    public static final String PROP_NAME_PATTERN = "NAME_PATTERN"; //$NON-NLS-1$

    public boolean isEnable() {
        return PreferenceUtil.getBoolean(PROP_ENABLE);
    }

    public void updatePreference() {
        String pattern = PreferenceUtil.getString(PROP_NAME_PATTERN);
        setPattern(pattern);
    }

}
