package org.apache.xml.security.stax.ext;

import java.io.InputStream;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/ResourceResolver.class */
public interface ResourceResolver {
    boolean isSameDocumentReference();

    boolean matches(XMLSecStartElement xMLSecStartElement);

    InputStream getInputStreamFromExternalReference() throws XMLSecurityException;
}
