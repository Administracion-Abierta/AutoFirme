package org.apache.xml.security.binding.xmldsig11;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmldsig11/PrimeFieldParamsType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrimeFieldParamsType", namespace = "http://www.w3.org/2009/xmldsig11#", propOrder = {"p"})
public class PrimeFieldParamsType {

    @XmlElement(name = "P", namespace = "http://www.w3.org/2009/xmldsig11#", required = true)
    protected byte[] p;

    public byte[] getP() {
        return this.p;
    }

    public void setP(byte[] value) {
        this.p = value;
    }
}
