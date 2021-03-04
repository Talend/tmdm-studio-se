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
package com.amalto.workbench.providers.datamodel.util;

import org.eclipse.xsd.XSDAnnotation;

import com.amalto.workbench.utils.XSDAnnotationsStructure;

class SchemaItemRoleSelectorOnWrite extends SchemaItemRoleSelectorOnNotAll {

    @Override
    protected boolean isRoleValid(String role, XSDAnnotation annotation) {
        if (annotation != null) {
            XSDAnnotationsStructure annotion = new XSDAnnotationsStructure(annotation);
            return annotion.getWriteAccesses().values().contains(role);
        }

        return false;
    }

}
