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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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

import com.amalto.workbench.utils.IXMLConstants;

public abstract class AbstractDataModelUpdator {

    private static final Logger LOG = Logger.getLogger(AbstractDataModelUpdator.class);

    public boolean updateDatamodel(Item item) {
        boolean modified = false;
        if (item != null && item instanceof WSDataModelItem && accept(item)) {
            WSDataModelItem modelItem = (WSDataModelItem) item;
            EList<ReferenceFileItem> resources = modelItem.getReferenceResources();
            for (ReferenceFileItem fileItem : resources) {
                if (fileItem.getExtension().equals("xsd")) { //$NON-NLS-1$
                    ByteArray content = fileItem.getContent();
                    String xsdSchema = doUpdate(content.getInnerContent());
                    if (xsdSchema != null) {
                        try {
                            byte[] byteContent = xsdSchema.getBytes("utf-8"); //$NON-NLS-1$
                            content.setInnerContent(byteContent);
                            modelItem.getWsDataModel().setXsdSchema(new String(byteContent, "utf-8")); //$NON-NLS-1$
                        } catch (UnsupportedEncodingException e) {
                            LOG.error("Encodes/Decodes string by 'utf-8' encoding failed", e); //$NON-NLS-1$
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

    protected abstract boolean accept(Item item);

    protected abstract String doUpdate(byte[] byteContent);

    protected Document getSchemaDocument(byte[] byteContent) throws ParserConfigurationException {
        DocumentBuilder documentBuilder = getDocumentBuilder();

        Document document = null;
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(byteContent);
            document = documentBuilder.parse(inputStream);
        } catch (Exception e) {
            LOG.error("Parse input stream error", e); //$NON-NLS-1$
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    LOG.error("Close input stream error", e); //$NON-NLS-1$
                }
            }
        }

        return document;
    }

    protected DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        documentBuilderFactory.setFeature(IXMLConstants.DISALLOW_DOCTYPE_DECL, true);
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setValidating(false);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder;
    }

}
