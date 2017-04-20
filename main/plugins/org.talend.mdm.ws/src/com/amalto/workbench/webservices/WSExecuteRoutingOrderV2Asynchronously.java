
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSExecuteRoutingOrderV2Asynchronously complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSExecuteRoutingOrderV2Asynchronously">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="routingOrderV2PK" type="{http://www.talend.com/mdm}WSRoutingOrderV2PK" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSExecuteRoutingOrderV2Asynchronously", propOrder = {
    "routingOrderV2PK"
})
public class WSExecuteRoutingOrderV2Asynchronously {

    protected WSRoutingOrderV2PK routingOrderV2PK;

    /**
     * Default no-arg constructor
     * 
     */
    public WSExecuteRoutingOrderV2Asynchronously() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public WSExecuteRoutingOrderV2Asynchronously(final WSRoutingOrderV2PK routingOrderV2PK) {
        this.routingOrderV2PK = routingOrderV2PK;
    }

    /**
     * Gets the value of the routingOrderV2PK property.
     * 
     * @return
     *     possible object is
     *     {@link WSRoutingOrderV2PK }
     *     
     */
    public WSRoutingOrderV2PK getRoutingOrderV2PK() {
        return routingOrderV2PK;
    }

    /**
     * Sets the value of the routingOrderV2PK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSRoutingOrderV2PK }
     *     
     */
    public void setRoutingOrderV2PK(WSRoutingOrderV2PK value) {
        this.routingOrderV2PK = value;
    }

}
