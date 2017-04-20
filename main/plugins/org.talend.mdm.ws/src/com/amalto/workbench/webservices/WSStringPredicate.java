
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WSStringPredicate.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WSStringPredicate">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NONE"/>
 *     &lt;enumeration value="OR"/>
 *     &lt;enumeration value="AND"/>
 *     &lt;enumeration value="STRICTAND"/>
 *     &lt;enumeration value="EXACTLY"/>
 *     &lt;enumeration value="NOT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WSStringPredicate")
@XmlEnum
public enum WSStringPredicate {

    NONE,
    OR,
    AND,
    STRICTAND,
    EXACTLY,
    NOT;

    public String value() {
        return name();
    }

    public static WSStringPredicate fromValue(String v) {
        return valueOf(v);
    }

}
