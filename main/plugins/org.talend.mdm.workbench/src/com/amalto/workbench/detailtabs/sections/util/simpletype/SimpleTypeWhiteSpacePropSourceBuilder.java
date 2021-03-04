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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import com.amalto.workbench.detailtabs.sections.model.simpletype.propsource.SimpleTypeFacetArrayStrPropertySource;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.widgets.composites.property.IPropertySource;

public class SimpleTypeWhiteSpacePropSourceBuilder extends SimpleTypeFacetPropSourceBuilder {

    private String[] candidates = new String[] { "preserve", "replace", "collapse" };//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

    @Override
    protected IPropertySource<?> doCreatePropSource(XSDSimpleTypeDefinition simpleType, Composite cellEditorParent,
            Object sourceFacetValue) {
        return new SimpleTypeFacetArrayStrPropertySource(cellEditorParent, IConstants.SIMPLETYPE_FACETNAME_WHITESPACE,
                (String) sourceFacetValue, candidates);
    }

    @Override
    protected Object getSourceFacetValue(XSDSimpleTypeDefinition simpleType) {
        return SimpleTypeFacetValueExtractor.getStringFacetValue(simpleType.getWhiteSpaceFacet());
    }

}
