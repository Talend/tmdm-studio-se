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

import org.eclipse.xsd.XSDElementDeclaration;

/**
 * created by HHB on 2013-10-28 Only select entity
 *
 *
 */
public class EntitySelectionFilter implements IXPathSelectionFilter {

    public FilterResult check(Object obj) {
        if (obj instanceof XSDElementDeclaration) {
            return FilterResult.ENABLE;
        }

        return FilterResult.DISABLE;
    }

}
