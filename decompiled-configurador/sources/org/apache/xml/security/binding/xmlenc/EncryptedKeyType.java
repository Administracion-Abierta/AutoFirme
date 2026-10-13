package org.apache.xml.security.binding.xmlenc;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.apache.xml.security.utils.EncryptionConstants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmlenc/EncryptedKeyType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EncryptedKeyType", namespace = "http://www.w3.org/2001/04/xmlenc#", propOrder = {"referenceList", "carriedKeyName"})
public class EncryptedKeyType extends EncryptedType {

    @XmlElement(name = EncryptionConstants._TAG_REFERENCELIST, namespace = "http://www.w3.org/2001/04/xmlenc#")
    protected ReferenceList referenceList;

    @XmlElement(name = EncryptionConstants._TAG_CARRIEDKEYNAME, namespace = "http://www.w3.org/2001/04/xmlenc#")
    protected String carriedKeyName;

    @XmlAttribute(name = EncryptionConstants._ATT_RECIPIENT)
    protected String recipient;

    public ReferenceList getReferenceList() {
        return this.referenceList;
    }

    public void setReferenceList(ReferenceList value) {
        this.referenceList = value;
    }

    public String getCarriedKeyName() {
        return this.carriedKeyName;
    }

    public void setCarriedKeyName(String value) {
        this.carriedKeyName = value;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public void setRecipient(String value) {
        this.recipient = value;
    }
}
