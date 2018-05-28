
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSPutItemWithReport complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPutItemWithReport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="invokeBeforeSaving" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="messageType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="source" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="warningApprovedBeforeSave" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="wsPutItem" type="{http://www.talend.com/mdm}WSPutItem" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSPutItemWithReport", propOrder = {
    "invokeBeforeSaving",
    "source",
    "wsPutItem"
})
public class WSPutItemWithReport {

    protected Boolean invokeBeforeSaving;

    protected String message;

    protected String messageType;

    protected String source;

    protected Boolean warningApprovedBeforeSave = false;

    protected WSPutItem wsPutItem;

    /**
     * Default no-arg constructor
     * 
     */
    public WSPutItemWithReport() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public WSPutItemWithReport(final Boolean invokeBeforeSaving, final String source, final WSPutItem wsPutItem) {
        this.invokeBeforeSaving = invokeBeforeSaving;
        this.source = source;
        this.wsPutItem = wsPutItem;
    }

    /**
     * Gets the value of the invokeBeforeSaving property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     * 
     */
    public Boolean isInvokeBeforeSaving() {
        return invokeBeforeSaving;
    }

    /**
     * Sets the value of the invokeBeforeSaving property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     * 
     */
    public void setInvokeBeforeSaving(Boolean value) {
        this.invokeBeforeSaving = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     * 
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     * 
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the messageType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     * 
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * Sets the value of the messageType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     * 
     */
    public void setMessageType(String value) {
        this.messageType = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     * 
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     * 
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Gets the value of the warningApprovedBeforeSave property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     * 
     */
    public Boolean isWarningApprovedBeforeSave() {
        return warningApprovedBeforeSave;
    }

    /**
     * Sets the value of the warningApprovedBeforeSave property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     * 
     */
    public void setWarningApprovedBeforeSave(Boolean value) {
        this.warningApprovedBeforeSave = value;
    }

    /**
     * Gets the value of the wsPutItem property.
     * 
     * @return
     *     possible object is
     *     {@link WSPutItem }
     * 
     */
    public WSPutItem getWsPutItem() {
        return wsPutItem;
    }

    /**
     * Sets the value of the wsPutItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSPutItem }
     * 
     */
    public void setWsPutItem(WSPutItem value) {
        this.wsPutItem = value;
    }

}
