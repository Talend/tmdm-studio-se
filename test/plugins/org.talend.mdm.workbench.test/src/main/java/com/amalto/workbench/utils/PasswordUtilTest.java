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
package com.amalto.workbench.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PasswordUtilTest {
    @Test
    public void testDecryptPasswordStringString() throws Exception {
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
        decryptPassword = PasswordUtil.decryptPassword(encodedPassword, algorithm);
        assertNull(decryptPassword);

        //
        algorithm = PasswordUtil.ALGORITHM_COMMON_V3;
        String encryptPasswordAES = "enc:system.encryption.key.v1:Fxw4XQoKfAbUuEzhUodUEx2q93zLxY1Tv9nGRs6xiUlBW/xyNSWUTBE=";
        decryptPassword = PasswordUtil.decryptPassword(encryptPasswordAES, algorithm);
        assertEquals(plainPassword, decryptPassword);
        assertEquals(plainPassword, PasswordUtil.decryptPasswordAES(encryptPasswordAES));
    }

    @Test
    public void testDecryptPasswordBase64() {
        String plainPassword = "plainPassword"; //$NON-NLS-1$
        String encryptPasswordBase64 = "cGxhaW5QYXNzd29yZA=="; //$NON-NLS-1$
        String decryptPasswordBase64 = PasswordUtil.decryptPasswordBase64(encryptPasswordBase64);
        assertEquals(plainPassword, decryptPasswordBase64);
    }

    @Test
    public void testEncryptPasswordStringString() throws Exception {
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
        encryptedPassword = PasswordUtil.encryptPassword(plainPassword, algorithm);
        assertNull(encryptedPassword);

        //
        algorithm = PasswordUtil.ALGORITHM_COMMON_V3;
        
        encryptedPassword = PasswordUtil.encryptPassword(plainPassword, algorithm);
        assertTrue(encryptedPassword.startsWith("enc:system.encryption.key"));
    }

    @Test
    public void testEncryptPasswordBase64() {
        String plainPassword = "plainPassword"; //$NON-NLS-1$
        String encryptPasswordBase64 = "cGxhaW5QYXNzd29yZA=="; //$NON-NLS-1$
        String encryptPasswordBase64_2 = PasswordUtil.encryptPasswordBase64(plainPassword);
        assertEquals(encryptPasswordBase64, encryptPasswordBase64_2);
    }

    @Test
    public void testEncryptPasswordAES() {
        String plainPassword = "plainPassword"; //$NON-NLS-1$
        String encrypted = PasswordUtil.encryptPasswordAES(plainPassword);
        assertTrue(encrypted.startsWith("enc:system.encryption.key"));
    }

    @Test
    public void decryptPasswordAES() {
        String plainPassword = "plainPassword"; //$NON-NLS-1$
        String encryptPasswordAES = "enc:system.encryption.key.v1:Fxw4XQoKfAbUuEzhUodUEx2q93zLxY1Tv9nGRs6xiUlBW/xyNSWUTBE=";
        String dencrypted = PasswordUtil.decryptPasswordAES(encryptPasswordAES);
        assertEquals(plainPassword, dencrypted);
    }
}
