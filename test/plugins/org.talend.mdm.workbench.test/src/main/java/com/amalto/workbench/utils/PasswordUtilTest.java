// ============================================================================
//
// Copyright (C) 2006-2020 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.workbench.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PasswordUtilTest {

    @Test
    public void testDecryptPasswordStringString() {
        String encodedPassword = null;

        //
        try {
            PasswordUtil.decryptPassword(encodedPassword, null);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
        encodedPassword = "ecodedPassword"; //$NON-NLS-1$

        //
        String plainPassword = "plainPassword"; //$NON-NLS-1$
        String encryptPasswordBase64 = "cGxhaW5QYXNzd29yZA=="; //$NON-NLS-1$
        //
        String algorithm = null;
        String decryptPassword = PasswordUtil.decryptPassword(encryptPasswordBase64, algorithm);
        assertEquals(plainPassword, decryptPassword);
        assertEquals(plainPassword, PasswordUtil.decryptPasswordBase64(encryptPasswordBase64));

        //
        algorithm = "arbitrary algorithm";// on behalf of arbitrary algorithm,not null //$NON-NLS-1$
        decryptPassword = PasswordUtil.decryptPassword(encryptPasswordBase64, algorithm);
        assertEquals(plainPassword, decryptPassword);
        assertEquals(plainPassword, PasswordUtil.decryptPasswordBase64(encryptPasswordBase64));

        //
        algorithm = PasswordUtil.ALGORITHM_COMMON;
        decryptPassword = PasswordUtil.decryptPassword(encodedPassword, algorithm);
        assertNull(decryptPassword);

        //
        algorithm = PasswordUtil.ALGORITHM_COMMON_V2;
        String decryptPassword_expect = "iSyS3gRIvZZOJMYG24p3Sw==";
        decryptPassword = PasswordUtil.decryptPassword(decryptPassword_expect, algorithm);
        assertEquals(plainPassword, decryptPassword);
    }

    @Test
    public void testDecryptPasswordBase64() {
        String plainPassword = "plainPassword"; //$NON-NLS-1$
        String encryptPasswordBase64 = "cGxhaW5QYXNzd29yZA=="; //$NON-NLS-1$
        String decryptPasswordBase64 = PasswordUtil.decryptPasswordBase64(encryptPasswordBase64);
        assertEquals(plainPassword, decryptPasswordBase64);
    }

    @Test
    public void testEncryptPasswordStringString() {
        String plainPassword = null;

        //
        try {
            PasswordUtil.encryptPassword(plainPassword, null);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        plainPassword = "plainPassword"; //$NON-NLS-1$
        String encryptPasswordBase64 = "cGxhaW5QYXNzd29yZA=="; //$NON-NLS-1$
        //
        String algorithm = null;
        String encryptedPassword = PasswordUtil.encryptPassword(plainPassword, algorithm);
        assertEquals(encryptPasswordBase64, encryptedPassword);
        assertEquals(encryptPasswordBase64, PasswordUtil.encryptPasswordBase64(plainPassword));

        //
        algorithm = "arbitrary algorithm";// on behalf of arbitrary algorithm,not null //$NON-NLS-1$
        encryptedPassword = PasswordUtil.encryptPassword(plainPassword, algorithm);
        assertEquals(encryptPasswordBase64, encryptedPassword);
        assertEquals(encryptPasswordBase64, PasswordUtil.encryptPasswordBase64(plainPassword));

        //
        algorithm = PasswordUtil.ALGORITHM_COMMON;
        encryptedPassword = PasswordUtil.encryptPassword(plainPassword, algorithm);
        assertNull(encryptedPassword);
        //
        algorithm = PasswordUtil.ALGORITHM_COMMON_V2;
        String encryptedPassword_expect2 = "iSyS3gRIvZZOJMYG24p3Sw=="; //$NON-NLS-1$
        encryptedPassword = PasswordUtil.encryptPassword(plainPassword, algorithm);
        assertEquals(encryptedPassword_expect2, encryptedPassword);
    }

    @Test
    public void testEncryptPasswordBase64() {
        String plainPassword = "plainPassword"; //$NON-NLS-1$
        String encryptPasswordBase64 = "cGxhaW5QYXNzd29yZA=="; //$NON-NLS-1$
        String encryptPasswordBase64_2 = PasswordUtil.encryptPasswordBase64(plainPassword);
        assertEquals(encryptPasswordBase64, encryptPasswordBase64_2);
    }

}
