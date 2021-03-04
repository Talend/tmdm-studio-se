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
package com.amalto.workbench.detailtabs.sections.util;

import com.amalto.workbench.detailtabs.sections.BasePropertySection;

public class FixDMNameBasePropertySectionDataModelExtractor extends BasePropertySectionDataModelExtractor {

    public FixDMNameBasePropertySectionDataModelExtractor(BasePropertySection propSection) {
        this(propSection, "");//$NON-NLS-1$
    }

    public FixDMNameBasePropertySectionDataModelExtractor(BasePropertySection propSection, String defaultDataModel) {
        super(propSection, defaultDataModel);
    }

    @Override
    public String[] getAllDataModelNames() {

        if (defaultDataModel == null || "".equals(defaultDataModel.trim()))//$NON-NLS-1$
            return new String[0];

        return new String[] { defaultDataModel };
    }
}
