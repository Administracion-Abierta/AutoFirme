package com.ctc.wstx.shaded.msv_core.datatype.regexp;

import java.text.CharacterIterator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/BMPattern.class */
final class BMPattern {
    char[] pattern;
    int[] shiftTable;
    boolean ignoreCase;

    public BMPattern(String pat, boolean ignoreCase) {
        this(pat, 256, ignoreCase);
    }

    public BMPattern(String pat, int tableSize, boolean ignoreCase) {
        this.pattern = pat.toCharArray();
        this.shiftTable = new int[tableSize];
        this.ignoreCase = ignoreCase;
        int length = this.pattern.length;
        for (int i = 0; i < this.shiftTable.length; i++) {
            this.shiftTable[i] = length;
        }
        for (int i2 = 0; i2 < length; i2++) {
            char ch = this.pattern[i2];
            int diff = (length - i2) - 1;
            int index = ch % this.shiftTable.length;
            if (diff < this.shiftTable[index]) {
                this.shiftTable[index] = diff;
            }
            if (this.ignoreCase) {
                char ch2 = Character.toUpperCase(ch);
                int index2 = ch2 % this.shiftTable.length;
                if (diff < this.shiftTable[index2]) {
                    this.shiftTable[index2] = diff;
                }
                int index3 = Character.toLowerCase(ch2) % this.shiftTable.length;
                if (diff < this.shiftTable[index3]) {
                    this.shiftTable[index3] = diff;
                }
            }
        }
    }

    public int matches(CharacterIterator iterator, int start, int limit) {
        char ch;
        if (this.ignoreCase) {
            return matchesIgnoreCase(iterator, start, limit);
        }
        int plength = this.pattern.length;
        if (plength == 0) {
            return start;
        }
        int index = start + plength;
        while (index <= limit) {
            int pindex = plength;
            int nindex = index + 1;
            do {
                index--;
                ch = iterator.setIndex(index);
                pindex--;
                if (ch != this.pattern[pindex]) {
                    break;
                }
                if (pindex == 0) {
                    return index;
                }
            } while (pindex > 0);
            index += this.shiftTable[ch % this.shiftTable.length] + 1;
            if (index < nindex) {
                index = nindex;
            }
        }
        return -1;
    }

    public int matches(String str, int start, int limit) {
        char ch;
        if (this.ignoreCase) {
            return matchesIgnoreCase(str, start, limit);
        }
        int plength = this.pattern.length;
        if (plength == 0) {
            return start;
        }
        int index = start + plength;
        while (index <= limit) {
            int pindex = plength;
            int nindex = index + 1;
            do {
                index--;
                ch = str.charAt(index);
                pindex--;
                if (ch != this.pattern[pindex]) {
                    break;
                }
                if (pindex == 0) {
                    return index;
                }
            } while (pindex > 0);
            index += this.shiftTable[ch % this.shiftTable.length] + 1;
            if (index < nindex) {
                index = nindex;
            }
        }
        return -1;
    }

    public int matches(char[] chars, int start, int limit) {
        char ch;
        if (this.ignoreCase) {
            return matchesIgnoreCase(chars, start, limit);
        }
        int plength = this.pattern.length;
        if (plength == 0) {
            return start;
        }
        int index = start + plength;
        while (index <= limit) {
            int pindex = plength;
            int nindex = index + 1;
            do {
                index--;
                ch = chars[index];
                pindex--;
                if (ch != this.pattern[pindex]) {
                    break;
                }
                if (pindex == 0) {
                    return index;
                }
            } while (pindex > 0);
            index += this.shiftTable[ch % this.shiftTable.length] + 1;
            if (index < nindex) {
                index = nindex;
            }
        }
        return -1;
    }

    int matchesIgnoreCase(CharacterIterator iterator, int start, int limit) {
        char ch1;
        char ch2;
        char ch3;
        int plength = this.pattern.length;
        if (plength == 0) {
            return start;
        }
        int index = start + plength;
        while (index <= limit) {
            int pindex = plength;
            int nindex = index + 1;
            do {
                index--;
                ch1 = iterator.setIndex(index);
                pindex--;
                char ch4 = this.pattern[pindex];
                if (ch1 != ch4 && (ch2 = Character.toUpperCase(ch1)) != (ch3 = Character.toUpperCase(ch4)) && Character.toLowerCase(ch2) != Character.toLowerCase(ch3)) {
                    break;
                }
                if (pindex == 0) {
                    return index;
                }
            } while (pindex > 0);
            index += this.shiftTable[ch1 % this.shiftTable.length] + 1;
            if (index < nindex) {
                index = nindex;
            }
        }
        return -1;
    }

    int matchesIgnoreCase(String text, int start, int limit) {
        char ch1;
        char ch2;
        char ch3;
        int plength = this.pattern.length;
        if (plength == 0) {
            return start;
        }
        int index = start + plength;
        while (index <= limit) {
            int pindex = plength;
            int nindex = index + 1;
            do {
                index--;
                ch1 = text.charAt(index);
                pindex--;
                char ch4 = this.pattern[pindex];
                if (ch1 != ch4 && (ch2 = Character.toUpperCase(ch1)) != (ch3 = Character.toUpperCase(ch4)) && Character.toLowerCase(ch2) != Character.toLowerCase(ch3)) {
                    break;
                }
                if (pindex == 0) {
                    return index;
                }
            } while (pindex > 0);
            index += this.shiftTable[ch1 % this.shiftTable.length] + 1;
            if (index < nindex) {
                index = nindex;
            }
        }
        return -1;
    }

    int matchesIgnoreCase(char[] chars, int start, int limit) {
        char ch1;
        char ch2;
        char ch3;
        int plength = this.pattern.length;
        if (plength == 0) {
            return start;
        }
        int index = start + plength;
        while (index <= limit) {
            int pindex = plength;
            int nindex = index + 1;
            do {
                index--;
                ch1 = chars[index];
                pindex--;
                char ch4 = this.pattern[pindex];
                if (ch1 != ch4 && (ch2 = Character.toUpperCase(ch1)) != (ch3 = Character.toUpperCase(ch4)) && Character.toLowerCase(ch2) != Character.toLowerCase(ch3)) {
                    break;
                }
                if (pindex == 0) {
                    return index;
                }
            } while (pindex > 0);
            index += this.shiftTable[ch1 % this.shiftTable.length] + 1;
            if (index < nindex) {
                index = nindex;
            }
        }
        return -1;
    }
}
