// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.utils.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.workbench.webservices.WSRoutingRuleExpression;
import com.amalto.workbench.webservices.WSRoutingRuleOperator;
import com.amalto.workbench.webservices.WSStringPredicate;
import com.amalto.workbench.webservices.WSWhereCondition;
import com.amalto.workbench.webservices.WSWhereOperator;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Util.class })
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
    public void testCheckAndAddSuffix() {
        String suffix = "?wsdl"; //$NON-NLS-1$
        PowerMockito.mockStatic(Util.class);
        try {
            String method = "checkAndAddSuffix"; //$NON-NLS-1$
            PowerMockito.when(Util.class, method, Mockito.any(URL.class)).thenCallRealMethod(); // $NON-NLS-1$
            // http
            String url_str = "http://localhost:8180/talendmdm/services/soap"; //$NON-NLS-1$
            URL url_in = new URL(url_str);
            URL url_result = Whitebox.invokeMethod(Util.class, method, url_in);
            assertEquals(url_str + suffix, url_result.toString());

            url_str = "http://localhost:8180/talendmdm/services/soap?wsdl"; //$NON-NLS-1$
            url_in = new URL(url_str);
            url_result = Whitebox.invokeMethod(Util.class, method, url_in);
            assertEquals(url_str, url_result.toString());

            // https
            url_str = "https://localhost:8543/talendmdm/services/soap"; //$NON-NLS-1$
            url_in = new URL(url_str);
            url_result = Whitebox.invokeMethod(Util.class, method, url_in);
            assertEquals(url_str + suffix, url_result.toString());

            url_str = "https://localhost:8543/talendmdm/services/soap?wsdl"; //$NON-NLS-1$
            url_in = new URL(url_str);
            url_result = Whitebox.invokeMethod(Util.class, method, url_in);
            assertEquals(url_str, url_result.toString());

            // ftp
            url_str = "ftp://localhost:8543/talendmdm/services/soap"; //$NON-NLS-1$
            url_in = new URL(url_str);
            url_result = Whitebox.invokeMethod(Util.class, method, url_in);
            assertEquals(url_str, url_result.toString());

            url_str = "ftp://localhost:8543/talendmdm/services/soap?wsdl"; //$NON-NLS-1$
            url_in = new URL(url_str);
            url_result = Whitebox.invokeMethod(Util.class, method, url_in);
            assertEquals(url_str, url_result.toString());

            // ftps
            url_str = "ftps://localhost:8543/talendmdm/services/soap"; //$NON-NLS-1$
            url_in = new URL(url_str);
            url_result = Whitebox.invokeMethod(Util.class, method, url_in);
            assertEquals(url_str, url_result.toString());

            url_str = "ftps://localhost:8543/talendmdm/services/soap?wsdl"; //$NON-NLS-1$
            url_in = new URL(url_str);
            url_result = Whitebox.invokeMethod(Util.class, method, url_in);
            assertEquals(url_str, url_result.toString());

            // file
            url_str = "file://d:/dir/Product.proc"; //$NON-NLS-1$
            url_in = new URL(url_str);
            url_result = Whitebox.invokeMethod(Util.class, method, url_in);
            assertEquals(url_str, url_result.toString());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
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
    public void testIsReferrenced() {
        PowerMockito.mockStatic(Util.class);
        try {
            String method_isReferenced = "isReferrenced"; //$NON-NLS-1$
            PowerMockito.when(Util.class, method_isReferenced, Mockito.any(XSDElementDeclaration.class),
                    Mockito.any(XSDElementDeclaration.class))
                    .thenCallRealMethod();
            XSDElementDeclaration xsdElementDeclaration1 = XSDFactory.eINSTANCE.createXSDElementDeclaration();
            XSDElementDeclaration xsdElementDeclaration2 = XSDFactory.eINSTANCE.createXSDElementDeclaration();

            //
            boolean isReferenced = Whitebox.invokeMethod(Util.class, method_isReferenced, xsdElementDeclaration1,
                    xsdElementDeclaration1);
            assertTrue(isReferenced);

            //
            isReferenced = Whitebox.invokeMethod(Util.class, method_isReferenced, xsdElementDeclaration1, xsdElementDeclaration2);
            assertFalse(isReferenced);

            //
            XSDParticle xsdParticle = buildXSDParticleWithChildren(3, new String[] { "a", "b", "c" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            XSDComplexTypeDefinition xsdComplexTypeDefinition = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
            xsdComplexTypeDefinition.setContent(xsdParticle);
            xsdElementDeclaration2.setTypeDefinition(xsdComplexTypeDefinition);
            isReferenced = Whitebox.invokeMethod(Util.class, method_isReferenced, xsdElementDeclaration1, xsdElementDeclaration2);
            assertFalse(isReferenced);

            //
            XSDParticle xsdParticle1 = XSDFactory.eINSTANCE.createXSDParticle();
            xsdParticle1.setTerm(xsdElementDeclaration1);
            XSDModelGroup modelGroup = ((XSDModelGroup) xsdParticle.getTerm());
            modelGroup.getContents().add(xsdParticle1);
            isReferenced = Whitebox.invokeMethod(Util.class, method_isReferenced, xsdElementDeclaration1, xsdElementDeclaration2);
            assertTrue(isReferenced);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private XSDParticle buildXSDParticleWithChildren(int childrenNumb, String[] childrenNames) {
        XSDParticle xsdParticle = XSDFactory.eINSTANCE.createXSDParticle();

        XSDModelGroup xsdModelGroup = XSDFactory.eINSTANCE.createXSDModelGroup();
        xsdParticle.setTerm(xsdModelGroup);

        for (int i = 0; i < childrenNumb && i < childrenNames.length; i++) {
            XSDParticle xsdParticle_child = XSDFactory.eINSTANCE.createXSDParticle();
            XSDElementDeclaration xsdElementDeclaration_child = XSDFactory.eINSTANCE.createXSDElementDeclaration();
            xsdElementDeclaration_child.setName(childrenNames[i]);
            xsdParticle_child.setTerm(xsdElementDeclaration_child);
            xsdModelGroup.getContents().add(xsdParticle_child);
        }

        return xsdParticle;
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
    public void testGetParent() {
        Object parent = Util.getParent(null);
        assertNull(parent);

        XSDElementDeclaration concept = XSDFactory.eINSTANCE.createXSDElementDeclaration();
        XSDParticle xsdParticle = XSDFactory.eINSTANCE.createXSDParticle();
        parent = Util.getParent(concept);
        assertNull(parent);
        parent = Util.getParent(xsdParticle);
        assertNull(parent);

        XSDSchema xsdSchema = XSDFactory.eINSTANCE.createXSDSchema();
        xsdSchema.getContents().add(concept);
        parent = Util.getParent(concept);
        assertSame(concept, parent);// concept's parent is itself

        //
        PowerMockito.mockStatic(Util.class);
        String method_private = "findOutSpecialSonElement"; //$NON-NLS-1$
        try {
            PowerMockito.when(Util.getParent(Mockito.any(Object.class))).thenCallRealMethod();

            XSDComplexTypeDefinition xsdComplexTypeDef = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
            xsdComplexTypeDef.setBaseTypeDefinition(
                    xsdSchema.resolveComplexTypeDefinition(xsdSchema.getSchemaForSchemaNamespace(), "anyType")); //$NON-NLS-1$
            XSDParticle particle = XSDFactory.eINSTANCE.createXSDParticle();
            xsdComplexTypeDef.setContent(particle);
            concept.setAnonymousTypeDefinition(xsdComplexTypeDef);

            XSDModelGroup modelGroup = XSDFactory.eINSTANCE.createXSDModelGroup();
            particle.setContent(modelGroup);

            XSDParticle particle1 = XSDFactory.eINSTANCE.createXSDParticle();
            XSDElementDeclaration elementDeclaration1 = XSDFactory.eINSTANCE.createXSDElementDeclaration();
            particle1.setContent(elementDeclaration1);

            XSDParticle particle2 = XSDFactory.eINSTANCE.createXSDParticle();
            XSDElementDeclaration elementDeclaration2 = XSDFactory.eINSTANCE.createXSDElementDeclaration();
            particle2.setContent(elementDeclaration2);
            // add children to elementDeclaration2
            XSDComplexTypeDefinition complexType2 = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
            complexType2.setBaseTypeDefinition(
                    xsdSchema.resolveComplexTypeDefinition(xsdSchema.getSchemaForSchemaNamespace(), "anyType")); //$NON-NLS-1$
            XSDModelGroup modelGroup_type = XSDFactory.eINSTANCE.createXSDModelGroup();
            XSDParticle particle_child = XSDFactory.eINSTANCE.createXSDParticle();
            XSDElementDeclaration elementDeclaration_child = XSDFactory.eINSTANCE.createXSDElementDeclaration();
            particle_child.setContent(elementDeclaration_child);
            modelGroup_type.getContents().add(particle_child);
            XSDParticle particle_type = XSDFactory.eINSTANCE.createXSDParticle();
            particle_type.setContent(modelGroup_type);
            complexType2.setContent(particle_type);
            elementDeclaration2.setAnonymousTypeDefinition(complexType2);

            modelGroup.getContents().add(particle1);
            modelGroup.getContents().add(particle2);

            parent = Util.getParent(elementDeclaration1);
            assertSame(concept, parent);
            parent = Util.getParent(elementDeclaration2);
            assertSame(concept, parent);

            XSDElementDeclaration son = XSDFactory.eINSTANCE.createXSDElementDeclaration();
            parent = Util.getParent(son);
            assertNull(parent);

            //
            xsdSchema.getContents().clear();
            xsdSchema.getContents().add(xsdComplexTypeDef);
            parent = Util.getParent(elementDeclaration1);
            assertSame(xsdComplexTypeDef, parent);
            parent = Util.getParent(elementDeclaration2);
            assertSame(xsdComplexTypeDef, parent);

            //
            XSDElementDeclaration element = XSDFactory.eINSTANCE.createXSDElementDeclaration();

            PowerMockito.when(Util.class, method_private, Mockito.any(XSDElementDeclaration.class),
                    Mockito.any(XSDElementDeclaration.class), Mockito.anySet()).thenReturn(element);
            parent = Util.getParent(elementDeclaration_child);
            assertSame(element, parent);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
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
    public void testGetTopElement() {// private
        String methodToExpect = "getTopElement"; //$NON-NLS-1$

        PowerMockito.mockStatic(Util.class);
        try {
            PowerMockito.when(Util.class, methodToExpect, Mockito.any()).thenCallRealMethod();

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
            concept.setAnonymousTypeDefinition(xsdComplexTypeDef);
            xsdSchema.getContents().add(xsdComplexTypeDef);
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

            Object primaryKey = Whitebox.invokeMethod(Util.class, methodToExpect, concept, childElement1);
            assertEquals(element1, primaryKey);

            XSDElementDeclaration elementDecl = factory.createXSDElementDeclaration();
            elementDecl.setTypeDefinition(factory.createXSDSimpleTypeDefinition());
            primaryKey = Whitebox.invokeMethod(Util.class, methodToExpect, elementDecl, childElement1);
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
            concept.setAnonymousTypeDefinition(xsdComplexTypeDef2);
            primaryKey = Whitebox.invokeMethod(Util.class, methodToExpect, concept, childElement1);
            assertEquals(element1, primaryKey);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testFindOutSpecialSonElement() {
        String method_findspecial = "findOutSpecialSonElement"; //$NON-NLS-1$
        String method_findall = "findOutAllSonElements"; //$NON-NLS-1$

        Set<XSDConcreteComponent> complexTypes = new HashSet<XSDConcreteComponent>();
        String[] names = { "ele1", "ele2", "ele3", "ele4" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        PowerMockito.mockStatic(Util.class);
        try {
            PowerMockito.when(Util.class, method_findspecial, any(XSDElementDeclaration.class),
                    any(XSDElementDeclaration.class), anySet()).thenCallRealMethod();

            XSDFactory factory = XSDFactory.eINSTANCE;
            XSDElementDeclaration parent = factory.createXSDElementDeclaration();
            XSDElementDeclaration xsdElementDeclaration1 = factory.createXSDElementDeclaration();
            xsdElementDeclaration1.setName(names[0]);
            XSDElementDeclaration xsdElementDeclaration2 = factory.createXSDElementDeclaration();
            xsdElementDeclaration2.setName(names[1]);

            List<XSDElementDeclaration> particleElementList1 = new ArrayList<XSDElementDeclaration>();
            particleElementList1.add(xsdElementDeclaration1);
            particleElementList1.add(xsdElementDeclaration2);
            //

            PowerMockito.when(Util.class, method_findall, any(XSDElementDeclaration.class), any(XSDComplexTypeDefinition.class))
                    .thenReturn(particleElementList1);
            Object result = Whitebox.invokeMethod(Util.class, method_findspecial, parent, xsdElementDeclaration1, complexTypes);
            assertSame(parent, result);

            //
            XSDElementDeclaration xsdEleDeclaration1 = factory.createXSDElementDeclaration();
            xsdEleDeclaration1.setName(names[2]);
            XSDElementDeclaration xsdEleDeclaration2 = factory.createXSDElementDeclaration();
            xsdEleDeclaration2.setName(names[3]);
            List<XSDElementDeclaration> particleElementList2 = new ArrayList<XSDElementDeclaration>();
            particleElementList2.add(xsdEleDeclaration1);
            particleElementList2.add(xsdEleDeclaration2);
            PowerMockito.when(Util.class, method_findall, any(XSDElementDeclaration.class), any(XSDComplexTypeDefinition.class))
                    .thenReturn(particleElementList1, particleElementList2);
            result = Whitebox.invokeMethod(Util.class, method_findspecial, parent, xsdEleDeclaration1, complexTypes);
            assertSame(xsdElementDeclaration1, result);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testFindOutAllSonElements() {
        XSDFactory factory = XSDFactory.eINSTANCE;
        Set<XSDConcreteComponent> complexTypes = new HashSet<XSDConcreteComponent>();

        String[] targetNameSpaces = { "http://www.w3.org/2001/XMLSchema", "", "" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String[] names = { "p1", "p2", "p3" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        XSDTypeDefinition[] types = { factory.createXSDSimpleTypeDefinition(), factory.createXSDSimpleTypeDefinition(),
                factory.createXSDSimpleTypeDefinition() };

        XSDElementDeclaration xsdElementDeclaration = factory.createXSDElementDeclaration();
        xsdElementDeclaration.setName(names[0]);
        xsdElementDeclaration.setTargetNamespace(targetNameSpaces[0]);
        XSDComplexTypeDefinition xsdComplexTypeDef = factory.createXSDComplexTypeDefinition();
        xsdElementDeclaration.setAnonymousTypeDefinition(xsdComplexTypeDef);
        XSDParticle typeParticle = factory.createXSDParticle();
        xsdComplexTypeDef.setContent(typeParticle);
        

        XSDModelGroup xsdModelGroup = factory.createXSDModelGroup();
        for (int i = 0; i < names.length; i++) {
            XSDElementDeclaration xsdEleDec = factory.createXSDElementDeclaration();
            xsdEleDec.setName(names[i]);
            xsdEleDec.setTargetNamespace(targetNameSpaces[i]);
            if (i == 0) {
                xsdEleDec.setTypeDefinition(xsdComplexTypeDef);
            } else {
                xsdEleDec.setTypeDefinition(types[i]);
            }
            XSDParticle xsdParticle = factory.createXSDParticle();
            xsdParticle.setContent(xsdEleDec);
            xsdModelGroup.getContents().add(xsdParticle);
        }
        typeParticle.setContent(xsdModelGroup);

        PowerMockito.mockStatic(Util.class);
        try {
            String methodToExecute = "findOutAllSonElements"; //$NON-NLS-1$
            PowerMockito.when(Util.class, methodToExecute, any(XSDElementDeclaration.class), anySet()).thenCallRealMethod();
            List<XSDElementDeclaration> allson = Whitebox.invokeMethod(Util.class, methodToExecute, xsdElementDeclaration,
                    complexTypes);
            assertNotNull(allson);
            assertTrue(allson.size() == 2);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

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
    public void testUpdateReferenceToXSDTypeDefinition() {
        String method_getall = "getAllObject"; //$NON-NLS-1$
        String method_updateref = "updateReferenceToXSDTypeDefinition"; //$NON-NLS-1$
        PowerMockito.mockStatic(Util.class);
        try {
            PowerMockito.when(Util.class, method_updateref, any(), any(XSDTypeDefinition.class),
                    any(IStructuredContentProvider.class)).thenCallRealMethod();

            Object[] allNodes = {};
            XSDFactory factory = XSDFactory.eINSTANCE;

            XSDTypeDefinition[] newTypes = { factory.createXSDComplexTypeDefinition(), factory.createXSDSimpleTypeDefinition() };
            for (XSDTypeDefinition newType : newTypes) {
                //
                XSDElementDeclaration mockElementDecl = mock(XSDElementDeclaration.class);//
                when(mockElementDecl.getTypeDefinition()).thenReturn(newType);

                //
                XSDParticle xsdParticle1 = mock(XSDParticle.class);//
                XSDElementDeclaration mockElementDec2 = mock(XSDElementDeclaration.class);
                when(mockElementDec2.getTypeDefinition()).thenReturn(newType);
                when(xsdParticle1.getTerm()).thenReturn(mockElementDec2);

                //
                XSDParticle xsdParticle2 = mock(XSDParticle.class);//

                XSDParticle particle_c1 = mock(XSDParticle.class);
                XSDElementDeclaration mockElementDec_c1 = mock(XSDElementDeclaration.class);
                when(mockElementDec_c1.getTypeDefinition()).thenReturn(newType);
                when(particle_c1.getContent()).thenReturn(mockElementDec_c1);

                XSDParticle particle_c2 = mock(XSDParticle.class);
                XSDElementDeclaration mockElementDec_c2 = mock(XSDElementDeclaration.class);
                when(mockElementDec_c2.getTypeDefinition()).thenReturn(newType);
                when(particle_c2.getContent()).thenReturn(mockElementDec_c2);

                XSDParticle particle_c3 = mock(XSDParticle.class);
                XSDElementDeclaration mockElementDec_c3 = mock(XSDElementDeclaration.class);
                when(mockElementDec_c3.getTypeDefinition()).thenReturn(factory.createXSDComplexTypeDefinition());
                when(particle_c3.getContent()).thenReturn(mockElementDec_c3);

                XSDModelGroup xsdModelGroup = mock(XSDModelGroup.class);
                EList<XSDParticle> elist = new BasicEList<XSDParticle>();
                elist.add(particle_c1);
                elist.add(particle_c2);
                elist.add(particle_c3);
                when(xsdModelGroup.getContents()).thenReturn(elist);
                when(xsdParticle2.getTerm()).thenReturn(xsdModelGroup);

                allNodes = new Object[] { mockElementDecl, xsdParticle1, xsdParticle2 };

                //
                PowerMockito.when(Util.class, method_getall, any(), anyList(), any(IStructuredContentProvider.class))
                        .thenReturn(allNodes);
                Util.updateReferenceToXSDTypeDefinition(new Object(), newType, mock(IStructuredContentProvider.class));

                Mockito.verify(mockElementDecl).setTypeDefinition(newType);
                Mockito.verify(mockElementDec2).setTypeDefinition(newType);
                Mockito.verify(mockElementDec_c1).setTypeDefinition(newType);
                Mockito.verify(mockElementDec_c2).setTypeDefinition(newType);
                if (newType instanceof XSDComplexTypeDefinition) {
                    PowerMockito.verifyStatic();
                    Util.updateChildrenReferenceToComplexType((XSDComplexTypeDefinition) newType);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testUpdatePrimaryKeyInfo() {
        String method_private = "updatePrimaryKeyInfo"; //$NON-NLS-1$
        String app_primaryKey = "X_PrimaryKeyInfo"; //$NON-NLS-1$
        XSDFactory factory = XSDFactory.eINSTANCE;

        PowerMockito.mockStatic(Util.class);
        try {
            PowerMockito.when(Util.class, method_private, any(XSDElementDeclaration.class), anyString(), anyString())
                    .thenCallRealMethod();

            XSDElementDeclaration concept = factory.createXSDElementDeclaration();
            XSDAnnotation conceptAnnotation = factory.createXSDAnnotation();
            concept.setAnnotation(conceptAnnotation);
            XSDSchema xsdSchema = factory.createXSDSchema();
            xsdSchema.getContents().add(concept);

            String pk1 = "Product/Id";//$NON-NLS-1$
            String pk2 = "Product/code";//$NON-NLS-1$
            String attr_key = "source";//$NON-NLS-1$
            String namespaceURI = "http://www.w3.org/XML/1998/namespace"; //$NON-NLS-1$

            Document doc = getEmptyDocument();
            xsdSchema.setDocument(doc);

            Element appinfoElement1 = doc.createElementNS(namespaceURI, "appinfo"); //$NON-NLS-1$
            appinfoElement1.setAttribute(attr_key, app_primaryKey);
            appinfoElement1.appendChild(doc.createTextNode(pk1));

            Element appinfoElement2 = doc.createElementNS(namespaceURI, "appinfosssss"); //$NON-NLS-1$
            appinfoElement2.setAttribute(attr_key, app_primaryKey);
            appinfoElement2.appendChild(doc.createTextNode(pk2));

            Element annoElement = doc.createElement("s"); //$NON-NLS-1$
            annoElement.appendChild(appinfoElement1);
            annoElement.appendChild(appinfoElement2);
            conceptAnnotation.setElement(annoElement);
            EList<Element> applicationInformations = conceptAnnotation.getApplicationInformation();
            applicationInformations.add(appinfoElement1);
            applicationInformations.add(appinfoElement2);

            String newValue = "Product_sufix/Id"; //$NON-NLS-1$
            Whitebox.invokeMethod(Util.class, method_private, concept, pk1, newValue);
            assertEquals(newValue, annoElement.getChildNodes().item(1).getTextContent());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testUpdateReference() {
        fail();
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
    public void testGetAllForeignKeyRelatedInfos() {
        fail();
    }

    @Test
    public void testGetComplexChilds() {// private
        fail();
    }

    @Test
    public void testIsAImporedElement() {
        fail();
    }

    @Test
    public void testCreateXsdSchema() {
        fail();
    }

    @Test
    public void testGetComplexTypes() {
        fail();
    }

    @Test
    public void testGetSimpleTypeDefinitionChildren() {
        fail();
    }

    @Test
    public void testFilterOutDuplicatedElems() {
        fail();
    }

    @Test
    public void testIsSimpleTypedParticle() {
        fail();
    }

    @Test
    public void testFindReference() {
        fail();
    }

    @Test
    public void testGetContextPath() {
        fail();
    }
}
