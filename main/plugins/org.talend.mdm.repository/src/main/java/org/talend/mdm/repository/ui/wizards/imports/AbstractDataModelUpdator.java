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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.amalto.workbench.utils.IXMLConstants;

/**
 * @author sbliu
 *
 */
public abstract class AbstractDataModelUpdator {

    private static Logger log = Logger.getLogger(AbstractDataModelUpdator.class);

    public boolean updateDatamodel(Item item) {
        boolean modified = false;
        if (item != null && item instanceof WSDataModelItem && accept(item)) {
            WSDataModelItem modelItem = (WSDataModelItem) item;
            EList<ReferenceFileItem> resources = modelItem.getReferenceResources();
            for (ReferenceFileItem fileItem : resources) {
                if (fileItem.getExtension().equals("xsd")) { //$NON-NLS-1$
                    ByteArray content = fileItem.getContent();
                    String xsdSchema = doUpdation(content.getInnerContent());
                    if (xsdSchema != null) {
                        try {
                            byte[] byteContent = xsdSchema.getBytes("utf-8"); //$NON-NLS-1$
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

    protected boolean accept(Item item) {
        return true;
    }

    protected abstract String doUpdation(byte[] byteContent);

    protected Document getSchemaDocument(byte[] byteContent) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        documentBuilderFactory.setFeature(IXMLConstants.DISALLOW_DOCTYPE_DECL, true);
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setValidating(false);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        InputSource source = new InputSource(new ByteArrayInputStream(byteContent));
        Document document = documentBuilder.parse(source);
        return document;
    }

}
