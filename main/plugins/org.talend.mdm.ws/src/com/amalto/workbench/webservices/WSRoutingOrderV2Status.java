
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSRoutingOrderV2Status.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WSRoutingOrderV2Status">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ACTIVE"/>
 *     &lt;enumeration value="FAILED"/>
 *     &lt;enumeration value="COMPLETED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WSRoutingOrderV2Status")
@XmlEnum
public enum WSRoutingOrderV2Status {

    ACTIVE,
    FAILED,
    COMPLETED;

    public String value() {
        return name();
    }

    public static WSRoutingOrderV2Status fromValue(String v) {
        return valueOf(v);
    }

}
