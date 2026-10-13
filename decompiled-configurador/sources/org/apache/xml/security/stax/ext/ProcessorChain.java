package org.apache.xml.security.stax.ext;

import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/ext/ProcessorChain.class */
public interface ProcessorChain {
    void reset();

    void doFinal() throws XMLSecurityException, XMLStreamException;
}
