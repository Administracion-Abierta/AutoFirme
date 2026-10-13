package com.ctc.wstx.dtd;

import com.ctc.wstx.util.PrefixedName;
import java.util.Iterator;
import java.util.TreeSet;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/LargePrefixedNameSet.class */
public final class LargePrefixedNameSet extends PrefixedNameSet {
    static final int MIN_HASH_AREA = 8;
    final boolean mNsAware;
    final PrefixedName[] mNames;
    final Bucket[] mBuckets;

    public LargePrefixedNameSet(boolean nsAware, PrefixedName[] names) {
        int tableSize;
        Bucket old;
        this.mNsAware = nsAware;
        int len = names.length;
        int minSize = len + ((len + 7) >> 3);
        int i = 8;
        while (true) {
            tableSize = i;
            if (tableSize >= minSize) {
                break;
            } else {
                i = tableSize + tableSize;
            }
        }
        this.mNames = new PrefixedName[tableSize];
        Bucket[] buckets = null;
        int mask = tableSize - 1;
        for (PrefixedName nk : names) {
            int ix = nk.hashCode() & mask;
            if (this.mNames[ix] == null) {
                this.mNames[ix] = nk;
            } else {
                int ix2 = ix >> 2;
                if (buckets == null) {
                    buckets = new Bucket[tableSize >> 2];
                    old = null;
                } else {
                    old = buckets[ix2];
                }
                buckets[ix2] = new Bucket(nk, old);
            }
        }
        this.mBuckets = buckets;
    }

    @Override // com.ctc.wstx.dtd.PrefixedNameSet
    public boolean hasMultiple() {
        return true;
    }

    @Override // com.ctc.wstx.dtd.PrefixedNameSet
    public boolean contains(PrefixedName name) {
        PrefixedName[] hashArea = this.mNames;
        int index = name.hashCode() & (hashArea.length - 1);
        PrefixedName res = hashArea[index];
        if (res != null && res.equals(name)) {
            return true;
        }
        Bucket[] buckets = this.mBuckets;
        if (buckets != null) {
            Bucket next = buckets[index >> 2];
            while (true) {
                Bucket bucket = next;
                if (bucket != null) {
                    if (!bucket.getName().equals(name)) {
                        next = bucket.getNext();
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    @Override // com.ctc.wstx.dtd.PrefixedNameSet
    public void appendNames(StringBuilder sb, String sep) {
        TreeSet<PrefixedName> ts = new TreeSet<>();
        for (int i = 0; i < this.mNames.length; i++) {
            PrefixedName name = this.mNames[i];
            if (name != null) {
                ts.add(name);
            }
        }
        if (this.mBuckets != null) {
            for (int i2 = 0; i2 < (this.mNames.length >> 2); i2++) {
                Bucket next = this.mBuckets[i2];
                while (true) {
                    Bucket b = next;
                    if (b != null) {
                        ts.add(b.getName());
                        next = b.getNext();
                    }
                }
            }
        }
        Iterator<PrefixedName> it = ts.iterator();
        boolean first = true;
        while (it.hasNext()) {
            if (first) {
                first = false;
            } else {
                sb.append(sep);
            }
            sb.append(it.next().toString());
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/LargePrefixedNameSet$Bucket.class */
    private static final class Bucket {
        final PrefixedName mName;
        final Bucket mNext;

        public Bucket(PrefixedName name, Bucket next) {
            this.mName = name;
            this.mNext = next;
        }

        public PrefixedName getName() {
            return this.mName;
        }

        public Bucket getNext() {
            return this.mNext;
        }
    }
}
