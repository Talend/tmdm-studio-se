
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dropItemResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dropItemResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://www.talend.com/mdm}WSDroppedItemPK" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dropItemResponse", propOrder = {
    "_return"
})
public class DropItemResponse {

    @XmlElement(name = "return")
    protected WSDroppedItemPK _return;

    /**
     * Default no-arg constructor
     * 
     */
    public DropItemResponse() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public DropItemResponse(final WSDroppedItemPK _return) {
        this._return = _return;
    }

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link WSDroppedItemPK }
     *     
     */
    public WSDroppedItemPK getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSDroppedItemPK }
     *     
     */
    public void setReturn(WSDroppedItemPK value) {
        this._return = value;
    }

}
