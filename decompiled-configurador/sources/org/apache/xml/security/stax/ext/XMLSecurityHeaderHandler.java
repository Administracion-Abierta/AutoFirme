package org.apache.xml.security.stax.ext;

import java.util.Deque;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/ext/XMLSecurityHeaderHandler.class */
public interface XMLSecurityHeaderHandler {
    void handle(InputProcessorChain inputProcessorChain, XMLSecurityProperties xMLSecurityProperties, Deque<XMLSecEvent> deque, Integer num) throws XMLSecurityException;
}
