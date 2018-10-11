// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.ui.wizards.imports;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ReferenceFileItem;
import org.talend.mdm.repository.model.mdmproperties.WSDataModelItem;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.amalto.workbench.utils.Util;

/**
 * @author sbliu
 *
 */
public class DatamodelSchemaUpdator {

    private static Logger log = Logger.getLogger(DatamodelSchemaUpdator.class);

    private static final String TAG_XSD_UNIQUE = "xsd:unique"; //$NON-NLS-1$

    private static final String TAG_XSD_ELEMENT = "xsd:element"; //$NON-NLS-1$

    private static final String TAG_XSD_FIELD = "xsd:field"; //$NON-NLS-1$

    private static final String TAG_XSD_SEQUENCE = "xsd:sequence"; //$NON-NLS-1$

    private static final String ATTR_XPATH = "xpath"; //$NON-NLS-1$

    private static final String ID = "ID"; //$NON-NLS-1$

    private static final String TIME_IN_MILLIS = "TimeInMillis"; //$NON-NLS-1$

    public boolean updateSchema(Item item) {
        boolean modified = false;
        if (item != null && item instanceof WSDataModelItem) {

            WSDataModelItem modelItem = (WSDataModelItem) item;
            if ("UpdateReport".equalsIgnoreCase(modelItem.getProperty().getLabel())) { //$NON-NLS-1$
                EList<ReferenceFileItem> resources = modelItem.getReferenceResources();
                for (ReferenceFileItem fileItem : resources) {
                    if (fileItem.getExtension().equals("xsd")) { //$NON-NLS-1$
                        ByteArray content = fileItem.getContent();
                        byte[] byteContent = content.getInnerContent();
                        String xsdSchema = updateSchema(byteContent);
                        if (xsdSchema != null) {
                            try {
                                byteContent = xsdSchema.getBytes("utf-8"); //$NON-NLS-1$
                                content.setInnerContent(byteContent);
                                modelItem.getWsDataModel().setXsdSchema(new String(byteContent, "utf-8")); //$NON-NLS-1$
                            } catch (UnsupportedEncodingException e) {
                                log.error(e.getMessage(), e);
                            }

                            modified = true;
                        }
                        break;
                    }
                }

                if (modified) {
                    RepositoryResourceUtil.saveItem(item);
                }
            }
        }

        return modified;

    }

    private String updateSchema(byte[] byteContent) {
        String result = null;
        if (byteContent != null) {

            boolean modified = false;
            try {
                Document document = getSchemaDocument(byteContent);

                NodeList element_fields = document.getElementsByTagName(TAG_XSD_FIELD);
                if (element_fields.getLength() > 0) {
                    modified = handlePKFileds(element_fields);
                } else {
                    addPKFields(document);
                    modified = true;
                }

                if (modified) {
                    handlePKElement(document);
                }

                if (modified) {
                    result = Util.nodeToString(document);
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return result;
    }


    private boolean handlePKFileds(NodeList element_fields) {
        boolean modified = true;

        Node item0 = element_fields.item(0);
        Node item0Sibling = item0.getNextSibling();
        Node parentNode = item0.getParentNode();

        Node idN = null;
        Node timeInMillisNode = null;
        for (int index = 0; index < element_fields.getLength(); index++) {
            Node field = element_fields.item(index);
            Node xpathAttr = field.getAttributes().getNamedItem(ATTR_XPATH);
            if (idN == null && ID.equals(xpathAttr.getNodeValue())) {
                idN = field;
                continue;
            } else if (TIME_IN_MILLIS == null && TIME_IN_MILLIS.equals(xpathAttr.getNodeValue())) {
                timeInMillisNode = field;
                continue;
            }


            if (field.getNextSibling().getNodeType() == Node.TEXT_NODE) {
                parentNode.removeChild(field.getNextSibling());
            }
            parentNode.removeChild(field);
            modified = true;
        }

        if (idN == null) {
            addNode(parentNode, item0, item0Sibling, ID);
            modified = true;
        }

        if (timeInMillisNode == null) {
            addNode(parentNode, item0, item0Sibling, TIME_IN_MILLIS);
            modified = true;
        }

        return modified;
    }

    private void addNode(Node parentNode, Node item0, Node item0Sibling, String xpathValue) {
        if (item0Sibling.getNodeType() == Node.TEXT_NODE) {
            Node textNode = item0Sibling.cloneNode(true);
            parentNode.appendChild(textNode);
        }

        Node idNode = item0.cloneNode(true);
        Node xpath = idNode.getAttributes().getNamedItem(ATTR_XPATH);
        xpath.setNodeValue(xpathValue);
        parentNode.appendChild(idNode);
    }

    private boolean addPKFields(Document document) {
        NodeList xsdelementNode = document.getElementsByTagName(TAG_XSD_ELEMENT);
        Element xsdUnique = document.createElement(TAG_XSD_UNIQUE);
        xsdelementNode.item(0).appendChild(xsdUnique);

        for (String xpath : new String[] { ID, TIME_IN_MILLIS }) {
            Element pkElement = document.createElement(TAG_XSD_FIELD);
            pkElement.setAttribute(ATTR_XPATH, xpath);
            xsdUnique.appendChild(pkElement);
        }

        return true;
    }

    private void handlePKElement(Document document) {
        NodeList sequence = document.getElementsByTagName(TAG_XSD_SEQUENCE);
        if (sequence.getLength() > 0) {
            Node sequenceNode = sequence.item(0);

            boolean foundIDElement = false;
            boolean foundTimeInMillisElement = false;
            NodeList childNodes = sequenceNode.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                NamedNodeMap attrs = childNodes.item(i).getAttributes();
                if (attrs != null) {
                    Node namedItem = attrs.getNamedItem("name"); //$NON-NLS-1$
                    if (namedItem != null && ID.equals(namedItem.getNodeValue())) {
                        foundIDElement = true;
                    }
                    if (namedItem != null && TIME_IN_MILLIS.equals(namedItem.getNodeValue())) {
                        foundTimeInMillisElement = true;
                    }
                }

                if (foundIDElement && foundTimeInMillisElement) {
                    break;
                }
            }

            if (!foundIDElement) {
                addChildToSequence(document, sequenceNode, ID);
            }

            if (!foundTimeInMillisElement) {
                addChildToSequence(document, sequenceNode, TIME_IN_MILLIS);
            }
        }
    }

    private void addChildToSequence(Document document, Node sequenceNode, String nameValue) {
        Node firstChild = sequenceNode.getFirstChild();
        Element element = createElement(document, nameValue);
        sequenceNode.insertBefore(element, firstChild);

        if (firstChild.getNodeType() == Node.TEXT_NODE) {
            String textNodeValue = firstChild.getNodeValue();
            Text textNode = document.createTextNode(textNodeValue);
            sequenceNode.insertBefore(textNode, element);
        }
    }

    private Element createElement(Document document, String nameValue) {
        Element idElement = document.createElement(TAG_XSD_ELEMENT);
        idElement.setAttribute("maxOccurs", "1"); //$NON-NLS-1$ //$NON-NLS-2$
        idElement.setAttribute("minOccurs", "1"); //$NON-NLS-1$ //$NON-NLS-2$
        idElement.setAttribute("name", nameValue); //$NON-NLS-1$
        idElement.setAttribute("nillable", "false"); //$NON-NLS-1$ //$NON-NLS-2$
        idElement.setAttribute("type", "xsd:string"); //$NON-NLS-1$ //$NON-NLS-2$

        return idElement;
    }

    private Document getSchemaDocument(byte[] byteContent) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setValidating(false);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        InputSource source = new InputSource(new ByteArrayInputStream(byteContent));
        Document document = documentBuilder.parse(source);
        return document;
    }
}
