package org.apache.xml.security.keys.keyresolver.implementations;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import javax.crypto.SecretKey;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.keys.content.x509.XMLX509IssuerSerial;
import org.apache.xml.security.keys.content.x509.XMLX509SKI;
import org.apache.xml.security.keys.content.x509.XMLX509SubjectName;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/keyresolver/implementations/PrivateKeyResolver.class */
public class PrivateKeyResolver extends KeyResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) PrivateKeyResolver.class);
    private final KeyStore keyStore;
    private final char[] password;

    public PrivateKeyResolver(KeyStore keyStore, char[] password) {
        this.keyStore = keyStore;
        this.password = password;
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        return XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_X509DATA) || XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_KEYNAME);
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected PublicKey engineResolvePublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        return null;
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected X509Certificate engineResolveX509Certificate(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        return null;
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    protected SecretKey engineResolveSecretKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        return null;
    }

    @Override // org.apache.xml.security.keys.keyresolver.KeyResolverSpi
    public PrivateKey engineResolvePrivateKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        if (XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_X509DATA)) {
            PrivateKey privKey = resolveX509Data(element, baseURI);
            if (privKey != null) {
                return privKey;
            }
            return null;
        }
        if (XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_KEYNAME)) {
            LOG.debug("Can I resolve KeyName?");
            String keyName = element.getFirstChild().getNodeValue();
            try {
                Key key = this.keyStore.getKey(keyName, this.password);
                if (key instanceof PrivateKey) {
                    return (PrivateKey) key;
                }
                return null;
            } catch (Exception e) {
                LOG.debug("Cannot recover the key", (Throwable) e);
                return null;
            }
        }
        return null;
    }

    private PrivateKey resolveX509Data(Element element, String baseURI) {
        LOG.debug("Can I resolve X509Data?");
        try {
            X509Data x509Data = new X509Data(element, baseURI);
            int len = x509Data.lengthSKI();
            for (int i = 0; i < len; i++) {
                XMLX509SKI x509SKI = x509Data.itemSKI(i);
                PrivateKey privKey = resolveX509SKI(x509SKI);
                if (privKey != null) {
                    return privKey;
                }
            }
            int len2 = x509Data.lengthIssuerSerial();
            for (int i2 = 0; i2 < len2; i2++) {
                XMLX509IssuerSerial x509Serial = x509Data.itemIssuerSerial(i2);
                PrivateKey privKey2 = resolveX509IssuerSerial(x509Serial);
                if (privKey2 != null) {
                    return privKey2;
                }
            }
            int len3 = x509Data.lengthSubjectName();
            for (int i3 = 0; i3 < len3; i3++) {
                XMLX509SubjectName x509SubjectName = x509Data.itemSubjectName(i3);
                PrivateKey privKey3 = resolveX509SubjectName(x509SubjectName);
                if (privKey3 != null) {
                    return privKey3;
                }
            }
            int len4 = x509Data.lengthCertificate();
            for (int i4 = 0; i4 < len4; i4++) {
                XMLX509Certificate x509Cert = x509Data.itemCertificate(i4);
                PrivateKey privKey4 = resolveX509Certificate(x509Cert);
                if (privKey4 != null) {
                    return privKey4;
                }
            }
            return null;
        } catch (KeyStoreException e) {
            LOG.debug("KeyStoreException", (Throwable) e);
            return null;
        } catch (XMLSecurityException e2) {
            LOG.debug("XMLSecurityException", (Throwable) e2);
            return null;
        }
    }

    private PrivateKey resolveX509SKI(XMLX509SKI x509SKI) throws XMLSecurityException, KeyStoreException {
        LOG.debug("Can I resolve X509SKI?");
        Enumeration<String> aliases = this.keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (this.keyStore.isKeyEntry(alias)) {
                Certificate cert = this.keyStore.getCertificate(alias);
                if (cert instanceof X509Certificate) {
                    XMLX509SKI certSKI = new XMLX509SKI(x509SKI.getDocument(), (X509Certificate) cert);
                    if (certSKI.equals(x509SKI)) {
                        LOG.debug("match !!! ");
                        try {
                            Key key = this.keyStore.getKey(alias, this.password);
                            if (key instanceof PrivateKey) {
                                return (PrivateKey) key;
                            }
                            continue;
                        } catch (Exception e) {
                            LOG.debug("Cannot recover the key", (Throwable) e);
                        }
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }
        }
        return null;
    }

    private PrivateKey resolveX509IssuerSerial(XMLX509IssuerSerial x509Serial) throws KeyStoreException {
        LOG.debug("Can I resolve X509IssuerSerial?");
        Enumeration<String> aliases = this.keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (this.keyStore.isKeyEntry(alias)) {
                Certificate cert = this.keyStore.getCertificate(alias);
                if (cert instanceof X509Certificate) {
                    XMLX509IssuerSerial certSerial = new XMLX509IssuerSerial(x509Serial.getDocument(), (X509Certificate) cert);
                    if (certSerial.equals(x509Serial)) {
                        LOG.debug("match !!! ");
                        try {
                            Key key = this.keyStore.getKey(alias, this.password);
                            if (key instanceof PrivateKey) {
                                return (PrivateKey) key;
                            }
                            continue;
                        } catch (Exception e) {
                            LOG.debug("Cannot recover the key", (Throwable) e);
                        }
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }
        }
        return null;
    }

    private PrivateKey resolveX509SubjectName(XMLX509SubjectName x509SubjectName) throws KeyStoreException {
        LOG.debug("Can I resolve X509SubjectName?");
        Enumeration<String> aliases = this.keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (this.keyStore.isKeyEntry(alias)) {
                Certificate cert = this.keyStore.getCertificate(alias);
                if (cert instanceof X509Certificate) {
                    XMLX509SubjectName certSN = new XMLX509SubjectName(x509SubjectName.getDocument(), (X509Certificate) cert);
                    if (certSN.equals(x509SubjectName)) {
                        LOG.debug("match !!! ");
                        try {
                            Key key = this.keyStore.getKey(alias, this.password);
                            if (key instanceof PrivateKey) {
                                return (PrivateKey) key;
                            }
                            continue;
                        } catch (Exception e) {
                            LOG.debug("Cannot recover the key", (Throwable) e);
                        }
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }
        }
        return null;
    }

    private PrivateKey resolveX509Certificate(XMLX509Certificate x509Cert) throws XMLSecurityException, KeyStoreException {
        LOG.debug("Can I resolve X509Certificate?");
        byte[] x509CertBytes = x509Cert.getCertificateBytes();
        Enumeration<String> aliases = this.keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (this.keyStore.isKeyEntry(alias)) {
                Certificate cert = this.keyStore.getCertificate(alias);
                if (cert instanceof X509Certificate) {
                    byte[] certBytes = null;
                    try {
                        certBytes = cert.getEncoded();
                    } catch (CertificateEncodingException e1) {
                        LOG.debug("Cannot recover the key", (Throwable) e1);
                    }
                    if (certBytes != null && Arrays.equals(certBytes, x509CertBytes)) {
                        LOG.debug("match !!! ");
                        try {
                            Key key = this.keyStore.getKey(alias, this.password);
                            if (key instanceof PrivateKey) {
                                return (PrivateKey) key;
                            }
                            continue;
                        } catch (Exception e) {
                            LOG.debug("Cannot recover the key", (Throwable) e);
                        }
                    }
                } else {
                    continue;
                }
            }
        }
        return null;
    }
}
