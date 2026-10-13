package com.twelvemonkeys.util;

import com.twelvemonkeys.lang.DateUtil;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/TimeoutMap.class */
public class TimeoutMap<K, V> extends AbstractDecoratedMap<K, V> implements ExpiringMap<K, V>, Serializable, Cloneable {
    protected long expiryTime;
    private volatile long nextExpiryTime;

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public /* bridge */ /* synthetic */ boolean containsValue(Object obj) {
        return super.containsValue(obj);
    }

    public TimeoutMap() {
        this.expiryTime = DateUtil.MINUTE;
        this.nextExpiryTime = Long.MAX_VALUE;
    }

    public TimeoutMap(Map<? extends K, ? extends V> map) {
        super(map);
        this.expiryTime = DateUtil.MINUTE;
        this.nextExpiryTime = Long.MAX_VALUE;
    }

    public TimeoutMap(long j) {
        this();
        this.expiryTime = j;
    }

    public TimeoutMap(Map<K, Map.Entry<K, V>> map, Map<? extends K, ? extends V> map2, long j) {
        super(map, map2);
        this.expiryTime = DateUtil.MINUTE;
        this.nextExpiryTime = Long.MAX_VALUE;
        this.expiryTime = j;
    }

    public long getExpiryTime() {
        return this.expiryTime;
    }

    public void setExpiryTime(long j) {
        long j2 = this.expiryTime;
        this.expiryTime = j;
        if (this.expiryTime < j2) {
            this.nextExpiryTime = 0L;
            removeExpiredEntries();
        }
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public int size() {
        removeExpiredEntries();
        return this.entries.size();
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public boolean isEmpty() {
        return size() <= 0;
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object obj) {
        removeExpiredEntries();
        return this.entries.containsKey(obj);
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public V get(Object obj) {
        TimedEntry timedEntry = (TimedEntry) this.entries.get(obj);
        if (timedEntry == null) {
            return null;
        }
        if (timedEntry.isExpired()) {
            this.entries.remove(obj);
            processRemoved(timedEntry);
            return null;
        }
        return timedEntry.getValue();
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public V put(K k, V v) {
        V v2;
        TimedEntry timedEntry = (TimedEntry) this.entries.get(k);
        if (timedEntry == null) {
            v2 = null;
            this.entries.put(k, createEntry((Object) k, (Object) v));
        } else {
            v2 = timedEntry.mValue;
            timedEntry.setValue(v);
            timedEntry.recordAccess(this);
        }
        removeExpiredEntries();
        this.modCount++;
        return v2;
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public V remove(Object obj) {
        TimedEntry timedEntry = (TimedEntry) this.entries.remove(obj);
        if (timedEntry != null) {
            return timedEntry.getValue();
        }
        return null;
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public void clear() {
        this.entries.clear();
        init();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.twelvemonkeys.util.AbstractDecoratedMap
    public TimeoutMap<K, V>.TimedEntry createEntry(K k, V v) {
        return new TimedEntry(k, v);
    }

    protected void removeExpiredEntries() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis > this.nextExpiryTime) {
            removeExpiredEntriesSynced(jCurrentTimeMillis);
        }
    }

    private synchronized void removeExpiredEntriesSynced(long j) {
        if (j > this.nextExpiryTime) {
            long j2 = Long.MAX_VALUE;
            this.nextExpiryTime = Long.MAX_VALUE;
            EntryIterator entryIterator = new EntryIterator();
            while (entryIterator.hasNext()) {
                long jExpires = ((TimedEntry) entryIterator.next()).expires();
                if (jExpires < j2) {
                    j2 = jExpires;
                }
            }
            this.nextExpiryTime = j2;
        }
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public Collection<V> values() {
        removeExpiredEntries();
        return super.values();
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        removeExpiredEntries();
        return super.entrySet();
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public Set<K> keySet() {
        removeExpiredEntries();
        return super.keySet();
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap
    protected Iterator<K> newKeyIterator() {
        return new KeyIterator();
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap
    protected Iterator<V> newValueIterator() {
        return new ValueIterator();
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap
    protected Iterator<Map.Entry<K, V>> newEntryIterator() {
        return new EntryIterator();
    }

    @Override // com.twelvemonkeys.util.ExpiringMap
    public void processRemoved(Map.Entry entry) {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/TimeoutMap$TimeoutMapIterator.class */
    private abstract class TimeoutMapIterator<E> implements Iterator<E> {
        Iterator<Map.Entry<K, Map.Entry<K, V>>> mIterator;
        AbstractDecoratedMap.BasicEntry<K, V> mNext;
        long mNow;

        private TimeoutMapIterator() {
            this.mIterator = TimeoutMap.this.entries.entrySet().iterator();
            this.mNow = System.currentTimeMillis();
        }

        @Override // java.util.Iterator
        public void remove() {
            this.mNext = null;
            this.mIterator.remove();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            if (this.mNext != null) {
                return true;
            }
            while (this.mNext == null && this.mIterator.hasNext()) {
                TimedEntry timedEntry = (TimedEntry) this.mIterator.next().getValue();
                if (timedEntry.isExpiredBy(this.mNow)) {
                    this.mIterator.remove();
                    TimeoutMap.this.processRemoved(timedEntry);
                } else {
                    this.mNext = timedEntry;
                    return true;
                }
            }
            return false;
        }

        AbstractDecoratedMap.BasicEntry<K, V> nextEntry() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            AbstractDecoratedMap.BasicEntry<K, V> basicEntry = this.mNext;
            this.mNext = null;
            return basicEntry;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/TimeoutMap$KeyIterator.class */
    private class KeyIterator extends TimeoutMap<K, V>.TimeoutMapIterator<K> {
        private KeyIterator() {
            super();
        }

        @Override // java.util.Iterator
        public K next() {
            return nextEntry().mKey;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/TimeoutMap$ValueIterator.class */
    private class ValueIterator extends TimeoutMap<K, V>.TimeoutMapIterator<V> {
        private ValueIterator() {
            super();
        }

        @Override // java.util.Iterator
        public V next() {
            return nextEntry().mValue;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/TimeoutMap$EntryIterator.class */
    private class EntryIterator extends TimeoutMap<K, V>.TimeoutMapIterator<Map.Entry<K, V>> {
        private EntryIterator() {
            super();
        }

        @Override // java.util.Iterator
        public Map.Entry<K, V> next() {
            return nextEntry();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/TimeoutMap$TimedEntry.class */
    private class TimedEntry extends AbstractDecoratedMap.BasicEntry<K, V> {
        private long mTimestamp;

        TimedEntry(K k, V v) {
            super(k, v);
            updateTimestamp();
        }

        @Override // com.twelvemonkeys.util.AbstractDecoratedMap.BasicEntry, java.util.Map.Entry
        public V setValue(V v) {
            updateTimestamp();
            return (V) super.setValue(v);
        }

        private void updateTimestamp() {
            this.mTimestamp = System.currentTimeMillis();
            long jExpires = expires();
            if (jExpires < TimeoutMap.this.nextExpiryTime) {
                TimeoutMap.this.nextExpiryTime = jExpires;
            }
        }

        final boolean isExpired() {
            return isExpiredBy(System.currentTimeMillis());
        }

        final boolean isExpiredBy(long j) {
            return j > expires();
        }

        final long expires() {
            return this.mTimestamp + TimeoutMap.this.expiryTime;
        }
    }
}
