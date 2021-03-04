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
package com.amalto.workbench.dialogs.datacontainer;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.workbench.utils.Util;


/**
 * created by liusongbo on 2014-1-24
 */
@SuppressWarnings("nls")
public class AutoIncrementHelperTest {

    private String autoIncrementRecord = "";

    @Before
    public void setUp() throws Exception {
        autoIncrementRecord = "<AutoIncrement>\r\n" + "  <id>AutoIncrement</id>\r\n" + "  <entry>\r\n"
                + "    <key>[HEAD].Product.Store.Id</key>\r\n" + "    <value>1</value>\r\n" + "  </entry>\r\n" + "  <entry>\r\n"
                + "    <key>[HEAD].Product.Product.Id</key>\r\n" + "    <value>2</value>\r\n" + "  </entry>\r\n"
                + "  <entry>\r\n" + "    <key>[HEAD].TestModel.ModelA.Id</key>\r\n" + "    <value>3</value>\r\n"
                + "  </entry>\r\n" + "</AutoIncrement>\r\n" + "";
    }

    @Test
    public void testGetCurrentValue() throws Exception {
        assertNull(AutoIncrementHelper.getCurrentValue(null, null));
        assertNull(AutoIncrementHelper.getCurrentValue("", new HashMap<String, String>()));

        Map<String, String> entityToKeys = new HashMap<String, String>();
        entityToKeys.put("Store", "[HEAD].Product.Store.Id");
        entityToKeys.put("Product", "[HEAD].Product.Product.Id");
        entityToKeys.put("ModelA", "[HEAD].TestModel.ModelA.Id");
        entityToKeys.put("ProductFamily", "[HEAD].Product.ProductFamily.Id");

        assertNull(AutoIncrementHelper.getCurrentValue(autoIncrementRecord, null));
        assertNull(AutoIncrementHelper.getCurrentValue(autoIncrementRecord, new HashMap<String, String>()));
        assertNull(AutoIncrementHelper.getCurrentValue(null, entityToKeys));
        assertNull(AutoIncrementHelper.getCurrentValue("", entityToKeys));

        Map<String, String> currentValues = AutoIncrementHelper.getCurrentValue(autoIncrementRecord, entityToKeys);
        assertNotNull(currentValues);
        assertTrue(currentValues.size() == 4);
        assertEquals("1", currentValues.get("Store"));
        assertEquals("2", currentValues.get("Product"));
        assertEquals("3", currentValues.get("ModelA"));
        assertEquals("0", currentValues.get("ProductFamily"));

    }

    @Test
    public void testUpdateValue() throws Exception {
        Map<String, String> keyToValues = new HashMap<String, String>();
        keyToValues.put("[HEAD].Product.Store.Id", "3");
        keyToValues.put("[HEAD].Product.Product.Id", "4");
        keyToValues.put("[HEAD].Product.ProductFamily.Id", "5");
        keyToValues.put("[HEAD].TestModel.ModelA.Id", "0");
        String updatedValue = AutoIncrementHelper.updateValue(autoIncrementRecord, keyToValues);

        assertNotNull(updatedValue);

        String TAG_KEY = "key";
        String TAG_VALUE = "value";
        String TAG_ENTRY = "entry";
        Document doc = Util.parse(updatedValue);
        NodeList entries = doc.getElementsByTagName(TAG_ENTRY);
        assertNotNull(entries);
        assertTrue(entries.getLength() == 3);

        Map<String, String> keyvalues = new HashMap<String, String>();
        for (int i = 0; i < entries.getLength(); i++) {
            Node item = entries.item(i);
            NodeList childNodes = item.getChildNodes();
            String key = null, value = null;
            for (int j = 0; j < childNodes.getLength(); j++) {
                String nodeName = childNodes.item(j).getNodeName();
                if (nodeName.equals(TAG_KEY)) {
                    key = childNodes.item(j).getTextContent();
                } else if (nodeName.equals(TAG_VALUE)) {
                    value = childNodes.item(j).getTextContent();
                }
            }
            keyvalues.put(key, value);
        }

        assertEquals("3", keyvalues.get("[HEAD].Product.Store.Id"));
        assertEquals("4", keyvalues.get("[HEAD].Product.Product.Id"));
        assertEquals("5", keyvalues.get("[HEAD].Product.ProductFamily.Id"));
        assertEquals(null, keyvalues.get("[HEAD].TestModel.ModelA.Id"));
    }

}
