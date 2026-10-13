package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESConstants;
import java.security.GeneralSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SigningCertificateV2Details.class */
public class SigningCertificateV2Details extends XAdESStructure {
    public SigningCertificateV2Details(Document document, SignedSignatureProperties ssp, SigningCertificateV2 signingCertificate, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) throws GeneralSecurityException {
        super(document, ssp, XAdESConstants.TAG_SIGNING_CERTIFICATE_V2, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        Element cert = createElement("Cert");
        Element certDigest = createElement("CertDigest");
        Element digestMethod = createElementNS("http://www.w3.org/2000/09/xmldsig#", xmlSignaturePrefix, "DigestMethod");
        digestMethod.setPrefix(xmlSignaturePrefix);
        digestMethod.setAttributeNS(xmlSignaturePrefix, "Algorithm", signingCertificate.getDigestMethodAlgorithm());
        Element digestValue = createElementNS("http://www.w3.org/2000/09/xmldsig#", xmlSignaturePrefix, "DigestValue");
        digestValue.setPrefix(xmlSignaturePrefix);
        digestValue.setTextContent(signingCertificate.getDigestValue());
        certDigest.appendChild(digestMethod);
        certDigest.appendChild(digestValue);
        Element issuerSerial = createElement("IssuerSerialV2");
        issuerSerial.setTextContent(signingCertificate.getIssuerSerialV2());
        cert.appendChild(certDigest);
        cert.appendChild(issuerSerial);
        getNode().appendChild(cert);
    }

    public SigningCertificateV2Details(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }
}
