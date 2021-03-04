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

import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDSchema;

import com.amalto.workbench.i18n.Messages;

public class EditXSDIdentityConstraintNameValidator implements IInputValidator {

    private XSDIdentityConstraintDefinition key;

    public EditXSDIdentityConstraintNameValidator(XSDIdentityConstraintDefinition key) {
        this.key = key;
    }

    public XSDSchema getSchema() {
        return key.getSchema();
    }

    public XSDIdentityConstraintDefinition getKey() {
        return key;
    }

    // @Override
    public String isValid(String newText) {

        if ((newText == null) || "".equals(newText))//$NON-NLS-1$
            return Messages.EditXSDIdXXValidator_UniqueKeyNameCannotbeEmpty;

        if (Pattern.compile("^\\s+\\w+\\s*").matcher(newText).matches()//$NON-NLS-1$
                || newText.trim().replaceAll("\\s", "").length() != newText.trim().length())//$NON-NLS-1$//$NON-NLS-2$
            return Messages.EditXSDIdXXValidator_UniqueKeyNameCannotContainEmpty;

        if (XSDIdentityConstraintCategory.UNIQUE_LITERAL.equals(key.getIdentityConstraintCategory())
                && !newText.trim().equals(((XSDElementDeclaration) key.getContainer()).getName())) {
            return Messages.EditXSDIdXXValidator_UniqueKeyNameMustbeEqualXX;
        }

        for (XSDIdentityConstraintDefinition eachId : getSchema().getIdentityConstraintDefinitions()) {
            if (eachId.getName().equals(newText.trim()))
                return Messages.EditXSDIdXXValidator_KeyAlreadyExist;
        }

        return null;
    }

}
