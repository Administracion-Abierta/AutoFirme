package org.apache.xml.security.stax.impl.processor.output;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.PSSParameterSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.algorithms.implementations.SignatureBaseRSA;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.AbstractBufferingOutputProcessor;
import org.apache.xml.security.stax.ext.AbstractOutputProcessor;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.Transformer;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.SignaturePartDef;
import org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm;
import org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithmFactory;
import org.apache.xml.security.stax.impl.transformer.canonicalizer.Canonicalizer20010315_Excl;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.impl.util.SignerOutputStream;
import org.apache.xml.security.stax.securityToken.OutboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;
import org.apache.xml.security.utils.UnsyncBufferedOutputStream;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/processor/output/AbstractSignatureEndingOutputProcessor.class */
public abstract class AbstractSignatureEndingOutputProcessor extends AbstractBufferingOutputProcessor {
    private static final transient Logger LOG = LoggerFactory.getLogger((Class<?>) AbstractSignatureEndingOutputProcessor.class);
    private List<SignaturePartDef> signaturePartDefList;

    protected abstract SignedInfoProcessor newSignedInfoProcessor(SignatureAlgorithm signatureAlgorithm, String str, XMLSecStartElement xMLSecStartElement, OutputProcessorChain outputProcessorChain) throws XMLSecurityException;

    protected abstract void createTransformsStructureForSignature(OutputProcessorChain outputProcessorChain, SignaturePartDef signaturePartDef) throws XMLSecurityException, XMLStreamException;

    protected abstract void createKeyInfoStructureForSignature(OutputProcessorChain outputProcessorChain, OutboundSecurityToken outboundSecurityToken, boolean z) throws XMLSecurityException, XMLStreamException;

    public AbstractSignatureEndingOutputProcessor(AbstractSignatureOutputProcessor signatureOutputProcessor) throws XMLSecurityException {
        this.signaturePartDefList = signatureOutputProcessor.getSignaturePartDefList();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.ext.AbstractBufferingOutputProcessor
    public void processHeaderEvent(OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
        List<XMLSecAttribute> attributes;
        List<XMLSecAttribute> attributes2;
        String sigRefId;
        OutputProcessorChain subOutputProcessorChain = outputProcessorChain.createSubChain(this);
        new ArrayList(1);
        String signatureId = null;
        if (this.securityProperties.isSignatureGenerateIds()) {
            attributes = new ArrayList<>(1);
            signatureId = IDGenerator.generateID(null);
            attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Id, signatureId));
        } else {
            attributes = Collections.emptyList();
        }
        XMLSecStartElement signatureElement = createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_Signature, true, attributes);
        try {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithmFactory.getInstance().getSignatureAlgorithm(getSecurityProperties().getSignatureAlgorithm());
            if (getSecurityProperties().getAlgorithmParameterSpec() != null) {
                signatureAlgorithm.engineSetParameter(getSecurityProperties().getAlgorithmParameterSpec());
            }
            String tokenId = (String) outputProcessorChain.getSecurityContext().get(XMLSecurityConstants.PROP_USE_THIS_TOKEN_ID_FOR_SIGNATURE);
            if (tokenId == null) {
                throw new XMLSecurityException("stax.keyNotFound");
            }
            SecurityTokenProvider<OutboundSecurityToken> wrappingSecurityTokenProvider = outputProcessorChain.getSecurityContext().getSecurityTokenProvider(tokenId);
            if (wrappingSecurityTokenProvider == null) {
                throw new XMLSecurityException("stax.keyNotFound");
            }
            OutboundSecurityToken wrappingSecurityToken = wrappingSecurityTokenProvider.getSecurityToken();
            if (wrappingSecurityToken == null) {
                throw new XMLSecurityException("stax.keyNotFound");
            }
            String sigAlgorithm = getSecurityProperties().getSignatureAlgorithm();
            Key key = wrappingSecurityToken.getSecretKey(sigAlgorithm);
            if ("http://www.w3.org/2000/09/xmldsig#hmac-sha1".equals(sigAlgorithm)) {
                key = XMLSecurityUtils.prepareSecretKey(sigAlgorithm, key.getEncoded());
            }
            signatureAlgorithm.engineInitSign(key);
            SignedInfoProcessor signedInfoProcessor = newSignedInfoProcessor(signatureAlgorithm, signatureId, signatureElement, subOutputProcessorChain);
            createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_SignedInfo, false, (List<XMLSecAttribute>) null);
            List<XMLSecAttribute> attributes3 = new ArrayList<>(1);
            String signatureCanonicalizationAlgorithm = getSecurityProperties().getSignatureCanonicalizationAlgorithm();
            attributes3.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, signatureCanonicalizationAlgorithm));
            createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_CanonicalizationMethod, false, attributes3);
            if (getSecurityProperties().isAddExcC14NInclusivePrefixes() && "http://www.w3.org/2001/10/xml-exc-c14n#".equals(signatureCanonicalizationAlgorithm)) {
                List<XMLSecAttribute> attributes4 = new ArrayList<>(1);
                attributes4.add(createAttribute(XMLSecurityConstants.ATT_NULL_PrefixList, signedInfoProcessor.getInclusiveNamespacePrefixes()));
                createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_c14nExcl_InclusiveNamespaces, true, attributes4);
                createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_c14nExcl_InclusiveNamespaces);
            }
            createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_CanonicalizationMethod);
            List<XMLSecAttribute> attributes5 = new ArrayList<>(1);
            attributes5.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, getSecurityProperties().getSignatureAlgorithm()));
            createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_SignatureMethod, false, attributes5);
            if (getSecurityProperties().getAlgorithmParameterSpec() instanceof PSSParameterSpec) {
                PSSParameterSpec pssParams = (PSSParameterSpec) getSecurityProperties().getAlgorithmParameterSpec();
                createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsigmore_RSAPSSPARAMS, false, (List<XMLSecAttribute>) null);
                List<XMLSecAttribute> attributes6 = new ArrayList<>(1);
                attributes6.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, SignatureBaseRSA.SignatureRSASSAPSS.DigestAlgorithm.fromDigestAlgorithm(pssParams.getDigestAlgorithm()).getXmlDigestAlgorithm()));
                createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_DigestMethod, false, attributes6);
                createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_DigestMethod);
                createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsigmore_SALTLENGTH, false, (List<XMLSecAttribute>) null);
                createCharactersAndOutputAsEvent(subOutputProcessorChain, String.valueOf(pssParams.getSaltLength()));
                createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsigmore_SALTLENGTH);
                createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsigmore_TRAILERFIELD, false, (List<XMLSecAttribute>) null);
                createCharactersAndOutputAsEvent(subOutputProcessorChain, String.valueOf(pssParams.getTrailerField()));
                createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsigmore_TRAILERFIELD);
                createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsigmore_RSAPSSPARAMS);
            }
            createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_SignatureMethod);
            for (SignaturePartDef signaturePartDef : this.signaturePartDefList) {
                if (signaturePartDef.isExternalResource()) {
                    sigRefId = signaturePartDef.getSigRefId();
                } else if (signaturePartDef.getSigRefId() != null) {
                    if (signaturePartDef.isGenerateXPointer()) {
                        sigRefId = "#xpointer(id('" + signaturePartDef.getSigRefId() + "'))";
                    } else {
                        sigRefId = "#" + signaturePartDef.getSigRefId();
                    }
                } else {
                    sigRefId = "";
                }
                String uriString = sigRefId;
                List<XMLSecAttribute> attributes7 = new ArrayList<>(1);
                attributes7.add(createAttribute(XMLSecurityConstants.ATT_NULL_URI, uriString));
                createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_Reference, false, attributes7);
                createTransformsStructureForSignature(subOutputProcessorChain, signaturePartDef);
                List<XMLSecAttribute> attributes8 = new ArrayList<>(1);
                attributes8.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, signaturePartDef.getDigestAlgo()));
                createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_DigestMethod, false, attributes8);
                createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_DigestMethod);
                createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_DigestValue, false, (List<XMLSecAttribute>) null);
                createCharactersAndOutputAsEvent(subOutputProcessorChain, signaturePartDef.getDigestValue());
                createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_DigestValue);
                createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_Reference);
            }
            createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_SignedInfo);
            subOutputProcessorChain.removeProcessor(signedInfoProcessor);
            createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_SignatureValue, false, (List<XMLSecAttribute>) null);
            byte[] signatureValue = signedInfoProcessor.getSignatureValue();
            createCharactersAndOutputAsEvent(subOutputProcessorChain, XMLUtils.encodeToString(signatureValue));
            createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_SignatureValue);
            if (this.securityProperties.isSignatureGenerateIds()) {
                attributes2 = new ArrayList<>(1);
                attributes2.add(createAttribute(XMLSecurityConstants.ATT_NULL_Id, IDGenerator.generateID(null)));
            } else {
                attributes2 = Collections.emptyList();
            }
            if (!getSecurityProperties().getSignatureKeyIdentifiers().contains(SecurityTokenConstants.KeyIdentifier_NoKeyInfo)) {
                createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo, false, attributes2);
                createKeyInfoStructureForSignature(subOutputProcessorChain, wrappingSecurityToken, getSecurityProperties().isUseSingleCert());
                createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo);
            }
            createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_Signature);
            if (key instanceof Destroyable) {
                try {
                    ((Destroyable) key).destroy();
                } catch (DestroyFailedException e) {
                    LOG.debug("Error destroying key: {}", e.getMessage());
                }
            }
        } catch (NoSuchAlgorithmException | NoSuchProviderException e2) {
            throw new XMLSecurityException(e2);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/processor/output/AbstractSignatureEndingOutputProcessor$SignedInfoProcessor.class */
    protected static class SignedInfoProcessor extends AbstractOutputProcessor {
        private SignerOutputStream signerOutputStream;
        private OutputStream bufferedSignerOutputStream;
        private Transformer transformer;
        private byte[] signatureValue;
        private String inclusiveNamespacePrefixes;
        private SignatureAlgorithm signatureAlgorithm;
        private XMLSecStartElement xmlSecStartElement;
        private String signatureId;

        public SignedInfoProcessor(SignatureAlgorithm signatureAlgorithm, String signatureId, XMLSecStartElement xmlSecStartElement) throws XMLSecurityException {
            this.signatureAlgorithm = signatureAlgorithm;
            this.xmlSecStartElement = xmlSecStartElement;
            this.signatureId = signatureId;
        }

        @Override // org.apache.xml.security.stax.ext.AbstractOutputProcessor, org.apache.xml.security.stax.ext.OutputProcessor
        public void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
            this.signerOutputStream = new SignerOutputStream(this.signatureAlgorithm);
            this.bufferedSignerOutputStream = new UnsyncBufferedOutputStream(this.signerOutputStream);
            String canonicalizationAlgorithm = getSecurityProperties().getSignatureCanonicalizationAlgorithm();
            Map<String, Object> transformerProperties = null;
            if (getSecurityProperties().isAddExcC14NInclusivePrefixes() && "http://www.w3.org/2001/10/xml-exc-c14n#".equals(canonicalizationAlgorithm)) {
                Set<String> prefixSet = XMLSecurityUtils.getExcC14NInclusiveNamespacePrefixes(this.xmlSecStartElement, false);
                StringBuilder prefixes = new StringBuilder();
                for (String prefix : prefixSet) {
                    if (prefixes.length() != 0) {
                        prefixes.append(' ');
                    }
                    prefixes.append(prefix);
                }
                this.inclusiveNamespacePrefixes = prefixes.toString();
                transformerProperties = new HashMap<>(2);
                transformerProperties.put(Canonicalizer20010315_Excl.INCLUSIVE_NAMESPACES_PREFIX_LIST, new ArrayList(prefixSet));
            }
            this.transformer = XMLSecurityUtils.getTransformer(null, this.bufferedSignerOutputStream, transformerProperties, canonicalizationAlgorithm, XMLSecurityConstants.DIRECTION.OUT);
            super.init(outputProcessorChain);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
        public byte[] getSignatureValue() throws XMLSecurityException, XMLStreamException {
            if (this.signatureValue != null) {
                return this.signatureValue;
            }
            try {
                this.transformer.doFinal();
                this.bufferedSignerOutputStream.close();
                this.signatureValue = this.signerOutputStream.sign();
                return this.signatureValue;
            } catch (IOException | XMLStreamException e) {
                throw new XMLSecurityException(e);
            }
        }

        public String getSignatureId() {
            return this.signatureId;
        }

        public String getInclusiveNamespacePrefixes() {
            return this.inclusiveNamespacePrefixes;
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
        @Override // org.apache.xml.security.stax.ext.OutputProcessor
        public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
            this.transformer.transform(xmlSecEvent);
            outputProcessorChain.processEvent(xmlSecEvent);
        }
    }
}
