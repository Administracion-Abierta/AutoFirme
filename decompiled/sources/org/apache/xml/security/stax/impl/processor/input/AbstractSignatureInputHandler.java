package org.apache.xml.security.stax.impl.processor.input;

import jakarta.xml.bind.JAXBElement;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.xml.security.algorithms.implementations.SignatureBaseRSA;
import org.apache.xml.security.binding.excc14n.InclusiveNamespaces;
import org.apache.xml.security.binding.xmldsig.CanonicalizationMethodType;
import org.apache.xml.security.binding.xmldsig.SignatureMethodType;
import org.apache.xml.security.binding.xmldsig.SignatureType;
import org.apache.xml.security.binding.xmldsig.SignedInfoType;
import org.apache.xml.security.binding.xmldsigmore.RSAPSSParamsType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.stax.ext.AbstractInputSecurityHeaderHandler;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.Transformer;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm;
import org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithmFactory;
import org.apache.xml.security.stax.impl.transformer.canonicalizer.Canonicalizer20010315_Excl;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.impl.util.SignerOutputStream;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.utils.UnsyncBufferedOutputStream;
import org.apache.xml.security.utils.UnsyncByteArrayInputStream;
import org.apache.xml.security.utils.UnsyncByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/processor/input/AbstractSignatureInputHandler.class */
public abstract class AbstractSignatureInputHandler extends AbstractInputSecurityHeaderHandler {
    private static final transient Logger LOG = LoggerFactory.getLogger((Class<?>) AbstractSignatureInputHandler.class);
    private static final Set<String> C14N_ALGORITHMS;

    protected abstract void addSignatureReferenceInputProcessorToChain(InputProcessorChain inputProcessorChain, XMLSecurityProperties xMLSecurityProperties, SignatureType signatureType, InboundSecurityToken inboundSecurityToken) throws XMLSecurityException;

    protected abstract SignatureVerifier newSignatureVerifier(InputProcessorChain inputProcessorChain, XMLSecurityProperties xMLSecurityProperties, SignatureType signatureType) throws XMLSecurityException;

    static {
        Set<String> algorithms = new HashSet<>();
        algorithms.add("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
        algorithms.add("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
        algorithms.add("http://www.w3.org/2001/10/xml-exc-c14n#");
        algorithms.add("http://www.w3.org/2001/10/xml-exc-c14n#WithComments");
        algorithms.add("http://www.w3.org/2006/12/xml-c14n11");
        algorithms.add("http://www.w3.org/2006/12/xml-c14n11#WithComments");
        C14N_ALGORITHMS = Collections.unmodifiableSet(algorithms);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.ext.XMLSecurityHeaderHandler
    public void handle(InputProcessorChain inputProcessorChain, XMLSecurityProperties securityProperties, Deque<XMLSecEvent> eventQueue, Integer index) throws XMLSecurityException, XMLStreamException {
        SignatureType signatureType = (SignatureType) ((JAXBElement) parseStructure(eventQueue, index.intValue(), securityProperties)).getValue();
        if (signatureType.getSignedInfo() == null) {
            throw new XMLSecurityException("stax.signature.signedInfoMissing");
        }
        if (signatureType.getSignedInfo().getSignatureMethod() == null) {
            throw new XMLSecurityException("stax.signature.signatureMethodMissing");
        }
        if (signatureType.getSignedInfo().getCanonicalizationMethod() == null) {
            throw new XMLSecurityException("stax.signature.canonicalizationMethodMissing");
        }
        if (signatureType.getSignatureValue() == null) {
            throw new XMLSecurityException("stax.signature.signatureValueMissing");
        }
        if (signatureType.getId() == null) {
            signatureType.setId(IDGenerator.generateID(null));
        }
        InboundSecurityToken inboundSecurityToken = verifySignedInfo(inputProcessorChain, securityProperties, signatureType, eventQueue, index.intValue());
        addSignatureReferenceInputProcessorToChain(inputProcessorChain, securityProperties, signatureType, inboundSecurityToken);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected InboundSecurityToken verifySignedInfo(InputProcessorChain inputProcessorChain, XMLSecurityProperties securityProperties, SignatureType signatureType, Deque<XMLSecEvent> eventDeque, int index) throws XMLSecurityException, XMLStreamException {
        Iterator<XMLSecEvent> iterator;
        String c14NMethod = signatureType.getSignedInfo().getCanonicalizationMethod().getAlgorithm();
        if (c14NMethod != null && C14N_ALGORITHMS.contains(c14NMethod)) {
            iterator = eventDeque.descendingIterator();
            for (int i = 0; i < index; i++) {
                iterator.next();
            }
        } else {
            iterator = reparseSignedInfo(inputProcessorChain, securityProperties, signatureType, eventDeque, index).descendingIterator();
        }
        SignatureVerifier signatureVerifier = newSignatureVerifier(inputProcessorChain, securityProperties, signatureType);
        while (iterator.hasNext()) {
            try {
                XMLSecEvent xmlSecEvent = iterator.next();
                if (1 == xmlSecEvent.getEventType() && xmlSecEvent.mo1376asStartElement().getName().equals(XMLSecurityConstants.TAG_dsig_SignedInfo)) {
                    signatureVerifier.processEvent(xmlSecEvent);
                    break;
                }
            } catch (XMLStreamException e) {
                throw new XMLSecurityException((Exception) e);
            }
        }
        while (iterator.hasNext()) {
            XMLSecEvent xmlSecEvent2 = iterator.next();
            signatureVerifier.processEvent(xmlSecEvent2);
            if (2 == xmlSecEvent2.getEventType() && xmlSecEvent2.mo1375asEndElement().getName().equals(XMLSecurityConstants.TAG_dsig_SignedInfo)) {
                break;
            }
        }
        signatureVerifier.doFinal();
        return signatureVerifier.getInboundSecurityToken();
    }

    /* JADX WARN: Not initialized variable reg: 13, insn: 0x017b: MOVE (r1 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = 
      (r13 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('unsynchronizedByteArrayOutputStream' org.apache.xml.security.utils.UnsyncByteArrayOutputStream)])
    , block:B:45:0x017b */
    /* JADX WARN: Not initialized variable reg: 14, insn: 0x0179: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r14 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:44:0x0179 */
    protected Deque<XMLSecEvent> reparseSignedInfo(InputProcessorChain inputProcessorChain, XMLSecurityProperties securityProperties, SignatureType signatureType, Deque<XMLSecEvent> eventDeque, int index) throws XMLSecurityException {
        Throwable th;
        UnsyncByteArrayOutputStream unsynchronizedByteArrayOutputStream;
        Deque<XMLSecEvent> signedInfoDeque = new ArrayDeque<>();
        try {
            try {
                UnsyncByteArrayOutputStream unsynchronizedByteArrayOutputStream2 = new UnsyncByteArrayOutputStream();
                Transformer transformer = XMLSecurityUtils.getTransformer(null, unsynchronizedByteArrayOutputStream2, null, signatureType.getSignedInfo().getCanonicalizationMethod().getAlgorithm(), XMLSecurityConstants.DIRECTION.IN);
                Iterator<XMLSecEvent> iterator = eventDeque.descendingIterator();
                for (int i = 0; i < index; i++) {
                    iterator.next();
                }
                while (iterator.hasNext()) {
                    XMLSecEvent xmlSecEvent = iterator.next();
                    if (1 == xmlSecEvent.getEventType() && xmlSecEvent.mo1376asStartElement().getName().equals(XMLSecurityConstants.TAG_dsig_SignedInfo)) {
                        transformer.transform(xmlSecEvent);
                        break;
                    }
                }
                while (iterator.hasNext()) {
                    XMLSecEvent xmlSecEvent2 = iterator.next();
                    transformer.transform(xmlSecEvent2);
                    if (2 == xmlSecEvent2.getEventType() && xmlSecEvent2.mo1375asEndElement().getName().equals(XMLSecurityConstants.TAG_dsig_SignedInfo)) {
                        break;
                    }
                }
                transformer.doFinal();
                InputStream is = new UnsyncByteArrayInputStream(unsynchronizedByteArrayOutputStream2.toByteArray());
                try {
                    XMLStreamReader xmlStreamReader = ((XMLInputFactory) inputProcessorChain.getSecurityContext().get(XMLSecurityConstants.XMLINPUTFACTORY)).createXMLStreamReader(is);
                    while (xmlStreamReader.hasNext()) {
                        signedInfoDeque.push(XMLSecEventFactory.allocate(xmlStreamReader, null));
                        xmlStreamReader.next();
                    }
                    SignedInfoType signedInfoType = (SignedInfoType) ((JAXBElement) parseStructure(signedInfoDeque, 0, securityProperties)).getValue();
                    signatureType.setSignedInfo(signedInfoType);
                    $closeResource(null, is);
                    $closeResource(null, unsynchronizedByteArrayOutputStream2);
                    return signedInfoDeque;
                } catch (Throwable th2) {
                    $closeResource(null, is);
                    throw th2;
                }
            } catch (Throwable th3) {
                $closeResource(th, unsynchronizedByteArrayOutputStream);
                throw th3;
            }
        } catch (XMLStreamException | IOException e) {
            throw new XMLSecurityException((Exception) e);
        }
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) throws Exception {
        if (x0 == null) {
            x1.close();
            return;
        }
        try {
            x1.close();
        } catch (Throwable th) {
            x0.addSuppressed(th);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/processor/input/AbstractSignatureInputHandler$SignatureVerifier.class */
    public abstract class SignatureVerifier {
        private final SignatureType signatureType;
        private final InboundSecurityToken inboundSecurityToken;
        private SignerOutputStream signerOutputStream;
        private OutputStream bufferedSignerOutputStream;
        private Transformer transformer;

        protected abstract InboundSecurityToken retrieveSecurityToken(SignatureType signatureType, XMLSecurityProperties xMLSecurityProperties, InboundSecurityContext inboundSecurityContext) throws XMLSecurityException;

        public SignatureVerifier(SignatureType signatureType, InboundSecurityContext inboundSecurityContext, XMLSecurityProperties securityProperties) throws XMLSecurityException {
            this.signatureType = signatureType;
            InboundSecurityToken inboundSecurityToken = retrieveSecurityToken(signatureType, securityProperties, inboundSecurityContext);
            this.inboundSecurityToken = inboundSecurityToken;
            createSignatureAlgorithm(inboundSecurityToken, signatureType);
        }

        public InboundSecurityToken getInboundSecurityToken() {
            return this.inboundSecurityToken;
        }

        protected void createSignatureAlgorithm(InboundSecurityToken inboundSecurityToken, SignatureType signatureType) throws XMLSecurityException {
            Key verifyKey;
            String algorithmURI = signatureType.getSignedInfo().getSignatureMethod().getAlgorithm();
            if (inboundSecurityToken.isAsymmetric()) {
                verifyKey = inboundSecurityToken.getPublicKey(algorithmURI, XMLSecurityConstants.Asym_Sig, signatureType.getId());
            } else {
                verifyKey = inboundSecurityToken.getSecretKey(algorithmURI, XMLSecurityConstants.Sym_Sig, signatureType.getId());
                if (verifyKey != null) {
                    verifyKey = XMLSecurityUtils.prepareSecretKey(algorithmURI, verifyKey.getEncoded());
                }
            }
            if (verifyKey == null) {
                throw new XMLSecurityException("KeyInfo.nokey", new Object[]{"the inbound security token"});
            }
            try {
                SignatureAlgorithm signatureAlgorithm = SignatureAlgorithmFactory.getInstance().getSignatureAlgorithm(algorithmURI);
                if (XMLSignature.ALGO_ID_SIGNATURE_RSA_PSS.equals(algorithmURI)) {
                    PSSParameterSpec spec = rsaPSSParameterSpec(signatureType);
                    signatureAlgorithm.engineSetParameter(spec);
                }
                signatureAlgorithm.engineInitVerify(verifyKey);
                this.signerOutputStream = new SignerOutputStream(signatureAlgorithm);
                this.bufferedSignerOutputStream = new UnsyncBufferedOutputStream(this.signerOutputStream);
                CanonicalizationMethodType canonicalizationMethodType = signatureType.getSignedInfo().getCanonicalizationMethod();
                InclusiveNamespaces inclusiveNamespacesType = (InclusiveNamespaces) XMLSecurityUtils.getQNameType(canonicalizationMethodType.getContent(), XMLSecurityConstants.TAG_c14nExcl_InclusiveNamespaces);
                Map<String, Object> transformerProperties = null;
                if (inclusiveNamespacesType != null) {
                    transformerProperties = new HashMap<>();
                    transformerProperties.put(Canonicalizer20010315_Excl.INCLUSIVE_NAMESPACES_PREFIX_LIST, inclusiveNamespacesType.getPrefixList());
                }
                this.transformer = XMLSecurityUtils.getTransformer(null, this.bufferedSignerOutputStream, transformerProperties, canonicalizationMethodType.getAlgorithm(), XMLSecurityConstants.DIRECTION.IN);
                if (verifyKey instanceof Destroyable) {
                    try {
                        ((Destroyable) verifyKey).destroy();
                    } catch (DestroyFailedException e) {
                        AbstractSignatureInputHandler.LOG.debug("Error destroying key: {}", e.getMessage());
                    }
                }
            } catch (NoSuchAlgorithmException | NoSuchProviderException e2) {
                throw new XMLSecurityException(e2);
            }
        }

        private PSSParameterSpec rsaPSSParameterSpec(SignatureType signatureType) throws XMLSecurityException {
            SignatureMethodType signatureMethod = signatureType.getSignedInfo().getSignatureMethod();
            RSAPSSParamsType rsapssParams = null;
            for (Object o : signatureMethod.getContent()) {
                if (o instanceof RSAPSSParamsType) {
                    rsapssParams = (RSAPSSParamsType) o;
                    break;
                }
            }
            if (rsapssParams == null) {
                throw new XMLSecurityException("algorithms.MissingRSAPSSParams");
            }
            String digestMethod = rsapssParams.getDigestMethod() == null ? SignatureBaseRSA.SignatureRSASSAPSS.DigestAlgorithm.SHA256.getXmlDigestAlgorithm() : rsapssParams.getDigestMethod().getAlgorithm();
            String maskGenerationDigestMethod = rsapssParams.getMaskGenerationFunction() == null ? SignatureBaseRSA.SignatureRSASSAPSS.DigestAlgorithm.SHA256.getXmlDigestAlgorithm() : rsapssParams.getMaskGenerationFunction().getDigestMethod().getAlgorithm();
            SignatureBaseRSA.SignatureRSASSAPSS.DigestAlgorithm digestAlgorithm = SignatureBaseRSA.SignatureRSASSAPSS.DigestAlgorithm.fromXmlDigestAlgorithm(digestMethod);
            int saltLength = rsapssParams.getSaltLength() == null ? digestAlgorithm.getSaltLength() : rsapssParams.getSaltLength().intValue();
            int trailerField = rsapssParams.getTrailerField() == null ? 1 : rsapssParams.getTrailerField().intValue();
            String maskDigestAlgorithm = SignatureBaseRSA.SignatureRSASSAPSS.DigestAlgorithm.fromXmlDigestAlgorithm(maskGenerationDigestMethod).getDigestAlgorithm();
            return new PSSParameterSpec(digestAlgorithm.getDigestAlgorithm(), "MGF1", new MGF1ParameterSpec(maskDigestAlgorithm), saltLength, trailerField);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
        protected void processEvent(XMLSecEvent xmlSecEvent) throws XMLStreamException {
            this.transformer.transform(xmlSecEvent);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
        protected void doFinal() throws XMLSecurityException, XMLStreamException {
            try {
                this.transformer.doFinal();
                this.bufferedSignerOutputStream.close();
                if (!this.signerOutputStream.verify(this.signatureType.getSignatureValue().getValue())) {
                    throw new XMLSecurityException("errorMessages.InvalidSignatureValueException");
                }
            } catch (IOException | XMLStreamException e) {
                throw new XMLSecurityException(e);
            }
        }
    }
}
