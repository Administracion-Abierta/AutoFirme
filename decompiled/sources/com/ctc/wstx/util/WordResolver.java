package com.ctc.wstx.util;

import java.util.TreeSet;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/util/WordResolver.class */
public final class WordResolver {
    public static final int MAX_WORDS = 8192;
    static final char CHAR_NULL = 0;
    static final int NEGATIVE_OFFSET = 57344;
    static final int MIN_BINARY_SEARCH = 7;
    final char[] mData;
    final String[] mWords;

    WordResolver(String[] words, char[] index) {
        this.mWords = words;
        this.mData = index;
    }

    public static WordResolver constructInstance(TreeSet<String> wordSet) {
        if (wordSet.size() > 8192) {
            return null;
        }
        return new Builder(wordSet).construct();
    }

    public int size() {
        return this.mWords.length;
    }

    public String find(char[] str, int start, int end) {
        char[] data = this.mData;
        if (data == null) {
            return findFromOne(str, start, end);
        }
        int ptr = 0;
        int offset = start;
        while (offset != end) {
            int i = ptr;
            int ptr2 = ptr + 1;
            char c = data[i];
            int i2 = offset;
            offset++;
            char c2 = str[i2];
            if (c < 7) {
                if (data[ptr2] == c2) {
                    ptr = data[ptr2 + 1];
                } else if (data[ptr2 + 2] == c2) {
                    ptr = data[ptr2 + 3];
                } else {
                    int branchEnd = ptr2 + (c << 1);
                    for (int ptr3 = ptr2 + 4; ptr3 < branchEnd; ptr3 += 2) {
                        if (data[ptr3] == c2) {
                            ptr = data[ptr3 + 1];
                        }
                    }
                    return null;
                }
            } else {
                int low = 0;
                int high = c - 1;
                while (low <= high) {
                    int mid = (low + high) >> 1;
                    int ix = ptr2 + (mid << 1);
                    int diff = data[ix] - c2;
                    if (diff > 0) {
                        high = mid - 1;
                    } else if (diff < 0) {
                        low = mid + 1;
                    } else {
                        ptr = data[ix + 1];
                    }
                }
                return null;
            }
            if (ptr >= NEGATIVE_OFFSET) {
                String word = this.mWords[ptr - NEGATIVE_OFFSET];
                int expLen = end - start;
                if (word.length() != expLen) {
                    return null;
                }
                int i3 = offset - start;
                while (offset < end) {
                    if (word.charAt(i3) == str[offset]) {
                        i3++;
                        offset++;
                    } else {
                        return null;
                    }
                }
                return word;
            }
        }
        if (data[ptr + 1] == 0) {
            return this.mWords[data[ptr + 2] - NEGATIVE_OFFSET];
        }
        return null;
    }

    private String findFromOne(char[] str, int start, int end) {
        String word = this.mWords[0];
        int len = end - start;
        if (word.length() != len) {
            return null;
        }
        for (int i = 0; i < len; i++) {
            if (word.charAt(i) != str[start + i]) {
                return null;
            }
        }
        return word;
    }

    public String find(String str) {
        char[] data = this.mData;
        if (data == null) {
            String word = this.mWords[0];
            if (word.equals(str)) {
                return word;
            }
            return null;
        }
        int ptr = 0;
        int offset = 0;
        int end = str.length();
        while (offset != end) {
            int i = ptr;
            int ptr2 = ptr + 1;
            char c = data[i];
            int i2 = offset;
            offset++;
            char c2 = str.charAt(i2);
            if (c < 7) {
                if (data[ptr2] == c2) {
                    ptr = data[ptr2 + 1];
                } else if (data[ptr2 + 2] == c2) {
                    ptr = data[ptr2 + 3];
                } else {
                    int branchEnd = ptr2 + (c << 1);
                    for (int ptr3 = ptr2 + 4; ptr3 < branchEnd; ptr3 += 2) {
                        if (data[ptr3] == c2) {
                            ptr = data[ptr3 + 1];
                        }
                    }
                    return null;
                }
            } else {
                int low = 0;
                int high = c - 1;
                while (low <= high) {
                    int mid = (low + high) >> 1;
                    int ix = ptr2 + (mid << 1);
                    int diff = data[ix] - c2;
                    if (diff > 0) {
                        high = mid - 1;
                    } else if (diff < 0) {
                        low = mid + 1;
                    } else {
                        ptr = data[ix + 1];
                    }
                }
                return null;
            }
            if (ptr >= NEGATIVE_OFFSET) {
                String word2 = this.mWords[ptr - NEGATIVE_OFFSET];
                if (word2.length() != str.length()) {
                    return null;
                }
                while (offset < end) {
                    if (word2.charAt(offset) == str.charAt(offset)) {
                        offset++;
                    } else {
                        return null;
                    }
                }
                return word2;
            }
        }
        if (data[ptr + 1] == 0) {
            return this.mWords[data[ptr + 2] - NEGATIVE_OFFSET];
        }
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(16 + (this.mWords.length << 3));
        int len = this.mWords.length;
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.mWords[i]);
        }
        return sb.toString();
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/util/WordResolver$Builder.class */
    private static final class Builder {
        final String[] mWords;
        char[] mData;
        int mSize;

        public Builder(TreeSet<String> wordSet) {
            int wordCount = wordSet.size();
            this.mWords = new String[wordCount];
            wordSet.toArray(this.mWords);
            if (wordCount < 2) {
                if (wordCount == 0) {
                    throw new IllegalArgumentException();
                }
                this.mData = null;
            } else {
                int size = wordCount * 6;
                this.mData = new char[size < 256 ? 256 : size];
            }
        }

        public WordResolver construct() {
            char[] result;
            if (this.mData == null) {
                result = null;
            } else {
                constructBranch(0, 0, this.mWords.length);
                if (this.mSize > WordResolver.NEGATIVE_OFFSET) {
                    return null;
                }
                result = new char[this.mSize];
                System.arraycopy(this.mData, 0, result, 0, this.mSize);
            }
            return new WordResolver(this.mWords, result);
        }

        private void constructBranch(int charIndex, int start, int end) {
            boolean gotRunt;
            if (this.mSize >= this.mData.length) {
                expand(1);
            }
            char[] cArr = this.mData;
            int i = this.mSize;
            this.mSize = i + 1;
            cArr[i] = 0;
            int structStart = this.mSize + 1;
            int groupCount = 0;
            int groupStart = start;
            String[] words = this.mWords;
            if (words[groupStart].length() == charIndex) {
                if (this.mSize + 2 > this.mData.length) {
                    expand(2);
                }
                char[] cArr2 = this.mData;
                int i2 = this.mSize;
                this.mSize = i2 + 1;
                cArr2[i2] = 0;
                char[] cArr3 = this.mData;
                int i3 = this.mSize;
                this.mSize = i3 + 1;
                cArr3[i3] = (char) (WordResolver.NEGATIVE_OFFSET + groupStart);
                groupStart++;
                groupCount = 0 + 1;
                gotRunt = true;
            } else {
                gotRunt = false;
            }
            while (groupStart < end) {
                char c = words[groupStart].charAt(charIndex);
                int j = groupStart + 1;
                while (j < end && words[j].charAt(charIndex) == c) {
                    j++;
                }
                if (this.mSize + 2 > this.mData.length) {
                    expand(2);
                }
                char[] cArr4 = this.mData;
                int i4 = this.mSize;
                this.mSize = i4 + 1;
                cArr4[i4] = c;
                char[] cArr5 = this.mData;
                int i5 = this.mSize;
                this.mSize = i5 + 1;
                cArr5[i5] = (char) (j - groupStart);
                groupStart = j;
                groupCount++;
            }
            this.mData[structStart - 2] = (char) groupCount;
            int groupStart2 = start;
            if (gotRunt) {
                structStart += 2;
                groupStart2++;
            }
            int structEnd = this.mSize;
            int charIndex2 = charIndex + 1;
            while (structStart < structEnd) {
                char c2 = this.mData[structStart];
                if (c2 == 1) {
                    this.mData[structStart] = (char) (WordResolver.NEGATIVE_OFFSET + groupStart2);
                } else {
                    this.mData[structStart] = (char) this.mSize;
                    constructBranch(charIndex2, groupStart2, groupStart2 + c2);
                }
                groupStart2 += c2;
                structStart += 2;
            }
        }

        private char[] expand(int needSpace) {
            char[] old = this.mData;
            int len = old.length;
            int newSize = len + (len < 4096 ? len : len >> 1);
            if (newSize < this.mSize + needSpace) {
                newSize = this.mSize + needSpace + 64;
            }
            this.mData = new char[newSize];
            System.arraycopy(old, 0, this.mData, 0, len);
            return this.mData;
        }
    }
}
