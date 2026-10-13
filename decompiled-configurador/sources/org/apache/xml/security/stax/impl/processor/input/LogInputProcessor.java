package org.apache.xml.security.stax.impl.processor.input;

import java.io.StringWriter;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.AbstractInputProcessor;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/processor/input/LogInputProcessor.class */
public class LogInputProcessor extends AbstractInputProcessor {
    private static final transient Logger LOG = LoggerFactory.getLogger((Class<?>) LogInputProcessor.class);

    public LogInputProcessor(XMLSecurityProperties securityProperties) {
        super(securityProperties);
        setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessor
    public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
        return inputProcessorChain.processHeaderEvent();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.ext.InputProcessor
    public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
        XMLSecEvent xmlSecEvent = inputProcessorChain.processEvent();
        StringWriter stringWriter = new StringWriter();
        xmlSecEvent.writeAsEncodedUnicode(stringWriter);
        LOG.trace(stringWriter.toString());
        return xmlSecEvent;
    }
}
