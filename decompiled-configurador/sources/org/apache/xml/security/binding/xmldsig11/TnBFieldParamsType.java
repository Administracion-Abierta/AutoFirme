package org.apache.xml.security.binding.xmldsig11;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmldsig11/TnBFieldParamsType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TnBFieldParamsType", namespace = "http://www.w3.org/2009/xmldsig11#", propOrder = {"k"})
public class TnBFieldParamsType extends CharTwoFieldParamsType {

    @XmlSchemaType(name = "positiveInteger")
    @XmlElement(name = "K", namespace = "http://www.w3.org/2009/xmldsig11#", required = true)
    protected BigInteger k;

    public BigInteger getK() {
        return this.k;
    }

    public void setK(BigInteger value) {
        this.k = value;
    }
}
