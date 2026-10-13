package org.apache.xml.security.keys.storage.implementations;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.xml.security.keys.storage.StorageResolverSpi;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/keys/storage/implementations/SingleCertificateResolver.class */
public class SingleCertificateResolver extends StorageResolverSpi {
    private final X509Certificate certificate;

    public SingleCertificateResolver(X509Certificate x509cert) {
        this.certificate = x509cert;
    }

    @Override // org.apache.xml.security.keys.storage.StorageResolverSpi
    public Iterator<Certificate> getIterator() {
        return new InternalIterator(this.certificate);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/keys/storage/implementations/SingleCertificateResolver$InternalIterator.class */
    static class InternalIterator implements Iterator<Certificate> {
        private boolean alreadyReturned;
        private final X509Certificate certificate;

        public InternalIterator(X509Certificate x509cert) {
            this.certificate = x509cert;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return !this.alreadyReturned;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Certificate next() {
            if (this.alreadyReturned) {
                throw new NoSuchElementException();
            }
            this.alreadyReturned = true;
            return this.certificate;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException("Can't remove keys from KeyStore");
        }
    }
}
