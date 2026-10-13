package org.apache.xml.security.keys.storage;

import java.security.cert.Certificate;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/keys/storage/StorageResolverSpi.class */
public abstract class StorageResolverSpi {
    public abstract Iterator<Certificate> getIterator();
}
