package org.apache.xml.security.stax.impl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.DocumentContext;
import org.apache.xml.security.stax.ext.OutboundSecurityContext;
import org.apache.xml.security.stax.ext.OutputProcessor;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/OutputProcessorChainImpl.class */
public class OutputProcessorChainImpl implements OutputProcessorChain {
    protected static final transient Logger LOG = LoggerFactory.getLogger((Class<?>) OutputProcessorChainImpl.class);
    private List<OutputProcessor> outputProcessors;
    private int startPos;
    private int curPos;
    private XMLSecStartElement parentXmlSecStartElement;
    private final OutboundSecurityContext outboundSecurityContext;
    private final DocumentContextImpl documentContext;

    public OutputProcessorChainImpl(OutboundSecurityContext outboundSecurityContext) {
        this(outboundSecurityContext, 0);
    }

    public OutputProcessorChainImpl(OutboundSecurityContext outboundSecurityContext, int startPos) {
        this(outboundSecurityContext, new DocumentContextImpl(), startPos, new ArrayList(20));
    }

    public OutputProcessorChainImpl(OutboundSecurityContext outboundSecurityContext, DocumentContextImpl documentContext) {
        this(outboundSecurityContext, documentContext, 0, new ArrayList(20));
    }

    protected OutputProcessorChainImpl(OutboundSecurityContext outboundSecurityContext, DocumentContextImpl documentContextImpl, int startPos, List<OutputProcessor> outputProcessors) {
        this.outboundSecurityContext = outboundSecurityContext;
        this.startPos = startPos;
        this.curPos = startPos;
        this.documentContext = documentContextImpl;
        this.outputProcessors = outputProcessors;
    }

    @Override // org.apache.xml.security.stax.ext.ProcessorChain
    public void reset() {
        this.curPos = this.startPos;
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessorChain
    public OutboundSecurityContext getSecurityContext() {
        return this.outboundSecurityContext;
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessorChain
    public DocumentContext getDocumentContext() {
        return this.documentContext;
    }

    private static int compare(OutputProcessor o1, OutputProcessor o2) {
        int d;
        int d2 = o1.getPhase().compareTo(o2.getPhase());
        if (d2 != 0) {
            return d2;
        }
        if (o1.getActionOrder() >= 0 && o2.getActionOrder() >= 0 && (d = o1.getActionOrder() - o2.getActionOrder()) != 0) {
            return d;
        }
        if (o1.getBeforeProcessors().contains(o2.getClass()) || o2.getAfterProcessors().contains(o1.getClass())) {
            if (o1.getAfterProcessors().contains(o2.getClass()) || o2.getBeforeProcessors().contains(o1.getClass())) {
                throw new IllegalArgumentException(String.format("Conflicting order of processors %s and %s", o1, o2));
            }
            return -1;
        }
        if (o1.getAfterProcessors().contains(o2.getClass()) || o2.getBeforeProcessors().contains(o1.getClass())) {
            if (o2.getAfterProcessors().contains(o1.getClass())) {
                throw new IllegalArgumentException(String.format("Conflicting order of processors %s and %s", o1, o2));
            }
            return 1;
        }
        return 0;
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessorChain
    public void addProcessor(OutputProcessor newOutputProcessor) {
        int idxToInsert = this.outputProcessors.size();
        boolean pointOfNoReturn = false;
        int idx = this.outputProcessors.size();
        while (true) {
            idx--;
            if (idx >= 0) {
                OutputProcessor outputProcessor = this.outputProcessors.get(idx);
                int d = compare(newOutputProcessor, outputProcessor);
                if (d < 0) {
                    if (pointOfNoReturn) {
                        throw new IllegalArgumentException(String.format("Conflicting order of processors %s and %s", newOutputProcessor, outputProcessor));
                    }
                    idxToInsert = idx;
                } else if (d > 0) {
                    pointOfNoReturn = true;
                }
            } else {
                this.outputProcessors.add(idxToInsert, newOutputProcessor);
                if (idxToInsert < this.curPos) {
                    this.curPos++;
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Added {} to output chain: ", newOutputProcessor.getClass().getName());
                    for (OutputProcessor outputProcessor2 : this.outputProcessors) {
                        LOG.debug("Name: {} phase: {}", outputProcessor2.getClass().getName(), outputProcessor2.getPhase());
                    }
                    return;
                }
                return;
            }
        }
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessorChain
    public void removeProcessor(OutputProcessor outputProcessor) {
        LOG.debug("Removing processor {} from output chain", outputProcessor.getClass().getName());
        if (this.outputProcessors.indexOf(outputProcessor) <= this.curPos) {
            this.curPos--;
        }
        this.outputProcessors.remove(outputProcessor);
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessorChain
    public List<OutputProcessor> getProcessors() {
        return this.outputProcessors;
    }

    private void setParentXmlSecStartElement(XMLSecStartElement xmlSecStartElement) {
        this.parentXmlSecStartElement = xmlSecStartElement;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.ext.OutputProcessorChain
    public void processEvent(XMLSecEvent xmlSecEvent) throws XMLSecurityException, XMLStreamException {
        boolean reparent = false;
        if (this.curPos == this.startPos) {
            switch (xmlSecEvent.getEventType()) {
                case 1:
                    if (xmlSecEvent == this.parentXmlSecStartElement) {
                        this.parentXmlSecStartElement = null;
                    }
                    xmlSecEvent.setParentXMLSecStartElement(this.parentXmlSecStartElement);
                    this.parentXmlSecStartElement = xmlSecEvent.mo458asStartElement();
                    break;
                case 2:
                    xmlSecEvent.setParentXMLSecStartElement(this.parentXmlSecStartElement);
                    reparent = true;
                    break;
                default:
                    xmlSecEvent.setParentXMLSecStartElement(this.parentXmlSecStartElement);
                    break;
            }
        }
        List<OutputProcessor> list = this.outputProcessors;
        int i = this.curPos;
        this.curPos = i + 1;
        list.get(i).processEvent(xmlSecEvent, this);
        if (reparent && this.parentXmlSecStartElement != null) {
            this.parentXmlSecStartElement = this.parentXmlSecStartElement.getParentXMLSecStartElement();
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.ext.ProcessorChain
    public void doFinal() throws XMLSecurityException, XMLStreamException {
        List<OutputProcessor> list = this.outputProcessors;
        int i = this.curPos;
        this.curPos = i + 1;
        list.get(i).doFinal(this);
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessorChain
    public OutputProcessorChain createSubChain(OutputProcessor outputProcessor) throws XMLSecurityException, XMLStreamException {
        return createSubChain(outputProcessor, null);
    }

    @Override // org.apache.xml.security.stax.ext.OutputProcessorChain
    public OutputProcessorChain createSubChain(OutputProcessor outputProcessor, XMLSecStartElement parentXMLSecStartElement) throws XMLSecurityException, XMLStreamException {
        try {
            OutputProcessorChainImpl outputProcessorChain = new OutputProcessorChainImpl(this.outboundSecurityContext, this.documentContext.m459clone(), this.outputProcessors.indexOf(outputProcessor) + 1, this.outputProcessors);
            if (parentXMLSecStartElement != null) {
                outputProcessorChain.setParentXmlSecStartElement(parentXMLSecStartElement);
            } else {
                outputProcessorChain.setParentXmlSecStartElement(this.parentXmlSecStartElement);
            }
            return outputProcessorChain;
        } catch (CloneNotSupportedException e) {
            throw new XMLSecurityException(e);
        }
    }
}
