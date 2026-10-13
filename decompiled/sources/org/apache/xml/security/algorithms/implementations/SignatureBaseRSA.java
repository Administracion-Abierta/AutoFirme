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
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.algorithms.SignatureAlgorithmSpi;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA.class */
public abstract class SignatureBaseRSA extends SignatureAlgorithmSpi {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) SignatureBaseRSA.class);
    private final Signature signatureAlgorithm;

    public SignatureBaseRSA() throws XMLSignatureException {
        this(null);
    }

    public SignatureBaseRSA(Provider provider) throws XMLSignatureException {
        String algorithmID = JCEMapper.translateURItoJCEID(engineGetURI());
        this.signatureAlgorithm = getSignature(provider, algorithmID);
        LOG.debug("Created SignatureRSA using {0}", algorithmID);
    }

    Signature getSignature(Provider provider, String algorithmID) throws XMLSignatureException {
        try {
            if (provider == null) {
                String providerId = JCEMapper.getProviderId();
                if (providerId == null) {
                    return Signature.getInstance(algorithmID);
                }
                return Signature.getInstance(algorithmID, providerId);
            }
            return Signature.getInstance(algorithmID, provider);
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
        throw new XMLSignatureException("algorithms.CannotUseAlgorithmParameterSpecOnRSA");
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureRSASHA1.class */
    public static class SignatureRSASHA1 extends SignatureBaseRSA {
        public SignatureRSASHA1() throws XMLSignatureException {
        }

        public SignatureRSASHA1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureRSASHA224.class */
    public static class SignatureRSASHA224 extends SignatureBaseRSA {
        public SignatureRSASHA224() throws XMLSignatureException {
        }

        public SignatureRSASHA224(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA224;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureRSASHA256.class */
    public static class SignatureRSASHA256 extends SignatureBaseRSA {
        public SignatureRSASHA256() throws XMLSignatureException {
        }

        public SignatureRSASHA256(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureRSASHA384.class */
    public static class SignatureRSASHA384 extends SignatureBaseRSA {
        public SignatureRSASHA384() throws XMLSignatureException {
        }

        public SignatureRSASHA384(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureRSASHA512.class */
    public static class SignatureRSASHA512 extends SignatureBaseRSA {
        public SignatureRSASHA512() throws XMLSignatureException {
        }

        public SignatureRSASHA512(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureRSARIPEMD160.class */
    public static class SignatureRSARIPEMD160 extends SignatureBaseRSA {
        public SignatureRSARIPEMD160() throws XMLSignatureException {
        }

        public SignatureRSARIPEMD160(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_RIPEMD160;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureRSAMD5.class */
    public static class SignatureRSAMD5 extends SignatureBaseRSA {
        public SignatureRSAMD5() throws XMLSignatureException {
        }

        public SignatureRSAMD5(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureBaseRSAPSS.class */
    public static abstract class SignatureBaseRSAPSS extends SignatureBaseRSA {
        abstract PSSParameterSpec getPSSParameterSpec();

        public SignatureBaseRSAPSS() throws XMLSignatureException {
        }

        public SignatureBaseRSAPSS(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.implementations.SignatureBaseRSA
        Signature getSignature(Provider provider, String algorithmID) throws XMLSignatureException {
            Signature sig;
            try {
                if (provider == null) {
                    String providerId = JCEMapper.getProviderId();
                    if (providerId == null) {
                        sig = Signature.getInstance("RSASSA-PSS");
                    } else {
                        sig = Signature.getInstance("RSASSA-PSS", providerId);
                    }
                } else {
                    sig = Signature.getInstance("RSASSA-PSS", provider);
                }
                try {
                    sig.setParameter(getPSSParameterSpec());
                    return sig;
                } catch (InvalidAlgorithmParameterException e) {
                    throw new NoSuchAlgorithmException("Should not happen", e);
                }
            } catch (NoSuchAlgorithmException | NoSuchProviderException e2) {
                return super.getSignature(provider, algorithmID);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureRSASHA1MGF1.class */
    public static class SignatureRSASHA1MGF1 extends SignatureBaseRSAPSS {
        private static final PSSParameterSpec SHA1_MGF1_PARAMS = new PSSParameterSpec("SHA-1", "MGF1", MGF1ParameterSpec.SHA1, 20, 1);

        public SignatureRSASHA1MGF1() throws XMLSignatureException {
        }

        public SignatureRSASHA1MGF1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1_MGF1;
        }

        @Override // org.apache.xml.security.algorithms.implementations.SignatureBaseRSA.SignatureBaseRSAPSS
        public PSSParameterSpec getPSSParameterSpec() {
            return SHA1_MGF1_PARAMS;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureRSASHA224MGF1.class */
    public static class SignatureRSASHA224MGF1 extends SignatureBaseRSAPSS {
        private static final PSSParameterSpec SHA224_MGF1_PARAMS = new PSSParameterSpec("SHA-224", "MGF1", MGF1ParameterSpec.SHA224, 28, 1);

        public SignatureRSASHA224MGF1() throws XMLSignatureException {
        }

        public SignatureRSASHA224MGF1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA224_MGF1;
        }

        @Override // org.apache.xml.security.algorithms.implementations.SignatureBaseRSA.SignatureBaseRSAPSS
        public PSSParameterSpec getPSSParameterSpec() {
            return SHA224_MGF1_PARAMS;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureRSASHA256MGF1.class */
    public static class SignatureRSASHA256MGF1 extends SignatureBaseRSAPSS {
        private static final PSSParameterSpec SHA256_MGF1_PARAMS = new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1);

        public SignatureRSASHA256MGF1() throws XMLSignatureException {
        }

        public SignatureRSASHA256MGF1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256_MGF1;
        }

        @Override // org.apache.xml.security.algorithms.implementations.SignatureBaseRSA.SignatureBaseRSAPSS
        public PSSParameterSpec getPSSParameterSpec() {
            return SHA256_MGF1_PARAMS;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureRSASHA384MGF1.class */
    public static class SignatureRSASHA384MGF1 extends SignatureBaseRSAPSS {
        private static final PSSParameterSpec SHA384_MGF1_PARAMS = new PSSParameterSpec("SHA-384", "MGF1", MGF1ParameterSpec.SHA384, 48, 1);

        public SignatureRSASHA384MGF1() throws XMLSignatureException {
        }

        public SignatureRSASHA384MGF1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384_MGF1;
        }

        @Override // org.apache.xml.security.algorithms.implementations.SignatureBaseRSA.SignatureBaseRSAPSS
        public PSSParameterSpec getPSSParameterSpec() {
            return SHA384_MGF1_PARAMS;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureRSASHA512MGF1.class */
    public static class SignatureRSASHA512MGF1 extends SignatureBaseRSAPSS {
        private static final PSSParameterSpec SHA512_MGF1_PARAMS = new PSSParameterSpec("SHA-512", "MGF1", MGF1ParameterSpec.SHA512, 64, 1);

        public SignatureRSASHA512MGF1() throws XMLSignatureException {
        }

        public SignatureRSASHA512MGF1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA512_MGF1;
        }

        @Override // org.apache.xml.security.algorithms.implementations.SignatureBaseRSA.SignatureBaseRSAPSS
        public PSSParameterSpec getPSSParameterSpec() {
            return SHA512_MGF1_PARAMS;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureRSASHA3_224MGF1.class */
    public static class SignatureRSASHA3_224MGF1 extends SignatureBaseRSAPSS {
        private static final PSSParameterSpec SHA3_224_MGF1_PARAMS = new PSSParameterSpec(MessageDigestAlgorithms.SHA3_224, "MGF1", new MGF1ParameterSpec(MessageDigestAlgorithms.SHA3_224), 28, 1);

        public SignatureRSASHA3_224MGF1() throws XMLSignatureException {
        }

        public SignatureRSASHA3_224MGF1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA3_224_MGF1;
        }

        @Override // org.apache.xml.security.algorithms.implementations.SignatureBaseRSA.SignatureBaseRSAPSS
        public PSSParameterSpec getPSSParameterSpec() {
            return SHA3_224_MGF1_PARAMS;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureRSASHA3_256MGF1.class */
    public static class SignatureRSASHA3_256MGF1 extends SignatureBaseRSAPSS {
        private static final PSSParameterSpec SHA3_256_MGF1_PARAMS = new PSSParameterSpec("SHA3-256", "MGF1", new MGF1ParameterSpec("SHA3-256"), 32, 1);

        public SignatureRSASHA3_256MGF1() throws XMLSignatureException {
        }

        public SignatureRSASHA3_256MGF1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA3_256_MGF1;
        }

        @Override // org.apache.xml.security.algorithms.implementations.SignatureBaseRSA.SignatureBaseRSAPSS
        public PSSParameterSpec getPSSParameterSpec() {
            return SHA3_256_MGF1_PARAMS;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureRSASHA3_384MGF1.class */
    public static class SignatureRSASHA3_384MGF1 extends SignatureBaseRSAPSS {
        private static final PSSParameterSpec SHA3_384_MGF1_PARAMS = new PSSParameterSpec(MessageDigestAlgorithms.SHA3_384, "MGF1", new MGF1ParameterSpec(MessageDigestAlgorithms.SHA3_384), 48, 1);

        public SignatureRSASHA3_384MGF1() throws XMLSignatureException {
        }

        public SignatureRSASHA3_384MGF1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA3_384_MGF1;
        }

        @Override // org.apache.xml.security.algorithms.implementations.SignatureBaseRSA.SignatureBaseRSAPSS
        public PSSParameterSpec getPSSParameterSpec() {
            return SHA3_384_MGF1_PARAMS;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureRSASHA3_512MGF1.class */
    public static class SignatureRSASHA3_512MGF1 extends SignatureBaseRSAPSS {
        private static final PSSParameterSpec SHA3_512_MGF1_PARAMS = new PSSParameterSpec(MessageDigestAlgorithms.SHA3_512, "MGF1", new MGF1ParameterSpec(MessageDigestAlgorithms.SHA3_512), 64, 1);

        public SignatureRSASHA3_512MGF1() throws XMLSignatureException {
        }

        public SignatureRSASHA3_512MGF1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA3_512_MGF1;
        }

        @Override // org.apache.xml.security.algorithms.implementations.SignatureBaseRSA.SignatureBaseRSAPSS
        public PSSParameterSpec getPSSParameterSpec() {
            return SHA3_512_MGF1_PARAMS;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureRSASSAPSS.class */
    public static class SignatureRSASSAPSS extends SignatureBaseRSA {
        private PSSParameterSpec pssParameterSpec;

        /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/implementations/SignatureBaseRSA$SignatureRSASSAPSS$DigestAlgorithm.class */
        public enum DigestAlgorithm {
            SHA256("SHA-256", "http://www.w3.org/2001/04/xmlenc#sha256", 32),
            SHA384("SHA-384", MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA384, 48),
            SHA512("SHA-512", "http://www.w3.org/2001/04/xmlenc#sha512", 64);

            private final String xmlDigestAlgorithm;
            private final String digestAlgorithm;
            private final int saltLength;

            DigestAlgorithm(String digestAlgorithm, String xmlDigestAlgorithm, int saltLength) {
                this.digestAlgorithm = digestAlgorithm;
                this.xmlDigestAlgorithm = xmlDigestAlgorithm;
                this.saltLength = saltLength;
            }

            public String getXmlDigestAlgorithm() {
                return this.xmlDigestAlgorithm;
            }

            public String getDigestAlgorithm() {
                return this.digestAlgorithm;
            }

            public int getSaltLength() {
                return this.saltLength;
            }

            public static DigestAlgorithm fromXmlDigestAlgorithm(String xmlDigestAlgorithm) throws XMLSignatureException {
                for (DigestAlgorithm value : values()) {
                    if (value.getXmlDigestAlgorithm().equals(xmlDigestAlgorithm)) {
                        return value;
                    }
                }
                throw new XMLSignatureException();
            }

            public static DigestAlgorithm fromDigestAlgorithm(String digestAlgorithm) throws XMLSignatureException {
                for (DigestAlgorithm value : values()) {
                    if (value.getDigestAlgorithm().equals(digestAlgorithm)) {
                        return value;
                    }
                }
                throw new XMLSignatureException();
            }
        }

        public SignatureRSASSAPSS() throws XMLSignatureException {
        }

        public SignatureRSASSAPSS(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_PSS;
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        protected void engineAddContextToElement(Element element) throws XMLSignatureException {
            if (element == null) {
                throw new IllegalArgumentException("null element");
            }
            Document doc = element.getOwnerDocument();
            Element rsaPssParamsElement = doc.createElementNS("http://www.w3.org/2007/05/xmldsig-more#", "pss:RSAPSSParams");
            rsaPssParamsElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:pss", "http://www.w3.org/2007/05/xmldsig-more#");
            Element digestMethodElement = XMLUtils.createElementInSignatureSpace(rsaPssParamsElement.getOwnerDocument(), "DigestMethod");
            digestMethodElement.setAttributeNS(null, "Algorithm", DigestAlgorithm.fromDigestAlgorithm(this.pssParameterSpec.getDigestAlgorithm()).getXmlDigestAlgorithm());
            XMLUtils.addReturnToElement(rsaPssParamsElement);
            rsaPssParamsElement.appendChild(digestMethodElement);
            XMLUtils.addReturnToElement(rsaPssParamsElement);
            Element saltLengthElement = rsaPssParamsElement.getOwnerDocument().createElementNS("http://www.w3.org/2007/05/xmldsig-more#", "pss:SaltLength");
            Node saltLengthText = rsaPssParamsElement.getOwnerDocument().createTextNode(String.valueOf(this.pssParameterSpec.getSaltLength()));
            saltLengthElement.appendChild(saltLengthText);
            rsaPssParamsElement.appendChild(saltLengthElement);
            XMLUtils.addReturnToElement(rsaPssParamsElement);
            Element trailerFieldElement = rsaPssParamsElement.getOwnerDocument().createElementNS("http://www.w3.org/2007/05/xmldsig-more#", "pss:TrailerField");
            Node trailerFieldText = rsaPssParamsElement.getOwnerDocument().createTextNode(String.valueOf(this.pssParameterSpec.getTrailerField()));
            trailerFieldElement.appendChild(trailerFieldText);
            rsaPssParamsElement.appendChild(trailerFieldElement);
            XMLUtils.addReturnToElement(rsaPssParamsElement);
            XMLUtils.addReturnToElement(element);
            element.appendChild(rsaPssParamsElement);
            XMLUtils.addReturnToElement(element);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        protected void engineGetContextFromElement(Element element) throws XMLSignatureException {
            if (this.pssParameterSpec == null) {
                super.engineGetContextFromElement(element);
                Element rsaPssParams = XMLUtils.selectNode(element.getFirstChild(), "http://www.w3.org/2007/05/xmldsig-more#", Constants._TAG_RSAPSSPARAMS, 0);
                if (rsaPssParams == null) {
                    throw new XMLSignatureException("algorithms.MissingRSAPSSParams");
                }
                Element saltLengthNode = XMLUtils.selectNode(rsaPssParams.getFirstChild(), "http://www.w3.org/2007/05/xmldsig-more#", Constants._TAG_SALTLENGTH, 0);
                Element trailerFieldNode = XMLUtils.selectNode(rsaPssParams.getFirstChild(), "http://www.w3.org/2007/05/xmldsig-more#", Constants._TAG_TRAILERFIELD, 0);
                int trailerField = 1;
                if (trailerFieldNode != null) {
                    try {
                        trailerField = Integer.parseInt(trailerFieldNode.getTextContent());
                    } catch (NumberFormatException e) {
                        throw new XMLSignatureException("empty", new Object[]{"Invalid trailer field value supplied"});
                    }
                }
                String xmlAlgorithm = XMLUtils.selectDsNode(rsaPssParams.getFirstChild(), "DigestMethod", 0).getAttribute("Algorithm");
                DigestAlgorithm digestAlgorithm = DigestAlgorithm.fromXmlDigestAlgorithm(xmlAlgorithm);
                String digestAlgorithmName = digestAlgorithm.getDigestAlgorithm();
                int saltLength = digestAlgorithm.getSaltLength();
                if (saltLengthNode != null) {
                    try {
                        saltLength = Integer.parseInt(saltLengthNode.getTextContent());
                    } catch (NumberFormatException e2) {
                        throw new XMLSignatureException("empty", new Object[]{"Invalid salt length value supplied"});
                    }
                }
                engineSetParameter(new PSSParameterSpec(digestAlgorithmName, "MGF1", new MGF1ParameterSpec(digestAlgorithmName), saltLength, trailerField));
            }
        }

        @Override // org.apache.xml.security.algorithms.implementations.SignatureBaseRSA, org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        protected void engineSetParameter(AlgorithmParameterSpec params) throws XMLSignatureException {
            this.pssParameterSpec = (PSSParameterSpec) params;
            super.engineSetParameter(params);
        }
    }
}
