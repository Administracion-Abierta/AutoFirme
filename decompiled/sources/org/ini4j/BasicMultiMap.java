package org.ini4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/BasicMultiMap.class */
public class BasicMultiMap<K, V> implements MultiMap<K, V>, Serializable {
    private static final long serialVersionUID = 4716749660560043989L;
    private final Map<K, List<V>> _impl;

    public BasicMultiMap() {
        this(new LinkedHashMap());
    }

    public BasicMultiMap(Map<K, List<V>> impl) {
        this._impl = impl;
    }

    @Override // org.ini4j.MultiMap
    public List<V> getAll(Object key) {
        return this._impl.get(key);
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this._impl.isEmpty();
    }

    @Override // org.ini4j.MultiMap
    public void add(K key, V value) {
        getList(key, true).add(value);
    }

    @Override // org.ini4j.MultiMap
    public void add(K key, V value, int index) {
        getList(key, true).add(index, value);
    }

    @Override // java.util.Map
    public void clear() {
        this._impl.clear();
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return this._impl.containsKey(key);
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        boolean ret = false;
        for (List<V> all : this._impl.values()) {
            if (all.contains(value)) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> ret = new HashSet<>();
        for (K key : keySet()) {
            ret.add(new ShadowEntry(key));
        }
        return ret;
    }

    @Override // java.util.Map
    public V get(Object key) {
        List<V> values = getList(key, false);
        if (values == null) {
            return null;
        }
        return values.get(values.size() - 1);
    }

    @Override // org.ini4j.MultiMap
    public V get(Object key, int index) {
        List<V> values = getList(key, false);
        if (values == null) {
            return null;
        }
        return values.get(index);
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        return this._impl.keySet();
    }

    @Override // org.ini4j.MultiMap
    public int length(Object key) {
        List<V> values = getList(key, false);
        if (values == null) {
            return 0;
        }
        return values.size();
    }

    @Override // java.util.Map
    public V put(K key, V value) {
        V ret = null;
        List<V> values = getList(key, true);
        if (values.isEmpty()) {
            values.add(value);
        } else {
            ret = values.set(values.size() - 1, value);
        }
        return ret;
    }

    @Override // org.ini4j.MultiMap
    public V put(K key, V value, int index) {
        return getList(key, false).set(index, value);
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends V> map) {
        if (map instanceof MultiMap) {
            MultiMap<K, V> mm = (MultiMap) map;
            for (K k : mm.keySet()) {
                putAll(k, mm.getAll(k));
            }
            return;
        }
        for (K key : map.keySet()) {
            put(key, map.get(key));
        }
    }

    @Override // org.ini4j.MultiMap
    public List<V> putAll(K key, List<V> values) {
        List<V> ret = this._impl.get(key);
        this._impl.put(key, new ArrayList(values));
        return ret;
    }

    @Override // java.util.Map
    public V remove(Object key) {
        List<V> prev = this._impl.remove(key);
        if (prev == null) {
            return null;
        }
        return prev.get(0);
    }

    @Override // org.ini4j.MultiMap
    public V remove(Object key, int index) {
        V ret = null;
        List<V> values = getList(key, false);
        if (values != null) {
            ret = values.remove(index);
            if (values.isEmpty()) {
                this._impl.remove(key);
            }
        }
        return ret;
    }

    @Override // java.util.Map
    public int size() {
        return this._impl.size();
    }

    public String toString() {
        return this._impl.toString();
    }

    @Override // java.util.Map
    public Collection<V> values() {
        List<V> all = new ArrayList<>(this._impl.size());
        for (List<V> values : this._impl.values()) {
            all.addAll(values);
        }
        return all;
    }

    private List<V> getList(Object key, boolean create) {
        List<V> values = this._impl.get(key);
        if (values == null && create) {
            values = new ArrayList();
            this._impl.put(key, values);
        }
        return values;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/ini4j/BasicMultiMap$ShadowEntry.class */
    class ShadowEntry implements Map.Entry<K, V> {
        private final K _key;

        ShadowEntry(K key) {
            this._key = key;
        }

        @Override // java.util.Map.Entry
        public K getKey() {
            return this._key;
        }

        @Override // java.util.Map.Entry
        public V getValue() {
            return (V) BasicMultiMap.this.get(this._key);
        }

        @Override // java.util.Map.Entry
        public V setValue(V v) {
            return (V) BasicMultiMap.this.put(this._key, v);
        }
    }
}
