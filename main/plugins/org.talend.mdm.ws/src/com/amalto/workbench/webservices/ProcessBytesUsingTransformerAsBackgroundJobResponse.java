
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for processBytesUsingTransformerAsBackgroundJobResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="processBytesUsingTransformerAsBackgroundJobResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://www.talend.com/mdm}WSBackgroundJobPK" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "processBytesUsingTransformerAsBackgroundJobResponse", propOrder = {
    "_return"
})
public class ProcessBytesUsingTransformerAsBackgroundJobResponse {

    @XmlElement(name = "return")
    protected WSBackgroundJobPK _return;

    /**
     * Default no-arg constructor
     * 
     */
    public ProcessBytesUsingTransformerAsBackgroundJobResponse() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public ProcessBytesUsingTransformerAsBackgroundJobResponse(final WSBackgroundJobPK _return) {
        this._return = _return;
    }

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link WSBackgroundJobPK }
     *     
     */
    public WSBackgroundJobPK getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSBackgroundJobPK }
     *     
     */
    public void setReturn(WSBackgroundJobPK value) {
        this._return = value;
    }

}
