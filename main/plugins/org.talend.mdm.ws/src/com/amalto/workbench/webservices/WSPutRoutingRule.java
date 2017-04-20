
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSPutRoutingRule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPutRoutingRule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsRoutingRule" type="{http://www.talend.com/mdm}WSRoutingRule" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSPutRoutingRule", propOrder = {
    "wsRoutingRule"
})
public class WSPutRoutingRule {

    protected WSRoutingRule wsRoutingRule;

    /**
     * Default no-arg constructor
     * 
     */
    public WSPutRoutingRule() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public WSPutRoutingRule(final WSRoutingRule wsRoutingRule) {
        this.wsRoutingRule = wsRoutingRule;
    }

    /**
     * Gets the value of the wsRoutingRule property.
     * 
     * @return
     *     possible object is
     *     {@link WSRoutingRule }
     *     
     */
    public WSRoutingRule getWsRoutingRule() {
        return wsRoutingRule;
    }

    /**
     * Sets the value of the wsRoutingRule property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSRoutingRule }
     *     
     */
    public void setWsRoutingRule(WSRoutingRule value) {
        this.wsRoutingRule = value;
    }

}
