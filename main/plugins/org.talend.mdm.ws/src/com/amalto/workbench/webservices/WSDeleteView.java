
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSDeleteView complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSDeleteView">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsViewPK" type="{http://www.talend.com/mdm}WSViewPK" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSDeleteView", propOrder = {
    "wsViewPK"
})
public class WSDeleteView {

    protected WSViewPK wsViewPK;

    /**
     * Default no-arg constructor
     * 
     */
    public WSDeleteView() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public WSDeleteView(final WSViewPK wsViewPK) {
        this.wsViewPK = wsViewPK;
    }

    /**
     * Gets the value of the wsViewPK property.
     * 
     * @return
     *     possible object is
     *     {@link WSViewPK }
     *     
     */
    public WSViewPK getWsViewPK() {
        return wsViewPK;
    }

    /**
     * Sets the value of the wsViewPK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSViewPK }
     *     
     */
    public void setWsViewPK(WSViewPK value) {
        this.wsViewPK = value;
    }

}
