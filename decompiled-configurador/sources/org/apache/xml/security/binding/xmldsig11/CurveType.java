package org.apache.xml.security.binding.xmldsig11;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmldsig11/CurveType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CurveType", namespace = "http://www.w3.org/2009/xmldsig11#", propOrder = {"a", "b"})
public class CurveType {

    @XmlElement(name = "A", namespace = "http://www.w3.org/2009/xmldsig11#", required = true)
    protected byte[] a;

    @XmlElement(name = "B", namespace = "http://www.w3.org/2009/xmldsig11#", required = true)
    protected byte[] b;

    public byte[] getA() {
        return this.a;
    }

    public void setA(byte[] value) {
        this.a = value;
    }

    public byte[] getB() {
        return this.b;
    }

    public void setB(byte[] value) {
        this.b = value;
    }
}
