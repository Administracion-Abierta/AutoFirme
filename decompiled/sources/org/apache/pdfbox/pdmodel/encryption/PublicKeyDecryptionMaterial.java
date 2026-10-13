package org.apache.pdfbox.pdmodel.encryption;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/encryption/PublicKeyDecryptionMaterial.class */
public class PublicKeyDecryptionMaterial extends DecryptionMaterial {
    private String password;
    private KeyStore keyStore;
    private String alias;

    public PublicKeyDecryptionMaterial(KeyStore keystore, String a, String pwd) {
        this.password = null;
        this.keyStore = null;
        this.alias = null;
        this.keyStore = keystore;
        this.alias = a;
        this.password = pwd;
    }

    public X509Certificate getCertificate() throws KeyStoreException {
        if (this.keyStore.size() == 1) {
            Enumeration<String> aliases = this.keyStore.aliases();
            String keyStoreAlias = aliases.nextElement();
            return (X509Certificate) this.keyStore.getCertificate(keyStoreAlias);
        }
        if (this.keyStore.containsAlias(this.alias)) {
            return (X509Certificate) this.keyStore.getCertificate(this.alias);
        }
        throw new KeyStoreException("the keystore does not contain the given alias");
    }

    public String getPassword() {
        return this.password;
    }

    public Key getPrivateKey() throws KeyStoreException {
        try {
            if (this.keyStore.size() == 1) {
                Enumeration<String> aliases = this.keyStore.aliases();
                String keyStoreAlias = aliases.nextElement();
                return this.keyStore.getKey(keyStoreAlias, this.password.toCharArray());
            }
            if (this.keyStore.containsAlias(this.alias)) {
                return this.keyStore.getKey(this.alias, this.password.toCharArray());
            }
            throw new KeyStoreException("the keystore does not contain the given alias");
        } catch (NoSuchAlgorithmException ex) {
            throw new KeyStoreException("the algorithm necessary to recover the key is not available", ex);
        } catch (UnrecoverableKeyException ex2) {
            throw new KeyStoreException("the private key is not recoverable", ex2);
        }
    }
}
