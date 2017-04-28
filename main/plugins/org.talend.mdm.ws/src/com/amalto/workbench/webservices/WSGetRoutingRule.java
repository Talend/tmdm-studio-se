
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSGetRoutingRule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSGetRoutingRule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsRoutingRulePK" type="{http://www.talend.com/mdm}WSRoutingRulePK" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSGetRoutingRule", propOrder = {
    "wsRoutingRulePK"
})
public class WSGetRoutingRule {

    protected WSRoutingRulePK wsRoutingRulePK;

    /**
     * Default no-arg constructor
     * 
     */
    public WSGetRoutingRule() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public WSGetRoutingRule(final WSRoutingRulePK wsRoutingRulePK) {
        this.wsRoutingRulePK = wsRoutingRulePK;
    }

    /**
     * Gets the value of the wsRoutingRulePK property.
     * 
     * @return
     *     possible object is
     *     {@link WSRoutingRulePK }
     *     
     */
    public WSRoutingRulePK getWsRoutingRulePK() {
        return wsRoutingRulePK;
    }

    /**
     * Sets the value of the wsRoutingRulePK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSRoutingRulePK }
     *     
     */
    public void setWsRoutingRulePK(WSRoutingRulePK value) {
        this.wsRoutingRulePK = value;
    }

}
