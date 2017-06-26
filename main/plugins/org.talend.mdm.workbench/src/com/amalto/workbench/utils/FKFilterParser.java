// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.amalto.workbench.models.KeyValue;
import com.amalto.workbench.models.Line;


/**
 * created by liusongbo on 2013-2-28
 *
 */
public class FKFilterParser {

    public static final String endSeparator = "#"; //$NON-NLS-1$

    private static final String two$$ = "$$";//$NON-NLS-1$

    private static final String quot = "&quot;";//$NON-NLS-1$

    public static void parseFilter(String filter, List<Line> lines, String[] keyNames) {
        if (filter != null && filter.length() > 0) {
            lines.addAll(buildLine(filter, keyNames));
        }
    }

    private static List<Line> buildLine(String criteria, String[] keyNames) {
        List<Line> lines = new ArrayList<Line>();
        if (criteria != null && keyNames != null) {
            String[] criterias = criteria.split(endSeparator);
            for (String cria : criterias) {
                String[] values = cria.split("\\$\\$");//$NON-NLS-1$
                List<String> list = new ArrayList<String>();
                list.addAll(Arrays.asList(values));
                int num = 4 - list.size();
                for (int i = 0; i < num; i++) {
                    list.add("");//$NON-NLS-1$
                }
                // filter value
                if (list.get(2) != null && list.get(2).length() > 0) {
                    String value = list.get(2);
                    value = value.replaceAll(quot, "\"");//$NON-NLS-1$
                    list.set(2, value);
                }

                List<KeyValue> keyValues = buildKeyValue(keyNames, list.toArray(new String[list.size()]));
                Line line = new Line(keyValues);
                lines.add(line);
            }
        }
        return lines;
    }

    private static List<KeyValue> buildKeyValue(String[] keyNames, String[] values) {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        for (int i = 0; i < keyNames.length && i < values.length; i++) {
            keyValues.add(new KeyValue(keyNames[i], values[i]));
        }

        return keyValues;
    }

    public static String getDeParseredFilter(List<Line> lines) {
        StringBuffer sb = new StringBuffer();
        if (lines != null) {
            for (Line line : lines) {
                String xpath = line.keyValues.get(0).value;
                String operator = line.keyValues.get(1).value;
                String value = line.keyValues.get(2).value;
                value = normalizeValue(value);
                String predicate = line.keyValues.get(3).value;
                sb.append(xpath + two$$ + operator + two$$ + value + two$$ + predicate + endSeparator);
            }
        }
        return sb.toString();
    }

    private static String normalizeValue(String value) {
        String innerValue = value;
        if (innerValue != null && value.trim().length() > 0) {
            innerValue = value.replaceAll("\"", quot);//$NON-NLS-1$
            innerValue = innerValue.replaceAll("'", quot);//$NON-NLS-1$
        }

        return innerValue;
    }

}
