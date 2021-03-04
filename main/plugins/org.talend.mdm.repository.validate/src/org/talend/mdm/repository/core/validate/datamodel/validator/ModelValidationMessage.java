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
package org.talend.mdm.repository.core.validate.datamodel.validator;

import org.talend.mdm.repository.core.validate.ValidationMessage;
import org.w3c.dom.Element;

/**
 * created by HHB on 2013-1-28 Detailled comment
 *
 */
public class ModelValidationMessage extends ValidationMessage {

    private Element domElement;

    private String entityName;

    private String elementType;

    private String path;

    public ModelValidationMessage(int severity, String message, String key, String name, int lineNumber, int columnNumber,
            int msgGroup, Element domElement, String entityName, String elementType, String path) {
        super(severity, message, key, name, lineNumber, columnNumber, msgGroup);
        this.domElement = domElement;
        this.entityName = entityName;
        this.elementType = elementType;
        this.path = path;

    }

    public ModelValidationMessage() {

    }

    /**
     * Getter for path.
     *
     * @return the path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Sets the path.
     *
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Getter for domElement.
     *
     * @return the domElement
     */
    public Element getDomElement() {
        return this.domElement;
    }

    /**
     * Getter for entityName.
     *
     * @return the entityName
     */
    public String getEntityName() {
        return this.entityName;
    }

    /**
     * Getter for modelType.
     *
     * @return the modelType
     */
    public String getElementType() {
        return this.elementType;
    }

    /**
     * Sets the domElement.
     *
     * @param domElement the domElement to set
     */
    public void setDomElement(Element domElement) {
        this.domElement = domElement;
    }

    /**
     * Sets the entityName.
     *
     * @param entityName the entityName to set
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * Sets the modelType.
     *
     * @param elementType the modelType to set
     */
    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ModelValidationMessage)) {
            return false;
        }
        ModelValidationMessage msg = (ModelValidationMessage) obj;
        if (name.equals(msg.getName()) && lineNumber == msg.getLineNumber() && domElement.equals(msg.getDomElement())) {
            if (key == null && msg.getKey() == null) {
                return true;
            }
            if (key != null && msg.getKey() != null && getKey().equals(msg.getKey())) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = name.hashCode() * 7 + (key != null ? key.hashCode() * 17 : 17) + lineNumber * 31 + domElement.hashCode() * 37;
        return hash;
    }

}
