package org.apache.xml.security.binding.xop;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/xop/ObjectFactory.class */
@XmlRegistry
public class ObjectFactory {
    private static final QName _Include_QNAME = new QName(XMLSecurityConstants.NS_XOP, "Include");

    public Include createInclude() {
        return new Include();
    }

    @XmlElementDecl(namespace = XMLSecurityConstants.NS_XOP, name = "Include")
    public JAXBElement<Include> createInclude(Include value) {
        return new JAXBElement<>(_Include_QNAME, Include.class, null, value);
    }
}
