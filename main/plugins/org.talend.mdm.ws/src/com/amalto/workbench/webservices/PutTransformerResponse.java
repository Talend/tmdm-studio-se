
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for putTransformerResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="putTransformerResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://www.talend.com/mdm}WSTransformerPK" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "putTransformerResponse", propOrder = {
    "_return"
})
public class PutTransformerResponse {

    @XmlElement(name = "return")
    protected WSTransformerPK _return;

    /**
     * Default no-arg constructor
     * 
     */
    public PutTransformerResponse() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public PutTransformerResponse(final WSTransformerPK _return) {
        this._return = _return;
    }

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link WSTransformerPK }
     *     
     */
    public WSTransformerPK getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSTransformerPK }
     *     
     */
    public void setReturn(WSTransformerPK value) {
        this._return = value;
    }

}
