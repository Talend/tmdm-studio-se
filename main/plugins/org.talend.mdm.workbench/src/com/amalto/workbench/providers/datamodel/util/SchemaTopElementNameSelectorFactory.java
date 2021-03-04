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

import com.amalto.workbench.utils.SchemaElementNameFilterDes;

public class SchemaTopElementNameSelectorFactory {

    private static SchemaTopElementNameSelectorFactory INSTANCE;

    private SchemaTopElementNameSelectorFactory() {
    }

    public synchronized static SchemaTopElementNameSelectorFactory getInstance() {

        if (INSTANCE == null)
            INSTANCE = new SchemaTopElementNameSelectorFactory();

        return INSTANCE;
    }

    public SchemaTopElementNameSelector createSchemaTopElementNameSelectorOnPattern(SchemaItemLabelCreator labelExtractor,
            SchemaElementNameFilterDes nameFitlerDes) {

        if (labelExtractor == null || nameFitlerDes == null || !nameFitlerDes.isEnable())
            return new SchemaTopElementNameSelectorOnAll();

        if (labelExtractor instanceof TypeItemLabelCreator)
            return new TypeTopElementNameSelectorOnPattern(labelExtractor, nameFitlerDes);

        return new SchemaTopElementNameSelectorOnPattern(labelExtractor, nameFitlerDes);
    }
}
