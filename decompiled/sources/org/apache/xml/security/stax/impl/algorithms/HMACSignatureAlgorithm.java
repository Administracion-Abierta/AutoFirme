package org.apache.xml.security.stax.impl.algorithms;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Mac;
import org.apache.xml.security.exceptions.XMLSecurityException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/algorithms/HMACSignatureAlgorithm.class */
public class HMACSignatureAlgorithm implements SignatureAlgorithm {
    private Mac mac;

    public HMACSignatureAlgorithm(String jceName, String jceProvider) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (jceProvider != null) {
            this.mac = Mac.getInstance(jceName, jceProvider);
        } else {
            this.mac = Mac.getInstance(jceName);
        }
    }

    @Override // org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm
    public void engineUpdate(byte[] input) throws XMLSecurityException {
        this.mac.update(input);
    }

    @Override // org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm
    public void engineUpdate(byte input) throws XMLSecurityException {
        this.mac.update(input);
    }

    @Override // org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm
    public void engineUpdate(byte[] buf, int offset, int len) throws XMLSecurityException {
        this.mac.update(buf, offset, len);
    }

    @Override // org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm
    public void engineInitSign(Key signingKey) throws XMLSecurityException {
        try {
            this.mac.init(signingKey);
        } catch (InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override // org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm
    public void engineInitSign(Key signingKey, SecureRandom secureRandom) throws XMLSecurityException {
        try {
            this.mac.init(signingKey);
        } catch (InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override // org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm
    public void engineInitSign(Key signingKey, AlgorithmParameterSpec algorithmParameterSpec) throws XMLSecurityException {
        try {
            this.mac.init(signingKey, algorithmParameterSpec);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override // org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm
    public byte[] engineSign() throws XMLSecurityException {
        return this.mac.doFinal();
    }

    @Override // org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm
    public void engineInitVerify(Key verificationKey) throws XMLSecurityException {
        try {
            this.mac.init(verificationKey);
        } catch (InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override // org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm
    public boolean engineVerify(byte[] signature) throws XMLSecurityException {
        byte[] completeResult = this.mac.doFinal();
        return MessageDigest.isEqual(completeResult, signature);
    }

    @Override // org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm
    public void engineSetParameter(AlgorithmParameterSpec params) throws XMLSecurityException {
    }
}
