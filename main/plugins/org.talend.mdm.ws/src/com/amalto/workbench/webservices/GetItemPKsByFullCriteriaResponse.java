
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getItemPKsByFullCriteriaResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getItemPKsByFullCriteriaResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://www.talend.com/mdm}WSItemPKsByCriteriaResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getItemPKsByFullCriteriaResponse", propOrder = {
    "_return"
})
public class GetItemPKsByFullCriteriaResponse {

    @XmlElement(name = "return")
    protected WSItemPKsByCriteriaResponse _return;

    /**
     * Default no-arg constructor
     * 
     */
    public GetItemPKsByFullCriteriaResponse() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public GetItemPKsByFullCriteriaResponse(final WSItemPKsByCriteriaResponse _return) {
        this._return = _return;
    }

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link WSItemPKsByCriteriaResponse }
     *     
     */
    public WSItemPKsByCriteriaResponse getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSItemPKsByCriteriaResponse }
     *     
     */
    public void setReturn(WSItemPKsByCriteriaResponse value) {
        this._return = value;
    }

}
