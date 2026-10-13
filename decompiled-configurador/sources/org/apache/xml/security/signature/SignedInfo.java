package org.apache.xml.security.signature;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/signature/SignedInfo.class */
public class SignedInfo extends Manifest {
    private final SignatureAlgorithm signatureAlgorithm;
    private byte[] c14nizedBytes;
    private Element c14nMethod;
    private Element signatureMethod;

    public SignedInfo(Document doc) throws XMLSecurityException {
        this(doc, "http://www.w3.org/2000/09/xmldsig#dsa-sha1", "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
    }

    public SignedInfo(Document doc, String signatureMethodURI, String canonicalizationMethodURI) throws XMLSecurityException {
        this(doc, signatureMethodURI, 0, canonicalizationMethodURI, null, null);
    }

    public SignedInfo(Document doc, String signatureMethodURI, String canonicalizationMethodURI, Provider provider) throws XMLSecurityException {
        this(doc, signatureMethodURI, 0, canonicalizationMethodURI, provider, null);
    }

    public SignedInfo(Document doc, String signatureMethodURI, int hMACOutputLength, String canonicalizationMethodURI) throws XMLSecurityException {
        this(doc, signatureMethodURI, hMACOutputLength, canonicalizationMethodURI, null, null);
    }

    public SignedInfo(Document doc, String signatureMethodURI, int hMACOutputLength, String canonicalizationMethodURI, Provider provider, AlgorithmParameterSpec spec) throws XMLSecurityException {
        super(doc);
        this.c14nMethod = XMLUtils.createElementInSignatureSpace(getDocument(), Constants._TAG_CANONICALIZATIONMETHOD);
        this.c14nMethod.setAttributeNS(null, "Algorithm", canonicalizationMethodURI);
        appendSelf(this.c14nMethod);
        addReturnToSelf();
        if (hMACOutputLength > 0) {
            this.signatureAlgorithm = new SignatureAlgorithm(getDocument(), signatureMethodURI, hMACOutputLength, provider);
        } else {
            this.signatureAlgorithm = new SignatureAlgorithm(getDocument(), signatureMethodURI, provider, spec);
        }
        this.signatureMethod = this.signatureAlgorithm.getElement();
        appendSelf(this.signatureMethod);
        addReturnToSelf();
    }

    public SignedInfo(Document doc, Element signatureMethodElem, Element canonicalizationMethodElem) throws XMLSecurityException {
        this(doc, signatureMethodElem, canonicalizationMethodElem, (Provider) null);
    }

    public SignedInfo(Document doc, Element signatureMethodElem, Element canonicalizationMethodElem, Provider provider) throws XMLSecurityException {
        super(doc);
        this.c14nMethod = canonicalizationMethodElem;
        appendSelf(this.c14nMethod);
        addReturnToSelf();
        this.signatureAlgorithm = new SignatureAlgorithm(signatureMethodElem, (String) null, provider);
        this.signatureMethod = this.signatureAlgorithm.getElement();
        appendSelf(this.signatureMethod);
        addReturnToSelf();
    }

    public SignedInfo(Element element, String baseURI) throws XMLSecurityException {
        this(element, baseURI, true, (Provider) null);
    }

    public SignedInfo(Element element, String baseURI, boolean secureValidation) throws XMLSecurityException {
        this(element, baseURI, secureValidation, (Provider) null);
    }

    public SignedInfo(Element element, String baseURI, boolean secureValidation, Provider provider) throws XMLSecurityException {
        super(element, baseURI, secureValidation);
        this.c14nMethod = XMLUtils.getNextElement(element.getFirstChild());
        if (this.c14nMethod == null || !"http://www.w3.org/2000/09/xmldsig#".equals(this.c14nMethod.getNamespaceURI()) || !Constants._TAG_CANONICALIZATIONMETHOD.equals(this.c14nMethod.getLocalName())) {
            Object[] exArgs = {Constants._TAG_CANONICALIZATIONMETHOD, "SignedInfo"};
            throw new XMLSignatureException("xml.WrongContent", exArgs);
        }
        this.signatureMethod = XMLUtils.getNextElement(this.c14nMethod.getNextSibling());
        if (this.signatureMethod == null || !"http://www.w3.org/2000/09/xmldsig#".equals(this.signatureMethod.getNamespaceURI()) || !"SignatureMethod".equals(this.signatureMethod.getLocalName())) {
            Object[] exArgs2 = {"SignatureMethod", "SignedInfo"};
            throw new XMLSignatureException("xml.WrongContent", exArgs2);
        }
        this.signatureAlgorithm = new SignatureAlgorithm(this.signatureMethod, getBaseURI(), secureValidation, provider);
    }

    public boolean verify() throws XMLSecurityException {
        return super.verifyReferences(false);
    }

    public boolean verify(boolean followManifests) throws XMLSecurityException {
        return super.verifyReferences(followManifests);
    }

    public byte[] getCanonicalizedOctetStream() throws XMLSecurityException, IOException {
        if (this.c14nizedBytes == null) {
            Canonicalizer c14nizer = Canonicalizer.getInstance(getCanonicalizationMethodURI());
            String inclusiveNamespaces = getInclusiveNamespaces();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Throwable th = null;
            try {
                try {
                    if (inclusiveNamespaces == null) {
                        c14nizer.canonicalizeSubtree(getElement(), baos);
                    } else {
                        c14nizer.canonicalizeSubtree(getElement(), inclusiveNamespaces, baos);
                    }
                    this.c14nizedBytes = baos.toByteArray();
                    if (0 != 0) {
                        try {
                            baos.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        baos.close();
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (th != null) {
                    try {
                        baos.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    baos.close();
                }
                throw th4;
            }
        }
        return (byte[]) this.c14nizedBytes.clone();
    }

    public void signInOctetStream(OutputStream os) throws XMLSecurityException {
        if (this.c14nizedBytes == null) {
            Canonicalizer c14nizer = Canonicalizer.getInstance(getCanonicalizationMethodURI());
            String inclusiveNamespaces = getInclusiveNamespaces();
            if (inclusiveNamespaces == null) {
                c14nizer.canonicalizeSubtree(getElement(), os);
                return;
            } else {
                c14nizer.canonicalizeSubtree(getElement(), inclusiveNamespaces, os);
                return;
            }
        }
        try {
            os.write(this.c14nizedBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCanonicalizationMethodURI() {
        return this.c14nMethod.getAttributeNS(null, "Algorithm");
    }

    public String getSignatureMethodURI() {
        Element signatureElement = getSignatureMethodElement();
        if (signatureElement != null) {
            return signatureElement.getAttributeNS(null, "Algorithm");
        }
        return null;
    }

    public Element getSignatureMethodElement() {
        return this.signatureMethod;
    }

    public SecretKey createSecretKey(byte[] secretKeyBytes) {
        return new SecretKeySpec(secretKeyBytes, this.signatureAlgorithm.getJCEAlgorithmString());
    }

    public SignatureAlgorithm getSignatureAlgorithm() {
        return this.signatureAlgorithm;
    }

    @Override // org.apache.xml.security.signature.Manifest, org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return "SignedInfo";
    }

    public String getInclusiveNamespaces() {
        Element inclusiveElement;
        String c14nMethodURI = getCanonicalizationMethodURI();
        if (("http://www.w3.org/2001/10/xml-exc-c14n#".equals(c14nMethodURI) || "http://www.w3.org/2001/10/xml-exc-c14n#WithComments".equals(c14nMethodURI)) && (inclusiveElement = XMLUtils.getNextElement(this.c14nMethod.getFirstChild())) != null) {
            try {
                String inclusiveNamespaces = new InclusiveNamespaces(inclusiveElement, "http://www.w3.org/2001/10/xml-exc-c14n#").getInclusiveNamespaces();
                return inclusiveNamespaces;
            } catch (XMLSecurityException e) {
                return null;
            }
        }
        return null;
    }
}
