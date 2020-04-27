// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class ValidateUtilTest {

    String str = "aaaaabbbbbccccc"; //$NON-NLS-1$

    @Test
    public void testMatchCommonRegex() {
        // single char is all right
        assertTrue(ValidateUtil.matchCommonRegex("a")); //$NON-NLS-1$

        // this char - in this middle or front will be all right
        String ss[] = { "-" };//$NON-NLS-1$
        for (String s : ss) {
            String validStr1 = str + s + "a";//$NON-NLS-1$
            String validStr2 = s + "a";//$NON-NLS-1$
            boolean match1 = ValidateUtil.matchCommonRegex(validStr1);
            boolean match2 = ValidateUtil.matchCommonRegex(validStr2);
            assertTrue(match1);
            assertFalse(match2);
        }

        String validStr3 = str + "a";
        assertTrue(ValidateUtil.matchCommonRegex(validStr3));

        // invalid char in this middle or back
        String sep[] = { " ", "+", "*", "/", "\\", "(", ")", "[", "]", "{", "}", "|", "`", "~", "@", "$", "%", "^", "&" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$//$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$ //$NON-NLS-17$ //$NON-NLS-18$ //$NON-NLS-19$
        for (String s : sep) {
            String invalidStr1 = str + s + "a"; //$NON-NLS-1$
            String invalidStr2 = str + s;
            boolean matchCommon1 = ValidateUtil.matchCommonRegex(invalidStr1);
            boolean matchCommon2 = ValidateUtil.matchCommonRegex(invalidStr2);
            assertFalse(matchCommon1);
            assertFalse(matchCommon2);
            System.out.println(matchCommon1 + "" + matchCommon2); //$NON-NLS-1$
        }

        // "#", "-", "." in the back
        boolean match1 = ValidateUtil.matchCommonRegex(str + ss[0]);

        assertFalse(match1);

    }

    @Test
    public void testMatchCustomFormRegex() {
        // single char is all right
        assertTrue(ValidateUtil.matchCustomFormRegex("a")); //$NON-NLS-1$

        // this three char in this middle or front will be all right
        String ss[] = { "#", "." };//$NON-NLS-1$//$NON-NLS-2$
        for (String s : ss) {
            String validStr1 = str + s + "a";//$NON-NLS-1$
            String validStr2 = s + "a";//$NON-NLS-1$
            boolean match1 = ValidateUtil.matchCustomFormRegex(validStr1);
            boolean match2 = ValidateUtil.matchCustomFormRegex(validStr2);
            assertTrue(match1);
            assertFalse(match2);
        }

        // invalid char in this middle or back
        String sep[] = { " ", "+", "-", "*", "/", "\\", "(", ")", "[", "]", "{", "}", "|", "`", "~", "@", "$", "%", "^", "&" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$//$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$ //$NON-NLS-17$ //$NON-NLS-18$ //$NON-NLS-19$ //$NON-NLS-20$
        for (String s : sep) {
            String invalidStr1 = str + s + "a"; //$NON-NLS-1$
            String invalidStr2 = str + s;
            boolean matchCommon1 = ValidateUtil.matchCustomFormRegex(invalidStr1);
            boolean matchCommon2 = ValidateUtil.matchCustomFormRegex(invalidStr2);
            assertFalse(matchCommon1);
            assertFalse(matchCommon2);
            System.out.println(matchCommon1 + "" + matchCommon2); //$NON-NLS-1$
        }

        // "#", "." in the back
        boolean match1 = ValidateUtil.matchCustomFormRegex(str + ss[0]);
        boolean match2 = ValidateUtil.matchCustomFormRegex(str + ss[1]);

        assertFalse(match1);
        assertFalse(match2);
    }

    @Test
    public void testMatchViewProcessRegex() {
        // single char is all right
        assertTrue(ValidateUtil.matchViewProcessRegex("a")); //$NON-NLS-1$

        // this three char in this middle or front will be all right
        String ss[] = { "#", "." };//$NON-NLS-1$//$NON-NLS-2$
        for (String s : ss) {
            String validStr1 = str + s + "a";//$NON-NLS-1$
            String validStr2 = s + "a";//$NON-NLS-1$
            boolean match1 = ValidateUtil.matchViewProcessRegex(validStr1);
            boolean match2 = ValidateUtil.matchViewProcessRegex(validStr2);
            assertTrue(match1);
            assertFalse(match2);
        }
        for (String s : ss) {
            String validStr1 = str + s + "#";//$NON-NLS-1$
            String validStr2 = s + "#";//$NON-NLS-1$
            boolean match1 = ValidateUtil.matchViewProcessRegex(validStr1);
            boolean match2 = ValidateUtil.matchViewProcessRegex(validStr2);
            assertTrue(match1);
            assertFalse(match2);
        }
        assertTrue(ValidateUtil.matchViewProcessRegex(str + "#")); //$NON-NLS-1$

        // invalid char in this middle or back
        String sep[] = { " ", "+", "-", "*", "/", "\\", "(", ")", "[", "]", "{", "}", "|", "`", "~", "@", "$", "%", "^", "&" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$//$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$ //$NON-NLS-17$ //$NON-NLS-18$ //$NON-NLS-19$ //$NON-NLS-20$
        for (String s : sep) {
            String invalidStr1 = str + s + "a"; //$NON-NLS-1$
            String invalidStr2 = str + s;
            boolean matchCommon1 = ValidateUtil.matchCustomFormRegex(invalidStr1);
            boolean matchCommon2 = ValidateUtil.matchCustomFormRegex(invalidStr2);
            assertFalse(matchCommon1);
            assertFalse(matchCommon2);
            System.out.println(matchCommon1 + "" + matchCommon2); //$NON-NLS-1$
        }

        // "#", "." in the back
        boolean match1 = ValidateUtil.matchCustomFormRegex(str + "."); //$NON-NLS-1$

        assertFalse(match1);

    }

    @Test
    public void testMatchSmartViewRegex() throws Exception {
        String smartViewProcess = "Smart_view_a";
        boolean matchSmartViewRegex = ValidateUtil.matchSmartViewRegex(smartViewProcess);
        assertTrue(matchSmartViewRegex);

        smartViewProcess = "Smart_view_a#b";
        matchSmartViewRegex = ValidateUtil.matchSmartViewRegex(smartViewProcess);
        assertTrue(matchSmartViewRegex);

        smartViewProcess = "Smart_view_a#b#c";
        matchSmartViewRegex = ValidateUtil.matchSmartViewRegex(smartViewProcess);
        assertFalse(matchSmartViewRegex);
    }

    @Test
    public void testMatchRoleRegex() {
        // single char is all right
        assertTrue(ValidateUtil.matchRoleRegex("a")); //$NON-NLS-1$

        // this two char in this middle or front will be all right
        String ss[] = { "#" };//$NON-NLS-1$
        for (String s : ss) {
            String validStr1 = str + s + "a";//$NON-NLS-1$
            String validStr2 = s + "a";//$NON-NLS-1$
            boolean match1 = ValidateUtil.matchRoleRegex(validStr1);
            boolean match2 = ValidateUtil.matchRoleRegex(validStr2);
            assertTrue(match1);
            assertFalse(match2);
        }

        // invalid char in this middle or back
        String sep[] = {
                " ", "+", "-", ".", "*", "/", "\\", "(", ")", "[", "]", "{", "}", "|", "`", "~", "@", "$", "%", "^", "&", "*" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$//$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$ //$NON-NLS-17$ //$NON-NLS-18$ //$NON-NLS-19$ //$NON-NLS-20$ //$NON-NLS-21$ //$NON-NLS-22$
        for (String s : sep) {
            String invalidStr1 = str + s + "a"; //$NON-NLS-1$
            String invalidStr2 = str + s;
            boolean matchCommon1 = ValidateUtil.matchRoleRegex(invalidStr1);
            boolean matchCommon2 = ValidateUtil.matchRoleRegex(invalidStr2);
            assertFalse(matchCommon1);
            assertFalse(matchCommon2);
        }

        // "#" in the back
        boolean match1 = ValidateUtil.matchRoleRegex(str + ss[0]);

        assertFalse(match1);

        String[] sys_buf = buildSystemRole();

        for (String sysRole : sys_buf) {
            assertFalse(ValidateUtil.matchRoleRegex(sysRole) && !ValidateUtil.isSystemRoleName(sysRole));
        }
    }

    private String[] buildSystemRole() {
        String sys_like = "system_"; //$NON-NLS-1$
        String sys_admin = "administration"; //$NON-NLS-1$
        Random random = new Random();
        int randomNum = random.nextInt(10) + 1;
        for (int i = 0; i < randomNum; i++) {
            int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
            if (random.nextBoolean()) {
                sys_like += (char) (random.nextInt(26) + temp);
            } else {
                sys_like += random.nextInt(10);
            }
        }

        StringBuffer syslike_buf = new StringBuffer();
        for (int i = 0; i < sys_like.length(); i++) {
            char charA = sys_like.charAt(i);
            if (random.nextBoolean() && Character.isLetter(charA)) {
                syslike_buf.append(Character.toUpperCase(charA));
            } else {
                syslike_buf.append(charA);
            }
        }
        StringBuffer sysadmin_buf = new StringBuffer();
        for (int i = 0; i < sys_admin.length(); i++) {
            if (random.nextBoolean()) {
                sysadmin_buf.append(Character.toUpperCase(sys_admin.charAt(i)));
            } else {
                sysadmin_buf.append(sys_admin.charAt(i));
            }
        }
        return new String[] { syslike_buf.toString(), sysadmin_buf.toString() };
    }
}
