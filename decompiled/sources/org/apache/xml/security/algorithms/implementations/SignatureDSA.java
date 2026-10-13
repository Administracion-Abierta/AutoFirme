package org.apache.xml.security.algorithms.implementations;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.DSAKey;
import java.security.spec.AlgorithmParameterSpec;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.SignatureAlgorithmSpi;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureDSA.class */
public class SignatureDSA extends SignatureAlgorithmSpi {
    public static final String URI = "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) SignatureDSA.class);
    private final Signature signatureAlgorithm;
    private int size;

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected String engineGetURI() {
        return "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
    }

    public SignatureDSA() throws XMLSignatureException {
        this(null);
    }

    public SignatureDSA(Provider provider) throws XMLSignatureException {
        String algorithmID = JCEMapper.translateURItoJCEID(engineGetURI());
        LOG.debug("Created SignatureDSA using {}", algorithmID);
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
                LOG.debug("Called DSA.verify() on " + XMLUtils.encodeToString(signature));
            }
            byte[] jcebytes = JavaUtils.convertDsaXMLDSIGtoASN1(signature, this.size / 8);
            return this.signatureAlgorithm.verify(jcebytes);
        } catch (IOException | SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineInitVerify(Key publicKey) throws XMLSignatureException {
        engineInitVerify(publicKey, this.signatureAlgorithm);
        this.size = ((DSAKey) publicKey).getParams().getQ().bitLength();
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected byte[] engineSign() throws XMLSignatureException {
        try {
            byte[] jcebytes = this.signatureAlgorithm.sign();
            return JavaUtils.convertDsaASN1toXMLDSIG(jcebytes, this.size / 8);
        } catch (IOException | SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineInitSign(Key privateKey, SecureRandom secureRandom) throws XMLSignatureException {
        engineInitSign(privateKey, secureRandom, this.signatureAlgorithm);
        this.size = ((DSAKey) privateKey).getParams().getQ().bitLength();
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
        throw new XMLSignatureException("algorithms.CannotUseAlgorithmParameterSpecOnDSA");
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureDSA$SHA256.class */
    public static class SHA256 extends SignatureDSA {
        public SHA256() throws XMLSignatureException {
        }

        public SHA256(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.implementations.SignatureDSA, org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_DSA_SHA256;
        }
    }
}
