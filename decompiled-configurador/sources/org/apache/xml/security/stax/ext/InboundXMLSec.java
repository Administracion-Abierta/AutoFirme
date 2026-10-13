package org.apache.xml.security.stax.ext;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.xml.security.stax.impl.DocumentContextImpl;
import org.apache.xml.security.stax.impl.InboundSecurityContextImpl;
import org.apache.xml.security.stax.impl.InputProcessorChainImpl;
import org.apache.xml.security.stax.impl.XMLSecurityStreamReader;
import org.apache.xml.security.stax.impl.processor.input.LogInputProcessor;
import org.apache.xml.security.stax.impl.processor.input.XMLEventReaderInputProcessor;
import org.apache.xml.security.stax.impl.processor.input.XMLSecurityInputProcessor;
import org.apache.xml.security.stax.securityEvent.SecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEventListener;
import org.codehaus.stax2.XMLInputFactory2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/ext/InboundXMLSec.class */
public class InboundXMLSec {
    protected static final transient Logger LOG = LoggerFactory.getLogger((Class<?>) InboundXMLSec.class);
    private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    private final XMLSecurityProperties securityProperties;

    static {
        xmlInputFactory.setProperty("javax.xml.stream.supportDTD", false);
        xmlInputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
        try {
            xmlInputFactory.setProperty(XMLInputFactory2.P_INTERN_NAMES, true);
            xmlInputFactory.setProperty(XMLInputFactory2.P_INTERN_NS_URIS, true);
            xmlInputFactory.setProperty(XMLInputFactory2.P_PRESERVE_LOCATION, false);
        } catch (IllegalArgumentException e) {
            LOG.debug(e.getMessage(), (Throwable) e);
        }
    }

    public InboundXMLSec(XMLSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public XMLStreamReader processInMessage(XMLStreamReader xmlStreamReader) throws XMLStreamException {
        return processInMessage(xmlStreamReader, null, null);
    }

    public XMLStreamReader processInMessage(XMLStreamReader xmlStreamReader, List<SecurityEvent> requestSecurityEvents, SecurityEventListener securityEventListener) throws XMLStreamException {
        if (requestSecurityEvents == null) {
            requestSecurityEvents = Collections.emptyList();
        }
        InboundSecurityContextImpl inboundSecurityContext = new InboundSecurityContextImpl();
        inboundSecurityContext.putList(SecurityEvent.class, requestSecurityEvents);
        inboundSecurityContext.addSecurityEventListener(securityEventListener);
        inboundSecurityContext.put(XMLSecurityConstants.XMLINPUTFACTORY, xmlInputFactory);
        DocumentContextImpl documentContext = new DocumentContextImpl();
        documentContext.setEncoding(xmlStreamReader.getEncoding() != null ? xmlStreamReader.getEncoding() : StandardCharsets.UTF_8.name());
        Location location = xmlStreamReader.getLocation();
        if (location != null) {
            documentContext.setBaseURI(location.getSystemId());
        }
        InputProcessorChainImpl inputProcessorChain = new InputProcessorChainImpl(inboundSecurityContext, documentContext);
        inputProcessorChain.addProcessor(new XMLEventReaderInputProcessor(this.securityProperties, xmlStreamReader));
        List<InputProcessor> additionalInputProcessors = this.securityProperties.getInputProcessorList();
        if (!additionalInputProcessors.isEmpty()) {
            for (InputProcessor inputProcessor : additionalInputProcessors) {
                inputProcessorChain.addProcessor(inputProcessor);
            }
        }
        inputProcessorChain.addProcessor(new XMLSecurityInputProcessor(this.securityProperties));
        if (LOG.isTraceEnabled()) {
            LogInputProcessor logInputProcessor = new LogInputProcessor(this.securityProperties);
            logInputProcessor.addAfterProcessor(XMLSecurityInputProcessor.class.getName());
            inputProcessorChain.addProcessor(logInputProcessor);
        }
        return new XMLSecurityStreamReader(inputProcessorChain, this.securityProperties);
    }
}
