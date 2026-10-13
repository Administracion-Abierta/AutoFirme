package org.apache.xml.security.configuration;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import org.mozilla.javascript.ES6Iterator;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/configuration/ResolverType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResolverType", namespace = "http://www.xmlsecurity.org/NS/configuration", propOrder = {ES6Iterator.VALUE_PROPERTY})
public class ResolverType {

    @XmlValue
    protected String value;

    @XmlAttribute(name = "JAVACLASS", required = true)
    protected String javaclass;

    @XmlAttribute(name = "DESCRIPTION", required = true)
    protected String description;

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getJAVACLASS() {
        return this.javaclass;
    }

    public void setJAVACLASS(String value) {
        this.javaclass = value;
    }

    public String getDESCRIPTION() {
        return this.description;
    }

    public void setDESCRIPTION(String value) {
        this.description = value;
    }
}
