package org.apache.xml.security.binding.xmlenc;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlMixed;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import org.apache.xml.security.utils.EncryptionConstants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmlenc/EncryptionMethodType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EncryptionMethodType", namespace = "http://www.w3.org/2001/04/xmlenc#", propOrder = {"content"})
public class EncryptionMethodType {

    @XmlMixed
    @XmlElementRefs({@XmlElementRef(name = EncryptionConstants._TAG_KEYSIZE, namespace = "http://www.w3.org/2001/04/xmlenc#", type = JAXBElement.class, required = false), @XmlElementRef(name = EncryptionConstants._TAG_OAEPPARAMS, namespace = "http://www.w3.org/2001/04/xmlenc#", type = JAXBElement.class, required = false)})
    @XmlAnyElement(lax = true)
    protected List<Object> content;

    @XmlSchemaType(name = "anyURI")
    @XmlAttribute(name = "Algorithm", required = true)
    protected String algorithm;

    public List<Object> getContent() {
        if (this.content == null) {
            this.content = new ArrayList();
        }
        return this.content;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public void setAlgorithm(String value) {
        this.algorithm = value;
    }
}
