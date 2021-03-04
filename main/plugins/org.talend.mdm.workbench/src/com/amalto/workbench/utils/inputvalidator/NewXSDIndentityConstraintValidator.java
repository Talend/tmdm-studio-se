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
package com.amalto.workbench.utils.inputvalidator;

import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDSchema;

import com.amalto.workbench.i18n.Messages;

public class NewXSDIndentityConstraintValidator {

    private XSDSchema schema;

    public NewXSDIndentityConstraintValidator(XSDSchema schema) {
        this.schema = schema;
    }

    public XSDSchema getSchema() {
        return schema;
    }

    public String isValid(String keyName, XSDIdentityConstraintCategory type, XSDElementDeclaration element) {

        if (keyName == null || "".equals(keyName.trim()))//$NON-NLS-1$
            return Messages.NewXSDIndentityConstraintValidator_KeyNameCannotbeEmpty;

        for (XSDIdentityConstraintDefinition eachKey : schema.getIdentityConstraintDefinitions()) {

            if ((type.equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL))
                    && (eachKey.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL))
                    // TODO: Is it should be (icd.getContainer().equals(decl.getContainer()) ? otherwise we will permit
                    // there are more than 1 unique key under 1 element
                    // (icd.getContainer().equals(decl.getContainer()))) {
                    && (eachKey.getContainer().equals(element))) {
                return Messages.NewXSDIndentityConstraintValidator_BElemntAlreadyHasUniqueKey;
            }

            if (eachKey.getName().equals(keyName)) {
                return Messages.bind(Messages.NewXSDIndentityConstraintValidator_KeyInfo, keyName);
            }

        }

        return null;
    }

}
