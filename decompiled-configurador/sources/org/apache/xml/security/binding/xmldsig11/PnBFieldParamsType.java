package org.apache.xml.security.binding.xmldsig11;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmldsig11/PnBFieldParamsType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PnBFieldParamsType", namespace = "http://www.w3.org/2009/xmldsig11#", propOrder = {"k1", "k2", "k3"})
public class PnBFieldParamsType extends CharTwoFieldParamsType {

    @XmlSchemaType(name = "positiveInteger")
    @XmlElement(name = "K1", namespace = "http://www.w3.org/2009/xmldsig11#", required = true)
    protected BigInteger k1;

    @XmlSchemaType(name = "positiveInteger")
    @XmlElement(name = "K2", namespace = "http://www.w3.org/2009/xmldsig11#", required = true)
    protected BigInteger k2;

    @XmlSchemaType(name = "positiveInteger")
    @XmlElement(name = "K3", namespace = "http://www.w3.org/2009/xmldsig11#", required = true)
    protected BigInteger k3;

    public BigInteger getK1() {
        return this.k1;
    }

    public void setK1(BigInteger value) {
        this.k1 = value;
    }

    public BigInteger getK2() {
        return this.k2;
    }

    public void setK2(BigInteger value) {
        this.k2 = value;
    }

    public BigInteger getK3() {
        return this.k3;
    }

    public void setK3(BigInteger value) {
        this.k3 = value;
    }
}
