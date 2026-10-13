package org.apache.jcp.xml.dsig.internal.dom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/jcp/xml/dsig/internal/dom/DOMX509Data.class */
public final class DOMX509Data extends DOMStructure implements X509Data {
    private final List<Object> content;
    private CertificateFactory cf;

    public DOMX509Data(List<?> content) {
        if (content == null) {
            throw new NullPointerException("content cannot be null");
        }
        List<Object> contentCopy = new ArrayList<>(content);
        if (contentCopy.isEmpty()) {
            throw new IllegalArgumentException("content cannot be empty");
        }
        int size = contentCopy.size();
        for (int i = 0; i < size; i++) {
            Object x509Type = contentCopy.get(i);
            if (x509Type instanceof String) {
                new X500Principal((String) x509Type);
            } else if (!(x509Type instanceof byte[]) && !(x509Type instanceof X509Certificate) && !(x509Type instanceof X509CRL) && !(x509Type instanceof XMLStructure)) {
                throw new ClassCastException("content[" + i + "] is not a valid X509Data type");
            }
        }
        this.content = Collections.unmodifiableList(contentCopy);
    }

    public DOMX509Data(Element xdElem) throws MarshalException {
        List<Object> newContent = new ArrayList<>();
        Node firstChild = xdElem.getFirstChild();
        while (true) {
            Node firstChild2 = firstChild;
            if (firstChild2 != null) {
                if (firstChild2.getNodeType() == 1) {
                    Element childElem = (Element) firstChild2;
                    String localName = childElem.getLocalName();
                    String namespace = childElem.getNamespaceURI();
                    if ("X509Certificate".equals(localName) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                        newContent.add(unmarshalX509Certificate(childElem));
                    } else if (Constants._TAG_X509ISSUERSERIAL.equals(localName) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                        newContent.add(new DOMX509IssuerSerial(childElem));
                    } else if (Constants._TAG_X509SUBJECTNAME.equals(localName) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                        newContent.add(childElem.getFirstChild().getNodeValue());
                    } else if (Constants._TAG_X509SKI.equals(localName) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                        String content = XMLUtils.getFullTextChildrenFromNode(childElem);
                        newContent.add(XMLUtils.decode(content));
                    } else if (Constants._TAG_X509CRL.equals(localName) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                        newContent.add(unmarshalX509CRL(childElem));
                    } else {
                        newContent.add(new javax.xml.crypto.dom.DOMStructure(childElem));
                    }
                }
                firstChild = firstChild2.getNextSibling();
            } else {
                this.content = Collections.unmodifiableList(newContent);
                return;
            }
        }
    }

    public List<Object> getContent() {
        return this.content;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.DOMStructure
    public void marshal(Node parent, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element xdElem = DOMUtils.createElement(ownerDoc, Constants._TAG_X509DATA, "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        for (Object object : this.content) {
            if (object instanceof X509Certificate) {
                marshalCert((X509Certificate) object, xdElem, ownerDoc, dsPrefix);
            } else if (object instanceof XMLStructure) {
                if (object instanceof X509IssuerSerial) {
                    ((DOMX509IssuerSerial) object).marshal(xdElem, dsPrefix, context);
                } else {
                    javax.xml.crypto.dom.DOMStructure domContent = (javax.xml.crypto.dom.DOMStructure) object;
                    DOMUtils.appendChild(xdElem, domContent.getNode());
                }
            } else if (object instanceof byte[]) {
                marshalSKI((byte[]) object, xdElem, ownerDoc, dsPrefix);
            } else if (object instanceof String) {
                marshalSubjectName((String) object, xdElem, ownerDoc, dsPrefix);
            } else if (object instanceof X509CRL) {
                marshalCRL((X509CRL) object, xdElem, ownerDoc, dsPrefix);
            }
        }
        parent.appendChild(xdElem);
    }

    private void marshalSKI(byte[] skid, Node parent, Document doc, String dsPrefix) {
        Element skidElem = DOMUtils.createElement(doc, Constants._TAG_X509SKI, "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        skidElem.appendChild(doc.createTextNode(XMLUtils.encodeToString(skid)));
        parent.appendChild(skidElem);
    }

    private void marshalSubjectName(String name, Node parent, Document doc, String dsPrefix) {
        Element snElem = DOMUtils.createElement(doc, Constants._TAG_X509SUBJECTNAME, "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        snElem.appendChild(doc.createTextNode(name));
        parent.appendChild(snElem);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    private void marshalCert(X509Certificate cert, Node parent, Document doc, String dsPrefix) throws MarshalException {
        Element certElem = DOMUtils.createElement(doc, "X509Certificate", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        try {
            certElem.appendChild(doc.createTextNode(XMLUtils.encodeToString(cert.getEncoded())));
            parent.appendChild(certElem);
        } catch (CertificateEncodingException e) {
            throw new MarshalException("Error encoding X509Certificate", e);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    private void marshalCRL(X509CRL crl, Node parent, Document doc, String dsPrefix) throws MarshalException {
        Element crlElem = DOMUtils.createElement(doc, Constants._TAG_X509CRL, "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        try {
            crlElem.appendChild(doc.createTextNode(XMLUtils.encodeToString(crl.getEncoded())));
            parent.appendChild(crlElem);
        } catch (CRLException e) {
            throw new MarshalException("Error encoding X509CRL", e);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    private X509Certificate unmarshalX509Certificate(Element elem) throws Exception {
        try {
            ByteArrayInputStream bs = unmarshalBase64Binary(elem);
            Throwable th = null;
            try {
                try {
                    X509Certificate x509Certificate = (X509Certificate) this.cf.generateCertificate(bs);
                    if (bs != null) {
                        $closeResource(null, bs);
                    }
                    return x509Certificate;
                } catch (Throwable th2) {
                    th = th2;
                    throw th2;
                }
            } catch (Throwable th3) {
                if (bs != null) {
                    $closeResource(th, bs);
                }
                throw th3;
            }
        } catch (IOException e) {
            throw new MarshalException("Error closing stream", e);
        } catch (CertificateException e2) {
            throw new MarshalException("Cannot create X509Certificate", e2);
        }
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) throws Exception {
        if (x0 == null) {
            x1.close();
            return;
        }
        try {
            x1.close();
        } catch (Throwable th) {
            x0.addSuppressed(th);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    private X509CRL unmarshalX509CRL(Element elem) throws Exception {
        try {
            ByteArrayInputStream bs = unmarshalBase64Binary(elem);
            Throwable th = null;
            try {
                try {
                    X509CRL x509crl = (X509CRL) this.cf.generateCRL(bs);
                    if (bs != null) {
                        $closeResource(null, bs);
                    }
                    return x509crl;
                } catch (Throwable th2) {
                    th = th2;
                    throw th2;
                }
            } catch (Throwable th3) {
                if (bs != null) {
                    $closeResource(th, bs);
                }
                throw th3;
            }
        } catch (IOException e) {
            throw new MarshalException("Error closing stream", e);
        } catch (CRLException e2) {
            throw new MarshalException("Cannot create X509CRL", e2);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    private ByteArrayInputStream unmarshalBase64Binary(Element elem) throws MarshalException {
        try {
            if (this.cf == null) {
                this.cf = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID);
            }
            String content = XMLUtils.getFullTextChildrenFromNode(elem);
            return new ByteArrayInputStream(XMLUtils.decode(content));
        } catch (CertificateException e) {
            throw new MarshalException("Cannot create CertificateFactory", e);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof X509Data)) {
            return false;
        }
        X509Data oxd = (X509Data) o;
        List<?> ocontent = oxd.getContent();
        int size = this.content.size();
        if (size != ocontent.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            Object x = this.content.get(i);
            Object ox = ocontent.get(i);
            if (x instanceof byte[]) {
                if (!(ox instanceof byte[]) || !Arrays.equals((byte[]) x, (byte[]) ox)) {
                    return false;
                }
            } else if (!x.equals(ox)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int result = (31 * 17) + this.content.hashCode();
        return result;
    }
}
