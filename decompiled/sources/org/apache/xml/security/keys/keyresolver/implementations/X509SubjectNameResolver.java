package org.apache.xml.security.keys.keyresolver.implementations;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import javax.crypto.SecretKey;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.x509.XMLX509SubjectName;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/keyresolver/implementations/X509SubjectNameResolver.class */
public class X509SubjectNameResolver extends KeyResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) X509SubjectNameResolver.class);

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        Element[] x509childNodes;
        return XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_X509DATA) && (x509childNodes = XMLUtils.selectDsNodes(element.getFirstChild(), Constants._TAG_X509SUBJECTNAME)) != null && x509childNodes.length > 0;
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected PublicKey engineResolvePublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        X509Certificate cert = engineResolveX509Certificate(element, baseURI, storage, secureValidation);
        if (cert != null) {
            return cert.getPublicKey();
        }
        return null;
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected X509Certificate engineResolveX509Certificate(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        Element[] x509childNodes = XMLUtils.selectDsNodes(element.getFirstChild(), Constants._TAG_X509SUBJECTNAME);
        if (x509childNodes == null || x509childNodes.length <= 0) {
            return null;
        }
        try {
            if (storage == null) {
                Object[] exArgs = {Constants._TAG_X509SUBJECTNAME};
                KeyResolverException ex = new KeyResolverException("KeyResolver.needStorageResolver", exArgs);
                LOG.debug("", (Throwable) ex);
                throw ex;
            }
            XMLX509SubjectName[] x509childObject = new XMLX509SubjectName[x509childNodes.length];
            for (int i = 0; i < x509childNodes.length; i++) {
                x509childObject[i] = new XMLX509SubjectName(x509childNodes[i], baseURI);
            }
            Iterator<Certificate> storageIterator = storage.getIterator();
            while (storageIterator.hasNext()) {
                X509Certificate cert = (X509Certificate) storageIterator.next();
                XMLX509SubjectName certSN = new XMLX509SubjectName(element.getOwnerDocument(), cert);
                LOG.debug("Found Certificate SN: {}", certSN.getSubjectName());
                for (XMLX509SubjectName childSubject : x509childObject) {
                    LOG.debug("Found Element SN:     {}", childSubject.getSubjectName());
                    if (certSN.equals(childSubject)) {
                        LOG.debug("match !!! ");
                        return cert;
                    }
                    LOG.debug("no match...");
                }
            }
            return null;
        } catch (XMLSecurityException ex2) {
            LOG.debug("XMLSecurityException", (Throwable) ex2);
            throw new KeyResolverException(ex2);
        }
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
