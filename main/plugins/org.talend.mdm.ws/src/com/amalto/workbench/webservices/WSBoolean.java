
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSBoolean complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSBoolean">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="_true" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSBoolean", propOrder = {
    "_true"
})
public class WSBoolean {

    protected boolean _true;

    /**
     * Default no-arg constructor
     * 
     */
    public WSBoolean() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public WSBoolean(final boolean _true) {
        this._true = _true;
    }

    /**
     * Gets the value of the true property.
     * 
     */
    public boolean isTrue() {
        return _true;
    }

    /**
     * Sets the value of the true property.
     * 
     */
    public void setTrue(boolean value) {
        this._true = value;
    }

}
