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
package com.amalto.workbench.export;

import com.amalto.workbench.models.TreeObject;

public class Exports {

    private TreeObject[] items;

    private String[] schemas;

    private String autoIncrement;

    public String getAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(String autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public TreeObject[] getItems() {
        return items;
    }

    public void setItems(TreeObject[] items) {
        this.items = items;
    }

    public String[] getSchemas() {
        return schemas;
    }

    public void setSchemas(String[] schemas) {
        this.schemas = schemas;
    }
}
