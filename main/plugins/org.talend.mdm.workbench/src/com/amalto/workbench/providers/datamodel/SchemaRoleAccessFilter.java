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

import com.amalto.workbench.providers.datamodel.util.SchemaItemRoleSelector;
import com.amalto.workbench.providers.datamodel.util.SchemaItemRoleSelectorFactory;
import com.amalto.workbench.utils.DataModelFilter;

public class SchemaRoleAccessFilter extends ViewerFilter {

    private SchemaItemRoleSelector schemaItemRoleSelector;

    private DataModelFilter dataModelFilter;

    public SchemaRoleAccessFilter(DataModelFilter dataModelFilter) {
        setDataModelFilter(dataModelFilter);
    }

    public DataModelFilter getDataModelFilter() {
        return dataModelFilter;
    }

    public void setDataModelFilter(DataModelFilter dataModelFilter) {
        this.dataModelFilter = dataModelFilter;
        schemaItemRoleSelector = SchemaItemRoleSelectorFactory.getInstance().createSelectorByDataModelFilter(dataModelFilter);

    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        return schemaItemRoleSelector.isSatisfiedItem(dataModelFilter == null ? "" : dataModelFilter.getRole(), parentElement,//$NON-NLS-1$
                element);
    }

}
