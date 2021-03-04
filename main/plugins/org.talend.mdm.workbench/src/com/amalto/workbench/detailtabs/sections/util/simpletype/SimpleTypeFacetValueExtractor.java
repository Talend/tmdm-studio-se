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
package com.amalto.workbench.detailtabs.sections.util.simpletype;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.xsd.XSDFacet;

public class SimpleTypeFacetValueExtractor {

    private SimpleTypeFacetValueExtractor() {
    }

    public static String[] getStringArrayFacetValue(List<?> values) {

        List<String> enums = new ArrayList<String>();
        for (Object eachFacetValue : values) {

            if (!(eachFacetValue instanceof XSDFacet))
                continue;

            enums.add(((XSDFacet) eachFacetValue).getLexicalValue());
        }

        return enums.toArray(new String[0]);

    }

    public static String getStringFacetValue(XSDFacet facet) {

        if (facet == null)
            return "";//$NON-NLS-1$

        String result = "";//$NON-NLS-1$

        try {
            result = facet.getLexicalValue();
        } catch (Exception e) {
            return "";//$NON-NLS-1$
        }

        return (result == null ? "" : result);//$NON-NLS-1$
    }

    public static Integer getIntFacetValue(XSDFacet facet) {

        if (facet == null) {
            return null;
        }

        Integer result = 0;

        try {
            result = Integer.parseInt(facet.getLexicalValue());
        } catch (Exception e) {
            return 0;
        }

        return result;

    }

    public static Double getDoubleFacetValue(XSDFacet facet) {

        if (facet == null) {
            return null;
        }

        Double result = 0d;

        try {
            result = Double.parseDouble(facet.getLexicalValue());
        } catch (Exception e) {
            return 0d;
        }

        return result;

    }
}
