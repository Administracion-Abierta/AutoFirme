package org.apache.xml.security.binding.xmlenc;

import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.xml.security.binding.xmldsig.KeyInfoType;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.EncryptionConstants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmlenc/EncryptedType.class */
@XmlSeeAlso({EncryptedDataType.class, EncryptedKeyType.class})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EncryptedType", namespace = "http://www.w3.org/2001/04/xmlenc#", propOrder = {"encryptionMethod", "keyInfo", "cipherData", "encryptionProperties"})
public abstract class EncryptedType {

    @XmlElement(name = EncryptionConstants._TAG_ENCRYPTIONMETHOD, namespace = "http://www.w3.org/2001/04/xmlenc#")
    protected EncryptionMethodType encryptionMethod;

    @XmlElement(name = Constants._TAG_KEYINFO, namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected KeyInfoType keyInfo;

    @XmlElement(name = EncryptionConstants._TAG_CIPHERDATA, namespace = "http://www.w3.org/2001/04/xmlenc#", required = true)
    protected CipherDataType cipherData;

    @XmlElement(name = EncryptionConstants._TAG_ENCRYPTIONPROPERTIES, namespace = "http://www.w3.org/2001/04/xmlenc#")
    protected EncryptionPropertiesType encryptionProperties;

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = DTDParser.TYPE_ID)
    @XmlAttribute(name = "Id")
    @XmlID
    protected String id;

    @XmlSchemaType(name = "anyURI")
    @XmlAttribute(name = "Type")
    protected String type;

    @XmlAttribute(name = "MimeType")
    protected String mimeType;

    @XmlSchemaType(name = "anyURI")
    @XmlAttribute(name = "Encoding")
    protected String encoding;

    public EncryptionMethodType getEncryptionMethod() {
        return this.encryptionMethod;
    }

    public void setEncryptionMethod(EncryptionMethodType value) {
        this.encryptionMethod = value;
    }

    public KeyInfoType getKeyInfo() {
        return this.keyInfo;
    }

    public void setKeyInfo(KeyInfoType value) {
        this.keyInfo = value;
    }

    public CipherDataType getCipherData() {
        return this.cipherData;
    }

    public void setCipherData(CipherDataType value) {
        this.cipherData = value;
    }

    public EncryptionPropertiesType getEncryptionProperties() {
        return this.encryptionProperties;
    }

    public void setEncryptionProperties(EncryptionPropertiesType value) {
        this.encryptionProperties = value;
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

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String value) {
        this.mimeType = value;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String value) {
        this.encoding = value;
    }
}
