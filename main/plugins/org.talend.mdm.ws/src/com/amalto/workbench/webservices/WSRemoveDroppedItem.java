
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSRemoveDroppedItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSRemoveDroppedItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsDroppedItemPK" type="{http://www.talend.com/mdm}WSDroppedItemPK" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSRemoveDroppedItem", propOrder = {
    "wsDroppedItemPK"
})
public class WSRemoveDroppedItem {

    protected WSDroppedItemPK wsDroppedItemPK;

    /**
     * Default no-arg constructor
     * 
     */
    public WSRemoveDroppedItem() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public WSRemoveDroppedItem(final WSDroppedItemPK wsDroppedItemPK) {
        this.wsDroppedItemPK = wsDroppedItemPK;
    }

    /**
     * Gets the value of the wsDroppedItemPK property.
     * 
     * @return
     *     possible object is
     *     {@link WSDroppedItemPK }
     *     
     */
    public WSDroppedItemPK getWsDroppedItemPK() {
        return wsDroppedItemPK;
    }

    /**
     * Sets the value of the wsDroppedItemPK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSDroppedItemPK }
     *     
     */
    public void setWsDroppedItemPK(WSDroppedItemPK value) {
        this.wsDroppedItemPK = value;
    }

}
