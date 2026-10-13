package org.ini4j;

import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/MultiMap.class */
public interface MultiMap<K, V> extends Map<K, V> {
    List<V> getAll(Object obj);

    void add(K k, V v);

    void add(K k, V v, int i);

    V get(Object obj, int i);

    int length(Object obj);

    V put(K k, V v, int i);

    List<V> putAll(K k, List<V> list);

    V remove(Object obj, int i);
}
