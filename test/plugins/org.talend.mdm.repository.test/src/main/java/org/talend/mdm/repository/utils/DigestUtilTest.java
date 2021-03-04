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

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.junit.Test;


public class DigestUtilTest {

    private static Logger log = Logger.getLogger(DigestUtilTest.class);

    @Test
    public void testEncodeByMD5() {
        String encodeStr = "This is the content to be encoded"; //$NON-NLS-1$
        try {
            String encodedByMD5 = DigestUtil.encodeByMD5(encodeStr.getBytes("utf-8")); //$NON-NLS-1$
            assertNotNull(encodedByMD5);
            assertEquals(32, encodedByMD5.length());
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
    }


}
