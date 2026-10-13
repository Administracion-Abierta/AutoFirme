package org.apache.xml.security.configuration;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import org.mozilla.javascript.ES6Iterator;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/configuration/PropertyType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyType", namespace = "http://www.xmlsecurity.org/NS/configuration", propOrder = {ES6Iterator.VALUE_PROPERTY})
public class PropertyType {

    @XmlValue
    protected String value;

    @XmlAttribute(name = "NAME")
    protected String name;

    @XmlAttribute(name = "VAL")
    protected String val;

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNAME() {
        return this.name;
    }

    public void setNAME(String value) {
        this.name = value;
    }

    public String getVAL() {
        return this.val;
    }

    public void setVAL(String value) {
        this.val = value;
    }
}
