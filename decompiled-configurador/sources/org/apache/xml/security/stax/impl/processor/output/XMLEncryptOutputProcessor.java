package org.apache.xml.security.stax.impl.processor.output;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.EncryptionPartDef;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.securityToken.OutboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/processor/output/XMLEncryptOutputProcessor.class */
public class XMLEncryptOutputProcessor extends AbstractEncryptOutputProcessor {
    private static final transient Logger LOG = LoggerFactory.getLogger((Class<?>) XMLEncryptOutputProcessor.class);

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.processor.output.AbstractEncryptOutputProcessor, org.apache.xml.security.stax.ext.OutputProcessor
    public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
        SecurePart securePart;
        if (xmlSecEvent.getEventType() == 1) {
            XMLSecStartElement xmlSecStartElement = xmlSecEvent.mo458asStartElement();
            if (getActiveInternalEncryptionOutputProcessor() == null && (securePart = securePartMatches(xmlSecStartElement, outputProcessorChain, XMLSecurityConstants.ENCRYPTION_PARTS)) != null) {
                LOG.debug("Matched encryptionPart for encryption");
                String tokenId = (String) outputProcessorChain.getSecurityContext().get(XMLSecurityConstants.PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTION);
                SecurityTokenProvider<OutboundSecurityToken> securityTokenProvider = outputProcessorChain.getSecurityContext().getSecurityTokenProvider(tokenId);
                OutboundSecurityToken securityToken = securityTokenProvider.getSecurityToken();
                EncryptionPartDef encryptionPartDef = new EncryptionPartDef();
                encryptionPartDef.setSecurePart(securePart);
                encryptionPartDef.setModifier(securePart.getModifier());
                encryptionPartDef.setEncRefId(IDGenerator.generateID(null));
                encryptionPartDef.setKeyId(securityTokenProvider.getId());
                encryptionPartDef.setSymmetricKey(securityToken.getSecretKey(getSecurityProperties().getEncryptionSymAlgorithm()));
                outputProcessorChain.getSecurityContext().putAsList(EncryptionPartDef.class, encryptionPartDef);
                AbstractEncryptOutputProcessor.AbstractInternalEncryptionOutputProcessor internalEncryptionOutputProcessor = createInternalEncryptionOutputProcessor(encryptionPartDef, xmlSecStartElement, outputProcessorChain.getDocumentContext().getEncoding(), (OutboundSecurityToken) securityToken.getKeyWrappingToken());
                internalEncryptionOutputProcessor.setXMLSecurityProperties(getSecurityProperties());
                internalEncryptionOutputProcessor.setAction(getAction(), getActionOrder());
                internalEncryptionOutputProcessor.init(outputProcessorChain);
                setActiveInternalEncryptionOutputProcessor(internalEncryptionOutputProcessor);
            }
        }
        outputProcessorChain.processEvent(xmlSecEvent);
    }

    protected AbstractEncryptOutputProcessor.AbstractInternalEncryptionOutputProcessor createInternalEncryptionOutputProcessor(EncryptionPartDef encryptionPartDef, XMLSecStartElement startElement, String encoding, final OutboundSecurityToken keyWrappingToken) throws XMLSecurityException, XMLStreamException {
        AbstractEncryptOutputProcessor.AbstractInternalEncryptionOutputProcessor processor = new AbstractEncryptOutputProcessor.AbstractInternalEncryptionOutputProcessor(encryptionPartDef, startElement, encoding) { // from class: org.apache.xml.security.stax.impl.processor.output.XMLEncryptOutputProcessor.1
            /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
            @Override // org.apache.xml.security.stax.impl.processor.output.AbstractEncryptOutputProcessor.AbstractInternalEncryptionOutputProcessor
            protected void createKeyInfoStructure(OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
                int blockSize;
                if (keyWrappingToken == null) {
                    return;
                }
                String encryptionKeyTransportAlgorithm = getSecurityProperties().getEncryptionKeyTransportAlgorithm();
                PublicKey pubKey = keyWrappingToken.getPublicKey();
                Key secretKey = keyWrappingToken.getSecretKey(encryptionKeyTransportAlgorithm);
                if (pubKey == null && secretKey == null) {
                    return;
                }
                createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo, true, (List<XMLSecAttribute>) null);
                List<XMLSecAttribute> attributes = new ArrayList<>(1);
                String keyId = IDGenerator.generateID("EK");
                attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Id, keyId));
                createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptedKey, true, attributes);
                List<XMLSecAttribute> attributes2 = new ArrayList<>(1);
                attributes2.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, encryptionKeyTransportAlgorithm));
                createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptionMethod, false, attributes2);
                String encryptionKeyTransportDigestAlgorithm = getSecurityProperties().getEncryptionKeyTransportDigestAlgorithm();
                String encryptionKeyTransportMGFAlgorithm = "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(encryptionKeyTransportAlgorithm) ? null : getSecurityProperties().getEncryptionKeyTransportMGFAlgorithm();
                if ("http://www.w3.org/2009/xmlenc11#rsa-oaep".equals(encryptionKeyTransportAlgorithm) || "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(encryptionKeyTransportAlgorithm)) {
                    byte[] oaepParams = getSecurityProperties().getEncryptionKeyTransportOAEPParams();
                    if (oaepParams != null) {
                        createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_OAEPparams, false, (List<XMLSecAttribute>) null);
                        createCharactersAndOutputAsEvent(outputProcessorChain, XMLUtils.encodeToString(oaepParams));
                        createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_OAEPparams);
                    }
                    if (encryptionKeyTransportDigestAlgorithm != null) {
                        List<XMLSecAttribute> attributes3 = new ArrayList<>(1);
                        attributes3.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, encryptionKeyTransportDigestAlgorithm));
                        createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_DigestMethod, true, attributes3);
                        createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_DigestMethod);
                    }
                    if (encryptionKeyTransportMGFAlgorithm != null) {
                        List<XMLSecAttribute> attributes4 = new ArrayList<>(1);
                        attributes4.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, encryptionKeyTransportMGFAlgorithm));
                        createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc11_MGF, true, attributes4);
                        createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc11_MGF);
                    }
                }
                createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptionMethod);
                createKeyInfoStructureForEncryptedKey(outputProcessorChain, keyWrappingToken);
                createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherData, false, (List<XMLSecAttribute>) null);
                createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherValue, false, (List<XMLSecAttribute>) null);
                String jceid = JCEMapper.translateURItoJCEID(encryptionKeyTransportAlgorithm);
                if (jceid == null) {
                    throw new XMLSecurityException("algorithms.NoSuchMap", new Object[]{encryptionKeyTransportAlgorithm});
                }
                try {
                    Cipher cipher = Cipher.getInstance(jceid);
                    AlgorithmParameterSpec algorithmParameterSpec = null;
                    if ("http://www.w3.org/2009/xmlenc11#rsa-oaep".equals(encryptionKeyTransportAlgorithm) || "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(encryptionKeyTransportAlgorithm)) {
                        String jceDigestAlgorithm = "SHA-1";
                        if (encryptionKeyTransportDigestAlgorithm != null) {
                            jceDigestAlgorithm = JCEMapper.translateURItoJCEID(encryptionKeyTransportDigestAlgorithm);
                        }
                        PSource.PSpecified pSource = PSource.PSpecified.DEFAULT;
                        byte[] oaepParams2 = getSecurityProperties().getEncryptionKeyTransportOAEPParams();
                        if (oaepParams2 != null) {
                            pSource = new PSource.PSpecified(oaepParams2);
                        }
                        MGF1ParameterSpec mgfParameterSpec = new MGF1ParameterSpec("SHA-1");
                        if (encryptionKeyTransportMGFAlgorithm != null && !"http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(encryptionKeyTransportAlgorithm)) {
                            String jceMGFAlgorithm = JCEMapper.translateURItoJCEID(encryptionKeyTransportMGFAlgorithm);
                            mgfParameterSpec = new MGF1ParameterSpec(jceMGFAlgorithm);
                        }
                        algorithmParameterSpec = new OAEPParameterSpec(jceDigestAlgorithm, "MGF1", mgfParameterSpec, pSource);
                    }
                    if (pubKey != null) {
                        cipher.init(3, pubKey, algorithmParameterSpec);
                    } else {
                        cipher.init(3, secretKey, algorithmParameterSpec);
                    }
                    String tokenId = (String) outputProcessorChain.getSecurityContext().get(XMLSecurityConstants.PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTION);
                    SecurityTokenProvider<OutboundSecurityToken> securityTokenProvider = outputProcessorChain.getSecurityContext().getSecurityTokenProvider(tokenId);
                    OutboundSecurityToken securityToken = securityTokenProvider.getSecurityToken();
                    Key sessionKey = securityToken.getSecretKey(getSecurityProperties().getEncryptionSymAlgorithm());
                    if (pubKey != null && (blockSize = cipher.getBlockSize()) > 0 && blockSize < sessionKey.getEncoded().length) {
                        throw new XMLSecurityException("stax.unsupportedKeyTransp");
                    }
                    byte[] encryptedEphemeralKey = cipher.wrap(sessionKey);
                    createCharactersAndOutputAsEvent(outputProcessorChain, XMLUtils.encodeToString(encryptedEphemeralKey));
                    createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherValue);
                    createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherData);
                    createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptedKey);
                    createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo);
                } catch (InvalidAlgorithmParameterException e) {
                    throw new XMLSecurityException(e);
                } catch (InvalidKeyException e2) {
                    throw new XMLSecurityException(e2);
                } catch (NoSuchAlgorithmException e3) {
                    throw new XMLSecurityException(e3);
                } catch (IllegalBlockSizeException e4) {
                    throw new XMLSecurityException(e4);
                } catch (NoSuchPaddingException e5) {
                    throw new XMLSecurityException(e5);
                }
            }

            /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
            protected void createKeyInfoStructureForEncryptedKey(OutputProcessorChain outputProcessorChain, OutboundSecurityToken securityToken) throws XMLSecurityException, XMLStreamException {
                SecurityTokenConstants.KeyIdentifier keyIdentifier = getSecurityProperties().getEncryptionKeyIdentifier();
                X509Certificate[] x509Certificates = securityToken.getX509Certificates();
                if (x509Certificates == null) {
                    if (securityToken.getPublicKey() != null && SecurityTokenConstants.KeyIdentifier_KeyValue.equals(keyIdentifier)) {
                        createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo, true, (List<XMLSecAttribute>) null);
                        XMLSecurityUtils.createKeyValueTokenStructure(this, outputProcessorChain, securityToken.getPublicKey());
                        createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo);
                        return;
                    }
                    return;
                }
                if (!SecurityTokenConstants.KeyIdentifier_NoKeyInfo.equals(keyIdentifier)) {
                    createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo, true, (List<XMLSecAttribute>) null);
                    if (keyIdentifier == null || SecurityTokenConstants.KeyIdentifier_IssuerSerial.equals(keyIdentifier)) {
                        XMLSecurityUtils.createX509IssuerSerialStructure(this, outputProcessorChain, x509Certificates);
                    } else if (SecurityTokenConstants.KeyIdentifier_KeyValue.equals(keyIdentifier)) {
                        XMLSecurityUtils.createKeyValueTokenStructure(this, outputProcessorChain, x509Certificates);
                    } else if (SecurityTokenConstants.KeyIdentifier_SkiKeyIdentifier.equals(keyIdentifier)) {
                        XMLSecurityUtils.createX509SubjectKeyIdentifierStructure(this, outputProcessorChain, x509Certificates);
                    } else if (SecurityTokenConstants.KeyIdentifier_X509KeyIdentifier.equals(keyIdentifier)) {
                        XMLSecurityUtils.createX509CertificateStructure(this, outputProcessorChain, x509Certificates);
                    } else if (SecurityTokenConstants.KeyIdentifier_X509SubjectName.equals(keyIdentifier)) {
                        XMLSecurityUtils.createX509SubjectNameStructure(this, outputProcessorChain, x509Certificates);
                    } else if (SecurityTokenConstants.KeyIdentifier_KeyName.equals(keyIdentifier)) {
                        String keyName = getSecurityProperties().getEncryptionKeyName();
                        XMLSecurityUtils.createKeyNameTokenStructure(this, outputProcessorChain, keyName);
                    } else {
                        throw new XMLSecurityException("stax.unsupportedToken", new Object[]{keyIdentifier});
                    }
                    createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo);
                }
            }
        };
        processor.getAfterProcessors().add(XMLEncryptOutputProcessor.class);
        return processor;
    }
}
