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
package com.amalto.workbench.utils;

/**
 * <copyright>
 * 
 * Copyright (c) 2002-2004 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM - Initial API and implementation
 * 
 * </copyright>
 * 
 * $Id: XSDGenerateHTML.java,v 1.1 2005/11/13 21:54:24 amaltodev Exp $
 */

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDCardinality;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDFractionDigitsFacet;
import org.eclipse.xsd.XSDLengthFacet;
import org.eclipse.xsd.XSDMaxExclusiveFacet;
import org.eclipse.xsd.XSDMaxInclusiveFacet;
import org.eclipse.xsd.XSDMaxLengthFacet;
import org.eclipse.xsd.XSDMinExclusiveFacet;
import org.eclipse.xsd.XSDMinInclusiveFacet;
import org.eclipse.xsd.XSDMinLengthFacet;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTotalDigitsFacet;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import org.eclipse.xsd.XSDWhiteSpaceFacet;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.impl.XSDNamedComponentImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.talend.utils.xml.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Generates HTML annotated documentation that summarizes the built-in simple type hierarchy. It implements the method
 * {@link #run run}, which is called just like main during headless workbench invocation.
 * <p>
 * You can execute this example by running
 * 
 * <pre>
 *   xsd-generate-schema-for-schema-html.bat
 * </pre>
 * 
 * from the directory:
 * 
 * <pre>
 *   plugins/org.eclipse.xsd.example/data/
 * </pre>
 * 
 * This script uses the file
 * 
 * <pre>
 * plugins / org.eclipse.xsd.example / SampleMarkup.xml
 * </pre>
 * 
 * for annotations. The resultin HTML document is stored in <code>SchemaForSchema.html</code> and an
 * <code>index.html</code> is provided frame-based viewing of the document. The script
 * 
 * <pre>
 * xsd - generate - html.bat
 * </pre>
 * 
 * allows you to pass in your own annotations.
 * </p>
 */
public class XSDGenerateHTML implements IPlatformRunnable {

    private static Log log = LogFactory.getLog(XSDGenerateHTML.class);
    {
        // This is needed because we can't have the following in the plugin.xml
        //
        // <extension point = "com.ibm.etools.emf.extension_parser">
        // <parser type="xsd"
        // class="org.eclipse.xsd.util.XSDResourceFactoryImpl"/>
        // </extension>
        //
        // The com.ibm.etools.xsdmodel plugin, shipped with WSAD, has a
        // conflicting registration for this.
        //
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd", new XSDResourceFactoryImpl());//$NON-NLS-1$
    }

    /**
     * The map from schema type to Java class.
     */
    public Map schemaTypeToJavaClassMap = new HashMap();

    /**
     * The map from String keys to documentation.
     * 
     * @see #readMarkup
     * @see #handleMarkup
     */
    public Map contentDocumentationMap = new HashMap();

    /**
     * The map from String keys to documentation for {@link XSDElementDeclaration}.
     * 
     * @see #readMarkup
     * @see #handleMarkup
     */
    public Map elementDeclarationMarkupMap = new HashMap();

    /**
     * The map from String keys to documentation for {@link XSDAttributeDeclaration}s.
     * 
     * @see #readMarkup
     * @see #handleMarkup
     */
    public Map attributeDeclarationMarkupMap = new HashMap();

    /**
     * The map from {@link XSDElementDeclaration} to an anchor string.
     */
    public Map specialAnchorMap = new HashMap();

    /**
     * The list of anchors in <a href="http://www.w3.org/TR/xmlschema-1/">Part 1</a>.
     */
    protected List part1Anchors = Arrays.asList(new String[] { "all", "annotation", "any", "anyAttribute", "appinfo", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            "attribute", "attributeGroup", "choice", "complexContent", "complexContent::extension", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            "complexContent::restriction", "complexType", "documentation", "element", "field", "group", "import", "include", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
            "key", "keyref", "list", "notation", "redefine", "restriction", "schema", "selector", "sequence", "simpleContent", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
            // "simpleContent::attributeGroup",
            "simpleContent::extension", "simpleContent::restriction", "simpleType", "union", "unique", }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

    /**
     * The list of components in <a href="http://www.w3.org/TR/xmlschema-1/">Part 1</a>.
     */
    List part1Components = Arrays.asList(new String[] { "all", "Model_Group_details Particle_details", "annotation", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "Annotation_details", "any", "Wildcard_details", "anyAttribute", "Wildcard_details", "appinfo", "Annotation_details", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
            "attribute", "Attribute_Declaration_details AU_details", "attributeGroup", "Attribute_Group_Definition_details", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            "choice", "Model_Group_details Particle_details", "complexContent", "Complex_Type_Definition_details", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            "complexContent::extension", "Complex_Type_Definition_details", "complexContent::restriction", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "Complex_Type_Definition_details", "complexType", "Complex_Type_Definition_details", "documentation", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            "Annotation_details", "element", "Element_Declaration_details Particle_details", "field", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            "Identity-constraint_Definition_details", "group", "Model_Group_Definition_details Particle_details", "import", "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            "include", "", "key", "Identity-constraint_Definition_details", "keyref", "Identity-constraint_Definition_details", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
            "list", "Simple_Type_Definition_details", "notation", "Notation_Declaration_details", "redefine", "", "restriction", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
            "Simple_Type_Definition_details", "schema", "Schema_details", "selector", "Identity-constraint_Definition_details", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            "sequence", "Model_Group_details Particle_details", "simpleContent", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "Complex_Type_Definition_details", //$NON-NLS-1$
            // "simpleContent::attributeGroup", "",
            "simpleContent::extension", "Complex_Type_Definition_details", "simpleContent::restriction", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "Complex_Type_Definition_details", "simpleType", "Simple_Type_Definition_details", "union", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            "Simple_Type_Definition_details", "unique", "Identity-constraint_Definition_details", }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /**
     * The list of anchors in <a href="http://www.w3.org/TR/xmlschema-2/">Part 2</a>.
     */
    protected List part2Anchors = Arrays.asList(new String[] { "enumeration", "fractionDigits", "length", "list", "maxExclusive", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            "maxInclusive", "maxLength", "minExclusive", "minInclusive", "minLength", "pattern", "restriction", "simpleType", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
            "totalDigits", "union", "whiteSpace", }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /**
     * The list of components in <a href="http://www.w3.org/TR/xmlschema-2/">Part 2</a>.
     */
    protected List part2Components = Arrays.asList(new String[] { "enumeration", "dc-enumeration", "fractionDigits", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "dc-fractionDigits", "length", "dc-length", "list", "dc-defn", "maxExclusive", "dc-maxExclusive", "maxInclusive", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
            "dc-maxInclusive", "maxLength", "dc-maxLength", "minExclusive", "dc-minExclusive", "minInclusive", "dc-minInclusive", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
            "minLength", "dc-minLength", "pattern", "dc-pattern", "restriction", "dc-defn", "simpleType", "dc-defn", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
            "totalDigits", "dc-totalDigits", "union", "dc-defn", "whiteSpace", "dc-whiteSpace", }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    /**
     * The URL for errata.
     */
    protected String errata = "http://www.w3.org/2001/05/xmlschema-rec-comments"; //$NON-NLS-1$

    /**
     * A markup style indicating the feature is required to be supported.
     */
    public static final String REQUIRES = "requires"; //$NON-NLS-1$

    /**
     * A markup style indicating the feature not required to be supported.
     */
    public static final String ALLOWS = "allows"; //$NON-NLS-1$

    /**
     * A markup style indicating the feature will eventually be required to be supported.
     */
    public static final String FUTURE = "future"; //$NON-NLS-1$

    /**
     * A markup style indicating the feature will never be required to be supported.
     */
    public static final String NEVER = "never"; //$NON-NLS-1$

    /**
     * Creates an instance.
     */
    public XSDGenerateHTML() {
    }

    /**
     * Read the markup from the .xml input.
     * 
     * @param fileName the name of an XML file.
     */
    public void readMarkup(String fileName) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setValidating(false);
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            documentBuilderFactory.setFeature(IXMLConstants.DISALLOW_DOCTYPE_DECL, true);

            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(fileName);
            for (Node child = document.getDocumentElement().getFirstChild(); child != null; child = child.getNextSibling()) {
                if ("elementAnnotation".equals(child.getLocalName())) { //$NON-NLS-1$
                    handleMarkup(elementDeclarationMarkupMap, (Element) child);
                } else if ("attributeAnnotation".equals(child.getLocalName())) { //$NON-NLS-1$
                    handleMarkup(attributeDeclarationMarkupMap, (Element) child);
                } else if ("content".equals(child.getLocalName())) { //$NON-NLS-1$
                    handleMarkup(contentDocumentationMap, (Element) child);
                } else if ("typeMap".equals(child.getLocalName())) { //$NON-NLS-1$
                    Element markupElement = (Element) child;
                    String schemaType = markupElement.getAttribute("schemaType"); //$NON-NLS-1$
                    String javaClass = markupElement.getAttribute("javaClass"); //$NON-NLS-1$
                    schemaTypeToJavaClassMap.put(schemaType, javaClass);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }
    }

    /**
     * Handle a markup element by caching information in a map.
     * 
     * @param markupMap the map to contain the markup.
     * @param markupElement the element specifying the markup.
     */
    public void handleMarkup(Map markupMap, Element markupElement) {
        String keyList = markupElement.getAttribute("key"); //$NON-NLS-1$
        for (StringTokenizer stringTokenizer = new StringTokenizer(keyList); stringTokenizer.hasMoreTokens();) {
            String key = stringTokenizer.nextToken();
            String markup = markupElement.getAttribute("markup"); //$NON-NLS-1$
            if (markup.length() > 0) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    Transformer transformer = XmlUtils.getXmlSecureTransform();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
                    transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); //$NON-NLS-1$

                    for (Node grandChild = markupElement.getFirstChild(); grandChild != null; grandChild = grandChild
                            .getNextSibling()) {
                        if (grandChild.getNodeType() == Node.ELEMENT_NODE) {
                            transformer.transform(new DOMSource(grandChild), new StreamResult(out));
                        }
                    }
                    String serialization = out.toString();
                    serialization = serialization.substring(serialization.indexOf("<div>")); //$NON-NLS-1$
                    markupMap.put(key, markup + "@" + serialization); //$NON-NLS-1$
                } catch (Exception exception) {
                    exception.printStackTrace(System.err);
                }
            }
        }
    }

    /**
     * Returns the content documentation associated with the key.
     * 
     * @param key the key to look up.
     * @return the associated content documentation.
     * @see #handleMarkup
     */
    public String getContentDocumentation(String key) {
        String result = (String) contentDocumentationMap.get(key);
        if (result != null) {
            result = result.substring(result.indexOf("@") + 1); //$NON-NLS-1$
            if (result.length() == 0) {
                result = null;
            }
        }
        return result;
    }

    /**
     * Returns the element markup associated with the key.
     * 
     * @param key the key to look up.
     * @return the associated element markup.
     * @see #handleMarkup
     */
    public String getElementDeclarationMarkup(String key) {
        String result = (String) elementDeclarationMarkupMap.get(key);
        if (result != null) {
            result = result.substring(0, result.indexOf("@")); //$NON-NLS-1$
            if (result.length() == 0) {
                result = null;
            }
        }
        return result;
    }

    /**
     * Returns the element documentation associated with the key.
     * 
     * @param key the key to look up.
     * @return the associated element documentation.
     * @see #handleMarkup
     */
    public String getElementDeclarationDocumentation(String key) {
        String result = (String) elementDeclarationMarkupMap.get(key);
        if (result != null) {
            result = result.substring(result.indexOf("@") + 1); //$NON-NLS-1$
            if (result.length() == 0) {
                result = null;
            }
        }
        return result;
    }

    /**
     * Returns the attribute markup associated with the key.
     * 
     * @param key the key to look up.
     * @return the associated attribute markup.
     * @see #handleMarkup
     */
    public String getAttributeDeclarationMarkup(String key) {
        String result = (String) attributeDeclarationMarkupMap.get(key);
        if (result != null) {
            result = result.substring(0, result.indexOf("@")); //$NON-NLS-1$
            if (result.length() == 0) {
                result = null;
            }
        }
        return result;
    }

    /**
     * Returns the attribute documentation associated with the key.
     * 
     * @param key the key to look up.
     * @return the associated attribute documentation.
     * @see #handleMarkup
     */
    public String getAttributeDeclarationDocumentation(String key) {
        String result = (String) attributeDeclarationMarkupMap.get(key);
        if (result != null) {
            result = result.substring(result.indexOf("@") + 1); //$NON-NLS-1$
            if (result.length() == 0) {
                result = null;
            }
        }
        return result;
    }

    /**
     * Returns an href in Part 1 or Part 2 of the XML specification for the given element.
     * 
     * @param xsdElementDeclaration an element declaration in the schema for schema.
     * @return an href.
     */
    public String getStandardLink(XSDElementDeclaration xsdElementDeclaration) {
        String result = xsdElementDeclaration.getName();
        XSDElementDeclaration parentElementDeclaration = (XSDElementDeclaration) specialAnchorMap.get(xsdElementDeclaration);
        if (parentElementDeclaration != null) {
            result = "<a target='Part1' href='" + XSDConstants.PART1 + "#element-" + parentElementDeclaration.getName() + "::" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    + result;
        } else if (part2Anchors.contains(result)) {
            result = "<a target='Part2' href='" + XSDConstants.PART2 + "#element-" + result; //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            result = "<a target='Part1' href='" + XSDConstants.PART1 + "#element-" + result; //$NON-NLS-1$ //$NON-NLS-2$
        }

        return result + "'>"; //$NON-NLS-1$
    }

    /**
     * Returns an href in Part 1 or Part 2 of the component specification for the given element.
     * 
     * @param xsdElementDeclaration a simple type defintion in the schema for schema.
     * @return an href.
     */
    public String getComponentLinks(XSDElementDeclaration xsdElementDeclaration) {
        String name = xsdElementDeclaration.getName();
        XSDElementDeclaration parentElementDeclaration = (XSDElementDeclaration) specialAnchorMap.get(xsdElementDeclaration);
        if (parentElementDeclaration != null) {
            name = parentElementDeclaration.getName() + "::" + name; //$NON-NLS-1$
        }

        int part = 0;
        String anchors = null;
        int index = part2Components.indexOf(name);
        if (index != -1) {
            part = 2;
            anchors = (String) part2Components.get(index + 1);
        } else {
            index = part1Components.indexOf(name);
            if (index != -1) {
                part = 1;
                anchors = (String) part1Components.get(index + 1);
            }
        }

        if (part != 0) {
            StringBuffer result = new StringBuffer();
            int count = 0;
            for (StringTokenizer stringTokenizer = new StringTokenizer(anchors); stringTokenizer.hasMoreTokens();) {
                String anchor = stringTokenizer.nextToken();
                result.append("&nbsp;&nbsp;<a target='Part"); //$NON-NLS-1$
                result.append(part);
                result.append("' href='"); //$NON-NLS-1$
                result.append(part == 1 ? XSDConstants.PART1 : XSDConstants.PART2);
                result.append("#"); //$NON-NLS-1$
                result.append(anchor);
                result.append("'><font size=-2>"); //$NON-NLS-1$
                result.append(++count);
                result.append("</font></a>"); //$NON-NLS-1$
            }
            return result.length() > 0 ? result.toString() : null;
        } else {
            return null;
        }
    }

    /**
     * Returns an href in Part 2 of the component specification for the given simple type definition.
     * 
     * @param xsdSimpleTypeDefinition an element declaration in the schema for schema.
     * @return an href.
     */
    public String getSimpleTypeDefinitionLink(XSDSimpleTypeDefinition xsdSimpleTypeDefinition) {
        String reference = xsdSimpleTypeDefinition.getName();
        StringBuffer result = new StringBuffer();
        if ("anyType".equals(reference)) { //$NON-NLS-1$
            result.append("<a target='Part1' href='"); //$NON-NLS-1$
            result.append(XSDConstants.PART1);
        } else {
            result.append("<a target='Part2' href='"); //$NON-NLS-1$
            result.append(XSDConstants.PART2);
        }
        result.append("#"); //$NON-NLS-1$
        if ("anyType".equals(reference)) { //$NON-NLS-1$
            reference = "ur-type-itself"; //$NON-NLS-1$
        } else if ("anySimpleType".equals(reference)) { //$NON-NLS-1$
            reference = "anySimpleType-component"; //$NON-NLS-1$
        } else if ("anyListType".equals(reference)) { //$NON-NLS-1$
            reference = "element-list"; //$NON-NLS-1$
        } else if ("anyUnionType".equals(reference)) { //$NON-NLS-1$
            reference = "element-union"; //$NON-NLS-1$
        }
        result.append(reference);
        result.append("'>"); //$NON-NLS-1$
        result.append(xsdSimpleTypeDefinition.getName());
        result.append("</a>"); //$NON-NLS-1$
        return result.toString();
    }

    /**
     * Returns an anchor that can be used locally for the given element declaration.
     * 
     * @param xsdElementDeclaration an element declaration in the schema for schema.
     * @return an anchor.
     */
    public String getLocalAnchor(XSDElementDeclaration xsdElementDeclaration) {
        String result = xsdElementDeclaration.getName();
        XSDElementDeclaration parentElementDeclaration = (XSDElementDeclaration) specialAnchorMap.get(xsdElementDeclaration);
        if (parentElementDeclaration != null) {
            result = "element-" + parentElementDeclaration.getName() + "::" + result; //$NON-NLS-1$ //$NON-NLS-2$
        } else if (part2Anchors.contains(result)) {
            result = "element-2-" + result; //$NON-NLS-1$
        } else {
            result = "element-" + result; //$NON-NLS-1$
        }

        return result;
    }

    /**
     * Generate HTML annotated documentation that summarizes the built-in simple type hierarchy.
     * 
     * @param object an array of Strings.
     * @return <code>0</code> indicating success, or <code>1</code> indicating failure.
     */
    @Override
    public Object run(Object object) {
        try {
            String[] arguments = (String[]) object;

            readMarkup(arguments[0]);

            printHeader();

            // Iterate over the schema arguments.
            //
            for (int i = 1; i < arguments.length; ++i) {
                System.out.println("<!-- << " + arguments[i] + " >> -->"); //$NON-NLS-1$ //$NON-NLS-2$
                loadAndPrint(arguments[i]);
            }
            printFooter();

            return new Integer(0);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Integer(1);
        }
    }

    /**
     * Print the start of the document.
     */
    public void printHeader() {
        System.out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"); //$NON-NLS-1$
        System.out.println("<html>"); //$NON-NLS-1$

        System.out.println("<style type='text/css'>"); //$NON-NLS-1$
        System.out.println("  code { font-family: monospace; font-size: 100%}"); //$NON-NLS-1$
        System.out.println("  div.reprdef { border: 4px double gray; margin: 0em 1em; padding: 0em }"); //$NON-NLS-1$
        System.out.println("  span.reprdef { color: #A52A2A }"); //$NON-NLS-1$

        System.out.println("  div.reprHeader { margin: 4px; font-weight: bold }"); //$NON-NLS-1$
        System.out
                .println("  div.reprBody { border-top-width: 4px; border-top-style: double; border-top-color: #d3d3d3; padding: 4px ; margin: 0em}"); //$NON-NLS-1$

        System.out.println("  div.never, span.never { color : #7F7F7F }"); //$NON-NLS-1$
        // System.out.println(" div.future, span.future { color : #AF7F7F }");
        System.out.println("  div.allows, span.allows { color : #7FAF7F }"); //$NON-NLS-1$
        System.out.println("  div.future, span.future { color : #7F7FAF }"); //$NON-NLS-1$

        System.out.println("</style>"); //$NON-NLS-1$
    }

    /**
     * Print the end of the document.
     */
    public void printFooter() {
        System.out.println("</html>"); //$NON-NLS-1$
    }

    /**
     * Load the XML Schema file and print the documentation based on it.
     * 
     * @param xsdFile the name of an XML Schema file.
     */
    public void loadAndPrint(String xsdFile) throws Exception {
        XSDFactory xsdFactory = XSDFactory.eINSTANCE;

        // Create a resource set and load the main schema file into it.
        //
        ResourceSet resourceSet = new ResourceSetImpl();
        XSDResourceImpl xsdResource = (XSDResourceImpl) resourceSet.getResource(URI.createFileURI(xsdFile), true);
        XSDSchema xsdSchema = xsdResource.getSchema();

        String elementContentHeaderDocumentation = getContentDocumentation("element-header"); //$NON-NLS-1$
        if (elementContentHeaderDocumentation != null) {
            System.out.println(elementContentHeaderDocumentation);
        }

        List all = new ArrayList(xsdSchema.getElementDeclarations());

        XSDElementDeclaration simpleContent = xsdSchema.resolveElementDeclaration("simpleContent"); //$NON-NLS-1$
        XSDElementDeclaration complexContent = xsdSchema.resolveElementDeclaration("complexContent"); //$NON-NLS-1$
        for (int i = 0; i <= 1; ++i) {
            for (int j = 0; j <= 1; ++j) {
                XSDElementDeclaration parentElement = (i == 0 ? complexContent : simpleContent);
                XSDComplexTypeDefinition xsdComplexTypeDefinition = (XSDComplexTypeDefinition) parentElement.getTypeDefinition();
                XSDElementDeclaration specialElementDeclaration = (XSDElementDeclaration) ((XSDModelGroup) ((XSDModelGroup) ((XSDParticle) (xsdComplexTypeDefinition
                        .getContentType())).getTerm()).getParticles().get(1).getTerm()).getParticles().get(j).getTerm();
                all.add(specialElementDeclaration);
                specialAnchorMap.put(specialElementDeclaration, parentElement);
            }
        }

        all = XSDNamedComponentImpl.sortNamedComponents(all);

        for (Iterator i = all.iterator(); i.hasNext();) {
            XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) i.next();
            XSDElementDeclaration parentElementDeclaration = (XSDElementDeclaration) specialAnchorMap.get(xsdElementDeclaration);
            String elementDeclarationName = xsdElementDeclaration.getName();
            String key = (parentElementDeclaration == null ? "" : parentElementDeclaration.getName() + "::") //$NON-NLS-1$ //$NON-NLS-2$
                    + elementDeclarationName;
            String elementDeclarationMarkup = getElementDeclarationMarkup(key);
            System.out.print("<h2>"); //$NON-NLS-1$
            System.out.print(elementDeclarationName.substring(0, 1).toUpperCase());
            System.out.print(elementDeclarationName.substring(1));
            System.out.println("</h2>"); //$NON-NLS-1$
            System.out.println("<div class='reprdef'>"); //$NON-NLS-1$
            System.out.println("<table cols=1 width='100%'><tr><td>"); //$NON-NLS-1$
            System.out
                    .print("<div class='reprHeader'><span class='reprdef'>XML&nbsp;Representation&nbsp;Summary:&nbsp;</span><code>"); //$NON-NLS-1$
            System.out.print("<a name='" + getLocalAnchor(xsdElementDeclaration) + "'>"); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.print(getStandardLink(xsdElementDeclaration));
            System.out.print(elementDeclarationName);
            System.out.print("</a></a></code>"); //$NON-NLS-1$
            System.out.print("&nbsp;Element&nbsp;Information&nbsp;Item&nbsp;"); //$NON-NLS-1$
            if (parentElementDeclaration != null) {
                System.out.print("<small>("); //$NON-NLS-1$
                System.out.print("<a href='#" + getLocalAnchor(parentElementDeclaration) + "'>"); //$NON-NLS-1$ //$NON-NLS-2$
                System.out.print(parentElementDeclaration.getName());
                System.out.print("</a>)</small>"); //$NON-NLS-1$
            } else if ("restriction".equals(elementDeclarationName)) { //$NON-NLS-1$
                System.out.print("<small>(simpleType)</small>"); //$NON-NLS-1$
            }
            System.out.println("</div>"); //$NON-NLS-1$

            System.out.println("<div class='reprBody'>"); //$NON-NLS-1$

            if (elementDeclarationMarkup != null) {
                System.out.print("<div class='" + elementDeclarationMarkup + "'>"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            System.out.print("<tt>&lt;"); //$NON-NLS-1$
            System.out.print(elementDeclarationName);
            System.out.print("</tt>"); //$NON-NLS-1$

            String componentLinks = getComponentLinks(xsdElementDeclaration);
            if (componentLinks != null) {
                System.out.print(componentLinks);
            }

            System.out.println("<br>"); //$NON-NLS-1$

            StringBuffer attributeDocumentationBuffer = new StringBuffer();
            Map repeatedDocumentationMap = new HashMap();

            XSDTypeDefinition xsdTypeDefinition = xsdElementDeclaration.getTypeDefinition();
            XSDComplexTypeDefinition generalType = xsdSchema.resolveComplexTypeDefinitionURI(xsdElementDeclaration.getURI());
            if (generalType.getContainer() != null) {
                xsdTypeDefinition = generalType;
            }

            if (xsdTypeDefinition instanceof XSDSimpleTypeDefinition) {
                System.out.println("<tt>></tt><br>"); //$NON-NLS-1$
            } else if (xsdTypeDefinition instanceof XSDComplexTypeDefinition) {
                XSDComplexTypeDefinition xsdComplexTypeDefinition = (XSDComplexTypeDefinition) xsdTypeDefinition;
                for (Iterator attributeUses = xsdComplexTypeDefinition.getAttributeUses().iterator(); attributeUses.hasNext();) {
                    XSDAttributeUse xsdAttributeUse = (XSDAttributeUse) attributeUses.next();
                    XSDAttributeDeclaration xsdAttributeDeclaration = xsdAttributeUse.getAttributeDeclaration();
                    String attributeDeclarationName = xsdAttributeDeclaration.getName();
                    System.out.print("<tt>&nbsp;&nbsp;"); //$NON-NLS-1$
                    if (xsdAttributeDeclaration.getTargetNamespace() != null) {
                        System.out.print("xml:"); //$NON-NLS-1$
                    }
                    String attributeDeclarationMarkup = null;
                    String attributeDeclarationDocumentation = null;
                    if (!"ignored".equals(elementDeclarationMarkup)) { //$NON-NLS-1$
                        attributeDeclarationMarkup = getAttributeDeclarationMarkup(attributeDeclarationName);
                        if (attributeDeclarationMarkup == null) {
                            attributeDeclarationMarkup = getAttributeDeclarationMarkup(elementDeclarationName + "." //$NON-NLS-1$
                                    + attributeDeclarationName);
                        }

                        attributeDeclarationDocumentation = getAttributeDeclarationDocumentation(attributeDeclarationName);
                        if (attributeDeclarationDocumentation == null) {
                            attributeDeclarationDocumentation = getAttributeDeclarationDocumentation(elementDeclarationName + "." //$NON-NLS-1$
                                    + attributeDeclarationName);
                        }
                    }

                    if (attributeDeclarationDocumentation != null) {
                        Integer oldInsertIndex = (Integer) repeatedDocumentationMap.get(attributeDeclarationDocumentation);
                        if (oldInsertIndex != null) {
                            String insertion = "<br>" + attributeDeclarationName; //$NON-NLS-1$
                            attributeDocumentationBuffer.insert(oldInsertIndex.intValue(), insertion);
                            repeatedDocumentationMap.put(attributeDeclarationDocumentation, new Integer(oldInsertIndex.intValue()
                                    + insertion.length()));
                        } else {
                            if (attributeDocumentationBuffer.length() == 0) {
                                attributeDocumentationBuffer.append("<table cols=2 width='100%'>\n"); //$NON-NLS-1$
                                attributeDocumentationBuffer
                                        .append("<tr>\n<th width=25% valign=top align=left><b>Attribute</b></th>\n"); //$NON-NLS-1$
                                attributeDocumentationBuffer
                                        .append("<th width=75% valign=top align=left><b>Description</b></th>\n</tr>\n"); //$NON-NLS-1$
                            }
                            attributeDocumentationBuffer.append("<tr><td><b>"); //$NON-NLS-1$
                            if (attributeDeclarationMarkup != null) {
                                attributeDocumentationBuffer.append("<span class='" + attributeDeclarationMarkup + "'>"); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                            attributeDocumentationBuffer.append(attributeDeclarationName);
                            int insertIndex = attributeDocumentationBuffer.length();
                            if (attributeDeclarationMarkup != null) {
                                attributeDocumentationBuffer.append("</span>"); //$NON-NLS-1$
                            }
                            attributeDocumentationBuffer.append("</b></td>\n<td valign=top>\n"); //$NON-NLS-1$
                            attributeDocumentationBuffer.append(attributeDeclarationDocumentation);
                            attributeDocumentationBuffer.append("</td></tr>"); //$NON-NLS-1$
                            repeatedDocumentationMap.put(attributeDeclarationDocumentation, new Integer(insertIndex));
                        }
                    }

                    if (attributeDeclarationMarkup != null) {
                        System.out.print("<span class='" + attributeDeclarationMarkup + "'>"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    if (xsdAttributeUse.isRequired()) {
                        System.out.print("<b>"); //$NON-NLS-1$
                        System.out.print(attributeDeclarationName);
                        System.out.print("</b>"); //$NON-NLS-1$
                    } else {
                        System.out.print(attributeDeclarationName);
                    }
                    if (attributeDeclarationMarkup != null) {
                        System.out.print("</span>"); //$NON-NLS-1$
                    }
                    System.out.print("&nbsp;=&nbsp;</tt>"); //$NON-NLS-1$
                    XSDSimpleTypeDefinition xsdSimpleTypeDefinition = xsdAttributeDeclaration.getTypeDefinition();
                    printSimpleTypeDefinition(xsdSimpleTypeDefinition);
                    if (xsdAttributeUse.getLexicalValue() != null) {
                        System.out.print("&nbsp;:&nbsp;"); //$NON-NLS-1$
                        if ("".equals(xsdAttributeUse.getLexicalValue())) { //$NON-NLS-1$
                            System.out.print("\"\""); //$NON-NLS-1$
                        } else {
                            System.out.print(xsdAttributeUse.getLexicalValue());
                        }
                    }
                    if (attributeUses.hasNext()) {
                        System.out.println("<br>"); //$NON-NLS-1$
                    }
                }
                if (xsdComplexTypeDefinition.getAttributeWildcard() != null) {
                    System.out.println("<br>"); //$NON-NLS-1$
                    System.out
                            .println("<span class='" //$NON-NLS-1$
                                    + ALLOWS
                                    + "'><tt><em>&nbsp;&nbsp;{&nbsp;any&nbsp;attributes&nbsp;with&nbsp;non-schema&nbsp;namespace&nbsp;.&nbsp;.&nbsp;.&nbsp;}</em></tt></span>"); //$NON-NLS-1$
                }

                System.out.println("<tt>></tt><br>"); //$NON-NLS-1$

                if (xsdComplexTypeDefinition.getContentType() instanceof XSDParticle) {
                    System.out.print("<tt><em>&nbsp;&nbsp;Content:</em>&nbsp;"); //$NON-NLS-1$

                    printParticle((XSDParticle) xsdComplexTypeDefinition.getContentType(), elementDeclarationMarkup);

                    System.out.print("</tt>"); //$NON-NLS-1$
                } else if (xsdComplexTypeDefinition.getContentType() instanceof XSDSimpleTypeDefinition) {
                    System.out.print("<b>***** simple</b>"); //$NON-NLS-1$
                } else {
                    System.out.print("<b>{ **** empty }</b>"); //$NON-NLS-1$
                }
                System.out.println("<br>"); //$NON-NLS-1$
            }
            System.out.print("<tt>&lt;/"); //$NON-NLS-1$
            System.out.print(elementDeclarationName);
            System.out.println("></tt>"); //$NON-NLS-1$

            if (elementDeclarationMarkup != null) {
                System.out.print("</div>"); //$NON-NLS-1$
            }
            System.out.println("</div>"); //$NON-NLS-1$

            String elementDeclarationDocumentation = getElementDeclarationDocumentation(key);
            if (elementDeclarationDocumentation != null) {
                System.out.println("<div class='reprBody'>"); //$NON-NLS-1$
                System.out.println(elementDeclarationDocumentation);
                System.out.println("</div>"); //$NON-NLS-1$
            }
            if (attributeDocumentationBuffer.length() > 0) {
                System.out.println("<div class='reprBody'>"); //$NON-NLS-1$
                System.out.print(attributeDocumentationBuffer);
                System.out.println("</table>"); //$NON-NLS-1$
                System.out.println("</div>"); //$NON-NLS-1$
            }
            System.out.println("</td></tr></table>"); //$NON-NLS-1$
            System.out.println("</div>"); //$NON-NLS-1$
        }

        // System.out.println("<h1>Built-in Datatypes</h1>");
        String typeContentHeaderDocumentation = getContentDocumentation("type-header"); //$NON-NLS-1$
        if (typeContentHeaderDocumentation != null) {
            System.out.println(typeContentHeaderDocumentation);
        }
        System.out.println("<table border=true cols=3 width='100%'>"); //$NON-NLS-1$
        System.out.println("<tr>"); //$NON-NLS-1$
        System.out.println("<th width=33% valign=top align=left><b>Type</b></th>"); //$NON-NLS-1$
        System.out.println("<th width=33% valign=top align=left><b>Properties&nbsp;&amp;&nbsp;Facets</b></th>"); //$NON-NLS-1$
        System.out.println("<th width=34% valign=top align=left><b>Effective&nbsp;Value</b></th>"); //$NON-NLS-1$
        System.out.println("</tr>"); //$NON-NLS-1$

        XSDSimpleTypeDefinition anyTypeDefinition = xsdSchema.getSchemaForSchema().resolveSimpleTypeDefinition("anyType"); //$NON-NLS-1$
        XSDSimpleTypeDefinition anySimpleTypeDefinition = xsdSchema.getSchemaForSchema().resolveSimpleTypeDefinition(
                "anySimpleType"); //$NON-NLS-1$

        XSDSimpleTypeDefinition anyListTypeDefinition = xsdFactory.createXSDSimpleTypeDefinition();
        anyListTypeDefinition.setVariety(XSDVariety.LIST_LITERAL);
        anyListTypeDefinition.setName("anyListType"); //$NON-NLS-1$
        anyListTypeDefinition.setItemTypeDefinition(anySimpleTypeDefinition);
        xsdSchema.getContents().add(anyListTypeDefinition);
        anyListTypeDefinition.getElement().setAttribute(XSDConstants.ID_ATTRIBUTE, "anyListType"); //$NON-NLS-1$

        XSDSimpleTypeDefinition anyUnionTypeDefinition = xsdFactory.createXSDSimpleTypeDefinition();
        anyUnionTypeDefinition.setVariety(XSDVariety.UNION_LITERAL);
        anyUnionTypeDefinition.setName("anyUnionType"); //$NON-NLS-1$
        anyUnionTypeDefinition.getMemberTypeDefinitions().add(anySimpleTypeDefinition);
        xsdSchema.getContents().add(anyUnionTypeDefinition);
        anyUnionTypeDefinition.getElement().setAttribute(XSDConstants.ID_ATTRIBUTE, "anyUnionType"); //$NON-NLS-1$

        List allTypeDefinitions = new ArrayList(xsdSchema.getTypeDefinitions());
        allTypeDefinitions.add(0, anySimpleTypeDefinition);
        allTypeDefinitions.add(0, anyTypeDefinition);
        for (Iterator i = allTypeDefinitions.iterator(); i.hasNext();) {
            XSDTypeDefinition xsdTypeDefinition = (XSDTypeDefinition) i.next();
            if (xsdTypeDefinition instanceof XSDSimpleTypeDefinition && xsdTypeDefinition.getElement() != null
                    && xsdTypeDefinition.getName().equals(xsdTypeDefinition.getElement().getAttribute(XSDConstants.ID_ATTRIBUTE))) {
                XSDSimpleTypeDefinition xsdSimpleTypeDefinition = (XSDSimpleTypeDefinition) xsdTypeDefinition;
                System.out.println("<tr>"); //$NON-NLS-1$
                System.out.println("<a name='" + xsdSimpleTypeDefinition.getName() + "-simple-type'>"); //$NON-NLS-1$ //$NON-NLS-2$
                System.out.println("<td>"); //$NON-NLS-1$
                boolean isPrimitive = XSDVariety.ATOMIC_LITERAL == xsdSimpleTypeDefinition.getVariety()
                        && xsdSimpleTypeDefinition.getBaseTypeDefinition() == anySimpleTypeDefinition;
                if (isPrimitive) {
                    System.out.print("<b>"); //$NON-NLS-1$
                }
                System.out.print(getSimpleTypeDefinitionLink(xsdSimpleTypeDefinition));
                if (isPrimitive) {
                    System.out.print("</b>"); //$NON-NLS-1$
                }
                for (XSDSimpleTypeDefinition baseTypeDefinition = xsdSimpleTypeDefinition;; baseTypeDefinition = baseTypeDefinition
                        .getBaseTypeDefinition()) {
                    String javaClass = (String) schemaTypeToJavaClassMap.get(baseTypeDefinition.getName());
                    if (javaClass != null) {
                        System.out.println("<br>&nbsp;<br>"); //$NON-NLS-1$
                        if (baseTypeDefinition == xsdSimpleTypeDefinition) {
                            System.out.print("<b>"); // ) //$NON-NLS-1$
                        }
                        int dotIndex = javaClass.lastIndexOf("."); //$NON-NLS-1$
                        System.out.print("<font size=-2>"); //$NON-NLS-1$
                        System.out.print(javaClass.substring(0, dotIndex + 1));
                        System.out.print("</font><br>&nbsp;&nbsp;"); //$NON-NLS-1$
                        System.out.print(javaClass.substring(dotIndex + 1));
                        if (baseTypeDefinition == xsdSimpleTypeDefinition) {
                            // (
                            System.out.print("<b>"); //$NON-NLS-1$
                        }
                        System.out.println();
                        break;
                    }
                }
                System.out.println("<br>"); //$NON-NLS-1$
                System.out.println("</td>"); //$NON-NLS-1$
                System.out.println("</a>"); //$NON-NLS-1$

                StringBuffer validFacets = new StringBuffer();
                StringBuffer effectiveFacetValues = new StringBuffer();

                validFacets.append("variety<br>\n"); //$NON-NLS-1$
                effectiveFacetValues.append(xsdSimpleTypeDefinition.isSetVariety() ? "<b>" + xsdSimpleTypeDefinition.getVariety() //$NON-NLS-1$
                        + "</b>" : "<em>absent</em>"); //$NON-NLS-1$ //$NON-NLS-2$
                effectiveFacetValues.append("<br>\n"); //$NON-NLS-1$

                validFacets.append("base type definition<br>\n"); //$NON-NLS-1$
                XSDSimpleTypeDefinition baseTypeDefinition = xsdSimpleTypeDefinition.getBaseTypeDefinition();
                while (baseTypeDefinition.getName() == null) {
                    baseTypeDefinition = baseTypeDefinition.getBaseTypeDefinition();
                }
                effectiveFacetValues.append("<a href='#"); //$NON-NLS-1$
                effectiveFacetValues.append(baseTypeDefinition.getName());
                effectiveFacetValues.append("-simple-type'>"); //$NON-NLS-1$
                effectiveFacetValues.append(baseTypeDefinition.getName());
                effectiveFacetValues.append("</a><br>\n"); //$NON-NLS-1$

                validFacets.append("ordered<br>\n"); //$NON-NLS-1$
                effectiveFacetValues.append("anyUnionType".equals(xsdSimpleTypeDefinition.getName()) ? "*" //$NON-NLS-1$ //$NON-NLS-2$
                        : xsdSimpleTypeDefinition.getOrderedFacet().getValue().getName());
                effectiveFacetValues.append("<br>\n"); //$NON-NLS-1$

                validFacets.append("bounded<br>\n"); //$NON-NLS-1$
                effectiveFacetValues.append("anyUnionType".equals(xsdSimpleTypeDefinition.getName()) ? "*" //$NON-NLS-1$ //$NON-NLS-2$
                        : xsdSimpleTypeDefinition.getBoundedFacet().isValue() ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
                effectiveFacetValues.append("<br>\n"); //$NON-NLS-1$

                validFacets.append("cardinality<br>\n"); //$NON-NLS-1$
                effectiveFacetValues
                        .append("anyUnionType".equals(xsdSimpleTypeDefinition.getName()) ? "*" //$NON-NLS-1$ //$NON-NLS-2$
                                : XSDCardinality.COUNTABLY_INFINITE_LITERAL == xsdSimpleTypeDefinition.getCardinalityFacet()
                                        .getValue() ? "countably infinite" : xsdSimpleTypeDefinition.getCardinalityFacet() //$NON-NLS-1$
                                        .getValue().getName());
                effectiveFacetValues.append("<br>\n"); //$NON-NLS-1$

                validFacets.append("numeric<br>\n"); //$NON-NLS-1$
                effectiveFacetValues.append("anyUnionType".equals(xsdSimpleTypeDefinition.getName()) ? "*" //$NON-NLS-1$ //$NON-NLS-2$
                        : xsdSimpleTypeDefinition.getNumericFacet().isValue() ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
                effectiveFacetValues.append("<br>\n"); //$NON-NLS-1$

                if (xsdSimpleTypeDefinition.getValidFacets().contains("length")) { //$NON-NLS-1$
                    validFacets.append("length<br>\n"); //$NON-NLS-1$
                    if (xsdSimpleTypeDefinition.getEffectiveLengthFacet() != null) {
                        XSDLengthFacet xsdLengthFacet = xsdSimpleTypeDefinition.getEffectiveLengthFacet();
                        if (xsdLengthFacet.isFixed()) {
                            effectiveFacetValues.append("<b>"); //$NON-NLS-1$
                        }
                        effectiveFacetValues.append(xsdLengthFacet.getValue());
                        if (xsdLengthFacet.isFixed()) {
                            effectiveFacetValues.append("</b>"); //$NON-NLS-1$
                        }
                    }
                    effectiveFacetValues.append("<br>\n"); //$NON-NLS-1$
                }

                if (xsdSimpleTypeDefinition.getValidFacets().contains("minLength")) { //$NON-NLS-1$
                    validFacets.append("minLength<br>\n"); //$NON-NLS-1$
                    if (xsdSimpleTypeDefinition.getEffectiveMinLengthFacet() != null) {
                        XSDMinLengthFacet xsdMinLengthFacet = xsdSimpleTypeDefinition.getEffectiveMinLengthFacet();
                        if (xsdMinLengthFacet.isFixed()) {
                            effectiveFacetValues.append("<b>"); //$NON-NLS-1$
                        }
                        effectiveFacetValues.append(xsdMinLengthFacet.getValue());
                        if (xsdMinLengthFacet.isFixed()) {
                            effectiveFacetValues.append("</b>"); //$NON-NLS-1$
                        }
                    }
                    effectiveFacetValues.append("<br>\n"); //$NON-NLS-1$
                }

                if (xsdSimpleTypeDefinition.getValidFacets().contains("maxLength")) { //$NON-NLS-1$
                    validFacets.append("maxLength<br>\n"); //$NON-NLS-1$
                    if (xsdSimpleTypeDefinition.getEffectiveMaxLengthFacet() != null) {
                        XSDMaxLengthFacet xsdMaxLengthFacet = xsdSimpleTypeDefinition.getEffectiveMaxLengthFacet();
                        if (xsdMaxLengthFacet.isFixed()) {
                            effectiveFacetValues.append("<b>"); //$NON-NLS-1$
                        }
                        effectiveFacetValues.append(xsdMaxLengthFacet.getValue());
                        if (xsdMaxLengthFacet.isFixed()) {
                            effectiveFacetValues.append("</b>"); //$NON-NLS-1$
                        }
                    }
                    effectiveFacetValues.append("<br>\n"); //$NON-NLS-1$
                }

                if (xsdSimpleTypeDefinition.getValidFacets().contains("pattern")) { //$NON-NLS-1$
                    validFacets.append("pattern<br>\n"); //$NON-NLS-1$
                    if (xsdSimpleTypeDefinition.getEffectivePatternFacet() != null) {
                        // XSDPatternFacet xsdPatternFacet =
                        // xsdSimpleTypeDefinition.getEffectivePatternFacet();
                        effectiveFacetValues.append("*"); //$NON-NLS-1$
                    }
                    effectiveFacetValues.append("<br>\n"); //$NON-NLS-1$
                }

                if (xsdSimpleTypeDefinition.getValidFacets().contains("enumeration")) { //$NON-NLS-1$
                    validFacets.append("enumeration<br>\n"); //$NON-NLS-1$
                    if (xsdSimpleTypeDefinition.getEffectiveEnumerationFacet() != null) {
                        XSDEnumerationFacet xsdEnumerationFacet = xsdSimpleTypeDefinition.getEffectiveEnumerationFacet();
                        for (Iterator enumerators = xsdEnumerationFacet.getValue().iterator(); enumerators.hasNext();) {
                            String enumerator = (String) enumerators.next();
                            effectiveFacetValues.append(enumerator);
                            effectiveFacetValues.append("&nbsp;"); //$NON-NLS-1$
                        }
                    }
                    effectiveFacetValues.append("<br>\n"); //$NON-NLS-1$
                }

                if (xsdSimpleTypeDefinition.getValidFacets().contains("whiteSpace")) { //$NON-NLS-1$
                    validFacets.append("whiteSpace<br>\n"); //$NON-NLS-1$
                    if (xsdSimpleTypeDefinition.getEffectiveWhiteSpaceFacet() != null) {
                        XSDWhiteSpaceFacet xsdWhiteSpaceFacet = xsdSimpleTypeDefinition.getEffectiveWhiteSpaceFacet();
                        if (xsdWhiteSpaceFacet.isFixed()) {
                            effectiveFacetValues.append("<b>"); //$NON-NLS-1$
                        }
                        effectiveFacetValues.append(xsdWhiteSpaceFacet.getValue());
                        if (xsdWhiteSpaceFacet.isFixed()) {
                            effectiveFacetValues.append("</b>"); //$NON-NLS-1$
                        }
                    }
                    effectiveFacetValues.append("<br>\n"); //$NON-NLS-1$
                }

                if (xsdSimpleTypeDefinition.getValidFacets().contains("maxInclusive")) { //$NON-NLS-1$
                    validFacets.append("maxInclusive<br>\n"); //$NON-NLS-1$
                    if (xsdSimpleTypeDefinition.getEffectiveMaxFacet() instanceof XSDMaxInclusiveFacet) {
                        XSDMaxInclusiveFacet xsdMaxInclusiveFacet = (XSDMaxInclusiveFacet) xsdSimpleTypeDefinition
                                .getEffectiveMaxFacet();
                        if (xsdMaxInclusiveFacet.isFixed()) {
                            effectiveFacetValues.append("<b>"); //$NON-NLS-1$
                        }
                        effectiveFacetValues.append(xsdMaxInclusiveFacet.getValue());
                        if (xsdMaxInclusiveFacet.isFixed()) {
                            effectiveFacetValues.append("</b>"); //$NON-NLS-1$
                        }
                    }
                    effectiveFacetValues.append("<br>\n"); //$NON-NLS-1$
                }

                if (xsdSimpleTypeDefinition.getValidFacets().contains("maxExclusive")) { //$NON-NLS-1$
                    validFacets.append("maxExclusive<br>\n"); //$NON-NLS-1$
                    if (xsdSimpleTypeDefinition.getEffectiveMaxFacet() instanceof XSDMaxExclusiveFacet) {
                        XSDMaxExclusiveFacet xsdMaxExclusiveFacet = (XSDMaxExclusiveFacet) xsdSimpleTypeDefinition
                                .getEffectiveMaxFacet();
                        if (xsdMaxExclusiveFacet.isFixed()) {
                            effectiveFacetValues.append("<b>"); //$NON-NLS-1$
                        }
                        effectiveFacetValues.append(xsdMaxExclusiveFacet.getValue());
                        if (xsdMaxExclusiveFacet.isFixed()) {
                            effectiveFacetValues.append("</b>"); //$NON-NLS-1$
                        }
                    }
                    effectiveFacetValues.append("<br>\n"); //$NON-NLS-1$
                }

                if (xsdSimpleTypeDefinition.getValidFacets().contains("minInclusive")) { //$NON-NLS-1$
                    validFacets.append("maxInclusive<br>\n"); //$NON-NLS-1$
                    if (xsdSimpleTypeDefinition.getEffectiveMinFacet() instanceof XSDMinInclusiveFacet) {
                        XSDMinInclusiveFacet xsdMinInclusiveFacet = (XSDMinInclusiveFacet) xsdSimpleTypeDefinition
                                .getEffectiveMinFacet();
                        if (xsdMinInclusiveFacet.isFixed()) {
                            effectiveFacetValues.append("<b>"); //$NON-NLS-1$
                        }
                        effectiveFacetValues.append(xsdMinInclusiveFacet.getValue());
                        if (xsdMinInclusiveFacet.isFixed()) {
                            effectiveFacetValues.append("</b>"); //$NON-NLS-1$
                        }
                    }
                    effectiveFacetValues.append("<br>\n"); //$NON-NLS-1$
                }

                if (xsdSimpleTypeDefinition.getValidFacets().contains("minExclusive")) { //$NON-NLS-1$
                    validFacets.append("maxExclusive<br>\n"); //$NON-NLS-1$
                    if (xsdSimpleTypeDefinition.getEffectiveMinFacet() instanceof XSDMinExclusiveFacet) {
                        XSDMinExclusiveFacet xsdMinExclusiveFacet = (XSDMinExclusiveFacet) xsdSimpleTypeDefinition
                                .getEffectiveMinFacet();
                        if (xsdMinExclusiveFacet.isFixed()) {
                            effectiveFacetValues.append("<b>"); //$NON-NLS-1$
                        }
                        effectiveFacetValues.append(xsdMinExclusiveFacet.getValue());
                        if (xsdMinExclusiveFacet.isFixed()) {
                            effectiveFacetValues.append("</b>"); //$NON-NLS-1$
                        }
                    }
                    effectiveFacetValues.append("<br>\n"); //$NON-NLS-1$
                }

                if (xsdSimpleTypeDefinition.getValidFacets().contains("totalDigits")) { //$NON-NLS-1$
                    validFacets.append("totalDigits<br>\n"); //$NON-NLS-1$
                    if (xsdSimpleTypeDefinition.getEffectiveTotalDigitsFacet() != null) {
                        XSDTotalDigitsFacet xsdTotalDigitsFacet = xsdSimpleTypeDefinition.getEffectiveTotalDigitsFacet();
                        if (xsdTotalDigitsFacet.isFixed()) {
                            effectiveFacetValues.append("<b>"); //$NON-NLS-1$
                        }
                        effectiveFacetValues.append(xsdTotalDigitsFacet.getValue());
                        if (xsdTotalDigitsFacet.isFixed()) {
                            effectiveFacetValues.append("</b>"); //$NON-NLS-1$
                        }
                    }
                    effectiveFacetValues.append("<br>\n"); //$NON-NLS-1$
                }

                if (xsdSimpleTypeDefinition.getValidFacets().contains("fractionDigits")) { //$NON-NLS-1$
                    validFacets.append("fractionDigits<br>\n"); //$NON-NLS-1$
                    if (xsdSimpleTypeDefinition.getEffectiveFractionDigitsFacet() != null) {
                        XSDFractionDigitsFacet xsdFractionDigitsFacet = xsdSimpleTypeDefinition.getEffectiveFractionDigitsFacet();
                        if (xsdFractionDigitsFacet.isFixed()) {
                            effectiveFacetValues.append("<b>"); //$NON-NLS-1$
                        }
                        effectiveFacetValues.append(xsdFractionDigitsFacet.getValue());
                        if (xsdFractionDigitsFacet.isFixed()) {
                            effectiveFacetValues.append("</b>"); //$NON-NLS-1$
                        }
                    }
                    effectiveFacetValues.append("<br>\n"); //$NON-NLS-1$
                }

                System.out.println("<td>"); //$NON-NLS-1$
                System.out.print(validFacets);
                System.out.println("</td>"); //$NON-NLS-1$
                System.out.print("<td>"); //$NON-NLS-1$
                System.out.println(effectiveFacetValues);
                System.out.println("</td>"); //$NON-NLS-1$
                System.out.println("</tr>"); //$NON-NLS-1$
            }
        }
        System.out.println("</table>"); //$NON-NLS-1$

        String appendixContentHeaderDocumentation = getContentDocumentation("appendix-header"); //$NON-NLS-1$
        if (appendixContentHeaderDocumentation != null) {
            System.out.println(appendixContentHeaderDocumentation);
        }
    }

    /**
     * Print a particle with markup for the document.
     * 
     * @param xsdParticle a particle.
     * @param rootElementDeclarationMarkup the markup.
     */
    public void printParticle(XSDParticle xsdParticle, String rootElementDeclarationMarkup) {
        int minOccurs = xsdParticle.getMinOccurs();
        int maxOccurs = xsdParticle.getMaxOccurs();
        XSDTerm xsdTerm = xsdParticle.getTerm();
        if (xsdTerm instanceof XSDElementDeclaration) {
            XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) xsdTerm;
            String elementDeclarationName = xsdElementDeclaration.getName();
            String elementDeclarationMarkup = null;
            if (rootElementDeclarationMarkup == null) {
                elementDeclarationMarkup = getElementDeclarationMarkup(elementDeclarationName);
            }
            if (elementDeclarationMarkup != null) {
                System.out.print("<span class='"); //$NON-NLS-1$
                System.out.print(elementDeclarationMarkup);
                System.out.print("'>"); //$NON-NLS-1$
            }
            System.out.print("<a href='#" + getLocalAnchor(xsdElementDeclaration) + "'>"); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.print(elementDeclarationName.charAt(0));
            System.out.print("</a>"); //$NON-NLS-1$
            System.out.print(elementDeclarationName.substring(1));
            if (elementDeclarationMarkup != null) {
                System.out.print("</span>"); //$NON-NLS-1$
            }
            if (minOccurs == 0) {
                if (maxOccurs == 1) {
                    System.out.print("?"); //$NON-NLS-1$
                } else {
                    System.out.print("*"); //$NON-NLS-1$
                }
            } else if (maxOccurs == -1) {
                System.out.print("+"); //$NON-NLS-1$
            }
        } else if (xsdTerm instanceof XSDModelGroup) {
            XSDModelGroup xsdModelGroup = (XSDModelGroup) xsdTerm;
            List particles = xsdModelGroup.getParticles();
            boolean isRedundant = particles.size() == 1 && minOccurs == 1 && maxOccurs == 1
                    && ((XSDParticle) particles.get(0)).getTerm() instanceof XSDModelGroup;
            if (!isRedundant) {
                System.out.print("("); // ) //$NON-NLS-1$
            }

            String separator = XSDCompositor.CHOICE_LITERAL == xsdModelGroup.getCompositor() ? " | " //$NON-NLS-1$
                    : XSDCompositor.SEQUENCE_LITERAL == xsdModelGroup.getCompositor() ? ",  " : "  &  "; //$NON-NLS-1$ //$NON-NLS-2$

            for (Iterator i = xsdModelGroup.getParticles().iterator(); i.hasNext();) {
                XSDParticle childParticle = (XSDParticle) i.next();
                printParticle(childParticle, rootElementDeclarationMarkup);
                if (i.hasNext()) {
                    System.out.print(separator);
                }
            }

            if (!isRedundant) {
                // (
                System.out.print(")"); //$NON-NLS-1$
                if (minOccurs == 0) {
                    if (maxOccurs == 1) {
                        System.out.print("?"); //$NON-NLS-1$
                    } else {
                        System.out.print("*"); //$NON-NLS-1$
                    }
                } else if (maxOccurs == -1) {
                    System.out.print("+"); //$NON-NLS-1$
                }
            }
        } else if (xsdTerm instanceof XSDWildcard) {
            System.out.print("<em>{any}</em>"); //$NON-NLS-1$
        }
    }

    /**
     * Print a simple type definition for the document.
     * 
     * @param xsdSimpleTypeDefinition a simple type definition in the schema for schema.
     */
    public void printSimpleTypeDefinition(XSDSimpleTypeDefinition xsdSimpleTypeDefinition) {
        if (xsdSimpleTypeDefinition == null) {
        } else if (xsdSimpleTypeDefinition.getEffectiveEnumerationFacet() != null) {
            List value = xsdSimpleTypeDefinition.getEffectiveEnumerationFacet().getValue();
            if (value.size() > 1) {
                System.out.print("("); //$NON-NLS-1$
            }
            for (Iterator enumerators = value.iterator(); enumerators.hasNext();) {
                String enumerator = enumerators.next().toString();
                System.out.print("<em>"); //$NON-NLS-1$
                System.out.print(enumerator);
                System.out.print("</em>"); //$NON-NLS-1$
                if (enumerators.hasNext()) {
                    System.out.print("&nbsp;|&nbsp;"); //$NON-NLS-1$
                }
            }
            if (value.size() > 1) {
                System.out.print(")"); //$NON-NLS-1$
            }
        } else if (xsdSimpleTypeDefinition.getElement() != null
                && xsdSimpleTypeDefinition.getElement().hasAttribute(XSDConstants.ID_ATTRIBUTE)) {
            System.out.print("<a href='#" + xsdSimpleTypeDefinition.getName() + "-simple-type'>"); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.print(xsdSimpleTypeDefinition.getName());
            System.out.print("</a>"); //$NON-NLS-1$
        } else if (XSDVariety.UNION_LITERAL == xsdSimpleTypeDefinition.getVariety()) {
            System.out.print("("); //$NON-NLS-1$
            for (Iterator members = xsdSimpleTypeDefinition.getMemberTypeDefinitions().iterator(); members.hasNext();) {
                XSDSimpleTypeDefinition memberTypeDefinition = (XSDSimpleTypeDefinition) members.next();
                printSimpleTypeDefinition(memberTypeDefinition);
                if (members.hasNext()) {
                    System.out.print("&nbsp;|&nbsp;"); //$NON-NLS-1$
                }
            }
            System.out.print(")"); //$NON-NLS-1$
        } else if (XSDVariety.UNION_LITERAL == xsdSimpleTypeDefinition.getVariety()) {
            System.out.print("List&nbsp;of&nbsp;"); //$NON-NLS-1$
            printSimpleTypeDefinition(xsdSimpleTypeDefinition.getItemTypeDefinition());
        } else if (xsdSimpleTypeDefinition.getName() != null) {
            if ("public".equals(xsdSimpleTypeDefinition.getName())) { //$NON-NLS-1$
                System.out.print("<a target='Part2' href='" + XSDConstants.PART2 + "#anyURI'>anyURI</a>&nbsp;&nbsp;"); //$NON-NLS-1$ //$NON-NLS-2$
                System.out.print("<a target='Errata' href='" + errata + "#pfipublic'><em>public</em></a>"); //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                System.out.print("<b><em>"); //$NON-NLS-1$
                System.out.print(xsdSimpleTypeDefinition.getName());
                System.out.print("</em></b>"); //$NON-NLS-1$
            }
        } else if (xsdSimpleTypeDefinition.getEffectivePatternFacet() != null) {
            // System.out.print(xsdSimpleTypeDefinition.getEffectivePatternFacet().getLexicalValue());

            System.out.print("<em>"); //$NON-NLS-1$
            System.out.print("<a target='Part1' href='" + XSDConstants.PART1 + "#coss-identity-constraint'>"); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.print("a restricted xpath expression"); //$NON-NLS-1$
            System.out.print("</a>"); //$NON-NLS-1$
            System.out.print("</em>"); //$NON-NLS-1$
        } else {
            System.out.print("***"); //$NON-NLS-1$
        }
    }
}
