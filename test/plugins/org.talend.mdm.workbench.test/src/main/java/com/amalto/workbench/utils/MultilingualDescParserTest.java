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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.amalto.workbench.widgets.MultilingualDescParser;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class MultilingualDescParserTest {

    /**
     * Test method for {@link com.amalto.workbench.widgets.DescAnnotaionParser#escapeMultiLanguageString(java.util.Map)}
     * .
     */
    @Test
    public void testEscapeMultiLanguageString() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("en", "English view description");
        map.put("fr", "French view description");
        String ret = MultilingualDescParser.escapeMultiLanguageString(map);
        assertTrue(ret.contains("EN"));
        assertTrue(ret.contains("FR"));

        map.clear();
        map.put("eN", "English view description");
        map.put("Fr", "French view description");
        ret = MultilingualDescParser.escapeMultiLanguageString(map);
        assertTrue(ret.contains("EN"));
        assertTrue(ret.contains("FR"));

        map.clear();
        map.put("Ab", "English view description");
        map.put("zh", "French view description");
        ret = MultilingualDescParser.escapeMultiLanguageString(map);
        assertTrue(ret.contains("AB"));
        assertTrue(ret.contains("ZH"));
    }

    @Test
    public void testParseMultiLanguageString() {
        Map<String, String> map = new HashMap<String, String>();

        String str = "[EN:English view description][FR:French view description]";
        MultilingualDescParser.parseMultiLanguageString(str, map);
        assertTrue(map.size() == 2);
        assertTrue(map.keySet().contains("en"));
        assertTrue(map.keySet().contains("fr"));

        map.clear();
        str = "[eN:English view description][Fr:French view description]";
        MultilingualDescParser.parseMultiLanguageString(str, map);
        assertTrue(map.size() == 2);
        assertTrue(map.keySet().contains("en"));
        assertTrue(map.keySet().contains("fr"));

        map.clear();
        str = "[ab:English view description][zh:French view description]";
        MultilingualDescParser.parseMultiLanguageString(str, map);
        assertTrue(map.size() == 2);
        assertTrue(map.keySet().contains("ab"));
        assertTrue(map.keySet().contains("zh"));
    }

}
