
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for processFileUsingTransformerResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="processFileUsingTransformerResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://www.talend.com/mdm}WSPipeline" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "processFileUsingTransformerResponse", propOrder = {
    "_return"
})
public class ProcessFileUsingTransformerResponse {

    @XmlElement(name = "return")
    protected WSPipeline _return;

    /**
     * Default no-arg constructor
     * 
     */
    public ProcessFileUsingTransformerResponse() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public ProcessFileUsingTransformerResponse(final WSPipeline _return) {
        this._return = _return;
    }

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link WSPipeline }
     *     
     */
    public WSPipeline getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSPipeline }
     *     
     */
    public void setReturn(WSPipeline value) {
        this._return = value;
    }

}
