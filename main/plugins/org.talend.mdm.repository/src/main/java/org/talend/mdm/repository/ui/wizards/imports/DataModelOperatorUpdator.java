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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.workbench.utils.Util;


public class DataModelOperatorUpdator extends AbstractDataModelUpdator implements IOperatorUpdator {

    private static final Logger LOG = Logger.getLogger(DataModelOperatorUpdator.class);

    @Override
    public boolean updateConditionOperator(Item item) {
        return updateDatamodel(item);
    }

    @Override
    protected String doUpdate(byte[] byteContent) {
        return updateOperator(byteContent);
    }

    @Override
    protected boolean accept(Item item) {
        String path = item.getState().getPath();
        return !path.toLowerCase().startsWith("system"); //$NON-NLS-1$
    }

    private String updateOperator(byte[] byteContent) {
        String result = null;
        if (byteContent != null) {

            boolean modified = false;
            try {
                Document document = getSchemaDocument(byteContent);
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
                LOG.error("Update data model condition operator failed", e); //$NON-NLS-1$
            }
        }

        return result;
    }
}
