package org.apache.xml.security.binding.xmldsig;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlMixed;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmldsig/ObjectType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {"content"})
public class ObjectType {

    @XmlMixed
    @XmlAnyElement(lax = true)
    protected List<Object> content;

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "ID")
    @XmlAttribute(name = "Id")
    @XmlID
    protected String id;

    @XmlAttribute(name = "MimeType")
    protected String mimeType;

    @XmlSchemaType(name = "anyURI")
    @XmlAttribute(name = "Encoding")
    protected String encoding;

    public List<Object> getContent() {
        if (this.content == null) {
            this.content = new ArrayList();
        }
        return this.content;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
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
