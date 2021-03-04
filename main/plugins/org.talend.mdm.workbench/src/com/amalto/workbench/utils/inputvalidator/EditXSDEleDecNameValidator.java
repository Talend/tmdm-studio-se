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
import org.eclipse.xsd.XSDSchema;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.XSDUtil;

public class EditXSDEleDecNameValidator implements IInputValidator {

    private XSDSchema schema;

    public EditXSDEleDecNameValidator(XSDSchema schema) {
        this.schema = schema;
    }

    // @Override
    public String isValid(String newText) {
        if (newText == null || "".equals(newText.trim())) {
            return Messages.EditXSDEleDecNameValidator_EntityNameCannotbeEmpty;
        }

        if (Pattern.compile("^\\s+\\w+\\s*").matcher(newText).matches()//$NON-NLS-1$
                || newText.trim().replaceAll("\\s", "").length() != newText.trim().length()) {
            return Messages.EditXSDEleDecNameValidator_EntityNameCannotContainEmpty;
        }

        if (!XSDUtil.isValidatedXSDName(newText)) {
            return Messages.InvalidName_Message;
        }

        for (XSDElementDeclaration eachElement : schema.getElementDeclarations()) {
            if (eachElement.getName().equalsIgnoreCase(newText.trim())) {
                return Messages.EditXSDEleDecNameValidator_EntityAlreadyExist;
            }
        }

        return null;
    }
}
