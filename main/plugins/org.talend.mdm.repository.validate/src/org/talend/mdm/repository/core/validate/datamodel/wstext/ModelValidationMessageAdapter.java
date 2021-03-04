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
package org.talend.mdm.repository.core.validate.datamodel.wstext;

import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.talend.mdm.repository.core.validate.datamodel.validator.ModelValidationMessage;
import org.w3c.dom.Element;

/**
 * created by HHB on 2013-1-29 Detailled comment
 *
 */
public class ModelValidationMessageAdapter extends ValidationMessage {

    private final ModelValidationMessage msg;

    /**
     * Getter for msgGroup.
     *
     * @return the msgGroup
     */
    public int getMsgGroup() {
        return msg.getMsgGroup();
    }

    /**
     * Getter for path.
     *
     * @return the path
     */
    public String getPath() {
        return msg.getPath();
    }

    public ModelValidationMessageAdapter(ModelValidationMessage msg) {
        super(msg.getMessage(), msg.getLineNumber(), msg.getColumnNumber(), null, msg.getKey(), null);
        this.msg = msg;
        setSeverity(msg.getSeverity());
    }

    /**
     * Getter for dataModelName.
     *
     * @return the dataModelName
     */
    public String getDataModelName() {
        return msg.getName();
    }

    /**
     * Getter for domElement.
     *
     * @return the domElement
     */
    public Element getDomElement() {
        return msg.getDomElement();
    }

    /**
     * Getter for entityName.
     *
     * @return the entityName
     */
    public String getEntityName() {
        return msg.getEntityName();
    }

    /**
     * Getter for modelType.
     *
     * @return the modelType
     */
    public String getElementType() {
        return msg.getElementType();
    }

    // @Override
    // public boolean equals(Object obj) {
    // if (obj == null || !(obj instanceof ModelValidationMessageAdapter)) {
    // return false;
    // }
    // ModelValidationMessageAdapter msg = (ModelValidationMessageAdapter) obj;
    // if (dataModelName.equals(msg.getDataModelName()) && lineNumber == msg.getLineNumber()
    // && domElement.equals(msg.getDomElement())) {
    // if (key == null && msg.getKey() == null) {
    // return true;
    // }
    // if (key != null && msg.getKey() != null && getKey().equals(msg.getKey())) {
    // return true;
    // }
    // }
    // return false;
    // }

}
