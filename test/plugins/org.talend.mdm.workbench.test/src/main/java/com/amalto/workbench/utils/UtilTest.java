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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.utils.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.junit.Before;
import org.junit.Test;
import org.talend.mdm.commmon.util.workbench.ZipToFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.workbench.webservices.WSRoutingRuleExpression;
import com.amalto.workbench.webservices.WSRoutingRuleOperator;
import com.amalto.workbench.webservices.WSStringPredicate;
import com.amalto.workbench.webservices.WSWhereCondition;
import com.amalto.workbench.webservices.WSWhereOperator;

public class UtilTest {

    private Logger log = Logger.getLogger(UtilTest.class);

    XSDSchema schema;

    @Before
    public void setUp() throws Exception {
        initSchema();
    }

    private void initSchema() throws Exception {
        String xsd = null;
        xsd = "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + //$NON-NLS-1$
                "<xsd:import namespace=\"http://www.w3.org/2001/XMLSchema\"/>" + //$NON-NLS-1$
                "<xsd:element name=\"Entity\">" + //$NON-NLS-1$
                "<xsd:complexType>" + //$NON-NLS-1$
                "<xsd:all>" + //$NON-NLS-1$
                "<xsd:element name=\"id\" type=\"xsd:string\"/>" + //$NON-NLS-1$
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"field1\" type=\"xsd:string\"/>" //$NON-NLS-1$
                + "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"field2\" type=\"xsd:string\"/>"//$NON-NLS-1$
                + "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"field3\" type=\"xsd:string\"/>"//$NON-NLS-1$
                + "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"field4\" type=\"xsd:string\"/>"//$NON-NLS-1$
                + "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"field5\" type=\"xsd:string\"/>"//$NON-NLS-1$
                + "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"field6\" type=\"xsd:string\"/>" + //$NON-NLS-1$
                "</xsd:all>" + //$NON-NLS-1$
                "</xsd:complexType>" + //$NON-NLS-1$
                "<xsd:unique name=\"Entity\">" + "<xsd:selector xpath=\".\"/>" //$NON-NLS-1$ //$NON-NLS-2$
                + "<xsd:field xpath=\"id\"/>" + //$NON-NLS-1$
                "</xsd:unique>" + //$NON-NLS-1$
                "</xsd:element>" + //$NON-NLS-1$
                "</xsd:schema>";//$NON-NLS-1$

        schema = Util.getXSDSchema(xsd);

    }

    /**
     * Test method for
     * {@link com.amalto.workbench.utils.Util#getComplexTypeDefinitionChildren(org.eclipse.xsd.XSDComplexTypeDefinition, boolean)}
     * .
     */
    @Test
    public void testGetComplexTypeDefinitionChildren() throws Exception {

        // get test model
        XSDSchema xsdSchema = getXSDSchema();

        EList<XSDElementDeclaration> elementDeclarations = xsdSchema.getElementDeclarations();
        // test
        assertEquals(1, elementDeclarations.size());
        XSDTypeDefinition typeDefinition = elementDeclarations.get(0).getTypeDefinition();
        ArrayList<Object> children = Util.getComplexTypeDefinitionChildren((XSDComplexTypeDefinition) typeDefinition, true);
        assertEquals(3, children.size());
        children = Util.getComplexTypeDefinitionChildren((XSDComplexTypeDefinition) typeDefinition, false);
        assertEquals(1, children.size());
    }

    private XSDSchema getXSDSchema() throws Exception {
        InputStream in = UtilTest.class.getResourceAsStream("TestGetComplexTypeDefinitionChildren_0.1.xsd"); //$NON-NLS-1$
        try {
            byte[] buf = new byte[in.available()];
            IOUtils.readFully(in, buf);
            String xsdString = new String(buf);
            if (xsdString != null) {
                XSDSchema xsdSchema = Util.getXSDSchema(xsdString);
                return xsdSchema;
            }
            return null;
        } finally {
            in.close();
        }
    }

    @Test
    public void testJoinStrings() {
        String[] strs = { "a", "b" }; //$NON-NLS-1$ //$NON-NLS-2$
        String str = Util.joinStrings(strs, ";"); //$NON-NLS-1$
        assertEquals("a;b", str); //$NON-NLS-1$
    }

    @Test
    public void testGetConceptFromPath() {
        String xpath = "Entity/Id[aa>0]"; //$NON-NLS-1$
        String entity = Util.getConceptFromPath(xpath);
        assertEquals("Entity", entity); //$NON-NLS-1$
    }

    @Test
    public void testGetConceptName() {
        XSDElementDeclaration el = schema.getElementDeclarations().get(0);
        XSDComplexTypeDefinition type = (XSDComplexTypeDefinition) el.getType();
        ArrayList<Object> children = Util.getComplexTypeDefinitionChildren(type);
        String concept = Util.getConceptName((XSDConcreteComponent) children.get(0));
        assertEquals("Entity", concept); //$NON-NLS-1$
    }

    @Test
    public void testNodeToString() throws Exception {
        String xml = "<a>a</a>"; //$NON-NLS-1$
        Node node = Util.parse(xml).getDocumentElement();
        String xml2 = Util.nodeToString(node);
        assertEquals(xml2.trim(), xml);
    }

    @Test
    public void testGetNodeList() throws Exception {
        String xml = "<node><id>id</id><name>name</name></node>"; //$NON-NLS-1$
        Node node = Util.parse(xml).getDocumentElement();
        NodeList list = Util.getNodeList(node, "id"); //$NON-NLS-1$
        assertEquals(list.item(0).getNodeName(), "id"); //$NON-NLS-1$
    }

    @Test
    public void testGetRootElement() throws Exception {
        Element root = Util.getRootElement("elementName", "namespace", null); //$NON-NLS-1$//$NON-NLS-2$
        assertEquals(root.getNamespaceURI(), "namespace"); //$NON-NLS-1$
    }

    @Test
    public void testParse() throws Exception {
        String xml = "<a>a</a>"; //$NON-NLS-1$
        Document d = Util.parse(xml);
        assertEquals(d.getDocumentElement().getNodeName(), "a"); //$NON-NLS-1$
    }

    @Test
    public void testGetAllCustomSimpleDataType() {
        List<String> list = Util.getAllCustomSimpleDataType(schema);
        assertEquals(list.size(), 0);
    }

    @Test
    public void testGetAllSchemaSimpleDataType() {
        List<String> list = Util.getAllSchemaSimpleDataType(schema);
        assertEquals(list.size(), 56);
    }

    @Test
    public void testGetKeyInfo() {
        List<Object> list = Util.getKeyInfo(schema.getElementDeclarations().get(0));
        assertEquals(list, null);
    }

    @Test
    public void testGetForeingKeyInSchema() {
        Set<String> list = new HashSet<String>();
        Util.getForeingKeyInSchema(list, schema);
        assertEquals(list.size(), 0);
    }

    @Test
    public void testGetChildElementNames() throws Exception {
        List<String> list = Util.getChildElementNames(schema, "Entity"); //$NON-NLS-1$
        assertEquals(7, list.size());
    }

    @Test
    public void testGetChildElements() throws Exception {
        EList<XSDElementDeclaration> xsdElementDeclarations = schema.getElementDeclarations();
        XSDElementDeclaration conceptEl = null;
        for (XSDElementDeclaration el : xsdElementDeclarations) {
            if (el.getName().equals("Entity")) { //$NON-NLS-1$
                conceptEl = el;
                break;
            }
        }

        Map<String, XSDParticle> childElements = Util.getChildElements("", conceptEl, false, new HashSet<Object>()); //$NON-NLS-1$
        assertNotNull(childElements);
        assertEquals(7, childElements.size());
    }

    @Test
    public void testGetChildElements4TypeArg() throws Exception {
        EList<XSDElementDeclaration> xsdElementDeclarations = schema.getElementDeclarations();
        XSDElementDeclaration conceptEl = null;
        for (XSDElementDeclaration el : xsdElementDeclarations) {
            if (el.getName().equals("Entity")) { //$NON-NLS-1$
                conceptEl = el;
                break;
            }
        }

        assertNotNull(conceptEl);

        Map<String, XSDParticle> childElements = Util.getChildElements(
                "", (XSDComplexTypeDefinition) conceptEl.getTypeDefinition(), false, new HashSet<Object>()); //$NON-NLS-1$
        assertNotNull(childElements);
        assertEquals(7, childElements.size());
    }

    @Test
    public void testConvertWhereCondition() {
        WSWhereCondition wc = new WSWhereCondition("Entity/Id", WSWhereOperator.CONTAINS, "id1", false, WSStringPredicate.OR); //$NON-NLS-1$ //$NON-NLS-2$
        String[] lines = Util.convertWhereCondition(wc);
        assertEquals(lines.length, 4);
        assertEquals(lines[0], "Entity/Id"); //$NON-NLS-1$
        assertEquals(lines[1], "Contains"); //$NON-NLS-1$
        assertEquals(lines[2], "id1"); //$NON-NLS-1$
        assertEquals(lines[3], "Or"); //$NON-NLS-1$
    }

    @Test
    public void testConvertRouteCondition() {
        WSRoutingRuleExpression wr = new WSRoutingRuleExpression("name", "value", WSRoutingRuleOperator.CONTAINS, "Entity/Id"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String[] lines = Util.convertRouteCondition(wr);
        assertEquals(lines.length, 4);
        assertEquals(lines[0], "Entity/Id"); //$NON-NLS-1$
        assertEquals(lines[1], "Contains"); //$NON-NLS-1$
        assertEquals(lines[2], "value"); //$NON-NLS-1$
        assertEquals(lines[3], "name"); //$NON-NLS-1$

    }

    @Test
    public void testConvertLineRoute() {
        String[] values = { "Entity/Id", "Contains", "value", "name" }; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        WSRoutingRuleExpression wr = Util.convertLineRoute(values);
        assertEquals(wr.getXpath(), "Entity/Id"); //$NON-NLS-1$
        assertEquals(wr.getWsOperator().value(), "CONTAINS"); //$NON-NLS-1$
        assertEquals(wr.getValue(), "value"); //$NON-NLS-1$
        assertEquals(wr.getName(), "name"); //$NON-NLS-1$
    }

    @Test
    public void testConvertLine() {
        String[] values = { "Entity/Id", "Contains", "id1", "Or" }; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        WSWhereCondition wc = Util.convertLine(values);
        assertEquals(wc.getLeftPath(), "Entity/Id"); //$NON-NLS-1$
        assertEquals(wc.getOperator().value(), "CONTAINS"); //$NON-NLS-1$
        assertEquals(wc.getRightValueOrPath(), "id1"); //$NON-NLS-1$
        assertEquals(wc.getStringPredicate().value(), "OR"); //$NON-NLS-1$
    }

    @Test
    public void testGetItemContent() {
        String xmlstring = "<c>Entity></c><t>1313434343</t><p>abcdefg</p>"; //$NON-NLS-1$
        String xml = Util.getItemContent(xmlstring);
        assertEquals(xml, "abcdefg"); //$NON-NLS-1$
        xmlstring = "<c>Entity></c><t>1313434343</t><p/>"; //$NON-NLS-1$
        xml = Util.getItemContent(xmlstring);
        assertEquals(xml, ""); //$NON-NLS-1$
    }

    @Test
    public void testGetAllComplexTypeChildren() {
        ArrayList<Object> list = Util.getAllComplexTypeChildren(schema.getElementDeclarations().get(0));
        assertEquals(list.size(), 1);
    }

    @Test
    public void testGetConcepts() {
        List<String> list = Util.getConcepts(schema);
        assertEquals(list.size(), 1);
        assertEquals(list.get(0), "Entity"); //$NON-NLS-1$
    }

    @Test
    public void testGetNewLabelString() {
        String oldString = "Routing Engine V2"; //$NON-NLS-1$
        String newLabel = Util.getNewLabelString(oldString);
        assertEquals("Event Manager", newLabel); //$NON-NLS-1$
    }

    @Test
    public void testCheckInCOpyTypeParticle() {
        Object[] sel = { schema.getElementDeclarations().get(0) };
        boolean ret = Util.checkInCOpyTypeParticle(sel);
        assertEquals(ret, false);
    }

    @Test
    public void testCheckInCopyTypeElement() {
        Object[] sel = { schema.getElementDeclarations().get(0) };
        boolean ret = Util.checkInCopyTypeElement(sel);
        assertEquals(ret, true);
    }

    @Test
    public void testGetParticleName() {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) schema.getElementDeclarations().get(0)
                .getTypeDefinition();
        XSDModelGroup group = (XSDModelGroup) ((XSDParticle) complexType.getContent()).getTerm();
        String name = Util.getParticleName(group.getParticles().get(0));
        assertEquals(name, "id"); //$NON-NLS-1$
    }

    @Test
    public void testGetParticleReferenceName() {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) schema.getElementDeclarations().get(0)
                .getTypeDefinition();
        XSDModelGroup group = (XSDModelGroup) ((XSDParticle) complexType.getContent()).getTerm();
        XSDParticle curXSDParticle = group.getParticles().get(0);
        String name = Util.getParticleReferenceName(curXSDParticle);
        assertEquals(name, ""); //$NON-NLS-1$
    }

    @Test
    public void testGetAllCustomTypeNames() {
        List<String> list = Util.getAllCustomTypeNames(schema);
        // AUTO_INCREMENT, PICTURE, MULTI_LINGUAL, UUID, URL
        assertEquals(list.size(), 5);
    }

    @Test
    public void testIsUUID() {
        boolean ret = Util.isUUID(schema.getElementDeclarations().get(0));
        assertEquals(ret, false);
    }

    @Test
    public void testIsCustomrType() {
        boolean ret = Util.isCustomrType(schema, "id"); //$NON-NLS-1$
        assertEquals(ret, false);
    }

    @Test
    public void testGetAllBuildInTypes() {
        List<XSDSimpleTypeDefinition> list = Util.getAllBuildInTypes(schema);
        assertEquals(list.size(), 56);
    }

    @Test
    public void testIsBuildInType() {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) schema.getElementDeclarations().get(0)
                .getTypeDefinition();
        XSDModelGroup group = (XSDModelGroup) ((XSDParticle) complexType.getContent()).getTerm();
        XSDElementDeclaration el = (XSDElementDeclaration) group.getParticles().get(0).getTerm();
        boolean ret = Util.isBuildInType((XSDSimpleTypeDefinition) el.getType());
        assertEquals(ret, true);
    }

    @Test
    public void testIsSequenceComplexType() {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) schema.getElementDeclarations().get(0)
                .getTypeDefinition();
        boolean ret = Util.isSequenceComplexType(complexType);
        assertEquals(ret, false);
    }

    @Test
    public void testIsAllComplexType() {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) schema.getElementDeclarations().get(0)
                .getTypeDefinition();
        boolean ret = Util.isAllComplexType(complexType);
        assertEquals(ret, true);
    }

    @Test
    public void testIsChoiceComplexType() {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) schema.getElementDeclarations().get(0)
                .getTypeDefinition();
        boolean ret = Util.isChoiceComplexType(complexType);
        assertEquals(ret, false);
    }

    @Test
    public void testGetComplexTypeGroupType() {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) schema.getElementDeclarations().get(0)
                .getTypeDefinition();
        XSDCompositor compositor = Util.getComplexTypeGroupType(complexType);
        assertEquals(compositor.getName(), "all"); //$NON-NLS-1$
    }

    @Test
    public void testUpdateChildrenReferenceToComplexType() {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) schema.getElementDeclarations().get(0)
                .getTypeDefinition();
        Util.updateChildrenReferenceToComplexType(complexType);
    }

    @Test
    public void testGetParentTypes() {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) schema.getElementDeclarations().get(0)
                .getTypeDefinition();
        List<XSDTypeDefinition> list = Util.getParentTypes(complexType);
        assertEquals(list.size(), 1);
    }

    @Test
    public void testIsDouble() {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) schema.getElementDeclarations().get(0)
                .getTypeDefinition();
        XSDModelGroup group = (XSDModelGroup) ((XSDParticle) complexType.getContent()).getTerm();
        XSDElementDeclaration el = (XSDElementDeclaration) group.getParticles().get(0).getTerm();
        boolean ret = Util.isDouble((XSDSimpleTypeDefinition) el.getTypeDefinition());
        assertEquals(ret, false);
    }

    @Test
    public void testIsFloat() {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) schema.getElementDeclarations().get(0)
                .getTypeDefinition();
        XSDModelGroup group = (XSDModelGroup) ((XSDParticle) complexType.getContent()).getTerm();
        XSDElementDeclaration el = (XSDElementDeclaration) group.getParticles().get(0).getTerm();
        boolean ret = Util.isFloat((XSDSimpleTypeDefinition) el.getTypeDefinition());
        assertEquals(ret, false);
    }

    @Test
    public void testIsDecimal() {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) schema.getElementDeclarations().get(0)
                .getTypeDefinition();
        XSDModelGroup group = (XSDModelGroup) ((XSDParticle) complexType.getContent()).getTerm();
        XSDElementDeclaration el = (XSDElementDeclaration) group.getParticles().get(0).getTerm();
        boolean ret = Util.isDecimal((XSDSimpleTypeDefinition) el.getTypeDefinition());
        assertEquals(ret, false);
    }

    @Test
    public void testIsSpecifiedBuildInType() {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) schema.getElementDeclarations().get(0)
                .getTypeDefinition();
        XSDModelGroup group = (XSDModelGroup) ((XSDParticle) complexType.getContent()).getTerm();
        XSDElementDeclaration el = (XSDElementDeclaration) group.getParticles().get(0).getTerm();
        boolean ret = Util.isSpecifiedBuildInType((XSDSimpleTypeDefinition) el.getTypeDefinition(), "string"); //$NON-NLS-1$
        assertEquals(ret, true);
    }

    @Test
    public void testFormatErrorMessage() {
        String sourceMessage = "[aaa]:3:5:a: "; //$NON-NLS-1$
        String xml = Util.formatErrorMessage(sourceMessage);
        assertEquals(xml, sourceMessage);

    }

    @Test
    public void testGetAllSuperComplexTypes() throws Exception {
        XSDSchema xsdSchema = createSchema();

        XSDComplexTypeDefinition childType = null;
        EList<XSDElementDeclaration> declarations = xsdSchema.getElementDeclarations();
        for (XSDElementDeclaration xed : declarations) {
            if (xed.getName().equals("opo")) { //$NON-NLS-1$
                childType = (XSDComplexTypeDefinition) xed.getType();
                break;
            }
        }

        if (childType != null) {
            List<XSDComplexTypeDefinition> superComplexTypes = Util.getAllSuperComplexTypes(childType);
            assertNotNull(superComplexTypes);
            assertEquals(superComplexTypes.size(), 3);
            assertEquals("Manager", superComplexTypes.get(0).getName()); //$NON-NLS-1$
            assertEquals("Employee", superComplexTypes.get(1).getName()); //$NON-NLS-1$
            assertEquals("Person", superComplexTypes.get(2).getName()); //$NON-NLS-1$
        }
    }

    @Test
    public void testGetRealKeyInfos() throws Exception {
        XSDSchema xsdSchema = createSchema();

        XSDElementDeclaration elementDeclaration = null;
        EList<XSDElementDeclaration> declarations = xsdSchema.getElementDeclarations();
        for (XSDElementDeclaration xed : declarations) {
            if (xed.getName().equals("opo")) { //$NON-NLS-1$
                elementDeclaration = xed;
                break;
            }
        }

        if (elementDeclaration != null) {
            XSDComplexTypeDefinition childType = (XSDComplexTypeDefinition) elementDeclaration.getType();
            childType = (XSDComplexTypeDefinition) childType.getBaseTypeDefinition();
            childType = (XSDComplexTypeDefinition) childType.getBaseTypeDefinition();
            XSDParticle xsdParticle = (XSDParticle) childType.getContent();
            XSDModelGroup modelGroup = (XSDModelGroup) xsdParticle.getTerm();
            EList<XSDParticle> particles = modelGroup.getParticles();

            XSDParticle primaryKeyParticle = null;
            XSDParticle nonePrimaryKeyParticle = null;
            for (XSDParticle particle : particles) {
                XSDElementDeclaration term = (XSDElementDeclaration) particle.getTerm();
                if (term.getName().equals("Id")) { //$NON-NLS-1$
                    primaryKeyParticle = particle;
                    break;
                } else {
                    nonePrimaryKeyParticle = particle;
                }
            }

            if (nonePrimaryKeyParticle == null) {
                nonePrimaryKeyParticle = particles.get(particles.size() - 1);
            }

            assertNull(Util.getRealKeyInfos(null, null));
            assertNull(Util.getRealKeyInfos(elementDeclaration, null));
            assertNull(Util.getRealKeyInfos(null, primaryKeyParticle));

            assertEquals(Util.getRealKeyInfos(elementDeclaration, nonePrimaryKeyParticle).size(), 0);

            List<Object> realKeyInfos = Util.getRealKeyInfos(elementDeclaration, primaryKeyParticle);
            assertNotNull(realKeyInfos);
            assertEquals(realKeyInfos.size(), 2);
            assertTrue((realKeyInfos.get(0) instanceof XSDIdentityConstraintDefinition));
            assertTrue((realKeyInfos.get(1) instanceof XSDXPathDefinition));
            assertEquals(((XSDXPathDefinition) realKeyInfos.get(1)).getValue(), "Id"); //$NON-NLS-1$
        }
    }

    private XSDSchema createSchema() throws Exception {
        String fileName = "ComplexTypeWithInheritance.xsd"; //$NON-NLS-1$
        String xsdContent = TestUtil.readTestResource(this.getClass(), fileName);

        return Util.getXSDSchema(xsdContent);
    }



    @Test
    public void testGetTextNodes() {
        Node contextNode = null;
        String xpath = ""; //$NON-NLS-1$
        Node namespaceNode = contextNode;

        try {
            xpath = "\"pathA\""; //$NON-NLS-1$
            String[] textNodes = Util.getTextNodes(contextNode, xpath, namespaceNode);
            assertNotNull(textNodes);
            assertEquals(1, textNodes.length);
            assertEquals("pathA", textNodes[0]); //$NON-NLS-1$


            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();
            Element nameElement = doc.createElement("xsd:element"); //$NON-NLS-1$
            nameElement.setAttribute("name", "NameA"); //$NON-NLS-1$ //$NON-NLS-2$
            nameElement.setAttribute("type", "xsd:string"); //$NON-NLS-1$ //$NON-NLS-2$
            nameElement.appendChild(doc.createElement("xsd:annotation")); //$NON-NLS-1$
            Element pictureElement = doc.createElement("xsd:element"); //$NON-NLS-1$
            pictureElement.setAttribute("name", "PictureA"); //$NON-NLS-1$ //$NON-NLS-2$
            pictureElement.setAttribute("type", "PICTURE"); //$NON-NLS-1$ //$NON-NLS-2$
            pictureElement.appendChild(doc.createElement("xsd:annotation")); //$NON-NLS-1$

            xpath = "@name"; //$NON-NLS-1$
            textNodes = Util.getTextNodes(nameElement, xpath, nameElement);
            assertNotNull(textNodes);
            assertEquals(1, textNodes.length);
            assertEquals("NameA", textNodes[0]); //$NON-NLS-1$
            textNodes = Util.getTextNodes(pictureElement, xpath, pictureElement);
            assertNotNull(textNodes);
            assertEquals(1, textNodes.length);
            assertEquals("PictureA", textNodes[0]); //$NON-NLS-1$

            xpath = "@type"; //$NON-NLS-1$
            textNodes = Util.getTextNodes(nameElement, xpath, nameElement);
            assertNotNull(textNodes);
            assertEquals(1, textNodes.length);
            assertEquals("xsd:string", textNodes[0]); //$NON-NLS-1$
            textNodes = Util.getTextNodes(pictureElement, xpath, pictureElement);
            assertNotNull(textNodes);
            assertEquals(1, textNodes.length);
            assertEquals("PICTURE", textNodes[0]); //$NON-NLS-1$

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testFindElementsUsingType() {
        String typenameA = "ComplexTypeA"; //$NON-NLS-1$
        String typenameB = "ComplexTypeB"; //$NON-NLS-1$

        XSDParticle xsdParticle0 = XSDFactory.eINSTANCE.createXSDParticle();

        //
        XSDSimpleTypeDefinition xsdSimpleTypeDefinition = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
        List<Object> objList = new ArrayList<Object>();
        objList.add(xsdSimpleTypeDefinition);
        boolean findElements = Util.findElementsUsingType(objList, xsdSimpleTypeDefinition);
        assertFalse(findElements);

        //
        objList.clear();
        XSDComplexTypeDefinition xsdComplexTypeDefinition1 = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
        objList.add(xsdComplexTypeDefinition1);
        findElements = Util.findElementsUsingType(objList, xsdComplexTypeDefinition1);
        assertFalse(findElements);

        //
        objList.clear();
        xsdComplexTypeDefinition1.setName(typenameA);
        XSDComplexTypeDefinition xsdComplexTypeDefinition2 = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
        xsdComplexTypeDefinition2.setName(typenameA);
        objList.add(xsdComplexTypeDefinition2);
        findElements = Util.findElementsUsingType(objList, xsdComplexTypeDefinition1);
        assertTrue(findElements);

        //
        objList.clear();
        XSDElementDeclaration xsdElementDeclaration = XSDFactory.eINSTANCE.createXSDElementDeclaration();
        xsdElementDeclaration.setTypeDefinition(xsdComplexTypeDefinition2);
        objList.add(xsdElementDeclaration);
        findElements = Util.findElementsUsingType(objList, xsdComplexTypeDefinition1);
        assertTrue(findElements);

        objList.clear();
        xsdParticle0.setContent(xsdElementDeclaration);
        objList.add(xsdParticle0);
        findElements = Util.findElementsUsingType(objList, xsdComplexTypeDefinition1);
        assertTrue(findElements);

        //
        objList.clear();
        XSDComplexTypeDefinition xsdComplexTypeDefinition_child = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
        xsdComplexTypeDefinition_child.setName(typenameA);
        XSDElementDeclaration xsdElementDeclaration_child = XSDFactory.eINSTANCE.createXSDElementDeclaration();
        xsdElementDeclaration_child.setTypeDefinition(xsdComplexTypeDefinition_child);
        XSDParticle xsdParticle_child = XSDFactory.eINSTANCE.createXSDParticle();
        xsdParticle_child.setContent(xsdElementDeclaration_child);
        EList<XSDParticle> elist = new BasicEList<XSDParticle>();
        elist.add(xsdParticle_child);
        XSDModelGroup xsdModelGroup = XSDFactory.eINSTANCE.createXSDModelGroup();
        xsdModelGroup.getContents().addAll(elist);
        XSDParticle xsdParticle = XSDFactory.eINSTANCE.createXSDParticle();
        xsdParticle.setTerm(xsdModelGroup);

        xsdComplexTypeDefinition2.setName(typenameB);
        xsdComplexTypeDefinition2.setContent(xsdParticle);
        objList.add(xsdElementDeclaration);
        findElements = Util.findElementsUsingType(objList, xsdComplexTypeDefinition1);
        assertTrue(findElements);

        objList.clear();
        xsdParticle0.setContent(xsdElementDeclaration);
        objList.add(xsdParticle0);
        findElements = Util.findElementsUsingType(objList, xsdComplexTypeDefinition1);
        assertTrue(findElements);

        //
        objList.clear();
        objList.add(xsdComplexTypeDefinition2);
        findElements = Util.findElementsUsingType(objList, xsdComplexTypeDefinition1);
        assertTrue(findElements);

        //
        XSDSimpleTypeDefinition xsdSimpleTypeDefinition_child = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
        xsdSimpleTypeDefinition_child.setName(typenameA);
        xsdElementDeclaration_child.setTypeDefinition(xsdSimpleTypeDefinition_child);
        findElements = Util.findElementsUsingType(objList, xsdComplexTypeDefinition1);
        assertFalse(findElements);

        //
        objList.clear();
        XSDSimpleTypeDefinition xsdSimpleBaseTypeDef = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
        xsdSimpleBaseTypeDef.setName(typenameA);
        XSDSimpleTypeDefinition xsdSimpleTypeDef = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
        xsdSimpleTypeDef.setBaseTypeDefinition(xsdSimpleBaseTypeDef);

        XSDSimpleTypeDefinition xsdSimpleTypeDef_tosearch = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
        xsdSimpleTypeDef_tosearch.setName(typenameA);
        objList.add(xsdSimpleTypeDef);
        findElements = Util.findElementsUsingType(objList, xsdSimpleTypeDef_tosearch);
        assertTrue(findElements);

        //
        objList.clear();
        XSDElementDeclaration xsdElementDecl = XSDFactory.eINSTANCE.createXSDElementDeclaration();
        xsdElementDecl.setTypeDefinition(xsdSimpleTypeDef);
        objList.add(xsdElementDecl);
        findElements = Util.findElementsUsingType(objList, xsdSimpleTypeDef_tosearch);
        assertTrue(findElements);

        objList.clear();
        xsdParticle0.setContent(xsdElementDecl);
        objList.add(xsdParticle0);
        findElements = Util.findElementsUsingType(objList, xsdSimpleTypeDef_tosearch);
        assertTrue(findElements);

        //
        XSDComplexTypeDefinition xsdComplexTypedef_search = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
        xsdComplexTypedef_search.setName(typenameB);
        findElements = Util.findElementsUsingType(objList, xsdComplexTypedef_search);
        assertFalse(findElements);

        //
        objList.clear();
        objList.add(xsdSimpleTypeDef);
        findElements = Util.findElementsUsingType(objList, xsdComplexTypedef_search);
        assertFalse(findElements);

    }

    @Test
    public void testGetForeignKeyofParcle() {
        Set<String> list = new HashSet<String>();
        try {
            Util.getforeignKeyOfElement(list, null);
            assertTrue(list.isEmpty());

            //
            Document doc = getEmptyDocument();

            Element appinfoElement1 = doc.createElementNS("http://www.w3.org/XML/1998/namespace", "appinfo"); //$NON-NLS-1$ //$NON-NLS-2$
            appinfoElement1.setAttribute("source", "X_ForeignKey"); //$NON-NLS-1$ //$NON-NLS-2$
            appinfoElement1.appendChild(doc.createTextNode("Store/Id")); //$NON-NLS-1$

            Element appinfoElement2 = doc.createElementNS("http://www.w3.org/XML/1998/namespace", "appinfosssss"); //$NON-NLS-1$ //$NON-NLS-2$
            appinfoElement2.setAttribute("source", "X_ForeignKey"); //$NON-NLS-1$ //$NON-NLS-2$
            appinfoElement2.appendChild(doc.createTextNode("ProductFamily/Id")); //$NON-NLS-1$

            XSDAnnotation xsdAnnotation = XSDFactory.eINSTANCE.createXSDAnnotation();
            EList<Element> applicationInformations = xsdAnnotation.getApplicationInformation();
            applicationInformations.add(appinfoElement1);
            applicationInformations.add(appinfoElement2);

            Util.getForeignKeyofParcle(list, xsdAnnotation);
            assertTrue(list.size() == 1);
            assertTrue(list.contains("Store")); //$NON-NLS-1$
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private Document getEmptyDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        return doc;
    }

    @Test
    public void testGetforeignKeyOfElement() {
        String attKey = "source"; //$NON-NLS-1$
        String attValue = "X_ForeignKey"; //$NON-NLS-1$
        String namespaceURI = "http://www.w3.org/XML/1998/namespace"; //$NON-NLS-1$
        String qualifiedName = "appinfo"; //$NON-NLS-1$

        Set<String> list = new HashSet<String>();
        try {
            Util.getforeignKeyOfElement(list, null);
            assertTrue(list.isEmpty());

            //
            Document doc = getEmptyDocument();

            Element appinfoElement1 = doc.createElementNS(namespaceURI, qualifiedName);
            appinfoElement1.setAttribute(attKey, attValue);
            appinfoElement1.appendChild(doc.createTextNode("StoreA/Id")); //$NON-NLS-1$

            Element appinfoElement2 = doc.createElementNS(namespaceURI, "appinfosssss"); //$NON-NLS-1$
            appinfoElement2.setAttribute(attKey, attValue);
            appinfoElement2.appendChild(doc.createTextNode("StoreB/Id")); //$NON-NLS-1$

            XSDAnnotation xsdAnnotation = XSDFactory.eINSTANCE.createXSDAnnotation();
            EList<Element> applicationInformations = xsdAnnotation.getApplicationInformation();
            applicationInformations.add(appinfoElement1);
            applicationInformations.add(appinfoElement2);

            // prepare a
            XSDAnnotation xsdAnnotation_a = XSDFactory.eINSTANCE.createXSDAnnotation();
            Element appElement_a = doc.createElementNS(namespaceURI, qualifiedName);
            appElement_a.setAttribute(attKey, attValue); // $NON-NLS-1$
            appElement_a.appendChild(doc.createTextNode("StoreC/Id")); //$NON-NLS-1$
            xsdAnnotation_a.getApplicationInformation().add(appElement_a);
            XSDElementDeclaration xsdElementDeclaration_a = XSDFactory.eINSTANCE.createXSDElementDeclaration();
            xsdElementDeclaration_a.setAnnotation(xsdAnnotation_a);
            XSDParticle xsdParticle_a = XSDFactory.eINSTANCE.createXSDParticle();
            xsdParticle_a.setTerm(xsdElementDeclaration_a);

            // prepare b
            XSDAnnotation xsdAnnotation_b = XSDFactory.eINSTANCE.createXSDAnnotation();
            Element appElement_b = doc.createElementNS(namespaceURI, qualifiedName);
            appElement_b.setAttribute(attKey, attValue); // $NON-NLS-1$
            appElement_b.appendChild(doc.createTextNode("StoreD/Id")); //$NON-NLS-1$
            xsdAnnotation_b.getApplicationInformation().add(appElement_b);
            XSDElementDeclaration xsdElementDeclaration_b = XSDFactory.eINSTANCE.createXSDElementDeclaration();
            xsdElementDeclaration_b.setAnnotation(xsdAnnotation_b);
            XSDParticle xsdParticle_b = XSDFactory.eINSTANCE.createXSDParticle();
            xsdParticle_b.setTerm(xsdElementDeclaration_b);

            // prepare c
            XSDAnnotation xsdAnnotation_c = XSDFactory.eINSTANCE.createXSDAnnotation();
            Element appElement_c = doc.createElementNS(namespaceURI, qualifiedName);
            appElement_c.setAttribute(attKey, attValue); // $NON-NLS-1$
            appElement_c.appendChild(doc.createTextNode("StoreE/Id")); //$NON-NLS-1$
            xsdAnnotation_c.getApplicationInformation().add(appElement_c);
            XSDElementDeclaration xsdElementDeclaration_c = XSDFactory.eINSTANCE.createXSDElementDeclaration();
            xsdElementDeclaration_c.setAnnotation(xsdAnnotation_c);
            XSDComplexTypeDefinition xsdComplexTypeDefinition_c = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
            xsdElementDeclaration_c.setTypeDefinition(xsdComplexTypeDefinition_c);
            XSDParticle xsdParticle_c_1 = XSDFactory.eINSTANCE.createXSDParticle();
            xsdComplexTypeDefinition_c.setContent(xsdParticle_c_1);
            XSDModelGroup xsdModelGroup_c_1 = XSDFactory.eINSTANCE.createXSDModelGroup();
            xsdParticle_c_1.setTerm(xsdModelGroup_c_1);
            XSDParticle xsdParticle_c_2 = XSDFactory.eINSTANCE.createXSDParticle();
            xsdModelGroup_c_1.getContents().add(xsdParticle_c_2);
            XSDElementDeclaration xsdElementDeclaration_c_2 = XSDFactory.eINSTANCE.createXSDElementDeclaration();
            xsdParticle_c_2.setTerm(xsdElementDeclaration_c_2);
            XSDAnnotation xsdAnnotation_c_2 = XSDFactory.eINSTANCE.createXSDAnnotation();
            Element appElement_c_2 = doc.createElementNS(namespaceURI, qualifiedName);
            appElement_c_2.setAttribute(attKey, attValue); // $NON-NLS-1$
            appElement_c_2.appendChild(doc.createTextNode("StoreF/Id")); //$NON-NLS-1$
            xsdAnnotation_c_2.getApplicationInformation().add(appElement_c_2);
            xsdElementDeclaration_c_2.setAnnotation(xsdAnnotation_c_2);

            XSDParticle xsdParticle_c = XSDFactory.eINSTANCE.createXSDParticle();
            xsdParticle_c.setTerm(xsdElementDeclaration_c);


            XSDModelGroup xsdModelGroup_a = XSDFactory.eINSTANCE.createXSDModelGroup();
            xsdModelGroup_a.getContents().add(xsdParticle_a);
            xsdModelGroup_a.getContents().add(xsdParticle_b);// referecened
            xsdModelGroup_a.getContents().add(xsdParticle_c);
            XSDParticle xsdParticle_child = XSDFactory.eINSTANCE.createXSDParticle();
            xsdParticle_child.setTerm(xsdModelGroup_a);
            XSDComplexTypeDefinition xsdComplexTypeDefinition = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
            xsdComplexTypeDefinition.setContent(xsdParticle_child);

            XSDElementDeclaration xsdElementDeclaration = xsdElementDeclaration_b;
            xsdElementDeclaration.setAnnotation(xsdAnnotation);
            xsdElementDeclaration.setTypeDefinition(xsdComplexTypeDefinition);

            // run and check
            Util.getforeignKeyOfElement(list, xsdElementDeclaration);
            assertTrue(list.size() == 4);
            assertTrue(list.contains("StoreA")); //$NON-NLS-1$
            assertTrue(list.contains("StoreC")); //$NON-NLS-1$
            assertTrue(list.contains("StoreE")); //$NON-NLS-1$
            assertTrue(list.contains("StoreF")); //$NON-NLS-1$
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testGetTypeDefinition() {
        String s_name = "simpleType"; //$NON-NLS-1$
        String c_name = "complexType"; //$NON-NLS-1$
        String e_name = "xsdElementDeclaration"; //$NON-NLS-1$

        XSDSchema xsdSchema = XSDFactory.eINSTANCE.createXSDSchema();
        XSDSimpleTypeDefinition xsdSimpleTypeDefinition1 = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
        XSDSimpleTypeDefinition xsdSimpleTypeDefinition2 = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
        XSDComplexTypeDefinition xsdComplexTypeDefinition1 = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
        XSDComplexTypeDefinition xsdComplexTypeDefinition2 = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
        xsdSimpleTypeDefinition1.setName(s_name + 1);
        xsdSimpleTypeDefinition2.setName(s_name + 2);
        xsdComplexTypeDefinition1.setName(c_name + 1);
        xsdComplexTypeDefinition2.setName(c_name + 2);
        xsdSimpleTypeDefinition1
        .setBaseTypeDefinition(xsdSchema.resolveSimpleTypeDefinition(xsdSchema.getSchemaForSchemaNamespace(), "string")); //$NON-NLS-1$ anyType
        xsdSimpleTypeDefinition2
        .setBaseTypeDefinition(xsdSchema.resolveSimpleTypeDefinition(xsdSchema.getSchemaForSchemaNamespace(), "string")); //$NON-NLS-1$
        xsdComplexTypeDefinition1.setBaseTypeDefinition(
                xsdSchema.resolveComplexTypeDefinition(xsdSchema.getSchemaForSchemaNamespace(), "anyType")); //$NON-NLS-1$
        xsdComplexTypeDefinition2.setBaseTypeDefinition(
                xsdSchema.resolveComplexTypeDefinition(xsdSchema.getSchemaForSchemaNamespace(), "anyType")); //$NON-NLS-1$
        EList<XSDSchemaContent> contents = xsdSchema.getContents();

        contents.add(xsdSimpleTypeDefinition1);
        contents.add(xsdSimpleTypeDefinition2);
        contents.add(xsdComplexTypeDefinition1);
        contents.add(xsdComplexTypeDefinition2);

        XSDElementDeclaration xsdElementDeclaration = XSDFactory.eINSTANCE.createXSDElementDeclaration();
        xsdElementDeclaration.setName(e_name);
        contents.add(xsdElementDeclaration);
        Map<String, XSDTypeDefinition> typeDefinition = Util.getTypeDefinition(xsdSchema);
        assertNotNull(typeDefinition);
        assertTrue(typeDefinition.size() == 4);
        assertTrue(typeDefinition.keySet().contains(s_name + 1));
        assertTrue(typeDefinition.keySet().contains(s_name + 2));
        assertTrue(typeDefinition.keySet().contains(c_name + 1));
        assertTrue(typeDefinition.keySet().contains(c_name + 2));
        assertTrue(typeDefinition.values().contains(xsdSimpleTypeDefinition1));
        assertTrue(typeDefinition.values().contains(xsdSimpleTypeDefinition2));
        assertTrue(typeDefinition.values().contains(xsdComplexTypeDefinition1));
        assertTrue(typeDefinition.values().contains(xsdComplexTypeDefinition2));
    }

    @Test
    public void testGetTopParent() {
        //
        List<Object> topParent = Util.getTopParent(null);
        assertNull(topParent);

        XSDFactory factory = XSDFactory.eINSTANCE;
        //
        XSDElementDeclaration concept = factory.createXSDElementDeclaration();
        topParent = Util.getTopParent(concept);
        assertNull(topParent);

        // concept with three children
        String element1 = "Id"; //$NON-NLS-1$
        String element2 = "code"; //$NON-NLS-1$
        String element3 = "address"; //$NON-NLS-1$
        XSDSchema xsdSchema = factory.createXSDSchema();
        xsdSchema.getContents().add(concept);
        XSDComplexTypeDefinition xsdComplexTypeDef = factory.createXSDComplexTypeDefinition();
        xsdComplexTypeDef.setBaseTypeDefinition(
                xsdSchema.resolveComplexTypeDefinition(xsdSchema.getSchemaForSchemaNamespace(), "anyType")); //$NON-NLS-1$
        concept.setAnonymousTypeDefinition(xsdComplexTypeDef);
        XSDParticle xsdParticle = factory.createXSDParticle();
        xsdComplexTypeDef.setContent(xsdParticle);
        XSDModelGroup xsdModelGroup = factory.createXSDModelGroup();
        xsdParticle.setContent(xsdModelGroup);

        XSDParticle childParticle1 = factory.createXSDParticle();
        XSDElementDeclaration childElement1 = factory.createXSDElementDeclaration();
        childElement1.setName(element1);
        childParticle1.setContent(childElement1);
        XSDParticle childParticle2 = factory.createXSDParticle();
        XSDElementDeclaration childElement2 = factory.createXSDElementDeclaration();
        childElement2.setName(element2);
        childParticle2.setContent(childElement2);
        XSDParticle childParticle3 = factory.createXSDParticle();
        XSDElementDeclaration childElement3 = factory.createXSDElementDeclaration();
        childElement3.setName(element3);
        childParticle3.setContent(childElement3);
        xsdModelGroup.getContents().add(childParticle1);
        xsdModelGroup.getContents().add(childParticle2);
        xsdModelGroup.getContents().add(childParticle3);

        XSDIdentityConstraintDefinition xsdIdConsDef = factory.createXSDIdentityConstraintDefinition();
        concept.getIdentityConstraintDefinitions().add(xsdIdConsDef);
        XSDXPathDefinition xsdXPathDefinition1 = factory.createXSDXPathDefinition();
        xsdXPathDefinition1.setValue(element1);
        XSDXPathDefinition xsdXPathDefinition2 = factory.createXSDXPathDefinition();
        xsdXPathDefinition2.setValue(element2);
        xsdIdConsDef.getFields().add(xsdXPathDefinition1);
        xsdIdConsDef.getFields().add(xsdXPathDefinition2);

        // complex type with one child
        XSDComplexTypeDefinition xsdComplexTypeDef2 = factory.createXSDComplexTypeDefinition();
        xsdComplexTypeDef2.setBaseTypeDefinition(
                xsdSchema.resolveComplexTypeDefinition(xsdSchema.getSchemaForSchemaNamespace(), "anyType")); //$NON-NLS-1$
        XSDParticle xsdParticle2 = factory.createXSDParticle();
        xsdComplexTypeDef2.setContent(xsdParticle2);
        XSDModelGroup xsdModelGroup2 = factory.createXSDModelGroup();
        xsdParticle2.setContent(xsdModelGroup2);

        XSDParticle childParticle_2 = factory.createXSDParticle();
        XSDElementDeclaration childElement_2 = factory.createXSDElementDeclaration();
        childElement_2.setName(element1);
        childParticle_2.setContent(childElement_2);
        xsdModelGroup2.getContents().add(childParticle_2);
        xsdSchema.getContents().add(xsdComplexTypeDef2);

        //
        topParent = Util.getTopParent(childElement1);
        assertNotNull(topParent);
        assertTrue(topParent.size() == 2);

        //
        topParent = Util.getTopParent(childElement_2);
        assertNull(topParent);

    }

    @Test
    public void testCheckConcept() {
        XSDElementDeclaration concept = XSDFactory.eINSTANCE.createXSDElementDeclaration();
        XSDIdentityConstraintDefinition xsdIdConsDef1 = XSDFactory.eINSTANCE.createXSDIdentityConstraintDefinition();
        XSDIdentityConstraintDefinition xsdIdConsDef2 = XSDFactory.eINSTANCE.createXSDIdentityConstraintDefinition();
        concept.getIdentityConstraintDefinitions().add(xsdIdConsDef1);
        concept.getIdentityConstraintDefinitions().add(xsdIdConsDef2);
        boolean isConcept = Util.checkConcept(concept);
        assertFalse(isConcept);

        xsdIdConsDef1.setIdentityConstraintCategory(XSDIdentityConstraintCategory.UNIQUE_LITERAL);
        xsdIdConsDef2.setIdentityConstraintCategory(XSDIdentityConstraintCategory.KEY_LITERAL);
        isConcept = Util.checkConcept(concept);
        assertTrue(isConcept);
    }



    @Test
    public void testUpdateForeignKeyRelatedInfo() {
        String attr_key = "source";//$NON-NLS-1$
        String namespaceURI = "http://www.w3.org/XML/1998/namespace"; //$NON-NLS-1$

        String[] appFKRelated = { "X_ForeignKey_Filter", "X_ForeignKey_Info", "X_ForeignKey" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        //
        Element[] allForeignKeyAndInfos = new Element[appFKRelated.length];
        String[] oldValue = { "ProductFamily/Id$$Contains$$aaa$$#ProductFamily/Name$$Contains$$bbb$$#", "ProductFamily/Name", //$NON-NLS-1$ //$NON-NLS-2$
        "ProductFamily/Id" }; //$NON-NLS-1$
        String newValue = "ProductFamily/Names1"; //$NON-NLS-1$

        try {
            Document doc = getEmptyDocument();
            if (doc == null) {
                return;
            }

            for (int i = 0; i < appFKRelated.length; i++) {
                Element ele = doc.createElementNS(namespaceURI, "node" + i); //$NON-NLS-1$
                ele.setAttribute(attr_key, appFKRelated[i]);
                ele.appendChild(doc.createTextNode(oldValue[i]));
                allForeignKeyAndInfos[i] = ele;
            }

            Util.updateForeignKeyRelatedInfo("ProductFamily/Name", newValue, allForeignKeyAndInfos); //$NON-NLS-1$
            assertEquals("ProductFamily/Id$$Contains$$aaa$$#ProductFamily/Names1$$Contains$$bbb$$#", //$NON-NLS-1$
                    allForeignKeyAndInfos[0].getChildNodes().item(0).getTextContent());
            assertEquals(newValue, allForeignKeyAndInfos[1].getChildNodes().item(0).getTextContent());


        } catch (ParserConfigurationException e) {
            log.error(e.getMessage(), e);
        }

    }


    @Test
    public void testRetrieveXSDComponentPath() {
        String conceptName = "Product"; //$NON-NLS-1$
        String complextypeName = "ctype"; //$NON-NLS-1$
        String simpletypeName = "stype"; //$NON-NLS-1$
        String identityName = "identityName"; //$NON-NLS-1$
        String xsdxpathValue = "Id"; //$NON-NLS-1$
        String childelementName = "childelementName"; //$NON-NLS-1$

        List<String> xsdComponentPath = null;

        XSDFactory factory = XSDFactory.eINSTANCE;
        XSDSchema xschema = factory.createXSDSchema();
        XSDElementDeclaration concept = factory.createXSDElementDeclaration();
        concept.setName(conceptName);
        xschema.getContents().add(concept);

        XSDComplexTypeDefinition complexTypeDefinition = factory.createXSDComplexTypeDefinition();
        complexTypeDefinition
        .setBaseTypeDefinition(xschema.resolveComplexTypeDefinition(xschema.getSchemaForSchemaNamespace(), "anyType")); //$NON-NLS-1$ );
        xschema.getContents().add(complexTypeDefinition);

        XSDSimpleTypeDefinition simpleTypeDefinition = factory.createXSDSimpleTypeDefinition();
        simpleTypeDefinition.setName(simpletypeName);

        XSDModelGroup modelGroup = factory.createXSDModelGroup();
        modelGroup.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
        XSDParticle typeparticle = factory.createXSDParticle();
        complexTypeDefinition.setContent(typeparticle);
        typeparticle.setContent(modelGroup);

        XSDParticle childParticle = factory.createXSDParticle();
        XSDElementDeclaration childelement = factory.createXSDElementDeclaration();
        childelement.setName(childelementName);
        childParticle.setContent(childelement);
        childParticle.setTerm(childelement);
        modelGroup.getContents().add(childParticle);

        XSDIdentityConstraintDefinition IdConsDef = factory.createXSDIdentityConstraintDefinition();
        IdConsDef.setName(identityName );
        concept.getIdentityConstraintDefinitions().add(IdConsDef);

        XSDXPathDefinition xsdXPathDefinition = factory.createXSDXPathDefinition();
        xsdXPathDefinition.setValue(xsdxpathValue);
        IdConsDef.getFields().add(xsdXPathDefinition);

        XSDAnnotation conceptAnnotation = factory.createXSDAnnotation();
        concept.setAnnotation(conceptAnnotation);

        XSDElementDeclaration anotherConcept = factory.createXSDElementDeclaration();
        String concept2name = "anotherConcept"; //$NON-NLS-1$
        anotherConcept.setName(concept2name);
        xschema.getContents().add(anotherConcept);

        //
        List<String> buffer = new ArrayList<String>();
        xsdComponentPath = Util.retrieveXSDComponentPath(concept, xschema, buffer);
        assertEquals(1, xsdComponentPath.size());
        assertEquals("//xsd:element[@name='" + conceptName + "']", xsdComponentPath.get(0)); //$NON-NLS-1$ //$NON-NLS-2$

        //
        buffer.clear();
        xsdComponentPath = Util.retrieveXSDComponentPath(complexTypeDefinition, xschema, buffer);
        assertEquals(1, xsdComponentPath.size());
        assertEquals("//xsd:complexType", xsdComponentPath.get(0)); //$NON-NLS-1$

        //
        buffer.clear();
        complexTypeDefinition.setName(complextypeName);
        xsdComponentPath = Util.retrieveXSDComponentPath(complexTypeDefinition, xschema, buffer);
        assertEquals(1, xsdComponentPath.size());
        assertEquals("//xsd:complexType[@name='" + complextypeName + "']", xsdComponentPath.get(0)); //$NON-NLS-1$ //$NON-NLS-2$

        //
        buffer.clear();
        xsdComponentPath = Util.retrieveXSDComponentPath(simpleTypeDefinition, xschema, buffer);
        assertEquals(1, xsdComponentPath.size());
        assertEquals("//xsd:simpleType[@name='" + simpletypeName + "']", xsdComponentPath.get(0)); //$NON-NLS-1$ //$NON-NLS-2$

        //
        buffer.clear();
        xsdComponentPath = Util.retrieveXSDComponentPath(modelGroup, xschema, buffer);
        assertEquals(2, xsdComponentPath.size());
        assertTrue(xsdComponentPath.contains("//xsd:" + XSDCompositor.SEQUENCE_LITERAL.getLiteral())); //$NON-NLS-1$
        assertTrue(xsdComponentPath.contains("//xsd:complexType[@name='" + complextypeName + "']")); //$NON-NLS-1$ //$NON-NLS-2$

        //
        buffer.clear();
        xsdComponentPath = Util.retrieveXSDComponentPath(IdConsDef, xschema, buffer);
        assertEquals(2, xsdComponentPath.size());
        assertTrue(xsdComponentPath.contains("//xsd:element[@name='" + conceptName + "']")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(xsdComponentPath.contains("//xsd:unique[@name='" + identityName + "']")); //$NON-NLS-1$ //$NON-NLS-2$

        //
        buffer.clear();
        xsdComponentPath = Util.retrieveXSDComponentPath(xsdXPathDefinition, xschema, buffer);
        assertEquals(3, xsdComponentPath.size());
        assertTrue(xsdComponentPath.contains("//xsd:element[@name='" + conceptName + "']")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(xsdComponentPath.contains("//xsd:unique[@name='" + identityName + "']")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(xsdComponentPath.contains("//xsd:field[@xpath='" + xsdxpathValue + "']")); //$NON-NLS-1$ //$NON-NLS-2$

        //
        buffer.clear();
        xsdComponentPath = Util.retrieveXSDComponentPath(conceptAnnotation, xschema, buffer);
        assertEquals(2, xsdComponentPath.size());
        assertTrue(xsdComponentPath.contains("//xsd:element[@name='" + conceptName + "']")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(xsdComponentPath.contains("//xsd:annotation")); //$NON-NLS-1$

        //
        buffer.clear();
        xsdComponentPath = Util.retrieveXSDComponentPath(childParticle, xschema, buffer);
        assertEquals(2, xsdComponentPath.size());
        assertTrue(xsdComponentPath.contains("//xsd:" + XSDCompositor.SEQUENCE_LITERAL.getLiteral())); //$NON-NLS-1$
        assertTrue(xsdComponentPath.contains("//xsd:complexType[@name='" + complextypeName + "']")); //$NON-NLS-1$ //$NON-NLS-2$

        //
        buffer.clear();
        xsdComponentPath = Util.retrieveXSDComponentPath(childelement, xschema, buffer);
        assertEquals(3, xsdComponentPath.size());
        assertTrue(xsdComponentPath.contains("//xsd:" + XSDCompositor.SEQUENCE_LITERAL.getLiteral())); //$NON-NLS-1$
        assertTrue(xsdComponentPath.contains("//xsd:complexType[@name='" + complextypeName + "']")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(xsdComponentPath.contains("//xsd:element[@name='" + childelementName + "']")); //$NON-NLS-1$ //$NON-NLS-2$

        //
        buffer.clear();
        childelement.setResolvedElementDeclaration(anotherConcept);
        xsdComponentPath = Util.retrieveXSDComponentPath(childParticle, xschema, buffer);
        assertEquals(3, xsdComponentPath.size());
        assertTrue(xsdComponentPath.contains("//xsd:" + XSDCompositor.SEQUENCE_LITERAL.getLiteral())); //$NON-NLS-1$
        assertTrue(xsdComponentPath.contains("//xsd:complexType[@name='" + complextypeName + "']")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(xsdComponentPath.contains("//xsd:element[@name='" + concept2name + "' or @ref='" + concept2name + "']")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        //
        buffer.clear();
        String targetNamespace = "targetnamespace"; //$NON-NLS-1$
        String key = "prefix"; //$NON-NLS-1$
        anotherConcept.setTargetNamespace(targetNamespace);
        xschema.getQNamePrefixToNamespaceMap().put(key, targetNamespace);
        xsdComponentPath = Util.retrieveXSDComponentPath(childParticle, xschema, buffer);
        assertEquals(3, xsdComponentPath.size());
        assertTrue(xsdComponentPath.contains("//xsd:" + XSDCompositor.SEQUENCE_LITERAL.getLiteral())); //$NON-NLS-1$
        assertTrue(xsdComponentPath.contains("//xsd:complexType[@name='" + complextypeName + "']")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(xsdComponentPath
                .contains("//xsd:element[@name='" + concept2name + "' or @ref='" + key + ":" + concept2name + "']")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    }


    @Test
    public void testFilterOutDuplicatedElems() {
        String simpletypeName = "simpletype"; //$NON-NLS-1$
        String complextypeName = "complextype"; //$NON-NLS-1$
        String concepName = "concept"; //$NON-NLS-1$
        String idconstraintName = "identityconstraint"; //$NON-NLS-1$
        String modelgroupName = "modelgroup"; //$NON-NLS-1$

        XSDFactory factory = XSDFactory.eINSTANCE;

        XSDSimpleTypeDefinition simpleTypeDefinition = factory.createXSDSimpleTypeDefinition();
        XSDComplexTypeDefinition complexTypeDefinition = factory.createXSDComplexTypeDefinition();
        XSDElementDeclaration elementDeclaration = factory.createXSDElementDeclaration();
        XSDIdentityConstraintDefinition identityConstraintDefinition = factory.createXSDIdentityConstraintDefinition();
        XSDModelGroupDefinition modelGroupDefinition = factory.createXSDModelGroupDefinition();

        simpleTypeDefinition.setName(simpletypeName);
        complexTypeDefinition.setName(complextypeName);
        elementDeclaration.setName(concepName);
        identityConstraintDefinition.setName(idconstraintName);
        modelGroupDefinition.setName(modelgroupName);

        XSDNamedComponent[] checkedElements = { simpleTypeDefinition, complexTypeDefinition, elementDeclaration,
                identityConstraintDefinition, modelGroupDefinition };
        Object[] duplicatedElems = Util.filterOutDuplicatedElems(checkedElements);
        List<XSDNamedComponent> allElements = Arrays.asList(checkedElements);
        assertNotNull(duplicatedElems);
        assertTrue(checkedElements.length == duplicatedElems.length);
        for (int i = 0; i < duplicatedElems.length; i++) {
            assertTrue(allElements.contains(duplicatedElems[i]));
        }

        //
        XSDSimpleTypeDefinition simpleTypeDefinition2 = factory.createXSDSimpleTypeDefinition();
        XSDComplexTypeDefinition complexTypeDefinition2 = factory.createXSDComplexTypeDefinition();
        XSDElementDeclaration elementDeclaration2 = factory.createXSDElementDeclaration();
        XSDIdentityConstraintDefinition identityConstraintDefinition2 = factory.createXSDIdentityConstraintDefinition();
        XSDModelGroupDefinition modelGroupDefinition2 = factory.createXSDModelGroupDefinition();

        simpleTypeDefinition2.setName(simpletypeName);
        complexTypeDefinition2.setName(complextypeName);
        elementDeclaration2.setName(concepName);
        identityConstraintDefinition2.setName(idconstraintName);
        modelGroupDefinition2.setName(modelgroupName);

        XSDNamedComponent[] checkedElements2 = { simpleTypeDefinition, complexTypeDefinition, elementDeclaration,
                identityConstraintDefinition, modelGroupDefinition, simpleTypeDefinition2, complexTypeDefinition2,
                elementDeclaration2, identityConstraintDefinition2, modelGroupDefinition2 };

        duplicatedElems = Util.filterOutDuplicatedElems(checkedElements2);
        assertNotNull(duplicatedElems);
        assertTrue(checkedElements.length == duplicatedElems.length);
        for (int i = 0; i < duplicatedElems.length; i++) {
            assertTrue(allElements.contains(duplicatedElems[i]));
        }
    }

    @Test
    public void testIsSimpleTypedParticle() {
        boolean isSimpleTypedParticle = Util.isSimpleTypedParticle(null);
        assertFalse(isSimpleTypedParticle);

        XSDFactory factory = XSDFactory.eINSTANCE;
        XSDParticle particle = factory.createXSDParticle();

        isSimpleTypedParticle = Util.isSimpleTypedParticle(particle);
        assertFalse(isSimpleTypedParticle);

        XSDElementDeclaration elementDeclaration = factory.createXSDElementDeclaration();
        elementDeclaration.setTypeDefinition(factory.createXSDSimpleTypeDefinition());
        particle.setContent(elementDeclaration);

        isSimpleTypedParticle = Util.isSimpleTypedParticle(particle);
        assertTrue(isSimpleTypedParticle);

        XSDModelGroup modelGroup = factory.createXSDModelGroup();
        particle.setContent(modelGroup);
        isSimpleTypedParticle = Util.isSimpleTypedParticle(particle);
        assertFalse(isSimpleTypedParticle);
    }

    @Test
    public void testFindReference() {
        XSDFactory factory = XSDFactory.eINSTANCE;
        String conceptName1 = "Product", conceptName2 = "Store"; //$NON-NLS-1$ //$NON-NLS-2$
        String refName = conceptName2;
        XSDSchema xschema = factory.createXSDSchema();

        XSDElementDeclaration referencedEntity = Util.findReference(null, null);
        assertNull(referencedEntity);

        referencedEntity = Util.findReference(refName, null);
        assertNull(referencedEntity);

        referencedEntity = Util.findReference(null, xschema);
        assertNull(referencedEntity);

        referencedEntity = Util.findReference(refName, xschema);
        assertNull(referencedEntity);

        XSDElementDeclaration concept1 = factory.createXSDElementDeclaration();
        concept1.setName(conceptName1);
        XSDElementDeclaration concept2 = factory.createXSDElementDeclaration();
        concept2.setName(conceptName2);
        xschema.getContents().add(concept1);
        xschema.getContents().add(concept2);
        referencedEntity = Util.findReference(refName, xschema);
        assertNotNull(referencedEntity);
        assertSame(concept2, referencedEntity);

        refName = refName + " : " + "fair"; //$NON-NLS-1$//$NON-NLS-2$
        referencedEntity = Util.findReference(refName, xschema);
        assertNotNull(referencedEntity);
        assertSame(concept2, referencedEntity);

        refName = "ProductFamily"; //$NON-NLS-1$
        referencedEntity = Util.findReference(refName, xschema);
        assertNull(referencedEntity);
    }

    @Test
    public void testUnZipFile() {
        String usrDir = System.getProperty("java.io.tmpdir");//$NON-NLS-1$

        long currentTimeMillis = System.currentTimeMillis();
        File zipFolder = new File(usrDir + File.separator + currentTimeMillis);
        if (!zipFolder.exists()) {
            zipFolder.mkdirs();
        }
        File unzipFolder = new File(usrDir + File.separator + currentTimeMillis + 1);
        if (!unzipFolder.exists()) {
            unzipFolder.mkdirs();
        }

        String file = new File(zipFolder, "testzp.zip").getAbsolutePath(); //$NON-NLS-1$
        createZipFile(file);

        try {
            Util.unZipFile(file, unzipFolder.getAbsolutePath(), 8, new NullProgressMonitor());
            String[] unzippedFiles = unzipFolder.list();
            assertNotNull(unzippedFiles);
            assertTrue(unzippedFiles.length == 1);

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                ZipToFile.deleteDirectory(zipFolder);
                ZipToFile.deleteDirectory(unzipFolder);
            } catch (Exception e) {
            }
        }
    }

    private void createZipFile(String zipfile) {
        StringBuilder sb = new StringBuilder();
        sb.append("Test String"); //$NON-NLS-1$

        ZipOutputStream out = null;
        try {
            File f = new File(zipfile);
            out = new ZipOutputStream(new FileOutputStream(f));
            ZipEntry e = new ZipEntry("mytext.txt"); //$NON-NLS-1$
            out.putNextEntry(e);

            byte[] data = sb.toString().getBytes();
            out.write(data, 0, data.length);
        } catch (Exception e) {//
            log.error(e.getMessage(), e);
        } finally {
            if (out != null) {
                try {
                    out.closeEntry();
                    out.close();
                } catch (IOException e) {//
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Test
    public void testGetContextPath() {
        String urlPath = "/talendmdm/services/soap"; //$NON-NLS-1$
        String expectedContextPath = "/talendmdm"; //$NON-NLS-1$

        String contextPath = Util.getContextPath(null);
        assertTrue(contextPath.isEmpty());

        contextPath = Util.getContextPath(urlPath);
        assertEquals(expectedContextPath, contextPath);
    }

    @Test
    public void testGetComponentName() {
        String prefix = "name=\"", suffix = "\""; //$NON-NLS-1$ //$NON-NLS-2$
        String[] objNames = { "product_elementdeclaration", "product_particle", "p_complextype", "p_simpletype", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                "product_identityconstraintdef", "p_xpathdef" }; //$NON-NLS-1$ //$NON-NLS-2$
        String[] expectedObjNames = new String[objNames.length];
        for (int i = 0; i < objNames.length - 1; i++) {
            expectedObjNames[i] = prefix + objNames[i] + suffix;
        }
        expectedObjNames[objNames.length - 1] = "xpath=\"" + objNames[objNames.length - 1] + suffix; //$NON-NLS-1$

        XSDFactory factory = XSDFactory.eINSTANCE;

        try {
            XSDElementDeclaration xsdElementDeclaration = factory.createXSDElementDeclaration();
            xsdElementDeclaration.setName(objNames[0]);

            XSDParticle xsdParticle = factory.createXSDParticle();
            XSDElementDeclaration xsdParticleDeclaration = factory.createXSDElementDeclaration();
            xsdParticleDeclaration.setName(objNames[1]);
            xsdParticle.setTerm(xsdParticleDeclaration);

            XSDComplexTypeDefinition xsdComplexTypeDefinition = factory.createXSDComplexTypeDefinition();
            xsdComplexTypeDefinition.setName(objNames[2]);

            XSDSimpleTypeDefinition xsdSimpleTypeDefinition = factory.createXSDSimpleTypeDefinition();
            xsdSimpleTypeDefinition.setName(objNames[3]);

            XSDIdentityConstraintDefinition xsdIdConsDef = factory.createXSDIdentityConstraintDefinition();
            xsdIdConsDef.setName(objNames[4]);

            XSDXPathDefinition xsdPathDefinition = factory.createXSDXPathDefinition();
            xsdPathDefinition.setValue(objNames[5]);

            String name = Util.getComponentName(xsdElementDeclaration);
            assertEquals(expectedObjNames[0], name);

            name = Util.getComponentName(xsdParticle);
            assertEquals(expectedObjNames[1], name);

            name = Util.getComponentName(xsdComplexTypeDefinition);
            assertEquals(expectedObjNames[2], name);

            name = Util.getComponentName(xsdSimpleTypeDefinition);
            assertEquals(expectedObjNames[3], name);

            name = Util.getComponentName(xsdIdConsDef);
            assertEquals(expectedObjNames[4], name);

            name = Util.getComponentName(xsdPathDefinition);
            assertEquals(expectedObjNames[5], name);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testGetTopElement() {
        try {
            XSDFactory factory = XSDFactory.eINSTANCE;
            //
            XSDElementDeclaration concept = factory.createXSDElementDeclaration();
            // concept with three children
            String element1 = "Id"; //$NON-NLS-1$
            String element2 = "code"; //$NON-NLS-1$
            String element3 = "address"; //$NON-NLS-1$
            XSDSchema xsdSchema = factory.createXSDSchema();
            xsdSchema.getContents().add(concept);
            XSDComplexTypeDefinition xsdComplexTypeDef = factory.createXSDComplexTypeDefinition();
            xsdComplexTypeDef.setBaseTypeDefinition(
                    xsdSchema.resolveComplexTypeDefinition(xsdSchema.getSchemaForSchemaNamespace(), "anyType")); //$NON-NLS-1$
            xsdSchema.getContents().add(xsdComplexTypeDef);
            XSDParticle xsdParticle = factory.createXSDParticle();
            xsdComplexTypeDef.setContent(xsdParticle);
            XSDModelGroup xsdModelGroup = factory.createXSDModelGroup();
            xsdParticle.setContent(xsdModelGroup);
            concept.setAnonymousTypeDefinition(xsdComplexTypeDef);

            XSDParticle childParticle1 = factory.createXSDParticle();
            XSDElementDeclaration childElement1 = factory.createXSDElementDeclaration();
            childElement1.setName(element1);
            childParticle1.setContent(childElement1);
            XSDParticle childParticle2 = factory.createXSDParticle();
            XSDElementDeclaration childElement2 = factory.createXSDElementDeclaration();
            childElement2.setName(element2);
            childParticle2.setContent(childElement2);
            XSDParticle childParticle3 = factory.createXSDParticle();
            XSDElementDeclaration childElement3 = factory.createXSDElementDeclaration();
            childElement3.setName(element3);
            childParticle3.setContent(childElement3);
            xsdModelGroup.getContents().add(childParticle1);
            xsdModelGroup.getContents().add(childParticle2);
            xsdModelGroup.getContents().add(childParticle3);

            Object primaryKey = Util.getTopElement(concept, childElement1);
            assertEquals(element1, primaryKey);

            XSDElementDeclaration elementDecl = factory.createXSDElementDeclaration();
            elementDecl.setTypeDefinition(factory.createXSDSimpleTypeDefinition());
            primaryKey = Util.getTopElement(elementDecl, childElement1);
            assertNull(primaryKey);

            //

            XSDComplexTypeDefinition xsdComplexTypeDef2 = factory.createXSDComplexTypeDefinition();
            xsdComplexTypeDef2.setBaseTypeDefinition(xsdComplexTypeDef);
            XSDParticle xsdParticle2 = factory.createXSDParticle();
            xsdComplexTypeDef2.setContent(xsdParticle2);
            XSDModelGroup xsdModelGroup2 = factory.createXSDModelGroup();
            xsdParticle2.setContent(xsdModelGroup2);

            XSDParticle childParticle_2 = factory.createXSDParticle();
            XSDElementDeclaration childElement_2 = factory.createXSDElementDeclaration();
            childElement_2.setName(element1);
            childParticle_2.setContent(childElement_2);
            xsdModelGroup2.getContents().add(childParticle_2);
            xsdSchema.getContents().add(xsdComplexTypeDef2);
            concept.setTypeDefinition(xsdComplexTypeDef2);
            primaryKey = Util.getTopElement(concept, childElement1);
            assertEquals(element1, primaryKey);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testToReadable() {
        for (WSWhereOperator wsOperator : WSWhereOperator.values()) {
            if (wsOperator == WSWhereOperator.NO_OPERATOR) {
                continue;
            }
            assertTrue(!Util.toReadable(wsOperator).isEmpty());
        }
    }
}
