
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSRoutingRuleOperator.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WSRoutingRuleOperator">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CONTAINS"/>
 *     &lt;enumeration value="MATCHES"/>
 *     &lt;enumeration value="STARTSWITH"/>
 *     &lt;enumeration value="EQUALS"/>
 *     &lt;enumeration value="NOT_EQUALS"/>
 *     &lt;enumeration value="GREATER_THAN"/>
 *     &lt;enumeration value="GREATER_THAN_OR_EQUAL"/>
 *     &lt;enumeration value="LOWER_THAN"/>
 *     &lt;enumeration value="LOWER_THAN_OR_EQUAL"/>
 *     &lt;enumeration value="IS_NULL"/>
 *     &lt;enumeration value="IS_NOT_NULL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WSRoutingRuleOperator")
@XmlEnum
public enum WSRoutingRuleOperator {

    CONTAINS,
    MATCHES,
    STARTSWITH,
    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL,
    LOWER_THAN,
    LOWER_THAN_OR_EQUAL,
    IS_NULL,
    IS_NOT_NULL;

    public String value() {
        return name();
    }

    public static WSRoutingRuleOperator fromValue(String v) {
        return valueOf(v);
    }

}
