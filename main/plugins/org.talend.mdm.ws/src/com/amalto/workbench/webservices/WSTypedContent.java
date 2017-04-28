
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSTypedContent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSTypedContent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="contentType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="wsBytes" type="{http://www.talend.com/mdm}WSByteArray" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSTypedContent", propOrder = {
    "contentType",
    "url",
    "wsBytes"
})
public class WSTypedContent {

    protected String contentType;
    protected String url;
    protected WSByteArray wsBytes;

    /**
     * Default no-arg constructor
     * 
     */
    public WSTypedContent() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public WSTypedContent(final String contentType, final String url, final WSByteArray wsBytes) {
        this.contentType = contentType;
        this.url = url;
        this.wsBytes = wsBytes;
    }

    /**
     * Gets the value of the contentType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the value of the contentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentType(String value) {
        this.contentType = value;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the wsBytes property.
     * 
     * @return
     *     possible object is
     *     {@link WSByteArray }
     *     
     */
    public WSByteArray getWsBytes() {
        return wsBytes;
    }

    /**
     * Sets the value of the wsBytes property.
     * 
     * @param value
     *     allowed object is
     *     {@link WSByteArray }
     *     
     */
    public void setWsBytes(WSByteArray value) {
        this.wsBytes = value;
    }

}
