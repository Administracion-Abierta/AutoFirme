package org.apache.pdfbox.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/util/SmallMap.class */
public class SmallMap<K, V> implements Map<K, V> {
    private Object[] mapArr;

    public SmallMap() {
    }

    public SmallMap(Map<? extends K, ? extends V> initMap) {
        putAll(initMap);
    }

    private int findKey(Object key) {
        if (isEmpty() || key == null) {
            return -1;
        }
        for (int aIdx = 0; aIdx < this.mapArr.length; aIdx += 2) {
            if (key.equals(this.mapArr[aIdx])) {
                return aIdx;
            }
        }
        return -1;
    }

    private int findValue(Object value) {
        if (isEmpty() || value == null) {
            return -1;
        }
        for (int aIdx = 1; aIdx < this.mapArr.length; aIdx += 2) {
            if (value.equals(this.mapArr[aIdx])) {
                return aIdx;
            }
        }
        return -1;
    }

    @Override // java.util.Map
    public int size() {
        if (this.mapArr == null) {
            return 0;
        }
        return this.mapArr.length >> 1;
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.mapArr == null || this.mapArr.length == 0;
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return findKey(key) >= 0;
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return findValue(value) >= 0;
    }

    @Override // java.util.Map
    public V get(Object obj) {
        int iFindKey = findKey(obj);
        if (iFindKey < 0) {
            return null;
        }
        return (V) this.mapArr[iFindKey + 1];
    }

    @Override // java.util.Map
    public V put(K k, V v) {
        if (k == null || v == null) {
            throw new NullPointerException("Key or value must not be null.");
        }
        if (this.mapArr == null) {
            this.mapArr = new Object[]{k, v};
            return null;
        }
        int iFindKey = findKey(k);
        if (iFindKey < 0) {
            int length = this.mapArr.length;
            Object[] objArr = new Object[length + 2];
            System.arraycopy(this.mapArr, 0, objArr, 0, length);
            objArr[length] = k;
            objArr[length + 1] = v;
            this.mapArr = objArr;
            return null;
        }
        V v2 = (V) this.mapArr[iFindKey + 1];
        this.mapArr[iFindKey + 1] = v;
        return v2;
    }

    @Override // java.util.Map
    public V remove(Object obj) {
        int iFindKey = findKey(obj);
        if (iFindKey < 0) {
            return null;
        }
        V v = (V) this.mapArr[iFindKey + 1];
        int length = this.mapArr.length;
        if (length == 2) {
            this.mapArr = null;
        } else {
            Object[] objArr = new Object[length - 2];
            System.arraycopy(this.mapArr, 0, objArr, 0, iFindKey);
            System.arraycopy(this.mapArr, iFindKey + 2, objArr, iFindKey, (length - iFindKey) - 2);
            this.mapArr = objArr;
        }
        return v;
    }

    @Override // java.util.Map
    public final void putAll(Map<? extends K, ? extends V> otherMap) {
        if (this.mapArr == null || this.mapArr.length == 0) {
            this.mapArr = new Object[otherMap.size() << 1];
            int aIdx = 0;
            for (Map.Entry<? extends K, ? extends V> entry : otherMap.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    throw new NullPointerException("Key or value must not be null.");
                }
                int i = aIdx;
                int aIdx2 = aIdx + 1;
                this.mapArr[i] = entry.getKey();
                aIdx = aIdx2 + 1;
                this.mapArr[aIdx2] = entry.getValue();
            }
            return;
        }
        int oldLen = this.mapArr.length;
        Object[] newMapArr = new Object[oldLen + (otherMap.size() << 1)];
        System.arraycopy(this.mapArr, 0, newMapArr, 0, oldLen);
        int newIdx = oldLen;
        for (Map.Entry<? extends K, ? extends V> entry2 : otherMap.entrySet()) {
            if (entry2.getKey() == null || entry2.getValue() == null) {
                throw new NullPointerException("Key or value must not be null.");
            }
            int existKeyIdx = findKey(entry2.getKey());
            if (existKeyIdx >= 0) {
                newMapArr[existKeyIdx + 1] = entry2.getValue();
            } else {
                int i2 = newIdx;
                int newIdx2 = newIdx + 1;
                newMapArr[i2] = entry2.getKey();
                newIdx = newIdx2 + 1;
                newMapArr[newIdx2] = entry2.getValue();
            }
        }
        if (newIdx < newMapArr.length) {
            Object[] reducedMapArr = new Object[newIdx];
            System.arraycopy(newMapArr, 0, reducedMapArr, 0, newIdx);
            newMapArr = reducedMapArr;
        }
        this.mapArr = newMapArr;
    }

    @Override // java.util.Map
    public void clear() {
        this.mapArr = null;
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        if (isEmpty()) {
            return Collections.emptySet();
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (int kIdx = 0; kIdx < this.mapArr.length; kIdx += 2) {
            linkedHashSet.add(this.mapArr[kIdx]);
        }
        return Collections.unmodifiableSet(linkedHashSet);
    }

    @Override // java.util.Map
    public Collection<V> values() {
        if (isEmpty()) {
            return Collections.emptySet();
        }
        ArrayList arrayList = new ArrayList(this.mapArr.length >> 1);
        for (int vIdx = 1; vIdx < this.mapArr.length; vIdx += 2) {
            arrayList.add(this.mapArr[vIdx]);
        }
        return Collections.unmodifiableList(arrayList);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/util/SmallMap$SmallMapEntry.class */
    private class SmallMapEntry implements Map.Entry<K, V> {
        private final int keyIdx;

        SmallMapEntry(int keyInMapIdx) {
            this.keyIdx = keyInMapIdx;
        }

        @Override // java.util.Map.Entry
        public K getKey() {
            return (K) SmallMap.this.mapArr[this.keyIdx];
        }

        @Override // java.util.Map.Entry
        public V getValue() {
            return (V) SmallMap.this.mapArr[this.keyIdx + 1];
        }

        @Override // java.util.Map.Entry
        public V setValue(V v) {
            if (v == null) {
                throw new NullPointerException("Key or value must not be null.");
            }
            V v2 = (V) getValue();
            SmallMap.this.mapArr[this.keyIdx + 1] = v;
            return v2;
        }

        @Override // java.util.Map.Entry
        public int hashCode() {
            return getKey().hashCode();
        }

        @Override // java.util.Map.Entry
        public boolean equals(Object obj) {
            if (!(obj instanceof SmallMapEntry)) {
                return false;
            }
            SmallMap<K, V>.SmallMapEntry other = (SmallMapEntry) obj;
            return getKey().equals(other.getKey()) && getValue().equals(other.getValue());
        }
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        if (isEmpty()) {
            return Collections.emptySet();
        }
        Set<Map.Entry<K, V>> entries = new LinkedHashSet<>();
        for (int kIdx = 0; kIdx < this.mapArr.length; kIdx += 2) {
            entries.add(new SmallMapEntry(kIdx));
        }
        return Collections.unmodifiableSet(entries);
    }
}
