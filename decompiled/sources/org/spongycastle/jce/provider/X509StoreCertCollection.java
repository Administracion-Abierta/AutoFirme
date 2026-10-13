package org.spongycastle.jce.provider;

import java.util.Collection;
import org.spongycastle.util.CollectionStore;
import org.spongycastle.util.Selector;
import org.spongycastle.x509.X509CollectionStoreParameters;
import org.spongycastle.x509.X509StoreParameters;
import org.spongycastle.x509.X509StoreSpi;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/provider/X509StoreCertCollection.class */
public class X509StoreCertCollection extends X509StoreSpi {
    private CollectionStore _store;

    @Override // org.spongycastle.x509.X509StoreSpi
    public void engineInit(X509StoreParameters params) {
        if (!(params instanceof X509CollectionStoreParameters)) {
            throw new IllegalArgumentException(params.toString());
        }
        this._store = new CollectionStore(((X509CollectionStoreParameters) params).getCollection());
    }

    @Override // org.spongycastle.x509.X509StoreSpi
    public Collection engineGetMatches(Selector selector) {
        return this._store.getMatches(selector);
    }
}
