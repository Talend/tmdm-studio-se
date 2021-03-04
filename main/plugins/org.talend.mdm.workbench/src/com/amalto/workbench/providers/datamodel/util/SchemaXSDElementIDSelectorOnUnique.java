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

import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;

class SchemaXSDElementIDSelectorOnUnique implements SchemaXSDElementIDSelector {

    public boolean isSatisfiedElement(Object parentElement, Object element) {

        if (element instanceof XSDElementDeclaration)
            return isUniqueXSDElementDeclaration((XSDElementDeclaration) element);

        return true;
    }

    private boolean isUniqueXSDElementDeclaration(XSDElementDeclaration element) {

        for (XSDIdentityConstraintDefinition eachIDDef : element.getIdentityConstraintDefinitions()) {
            if (eachIDDef.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL))
                return true;
        }

        return false;
    }
}
