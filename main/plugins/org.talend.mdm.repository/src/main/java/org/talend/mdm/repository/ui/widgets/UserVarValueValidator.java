package org.talend.mdm.repository.ui.widgets;

import java.util.ArrayList;
import java.util.List;

public class UserVarValueValidator {

    private static final String _PREFIX_USER_VAR = "${user_context."; //$NON-NLS-1$

    private static final String _SURFIX_USER_VAR = "}"; //$NON-NLS-1$

    /**
     * True: Is valid
     */
    public static boolean validate(String userVarValue) {
        List<String> validUserVars = new ArrayList<>();
        validUserVars.add(_PREFIX_USER_VAR + UserField.Id.field + _SURFIX_USER_VAR);
        validUserVars.add(_PREFIX_USER_VAR + UserField.First_Name.field + _SURFIX_USER_VAR);
        validUserVars.add(_PREFIX_USER_VAR + UserField.Last_Name.field + _SURFIX_USER_VAR);
        validUserVars.add(_PREFIX_USER_VAR + UserField.User_Name.field + _SURFIX_USER_VAR);
        validUserVars.add(_PREFIX_USER_VAR + UserField.Language.field + _SURFIX_USER_VAR);

        String propertyFieldHead = _PREFIX_USER_VAR + UserField.Properties.field + "[\""; //$NON-NLS-1$
        String propertyFieldTail = "\"]" + _SURFIX_USER_VAR; //$NON-NLS-1$

        boolean isUserVarPattern = userVarValue.startsWith(_PREFIX_USER_VAR) && userVarValue.endsWith(_SURFIX_USER_VAR);
        boolean propPattern = userVarValue.startsWith(propertyFieldHead) && userVarValue.endsWith(propertyFieldTail)
                && userVarValue.length() > propertyFieldHead.length() + propertyFieldTail.length();
        if (isUserVarPattern && !validUserVars.contains(userVarValue) && !propPattern) {
            return false;
        }

        return true;
    }
}