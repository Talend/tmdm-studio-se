
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getDataModelPKsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDataModelPKsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://www.talend.com/mdm}WSDataModelPKArray" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDataModelPKsResponse", propOrder = {
    "_return"
})
public class GetDataModelPKsResponse {

    @XmlElement(name = "return")
    protected WSDataModelPKArray _return;

    /**
     * Default no-arg constructor
     * 
     */
    public GetDataModelPKsResponse() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public GetDataModelPKsResponse(final WSDataModelPKArray _return) {
        this._return = _return;
    }

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link WSDataModelPKArray }
     *     
     */
    public WSDataModelPKArray getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSDataModelPKArray }
     *     
     */
    public void setReturn(WSDataModelPKArray value) {
        this._return = value;
    }

}
