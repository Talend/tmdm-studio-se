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

import javax.xml.XMLConstants;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.amalto.workbench.utils.IXMLConstants;
import com.amalto.workbench.utils.Util;


/**
 * created by liusongbo on Apr 14, 2016
 *
 */
public class DatamodelOperatorUpdator implements IOperatorUpdator {

    private static Logger log = Logger.getLogger(DatamodelOperatorUpdator.class);

    @Override
    public boolean updateConditionOperator(Item item) {
        boolean modified = false;
        if(item != null && item instanceof WSDataModelItem) {
            
            WSDataModelItem modelItem = (WSDataModelItem) item;
            EList<ReferenceFileItem> resources = modelItem.getReferenceResources();
            for (ReferenceFileItem fileItem : resources) {
                if (fileItem.getExtension().equals("xsd")) { //$NON-NLS-1$
                    ByteArray content = fileItem.getContent();
                    byte[] byteContent = content.getInnerContent();
                    String xsdSchema = updateOperator(byteContent);
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

        return modified;
    }

    private String updateOperator(byte[] byteContent) {
        String result = null;
        if (byteContent != null) {

            boolean modified = false;
            try {
                DocumentBuilder documentBuilder = getDocumentBuilder();
                InputSource source = new InputSource(new ByteArrayInputStream(byteContent));
                Document document = documentBuilder.parse(source);
                NodeList appinfoTags = document.getElementsByTagName("xsd:appinfo"); //$NON-NLS-1$
                int len = appinfoTags.getLength();
                for (int i = 0; i < len; i++) {
                    Node appItem = appinfoTags.item(i);
                    Node sourceAttr = appItem.getAttributes().getNamedItem("source"); //$NON-NLS-1$
                    if (sourceAttr.getNodeValue().equals("X_ForeignKey_Filter")) { //$NON-NLS-1$
                        String content = appItem.getTextContent();
                        if (content.contains("Strict Contains") || content.contains("Contains Text Of")) { //$NON-NLS-1$//$NON-NLS-2$
                            content = content.replaceAll("Contains Text Of", "Contains"); //$NON-NLS-1$ //$NON-NLS-2$
                            content = content.replaceAll("Strict Contains", "Contains"); //$NON-NLS-1$ //$NON-NLS-2$
                            appItem.setTextContent(content);

                            modified = true;
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

    private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        documentBuilderFactory.setFeature(IXMLConstants.DISALLOW_DOCTYPE_DECL, true);
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setValidating(false);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder;
    }
}
