
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for putItemWithReportArrayResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="putItemWithReportArrayResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://www.talend.com/mdm}WSItemPKArray" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "putItemWithReportArrayResponse", propOrder = {
    "_return"
})
public class PutItemWithReportArrayResponse {

    @XmlElement(name = "return")
    protected WSItemPKArray _return;

    /**
     * Default no-arg constructor
     * 
     */
    public PutItemWithReportArrayResponse() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public PutItemWithReportArrayResponse(final WSItemPKArray _return) {
        this._return = _return;
    }

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link WSItemPKArray }
     *     
     */
    public WSItemPKArray getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSItemPKArray }
     *     
     */
    public void setReturn(WSItemPKArray value) {
        this._return = value;
    }

}
