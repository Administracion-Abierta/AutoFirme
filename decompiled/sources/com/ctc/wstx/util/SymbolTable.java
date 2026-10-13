package com.ctc.wstx.util;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/util/SymbolTable.class */
public class SymbolTable {
    protected static final int DEFAULT_TABLE_SIZE = 128;
    protected static final float DEFAULT_FILL_FACTOR = 0.75f;
    protected static final String EMPTY_STRING = "";
    protected boolean mInternStrings;
    protected String[] mSymbols;
    protected Bucket[] mBuckets;
    protected int mSize;
    protected int mSizeThreshold;
    protected int mIndexMask;
    protected int mThisVersion;
    protected boolean mDirty;

    public SymbolTable() {
        this(true);
    }

    public SymbolTable(boolean internStrings) {
        this(internStrings, 128);
    }

    public SymbolTable(boolean internStrings, int initialSize) {
        this(internStrings, initialSize, DEFAULT_FILL_FACTOR);
    }

    public SymbolTable(boolean internStrings, int initialSize, float fillFactor) {
        int currSize;
        this.mInternStrings = internStrings;
        this.mThisVersion = 1;
        this.mDirty = true;
        if (initialSize < 1) {
            throw new IllegalArgumentException("Can not use negative/zero initial size: " + initialSize);
        }
        int i = 4;
        while (true) {
            currSize = i;
            if (currSize >= initialSize) {
                break;
            } else {
                i = currSize + currSize;
            }
        }
        this.mSymbols = new String[currSize];
        this.mBuckets = new Bucket[currSize >> 1];
        this.mIndexMask = currSize - 1;
        this.mSize = 0;
        if (fillFactor < 0.01f) {
            throw new IllegalArgumentException("Fill factor can not be lower than 0.01.");
        }
        if (fillFactor > 10.0f) {
            throw new IllegalArgumentException("Fill factor can not be higher than 10.0.");
        }
        this.mSizeThreshold = (int) (((double) (currSize * fillFactor)) + 0.5d);
    }

    private SymbolTable(boolean internStrings, String[] symbols, Bucket[] buckets, int size, int sizeThreshold, int indexMask, int version) {
        this.mInternStrings = internStrings;
        this.mSymbols = symbols;
        this.mBuckets = buckets;
        this.mSize = size;
        this.mSizeThreshold = sizeThreshold;
        this.mIndexMask = indexMask;
        this.mThisVersion = version;
        this.mDirty = false;
    }

    public SymbolTable makeChild() {
        boolean internStrings;
        String[] symbols;
        Bucket[] buckets;
        int size;
        int sizeThreshold;
        int indexMask;
        int version;
        synchronized (this) {
            internStrings = this.mInternStrings;
            symbols = this.mSymbols;
            buckets = this.mBuckets;
            size = this.mSize;
            sizeThreshold = this.mSizeThreshold;
            indexMask = this.mIndexMask;
            version = this.mThisVersion + 1;
        }
        return new SymbolTable(internStrings, symbols, buckets, size, sizeThreshold, indexMask, version);
    }

    public synchronized void mergeChild(SymbolTable child) {
        if (child.size() <= size()) {
            return;
        }
        this.mSymbols = child.mSymbols;
        this.mBuckets = child.mBuckets;
        this.mSize = child.mSize;
        this.mSizeThreshold = child.mSizeThreshold;
        this.mIndexMask = child.mIndexMask;
        this.mThisVersion++;
        this.mDirty = false;
        child.mDirty = false;
    }

    public void setInternStrings(boolean state) {
        this.mInternStrings = state;
    }

    public int size() {
        return this.mSize;
    }

    public int version() {
        return this.mThisVersion;
    }

    public boolean isDirty() {
        return this.mDirty;
    }

    public boolean isDirectChildOf(SymbolTable t) {
        if (this.mThisVersion == t.mThisVersion + 1) {
            return true;
        }
        return false;
    }

    public String findSymbol(char[] buffer, int start, int len, int hash) {
        String sym;
        if (len < 1) {
            return "";
        }
        int hash2 = hash & this.mIndexMask;
        String sym2 = this.mSymbols[hash2];
        if (sym2 != null) {
            if (sym2.length() == len) {
                int i = 0;
                while (sym2.charAt(i) == buffer[start + i]) {
                    i++;
                    if (i >= len) {
                        break;
                    }
                }
                if (i == len) {
                    return sym2;
                }
            }
            Bucket b = this.mBuckets[hash2 >> 1];
            if (b != null && (sym = b.find(buffer, start, len)) != null) {
                return sym;
            }
        }
        if (this.mSize >= this.mSizeThreshold) {
            rehash();
            hash2 = calcHash(buffer, start, len) & this.mIndexMask;
        } else if (!this.mDirty) {
            copyArrays();
            this.mDirty = true;
        }
        this.mSize++;
        String newSymbol = new String(buffer, start, len);
        if (this.mInternStrings) {
            newSymbol = newSymbol.intern();
        }
        if (this.mSymbols[hash2] == null) {
            this.mSymbols[hash2] = newSymbol;
        } else {
            int bix = hash2 >> 1;
            this.mBuckets[bix] = new Bucket(newSymbol, this.mBuckets[bix]);
        }
        return newSymbol;
    }

    public String findSymbolIfExists(char[] buffer, int start, int len, int hash) {
        String sym;
        if (len < 1) {
            return "";
        }
        int hash2 = hash & this.mIndexMask;
        String sym2 = this.mSymbols[hash2];
        if (sym2 != null) {
            if (sym2.length() == len) {
                int i = 0;
                while (sym2.charAt(i) == buffer[start + i]) {
                    i++;
                    if (i >= len) {
                        break;
                    }
                }
                if (i == len) {
                    return sym2;
                }
            }
            Bucket b = this.mBuckets[hash2 >> 1];
            if (b != null && (sym = b.find(buffer, start, len)) != null) {
                return sym;
            }
            return null;
        }
        return null;
    }

    public String findSymbol(String str) {
        String sym;
        int len = str.length();
        if (len < 1) {
            return "";
        }
        int index = calcHash(str) & this.mIndexMask;
        String sym2 = this.mSymbols[index];
        if (sym2 != null) {
            if (sym2.length() == len) {
                int i = 0;
                while (i < len && sym2.charAt(i) == str.charAt(i)) {
                    i++;
                }
                if (i == len) {
                    return sym2;
                }
            }
            Bucket b = this.mBuckets[index >> 1];
            if (b != null && (sym = b.find(str)) != null) {
                return sym;
            }
        }
        if (this.mSize >= this.mSizeThreshold) {
            rehash();
            index = calcHash(str) & this.mIndexMask;
        } else if (!this.mDirty) {
            copyArrays();
            this.mDirty = true;
        }
        this.mSize++;
        if (this.mInternStrings) {
            str = str.intern();
        }
        if (this.mSymbols[index] == null) {
            this.mSymbols[index] = str;
        } else {
            int bix = index >> 1;
            this.mBuckets[bix] = new Bucket(str, this.mBuckets[bix]);
        }
        return str;
    }

    public static int calcHash(char[] buffer, int start, int len) {
        int hash = buffer[start];
        for (int i = 1; i < len; i++) {
            hash = (hash * 31) + buffer[start + i];
        }
        return hash;
    }

    public static int calcHash(String key) {
        int hash = key.charAt(0);
        int len = key.length();
        for (int i = 1; i < len; i++) {
            hash = (hash * 31) + key.charAt(i);
        }
        return hash;
    }

    private void copyArrays() {
        String[] oldSyms = this.mSymbols;
        int size = oldSyms.length;
        this.mSymbols = new String[size];
        System.arraycopy(oldSyms, 0, this.mSymbols, 0, size);
        Bucket[] oldBuckets = this.mBuckets;
        int size2 = oldBuckets.length;
        this.mBuckets = new Bucket[size2];
        System.arraycopy(oldBuckets, 0, this.mBuckets, 0, size2);
    }

    private void rehash() {
        int size = this.mSymbols.length;
        int newSize = size + size;
        String[] oldSyms = this.mSymbols;
        Bucket[] oldBuckets = this.mBuckets;
        this.mSymbols = new String[newSize];
        this.mBuckets = new Bucket[newSize >> 1];
        this.mIndexMask = newSize - 1;
        this.mSizeThreshold += this.mSizeThreshold;
        int count = 0;
        for (int i = 0; i < size; i++) {
            String symbol = oldSyms[i];
            if (symbol != null) {
                count++;
                int index = calcHash(symbol) & this.mIndexMask;
                if (this.mSymbols[index] == null) {
                    this.mSymbols[index] = symbol;
                } else {
                    int bix = index >> 1;
                    this.mBuckets[bix] = new Bucket(symbol, this.mBuckets[bix]);
                }
            }
        }
        int size2 = size >> 1;
        for (int i2 = 0; i2 < size2; i2++) {
            Bucket next = oldBuckets[i2];
            while (true) {
                Bucket b = next;
                if (b != null) {
                    count++;
                    String symbol2 = b.getSymbol();
                    int index2 = calcHash(symbol2) & this.mIndexMask;
                    if (this.mSymbols[index2] == null) {
                        this.mSymbols[index2] = symbol2;
                    } else {
                        int bix2 = index2 >> 1;
                        this.mBuckets[bix2] = new Bucket(symbol2, this.mBuckets[bix2]);
                    }
                    next = b.getNext();
                }
            }
        }
        if (count != this.mSize) {
            throw new IllegalStateException("Internal error on SymbolTable.rehash(): had " + this.mSize + " entries; now have " + count + ".");
        }
    }

    public double calcAvgSeek() {
        int count = 0;
        int len = this.mSymbols.length;
        for (int i = 0; i < len; i++) {
            if (this.mSymbols[i] != null) {
                count++;
            }
        }
        int len2 = this.mBuckets.length;
        for (int i2 = 0; i2 < len2; i2++) {
            int cost = 2;
            for (Bucket b = this.mBuckets[i2]; b != null; b = b.getNext()) {
                count += cost;
                cost++;
            }
        }
        return ((double) count) / ((double) this.mSize);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/util/SymbolTable$Bucket.class */
    static final class Bucket {
        private final String mSymbol;
        private final Bucket mNext;

        public Bucket(String symbol, Bucket next) {
            this.mSymbol = symbol;
            this.mNext = next;
        }

        public String getSymbol() {
            return this.mSymbol;
        }

        public Bucket getNext() {
            return this.mNext;
        }

        public String find(char[] buf, int start, int len) {
            String sym = this.mSymbol;
            Bucket next = this.mNext;
            while (true) {
                Bucket b = next;
                if (sym.length() == len) {
                    int i = 0;
                    while (sym.charAt(i) == buf[start + i]) {
                        i++;
                        if (i >= len) {
                            break;
                        }
                    }
                    if (i == len) {
                        return sym;
                    }
                }
                if (b != null) {
                    sym = b.getSymbol();
                    next = b.getNext();
                } else {
                    return null;
                }
            }
        }

        public String find(String str) {
            String sym = this.mSymbol;
            Bucket next = this.mNext;
            while (true) {
                Bucket b = next;
                if (sym.equals(str)) {
                    return sym;
                }
                if (b != null) {
                    sym = b.getSymbol();
                    next = b.getNext();
                } else {
                    return null;
                }
            }
        }
    }
}
