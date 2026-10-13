package org.apache.xml.security.stax.impl.processor.output;

import java.io.OutputStream;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.AbstractOutputProcessor;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.XMLSecurityEventWriter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/processor/output/FinalOutputProcessor.class */
public class FinalOutputProcessor extends AbstractOutputProcessor {
    private XMLEventWriter xmlEventWriter;

    public FinalOutputProcessor(OutputStream outputStream, String encoding) throws XMLSecurityException {
        setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
        try {
            this.xmlEventWriter = new XMLSecurityEventWriter(XMLSecurityConstants.xmlOutputFactory.createXMLStreamWriter(outputStream, encoding));
        } catch (XMLStreamException e) {
            throw new XMLSecurityException((Exception) e);
        }
    }

    public FinalOutputProcessor(XMLStreamWriter xmlStreamWriter) throws XMLSecurityException {
        setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
        this.xmlEventWriter = new XMLSecurityEventWriter(xmlStreamWriter);
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessor
    public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
        this.xmlEventWriter.add(xmlSecEvent);
    }

    @Override // org.apache.xml.security.stax.ext.AbstractOutputProcessor, org.apache.xml.security.stax.ext.OutputProcessor
    public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        try {
            this.xmlEventWriter.flush();
            this.xmlEventWriter.close();
        } catch (XMLStreamException e) {
            throw new XMLSecurityException((Exception) e);
        }
    }
}
