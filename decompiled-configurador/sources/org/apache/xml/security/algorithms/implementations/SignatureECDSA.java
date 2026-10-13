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
import java.security.interfaces.ECPrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.SignatureAlgorithmSpi;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/SignatureECDSA.class */
public abstract class SignatureECDSA extends SignatureAlgorithmSpi {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) SignatureECDSA.class);
    private final Signature signatureAlgorithm;
    private int signIntLen;

    public static byte[] convertASN1toXMLDSIG(byte[] asn1Bytes, int rawLen) throws IOException {
        return ECDSAUtils.convertASN1toXMLDSIG(asn1Bytes, rawLen);
    }

    public static byte[] convertXMLDSIGtoASN1(byte[] xmldsigBytes) throws IOException {
        return ECDSAUtils.convertXMLDSIGtoASN1(xmldsigBytes);
    }

    public SignatureECDSA() throws XMLSignatureException {
        this(null);
    }

    public SignatureECDSA(Provider provider) throws XMLSignatureException {
        this.signIntLen = -1;
        String algorithmID = JCEMapper.translateURItoJCEID(engineGetURI());
        LOG.debug("Created SignatureECDSA using {}", algorithmID);
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
            byte[] jcebytes = convertXMLDSIGtoASN1(signature);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Called ECDSA.verify() on " + XMLUtils.encodeToString(signature));
            }
            return this.signatureAlgorithm.verify(jcebytes);
        } catch (IOException | SignatureException ex) {
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
            byte[] jcebytes = this.signatureAlgorithm.sign();
            return convertASN1toXMLDSIG(jcebytes, this.signIntLen);
        } catch (IOException | SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineInitSign(Key privateKey, SecureRandom secureRandom) throws XMLSignatureException {
        if (privateKey instanceof ECPrivateKey) {
            ECPrivateKey ecKey = (ECPrivateKey) privateKey;
            this.signIntLen = (ecKey.getParams().getCurve().getField().getFieldSize() + 7) / 8;
        }
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
        throw new XMLSignatureException("algorithms.CannotUseAlgorithmParameterSpecOnRSA");
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/SignatureECDSA$SignatureECDSASHA1.class */
    public static class SignatureECDSASHA1 extends SignatureECDSA {
        public SignatureECDSASHA1() throws XMLSignatureException {
        }

        public SignatureECDSASHA1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/SignatureECDSA$SignatureECDSASHA224.class */
    public static class SignatureECDSASHA224 extends SignatureECDSA {
        public SignatureECDSASHA224() throws XMLSignatureException {
        }

        public SignatureECDSASHA224(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA224;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/SignatureECDSA$SignatureECDSASHA256.class */
    public static class SignatureECDSASHA256 extends SignatureECDSA {
        public SignatureECDSASHA256() throws XMLSignatureException {
        }

        public SignatureECDSASHA256(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA256;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/SignatureECDSA$SignatureECDSASHA384.class */
    public static class SignatureECDSASHA384 extends SignatureECDSA {
        public SignatureECDSASHA384() throws XMLSignatureException {
        }

        public SignatureECDSASHA384(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA384;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/SignatureECDSA$SignatureECDSASHA512.class */
    public static class SignatureECDSASHA512 extends SignatureECDSA {
        public SignatureECDSASHA512() throws XMLSignatureException {
        }

        public SignatureECDSASHA512(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA512;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/SignatureECDSA$SignatureECDSASHA3_224.class */
    public static class SignatureECDSASHA3_224 extends SignatureECDSA {
        public SignatureECDSASHA3_224() throws XMLSignatureException {
        }

        public SignatureECDSASHA3_224(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA3_224;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/SignatureECDSA$SignatureECDSASHA3_256.class */
    public static class SignatureECDSASHA3_256 extends SignatureECDSA {
        public SignatureECDSASHA3_256() throws XMLSignatureException {
        }

        public SignatureECDSASHA3_256(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA3_256;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/SignatureECDSA$SignatureECDSASHA3_384.class */
    public static class SignatureECDSASHA3_384 extends SignatureECDSA {
        public SignatureECDSASHA3_384() throws XMLSignatureException {
        }

        public SignatureECDSASHA3_384(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA3_384;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/SignatureECDSA$SignatureECDSASHA3_512.class */
    public static class SignatureECDSASHA3_512 extends SignatureECDSA {
        public SignatureECDSASHA3_512() throws XMLSignatureException {
        }

        public SignatureECDSASHA3_512(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA3_512;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/SignatureECDSA$SignatureECDSARIPEMD160.class */
    public static class SignatureECDSARIPEMD160 extends SignatureECDSA {
        public SignatureECDSARIPEMD160() throws XMLSignatureException {
        }

        public SignatureECDSARIPEMD160(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_ECDSA_RIPEMD160;
        }
    }
}
