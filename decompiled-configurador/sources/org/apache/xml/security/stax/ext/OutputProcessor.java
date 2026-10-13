package org.apache.xml.security.stax.ext;

import java.util.Set;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/ext/OutputProcessor.class */
public interface OutputProcessor {
    void setXMLSecurityProperties(XMLSecurityProperties xMLSecurityProperties);

    void setAction(XMLSecurityConstants.Action action, int i);

    XMLSecurityConstants.Action getAction();

    int getActionOrder();

    void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException;

    void addBeforeProcessor(Class<? extends OutputProcessor> cls);

    Set<Class<? extends OutputProcessor>> getBeforeProcessors();

    void addAfterProcessor(Class<? extends OutputProcessor> cls);

    Set<Class<? extends OutputProcessor>> getAfterProcessors();

    XMLSecurityConstants.Phase getPhase();

    void processEvent(XMLSecEvent xMLSecEvent, OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException;

    void doFinal(OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException;
}
