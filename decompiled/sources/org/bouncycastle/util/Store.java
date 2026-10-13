package org.bouncycastle.util;

import java.util.Collection;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/Store.class */
public interface Store<T> {
    Collection<T> getMatches(Selector<T> selector) throws StoreException;
}
