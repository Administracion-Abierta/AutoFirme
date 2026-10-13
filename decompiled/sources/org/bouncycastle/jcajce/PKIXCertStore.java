package org.bouncycastle.jcajce;

import java.security.cert.Certificate;
import java.util.Collection;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/PKIXCertStore.class */
public interface PKIXCertStore<T extends Certificate> extends Store<T> {
    @Override // org.bouncycastle.util.Store
    Collection<T> getMatches(Selector<T> selector) throws StoreException;
}
