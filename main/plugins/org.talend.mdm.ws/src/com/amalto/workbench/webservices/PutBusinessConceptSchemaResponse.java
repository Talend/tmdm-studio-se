
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for putBusinessConceptSchemaResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="putBusinessConceptSchemaResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://www.talend.com/mdm}WSString" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "putBusinessConceptSchemaResponse", propOrder = {
    "_return"
})
public class PutBusinessConceptSchemaResponse {

    @XmlElement(name = "return")
    protected WSString _return;

    /**
     * Default no-arg constructor
     * 
     */
    public PutBusinessConceptSchemaResponse() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public PutBusinessConceptSchemaResponse(final WSString _return) {
        this._return = _return;
    }

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link WSString }
     *     
     */
    public WSString getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSString }
     *     
     */
    public void setReturn(WSString value) {
        this._return = value;
    }

}
