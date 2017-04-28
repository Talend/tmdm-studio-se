
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSPutDataCluster complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSPutDataCluster">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsDataCluster" type="{http://www.talend.com/mdm}WSDataCluster" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSPutDataCluster", propOrder = {
    "wsDataCluster"
})
public class WSPutDataCluster {

    protected WSDataCluster wsDataCluster;

    /**
     * Default no-arg constructor
     * 
     */
    public WSPutDataCluster() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public WSPutDataCluster(final WSDataCluster wsDataCluster) {
        this.wsDataCluster = wsDataCluster;
    }

    /**
     * Gets the value of the wsDataCluster property.
     * 
     * @return
     *     possible object is
     *     {@link WSDataCluster }
     *     
     */
    public WSDataCluster getWsDataCluster() {
        return wsDataCluster;
    }

    /**
     * Sets the value of the wsDataCluster property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSDataCluster }
     *     
     */
    public void setWsDataCluster(WSDataCluster value) {
        this.wsDataCluster = value;
    }

}
