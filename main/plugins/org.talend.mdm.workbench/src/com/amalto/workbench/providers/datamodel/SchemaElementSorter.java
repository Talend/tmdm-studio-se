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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;

import com.amalto.workbench.providers.datamodel.util.SchemaItemLabelCreator;


public class SchemaElementSorter extends ViewerSorter {

    protected int sortFactor = 1;

    public static final int CATEGORYCODE_DEFAULT = 200;

    public static final int CATEGORYCODE_XSDFACET = 100;

    public static final int CATEGORYCODE_XSDIDCONSDEF_UNIQUE = 300;

    public static final int CATEGORYCODE_XSDIDCONSDEF_KEY = 301;

    public static final int CATEGORYCODE_XSDIDCONSDEF_DEFAULT = 302;

    public static final int CATEGORYCODE_XSDElEMENTDECLARARION = 1;

    public SchemaElementSorter() {
        this(true);
    }

    public SchemaElementSorter(boolean isASC) {
        setSortedType(isASC);
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {

        int cat1 = getCategoryCode(e1);
        int cat2 = getCategoryCode(e2);

        if (isSortByLabelAvailable(cat1, cat2))
            return compareElementsWithSameCategory(e1, e2);

        return cat1 - cat2;
    }

    public boolean isSortedASC() {
        return sortFactor > 0;
    }

    public void setSortedType(boolean isASC) {

        if (isASC)
            sortFactor = 1;
        else
            sortFactor = -1;
    }

    protected int getCategoryCode(Object element) {

        if (element instanceof XSDFacet)
            return CATEGORYCODE_XSDFACET;

        if (element instanceof XSDIdentityConstraintDefinition)
            return getCatCodeForXSDIdentityConstraintDefinition((XSDIdentityConstraintDefinition) element);

        if (element instanceof XSDElementDeclaration)
            return CATEGORYCODE_XSDElEMENTDECLARARION;

        return CATEGORYCODE_DEFAULT;
    }

    private int getCatCodeForXSDIdentityConstraintDefinition(XSDIdentityConstraintDefinition element) {

        switch (element.getIdentityConstraintCategory()) {
        case UNIQUE_LITERAL:
            return CATEGORYCODE_XSDIDCONSDEF_UNIQUE;
        case KEY_LITERAL:
            return CATEGORYCODE_XSDIDCONSDEF_KEY;
        default:
            return CATEGORYCODE_XSDIDCONSDEF_DEFAULT;
        }
    }

    protected int compareElementsWithSameCategory(Object e1, Object e2) {

        return SchemaItemLabelCreator.getInstance().getLabel(e1)
                .compareToIgnoreCase(SchemaItemLabelCreator.getInstance().getLabel(e2))
                * sortFactor;
    }

    protected boolean isSortByLabelAvailable(int category1, int category2) {
        return (category1 == category2) && (category1 == CATEGORYCODE_XSDElEMENTDECLARARION);
    }
}
