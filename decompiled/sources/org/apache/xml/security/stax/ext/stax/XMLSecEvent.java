package org.apache.xml.security.stax.ext.stax;

import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.events.XMLEvent;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/stax/XMLSecEvent.class */
public interface XMLSecEvent extends XMLEvent {
    void setParentXMLSecStartElement(XMLSecStartElement xMLSecStartElement);

    XMLSecStartElement getParentXMLSecStartElement();

    int getDocumentLevel();

    void getElementPath(List<QName> list);

    List<QName> getElementPath();

    XMLSecStartElement getStartElementAtLevel(int i);

    @Override // 
    /* JADX INFO: renamed from: asStartElement */
    XMLSecStartElement mo1376asStartElement();

    @Override // 
    /* JADX INFO: renamed from: asEndElement */
    XMLSecEndElement mo1375asEndElement();

    @Override // 
    /* JADX INFO: renamed from: asCharacters */
    XMLSecCharacters mo1374asCharacters();
}
