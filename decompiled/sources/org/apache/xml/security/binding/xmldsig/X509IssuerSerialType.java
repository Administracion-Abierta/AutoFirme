package org.apache.xml.security.binding.xmldsig;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigInteger;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmldsig/X509IssuerSerialType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "X509IssuerSerialType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {"x509IssuerName", "x509SerialNumber"})
public class X509IssuerSerialType {

    @XmlElement(name = Constants._TAG_X509ISSUERNAME, namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected String x509IssuerName;

    @XmlElement(name = Constants._TAG_X509SERIALNUMBER, namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected BigInteger x509SerialNumber;

    public String getX509IssuerName() {
        return this.x509IssuerName;
    }

    public void setX509IssuerName(String value) {
        this.x509IssuerName = value;
    }

    public BigInteger getX509SerialNumber() {
        return this.x509SerialNumber;
    }

    public void setX509SerialNumber(BigInteger value) {
        this.x509SerialNumber = value;
    }
}
