package org.apache.xml.security.encryption.keys.content.derivedKey;

import org.apache.xml.security.encryption.params.KeyDerivationParameters;
import org.apache.xml.security.exceptions.XMLSecurityException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/keys/content/derivedKey/DerivationAlgorithm.class */
public interface DerivationAlgorithm<T extends KeyDerivationParameters> {
    byte[] deriveKey(byte[] bArr, T t) throws XMLSecurityException;
}
