package org.apache.xml.security.keys.keyresolver.implementations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.crypto.SecretKey;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.RetrievalMethod;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.keys.keyresolver.KeyResolver;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/keyresolver/implementations/RetrievalMethodResolver.class */
public class RetrievalMethodResolver extends KeyResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) RetrievalMethodResolver.class);

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        return XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_RETRIEVALMETHOD);
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected PublicKey engineResolvePublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        try {
            RetrievalMethod rm = new RetrievalMethod(element, baseURI);
            String type = rm.getType();
            XMLSignatureInput resource = resolveInput(rm, baseURI, secureValidation);
            if (RetrievalMethod.TYPE_RAWX509.equals(type)) {
                X509Certificate cert = getRawCertificate(resource);
                if (cert != null) {
                    return cert.getPublicKey();
                }
                return null;
            }
            Element e = obtainReferenceElement(resource, secureValidation);
            if (XMLUtils.elementIsInSignatureSpace(e, Constants._TAG_RETRIEVALMETHOD)) {
                if (secureValidation) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Error: It is forbidden to have one RetrievalMethod point to another with secure validation");
                        return null;
                    }
                    return null;
                }
                RetrievalMethod rm2 = new RetrievalMethod(e, baseURI);
                XMLSignatureInput resource2 = resolveInput(rm2, baseURI, secureValidation);
                Element e2 = obtainReferenceElement(resource2, secureValidation);
                if (e2 == element) {
                    LOG.debug("Error: Can't have RetrievalMethods pointing to each other");
                    return null;
                }
            }
            return resolveKey(e, baseURI, storage, secureValidation);
        } catch (IOException ex) {
            LOG.debug("IOException", (Throwable) ex);
            return null;
        } catch (CertificateException ex2) {
            LOG.debug("CertificateException", (Throwable) ex2);
            return null;
        } catch (XMLSecurityException ex3) {
            LOG.debug("XMLSecurityException", (Throwable) ex3);
            return null;
        }
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected X509Certificate engineResolveX509Certificate(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        try {
            RetrievalMethod rm = new RetrievalMethod(element, baseURI);
            String type = rm.getType();
            XMLSignatureInput resource = resolveInput(rm, baseURI, secureValidation);
            if (RetrievalMethod.TYPE_RAWX509.equals(type)) {
                return getRawCertificate(resource);
            }
            Element e = obtainReferenceElement(resource, secureValidation);
            if (XMLUtils.elementIsInSignatureSpace(e, Constants._TAG_RETRIEVALMETHOD)) {
                if (secureValidation) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Error: It is forbidden to have one RetrievalMethod point to another with secure validation");
                        return null;
                    }
                    return null;
                }
                RetrievalMethod rm2 = new RetrievalMethod(e, baseURI);
                XMLSignatureInput resource2 = resolveInput(rm2, baseURI, secureValidation);
                Element e2 = obtainReferenceElement(resource2, secureValidation);
                if (e2 == element) {
                    LOG.debug("Error: Can't have RetrievalMethods pointing to each other");
                    return null;
                }
            }
            return resolveCertificate(e, baseURI, storage, secureValidation);
        } catch (IOException ex) {
            LOG.debug("IOException", (Throwable) ex);
            return null;
        } catch (CertificateException ex2) {
            LOG.debug("CertificateException", (Throwable) ex2);
            return null;
        } catch (XMLSecurityException ex3) {
            LOG.debug("XMLSecurityException", (Throwable) ex3);
            return null;
        }
    }

    private static X509Certificate resolveCertificate(Element e, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        if (e != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Now we have a {" + e.getNamespaceURI() + "}" + e.getLocalName() + " Element");
            }
            return KeyResolver.getX509Certificate(e, baseURI, storage, secureValidation);
        }
        return null;
    }

    private static PublicKey resolveKey(Element e, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        if (e != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Now we have a {" + e.getNamespaceURI() + "}" + e.getLocalName() + " Element");
            }
            return KeyResolver.getPublicKey(e, baseURI, storage, secureValidation);
        }
        return null;
    }

    private static Element obtainReferenceElement(XMLSignatureInput resource, boolean secureValidation) throws CanonicalizationException, IOException, KeyResolverException, XMLParserException {
        Element e;
        if (resource.isElement()) {
            e = (Element) resource.getSubNode();
        } else if (resource.isNodeSet()) {
            e = getDocumentElement(resource.getNodeSet());
        } else {
            byte[] inputBytes = resource.getBytes();
            e = getDocFromBytes(inputBytes, secureValidation);
            LOG.debug("we have to parse {} bytes", Integer.valueOf(inputBytes.length));
        }
        return e;
    }

    private static X509Certificate getRawCertificate(XMLSignatureInput resource) throws CanonicalizationException, IOException, CertificateException {
        byte[] inputBytes = resource.getBytes();
        CertificateFactory certFact = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID);
        InputStream is = new ByteArrayInputStream(inputBytes);
        Throwable th = null;
        try {
            try {
                X509Certificate x509Certificate = (X509Certificate) certFact.generateCertificate(is);
                if (0 != 0) {
                    try {
                        is.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    is.close();
                }
                return x509Certificate;
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (th != null) {
                try {
                    is.close();
                } catch (Throwable th5) {
                    th.addSuppressed(th5);
                }
            } else {
                is.close();
            }
            throw th4;
        }
    }

    private static XMLSignatureInput resolveInput(RetrievalMethod rm, String baseURI, boolean secureValidation) throws XMLSecurityException {
        Attr uri = rm.getURIAttr();
        Transforms transforms = rm.getTransforms();
        ResourceResolverContext resContext = new ResourceResolverContext(uri, baseURI, secureValidation);
        if (resContext.isURISafeToResolve()) {
            XMLSignatureInput resource = ResourceResolver.resolve(resContext);
            if (transforms != null) {
                LOG.debug("We have Transforms");
                resource = transforms.performTransforms(resource);
            }
            return resource;
        }
        String uriToResolve = uri != null ? uri.getValue() : null;
        Object[] exArgs = new Object[2];
        exArgs[0] = uriToResolve != null ? uriToResolve : "null";
        exArgs[1] = baseURI;
        throw new ResourceResolverException("utils.resolver.noClass", exArgs, uriToResolve, baseURI);
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    public SecretKey engineResolveSecretKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        return null;
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected PrivateKey engineResolvePrivateKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        return null;
    }

    private static Element getDocumentElement(Set<Node> set) {
        Element e = null;
        for (Node currentNode : set) {
            if (currentNode != null && 1 == currentNode.getNodeType()) {
                e = (Element) currentNode;
                break;
            }
        }
        List<Node> parents = new ArrayList<>();
        while (e != null) {
            parents.add(e);
            Node n = e.getParentNode();
            if (n == null || 1 != n.getNodeType()) {
                break;
            }
            e = (Element) n;
        }
        ListIterator<Node> it2 = parents.listIterator(parents.size() - 1);
        while (it2.hasPrevious()) {
            Element ele = (Element) it2.previous();
            if (set.contains(ele)) {
                return ele;
            }
        }
        return null;
    }
}
