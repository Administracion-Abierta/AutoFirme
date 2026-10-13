package org.apache.xml.security.stax.ext.stax;

import java.util.List;
import javax.xml.stream.events.StartElement;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/stax/XMLSecStartElement.class */
public interface XMLSecStartElement extends XMLSecEvent, StartElement {
    XMLSecNamespace getElementNamespace();

    void getNamespacesFromCurrentScope(List<XMLSecNamespace> list);

    List<XMLSecNamespace> getOnElementDeclaredNamespaces();

    void addNamespace(XMLSecNamespace xMLSecNamespace);

    void getAttributesFromCurrentScope(List<XMLSecAttribute> list);

    List<XMLSecAttribute> getOnElementDeclaredAttributes();

    void addAttribute(XMLSecAttribute xMLSecAttribute);

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecEvent
    /* JADX INFO: renamed from: asStartElement, reason: merged with bridge method [inline-methods] */
    XMLSecStartElement mo1376asStartElement();
}
