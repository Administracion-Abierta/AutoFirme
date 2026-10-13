package com.twelvemonkeys.util;

import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/WeakWeakMap.class */
public class WeakWeakMap<K, V> extends WeakHashMap<K, V> {
    public WeakWeakMap() {
    }

    public WeakWeakMap(int i) {
        super(i);
    }

    public WeakWeakMap(int i, float f) {
        super(i, f);
    }

    public WeakWeakMap(Map<? extends K, ? extends V> map) {
        super(map);
    }

    @Override // java.util.WeakHashMap, java.util.AbstractMap, java.util.Map
    public V put(K k, V v) {
        return (V) super.put(k, new WeakReference(v));
    }

    @Override // java.util.WeakHashMap, java.util.AbstractMap, java.util.Map
    public V get(Object obj) {
        WeakReference weakReference = (WeakReference) super.get(obj);
        if (weakReference != null) {
            return (V) weakReference.get();
        }
        return null;
    }

    @Override // java.util.WeakHashMap, java.util.AbstractMap, java.util.Map
    public V remove(Object obj) {
        WeakReference weakReference = (WeakReference) super.remove(obj);
        if (weakReference != null) {
            return (V) weakReference.get();
        }
        return null;
    }

    @Override // java.util.WeakHashMap, java.util.AbstractMap, java.util.Map
    public boolean containsValue(Object obj) {
        for (V v : values()) {
            if (obj == v) {
                return true;
            }
            if (v != null && v.equals(obj)) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.WeakHashMap, java.util.AbstractMap, java.util.Map
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override // java.util.WeakHashMap, java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        return new AbstractSet<Map.Entry<K, V>>() { // from class: com.twelvemonkeys.util.WeakWeakMap.1

            /* JADX INFO: renamed from: com.twelvemonkeys.util.WeakWeakMap$1$1, reason: invalid class name and collision with other inner class name */
            /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/WeakWeakMap$1$1.class */
            class C00051 implements Iterator<Map.Entry<K, V>> {
                final Iterator<Map.Entry<K, WeakReference<V>>> iterator;

                C00051() {
                    this.iterator = WeakWeakMap.super.entrySet().iterator();
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.iterator.hasNext();
                }

                @Override // java.util.Iterator
                public Map.Entry<K, V> next() {
                    return new Map.Entry<K, V>() { // from class: com.twelvemonkeys.util.WeakWeakMap.1.1.1
                        final Map.Entry<K, WeakReference<V>> entry;

                        {
                            this.entry = C00051.this.iterator.next();
                        }

                        @Override // java.util.Map.Entry
                        public K getKey() {
                            return this.entry.getKey();
                        }

                        @Override // java.util.Map.Entry
                        public V getValue() {
                            return this.entry.getValue().get();
                        }

                        @Override // java.util.Map.Entry
                        public V setValue(V v) {
                            WeakReference<V> value = this.entry.setValue(new WeakReference<>(v));
                            if (value != null) {
                                return value.get();
                            }
                            return null;
                        }

                        @Override // java.util.Map.Entry
                        public boolean equals(Object obj) {
                            return this.entry.equals(obj);
                        }

                        @Override // java.util.Map.Entry
                        public int hashCode() {
                            return this.entry.hashCode();
                        }

                        public String toString() {
                            return this.entry.toString();
                        }
                    };
                }

                @Override // java.util.Iterator
                public void remove() {
                    this.iterator.remove();
                }
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
            public Iterator<Map.Entry<K, V>> iterator() {
                return new C00051();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public int size() {
                return WeakWeakMap.this.size();
            }
        };
    }

    @Override // java.util.WeakHashMap, java.util.AbstractMap, java.util.Map
    public Collection<V> values() {
        return new AbstractCollection<V>() { // from class: com.twelvemonkeys.util.WeakWeakMap.2
            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
            public Iterator<V> iterator() {
                return new Iterator<V>() { // from class: com.twelvemonkeys.util.WeakWeakMap.2.1
                    Iterator<WeakReference<V>> iterator;

                    {
                        this.iterator = WeakWeakMap.super.values().iterator();
                    }

                    @Override // java.util.Iterator
                    public boolean hasNext() {
                        return this.iterator.hasNext();
                    }

                    @Override // java.util.Iterator
                    public V next() {
                        return this.iterator.next().get();
                    }

                    @Override // java.util.Iterator
                    public void remove() {
                        this.iterator.remove();
                    }
                };
            }

            @Override // java.util.AbstractCollection, java.util.Collection
            public int size() {
                return WeakWeakMap.this.size();
            }
        };
    }
}
