package org.talend.mdm.repository.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class UserVarValueValidator {
        private static final String _PREFIX_USER_VAR = "${user_context."; //$NON-NLS-1$
        private static final String _SURFIX_USER_VAR = "}"; //$NON-NLS-1$
        
        /**
         * true: valid
         */
        public static boolean validate(String userVarValue){
            List<String> validUserVars = new ArrayList<String>();
            validUserVars.add(_PREFIX_USER_VAR+UserField.Id.field+_SURFIX_USER_VAR);
            validUserVars.add(_PREFIX_USER_VAR+UserField.First_Name.field+_SURFIX_USER_VAR);
            validUserVars.add(_PREFIX_USER_VAR+UserField.Last_Name.field+_SURFIX_USER_VAR);
            validUserVars.add(_PREFIX_USER_VAR+UserField.User_Name.field+_SURFIX_USER_VAR);
            validUserVars.add(_PREFIX_USER_VAR+UserField.Language.field+_SURFIX_USER_VAR);
            
            boolean result = true;
            if(StringUtils.isBlank(userVarValue)) {
                result = false;
            } else if(userVarValue.startsWith(_PREFIX_USER_VAR) && userVarValue.endsWith(_SURFIX_USER_VAR)) {
                if(!validUserVars.contains(userVarValue)){
                    String propertyFieldHead = _PREFIX_USER_VAR+UserField.Properties.field+"[\""; //$NON-NLS-1$
                    String propertyFieldTail = "\"]"+_SURFIX_USER_VAR; //$NON-NLS-1$
                    if(!(userVarValue.startsWith(propertyFieldHead) && userVarValue.endsWith(propertyFieldTail))) {
                        result = false;
                    }
                }
            }

            return result;
        }
    }