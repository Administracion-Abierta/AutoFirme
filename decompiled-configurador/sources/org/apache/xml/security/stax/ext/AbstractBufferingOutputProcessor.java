package org.apache.xml.security.stax.ext;

import java.util.ArrayDeque;
import java.util.Deque;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/ext/AbstractBufferingOutputProcessor.class */
public abstract class AbstractBufferingOutputProcessor extends AbstractOutputProcessor {
    private final ArrayDeque<XMLSecEvent> xmlSecEventBuffer = new ArrayDeque<>(100);

    protected abstract void processHeaderEvent(OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException;

    protected AbstractBufferingOutputProcessor() throws XMLSecurityException {
    }

    protected Deque<XMLSecEvent> getXmlSecEventBuffer() {
        return this.xmlSecEventBuffer;
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessor
    public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
        this.xmlSecEventBuffer.offer(xmlSecEvent);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.ext.AbstractOutputProcessor, org.apache.xml.security.stax.ext.OutputProcessor
    public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
        OutputProcessorChain subOutputProcessorChain = outputProcessorChain.createSubChain(this);
        flushBufferAndCallbackAfterHeader(subOutputProcessorChain, getXmlSecEventBuffer());
        subOutputProcessorChain.doFinal();
        outputProcessorChain.removeProcessor(this);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void flushBufferAndCallbackAfterHeader(OutputProcessorChain outputProcessorChain, Deque<XMLSecEvent> xmlSecEventDeque) throws XMLSecurityException, XMLStreamException {
        processHeaderEvent(outputProcessorChain);
        while (!xmlSecEventDeque.isEmpty()) {
            XMLSecEvent xmlSecEvent = xmlSecEventDeque.pop();
            outputProcessorChain.reset();
            outputProcessorChain.processEvent(xmlSecEvent);
        }
        outputProcessorChain.reset();
    }
}
