package org.apache.xml.security.binding.xmlenc;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.language.bm.Languages;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmlenc/ReferenceType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferenceType", namespace = "http://www.w3.org/2001/04/xmlenc#", propOrder = {Languages.ANY})
public class ReferenceType {

    @XmlAnyElement(lax = true)
    protected List<Object> any;

    @XmlSchemaType(name = "anyURI")
    @XmlAttribute(name = "URI", required = true)
    protected String uri;

    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList();
        }
        return this.any;
    }

    public String getURI() {
        return this.uri;
    }

    public void setURI(String value) {
        this.uri = value;
    }
}
