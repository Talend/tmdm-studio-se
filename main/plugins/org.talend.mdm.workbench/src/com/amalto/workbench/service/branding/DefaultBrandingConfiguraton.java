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
package com.amalto.workbench.service.branding;

public class DefaultBrandingConfiguraton implements IBrandingConfiguration {

    private String title = "";//$NON-NLS-1$

    public String getAdditionalTitle() {

        return title;
    }

    public void setAdditionalTitle(String title) {
        this.title = title;

    }

}
