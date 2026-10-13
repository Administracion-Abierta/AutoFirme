package org.apache.jcp.xml.dsig.internal.dom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.XMLSignatureException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.UnsyncBufferedOutputStream;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignedInfo.class */
public final class DOMSignedInfo extends DOMStructure implements SignedInfo {
    public static final int MAXIMUM_REFERENCE_COUNT = 30;
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DOMSignedInfo.class);
    private static final String ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5 = "http://www.w3.org/2001/04/xmldsig-more#rsa-md5";
    private static final String ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5 = "http://www.w3.org/2001/04/xmldsig-more#hmac-md5";
    private final List<Reference> references;
    private final CanonicalizationMethod canonicalizationMethod;
    private final SignatureMethod signatureMethod;
    private String id;
    private Document ownerDoc;
    private Element localSiElem;
    private InputStream canonData;

    public DOMSignedInfo(CanonicalizationMethod cm, SignatureMethod sm, List<? extends Reference> references) {
        if (cm == null || sm == null || references == null) {
            throw new NullPointerException();
        }
        this.canonicalizationMethod = cm;
        this.signatureMethod = sm;
        this.references = Collections.unmodifiableList(new ArrayList(references));
        if (this.references.isEmpty()) {
            throw new IllegalArgumentException("list of references must contain at least one entry");
        }
        for (Object obj : this.references) {
            if (!(obj instanceof Reference)) {
                throw new ClassCastException("list of references contains an illegal " + obj.getClass());
            }
        }
    }

    public DOMSignedInfo(CanonicalizationMethod cm, SignatureMethod sm, List<? extends Reference> references, String id) {
        this(cm, sm, references);
        this.id = id;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    public DOMSignedInfo(Element siElem, XMLCryptoContext context, Provider provider) throws MarshalException {
        this.localSiElem = siElem;
        this.ownerDoc = siElem.getOwnerDocument();
        this.id = DOMUtils.getAttributeValue(siElem, "Id");
        Element cmElem = DOMUtils.getFirstChildElement(siElem, Constants._TAG_CANONICALIZATIONMETHOD, "http://www.w3.org/2000/09/xmldsig#");
        this.canonicalizationMethod = new DOMCanonicalizationMethod(cmElem, context, provider);
        Element smElem = DOMUtils.getNextSiblingElement(cmElem, "SignatureMethod", "http://www.w3.org/2000/09/xmldsig#");
        this.signatureMethod = DOMSignatureMethod.unmarshal(smElem);
        boolean secVal = Utils.secureValidation(context);
        String signatureMethodAlgorithm = this.signatureMethod.getAlgorithm();
        if (secVal && ("http://www.w3.org/2001/04/xmldsig-more#hmac-md5".equals(signatureMethodAlgorithm) || "http://www.w3.org/2001/04/xmldsig-more#rsa-md5".equals(signatureMethodAlgorithm))) {
            throw new MarshalException("It is forbidden to use algorithm " + this.signatureMethod + " when secure validation is enabled");
        }
        ArrayList<Reference> refList = new ArrayList<>(5);
        Element refElem = DOMUtils.getNextSiblingElement(smElem, "Reference", "http://www.w3.org/2000/09/xmldsig#");
        refList.add(new DOMReference(refElem, context, provider));
        Element nextSiblingElement = DOMUtils.getNextSiblingElement(refElem);
        while (true) {
            Element refElem2 = nextSiblingElement;
            if (refElem2 != null) {
                String name = refElem2.getLocalName();
                String namespace = refElem2.getNamespaceURI();
                if (!"Reference".equals(name) || !"http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                    throw new MarshalException("Invalid element name: " + namespace + ":" + name + ", expected Reference");
                }
                refList.add(new DOMReference(refElem2, context, provider));
                if (secVal && refList.size() > 30) {
                    throw new MarshalException("A maximum of 30 references per Manifest are allowed with secure validation");
                }
                nextSiblingElement = DOMUtils.getNextSiblingElement(refElem2);
            } else {
                this.references = Collections.unmodifiableList(refList);
                return;
            }
        }
    }

    public CanonicalizationMethod getCanonicalizationMethod() {
        return this.canonicalizationMethod;
    }

    public SignatureMethod getSignatureMethod() {
        return this.signatureMethod;
    }

    public String getId() {
        return this.id;
    }

    public List<Reference> getReferences() {
        return this.references;
    }

    public InputStream getCanonicalizedData() {
        return this.canonData;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    public void canonicalize(XMLCryptoContext context, ByteArrayOutputStream bos) throws XMLSignatureException {
        if (context == null) {
            throw new NullPointerException("context cannot be null");
        }
        Data dOMSubTreeData = new DOMSubTreeData(this.localSiElem, true);
        try {
            OutputStream os = new UnsyncBufferedOutputStream(bos);
            Throwable th = null;
            try {
                try {
                    ((DOMCanonicalizationMethod) this.canonicalizationMethod).canonicalize(dOMSubTreeData, context, os);
                    os.flush();
                    byte[] signedInfoBytes = bos.toByteArray();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Canonicalized SignedInfo:");
                        StringBuilder sb = new StringBuilder(signedInfoBytes.length);
                        for (byte signedInfoByte : signedInfoBytes) {
                            sb.append((char) signedInfoByte);
                        }
                        LOG.debug(sb.toString());
                        LOG.debug("Data to be signed/verified:" + XMLUtils.encodeToString(signedInfoBytes));
                    }
                    this.canonData = new ByteArrayInputStream(signedInfoBytes);
                    if (0 != 0) {
                        try {
                            os.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        os.close();
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (th != null) {
                    try {
                        os.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    os.close();
                }
                throw th4;
            }
        } catch (TransformException te) {
            throw new XMLSignatureException(te);
        } catch (IOException e) {
            LOG.debug(e.getMessage(), (Throwable) e);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.DOMStructure
    public void marshal(Node parent, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        this.ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element siElem = DOMUtils.createElement(this.ownerDoc, "SignedInfo", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        DOMCanonicalizationMethod dcm = (DOMCanonicalizationMethod) this.canonicalizationMethod;
        dcm.marshal(siElem, dsPrefix, context);
        this.signatureMethod.marshal(siElem, dsPrefix, context);
        for (Reference reference : this.references) {
            ((DOMReference) reference).marshal(siElem, dsPrefix, context);
        }
        DOMUtils.setAttributeID(siElem, "Id", this.id);
        parent.appendChild(siElem);
        this.localSiElem = siElem;
    }

    public boolean equals(Object o) {
        boolean zEquals;
        if (this == o) {
            return true;
        }
        if (!(o instanceof SignedInfo)) {
            return false;
        }
        SignedInfo osi = (SignedInfo) o;
        if (this.id == null) {
            zEquals = osi.getId() == null;
        } else {
            zEquals = this.id.equals(osi.getId());
        }
        boolean idEqual = zEquals;
        return this.canonicalizationMethod.equals(osi.getCanonicalizationMethod()) && this.signatureMethod.equals(osi.getSignatureMethod()) && this.references.equals(osi.getReferences()) && idEqual;
    }

    public static List<Reference> getSignedInfoReferences(SignedInfo si) {
        return si.getReferences();
    }

    public int hashCode() {
        int result = 17;
        if (this.id != null) {
            result = (31 * 17) + this.id.hashCode();
        }
        return (31 * ((31 * ((31 * result) + this.canonicalizationMethod.hashCode())) + this.signatureMethod.hashCode())) + this.references.hashCode();
    }
}
