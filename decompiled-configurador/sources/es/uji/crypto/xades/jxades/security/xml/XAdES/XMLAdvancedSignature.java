package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.uji.crypto.xades.jxades.security.xml.SignatureStatus;
import es.uji.crypto.xades.jxades.security.xml.WrappedKeyStorePlace;
import es.uji.crypto.xades.jxades.security.xml.XMLSignatureElement;
import es.uji.crypto.xades.jxades.security.xml.XmlWrappedKeyInfo;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/XMLAdvancedSignature.class */
public class XMLAdvancedSignature {
    public static final String XADES_v132 = "http://uri.etsi.org/01903/v1.3.2#";
    public static final String XADES_v141 = "http://uri.etsi.org/01903/v1.4.1#";
    public static final String ELEMENT_SIGNATURE = "Signature";
    public static final String ELEMENT_SIGNATURE_VALUE = "SignatureValue";
    protected BaseXAdESImpl xades;
    protected Element baseElement;
    protected XMLSignatureFactory xmlSignatureFactory;
    protected DigestMethod digestMethod;
    protected String xadesNamespace;
    protected String defaultXMLObjectId;
    protected String defaultXMLObjectMimeType;
    protected String defaultXMLObjectEncoding;
    protected XMLSignature signature;
    protected DOMSignContext signContext;
    private List<QualifyingPropertiesReference> qualifyingPropertiesReferences;
    public String signedPropertiesTypeUrl = "http://uri.etsi.org/01903#SignedProperties";
    protected XmlWrappedKeyInfo wrappedKeyInfo = XmlWrappedKeyInfo.CERTIFICATE;
    protected List<XMLObject> xmlObjects = new ArrayList();
    protected List<XMLStructure> defaultXMLObjectItems = new ArrayList();

    protected XMLAdvancedSignature(XAdESBase xades) {
        if (xades == null) {
            throw new IllegalArgumentException("XAdES parameter can not be NULL.");
        }
        this.baseElement = xades.getBaseElement();
        this.xades = (BaseXAdESImpl) xades;
    }

    public static XMLAdvancedSignature newInstance(XAdESBase xades) throws GeneralSecurityException {
        XMLAdvancedSignature result = new XMLAdvancedSignature(xades);
        result.setDigestMethod(xades.getDigestMethod());
        result.setXadesNamespace(xades.getXadesNamespace());
        return result;
    }

    public static XMLAdvancedSignature getInstance(XAdESBase xades) throws GeneralSecurityException {
        return newInstance(xades);
    }

    public Element getBaseElement() {
        return this.baseElement;
    }

    public Document getBaseDocument() {
        return this.xades.getBaseDocument();
    }

    public void setXadesNamespace(String xadesNamespace) {
        this.xadesNamespace = xadesNamespace;
    }

    public void setSignedPropertiesTypeUrl(String signedPropertiesTypeUrl) {
        this.signedPropertiesTypeUrl = signedPropertiesTypeUrl;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.TransformException */
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    public void sign(X509Certificate signingCert, PrivateKey privateKey, String signatureMethod, List refsIdList, String signatureIdPrefix) throws TransformException, GeneralSecurityException, MarshalException, ParserConfigurationException, SAXException, XMLSignatureException, IOException {
        sign(signingCert, privateKey, signatureMethod, refsIdList, signatureIdPrefix, null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    public void sign(X509Certificate certificate, PrivateKey privateKey, String signatureMethod, List refsIdList, String signatureIdPrefix, SigningCertificateInfo signingCertInfo) throws TransformException, GeneralSecurityException, MarshalException, ParserConfigurationException, SAXException, XMLSignatureException, IOException {
        List referencesIdList = new ArrayList(refsIdList);
        if (WrappedKeyStorePlace.SIGNING_CERTIFICATE_PROPERTY.equals(getWrappedKeyStorePlace())) {
            if (this.xades instanceof XadesWithBasicAttributes) {
                ((XadesWithBasicAttributes) this.xades).setSigningCertificate(certificate);
            } else if (this.xades instanceof XadesWithBaselineAttributes) {
                SigningCertificateV2Info certInfo = (SigningCertificateV2Info) signingCertInfo;
                ((XadesWithBaselineAttributes) this.xades).setSigningCertificateV2(certificate, certInfo);
            } else {
                throw new XMLSignatureException("Unsupported signature profile");
            }
        }
        XMLObject xadesObject = marshalXMLSignature(this.xadesNamespace, this.signedPropertiesTypeUrl, signatureIdPrefix, referencesIdList);
        addXMLObject(xadesObject);
        String signatureId = getSignatureId(signatureIdPrefix);
        String signatureValueId = getSignatureValueId(signatureIdPrefix);
        XMLSignatureFactory fac = getXMLSignatureFactory();
        CanonicalizationMethod cm = fac.newCanonicalizationMethod("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (C14NMethodParameterSpec) null);
        List<Reference> documentReferences = getReferences(referencesIdList);
        String keyInfoId = getKeyInfoId(signatureIdPrefix);
        documentReferences.add(fac.newReference("#" + keyInfoId, getDigestMethod()));
        SignatureMethod sm = fac.newSignatureMethod(signatureMethod, (SignatureMethodParameterSpec) null);
        SignedInfo si = fac.newSignedInfo(cm, sm, documentReferences);
        this.signature = fac.newXMLSignature(si, newKeyInfo(certificate, keyInfoId), getXMLObjects(), signatureId, signatureValueId);
        if (this.baseElement != null) {
            this.signContext = new DOMSignContext(privateKey, getBaseElement());
        } else {
            this.signContext = new DOMSignContext(privateKey, getBaseDocument());
        }
        this.signContext.putNamespacePrefix("http://www.w3.org/2000/09/xmldsig#", this.xades.getXmlSignaturePrefix());
        this.signContext.putNamespacePrefix(this.xadesNamespace, this.xades.getXadesPrefix());
        this.signature.sign(this.signContext);
    }

    public List<SignatureStatus> validate() {
        List<XMLSignatureElement> signatureElements = getXMLSignatureElements();
        ArrayList<SignatureStatus> validateResult = new ArrayList<>(signatureElements.size());
        for (XMLSignatureElement signatureElement : signatureElements) {
            validateResult.add(signatureElement.validate());
        }
        return validateResult;
    }

    public WrappedKeyStorePlace getWrappedKeyStorePlace() {
        return WrappedKeyStorePlace.KEY_INFO;
    }

    public void setWrappedKeyStorePlace(WrappedKeyStorePlace wrappedKeyStorePlace) {
    }

    public XmlWrappedKeyInfo getXmlWrappedKeyInfo() {
        return this.wrappedKeyInfo;
    }

    public List<XMLObject> getXMLObjects() {
        return this.xmlObjects;
    }

    public void setXmlWrappedKeyInfo(XmlWrappedKeyInfo wrappedKeyInfo) {
        this.wrappedKeyInfo = wrappedKeyInfo;
    }

    protected List<XMLSignatureElement> getXMLSignatureElements() {
        NodeList nl;
        if (this.baseElement != null) {
            nl = getBaseElement().getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
        } else {
            nl = getBaseDocument().getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
        }
        int size = nl.getLength();
        ArrayList<XMLSignatureElement> signatureElements = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            signatureElements.add(new XMLSignatureElement((Element) nl.item(i)));
        }
        return signatureElements;
    }

    protected String getSignatureId(String idPrefix) {
        return idPrefix + "-Signature";
    }

    protected String getSignatureValueId(String idPrefix) {
        return idPrefix + "-SignatureValue";
    }

    protected String getKeyInfoId(String idPrefix) {
        return idPrefix + "-KeyInfo";
    }

    protected XMLSignatureFactory getXMLSignatureFactory() {
        if (this.xmlSignatureFactory == null) {
            this.xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");
        }
        return this.xmlSignatureFactory;
    }

    protected Reference getReference(String uri) throws GeneralSecurityException {
        return getReference(uri, null);
    }

    protected Reference getReference(String uri, String type) throws GeneralSecurityException {
        return getReference(uri, null, type, null);
    }

    protected Reference getReference(String uri, List<Transform> transforms, String type) throws GeneralSecurityException {
        return getReference(uri, transforms, type, null);
    }

    protected Reference getReference(String uri, List<Transform> transforms, String type, String referenceId) throws GeneralSecurityException {
        XMLSignatureFactory fac = getXMLSignatureFactory();
        DigestMethod dm = getDigestMethod();
        String uri2 = uri.trim();
        if (uri2.equals("")) {
            Transform envelopedTransform = fac.newTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature", (TransformParameterSpec) null);
            if (transforms != null) {
                transforms.add(envelopedTransform);
            } else {
                transforms = Collections.singletonList(envelopedTransform);
            }
        } else if (!uri2.startsWith("#") && !uri2.startsWith("http://") && !uri2.startsWith("https://")) {
            uri2 = "#" + uri2;
        }
        return fac.newReference(uri2, dm, transforms, type, referenceId);
    }

    protected List<Reference> getReferences(List<?> idList) throws GeneralSecurityException {
        ArrayList<Reference> references = new ArrayList<>(idList.size());
        for (Object id : idList) {
            if (id instanceof Reference) {
                references.add((Reference) id);
            } else {
                references.add(getReference((String) id));
            }
        }
        return references;
    }

    protected DigestMethod getDigestMethod() throws GeneralSecurityException {
        if (this.digestMethod == null) {
            this.digestMethod = getXMLSignatureFactory().newDigestMethod("http://www.w3.org/2000/09/xmldsig#sha1", (DigestMethodParameterSpec) null);
        }
        return this.digestMethod;
    }

    public void setDigestMethod(String method) throws GeneralSecurityException {
        this.digestMethod = getXMLSignatureFactory().newDigestMethod(method, (DigestMethodParameterSpec) null);
    }

    protected KeyInfo newKeyInfo(X509Certificate certificate, String keyInfoId) throws KeyException {
        KeyInfoFactory keyInfoFactory = getXMLSignatureFactory().getKeyInfoFactory();
        KeyValue keyValue = keyInfoFactory.newKeyValue(certificate.getPublicKey());
        List<Object> x509DataList = new ArrayList<>();
        if (!XmlWrappedKeyInfo.PUBLIC_KEY.equals(getXmlWrappedKeyInfo())) {
            x509DataList.add(certificate);
        }
        X509IssuerSerial x509IssuerSerial = keyInfoFactory.newX509IssuerSerial(certificate.getIssuerX500Principal().getName(), certificate.getSerialNumber());
        x509DataList.add(certificate.getSubjectX500Principal().getName("RFC1779"));
        x509DataList.add(x509IssuerSerial);
        X509Data x509Data = keyInfoFactory.newX509Data(x509DataList);
        List<XMLStructure> newList = new ArrayList<>();
        newList.add(keyValue);
        newList.add(x509Data);
        return keyInfoFactory.newKeyInfo(newList, keyInfoId);
    }

    protected XMLObject newXMLObject(List<XMLStructure> xmlObjects) {
        return newXMLObject(xmlObjects, getDefaultXMLObjectId());
    }

    protected XMLObject newXMLObject(List<XMLStructure> xmlObjects, String id) {
        return newXMLObject(xmlObjects, id, getDefaultXMLObjectMimeType());
    }

    protected XMLObject newXMLObject(List<XMLStructure> xmlObjects, String id, String mimeType) {
        return newXMLObject(xmlObjects, id, mimeType, getDefaultXMLObjectEncoding());
    }

    protected XMLObject newXMLObject(List<XMLStructure> xmlObjects, String id, String mimeType, String encoding) {
        XMLSignatureFactory fac = getXMLSignatureFactory();
        return fac.newXMLObject(xmlObjects, id, mimeType, encoding);
    }

    protected String getDefaultXMLObjectId() {
        return this.defaultXMLObjectId;
    }

    protected String getDefaultXMLObjectMimeType() {
        return this.defaultXMLObjectMimeType;
    }

    protected String getDefaultXMLObjectEncoding() {
        return this.defaultXMLObjectEncoding;
    }

    public XMLObject addXMLObject(XMLObject xmlObject) {
        this.xmlObjects.add(xmlObject);
        return xmlObject;
    }

    protected QualifyingProperties marshalQualifyingProperties(String xmlNamespace, String signedPropertiesTypeUrl, String signatureIdPrefix, List referencesIdList) throws GeneralSecurityException, MarshalException {
        return marshalQualifyingProperties(xmlNamespace, signedPropertiesTypeUrl, signatureIdPrefix, referencesIdList, null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    protected QualifyingProperties marshalQualifyingProperties(String xmlNamespace, String signedPropertiesTypeUrl, String signatureIdPrefix, List referencesIdList, List<Transform> transforms) throws GeneralSecurityException, MarshalException {
        QualifyingProperties qp = new QualifyingProperties(this.xades.getBaseDocument(), getBaseElement(), signatureIdPrefix, this.xades.getXadesPrefix(), xmlNamespace, this.xades.getXmlSignaturePrefix());
        this.xades.marshalQualifyingProperties(qp, signatureIdPrefix, referencesIdList);
        SignedProperties sp = qp.getSignedProperties();
        String spId = sp.getId();
        Reference spReference = getReference(spId, transforms, this.signedPropertiesTypeUrl);
        referencesIdList.add(spReference);
        return qp;
    }

    protected XMLObject marshalXMLSignature(String xadesNamespace, String signedPropertiesTypeUrl, String signatureIdPrefix, List referencesIdList) throws GeneralSecurityException, MarshalException {
        return marshalXMLSignature(xadesNamespace, signedPropertiesTypeUrl, signatureIdPrefix, referencesIdList, null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    protected XMLObject marshalXMLSignature(String xadesNamespace, String signedPropertiesTypeUrl, String signatureIdPrefix, List referencesIdList, List<Transform> SignedPropertiesTransforms) throws GeneralSecurityException, MarshalException {
        QualifyingProperties qp = marshalQualifyingProperties(xadesNamespace, signedPropertiesTypeUrl, signatureIdPrefix, referencesIdList, SignedPropertiesTransforms);
        List<QualifyingPropertiesReference> qpr = getQualifyingPropertiesReferences();
        ArrayList<XMLStructure> content = new ArrayList<>(qpr.size() + 1);
        content.add(qp);
        content.addAll(qpr);
        return newXMLObject(content);
    }

    public List<QualifyingPropertiesReference> getQualifyingPropertiesReferences() {
        if (this.qualifyingPropertiesReferences == null) {
            this.qualifyingPropertiesReferences = new ArrayList();
        }
        return this.qualifyingPropertiesReferences;
    }

    public void setQualifyingPropertiesReferences(List<QualifyingPropertiesReference> refs) {
        this.qualifyingPropertiesReferences = refs;
    }
}
