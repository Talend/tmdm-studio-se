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
package org.talend.mdm.repository.core.validate;

/**
 * created by HHB on 2013-1-28 Detailled comment
 *
 */
public class ValidationMessage {

    protected int columnNumber;

    /**
     * DOC HHB ValidationMessage constructor comment.
     *
     * @param severity
     * @param message
     * @param key
     * @param name
     * @param lineNumber
     * @param columnNumber
     * @param msgGroup
     */
    public ValidationMessage(int severity, String message, String key, String name, int lineNumber, int columnNumber, int msgGroup) {
        super();
        this.severity = severity;
        this.message = message;
        this.key = key;
        this.name = name;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.msgGroup = msgGroup;
    }

    public ValidationMessage() {
    }

    protected String key;

    protected int lineNumber;

    protected String message;

    protected int msgGroup;

    protected String name;

    protected int severity;

    /**
     * Getter for columnNumber.
     *
     * @return the columnNumber
     */
    public int getColumnNumber() {
        return this.columnNumber;
    }

    /**
     * Getter for key.
     *
     * @return the key
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Getter for lineNumber.
     *
     * @return the lineNumber
     */
    public int getLineNumber() {
        return this.lineNumber;
    }

    /**
     * Getter for message.
     *
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Getter for msgGroup.
     *
     * @return the msgGroup
     */
    public int getMsgGroup() {
        return this.msgGroup;
    }

    /**
     * Getter for name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for severity.
     *
     * @return the severity
     */
    public int getSeverity() {
        return this.severity;
    }

    /**
     * Sets the columnNumber.
     *
     * @param columnNumber the columnNumber to set
     */
    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    /**
     * Sets the key.
     *
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Sets the lineNumber.
     *
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Sets the message.
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets the msgGroup.
     *
     * @param msgGroup the msgGroup to set
     */
    public void setMsgGroup(int msgGroup) {
        this.msgGroup = msgGroup;
    }

    /**
     * Sets the name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the severity.
     *
     * @param severity the severity to set
     */
    public void setSeverity(int severity) {
        this.severity = severity;
    }
}
