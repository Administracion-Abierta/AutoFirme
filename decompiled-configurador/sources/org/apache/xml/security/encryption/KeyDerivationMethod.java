package org.apache.xml.security.encryption;

import org.apache.xml.security.encryption.keys.content.derivedKey.KDFParams;
import org.apache.xml.security.exceptions.XMLSecurityException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/KeyDerivationMethod.class */
public interface KeyDerivationMethod {
    String getAlgorithm();

    KDFParams getKDFParams() throws XMLSecurityException;
}
