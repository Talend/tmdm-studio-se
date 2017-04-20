
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSGetItemPKsByFullCriteria complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSGetItemPKsByFullCriteria">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="useFTSearch" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="wsGetItemPKsByCriteria" type="{http://www.talend.com/mdm}WSGetItemPKsByCriteria" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSGetItemPKsByFullCriteria", propOrder = {
    "useFTSearch",
    "wsGetItemPKsByCriteria"
})
public class WSGetItemPKsByFullCriteria {

    protected boolean useFTSearch;
    protected WSGetItemPKsByCriteria wsGetItemPKsByCriteria;

    /**
     * Default no-arg constructor
     * 
     */
    public WSGetItemPKsByFullCriteria() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public WSGetItemPKsByFullCriteria(final boolean useFTSearch, final WSGetItemPKsByCriteria wsGetItemPKsByCriteria) {
        this.useFTSearch = useFTSearch;
        this.wsGetItemPKsByCriteria = wsGetItemPKsByCriteria;
    }

    /**
     * Gets the value of the useFTSearch property.
     * 
     */
    public boolean isUseFTSearch() {
        return useFTSearch;
    }

    /**
     * Sets the value of the useFTSearch property.
     * 
     */
    public void setUseFTSearch(boolean value) {
        this.useFTSearch = value;
    }

    /**
     * Gets the value of the wsGetItemPKsByCriteria property.
     * 
     * @return
     *     possible object is
     *     {@link WSGetItemPKsByCriteria }
     *     
     */
    public WSGetItemPKsByCriteria getWsGetItemPKsByCriteria() {
        return wsGetItemPKsByCriteria;
    }

    /**
     * Sets the value of the wsGetItemPKsByCriteria property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSGetItemPKsByCriteria }
     *     
     */
    public void setWsGetItemPKsByCriteria(WSGetItemPKsByCriteria value) {
        this.wsGetItemPKsByCriteria = value;
    }

}
