package org.apache.xml.security.algorithms.implementations;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.algorithms.SignatureAlgorithmSpi;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.tls.CipherSuite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/IntegrityHmac.class */
public abstract class IntegrityHmac extends SignatureAlgorithmSpi {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) IntegrityHmac.class);
    private final Mac macAlgorithm;
    private HMACOutputLength hmacOutputLength;

    abstract int getDigestLength();

    public IntegrityHmac() throws XMLSignatureException {
        this(null);
    }

    public IntegrityHmac(Provider provider) throws XMLSignatureException {
        String algorithmID = JCEMapper.translateURItoJCEID(engineGetURI());
        LOG.debug("Created IntegrityHmacSHA1 using {}", algorithmID);
        try {
            this.macAlgorithm = provider == null ? Mac.getInstance(algorithmID) : Mac.getInstance(algorithmID, provider);
        } catch (NoSuchAlgorithmException ex) {
            Object[] exArgs = {algorithmID, ex.getLocalizedMessage()};
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs);
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineSetParameter(AlgorithmParameterSpec params) throws XMLSignatureException {
        throw new XMLSignatureException("empty", new Object[]{"Incorrect method call"});
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected boolean engineVerify(byte[] signature) throws XMLSignatureException {
        try {
            if (this.hmacOutputLength != null && this.hmacOutputLength.length < getDigestLength()) {
                LOG.debug("HMACOutputLength must not be less than {}", Integer.valueOf(getDigestLength()));
                Object[] exArgs = {String.valueOf(getDigestLength())};
                throw new XMLSignatureException("algorithms.HMACOutputLengthMin", exArgs);
            }
            byte[] completeResult = this.macAlgorithm.doFinal();
            return MessageDigestAlgorithm.isEqual(completeResult, signature);
        } catch (IllegalStateException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineInitVerify(Key secretKey) throws XMLSignatureException {
        if (!(secretKey instanceof SecretKey)) {
            String supplied = null;
            if (secretKey != null) {
                supplied = secretKey.getClass().getName();
            }
            String needed = SecretKey.class.getName();
            Object[] exArgs = {supplied, needed};
            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", exArgs);
        }
        try {
            this.macAlgorithm.init(secretKey);
        } catch (InvalidKeyException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected byte[] engineSign() throws XMLSignatureException {
        try {
            if (this.hmacOutputLength != null && this.hmacOutputLength.length < getDigestLength()) {
                LOG.debug("HMACOutputLength must not be less than {}", Integer.valueOf(getDigestLength()));
                Object[] exArgs = {String.valueOf(getDigestLength())};
                throw new XMLSignatureException("algorithms.HMACOutputLengthMin", exArgs);
            }
            return this.macAlgorithm.doFinal();
        } catch (IllegalStateException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineInitSign(Key secretKey) throws XMLSignatureException {
        engineInitSign(secretKey, (AlgorithmParameterSpec) null);
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineInitSign(Key secretKey, AlgorithmParameterSpec algorithmParameterSpec) throws XMLSignatureException {
        if (!(secretKey instanceof SecretKey)) {
            String supplied = null;
            if (secretKey != null) {
                supplied = secretKey.getClass().getName();
            }
            String needed = SecretKey.class.getName();
            Object[] exArgs = {supplied, needed};
            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", exArgs);
        }
        try {
            if (algorithmParameterSpec == null) {
                this.macAlgorithm.init(secretKey);
            } else {
                this.macAlgorithm.init(secretKey, algorithmParameterSpec);
            }
        } catch (InvalidAlgorithmParameterException | InvalidKeyException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineInitSign(Key secretKey, SecureRandom secureRandom) throws XMLSignatureException {
        throw new XMLSignatureException("algorithms.CannotUseSecureRandomOnMAC");
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineUpdate(byte[] input) throws XMLSignatureException {
        try {
            this.macAlgorithm.update(input);
        } catch (IllegalStateException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineUpdate(byte input) throws XMLSignatureException {
        try {
            this.macAlgorithm.update(input);
        } catch (IllegalStateException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineUpdate(byte[] buf, int offset, int len) throws XMLSignatureException {
        try {
            this.macAlgorithm.update(buf, offset, len);
        } catch (IllegalStateException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected String engineGetJCEAlgorithmString() {
        return this.macAlgorithm.getAlgorithm();
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected String engineGetJCEProviderName() {
        return this.macAlgorithm.getProvider().getName();
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineSetHMACOutputLength(int length) throws XMLSignatureException {
        this.hmacOutputLength = new HMACOutputLength(length);
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineGetContextFromElement(Element element) throws XMLSignatureException {
        String hmacLength;
        if (element == null) {
            throw new IllegalArgumentException("element null");
        }
        Node n = XMLUtils.selectDsNode(element.getFirstChild(), Constants._TAG_HMACOUTPUTLENGTH, 0);
        if (n != null && (hmacLength = XMLUtils.getFullTextChildrenFromNode(n)) != null && hmacLength.length() != 0) {
            this.hmacOutputLength = new HMACOutputLength(Integer.parseInt(hmacLength));
        }
    }

    @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
    protected void engineAddContextToElement(Element element) throws XMLSignatureException {
        if (element == null) {
            throw new IllegalArgumentException("null element");
        }
        if (this.hmacOutputLength != null) {
            Document doc = element.getOwnerDocument();
            Element HMElem = XMLUtils.createElementInSignatureSpace(doc, Constants._TAG_HMACOUTPUTLENGTH);
            Text HMText = doc.createTextNode("" + this.hmacOutputLength.length);
            HMElem.appendChild(HMText);
            XMLUtils.addReturnToElement(element);
            element.appendChild(HMElem);
            XMLUtils.addReturnToElement(element);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/IntegrityHmac$IntegrityHmacSHA1.class */
    public static class IntegrityHmacSHA1 extends IntegrityHmac {
        public IntegrityHmacSHA1() throws XMLSignatureException {
        }

        public IntegrityHmacSHA1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
        }

        @Override // org.apache.xml.security.algorithms.implementations.IntegrityHmac
        int getDigestLength() {
            return CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/IntegrityHmac$IntegrityHmacSHA224.class */
    public static class IntegrityHmacSHA224 extends IntegrityHmac {
        public IntegrityHmacSHA224() throws XMLSignatureException {
        }

        public IntegrityHmacSHA224(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_MAC_HMAC_SHA224;
        }

        @Override // org.apache.xml.security.algorithms.implementations.IntegrityHmac
        int getDigestLength() {
            return 224;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/IntegrityHmac$IntegrityHmacSHA256.class */
    public static class IntegrityHmacSHA256 extends IntegrityHmac {
        public IntegrityHmacSHA256() throws XMLSignatureException {
        }

        public IntegrityHmacSHA256(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
        }

        @Override // org.apache.xml.security.algorithms.implementations.IntegrityHmac
        int getDigestLength() {
            return 256;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/IntegrityHmac$IntegrityHmacSHA384.class */
    public static class IntegrityHmacSHA384 extends IntegrityHmac {
        public IntegrityHmacSHA384() throws XMLSignatureException {
        }

        public IntegrityHmacSHA384(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
        }

        @Override // org.apache.xml.security.algorithms.implementations.IntegrityHmac
        int getDigestLength() {
            return 384;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/IntegrityHmac$IntegrityHmacSHA512.class */
    public static class IntegrityHmacSHA512 extends IntegrityHmac {
        public IntegrityHmacSHA512() throws XMLSignatureException {
        }

        public IntegrityHmacSHA512(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
        }

        @Override // org.apache.xml.security.algorithms.implementations.IntegrityHmac
        int getDigestLength() {
            return 512;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/IntegrityHmac$IntegrityHmacRIPEMD160.class */
    public static class IntegrityHmacRIPEMD160 extends IntegrityHmac {
        public IntegrityHmacRIPEMD160() throws XMLSignatureException {
        }

        public IntegrityHmacRIPEMD160(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_MAC_HMAC_RIPEMD160;
        }

        @Override // org.apache.xml.security.algorithms.implementations.IntegrityHmac
        int getDigestLength() {
            return CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/IntegrityHmac$IntegrityHmacMD5.class */
    public static class IntegrityHmacMD5 extends IntegrityHmac {
        public IntegrityHmacMD5() throws XMLSignatureException {
        }

        public IntegrityHmacMD5(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override // org.apache.xml.security.algorithms.SignatureAlgorithmSpi
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5;
        }

        @Override // org.apache.xml.security.algorithms.implementations.IntegrityHmac
        int getDigestLength() {
            return 128;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/implementations/IntegrityHmac$HMACOutputLength.class */
    private static class HMACOutputLength {
        private static final int MIN_LENGTH = 128;
        private static final int MAX_LENGTH = 2048;
        private final int length;

        public HMACOutputLength(int length) throws XMLSignatureException {
            this.length = length;
            if (length < 128) {
                IntegrityHmac.LOG.debug("HMACOutputLength must not be less than {}", (Object) 128);
                Object[] exArgs = {String.valueOf(128)};
                throw new XMLSignatureException("algorithms.HMACOutputLengthMin", exArgs);
            }
            if (length > 2048) {
                IntegrityHmac.LOG.debug("HMACOutputLength must not be more than {}", (Object) 2048);
                Object[] exArgs2 = {String.valueOf(2048)};
                throw new XMLSignatureException("algorithms.HMACOutputLengthMax", exArgs2);
            }
        }
    }
}
