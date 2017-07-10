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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axis.utils.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
            PowerMockito.when(Util.class, method, any(URL.class)).thenCallRealMethod(); // $NON-NLS-1$
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
        fail();
    }

    @Test
    public void testGetForeignKeyofParcle() {
        fail();
    }

    @Test
    public void testGetforeignKeyOfElement() {
        fail();
    }

    @Test
    public void testIsReferrenced() {// private
        fail();
    }

    @Test
    public void testGetTypeDefinition() {
        fail();
    }

    @Test
    public void testGetParent() {
        fail();
    }

    @Test
    public void testGetTopParent() {
        fail();
    }

    @Test
    public void testGetTopElement() {// private
        fail();
    }

    @Test
    public void testFindOutSpecialSonElement() {// private
        fail();
    }

    @Test
    public void testFindOutAllSonElements() {// private
        fail();
    }

    @Test
    public void testCheckConcept() {
        fail();
    }

    @Test
    public void testUpdateReferenceToXSDTypeDefinition() {
        fail();
    }

    @Test
    public void testUpdatePrimaryKeyInfo() {// private
        fail();
    }

    @Test
    public void testUpdateReference() {
        fail();
    }

    @Test
    public void testUpdateForeignKeyRelatedInfo() {
        fail();
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
