
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSRoutingEngineV2Status.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WSRoutingEngineV2Status">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DEAD"/>
 *     &lt;enumeration value="STOPPED"/>
 *     &lt;enumeration value="SUSPENDED"/>
 *     &lt;enumeration value="RUNNING"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WSRoutingEngineV2Status")
@XmlEnum
public enum WSRoutingEngineV2Status {

    DEAD,
    STOPPED,
    SUSPENDED,
    RUNNING;

    public String value() {
        return name();
    }

    public static WSRoutingEngineV2Status fromValue(String v) {
        return valueOf(v);
    }

}
