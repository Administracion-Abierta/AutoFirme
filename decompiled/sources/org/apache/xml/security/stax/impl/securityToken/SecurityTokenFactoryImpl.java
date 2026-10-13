package org.apache.xml.security.stax.impl.securityToken;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;
import org.apache.xml.security.binding.xmldsig.DSAKeyValueType;
import org.apache.xml.security.binding.xmldsig.KeyInfoType;
import org.apache.xml.security.binding.xmldsig.KeyValueType;
import org.apache.xml.security.binding.xmldsig.RSAKeyValueType;
import org.apache.xml.security.binding.xmldsig.X509DataType;
import org.apache.xml.security.binding.xmldsig.X509IssuerSerialType;
import org.apache.xml.security.binding.xmldsig11.ECKeyValueType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.stax.securityToken.SecurityTokenFactory;
import org.apache.xml.security.utils.RFC2253Parser;
import org.apache.xml.security.utils.UnsyncByteArrayInputStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/securityToken/SecurityTokenFactoryImpl.class */
public class SecurityTokenFactoryImpl extends SecurityTokenFactory {
    @Override // org.apache.xml.security.stax.securityToken.SecurityTokenFactory
    public InboundSecurityToken getSecurityToken(KeyInfoType keyInfoType, SecurityTokenConstants.KeyUsage keyUsage, XMLSecurityProperties securityProperties, InboundSecurityContext inboundSecurityContext) throws XMLSecurityException {
        if (keyInfoType != null) {
            X509DataType x509DataType = (X509DataType) XMLSecurityUtils.getQNameType(keyInfoType.getContent(), XMLSecurityConstants.TAG_dsig_X509Data);
            if (x509DataType != null) {
                return getSecurityToken(x509DataType, securityProperties, inboundSecurityContext, keyUsage);
            }
            KeyValueType keyValueType = (KeyValueType) XMLSecurityUtils.getQNameType(keyInfoType.getContent(), XMLSecurityConstants.TAG_dsig_KeyValue);
            if (keyValueType != null) {
                return getSecurityToken(keyValueType, securityProperties, inboundSecurityContext, keyUsage);
            }
            String keyName = (String) XMLSecurityUtils.getQNameType(keyInfoType.getContent(), XMLSecurityConstants.TAG_dsig_KeyName);
            if (keyName != null) {
                KeyNameSecurityToken token = getSecurityToken(keyName, securityProperties, inboundSecurityContext, keyUsage);
                return token;
            }
        }
        if (SecurityTokenConstants.KeyUsage_Signature_Verification.equals(keyUsage) && securityProperties.getSignatureVerificationKey() != null) {
            return getDefaultSecurityToken(securityProperties, inboundSecurityContext, keyUsage);
        }
        if (SecurityTokenConstants.KeyUsage_Decryption.equals(keyUsage) && securityProperties.getDecryptionKey() != null) {
            return getDefaultSecurityToken(securityProperties, inboundSecurityContext, keyUsage);
        }
        throw new XMLSecurityException("stax.noKey", new Object[]{keyUsage});
    }

    private InboundSecurityToken getDefaultSecurityToken(XMLSecurityProperties securityProperties, InboundSecurityContext inboundSecurityContext, SecurityTokenConstants.KeyUsage keyUsage) {
        AbstractInboundSecurityToken token = new AbstractInboundSecurityToken(inboundSecurityContext, IDGenerator.generateID(null), SecurityTokenConstants.KeyIdentifier_NoKeyInfo, false) { // from class: org.apache.xml.security.stax.impl.securityToken.SecurityTokenFactoryImpl.1
            @Override // org.apache.xml.security.stax.securityToken.SecurityToken
            public SecurityTokenConstants.TokenType getTokenType() {
                return SecurityTokenConstants.DefaultToken;
            }
        };
        setTokenKey(securityProperties, keyUsage, token);
        return token;
    }

    private KeyNameSecurityToken getSecurityToken(String keyName, XMLSecurityProperties securityProperties, InboundSecurityContext inboundSecurityContext, SecurityTokenConstants.KeyUsage keyUsage) throws XMLSecurityException {
        KeyNameSecurityToken token = new KeyNameSecurityToken(keyName, inboundSecurityContext);
        if (SecurityTokenConstants.KeyUsage_Signature_Verification.equals(keyUsage) && securityProperties.getSignatureVerificationKey() == null) {
            Map<String, Key> keyNameMap = securityProperties.getKeyNameMap();
            Key key = keyNameMap.get(keyName);
            if (key == null) {
                throw new XMLSecurityException("stax.keyNotFoundForName", new Object[]{keyName});
            }
            if (key instanceof PublicKey) {
                token.setPublicKey((PublicKey) key);
            } else {
                throw new XMLSecurityException("stax.keyTypeNotSupported", new Object[]{key.getClass().getSimpleName()});
            }
        }
        setTokenKey(securityProperties, keyUsage, token);
        return token;
    }

    private static InboundSecurityToken getSecurityToken(KeyValueType keyValueType, XMLSecurityProperties securityProperties, InboundSecurityContext inboundSecurityContext, SecurityTokenConstants.KeyUsage keyUsage) throws XMLSecurityException {
        RSAKeyValueType rsaKeyValueType = (RSAKeyValueType) XMLSecurityUtils.getQNameType(keyValueType.getContent(), XMLSecurityConstants.TAG_dsig_RSAKeyValue);
        if (rsaKeyValueType != null) {
            RsaKeyValueSecurityToken token = new RsaKeyValueSecurityToken(rsaKeyValueType, inboundSecurityContext);
            setTokenKey(securityProperties, keyUsage, token);
            return token;
        }
        DSAKeyValueType dsaKeyValueType = (DSAKeyValueType) XMLSecurityUtils.getQNameType(keyValueType.getContent(), XMLSecurityConstants.TAG_dsig_DSAKeyValue);
        if (dsaKeyValueType != null) {
            DsaKeyValueSecurityToken token2 = new DsaKeyValueSecurityToken(dsaKeyValueType, inboundSecurityContext);
            setTokenKey(securityProperties, keyUsage, token2);
            return token2;
        }
        ECKeyValueType ecKeyValueType = (ECKeyValueType) XMLSecurityUtils.getQNameType(keyValueType.getContent(), XMLSecurityConstants.TAG_dsig11_ECKeyValue);
        if (ecKeyValueType != null) {
            ECKeyValueSecurityToken token3 = new ECKeyValueSecurityToken(ecKeyValueType, inboundSecurityContext);
            setTokenKey(securityProperties, keyUsage, token3);
            return token3;
        }
        throw new XMLSecurityException("stax.unsupportedKeyValue");
    }

    private static InboundSecurityToken getSecurityToken(X509DataType x509DataType, XMLSecurityProperties securityProperties, InboundSecurityContext inboundSecurityContext, SecurityTokenConstants.KeyUsage keyUsage) throws XMLSecurityException {
        byte[] certBytes = (byte[]) XMLSecurityUtils.getQNameType(x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName(), XMLSecurityConstants.TAG_dsig_X509Certificate);
        if (certBytes != null) {
            X509Certificate cert = getCertificateFromBytes(certBytes);
            SecurityTokenConstants.TokenType tokenType = SecurityTokenConstants.X509V3Token;
            if (cert.getVersion() == 1) {
                tokenType = SecurityTokenConstants.X509V1Token;
            }
            X509SecurityToken token = new X509SecurityToken(tokenType, inboundSecurityContext, IDGenerator.generateID(null), SecurityTokenConstants.KeyIdentifier_X509KeyIdentifier, true);
            token.setX509Certificates(new X509Certificate[]{cert});
            setTokenKey(securityProperties, keyUsage, token);
            return token;
        }
        X509IssuerSerialType issuerSerialType = (X509IssuerSerialType) XMLSecurityUtils.getQNameType(x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName(), XMLSecurityConstants.TAG_dsig_X509IssuerSerial);
        if (issuerSerialType != null) {
            if (issuerSerialType.getX509IssuerName() == null || issuerSerialType.getX509SerialNumber() == null || ((SecurityTokenConstants.KeyUsage_Signature_Verification.equals(keyUsage) && securityProperties.getSignatureVerificationKey() == null) || (SecurityTokenConstants.KeyUsage_Decryption.equals(keyUsage) && securityProperties.getDecryptionKey() == null))) {
                throw new XMLSecurityException("stax.noKey", new Object[]{keyUsage});
            }
            X509IssuerSerialSecurityToken token2 = new X509IssuerSerialSecurityToken(SecurityTokenConstants.X509V3Token, inboundSecurityContext, IDGenerator.generateID(null));
            token2.setIssuerName(issuerSerialType.getX509IssuerName());
            token2.setSerialNumber(issuerSerialType.getX509SerialNumber());
            setTokenKey(securityProperties, keyUsage, token2);
            return token2;
        }
        byte[] skiBytes = (byte[]) XMLSecurityUtils.getQNameType(x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName(), XMLSecurityConstants.TAG_dsig_X509SKI);
        if (skiBytes != null) {
            if ((SecurityTokenConstants.KeyUsage_Signature_Verification.equals(keyUsage) && securityProperties.getSignatureVerificationKey() == null) || (SecurityTokenConstants.KeyUsage_Decryption.equals(keyUsage) && securityProperties.getDecryptionKey() == null)) {
                throw new XMLSecurityException("stax.noKey", new Object[]{keyUsage});
            }
            X509SKISecurityToken token3 = new X509SKISecurityToken(SecurityTokenConstants.X509V3Token, inboundSecurityContext, IDGenerator.generateID(null));
            token3.setSkiBytes(skiBytes);
            setTokenKey(securityProperties, keyUsage, token3);
            return token3;
        }
        String subjectName = (String) XMLSecurityUtils.getQNameType(x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName(), XMLSecurityConstants.TAG_dsig_X509SubjectName);
        if (subjectName != null) {
            if ((SecurityTokenConstants.KeyUsage_Signature_Verification.equals(keyUsage) && securityProperties.getSignatureVerificationKey() == null) || (SecurityTokenConstants.KeyUsage_Decryption.equals(keyUsage) && securityProperties.getDecryptionKey() == null)) {
                throw new XMLSecurityException("stax.noKey", new Object[]{keyUsage});
            }
            String normalizedSubjectName = RFC2253Parser.normalize(subjectName);
            X509SubjectNameSecurityToken token4 = new X509SubjectNameSecurityToken(SecurityTokenConstants.X509V3Token, inboundSecurityContext, IDGenerator.generateID(null));
            token4.setSubjectName(normalizedSubjectName);
            setTokenKey(securityProperties, keyUsage, token4);
            return token4;
        }
        throw new XMLSecurityException("stax.noKey", new Object[]{keyUsage});
    }

    private static void setTokenKey(XMLSecurityProperties securityProperties, SecurityTokenConstants.KeyUsage keyUsage, AbstractInboundSecurityToken token) {
        Key key = null;
        if (SecurityTokenConstants.KeyUsage_Signature_Verification.equals(keyUsage)) {
            key = securityProperties.getSignatureVerificationKey();
        } else if (SecurityTokenConstants.KeyUsage_Decryption.equals(keyUsage)) {
            key = securityProperties.getDecryptionKey();
        }
        if ((key instanceof PublicKey) && !SecurityTokenConstants.KeyValueToken.equals(token.getTokenType())) {
            token.setPublicKey((PublicKey) key);
        } else {
            token.setSecretKey("", key);
        }
    }

    private static X509Certificate getCertificateFromBytes(byte[] data) throws XMLSecurityException {
        try {
            InputStream in = new UnsyncByteArrayInputStream(data);
            Throwable th = null;
            try {
                CertificateFactory factory = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID);
                X509Certificate x509Certificate = (X509Certificate) factory.generateCertificate(in);
                if (0 != 0) {
                    try {
                        in.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    in.close();
                }
                return x509Certificate;
            } catch (Throwable th3) {
                if (0 != 0) {
                    try {
                        in.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    in.close();
                }
                throw th3;
            }
        } catch (IOException | CertificateException e) {
            throw new XMLSecurityException(e);
        }
    }
}
