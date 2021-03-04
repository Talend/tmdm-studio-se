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
package com.amalto.workbench.providers;

public class CellEditorValueExtractor<T> {

    protected String inforAccessMethod;

    public CellEditorValueExtractor(String inforAccessMethod) {
        this.inforAccessMethod = inforAccessMethod;
    }

    public Object getValue(T info) {

        if (info == null)
            return "";//$NON-NLS-1$

        Object result = getInfoContent(info);

        if (result == null)
            return "";//$NON-NLS-1$

        return result;
    }

    protected Object getInfoContent(T info) {

        if (inforAccessMethod == null || "".equals(inforAccessMethod.trim()))//$NON-NLS-1$
            return null;

        try {
            return info.getClass().getMethod(inforAccessMethod).invoke(info);
        } catch (Exception e) {
            return null;
        }
    }
}
