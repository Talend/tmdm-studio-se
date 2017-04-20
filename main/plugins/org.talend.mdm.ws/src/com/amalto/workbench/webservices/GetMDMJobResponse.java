
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getMDMJobResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getMDMJobResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://www.talend.com/mdm}WSMDMJobArray" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getMDMJobResponse", propOrder = {
    "_return"
})
public class GetMDMJobResponse {

    @XmlElement(name = "return")
    protected WSMDMJobArray _return;

    /**
     * Default no-arg constructor
     * 
     */
    public GetMDMJobResponse() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public GetMDMJobResponse(final WSMDMJobArray _return) {
        this._return = _return;
    }

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link WSMDMJobArray }
     *     
     */
    public WSMDMJobArray getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSMDMJobArray }
     *     
     */
    public void setReturn(WSMDMJobArray value) {
        this._return = value;
    }

}
