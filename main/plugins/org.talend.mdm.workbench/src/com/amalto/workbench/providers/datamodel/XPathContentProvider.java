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

import java.util.List;

import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDSchema;

public class XPathContentProvider extends SchemaTreeContentProvider {

    public XPathContentProvider() {
        super(null, null);
    }

    @Override
    public Object[] getElements(Object inputElement) {

        if (!(inputElement instanceof XSDSchema))
            return new Object[0];

        return getChildren(inputElement);
    }

    @Override
    protected Object[] getXSDAnnotationChildren(XSDAnnotation parent) {
        return new Object[0];
    }

    @Override
    protected XSDIdentityConstraintDefinition[] getXSDElementDeclarationChildren_IDs(XSDElementDeclaration parent) {
        return new XSDIdentityConstraintDefinition[0];
    }

    @Override
    protected void addEleDeclarationAnn2List(List<Object> list, XSDElementDeclaration element) {
    }

    @Override
    protected Object[] getXSDIdentityConstraintDefinitionChildren(XSDIdentityConstraintDefinition parent) {
        return new Object[0];
    }
}
