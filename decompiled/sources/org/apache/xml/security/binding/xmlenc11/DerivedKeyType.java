package org.apache.xml.security.binding.xmlenc11;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.xml.security.binding.xmlenc.ReferenceList;
import org.apache.xml.security.utils.EncryptionConstants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmlenc11/DerivedKeyType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DerivedKeyType", namespace = "http://www.w3.org/2009/xmlenc11#", propOrder = {"keyDerivationMethod", "referenceList", "derivedKeyName", "masterKeyName"})
public class DerivedKeyType {

    @XmlElement(name = EncryptionConstants._TAG_KEYDERIVATIONMETHOD, namespace = "http://www.w3.org/2009/xmlenc11#")
    protected KeyDerivationMethodType keyDerivationMethod;

    @XmlElement(name = EncryptionConstants._TAG_REFERENCELIST, namespace = "http://www.w3.org/2001/04/xmlenc#")
    protected ReferenceList referenceList;

    @XmlElement(name = "DerivedKeyName", namespace = "http://www.w3.org/2009/xmlenc11#")
    protected String derivedKeyName;

    @XmlElement(name = "MasterKeyName", namespace = "http://www.w3.org/2009/xmlenc11#")
    protected String masterKeyName;

    @XmlAttribute(name = EncryptionConstants._ATT_RECIPIENT)
    protected String recipient;

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "ID")
    @XmlAttribute(name = "Id")
    @XmlID
    protected String id;

    @XmlSchemaType(name = "anyURI")
    @XmlAttribute(name = "Type")
    protected String type;

    public KeyDerivationMethodType getKeyDerivationMethod() {
        return this.keyDerivationMethod;
    }

    public void setKeyDerivationMethod(KeyDerivationMethodType value) {
        this.keyDerivationMethod = value;
    }

    public ReferenceList getReferenceList() {
        return this.referenceList;
    }

    public void setReferenceList(ReferenceList value) {
        this.referenceList = value;
    }

    public String getDerivedKeyName() {
        return this.derivedKeyName;
    }

    public void setDerivedKeyName(String value) {
        this.derivedKeyName = value;
    }

    public String getMasterKeyName() {
        return this.masterKeyName;
    }

    public void setMasterKeyName(String value) {
        this.masterKeyName = value;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public void setRecipient(String value) {
        this.recipient = value;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String value) {
        this.type = value;
    }
}
