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

public class SchemaXSDElementIDSelectorFactory {

    private static SchemaXSDElementIDSelectorFactory INSTANCE;

    private SchemaXSDElementIDSelectorFactory() {
    }

    public synchronized static SchemaXSDElementIDSelectorFactory getInstance() {

        if (INSTANCE == null)
            INSTANCE = new SchemaXSDElementIDSelectorFactory();

        return INSTANCE;
    }

    public SchemaXSDElementIDSelector createSchemaXSDElementSelectorOnAll() {
        return new SchemaXSDElementIDSelectorOnAll();
    }

    public SchemaXSDElementIDSelector createSchemaXSDElementSelectorOnUnique() {
        return new SchemaXSDElementIDSelectorOnUnique();
    }

    public SchemaXSDElementIDSelector createSchemaXSDElementSelector(boolean isFiltUnique) {

        if (isFiltUnique)
            return createSchemaXSDElementSelectorOnUnique();

        return createSchemaXSDElementSelectorOnAll();
    }
}
