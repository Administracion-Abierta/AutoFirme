package org.apache.xml.security.binding.xmldsig;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xmldsig/PGPDataType.class */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PGPDataType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {"content"})
public class PGPDataType {

    @XmlElementRefs({@XmlElementRef(name = Constants._TAG_PGPKEYID, namespace = "http://www.w3.org/2000/09/xmldsig#", type = JAXBElement.class, required = false), @XmlElementRef(name = Constants._TAG_PGPKEYPACKET, namespace = "http://www.w3.org/2000/09/xmldsig#", type = JAXBElement.class, required = false)})
    @XmlAnyElement(lax = true)
    protected List<Object> content;

    public List<Object> getContent() {
        if (this.content == null) {
            this.content = new ArrayList();
        }
        return this.content;
    }
}
