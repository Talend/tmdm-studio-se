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
import org.eclipse.jface.viewers.ViewerFilter;

import com.amalto.workbench.providers.datamodel.util.SchemaXSDElementIDSelector;
import com.amalto.workbench.providers.datamodel.util.SchemaXSDElementIDSelectorFactory;

public class SchemaUniqueElementFilter extends ViewerFilter {

    private SchemaXSDElementIDSelector selector;

    public SchemaUniqueElementFilter() {
        this(false);
    }

    public SchemaUniqueElementFilter(boolean isFiltUnique) {
        setSelector(isFiltUnique);
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        return selector.isSatisfiedElement(parentElement, element);
    }

    public SchemaXSDElementIDSelector getSelector() {
        return selector;
    }

    public void setSelector(boolean isFiltUnique) {
        selector = SchemaXSDElementIDSelectorFactory.getInstance().createSchemaXSDElementSelector(isFiltUnique);
    }

}
