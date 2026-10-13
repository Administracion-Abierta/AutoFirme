package org.apache.xml.security.keys.content.x509;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.RFC2253Parser;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/content/x509/XMLX509IssuerSerial.class */
public class XMLX509IssuerSerial extends SignatureElementProxy implements XMLX509DataContent {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) XMLX509IssuerSerial.class);

    public XMLX509IssuerSerial(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public XMLX509IssuerSerial(Document doc, String x509IssuerName, BigInteger x509SerialNumber) {
        super(doc);
        addReturnToSelf();
        addTextElement(x509IssuerName, Constants._TAG_X509ISSUERNAME);
        addTextElement(x509SerialNumber.toString(), Constants._TAG_X509SERIALNUMBER);
    }

    public XMLX509IssuerSerial(Document doc, String x509IssuerName, String x509SerialNumber) {
        this(doc, x509IssuerName, new BigInteger(x509SerialNumber));
    }

    public XMLX509IssuerSerial(Document doc, String x509IssuerName, int x509SerialNumber) {
        this(doc, x509IssuerName, new BigInteger(Integer.toString(x509SerialNumber)));
    }

    public XMLX509IssuerSerial(Document doc, X509Certificate x509certificate) {
        this(doc, x509certificate.getIssuerX500Principal().getName(), x509certificate.getSerialNumber());
    }

    public BigInteger getSerialNumber() {
        String text = getTextFromChildElement(Constants._TAG_X509SERIALNUMBER, "http://www.w3.org/2000/09/xmldsig#");
        LOG.debug("X509SerialNumber text: {}", text);
        return new BigInteger(text);
    }

    public int getSerialNumberInteger() {
        return getSerialNumber().intValue();
    }

    public String getIssuerName() {
        return RFC2253Parser.normalize(getTextFromChildElement(Constants._TAG_X509ISSUERNAME, "http://www.w3.org/2000/09/xmldsig#"));
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof XMLX509IssuerSerial)) {
            return false;
        }
        XMLX509IssuerSerial other = (XMLX509IssuerSerial) obj;
        return getSerialNumber().equals(other.getSerialNumber()) && getIssuerName().equals(other.getIssuerName());
    }

    public int hashCode() {
        int result = (31 * 17) + getSerialNumber().hashCode();
        return (31 * result) + getIssuerName().hashCode();
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return Constants._TAG_X509ISSUERSERIAL;
    }
}
