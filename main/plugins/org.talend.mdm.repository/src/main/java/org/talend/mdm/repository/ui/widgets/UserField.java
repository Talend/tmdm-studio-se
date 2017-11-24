package org.talend.mdm.repository.ui.widgets;

public enum UserField {
        // TAC
        User_Name("username"), //$NON-NLS-1$
        First_Name("givenname"), //$NON-NLS-1$
        Last_Name("familyname"), //$NON-NLS-1$
        //MDM
        Id("id"), //$NON-NLS-1$
        Language("language"), //$NON-NLS-1$
        Properties("properties"); //$NON-NLS-1$
        
        ////////
        String field;
        UserField(String f) {
            this.field = f;
        }
        
        public static boolean isValidUserField(String ufield){
            boolean contains = false;
            for(UserField userfield:values()) {
                if(userfield.field.equals(ufield)) {
                    contains = true;
                    break;
                }
            }
            return contains;
        }
    }