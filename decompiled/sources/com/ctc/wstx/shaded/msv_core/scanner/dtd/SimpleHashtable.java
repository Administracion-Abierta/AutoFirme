package com.ctc.wstx.shaded.msv_core.scanner.dtd;

import com.sun.jna.platform.win32.WinNT;
import java.util.Enumeration;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/scanner/dtd/SimpleHashtable.class */
final class SimpleHashtable implements Enumeration {
    private Entry[] table;
    private Entry current;
    private int currentBucket;
    private int count;
    private int threshold;
    private static final float loadFactor = 0.75f;

    public SimpleHashtable(int initialCapacity) {
        this.current = null;
        this.currentBucket = 0;
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        initialCapacity = initialCapacity == 0 ? 1 : initialCapacity;
        this.table = new Entry[initialCapacity];
        this.threshold = (int) (initialCapacity * loadFactor);
    }

    public SimpleHashtable() {
        this(11);
    }

    public void clear() {
        this.count = 0;
        this.currentBucket = 0;
        this.current = null;
        for (int i = 0; i < this.table.length; i++) {
            this.table[i] = null;
        }
    }

    public int size() {
        return this.count;
    }

    public Enumeration keys() {
        this.currentBucket = 0;
        this.current = null;
        return this;
    }

    @Override // java.util.Enumeration
    public boolean hasMoreElements() {
        if (this.current != null) {
            return true;
        }
        while (this.currentBucket < this.table.length) {
            Entry[] entryArr = this.table;
            int i = this.currentBucket;
            this.currentBucket = i + 1;
            this.current = entryArr[i];
            if (this.current != null) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.Enumeration
    public Object nextElement() {
        if (this.current == null) {
            throw new IllegalStateException();
        }
        Object retval = this.current.key;
        this.current = this.current.next;
        return retval;
    }

    public Object get(String key) {
        Entry[] tab = this.table;
        int hash = key.hashCode();
        int index = (hash & WinNT.MAXLONG) % tab.length;
        Entry entry = tab[index];
        while (true) {
            Entry e = entry;
            if (e != null) {
                if (e.hash != hash || e.key != key) {
                    entry = e.next;
                } else {
                    return e.value;
                }
            } else {
                return null;
            }
        }
    }

    public Object getNonInterned(String key) {
        Entry[] tab = this.table;
        int hash = key.hashCode();
        int index = (hash & WinNT.MAXLONG) % tab.length;
        Entry entry = tab[index];
        while (true) {
            Entry e = entry;
            if (e != null) {
                if (e.hash != hash || !e.key.equals(key)) {
                    entry = e.next;
                } else {
                    return e.value;
                }
            } else {
                return null;
            }
        }
    }

    private void rehash() {
        int oldCapacity = this.table.length;
        Entry[] oldMap = this.table;
        int newCapacity = (oldCapacity * 2) + 1;
        Entry[] newMap = new Entry[newCapacity];
        this.threshold = (int) (newCapacity * loadFactor);
        this.table = newMap;
        int i = oldCapacity;
        while (true) {
            int i2 = i;
            i--;
            if (i2 > 0) {
                Entry old = oldMap[i];
                while (old != null) {
                    Entry e = old;
                    old = old.next;
                    int index = (e.hash & WinNT.MAXLONG) % newCapacity;
                    e.next = newMap[index];
                    newMap[index] = e;
                }
            } else {
                return;
            }
        }
    }

    public Object put(Object key, Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        Entry[] tab = this.table;
        int hash = key.hashCode();
        int index = (hash & WinNT.MAXLONG) % tab.length;
        Entry entry = tab[index];
        while (true) {
            Entry e = entry;
            if (e != null) {
                if (e.hash != hash || e.key != key) {
                    entry = e.next;
                } else {
                    Object old = e.value;
                    e.value = value;
                    return old;
                }
            } else {
                if (this.count >= this.threshold) {
                    rehash();
                    tab = this.table;
                    index = (hash & WinNT.MAXLONG) % tab.length;
                }
                tab[index] = new Entry(hash, key, value, tab[index]);
                this.count++;
                return null;
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/scanner/dtd/SimpleHashtable$Entry.class */
    private static class Entry {
        int hash;
        Object key;
        Object value;
        Entry next;

        protected Entry(int hash, Object key, Object value, Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
