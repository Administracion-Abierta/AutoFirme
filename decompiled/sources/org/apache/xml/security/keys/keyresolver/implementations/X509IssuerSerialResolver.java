package org.apache.xml.security.keys.keyresolver.implementations;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import javax.crypto.SecretKey;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.content.x509.XMLX509IssuerSerial;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/keyresolver/implementations/X509IssuerSerialResolver.class */
public class X509IssuerSerialResolver extends KeyResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) X509IssuerSerialResolver.class);

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        if (XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_X509DATA)) {
            try {
                X509Data x509Data = new X509Data(element, baseURI);
                return x509Data.containsIssuerSerial();
            } catch (XMLSecurityException e) {
                return false;
            }
        }
        return false;
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
        try {
            X509Data x509data = new X509Data(element, baseURI);
            if (!x509data.containsIssuerSerial()) {
                return null;
            }
            try {
                if (storage == null) {
                    Object[] exArgs = {Constants._TAG_X509ISSUERSERIAL};
                    KeyResolverException ex = new KeyResolverException("KeyResolver.needStorageResolver", exArgs);
                    LOG.debug("", (Throwable) ex);
                    throw ex;
                }
                int noOfISS = x509data.lengthIssuerSerial();
                Iterator<Certificate> storageIterator = storage.getIterator();
                while (storageIterator.hasNext()) {
                    X509Certificate cert = (X509Certificate) storageIterator.next();
                    XMLX509IssuerSerial certSerial = new XMLX509IssuerSerial(element.getOwnerDocument(), cert);
                    LOG.debug("Found Certificate Issuer: {}", certSerial.getIssuerName());
                    LOG.debug("Found Certificate Serial: {}", certSerial.getSerialNumber().toString());
                    for (int i = 0; i < noOfISS; i++) {
                        XMLX509IssuerSerial xmliss = x509data.itemIssuerSerial(i);
                        LOG.debug("Found Element Issuer:     {}", xmliss.getIssuerName());
                        LOG.debug("Found Element Serial:     {}", xmliss.getSerialNumber().toString());
                        if (certSerial.equals(xmliss)) {
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
        } catch (XMLSecurityException e) {
            return null;
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
