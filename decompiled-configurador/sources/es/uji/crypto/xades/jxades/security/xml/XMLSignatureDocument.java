package es.uji.crypto.xades.jxades.security.xml;

import java.io.File;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignatureProperties;
import javax.xml.crypto.dsig.SignatureProperty;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XMLSignatureDocument.class */
public class XMLSignatureDocument {
    private Element baseElement;
    private XMLSignatureFactory xmlSignatureFactory;
    private DigestMethod digestMethod;
    private String defaultSignaturePropertiesId;
    private String defaultXMLObjectId;
    private String defaultXMLObjectMimeType;
    private String defaultXMLObjectEncoding;
    private XmlWrappedKeyInfo wrappedKeyInfo = XmlWrappedKeyInfo.CERTIFICATE;
    private List<SignatureProperties> listOfSignatureProperties = new ArrayList();
    private List<SignatureProperty> defaultSignatureProperties = new ArrayList();
    private List<XMLObject> xmlObjects = new ArrayList();
    private List<XMLStructure> defaultXMLObjectItems = new ArrayList();

    public XMLSignatureDocument(Element baseElement) {
        if (baseElement == null) {
            throw new IllegalArgumentException("Root/Base XML Element can not be NULL");
        }
        this.baseElement = baseElement;
    }

    public Element getBaseElement() {
        return this.baseElement;
    }

    protected XMLSignatureFactory getXMLSignatureFactory() {
        if (this.xmlSignatureFactory == null) {
            this.xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");
        }
        return this.xmlSignatureFactory;
    }

    public DigestMethod getDigestMethod() throws GeneralSecurityException {
        if (this.digestMethod == null) {
            this.digestMethod = getXMLSignatureFactory().newDigestMethod("http://www.w3.org/2000/09/xmldsig#sha1", (DigestMethodParameterSpec) null);
        }
        return this.digestMethod;
    }

    public void setDigestMethod(DigestMethod digestMethod) {
        this.digestMethod = digestMethod;
    }

    public List<XMLSignatureElement> getXMLSignatureElements() {
        NodeList nl = this.baseElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
        int size = nl.getLength();
        ArrayList<XMLSignatureElement> signatureElements = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            signatureElements.add(new XMLSignatureElement((Element) nl.item(i)));
        }
        return signatureElements;
    }

    public XmlWrappedKeyInfo getXmlWrappedKeyInfo() {
        return this.wrappedKeyInfo;
    }

    public void setXmlWrappedKeyInfo(XmlWrappedKeyInfo wrappedKeyInfo) {
        this.wrappedKeyInfo = wrappedKeyInfo;
    }

    public WrappedKeyStorePlace getWrappedKeyStorePlace() {
        return WrappedKeyStorePlace.KEY_INFO;
    }

    public void setWrappedKeyStorePlace(WrappedKeyStorePlace wrappedKeyStorePlace) {
    }

    protected Reference getReference(String uri) throws GeneralSecurityException {
        return getReference(uri, null);
    }

    protected Reference getReference(String uri, String type) throws GeneralSecurityException {
        return getReference(uri, null, type, null);
    }

    protected Reference getReference(String uri, List transforms, String type) throws GeneralSecurityException {
        return getReference(uri, transforms, type, null);
    }

    protected Reference getReference(String uri, List transforms, String type, String referenceId) throws GeneralSecurityException {
        XMLSignatureFactory fac = getXMLSignatureFactory();
        DigestMethod dm = getDigestMethod();
        String uri2 = uri.trim();
        if (!uri2.startsWith("#")) {
            uri2 = "#" + uri2;
        }
        return fac.newReference(uri2, dm, transforms, type, referenceId);
    }

    protected List<Reference> getReferences(List idList) throws GeneralSecurityException {
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

    public List<SignatureStatus> validate() {
        List<XMLSignatureElement> signatureElements = getXMLSignatureElements();
        ArrayList<SignatureStatus> validateResult = new ArrayList<>(signatureElements.size());
        for (XMLSignatureElement signatureElement : signatureElements) {
            validateResult.add(signatureElement.validate());
        }
        return validateResult;
    }

    public void sign(X509Certificate certificate, PrivateKey privateKey, String signatureMethod, List referencesIdList, String signatureIdPrefix, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) throws GeneralSecurityException, MarshalException, XMLSignatureException {
        String signatureId = getSignatureId(signatureIdPrefix);
        String signatureValueId = getSignatureValueId(signatureIdPrefix);
        XMLSignatureFactory fac = getXMLSignatureFactory();
        CanonicalizationMethod cm = fac.newCanonicalizationMethod("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments", (C14NMethodParameterSpec) null);
        List<Reference> documentReferences = getReferences(referencesIdList);
        String keyInfoId = getKeyInfoId(signatureIdPrefix);
        documentReferences.add(fac.newReference("#" + keyInfoId, getDigestMethod()));
        SignatureMethod sm = fac.newSignatureMethod(signatureMethod, (SignatureMethodParameterSpec) null);
        SignedInfo si = fac.newSignedInfo(cm, sm, documentReferences);
        XMLSignature signature = fac.newXMLSignature(si, newKeyInfo(certificate, keyInfoId), getXMLObjects(), signatureId, signatureValueId);
        DOMSignContext signContext = new DOMSignContext(privateKey, this.baseElement);
        signContext.putNamespacePrefix("http://www.w3.org/2000/09/xmldsig#", xmlSignaturePrefix);
        signContext.putNamespacePrefix(xadesNamespace, xadesPrefix);
        signature.sign(signContext);
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

    public KeyInfo newKeyInfo(X509Certificate certificate, String keyInfoId) throws KeyException {
        KeyInfoFactory kif = getXMLSignatureFactory().getKeyInfoFactory();
        if (XmlWrappedKeyInfo.PUBLIC_KEY.equals(getXmlWrappedKeyInfo())) {
            KeyValue kv = kif.newKeyValue(certificate.getPublicKey());
            return kif.newKeyInfo(Collections.singletonList(kv), keyInfoId);
        }
        X509Data certData = kif.newX509Data(Collections.singletonList(certificate));
        return kif.newKeyInfo(Collections.singletonList(certData), keyInfoId);
    }

    public List<SignatureProperties> getListOfSignatureProperties() {
        this.listOfSignatureProperties.add(getDefaultSignatureProperties());
        return this.listOfSignatureProperties;
    }

    public void setListOfSignatureProperties(List<SignatureProperties> listOfSignatureProperties) {
        this.listOfSignatureProperties = listOfSignatureProperties;
    }

    public void addSignatureProperty(SignatureProperty signatureProperty) {
        this.defaultSignatureProperties.add(signatureProperty);
    }

    public void addSignatureProperty(List<DOMStructure> content, String target, String id) {
        XMLSignatureFactory fac = getXMLSignatureFactory();
        addSignatureProperty(fac.newSignatureProperty(content, target, id));
    }

    public SignatureProperties getDefaultSignatureProperties() {
        XMLSignatureFactory fac = getXMLSignatureFactory();
        return fac.newSignatureProperties(this.defaultSignatureProperties, this.defaultSignaturePropertiesId);
    }

    public void setDefaultSignaturePropertiesId(String id) {
        this.defaultSignaturePropertiesId = id;
    }

    public String getDefaultSignaturePropertiesId() {
        return this.defaultSignaturePropertiesId;
    }

    public XMLObject newXMLObject(List<XMLStructure> xmlObjects) {
        return newXMLObject(xmlObjects, getDefaultXMLObjectId());
    }

    public XMLObject newXMLObject(List<XMLStructure> xmlObjects, String id) {
        return newXMLObject(xmlObjects, id, getDefaultXMLObjectMimeType());
    }

    public XMLObject newXMLObject(List<XMLStructure> xmlObjects, String id, String mimeType) {
        return newXMLObject(xmlObjects, id, mimeType, getDefaultXMLObjectEncoding());
    }

    public XMLObject newXMLObject(List<XMLStructure> xmlObjects, String id, String mimeType, String encoding) {
        XMLSignatureFactory fac = getXMLSignatureFactory();
        return fac.newXMLObject(xmlObjects, id, mimeType, encoding);
    }

    public XMLObject addXMLObject(List<XMLStructure> xmlObjects) {
        XMLSignatureFactory fac = getXMLSignatureFactory();
        return addXMLObject(fac.newXMLObject(xmlObjects, getDefaultXMLObjectId(), getDefaultXMLObjectMimeType(), getDefaultXMLObjectEncoding()));
    }

    public XMLObject addXMLObject(XMLObject xmlObject) {
        this.xmlObjects.add(xmlObject);
        return xmlObject;
    }

    public List<XMLObject> getXMLObjects() {
        return this.xmlObjects;
    }

    public void addXMLObjectItem(XMLStructure xmlObjectItem) {
        this.defaultXMLObjectItems.add(xmlObjectItem);
    }

    public XMLObject getDefaultXMLObject() {
        XMLSignatureFactory fac = getXMLSignatureFactory();
        return fac.newXMLObject(getXMLObjectItems(), getDefaultXMLObjectId(), getDefaultXMLObjectMimeType(), getDefaultXMLObjectEncoding());
    }

    public List<XMLStructure> getXMLObjectItems() {
        return this.defaultXMLObjectItems;
    }

    public void setDefaultXMLObjectId(String defaultXMLObjectId) {
        this.defaultXMLObjectId = defaultXMLObjectId;
    }

    public String getDefaultXMLObjectId() {
        return this.defaultXMLObjectId;
    }

    public void setDefaultXMLObjectMimeType(String defaultXMLObjectMimeType) {
        this.defaultXMLObjectMimeType = defaultXMLObjectMimeType;
    }

    public String getDefaultXMLObjectMimeType() {
        return this.defaultXMLObjectMimeType;
    }

    public void setDefaultXMLObjectEncoding(String defaultXMLObjectEncoding) {
        this.defaultXMLObjectEncoding = defaultXMLObjectEncoding;
    }

    public String getDefaultXMLObjectEncoding() {
        return this.defaultXMLObjectEncoding;
    }

    private static Document loadEncryptionDocument() throws Exception {
        File encryptionFile = new File("BluesRecorderPro.log.wse");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(encryptionFile);
        System.out.println("Encryption document loaded from " + encryptionFile.toString());
        return document;
    }

    public static void printDocument(Node node) throws TransformerException {
        OutputStream os = System.out;
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(node), new StreamResult(os));
    }
}
