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
package com.amalto.workbench.providers.datamodel.util;

import com.amalto.workbench.utils.DataModelFilter;

public class SchemaItemRoleSelectorFactory {

    private static SchemaItemRoleSelectorFactory INSTANCE;

    private SchemaItemRoleSelectorFactory() {
    }

    public static synchronized SchemaItemRoleSelectorFactory getInstance() {

        if (INSTANCE == null)
            INSTANCE = new SchemaItemRoleSelectorFactory();

        return INSTANCE;
    }

    public SchemaItemRoleSelector creatSelectorOnAll() {
        return new SchemaItemRoleSelectorOnAll();
    }

    public SchemaItemRoleSelector creatSelectorOnWrite() {
        return new SchemaItemRoleSelectorOnWrite();
    }

    public SchemaItemRoleSelector creatSelectorOnHidden() {
        return new SchemaItemRoleSelectorOnHidden();
    }

    public SchemaItemRoleSelector creatSelectorOnReadonly() {
        return new SchemaItemRoleSelectorOnReadonly();
    }

    public SchemaItemRoleSelector createSelectorByDataModelFilter(DataModelFilter filter) {

        if (filter == null)
            return creatSelectorOnAll();

        if (filter.isAll())
            return creatSelectorOnAll();
        else if (filter.isWriteAccess())
            return creatSelectorOnWrite();
        else if (filter.isHiddenAccess())
            return creatSelectorOnHidden();
        else if (filter.isReadOnly())
            return creatSelectorOnReadonly();

        return creatSelectorOnAll();
    }
}
