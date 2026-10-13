package org.spongycastle.util;

import java.util.Collection;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/Store.class */
public interface Store<T> {
    Collection<T> getMatches(Selector<T> selector) throws StoreException;
}
