package com.aowagie.text.pdf;

import com.sun.jna.platform.win32.WinNT;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/IntHashtable.class */
class IntHashtable implements Cloneable {
    private transient Entry[] table;
    private transient int count;
    private int threshold;
    private final float loadFactor;

    public IntHashtable() {
        this(150, 0.75f);
    }

    public IntHashtable(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    private IntHashtable(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        if (loadFactor <= ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            throw new IllegalArgumentException("Illegal Load: " + loadFactor);
        }
        initialCapacity = initialCapacity == 0 ? 1 : initialCapacity;
        this.loadFactor = loadFactor;
        this.table = new Entry[initialCapacity];
        this.threshold = (int) (initialCapacity * loadFactor);
    }

    int size() {
        return this.count;
    }

    public boolean isEmpty() {
        return this.count == 0;
    }

    boolean containsKey(int key) {
        Entry[] tab = this.table;
        int index = (key & WinNT.MAXLONG) % tab.length;
        Entry entry = tab[index];
        while (true) {
            Entry e = entry;
            if (e != null) {
                if (e.hash == key && e.key == key) {
                    return true;
                }
                entry = e.next;
            } else {
                return false;
            }
        }
    }

    int get(int key) {
        Entry[] tab = this.table;
        int index = (key & WinNT.MAXLONG) % tab.length;
        Entry entry = tab[index];
        while (true) {
            Entry e = entry;
            if (e != null) {
                if (e.hash != key || e.key != key) {
                    entry = e.next;
                } else {
                    return e.value;
                }
            } else {
                return 0;
            }
        }
    }

    private void rehash() {
        int oldCapacity = this.table.length;
        Entry[] oldMap = this.table;
        int newCapacity = (oldCapacity * 2) + 1;
        Entry[] newMap = new Entry[newCapacity];
        this.threshold = (int) (newCapacity * this.loadFactor);
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

    int put(int key, int value) {
        Entry[] tab = this.table;
        int index = (key & WinNT.MAXLONG) % tab.length;
        Entry entry = tab[index];
        while (true) {
            Entry e = entry;
            if (e != null) {
                if (e.hash != key || e.key != key) {
                    entry = e.next;
                } else {
                    int old = e.value;
                    e.value = value;
                    return old;
                }
            } else {
                if (this.count >= this.threshold) {
                    rehash();
                    tab = this.table;
                    index = (key & WinNT.MAXLONG) % tab.length;
                }
                tab[index] = new Entry(key, key, value, tab[index]);
                this.count++;
                return 0;
            }
        }
    }

    int remove(int key) {
        Entry[] tab = this.table;
        int index = (key & WinNT.MAXLONG) % tab.length;
        Entry prev = null;
        for (Entry e = tab[index]; e != null; e = e.next) {
            if (e.hash != key || e.key != key) {
                prev = e;
            } else {
                if (prev == null) {
                    tab[index] = e.next;
                } else {
                    prev.next = e.next;
                }
                this.count--;
                int oldValue = e.value;
                e.value = 0;
                return oldValue;
            }
        }
        return 0;
    }

    void clear() {
        Entry[] tab = this.table;
        int index = tab.length;
        while (true) {
            index--;
            if (index >= 0) {
                tab[index] = null;
            } else {
                this.count = 0;
                return;
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/IntHashtable$Entry.class */
    static class Entry {
        private final int hash;
        private final int key;
        private int value;
        private Entry next;

        private Entry(int hash, int key, int value, Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public int getKey() {
            return this.key;
        }

        public int getValue() {
            return this.value;
        }

        protected Object clone() {
            Entry entry = new Entry(this.hash, this.key, this.value, this.next != null ? (Entry) this.next.clone() : null);
            return entry;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/IntHashtable$IntHashtableIterator.class */
    private static class IntHashtableIterator implements Iterator {
        private int index;
        private final Entry[] table;
        private Entry entry;

        private IntHashtableIterator(Entry[] table) {
            this.table = table;
            this.index = table.length;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            Entry entry;
            if (this.entry != null) {
                return true;
            }
            do {
                int i = this.index;
                this.index = i - 1;
                if (i > 0) {
                    entry = this.table[this.index];
                    this.entry = entry;
                } else {
                    return false;
                }
            } while (entry == null);
            return true;
        }

        @Override // java.util.Iterator
        public Object next() {
            Entry entry;
            if (this.entry == null) {
                do {
                    int i = this.index;
                    this.index = i - 1;
                    if (i <= 0) {
                        break;
                    }
                    entry = this.table[this.index];
                    this.entry = entry;
                } while (entry == null);
            }
            if (this.entry != null) {
                Entry e = this.entry;
                this.entry = e.next;
                return e;
            }
            throw new NoSuchElementException("IntHashtableIterator");
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException("remove() not supported.");
        }
    }

    public Iterator getEntryIterator() {
        return new IntHashtableIterator(this.table);
    }

    int[] toOrderedKeys() {
        int[] res = getKeys();
        Arrays.sort(res);
        return res;
    }

    public int[] getKeys() {
        Entry entry;
        int[] res = new int[this.count];
        int ptr = 0;
        int index = this.table.length;
        Entry entry2 = null;
        while (true) {
            if (entry2 == null) {
                do {
                    int i = index;
                    index--;
                    if (i <= 0) {
                        break;
                    }
                    entry = this.table[index];
                    entry2 = entry;
                } while (entry == null);
            }
            if (entry2 != null) {
                Entry e = entry2;
                entry2 = e.next;
                int i2 = ptr;
                ptr++;
                res[i2] = e.key;
            } else {
                return res;
            }
        }
    }

    public int getOneKey() {
        Entry entry;
        if (this.count == 0) {
            return 0;
        }
        int index = this.table.length;
        Entry entry2 = null;
        do {
            int i = index;
            index--;
            if (i <= 0) {
                break;
            }
            entry = this.table[index];
            entry2 = entry;
        } while (entry == null);
        if (entry2 != null) {
            return entry2.key;
        }
        return 0;
    }

    public Object clone() {
        try {
            IntHashtable t = (IntHashtable) super.clone();
            t.table = new Entry[this.table.length];
            int i = this.table.length;
            while (true) {
                int i2 = i;
                i--;
                if (i2 > 0) {
                    t.table[i] = this.table[i] != null ? (Entry) this.table[i].clone() : null;
                } else {
                    return t;
                }
            }
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}
