package org.apache.xml.security.algorithms;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import org.apache.xml.security.signature.XMLSignatureException;
import org.w3c.dom.Document;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/algorithms/MessageDigestAlgorithm.class */
public final class MessageDigestAlgorithm extends Algorithm {
    public static final String ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5 = "http://www.w3.org/2001/04/xmldsig-more#md5";
    public static final String ALGO_ID_DIGEST_SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
    public static final String ALGO_ID_DIGEST_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#sha224";
    public static final String ALGO_ID_DIGEST_SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
    public static final String ALGO_ID_DIGEST_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#sha384";
    public static final String ALGO_ID_DIGEST_SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
    public static final String ALGO_ID_DIGEST_RIPEMD160 = "http://www.w3.org/2001/04/xmlenc#ripemd160";
    public static final String ALGO_ID_DIGEST_WHIRLPOOL = "http://www.w3.org/2007/05/xmldsig-more#whirlpool";
    public static final String ALGO_ID_DIGEST_SHA3_224 = "http://www.w3.org/2007/05/xmldsig-more#sha3-224";
    public static final String ALGO_ID_DIGEST_SHA3_256 = "http://www.w3.org/2007/05/xmldsig-more#sha3-256";
    public static final String ALGO_ID_DIGEST_SHA3_384 = "http://www.w3.org/2007/05/xmldsig-more#sha3-384";
    public static final String ALGO_ID_DIGEST_SHA3_512 = "http://www.w3.org/2007/05/xmldsig-more#sha3-512";
    private final MessageDigest algorithm;

    private MessageDigestAlgorithm(Document doc, String algorithmURI) throws XMLSignatureException {
        super(doc, algorithmURI);
        this.algorithm = getDigestInstance(algorithmURI);
    }

    public static MessageDigestAlgorithm getInstance(Document doc, String algorithmURI) throws XMLSignatureException {
        return new MessageDigestAlgorithm(doc, algorithmURI);
    }

    public static MessageDigest getDigestInstance(String algorithmURI) throws XMLSignatureException {
        MessageDigest md;
        String algorithmID = JCEMapper.translateURItoJCEID(algorithmURI);
        if (algorithmID == null) {
            Object[] exArgs = {algorithmURI};
            throw new XMLSignatureException("algorithms.NoSuchMap", exArgs);
        }
        String provider = JCEMapper.getProviderId();
        try {
            if (provider == null) {
                md = MessageDigest.getInstance(algorithmID);
            } else {
                md = MessageDigest.getInstance(algorithmID, provider);
            }
            return md;
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            Object[] exArgs2 = {algorithmID, ex.getLocalizedMessage()};
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs2);
        }
    }

    public MessageDigest getAlgorithm() {
        return this.algorithm;
    }

    public static boolean isEqual(byte[] digesta, byte[] digestb) {
        return MessageDigest.isEqual(digesta, digestb);
    }

    public byte[] digest() {
        return this.algorithm.digest();
    }

    public byte[] digest(byte[] input) {
        return this.algorithm.digest(input);
    }

    public int digest(byte[] buf, int offset, int len) throws DigestException {
        return this.algorithm.digest(buf, offset, len);
    }

    public String getJCEAlgorithmString() {
        return this.algorithm.getAlgorithm();
    }

    public Provider getJCEProvider() {
        return this.algorithm.getProvider();
    }

    public int getDigestLength() {
        return this.algorithm.getDigestLength();
    }

    public void reset() {
        this.algorithm.reset();
    }

    public void update(byte[] input) {
        this.algorithm.update(input);
    }

    public void update(byte input) {
        this.algorithm.update(input);
    }

    public void update(byte[] buf, int offset, int len) {
        this.algorithm.update(buf, offset, len);
    }

    @Override // org.apache.xml.security.utils.SignatureElementProxy, org.apache.xml.security.utils.ElementProxy
    public String getBaseNamespace() {
        return "http://www.w3.org/2000/09/xmldsig#";
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return "DigestMethod";
    }
}
