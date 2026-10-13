package com.twelvemonkeys.util;

import com.aowagie.text.pdf.ColumnText;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/LRUHashMap.class */
public class LRUHashMap<K, V> extends LinkedHashMap<K, V> implements ExpiringMap<K, V> {
    private int maxSize;
    private float trimFactor;

    public LRUHashMap() {
        super(16, 0.75f, true);
        this.maxSize = 1000;
        this.trimFactor = 0.01f;
    }

    public LRUHashMap(int i) {
        super(16, 0.75f, true);
        this.maxSize = 1000;
        this.trimFactor = 0.01f;
        setMaxSize(i);
    }

    public LRUHashMap(Map<? extends K, ? extends V> map) {
        super(16, 0.75f, true);
        this.maxSize = 1000;
        this.trimFactor = 0.01f;
        putAll(map);
    }

    public LRUHashMap(Map<? extends K, ? extends V> map, int i) {
        super(16, 0.75f, true);
        this.maxSize = 1000;
        this.trimFactor = 0.01f;
        setMaxSize(i);
        putAll(map);
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public void setMaxSize(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("max size must be positive");
        }
        this.maxSize = i;
        while (size() > this.maxSize) {
            removeLRU();
        }
    }

    public float getTrimFactor() {
        return this.trimFactor;
    }

    public void setTrimFactor(float f) {
        if (f < ColumnText.GLOBAL_SPACE_CHAR_RATIO || f >= 1.0f) {
            throw new IllegalArgumentException("trim factor must be between 0 and 1");
        }
        this.trimFactor = f;
    }

    @Override // java.util.LinkedHashMap
    protected boolean removeEldestEntry(Map.Entry<K, V> entry) {
        if (size() >= this.maxSize) {
            removeLRU();
            return false;
        }
        return false;
    }

    @Override // com.twelvemonkeys.util.ExpiringMap
    public void processRemoved(Map.Entry<K, V> entry) {
    }

    public void removeLRU() {
        int iMax = (int) Math.max(size() * this.trimFactor, 1.0f);
        Iterator<Map.Entry<K, V>> it = entrySet().iterator();
        while (true) {
            int i = iMax;
            iMax--;
            if (i > 0 && it.hasNext()) {
                it.next();
                it.remove();
            } else {
                return;
            }
        }
    }
}
