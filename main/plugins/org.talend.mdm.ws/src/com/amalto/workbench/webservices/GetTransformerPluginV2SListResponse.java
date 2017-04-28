
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getTransformerPluginV2SListResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getTransformerPluginV2SListResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://www.talend.com/mdm}WSTransformerPluginV2SList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getTransformerPluginV2SListResponse", propOrder = {
    "_return"
})
public class GetTransformerPluginV2SListResponse {

    @XmlElement(name = "return")
    protected WSTransformerPluginV2SList _return;

    /**
     * Default no-arg constructor
     * 
     */
    public GetTransformerPluginV2SListResponse() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public GetTransformerPluginV2SListResponse(final WSTransformerPluginV2SList _return) {
        this._return = _return;
    }

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link WSTransformerPluginV2SList }
     *     
     */
    public WSTransformerPluginV2SList getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSTransformerPluginV2SList }
     *     
     */
    public void setReturn(WSTransformerPluginV2SList value) {
        this._return = value;
    }

}
