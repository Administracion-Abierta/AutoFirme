package com.ctc.wstx.util;

import java.util.LinkedHashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/util/SimpleCache.class */
public final class SimpleCache<K, V> {
    final LimitMap<K, V> mItems;
    final int mMaxSize;

    public SimpleCache(int maxSize) {
        this.mItems = new LimitMap<>(maxSize);
        this.mMaxSize = maxSize;
    }

    public V find(K key) {
        return this.mItems.get(key);
    }

    public void add(K key, V value) {
        this.mItems.put(key, value);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/util/SimpleCache$LimitMap.class */
    static final class LimitMap<K, V> extends LinkedHashMap<K, V> {
        final int mMaxSize;

        public LimitMap(int size) {
            super(size, 0.8f, true);
            this.mMaxSize = size;
        }

        @Override // java.util.LinkedHashMap
        public boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() >= this.mMaxSize;
        }
    }
}
