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

import com.amalto.workbench.detailtabs.sections.model.simpletype.propsource.SimpleTypeFacetEnumPropertySource;
import com.amalto.workbench.widgets.composites.property.IPropertySource;

public class SimpleTypeEnumFacetPropSourceBuilder extends SimpleTypeFacetPropSourceBuilder {

    @Override
    protected IPropertySource<?> doCreatePropSource(XSDSimpleTypeDefinition simpleType, Composite cellEditorParent,
            Object sourceFacetValue) {

        return new SimpleTypeFacetEnumPropertySource(cellEditorParent, (String[]) sourceFacetValue);

    }

    @Override
    protected String[] getSourceFacetValue(XSDSimpleTypeDefinition simpleType) {
        return SimpleTypeFacetValueExtractor.getStringArrayFacetValue(simpleType.getEnumerationFacets());
    }

}
