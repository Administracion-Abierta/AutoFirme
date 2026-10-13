package org.apache.xml.security.c14n.implementations;

import com.sun.jna.platform.win32.WinNT;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: compiled from: NameSpaceSymbTable.java */
/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/c14n/implementations/SymbMap.class */
class SymbMap implements Cloneable {
    int free = 23;
    NameSpaceSymbEntry[] entries = new NameSpaceSymbEntry[this.free];
    String[] keys = new String[this.free];

    SymbMap() {
    }

    void put(String key, NameSpaceSymbEntry value) {
        int index = index(key);
        Object oldKey = this.keys[index];
        this.keys[index] = key;
        this.entries[index] = value;
        if (oldKey == null || !oldKey.equals(key)) {
            int i = this.free - 1;
            this.free = i;
            if (i == 0) {
                this.free = this.entries.length;
                int newCapacity = this.free << 2;
                rehash(newCapacity);
            }
        }
    }

    List<NameSpaceSymbEntry> entrySet() {
        List<NameSpaceSymbEntry> a = new ArrayList<>();
        for (NameSpaceSymbEntry nsEntry : this.entries) {
            if (nsEntry != null && !nsEntry.uri.isEmpty()) {
                a.add(nsEntry);
            }
        }
        return a;
    }

    protected int index(Object obj) {
        Object cur;
        Object[] set = this.keys;
        int length = set.length;
        int index = (obj.hashCode() & WinNT.MAXLONG) % length;
        Object cur2 = set[index];
        if (cur2 == null || cur2.equals(obj)) {
            return index;
        }
        int length2 = length - 1;
        do {
            index = index == length2 ? 0 : index + 1;
            cur = set[index];
            if (cur == null) {
                break;
            }
        } while (!cur.equals(obj));
        return index;
    }

    protected void rehash(int newCapacity) {
        int oldCapacity = this.keys.length;
        String[] oldKeys = this.keys;
        NameSpaceSymbEntry[] oldVals = this.entries;
        this.keys = new String[newCapacity];
        this.entries = new NameSpaceSymbEntry[newCapacity];
        int i = oldCapacity;
        while (true) {
            int i2 = i;
            i--;
            if (i2 > 0) {
                if (oldKeys[i] != null) {
                    String o = oldKeys[i];
                    int index = index(o);
                    this.keys[index] = o;
                    this.entries[index] = oldVals[i];
                }
            } else {
                return;
            }
        }
    }

    NameSpaceSymbEntry get(String key) {
        return this.entries[index(key)];
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public SymbMap m1322clone() throws CloneNotSupportedException {
        SymbMap copy = (SymbMap) super.clone();
        copy.entries = new NameSpaceSymbEntry[this.entries.length];
        System.arraycopy(this.entries, 0, copy.entries, 0, this.entries.length);
        copy.keys = new String[this.keys.length];
        System.arraycopy(this.keys, 0, copy.keys, 0, this.keys.length);
        return copy;
    }
}
