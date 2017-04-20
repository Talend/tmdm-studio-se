
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSCheckServiceConfigResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSCheckServiceConfigResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="checkResult" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSCheckServiceConfigResponse", propOrder = {
    "checkResult"
})
public class WSCheckServiceConfigResponse {

    protected Boolean checkResult;

    /**
     * Default no-arg constructor
     * 
     */
    public WSCheckServiceConfigResponse() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public WSCheckServiceConfigResponse(final Boolean checkResult) {
        this.checkResult = checkResult;
    }

    /**
     * Gets the value of the checkResult property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCheckResult() {
        return checkResult;
    }

    /**
     * Sets the value of the checkResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCheckResult(Boolean value) {
        this.checkResult = value;
    }

}
