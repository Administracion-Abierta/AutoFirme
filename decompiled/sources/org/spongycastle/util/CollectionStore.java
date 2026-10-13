package org.spongycastle.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/util/CollectionStore.class */
public class CollectionStore<T> implements Store<T>, Iterable<T> {
    private Collection<T> _local;

    public CollectionStore(Collection<T> collection) {
        this._local = new ArrayList(collection);
    }

    @Override // org.spongycastle.util.Store
    public Collection<T> getMatches(Selector<T> selector) {
        if (selector == null) {
            return new ArrayList(this._local);
        }
        List<T> col = new ArrayList<>();
        for (T obj : this._local) {
            if (selector.match(obj)) {
                col.add(obj);
            }
        }
        return col;
    }

    @Override // org.spongycastle.util.Iterable, java.lang.Iterable
    public Iterator<T> iterator() {
        return getMatches(null).iterator();
    }
}
