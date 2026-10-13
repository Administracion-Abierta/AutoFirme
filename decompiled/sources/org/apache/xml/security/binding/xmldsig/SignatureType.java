package org.apache.xml.security.binding.xmldsig;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmldsig/SignatureType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignatureType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {"signedInfo", "signatureValue", "keyInfo", "object"})
public class SignatureType {

    @XmlElement(name = "SignedInfo", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected SignedInfoType signedInfo;

    @XmlElement(name = "SignatureValue", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected SignatureValueType signatureValue;

    @XmlElement(name = Constants._TAG_KEYINFO, namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected KeyInfoType keyInfo;

    @XmlElement(name = Constants._TAG_OBJECT, namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected List<ObjectType> object;

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "ID")
    @XmlAttribute(name = "Id")
    @XmlID
    protected String id;

    public SignedInfoType getSignedInfo() {
        return this.signedInfo;
    }

    public void setSignedInfo(SignedInfoType value) {
        this.signedInfo = value;
    }

    public SignatureValueType getSignatureValue() {
        return this.signatureValue;
    }

    public void setSignatureValue(SignatureValueType value) {
        this.signatureValue = value;
    }

    public KeyInfoType getKeyInfo() {
        return this.keyInfo;
    }

    public void setKeyInfo(KeyInfoType value) {
        this.keyInfo = value;
    }

    public List<ObjectType> getObject() {
        if (this.object == null) {
            this.object = new ArrayList();
        }
        return this.object;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }
}
