package org.apache.xml.security.stax.impl.stax;

import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecEndDocument;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/stax/XMLSecEndDocumentImpl.class */
public class XMLSecEndDocumentImpl extends XMLSecEventBaseImpl implements XMLSecEndDocument {
    public int getEventType() {
        return 8;
    }

    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public boolean isEndDocument() {
        return true;
    }

    @Override // org.apache.xml.security.stax.ext.stax.XMLSecEndDocument
    public XMLSecEndDocument asEndEndDocument() {
        return this;
    }

    @Override // org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
    }
}
