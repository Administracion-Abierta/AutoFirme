package org.apache.xml.security.stax.ext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/ext/AbstractInputProcessor.class */
public abstract class AbstractInputProcessor implements InputProcessor {
    private final XMLSecurityProperties securityProperties;
    private XMLSecurityConstants.Phase phase = XMLSecurityConstants.Phase.PROCESSING;
    private Set<Object> beforeProcessors;
    private Set<Object> afterProcessors;

    public AbstractInputProcessor(XMLSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessor
    public XMLSecurityConstants.Phase getPhase() {
        return this.phase;
    }

    public void setPhase(XMLSecurityConstants.Phase phase) {
        this.phase = phase;
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessor
    public void addBeforeProcessor(Object processor) {
        this.beforeProcessors = new HashSet();
        this.beforeProcessors.add(processor);
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessor
    public Set<Object> getBeforeProcessors() {
        if (this.beforeProcessors == null) {
            return Collections.emptySet();
        }
        return this.beforeProcessors;
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessor
    public void addAfterProcessor(Object processor) {
        this.afterProcessors = new HashSet();
        this.afterProcessors.add(processor);
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessor
    public Set<Object> getAfterProcessors() {
        if (this.afterProcessors == null) {
            return Collections.emptySet();
        }
        return this.afterProcessors;
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessor
    public void doFinal(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
        inputProcessorChain.doFinal();
    }

    public XMLSecurityProperties getSecurityProperties() {
        return this.securityProperties;
    }

    public Attribute getReferenceIDAttribute(XMLSecStartElement xmlSecStartElement) {
        return xmlSecStartElement.getAttributeByName(this.securityProperties.getIdAttributeNS());
    }
}
