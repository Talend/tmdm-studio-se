// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.junit.Test;
import org.talend.commons.utils.StringUtils;

import com.amalto.workbench.dialogs.datamodel.MatchRuleSelectionFilter;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class XSDUtilTest {

    final String X_FOREIGN_KEY = "X_ForeignKey"; //$NON-NLS-1$

    @Test
    public void testGetAnnotationValue() throws Exception {
        String fileName = "Product_0.1.xsd"; //$NON-NLS-1$
        String elementName = "Family"; //$NON-NLS-1$
        String fk = "Store/Id"; //$NON-NLS-1$
        String xsdString = TestUtil.readTestResource(XSDUtilTest.this.getClass(), fileName);
        assertNotNull(xsdString);
        XSDSchema xsdSchema = Util.getXSDSchema(xsdString);
        for (XSDElementDeclaration element : xsdSchema.getElementDeclarations()) {
            XSDTypeDefinition typeDefinition = element.getTypeDefinition();

            if (typeDefinition instanceof XSDComplexTypeDefinition) {
                XSDComplexTypeContent xsdComplexTypeContent = ((XSDComplexTypeDefinition) typeDefinition).getContent();
                if (xsdComplexTypeContent instanceof XSDParticle) {
                    XSDParticleContent content = ((XSDParticle) xsdComplexTypeContent).getContent();
                    if (content instanceof XSDModelGroup) {
                        for (XSDParticle particle : ((XSDModelGroup) content).getParticles()) {
                            XSDTerm term = particle.getTerm();
                            if (term instanceof XSDElementDeclaration) {
                                XSDElementDeclaration elementDeclaration = (XSDElementDeclaration) term;
                                if (elementDeclaration.getName().equals(elementName)) {
                                    String value = XSDUtil.getAnnotationValue(elementDeclaration, X_FOREIGN_KEY);
                                    assertEquals(fk, value);
                                    return;
                                }
                            }
                        }
                    }

                }
            }
        }
        fail();
    }

    @Test
    public void testGetEntityName() {
        XSDSchema mockSchema = mock(XSDSchema.class);
        List<String> declNames = new ArrayList<String>();
        declNames.add("Product"); //$NON-NLS-1$
        declNames.add("Family"); //$NON-NLS-1$
        declNames.add("Store"); //$NON-NLS-1$

        EList<XSDElementDeclaration> decls = new BasicEList<XSDElementDeclaration>();
        for (String name : declNames) {
            XSDElementDeclaration mockDecl = mock(XSDElementDeclaration.class);
            when(mockDecl.getName()).thenReturn(name);
            decls.add(mockDecl);
        }
        when(mockSchema.getElementDeclarations()).thenReturn(decls);

        List<String> entityNames = XSDUtil.getEntityName(mockSchema);
        assertNotNull(entityNames);
        assertEquals(decls.size(), entityNames.size());
        assertTrue(entityNames.containsAll(declNames));
    }

    @Test
    public void testIsValidatedXSDName() {
        int resultMaxLen = 10;
        String xsdname = null;
        boolean isValid = XSDUtil.isValidatedXSDName(xsdname);
        assertFalse(isValid);

        xsdname = "";// empty //$NON-NLS-1$
        isValid = XSDUtil.isValidatedXSDName(xsdname);
        assertFalse(isValid);

        xsdname = " ";// bland //$NON-NLS-1$
        isValid = XSDUtil.isValidatedXSDName(xsdname);
        assertFalse(isValid);

        String str = new String("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"); //$NON-NLS-1$
        xsdname = buildArbitraryString(str, resultMaxLen);
        isValid = XSDUtil.isValidatedXSDName(xsdname);
        assertTrue(isValid);

        String others1 = "-."; //$NON-NLS-1$
        String others2 = "_012346789"; //$NON-NLS-1$
        String allValidChars = str + others2;
        int randomIndex1 = getRandomIndex(str);
        int randomIndex2 = getRandomIndex(allValidChars);
        String xsdname2 = str.substring(randomIndex1, randomIndex1 + 1)
                + buildArbitraryString(str + others1 + others2, resultMaxLen)
                + allValidChars.substring(randomIndex2, randomIndex2 + 1);
        isValid = XSDUtil.isValidatedXSDName(xsdname2);
        assertTrue(isValid);

        xsdname2 = buildArbitraryString(others1 + others2, resultMaxLen) + xsdname2;
        isValid = XSDUtil.isValidatedXSDName(xsdname2);
        assertFalse(isValid);

        String[] invalidChars = { "~", "`", "!", "#", "$", "%", "^", "&", "*", "(", ")", "+", "=", "[", "]", "{", "}", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$ //$NON-NLS-17$
                "\\", ";", "'", "\"", "<", ">", ",", "?", " ", "   " }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
        String joinedStr = StringUtils.join(invalidChars, ""); //$NON-NLS-1$
        String xsdname3 = xsdname + buildArbitraryString(joinedStr, resultMaxLen) + xsdname;
        isValid = XSDUtil.isValidatedXSDName(xsdname3);
        assertFalse(isValid);

        xsdname2 = buildArbitraryString(joinedStr, resultMaxLen) + xsdname + buildArbitraryString(joinedStr, resultMaxLen);
        isValid = XSDUtil.isValidatedXSDName(xsdname2);
        assertFalse(isValid);
    }

    /**
     * random length, random content
     */
    private String buildArbitraryString(String candidateChars, int resultMaxLen) {
        StringBuilder builder = new StringBuilder();

        if (candidateChars != null) {
            if (resultMaxLen < 1) {
                resultMaxLen = 10;// set default
            }

            char[] charArray = candidateChars.toCharArray();
            int randomIntValue = new Double(Math.floor(Math.random() * 100)).intValue();// a int number 0~100
            int size = randomIntValue % resultMaxLen + 1; // random length, no less that one
            for (int i = 0; i < size; i++) {
                int index = getRandomIndex(candidateChars);
                builder.append(charArray[index]);
            }
        }

        return builder.toString();
    }

    private int getRandomIndex(String candidateChars) {
        int wholeLen = candidateChars.length();
        int index = new Double(Math.floor(Math.random() * wholeLen)).intValue();
        return index;
    }

    @Test
    public void testGetValidElementPaths() throws Exception {
        String fileName = "Product_0.1.xsd"; //$NON-NLS-1$
        String elementName = "Product"; //$NON-NLS-1$
        String xsdString = TestUtil.readTestResource(XSDUtilTest.this.getClass(), fileName);

        assertNotNull(xsdString);
        XSDSchema xsdSchema = Util.createXsdSchema(xsdString, null);

        XSDElementDeclaration decl = null;
        for (XSDElementDeclaration element : xsdSchema.getElementDeclarations()) {
            if (element.getName().equals(elementName)) {
                decl = element;
                break;
            }
        }

        Set<String> expected = new HashSet<String>();
        String[] ee = { "Product/Availability", "Product/Family", "Product/Description", "Product/OnlineStore", "Product/Name", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                "Product/Picture", "Product/Id", "Product/Price" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        expected.addAll(Arrays.asList(ee));

        if (decl != null) {
            Set<String> validElementPaths = XSDUtil.getValidElementPaths(decl, new MatchRuleSelectionFilter());
            assertNotNull(validElementPaths);
            assertEquals(expected.size(), validElementPaths.size());
            assertTrue(expected.containsAll(validElementPaths));
            assertTrue(validElementPaths.containsAll(expected));
        }
    }

    @Test
    public void testIsEntity() throws Exception {
        String fileName = "Product_0.1.xsd"; //$NON-NLS-1$
        String elementName = "Product"; //$NON-NLS-1$
        String xsdString = TestUtil.readTestResource(XSDUtilTest.this.getClass(), fileName);
        assertNotNull(xsdString);
        XSDSchema xsdSchema = Util.getXSDSchema(xsdString);

        XSDElementDeclaration decl = null;
        for (XSDElementDeclaration element : xsdSchema.getElementDeclarations()) {
            if (element.getName().equals(elementName)) {
                decl = element;
            }
            assertTrue(XSDUtil.isEntity(element));
        }

        if (decl != null) {
            XSDComplexTypeDefinition ctypeDefinition = (XSDComplexTypeDefinition) decl.getTypeDefinition();
            XSDComplexTypeContent content = ctypeDefinition.getContent();
            if (content instanceof XSDParticle) {
                XSDParticle xsdParticle = (XSDParticle) content;
                XSDParticleContent particleContent = xsdParticle.getContent();
                if (particleContent instanceof XSDModelGroup) {
                    XSDModelGroup modelGroup = (XSDModelGroup) particleContent;
                    EList<XSDParticle> contents = modelGroup.getContents();
                    for (XSDParticle particle : contents) {
                        if (particle.getContent() instanceof XSDElementDeclaration) {
                            assertFalse(XSDUtil.isEntity(particle.getContent()));
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testIsFirstLevelChild() throws Exception {
        String fileName = "Product_0.1.xsd"; //$NON-NLS-1$
        String elementName = "Product"; //$NON-NLS-1$
        String xsdString = TestUtil.readTestResource(XSDUtilTest.this.getClass(), fileName);
        assertNotNull(xsdString);
        XSDSchema xsdSchema = Util.getXSDSchema(xsdString);

        XSDElementDeclaration decl = null;
        for (XSDElementDeclaration element : xsdSchema.getElementDeclarations()) {
            if (element.getName().equals(elementName)) {
                decl = element;
            }
        }

        if (decl != null) {
            XSDComplexTypeDefinition ctypeDefinition = (XSDComplexTypeDefinition) decl.getTypeDefinition();
            XSDComplexTypeContent content = ctypeDefinition.getContent();
            if (content instanceof XSDParticle) {
                XSDParticle xsdParticle = (XSDParticle) content;
                XSDParticleContent particleContent = xsdParticle.getContent();
                if (particleContent instanceof XSDModelGroup) {
                    XSDModelGroup modelGroup = (XSDModelGroup) particleContent;
                    EList<XSDParticle> contents = modelGroup.getContents();
                    for (XSDParticle particle : contents) {
                        assertTrue(XSDUtil.isFirstLevelChild(particle));
                        if (particle.getTerm() instanceof XSDElementDeclaration) {
                            XSDElementDeclaration xsdElementDecl = (XSDElementDeclaration) particle.getTerm();
                            XSDTypeDefinition typeDefinition = xsdElementDecl.getTypeDefinition();
                            if (typeDefinition instanceof XSDComplexTypeDefinition) {
                                XSDParticle childPart = (XSDParticle) ((XSDComplexTypeDefinition) typeDefinition).getContent();
                                XSDModelGroup childModelGroup = (XSDModelGroup) childPart.getContent();
                                for (XSDParticle childParticle : childModelGroup.getContents()) {
                                    assertFalse(XSDUtil.isFirstLevelChild(childParticle));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testBuildEntityUsedComplexTypeMap() throws Exception {
        String fileName = "Product_0.1.xsd"; //$NON-NLS-1$
        String xsdString = TestUtil.readTestResource(XSDUtilTest.this.getClass(), fileName);
        assertNotNull(xsdString);
        XSDSchema xsdSchema = Util.getXSDSchema(xsdString);

        Map<XSDElementDeclaration, List<XSDComplexTypeDefinition>> entityMapComplexType = XSDUtil
                .buildEntityUsedComplexTypeMap(xsdSchema);
        assertNotNull(entityMapComplexType);
        assertTrue(entityMapComplexType.size() == 3);
        for (List<XSDComplexTypeDefinition> ctypes : entityMapComplexType.values()) {
            assertTrue(ctypes.size() == 1);
        }
    }

    @Test
    public void testIsSimpleTypeElement() {
        XSDFactory factory = XSDFactory.eINSTANCE;
        XSDParticle particle = factory.createXSDParticle();
        XSDElementDeclaration xsdElementDeclaration = factory.createXSDElementDeclaration();
        XSDSimpleTypeDefinition xsdSimpleTypeDefinition = factory.createXSDSimpleTypeDefinition();
        xsdElementDeclaration.setTypeDefinition(xsdSimpleTypeDefinition);
        particle.setContent(xsdElementDeclaration);
        boolean isSimpleTypeElement = XSDUtil.isSimpleTypeElement(particle);
        assertTrue(isSimpleTypeElement);

        xsdElementDeclaration.setTypeDefinition(factory.createXSDComplexTypeDefinition());
        isSimpleTypeElement = XSDUtil.isSimpleTypeElement(particle);
        assertFalse(isSimpleTypeElement);
    }

    @Test
    public void testIsPrimaryKeyElement() throws Exception {
        String fileName = "Product_0.1.xsd"; //$NON-NLS-1$
        String elementName = "Product"; //$NON-NLS-1$
        String xsdString = TestUtil.readTestResource(XSDUtilTest.this.getClass(), fileName);
        assertNotNull(xsdString);
        XSDSchema xsdSchema = Util.getXSDSchema(xsdString);

        XSDElementDeclaration decl = null;
        for (XSDElementDeclaration element : xsdSchema.getElementDeclarations()) {
            if (element.getName().equals(elementName)) {
                decl = element;
            }
        }

        if (decl != null) {
            XSDComplexTypeDefinition ctypeDefinition = (XSDComplexTypeDefinition) decl.getTypeDefinition();
            XSDComplexTypeContent ctypeContent = ctypeDefinition.getContent();
            if (ctypeContent instanceof XSDParticle) {
                XSDParticle typeParticle = (XSDParticle) ctypeContent;
                XSDParticleContent particleContent = typeParticle.getContent();
                if (particleContent instanceof XSDModelGroup) {
                    XSDModelGroup particleGroup = (XSDModelGroup) particleContent;
                    for (XSDParticle particle : particleGroup.getContents()) {
                        if (particle.getContent() instanceof XSDElementDeclaration
                                && ((XSDElementDeclaration) particle.getContent()).getName().equals("Id")) { //$NON-NLS-1$
                            assertTrue(XSDUtil.isPrimaryKeyElement(particle));
                        } else {
                            assertFalse(XSDUtil.isPrimaryKeyElement(particle));
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testGetKeyFields() throws Exception {
        XSDFactory factory = XSDFactory.eINSTANCE;
        String conceptName = "Product"; //$NON-NLS-1$
        String[] fields = { "Id", "code" }; //$NON-NLS-1$ //$NON-NLS-2$
        XSDElementDeclaration concept = factory.createXSDElementDeclaration();
        concept.setName(conceptName);
        XSDIdentityConstraintDefinition ideCDef = factory.createXSDIdentityConstraintDefinition();
        XSDXPathDefinition xpathDefinition1 = factory.createXSDXPathDefinition();
        xpathDefinition1.setValue(fields[0]);
        ideCDef.getFields().add(xpathDefinition1);
        concept.getIdentityConstraintDefinitions().add(ideCDef);

        List<String> keyFields = XSDUtil.getKeyFields(concept);
        assertTrue(keyFields.isEmpty());

        //
        ideCDef.setName(conceptName);
        XSDXPathDefinition xpathDefinition2 = factory.createXSDXPathDefinition();
        xpathDefinition2.setValue(fields[1]);
        ideCDef.getFields().add(xpathDefinition2);

        keyFields = XSDUtil.getKeyFields(concept);
        assertTrue(keyFields.size() == 2);
        assertTrue(keyFields.contains(fields[0]));
        assertTrue(keyFields.contains(fields[1]));
    }

    @Test
    public void testIsValidatedXSDDate() {
        assertTrue(XSDUtil.isValidatedXSDDate("2011-01-02")); //$NON-NLS-1$
        assertFalse(XSDUtil.isValidatedXSDDate("2011-01-2")); //$NON-NLS-1$

        assertTrue(XSDUtil.isValidatedXSDDate("2011-01-02Z")); //$NON-NLS-1$
        assertFalse(XSDUtil.isValidatedXSDDate("2011-01-2A")); //$NON-NLS-1$
        assertFalse(XSDUtil.isValidatedXSDDate("2011-01-2BC")); //$NON-NLS-1$

        assertTrue(XSDUtil.isValidatedXSDDate("2011-01-02+10:01")); //$NON-NLS-1$
        assertTrue(XSDUtil.isValidatedXSDDate("2011-01-02+14:00")); //$NON-NLS-1$
        assertFalse(XSDUtil.isValidatedXSDDate("2011-01-02+15:01")); //$NON-NLS-1$
    }

    @Test
    public void testGetAllPKXpaths() throws Exception {
        String fileName = "Product_0.1.xsd"; //$NON-NLS-1$
        String[] xpaths = { "Store", "ProductFamily", "Product", "Product/Id", "ProductFamily/Id", "Store/Id" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        List<String> resultXpaths = Arrays.asList(xpaths);
        String xsdString = TestUtil.readTestResource(XSDUtilTest.this.getClass(), fileName);
        XSDSchema xsdSchema = Util.getXSDSchema(xsdString);
        List<String> allPKXpaths = XSDUtil.getAllPKXpaths(xsdSchema);
        assertTrue(allPKXpaths.size() == 6);
        assertTrue(allPKXpaths.containsAll(resultXpaths));
    }

    @Test
    public void testGetContainerTypeOfField() throws Exception {
        String fileName = "TestCategory03.xsd"; //$NON-NLS-1$
        String xsdString = TestUtil.readTestResource(XSDUtilTest.this.getClass(), fileName);
        XSDSchema xsdSchema = Util.getXSDSchema(xsdString);
        EList<XSDElementDeclaration> elementDeclarations = xsdSchema.getElementDeclarations();
        for (XSDElementDeclaration concept : elementDeclarations) {
            XSDComplexTypeDefinition type = (XSDComplexTypeDefinition) concept.getTypeDefinition();
            if (type.getContent() instanceof XSDParticle) {
                XSDParticle particle = (XSDParticle) type.getContent();
                if (particle.getTerm() instanceof XSDModelGroup) {
                    XSDModelGroup group = (XSDModelGroup) particle.getTerm();
                    EList<XSDParticle> elist = group.getContents();
                    assertSame(type, XSDUtil.getContainerTypeOfField(elist.get(0)));// assert
                }
            }
        }
    }

    @Test
    public void testIsAnonymousType() throws Exception {
        String fileName = "TestCategory03.xsd"; //$NON-NLS-1$
        String xsdString = TestUtil.readTestResource(XSDUtilTest.this.getClass(), fileName);
        XSDSchema xsdSchema = Util.getXSDSchema(xsdString);

        EList<XSDElementDeclaration> elementDeclarations = xsdSchema.getElementDeclarations();
        for (XSDElementDeclaration concept : elementDeclarations) {
            XSDComplexTypeDefinition ctypeDef = (XSDComplexTypeDefinition) concept.getTypeDefinition();
            if (ctypeDef.getName() == null) {
                assertTrue(XSDUtil.isAnonymousType(ctypeDef));
            } else {
                assertFalse(XSDUtil.isAnonymousType(ctypeDef));
            }
        }
    }

    @Test
    public void testHasBoundToConcept() throws Exception {
        String fileName = "TestCategory03.xsd"; //$NON-NLS-1$
        String xsdString = TestUtil.readTestResource(XSDUtilTest.this.getClass(), fileName);
        XSDSchema xsdSchema = Util.getXSDSchema(xsdString);

        XSDComplexTypeDefinition typeO = null, typeA = null, typeB = null;
        EList<XSDTypeDefinition> typeDefinitions = xsdSchema.getTypeDefinitions();
        for (XSDTypeDefinition type : typeDefinitions) {
            if (type instanceof XSDComplexTypeDefinition) {
                if (type.getName().equals("CType")) {
                    typeO = (XSDComplexTypeDefinition) type;
                } else if (type.getName().equals("CTypeA")) {
                    typeA = (XSDComplexTypeDefinition) type;
                } else if (type.getName().equals("CTypeB")) {
                    typeB = (XSDComplexTypeDefinition) type;
                }
            }
        }

        assertTrue(typeO != null && typeA != null && typeB != null);

        XSDElementDeclaration conceptA = null, conceptB = null, conceptC = null;
        EList<XSDElementDeclaration> elementDeclarations = xsdSchema.getElementDeclarations();
        for (XSDElementDeclaration concept : elementDeclarations) {
            if (concept.getName().equals("EntityA")) {
                conceptA = concept;
            } else if (concept.getName().equals("EntityB")) {
                conceptB = concept;
            } else if (concept.getName().equals("EntityC")) {
                conceptC = concept;
            }
        }

        assertTrue(XSDUtil.hasBoundToConcept(typeO, conceptA));
        assertTrue(XSDUtil.hasBoundToConcept(typeO, conceptB));
        assertTrue(XSDUtil.hasBoundToConcept(typeA, conceptA));
        assertTrue(XSDUtil.hasBoundToConcept(typeA, conceptB));
        assertFalse(XSDUtil.hasBoundToConcept(typeB, conceptA));
        assertTrue(XSDUtil.hasBoundToConcept(typeB, conceptB));
        assertFalse(XSDUtil.hasBoundToConcept(typeO, conceptC));
        assertFalse(XSDUtil.hasBoundToConcept(typeA, conceptC));
        assertFalse(XSDUtil.hasBoundToConcept(typeB, conceptC));
    }

    @Test
    public void testGetConceptsOfField() throws Exception {
        String fileName = "TestCategory03.xsd"; //$NON-NLS-1$
        String xsdString = TestUtil.readTestResource(XSDUtilTest.this.getClass(), fileName);
        XSDSchema xsdSchema = Util.getXSDSchema(xsdString);

        XSDComplexTypeDefinition typeA = null, typeB = null;
        EList<XSDTypeDefinition> typeDefinitions = xsdSchema.getTypeDefinitions();
        for (XSDTypeDefinition type : typeDefinitions) {
            if (type instanceof XSDComplexTypeDefinition) {
                if (type.getName().equals("CTypeA")) {
                    typeA = (XSDComplexTypeDefinition) type;
                } else if (type.getName().equals("CTypeB")) {
                    typeB = (XSDComplexTypeDefinition) type;
                }
            }
        }

        assertTrue(typeA != null && typeB != null);
        if (typeA.getContent() instanceof XSDParticle) {
            XSDParticle particle = (XSDParticle) typeA.getContent();
            if (particle.getTerm() instanceof XSDModelGroup) {
                XSDModelGroup group = (XSDModelGroup) particle.getTerm();
                EList<XSDParticle> elist = group.getContents();
                for (XSDParticle field : elist) {
                    List<XSDElementDeclaration> concepts = XSDUtil.getConceptsOfField(field);
                    List<String> conceptNames = concepts.stream().map(f -> f.getName()).collect(Collectors.toList());
                    assertTrue(conceptNames.size() == 2 && conceptNames.contains("EntityA") && conceptNames.contains("EntityB"));
                }
            }
        }
        if (typeB.getContent() instanceof XSDParticle) {
            XSDParticle particle = (XSDParticle) typeB.getContent();
            if (particle.getTerm() instanceof XSDModelGroup) {
                XSDModelGroup group = (XSDModelGroup) particle.getTerm();
                EList<XSDParticle> elist = group.getContents();
                for (XSDParticle field : elist) {
                    List<XSDElementDeclaration> concepts = XSDUtil.getConceptsOfField(field);
                    List<String> conceptNames = concepts.stream().map(f -> f.getName()).collect(Collectors.toList());
                    if (conceptNames.size() == 1) {
                        assertTrue(conceptNames.contains("EntityB") && !conceptNames.contains("EntityA"));
                    } else {
                        assertTrue(
                                conceptNames.size() == 2 && conceptNames.contains("EntityB") && conceptNames.contains("EntityA"));
                    }
                }
            }
        }
    }

    @Test
    public void testSyncEntityCategoryAnnotation() throws Exception {
        String fileName = "TestCategory03.xsd"; //$NON-NLS-1$
        String xsdString = TestUtil.readTestResource(XSDUtilTest.this.getClass(), fileName);
        XSDSchema schema = Util.getXSDSchema(xsdString);
        for (XSDElementDeclaration concept : schema.getElementDeclarations()) {
            if (concept.getName().equals("EntityA")) {
                XSDUtil.syncEntityCategoryAnnotation(concept, "name", "newname");
                XSDAnnotationsStructure annoStructure = new XSDAnnotationsStructure(concept);
                Map<String, String> fieldCategoryMap = annoStructure.getFieldCategoryMap();
                assertTrue(!fieldCategoryMap.containsKey("name") && fieldCategoryMap.containsKey("newname"));
            }
            if (concept.getName().equals("EntityC")) {
                XSDUtil.syncEntityCategoryAnnotation(concept, "name", "newname");
                XSDAnnotationsStructure annoStructure = new XSDAnnotationsStructure(concept);
                Map<String, String> fieldCategoryMap = annoStructure.getFieldCategoryMap();
                assertTrue(!fieldCategoryMap.containsKey("name") && fieldCategoryMap.containsKey("newname"));
            }
        }
    }

    @Test
    public void testIsValidatedXSDTime() {
        assertTrue(XSDUtil.isValidatedXSDTime("23:01:59")); //$NON-NLS-1$
        assertFalse(XSDUtil.isValidatedXSDTime("23:01:61")); //$NON-NLS-1$
        assertFalse(XSDUtil.isValidatedXSDTime("24:01:01")); //$NON-NLS-1$
        assertFalse(XSDUtil.isValidatedXSDTime("23:61:01")); //$NON-NLS-1$
        assertTrue(XSDUtil.isValidatedXSDTime("24:00:00")); //$NON-NLS-1$

        assertTrue(XSDUtil.isValidatedXSDTime("23:01:59Z")); //$NON-NLS-1$
        assertFalse(XSDUtil.isValidatedXSDTime("23:01:59A")); //$NON-NLS-1$
        assertFalse(XSDUtil.isValidatedXSDTime("23:01:59BC")); //$NON-NLS-1$

        assertTrue(XSDUtil.isValidatedXSDTime("23:01:59+10:01")); //$NON-NLS-1$
        assertTrue(XSDUtil.isValidatedXSDTime("23:01:59+14:00")); //$NON-NLS-1$
        assertFalse(XSDUtil.isValidatedXSDTime("23:01:59+15:01")); //$NON-NLS-1$
    }

    @Test
    public void testIsValidatedXSDDateTime() {
        assertTrue(XSDUtil.isValidatedXSDDateTime("2011-01-02T23:01:59")); //$NON-NLS-1$
        assertFalse(XSDUtil.isValidatedXSDDateTime("2011-01-02T23:01:61")); //$NON-NLS-1$
        assertFalse(XSDUtil.isValidatedXSDDateTime("2011-01-02T24:01:01")); //$NON-NLS-1$
        assertFalse(XSDUtil.isValidatedXSDDateTime("2011-01-02T23:61:01")); //$NON-NLS-1$
        assertTrue(XSDUtil.isValidatedXSDDateTime("2011-01-02T24:00:00")); //$NON-NLS-1$

        assertTrue(XSDUtil.isValidatedXSDDateTime("2011-01-02T23:01:59Z")); //$NON-NLS-1$
        assertFalse(XSDUtil.isValidatedXSDDateTime("2011-01-02T23:01:59A")); //$NON-NLS-1$
        assertFalse(XSDUtil.isValidatedXSDDateTime("2011-01-02T23:01:59BC")); //$NON-NLS-1$

        assertTrue(XSDUtil.isValidatedXSDDateTime("2011-01-02T23:01:59+10:01")); //$NON-NLS-1$
        assertTrue(XSDUtil.isValidatedXSDDateTime("2011-01-02T23:01:59+14:00")); //$NON-NLS-1$
        assertFalse(XSDUtil.isValidatedXSDDateTime("2011-01-02T23:01:59+15:01")); //$NON-NLS-1$
    }

    @Test
    public void testValidateCategory() {
        // #1 no error
        Map<String, String> result = XSDUtil.validateCategory(XSDUtilTest.class.getResourceAsStream("ValidateCategory01.xsd"));
        assertNull(result);

        // #2
        result = XSDUtil.validateCategory(XSDUtilTest.class.getResourceAsStream("ValidateCategory02.xsd"));
        assertEquals(1, result.size());
        assertTrue(result.containsKey("EntityA"));
        // #3
        result = XSDUtil.validateCategory(XSDUtilTest.class.getResourceAsStream("ValidateCategory03.xsd"));
        assertEquals(1, result.size());
        assertTrue(result.containsKey("EntityA"));
        // #4
        result = XSDUtil.validateCategory(XSDUtilTest.class.getResourceAsStream("ValidateCategory04.xsd"));
        assertEquals(1, result.size());
        assertTrue(result.containsKey("EntityA"));
        // #5
        result = XSDUtil.validateCategory(XSDUtilTest.class.getResourceAsStream("ValidateCategory05.xsd"));
        assertEquals(1, result.size());
        assertTrue(result.containsKey("EntityA"));
    }
}
