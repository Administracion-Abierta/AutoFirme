package org.apache.xml.security.keys;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.crypto.SecretKey;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.DEREncodedKeyValue;
import org.apache.xml.security.keys.content.KeyInfoReference;
import org.apache.xml.security.keys.content.KeyName;
import org.apache.xml.security.keys.content.KeyValue;
import org.apache.xml.security.keys.content.MgmtData;
import org.apache.xml.security.keys.content.PGPData;
import org.apache.xml.security.keys.content.RetrievalMethod;
import org.apache.xml.security.keys.content.SPKIData;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.content.keyvalues.DSAKeyValue;
import org.apache.xml.security.keys.content.keyvalues.RSAKeyValue;
import org.apache.xml.security.keys.keyresolver.KeyResolver;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/KeyInfo.class */
public class KeyInfo extends ElementProxy {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) KeyInfo.class);
    private List<X509Data> x509Datas;
    private List<EncryptedKey> encryptedKeys;
    private static final List<StorageResolver> nullList;
    private List<StorageResolver> storageResolvers;
    private List<KeyResolverSpi> internalKeyResolvers;
    private boolean secureValidation;

    static {
        List<StorageResolver> list = new ArrayList<>(1);
        list.add(null);
        nullList = Collections.unmodifiableList(list);
    }

    public KeyInfo(Document doc) {
        super(doc);
        this.storageResolvers = nullList;
        this.internalKeyResolvers = new ArrayList();
        this.secureValidation = true;
        addReturnToSelf();
        String prefix = ElementProxy.getDefaultPrefix(getBaseNamespace());
        if (prefix != null && prefix.length() > 0) {
            getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, getBaseNamespace());
        }
    }

    public KeyInfo(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
        this.storageResolvers = nullList;
        this.internalKeyResolvers = new ArrayList();
        this.secureValidation = true;
        Attr attr = element.getAttributeNodeNS(null, "Id");
        if (attr != null) {
            element.setIdAttributeNode(attr, true);
        }
    }

    public void setSecureValidation(boolean secureValidation) {
        this.secureValidation = secureValidation;
    }

    public void setId(String id) {
        if (id != null) {
            setLocalIdAttribute("Id", id);
        }
    }

    public String getId() {
        return getLocalAttribute("Id");
    }

    public void addKeyName(String keynameString) {
        add(new KeyName(getDocument(), keynameString));
    }

    public void add(KeyName keyname) {
        appendSelf(keyname);
        addReturnToSelf();
    }

    public void addKeyValue(PublicKey pk) {
        add(new KeyValue(getDocument(), pk));
    }

    public void addKeyValue(Element unknownKeyValueElement) {
        add(new KeyValue(getDocument(), unknownKeyValueElement));
    }

    public void add(DSAKeyValue dsakeyvalue) {
        add(new KeyValue(getDocument(), dsakeyvalue));
    }

    public void add(RSAKeyValue rsakeyvalue) {
        add(new KeyValue(getDocument(), rsakeyvalue));
    }

    public void add(PublicKey pk) {
        if (KeyValue.isSupportedKeyType(pk)) {
            add(new KeyValue(getDocument(), pk));
            return;
        }
        try {
            add(new DEREncodedKeyValue(getDocument(), pk));
        } catch (XMLSecurityException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public void add(KeyValue keyvalue) {
        appendSelf(keyvalue);
        addReturnToSelf();
    }

    public void addMgmtData(String mgmtdata) {
        add(new MgmtData(getDocument(), mgmtdata));
    }

    public void add(MgmtData mgmtdata) {
        appendSelf(mgmtdata);
        addReturnToSelf();
    }

    public void add(PGPData pgpdata) {
        appendSelf(pgpdata);
        addReturnToSelf();
    }

    public void addRetrievalMethod(String uri, Transforms transforms, String Type) {
        add(new RetrievalMethod(getDocument(), uri, transforms, Type));
    }

    public void add(RetrievalMethod retrievalmethod) {
        appendSelf(retrievalmethod);
        addReturnToSelf();
    }

    public void add(SPKIData spkidata) {
        appendSelf(spkidata);
        addReturnToSelf();
    }

    public void add(X509Data x509data) {
        if (this.x509Datas == null) {
            this.x509Datas = new ArrayList();
        }
        this.x509Datas.add(x509data);
        appendSelf(x509data);
        addReturnToSelf();
    }

    public void add(EncryptedKey encryptedKey) throws XMLEncryptionException {
        if (this.encryptedKeys == null) {
            this.encryptedKeys = new ArrayList();
        }
        this.encryptedKeys.add(encryptedKey);
        XMLCipher cipher = XMLCipher.getInstance();
        appendSelf(cipher.martial(encryptedKey));
    }

    public void addDEREncodedKeyValue(PublicKey pk) throws XMLSecurityException {
        add(new DEREncodedKeyValue(getDocument(), pk));
    }

    public void add(DEREncodedKeyValue derEncodedKeyValue) {
        appendSelf(derEncodedKeyValue);
        addReturnToSelf();
    }

    public void addKeyInfoReference(String URI) throws XMLSecurityException {
        add(new KeyInfoReference(getDocument(), URI));
    }

    public void add(KeyInfoReference keyInfoReference) {
        appendSelf(keyInfoReference);
        addReturnToSelf();
    }

    public void addUnknownElement(Element element) {
        appendSelf(element);
        addReturnToSelf();
    }

    public int lengthKeyName() {
        return length("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_KEYNAME);
    }

    public int lengthKeyValue() {
        return length("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_KEYVALUE);
    }

    public int lengthMgmtData() {
        return length("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_MGMTDATA);
    }

    public int lengthPGPData() {
        return length("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_PGPDATA);
    }

    public int lengthRetrievalMethod() {
        return length("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_RETRIEVALMETHOD);
    }

    public int lengthSPKIData() {
        return length("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_SPKIDATA);
    }

    public int lengthX509Data() {
        if (this.x509Datas != null) {
            return this.x509Datas.size();
        }
        return length("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_X509DATA);
    }

    public int lengthDEREncodedKeyValue() {
        return length("http://www.w3.org/2009/xmldsig11#", Constants._TAG_DERENCODEDKEYVALUE);
    }

    public int lengthKeyInfoReference() {
        return length("http://www.w3.org/2009/xmldsig11#", Constants._TAG_KEYINFOREFERENCE);
    }

    public int lengthUnknownElement() {
        int res = 0;
        Node firstChild = getElement().getFirstChild();
        while (true) {
            Node childNode = firstChild;
            if (childNode != null) {
                if (childNode.getNodeType() == 1 && childNode.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")) {
                    res++;
                }
                firstChild = childNode.getNextSibling();
            } else {
                return res;
            }
        }
    }

    public KeyName itemKeyName(int i) throws XMLSecurityException {
        Element e = XMLUtils.selectDsNode(getFirstChild(), Constants._TAG_KEYNAME, i);
        if (e != null) {
            return new KeyName(e, this.baseURI);
        }
        return null;
    }

    public KeyValue itemKeyValue(int i) throws XMLSecurityException {
        Element e = XMLUtils.selectDsNode(getFirstChild(), Constants._TAG_KEYVALUE, i);
        if (e != null) {
            return new KeyValue(e, this.baseURI);
        }
        return null;
    }

    public MgmtData itemMgmtData(int i) throws XMLSecurityException {
        Element e = XMLUtils.selectDsNode(getFirstChild(), Constants._TAG_MGMTDATA, i);
        if (e != null) {
            return new MgmtData(e, this.baseURI);
        }
        return null;
    }

    public PGPData itemPGPData(int i) throws XMLSecurityException {
        Element e = XMLUtils.selectDsNode(getFirstChild(), Constants._TAG_PGPDATA, i);
        if (e != null) {
            return new PGPData(e, this.baseURI);
        }
        return null;
    }

    public RetrievalMethod itemRetrievalMethod(int i) throws XMLSecurityException {
        Element e = XMLUtils.selectDsNode(getFirstChild(), Constants._TAG_RETRIEVALMETHOD, i);
        if (e != null) {
            return new RetrievalMethod(e, this.baseURI);
        }
        return null;
    }

    public SPKIData itemSPKIData(int i) throws XMLSecurityException {
        Element e = XMLUtils.selectDsNode(getFirstChild(), Constants._TAG_SPKIDATA, i);
        if (e != null) {
            return new SPKIData(e, this.baseURI);
        }
        return null;
    }

    public X509Data itemX509Data(int i) throws XMLSecurityException {
        if (this.x509Datas != null) {
            return this.x509Datas.get(i);
        }
        Element e = XMLUtils.selectDsNode(getFirstChild(), Constants._TAG_X509DATA, i);
        if (e != null) {
            return new X509Data(e, this.baseURI);
        }
        return null;
    }

    public EncryptedKey itemEncryptedKey(int i) throws XMLSecurityException {
        if (this.encryptedKeys != null) {
            return this.encryptedKeys.get(i);
        }
        Element e = XMLUtils.selectXencNode(getFirstChild(), EncryptionConstants._TAG_ENCRYPTEDKEY, i);
        if (e != null) {
            XMLCipher cipher = XMLCipher.getInstance();
            cipher.init(4, null);
            return cipher.loadEncryptedKey(e);
        }
        return null;
    }

    public DEREncodedKeyValue itemDEREncodedKeyValue(int i) throws XMLSecurityException {
        Element e = XMLUtils.selectDs11Node(getFirstChild(), Constants._TAG_DERENCODEDKEYVALUE, i);
        if (e != null) {
            return new DEREncodedKeyValue(e, this.baseURI);
        }
        return null;
    }

    public KeyInfoReference itemKeyInfoReference(int i) throws XMLSecurityException {
        Element e = XMLUtils.selectDs11Node(getFirstChild(), Constants._TAG_KEYINFOREFERENCE, i);
        if (e != null) {
            return new KeyInfoReference(e, this.baseURI);
        }
        return null;
    }

    public Element itemUnknownElement(int i) {
        int res = 0;
        Node firstChild = getElement().getFirstChild();
        while (true) {
            Node childNode = firstChild;
            if (childNode != null) {
                if (childNode.getNodeType() == 1 && childNode.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")) {
                    res++;
                    if (res == i) {
                        return (Element) childNode;
                    }
                }
                firstChild = childNode.getNextSibling();
            } else {
                return null;
            }
        }
    }

    public boolean isEmpty() {
        return getFirstChild() == null;
    }

    public boolean containsKeyName() {
        return lengthKeyName() > 0;
    }

    public boolean containsKeyValue() {
        return lengthKeyValue() > 0;
    }

    public boolean containsMgmtData() {
        return lengthMgmtData() > 0;
    }

    public boolean containsPGPData() {
        return lengthPGPData() > 0;
    }

    public boolean containsRetrievalMethod() {
        return lengthRetrievalMethod() > 0;
    }

    public boolean containsSPKIData() {
        return lengthSPKIData() > 0;
    }

    public boolean containsUnknownElement() {
        return lengthUnknownElement() > 0;
    }

    public boolean containsX509Data() {
        return lengthX509Data() > 0;
    }

    public boolean containsDEREncodedKeyValue() {
        return lengthDEREncodedKeyValue() > 0;
    }

    public boolean containsKeyInfoReference() {
        return lengthKeyInfoReference() > 0;
    }

    public PublicKey getPublicKey() throws KeyResolverException {
        PublicKey pk = getPublicKeyFromInternalResolvers();
        if (pk != null) {
            LOG.debug("I could find a key using the per-KeyInfo key resolvers");
            return pk;
        }
        LOG.debug("I couldn't find a key using the per-KeyInfo key resolvers");
        PublicKey pk2 = getPublicKeyFromStaticResolvers();
        if (pk2 != null) {
            LOG.debug("I could find a key using the system-wide key resolvers");
            return pk2;
        }
        LOG.debug("I couldn't find a key using the system-wide key resolvers");
        return null;
    }

    PublicKey getPublicKeyFromStaticResolvers() throws KeyResolverException {
        Iterator<KeyResolverSpi> it = KeyResolver.iterator();
        while (it.hasNext()) {
            KeyResolverSpi keyResolver = it.next();
            String uri = getBaseURI();
            for (Node currentChild = getFirstChild(); currentChild != null; currentChild = currentChild.getNextSibling()) {
                if (currentChild.getNodeType() == 1) {
                    for (StorageResolver storage : this.storageResolvers) {
                        PublicKey pk = keyResolver.engineLookupAndResolvePublicKey((Element) currentChild, uri, storage, this.secureValidation);
                        if (pk != null) {
                            return pk;
                        }
                    }
                }
            }
        }
        return null;
    }

    PublicKey getPublicKeyFromInternalResolvers() throws KeyResolverException {
        for (KeyResolverSpi keyResolver : this.internalKeyResolvers) {
            LOG.debug("Try {}", keyResolver.getClass().getName());
            String uri = getBaseURI();
            for (Node currentChild = getFirstChild(); currentChild != null; currentChild = currentChild.getNextSibling()) {
                if (currentChild.getNodeType() == 1) {
                    for (StorageResolver storage : this.storageResolvers) {
                        PublicKey pk = keyResolver.engineLookupAndResolvePublicKey((Element) currentChild, uri, storage, this.secureValidation);
                        if (pk != null) {
                            return pk;
                        }
                    }
                }
            }
        }
        return null;
    }

    public X509Certificate getX509Certificate() throws KeyResolverException {
        X509Certificate cert = getX509CertificateFromInternalResolvers();
        if (cert != null) {
            LOG.debug("I could find a X509Certificate using the per-KeyInfo key resolvers");
            return cert;
        }
        LOG.debug("I couldn't find a X509Certificate using the per-KeyInfo key resolvers");
        X509Certificate cert2 = getX509CertificateFromStaticResolvers();
        if (cert2 != null) {
            LOG.debug("I could find a X509Certificate using the system-wide key resolvers");
            return cert2;
        }
        LOG.debug("I couldn't find a X509Certificate using the system-wide key resolvers");
        return null;
    }

    X509Certificate getX509CertificateFromStaticResolvers() throws KeyResolverException {
        LOG.debug("Start getX509CertificateFromStaticResolvers() with {} resolvers", Integer.valueOf(KeyResolver.length()));
        String uri = getBaseURI();
        Iterator<KeyResolverSpi> it = KeyResolver.iterator();
        while (it.hasNext()) {
            KeyResolverSpi keyResolver = it.next();
            X509Certificate cert = applyCurrentResolver(uri, keyResolver);
            if (cert != null) {
                return cert;
            }
        }
        return null;
    }

    private X509Certificate applyCurrentResolver(String uri, KeyResolverSpi keyResolver) throws KeyResolverException {
        Node firstChild = getFirstChild();
        while (true) {
            Node currentChild = firstChild;
            if (currentChild != null) {
                if (currentChild.getNodeType() == 1) {
                    for (StorageResolver storage : this.storageResolvers) {
                        X509Certificate cert = keyResolver.engineLookupResolveX509Certificate((Element) currentChild, uri, storage, this.secureValidation);
                        if (cert != null) {
                            return cert;
                        }
                    }
                }
                firstChild = currentChild.getNextSibling();
            } else {
                return null;
            }
        }
    }

    X509Certificate getX509CertificateFromInternalResolvers() throws KeyResolverException {
        LOG.debug("Start getX509CertificateFromInternalResolvers() with {} resolvers", Integer.valueOf(lengthInternalKeyResolver()));
        String uri = getBaseURI();
        for (KeyResolverSpi keyResolver : this.internalKeyResolvers) {
            LOG.debug("Try {}", keyResolver.getClass().getName());
            X509Certificate cert = applyCurrentResolver(uri, keyResolver);
            if (cert != null) {
                return cert;
            }
        }
        return null;
    }

    public SecretKey getSecretKey() throws KeyResolverException {
        SecretKey sk = getSecretKeyFromInternalResolvers();
        if (sk != null) {
            LOG.debug("I could find a secret key using the per-KeyInfo key resolvers");
            return sk;
        }
        LOG.debug("I couldn't find a secret key using the per-KeyInfo key resolvers");
        SecretKey sk2 = getSecretKeyFromStaticResolvers();
        if (sk2 != null) {
            LOG.debug("I could find a secret key using the system-wide key resolvers");
            return sk2;
        }
        LOG.debug("I couldn't find a secret key using the system-wide key resolvers");
        return null;
    }

    SecretKey getSecretKeyFromStaticResolvers() throws KeyResolverException {
        Iterator<KeyResolverSpi> it = KeyResolver.iterator();
        while (it.hasNext()) {
            KeyResolverSpi keyResolver = it.next();
            String uri = getBaseURI();
            for (Node currentChild = getFirstChild(); currentChild != null; currentChild = currentChild.getNextSibling()) {
                if (currentChild.getNodeType() == 1) {
                    for (StorageResolver storage : this.storageResolvers) {
                        SecretKey sk = keyResolver.engineLookupAndResolveSecretKey((Element) currentChild, uri, storage, this.secureValidation);
                        if (sk != null) {
                            return sk;
                        }
                    }
                }
            }
        }
        return null;
    }

    SecretKey getSecretKeyFromInternalResolvers() throws KeyResolverException {
        for (KeyResolverSpi keyResolver : this.internalKeyResolvers) {
            LOG.debug("Try {}", keyResolver.getClass().getName());
            String uri = getBaseURI();
            for (Node currentChild = getFirstChild(); currentChild != null; currentChild = currentChild.getNextSibling()) {
                if (currentChild.getNodeType() == 1) {
                    for (StorageResolver storage : this.storageResolvers) {
                        SecretKey sk = keyResolver.engineLookupAndResolveSecretKey((Element) currentChild, uri, storage, this.secureValidation);
                        if (sk != null) {
                            return sk;
                        }
                    }
                }
            }
        }
        return null;
    }

    public PrivateKey getPrivateKey() throws KeyResolverException {
        PrivateKey pk = getPrivateKeyFromInternalResolvers();
        if (pk != null) {
            LOG.debug("I could find a private key using the per-KeyInfo key resolvers");
            return pk;
        }
        LOG.debug("I couldn't find a secret key using the per-KeyInfo key resolvers");
        PrivateKey pk2 = getPrivateKeyFromStaticResolvers();
        if (pk2 != null) {
            LOG.debug("I could find a private key using the system-wide key resolvers");
            return pk2;
        }
        LOG.debug("I couldn't find a private key using the system-wide key resolvers");
        return null;
    }

    PrivateKey getPrivateKeyFromStaticResolvers() throws KeyResolverException {
        PrivateKey pk;
        Iterator<KeyResolverSpi> it = KeyResolver.iterator();
        while (it.hasNext()) {
            KeyResolverSpi keyResolver = it.next();
            String uri = getBaseURI();
            for (Node currentChild = getFirstChild(); currentChild != null; currentChild = currentChild.getNextSibling()) {
                if (currentChild.getNodeType() == 1 && (pk = keyResolver.engineLookupAndResolvePrivateKey((Element) currentChild, uri, null, this.secureValidation)) != null) {
                    return pk;
                }
            }
        }
        return null;
    }

    PrivateKey getPrivateKeyFromInternalResolvers() throws KeyResolverException {
        PrivateKey pk;
        for (KeyResolverSpi keyResolver : this.internalKeyResolvers) {
            LOG.debug("Try {}", keyResolver.getClass().getName());
            String uri = getBaseURI();
            for (Node currentChild = getFirstChild(); currentChild != null; currentChild = currentChild.getNextSibling()) {
                if (currentChild.getNodeType() == 1 && (pk = keyResolver.engineLookupAndResolvePrivateKey((Element) currentChild, uri, null, this.secureValidation)) != null) {
                    return pk;
                }
            }
        }
        return null;
    }

    public void registerInternalKeyResolver(KeyResolverSpi realKeyResolver) {
        this.internalKeyResolvers.add(realKeyResolver);
    }

    int lengthInternalKeyResolver() {
        return this.internalKeyResolvers.size();
    }

    KeyResolverSpi itemInternalKeyResolver(int i) {
        return this.internalKeyResolvers.get(i);
    }

    public void addStorageResolver(StorageResolver storageResolver) {
        if (this.storageResolvers == nullList) {
            this.storageResolvers = new ArrayList();
        }
        this.storageResolvers.add(storageResolver);
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return Constants._TAG_KEYINFO;
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseNamespace() {
        return "http://www.w3.org/2000/09/xmldsig#";
    }
}
