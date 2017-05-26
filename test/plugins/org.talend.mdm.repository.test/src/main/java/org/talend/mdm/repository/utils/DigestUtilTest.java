package org.talend.mdm.repository.utils;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;


public class DigestUtilTest {

    @Test
    public void testEncodeByMD5() {
        String encodeStr = "This is the content to be encoded"; //$NON-NLS-1$
        try {
            String encodedByMD5 = DigestUtil.encodeByMD5(encodeStr.getBytes("utf-8")); //$NON-NLS-1$
            assertNotNull(encodedByMD5);
            assertEquals(32, encodedByMD5.length());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}
