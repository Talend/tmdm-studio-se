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
package com.amalto.workbench.utils;

import java.util.HashSet;
import java.util.Set;

public enum EContentType {
    TEXT_XML("text/xml"), //$NON-NLS-1$
    TEXT_PLAIN("text/plain"), //$NON-NLS-1$
    APPLICATION_XML("application/xhtml+xml"), //$NON-NLS-1$
    APPLICATION_ITEMPK("application/xtentis.itempk");//$NON-NLS-1$

    String name;

    EContentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Set<String> allTypes() {
        Set<String> list = new HashSet<String>();
        for (EContentType type : values()) {
            list.add(type.name);
        }
        return list;
    }
}
