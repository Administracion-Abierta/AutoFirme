package org.apache.xml.security.keys.keyresolver.implementations;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import javax.crypto.SecretKey;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.KeyInfoReference;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/keyresolver/implementations/KeyInfoReferenceResolver.class */
public class KeyInfoReferenceResolver extends KeyResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) KeyInfoReferenceResolver.class);

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        return XMLUtils.elementIsInSignature11Space(element, Constants._TAG_KEYINFOREFERENCE);
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected PublicKey engineResolvePublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        try {
            KeyInfo referent = resolveReferentKeyInfo(element, baseURI, storage, secureValidation);
            if (referent != null) {
                return referent.getPublicKey();
            }
            return null;
        } catch (XMLSecurityException e) {
            LOG.debug("XMLSecurityException", (Throwable) e);
            return null;
        }
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected X509Certificate engineResolveX509Certificate(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        try {
            KeyInfo referent = resolveReferentKeyInfo(element, baseURI, storage, secureValidation);
            if (referent != null) {
                return referent.getX509Certificate();
            }
            return null;
        } catch (XMLSecurityException e) {
            LOG.debug("XMLSecurityException", (Throwable) e);
            return null;
        }
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected SecretKey engineResolveSecretKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        try {
            KeyInfo referent = resolveReferentKeyInfo(element, baseURI, storage, secureValidation);
            if (referent != null) {
                return referent.getSecretKey();
            }
            return null;
        } catch (XMLSecurityException e) {
            LOG.debug("XMLSecurityException", (Throwable) e);
            return null;
        }
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    public PrivateKey engineResolvePrivateKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        try {
            KeyInfo referent = resolveReferentKeyInfo(element, baseURI, storage, secureValidation);
            if (referent != null) {
                return referent.getPrivateKey();
            }
            return null;
        } catch (XMLSecurityException e) {
            LOG.debug("XMLSecurityException", (Throwable) e);
            return null;
        }
    }

    private KeyInfo resolveReferentKeyInfo(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws XMLSecurityException {
        KeyInfoReference reference = new KeyInfoReference(element, baseURI);
        Attr uriAttr = reference.getURIAttr();
        XMLSignatureInput resource = resolveInput(uriAttr, baseURI, secureValidation);
        try {
            Element referentElement = obtainReferenceElement(resource, secureValidation);
            if (referentElement == null) {
                LOG.debug("De-reference of KeyInfoReference URI returned null: {}", uriAttr.getValue());
                return null;
            }
            validateReference(referentElement, secureValidation);
            KeyInfo referent = new KeyInfo(referentElement, baseURI);
            referent.setSecureValidation(secureValidation);
            referent.addStorageResolver(storage);
            return referent;
        } catch (Exception e) {
            LOG.debug("XMLSecurityException", (Throwable) e);
            return null;
        }
    }

    private void validateReference(Element referentElement, boolean secureValidation) throws XMLSecurityException {
        if (!XMLUtils.elementIsInSignatureSpace(referentElement, Constants._TAG_KEYINFO)) {
            Object[] exArgs = {new QName(referentElement.getNamespaceURI(), referentElement.getLocalName())};
            throw new XMLSecurityException("KeyInfoReferenceResolver.InvalidReferentElement.WrongType", exArgs);
        }
        KeyInfo referent = new KeyInfo(referentElement, "");
        if (referent.containsKeyInfoReference() || referent.containsRetrievalMethod()) {
            if (secureValidation) {
                throw new XMLSecurityException("KeyInfoReferenceResolver.InvalidReferentElement.ReferenceWithSecure");
            }
            throw new XMLSecurityException("KeyInfoReferenceResolver.InvalidReferentElement.ReferenceWithoutSecure");
        }
    }

    private XMLSignatureInput resolveInput(Attr uri, String baseURI, boolean secureValidation) throws XMLSecurityException {
        ResourceResolverContext resContext = new ResourceResolverContext(uri, baseURI, secureValidation);
        if (resContext.isURISafeToResolve()) {
            return ResourceResolver.resolve(resContext);
        }
        String uriToResolve = uri != null ? uri.getValue() : null;
        Object[] exArgs = new Object[2];
        exArgs[0] = uriToResolve != null ? uriToResolve : "null";
        exArgs[1] = baseURI;
        throw new ResourceResolverException("utils.resolver.noClass", exArgs, uriToResolve, baseURI);
    }

    private Element obtainReferenceElement(XMLSignatureInput resource, boolean secureValidation) throws CanonicalizationException, ParserConfigurationException, SAXException, IOException, KeyResolverException {
        Element e;
        if (resource.isElement()) {
            e = (Element) resource.getSubNode();
        } else {
            if (resource.isNodeSet()) {
                LOG.debug("De-reference of KeyInfoReference returned an unsupported NodeSet");
                return null;
            }
            byte[] inputBytes = resource.getBytes();
            e = getDocFromBytes(inputBytes, secureValidation);
        }
        return e;
    }
}
