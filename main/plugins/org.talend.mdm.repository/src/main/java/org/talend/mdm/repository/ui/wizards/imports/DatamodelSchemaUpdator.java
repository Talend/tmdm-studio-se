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

import com.amalto.workbench.utils.Util;

/**
 * @author sbliu
 *
 */
public class DatamodelSchemaUpdator {

    private static Logger log = Logger.getLogger(DatamodelSchemaUpdator.class);

    private static final String ID = "ID"; //$NON-NLS-1$

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
                DocumentBuilder documentBuilder = getDocumentBuilder();
                InputSource source = new InputSource(new ByteArrayInputStream(byteContent));
                Document document = documentBuilder.parse(source);

                NodeList element_fields = document.getElementsByTagName("xsd:field"); //$NON-NLS-1$
                if (element_fields.getLength() > 0) {
                    Node field = element_fields.item(0);
                    Node xpathAttr = field.getAttributes().getNamedItem("xpath"); //$NON-NLS-1$
                    if (!ID.equals(xpathAttr.getNodeValue())) {
                        xpathAttr.setNodeValue(ID);
                        modified = true;
                    }
                    Node parentNode = field.getParentNode();
                    for(int i =1; i<element_fields.getLength(); i++) {
                        Node item = element_fields.item(i);
                        if (item.getNextSibling().getNodeType() == Node.TEXT_NODE) {
                            parentNode.removeChild(item.getNextSibling());
                        }
                        parentNode.removeChild(item);

                        modified = true;
                    }

                    if (modified) {
                        NodeList sequence = document.getElementsByTagName("xsd:sequence"); //$NON-NLS-1$
                        Node sequenceNode = sequence.item(0);

                        boolean foundIDElement = false;
                        NodeList childNodes = sequenceNode.getChildNodes();
                        for (int i = 0; i < childNodes.getLength(); i++) {
                            NamedNodeMap attrs = childNodes.item(i).getAttributes();
                            if (attrs != null) {
                                Node namedItem = attrs.getNamedItem("name"); //$NON-NLS-1$
                                if (namedItem != null && ID.equals(namedItem.getNodeValue())) {
                                    foundIDElement = true;
                                    break;
                                }
                            }
                        }
                        
                        if (!foundIDElement) {
                            Node firstChild = sequenceNode.getFirstChild();
                            Element idElement = createIDElement(document);
                            sequenceNode.insertBefore(idElement, firstChild);

                            if (firstChild.getNodeType() == Node.TEXT_NODE) {
                                String textNodeValue = firstChild.getNodeValue();
                                Text textNode = document.createTextNode(textNodeValue);
                                sequenceNode.insertBefore(textNode, idElement);
                            }
                        }
                    }
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

    private Element createIDElement(Document document) {
        Element idElement = document.createElement("xsd:element"); //$NON-NLS-1$
        idElement.setAttribute("maxOccurs", "1"); //$NON-NLS-1$ //$NON-NLS-2$
        idElement.setAttribute("minOccurs", "1"); //$NON-NLS-1$ //$NON-NLS-2$
        idElement.setAttribute("name", ID); //$NON-NLS-1$
        idElement.setAttribute("nillable", "false"); //$NON-NLS-1$ //$NON-NLS-2$
        idElement.setAttribute("nillable", "false"); //$NON-NLS-1$ //$NON-NLS-2$
        idElement.setAttribute("type", "xsd:string"); //$NON-NLS-1$ //$NON-NLS-2$
        return idElement;
    }

    private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setValidating(false);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder;
    }
}
