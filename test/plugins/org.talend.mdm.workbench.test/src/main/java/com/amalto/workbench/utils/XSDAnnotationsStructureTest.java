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
package com.amalto.workbench.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.junit.Before;
import org.junit.Test;


/**
 * DOC Administrator  class global comment. Detailled comment
 */
@SuppressWarnings("nls")
public class XSDAnnotationsStructureTest {

    XSDSchema schema = null;

    XSDElementDeclaration decl = null;

    XSDAnnotationsStructure strct = null;
    @Before
    public void setUp() throws Exception {
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
        schema.getElementDeclarations();
        decl = schema.getElementDeclarations().get(0);
        strct = new XSDAnnotationsStructure(decl);
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#setLabel(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testSetLabel() {
        strct.getLabels().clear();
        strct.setLabel("zh", "china");
        assertTrue(strct.getLabels().containsKey("zh"));
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#setLabels(java.util.LinkedHashMap)}.
     */
    @Test
    public void testSetLabels() {
        strct.getLabels().clear();
        Map<String, String> hm = new LinkedHashMap<String, String>();
        hm.put("zh", "china");
        hm.put("en", "english");
        strct.setLabels(hm);
        assertTrue(strct.getLabels().size() == 2);
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#getLabels()}.
     */
    @Test
    public void testGetLabels() {
        strct.getLabels().clear();
        Map<String, String> hm = new LinkedHashMap<String, String>();
        hm.put("zh", "china");
        hm.put("en", "english");
        strct.setLabels(hm);
        assertTrue(strct.getLabels().size() == 2);
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#removeDescription(java.lang.String)}.
     */
    @Test
    public void testRemoveDescription() {
        strct.getDescriptions().clear();
        strct.setDescription("zh", "china");
        strct.removeDescription("zh");
        assertTrue(strct.getDescriptions().size() == 0);
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#removeAllDescriptions()}.
     */
    @Test
    public void testRemoveAllDescriptions() {
        strct.getDescriptions().clear();
        strct.setDescription("zh", "china");
        strct.removeAllDescriptions();
        assertTrue(strct.getDescriptions().size() == 0);
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#setDescription(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testSetDescription() {
        strct.getDescriptions().clear();
        strct.setDescription("zh", "china");
        assertTrue(strct.getDescriptions().size() == 1);
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#setDescriptions(java.util.LinkedHashMap)}.
     */
    @Test
    public void testSetDescriptions() {
        strct.getDescriptions().clear();
        Map<String, String> hm = new LinkedHashMap<String, String>();
        hm.put("zh", "china");
        strct.setDescriptions(hm);
        assertTrue(strct.getDescriptions().size() == 1);
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#getDescriptions()}.
     */
    @Test
    public void testGetDescriptions() {
        strct.getDescriptions().clear();
        Map<String, String> hm = new LinkedHashMap<String, String>();
        hm.put("zh", "china");
        strct.setDescriptions(hm);
        assertTrue(strct.getDescriptions().containsKey("zh"));
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#setFKFilter(java.lang.String)}.
     */
    @Test
    public void testSetFKFilter() {
        strct.setFKFilter("Entity/Id");
        assertTrue(strct.getFKFilter().equals("Entity/Id"));
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#getFKFilter()}.
     */
    @Test
    public void testGetFKFilter() {
        strct.setFKFilter("Entity/Id");
        assertTrue(strct.getFKFilter().equals("Entity/Id"));
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#setForeignKey(java.lang.String)}.
     */
    @Test
    public void testSetForeignKey() {
        strct.setForeignKey("Entity/Id");
        assertTrue("Entity/Id".equals(strct.getForeignKey()));
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#getForeignKey()}.
     */
    @Test
    public void testGetForeignKey() {
        strct.setForeignKey("Entity/Id");
        assertTrue("Entity/Id".equals(strct.getForeignKey()));
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#setForeignKeyNotSep(java.lang.Boolean)}.
     */
    @Test
    public void testSetForeignKeyNotSep() {
        strct.setForeignKeyNotSep(true);
        assertTrue("true".equals(strct.getForeignKeyNotSep()));
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#getForeignKeyNotSep()}.
     */
    @Test
    public void testGetForeignKeyNotSep() {
        strct.setForeignKeyNotSep(true);
        assertTrue("true".equals(strct.getForeignKeyNotSep()));
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#setVisibleRule(java.lang.String)}.
     */
    @Test
    public void testSetVisibleRule() {
        strct.setVisibleRule("Id");
        assertTrue("Id".equals(strct.getVisibleRule()));
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#getVisibleRule()}.
     */
    @Test
    public void testGetVisibleRule() {
        strct.setVisibleRule("Id");
        assertTrue("Id".equals(strct.getVisibleRule()));
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#setDefaultValueRule(java.lang.String)}.
     */
    @Test
    public void testSetDefaultValueRule() {
        strct.setDefaultValueRule("Id");
        assertTrue("Id".equals(strct.getDefaultValueRule()));
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#getDefaultValueRule()}.
     */
    @Test
    public void testGetDefaultValueRule() {
        strct.setDefaultValueRule("Id");
        assertTrue("Id".equals(strct.getDefaultValueRule()));
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#setPrimaryKeyInfos(java.util.List)}.
     */
    @Test
    public void testSetPrimaryKeyInfos() {
        List<String> xpaths = new ArrayList<String>();
        xpaths.add("Id");
        strct.setPrimaryKeyInfos(xpaths);
        assertTrue(strct.getPrimaryKeyInfos().containsValue("Id"));
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#setForeignKeyInfos(java.util.List)}.
     */
    @Test
    public void testSetForeignKeyInfos() {
        List<String> xpaths = new ArrayList<String>();
        xpaths.add("Id");
        strct.setPrimaryKeyInfos(xpaths);
        assertTrue(strct.getPrimaryKeyInfos().containsValue("Id"));
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#setRetrieveFKinfos(boolean)}.
     */
    @Test
    public void testSetRetrieveFKinfos() {
        strct.setRetrieveFKinfos(true);
        assertTrue(true == strct.getRetrieveFKinfos());
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#getRetrieveFKinfos()}.
     */
    @Test
    public void testGetRetrieveFKinfos() {
        strct.setRetrieveFKinfos(true);
        assertTrue(true == strct.getRetrieveFKinfos());
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#setForeignKeyInfo(int, java.lang.String)}.
     */
    @Test
    public void testSetForeignKeyInfo() {
        strct.setForeignKeyInfo(0, "Id");
        assertTrue(strct.getForeignKeyInfos().containsValue("Id"));
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#getForeignKeyInfos()}.
     */
    @Test
    public void testGetForeignKeyInfos() {
        strct.setForeignKeyInfo(0, "Id");
        assertTrue(strct.getForeignKeyInfos().containsValue("Id"));
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#getDenyPhysicalDelete()}.
     */
    @Test
    public void testGetDenyPhysicalDelete() {
        assertTrue(strct.getDenyPhysicalDelete().size() == 0);
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#getDenyCreate()}.
     */
    @Test
    public void testGetDenyCreate() {
        assertTrue(strct.getDenyCreate().size() == 0);
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#getDenyLogicalDelete()}.
     */
    @Test
    public void testGetDenyLogicalDelete() {
        assertTrue(strct.getDenyLogicalDelete().size() == 0);
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#getLookupFields()}.
     */
    @Test
    public void testGetLookupFields() {
        assertTrue(strct.getLookupFields().size() == 0);
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#getPrimaryKeyInfos()}.
     */
    @Test
    public void testGetPrimaryKeyInfos() {
        List<String> xPaths = new ArrayList<String>();
        xPaths.add("Id");
        strct.setPrimaryKeyInfos(xPaths);
        assertTrue(strct.getPrimaryKeyInfos().containsValue("Id"));
    }
    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#setSchematrons(java.util.Collection)}.
     */
    @Test
    public void testSetSchematrons() {
        List<String> schematrons = new ArrayList<String>();
        schematrons.add("schematrons");
        strct.setSchematrons(schematrons);
        assertTrue(strct.getSchematrons().containsValue("schematrons"));
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#setSchematron(int, java.lang.String)}.
     */
    @Test
    public void testSetSchematron() {
        strct.setSchematron(0, "schematron");
        assertTrue(strct.getSchematrons().containsValue("schematron"));
    }

    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#addSchematron(java.lang.String)}.
     */
    @Test
    public void testAddSchematron() {
        strct.addSchematron("schematron");
        assertTrue(strct.getSchematrons().containsValue("schematron"));
    }


    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#setWorkflows(java.util.Collection)}.
     */
    @Test
    public void testSetWorkflows() {
        List<String> list = new ArrayList<String>();
        list.add("workflow");
        strct.setWorkflows(list);
        assertTrue(strct.getWorkflows().containsValue("workflow"));
    }



    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#getWorkflows()}.
     */
    @Test
    public void testGetWorkflows() {
        List<String> list = new ArrayList<String>();
        list.add("workflow");
        strct.setWorkflows(list);
        assertTrue(strct.getWorkflows().containsValue("workflow"));
    }


    /**
     * Test method for {@link com.amalto.workbench.utils.XSDAnnotationsStructure#getFactMessage()}.
     */
    @Test
    public void testGetFactMessage() {
        Map<String, String> facts = new LinkedHashMap<String, String>();
        facts.put("fact", "fact");
        strct.setFactMessage(facts);
        assertTrue("fact".equals(facts.get("fact")));
    }
}
