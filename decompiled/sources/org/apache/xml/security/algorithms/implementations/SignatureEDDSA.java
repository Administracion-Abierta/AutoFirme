package org.apache.xml.security.algorithms.implementations;

import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.SignatureAlgorithmSpi;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureEDDSA.class */
public abstract class SignatureEDDSA extends SignatureAlgorithmSpi {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) SignatureEDDSA.class);
    private final Signature signatureAlgorithm;

    public SignatureEDDSA() throws XMLSignatureException {
        this(null);
    }

    public SignatureEDDSA(Provider provider) throws XMLSignatureException {
        String algorithmID = JCEMapper.translateURItoJCEID(engineGetURI());
        LOG.debug("Created SignatureEDDSA using {}", algorithmID);
        try {
            if (provider == null) {
                String providerId = JCEMapper.getProviderId();
                if (providerId == null) {
                    this.signatureAlgorithm = Signature.getInstance(algorithmID);
                } else {
                    this.signatureAlgorithm = Signature.getInstance(algorithmID, providerId);
                }
            } else {
                this.signatureAlgorithm = Signature.getInstance(algorithmID, provider);
            }
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            Object[] exArgs = {algorithmID, ex.getLocalizedMessage()};
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs);
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineSetParameter(AlgorithmParameterSpec params) throws XMLSignatureException {
        try {
            this.signatureAlgorithm.setParameter(params);
        } catch (InvalidAlgorithmParameterException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected boolean engineVerify(byte[] signature) throws XMLSignatureException {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Called SignatureEDDSA.verify() on " + XMLUtils.encodeToString(signature));
            }
            return this.signatureAlgorithm.verify(signature);
        } catch (SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineInitVerify(Key publicKey) throws XMLSignatureException {
        engineInitVerify(publicKey, this.signatureAlgorithm);
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected byte[] engineSign() throws XMLSignatureException {
        try {
            return this.signatureAlgorithm.sign();
        } catch (SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineInitSign(Key privateKey, SecureRandom secureRandom) throws XMLSignatureException {
        engineInitSign(privateKey, secureRandom, this.signatureAlgorithm);
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineInitSign(Key privateKey) throws XMLSignatureException {
        engineInitSign(privateKey, (SecureRandom) null);
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineUpdate(byte[] input) throws XMLSignatureException {
        try {
            this.signatureAlgorithm.update(input);
        } catch (SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineUpdate(byte input) throws XMLSignatureException {
        try {
            this.signatureAlgorithm.update(input);
        } catch (SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineUpdate(byte[] buf, int offset, int len) throws XMLSignatureException {
        try {
            this.signatureAlgorithm.update(buf, offset, len);
        } catch (SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected String engineGetJCEAlgorithmString() {
        return this.signatureAlgorithm.getAlgorithm();
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected String engineGetJCEProviderName() {
        return this.signatureAlgorithm.getProvider().getName();
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineSetHMACOutputLength(int HMACOutputLength) throws XMLSignatureException {
        throw new XMLSignatureException("algorithms.HMACOutputLengthOnlyForHMAC");
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineInitSign(Key signingKey, AlgorithmParameterSpec algorithmParameterSpec) throws XMLSignatureException {
        throw new XMLSignatureException("algorithms.CannotUseAlgorithmParameterSpecOnEdDSA");
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureEDDSA$SignatureEd25519.class */
    public static class SignatureEd25519 extends SignatureEDDSA {
        public SignatureEd25519() throws XMLSignatureException {
        }

        public SignatureEd25519(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_EDDSA_ED25519;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureEDDSA$SignatureEd448.class */
    public static class SignatureEd448 extends SignatureEDDSA {
        public SignatureEd448() throws XMLSignatureException {
        }

        public SignatureEd448(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_EDDSA_ED448;
        }
    }
}
