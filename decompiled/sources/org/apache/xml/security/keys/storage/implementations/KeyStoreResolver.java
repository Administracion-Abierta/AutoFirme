package org.apache.xml.security.keys.storage.implementations;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.xml.security.keys.storage.StorageResolverException;
import org.apache.xml.security.keys.storage.StorageResolverSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/storage/implementations/KeyStoreResolver.class */
public class KeyStoreResolver extends StorageResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) KeyStoreResolver.class);
    private final KeyStore keyStore;

    public KeyStoreResolver(KeyStore keyStore) throws StorageResolverException {
        this.keyStore = keyStore;
        try {
            keyStore.aliases();
        } catch (KeyStoreException ex) {
            throw new StorageResolverException(ex);
        }
    }

    @Override // org.apache.xml.security.keys.storage.StorageResolverSpi
    public Iterator<Certificate> getIterator() {
        return new KeyStoreIterator(this.keyStore);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/storage/implementations/KeyStoreResolver$KeyStoreIterator.class */
    static class KeyStoreIterator implements Iterator<Certificate> {
        private final List<Certificate> certs;
        private int i;

        public KeyStoreIterator(KeyStore keyStore) {
            List<Certificate> tmpCerts = new ArrayList<>();
            try {
                Enumeration<String> aliases = keyStore.aliases();
                while (aliases.hasMoreElements()) {
                    String alias = aliases.nextElement();
                    Certificate cert = keyStore.getCertificate(alias);
                    if (cert != null) {
                        tmpCerts.add(cert);
                    }
                }
            } catch (KeyStoreException ex) {
                KeyStoreResolver.LOG.debug("Error reading certificates: {}", ex.getMessage());
            }
            this.certs = Collections.unmodifiableList(tmpCerts);
            this.i = 0;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.i < this.certs.size();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Certificate next() {
            if (hasNext()) {
                List<Certificate> list = this.certs;
                int i = this.i;
                this.i = i + 1;
                return list.get(i);
            }
            throw new NoSuchElementException();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException("Can't remove keys from KeyStore");
        }
    }
}
