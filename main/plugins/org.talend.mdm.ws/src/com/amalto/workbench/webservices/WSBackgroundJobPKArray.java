
package com.amalto.workbench.webservices;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSBackgroundJobPKArray complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSBackgroundJobPKArray">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wsBackgroundJobPK" type="{http://www.talend.com/mdm}WSBackgroundJobPK" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSBackgroundJobPKArray", propOrder = {
    "wsBackgroundJobPK"
})
public class WSBackgroundJobPKArray {

    @XmlElement(nillable = true)
    protected List<WSBackgroundJobPK> wsBackgroundJobPK;

    /**
     * Default no-arg constructor
     * 
     */
    public WSBackgroundJobPKArray() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public WSBackgroundJobPKArray(final List<WSBackgroundJobPK> wsBackgroundJobPK) {
        this.wsBackgroundJobPK = wsBackgroundJobPK;
    }

    /**
     * Gets the value of the wsBackgroundJobPK property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wsBackgroundJobPK property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWsBackgroundJobPK().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSBackgroundJobPK }
     * 
     * 
     */
    public List<WSBackgroundJobPK> getWsBackgroundJobPK() {
        if (wsBackgroundJobPK == null) {
            wsBackgroundJobPK = new ArrayList<WSBackgroundJobPK>();
        }
        return this.wsBackgroundJobPK;
    }

}
