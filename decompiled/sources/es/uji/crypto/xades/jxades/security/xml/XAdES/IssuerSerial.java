package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/IssuerSerial.class */
public class IssuerSerial extends XAdESStructure {
    public IssuerSerial(Document document, XAdESStructure parent, X509Certificate cert, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        this(document, parent, cert.getIssuerX500Principal(), cert.getSerialNumber(), xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public IssuerSerial(Document document, XAdESStructure parent, X500Principal issuer, BigInteger serialNumber, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(document, parent, "IssuerSerial", xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        Element thisElement = getElement();
        Element element = createElement(Constants._TAG_X509ISSUERNAME);
        thisElement.appendChild(element);
        element.setTextContent(issuer.getName());
        Element element2 = createElement(Constants._TAG_X509SERIALNUMBER);
        thisElement.appendChild(element2);
        element2.setTextContent(serialNumber.toString());
    }

    public IssuerSerial(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public String getIssuerName() {
        return getChildElementTextContent(Constants._TAG_X509ISSUERNAME);
    }

    public String getSerialNumber() {
        return getChildElementTextContent(Constants._TAG_X509SERIALNUMBER);
    }
}
