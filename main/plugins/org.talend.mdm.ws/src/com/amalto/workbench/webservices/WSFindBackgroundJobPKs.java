
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSFindBackgroundJobPKs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSFindBackgroundJobPKs">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="status" type="{http://www.talend.com/mdm}BackgroundJobStatusType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSFindBackgroundJobPKs", propOrder = {
    "status"
})
public class WSFindBackgroundJobPKs {

    protected BackgroundJobStatusType status;

    /**
     * Default no-arg constructor
     * 
     */
    public WSFindBackgroundJobPKs() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public WSFindBackgroundJobPKs(final BackgroundJobStatusType status) {
        this.status = status;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link BackgroundJobStatusType }
     *     
     */
    public BackgroundJobStatusType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link BackgroundJobStatusType }
     *     
     */
    public void setStatus(BackgroundJobStatusType value) {
        this.status = value;
    }

}
