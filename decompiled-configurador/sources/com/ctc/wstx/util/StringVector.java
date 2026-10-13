package com.ctc.wstx.util;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/util/StringVector.class */
public final class StringVector {
    private String[] mStrings;
    private int mSize;

    public StringVector(int initialCount) {
        this.mStrings = new String[initialCount];
    }

    public int size() {
        return this.mSize;
    }

    public boolean isEmpty() {
        return this.mSize == 0;
    }

    public String getString(int index) {
        if (index < 0 || index >= this.mSize) {
            throw new IllegalArgumentException("Index " + index + " out of valid range; current size: " + this.mSize + ".");
        }
        return this.mStrings[index];
    }

    public String getLastString() {
        if (this.mSize < 1) {
            throw new IllegalStateException("getLastString() called on empty StringVector.");
        }
        return this.mStrings[this.mSize - 1];
    }

    public String[] getInternalArray() {
        return this.mStrings;
    }

    public String[] asArray() {
        String[] strs = new String[this.mSize];
        System.arraycopy(this.mStrings, 0, strs, 0, this.mSize);
        return strs;
    }

    public boolean containsInterned(String value) {
        String[] str = this.mStrings;
        int len = this.mSize;
        for (int i = 0; i < len; i++) {
            if (str[i] == value) {
                return true;
            }
        }
        return false;
    }

    public void addString(String str) {
        if (this.mSize == this.mStrings.length) {
            String[] old = this.mStrings;
            int oldSize = old.length;
            this.mStrings = new String[oldSize + (oldSize << 1)];
            System.arraycopy(old, 0, this.mStrings, 0, oldSize);
        }
        String[] strArr = this.mStrings;
        int i = this.mSize;
        this.mSize = i + 1;
        strArr[i] = str;
    }

    public void addStrings(String str1, String str2) {
        if (this.mSize + 2 > this.mStrings.length) {
            String[] old = this.mStrings;
            int oldSize = old.length;
            this.mStrings = new String[oldSize + (oldSize << 1)];
            System.arraycopy(old, 0, this.mStrings, 0, oldSize);
        }
        this.mStrings[this.mSize] = str1;
        this.mStrings[this.mSize + 1] = str2;
        this.mSize += 2;
    }

    public void setString(int index, String str) {
        this.mStrings[index] = str;
    }

    public void clear(boolean removeRefs) {
        if (removeRefs) {
            int len = this.mSize;
            for (int i = 0; i < len; i++) {
                this.mStrings[i] = null;
            }
        }
        this.mSize = 0;
    }

    public String removeLast() {
        String[] strArr = this.mStrings;
        int i = this.mSize - 1;
        this.mSize = i;
        String result = strArr[i];
        this.mStrings[this.mSize] = null;
        return result;
    }

    public void removeLast(int count) {
        while (true) {
            count--;
            if (count >= 0) {
                String[] strArr = this.mStrings;
                int i = this.mSize - 1;
                this.mSize = i;
                strArr[i] = null;
            } else {
                return;
            }
        }
    }

    public String findLastFromMap(String key) {
        int index = this.mSize;
        do {
            index -= 2;
            if (index < 0) {
                return null;
            }
        } while (this.mStrings[index] != key);
        return this.mStrings[index + 1];
    }

    public String findLastNonInterned(String key) {
        int index = this.mSize;
        while (true) {
            index -= 2;
            if (index >= 0) {
                String curr = this.mStrings[index];
                if (curr == key || (curr != null && curr.equals(key))) {
                    return this.mStrings[index + 1];
                }
            } else {
                return null;
            }
        }
    }

    public int findLastIndexNonInterned(String key) {
        int index = this.mSize;
        while (true) {
            index -= 2;
            if (index >= 0) {
                String curr = this.mStrings[index];
                if (curr == key || (curr != null && curr.equals(key))) {
                    return index;
                }
            } else {
                return -1;
            }
        }
    }

    public String findLastByValueNonInterned(String value) {
        for (int index = this.mSize - 1; index > 0; index -= 2) {
            String currVal = this.mStrings[index];
            if (currVal == value || (currVal != null && currVal.equals(value))) {
                return this.mStrings[index - 1];
            }
        }
        return null;
    }

    public int findLastIndexByValueNonInterned(String value) {
        for (int index = this.mSize - 1; index > 0; index -= 2) {
            String currVal = this.mStrings[index];
            if (currVal == value || (currVal != null && currVal.equals(value))) {
                return index - 1;
            }
        }
        return -1;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.mSize * 16);
        sb.append("[(size = ");
        sb.append(this.mSize);
        sb.append(" ) ");
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append('\"');
            sb.append(this.mStrings[i]);
            sb.append('\"');
            sb.append(" == ");
            sb.append(Integer.toHexString(System.identityHashCode(this.mStrings[i])));
        }
        sb.append(']');
        return sb.toString();
    }
}
