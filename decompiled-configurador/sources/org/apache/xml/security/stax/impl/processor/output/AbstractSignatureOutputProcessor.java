package org.apache.xml.security.stax.impl.processor.output;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.ResourceResolverMapper;
import org.apache.xml.security.stax.ext.AbstractOutputProcessor;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.ResourceResolver;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.Transformer;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.SignaturePartDef;
import org.apache.xml.security.stax.impl.transformer.TransformIdentity;
import org.apache.xml.security.stax.impl.transformer.canonicalizer.Canonicalizer20010315_Excl;
import org.apache.xml.security.stax.impl.util.DigestOutputStream;
import org.apache.xml.security.utils.UnsyncBufferedOutputStream;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/processor/output/AbstractSignatureOutputProcessor.class */
public abstract class AbstractSignatureOutputProcessor extends AbstractOutputProcessor {
    private static final transient Logger LOG = LoggerFactory.getLogger((Class<?>) AbstractSignatureOutputProcessor.class);
    private final List<SignaturePartDef> signaturePartDefList = new ArrayList();
    private InternalSignatureOutputProcessor activeInternalSignatureOutputProcessor;

    @Override // org.apache.xml.security.stax.ext.OutputProcessor
    public abstract void processEvent(XMLSecEvent xMLSecEvent, OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException;

    public List<SignaturePartDef> getSignaturePartDefList() {
        return this.signaturePartDefList;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.ext.AbstractOutputProcessor, org.apache.xml.security.stax.ext.OutputProcessor
    public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
        doFinalInternal(outputProcessorChain);
        super.doFinal(outputProcessorChain);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void doFinalInternal(OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
        Map<Object, SecurePart> dynamicSecureParts = outputProcessorChain.getSecurityContext().getAsMap(XMLSecurityConstants.SIGNATURE_PARTS);
        if (dynamicSecureParts != null) {
            for (Map.Entry<Object, SecurePart> securePartEntry : dynamicSecureParts.entrySet()) {
                SecurePart securePart = securePartEntry.getValue();
                if (securePart.getExternalReference() != null) {
                    digestExternalReference(outputProcessorChain, securePart);
                }
            }
        }
        verifySignatureParts(outputProcessorChain);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void digestExternalReference(OutputProcessorChain outputProcessorChain, SecurePart securePart) throws XMLSecurityException, XMLStreamException {
        String externalReference = securePart.getExternalReference();
        ResourceResolver resourceResolver = ResourceResolverMapper.getResourceResolver(externalReference, outputProcessorChain.getDocumentContext().getBaseURI());
        String digestAlgo = securePart.getDigestMethod();
        if (digestAlgo == null) {
            digestAlgo = getSecurityProperties().getSignatureDigestAlgorithm();
        }
        DigestOutputStream digestOutputStream = createMessageDigestOutputStream(digestAlgo);
        InputStream inputStream = resourceResolver.getInputStreamFromExternalReference();
        SignaturePartDef signaturePartDef = new SignaturePartDef();
        signaturePartDef.setSecurePart(securePart);
        signaturePartDef.setSigRefId(externalReference);
        signaturePartDef.setExternalResource(true);
        signaturePartDef.setTransforms(securePart.getTransforms());
        signaturePartDef.setDigestAlgo(digestAlgo);
        try {
            if (securePart.getTransforms() != null) {
                signaturePartDef.setExcludeVisibleC14Nprefixes(true);
                Transformer transformer = buildTransformerChain(digestOutputStream, signaturePartDef, null);
                transformer.transform(inputStream);
                transformer.doFinal();
            } else {
                XMLSecurityUtils.copy(inputStream, digestOutputStream);
            }
            digestOutputStream.close();
            String calculatedDigest = XMLUtils.encodeToString(digestOutputStream.getDigestValue());
            LOG.debug("Calculated Digest: {}", calculatedDigest);
            signaturePartDef.setDigestValue(calculatedDigest);
            getSignaturePartDefList().add(signaturePartDef);
        } catch (IOException e) {
            throw new XMLSecurityException(e);
        }
    }

    protected void verifySignatureParts(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        List<SignaturePartDef> signaturePartDefs = getSignaturePartDefList();
        Map<Object, SecurePart> dynamicSecureParts = outputProcessorChain.getSecurityContext().getAsMap(XMLSecurityConstants.SIGNATURE_PARTS);
        if (dynamicSecureParts == null) {
            return;
        }
        for (Map.Entry<Object, SecurePart> securePartEntry : dynamicSecureParts.entrySet()) {
            SecurePart securePart = securePartEntry.getValue();
            if (securePart.isRequired() && !findSecurePart(securePart, signaturePartDefs)) {
                throw new XMLSecurityException("stax.signature.securePartNotFound", new Object[]{securePart.getName()});
            }
        }
    }

    private boolean findSecurePart(SecurePart securePart, List<SignaturePartDef> signaturePartDefs) {
        for (SignaturePartDef signaturePartDef : signaturePartDefs) {
            if (signaturePartDef.getSecurePart() == securePart) {
                return true;
            }
        }
        return false;
    }

    protected InternalSignatureOutputProcessor getActiveInternalSignatureOutputProcessor() {
        return this.activeInternalSignatureOutputProcessor;
    }

    protected void setActiveInternalSignatureOutputProcessor(InternalSignatureOutputProcessor activeInternalSignatureOutputProcessor) {
        this.activeInternalSignatureOutputProcessor = activeInternalSignatureOutputProcessor;
    }

    protected DigestOutputStream createMessageDigestOutputStream(String digestAlgorithm) throws XMLSecurityException {
        MessageDigest messageDigest;
        String jceName = JCEMapper.translateURItoJCEID(digestAlgorithm);
        String jceProvider = JCEMapper.getJCEProviderFromURI(digestAlgorithm);
        if (jceName == null) {
            throw new XMLSecurityException("algorithms.NoSuchMap", new Object[]{digestAlgorithm});
        }
        try {
            if (jceProvider != null) {
                messageDigest = MessageDigest.getInstance(jceName, jceProvider);
            } else {
                messageDigest = MessageDigest.getInstance(jceName);
            }
            return new DigestOutputStream(messageDigest);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new XMLSecurityException(e);
        }
    }

    protected Transformer buildTransformerChain(OutputStream outputStream, SignaturePartDef signaturePartDef, XMLSecStartElement xmlSecStartElement) throws XMLSecurityException {
        Transformer transformer;
        String[] transforms = signaturePartDef.getTransforms();
        if (transforms == null || transforms.length == 0) {
            Transformer transformer2 = new TransformIdentity();
            transformer2.setOutputStream(outputStream);
            return transformer2;
        }
        Transformer parentTransformer = null;
        for (int i = transforms.length - 1; i >= 0; i--) {
            String transform = transforms[i];
            Map<String, Object> transformerProperties = null;
            if (getSecurityProperties().isAddExcC14NInclusivePrefixes() && "http://www.w3.org/2001/10/xml-exc-c14n#".equals(transform)) {
                Set<String> prefixSet = XMLSecurityUtils.getExcC14NInclusiveNamespacePrefixes(xmlSecStartElement, signaturePartDef.isExcludeVisibleC14Nprefixes());
                StringBuilder prefixes = new StringBuilder();
                for (String prefix : prefixSet) {
                    if (prefixes.length() != 0) {
                        prefixes.append(' ');
                    }
                    prefixes.append(prefix);
                }
                signaturePartDef.setInclusiveNamespacesPrefixes(prefixes.toString());
                List<String> inclusiveNamespacePrefixes = new ArrayList<>(prefixSet);
                transformerProperties = new HashMap<>();
                transformerProperties.put(Canonicalizer20010315_Excl.INCLUSIVE_NAMESPACES_PREFIX_LIST, inclusiveNamespacePrefixes);
            }
            if (parentTransformer != null) {
                transformer = XMLSecurityUtils.getTransformer(parentTransformer, null, transformerProperties, transform, XMLSecurityConstants.DIRECTION.OUT);
            } else {
                transformer = XMLSecurityUtils.getTransformer(null, outputStream, transformerProperties, transform, XMLSecurityConstants.DIRECTION.OUT);
            }
            parentTransformer = transformer;
        }
        return parentTransformer;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/processor/output/AbstractSignatureOutputProcessor$InternalSignatureOutputProcessor.class */
    public class InternalSignatureOutputProcessor extends AbstractOutputProcessor {
        private final SignaturePartDef signaturePartDef;
        private final XMLSecStartElement xmlSecStartElement;
        private int elementCounter;
        private OutputStream bufferedDigestOutputStream;
        private DigestOutputStream digestOutputStream;
        private Transformer transformer;

        public InternalSignatureOutputProcessor(SignaturePartDef signaturePartDef, XMLSecStartElement xmlSecStartElement) throws XMLSecurityException {
            addBeforeProcessor(InternalSignatureOutputProcessor.class);
            this.signaturePartDef = signaturePartDef;
            this.xmlSecStartElement = xmlSecStartElement;
        }

        @Override // org.apache.xml.security.stax.ext.AbstractOutputProcessor, org.apache.xml.security.stax.ext.OutputProcessor
        public void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
            this.digestOutputStream = AbstractSignatureOutputProcessor.this.createMessageDigestOutputStream(this.signaturePartDef.getDigestAlgo());
            this.bufferedDigestOutputStream = new UnsyncBufferedOutputStream(this.digestOutputStream);
            this.transformer = AbstractSignatureOutputProcessor.this.buildTransformerChain(this.bufferedDigestOutputStream, this.signaturePartDef, this.xmlSecStartElement);
            super.init(outputProcessorChain);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
        @Override // org.apache.xml.security.stax.ext.OutputProcessor
        public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
            this.transformer.transform(xmlSecEvent);
            if (1 == xmlSecEvent.getEventType()) {
                this.elementCounter++;
            } else if (2 == xmlSecEvent.getEventType()) {
                this.elementCounter--;
                if (this.elementCounter == 0 && xmlSecEvent.mo457asEndElement().getName().equals(this.xmlSecStartElement.getName())) {
                    this.transformer.doFinal();
                    try {
                        this.bufferedDigestOutputStream.close();
                        String calculatedDigest = XMLUtils.encodeToString(this.digestOutputStream.getDigestValue());
                        AbstractSignatureOutputProcessor.LOG.debug("Calculated Digest: {}", calculatedDigest);
                        this.signaturePartDef.setDigestValue(calculatedDigest);
                        outputProcessorChain.removeProcessor(this);
                        AbstractSignatureOutputProcessor.this.setActiveInternalSignatureOutputProcessor(null);
                    } catch (IOException e) {
                        throw new XMLSecurityException(e);
                    }
                }
            }
            outputProcessorChain.processEvent(xmlSecEvent);
        }
    }
}
