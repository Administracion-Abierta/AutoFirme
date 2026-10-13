package org.apache.xml.security.binding.xmldsig11;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmldsig11/ECValidationDataType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ECValidationDataType", namespace = "http://www.w3.org/2009/xmldsig11#", propOrder = {"seed"})
public class ECValidationDataType {

    @XmlElement(namespace = "http://www.w3.org/2009/xmldsig11#", required = true)
    protected byte[] seed;

    @XmlSchemaType(name = "anyURI")
    @XmlAttribute(name = "hashAlgorithm", required = true)
    protected String hashAlgorithm;

    public byte[] getSeed() {
        return this.seed;
    }

    public void setSeed(byte[] value) {
        this.seed = value;
    }

    public String getHashAlgorithm() {
        return this.hashAlgorithm;
    }

    public void setHashAlgorithm(String value) {
        this.hashAlgorithm = value;
    }
}
