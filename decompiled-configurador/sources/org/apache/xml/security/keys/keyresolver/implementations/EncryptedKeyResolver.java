package org.apache.xml.security.keys.keyresolver.implementations;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.crypto.SecretKey;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/keys/keyresolver/implementations/EncryptedKeyResolver.class */
public class EncryptedKeyResolver extends KeyResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) RSAKeyValueResolver.class);
    private final Key kek;
    private final String algorithm;
    private final List<KeyResolverSpi> internalKeyResolvers;

    public EncryptedKeyResolver(String algorithm, List<KeyResolverSpi> internalKeyResolvers) {
        this(algorithm, null, internalKeyResolvers);
    }

    public EncryptedKeyResolver(String algorithm, Key kek, List<KeyResolverSpi> internalKeyResolvers) {
        this.algorithm = algorithm;
        this.kek = kek;
        if (internalKeyResolvers != null) {
            this.internalKeyResolvers = new ArrayList(internalKeyResolvers);
        } else {
            this.internalKeyResolvers = Collections.emptyList();
        }
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        return XMLUtils.elementIsInEncryptionSpace(element, EncryptionConstants._TAG_ENCRYPTEDKEY);
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected PublicKey engineResolvePublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        return null;
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected X509Certificate engineResolveX509Certificate(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        return null;
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected SecretKey engineResolveSecretKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        if (element == null) {
            return null;
        }
        LOG.debug("EncryptedKeyResolver - Can I resolve {}", element.getTagName());
        SecretKey key = null;
        LOG.debug("Passed an Encrypted Key");
        try {
            XMLCipher cipher = XMLCipher.getInstance();
            cipher.init(4, this.kek);
            int size = this.internalKeyResolvers.size();
            for (int i = 0; i < size; i++) {
                cipher.registerInternalKeyResolver(this.internalKeyResolvers.get(i));
            }
            EncryptedKey ek = cipher.loadEncryptedKey(element);
            key = (SecretKey) cipher.decryptKey(ek, this.algorithm);
        } catch (XMLEncryptionException e) {
            LOG.debug(e.getMessage(), (Throwable) e);
        }
        return key;
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected PrivateKey engineResolvePrivateKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        return null;
    }
}
