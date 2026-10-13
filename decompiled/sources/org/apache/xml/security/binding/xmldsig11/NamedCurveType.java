package org.apache.xml.security.binding.xmldsig11;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmldsig11/NamedCurveType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NamedCurveType", namespace = "http://www.w3.org/2009/xmldsig11#")
public class NamedCurveType {

    @XmlSchemaType(name = "anyURI")
    @XmlAttribute(name = "URI", required = true)
    protected String uri;

    public String getURI() {
        return this.uri;
    }

    public void setURI(String value) {
        this.uri = value;
    }
}
