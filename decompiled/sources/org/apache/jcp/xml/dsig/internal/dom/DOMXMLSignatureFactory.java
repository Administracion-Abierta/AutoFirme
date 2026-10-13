package org.apache.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.List;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignatureProperties;
import javax.xml.crypto.dsig.SignatureProperty;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMXMLSignatureFactory.class */
public final class DOMXMLSignatureFactory extends XMLSignatureFactory {
    public XMLSignature newXMLSignature(SignedInfo si, KeyInfo ki) {
        return new DOMXMLSignature(si, ki, null, null, null);
    }

    public XMLSignature newXMLSignature(SignedInfo si, KeyInfo ki, List objects, String id, String signatureValueId) {
        return new DOMXMLSignature(si, ki, objects, id, signatureValueId);
    }

    public Reference newReference(String uri, DigestMethod dm) {
        return newReference(uri, dm, null, null, null);
    }

    public Reference newReference(String uri, DigestMethod dm, List transforms, String type, String id) {
        return new DOMReference(uri, type, dm, transforms, id, getProvider());
    }

    public Reference newReference(String uri, DigestMethod dm, List appliedTransforms, Data result, List transforms, String type, String id) {
        if (appliedTransforms == null) {
            throw new NullPointerException("appliedTransforms cannot be null");
        }
        if (appliedTransforms.isEmpty()) {
            throw new NullPointerException("appliedTransforms cannot be empty");
        }
        if (result == null) {
            throw new NullPointerException("result cannot be null");
        }
        return new DOMReference(uri, type, dm, appliedTransforms, result, transforms, id, getProvider());
    }

    public Reference newReference(String uri, DigestMethod dm, List transforms, String type, String id, byte[] digestValue) {
        if (digestValue == null) {
            throw new NullPointerException("digestValue cannot be null");
        }
        return new DOMReference(uri, type, dm, null, null, transforms, id, digestValue, getProvider());
    }

    public SignedInfo newSignedInfo(CanonicalizationMethod cm, SignatureMethod sm, List references) {
        return newSignedInfo(cm, sm, references, null);
    }

    public SignedInfo newSignedInfo(CanonicalizationMethod cm, SignatureMethod sm, List references, String id) {
        return new DOMSignedInfo(cm, sm, references, id);
    }

    public XMLObject newXMLObject(List content, String id, String mimeType, String encoding) {
        return new DOMXMLObject(content, id, mimeType, encoding);
    }

    public Manifest newManifest(List references) {
        return newManifest(references, null);
    }

    public Manifest newManifest(List references, String id) {
        return new DOMManifest(references, id);
    }

    public SignatureProperties newSignatureProperties(List props, String id) {
        return new DOMSignatureProperties(props, id);
    }

    public SignatureProperty newSignatureProperty(List info, String target, String id) {
        return new DOMSignatureProperty(info, target, id);
    }

    public XMLSignature unmarshalXMLSignature(XMLValidateContext context) throws MarshalException {
        if (context == null) {
            throw new NullPointerException("context cannot be null");
        }
        return unmarshal(((DOMValidateContext) context).getNode(), context);
    }

    public XMLSignature unmarshalXMLSignature(XMLStructure xmlStructure) throws MarshalException {
        if (xmlStructure == null) {
            throw new NullPointerException("xmlStructure cannot be null");
        }
        if (!(xmlStructure instanceof javax.xml.crypto.dom.DOMStructure)) {
            throw new ClassCastException("xmlStructure must be of type DOMStructure");
        }
        return unmarshal(((javax.xml.crypto.dom.DOMStructure) xmlStructure).getNode(), new UnmarshalContext());
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMXMLSignatureFactory$UnmarshalContext.class */
    private static class UnmarshalContext extends DOMCryptoContext {
        UnmarshalContext() {
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    private XMLSignature unmarshal(Node node, XMLCryptoContext context) throws MarshalException {
        Element element;
        node.normalize();
        if (node.getNodeType() == 9) {
            element = ((Document) node).getDocumentElement();
        } else if (node.getNodeType() == 1) {
            element = (Element) node;
        } else {
            throw new MarshalException("Signature element is not a proper Node");
        }
        String tag = element.getLocalName();
        String namespace = element.getNamespaceURI();
        if (tag == null || namespace == null) {
            throw new MarshalException("Document implementation must support DOM Level 2 and be namespace aware");
        }
        if ("Signature".equals(tag) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
            try {
                return new DOMXMLSignature(element, context, getProvider());
            } catch (MarshalException me) {
                throw me;
            } catch (Exception e) {
                throw new MarshalException(e);
            }
        }
        throw new MarshalException("Invalid Signature tag: " + namespace + ":" + tag);
    }

    public boolean isFeatureSupported(String feature) {
        if (feature == null) {
            throw new NullPointerException();
        }
        return false;
    }

    public DigestMethod newDigestMethod(String algorithm, DigestMethodParameterSpec params) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        if (algorithm == null) {
            throw new NullPointerException();
        }
        if (algorithm.equals("http://www.w3.org/2000/09/xmldsig#sha1")) {
            return new DOMDigestMethod.SHA1((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA224)) {
            return new DOMDigestMethod.SHA224((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals("http://www.w3.org/2001/04/xmlenc#sha256")) {
            return new DOMDigestMethod.SHA256((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA384)) {
            return new DOMDigestMethod.SHA384((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals("http://www.w3.org/2001/04/xmlenc#sha512")) {
            return new DOMDigestMethod.SHA512((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals("http://www.w3.org/2001/04/xmlenc#ripemd160")) {
            return new DOMDigestMethod.RIPEMD160((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(MessageDigestAlgorithm.ALGO_ID_DIGEST_WHIRLPOOL)) {
            return new DOMDigestMethod.WHIRLPOOL((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA3_224)) {
            return new DOMDigestMethod.SHA3_224((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA3_256)) {
            return new DOMDigestMethod.SHA3_256((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA3_384)) {
            return new DOMDigestMethod.SHA3_384((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA3_512)) {
            return new DOMDigestMethod.SHA3_512((AlgorithmParameterSpec) params);
        }
        throw new NoSuchAlgorithmException("unsupported algorithm");
    }

    public SignatureMethod newSignatureMethod(String algorithm, SignatureMethodParameterSpec params) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        if (algorithm == null) {
            throw new NullPointerException();
        }
        if (algorithm.equals("http://www.w3.org/2000/09/xmldsig#rsa-sha1")) {
            return new DOMSignatureMethod.SHA1withRSA((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA224)) {
            return new DOMSignatureMethod.SHA224withRSA((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256")) {
            return new DOMSignatureMethod.SHA256withRSA((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384")) {
            return new DOMSignatureMethod.SHA384withRSA((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512")) {
            return new DOMSignatureMethod.SHA512withRSA((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_RIPEMD160)) {
            return new DOMSignatureMethod.RIPEMD160withRSA((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1_MGF1)) {
            return new DOMSignatureMethod.SHA1withRSAandMGF1((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA224_MGF1)) {
            return new DOMSignatureMethod.SHA224withRSAandMGF1((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256_MGF1)) {
            return new DOMSignatureMethod.SHA256withRSAandMGF1((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384_MGF1)) {
            return new DOMSignatureMethod.SHA384withRSAandMGF1((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA512_MGF1)) {
            return new DOMSignatureMethod.SHA512withRSAandMGF1((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA3_224_MGF1)) {
            return new DOMSignatureMethod.SHA3_224withRSAandMGF1((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA3_256_MGF1)) {
            return new DOMSignatureMethod.SHA3_256withRSAandMGF1((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA3_384_MGF1)) {
            return new DOMSignatureMethod.SHA3_384withRSAandMGF1((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA3_512_MGF1)) {
            return new DOMSignatureMethod.SHA3_512withRSAandMGF1((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_PSS)) {
            return new DOMRSAPSSSignatureMethod.RSAPSS((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals("http://www.w3.org/2007/05/xmldsig-more#ripemd160-rsa-MGF1")) {
            return new DOMSignatureMethod.RIPEMD160withRSAandMGF1((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals("http://www.w3.org/2000/09/xmldsig#dsa-sha1")) {
            return new DOMSignatureMethod.SHA1withDSA((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_DSA_SHA256)) {
            return new DOMSignatureMethod.SHA256withDSA((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals("http://www.w3.org/2000/09/xmldsig#hmac-sha1")) {
            return new DOMHMACSignatureMethod.SHA1((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_MAC_HMAC_SHA224)) {
            return new DOMHMACSignatureMethod.SHA224((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256")) {
            return new DOMHMACSignatureMethod.SHA256((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384")) {
            return new DOMHMACSignatureMethod.SHA384((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512")) {
            return new DOMHMACSignatureMethod.SHA512((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_MAC_HMAC_RIPEMD160)) {
            return new DOMHMACSignatureMethod.RIPEMD160((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1")) {
            return new DOMSignatureMethod.SHA1withECDSA((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA224)) {
            return new DOMSignatureMethod.SHA224withECDSA((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA256)) {
            return new DOMSignatureMethod.SHA256withECDSA((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA384)) {
            return new DOMSignatureMethod.SHA384withECDSA((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA512)) {
            return new DOMSignatureMethod.SHA512withECDSA((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA3_224)) {
            return new DOMSignatureMethod.SHA3_224withECDSA((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA3_256)) {
            return new DOMSignatureMethod.SHA3_256withECDSA((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA3_384)) {
            return new DOMSignatureMethod.SHA3_384withECDSA((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA3_512)) {
            return new DOMSignatureMethod.SHA3_512withECDSA((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_ECDSA_RIPEMD160)) {
            return new DOMSignatureMethod.RIPEMD160withECDSA((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_EDDSA_ED25519)) {
            return new DOMSignatureMethod.EDDSA_ED25519((AlgorithmParameterSpec) params);
        }
        if (algorithm.equals(org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_EDDSA_ED448)) {
            return new DOMSignatureMethod.EDDSA_ED448((AlgorithmParameterSpec) params);
        }
        throw new NoSuchAlgorithmException("unsupported algorithm");
    }

    public Transform newTransform(String algorithm, TransformParameterSpec params) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        TransformService spi;
        if (getProvider() == null) {
            spi = TransformService.getInstance(algorithm, "DOM");
        } else {
            try {
                spi = TransformService.getInstance(algorithm, "DOM", getProvider());
            } catch (NoSuchAlgorithmException e) {
                spi = TransformService.getInstance(algorithm, "DOM");
            }
        }
        spi.init(params);
        return new DOMTransform(spi);
    }

    public Transform newTransform(String algorithm, XMLStructure params) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        TransformService spi;
        if (getProvider() == null) {
            spi = TransformService.getInstance(algorithm, "DOM");
        } else {
            try {
                spi = TransformService.getInstance(algorithm, "DOM", getProvider());
            } catch (NoSuchAlgorithmException e) {
                spi = TransformService.getInstance(algorithm, "DOM");
            }
        }
        if (params == null) {
            spi.init((TransformParameterSpec) null);
        } else {
            spi.init(params, (XMLCryptoContext) null);
        }
        return new DOMTransform(spi);
    }

    public CanonicalizationMethod newCanonicalizationMethod(String algorithm, C14NMethodParameterSpec params) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        TransformService spi;
        if (getProvider() == null) {
            spi = TransformService.getInstance(algorithm, "DOM");
        } else {
            try {
                spi = TransformService.getInstance(algorithm, "DOM", getProvider());
            } catch (NoSuchAlgorithmException e) {
                spi = TransformService.getInstance(algorithm, "DOM");
            }
        }
        spi.init(params);
        return new DOMCanonicalizationMethod(spi);
    }

    public CanonicalizationMethod newCanonicalizationMethod(String algorithm, XMLStructure params) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        TransformService spi;
        if (getProvider() == null) {
            spi = TransformService.getInstance(algorithm, "DOM");
        } else {
            try {
                spi = TransformService.getInstance(algorithm, "DOM", getProvider());
            } catch (NoSuchAlgorithmException e) {
                spi = TransformService.getInstance(algorithm, "DOM");
            }
        }
        if (params == null) {
            spi.init((TransformParameterSpec) null);
        } else {
            spi.init(params, (XMLCryptoContext) null);
        }
        return new DOMCanonicalizationMethod(spi);
    }

    public URIDereferencer getURIDereferencer() {
        return DOMURIDereferencer.INSTANCE;
    }
}
