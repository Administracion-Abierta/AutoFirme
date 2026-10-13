package org.apache.xml.security.binding.xmlenc;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlMixed;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/binding/xmlenc/CipherValueType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CipherValueType", namespace = "http://www.w3.org/2001/04/xmlenc#", propOrder = {"content"})
public class CipherValueType {

    @XmlElementRef(name = "Include", namespace = XMLSecurityConstants.NS_XOP, type = JAXBElement.class, required = false)
    @XmlMixed
    protected List<Serializable> content;

    public List<Serializable> getContent() {
        if (this.content == null) {
            this.content = new ArrayList();
        }
        return this.content;
    }
}
