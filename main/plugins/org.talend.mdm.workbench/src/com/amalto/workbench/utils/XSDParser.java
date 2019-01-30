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
 * $Id: XSDParser.java,v 1.3 2006/04/27 06:55:43 amaltodev Exp $
 */
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.StringReader;
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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.talend.utils.xml.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Generates HTML annotated documentation that summarizes the built-in simple type hierarchy. It implements the method
 * {@link #run run}, which is called just like main during headless workbench invocation.
 * <p>
 * You can execute this example by running
 * 
 * <pre>
 *    xsd-generate-schema-for-schema-html.bat
 * </pre>
 * 
 * from the directory:
 * 
 * <pre>
 *    plugins/org.eclipse.xsd.example/data/
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
public class XSDParser {

    private static Log log = LogFactory.getLog(XSDParser.class);
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
        /*
         * Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put( "xsd", new XSDResourceFactoryImpl());
         */
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
    public XSDParser() {
    }

    /**
     * Read the markup from the .xml input.
     * 
     * @param fileName the name of an XML file.
     */
    public void readMarkup(String markup) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setValidating(false);
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            documentBuilderFactory.setFeature(IXMLConstants.DISALLOW_DOCTYPE_DECL, true);

            InputSource source = new InputSource(new StringReader(markup));
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(source);
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
    /*
     * public Object run(Object object) { try { String[] arguments = (String[]) object;
     * 
     * readMarkup(arguments[0]);
     * 
     * printHeader();
     * 
     * // Iterate over the schema arguments. // for (int i = 0; i < arguments.length; ++i) {
     * System.out.println("Argument " + i);// ("<!-- << " + // arguments[i] + " >> // -->"); loadAndPrint(arguments[i]);
     * } printFooter();
     * 
     * return new Integer(0); } catch (Exception exception) { exception.printStackTrace(); return new Integer(1); } }
     */

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
    public void loadAndPrint(String xsd) throws Exception {
        // XSDFactory xsdFactory = XSDFactory.eINSTANCE;

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setValidating(false);
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        documentBuilderFactory.setFeature(IXMLConstants.DISALLOW_DOCTYPE_DECL, true);
        InputSource source = new InputSource(new StringReader(xsd));
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(source);
        XSDSchema xsdSchema = XSDSchemaImpl.createSchema(document.getDocumentElement());

        // Create a resource set and load the main schema file into it.
        //
        /*
         * ResourceSet resourceSet = new ResourceSetImpl(); XSDResourceImpl xsdResource = (XSDResourceImpl) resourceSet
         * .getResource(URI.createFileURI(xsdFile), true);
         * 
         * XSDResourceImpl res = new XSDResourceImpl();
         * 
         * XSDSchema xsdSchema = xsdResource.getSchema();
         */

        String elementContentHeaderDocumentation = getContentDocumentation("element-header"); //$NON-NLS-1$
        if (elementContentHeaderDocumentation != null) {
            System.out.println(elementContentHeaderDocumentation);
        }

        List all = new ArrayList(xsdSchema.getElementDeclarations());

        for (Iterator iter = all.iterator(); iter.hasNext();) {
            XSDElementDeclaration concept = (XSDElementDeclaration) iter.next();
            System.out.println("CONCEPT: " + concept.getName() + "-- " + concept.getContainer().getClass().getName()); //$NON-NLS-1$ //$NON-NLS-2$

            // name of the concept in English
            XSDAnnotation annotation = concept.getAnnotation();
            if (annotation != null) {
                EList list = annotation.getApplicationInformation();
                for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                    Element appinfo = (Element) iterator.next();
                    String language = appinfo.getAttribute("source"); //$NON-NLS-1$
                    if ("EN".equals(language.toUpperCase())) { //$NON-NLS-1$
                        if (appinfo.getFirstChild() != null)
                         {
                            System.out.println("     " + appinfo.getFirstChild().getNodeValue()); //$NON-NLS-1$
                        }
                    }
                }
            }

            // children
            XSDTypeDefinition typedef = concept.getTypeDefinition();
            if (typedef instanceof XSDComplexTypeDefinition) {
                XSDComplexTypeContent xsdContent = ((XSDComplexTypeDefinition) typedef).getContent();
                XSDParticle xsdParticle = (XSDParticle) xsdContent;
                String ident = "    "; //$NON-NLS-1$
                processParticle(xsdParticle, ident);
            }

        } // for elements
    }

    public void processParticle(XSDParticle xsdParticle, String ident) {
        int minOccurs = xsdParticle.getMinOccurs();
        int maxOccurs = xsdParticle.getMaxOccurs();
        XSDTerm xsdTerm = xsdParticle.getTerm();
        if (xsdTerm instanceof XSDElementDeclaration) {
            XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) xsdTerm;
            String elementDeclarationName = xsdElementDeclaration.getName();

            System.out.print(elementDeclarationName);
            if (minOccurs == 0) {
                if (maxOccurs == 1) {
                    System.out.print("?"); //$NON-NLS-1$
                } else {
                    System.out.print("*"); //$NON-NLS-1$
                }
            } else if (maxOccurs == -1) {
                System.out.print("+"); //$NON-NLS-1$
            }
            System.out.println("  -- root Container: " //$NON-NLS-1$
                    + ((XSDParticle) xsdElementDeclaration.getContainer()).getTerm().getClass().getName());

        } else if (xsdTerm instanceof XSDModelGroup) {
            XSDModelGroup xsdModelGroup = (XSDModelGroup) xsdTerm;
            List particles = xsdModelGroup.getParticles();

            String separator = XSDCompositor.CHOICE_LITERAL == xsdModelGroup.getCompositor() ? "|" //$NON-NLS-1$
                    : XSDCompositor.SEQUENCE_LITERAL == xsdModelGroup.getCompositor() ? "&" : "a"; //$NON-NLS-1$ //$NON-NLS-2$

            for (Iterator it = particles.iterator(); it.hasNext();) {
                XSDParticle childParticle = (XSDParticle) it.next();
                System.out.print(ident + "[" + separator + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                processParticle(childParticle, ident + "    "); //$NON-NLS-1$
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

            System.out.println();

        } else if (xsdTerm instanceof XSDWildcard) {
            System.out.print("<em>{any}</em>"); //$NON-NLS-1$
        }
    }

    /**
     * Print a particle with markup for the document.
     * 
     * @param xsdParticle a particle.
     * @param rootElementDeclarationMarkup the markup.
     */
    /*
     * public void printParticle(XSDParticle xsdParticle, String rootElementDeclarationMarkup) { int minOccurs =
     * xsdParticle.getMinOccurs(); int maxOccurs = xsdParticle.getMaxOccurs(); XSDTerm xsdTerm = xsdParticle.getTerm();
     * if (xsdTerm instanceof XSDElementDeclaration) { XSDElementDeclaration xsdElementDeclaration =
     * (XSDElementDeclaration) xsdTerm; String elementDeclarationName = xsdElementDeclaration.getName(); String
     * elementDeclarationMarkup = null; if (rootElementDeclarationMarkup == null) { elementDeclarationMarkup =
     * getElementDeclarationMarkup(elementDeclarationName); } if (elementDeclarationMarkup != null) {
     * System.out.print("<span class='"); System.out.print(elementDeclarationMarkup); System.out.print("'>"); }
     * System.out.print("<a href='#" + getLocalAnchor(xsdElementDeclaration) + "'>");
     * System.out.print(elementDeclarationName.charAt(0)); System.out.print("</a>");
     * System.out.print(elementDeclarationName.substring(1)); if (elementDeclarationMarkup != null) {
     * System.out.print("</span>"); } if (minOccurs == 0) { if (maxOccurs == 1) { System.out.print("?"); } else {
     * System.out.print("*"); } } else if (maxOccurs == -1) { System.out.print("+"); } } else if (xsdTerm instanceof
     * XSDModelGroup) { XSDModelGroup xsdModelGroup = (XSDModelGroup) xsdTerm; List particles =
     * xsdModelGroup.getParticles(); boolean isRedundant = particles.size() == 1 && minOccurs == 1 && maxOccurs == 1 &&
     * ((XSDParticle) particles.get(0)).getTerm() instanceof XSDModelGroup; if (!isRedundant) { System.out.print("(");
     * // ) }
     * 
     * String separator = XSDCompositor.CHOICE_LITERAL == xsdModelGroup .getCompositor() ? " | " :
     * XSDCompositor.SEQUENCE_LITERAL == xsdModelGroup .getCompositor() ? ",  " : "  &  ";
     * 
     * for (Iterator i = xsdModelGroup.getParticles().iterator(); i .hasNext();) { XSDParticle childParticle =
     * (XSDParticle) i.next(); printParticle(childParticle, rootElementDeclarationMarkup); if (i.hasNext()) {
     * System.out.print(separator); } }
     * 
     * if (!isRedundant) { // ( System.out.print(")"); if (minOccurs == 0) { if (maxOccurs == 1) {
     * System.out.print("?"); } else { System.out.print("*"); } } else if (maxOccurs == -1) { System.out.print("+"); } }
     * } else if (xsdTerm instanceof XSDWildcard) { System.out.print("<em>{any}</em>"); } }
     */

    /**
     * Print a simple type definition for the document.
     * 
     * @param xsdSimpleTypeDefinition a simple type definition in the schema for schema.
     */
    /*
     * public void printSimpleTypeDefinition( XSDSimpleTypeDefinition xsdSimpleTypeDefinition) { if
     * (xsdSimpleTypeDefinition == null) { } else if (xsdSimpleTypeDefinition.getEffectiveEnumerationFacet() != null) {
     * List value = xsdSimpleTypeDefinition.getEffectiveEnumerationFacet() .getValue(); if (value.size() > 1) {
     * System.out.print("("); } for (Iterator enumerators = value.iterator(); enumerators.hasNext();) { String
     * enumerator = enumerators.next().toString(); System.out.print("<em>"); System.out.print(enumerator);
     * System.out.print("</em>"); if (enumerators.hasNext()) { System.out.print("&nbsp;|&nbsp;"); } } if (value.size() >
     * 1) { System.out.print(")"); } } else if (xsdSimpleTypeDefinition.getElement() != null &&
     * xsdSimpleTypeDefinition.getElement().hasAttribute( XSDConstants.ID_ATTRIBUTE)) { System.out.print("<a href='#" +
     * xsdSimpleTypeDefinition.getName() + "-simple-type'>"); System.out.print(xsdSimpleTypeDefinition.getName());
     * System.out.print("</a>"); } else if (XSDVariety.UNION_LITERAL == xsdSimpleTypeDefinition .getVariety()) {
     * System.out.print("("); for (Iterator members = xsdSimpleTypeDefinition .getMemberTypeDefinitions().iterator();
     * members.hasNext();) { XSDSimpleTypeDefinition memberTypeDefinition = (XSDSimpleTypeDefinition) members .next();
     * printSimpleTypeDefinition(memberTypeDefinition); if (members.hasNext()) { System.out.print("&nbsp;|&nbsp;"); } }
     * System.out.print(")"); } else if (XSDVariety.UNION_LITERAL == xsdSimpleTypeDefinition .getVariety()) {
     * System.out.print("List&nbsp;of&nbsp;"); printSimpleTypeDefinition(xsdSimpleTypeDefinition
     * .getItemTypeDefinition()); } else if (xsdSimpleTypeDefinition.getName() != null) { if
     * ("public".equals(xsdSimpleTypeDefinition.getName())) { System.out.print("<a target='Part2' href='" +
     * XSDConstants.PART2 + "#anyURI'>anyURI</a>&nbsp;&nbsp;"); System.out.print("<a target='Errata' href='" + errata +
     * "#pfipublic'><em>public</em></a>"); } else { System.out.print("<b><em>");
     * System.out.print(xsdSimpleTypeDefinition.getName()); System.out.print("</em></b>"); } } else if
     * (xsdSimpleTypeDefinition.getEffectivePatternFacet() != null) { //
     * System.out.print(xsdSimpleTypeDefinition.getEffectivePatternFacet().getLexicalValue());
     * 
     * System.out.print("<em>"); System.out.print("<a target='Part1' href='" + XSDConstants.PART1 +
     * "#coss-identity-constraint'>"); System.out.print("a restricted xpath expression"); System.out.print("</a>");
     * System.out.print("</em>"); } else { System.out.print("***"); } }
     */

    public static void main(String args[]) {
        try {
            /*
             * String xsd = ""; FileInputStream fis = new FileInputStream(
             * "/home/bgrieder/workspace/XCBL35/XCBL35.xsd"); BufferedReader br = new BufferedReader(new
             * InputStreamReader(fis, "utf-8")); String line; while ((line = br.readLine()) != null) xsd += line + "\n";
             * 
             * XSDParser parser = new XSDParser(); parser.loadAndPrint(xsd);
             */
            FileWriter fw = new FileWriter("/tmp/xcb35sr.xsd"); //$NON-NLS-1$

            Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd", new XSDResourceFactoryImpl()); //$NON-NLS-1$
            String xsdFile = "/home/bgrieder/workspace/XCBL35/XCBL35.xsd"; //$NON-NLS-1$
            ResourceSet resourceSet = new ResourceSetImpl();
            XSDResourceImpl xsdResource = (XSDResourceImpl) resourceSet.getResource(URI.createFileURI(xsdFile), true);

            /*
             * XSDResourceImpl res = new XSDResourceImpl(URI.createFileURI(xsdFile));
             */
            XSDSchema xsdSchema = xsdResource.getSchema();

            String header = "<xsd:schema " + "elementFormDefault=\"qualified\" " //$NON-NLS-1$ //$NON-NLS-2$
                    + "targetNamespace=\"rrn:org.xcbl:schemas/xcbl/v3_5/xcbl35.xsd\" " //$NON-NLS-1$
                    + "xmlns=\"rrn:org.xcbl:schemas/xcbl/v3_5/xcbl35.xsd\" " + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"; //$NON-NLS-1$ //$NON-NLS-2$

            fw.write(header);

            Iterator it = xsdSchema.getElementDeclarations().iterator();
            for (; it.hasNext();) {
                XSDElementDeclaration elementDeclaration = (XSDElementDeclaration) it.next();
                // if ("Order".equals(elementDeclaration.getName())) {
                fw.write(Util.nodeToString(elementDeclaration.getElement()).replaceAll(
                        "xmlns:xsd=\"http:\\/\\/www\\.w3\\.org\\/2001\\/XMLSchema\"", "")); //$NON-NLS-1$ //$NON-NLS-2$
                // }
            }
            it = xsdSchema.getTypeDefinitions().iterator();
            for (; it.hasNext();) {
                XSDTypeDefinition typedef = (XSDTypeDefinition) it.next();
                fw.write(Util.nodeToString(typedef.getElement()));
            }
            String footer = "</xsd:schema>"; //$NON-NLS-1$
            fw.write(footer);
            fw.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
