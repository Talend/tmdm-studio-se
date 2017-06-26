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
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.junit.Test;
import org.talend.commons.utils.StringUtils;

import com.amalto.workbench.dialogs.datamodel.MatchRuleSelectionFilter;

/**
 * DOC hbhong  class global comment. Detailled comment
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
        xsdname = buildArbitraryString(str, 10);
        isValid = XSDUtil.isValidatedXSDName(xsdname);
        assertTrue(isValid);

        String others = "-._012346789"; //$NON-NLS-1$
        String xsdname2 = xsdname + buildArbitraryString(others, 10);
        isValid = XSDUtil.isValidatedXSDName(xsdname2);
        assertTrue(isValid);

        xsdname2 = others + xsdname;
        isValid = XSDUtil.isValidatedXSDName(xsdname2);
        assertFalse(isValid);

        String[] invalidChars = { "~", "`", "!", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "+", "=", "[", "]", "{", "}", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$ //$NON-NLS-17$ //$NON-NLS-18$ //$NON-NLS-19$
                "|", "\\", ";", "'", "\"", "<", ">", ",", "?", " ", "   " }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$
        String joinedStr = StringUtils.join(invalidChars, ""); //$NON-NLS-1$
        xsdname2 = xsdname + buildArbitraryString(joinedStr, 10);
        isValid = XSDUtil.isValidatedXSDName(xsdname2);
        assertFalse(isValid);

        xsdname2 = buildArbitraryString(str + joinedStr, 10);
        isValid = XSDUtil.isValidatedXSDName(xsdname2);
        assertFalse(isValid);
    }

    /**
     * random length, random content
     */
    private String buildArbitraryString(String candidateChars, int resultMaxLen) {
        StringBuilder builder = new StringBuilder();

        if (candidateChars != null) {
            int wholeLen = candidateChars.length();
            if (resultMaxLen < 1) {
                resultMaxLen = 10;// set default
            }

            char[] charArray = candidateChars.toCharArray();
            int randomIntValue = new Double(Math.floor(Math.random() * 100)).intValue();// a int number 0~100
            int size = randomIntValue % resultMaxLen; // random length
            for (int i = 0; i < size; i++) {
                int index = new Double(Math.floor(Math.random() * wholeLen)).intValue();
                builder.append(charArray[index]);
            }
        }

        return builder.toString();
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
}
