package org.apache.xml.security.stax.impl.processor.input;

import com.twelvemonkeys.imageio.metadata.exif.EXIF;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.binding.xmldsig.KeyInfoType;
import org.apache.xml.security.binding.xmlenc.EncryptedDataType;
import org.apache.xml.security.binding.xmlenc.EncryptedKeyType;
import org.apache.xml.security.binding.xmlenc.ReferenceList;
import org.apache.xml.security.binding.xmlenc.ReferenceType;
import org.apache.xml.security.binding.xop.Include;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.ConfigurationProperties;
import org.apache.xml.security.stax.ext.AbstractInputProcessor;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.UncheckedXMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.XMLSecurityEventReader;
import org.apache.xml.security.stax.impl.util.FullyBufferedOutputStream;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.impl.util.IVSplittingOutputStream;
import org.apache.xml.security.stax.impl.util.MultiInputStream;
import org.apache.xml.security.stax.impl.util.ReplaceableOuputStream;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.stax.securityToken.SecurityTokenFactory;
import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;
import org.apache.xml.security.utils.UnsyncByteArrayInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/processor/input/AbstractDecryptInputProcessor.class */
public abstract class AbstractDecryptInputProcessor extends AbstractInputProcessor {
    private static final transient Logger LOG = LoggerFactory.getLogger((Class<?>) AbstractDecryptInputProcessor.class);
    protected static final Integer maximumAllowedXMLStructureDepth = Integer.valueOf(ConfigurationProperties.getProperty("MaximumAllowedXMLStructureDepth"));
    protected static final Integer maximumAllowedEncryptedDataEvents = Integer.valueOf(ConfigurationProperties.getProperty("MaximumAllowedEncryptedDataEvents"));
    private final KeyInfoType keyInfoType;
    private final Map<String, ReferenceType> references;
    private final List<ReferenceType> processedReferences;
    private final String uuid;
    private final QName wrapperElementName;
    private final ArrayDeque<XMLSecEvent> tmpXmlEventList;

    protected abstract AbstractDecryptedEventReaderInputProcessor newDecryptedEventReaderInputProcessor(boolean z, XMLSecStartElement xMLSecStartElement, EncryptedDataType encryptedDataType, InboundSecurityToken inboundSecurityToken, InboundSecurityContext inboundSecurityContext) throws XMLSecurityException;

    protected abstract void handleSecurityToken(InboundSecurityToken inboundSecurityToken, InboundSecurityContext inboundSecurityContext, EncryptedDataType encryptedDataType) throws XMLSecurityException;

    protected abstract void handleEncryptedContent(InputProcessorChain inputProcessorChain, XMLSecStartElement xMLSecStartElement, InboundSecurityToken inboundSecurityToken, EncryptedDataType encryptedDataType) throws XMLSecurityException;

    protected abstract void handleCipherReference(InputProcessorChain inputProcessorChain, EncryptedDataType encryptedDataType, Cipher cipher, InboundSecurityToken inboundSecurityToken) throws XMLSecurityException;

    protected abstract InputStream handleXOPInclude(InputProcessorChain inputProcessorChain, EncryptedDataType encryptedDataType, String str, Cipher cipher, InboundSecurityToken inboundSecurityToken) throws XMLSecurityException;

    public AbstractDecryptInputProcessor(XMLSecurityProperties securityProperties) throws XMLSecurityException {
        super(securityProperties);
        this.uuid = IDGenerator.generateID(null);
        this.wrapperElementName = new QName("http://dummy", "dummy", this.uuid);
        this.tmpXmlEventList = new ArrayDeque<>();
        this.keyInfoType = null;
        this.references = null;
        this.processedReferences = null;
    }

    public AbstractDecryptInputProcessor(KeyInfoType keyInfoType, ReferenceList referenceList, XMLSecurityProperties securityProperties) throws XMLSecurityException {
        super(securityProperties);
        this.uuid = IDGenerator.generateID(null);
        this.wrapperElementName = new QName("http://dummy", "dummy", this.uuid);
        this.tmpXmlEventList = new ArrayDeque<>();
        this.keyInfoType = keyInfoType;
        List<JAXBElement<ReferenceType>> dataReferenceOrKeyReference = referenceList.getDataReferenceOrKeyReference();
        int dataReferenceOrKeyReferenceSize = dataReferenceOrKeyReference.size();
        this.references = new HashMap((int) Math.ceil(((double) dataReferenceOrKeyReferenceSize) / 0.75d));
        this.processedReferences = new ArrayList(dataReferenceOrKeyReferenceSize);
        for (JAXBElement<ReferenceType> referenceTypeJAXBElement : dataReferenceOrKeyReference) {
            ReferenceType referenceType = referenceTypeJAXBElement.getValue();
            if (referenceType.getURI() == null) {
                throw new XMLSecurityException("stax.emptyReferenceURI");
            }
            this.references.put(XMLSecurityUtils.dropReferenceMarker(referenceType.getURI()), referenceType);
        }
    }

    public Map<String, ReferenceType> getReferences() {
        return this.references;
    }

    public List<ReferenceType> getProcessedReferences() {
        return this.processedReferences;
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessor
    public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
        return processEvent(inputProcessorChain, true);
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessor
    public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
        return processEvent(inputProcessorChain, false);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private XMLSecEvent processEvent(InputProcessorChain inputProcessorChain, boolean isSecurityHeaderEvent) throws XMLSecurityException, XMLStreamException {
        XMLSecEvent xMLSecEventProcessEvent;
        XMLSecEvent nextEvent;
        InputStream decryptInputStream;
        if (!this.tmpXmlEventList.isEmpty()) {
            return this.tmpXmlEventList.pollLast();
        }
        if (isSecurityHeaderEvent) {
            xMLSecEventProcessEvent = inputProcessorChain.processHeaderEvent();
        } else {
            xMLSecEventProcessEvent = inputProcessorChain.processEvent();
        }
        XMLSecEvent xmlSecEvent = xMLSecEventProcessEvent;
        boolean encryptedHeader = false;
        if (xmlSecEvent.getEventType() == 1) {
            XMLSecStartElement xmlSecStartElement = xmlSecEvent.mo1376asStartElement();
            if (xmlSecStartElement.getName().equals(XMLSecurityConstants.TAG_wsse11_EncryptedHeader)) {
                xmlSecEvent = readAndBufferEncryptedHeader(inputProcessorChain, isSecurityHeaderEvent, xmlSecEvent);
                xmlSecStartElement = xmlSecEvent.mo1376asStartElement();
                encryptedHeader = true;
            }
            if (xmlSecStartElement.getName().equals(XMLSecurityConstants.TAG_xenc_EncryptedData)) {
                ReferenceType referenceType = null;
                if (this.references != null) {
                    referenceType = matchesReferenceId(xmlSecStartElement);
                    if (referenceType == null) {
                        if (!this.tmpXmlEventList.isEmpty()) {
                            return this.tmpXmlEventList.pollLast();
                        }
                        return xmlSecEvent;
                    }
                    if (this.processedReferences.contains(referenceType)) {
                        throw new XMLSecurityException("signature.Verification.MultipleIDs");
                    }
                    this.processedReferences.add(referenceType);
                }
                this.tmpXmlEventList.clear();
                InputProcessorChain subInputProcessorChain = inputProcessorChain.createSubChain(this);
                EncryptedDataType encryptedDataType = parseEncryptedDataStructure(isSecurityHeaderEvent, xmlSecEvent, subInputProcessorChain);
                if (encryptedDataType.getId() == null) {
                    encryptedDataType.setId(IDGenerator.generateID(null));
                }
                InboundSecurityToken inboundSecurityToken = getSecurityToken(inputProcessorChain, xmlSecStartElement, encryptedDataType);
                handleSecurityToken(inboundSecurityToken, inputProcessorChain.getSecurityContext(), encryptedDataType);
                String algorithmURI = encryptedDataType.getEncryptionMethod().getAlgorithm();
                int ivLength = JCEMapper.getIVLengthFromURI(algorithmURI) / 8;
                Cipher symCipher = getCipher(algorithmURI);
                if (encryptedDataType.getCipherData().getCipherReference() != null) {
                    handleCipherReference(inputProcessorChain, encryptedDataType, symCipher, inboundSecurityToken);
                    subInputProcessorChain.reset();
                    if (isSecurityHeaderEvent) {
                        return subInputProcessorChain.processHeaderEvent();
                    }
                    return subInputProcessorChain.processEvent();
                }
                XMLSecStartElement parentXMLSecStartElement = xmlSecStartElement.getParentXMLSecStartElement();
                if (encryptedHeader) {
                    parentXMLSecStartElement = parentXMLSecStartElement.getParentXMLSecStartElement();
                }
                AbstractDecryptedEventReaderInputProcessor decryptedEventReaderInputProcessor = newDecryptedEventReaderInputProcessor(encryptedHeader, parentXMLSecStartElement, encryptedDataType, inboundSecurityToken, inputProcessorChain.getSecurityContext());
                inputProcessorChain.addProcessor(decryptedEventReaderInputProcessor);
                inputProcessorChain.getDocumentContext().setIsInEncryptedContent(inputProcessorChain.getProcessors().indexOf(decryptedEventReaderInputProcessor), decryptedEventReaderInputProcessor);
                if (SecurePart.Modifier.Content.getModifier().equals(encryptedDataType.getType())) {
                    handleEncryptedContent(inputProcessorChain, xmlSecStartElement.getParentXMLSecStartElement(), inboundSecurityToken, encryptedDataType);
                }
                subInputProcessorChain.reset();
                if (isSecurityHeaderEvent) {
                    nextEvent = subInputProcessorChain.processHeaderEvent();
                } else {
                    nextEvent = subInputProcessorChain.processEvent();
                }
                if (nextEvent.isStartElement() && nextEvent.mo1376asStartElement().getName().equals(XMLSecurityConstants.TAG_XOP_INCLUDE)) {
                    try {
                        Deque<XMLSecEvent> xmlSecEvents = new ArrayDeque<>();
                        xmlSecEvents.push(nextEvent);
                        xmlSecEvents.push(XMLSecEventFactory.createXmlSecEndElement(XMLSecurityConstants.TAG_XOP_INCLUDE));
                        Unmarshaller unmarshaller = XMLSecurityConstants.getJaxbUnmarshaller(getSecurityProperties().isDisableSchemaValidation());
                        JAXBElement<Include> includeJAXBElement = (JAXBElement) unmarshaller.unmarshal(new XMLSecurityEventReader(xmlSecEvents, 0));
                        Include include = includeJAXBElement.getValue();
                        String href = include.getHref();
                        decryptInputStream = handleXOPInclude(inputProcessorChain, encryptedDataType, href, symCipher, inboundSecurityToken);
                    } catch (JAXBException e) {
                        throw new XMLSecurityException(e);
                    }
                } else {
                    DecryptionThread decryptionThread = new DecryptionThread(subInputProcessorChain, isSecurityHeaderEvent, nextEvent);
                    Key decryptionKey = inboundSecurityToken.getSecretKey(algorithmURI, XMLSecurityConstants.Enc, encryptedDataType.getId());
                    decryptionThread.setSecretKey(XMLSecurityUtils.prepareSecretKey(algorithmURI, decryptionKey.getEncoded()));
                    decryptionThread.setSymmetricCipher(symCipher);
                    decryptionThread.setIvLength(ivLength);
                    Thread thread = new Thread(decryptionThread);
                    thread.setPriority(6);
                    thread.setName("decryption thread");
                    thread.setUncaughtExceptionHandler(decryptedEventReaderInputProcessor);
                    decryptedEventReaderInputProcessor.setDecryptionThread(thread);
                    LOG.debug("Starting decryption thread");
                    thread.start();
                    decryptInputStream = decryptionThread.getPipedInputStream();
                }
                try {
                    InputStream prologInputStream = writeWrapperStartElement(xmlSecStartElement);
                    InputStream epilogInputStream = writeWrapperEndElement();
                    XMLStreamReader xmlStreamReader = ((XMLInputFactory) inputProcessorChain.getSecurityContext().get(XMLSecurityConstants.XMLINPUTFACTORY)).createXMLStreamReader(new MultiInputStream(prologInputStream, applyTransforms(referenceType, decryptInputStream), epilogInputStream), StandardCharsets.UTF_8.name());
                    forwardToWrapperElement(xmlStreamReader);
                    decryptedEventReaderInputProcessor.setXmlStreamReader(xmlStreamReader);
                    if (isSecurityHeaderEvent) {
                        return decryptedEventReaderInputProcessor.processHeaderEvent(inputProcessorChain);
                    }
                    return decryptedEventReaderInputProcessor.processEvent(inputProcessorChain);
                } catch (IOException e2) {
                    throw new XMLSecurityException(e2);
                }
            }
        }
        return xmlSecEvent;
    }

    protected InputStream applyTransforms(ReferenceType referenceType, InputStream inputStream) throws XMLSecurityException {
        return inputStream;
    }

    private InputStream writeWrapperStartElement(XMLSecStartElement xmlSecStartElement) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('<');
        stringBuilder.append(this.wrapperElementName.getPrefix());
        stringBuilder.append(':');
        stringBuilder.append(this.wrapperElementName.getLocalPart());
        stringBuilder.append(" xmlns:");
        stringBuilder.append(this.wrapperElementName.getPrefix());
        stringBuilder.append("=\"");
        stringBuilder.append(this.wrapperElementName.getNamespaceURI());
        stringBuilder.append('\"');
        List<XMLSecNamespace> comparableNamespacesToApply = new ArrayList<>();
        List<XMLSecNamespace> comparableNamespaceList = new ArrayList<>();
        xmlSecStartElement.getNamespacesFromCurrentScope(comparableNamespaceList);
        for (int i = comparableNamespaceList.size() - 1; i >= 0; i--) {
            XMLSecNamespace comparableNamespace = comparableNamespaceList.get(i);
            if (!comparableNamespacesToApply.contains(comparableNamespace)) {
                comparableNamespacesToApply.add(comparableNamespace);
                stringBuilder.append(' ');
                String prefix = comparableNamespace.getPrefix();
                String uri = comparableNamespace.getNamespaceURI();
                if (prefix == null || prefix.isEmpty()) {
                    stringBuilder.append("xmlns=\"");
                    stringBuilder.append(uri);
                    stringBuilder.append('\"');
                } else {
                    stringBuilder.append("xmlns:");
                    stringBuilder.append(prefix);
                    stringBuilder.append("=\"");
                    stringBuilder.append(uri);
                    stringBuilder.append('\"');
                }
            }
        }
        stringBuilder.append('>');
        return new UnsyncByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private InputStream writeWrapperEndElement() throws IOException {
        return new UnsyncByteArrayInputStream(("</" + this.wrapperElementName.getPrefix() + ':' + this.wrapperElementName.getLocalPart() + '>').getBytes(StandardCharsets.UTF_8));
    }

    private void forwardToWrapperElement(XMLStreamReader xmlStreamReader) throws XMLStreamException {
        do {
            if (xmlStreamReader.getEventType() == 1 && xmlStreamReader.getName().equals(this.wrapperElementName)) {
                xmlStreamReader.next();
                return;
            }
            xmlStreamReader.next();
        } while (xmlStreamReader.hasNext());
    }

    private Cipher getCipher(String algorithmURI) throws XMLSecurityException {
        Cipher symCipher;
        try {
            String jceName = JCEMapper.translateURItoJCEID(algorithmURI);
            String jceProvider = JCEMapper.getJCEProviderFromURI(algorithmURI);
            if (jceName == null) {
                throw new XMLSecurityException("algorithms.NoSuchMap", new Object[]{algorithmURI});
            }
            if (jceProvider != null) {
                symCipher = Cipher.getInstance(jceName, jceProvider);
            } else {
                symCipher = Cipher.getInstance(jceName);
            }
            return symCipher;
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            throw new XMLSecurityException(e);
        }
    }

    private InboundSecurityToken getSecurityToken(InputProcessorChain inputProcessorChain, XMLSecStartElement xmlSecStartElement, EncryptedDataType encryptedDataType) throws XMLSecurityException {
        EncryptedKeyType encryptedKeyType;
        KeyInfoType keyInfoType = this.keyInfoType;
        if (keyInfoType == null) {
            keyInfoType = encryptedDataType.getKeyInfo();
        }
        if (keyInfoType != null && (encryptedKeyType = (EncryptedKeyType) XMLSecurityUtils.getQNameType(keyInfoType.getContent(), XMLSecurityConstants.TAG_xenc_EncryptedKey)) != null) {
            XMLEncryptedKeyInputHandler handler = new XMLEncryptedKeyInputHandler();
            handler.handle(inputProcessorChain, encryptedKeyType, xmlSecStartElement, getSecurityProperties());
            SecurityTokenProvider<? extends InboundSecurityToken> securityTokenProvider = inputProcessorChain.getSecurityContext().getSecurityTokenProvider(encryptedKeyType.getId());
            return securityTokenProvider.getSecurityToken();
        }
        return SecurityTokenFactory.getInstance().getSecurityToken(keyInfoType, SecurityTokenConstants.KeyUsage_Decryption, getSecurityProperties(), inputProcessorChain.getSecurityContext());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private EncryptedDataType parseEncryptedDataStructure(boolean isSecurityHeaderEvent, XMLSecEvent xmlSecEvent, InputProcessorChain subInputProcessorChain) throws XMLSecurityException, XMLStreamException {
        XMLSecEvent encryptedDataXMLSecEvent;
        Deque<XMLSecEvent> xmlSecEvents = new ArrayDeque<>();
        xmlSecEvents.push(xmlSecEvent);
        int count = 0;
        int keyInfoCount = 0;
        while (true) {
            subInputProcessorChain.reset();
            if (isSecurityHeaderEvent) {
                encryptedDataXMLSecEvent = subInputProcessorChain.processHeaderEvent();
            } else {
                encryptedDataXMLSecEvent = subInputProcessorChain.processEvent();
            }
            xmlSecEvents.push(encryptedDataXMLSecEvent);
            count++;
            if (count >= maximumAllowedEncryptedDataEvents.intValue()) {
                throw new XMLSecurityException("stax.xmlStructureSizeExceeded", new Object[]{maximumAllowedEncryptedDataEvents});
            }
            if (encryptedDataXMLSecEvent.getEventType() == 1 && encryptedDataXMLSecEvent.mo1376asStartElement().getName().equals(XMLSecurityConstants.TAG_dsig_KeyInfo)) {
                keyInfoCount++;
            } else if (encryptedDataXMLSecEvent.getEventType() == 2 && encryptedDataXMLSecEvent.mo1375asEndElement().getName().equals(XMLSecurityConstants.TAG_dsig_KeyInfo)) {
                keyInfoCount--;
            }
            if ((encryptedDataXMLSecEvent.getEventType() == 1 && encryptedDataXMLSecEvent.mo1376asStartElement().getName().equals(XMLSecurityConstants.TAG_xenc_CipherValue)) || (encryptedDataXMLSecEvent.getEventType() == 2 && encryptedDataXMLSecEvent.mo1375asEndElement().getName().equals(XMLSecurityConstants.TAG_xenc_EncryptedData))) {
                if (keyInfoCount == 0) {
                    xmlSecEvents.push(XMLSecEventFactory.createXmlSecEndElement(XMLSecurityConstants.TAG_xenc_CipherValue));
                    xmlSecEvents.push(XMLSecEventFactory.createXmlSecEndElement(XMLSecurityConstants.TAG_xenc_CipherData));
                    xmlSecEvents.push(XMLSecEventFactory.createXmlSecEndElement(XMLSecurityConstants.TAG_xenc_EncryptedData));
                    try {
                        Unmarshaller unmarshaller = XMLSecurityConstants.getJaxbUnmarshaller(getSecurityProperties().isDisableSchemaValidation());
                        JAXBElement<EncryptedDataType> encryptedDataTypeJAXBElement = (JAXBElement) unmarshaller.unmarshal(new XMLSecurityEventReader(xmlSecEvents, 0));
                        EncryptedDataType encryptedDataType = encryptedDataTypeJAXBElement.getValue();
                        return encryptedDataType;
                    } catch (JAXBException e) {
                        throw new XMLSecurityException(e);
                    }
                }
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private XMLSecEvent readAndBufferEncryptedHeader(InputProcessorChain inputProcessorChain, boolean isSecurityHeaderEvent, XMLSecEvent xmlSecEvent) throws XMLSecurityException, XMLStreamException {
        InputProcessorChain subInputProcessorChain = inputProcessorChain.createSubChain(this);
        while (true) {
            this.tmpXmlEventList.push(xmlSecEvent);
            subInputProcessorChain.reset();
            if (isSecurityHeaderEvent) {
                xmlSecEvent = subInputProcessorChain.processHeaderEvent();
            } else {
                xmlSecEvent = subInputProcessorChain.processEvent();
            }
            if (xmlSecEvent.getEventType() == 1 && xmlSecEvent.mo1376asStartElement().getName().equals(XMLSecurityConstants.TAG_xenc_EncryptedData)) {
                this.tmpXmlEventList.push(xmlSecEvent);
                return xmlSecEvent;
            }
        }
    }

    protected ReferenceType matchesReferenceId(XMLSecStartElement xmlSecStartElement) {
        Attribute refId = getReferenceIDAttribute(xmlSecStartElement);
        if (refId != null) {
            return this.references.get(refId.getValue());
        }
        return null;
    }

    @Override // org.apache.xml.security.stax.ext.AbstractInputProcessor, org.apache.xml.security.stax.ext.InputProcessor
    public void doFinal(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
        inputProcessorChain.doFinal();
        if (this.references != null) {
            for (Map.Entry<String, ReferenceType> referenceTypeEntry : this.references.entrySet()) {
                if (!this.processedReferences.contains(referenceTypeEntry.getValue())) {
                    throw new XMLSecurityException("stax.encryption.unprocessedReferences");
                }
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/processor/input/AbstractDecryptInputProcessor$AbstractDecryptedEventReaderInputProcessor.class */
    public abstract class AbstractDecryptedEventReaderInputProcessor extends AbstractInputProcessor implements Thread.UncaughtExceptionHandler {
        private int currentXMLStructureDepth;
        private XMLStreamReader xmlStreamReader;
        private XMLSecStartElement parentXmlSecStartElement;
        private boolean encryptedHeader;
        private final InboundSecurityToken inboundSecurityToken;
        private boolean rootElementProcessed;
        private EncryptedDataType encryptedDataType;
        private Thread decryptionThread;
        private volatile Throwable thrownException;

        protected abstract void handleEncryptedElement(InputProcessorChain inputProcessorChain, XMLSecStartElement xMLSecStartElement, InboundSecurityToken inboundSecurityToken, EncryptedDataType encryptedDataType) throws XMLSecurityException;

        public AbstractDecryptedEventReaderInputProcessor(XMLSecurityProperties securityProperties, SecurePart.Modifier encryptionModifier, boolean encryptedHeader, XMLSecStartElement xmlSecStartElement, EncryptedDataType encryptedDataType, AbstractDecryptInputProcessor abstractDecryptInputProcessor, InboundSecurityToken inboundSecurityToken) {
            super(securityProperties);
            this.encryptedHeader = false;
            addAfterProcessor(abstractDecryptInputProcessor);
            this.rootElementProcessed = encryptionModifier == SecurePart.Modifier.Content;
            this.encryptedHeader = encryptedHeader;
            this.inboundSecurityToken = inboundSecurityToken;
            this.parentXmlSecStartElement = xmlSecStartElement;
            this.encryptedDataType = encryptedDataType;
            if (xmlSecStartElement != null) {
                this.currentXMLStructureDepth = xmlSecStartElement.getDocumentLevel();
            }
        }

        public void setDecryptionThread(Thread decryptionThread) {
            this.decryptionThread = decryptionThread;
        }

        public void setXmlStreamReader(XMLStreamReader xmlStreamReader) {
            this.xmlStreamReader = xmlStreamReader;
        }

        @Override // org.apache.xml.security.stax.ext.InputProcessor
        public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
            return processEvent(inputProcessorChain, true);
        }

        @Override // org.apache.xml.security.stax.ext.InputProcessor
        public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
            return processEvent(inputProcessorChain, false);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
        private XMLSecEvent processEvent(InputProcessorChain inputProcessorChain, boolean headerEvent) throws XMLSecurityException, XMLStreamException {
            QName endElement;
            XMLSecEvent endEvent;
            testAndThrowUncaughtException();
            XMLSecEvent xmlSecEvent = XMLSecEventFactory.allocate(this.xmlStreamReader, this.parentXmlSecStartElement);
            if (1 == xmlSecEvent.getEventType()) {
                this.currentXMLStructureDepth++;
                if (this.currentXMLStructureDepth > AbstractDecryptInputProcessor.maximumAllowedXMLStructureDepth.intValue()) {
                    throw new XMLSecurityException("secureProcessing.MaximumAllowedXMLStructureDepth", new Object[]{AbstractDecryptInputProcessor.maximumAllowedXMLStructureDepth});
                }
                this.parentXmlSecStartElement = xmlSecEvent.mo1376asStartElement();
                if (!this.rootElementProcessed) {
                    handleEncryptedElement(inputProcessorChain, this.parentXmlSecStartElement, this.inboundSecurityToken, this.encryptedDataType);
                    this.rootElementProcessed = true;
                }
            } else if (2 == xmlSecEvent.getEventType()) {
                this.currentXMLStructureDepth--;
                if (this.parentXmlSecStartElement != null) {
                    this.parentXmlSecStartElement = this.parentXmlSecStartElement.getParentXMLSecStartElement();
                }
                if (xmlSecEvent.mo1375asEndElement().getName().equals(AbstractDecryptInputProcessor.this.wrapperElementName)) {
                    InputProcessorChain subInputProcessorChain = inputProcessorChain.createSubChain(this);
                    if (this.encryptedHeader) {
                        endElement = XMLSecurityConstants.TAG_wsse11_EncryptedHeader;
                    } else {
                        endElement = XMLSecurityConstants.TAG_xenc_EncryptedData;
                    }
                    while (true) {
                        subInputProcessorChain.reset();
                        if (headerEvent) {
                            endEvent = subInputProcessorChain.processHeaderEvent();
                        } else {
                            endEvent = subInputProcessorChain.processEvent();
                        }
                        if (endEvent.getEventType() == 2 && endEvent.mo1375asEndElement().getName().equals(endElement)) {
                            break;
                        }
                    }
                    inputProcessorChain.getDocumentContext().unsetIsInEncryptedContent(this);
                    if (headerEvent) {
                        xmlSecEvent = inputProcessorChain.processHeaderEvent();
                    } else {
                        xmlSecEvent = inputProcessorChain.processEvent();
                    }
                    if (this.decryptionThread != null) {
                        try {
                            this.decryptionThread.join();
                            testAndThrowUncaughtException();
                        } catch (InterruptedException e) {
                            throw new XMLStreamException(e);
                        }
                    }
                    inputProcessorChain.removeProcessor(this);
                }
            }
            this.xmlStreamReader.next();
            return xmlSecEvent;
        }

        @Override // java.lang.Thread.UncaughtExceptionHandler
        public void uncaughtException(Thread t, Throwable e) {
            this.thrownException = e;
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
        private void testAndThrowUncaughtException() throws XMLStreamException {
            if (this.thrownException != null) {
                if (this.thrownException instanceof UncheckedXMLSecurityException) {
                    UncheckedXMLSecurityException uxse = (UncheckedXMLSecurityException) this.thrownException;
                    throw new XMLStreamException(uxse.getCause());
                }
                throw new XMLStreamException(this.thrownException.getCause());
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/processor/input/AbstractDecryptInputProcessor$DecryptionThread.class */
    static class DecryptionThread implements Runnable {
        private final InputProcessorChain inputProcessorChain;
        private final boolean header;
        private final PipedOutputStream pipedOutputStream;
        private final PipedInputStream pipedInputStream = new PipedInputStream(EXIF.TAG_FLASHPIX_VERSION);
        private Cipher symmetricCipher;
        private int ivLength;
        private Key secretKey;
        private final XMLSecEvent firstEvent;

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
        protected DecryptionThread(InputProcessorChain inputProcessorChain, boolean header, XMLSecEvent firstEvent) throws XMLSecurityException, XMLStreamException {
            this.inputProcessorChain = inputProcessorChain;
            this.header = header;
            this.firstEvent = firstEvent;
            try {
                this.pipedOutputStream = new PipedOutputStream(this.pipedInputStream);
            } catch (IOException e) {
                throw new XMLStreamException(e);
            }
        }

        public PipedInputStream getPipedInputStream() {
            return this.pipedInputStream;
        }

        private XMLSecEvent processNextEvent() throws XMLSecurityException, XMLStreamException {
            this.inputProcessorChain.reset();
            if (this.header) {
                return this.inputProcessorChain.processHeaderEvent();
            }
            return this.inputProcessorChain.processEvent();
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
        @Override // java.lang.Runnable
        public void run() throws XMLStreamException {
            OutputStream outputStream;
            try {
                final Cipher cipher = getSymmetricCipher();
                if (cipher.getAlgorithm().toUpperCase().contains("GCM")) {
                    outputStream = new FullyBufferedOutputStream(this.pipedOutputStream);
                } else {
                    outputStream = this.pipedOutputStream;
                }
                final OutputStream outputStream2 = outputStream;
                CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher) { // from class: org.apache.xml.security.stax.impl.processor.input.AbstractDecryptInputProcessor.DecryptionThread.1
                    @Override // javax.crypto.CipherOutputStream, java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
                    public void close() throws IOException {
                        super.flush();
                        try {
                            byte[] bytes = cipher.doFinal();
                            outputStream2.write(bytes);
                            outputStream2.close();
                        } catch (BadPaddingException | IllegalBlockSizeException e) {
                            throw new IOException(e);
                        }
                    }
                };
                IVSplittingOutputStream ivSplittingOutputStream = new IVSplittingOutputStream(cipherOutputStream, cipher, getSecretKey(), getIvLength());
                ReplaceableOuputStream replaceableOuputStream = new ReplaceableOuputStream(ivSplittingOutputStream);
                OutputStream base64OutputStream = new Base64OutputStream(replaceableOuputStream, false);
                ivSplittingOutputStream.setParentOutputStream(replaceableOuputStream);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(base64OutputStream, Charset.forName(this.inputProcessorChain.getDocumentContext().getEncoding()));
                XMLSecEvent xmlSecEvent = this.firstEvent;
                while (xmlSecEvent.getEventType() != 2) {
                    if (xmlSecEvent.getEventType() == 4) {
                        char[] data = xmlSecEvent.mo1374asCharacters().getText();
                        outputStreamWriter.write(data);
                        xmlSecEvent = processNextEvent();
                    } else {
                        throw new XMLSecurityException("stax.unexpectedXMLEvent", new Object[]{XMLSecurityUtils.getXMLEventAsString(xmlSecEvent)});
                    }
                }
                outputStreamWriter.close();
                if (this.secretKey instanceof Destroyable) {
                    try {
                        ((Destroyable) this.secretKey).destroy();
                    } catch (DestroyFailedException e) {
                        AbstractDecryptInputProcessor.LOG.debug("Error destroying key: {}", e.getMessage());
                    }
                }
                AbstractDecryptInputProcessor.LOG.debug("Decryption thread finished");
            } catch (Exception e2) {
                try {
                    this.pipedOutputStream.close();
                } catch (IOException e3) {
                }
                throw new UncheckedXMLSecurityException(e2);
            }
        }

        protected Cipher getSymmetricCipher() {
            return this.symmetricCipher;
        }

        protected void setSymmetricCipher(Cipher symmetricCipher) {
            this.symmetricCipher = symmetricCipher;
        }

        int getIvLength() {
            return this.ivLength;
        }

        void setIvLength(int ivLength) {
            this.ivLength = ivLength;
        }

        protected Key getSecretKey() {
            return this.secretKey;
        }

        protected void setSecretKey(Key secretKey) {
            this.secretKey = secretKey;
        }
    }
}
