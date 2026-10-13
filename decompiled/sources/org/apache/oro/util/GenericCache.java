package org.apache.oro.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:org/apache/oro/util/GenericCache.class */
public abstract class GenericCache implements Cache, Serializable {
    private static final long serialVersionUID = 6123461456560210078L;
    public static final int DEFAULT_CAPACITY = 20;
    int _numEntries = 0;
    GenericCacheEntry[] _cache;
    HashMap<Object, Object> _table;

    @Override // org.apache.oro.util.Cache
    public abstract void addElement(Object obj, Object obj2);

    GenericCache(int cap) {
        int capacity = cap;
        this._table = new HashMap<>(capacity);
        this._cache = new GenericCacheEntry[capacity];
        while (true) {
            capacity--;
            if (capacity >= 0) {
                this._cache[capacity] = new GenericCacheEntry(capacity);
            } else {
                return;
            }
        }
    }

    @Override // org.apache.oro.util.Cache
    public synchronized Object getElement(Object key) {
        Object obj = this._table.get(key);
        if (obj != null) {
            return ((GenericCacheEntry) obj)._value;
        }
        return null;
    }

    public final Iterator<Object> keys() {
        return this._table.keySet().iterator();
    }

    @Override // org.apache.oro.util.Cache
    public final int size() {
        return this._numEntries;
    }

    @Override // org.apache.oro.util.Cache
    public final int capacity() {
        return this._cache.length;
    }

    public final boolean isFull() {
        return this._numEntries >= this._cache.length;
    }
}
