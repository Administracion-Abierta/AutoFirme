package org.apache.xml.security.stax.impl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.DocumentContext;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.ext.InputProcessor;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/InputProcessorChainImpl.class */
public class InputProcessorChainImpl implements InputProcessorChain {
    protected static final transient Logger LOG = LoggerFactory.getLogger((Class<?>) InputProcessorChainImpl.class);
    private List<InputProcessor> inputProcessors;
    private int startPos;
    private int curPos;
    private final InboundSecurityContext inboundSecurityContext;
    private final DocumentContextImpl documentContext;

    public InputProcessorChainImpl(InboundSecurityContext inboundSecurityContext) {
        this(inboundSecurityContext, 0);
    }

    public InputProcessorChainImpl(InboundSecurityContext inboundSecurityContext, int startPos) {
        this(inboundSecurityContext, new DocumentContextImpl(), startPos, new ArrayList(20));
    }

    public InputProcessorChainImpl(InboundSecurityContext inboundSecurityContext, DocumentContextImpl documentContext) {
        this(inboundSecurityContext, documentContext, 0, new ArrayList(20));
    }

    protected InputProcessorChainImpl(InboundSecurityContext inboundSecurityContext, DocumentContextImpl documentContextImpl, int startPos, List<InputProcessor> inputProcessors) {
        this.inboundSecurityContext = inboundSecurityContext;
        this.startPos = startPos;
        this.curPos = startPos;
        this.documentContext = documentContextImpl;
        this.inputProcessors = inputProcessors;
    }

    @Override // org.apache.xml.security.stax.ext.ProcessorChain
    public void reset() {
        this.curPos = this.startPos;
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessorChain
    public InboundSecurityContext getSecurityContext() {
        return this.inboundSecurityContext;
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessorChain
    public DocumentContext getDocumentContext() {
        return this.documentContext;
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessorChain
    public synchronized void addProcessor(InputProcessor newInputProcessor) {
        int startPhaseIdx = 0;
        int endPhaseIdx = this.inputProcessors.size();
        XMLSecurityConstants.Phase targetPhase = newInputProcessor.getPhase();
        for (int i = this.inputProcessors.size() - 1; i >= 0; i--) {
            if (this.inputProcessors.get(i).getPhase().ordinal() > targetPhase.ordinal()) {
                startPhaseIdx = i + 1;
                break;
            }
        }
        for (int i2 = startPhaseIdx; i2 < this.inputProcessors.size(); i2++) {
            if (this.inputProcessors.get(i2).getPhase().ordinal() < targetPhase.ordinal()) {
                endPhaseIdx = i2;
                break;
            }
        }
        if (newInputProcessor.getBeforeProcessors().isEmpty() && newInputProcessor.getAfterProcessors().isEmpty()) {
            this.inputProcessors.add(startPhaseIdx, newInputProcessor);
        } else if (newInputProcessor.getBeforeProcessors().isEmpty()) {
            int idxToInsert = startPhaseIdx;
            for (int i3 = endPhaseIdx - 1; i3 >= startPhaseIdx; i3--) {
                InputProcessor inputProcessor = this.inputProcessors.get(i3);
                if (newInputProcessor.getAfterProcessors().contains(inputProcessor) || newInputProcessor.getAfterProcessors().contains(inputProcessor.getClass().getName())) {
                    idxToInsert = i3;
                    break;
                }
            }
            this.inputProcessors.add(idxToInsert, newInputProcessor);
        } else if (newInputProcessor.getAfterProcessors().isEmpty()) {
            int idxToInsert2 = endPhaseIdx;
            for (int i4 = startPhaseIdx; i4 < endPhaseIdx; i4++) {
                InputProcessor inputProcessor2 = this.inputProcessors.get(i4);
                if (newInputProcessor.getBeforeProcessors().contains(inputProcessor2) || newInputProcessor.getBeforeProcessors().contains(inputProcessor2.getClass().getName())) {
                    idxToInsert2 = i4 + 1;
                    break;
                }
            }
            this.inputProcessors.add(idxToInsert2, newInputProcessor);
        } else {
            boolean found = false;
            int idxToInsert3 = startPhaseIdx;
            for (int i5 = endPhaseIdx - 1; i5 >= startPhaseIdx; i5--) {
                InputProcessor inputProcessor3 = this.inputProcessors.get(i5);
                if (newInputProcessor.getAfterProcessors().contains(inputProcessor3) || newInputProcessor.getAfterProcessors().contains(inputProcessor3.getClass().getName())) {
                    idxToInsert3 = i5;
                    found = true;
                    break;
                }
            }
            if (found) {
                this.inputProcessors.add(idxToInsert3, newInputProcessor);
            } else {
                for (int i6 = startPhaseIdx; i6 < endPhaseIdx; i6++) {
                    InputProcessor inputProcessor4 = this.inputProcessors.get(i6);
                    if (newInputProcessor.getBeforeProcessors().contains(inputProcessor4) || newInputProcessor.getBeforeProcessors().contains(inputProcessor4.getClass().getName())) {
                        idxToInsert3 = i6 + 1;
                        break;
                    }
                }
                this.inputProcessors.add(idxToInsert3, newInputProcessor);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Added {} to input chain: ", newInputProcessor.getClass().getName());
            for (InputProcessor inputProcessor5 : this.inputProcessors) {
                LOG.debug("Name: {} phase: {}", inputProcessor5.getClass().getName(), inputProcessor5.getPhase());
            }
        }
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessorChain
    public synchronized void removeProcessor(InputProcessor inputProcessor) {
        LOG.debug("Removing processor {} from input chain", inputProcessor.getClass().getName());
        if (this.inputProcessors.indexOf(inputProcessor) <= this.curPos) {
            this.curPos--;
        }
        this.inputProcessors.remove(inputProcessor);
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessorChain
    public List<InputProcessor> getProcessors() {
        return this.inputProcessors;
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessorChain
    public XMLSecEvent processHeaderEvent() throws XMLSecurityException, XMLStreamException {
        List<InputProcessor> list = this.inputProcessors;
        int i = this.curPos;
        this.curPos = i + 1;
        return list.get(i).processHeaderEvent(this);
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessorChain
    public XMLSecEvent processEvent() throws XMLSecurityException, XMLStreamException {
        List<InputProcessor> list = this.inputProcessors;
        int i = this.curPos;
        this.curPos = i + 1;
        return list.get(i).processEvent(this);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.ext.ProcessorChain
    public void doFinal() throws XMLSecurityException, XMLStreamException {
        List<InputProcessor> list = this.inputProcessors;
        int i = this.curPos;
        this.curPos = i + 1;
        list.get(i).doFinal(this);
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessorChain
    public InputProcessorChain createSubChain(InputProcessor inputProcessor) throws XMLSecurityException, XMLStreamException {
        return createSubChain(inputProcessor, true);
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessorChain
    public InputProcessorChain createSubChain(InputProcessor inputProcessor, boolean clone) throws XMLSecurityException, XMLStreamException {
        try {
            DocumentContextImpl docContext = clone ? this.documentContext.m459clone() : this.documentContext;
            InputProcessorChainImpl inputProcessorChain = new InputProcessorChainImpl(this.inboundSecurityContext, docContext, this.inputProcessors.indexOf(inputProcessor) + 1, new ArrayList(this.inputProcessors));
            return inputProcessorChain;
        } catch (CloneNotSupportedException e) {
            throw new XMLSecurityException(e);
        }
    }
}
