package org.apache.xml.security.binding.xmldsig;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmldsig/RetrievalMethodType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RetrievalMethodType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {"transforms"})
public class RetrievalMethodType {

    @XmlElement(name = "Transforms", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected TransformsType transforms;

    @XmlSchemaType(name = "anyURI")
    @XmlAttribute(name = "URI")
    protected String uri;

    @XmlSchemaType(name = "anyURI")
    @XmlAttribute(name = "Type")
    protected String type;

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

    public String getType() {
        return this.type;
    }

    public void setType(String value) {
        this.type = value;
    }
}
