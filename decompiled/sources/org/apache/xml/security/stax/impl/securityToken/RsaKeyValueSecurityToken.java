package org.apache.xml.security.stax.impl.securityToken;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import org.apache.xml.security.binding.xmldsig.RSAKeyValueType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/securityToken/RsaKeyValueSecurityToken.class */
public class RsaKeyValueSecurityToken extends AbstractInboundSecurityToken {
    private RSAKeyValueType rsaKeyValueType;

    public RsaKeyValueSecurityToken(RSAKeyValueType rsaKeyValueType, InboundSecurityContext inboundSecurityContext) {
        super(inboundSecurityContext, IDGenerator.generateID(null), SecurityTokenConstants.KeyIdentifier_KeyValue, true);
        this.rsaKeyValueType = rsaKeyValueType;
    }

    private PublicKey buildPublicKey(RSAKeyValueType rsaKeyValueType) throws InvalidKeySpecException, NoSuchAlgorithmException {
        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(new BigInteger(1, rsaKeyValueType.getModulus()), new BigInteger(1, rsaKeyValueType.getExponent()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(rsaPublicKeySpec);
    }

    @Override // org.apache.xml.security.stax.impl.securityToken.AbstractSecurityToken, org.apache.xml.security.stax.securityToken.SecurityToken
    public PublicKey getPublicKey() throws XMLSecurityException {
        if (super.getPublicKey() == null) {
            try {
                setPublicKey(buildPublicKey(this.rsaKeyValueType));
            } catch (NoSuchAlgorithmException e) {
                throw new XMLSecurityException(e);
            } catch (InvalidKeySpecException e2) {
                throw new XMLSecurityException(e2);
            }
        }
        return super.getPublicKey();
    }

    @Override // org.apache.xml.security.stax.impl.securityToken.AbstractSecurityToken, org.apache.xml.security.stax.securityToken.SecurityToken
    public boolean isAsymmetric() {
        return true;
    }

    @Override // org.apache.xml.security.stax.securityToken.SecurityToken
    public SecurityTokenConstants.TokenType getTokenType() {
        return SecurityTokenConstants.KeyValueToken;
    }
}
