package org.apache.xml.security.configuration;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/configuration/InOutAttrType.class */
@XmlType(name = "inOutAttrType", namespace = "http://www.xmlsecurity.org/NS/configuration")
@XmlEnum
public enum InOutAttrType {
    IN,
    OUT;

    public String value() {
        return name();
    }

    public static InOutAttrType fromValue(String v) {
        return valueOf(v);
    }
}
