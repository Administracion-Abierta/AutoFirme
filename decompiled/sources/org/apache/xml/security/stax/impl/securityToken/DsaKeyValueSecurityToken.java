package org.apache.xml.security.stax.impl.securityToken;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import org.apache.xml.security.binding.xmldsig.DSAKeyValueType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/securityToken/DsaKeyValueSecurityToken.class */
public class DsaKeyValueSecurityToken extends AbstractInboundSecurityToken {
    private DSAKeyValueType dsaKeyValueType;

    public DsaKeyValueSecurityToken(DSAKeyValueType dsaKeyValueType, InboundSecurityContext inboundSecurityContext) {
        super(inboundSecurityContext, IDGenerator.generateID(null), SecurityTokenConstants.KeyIdentifier_KeyValue, true);
        this.dsaKeyValueType = dsaKeyValueType;
    }

    private PublicKey buildPublicKey(DSAKeyValueType dsaKeyValueType) throws InvalidKeySpecException, NoSuchAlgorithmException {
        DSAPublicKeySpec dsaPublicKeySpec = new DSAPublicKeySpec(new BigInteger(1, dsaKeyValueType.getY()), new BigInteger(1, dsaKeyValueType.getP()), new BigInteger(1, dsaKeyValueType.getQ()), new BigInteger(1, dsaKeyValueType.getG()));
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return keyFactory.generatePublic(dsaPublicKeySpec);
    }

    @Override // org.apache.xml.security.stax.impl.securityToken.AbstractSecurityToken, org.apache.xml.security.stax.securityToken.SecurityToken
    public PublicKey getPublicKey() throws XMLSecurityException {
        if (super.getPublicKey() == null) {
            try {
                setPublicKey(buildPublicKey(this.dsaKeyValueType));
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
