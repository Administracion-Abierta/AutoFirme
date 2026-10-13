package org.apache.xml.security.keys.content;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.x509.XMLX509CRL;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.keys.content.x509.XMLX509Digest;
import org.apache.xml.security.keys.content.x509.XMLX509IssuerSerial;
import org.apache.xml.security.keys.content.x509.XMLX509SKI;
import org.apache.xml.security.keys.content.x509.XMLX509SubjectName;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/content/X509Data.class */
public class X509Data extends SignatureElementProxy implements KeyInfoContent {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) X509Data.class);

    public X509Data(Document doc) {
        super(doc);
        addReturnToSelf();
    }

    public X509Data(Element element, String baseURI) throws XMLSecurityException {
        Node sibling;
        super(element, baseURI);
        Node firstChild = getFirstChild();
        while (true) {
            sibling = firstChild;
            if (sibling == null || sibling.getNodeType() == 1) {
                break;
            } else {
                firstChild = sibling.getNextSibling();
            }
        }
        if (sibling == null || sibling.getNodeType() != 1) {
            Object[] exArgs = {"Elements", Constants._TAG_X509DATA};
            throw new XMLSecurityException("xml.WrongContent", exArgs);
        }
    }

    public void addIssuerSerial(String X509IssuerName, BigInteger X509SerialNumber) {
        add(new XMLX509IssuerSerial(getDocument(), X509IssuerName, X509SerialNumber));
    }

    public void addIssuerSerial(String X509IssuerName, String X509SerialNumber) {
        add(new XMLX509IssuerSerial(getDocument(), X509IssuerName, X509SerialNumber));
    }

    public void addIssuerSerial(String X509IssuerName, int X509SerialNumber) {
        add(new XMLX509IssuerSerial(getDocument(), X509IssuerName, X509SerialNumber));
    }

    public void add(XMLX509IssuerSerial xmlX509IssuerSerial) {
        appendSelf(xmlX509IssuerSerial);
        addReturnToSelf();
    }

    public void addSKI(byte[] skiBytes) {
        add(new XMLX509SKI(getDocument(), skiBytes));
    }

    public void addSKI(X509Certificate x509certificate) throws XMLSecurityException {
        add(new XMLX509SKI(getDocument(), x509certificate));
    }

    public void add(XMLX509SKI xmlX509SKI) {
        appendSelf(xmlX509SKI);
        addReturnToSelf();
    }

    public void addSubjectName(String subjectName) {
        add(new XMLX509SubjectName(getDocument(), subjectName));
    }

    public void addSubjectName(X509Certificate x509certificate) {
        add(new XMLX509SubjectName(getDocument(), x509certificate));
    }

    public void add(XMLX509SubjectName xmlX509SubjectName) {
        appendSelf(xmlX509SubjectName);
        addReturnToSelf();
    }

    public void addCertificate(X509Certificate x509certificate) throws XMLSecurityException {
        add(new XMLX509Certificate(getDocument(), x509certificate));
    }

    public void addCertificate(byte[] x509certificateBytes) {
        add(new XMLX509Certificate(getDocument(), x509certificateBytes));
    }

    public void add(XMLX509Certificate xmlX509Certificate) {
        appendSelf(xmlX509Certificate);
        addReturnToSelf();
    }

    public void addCRL(byte[] crlBytes) {
        add(new XMLX509CRL(getDocument(), crlBytes));
    }

    public void add(XMLX509CRL xmlX509CRL) {
        appendSelf(xmlX509CRL);
        addReturnToSelf();
    }

    public void addDigest(X509Certificate x509certificate, String algorithmURI) throws XMLSecurityException {
        add(new XMLX509Digest(getDocument(), x509certificate, algorithmURI));
    }

    public void addDigest(byte[] x509CertificateDigestBytes, String algorithmURI) {
        add(new XMLX509Digest(getDocument(), x509CertificateDigestBytes, algorithmURI));
    }

    public void add(XMLX509Digest xmlX509Digest) {
        appendSelf(xmlX509Digest);
        addReturnToSelf();
    }

    public void addUnknownElement(Element element) {
        appendSelf(element);
        addReturnToSelf();
    }

    public int lengthIssuerSerial() {
        return length("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_X509ISSUERSERIAL);
    }

    public int lengthSKI() {
        return length("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_X509SKI);
    }

    public int lengthSubjectName() {
        return length("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_X509SUBJECTNAME);
    }

    public int lengthCertificate() {
        return length("http://www.w3.org/2000/09/xmldsig#", "X509Certificate");
    }

    public int lengthCRL() {
        return length("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_X509CRL);
    }

    public int lengthDigest() {
        return length("http://www.w3.org/2009/xmldsig11#", Constants._TAG_X509DIGEST);
    }

    public int lengthUnknownElement() {
        int result = 0;
        Node firstChild = getFirstChild();
        while (true) {
            Node n = firstChild;
            if (n != null) {
                if (n.getNodeType() == 1 && !n.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")) {
                    result++;
                }
                firstChild = n.getNextSibling();
            } else {
                return result;
            }
        }
    }

    public XMLX509IssuerSerial itemIssuerSerial(int i) throws XMLSecurityException {
        Element e = XMLUtils.selectDsNode(getFirstChild(), Constants._TAG_X509ISSUERSERIAL, i);
        if (e != null) {
            return new XMLX509IssuerSerial(e, this.baseURI);
        }
        return null;
    }

    public XMLX509SKI itemSKI(int i) throws XMLSecurityException {
        Element e = XMLUtils.selectDsNode(getFirstChild(), Constants._TAG_X509SKI, i);
        if (e != null) {
            return new XMLX509SKI(e, this.baseURI);
        }
        return null;
    }

    public XMLX509SubjectName itemSubjectName(int i) throws XMLSecurityException {
        Element e = XMLUtils.selectDsNode(getFirstChild(), Constants._TAG_X509SUBJECTNAME, i);
        if (e != null) {
            return new XMLX509SubjectName(e, this.baseURI);
        }
        return null;
    }

    public XMLX509Certificate itemCertificate(int i) throws XMLSecurityException {
        Element e = XMLUtils.selectDsNode(getFirstChild(), "X509Certificate", i);
        if (e != null) {
            return new XMLX509Certificate(e, this.baseURI);
        }
        return null;
    }

    public XMLX509CRL itemCRL(int i) throws XMLSecurityException {
        Element e = XMLUtils.selectDsNode(getFirstChild(), Constants._TAG_X509CRL, i);
        if (e != null) {
            return new XMLX509CRL(e, this.baseURI);
        }
        return null;
    }

    public XMLX509Digest itemDigest(int i) throws XMLSecurityException {
        Element e = XMLUtils.selectDs11Node(getFirstChild(), Constants._TAG_X509DIGEST, i);
        if (e != null) {
            return new XMLX509Digest(e, this.baseURI);
        }
        return null;
    }

    public Element itemUnknownElement(int i) {
        LOG.debug("itemUnknownElement not implemented: {}", Integer.valueOf(i));
        return null;
    }

    public boolean containsIssuerSerial() {
        return lengthIssuerSerial() > 0;
    }

    public boolean containsSKI() {
        return lengthSKI() > 0;
    }

    public boolean containsSubjectName() {
        return lengthSubjectName() > 0;
    }

    public boolean containsCertificate() {
        return lengthCertificate() > 0;
    }

    public boolean containsDigest() {
        return lengthDigest() > 0;
    }

    public boolean containsCRL() {
        return lengthCRL() > 0;
    }

    public boolean containsUnknownElement() {
        return lengthUnknownElement() > 0;
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return Constants._TAG_X509DATA;
    }
}
