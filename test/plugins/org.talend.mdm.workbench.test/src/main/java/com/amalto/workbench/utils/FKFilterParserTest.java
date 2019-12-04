// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
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
import static org.mockito.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.amalto.workbench.models.KeyValue;
import com.amalto.workbench.models.Line;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ FKFilterParser.class })
public class FKFilterParserTest {

    private Logger log = Logger.getLogger(FKFilterParser.class);

    @Test
    public void testBuildLine() {
        String criteria = "Store/Long$$=$$111$$And#Store/Address$$Contains$$shanghai$$Or#"; //$NON-NLS-1$
        String[] keyNames = { "XPath", "Operator", "Value", "Predicate" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        String[] values1 = { "Store/Long", "=", "111", "And" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        String[] values2 = { "Store/Address", "Contains", "shanghai", "Or" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        List<KeyValue> keyValues1 = new ArrayList<KeyValue>();
        List<KeyValue> keyValues2 = new ArrayList<KeyValue>();
        for (int i = 0; i < keyNames.length && i < values1.length; i++) {
            keyValues1.add(new KeyValue(keyNames[i], values1[i]));
            keyValues2.add(new KeyValue(keyNames[i], values2[i]));
        }
        Line line1 = new Line(keyValues1);
        Line line2 = new Line(keyValues2);

        try {
            PowerMockito.mockStatic(FKFilterParser.class);
            PowerMockito.when(FKFilterParser.class, "buildLine", anyString(), any(String[].class)).thenCallRealMethod(); //$NON-NLS-1$
            PowerMockito.when(FKFilterParser.class, "buildKeyValue", any(String[].class), any(String[].class)) //$NON-NLS-1$
                    .thenCallRealMethod();
            List<Line> lines = Whitebox.invokeMethod(FKFilterParser.class, "buildLine", criteria, keyNames); //$NON-NLS-1$
            assertNotNull(lines);
            assertEquals(2, lines.size());
            assertTrue(lines.contains(line1));
            assertTrue(lines.contains(line2));

            lines = Whitebox.invokeMethod(FKFilterParser.class, "buildLine", (String) null, keyNames); //$NON-NLS-1$
            assertTrue(lines.size() == 0);

            lines = Whitebox.invokeMethod(FKFilterParser.class, "buildLine", criteria, null); //$NON-NLS-1$
            assertTrue(lines.size() == 0);

            lines = Whitebox.invokeMethod(FKFilterParser.class, "buildLine", (String) null, null); //$NON-NLS-1$
            assertTrue(lines.size() == 0);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testGetDeParseredFilter() {
        String criteria = "Store/Long$$=$$111$$And#Store/Address$$Contains$$shanghai$$Or#"; //$NON-NLS-1$
        String[] keyNames = { "XPath", "Operator", "Value", "Predicate" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        String[] values1 = { "Store/Long", "=", "111", "And" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        String[] values2 = { "Store/Address", "Contains", "shanghai", "Or" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        List<KeyValue> keyValues1 = new ArrayList<KeyValue>();
        List<KeyValue> keyValues2 = new ArrayList<KeyValue>();
        for (int i = 0; i < keyNames.length && i < values1.length; i++) {
            keyValues1.add(new KeyValue(keyNames[i], values1[i]));
            keyValues2.add(new KeyValue(keyNames[i], values2[i]));
        }
        Line line1 = new Line(keyValues1);
        Line line2 = new Line(keyValues2);

        String deParseredFilter = FKFilterParser.getDeParseredFilter(Arrays.asList(line1, line2));
        assertEquals(criteria, deParseredFilter);

        deParseredFilter = FKFilterParser.getDeParseredFilter(null);
        assertEquals("", deParseredFilter); //$NON-NLS-1$
    }

}
