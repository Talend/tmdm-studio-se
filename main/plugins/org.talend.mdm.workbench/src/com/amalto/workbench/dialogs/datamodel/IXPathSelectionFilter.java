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
package com.amalto.workbench.dialogs.datamodel;

/**
 * created by HHB on 2013-8-14 Detailled comment
 *
 */
public interface IXPathSelectionFilter {

    enum FilterResult {
        ENABLE,
        DISABLE,
        IGNORE
    }

    public FilterResult check(Object obj);
}
