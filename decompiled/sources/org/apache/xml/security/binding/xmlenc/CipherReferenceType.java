package org.apache.xml.security.binding.xmlenc;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmlenc/CipherReferenceType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CipherReferenceType", namespace = "http://www.w3.org/2001/04/xmlenc#", propOrder = {"transforms"})
public class CipherReferenceType {

    @XmlElement(name = "Transforms", namespace = "http://www.w3.org/2001/04/xmlenc#")
    protected TransformsType transforms;

    @XmlSchemaType(name = "anyURI")
    @XmlAttribute(name = "URI", required = true)
    protected String uri;

    public TransformsType getTransforms() {
        return this.transforms;
    }

    public void setTransforms(TransformsType value) {
        this.transforms = value;
    }

    public String getURI() {
        return this.uri;
    }

    public void setURI(String value) {
        this.uri = value;
    }
}
