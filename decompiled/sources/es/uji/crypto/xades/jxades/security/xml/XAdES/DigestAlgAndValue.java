package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.uji.crypto.xades.jxades.util.Base64;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.cert.X509CRL;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/DigestAlgAndValue.class */
public class DigestAlgAndValue extends XAdESStructure {
    private static final String ALGORITHM_ATTR = "Algorithm";
    private static final String DIGEST_METHOD_ELEMENT = "DigestMethod";
    private static final String DIGEST_VALUE_ELEMENT = "DigestValue";

    public DigestAlgAndValue(Document document, XAdESStructure parent, X509CRL crl, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) throws GeneralSecurityException {
        this(document, parent, "DigestAlgAndValue", crl.getEncoded(), xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    protected DigestAlgAndValue(Document document, XAdESStructure parent, String elementName, byte[] data, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) throws GeneralSecurityException {
        super(document, parent, elementName, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        Element thisElement = getElement();
        Element element = createElement("DigestMethod");
        element.setPrefix(xmlSignaturePrefix);
        thisElement.appendChild(element);
        element.setAttributeNS(xmlSignaturePrefix, "Algorithm", "http://www.w3.org/2000/09/xmldsig#sha1");
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        String digestValue = Base64.encodeBytes(md.digest(data));
        Element element2 = createElement("DigestValue");
        element2.setPrefix(xmlSignaturePrefix);
        thisElement.appendChild(element2);
        element2.setTextContent(digestValue);
    }

    protected DigestAlgAndValue(Document document, XAdESStructure parent, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) throws GeneralSecurityException {
        super(document, parent, "DigestAlgAndValue", xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public DigestAlgAndValue(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public String getDigestMethod() {
        return getChildElementTextContent("DigestMethod");
    }

    public byte[] getDigestValue() throws IOException {
        String value = getChildElementTextContent("DigestValue");
        if (value == null) {
            return null;
        }
        String value2 = value.trim();
        if (value2.length() > 0) {
            return Base64.decode(value2);
        }
        return null;
    }
}
