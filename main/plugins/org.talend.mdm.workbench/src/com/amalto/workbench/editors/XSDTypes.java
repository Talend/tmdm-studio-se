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
package com.amalto.workbench.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;


public class XSDTypes {

    private static final String[] types = { "string", "boolean", "short", "int", "long", "integer", "float", "double", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
            "decimal", "date", "dateTime", "time"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    public static String[] getXSDSimpleType(XSDSchema xsdSchema) {
        if(xsdSchema == null)
            return types;

        List<String> buildInTypes = new ArrayList<String>();
        for (Iterator<XSDTypeDefinition> iter = xsdSchema.getSchemaForSchema().getTypeDefinitions().iterator(); iter.hasNext();) {
            XSDTypeDefinition type = iter.next();
            if (type instanceof XSDSimpleTypeDefinition)
                buildInTypes.add(type.getName());
        }

        return buildInTypes.toArray(new String[0]);
    }
}
