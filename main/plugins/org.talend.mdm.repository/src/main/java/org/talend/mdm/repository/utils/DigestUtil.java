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
package org.talend.mdm.repository.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

/**
 * created by HHB on 2013-7-15 Detailled comment
 *
 */
public class DigestUtil {

    static Logger log = Logger.getLogger(DigestUtil.class);

    public static String encodeByMD5(byte[] content) {
        try {
            MessageDigest alg = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
            alg.reset();
            alg.update(content);
            byte[] digest = alg.digest();
            StringBuffer md5StrBuff = new StringBuffer();

            for (byte element : digest) {
                if (Integer.toHexString(0xFF & element).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & element)); //$NON-NLS-1$
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & element));
                }
            }
            return md5StrBuff.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        }
        return null;

    }
}
