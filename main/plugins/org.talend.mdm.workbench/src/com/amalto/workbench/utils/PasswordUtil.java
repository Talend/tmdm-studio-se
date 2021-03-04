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

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.talend.utils.security.StudioEncryption;


public class PasswordUtil {

    private static final Logger LOGGER = Logger.getLogger(PasswordUtil.class);

    public static final String ALGORITHM_COMMON = "Common"; //$NON-NLS-1$

    public static final String ALGORITHM_COMMON_V2 = "CommonV2"; //$NON-NLS-1$

    public static final String ALGORITHM_COMMON_V3 = "CommonV3"; //$NON-NLS-1$

    public static String decryptPassword(String encodedPassword, String algorithm) {
        if (encodedPassword == null) {
            throw new IllegalArgumentException();
        }
        if (algorithm != null) {
            if (algorithm.equals(ALGORITHM_COMMON_V3)) {
                try {
                    return decryptPasswordAES(encodedPassword);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    return null;
                }
            } else if (algorithm.equals(ALGORITHM_COMMON) || algorithm.equals(ALGORITHM_COMMON_V2)) {
                // not support ALGORITHM_COMMON and ALGORITHM_COMMON_V2 ,it will be upgraded by migration task
                return null;
            }
        }
        return decryptPasswordBase64(encodedPassword);
    }

    public static String decryptPasswordAES(String encodedPassword) {
        return StudioEncryption.getStudioEncryption(StudioEncryption.EncryptionKeyName.SYSTEM).decrypt(encodedPassword);
    }

    public static String decryptPassword(String encodedPassword) {
        return decryptPassword(encodedPassword, ALGORITHM_COMMON_V3);
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
            if (algorithm.equals(ALGORITHM_COMMON_V3)) {
                try {
                    return encryptPasswordAES(plainPassword);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    return null;
                }
            } else if (algorithm.equals(ALGORITHM_COMMON) || algorithm.equals(ALGORITHM_COMMON_V2)) {
                // not support ALGORITHM_COMMON and ALGORITHM_COMMON_V2,it will be upgraded by migration task
                return null;
            }
        }

        return encryptPasswordBase64(plainPassword);

    }

    public static String encryptPasswordAES(String plainPassword) {
        return StudioEncryption.getStudioEncryption(StudioEncryption.EncryptionKeyName.SYSTEM)
                .encrypt(plainPassword);
    }

    public static String encryptPassword(String plainPassword) {
        return encryptPassword(plainPassword, ALGORITHM_COMMON_V3);
    }

    public static String encryptPasswordBase64(String plainPassword) {
        Base64 base64 = new Base64();
        byte[] enbytes = null;
        String encodeStr = null;
        enbytes = base64.encode(plainPassword.getBytes());
        encodeStr = new String(enbytes);
        return encodeStr;
    }
}
