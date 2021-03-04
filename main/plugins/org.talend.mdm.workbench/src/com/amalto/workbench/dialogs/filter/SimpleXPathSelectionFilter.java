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
package com.amalto.workbench.dialogs.filter;

import org.eclipse.xsd.XSDParticle;

import com.amalto.workbench.dialogs.datamodel.IXPathSelectionFilter;
import com.amalto.workbench.utils.XSDUtil;

/**
 *
 */
public class SimpleXPathSelectionFilter implements IXPathSelectionFilter {

    public FilterResult check(Object obj) {
        if(XSDUtil.isEntity(obj)) {
            return FilterResult.ENABLE;
        }

        if (obj instanceof XSDParticle) {
            if (XSDUtil.isPrimaryKeyElement((XSDParticle) obj)) {
                return FilterResult.ENABLE;
            }
        }
        return FilterResult.DISABLE;
    }

}
