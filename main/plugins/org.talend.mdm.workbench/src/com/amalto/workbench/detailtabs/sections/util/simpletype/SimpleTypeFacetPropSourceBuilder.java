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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.widgets.composites.property.IPropertySource;

public abstract class SimpleTypeFacetPropSourceBuilder {

    private static Map<String, SimpleTypeFacetPropSourceBuilder> facetName2Builder = new HashMap<String, SimpleTypeFacetPropSourceBuilder>();

    public static IPropertySource<?> createFacetPropSource(XSDSimpleTypeDefinition oldSimpleType,
            XSDSimpleTypeDefinition targetSimpleType, String facetPropName, Composite cellEditorParent) {

        SimpleTypeFacetPropSourceBuilder builder = getPropSourceBuilder(facetPropName);

        if (builder == null)
            return null;

        return builder.doCreatePropSource(targetSimpleType, cellEditorParent, builder.getSourceFacetValue(oldSimpleType));
    }

    private static SimpleTypeFacetPropSourceBuilder getPropSourceBuilder(String facetPropName) {

        if (facetName2Builder.containsKey(facetPropName))
            return facetName2Builder.get(facetPropName);

        if (IConstants.SIMPLETYPE_FACETNAME_PATTERN.equals(facetPropName))
            facetName2Builder.put(facetPropName, new SimpleTypePatternFacetPropSourceBuilder());

        if (IConstants.SIMPLETYPE_FACETNAME_ENUM.equals(facetPropName))
            facetName2Builder.put(facetPropName, new SimpleTypeEnumFacetPropSourceBuilder());

        if (IConstants.SIMPLETYPE_FACETNAME_LENGTH.equals(facetPropName))
            facetName2Builder.put(facetPropName, new SimpleTypeLengthFacetPropSourceBuilder());

        if (IConstants.SIMPLETYPE_FACETNAME_MINLENGTH.equals(facetPropName))
            facetName2Builder.put(facetPropName, new SimpleTypeMinLengthFacetPropSourceBuilder());

        if (IConstants.SIMPLETYPE_FACETNAME_MAXLENGTH.equals(facetPropName))
            facetName2Builder.put(facetPropName, new SimpleTypeMaxLengthFacetPropSourceBuilder());

        if (IConstants.SIMPLETYPE_FACETNAME_TOTALDIGITS.equals(facetPropName))
            facetName2Builder.put(facetPropName, new SimpleTypeTotalDigitsFacetPropSourceBuilder());

        if (IConstants.SIMPLETYPE_FACETNAME_FRACTIONDIGITS.equals(facetPropName))
            facetName2Builder.put(facetPropName, new SimpleTypeFractionDigitsFacetPropSourceBuilder());

        if (IConstants.SIMPLETYPE_FACETNAME_MAXINCLUSIVE.equals(facetPropName))
            facetName2Builder.put(facetPropName, new SimpleTypeMaxInclusiveFacetPropSourceBuilder());

        if (IConstants.SIMPLETYPE_FACETNAME_MAXEXCLUSIVE.equals(facetPropName))
            facetName2Builder.put(facetPropName, new SimpleTypeMaxExclusiveFacetPropSourceBuilder());

        if (IConstants.SIMPLETYPE_FACETNAME_MININCLUSIVE.equals(facetPropName))
            facetName2Builder.put(facetPropName, new SimpleTypeMinInclusiveFacetPropSourceBuilder());

        if (IConstants.SIMPLETYPE_FACETNAME_MINEXCLUSIVE.equals(facetPropName))
            facetName2Builder.put(facetPropName, new SimpleTypeMinExclusiveFacetPropSourceBuilder());

        if (IConstants.SIMPLETYPE_FACETNAME_WHITESPACE.equals(facetPropName))
            facetName2Builder.put(facetPropName, new SimpleTypeWhiteSpacePropSourceBuilder());

        return facetName2Builder.get(facetPropName);
    }

    protected Integer toIntegerQuielty(Object value) {

        try {
            return (Integer) value;
        } catch (Exception e) {
            return 0;
        }
    }

    protected Double toDoubleQuietly(Object value) {

        if (value == null) {
            return null;
        }

        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0d;
        }

    }

    protected abstract IPropertySource<?> doCreatePropSource(XSDSimpleTypeDefinition targetSimpleType,
            Composite cellEditorParent, Object sourceFacetValue);

    protected abstract Object getSourceFacetValue(XSDSimpleTypeDefinition simpleType);

}
