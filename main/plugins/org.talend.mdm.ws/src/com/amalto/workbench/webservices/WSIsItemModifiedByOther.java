
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSIsItemModifiedByOther complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSIsItemModifiedByOther">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsItem" type="{http://www.talend.com/mdm}WSItem" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSIsItemModifiedByOther", propOrder = {
    "wsItem"
})
public class WSIsItemModifiedByOther {

    protected WSItem wsItem;

    /**
     * Default no-arg constructor
     * 
     */
    public WSIsItemModifiedByOther() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public WSIsItemModifiedByOther(final WSItem wsItem) {
        this.wsItem = wsItem;
    }

    /**
     * Gets the value of the wsItem property.
     * 
     * @return
     *     possible object is
     *     {@link WSItem }
     *     
     */
    public WSItem getWsItem() {
        return wsItem;
    }

    /**
     * Sets the value of the wsItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSItem }
     *     
     */
    public void setWsItem(WSItem value) {
        this.wsItem = value;
    }

}
