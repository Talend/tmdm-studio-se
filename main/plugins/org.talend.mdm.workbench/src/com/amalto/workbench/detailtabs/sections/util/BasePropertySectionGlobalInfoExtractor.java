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
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.Util;

public abstract class BasePropertySectionGlobalInfoExtractor {

    protected BasePropertySection propSection;

    protected int typeCode;

    public BasePropertySectionGlobalInfoExtractor(BasePropertySection propSection, int typeCode) {
        this.propSection = propSection;
        this.typeCode = typeCode;
    }

    public String[] getGlobalInfos() {

        if (propSection.getTreeObject() == null)
            return new String[0];
        if (propSection.getTreeObject().getServerRoot() == null) {
            if (typeCode == TreeObject.ROLE)
                return MDMRepositoryViewExtensionService.findAllRoleNames().toArray(new String[0]);
            if (typeCode == TreeObject.DATA_MODEL)
                return MDMRepositoryViewExtensionService.findAllDataModelNames().toArray(new String[0]);

        }
        return Util.getChildren(propSection.getTreeObject().getServerRoot(), typeCode).toArray(new String[0]);
    }

    public boolean hasGlobalInfo() {
        return getGlobalInfos().length > 0;
    }


}
