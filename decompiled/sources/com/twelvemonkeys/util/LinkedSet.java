package com.twelvemonkeys.util;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/LinkedSet.class */
public class LinkedSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, Serializable {
    private static final Object DUMMY = new Object();
    private final Map<E, Object> map;

    public LinkedSet() {
        this.map = new LinkedMap();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public LinkedSet(Collection<E> collection) {
        this();
        addAll(collection);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean addAll(Collection<? extends E> collection) {
        boolean z = false;
        Iterator<? extends E> it = collection.iterator();
        while (it.hasNext()) {
            if (add(it.next()) && !z) {
                z = true;
            }
        }
        return z;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean add(E e) {
        return this.map.put(e, DUMMY) == null;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public int size() {
        return this.map.size();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
    public Iterator<E> iterator() {
        return this.map.keySet().iterator();
    }
}
