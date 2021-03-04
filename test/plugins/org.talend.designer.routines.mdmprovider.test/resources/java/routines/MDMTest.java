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
package routines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;


/**
 * DOC class global comment. Detailled comment
 */

@SuppressWarnings("nls")
public class MDMTest {

    @Test
    public void testGetLanguageVariant() {
        String result1 = MDM.getLanguageVariant("EN", "[EN:US :: Las Angeles][ZH:China : Beijing :]");
        assertEquals("US :: Las Angeles", result1);

        String result2 = MDM.getLanguageVariant("ZH", "[EN:US :: Las Angeles][ZH:China : Beijing :]");
        assertEquals("China : Beijing :", result2);

        String result3 = MDM.getLanguageVariant("EN", "[EN:Product&Store][ZH:Product#Store]");
        assertEquals("Product&Store", result3);

        String result4 = MDM.getLanguageVariant("ZH", "[EN:Product&Store][ZH:Product#Store]");
        assertEquals("Product#Store", result4);

        String result5 = MDM.getLanguageVariant("FR", "[EN: Las Angeles][ZH: Beijing]");
        assertNull(result5);

        String result6 = MDM.getLanguageVariant("EN", "[EN Las Angeles][ZH: Beijing]");
        assertNull(result6);
    }

    @Test
    public void testSetLanguageVariant() {
        String result7 = MDM.setLanguageVariant("FR", "France#French", "China&Chinese", "ZH", true);
        assertEquals("[FR:France#French][ZH:China&Chinese]", result7);

        String result8 = MDM.setLanguageVariant("FR", "France#French", "England:English", null, true);
        assertEquals("[EN:England:English][FR:France#French]", result8);
    }

}

