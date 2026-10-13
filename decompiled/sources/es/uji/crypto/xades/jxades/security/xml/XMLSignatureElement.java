package es.uji.crypto.xades.jxades.security.xml;

import java.security.Key;
import java.security.KeyException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XMLSignatureElement.class */
public class XMLSignatureElement {
    private Element signatureElement;
    private XMLSignatureFactory xmlSignatureFactory;
    private KeySelector keySelector;

    public XMLSignatureElement(Element signatureElement) {
        if (signatureElement == null) {
            throw new IllegalArgumentException("Signature Element can not be NULL.");
        }
        this.signatureElement = signatureElement;
    }

    protected XMLSignatureFactory getXMLSignatureFactory() {
        if (this.xmlSignatureFactory == null) {
            this.xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");
        }
        return this.xmlSignatureFactory;
    }

    protected KeySelector getKeySelector() {
        if (this.keySelector == null) {
            this.keySelector = new KeyValueKeySelector();
        }
        return this.keySelector;
    }

    public XMLSignature getXMLSignature() throws MarshalException {
        DOMValidateContext valContext = new DOMValidateContext(getKeySelector(), this.signatureElement);
        XMLSignatureFactory fac = getXMLSignatureFactory();
        return fac.unmarshalXMLSignature(valContext);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    public KeyInfo getKeyInfo() throws MarshalException {
        XMLSignature xmlSignature = getXMLSignature();
        if (xmlSignature != null) {
            return xmlSignature.getKeyInfo();
        }
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    public X509Data getX509Data() throws MarshalException {
        KeyInfo keyInfo = getKeyInfo();
        if (keyInfo != null) {
            for (Object o1 : keyInfo.getContent()) {
                if (o1 instanceof X509Data) {
                    return (X509Data) o1;
                }
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    public X509Certificate getX509Certificate() throws MarshalException {
        X509Data x509Data = getX509Data();
        if (x509Data != null) {
            for (Object o1 : x509Data.getContent()) {
                if (o1 instanceof X509Certificate) {
                    return (X509Certificate) o1;
                }
            }
            return null;
        }
        return null;
    }

    public SignatureStatus validate() {
        String str;
        SignatureStatus signatureStatus;
        DOMValidateContext dOMValidateContext = new DOMValidateContext(getKeySelector(), this.signatureElement);
        XMLSignatureFactory xMLSignatureFactory = getXMLSignatureFactory();
        String attribute = this.signatureElement.getAttribute("Id");
        try {
            XMLSignature xMLSignatureUnmarshalXMLSignature = xMLSignatureFactory.unmarshalXMLSignature(dOMValidateContext);
            String id = xMLSignatureUnmarshalXMLSignature.getId();
            if (attribute == null || (id != null && !id.equals(attribute))) {
                str = attribute;
                str = attribute;
                str = id;
            }
            str = attribute;
            str = attribute;
            str = attribute;
            try {
                if (!xMLSignatureUnmarshalXMLSignature.validate(dOMValidateContext)) {
                    signatureStatus = new SignatureStatus(str, ValidateResult.INVALID);
                    XMLSignature.SignatureValue signatureValue = xMLSignatureUnmarshalXMLSignature.getSignatureValue();
                    try {
                        if (!signatureValue.validate(dOMValidateContext)) {
                            signatureStatus.addInvalidSignatureReason(new InvalidSignatureReason(signatureValue));
                        }
                    } catch (XMLSignatureException e) {
                        signatureStatus.addInvalidSignatureReason(new InvalidSignatureReason("SignatureValue", e));
                    } catch (NullPointerException e2) {
                        signatureStatus.addInvalidSignatureReason(new InvalidSignatureReason("SignatureValue", e2));
                    }
                    int i = 0;
                    for (Reference reference : xMLSignatureUnmarshalXMLSignature.getSignedInfo().getReferences()) {
                        try {
                            if (!reference.validate(dOMValidateContext)) {
                                signatureStatus.addInvalidSignatureReason(new InvalidSignatureReason(reference));
                            }
                        } catch (XMLSignatureException e3) {
                            signatureStatus.addInvalidSignatureReason(new InvalidSignatureReason("Reference", e3));
                        } catch (NullPointerException e4) {
                            signatureStatus.addInvalidSignatureReason(new InvalidSignatureReason("Reference", e4));
                        }
                        i++;
                    }
                } else {
                    signatureStatus = new SignatureStatus(str, ValidateResult.VALID);
                }
                return signatureStatus;
            } catch (XMLSignatureException e5) {
                return new SignatureStatus(str, ValidateResult.INVALID, new InvalidSignatureReason("XMLSignature", e5));
            } catch (ClassCastException e6) {
                return new SignatureStatus(str, ValidateResult.INVALID, new InvalidSignatureReason(InvalidSignature.NOT_COMPATIBLE_VALIDATE_CONTEXT, e6));
            } catch (NullPointerException e7) {
                return new SignatureStatus(str, ValidateResult.INVALID, new InvalidSignatureReason("XMLSignature", e7));
            }
        } catch (MarshalException e8) {
            return new SignatureStatus(attribute, e8);
        } catch (ClassCastException e9) {
            return new SignatureStatus(attribute, e9);
        } catch (NullPointerException e10) {
            return new SignatureStatus(attribute, e10);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XMLSignatureElement$KeyValueKeySelector.class */
    private static class KeyValueKeySelector extends KeySelector {
        private KeyValueKeySelector() {
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.KeySelectorException */
        public KeySelectorResult select(KeyInfo keyInfo, KeySelector.Purpose purpose, AlgorithmMethod method, XMLCryptoContext context) throws KeySelectorException {
            if (keyInfo == null) {
                throw new KeySelectorException("Null KeyInfo object!");
            }
            SignatureMethod sm = (SignatureMethod) method;
            List list = keyInfo.getContent();
            for (int i = 0; i < list.size(); i++) {
                KeyValue keyValue = (XMLStructure) list.get(i);
                if (keyValue instanceof KeyValue) {
                    try {
                        PublicKey pk = keyValue.getPublicKey();
                        if (algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
                            return new SimpleKeySelectorResult(pk);
                        }
                    } catch (KeyException ke) {
                        throw new KeySelectorException(ke);
                    }
                } else if (keyValue instanceof X509Data) {
                    X509Certificate cert = null;
                    List dataList = ((X509Data) keyValue).getContent();
                    for (Object dataObject : dataList) {
                        if (dataObject instanceof X509Certificate) {
                            cert = (X509Certificate) dataObject;
                        }
                    }
                    if (cert != null) {
                        if (algEquals(sm.getAlgorithm(), cert.getPublicKey().getAlgorithm())) {
                            return new X509DataKeySelectorResult((X509Data) keyValue);
                        }
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            throw new KeySelectorException("No KeyValue element found!");
        }

        static boolean algEquals(String algURI, String algName) {
            if (algName.equalsIgnoreCase("DSA") && algURI.toUpperCase().contains("DSA")) {
                return true;
            }
            if (algName.equalsIgnoreCase("RSA") && algURI.toUpperCase().contains("RSA")) {
                return true;
            }
            return false;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XMLSignatureElement$SimpleKeySelectorResult.class */
    private static class SimpleKeySelectorResult implements KeySelectorResult {
        private PublicKey pk;

        SimpleKeySelectorResult(PublicKey pk) {
            this.pk = pk;
        }

        public Key getKey() {
            return this.pk;
        }
    }
}
