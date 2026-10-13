package com.twelvemonkeys.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/IgnoreCaseMap.class */
public class IgnoreCaseMap<V> extends AbstractDecoratedMap<String, V> implements Serializable, Cloneable {
    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public /* bridge */ /* synthetic */ Set keySet() {
        return super.keySet();
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public /* bridge */ /* synthetic */ Set entrySet() {
        return super.entrySet();
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public /* bridge */ /* synthetic */ Collection values() {
        return super.values();
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public /* bridge */ /* synthetic */ boolean containsValue(Object obj) {
        return super.containsValue(obj);
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public /* bridge */ /* synthetic */ boolean isEmpty() {
        return super.isEmpty();
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public /* bridge */ /* synthetic */ void clear() {
        super.clear();
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public /* bridge */ /* synthetic */ int size() {
        return super.size();
    }

    public IgnoreCaseMap() {
    }

    public IgnoreCaseMap(Map<String, ? extends V> map) {
        super(map);
    }

    public IgnoreCaseMap(Map map, Map<String, ? extends V> map2) {
        super(map, map2);
    }

    /* JADX WARN: Type inference incomplete: some casts might be missing */
    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public V put(String str, V v) {
        CharSequence charSequence = (String) toUpper(str);
        return unwrap((Map.Entry) this.entries.put((K) charSequence, new AbstractDecoratedMap.BasicEntry(charSequence, v)));
    }

    private V unwrap(Map.Entry<String, V> entry) {
        if (entry != null) {
            return entry.getValue();
        }
        return null;
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public V get(Object obj) {
        return unwrap((Map.Entry) this.entries.get(toUpper(obj)));
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public V remove(Object obj) {
        return unwrap((Map.Entry) this.entries.remove(toUpper(obj)));
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object obj) {
        return this.entries.containsKey(toUpper(obj));
    }

    protected static Object toUpper(Object obj) {
        if (obj instanceof String) {
            return ((String) obj).toUpperCase();
        }
        return obj;
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap
    protected Iterator<Map.Entry<String, V>> newEntryIterator() {
        return this.entries.entrySet().iterator();
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap
    protected Iterator<String> newKeyIterator() {
        return this.entries.keySet().iterator();
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap
    protected Iterator<V> newValueIterator() {
        return this.entries.values().iterator();
    }
}
