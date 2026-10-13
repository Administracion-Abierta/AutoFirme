package org.apache.xml.security.binding.xmldsig;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmldsig/RSAKeyValueType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RSAKeyValueType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {"modulus", "exponent"})
public class RSAKeyValueType {

    @XmlElement(name = Constants._TAG_MODULUS, namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected byte[] modulus;

    @XmlElement(name = Constants._TAG_EXPONENT, namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected byte[] exponent;

    public byte[] getModulus() {
        return this.modulus;
    }

    public void setModulus(byte[] value) {
        this.modulus = value;
    }

    public byte[] getExponent() {
        return this.exponent;
    }

    public void setExponent(byte[] value) {
        this.exponent = value;
    }
}
