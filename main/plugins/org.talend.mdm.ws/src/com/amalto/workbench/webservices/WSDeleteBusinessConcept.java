
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSDeleteBusinessConcept complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSDeleteBusinessConcept">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="businessConceptName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="wsDataModelPK" type="{http://www.talend.com/mdm}WSDataModelPK" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSDeleteBusinessConcept", propOrder = {
    "businessConceptName",
    "wsDataModelPK"
})
public class WSDeleteBusinessConcept {

    protected String businessConceptName;
    protected WSDataModelPK wsDataModelPK;

    /**
     * Default no-arg constructor
     * 
     */
    public WSDeleteBusinessConcept() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public WSDeleteBusinessConcept(final String businessConceptName, final WSDataModelPK wsDataModelPK) {
        this.businessConceptName = businessConceptName;
        this.wsDataModelPK = wsDataModelPK;
    }

    /**
     * Gets the value of the businessConceptName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessConceptName() {
        return businessConceptName;
    }

    /**
     * Sets the value of the businessConceptName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessConceptName(String value) {
        this.businessConceptName = value;
    }

    /**
     * Gets the value of the wsDataModelPK property.
     * 
     * @return
     *     possible object is
     *     {@link WSDataModelPK }
     *     
     */
    public WSDataModelPK getWsDataModelPK() {
        return wsDataModelPK;
    }

    /**
     * Sets the value of the wsDataModelPK property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSDataModelPK }
     *     
     */
    public void setWsDataModelPK(WSDataModelPK value) {
        this.wsDataModelPK = value;
    }

}
