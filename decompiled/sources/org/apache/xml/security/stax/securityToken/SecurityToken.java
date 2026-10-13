package org.apache.xml.security.stax.securityToken;

import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import org.apache.xml.security.exceptions.XMLSecurityException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/securityToken/SecurityToken.class */
public interface SecurityToken {
    String getId();

    boolean isAsymmetric() throws XMLSecurityException;

    Map<String, Key> getSecretKey() throws XMLSecurityException;

    PublicKey getPublicKey() throws XMLSecurityException;

    X509Certificate[] getX509Certificates() throws XMLSecurityException;

    SecurityToken getKeyWrappingToken() throws XMLSecurityException;

    List<? extends SecurityToken> getWrappedTokens() throws XMLSecurityException;

    SecurityTokenConstants.KeyIdentifier getKeyIdentifier();

    SecurityTokenConstants.TokenType getTokenType();

    List<SecurityTokenConstants.TokenUsage> getTokenUsages();

    void addTokenUsage(SecurityTokenConstants.TokenUsage tokenUsage) throws XMLSecurityException;

    String getSha1Identifier();
}
