package com.twelvemonkeys.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/NullMap.class */
public final class NullMap<K, V> implements Map<K, V>, Serializable {
    @Override // java.util.Map
    public final int size() {
        return 0;
    }

    @Override // java.util.Map
    public final void clear() {
    }

    @Override // java.util.Map
    public final boolean isEmpty() {
        return true;
    }

    @Override // java.util.Map
    public final boolean containsKey(Object obj) {
        return false;
    }

    @Override // java.util.Map
    public final boolean containsValue(Object obj) {
        return false;
    }

    @Override // java.util.Map
    public final Collection<V> values() {
        return Collections.emptyList();
    }

    @Override // java.util.Map
    public final void putAll(Map map) {
    }

    @Override // java.util.Map
    public final Set<Map.Entry<K, V>> entrySet() {
        return Collections.emptySet();
    }

    @Override // java.util.Map
    public final Set<K> keySet() {
        return Collections.emptySet();
    }

    @Override // java.util.Map
    public final V get(Object obj) {
        return null;
    }

    @Override // java.util.Map
    public final V remove(Object obj) {
        return null;
    }

    @Override // java.util.Map
    public final V put(Object obj, Object obj2) {
        return null;
    }

    @Override // java.util.Map
    public boolean equals(Object obj) {
        return (obj instanceof Map) && ((Map) obj).isEmpty();
    }

    @Override // java.util.Map
    public int hashCode() {
        return 0;
    }
}
