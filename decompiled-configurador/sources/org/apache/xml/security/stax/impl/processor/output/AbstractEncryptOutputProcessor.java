package org.apache.xml.security.stax.impl.processor.output;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.encryption.XMLCipherUtil;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.AbstractOutputProcessor;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecCharacters;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.EncryptionPartDef;
import org.apache.xml.security.stax.impl.XMLSecurityEventWriter;
import org.apache.xml.security.stax.impl.util.TrimmerOutputStream;
import org.apache.xml.security.utils.XMLUtils;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/processor/output/AbstractEncryptOutputProcessor.class */
public abstract class AbstractEncryptOutputProcessor extends AbstractOutputProcessor {
    private static final XMLSecStartElement wrapperStartElement = XMLSecEventFactory.createXmlSecStartElement(new QName("a"), (List<XMLSecAttribute>) null, (List<XMLSecNamespace>) null);
    private static final XMLSecEndElement wrapperEndElement = XMLSecEventFactory.createXmlSecEndElement(new QName("a"));
    private AbstractInternalEncryptionOutputProcessor activeInternalEncryptionOutputProcessor;

    @Override // org.apache.xml.security.stax.ext.OutputProcessor
    public abstract void processEvent(XMLSecEvent xMLSecEvent, OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException;

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.ext.AbstractOutputProcessor, org.apache.xml.security.stax.ext.OutputProcessor
    public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
        doFinalInternal(outputProcessorChain);
        super.doFinal(outputProcessorChain);
    }

    protected void doFinalInternal(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        verifyEncryptionParts(outputProcessorChain);
    }

    protected void verifyEncryptionParts(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        List<EncryptionPartDef> encryptionPartDefs = outputProcessorChain.getSecurityContext().getAsList(EncryptionPartDef.class);
        Map<Object, SecurePart> dynamicSecureParts = outputProcessorChain.getSecurityContext().getAsMap(XMLSecurityConstants.ENCRYPTION_PARTS);
        for (Map.Entry<Object, SecurePart> securePartEntry : dynamicSecureParts.entrySet()) {
            SecurePart securePart = securePartEntry.getValue();
            if (securePart.isRequired() && !findSecurePart(securePart, encryptionPartDefs)) {
                throw new XMLSecurityException("stax.encryption.securePartNotFound", new Object[]{securePart.getName()});
            }
        }
    }

    private boolean findSecurePart(SecurePart securePart, List<EncryptionPartDef> encryptionPartDefs) {
        if (encryptionPartDefs == null) {
            return false;
        }
        for (EncryptionPartDef encryptionPartDef : encryptionPartDefs) {
            if (encryptionPartDef.getSecurePart() == securePart) {
                return true;
            }
        }
        return false;
    }

    protected AbstractInternalEncryptionOutputProcessor getActiveInternalEncryptionOutputProcessor() {
        return this.activeInternalEncryptionOutputProcessor;
    }

    protected void setActiveInternalEncryptionOutputProcessor(AbstractInternalEncryptionOutputProcessor activeInternalEncryptionOutputProcessor) {
        this.activeInternalEncryptionOutputProcessor = activeInternalEncryptionOutputProcessor;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/processor/output/AbstractEncryptOutputProcessor$AbstractInternalEncryptionOutputProcessor.class */
    public abstract class AbstractInternalEncryptionOutputProcessor extends AbstractOutputProcessor {
        private EncryptionPartDef encryptionPartDef;
        private CharacterEventGeneratorOutputStream characterEventGeneratorOutputStream;
        private XMLEventWriter xmlEventWriter;
        private OutputStream cipherOutputStream;
        private String encoding;
        private XMLSecStartElement xmlSecStartElement;
        private int elementCounter;

        protected abstract void createKeyInfoStructure(OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException;

        public AbstractInternalEncryptionOutputProcessor(EncryptionPartDef encryptionPartDef, XMLSecStartElement xmlSecStartElement, String encoding) throws XMLSecurityException {
            addBeforeProcessor(AbstractEncryptEndingOutputProcessor.class);
            addBeforeProcessor(AbstractInternalEncryptionOutputProcessor.class);
            addAfterProcessor(AbstractEncryptOutputProcessor.class);
            setEncryptionPartDef(encryptionPartDef);
            setXmlSecStartElement(xmlSecStartElement);
            setEncoding(encoding);
        }

        @Override // org.apache.xml.security.stax.ext.AbstractOutputProcessor, org.apache.xml.security.stax.ext.OutputProcessor
        public void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
            Base64OutputStream base64EncoderStream;
            String encryptionSymAlgorithm = this.securityProperties.getEncryptionSymAlgorithm();
            try {
                String jceAlgorithm = JCEMapper.translateURItoJCEID(encryptionSymAlgorithm);
                if (jceAlgorithm == null) {
                    throw new XMLSecurityException("algorithms.NoSuchMap", new Object[]{encryptionSymAlgorithm});
                }
                Cipher symmetricCipher = Cipher.getInstance(jceAlgorithm);
                int ivLen = JCEMapper.getIVLengthFromURI(encryptionSymAlgorithm) / 8;
                byte[] iv = XMLSecurityConstants.generateBytes(ivLen);
                AlgorithmParameterSpec parameterSpec = XMLCipherUtil.constructBlockCipherParameters(encryptionSymAlgorithm, iv);
                symmetricCipher.init(1, this.encryptionPartDef.getSymmetricKey(), parameterSpec);
                this.characterEventGeneratorOutputStream = AbstractEncryptOutputProcessor.this.new CharacterEventGeneratorOutputStream();
                if (XMLUtils.isIgnoreLineBreaks()) {
                    base64EncoderStream = new Base64OutputStream(this.characterEventGeneratorOutputStream, true, 0, null);
                } else {
                    base64EncoderStream = new Base64OutputStream(this.characterEventGeneratorOutputStream, true);
                }
                base64EncoderStream.write(iv);
                OutputStream outputStream = new CipherOutputStream(base64EncoderStream, symmetricCipher);
                this.cipherOutputStream = new TrimmerOutputStream(applyTransforms(outputStream), 81920, 3, 4);
                this.xmlEventWriter = new XMLSecurityEventWriter(XMLSecurityConstants.xmlOutputFactoryNonRepairingNs.createXMLStreamWriter(this.cipherOutputStream, StandardCharsets.UTF_8.name()));
                this.xmlEventWriter.add(AbstractEncryptOutputProcessor.wrapperStartElement);
                super.init(outputProcessorChain);
            } catch (NoSuchAlgorithmException e) {
                throw new XMLSecurityException(e);
            } catch (XMLStreamException e2) {
                throw new XMLSecurityException((Exception) e2);
            } catch (IOException e3) {
                throw new XMLSecurityException(e3);
            } catch (InvalidAlgorithmParameterException e4) {
                throw new XMLSecurityException(e4);
            } catch (InvalidKeyException e5) {
                throw new XMLSecurityException(e5);
            } catch (NoSuchPaddingException e6) {
                throw new XMLSecurityException(e6);
            }
        }

        protected OutputStream applyTransforms(OutputStream outputStream) throws XMLSecurityException {
            return outputStream;
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
        @Override // org.apache.xml.security.stax.ext.OutputProcessor
        public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
            switch (xmlSecEvent.getEventType()) {
                case 1:
                    XMLSecStartElement xmlSecStartElement = xmlSecEvent.mo458asStartElement();
                    if (this.elementCounter == 0 && xmlSecStartElement.getName().equals(getXmlSecStartElement().getName())) {
                        if (SecurePart.Modifier.Element == getEncryptionPartDef().getModifier()) {
                            OutputProcessorChain subOutputProcessorChain = outputProcessorChain.createSubChain(this);
                            processEventInternal(xmlSecStartElement, subOutputProcessorChain);
                            encryptEvent(xmlSecEvent);
                        } else if (SecurePart.Modifier.Content == getEncryptionPartDef().getModifier()) {
                            outputProcessorChain.createSubChain(this);
                            outputProcessorChain.processEvent(xmlSecEvent);
                            OutputProcessorChain subOutputProcessorChain2 = outputProcessorChain.createSubChain(this);
                            processEventInternal(xmlSecStartElement, subOutputProcessorChain2);
                        }
                    } else {
                        encryptEvent(xmlSecEvent);
                    }
                    this.elementCounter++;
                    break;
                case 2:
                    this.elementCounter--;
                    if (this.elementCounter == 0 && xmlSecEvent.mo457asEndElement().getName().equals(getXmlSecStartElement().getName())) {
                        OutputProcessorChain subOutputProcessorChain3 = outputProcessorChain.createSubChain(this);
                        if (SecurePart.Modifier.Element == getEncryptionPartDef().getModifier()) {
                            encryptEvent(xmlSecEvent);
                            doFinalInternal(subOutputProcessorChain3);
                        } else if (SecurePart.Modifier.Content == getEncryptionPartDef().getModifier()) {
                            doFinalInternal(subOutputProcessorChain3);
                            outputAsEvent(subOutputProcessorChain3, xmlSecEvent);
                        }
                        subOutputProcessorChain3.removeProcessor(this);
                        AbstractEncryptOutputProcessor.this.setActiveInternalEncryptionOutputProcessor(null);
                    } else {
                        encryptEvent(xmlSecEvent);
                    }
                    break;
                default:
                    encryptEvent(xmlSecEvent);
                    Deque<XMLSecCharacters> charactersBuffer = this.characterEventGeneratorOutputStream.getCharactersBuffer();
                    if (charactersBuffer.size() > 5) {
                        OutputProcessorChain subOutputProcessorChain4 = outputProcessorChain.createSubChain(this);
                        Iterator<XMLSecCharacters> charactersIterator = charactersBuffer.iterator();
                        while (charactersIterator.hasNext()) {
                            XMLSecCharacters characters = charactersIterator.next();
                            outputAsEvent(subOutputProcessorChain4, characters);
                            charactersIterator.remove();
                        }
                    }
                    break;
            }
        }

        private void encryptEvent(XMLSecEvent xmlSecEvent) throws XMLStreamException {
            this.xmlEventWriter.add(xmlSecEvent);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
        protected void processEventInternal(XMLSecStartElement xmlSecStartElement, OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
            List<XMLSecAttribute> attributes = new ArrayList<>(2);
            attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Id, getEncryptionPartDef().getEncRefId()));
            attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Type, getEncryptionPartDef().getModifier().getModifier()));
            createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptedData, true, attributes);
            List<XMLSecAttribute> attributes2 = new ArrayList<>(1);
            attributes2.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, this.securityProperties.getEncryptionSymAlgorithm()));
            createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptionMethod, false, attributes2);
            createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptionMethod);
            createKeyInfoStructure(outputProcessorChain);
            createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherData, false, (List<XMLSecAttribute>) null);
            createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherValue, false, (List<XMLSecAttribute>) null);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
        protected void doFinalInternal(OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
            try {
                this.xmlEventWriter.add(AbstractEncryptOutputProcessor.wrapperEndElement);
                this.xmlEventWriter.close();
                this.cipherOutputStream.close();
                Deque<XMLSecCharacters> charactersBuffer = this.characterEventGeneratorOutputStream.getCharactersBuffer();
                if (!charactersBuffer.isEmpty()) {
                    Iterator<XMLSecCharacters> charactersIterator = charactersBuffer.iterator();
                    while (charactersIterator.hasNext()) {
                        XMLSecCharacters characters = charactersIterator.next();
                        outputAsEvent(outputProcessorChain, characters);
                        charactersIterator.remove();
                    }
                }
                createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherValue);
                createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherData);
                createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptedData);
            } catch (IOException e) {
                throw new XMLStreamException(e);
            }
        }

        protected EncryptionPartDef getEncryptionPartDef() {
            return this.encryptionPartDef;
        }

        protected void setEncryptionPartDef(EncryptionPartDef encryptionPartDef) {
            this.encryptionPartDef = encryptionPartDef;
        }

        protected XMLSecStartElement getXmlSecStartElement() {
            return this.xmlSecStartElement;
        }

        protected void setXmlSecStartElement(XMLSecStartElement xmlSecStartElement) {
            this.xmlSecStartElement = xmlSecStartElement;
        }

        public String getEncoding() {
            return this.encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/processor/output/AbstractEncryptOutputProcessor$CharacterEventGeneratorOutputStream.class */
    public class CharacterEventGeneratorOutputStream extends OutputStream {
        private final Deque<XMLSecCharacters> charactersBuffer = new ArrayDeque();

        public CharacterEventGeneratorOutputStream() {
        }

        public Deque<XMLSecCharacters> getCharactersBuffer() {
            return this.charactersBuffer;
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            this.charactersBuffer.offer(AbstractEncryptOutputProcessor.this.createCharacters(new char[]{(char) b}));
        }

        @Override // java.io.OutputStream
        public void write(byte[] b) throws IOException {
            this.charactersBuffer.offer(AbstractEncryptOutputProcessor.this.createCharacters(AbstractEncryptOutputProcessor.this.byteToCharArray(b, 0, b.length)));
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            this.charactersBuffer.offer(AbstractEncryptOutputProcessor.this.createCharacters(AbstractEncryptOutputProcessor.this.byteToCharArray(b, off, len)));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public char[] byteToCharArray(byte[] bytes, int off, int len) {
        char[] chars = new char[len - off];
        for (int i = off; i < len; i++) {
            chars[i] = (char) bytes[i];
        }
        return chars;
    }
}
