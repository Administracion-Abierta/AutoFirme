package com.twelvemonkeys.util;

import com.aowagie.text.pdf.ColumnText;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/LRUMap.class */
public class LRUMap<K, V> extends LinkedMap<K, V> implements ExpiringMap<K, V> {
    private int maxSize;
    private float trimFactor;

    public LRUMap() {
        super((Map) null, true);
        this.maxSize = 1000;
        this.trimFactor = 0.01f;
    }

    public LRUMap(int i) {
        super((Map) null, true);
        this.maxSize = 1000;
        this.trimFactor = 0.01f;
        setMaxSize(i);
    }

    public LRUMap(Map<? extends K, ? extends V> map) {
        super((Map) map, true);
        this.maxSize = 1000;
        this.trimFactor = 0.01f;
    }

    public LRUMap(Map<? extends K, ? extends V> map, int i) {
        super((Map) map, true);
        this.maxSize = 1000;
        this.trimFactor = 0.01f;
        setMaxSize(i);
    }

    public LRUMap(Map<K, Map.Entry<K, V>> map, Map<? extends K, ? extends V> map2, int i) {
        super(map, map2, true);
        this.maxSize = 1000;
        this.trimFactor = 0.01f;
        setMaxSize(i);
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

    @Override // com.twelvemonkeys.util.LinkedMap
    protected boolean removeEldestEntry(Map.Entry entry) {
        if (size() >= this.maxSize) {
            removeLRU();
            return false;
        }
        return false;
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap
    protected Map.Entry<K, V> removeEntry(Map.Entry<K, V> entry) {
        Map.Entry<K, V> entryRemoveEntry = super.removeEntry(entry);
        processRemoved(entry);
        return entryRemoveEntry;
    }

    @Override // com.twelvemonkeys.util.ExpiringMap
    public void processRemoved(Map.Entry<K, V> entry) {
    }

    public void removeLRU() {
        int iMax = (int) Math.max(size() * this.trimFactor, 1.0f);
        while (true) {
            int i = iMax;
            iMax--;
            if (i > 0) {
                removeEntry(this.head.next);
            } else {
                return;
            }
        }
    }
}
