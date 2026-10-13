package org.apache.oro.util;

/* JADX INFO: loaded from: autofirma.jar:org/apache/oro/util/CacheLRU.class */
public final class CacheLRU extends GenericCache {
    private static final long serialVersionUID = 4464724778400452634L;
    private int __head;
    private int __tail;
    private final int[] __next;
    private final int[] __prev;

    public CacheLRU(int capacity) {
        super(capacity);
        this.__head = 0;
        this.__tail = 0;
        this.__next = new int[this._cache.length];
        this.__prev = new int[this._cache.length];
        for (int i = 0; i < this.__next.length; i++) {
            this.__prev[i] = -1;
            this.__next[i] = -1;
        }
    }

    public CacheLRU() {
        this(20);
    }

    private void __moveToFront(int index) {
        if (this.__head != index) {
            int next = this.__next[index];
            int prev = this.__prev[index];
            this.__next[prev] = next;
            if (next >= 0) {
                this.__prev[next] = prev;
            } else {
                this.__tail = prev;
            }
            this.__prev[index] = -1;
            this.__next[index] = this.__head;
            this.__prev[this.__head] = index;
            this.__head = index;
        }
    }

    @Override // org.apache.oro.util.GenericCache, org.apache.oro.util.Cache
    public synchronized Object getElement(Object key) {
        Object obj = this._table.get(key);
        if (obj != null) {
            GenericCacheEntry entry = (GenericCacheEntry) obj;
            __moveToFront(entry._index);
            return entry._value;
        }
        return null;
    }

    @Override // org.apache.oro.util.GenericCache, org.apache.oro.util.Cache
    public final synchronized void addElement(Object key, Object value) {
        Object obj = this._table.get(key);
        if (obj != null) {
            GenericCacheEntry entry = (GenericCacheEntry) obj;
            entry._value = value;
            entry._key = key;
            __moveToFront(entry._index);
            return;
        }
        if (!isFull()) {
            if (this._numEntries > 0) {
                this.__prev[this._numEntries] = this.__tail;
                this.__next[this._numEntries] = -1;
                __moveToFront(this._numEntries);
            }
            this._numEntries++;
        } else {
            this._table.remove(this._cache[this.__tail]._key);
            __moveToFront(this.__tail);
        }
        this._cache[this.__head]._value = value;
        this._cache[this.__head]._key = key;
        this._table.put(key, this._cache[this.__head]);
    }
}
