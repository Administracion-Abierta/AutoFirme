package com.ctc.wstx.util;

import javax.xml.stream.Location;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/util/ElementIdMap.class */
public final class ElementIdMap {
    protected static final int DEFAULT_SIZE = 128;
    protected static final int MIN_SIZE = 16;
    protected static final int FILL_PCT = 80;
    protected ElementId[] mTable;
    protected int mSize;
    protected int mSizeThreshold;
    protected int mIndexMask;
    protected ElementId mHead;
    protected ElementId mTail;

    public ElementIdMap() {
        this(128);
    }

    public ElementIdMap(int initialSize) {
        int i = 16;
        while (true) {
            int actual = i;
            if (actual < initialSize) {
                i = actual + actual;
            } else {
                this.mTable = new ElementId[actual];
                this.mIndexMask = actual - 1;
                this.mSize = 0;
                this.mSizeThreshold = (actual * 80) / 100;
                this.mTail = null;
                this.mHead = null;
                return;
            }
        }
    }

    public ElementId getFirstUndefined() {
        return this.mHead;
    }

    public ElementId addReferenced(char[] buffer, int start, int len, int hash, Location loc, PrefixedName elemName, PrefixedName attrName) {
        int index = hash & this.mIndexMask;
        ElementId elementIdNextColliding = this.mTable[index];
        while (true) {
            ElementId id = elementIdNextColliding;
            if (id != null) {
                if (id.idMatches(buffer, start, len)) {
                    return id;
                }
                elementIdNextColliding = id.nextColliding();
            } else {
                if (this.mSize >= this.mSizeThreshold) {
                    rehash();
                    index = hash & this.mIndexMask;
                }
                this.mSize++;
                String idStr = new String(buffer, start, len);
                ElementId id2 = new ElementId(idStr, loc, false, elemName, attrName);
                id2.setNextColliding(this.mTable[index]);
                this.mTable[index] = id2;
                if (this.mHead == null) {
                    this.mTail = id2;
                    this.mHead = id2;
                } else {
                    this.mTail.linkUndefined(id2);
                    this.mTail = id2;
                }
                return id2;
            }
        }
    }

    public ElementId addReferenced(String idStr, Location loc, PrefixedName elemName, PrefixedName attrName) {
        int hash = calcHash(idStr);
        int index = hash & this.mIndexMask;
        ElementId elementIdNextColliding = this.mTable[index];
        while (true) {
            ElementId id = elementIdNextColliding;
            if (id != null) {
                if (id.idMatches(idStr)) {
                    return id;
                }
                elementIdNextColliding = id.nextColliding();
            } else {
                if (this.mSize >= this.mSizeThreshold) {
                    rehash();
                    index = hash & this.mIndexMask;
                }
                this.mSize++;
                ElementId id2 = new ElementId(idStr, loc, false, elemName, attrName);
                id2.setNextColliding(this.mTable[index]);
                this.mTable[index] = id2;
                if (this.mHead == null) {
                    this.mTail = id2;
                    this.mHead = id2;
                } else {
                    this.mTail.linkUndefined(id2);
                    this.mTail = id2;
                }
                return id2;
            }
        }
    }

    public ElementId addDefined(char[] buffer, int start, int len, int hash, Location loc, PrefixedName elemName, PrefixedName attrName) {
        ElementId id;
        int index = hash & this.mIndexMask;
        ElementId elementIdNextColliding = this.mTable[index];
        while (true) {
            id = elementIdNextColliding;
            if (id == null || id.idMatches(buffer, start, len)) {
                break;
            }
            elementIdNextColliding = id.nextColliding();
        }
        if (id == null) {
            if (this.mSize >= this.mSizeThreshold) {
                rehash();
                index = hash & this.mIndexMask;
            }
            this.mSize++;
            String idStr = new String(buffer, start, len);
            id = new ElementId(idStr, loc, true, elemName, attrName);
            id.setNextColliding(this.mTable[index]);
            this.mTable[index] = id;
        } else if (!id.isDefined()) {
            id.markDefined(loc);
            if (id == this.mHead) {
                do {
                    this.mHead = this.mHead.nextUndefined();
                    if (this.mHead == null) {
                        break;
                    }
                } while (this.mHead.isDefined());
                if (this.mHead == null) {
                    this.mTail = null;
                }
            }
        }
        return id;
    }

    public ElementId addDefined(String idStr, Location loc, PrefixedName elemName, PrefixedName attrName) {
        ElementId id;
        int hash = calcHash(idStr);
        int index = hash & this.mIndexMask;
        ElementId elementIdNextColliding = this.mTable[index];
        while (true) {
            id = elementIdNextColliding;
            if (id == null || id.idMatches(idStr)) {
                break;
            }
            elementIdNextColliding = id.nextColliding();
        }
        if (id == null) {
            if (this.mSize >= this.mSizeThreshold) {
                rehash();
                index = hash & this.mIndexMask;
            }
            this.mSize++;
            id = new ElementId(idStr, loc, true, elemName, attrName);
            id.setNextColliding(this.mTable[index]);
            this.mTable[index] = id;
        } else if (!id.isDefined()) {
            id.markDefined(loc);
            if (id == this.mHead) {
                do {
                    this.mHead = this.mHead.nextUndefined();
                    if (this.mHead == null) {
                        break;
                    }
                } while (this.mHead.isDefined());
                if (this.mHead == null) {
                    this.mTail = null;
                }
            }
        }
        return id;
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

    private void rehash() {
        int size = this.mTable.length;
        int newSize = size << 2;
        ElementId[] oldSyms = this.mTable;
        this.mTable = new ElementId[newSize];
        this.mIndexMask = newSize - 1;
        this.mSizeThreshold <<= 2;
        int count = 0;
        for (int i = 0; i < size; i++) {
            ElementId elementId = oldSyms[i];
            while (true) {
                ElementId id = elementId;
                if (id != null) {
                    count++;
                    int index = calcHash(id.getId()) & this.mIndexMask;
                    ElementId nextIn = id.nextColliding();
                    id.setNextColliding(this.mTable[index]);
                    this.mTable[index] = id;
                    elementId = nextIn;
                }
            }
        }
        if (count != this.mSize) {
            ExceptionUtil.throwInternal("on rehash(): had " + this.mSize + " entries; now have " + count + ".");
        }
    }
}
