package org.apache.xml.security.keys.storage;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.xml.security.keys.storage.implementations.KeyStoreResolver;
import org.apache.xml.security.keys.storage.implementations.SingleCertificateResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/keys/storage/StorageResolver.class */
public class StorageResolver {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) StorageResolver.class);
    private final List<StorageResolverSpi> storageResolvers = new ArrayList();

    public StorageResolver(StorageResolverSpi resolver) {
        add(resolver);
    }

    public StorageResolver(KeyStore keyStore) {
        add(keyStore);
    }

    public StorageResolver(X509Certificate x509certificate) {
        add(x509certificate);
    }

    public void add(StorageResolverSpi resolver) {
        this.storageResolvers.add(resolver);
    }

    public void add(KeyStore keyStore) {
        try {
            add(new KeyStoreResolver(keyStore));
        } catch (StorageResolverException ex) {
            LOG.error("Could not add KeyStore because of: ", (Throwable) ex);
        }
    }

    public void add(X509Certificate x509certificate) {
        add(new SingleCertificateResolver(x509certificate));
    }

    public Iterator<Certificate> getIterator() {
        return new StorageResolverIterator(this.storageResolvers.iterator());
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/keys/storage/StorageResolver$StorageResolverIterator.class */
    static class StorageResolverIterator implements Iterator<Certificate> {
        private final Iterator<StorageResolverSpi> resolvers;
        private Iterator<Certificate> currentResolver = findNextResolver();

        public StorageResolverIterator(Iterator<StorageResolverSpi> resolvers) {
            this.resolvers = resolvers;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            if (this.currentResolver == null) {
                return false;
            }
            if (this.currentResolver.hasNext()) {
                return true;
            }
            this.currentResolver = findNextResolver();
            return this.currentResolver != null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Certificate next() {
            if (hasNext()) {
                return this.currentResolver.next();
            }
            throw new NoSuchElementException();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException("Can't remove keys from KeyStore");
        }

        private Iterator<Certificate> findNextResolver() {
            while (this.resolvers.hasNext()) {
                StorageResolverSpi resolverSpi = this.resolvers.next();
                Iterator<Certificate> iter = resolverSpi.getIterator();
                if (iter.hasNext()) {
                    return iter;
                }
            }
            return null;
        }
    }
}
