package org.apache.xml.security.binding.xmlenc11;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigInteger;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.EncryptionConstants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmlenc11/PBKDF2ParameterType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PBKDF2ParameterType", namespace = "http://www.w3.org/2009/xmlenc11#", propOrder = {"salt", "iterationCount", "keyLength", "prf"})
public class PBKDF2ParameterType {

    @XmlElement(name = Constants._TAG_SALT, namespace = "http://www.w3.org/2009/xmlenc11#", required = true)
    protected Salt salt;

    @XmlSchemaType(name = "positiveInteger")
    @XmlElement(name = "IterationCount", namespace = "http://www.w3.org/2009/xmlenc11#", required = true)
    protected BigInteger iterationCount;

    @XmlSchemaType(name = "positiveInteger")
    @XmlElement(name = EncryptionConstants._TAG_KEYLENGTH, namespace = "http://www.w3.org/2009/xmlenc11#", required = true)
    protected BigInteger keyLength;

    @XmlElement(name = EncryptionConstants._TAG_PRF, namespace = "http://www.w3.org/2009/xmlenc11#", required = true)
    protected PRFAlgorithmIdentifierType prf;

    public Salt getSalt() {
        return this.salt;
    }

    public void setSalt(Salt value) {
        this.salt = value;
    }

    public BigInteger getIterationCount() {
        return this.iterationCount;
    }

    public void setIterationCount(BigInteger value) {
        this.iterationCount = value;
    }

    public BigInteger getKeyLength() {
        return this.keyLength;
    }

    public void setKeyLength(BigInteger value) {
        this.keyLength = value;
    }

    public PRFAlgorithmIdentifierType getPRF() {
        return this.prf;
    }

    public void setPRF(PRFAlgorithmIdentifierType value) {
        this.prf = value;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmlenc11/PBKDF2ParameterType$Salt.class */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"specified", "otherSource"})
    public static class Salt {

        @XmlElement(name = "Specified", namespace = "http://www.w3.org/2009/xmlenc11#")
        protected byte[] specified;

        @XmlElement(name = "OtherSource", namespace = "http://www.w3.org/2009/xmlenc11#")
        protected AlgorithmIdentifierType otherSource;

        public byte[] getSpecified() {
            return this.specified;
        }

        public void setSpecified(byte[] value) {
            this.specified = value;
        }

        public AlgorithmIdentifierType getOtherSource() {
            return this.otherSource;
        }

        public void setOtherSource(AlgorithmIdentifierType value) {
            this.otherSource = value;
        }
    }
}
