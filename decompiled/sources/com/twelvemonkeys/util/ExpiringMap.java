package com.twelvemonkeys.util;

import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/ExpiringMap.class */
public interface ExpiringMap<K, V> extends Map<K, V> {
    void processRemoved(Map.Entry<K, V> entry);
}
