
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fkIntegrityCheckResult.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="fkIntegrityCheckResult">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="FORBIDDEN"/>
 *     &lt;enumeration value="FORBIDDEN_OVERRIDE_ALLOWED"/>
 *     &lt;enumeration value="ALLOWED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "fkIntegrityCheckResult")
@XmlEnum
public enum FkIntegrityCheckResult {

    FORBIDDEN,
    FORBIDDEN_OVERRIDE_ALLOWED,
    ALLOWED;

    public String value() {
        return name();
    }

    public static FkIntegrityCheckResult fromValue(String v) {
        return valueOf(v);
    }

}
