
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSPutStoredProcedure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPutStoredProcedure">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsStoredProcedure" type="{http://www.talend.com/mdm}WSStoredProcedure" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSPutStoredProcedure", propOrder = {
    "wsStoredProcedure"
})
public class WSPutStoredProcedure {

    protected WSStoredProcedure wsStoredProcedure;

    /**
     * Default no-arg constructor
     * 
     */
    public WSPutStoredProcedure() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public WSPutStoredProcedure(final WSStoredProcedure wsStoredProcedure) {
        this.wsStoredProcedure = wsStoredProcedure;
    }

    /**
     * Gets the value of the wsStoredProcedure property.
     * 
     * @return
     *     possible object is
     *     {@link WSStoredProcedure }
     *     
     */
    public WSStoredProcedure getWsStoredProcedure() {
        return wsStoredProcedure;
    }

    /**
     * Sets the value of the wsStoredProcedure property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSStoredProcedure }
     *     
     */
    public void setWsStoredProcedure(WSStoredProcedure value) {
        this.wsStoredProcedure = value;
    }

}
