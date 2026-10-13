package org.apache.xml.security.binding.xmldsig;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlMixed;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmldsig/DigestMethodType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DigestMethodType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {"content"})
public class DigestMethodType {

    @XmlMixed
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
