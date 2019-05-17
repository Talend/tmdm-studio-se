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
package com.amalto.workbench.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.talend.daikon.security.CryptoHelper;


public class PasswordUtil {

    private static Logger log = Logger.getLogger(PasswordUtil.class);

    public static final String ALGORITHM_COMMON = "Common"; //$NON-NLS-1$

    public static final String ALGORITHM_COMMON_V2 = "CommonV2"; //$NON-NLS-1$

    public static String decryptPassword(String encodedPassword, String algorithm) {
        if (encodedPassword == null) {
            throw new IllegalArgumentException();
        }
        if (algorithm != null) {
            if (algorithm.equals(ALGORITHM_COMMON_V2)) {
                try {
                    String decryptedPassword = CryptoHelper.getDefault().decrypt(encodedPassword);
                    return decryptedPassword;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            } else if (algorithm.equals(ALGORITHM_COMMON)) {
                // not support ALGORITHM_COMMON ,it will be upgraded by migration task
                return null;
            }
        }
        return decryptPasswordBase64(encodedPassword);
    }

    public static String decryptPassword(String encodedPassword) {
        return decryptPassword(encodedPassword, ALGORITHM_COMMON_V2);
    }

    public static String decryptPasswordBase64(String encodedPassword) {
        Base64 base64 = new Base64();
        byte[] debytes = null;
        String decodeStr = null;
        debytes = base64.decode(encodedPassword.getBytes());
        decodeStr = new String(debytes);
        return decodeStr;

    }

    public static String encryptPassword(String plainPassword, String algorithm) {
        if (plainPassword == null) {
            throw new IllegalArgumentException();
        }
        if (algorithm != null) {
            if (algorithm.equals(ALGORITHM_COMMON_V2)) {
                try {
                    return CryptoHelper.getDefault().encrypt(plainPassword);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            } else if (algorithm.equals(ALGORITHM_COMMON)) {
                // not support ALGORITHM_COMMON ,it will be upgraded by migration task
                return null;
            }
        }

        return encryptPasswordBase64(plainPassword);

    }

    public static String encryptPassword(String plainPassword) {
        return encryptPassword(plainPassword, ALGORITHM_COMMON_V2);
    }

    public static String encryptPasswordBase64(String plainPassword) {
        Base64 base64 = new Base64();
        byte[] enbytes = null;
        String encodeStr = null;
        enbytes = base64.encode(plainPassword.getBytes());
        encodeStr = new String(enbytes);
        return encodeStr;
    }

    public static void main(String args[]) {
        Base64 base64 = new Base64();
        String str = "qwe";//$NON-NLS-1$
        byte[] enbytes = null;
        String encodeStr = null;
        byte[] debytes = null;
        String decodeStr = null;
        enbytes = base64.encode(str.getBytes());
        encodeStr = new String(enbytes);
        debytes = base64.decode(enbytes);
        decodeStr = new String(debytes);
        System.out.println("plain password:" + str); //$NON-NLS-1$
        System.out.println("encrypted password:" + encodeStr); //$NON-NLS-1$
        System.out.println("decrypted password:" + decodeStr); //$NON-NLS-1$
    }
}
