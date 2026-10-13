package org.apache.xml.security.binding.xmlenc;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.apache.xml.security.utils.EncryptionConstants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmlenc/CipherDataType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CipherDataType", namespace = "http://www.w3.org/2001/04/xmlenc#", propOrder = {"cipherValue", "cipherReference"})
public class CipherDataType {

    @XmlElement(name = EncryptionConstants._TAG_CIPHERVALUE, namespace = "http://www.w3.org/2001/04/xmlenc#")
    protected CipherValueType cipherValue;

    @XmlElement(name = EncryptionConstants._TAG_CIPHERREFERENCE, namespace = "http://www.w3.org/2001/04/xmlenc#")
    protected CipherReferenceType cipherReference;

    public CipherValueType getCipherValue() {
        return this.cipherValue;
    }

    public void setCipherValue(CipherValueType value) {
        this.cipherValue = value;
    }

    public CipherReferenceType getCipherReference() {
        return this.cipherReference;
    }

    public void setCipherReference(CipherReferenceType value) {
        this.cipherReference = value;
    }
}
