package org.apache.xml.security.keys.keyresolver.implementations;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import javax.crypto.SecretKey;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/keyresolver/implementations/SingleKeyResolver.class */
public class SingleKeyResolver extends KeyResolverSpi {
    private final String keyName;
    private final PublicKey publicKey;
    private final PrivateKey privateKey;
    private final SecretKey secretKey;

    public SingleKeyResolver(String keyName, PublicKey publicKey) {
        this.keyName = keyName;
        this.publicKey = publicKey;
        this.privateKey = null;
        this.secretKey = null;
    }

    public SingleKeyResolver(String keyName, PrivateKey privateKey) {
        this.keyName = keyName;
        this.privateKey = privateKey;
        this.publicKey = null;
        this.secretKey = null;
    }

    public SingleKeyResolver(String keyName, SecretKey secretKey) {
        this.keyName = keyName;
        this.secretKey = secretKey;
        this.publicKey = null;
        this.privateKey = null;
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        return XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_KEYNAME);
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected PublicKey engineResolvePublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        if (this.publicKey != null) {
            String name = element.getFirstChild().getNodeValue();
            if (this.keyName.equals(name)) {
                return this.publicKey;
            }
            return null;
        }
        return null;
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected X509Certificate engineResolveX509Certificate(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        return null;
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected SecretKey engineResolveSecretKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        if (this.secretKey != null) {
            String name = element.getFirstChild().getNodeValue();
            if (this.keyName.equals(name)) {
                return this.secretKey;
            }
            return null;
        }
        return null;
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    public PrivateKey engineResolvePrivateKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        if (this.privateKey != null) {
            String name = element.getFirstChild().getNodeValue();
            if (this.keyName.equals(name)) {
                return this.privateKey;
            }
            return null;
        }
        return null;
    }
}
