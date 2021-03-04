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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.VisitorSupport;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.talend.utils.xml.XmlUtils;

import com.amalto.workbench.actions.XSDGetXPathAction;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.callback.AttributeProcess;
import com.amalto.workbench.utils.callback.DocumentCreate;
import com.amalto.workbench.utils.callback.ElementProcess;
import com.amalto.workbench.utils.callback.NodeProcess;

public final class XmlUtil {

    private static Log log = LogFactory.getLog(XSDGetXPathAction.class);

    private static final Log logger = LogFactory.getLog(XmlUtil.class);

    public static Document parse(URL url) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(url);
        return document;
    }

    public static Document parse(File file) throws MalformedURLException, DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        return document;
    }

    public static Document parse(InputStream in) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(in);
        return document;
    }

    public static Document parse(String fileName) throws DocumentException {
        InputStream is = null;
        try {
            is = XmlUtil.class.getResourceAsStream("/" + fileName);//$NON-NLS-1$
            Document document = parse(is);
            return document;
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {

                }
        }
    }

    public static void iterate(Document document, ElementProcess elementProcess) throws DocumentException {
        Element root = document.getRootElement();
        iterate(root, elementProcess);
    }

    public static void iterate(Element parentElement, ElementProcess elementProcess) throws DocumentException {
        // iterate through child elements of element
        for (Iterator i = parentElement.elementIterator(); i.hasNext();) {
            Element element = (Element) i.next();
            // do something
            elementProcess.process(element);
        }
    }

    public static void iterate(Document document, String elementName, ElementProcess elementProcess) throws DocumentException {

        Element root = document.getRootElement();

        iterate(root, elementName, elementProcess);

    }

    public static void iterate(Element parentElement, String elementName, ElementProcess elementProcess) throws DocumentException {

        // iterate through child elements of element with element specific
        // element
        // name
        for (Iterator i = parentElement.elementIterator(elementName); i.hasNext();) {
            Element element = (Element) i.next();
            // do something
            elementProcess.process(element);
        }

    }

    public static void iterateAttribute(Document document, AttributeProcess attributeProcess) throws DocumentException {

        Element root = document.getRootElement();

        iterateAttribute(root, attributeProcess);
    }

    public static void iterateAttribute(Element element, AttributeProcess attributeProcess) throws DocumentException {

        // iterate through attributes of element
        for (Iterator i = element.attributeIterator(); i.hasNext();) {
            Attribute attribute = (Attribute) i.next();
            // do something
            attributeProcess.process(attribute);
        }
    }

    public static void treeWalk(Document document, NodeProcess nodeProcess) {
        treeWalk(document.getRootElement(), nodeProcess);
    }

    public static void treeWalk(Element element, NodeProcess nodeProcess) {
        for (int i = 0, size = element.nodeCount(); i < size; i++) {
            Node node = element.node(i);

            if (node instanceof Element) {
                treeWalk((Element) node, nodeProcess);
            } else {
                nodeProcess.process(node);
            }
        }
    }

    public static void visit(Document document, VisitorSupport visitor) {
        visit(document.getRootElement(), visitor);
    }

    public static void visit(Element element, VisitorSupport visitor) {
        element.accept(visitor);
    }

    public static Node queryNode(Document document, String xPath) {

        Node node = document.selectSingleNode(xPath);

        return node;
    }

    public static List queryList(Document document, String xPath) {

        List list = document.selectNodes(xPath);

        return list;
    }

    public static List findLinks(Document document) throws DocumentException {

        List<String> urls = new ArrayList();

        List list = document.selectNodes("//a/@href");//$NON-NLS-1$

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Attribute attribute = (Attribute) iter.next();
            String url = attribute.getValue();
            urls.add(url);
        }

        return urls;
    }

    public static Document createDocument(DocumentCreate documentCreate) {

        Document document = DocumentHelper.createDocument();

        documentCreate.create(document);

        logger.info(Messages.XmlUtil_Loginfo);

        return document;
    }

    public static void write(Document document, String filePath, String printMode, String encoding) throws IOException {

        OutputFormat format = null;

        if (printMode.equalsIgnoreCase("pretty")) {//$NON-NLS-1$
            // Pretty print the document
            format = OutputFormat.createPrettyPrint();
        } else if (printMode.equalsIgnoreCase("compact")) {//$NON-NLS-1$
            // Compact format
            format = OutputFormat.createCompactFormat();
        }

        format.setEncoding(encoding);

        // lets write to a file
        XMLWriter writer = new XMLWriter(new FileOutputStream(filePath), format);

        // XMLWriter logger = new XMLWriter( System.out, format );

        writer.write(document);

        logger.info(Messages.bind(Messages.XmlUtil_Loginfo1, filePath));

        // logger.write( document );

        // logger.close();

        writer.close();
    }

    public static String format(Document document, OutputFormat format, String encoding) {

        StringWriter writer = new StringWriter();

        format.setEncoding(encoding);

        format.setNewLineAfterDeclaration(false);
        // format.setSuppressDeclaration(suppressDeclaration);

        XMLWriter xmlwriter = new XMLWriter(writer, format);

        String result = ""; //$NON-NLS-1$

        try {

            xmlwriter.write(document);
            result = writer.toString().replaceAll("<\\?xml.*?\\?>", "").trim();//$NON-NLS-1$//$NON-NLS-2$
        } catch (Exception e) {
            log.error(e.getMessage(), e);

        } finally {
            try {
                if (xmlwriter != null)
                    xmlwriter.close();

                if (writer != null)
                    writer.close();
            } catch (IOException e) {

            }
        }

        return result;
    }

    public static String formatPretty(String xml, String encoding) throws DocumentException {
        Document document = fromXml(xml);
        return format(document, OutputFormat.createPrettyPrint(), encoding);
    }

    public static String formatCompact(String xml, String encoding) throws DocumentException {
        Document document = fromXml(xml);
        return format(document, OutputFormat.createCompactFormat(), encoding);
    }

    public static String formatCompletely(String xml, String encoding) throws DocumentException {
        // FIXME:parse two times, not good
        String compactContent = formatCompact(xml, encoding);
        return formatPretty(compactContent, encoding);
    }

    public static String toXml(Document document) {

        String text = document.asXML();

        return text;
    }

    public static Document fromXml(String text) throws DocumentException {

        Document document = DocumentHelper.parseText(text);

        return document;
    }

    public static void print(Document document) {

        String text = toXml(document);

        log.info(text);
    }

    public static String formatXmlSource(String xmlSource) {

        SAXReader reader = new SAXReader();
        StringReader stringReader = new StringReader(xmlSource);
        StringWriter writer = null;
        XMLWriter xmlwriter = null;
        String result = xmlSource;

        try {
            Document document = reader.read(stringReader);
            writer = new StringWriter();
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");//$NON-NLS-1$
            format.setIndentSize(4);
            format.setSuppressDeclaration(true);
            xmlwriter = new XMLWriter(writer, format);
            xmlwriter.write(document);
            result = writer.toString();
        } catch (Exception e) {

        } finally {

            try {
                if (stringReader != null)
                    stringReader.close();

                if (xmlwriter != null)
                    xmlwriter.close();

                if (writer != null)
                    writer.close();

            } catch (Exception e) {

            }

        }
        return result;

    }

    public static Document styleDocument(Document document, String stylesheet) throws Exception {

        // load the transformer using JAXP
        TransformerFactory factory = XmlUtils.getXmlSecureTransformerFactory();
        Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));

        // now lets style the given document
        DocumentSource source = new DocumentSource(document);
        DocumentResult result = new DocumentResult();
        transformer.transform(source, result);

        // return the transformed document
        Document transformedDoc = result.getDocument();

        logger.info(Messages.XmlUtil_Loginfo2);

        return transformedDoc;
    }

    // //test
    // public static void main(String[] args) {
    // for (int i = 0; i < args.length; i++) {
    // String xml = "<Country>\n  <name>China1</name>\n  <continent>Asian123</continent>\n</Country>";
    // // Pattern tt = Pattern.compile("<\?xml\sversion=\"1.0\"\sencoding=\"UTF-8\"\?>");
    // try {
    // System.out.println(XmlUtil.formatCompact(xml, "UTF-8"));
    // } catch (DocumentException e) {
    // log.error(e.getMessage(), e);
    // }
    // }
    // }
}
