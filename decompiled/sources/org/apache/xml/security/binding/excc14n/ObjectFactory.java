package org.apache.xml.security.binding.excc14n;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/binding/excc14n/ObjectFactory.class */
@XmlRegistry
public class ObjectFactory {
    private static final QName _InclusiveNamespaces_QNAME = new QName("http://www.w3.org/2001/10/xml-exc-c14n#", org.apache.xml.security.transforms.params.InclusiveNamespaces._TAG_EC_INCLUSIVENAMESPACES);

    public InclusiveNamespaces createInclusiveNamespaces() {
        return new InclusiveNamespaces();
    }

    @XmlElementDecl(namespace = "http://www.w3.org/2001/10/xml-exc-c14n#", name = org.apache.xml.security.transforms.params.InclusiveNamespaces._TAG_EC_INCLUSIVENAMESPACES)
    public JAXBElement<InclusiveNamespaces> createInclusiveNamespaces(InclusiveNamespaces value) {
        return new JAXBElement<>(_InclusiveNamespaces_QNAME, InclusiveNamespaces.class, null, value);
    }
}
