package org.apache.xml.security.stax.ext;

import java.util.Set;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/InputProcessor.class */
public interface InputProcessor {
    void addBeforeProcessor(Object obj);

    Set<Object> getBeforeProcessors();

    void addAfterProcessor(Object obj);

    Set<Object> getAfterProcessors();

    XMLSecurityConstants.Phase getPhase();

    XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException;

    XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException;

    void doFinal(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException;
}
