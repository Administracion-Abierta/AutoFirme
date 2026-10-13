package com.ctc.wstx.util;

import java.util.TreeSet;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/util/WordSet.class */
public final class WordSet {
    static final char CHAR_NULL = 0;
    static final int NEGATIVE_OFFSET = 49152;
    static final int MIN_BINARY_SEARCH = 7;
    final char[] mData;

    private WordSet(char[] data) {
        this.mData = data;
    }

    public static WordSet constructSet(TreeSet<String> wordSet) {
        return new WordSet(new Builder(wordSet).construct());
    }

    public static char[] constructRaw(TreeSet<String> wordSet) {
        return new Builder(wordSet).construct();
    }

    public boolean contains(char[] buf, int start, int end) {
        return contains(this.mData, buf, start, end);
    }

    public static boolean contains(char[] data, char[] str, int start, int end) {
        int ptr = 0;
        do {
            int left = end - start;
            if (left == 0) {
                return data[ptr + 1] == 0;
            }
            int i = ptr;
            int ptr2 = ptr + 1;
            char c = data[i];
            if (c >= NEGATIVE_OFFSET) {
                int expCount = c - NEGATIVE_OFFSET;
                if (left != expCount) {
                    return false;
                }
                while (start < end) {
                    if (data[ptr2] != str[start]) {
                        return false;
                    }
                    ptr2++;
                    start++;
                }
                return true;
            }
            int i2 = start;
            start++;
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
                    return false;
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
                return false;
            }
        } while (ptr != 0);
        return start == end;
    }

    public boolean contains(String str) {
        return contains(this.mData, str);
    }

    public static boolean contains(char[] data, String str) {
        int ptr = 0;
        int start = 0;
        int end = str.length();
        do {
            int left = end - start;
            if (left == 0) {
                return data[ptr + 1] == 0;
            }
            int i = ptr;
            int ptr2 = ptr + 1;
            char c = data[i];
            if (c >= NEGATIVE_OFFSET) {
                int expCount = c - NEGATIVE_OFFSET;
                if (left != expCount) {
                    return false;
                }
                while (start < end) {
                    if (data[ptr2] != str.charAt(start)) {
                        return false;
                    }
                    ptr2++;
                    start++;
                }
                return true;
            }
            int i2 = start;
            start++;
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
                    return false;
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
                return false;
            }
        } while (ptr != 0);
        return start == end;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/util/WordSet$Builder.class */
    private static final class Builder {
        final String[] mWords;
        char[] mData;
        int mSize;

        public Builder(TreeSet<String> wordSet) {
            int wordCount = wordSet.size();
            this.mWords = new String[wordCount];
            wordSet.toArray(this.mWords);
            int size = wordCount * 12;
            this.mData = new char[size < 256 ? 256 : size];
        }

        public char[] construct() {
            if (this.mWords.length == 1) {
                constructLeaf(0, 0);
            } else {
                constructBranch(0, 0, this.mWords.length);
            }
            char[] result = new char[this.mSize];
            System.arraycopy(this.mData, 0, result, 0, this.mSize);
            return result;
        }

        private void constructBranch(int charIndex, int start, int end) {
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
                cArr3[i3] = 0;
                groupStart++;
                groupCount = 0 + 1;
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
            if (this.mData[structStart] == 0) {
                structStart += 2;
                groupStart2++;
            }
            int structEnd = this.mSize;
            int charIndex2 = charIndex + 1;
            while (structStart < structEnd) {
                char c2 = this.mData[structStart];
                this.mData[structStart] = (char) this.mSize;
                if (c2 == 1) {
                    String word = words[groupStart2];
                    if (word.length() == charIndex2) {
                        this.mData[structStart] = 0;
                    } else {
                        constructLeaf(charIndex2, groupStart2);
                    }
                } else {
                    constructBranch(charIndex2, groupStart2, groupStart2 + c2);
                }
                groupStart2 += c2;
                structStart += 2;
            }
        }

        private void constructLeaf(int charIndex, int wordIndex) {
            String word = this.mWords[wordIndex];
            int len = word.length();
            char[] data = this.mData;
            if (this.mSize + len + 1 >= data.length) {
                data = expand(len + 1);
            }
            int i = this.mSize;
            this.mSize = i + 1;
            data[i] = (char) (WordSet.NEGATIVE_OFFSET + (len - charIndex));
            while (charIndex < len) {
                int i2 = this.mSize;
                this.mSize = i2 + 1;
                data[i2] = word.charAt(charIndex);
                charIndex++;
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
