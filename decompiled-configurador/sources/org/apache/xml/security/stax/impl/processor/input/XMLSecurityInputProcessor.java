package org.apache.xml.security.stax.impl.processor.input;

import java.util.ArrayDeque;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.AbstractInputProcessor;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/processor/input/XMLSecurityInputProcessor.class */
public class XMLSecurityInputProcessor extends AbstractInputProcessor {
    private int startIndexForProcessor;
    private InternalBufferProcessor internalBufferProcessor;
    private boolean signatureElementFound;
    private boolean encryptedDataElementFound;
    private boolean decryptOnly;

    public XMLSecurityInputProcessor(XMLSecurityProperties securityProperties) {
        super(securityProperties);
        this.signatureElementFound = false;
        this.encryptedDataElementFound = false;
        this.decryptOnly = false;
        setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
        this.decryptOnly = securityProperties.getActions().size() == 1 && securityProperties.getActions().contains(XMLSecurityConstants.ENCRYPTION);
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessor
    public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.ext.InputProcessor
    public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
        if (!this.decryptOnly && this.internalBufferProcessor == null) {
            this.internalBufferProcessor = new InternalBufferProcessor(getSecurityProperties());
            inputProcessorChain.addProcessor(this.internalBufferProcessor);
        }
        XMLSecEvent xmlSecEvent = inputProcessorChain.processEvent();
        if (1 == xmlSecEvent.getEventType()) {
            final XMLSecStartElement xmlSecStartElement = xmlSecEvent.mo458asStartElement();
            if (!this.decryptOnly && xmlSecStartElement.getName().equals(XMLSecurityConstants.TAG_dsig_Signature)) {
                if (this.signatureElementFound) {
                    throw new XMLSecurityException("stax.multipleSignaturesNotSupported");
                }
                this.signatureElementFound = true;
                this.startIndexForProcessor = this.internalBufferProcessor.getXmlSecEventList().size() - 1;
            } else if (xmlSecStartElement.getName().equals(XMLSecurityConstants.TAG_xenc_EncryptedData)) {
                this.encryptedDataElementFound = true;
                XMLDecryptInputProcessor decryptInputProcessor = new XMLDecryptInputProcessor(getSecurityProperties());
                decryptInputProcessor.setPhase(XMLSecurityConstants.Phase.PREPROCESSING);
                decryptInputProcessor.addAfterProcessor(XMLEventReaderInputProcessor.class.getName());
                decryptInputProcessor.addBeforeProcessor(XMLSecurityInputProcessor.class.getName());
                decryptInputProcessor.addBeforeProcessor(InternalBufferProcessor.class.getName());
                inputProcessorChain.addProcessor(decryptInputProcessor);
                if (!this.decryptOnly) {
                    this.internalBufferProcessor.getXmlSecEventList().pollFirst();
                }
                AbstractInputProcessor abstractInputProcessor = new AbstractInputProcessor(getSecurityProperties()) { // from class: org.apache.xml.security.stax.impl.processor.input.XMLSecurityInputProcessor.1
                    @Override // org.apache.xml.security.stax.ext.InputProcessor
                    public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain2) throws XMLSecurityException, XMLStreamException {
                        return processEvent(inputProcessorChain2);
                    }

                    @Override // org.apache.xml.security.stax.ext.InputProcessor
                    public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain2) throws XMLSecurityException, XMLStreamException {
                        inputProcessorChain2.removeProcessor(this);
                        return xmlSecStartElement;
                    }
                };
                abstractInputProcessor.setPhase(XMLSecurityConstants.Phase.PREPROCESSING);
                abstractInputProcessor.addBeforeProcessor(decryptInputProcessor);
                inputProcessorChain.addProcessor(abstractInputProcessor);
                inputProcessorChain.reset();
                xmlSecEvent = inputProcessorChain.processEvent();
                if (!this.decryptOnly && xmlSecEvent.isStartElement() && xmlSecEvent.mo458asStartElement().getName().equals(XMLSecurityConstants.TAG_dsig_Signature) && !this.signatureElementFound) {
                    throw new XMLSecurityException("Internal error");
                }
            }
        } else if (2 == xmlSecEvent.getEventType()) {
            XMLSecEndElement xmlSecEndElement = xmlSecEvent.mo457asEndElement();
            if (this.signatureElementFound && xmlSecEndElement.getName().equals(XMLSecurityConstants.TAG_dsig_Signature)) {
                XMLSignatureInputHandler inputHandler = new XMLSignatureInputHandler();
                ArrayDeque<XMLSecEvent> xmlSecEventList = this.internalBufferProcessor.getXmlSecEventList();
                inputHandler.handle(inputProcessorChain, getSecurityProperties(), xmlSecEventList, Integer.valueOf(this.startIndexForProcessor));
                inputProcessorChain.removeProcessor(this.internalBufferProcessor);
                InternalReplayProcessor internalReplayProcessor = new InternalReplayProcessor(getSecurityProperties(), xmlSecEventList);
                internalReplayProcessor.addBeforeProcessor(XMLSignatureReferenceVerifyInputProcessor.class.getName());
                inputProcessorChain.addProcessor(internalReplayProcessor);
                InputProcessorChain subInputProcessorChain = inputProcessorChain.createSubChain(this, false);
                while (!xmlSecEventList.isEmpty()) {
                    subInputProcessorChain.reset();
                    subInputProcessorChain.processEvent();
                }
                inputProcessorChain.getProcessors().clear();
                inputProcessorChain.getProcessors().addAll(subInputProcessorChain.getProcessors());
            }
        }
        return xmlSecEvent;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.ext.AbstractInputProcessor, org.apache.xml.security.stax.ext.InputProcessor
    public void doFinal(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
        if (!this.signatureElementFound && !this.encryptedDataElementFound) {
            throw new XMLSecurityException("stax.unsecuredMessage");
        }
        super.doFinal(inputProcessorChain);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/processor/input/XMLSecurityInputProcessor$InternalBufferProcessor.class */
    public class InternalBufferProcessor extends AbstractInputProcessor {
        private final ArrayDeque<XMLSecEvent> xmlSecEventList;

        InternalBufferProcessor(XMLSecurityProperties securityProperties) {
            super(securityProperties);
            this.xmlSecEventList = new ArrayDeque<>();
            setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
            addBeforeProcessor(XMLSecurityInputProcessor.class.getName());
        }

        public ArrayDeque<XMLSecEvent> getXmlSecEventList() {
            return this.xmlSecEventList;
        }

        @Override // org.apache.xml.security.stax.ext.InputProcessor
        public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
            return null;
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
        @Override // org.apache.xml.security.stax.ext.InputProcessor
        public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
            XMLSecEvent xmlSecEvent = inputProcessorChain.processEvent();
            this.xmlSecEventList.push(xmlSecEvent);
            return xmlSecEvent;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/processor/input/XMLSecurityInputProcessor$InternalReplayProcessor.class */
    public static class InternalReplayProcessor extends AbstractInputProcessor {
        private final ArrayDeque<XMLSecEvent> xmlSecEventList;

        public InternalReplayProcessor(XMLSecurityProperties securityProperties, ArrayDeque<XMLSecEvent> xmlSecEventList) {
            super(securityProperties);
            this.xmlSecEventList = xmlSecEventList;
        }

        @Override // org.apache.xml.security.stax.ext.InputProcessor
        public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
            return null;
        }

        @Override // org.apache.xml.security.stax.ext.InputProcessor
        public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
            if (!this.xmlSecEventList.isEmpty()) {
                return this.xmlSecEventList.pollLast();
            }
            inputProcessorChain.removeProcessor(this);
            return inputProcessorChain.processEvent();
        }
    }
}
