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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * DOC hbhong  class global comment. Detailled comment
 */
public class TestUtil {

    /**
     * Used to read test resource
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static String readTestResource(Class cls, String fileName) throws Exception {
        InputStreamReader fr = null;
        BufferedReader br = null;
        try {
            InputStream inputStream = cls.getResourceAsStream(fileName);
            fr = new InputStreamReader(inputStream);
            br = new BufferedReader(fr);

            StringBuffer buffer = new StringBuffer();
            String tmpStr;
            while ((tmpStr = br.readLine()) != null) {
                buffer.append(tmpStr);
            }
            return buffer.toString();
        } finally {
            if (br != null) {
                br.close();
            }
            if (fr != null) {
                fr.close();
            }
        }

    }
}
