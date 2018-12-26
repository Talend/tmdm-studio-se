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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

@SuppressWarnings("nls")
public class UserVarValueValidatorTest {

    @Test
    public void testValidate() {
        String _PREFIX_USER_VAR = "${user_context.";
        String _SUFFIX_USER_VAR = "}";
        List<String> validUserVars = new ArrayList<>();

        validUserVars.add(_PREFIX_USER_VAR + UserField.Id.field + _SUFFIX_USER_VAR);
        validUserVars.add(_PREFIX_USER_VAR + UserField.First_Name.field + _SUFFIX_USER_VAR);
        validUserVars.add(_PREFIX_USER_VAR + UserField.Last_Name.field + _SUFFIX_USER_VAR);
        validUserVars.add(_PREFIX_USER_VAR + UserField.User_Name.field + _SUFFIX_USER_VAR);
        validUserVars.add(_PREFIX_USER_VAR + UserField.Language.field + _SUFFIX_USER_VAR);

        UserVarValueValidator userVarValueValidator = new UserVarValueValidator();
        // check normal user fields, except 'properties' field
        for (String val : validUserVars) {
            assertTrue(userVarValueValidator.validate(val));
        }

        int len = new Random().nextInt() % 10;
        len = len > 0 ? len : 1;

        // check any invalid user fields
        String invalidFields = getRandomString(len);
        if (validUserVars.contains(_PREFIX_USER_VAR + invalidFields + _SUFFIX_USER_VAR)) {
            invalidFields = "a" + invalidFields;
        }
        String invalidUserVar = _PREFIX_USER_VAR + invalidFields + _SUFFIX_USER_VAR;
        assertFalse(userVarValueValidator.validate(invalidUserVar));

        // check 'properties' field,but without specified property name
        String propertyFieldHead = _PREFIX_USER_VAR + UserField.Properties.field + "[\"";
        String propertyFieldTail = "\"]" + _SUFFIX_USER_VAR;
        String userPropValue = propertyFieldHead + propertyFieldTail;
        assertFalse(userVarValueValidator.validate(userPropValue));

        // check 'properties' field
        userPropValue = propertyFieldHead + getRandomString(len) + propertyFieldTail;
        assertTrue(userVarValueValidator.validate(userPropValue));

        // check normal string
        String randomProp = getRandomString(len);
        assertTrue(userVarValueValidator.validate(randomProp));
    }

    private String getRandomString(int length) {
        String KeyString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuffer sb = new StringBuffer(length);
        int len = KeyString.length();
        for (int i = 0; i < length; i++) {
            sb.append(KeyString.charAt((int) Math.round(Math.random() * (len - 1))));
        }

        return sb.toString();
    }
}
