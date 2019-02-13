// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.amalto.workbench.actions.XSDGetXPathAction;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.ISchemaContentProvider;
import com.amalto.workbench.utils.DataModelFilter;
import com.amalto.workbench.utils.IXMLConstants;
import com.amalto.workbench.utils.Util;

public class SchemaTreeContentProvider implements ITreeContentProvider, ISchemaContentProvider {

    private static Log log = LogFactory.getLog(XSDGetXPathAction.class);

    protected XSDSchema xsdSchema;

    protected IWorkbenchPartSite site = null;

    protected TreeObject treeObj;

    public SchemaTreeContentProvider(IWorkbenchPartSite site, XSDSchema invisibleRoot) {
        this.site = site;
        this.xsdSchema = invisibleRoot;
    }

    @Override
    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public Object[] getElements(Object parent) {
        if (parent.equals(site)) {
            return getChildren(xsdSchema);
        }
        return getChildren(parent);
    }

    @Override
    public Object getParent(Object child) {
        return null;
    }

    @Override
    public void setFilter(DataModelFilter filter) {
    }

    @Override
    public Object[] getChildren(Object parent) {

        if (parent == null) {
            return new Object[0];
        }

        return findChildren(parent);
    }

    protected Object[] findChildren(Object parent) {

        if (parent instanceof XSDSchema) {
            return getXSDSchemaChildren((XSDSchema) parent);
        }

        if (parent instanceof XSDAttributeGroupDefinition) {
            return getXSDAttributeGroupDefinitionChildren((XSDAttributeGroupDefinition) parent);
        }

        if (parent instanceof XSDParticle) {
            return getXSDParticleChildren((XSDParticle) parent);
        }

        if (parent instanceof XSDModelGroup) {
            return getXSDModelGroupChildren((XSDModelGroup) parent);
        }

        if (parent instanceof XSDSimpleTypeDefinition) {
            return getXSDSimpleTypeDefinitionChildren((XSDSimpleTypeDefinition) parent);
        }

        if (parent instanceof XSDElementDeclaration) {
            return getXSDElementDeclarationChildren((XSDElementDeclaration) parent);
        }

        if (parent instanceof XSDIdentityConstraintDefinition) {
            return getXSDIdentityConstraintDefinitionChildren((XSDIdentityConstraintDefinition) parent);
        }

        if (parent instanceof XSDAnnotation) {
            return getXSDAnnotationChildren((XSDAnnotation) parent);
        }

        // appinfos
        if (parent instanceof Element) {
            return new Object[0];
        }

        return new Object[0];
    }

    @Override
    public boolean hasChildren(Object parent) {
        return getChildren(parent).length > 0;
    }

    @Override
    public XSDSchema getXsdSchema() {
        return xsdSchema;
    }

    @Override
    public void setXsdSchema(String xsd) {
        try {
            xsdSchema = Util.createXsdSchema(xsd, treeObj);
        } catch (Exception e) {

            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void setXsdSchema(XSDSchema xsdSchema) {
        this.xsdSchema = xsdSchema;
    }

    public String getXSDSchemaAsString() throws Exception {

        if (xsdSchema == null)
         {
            return "";//$NON-NLS-1$
        }

        Document document = xsdSchema.getDocument();
        String schema = Util.nodeToString(document);

        // FIXES a bug in the XSD library that puts nillable attributes in
        // elements which are ref
        // That is illegal according to W3C §3.3.3 / 2.2 (and Xerces)
        Pattern p = Pattern.compile("(<([a-z]+:)?element.*?)nillable=['|\"].*?['|\"](.*?ref=.*?>)");//$NON-NLS-1$
        schema = p.matcher(schema).replaceAll("$1 $3");//$NON-NLS-1$

        return schema;
    }

    public XSDSchema createSchema(String location) {
        InputStream stream = null;
        try {
            stream = new FileInputStream(location);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setValidating(false);
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            documentBuilderFactory.setFeature(IXMLConstants.DISALLOW_DOCTYPE_DECL, true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(stream);
            return XSDSchemaImpl.createSchema(document.getDocumentElement());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    protected void addElementDeclarationFromSchema(XSDSchema schema,Collection<XSDElementDeclaration> declarations){
    	EList<XSDElementDeclaration> elementDeclarations = schema.getElementDeclarations();
    	for(XSDElementDeclaration declaration:elementDeclarations) {
    		if(declaration.eContainer().equals(schema)) {
                declarations.add(declaration);
            }
    	}
    }

	protected Object[] getXSDSchemaChildren(XSDSchema schema) {
		List<XSDElementDeclaration> declarations = new ArrayList<XSDElementDeclaration>();

        List<Object> attributeDeclarations = new ArrayList<Object>();
		if (null != xsdSchema) {
			for (XSDSchemaContent cnt : xsdSchema.getContents()) {
				if (cnt instanceof XSDInclude) {
					XSDInclude incu = (XSDInclude) cnt;
					String schemaLocation = incu.getSchemaLocation();
					XSDSchema schemaInc = createSchema(schemaLocation);
					addElementDeclarationFromSchema(schemaInc, declarations);
                } else if (cnt instanceof XSDAttributeDeclaration) {
                    XSDAttributeDeclaration attriDec = (XSDAttributeDeclaration) cnt;
                    attributeDeclarations.add(attriDec);
				}
			}
		}
		addElementDeclarationFromSchema(schema, declarations);

		Object[] schemaChildren = Util.filterOutDuplicatedElems(declarations
				.toArray(new XSDNamedComponent[declarations.size()]));
        attributeDeclarations.addAll(Arrays.asList(schemaChildren));

        return attributeDeclarations.toArray();
	}

    protected Object[] getXSDAttributeGroupDefinitionChildren(XSDAttributeGroupDefinition parent) {
        XSDAttributeGroupDefinition attributeGroupDefinition = parent;
        if (attributeGroupDefinition.getContents().size() == 0) {
            attributeGroupDefinition = attributeGroupDefinition.getResolvedAttributeGroupDefinition();
        }
        return attributeGroupDefinition.getContents().toArray(new Object[attributeGroupDefinition.getContents().size()]);
    }

    protected Object[] getXSDParticleChildren(XSDParticle particle) {

        if (particle.getTerm() instanceof XSDElementDeclaration) {
            return getXSDElementDeclarationChildren((XSDElementDeclaration) particle.getTerm());
        }

        if (particle.getTerm() instanceof XSDModelGroup) {
            return getXSDModelGroupChildren((XSDModelGroup) particle.getTerm());
        }

        if (particle.getTerm() instanceof XSDWildcard) {
        }

        return new Object[] {};

    }

    protected Object[] getXSDModelGroupChildren(XSDModelGroup parent) {
        return parent.getContents().toArray(new Object[parent.getContents().size()]);
    }

    protected Object[] getXSDSimpleTypeDefinitionChildren(XSDSimpleTypeDefinition parent) {
        Object[] result = null;

        switch (parent.getVariety()) {
        case ATOMIC_LITERAL:
            result = getXSDSimpleTypeDefinitionChildren_ATOMIC(parent);
            break;
        case LIST_LITERAL:
            result = getXSDSimpleTypeDefinitionChildren_LIST(parent);
            break;
        case UNION_LITERAL:
            result = getXSDSimpleTypeDefinitionChildren_UNION(parent);
            break;
        default:
            result = new Object[0];
        }

        return result;
    }

    private Object[] getXSDSimpleTypeDefinitionChildren_UNION(XSDSimpleTypeDefinition parent) {
        return parent.getMemberTypeDefinitions().toArray(new XSDSimpleTypeDefinition[parent.getMemberTypeDefinitions().size()]);
    }

    private Object[] getXSDSimpleTypeDefinitionChildren_LIST(XSDSimpleTypeDefinition parent) {
        // FIXME: How do we indicate it is a LIST?
        return new XSDSimpleTypeDefinition[] { parent.getBaseTypeDefinition() };
    }

    private Object[] getXSDSimpleTypeDefinitionChildren_ATOMIC(XSDSimpleTypeDefinition parent) {
        ArrayList<Object> list = new ArrayList<Object>();
        // add Base Type if not a pre-defined type
        if (parent != null && parent.getSchema() != null && parent.getSchema().getSchemaForSchema() != null) {
            if (!parent.getSchema().getSchemaForSchema().getTypeDefinitions().contains(parent)) {
                list.add(parent.getBaseTypeDefinition());
            }
        }

        if (!Util.isBuildInType(parent)) {
            list.addAll(parent.getFacetContents());
        }

        return list.toArray(new Object[list.size()]);
    }

    protected Object[] getXSDIdentityConstraintDefinitionChildren(XSDIdentityConstraintDefinition parent) {

        ArrayList<Object> list = new ArrayList<Object>();

        list.add(parent.getSelector());
        list.addAll(parent.getFields());

        return list.toArray(new Object[list.size()]);

    }

    protected Object[] getXSDAnnotationChildren(XSDAnnotation parent) {

        ArrayList<Object> list = new ArrayList<Object>();

        list.addAll(parent.getUserInformation());
        list.addAll(parent.getApplicationInformation());

        return list.toArray(new Object[list.size()]);

    }

    protected void addEleDeclarationAnn2List(List<Object> list, XSDElementDeclaration element) {
        if (element.getAnnotation() != null) {
            list.add(element.getAnnotation());
        }
    }

    protected Object[] getXSDElementDeclarationChildren_TypeDef(XSDElementDeclaration parent) {

        ArrayList<Object> list = new ArrayList<Object>();

        if (parent.getTypeDefinition() == null)
         {
            return new Object[0]; // elements with not type declaration
        }

        // handle extensions and restrictions directly
        if (parent.getTypeDefinition() instanceof XSDComplexTypeDefinition) {
            list.addAll(Util.getComplexTypeDefinitionChildren((XSDComplexTypeDefinition) parent.getTypeDefinition(), true));
        } else {
            list.addAll(Util.getSimpleTypeDefinitionChildren((XSDSimpleTypeDefinition)parent.getTypeDefinition()));
        }

        return list.toArray();
    }

    protected XSDIdentityConstraintDefinition[] getXSDElementDeclarationChildren_IDs(XSDElementDeclaration parent) {
        return parent.getIdentityConstraintDefinitions().toArray(new XSDIdentityConstraintDefinition[0]);
    }

    protected Object[] getXSDElementDeclarationChildren(XSDElementDeclaration parent) {
        // abstract elements do not have children
        if (parent.isAbstract()) {
            return new Object[0];
        }

        ArrayList<Object> list = new ArrayList<Object>();

        list.addAll(Arrays.asList(getXSDElementDeclarationChildren_TypeDef(parent)));

        // the keys
        list.addAll(Arrays.asList(getXSDElementDeclarationChildren_IDs(parent)));

        // the annotations
        addEleDeclarationAnn2List(list, parent);

        return list.toArray(new Object[list.size()]);
    }
}
