package org.apache.xml.security.stax.impl.processor.input;

import jakarta.xml.bind.JAXBElement;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;
import java.util.Deque;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.binding.xmldsig.DigestMethodType;
import org.apache.xml.security.binding.xmldsig.KeyInfoType;
import org.apache.xml.security.binding.xmlenc.CipherValueType;
import org.apache.xml.security.binding.xmlenc.EncryptedKeyType;
import org.apache.xml.security.binding.xmlenc11.MGFType;
import org.apache.xml.security.binding.xop.Include;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.AbstractInputSecurityHeaderHandler;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.securityToken.AbstractInboundSecurityToken;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.securityEvent.AlgorithmSuiteSecurityEvent;
import org.apache.xml.security.stax.securityEvent.EncryptedKeyTokenSecurityEvent;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.stax.securityToken.SecurityTokenFactory;
import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/processor/input/XMLEncryptedKeyInputHandler.class */
public class XMLEncryptedKeyInputHandler extends AbstractInputSecurityHeaderHandler {
    private static final transient Logger LOG = LoggerFactory.getLogger((Class<?>) XMLEncryptedKeyInputHandler.class);

    @Override // org.apache.xml.security.stax.ext.XMLSecurityHeaderHandler
    public void handle(InputProcessorChain inputProcessorChain, XMLSecurityProperties securityProperties, Deque<XMLSecEvent> eventQueue, Integer index) throws XMLSecurityException {
        EncryptedKeyType encryptedKeyType = (EncryptedKeyType) ((JAXBElement) parseStructure(eventQueue, index.intValue(), securityProperties)).getValue();
        XMLSecEvent responsibleXMLSecStartXMLEvent = getResponsibleStartXMLEvent(eventQueue, index.intValue());
        handle(inputProcessorChain, encryptedKeyType, responsibleXMLSecStartXMLEvent, securityProperties);
    }

    public void handle(InputProcessorChain inputProcessorChain, final EncryptedKeyType encryptedKeyType, final XMLSecEvent responsibleXMLSecStartXMLEvent, final XMLSecurityProperties securityProperties) throws XMLSecurityException {
        if (encryptedKeyType.getEncryptionMethod() == null) {
            throw new XMLSecurityException("stax.encryption.noEncAlgo");
        }
        if (encryptedKeyType.getId() == null) {
            encryptedKeyType.setId(IDGenerator.generateID(null));
        }
        final InboundSecurityContext inboundSecurityContext = inputProcessorChain.getSecurityContext();
        SecurityTokenProvider<InboundSecurityToken> securityTokenProvider = new SecurityTokenProvider<InboundSecurityToken>() { // from class: org.apache.xml.security.stax.impl.processor.input.XMLEncryptedKeyInputHandler.1
            private AbstractInboundSecurityToken securityToken;

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // org.apache.xml.security.stax.securityToken.SecurityTokenProvider
            public InboundSecurityToken getSecurityToken() throws XMLSecurityException {
                if (this.securityToken != null) {
                    return this.securityToken;
                }
                this.securityToken = new AbstractInboundSecurityToken(inboundSecurityContext, encryptedKeyType.getId(), SecurityTokenConstants.KeyIdentifier_EncryptedKey, true) { // from class: org.apache.xml.security.stax.impl.processor.input.XMLEncryptedKeyInputHandler.1.1
                    private byte[] decryptedKey;
                    private InboundSecurityToken wrappingSecurityToken;

                    @Override // org.apache.xml.security.stax.impl.securityToken.AbstractInboundSecurityToken
                    public Key getKey(String algorithmURI, XMLSecurityConstants.AlgorithmUsage algorithmUsage, String correlationID) throws XMLSecurityException {
                        Key key = getSecretKey().get(algorithmURI);
                        if (key != null) {
                            return key;
                        }
                        String algoFamily = JCEMapper.getJCEKeyAlgorithmFromURI(algorithmURI);
                        Key key2 = new SecretKeySpec(getSecret(this, correlationID, algorithmURI), algoFamily);
                        setSecretKey(algorithmURI, key2);
                        return key2;
                    }

                    @Override // org.apache.xml.security.stax.impl.securityToken.AbstractInboundSecurityToken, org.apache.xml.security.stax.securityToken.SecurityToken
                    public InboundSecurityToken getKeyWrappingToken() throws XMLSecurityException {
                        return getWrappingSecurityToken(this);
                    }

                    @Override // org.apache.xml.security.stax.securityToken.SecurityToken
                    public SecurityTokenConstants.TokenType getTokenType() {
                        return SecurityTokenConstants.EncryptedKeyToken;
                    }

                    private InboundSecurityToken getWrappingSecurityToken(InboundSecurityToken wrappedSecurityToken) throws XMLSecurityException {
                        if (this.wrappingSecurityToken != null) {
                            return this.wrappingSecurityToken;
                        }
                        KeyInfoType keyInfoType = encryptedKeyType.getKeyInfo();
                        this.wrappingSecurityToken = SecurityTokenFactory.getInstance().getSecurityToken(keyInfoType, SecurityTokenConstants.KeyUsage_Decryption, securityProperties, inboundSecurityContext);
                        this.wrappingSecurityToken.addWrappedToken(wrappedSecurityToken);
                        return this.wrappingSecurityToken;
                    }

                    private byte[] getSecret(InboundSecurityToken wrappedSecurityToken, String correlationID, String symmetricAlgorithmURI) throws XMLSecurityException {
                        XMLSecurityConstants.AlgorithmUsage algorithmUsage;
                        Cipher cipher;
                        if (this.decryptedKey != null) {
                            return this.decryptedKey;
                        }
                        String algorithmURI = encryptedKeyType.getEncryptionMethod().getAlgorithm();
                        if (algorithmURI == null) {
                            throw new XMLSecurityException("stax.encryption.noEncAlgo");
                        }
                        String jceName = JCEMapper.translateURItoJCEID(algorithmURI);
                        String jceProvider = JCEMapper.getJCEProviderFromURI(algorithmURI);
                        if (jceName == null) {
                            throw new XMLSecurityException("algorithms.NoSuchMap", new Object[]{algorithmURI});
                        }
                        InboundSecurityToken wrappingSecurityToken = getWrappingSecurityToken(wrappedSecurityToken);
                        try {
                            if (wrappingSecurityToken.isAsymmetric()) {
                                algorithmUsage = XMLSecurityConstants.Asym_Key_Wrap;
                            } else {
                                algorithmUsage = XMLSecurityConstants.Sym_Key_Wrap;
                            }
                            if (jceProvider == null) {
                                cipher = Cipher.getInstance(jceName);
                            } else {
                                cipher = Cipher.getInstance(jceName, jceProvider);
                            }
                            if ("http://www.w3.org/2009/xmlenc11#rsa-oaep".equals(algorithmURI) || "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(algorithmURI)) {
                                DigestMethodType digestMethodType = (DigestMethodType) XMLSecurityUtils.getQNameType(encryptedKeyType.getEncryptionMethod().getContent(), XMLSecurityConstants.TAG_dsig_DigestMethod);
                                String jceDigestAlgorithm = "SHA-1";
                                if (digestMethodType != null) {
                                    AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent = new AlgorithmSuiteSecurityEvent();
                                    algorithmSuiteSecurityEvent.setAlgorithmURI(digestMethodType.getAlgorithm());
                                    algorithmSuiteSecurityEvent.setAlgorithmUsage(XMLSecurityConstants.EncDig);
                                    algorithmSuiteSecurityEvent.setCorrelationID(correlationID);
                                    inboundSecurityContext.registerSecurityEvent(algorithmSuiteSecurityEvent);
                                    jceDigestAlgorithm = JCEMapper.translateURItoJCEID(digestMethodType.getAlgorithm());
                                }
                                PSource.PSpecified pSource = PSource.PSpecified.DEFAULT;
                                byte[] oaepParams = (byte[]) XMLSecurityUtils.getQNameType(encryptedKeyType.getEncryptionMethod().getContent(), XMLSecurityConstants.TAG_xenc_OAEPparams);
                                if (oaepParams != null) {
                                    pSource = new PSource.PSpecified(oaepParams);
                                }
                                MGF1ParameterSpec mgfParameterSpec = new MGF1ParameterSpec("SHA-1");
                                MGFType mgfType = (MGFType) XMLSecurityUtils.getQNameType(encryptedKeyType.getEncryptionMethod().getContent(), XMLSecurityConstants.TAG_xenc11_MGF);
                                if (mgfType != null) {
                                    String jceMGFAlgorithm = JCEMapper.translateURItoJCEID(mgfType.getAlgorithm());
                                    mgfParameterSpec = new MGF1ParameterSpec(jceMGFAlgorithm);
                                }
                                OAEPParameterSpec oaepParameterSpec = new OAEPParameterSpec(jceDigestAlgorithm, "MGF1", mgfParameterSpec, pSource);
                                cipher.init(4, wrappingSecurityToken.getSecretKey(algorithmURI, algorithmUsage, correlationID), oaepParameterSpec);
                            } else {
                                cipher.init(4, wrappingSecurityToken.getSecretKey(algorithmURI, algorithmUsage, correlationID));
                            }
                            if (encryptedKeyType.getCipherData() != null && encryptedKeyType.getCipherData().getCipherValue() != null && encryptedKeyType.getCipherData().getCipherValue().getContent() != null && !encryptedKeyType.getCipherData().getCipherValue().getContent().isEmpty()) {
                                byte[] encryptedBytes = getEncryptedBytes(encryptedKeyType.getCipherData().getCipherValue());
                                byte[] sha1Bytes = XMLEncryptedKeyInputHandler.this.generateDigest(encryptedBytes);
                                String sha1Identifier = XMLUtils.encodeToString(sha1Bytes);
                                super.setSha1Identifier(sha1Identifier);
                                try {
                                    Key key = cipher.unwrap(encryptedBytes, jceName, 3);
                                    byte[] encoded = key.getEncoded();
                                    this.decryptedKey = encoded;
                                    return encoded;
                                } catch (IllegalStateException e) {
                                    throw new XMLSecurityException(e);
                                } catch (Exception e2) {
                                    XMLEncryptedKeyInputHandler.LOG.warn("Unwrapping of the encrypted key failed with error: " + e2.getMessage() + ". Generating a faked one to mitigate timing attacks.");
                                    int keyLength = JCEMapper.getKeyLengthFromURI(symmetricAlgorithmURI);
                                    this.decryptedKey = XMLSecurityConstants.generateBytes(keyLength / 8);
                                    return this.decryptedKey;
                                }
                            }
                            throw new XMLSecurityException("stax.encryption.noCipherValue");
                        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e3) {
                            throw new XMLSecurityException(e3);
                        }
                    }
                };
                this.securityToken.setElementPath(responsibleXMLSecStartXMLEvent.getElementPath());
                this.securityToken.setXMLSecEvent(responsibleXMLSecStartXMLEvent);
                return this.securityToken;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public byte[] getEncryptedBytes(CipherValueType cipherValue) throws XMLSecurityException {
                Include include;
                StringBuilder sb = new StringBuilder();
                for (Object obj : cipherValue.getContent()) {
                    if (obj instanceof String) {
                        sb.append((String) obj);
                    } else if (obj instanceof JAXBElement) {
                        JAXBElement<?> element = (JAXBElement) obj;
                        if (XMLSecurityConstants.TAG_XOP_INCLUDE.equals(element.getName()) && (include = (Include) element.getValue()) != null && include.getHref() != null && include.getHref().startsWith("cid:")) {
                            return XMLEncryptedKeyInputHandler.this.getBytesFromAttachment(include.getHref(), securityProperties);
                        }
                    } else {
                        continue;
                    }
                }
                return Base64.getMimeDecoder().decode(sb.toString());
            }

            @Override // org.apache.xml.security.stax.securityToken.SecurityTokenProvider
            public String getId() {
                return encryptedKeyType.getId();
            }
        };
        inboundSecurityContext.registerSecurityTokenProvider(encryptedKeyType.getId(), securityTokenProvider);
        EncryptedKeyTokenSecurityEvent tokenSecurityEvent = new EncryptedKeyTokenSecurityEvent();
        tokenSecurityEvent.setSecurityToken(securityTokenProvider.getSecurityToken());
        tokenSecurityEvent.setCorrelationID(encryptedKeyType.getId());
        inboundSecurityContext.registerSecurityEvent(tokenSecurityEvent);
        if (encryptedKeyType.getReferenceList() != null) {
            handleReferenceList(inputProcessorChain, encryptedKeyType, securityProperties);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] generateDigest(byte[] inputBytes) throws XMLSecurityException {
        try {
            return MessageDigest.getInstance("SHA-1").digest(inputBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new XMLSecurityException(e);
        }
    }

    protected void handleReferenceList(InputProcessorChain inputProcessorChain, EncryptedKeyType encryptedKeyType, XMLSecurityProperties securityProperties) throws XMLSecurityException {
    }

    protected byte[] getBytesFromAttachment(String xopUri, XMLSecurityProperties securityProperties) throws XMLSecurityException {
        throw new XMLSecurityException("errorMessages.NotYetImplementedException");
    }
}
