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
import static org.mockito.Mockito.*;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.CDATA;
import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.Text;
import org.junit.Test;

import com.amalto.workbench.utils.callback.AttributeProcess;
import com.amalto.workbench.utils.callback.ElementProcess;
import com.amalto.workbench.utils.callback.NodeProcess;

public class XmlUtilTest {

    private static Logger log = Logger.getLogger(XmlUtilTest.class);

    /*
     * iterate(Element parentElement, ElementProcess elementProcess)
     */
    @Test
    public void testIterateElementElementProcess() {
        try {
            String rootTag = "root"; //$NON-NLS-1$
            String tagA = "elementA"; //$NON-NLS-1$
            String tagB = "elementB"; //$NON-NLS-1$
            String tagC = "elementC"; //$NON-NLS-1$
            Element root = DocumentHelper.createElement(rootTag);
            Element ele1 = DocumentHelper.createElement(tagA);
            Element ele2 = DocumentHelper.createElement(tagB);
            Element ele3 = DocumentHelper.createElement(tagC);
            root.add(ele1);
            root.add(ele2);
            root.add(ele3);

            ElementProcess mockElementProcess = mock(ElementProcess.class);
            XmlUtil.iterate(root, mockElementProcess);
            verify(mockElementProcess, times(1)).process(ele1);
            verify(mockElementProcess, times(1)).process(ele2);
            verify(mockElementProcess, times(1)).process(ele3);
        } catch (DocumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    /*
     * iterate(Element parentElement, String elementName, ElementProcess elementProcess)
     */
    @Test
    public void testIterateElementStringElementProcess() {
        try {
            String rootTag = "root"; //$NON-NLS-1$
            String tag = "elementA"; //$NON-NLS-1$
            String tagA = tag;
            String tagB = "elementB"; //$NON-NLS-1$
            String tagC = tag;
            Element root = DocumentHelper.createElement(rootTag);
            Element ele1 = DocumentHelper.createElement(tagA);
            Element ele2 = DocumentHelper.createElement(tagB);
            Element ele3 = DocumentHelper.createElement(tagC);
            root.add(ele1);
            root.add(ele2);
            root.add(ele3);

            ElementProcess mockElementProcess = mock(ElementProcess.class);
            XmlUtil.iterate(root, tag, mockElementProcess);
            verify(mockElementProcess, times(1)).process(ele1);
            verify(mockElementProcess, times(0)).process(ele2);
            verify(mockElementProcess, times(1)).process(ele3);
        } catch (DocumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    /*
     * iterateAttribute(Element element, AttributeProcess attributeProcess)
     */
    @Test
    public void testIterateAttribute() {
        String tag = "name"; //$NON-NLS-1$
        Element element = DocumentHelper.createElement(tag);
        Attribute attr1 = DocumentHelper.createAttribute(element, "Attr1", "Value1"); //$NON-NLS-1$ //$NON-NLS-2$
        Attribute attr2 = DocumentHelper.createAttribute(element, "Attr2", "Value2"); //$NON-NLS-1$ //$NON-NLS-2$
        Attribute attr3 = DocumentHelper.createAttribute(element, "Attr3", "Value3"); //$NON-NLS-1$ //$NON-NLS-2$
        element.add(attr1);
        element.add(attr2);
        element.add(attr3);
        AttributeProcess mockAttributeProcess = mock(AttributeProcess.class);
        try {
            XmlUtil.iterateAttribute(element, mockAttributeProcess);
            verify(mockAttributeProcess).process(attr1);
            verify(mockAttributeProcess).process(attr2);
            verify(mockAttributeProcess).process(attr3);
        } catch (DocumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    /*
     * treeWalk(Element element, NodeProcess nodeProcess)
     */
    @Test
    public void testTreeWalk() {
        String tag = "name"; //$NON-NLS-1$
        Element parent = DocumentHelper.createElement(tag);

        Attribute attr1 = DocumentHelper.createAttribute(parent, "Attr", "Value"); //$NON-NLS-1$ //$NON-NLS-2$
        CDATA cDATA1 = DocumentHelper.createCDATA("cdata"); //$NON-NLS-1$
        Comment comment1 = DocumentHelper.createComment("comment"); //$NON-NLS-1$
        Entity entity1 = DocumentHelper.createEntity("entity", "entityname"); //$NON-NLS-1$ //$NON-NLS-2$
        Text text1 = DocumentHelper.createText("text"); //$NON-NLS-1$
        parent.add(attr1);
        parent.add(cDATA1);
        parent.add(comment1);
        parent.add(entity1);
        parent.add(text1);

        Element childElement = DocumentHelper.createElement("child"); //$NON-NLS-1$
        parent.add(childElement);

        Attribute attr2 = DocumentHelper.createAttribute(parent, "Attr", "Value"); //$NON-NLS-1$ //$NON-NLS-2$
        CDATA cDATA2 = DocumentHelper.createCDATA("cdata"); //$NON-NLS-1$
        Comment comment2 = DocumentHelper.createComment("comment"); //$NON-NLS-1$
        Entity entity2 = DocumentHelper.createEntity("entity", "entityname"); //$NON-NLS-1$ //$NON-NLS-2$
        Text text2 = DocumentHelper.createText("text"); //$NON-NLS-1$
        childElement.add(attr2);
        childElement.add(cDATA2);
        childElement.add(comment2);
        childElement.add(entity2);
        childElement.add(text2);

        NodeProcess mockNodeProcess = mock(NodeProcess.class);
        XmlUtil.treeWalk(parent, mockNodeProcess);

        verify(mockNodeProcess, times(0)).process(attr1);
        verify(mockNodeProcess).process(cDATA1);
        verify(mockNodeProcess).process(comment1);
        verify(mockNodeProcess).process(entity1);
        verify(mockNodeProcess).process(text1);
        verify(mockNodeProcess, times(0)).process(attr2);
        verify(mockNodeProcess).process(cDATA2);
        verify(mockNodeProcess).process(comment2);
        verify(mockNodeProcess).process(entity2);
        verify(mockNodeProcess).process(text2);
    }

    @Test
    public void testFindLink() {
        Document doc = DocumentHelper.createDocument();
        Element rootElement = DocumentHelper.createElement("root"); //$NON-NLS-1$
        doc.add(rootElement);

        Element linkElement1 = DocumentHelper.createElement("a"); //$NON-NLS-1$
        String url1 = "url1"; //$NON-NLS-1$
        Attribute attr1 = DocumentHelper.createAttribute(rootElement, "href", url1); //$NON-NLS-1$
        linkElement1.add(attr1);
        rootElement.add(linkElement1);//

        Element linkElement2 = DocumentHelper.createElement("a"); //$NON-NLS-1$
        String url2 = "url2"; //$NON-NLS-1$
        Attribute attr2 = DocumentHelper.createAttribute(rootElement, "href", url2); //$NON-NLS-1$
        linkElement2.add(attr2);
        Element childElement = DocumentHelper.createElement("child"); //$NON-NLS-1$
        rootElement.add(childElement);
        childElement.add(linkElement2);
        try {
            List<?> links = XmlUtil.findLinks(doc);
            assertTrue(links.contains(url1));
            assertTrue(links.contains(url2));
        } catch (DocumentException e) {
            log.error(e.getMessage(), e);
        }

    }
}
