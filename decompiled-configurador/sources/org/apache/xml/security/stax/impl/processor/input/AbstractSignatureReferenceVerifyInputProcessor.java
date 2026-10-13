package org.apache.xml.security.stax.impl.processor.input;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.binding.excc14n.InclusiveNamespaces;
import org.apache.xml.security.binding.xmldsig.ReferenceType;
import org.apache.xml.security.binding.xmldsig.SignatureType;
import org.apache.xml.security.binding.xmldsig.TransformType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.ConfigurationProperties;
import org.apache.xml.security.stax.config.ResourceResolverMapper;
import org.apache.xml.security.stax.ext.AbstractInputProcessor;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.ResourceResolver;
import org.apache.xml.security.stax.ext.Transformer;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.transformer.canonicalizer.Canonicalizer20010315_Excl;
import org.apache.xml.security.stax.impl.transformer.canonicalizer.Canonicalizer20010315_OmitCommentsTransformer;
import org.apache.xml.security.stax.impl.util.DigestOutputStream;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.impl.util.KeyValue;
import org.apache.xml.security.stax.securityEvent.AlgorithmSuiteSecurityEvent;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.utils.UnsyncBufferedOutputStream;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/processor/input/AbstractSignatureReferenceVerifyInputProcessor.class */
public abstract class AbstractSignatureReferenceVerifyInputProcessor extends AbstractInputProcessor {
    private static final transient Logger LOG = LoggerFactory.getLogger((Class<?>) AbstractSignatureReferenceVerifyInputProcessor.class);
    protected static final Integer maximumAllowedReferencesPerManifest = Integer.valueOf(ConfigurationProperties.getProperty("MaximumAllowedReferencesPerManifest"));
    protected static final Integer maximumAllowedTransformsPerReference = Integer.valueOf(ConfigurationProperties.getProperty("MaximumAllowedTransformsPerReference"));
    protected static final Boolean doNotThrowExceptionForManifests = Boolean.valueOf(ConfigurationProperties.getProperty("DoNotThrowExceptionForManifests"));
    protected static final Boolean allowNotSameDocumentReferences = Boolean.valueOf(ConfigurationProperties.getProperty("AllowNotSameDocumentReferences"));
    private final SignatureType signatureType;
    private final InboundSecurityToken inboundSecurityToken;
    private final List<KeyValue<ResourceResolver, ReferenceType>> sameDocumentReferences;
    private final List<KeyValue<ResourceResolver, ReferenceType>> externalReferences;
    private final List<ReferenceType> processedReferences;

    protected abstract void processElementPath(List<QName> list, InputProcessorChain inputProcessorChain, XMLSecEvent xMLSecEvent, ReferenceType referenceType) throws XMLSecurityException;

    public AbstractSignatureReferenceVerifyInputProcessor(InputProcessorChain inputProcessorChain, SignatureType signatureType, InboundSecurityToken inboundSecurityToken, XMLSecurityProperties securityProperties) throws XMLSecurityException {
        super(securityProperties);
        this.signatureType = signatureType;
        this.inboundSecurityToken = inboundSecurityToken;
        List<ReferenceType> referencesTypeList = signatureType.getSignedInfo().getReference();
        if (referencesTypeList.size() > maximumAllowedReferencesPerManifest.intValue()) {
            throw new XMLSecurityException("secureProcessing.MaximumAllowedReferencesPerManifest", new Object[]{Integer.valueOf(referencesTypeList.size()), maximumAllowedReferencesPerManifest});
        }
        this.sameDocumentReferences = new ArrayList(referencesTypeList.size());
        this.externalReferences = new ArrayList(referencesTypeList.size());
        this.processedReferences = new ArrayList(referencesTypeList.size());
        for (ReferenceType referenceType : referencesTypeList) {
            if (!doNotThrowExceptionForManifests.booleanValue() && "http://www.w3.org/2000/09/xmldsig#Manifest".equals(referenceType.getType())) {
                throw new XMLSecurityException("secureProcessing.DoNotThrowExceptionForManifests");
            }
            if (referenceType.getURI() == null) {
                throw new XMLSecurityException("stax.emptyReferenceURI");
            }
            if (referenceType.getId() == null) {
                referenceType.setId(IDGenerator.generateID(null));
            }
            ResourceResolver resourceResolver = ResourceResolverMapper.getResourceResolver(referenceType.getURI(), inputProcessorChain.getDocumentContext().getBaseURI());
            if (resourceResolver.isSameDocumentReference()) {
                this.sameDocumentReferences.add(new KeyValue<>(resourceResolver, referenceType));
            } else {
                if (!allowNotSameDocumentReferences.booleanValue()) {
                    throw new XMLSecurityException("secureProcessing.AllowNotSameDocumentReferences");
                }
                this.externalReferences.add(new KeyValue<>(resourceResolver, referenceType));
            }
        }
    }

    public SignatureType getSignatureType() {
        return this.signatureType;
    }

    public List<ReferenceType> getProcessedReferences() {
        return this.processedReferences;
    }

    public InboundSecurityToken getInboundSecurityToken() {
        return this.inboundSecurityToken;
    }

    @Override // org.apache.xml.security.stax.ext.InputProcessor
    public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
        return inputProcessorChain.processHeaderEvent();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.ext.InputProcessor
    public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
        XMLSecEvent xmlSecEvent = inputProcessorChain.processEvent();
        if (1 == xmlSecEvent.getEventType()) {
            XMLSecStartElement xmlSecStartElement = xmlSecEvent.mo458asStartElement();
            List<ReferenceType> referenceTypes = resolvesResource(xmlSecStartElement);
            if (!referenceTypes.isEmpty()) {
                for (int i = 0; i < referenceTypes.size(); i++) {
                    ReferenceType referenceType = referenceTypes.get(i);
                    if (this.processedReferences.contains(referenceType)) {
                        throw new XMLSecurityException("signature.Verification.MultipleIDs", new Object[]{referenceType.getURI()});
                    }
                    InternalSignatureReferenceVerifier internalSignatureReferenceVerifier = getSignatureReferenceVerifier(getSecurityProperties(), inputProcessorChain, referenceType, xmlSecStartElement);
                    if (!internalSignatureReferenceVerifier.isFinished()) {
                        internalSignatureReferenceVerifier.processEvent(xmlSecEvent, inputProcessorChain);
                        inputProcessorChain.addProcessor(internalSignatureReferenceVerifier);
                    }
                    this.processedReferences.add(referenceType);
                    inputProcessorChain.getDocumentContext().setIsInSignedContent(inputProcessorChain.getProcessors().indexOf(internalSignatureReferenceVerifier), internalSignatureReferenceVerifier);
                    processElementPath(internalSignatureReferenceVerifier.getStartElementPath(), inputProcessorChain, internalSignatureReferenceVerifier.getStartElement(), referenceType);
                }
            }
        }
        return xmlSecEvent;
    }

    protected List<ReferenceType> resolvesResource(XMLSecStartElement xmlSecStartElement) {
        List<ReferenceType> referenceTypes = Collections.emptyList();
        for (int i = 0; i < this.sameDocumentReferences.size(); i++) {
            KeyValue<ResourceResolver, ReferenceType> keyValue = this.sameDocumentReferences.get(i);
            ResourceResolver resolver = keyValue.getKey();
            boolean resourceMatches = false;
            try {
                Method m = resolver.getClass().getMethod("matches", XMLSecStartElement.class, QName.class);
                if (m != null && ((Boolean) m.invoke(resolver, xmlSecStartElement, getSecurityProperties().getIdAttributeNS())).booleanValue()) {
                    if (referenceTypes == Collections.emptyList()) {
                        referenceTypes = new ArrayList();
                    }
                    referenceTypes.add(keyValue.getValue());
                    resourceMatches = true;
                }
            } catch (IllegalAccessException e) {
            } catch (NoSuchMethodException e2) {
            } catch (InvocationTargetException e3) {
            }
            if (!resourceMatches && keyValue.getKey().matches(xmlSecStartElement)) {
                if (referenceTypes == Collections.emptyList()) {
                    referenceTypes = new ArrayList();
                }
                referenceTypes.add(keyValue.getValue());
            }
        }
        return referenceTypes;
    }

    @Override // org.apache.xml.security.stax.ext.AbstractInputProcessor, org.apache.xml.security.stax.ext.InputProcessor
    public void doFinal(InputProcessorChain inputProcessorChain) throws Exception {
        inputProcessorChain.doFinal();
        for (int i = 0; i < this.sameDocumentReferences.size(); i++) {
            if (!this.processedReferences.contains(this.sameDocumentReferences.get(i).getValue())) {
                throw new XMLSecurityException("stax.signature.unprocessedReferences");
            }
        }
        if (!this.externalReferences.isEmpty()) {
            for (int i2 = 0; i2 < this.externalReferences.size(); i2++) {
                KeyValue<ResourceResolver, ReferenceType> keyValue = this.externalReferences.get(i2);
                verifyExternalReference(inputProcessorChain, keyValue.getKey().getInputStreamFromExternalReference(), keyValue.getValue());
                this.processedReferences.add(keyValue.getValue());
            }
            for (int i3 = 0; i3 < this.externalReferences.size(); i3++) {
                if (!this.processedReferences.contains(this.externalReferences.get(i3).getValue())) {
                    throw new XMLSecurityException("stax.signature.unprocessedReferences");
                }
            }
        }
    }

    protected InternalSignatureReferenceVerifier getSignatureReferenceVerifier(XMLSecurityProperties securityProperties, InputProcessorChain inputProcessorChain, ReferenceType referenceType, XMLSecStartElement startElement) throws XMLSecurityException {
        return new InternalSignatureReferenceVerifier(securityProperties, inputProcessorChain, referenceType, startElement);
    }

    /* JADX WARN: Not initialized variable reg: 10, insn: 0x00c3: MOVE (r1 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = 
      (r10 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('bufferedInputStream' java.io.BufferedInputStream)])
    , block:B:37:0x00c3 */
    /* JADX WARN: Not initialized variable reg: 11, insn: 0x00c1: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r11 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:36:0x00c1 */
    /* JADX WARN: Not initialized variable reg: 12, insn: 0x009d: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = 
      (r12 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('digestOutputStream' org.apache.xml.security.stax.impl.util.DigestOutputStream)])
     A[TRY_LEAVE], block:B:25:0x009d */
    /* JADX WARN: Not initialized variable reg: 13, insn: 0x00a2: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r13 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:27:0x00a2 */
    protected void verifyExternalReference(InputProcessorChain inputProcessorChain, InputStream inputStream, ReferenceType referenceType) throws Exception {
        Throwable th;
        BufferedInputStream bufferedInputStream;
        DigestOutputStream digestOutputStream;
        Throwable th2;
        try {
            try {
                BufferedInputStream bufferedInputStream2 = new BufferedInputStream(inputStream);
                try {
                    try {
                        DigestOutputStream digestOutputStream2 = createMessageDigestOutputStream(referenceType, inputProcessorChain.getSecurityContext());
                        UnsyncBufferedOutputStream bufferedDigestOutputStream = new UnsyncBufferedOutputStream(digestOutputStream2);
                        Throwable th3 = null;
                        try {
                            try {
                                if (referenceType.getTransforms() != null) {
                                    Transformer transformer = buildTransformerChain(referenceType, bufferedDigestOutputStream, inputProcessorChain, null);
                                    transformer.transform(bufferedInputStream2);
                                    bufferedDigestOutputStream.close();
                                } else {
                                    XMLSecurityUtils.copy(bufferedInputStream2, bufferedDigestOutputStream);
                                    bufferedDigestOutputStream.close();
                                }
                                compareDigest(digestOutputStream2.getDigestValue(), referenceType);
                                $closeResource(null, bufferedDigestOutputStream);
                                if (digestOutputStream2 != null) {
                                    $closeResource(null, digestOutputStream2);
                                }
                                $closeResource(null, bufferedInputStream2);
                            } catch (Throwable th4) {
                                th3 = th4;
                                throw th4;
                            }
                        } catch (Throwable th5) {
                            $closeResource(th3, bufferedDigestOutputStream);
                            throw th5;
                        }
                    } catch (Throwable th6) {
                        if (digestOutputStream != null) {
                            $closeResource(th2, digestOutputStream);
                        }
                        throw th6;
                    }
                } catch (Throwable th7) {
                    throw th7;
                }
            } catch (Throwable th8) {
                $closeResource(th, bufferedInputStream);
                throw th8;
            }
        } catch (IOException e) {
            throw new XMLSecurityException(e);
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

    protected DigestOutputStream createMessageDigestOutputStream(ReferenceType referenceType, InboundSecurityContext inboundSecurityContext) throws XMLSecurityException {
        MessageDigest messageDigest;
        String digestMethodAlgorithm = referenceType.getDigestMethod().getAlgorithm();
        String jceName = JCEMapper.translateURItoJCEID(digestMethodAlgorithm);
        String jceProvider = JCEMapper.getJCEProviderFromURI(digestMethodAlgorithm);
        if (jceName == null) {
            throw new XMLSecurityException("algorithms.NoSuchMap", new Object[]{digestMethodAlgorithm});
        }
        AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent = new AlgorithmSuiteSecurityEvent();
        algorithmSuiteSecurityEvent.setAlgorithmURI(digestMethodAlgorithm);
        algorithmSuiteSecurityEvent.setAlgorithmUsage(XMLSecurityConstants.SigDig);
        algorithmSuiteSecurityEvent.setCorrelationID(referenceType.getId());
        inboundSecurityContext.registerSecurityEvent(algorithmSuiteSecurityEvent);
        try {
            if (jceProvider != null) {
                messageDigest = MessageDigest.getInstance(jceName, jceProvider);
            } else {
                messageDigest = MessageDigest.getInstance(jceName);
            }
            return new DigestOutputStream(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            throw new XMLSecurityException(e);
        } catch (NoSuchProviderException e2) {
            throw new XMLSecurityException(e2);
        }
    }

    protected Transformer buildTransformerChain(ReferenceType referenceType, OutputStream outputStream, InputProcessorChain inputProcessorChain, InternalSignatureReferenceVerifier internalSignatureReferenceVerifier) throws XMLSecurityException {
        Transformer transformer;
        if (referenceType.getTransforms() == null || referenceType.getTransforms().getTransform().isEmpty()) {
            AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent = new AlgorithmSuiteSecurityEvent();
            algorithmSuiteSecurityEvent.setAlgorithmURI("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
            algorithmSuiteSecurityEvent.setAlgorithmUsage(XMLSecurityConstants.SigTransform);
            algorithmSuiteSecurityEvent.setCorrelationID(referenceType.getId());
            inputProcessorChain.getSecurityContext().registerSecurityEvent(algorithmSuiteSecurityEvent);
            Transformer transformer2 = new Canonicalizer20010315_OmitCommentsTransformer();
            transformer2.setOutputStream(outputStream);
            return transformer2;
        }
        List<TransformType> transformTypeList = referenceType.getTransforms().getTransform();
        if (transformTypeList.size() == 1 && "http://www.w3.org/2000/09/xmldsig#enveloped-signature".equals(transformTypeList.get(0).getAlgorithm())) {
            TransformType transformType = new TransformType();
            transformType.setAlgorithm("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
            transformTypeList.add(transformType);
        }
        if (transformTypeList.size() > maximumAllowedTransformsPerReference.intValue()) {
            throw new XMLSecurityException("secureProcessing.MaximumAllowedTransformsPerReference", new Object[]{Integer.valueOf(transformTypeList.size()), maximumAllowedTransformsPerReference});
        }
        Transformer parentTransformer = null;
        for (int i = transformTypeList.size() - 1; i >= 0; i--) {
            TransformType transformType2 = transformTypeList.get(i);
            String algorithm = transformType2.getAlgorithm();
            AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent2 = new AlgorithmSuiteSecurityEvent();
            algorithmSuiteSecurityEvent2.setAlgorithmURI(algorithm);
            algorithmSuiteSecurityEvent2.setAlgorithmUsage(XMLSecurityConstants.SigTransform);
            algorithmSuiteSecurityEvent2.setCorrelationID(referenceType.getId());
            inputProcessorChain.getSecurityContext().registerSecurityEvent(algorithmSuiteSecurityEvent2);
            InclusiveNamespaces inclusiveNamespacesType = (InclusiveNamespaces) XMLSecurityUtils.getQNameType(transformType2.getContent(), XMLSecurityConstants.TAG_c14nExcl_InclusiveNamespaces);
            Map<String, Object> transformerProperties = null;
            if (inclusiveNamespacesType != null) {
                transformerProperties = new HashMap<>();
                transformerProperties.put(Canonicalizer20010315_Excl.INCLUSIVE_NAMESPACES_PREFIX_LIST, inclusiveNamespacesType.getPrefixList());
            }
            if (parentTransformer != null) {
                transformer = XMLSecurityUtils.getTransformer(parentTransformer, null, transformerProperties, algorithm, XMLSecurityConstants.DIRECTION.IN);
            } else {
                transformer = XMLSecurityUtils.getTransformer(null, outputStream, transformerProperties, algorithm, XMLSecurityConstants.DIRECTION.IN);
            }
            parentTransformer = transformer;
        }
        return parentTransformer;
    }

    protected void compareDigest(byte[] calculatedDigest, ReferenceType referenceType) throws XMLSecurityException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Calculated Digest: {}", XMLUtils.encodeToString(calculatedDigest));
            LOG.debug("Stored Digest: {}", XMLUtils.encodeToString(referenceType.getDigestValue()));
        }
        if (!MessageDigest.isEqual(referenceType.getDigestValue(), calculatedDigest)) {
            throw new XMLSecurityException("signature.Verification.InvalidDigestOrReference", new Object[]{referenceType.getURI()});
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/processor/input/AbstractSignatureReferenceVerifyInputProcessor$InternalSignatureReferenceVerifier.class */
    public class InternalSignatureReferenceVerifier extends AbstractInputProcessor {
        private ReferenceType referenceType;
        private Transformer transformer;
        private DigestOutputStream digestOutputStream;
        private OutputStream bufferedDigestOutputStream;
        private List<QName> startElementPath;
        private XMLSecStartElement startElement;
        private int elementCounter;
        private boolean finished;

        public InternalSignatureReferenceVerifier(XMLSecurityProperties securityProperties, InputProcessorChain inputProcessorChain, ReferenceType referenceType, XMLSecStartElement startElement) throws XMLSecurityException {
            super(securityProperties);
            this.finished = false;
            setStartElement(startElement);
            setReferenceType(referenceType);
            this.digestOutputStream = AbstractSignatureReferenceVerifyInputProcessor.this.createMessageDigestOutputStream(referenceType, inputProcessorChain.getSecurityContext());
            this.bufferedDigestOutputStream = new UnsyncBufferedOutputStream(getDigestOutputStream());
            this.transformer = buildTransformerChain(referenceType, this.bufferedDigestOutputStream, inputProcessorChain);
        }

        public Transformer buildTransformerChain(ReferenceType referenceType, OutputStream outputStream, InputProcessorChain inputProcessorChain) throws XMLSecurityException {
            return AbstractSignatureReferenceVerifyInputProcessor.this.buildTransformerChain(referenceType, outputStream, inputProcessorChain, this);
        }

        @Override // org.apache.xml.security.stax.ext.InputProcessor
        public XMLSecEvent processHeaderEvent(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
            return inputProcessorChain.processHeaderEvent();
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
        @Override // org.apache.xml.security.stax.ext.InputProcessor
        public XMLSecEvent processEvent(InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
            XMLSecEvent xmlSecEvent = inputProcessorChain.processEvent();
            processEvent(xmlSecEvent, inputProcessorChain);
            return xmlSecEvent;
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
        public void processEvent(XMLSecEvent xmlSecEvent, InputProcessorChain inputProcessorChain) throws XMLSecurityException, XMLStreamException {
            getTransformer().transform(xmlSecEvent);
            if (1 == xmlSecEvent.getEventType()) {
                this.elementCounter++;
                return;
            }
            if (2 == xmlSecEvent.getEventType()) {
                XMLSecEndElement xmlSecEndElement = xmlSecEvent.mo457asEndElement();
                this.elementCounter--;
                if (this.elementCounter == 0 && xmlSecEndElement.getName().equals(this.startElement.getName())) {
                    getTransformer().doFinal();
                    try {
                        getBufferedDigestOutputStream().close();
                        AbstractSignatureReferenceVerifyInputProcessor.this.compareDigest(getDigestOutputStream().getDigestValue(), getReferenceType());
                        inputProcessorChain.removeProcessor(this);
                        inputProcessorChain.getDocumentContext().unsetIsInSignedContent(this);
                        setFinished(true);
                    } catch (IOException e) {
                        throw new XMLSecurityException(e);
                    }
                }
            }
        }

        public boolean isFinished() {
            return this.finished;
        }

        public void setFinished(boolean finished) {
            this.finished = finished;
        }

        public ReferenceType getReferenceType() {
            return this.referenceType;
        }

        public void setReferenceType(ReferenceType referenceType) {
            this.referenceType = referenceType;
        }

        public Transformer getTransformer() {
            return this.transformer;
        }

        public void setTransformer(Transformer transformer) {
            this.transformer = transformer;
        }

        public DigestOutputStream getDigestOutputStream() {
            return this.digestOutputStream;
        }

        public void setDigestOutputStream(DigestOutputStream digestOutputStream) {
            this.digestOutputStream = digestOutputStream;
        }

        public OutputStream getBufferedDigestOutputStream() {
            return this.bufferedDigestOutputStream;
        }

        public void setBufferedDigestOutputStream(OutputStream bufferedDigestOutputStream) {
            this.bufferedDigestOutputStream = bufferedDigestOutputStream;
        }

        public XMLSecStartElement getStartElement() {
            return this.startElement;
        }

        public void setStartElement(XMLSecStartElement startElement) {
            this.startElementPath = startElement.getElementPath();
            this.startElement = startElement;
        }

        public List<QName> getStartElementPath() {
            return this.startElementPath;
        }
    }
}
