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

public class ColumnTextExtractor<T> {

    private String accessMethodName;

    public ColumnTextExtractor(String methodName) {
        this.accessMethodName = methodName;
    }

    public String getAccessMethodName() {
        return accessMethodName;
    }

    public void setAccessMethodName(String accessMethodName) {

        if (accessMethodName == null || "".equals(accessMethodName.trim()))//$NON-NLS-1$
            return;

        this.accessMethodName = accessMethodName;
    }

    public String getColText(T annoInfo) {

        if (annoInfo == null)
            return "";//$NON-NLS-1$

        try {
            return annoInfo.getClass().getMethod(accessMethodName).invoke(annoInfo).toString();
        } catch (Exception e) {
            return "";//$NON-NLS-1$
        }
    }
}
