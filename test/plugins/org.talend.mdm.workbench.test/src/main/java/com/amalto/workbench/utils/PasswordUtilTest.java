package com.amalto.workbench.utils;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.utils.security.CryptoHelper;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PasswordUtil.class, CryptoHelper.class })
public class PasswordUtilTest {

    @Test
    public void testDecryptPasswordStringString() {
        String encodedPassword = null;
        String decryptPassword_expect = "decryptPassword"; //$NON-NLS-1$

        //
        try {
            PasswordUtil.decryptPassword(encodedPassword, null);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //
        String algorithm = null;
        PowerMockito.mockStatic(PasswordUtil.class);
        PowerMockito.when(PasswordUtil.decryptPassword(anyString(), anyString())).thenCallRealMethod();
        PowerMockito.when(PasswordUtil.decryptPasswordBase64(anyString())).thenReturn(decryptPassword_expect);

        encodedPassword = "ecodedPassword"; //$NON-NLS-1$
        String decryptPassword = PasswordUtil.decryptPassword(encodedPassword, algorithm);
        assertEquals(decryptPassword_expect, decryptPassword);
        PowerMockito.verifyStatic(null, null);
        PasswordUtil.decryptPasswordBase64(encodedPassword);

        //
        algorithm = "arbitrary algorithm";// on behalf of arbitrary algorithm,not null //$NON-NLS-1$
        decryptPassword = PasswordUtil.decryptPassword(encodedPassword, algorithm);
        assertEquals(decryptPassword_expect, decryptPassword);
        PowerMockito.verifyStatic(null, times(2));
        PasswordUtil.decryptPasswordBase64(encodedPassword);

        //
        algorithm = PasswordUtil.ALGORITHM_COMMON;
        decryptPassword = PasswordUtil.decryptPassword(encodedPassword, algorithm);
        assertNull(decryptPassword);

        //
        algorithm = PasswordUtil.ALGORITHM_COMMON_V2;
        PowerMockito.mockStatic(CryptoHelper.class);
        CryptoHelper mockCryptoHelper = PowerMockito.mock(CryptoHelper.class);
        String decryptPassword_expect2 = decryptPassword_expect + "2"; //$NON-NLS-1$
        PowerMockito.when(mockCryptoHelper.decrypt(anyString())).thenReturn(decryptPassword_expect2);
        PowerMockito.when(CryptoHelper.getDefault()).thenReturn(mockCryptoHelper);
        decryptPassword = PasswordUtil.decryptPassword(encodedPassword, algorithm);
        assertEquals(decryptPassword_expect2, decryptPassword);
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
        String encryptedPassword_expect = "encryptedPassword"; //$NON-NLS-1$

        //
        try {
            PasswordUtil.encryptPassword(plainPassword, null);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //
        String algorithm = null;
        PowerMockito.mockStatic(PasswordUtil.class);
        PowerMockito.when(PasswordUtil.encryptPassword(anyString(), anyString())).thenCallRealMethod();
        PowerMockito.when(PasswordUtil.encryptPasswordBase64(anyString())).thenReturn(encryptedPassword_expect);

        plainPassword = "plainPassword"; //$NON-NLS-1$
        String encryptedPassword = PasswordUtil.encryptPassword(plainPassword, algorithm);
        assertEquals(encryptedPassword_expect, encryptedPassword);
        PowerMockito.verifyStatic(null, null);
        PasswordUtil.encryptPasswordBase64(plainPassword);

        //
        algorithm = "arbitrary algorithm";// on behalf of arbitrary algorithm,not null //$NON-NLS-1$
        encryptedPassword = PasswordUtil.encryptPassword(plainPassword, algorithm);
        assertEquals(encryptedPassword_expect, encryptedPassword);
        PowerMockito.verifyStatic(null, times(2));
        PasswordUtil.encryptPasswordBase64(plainPassword);

        //
        algorithm = PasswordUtil.ALGORITHM_COMMON;
        encryptedPassword = PasswordUtil.encryptPassword(plainPassword, algorithm);
        assertNull(encryptedPassword);

        //
        algorithm = PasswordUtil.ALGORITHM_COMMON_V2;
        PowerMockito.mockStatic(CryptoHelper.class);
        CryptoHelper mockCryptoHelper = PowerMockito.mock(CryptoHelper.class);
        String encryptedPassword_expect2 = encryptedPassword_expect + "2"; //$NON-NLS-1$
        PowerMockito.when(mockCryptoHelper.encrypt(anyString())).thenReturn(encryptedPassword_expect2);
        PowerMockito.when(CryptoHelper.getDefault()).thenReturn(mockCryptoHelper);
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
