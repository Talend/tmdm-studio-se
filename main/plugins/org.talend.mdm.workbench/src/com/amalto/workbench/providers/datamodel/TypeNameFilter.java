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

import com.amalto.workbench.providers.datamodel.util.SchemaTopElementNameSelectorFactory;
import com.amalto.workbench.providers.datamodel.util.TypeItemLabelCreator;
import com.amalto.workbench.utils.SchemaElementNameFilterDes;

public class TypeNameFilter extends SchemaNameFilter {

    public TypeNameFilter() {
        super();
    }

    public TypeNameFilter(SchemaElementNameFilterDes nameFilterDes) {
        super(nameFilterDes);
    }

    @Override
    public void setNameFilterDes(SchemaElementNameFilterDes nameFilterDes) {

        selector = SchemaTopElementNameSelectorFactory.getInstance().createSchemaTopElementNameSelectorOnPattern(
                TypeItemLabelCreator.getInstance(), nameFilterDes);

    }
}
