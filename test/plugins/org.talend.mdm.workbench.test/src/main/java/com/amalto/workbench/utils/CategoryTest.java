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

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.junit.Test;
import org.w3c.dom.Element;

import com.amalto.workbench.models.IAnnotationConst;

@SuppressWarnings("nls")
public class CategoryTest {

    private static final Logger LOG = Logger.getLogger(CategoryTest.class);

    private XSDSchema getSchema(String resourceName) {
        InputStream inputStream = getClass().getResourceAsStream(resourceName); // $NON-NLS-1$
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            IOUtils.copy(inputStream, outputStream);
            String xsd = new String(outputStream.toByteArray());
            XSDSchema schema = Util.getXSDSchema(xsd);
            return schema;
        } catch (Exception e) {
            LOG.error("Fail in parsing schema:", e);
        } finally {
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(inputStream);
        }
        return null;
    }

    @Test
    public void testGetCategoryNamesFromEntity() {
        XSDElementDeclaration decl = getEntity(0);
        XSDAnnotationsStructure struct = new XSDAnnotationsStructure(decl);
        Set<String> entities = struct.getCategoryNamesFromEntity();
        assertTrue(entities.size() == 2);
        assertTrue(entities.contains("Category01"));
        assertTrue(entities.contains("c4"));
    }

    private XSDElementDeclaration getEntity(int index) {
        XSDSchema schema = getSchema("TestCategory01.xsd");
        XSDElementDeclaration decl = schema.getElementDeclarations().get(index);
        return decl;
    }

    @Test
    public void testGetFieldCategoryMap() {
        XSDElementDeclaration decl = getEntity(0);
        XSDAnnotationsStructure struct = new XSDAnnotationsStructure(decl);
        Map<String, String> fieldCategoryMap = struct.getFieldCategoryMap();
        assertTrue(fieldCategoryMap.size() == 3);
        assertTrue(fieldCategoryMap.get("size").equals("c4"));
        assertTrue(fieldCategoryMap.get("name").equals("c4"));
        assertTrue(fieldCategoryMap.get("id").equals("Category01"));
    }

    @Test
    public void testSetCategoryFields() {
        XSDElementDeclaration decl = getEntity(0);
        XSDAnnotationsStructure struct = new XSDAnnotationsStructure(decl);
        Map<String, String> newFieldMap = new HashMap<>();
        newFieldMap.put("id", "c4");
        newFieldMap.put("size", "Category01");
        newFieldMap.put("name", "Category01");
        struct.setCategoryFields(newFieldMap);
        Map<String, String> fieldCategoryMap = struct.getFieldCategoryMap();
        assertTrue(fieldCategoryMap.size() == 3);
        assertTrue(fieldCategoryMap.get("size").equals("Category01"));
        assertTrue(fieldCategoryMap.get("name").equals("Category01"));
        assertTrue(fieldCategoryMap.get("id").equals("c4"));
    }

    @Test
    public void testClearFields() {
        XSDElementDeclaration decl = getEntity(0);
        XSDAnnotationsStructure struct = new XSDAnnotationsStructure(decl);
        Map<String, String> newFieldMap = new HashMap<>();
        struct.setCategoryFields(newFieldMap);
        Map<String, String> fieldCategoryMap = struct.getFieldCategoryMap();
        assertTrue(fieldCategoryMap.size() == 0);
    }

    @Test
    public void testGetCategoryFromElement() {
        XSDElementDeclaration decl = getEntity(0);
        XSDAnnotationsStructure struct = new XSDAnnotationsStructure(decl);
        List<Element> elements = getCategoryElements(decl);
        assertTrue(elements.size() == 2);
        Map<String, Map<String, String>> categoryMap = struct.getCategoryFromElement(elements.get(1));
        assertTrue(categoryMap.size() == 1);
        Map<String, String> labelmap = categoryMap.get("c4");
        assertTrue(labelmap.size() == 1);
        assertTrue(labelmap.get("en").equals("abc"));
    }

    private List<Element> getCategoryElements(XSDElementDeclaration decl) {
        List<Element> elements = new ArrayList<>();
        for (Element element : decl.getAnnotation().getApplicationInformation()) {
            String name = element.getLocalName();
            if ("appinfo".equals(name.toLowerCase())) {
                name = element.getAttribute("source");
                if (name.equals(IAnnotationConst.KEY_CATEGORY)) {
                    elements.add(element);
                }
            }
        }
        return elements;
    }

    @Test
    public void testSetCategory4Entity() {
        XSDElementDeclaration decl = getEntity(0);
        XSDAnnotationsStructure struct = new XSDAnnotationsStructure(decl);
        List<Element> elements = getCategoryElements(decl);
        assertTrue(elements.size() == 2);
        String newCategoryName = "Category02";
        Map<String, String> labels = new HashMap<>();
        String value_en = "lablel1";
        labels.put("en", value_en);
        String value_fr = "lablel2";
        labels.put("fr", value_fr);
        struct.setCategory4Entity(elements.get(0), newCategoryName, labels);
        elements = getCategoryElements(decl);
        Map<String, Map<String, String>> categoryMap = struct.getCategoryFromElement(elements.get(0));
        assertTrue(categoryMap.size() == 1);
        Map<String, String> labelmap = categoryMap.get(newCategoryName);
        assertTrue(labelmap.size() == 2);
        assertTrue(labelmap.get("en").equals(value_en));
        assertTrue(labelmap.get("fr").equals(value_fr));
    }

    @Test
    public void testRemoveCategory() {
        XSDElementDeclaration decl = getEntity(0);
        XSDAnnotationsStructure struct = new XSDAnnotationsStructure(decl);
        List<Element> elements = getCategoryElements(decl);
        assertTrue(elements.size() == 2);
        struct.removeCategory(elements.get(0));
        elements = getCategoryElements(decl);
        assertTrue(elements.size() == 1);
    }

    @Test
    public void testGetFields() {
        XSDElementDeclaration decl = getEntity(0);
        List<String> fields = XSDUtil.getFields(decl);
        assertTrue(fields.size() == 3);
        assertTrue(fields.get(0).equals("id"));
        assertTrue(fields.get(1).equals("name"));
        assertTrue(fields.get(2).equals("size"));
    }

    @Test
    public void testGetFieldsWithInheritance() {
        XSDSchema schema = getSchema("TestCategory02.xsd");
        XSDElementDeclaration decl = schema.getElementDeclarations().get(0);

        List<String> fields = XSDUtil.getFields(decl);
        assertTrue(fields.size() == 3);
        assertTrue(fields.get(0).equals("id"));
        assertTrue(fields.get(1).equals("name"));
        assertTrue(fields.get(2).equals("size"));
    }
}
