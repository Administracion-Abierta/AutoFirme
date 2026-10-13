package org.apache.xml.security.stax.ext;

import java.io.OutputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.crypto.KeyGenerator;
import javax.xml.stream.XMLStreamWriter;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.impl.DocumentContextImpl;
import org.apache.xml.security.stax.impl.OutboundSecurityContextImpl;
import org.apache.xml.security.stax.impl.OutputProcessorChainImpl;
import org.apache.xml.security.stax.impl.XMLSecurityStreamWriter;
import org.apache.xml.security.stax.impl.processor.output.FinalOutputProcessor;
import org.apache.xml.security.stax.impl.processor.output.XMLEncryptOutputProcessor;
import org.apache.xml.security.stax.impl.processor.output.XMLSignatureOutputProcessor;
import org.apache.xml.security.stax.impl.securityToken.GenericOutboundSecurityToken;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.securityEvent.SecurityEventListener;
import org.apache.xml.security.stax.securityToken.OutboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/OutboundXMLSec.class */
public class OutboundXMLSec {
    private final XMLSecurityProperties securityProperties;

    public OutboundXMLSec(XMLSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public XMLStreamWriter processOutMessage(OutputStream outputStream, String encoding) throws XMLSecurityException {
        return processOutMessage((Object) outputStream, encoding, (SecurityEventListener) null);
    }

    public XMLStreamWriter processOutMessage(XMLStreamWriter xmlStreamWriter, String encoding) throws XMLSecurityException {
        return processOutMessage((Object) xmlStreamWriter, encoding, (SecurityEventListener) null);
    }

    public XMLStreamWriter processOutMessage(OutputStream outputStream, String encoding, SecurityEventListener eventListener) throws XMLSecurityException {
        return processOutMessage((Object) outputStream, encoding, eventListener);
    }

    public XMLStreamWriter processOutMessage(XMLStreamWriter xmlStreamWriter, String encoding, SecurityEventListener eventListener) throws XMLSecurityException {
        return processOutMessage((Object) xmlStreamWriter, encoding, eventListener);
    }

    private XMLStreamWriter processOutMessage(Object output, String encoding, SecurityEventListener eventListener) throws XMLSecurityException {
        OutboundSecurityContextImpl outboundSecurityContext = new OutboundSecurityContextImpl();
        if (eventListener != null) {
            outboundSecurityContext.addSecurityEventListener(eventListener);
        }
        DocumentContextImpl documentContext = new DocumentContextImpl();
        documentContext.setEncoding(encoding);
        OutputProcessorChainImpl outputProcessorChain = new OutputProcessorChainImpl(outboundSecurityContext, documentContext);
        SecurePart signEntireRequestPart = null;
        SecurePart encryptEntireRequestPart = null;
        int actionOrder = 0;
        for (XMLSecurityConstants.Action action : this.securityProperties.getActions()) {
            if (XMLSecurityConstants.SIGNATURE.equals(action)) {
                XMLSignatureOutputProcessor signatureOutputProcessor = new XMLSignatureOutputProcessor();
                int i = actionOrder;
                actionOrder++;
                initializeOutputProcessor(outputProcessorChain, signatureOutputProcessor, action, i);
                configureSignatureKeys(outboundSecurityContext);
                List<SecurePart> signatureParts = this.securityProperties.getSignatureSecureParts();
                for (SecurePart securePart : signatureParts) {
                    if (securePart.getIdToSecure() == null && securePart.getName() != null) {
                        outputProcessorChain.getSecurityContext().putAsMap(XMLSecurityConstants.SIGNATURE_PARTS, securePart.getName(), securePart);
                    } else if (securePart.getIdToSecure() != null) {
                        outputProcessorChain.getSecurityContext().putAsMap(XMLSecurityConstants.SIGNATURE_PARTS, securePart.getIdToSecure(), securePart);
                    } else if (securePart.getExternalReference() != null) {
                        outputProcessorChain.getSecurityContext().putAsMap(XMLSecurityConstants.SIGNATURE_PARTS, securePart.getExternalReference(), securePart);
                    } else if (securePart.isSecureEntireRequest()) {
                        signEntireRequestPart = securePart;
                    }
                }
            } else if (XMLSecurityConstants.ENCRYPTION.equals(action)) {
                XMLEncryptOutputProcessor encryptOutputProcessor = new XMLEncryptOutputProcessor();
                int i2 = actionOrder;
                actionOrder++;
                initializeOutputProcessor(outputProcessorChain, encryptOutputProcessor, action, i2);
                configureEncryptionKeys(outboundSecurityContext);
                List<SecurePart> encryptionParts = this.securityProperties.getEncryptionSecureParts();
                for (SecurePart securePart2 : encryptionParts) {
                    if (securePart2.getIdToSecure() == null && securePart2.getName() != null) {
                        outputProcessorChain.getSecurityContext().putAsMap(XMLSecurityConstants.ENCRYPTION_PARTS, securePart2.getName(), securePart2);
                    } else if (securePart2.getIdToSecure() != null) {
                        outputProcessorChain.getSecurityContext().putAsMap(XMLSecurityConstants.ENCRYPTION_PARTS, securePart2.getIdToSecure(), securePart2);
                    } else if (securePart2.isSecureEntireRequest()) {
                        encryptEntireRequestPart = securePart2;
                    }
                }
            }
        }
        if (output instanceof OutputStream) {
            FinalOutputProcessor finalOutputProcessor = new FinalOutputProcessor((OutputStream) output, encoding);
            initializeOutputProcessor(outputProcessorChain, finalOutputProcessor, null, -1);
        } else if (output instanceof XMLStreamWriter) {
            FinalOutputProcessor finalOutputProcessor2 = new FinalOutputProcessor((XMLStreamWriter) output);
            initializeOutputProcessor(outputProcessorChain, finalOutputProcessor2, null, -1);
        } else {
            throw new IllegalArgumentException(output + " is not supported as output");
        }
        XMLSecurityStreamWriter streamWriter = new XMLSecurityStreamWriter(outputProcessorChain);
        streamWriter.setSignEntireRequestPart(signEntireRequestPart);
        streamWriter.setEncryptEntireRequestPart(encryptEntireRequestPart);
        return streamWriter;
    }

    private void initializeOutputProcessor(OutputProcessorChainImpl outputProcessorChain, OutputProcessor outputProcessor, XMLSecurityConstants.Action action, int actionOrder) throws XMLSecurityException {
        outputProcessor.setXMLSecurityProperties(this.securityProperties);
        outputProcessor.setAction(action, actionOrder);
        outputProcessor.init(outputProcessorChain);
    }

    private void configureSignatureKeys(OutboundSecurityContextImpl outboundSecurityContext) throws XMLSecurityException {
        Key key = this.securityProperties.getSignatureKey();
        X509Certificate[] x509Certificates = this.securityProperties.getSignatureCerts();
        if ((key instanceof PrivateKey) && ((x509Certificates == null || x509Certificates.length == 0) && this.securityProperties.getSignatureVerificationKey() == null)) {
            throw new XMLSecurityException("stax.signature.publicKeyOrCertificateMissing");
        }
        final String securityTokenid = IDGenerator.generateID("SIG");
        final OutboundSecurityToken securityToken = new GenericOutboundSecurityToken(securityTokenid, SecurityTokenConstants.DefaultToken, key, x509Certificates);
        if (this.securityProperties.getSignatureVerificationKey() instanceof PublicKey) {
            ((GenericOutboundSecurityToken) securityToken).setPublicKey((PublicKey) this.securityProperties.getSignatureVerificationKey());
        }
        SecurityTokenProvider<OutboundSecurityToken> securityTokenProvider = new SecurityTokenProvider<OutboundSecurityToken>() { // from class: org.apache.xml.security.stax.ext.OutboundXMLSec.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // org.apache.xml.security.stax.securityToken.SecurityTokenProvider
            public OutboundSecurityToken getSecurityToken() throws XMLSecurityException {
                return securityToken;
            }

            @Override // org.apache.xml.security.stax.securityToken.SecurityTokenProvider
            public String getId() {
                return securityTokenid;
            }
        };
        outboundSecurityContext.registerSecurityTokenProvider(securityTokenid, securityTokenProvider);
        outboundSecurityContext.put(XMLSecurityConstants.PROP_USE_THIS_TOKEN_ID_FOR_SIGNATURE, securityTokenid);
    }

    private void configureEncryptionKeys(OutboundSecurityContextImpl outboundSecurityContext) throws XMLSecurityException {
        Key transportKey = this.securityProperties.getEncryptionTransportKey();
        X509Certificate transportCert = this.securityProperties.getEncryptionUseThisCertificate();
        X509Certificate[] transportCerts = null;
        if (transportCert != null) {
            transportCerts = new X509Certificate[]{transportCert};
        }
        OutboundSecurityToken transportSecurityToken = new GenericOutboundSecurityToken(IDGenerator.generateID(null), SecurityTokenConstants.DefaultToken, transportKey, transportCerts);
        Key key = this.securityProperties.getEncryptionKey();
        if (key == null) {
            if (transportCert == null && transportKey == null) {
                throw new XMLSecurityException("stax.encryption.encryptionKeyMissing");
            }
            String keyAlgorithm = JCEMapper.getJCEKeyAlgorithmFromURI(this.securityProperties.getEncryptionSymAlgorithm());
            try {
                KeyGenerator keyGen = KeyGenerator.getInstance(keyAlgorithm);
                if (keyAlgorithm.contains("AES")) {
                    int keyLength = JCEMapper.getKeyLengthFromURI(this.securityProperties.getEncryptionSymAlgorithm());
                    keyGen.init(keyLength);
                }
                key = keyGen.generateKey();
            } catch (NoSuchAlgorithmException e) {
                throw new XMLSecurityException(e);
            }
        }
        final String securityTokenid = IDGenerator.generateID(null);
        final GenericOutboundSecurityToken securityToken = new GenericOutboundSecurityToken(securityTokenid, SecurityTokenConstants.DefaultToken, key);
        securityToken.setKeyWrappingToken(transportSecurityToken);
        SecurityTokenProvider<OutboundSecurityToken> securityTokenProvider = new SecurityTokenProvider<OutboundSecurityToken>() { // from class: org.apache.xml.security.stax.ext.OutboundXMLSec.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // org.apache.xml.security.stax.securityToken.SecurityTokenProvider
            public OutboundSecurityToken getSecurityToken() throws XMLSecurityException {
                return securityToken;
            }

            @Override // org.apache.xml.security.stax.securityToken.SecurityTokenProvider
            public String getId() {
                return securityTokenid;
            }
        };
        outboundSecurityContext.registerSecurityTokenProvider(securityTokenid, securityTokenProvider);
        outboundSecurityContext.put(XMLSecurityConstants.PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTION, securityTokenid);
    }
}
