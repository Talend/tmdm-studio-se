
package com.amalto.workbench.webservices;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BackgroundJobStatusType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BackgroundJobStatusType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SCHEDULED"/>
 *     &lt;enumeration value="RUNNING"/>
 *     &lt;enumeration value="COMPLETED"/>
 *     &lt;enumeration value="SUSPENDED"/>
 *     &lt;enumeration value="CANCEL_REQUESTED"/>
 *     &lt;enumeration value="STOPPED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BackgroundJobStatusType")
@XmlEnum
public enum BackgroundJobStatusType {

    SCHEDULED,
    RUNNING,
    COMPLETED,
    SUSPENDED,
    CANCEL_REQUESTED,
    STOPPED;

    public String value() {
        return name();
    }

    public static BackgroundJobStatusType fromValue(String v) {
        return valueOf(v);
    }

}
