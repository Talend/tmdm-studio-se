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
package com.amalto.workbench.providers.datamodel;

import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import com.amalto.workbench.providers.datamodel.util.TypeItemLabelCreator;

public class TypeElementSorter extends SchemaElementSorter {

    public static final int CATEGORYCODE_XSDSIMPLETYPEDEF = 102;

    public static final int CATEGORYCODE_XSDCOMPLEXTYPEDEF = 101;

    @Override
    protected int getCategoryCode(Object element) {

        // we want facets before Base TypeDefinitions in
        // SimpleTypeDefinition
        if (element instanceof XSDFacet)
            return CATEGORYCODE_XSDFACET;
        // unique keys after element declarations and before other
        // keys
        if (element instanceof XSDSimpleTypeDefinition) {
            return CATEGORYCODE_XSDSIMPLETYPEDEF;
        }

        if (element instanceof XSDComplexTypeDefinition) {
            return CATEGORYCODE_XSDCOMPLEXTYPEDEF;
        }
        return CATEGORYCODE_DEFAULT;

    }

    @Override
    protected int compareElementsWithSameCategory(Object e1, Object e2) {

        return TypeItemLabelCreator.getInstance().getLabel(e1)
                .compareToIgnoreCase(TypeItemLabelCreator.getInstance().getLabel(e2))
                * sortFactor;

    }

    @Override
    protected boolean isSortByLabelAvailable(int category1, int category2) {
        return (category1 == category2)
                && (category1 == CATEGORYCODE_XSDSIMPLETYPEDEF || category1 == CATEGORYCODE_XSDCOMPLEXTYPEDEF);
    }
}
