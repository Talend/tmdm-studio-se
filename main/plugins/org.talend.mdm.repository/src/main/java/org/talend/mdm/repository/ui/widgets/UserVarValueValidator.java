// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.ui.widgets;

import java.util.ArrayList;
import java.util.List;

public class UserVarValueValidator {

    private static final String _PREFIX_USER_VAR = "${user_context."; //$NON-NLS-1$

    private static final String _SUFFIX_USER_VAR = "}"; //$NON-NLS-1$

    private static List<String> validUserVars = new ArrayList<>();

    static {
        validUserVars.add(_PREFIX_USER_VAR + UserField.Id.field + _SUFFIX_USER_VAR);
        validUserVars.add(_PREFIX_USER_VAR + UserField.First_Name.field + _SUFFIX_USER_VAR);
        validUserVars.add(_PREFIX_USER_VAR + UserField.Last_Name.field + _SUFFIX_USER_VAR);
        validUserVars.add(_PREFIX_USER_VAR + UserField.User_Name.field + _SUFFIX_USER_VAR);
        validUserVars.add(_PREFIX_USER_VAR + UserField.Language.field + _SUFFIX_USER_VAR);
    }

    /**
     * True: Is valid
     */
    public boolean validate(String userVarValue) {
        boolean isUserVarPattern = userVarValue.startsWith(_PREFIX_USER_VAR) && userVarValue.endsWith(_SUFFIX_USER_VAR);
        if (isUserVarPattern) {
            String propertyFieldHead = _PREFIX_USER_VAR + UserField.Properties.field + "[\""; //$NON-NLS-1$
            String propertyFieldTail = "\"]" + _SUFFIX_USER_VAR; //$NON-NLS-1$
            boolean propPattern = userVarValue.startsWith(propertyFieldHead) && userVarValue.endsWith(propertyFieldTail)
                    && userVarValue.length() > propertyFieldHead.length() + propertyFieldTail.length();
            if (!validUserVars.contains(userVarValue) && !propPattern) {
                return false;
            }
        }

        return true;
    }
}