
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSRoutingEngineV2ActionCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WSRoutingEngineV2ActionCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="START"/>
 *     &lt;enumeration value="STOP"/>
 *     &lt;enumeration value="SUSPEND"/>
 *     &lt;enumeration value="RESUME"/>
 *     &lt;enumeration value="STATUS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WSRoutingEngineV2ActionCode")
@XmlEnum
public enum WSRoutingEngineV2ActionCode {

    START,
    STOP,
    SUSPEND,
    RESUME,
    STATUS;

    public String value() {
        return name();
    }

    public static WSRoutingEngineV2ActionCode fromValue(String v) {
        return valueOf(v);
    }

}
