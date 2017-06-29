// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amalto.workbench.models.TreeObject;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ResourcesUtil.class })
public class ResourcesUtilTest {

    private static Logger log = Logger.getLogger(ResourcesUtilTest.class);


    @Test
    public void testGetResourcesMapFromURI() {
        String method_private_getXMLString = "getXMLString"; //$NON-NLS-1$
        String method_private_parsXMLString = "parsXMLString"; //$NON-NLS-1$
        String responseBody = "responseBody"; //$NON-NLS-1$
        String uri = "uri"; //$NON-NLS-1$
        TreeObject treeObj = new TreeObject();
        try {
            PowerMockito.mockStatic(ResourcesUtil.class);
            PowerMockito.when(ResourcesUtil.class, method_private_getXMLString, anyString(), any(TreeObject.class))
                    .thenReturn(responseBody);
            Document doc = null;
            PowerMockito.when(ResourcesUtil.class, method_private_parsXMLString, anyString()).thenReturn(doc);
            PowerMockito.when(ResourcesUtil.getResourcesMapFromURI(anyString(), any(TreeObject.class))).thenCallRealMethod();

            HashMap<String, String> resourcesMapFromURI = ResourcesUtil.getResourcesMapFromURI(uri, treeObj);
            assertTrue(resourcesMapFromURI.isEmpty());

            doc = DocumentHelper.createDocument();
            Element rootElement = DocumentHelper.createElement("root"); //$NON-NLS-1$
            doc.setRootElement(rootElement);
            PowerMockito.when(ResourcesUtil.class, method_private_parsXMLString, anyString()).thenReturn(doc);

            boolean[][] ownNameURIElement = { { true, true }, { true, false }, { false, true }, { false, false } };
            String[][] localNameUris = { { "name0", "uri0" }, { "name1", "uri1" }, { "name2", "uri2" }, { "name3", "uri3" } }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
            String entry = "entry"; //$NON-NLS-1$
            for (int i = 0; i < ownNameURIElement.length; i++) {
                createChildElement(rootElement, entry + i, localNameUris[i], ownNameURIElement[i][0], ownNameURIElement[i][1]);
            }

            resourcesMapFromURI = ResourcesUtil.getResourcesMapFromURI(uri, treeObj);
            assertNotNull(resourcesMapFromURI);
            assertEquals(4, resourcesMapFromURI.size());

            Map<String, String> resourcesMap_expected = new HashMap<String, String>();
            resourcesMap_expected.put("name0", "uri0"); //$NON-NLS-1$ //$NON-NLS-2$
            resourcesMap_expected.put("entry1name1", "uri/entry1name1"); //$NON-NLS-1$ //$NON-NLS-2$
            resourcesMap_expected.put("entry2uri2", "uri/entry2uri2"); //$NON-NLS-1$ //$NON-NLS-2$
            resourcesMap_expected.put("entry3", "uri/entry3"); //$NON-NLS-1$ //$NON-NLS-2$
            assertEquals(resourcesMap_expected, resourcesMapFromURI);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void createChildElement(Element parentElement, String entry, String[] localNameUris, boolean hasNameElement,
            boolean hasURIElement) {
        Element entryElement = DocumentHelper.createElement("entry"); //$NON-NLS-1$
        entryElement.setText(entry);
        if (hasNameElement) {
            Element nameElement = DocumentHelper.createElement("name"); //$NON-NLS-1$
            nameElement.setText(localNameUris[0]);
            entryElement.add(nameElement);
        }
        if (hasURIElement) {
            Element uriElement = DocumentHelper.createElement("uri"); //$NON-NLS-1$
            uriElement.setText(localNameUris[1]);
            entryElement.add(uriElement);
        }

        parentElement.add(entryElement);
    }

    @Test
    public void testGetResourcesNameListFromURI() {
        String method_private_getXMLString = "getXMLString"; //$NON-NLS-1$
        String method_private_parsXMLString = "parsXMLString"; //$NON-NLS-1$
        String responseBody = "responseBody"; //$NON-NLS-1$
        String uri = "uri"; //$NON-NLS-1$
        TreeObject treeObj = new TreeObject();
        try {
            PowerMockito.mockStatic(ResourcesUtil.class);
            PowerMockito.when(ResourcesUtil.class, method_private_getXMLString, anyString(), any(TreeObject.class))
                    .thenReturn(responseBody);
            Document doc = null;
            PowerMockito.when(ResourcesUtil.class, method_private_parsXMLString, anyString()).thenReturn(doc);
            PowerMockito.when(ResourcesUtil.getResourcesNameListFromURI(anyString(), any(TreeObject.class))).thenCallRealMethod();

            List<String> resourcesNameList = ResourcesUtil.getResourcesNameListFromURI(uri, treeObj);
            assertTrue(resourcesNameList.isEmpty());

            doc = DocumentHelper.createDocument();
            Element rootElement = DocumentHelper.createElement("root"); //$NON-NLS-1$
            doc.setRootElement(rootElement);
            PowerMockito.when(ResourcesUtil.class, method_private_parsXMLString, anyString()).thenReturn(doc);

            boolean[][] ownNameURIElement = { { true, false }, { false, false } };
            String[][] localNameUris = { { "name0", "uri0" }, { "name1", "uri1" } }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            String entry = "entry"; //$NON-NLS-1$
            for (int i = 0; i < ownNameURIElement.length; i++) {
                createChildElement(rootElement, entry + i, localNameUris[i], ownNameURIElement[i][0], ownNameURIElement[i][1]);
            }

            resourcesNameList = ResourcesUtil.getResourcesNameListFromURI(uri, treeObj);
            assertNotNull(resourcesNameList);
            assertEquals(2, resourcesNameList.size());

            List<String> resourcesNamesList = new ArrayList<String>();
            resourcesNamesList.add("name0"); //$NON-NLS-1$
            resourcesNamesList.add("entry1"); //$NON-NLS-1$
            assertEquals(resourcesNamesList, resourcesNameList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testGetEndpointHost() {
        String uri = null;
        String endpointHost = ResourcesUtil.getEndpointHost(uri);
        assertNull(endpointHost);

        uri = "abc//";// any invalid uri //$NON-NLS-1$
        endpointHost = ResourcesUtil.getEndpointHost(uri);
        assertEquals(uri, endpointHost);

        uri = "http://localhost:8180/talendmdm/services/soap?wsdl"; //$NON-NLS-1$

        endpointHost = ResourcesUtil.getEndpointHost(uri);
        assertEquals("localhost", endpointHost); //$NON-NLS-1$

        uri = "http://localhost/talendmdm/services/soap?wsdl"; //$NON-NLS-1$
        endpointHost = ResourcesUtil.getEndpointHost(uri);
        assertEquals("localhost", endpointHost); //$NON-NLS-1$

        uri = "file://localhost:1234/b/c/d.txt"; //$NON-NLS-1$
        endpointHost = ResourcesUtil.getEndpointHost(uri);
        assertEquals("localhost", endpointHost); //$NON-NLS-1$
    }

    @Test
    public void testGetEndpointPort() {
        String uri = null;
        String endpointHost = ResourcesUtil.getEndpointPort(uri);
        assertNull(endpointHost);

        uri = "abc//";// any invalid uri //$NON-NLS-1$
        endpointHost = ResourcesUtil.getEndpointPort(uri);
        assertEquals(uri, endpointHost);

        uri = "http://localhost:8180/talendmdm/services/soap?wsdl"; //$NON-NLS-1$

        endpointHost = ResourcesUtil.getEndpointPort(uri);
        assertEquals("8180", endpointHost); //$NON-NLS-1$

        uri = "http://localhost/talendmdm/services/soap?wsdl"; //$NON-NLS-1$
        endpointHost = ResourcesUtil.getEndpointPort(uri);
        assertEquals("80", endpointHost); //$NON-NLS-1$

        uri = "file://localhost:1234/b/c/d.txt"; //$NON-NLS-1$
        endpointHost = ResourcesUtil.getEndpointPort(uri);
        assertEquals("1234", endpointHost); //$NON-NLS-1$

    }

}
