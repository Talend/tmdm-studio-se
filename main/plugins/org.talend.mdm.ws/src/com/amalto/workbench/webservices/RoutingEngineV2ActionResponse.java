
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for routingEngineV2ActionResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="routingEngineV2ActionResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://www.talend.com/mdm}WSRoutingEngineV2Status" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "routingEngineV2ActionResponse", propOrder = {
    "_return"
})
public class RoutingEngineV2ActionResponse {

    @XmlElement(name = "return")
    protected WSRoutingEngineV2Status _return;

    /**
     * Default no-arg constructor
     * 
     */
    public RoutingEngineV2ActionResponse() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public RoutingEngineV2ActionResponse(final WSRoutingEngineV2Status _return) {
        this._return = _return;
    }

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link WSRoutingEngineV2Status }
     *     
     */
    public WSRoutingEngineV2Status getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSRoutingEngineV2Status }
     *     
     */
    public void setReturn(WSRoutingEngineV2Status value) {
        this._return = value;
    }

}
