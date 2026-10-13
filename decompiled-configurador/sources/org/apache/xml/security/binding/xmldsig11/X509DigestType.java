package org.apache.xml.security.binding.xmldsig11;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmldsig11/X509DigestType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "X509DigestType", namespace = "http://www.w3.org/2009/xmldsig11#", propOrder = {"value"})
public class X509DigestType {

    @XmlValue
    protected byte[] value;

    @XmlSchemaType(name = "anyURI")
    @XmlAttribute(name = "Algorithm", required = true)
    protected String algorithm;

    public byte[] getValue() {
        return this.value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public void setAlgorithm(String value) {
        this.algorithm = value;
    }
}
