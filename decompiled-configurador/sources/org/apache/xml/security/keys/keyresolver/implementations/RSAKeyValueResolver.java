package org.apache.xml.security.keys.keyresolver.implementations;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import javax.crypto.SecretKey;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.keyvalues.RSAKeyValue;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/keys/keyresolver/implementations/RSAKeyValueResolver.class */
public class RSAKeyValueResolver extends KeyResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) RSAKeyValueResolver.class);

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        return XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_KEYVALUE) || XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_RSAKEYVALUE);
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected PublicKey engineResolvePublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        if (element == null) {
            return null;
        }
        LOG.debug("Can I resolve {}", element.getTagName());
        boolean isKeyValue = XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_KEYVALUE);
        Element rsaKeyElement = null;
        if (isKeyValue) {
            rsaKeyElement = XMLUtils.selectDsNode(element.getFirstChild(), Constants._TAG_RSAKEYVALUE, 0);
        } else if (XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_RSAKEYVALUE)) {
            rsaKeyElement = element;
        }
        if (rsaKeyElement == null) {
            return null;
        }
        try {
            RSAKeyValue rsaKeyValue = new RSAKeyValue(rsaKeyElement, baseURI);
            return rsaKeyValue.getPublicKey();
        } catch (XMLSecurityException ex) {
            LOG.debug("XMLSecurityException", (Throwable) ex);
            return null;
        }
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected X509Certificate engineResolveX509Certificate(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        return null;
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected SecretKey engineResolveSecretKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        return null;
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected PrivateKey engineResolvePrivateKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        return null;
    }
}
