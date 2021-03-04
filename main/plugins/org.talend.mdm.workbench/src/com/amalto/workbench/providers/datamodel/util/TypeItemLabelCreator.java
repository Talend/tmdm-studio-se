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

import org.eclipse.xsd.XSDSimpleTypeDefinition;

public class TypeItemLabelCreator extends SchemaItemLabelCreator {

    protected static TypeItemLabelCreator INSTANCE;

    public static final String XSDSIMPLETYPEDEF_PREFIX_NOTKNOWN = "***";//$NON-NLS-1$

    public static final String XSDMODELGROUP_LABEL_NONNAME = "";//$NON-NLS-1$

    private TypeItemLabelCreator() {
    }

    public synchronized static SchemaItemLabelCreator getInstance() {

        if (INSTANCE == null)
            INSTANCE = new TypeItemLabelCreator();

        return INSTANCE;
    }

    @Override
    protected String getSuffixForXSDSimpleTypeDefinition(XSDSimpleTypeDefinition element) {

        if (element.getTargetNamespace() == null)
            return "";//$NON-NLS-1$

        return XSDSIMPLETYPEDEF_SEPARATOR + element.getTargetNamespace();

    }
}
