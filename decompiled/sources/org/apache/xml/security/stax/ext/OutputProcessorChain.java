package org.apache.xml.security.stax.ext;

import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/OutputProcessorChain.class */
public interface OutputProcessorChain extends ProcessorChain {
    void addProcessor(OutputProcessor outputProcessor);

    void removeProcessor(OutputProcessor outputProcessor);

    List<OutputProcessor> getProcessors();

    OutboundSecurityContext getSecurityContext();

    DocumentContext getDocumentContext();

    OutputProcessorChain createSubChain(OutputProcessor outputProcessor) throws XMLSecurityException, XMLStreamException;

    OutputProcessorChain createSubChain(OutputProcessor outputProcessor, XMLSecStartElement xMLSecStartElement) throws XMLSecurityException, XMLStreamException;

    void processEvent(XMLSecEvent xMLSecEvent) throws XMLSecurityException, XMLStreamException;
}
