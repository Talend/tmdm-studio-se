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

import com.amalto.workbench.providers.datamodel.util.SchemaItemLabelCreator;
import com.amalto.workbench.providers.datamodel.util.SchemaTopElementNameSelector;
import com.amalto.workbench.providers.datamodel.util.SchemaTopElementNameSelectorFactory;
import com.amalto.workbench.utils.SchemaElementNameFilterDes;

public class SchemaNameFilter extends ViewerFilter {

    protected SchemaTopElementNameSelector selector;

    public SchemaNameFilter() {
        this(null);
    }

    public SchemaNameFilter(SchemaElementNameFilterDes nameFilterDes) {
        setNameFilterDes(nameFilterDes);
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        return selector.isSatisfiedElement(parentElement, element);
    }

    public void setNameFilterDes(SchemaElementNameFilterDes nameFilterDes) {

        selector = SchemaTopElementNameSelectorFactory.getInstance().createSchemaTopElementNameSelectorOnPattern(
                SchemaItemLabelCreator.getInstance(), nameFilterDes);
    }
}
