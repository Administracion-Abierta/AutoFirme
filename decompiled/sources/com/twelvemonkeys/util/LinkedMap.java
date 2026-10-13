package com.twelvemonkeys.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.fontbox.ttf.HeaderTable;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/LinkedMap.class */
public class LinkedMap<K, V> extends AbstractDecoratedMap<K, V> implements Serializable {
    transient LinkedEntry<K, V> head;
    protected final boolean accessOrder;

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
    public /* bridge */ /* synthetic */ boolean containsKey(Object obj) {
        return super.containsKey(obj);
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

    public LinkedMap() {
        this((Map) null, false);
    }

    public LinkedMap(boolean z) {
        this((Map) null, z);
    }

    public LinkedMap(Map<? extends K, ? extends V> map) {
        this((Map) map, false);
    }

    public LinkedMap(Map<? extends K, ? extends V> map, boolean z) {
        super(map);
        this.accessOrder = z;
    }

    public LinkedMap(Map<K, Map.Entry<K, V>> map, Map<? extends K, ? extends V> map2) {
        this(map, map2, false);
    }

    public LinkedMap(Map<K, Map.Entry<K, V>> map, Map<? extends K, ? extends V> map2, boolean z) {
        super(map, map2);
        this.accessOrder = z;
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap
    protected void init() {
        this.head = new LinkedEntry<K, V>(null, null, null) { // from class: com.twelvemonkeys.util.LinkedMap.1
            @Override // com.twelvemonkeys.util.LinkedMap.LinkedEntry
            void addBefore(LinkedEntry linkedEntry) {
                throw new Error();
            }

            @Override // com.twelvemonkeys.util.LinkedMap.LinkedEntry
            void remove() {
                throw new Error();
            }

            @Override // com.twelvemonkeys.util.LinkedMap.LinkedEntry, com.twelvemonkeys.util.AbstractDecoratedMap.BasicEntry
            public void recordAccess(Map map) {
                throw new Error();
            }

            @Override // com.twelvemonkeys.util.LinkedMap.LinkedEntry, com.twelvemonkeys.util.AbstractDecoratedMap.BasicEntry
            public void recordRemoval(Map map) {
                throw new Error();
            }

            public void recordRemoval() {
                throw new Error();
            }

            @Override // com.twelvemonkeys.util.AbstractDecoratedMap.BasicEntry, java.util.Map.Entry
            public V getValue() {
                throw new Error();
            }

            @Override // com.twelvemonkeys.util.AbstractDecoratedMap.BasicEntry, java.util.Map.Entry
            public V setValue(V v) {
                throw new Error();
            }

            @Override // com.twelvemonkeys.util.AbstractDecoratedMap.BasicEntry, java.util.Map.Entry
            public K getKey() {
                throw new Error();
            }

            @Override // com.twelvemonkeys.util.AbstractDecoratedMap.BasicEntry
            public String toString() {
                return HeaderTable.TAG;
            }
        };
        LinkedEntry<K, V> linkedEntry = this.head;
        LinkedEntry<K, V> linkedEntry2 = this.head;
        LinkedEntry<K, V> linkedEntry3 = this.head;
        linkedEntry2.next = linkedEntry3;
        linkedEntry.previous = linkedEntry3;
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public boolean containsValue(Object obj) {
        if (obj == null) {
            LinkedEntry<K, V> linkedEntry = this.head.next;
            while (true) {
                LinkedEntry<K, V> linkedEntry2 = linkedEntry;
                if (linkedEntry2 != this.head) {
                    if (linkedEntry2.mValue != null) {
                        linkedEntry = linkedEntry2.next;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        } else {
            LinkedEntry<K, V> linkedEntry3 = this.head.next;
            while (true) {
                LinkedEntry<K, V> linkedEntry4 = linkedEntry3;
                if (linkedEntry4 != this.head) {
                    if (!obj.equals(linkedEntry4.mValue)) {
                        linkedEntry3 = linkedEntry4.next;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
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

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/LinkedMap$LinkedMapIterator.class */
    private abstract class LinkedMapIterator<E> implements Iterator<E> {
        LinkedEntry<K, V> mNextEntry;
        LinkedEntry<K, V> mLastReturned;
        int mExpectedModCount;

        private LinkedMapIterator() {
            this.mNextEntry = LinkedMap.this.head.next;
            this.mLastReturned = null;
            this.mExpectedModCount = LinkedMap.this.modCount;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.mNextEntry != LinkedMap.this.head;
        }

        @Override // java.util.Iterator
        public void remove() {
            if (this.mLastReturned == null) {
                throw new IllegalStateException();
            }
            if (LinkedMap.this.modCount != this.mExpectedModCount) {
                throw new ConcurrentModificationException();
            }
            LinkedMap.this.remove(this.mLastReturned.mKey);
            this.mLastReturned = null;
            this.mExpectedModCount = LinkedMap.this.modCount;
        }

        LinkedEntry<K, V> nextEntry() {
            if (LinkedMap.this.modCount != this.mExpectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (this.mNextEntry == LinkedMap.this.head) {
                throw new NoSuchElementException();
            }
            LinkedEntry<K, V> linkedEntry = this.mNextEntry;
            this.mLastReturned = linkedEntry;
            this.mNextEntry = linkedEntry.next;
            return linkedEntry;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/LinkedMap$KeyIterator.class */
    private class KeyIterator extends LinkedMap<K, V>.LinkedMapIterator<K> {
        private KeyIterator() {
            super();
        }

        @Override // java.util.Iterator
        public K next() {
            return nextEntry().mKey;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/LinkedMap$ValueIterator.class */
    private class ValueIterator extends LinkedMap<K, V>.LinkedMapIterator<V> {
        private ValueIterator() {
            super();
        }

        @Override // java.util.Iterator
        public V next() {
            return nextEntry().mValue;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/LinkedMap$EntryIterator.class */
    private class EntryIterator extends LinkedMap<K, V>.LinkedMapIterator<Map.Entry<K, V>> {
        private EntryIterator() {
            super();
        }

        @Override // java.util.Iterator
        public Map.Entry<K, V> next() {
            return nextEntry();
        }
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public V get(Object obj) {
        LinkedEntry linkedEntry = (LinkedEntry) this.entries.get(obj);
        if (linkedEntry != null) {
            linkedEntry.recordAccess(this);
            return linkedEntry.mValue;
        }
        return null;
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public V remove(Object obj) {
        LinkedEntry linkedEntry = (LinkedEntry) this.entries.remove(obj);
        if (linkedEntry != null) {
            linkedEntry.remove();
            this.modCount++;
            return linkedEntry.mValue;
        }
        return null;
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap, java.util.Map
    public V put(K k, V v) {
        V v2;
        LinkedEntry linkedEntry = (LinkedEntry) this.entries.get(k);
        if (linkedEntry == null) {
            v2 = null;
            LinkedEntry<K, V> linkedEntry2 = this.head.next;
            if (removeEldestEntry(linkedEntry2)) {
                removeEntry(linkedEntry2);
            }
            LinkedEntry<K, V> linkedEntryCreateEntry = createEntry((Object) k, (Object) v);
            linkedEntryCreateEntry.addBefore(this.head);
            this.entries.put(k, linkedEntryCreateEntry);
        } else {
            v2 = linkedEntry.mValue;
            linkedEntry.mValue = v;
            linkedEntry.recordAccess(this);
        }
        this.modCount++;
        return v2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.twelvemonkeys.util.AbstractDecoratedMap
    public LinkedEntry<K, V> createEntry(K k, V v) {
        return new LinkedEntry<>(k, v, null);
    }

    @Override // com.twelvemonkeys.util.AbstractDecoratedMap, java.util.AbstractMap
    public Object clone() throws CloneNotSupportedException {
        return (LinkedMap) super.clone();
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> entry) {
        return false;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/LinkedMap$LinkedEntry.class */
    protected static class LinkedEntry<K, V> extends AbstractDecoratedMap.BasicEntry<K, V> implements Serializable {
        LinkedEntry<K, V> previous;
        LinkedEntry<K, V> next;

        LinkedEntry(K k, V v, LinkedEntry<K, V> linkedEntry) {
            super(k, v);
            this.next = linkedEntry;
        }

        void addBefore(LinkedEntry<K, V> linkedEntry) {
            this.next = linkedEntry;
            this.previous = linkedEntry.previous;
            this.previous.next = this;
            this.next.previous = this;
        }

        void remove() {
            this.previous.next = this.next;
            this.next.previous = this.previous;
        }

        @Override // com.twelvemonkeys.util.AbstractDecoratedMap.BasicEntry
        protected void recordAccess(Map<K, V> map) {
            LinkedMap linkedMap = (LinkedMap) map;
            if (linkedMap.accessOrder) {
                linkedMap.modCount++;
                remove();
                addBefore(linkedMap.head);
            }
        }

        @Override // com.twelvemonkeys.util.AbstractDecoratedMap.BasicEntry
        protected void recordRemoval(Map<K, V> map) {
            remove();
        }
    }
}
