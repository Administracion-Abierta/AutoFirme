package org.apache.xml.security.stax.ext;

import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/InputProcessorChain.class */
public interface InputProcessorChain extends ProcessorChain {
    void addProcessor(InputProcessor inputProcessor);

    void removeProcessor(InputProcessor inputProcessor);

    List<InputProcessor> getProcessors();

    InboundSecurityContext getSecurityContext();

    DocumentContext getDocumentContext();

    InputProcessorChain createSubChain(InputProcessor inputProcessor) throws XMLSecurityException, XMLStreamException;

    InputProcessorChain createSubChain(InputProcessor inputProcessor, boolean z) throws XMLSecurityException, XMLStreamException;

    XMLSecEvent processHeaderEvent() throws XMLSecurityException, XMLStreamException;

    XMLSecEvent processEvent() throws XMLSecurityException, XMLStreamException;
}
