package org.apache.xml.security.binding.xmldsigmore;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigInteger;
import org.apache.xml.security.binding.xmlenc11.PRFAlgorithmIdentifierType;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.EncryptionConstants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmldsigmore/HKDFParamsType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HKDFParamsType", namespace = Constants.XML_DSIG_NS_MORE_21_04, propOrder = {"prf", "salt", "info", "keyLength"})
public class HKDFParamsType {

    @XmlElement(name = EncryptionConstants._TAG_PRF, namespace = Constants.XML_DSIG_NS_MORE_21_04, required = true)
    protected PRFAlgorithmIdentifierType prf;

    @XmlElement(name = Constants._TAG_SALT, namespace = Constants.XML_DSIG_NS_MORE_21_04)
    protected byte[] salt;

    @XmlElement(name = EncryptionConstants._TAG_INFO, namespace = Constants.XML_DSIG_NS_MORE_21_04)
    protected byte[] info;

    @XmlSchemaType(name = "positiveInteger")
    @XmlElement(name = EncryptionConstants._TAG_KEYLENGTH, namespace = Constants.XML_DSIG_NS_MORE_21_04)
    protected BigInteger keyLength;

    public PRFAlgorithmIdentifierType getPRF() {
        return this.prf;
    }

    public void setPRF(PRFAlgorithmIdentifierType value) {
        this.prf = value;
    }

    public byte[] getSalt() {
        return this.salt;
    }

    public void setSalt(byte[] value) {
        this.salt = value;
    }

    public byte[] getInfo() {
        return this.info;
    }

    public void setInfo(byte[] value) {
        this.info = value;
    }

    public BigInteger getKeyLength() {
        return this.keyLength;
    }

    public void setKeyLength(BigInteger value) {
        this.keyLength = value;
    }
}
