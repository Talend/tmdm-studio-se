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
package org.talend.mdm.repository.ui.wizards.imports;

import org.apache.log4j.Logger;
import org.talend.core.model.properties.Item;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.workbench.utils.Util;

@SuppressWarnings("nls")
public class DataModelSchemaUpdator extends AbstractDataModelUpdator {

    private static final Logger LOG = Logger.getLogger(DataModelSchemaUpdator.class);

    private static final String TAG_XSD_ELEMENT = "xsd:element";

    private static final String TAG_XSD_COMPLEXTYPE = "xsd:complexType";

    private static final String TAG_XSD_SEQUENCE = "xsd:sequence";

    private static final String ATTR_NAME = "name";

    private static final String UUID = "UUID";

    private static final String TIME_IN_MILLIS = "TimeInMillis";

    private static final String KEY = "Key";

    private static final String PRIMARY_KEY_INFO = "PrimaryKeyInfo";

    public boolean updateSchema(Item item) {
        return updateDatamodel(item);
    }

    @Override
    protected boolean accept(Item datamodelItem) {
        return "UpdateReport".equalsIgnoreCase(datamodelItem.getProperty().getLabel());
    }

    @Override
    protected String doUpdate(byte[] byteContent) {
        return updateSchema(byteContent);
    }

    private String updateSchema(byte[] byteContent) {
        String result = null;
        if (byteContent != null) {
            boolean modified = false;
            try {
                Document document = getSchemaDocument(byteContent);
                modified = updateDocument(document);
                if (modified) {
                    result = Util.formatXsdSource(Util.nodeToString(document));
                }

            } catch (Exception e) {
                LOG.error("Failed to update data model schema.", e);
            }
        }

        return result;
    }

    private boolean updateDocument(Document document) {
        boolean modified = false;

        NodeList complexTypeNodeList = document.getElementsByTagName(TAG_XSD_COMPLEXTYPE);
        if (complexTypeNodeList.getLength() > 0) {
            Node complexTypeNode = complexTypeNodeList.item(0);
            if (complexTypeNode.hasChildNodes()) {
                Node sequenceNode = complexTypeNode.getFirstChild();
                while (sequenceNode != null && !sequenceNode.getNodeName().equals(TAG_XSD_SEQUENCE)) {
                    sequenceNode = sequenceNode.getNextSibling();
                }

                if (sequenceNode != null) {
                    Node time_in_millis_Element = null;
                    Node key = null;
                    boolean hasUUIDElement = false;
                    boolean hasPKInfo = false;
                    Node child = sequenceNode.getFirstChild();
                    while (child != null) {
                        if (child.getAttributes() != null) {
                            Node namedItem = child.getAttributes().getNamedItem(ATTR_NAME);
                            if (namedItem != null) {
                                if (!hasUUIDElement) {
                                    if (UUID.equals(namedItem.getNodeValue())) {
                                        hasUUIDElement = true;
                                    } else if (TIME_IN_MILLIS.equals(namedItem.getNodeValue())) {
                                        time_in_millis_Element = child;
                                    }
                                }
                                if (!hasPKInfo) {
                                    if (PRIMARY_KEY_INFO.equals(namedItem.getNodeValue())) {
                                        hasPKInfo = true;
                                    } else if (KEY.equals(namedItem.getNodeValue())) {
                                        key = child;
                                    }
                                }
                            }
                        }
                        child = child.getNextSibling();
                    }

                    if (!hasUUIDElement) {
                        Element element = createElement(document, UUID, "false");
                        modified = true;
                        if (time_in_millis_Element != null) {
                            sequenceNode.insertBefore(element, time_in_millis_Element.getNextSibling());
                        } else {
                            sequenceNode.appendChild(element);
                        }
                    }
                    if (!hasPKInfo) {
                        Element element = createElement(document, PRIMARY_KEY_INFO, "true");
                        modified = true;
                        if (KEY != null) {
                            sequenceNode.insertBefore(element, key.getNextSibling());
                        } else {
                            sequenceNode.appendChild(element);
                        }
                    }
                }
            }
        }

        return modified;
    }

    private Element createElement(Document document, String nameValue, String nillable) {
        Element idElement = document.createElement(TAG_XSD_ELEMENT);
        idElement.setAttribute("maxOccurs", "1");
        idElement.setAttribute("minOccurs", "1");
        idElement.setAttribute("name", nameValue);
        idElement.setAttribute("nillable", nillable);
        idElement.setAttribute("type", "xsd:string");

        return idElement;
    }

}
