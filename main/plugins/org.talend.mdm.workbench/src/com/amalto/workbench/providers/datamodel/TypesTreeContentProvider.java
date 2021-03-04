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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import com.amalto.workbench.utils.Util;

public class TypesTreeContentProvider extends SchemaTreeContentProvider {

    public TypesTreeContentProvider(IWorkbenchPartSite site, XSDSchema invisibleRoot) {
        super(site, invisibleRoot);

    }

    @Override
    protected Object[] findChildren(Object parent) {

        if (parent instanceof XSDComplexTypeDefinition)
            return getXSDComplexTypeDefinitionChildren((XSDComplexTypeDefinition) parent);

        return super.findChildren(parent);
    }

    protected Object[] getXSDComplexTypeDefinitionChildren(XSDComplexTypeDefinition parent) {

        List<Object> list = new ArrayList<Object>();
        list.addAll(parent.getAttributeContents());

        EList<XSDAnnotation> annotations = parent.getAnnotations();
        if (annotations != null)
            list.addAll(Arrays.asList(annotations.toArray()));

        if (parent.getContent() == null) {
            list.add(parent.getBaseTypeDefinition());
        } else if (parent.getContent() instanceof XSDSimpleTypeDefinition) {
            list.add(parent.getContent());
        } else if (parent.getContent() instanceof XSDParticle) {
            list.removeAll(parent.getAttributeContents());

            Object[] xsdParticleChildren = getXSDParticleChildren((XSDParticle) parent.getContent());
            list.addAll(Arrays.asList(xsdParticleChildren));
        } else {
            list.add(parent.getContent());
        }

        return list.toArray();
    }

    @Override
    protected Object[] getXSDSchemaChildren(XSDSchema schema) {
        return Util.filterOutDuplicatedElems(schema.getTypeDefinitions().toArray(
                new XSDNamedComponent[schema.getTypeDefinitions().size()]));
    }

    @Override
    protected Object[] getXSDSimpleTypeDefinitionChildren(XSDSimpleTypeDefinition parent) {
        List<Object> resultList = new ArrayList<Object>();
        if (!Util.isBuildInType(parent)){
            if(parent.getAnnotations() != null) {
                EList<XSDAnnotation> annotations = parent.getAnnotations();
                resultList.addAll(Arrays.asList(annotations.toArray()));
            }
        }

        Object[] xsdSimpleTypeDefinition = super.getXSDSimpleTypeDefinitionChildren(parent);
        resultList.addAll(Arrays.asList(xsdSimpleTypeDefinition));

        return resultList.toArray();
    }
}
