package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESConstants;
import java.security.GeneralSecurityException;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SigningCertificateDetails.class */
public class SigningCertificateDetails extends XAdESStructure {
    public SigningCertificateDetails(Document document, SignedSignatureProperties ssp, SigningCertificate signingCertificate, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) throws GeneralSecurityException {
        super(document, ssp, XAdESConstants.TAG_SIGNING_CERTIFICATE, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
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
        Element issuerSerial = createElement("IssuerSerial");
        Element x509IssuerName = createElementNS("http://www.w3.org/2000/09/xmldsig#", xmlSignaturePrefix, Constants._TAG_X509ISSUERNAME);
        x509IssuerName.setPrefix(xmlSignaturePrefix);
        x509IssuerName.setTextContent(signingCertificate.getIssuerName());
        Element x509SerialNumber = createElementNS("http://www.w3.org/2000/09/xmldsig#", xmlSignaturePrefix, Constants._TAG_X509SERIALNUMBER);
        x509SerialNumber.setPrefix(xmlSignaturePrefix);
        x509SerialNumber.setTextContent(signingCertificate.getX509SerialNumber() + "");
        issuerSerial.appendChild(x509IssuerName);
        issuerSerial.appendChild(x509SerialNumber);
        cert.appendChild(certDigest);
        cert.appendChild(issuerSerial);
        getNode().appendChild(cert);
    }

    public SigningCertificateDetails(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }
}
