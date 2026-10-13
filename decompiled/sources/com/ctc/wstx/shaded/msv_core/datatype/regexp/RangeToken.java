package com.ctc.wstx.shaded.msv_core.datatype.regexp;

import java.io.Serializable;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/RangeToken.class */
final class RangeToken extends Token implements Serializable {
    int[] ranges;
    boolean sorted;
    boolean compacted;
    RangeToken icaseCache;
    int[] map;
    int nonMapIndex;
    private static final int MAPSIZE = 256;

    RangeToken(int type) {
        super(type);
        this.icaseCache = null;
        this.map = null;
        setSorted(false);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
    protected void addRange(int start, int end) {
        int r1;
        int r2;
        this.icaseCache = null;
        if (start <= end) {
            r1 = start;
            r2 = end;
        } else {
            r1 = end;
            r2 = start;
        }
        if (this.ranges == null) {
            this.ranges = new int[2];
            this.ranges[0] = r1;
            this.ranges[1] = r2;
            setSorted(true);
            return;
        }
        int pos = this.ranges.length;
        if (this.ranges[pos - 1] + 1 == r1) {
            this.ranges[pos - 1] = r2;
            return;
        }
        int[] temp = new int[pos + 2];
        System.arraycopy(this.ranges, 0, temp, 0, pos);
        this.ranges = temp;
        if (this.ranges[pos - 1] >= r1) {
            setSorted(false);
        }
        this.ranges[pos] = r1;
        this.ranges[pos + 1] = r2;
        if (!this.sorted) {
            sortRanges();
        }
    }

    private final boolean isSorted() {
        return this.sorted;
    }

    private final void setSorted(boolean sort) {
        this.sorted = sort;
        if (!sort) {
            this.compacted = false;
        }
    }

    private final boolean isCompacted() {
        return this.compacted;
    }

    private final void setCompacted() {
        this.compacted = true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
    protected void sortRanges() {
        if (isSorted() || this.ranges == null) {
            return;
        }
        for (int i = this.ranges.length - 4; i >= 0; i -= 2) {
            for (int j = 0; j <= i; j += 2) {
                if (this.ranges[j] > this.ranges[j + 2] || (this.ranges[j] == this.ranges[j + 2] && this.ranges[j + 1] > this.ranges[j + 3])) {
                    int tmp = this.ranges[j + 2];
                    this.ranges[j + 2] = this.ranges[j];
                    this.ranges[j] = tmp;
                    int tmp2 = this.ranges[j + 3];
                    this.ranges[j + 3] = this.ranges[j + 1];
                    this.ranges[j + 1] = tmp2;
                }
            }
        }
        setSorted(true);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
    protected void compactRanges() {
        if (this.ranges == null || this.ranges.length <= 2 || isCompacted()) {
            return;
        }
        int base = 0;
        int target = 0;
        while (target < this.ranges.length) {
            if (base != target) {
                int i = target;
                int target2 = target + 1;
                this.ranges[base] = this.ranges[i];
                target = target2 + 1;
                this.ranges[base + 1] = this.ranges[target2];
            } else {
                target += 2;
            }
            int baseend = this.ranges[base + 1];
            while (target < this.ranges.length && baseend + 1 >= this.ranges[target]) {
                if (baseend + 1 == this.ranges[target]) {
                    if (0 != 0) {
                        System.err.println("Token#compactRanges(): Compaction: [" + this.ranges[base] + ", " + this.ranges[base + 1] + "], [" + this.ranges[target] + ", " + this.ranges[target + 1] + "] -> [" + this.ranges[base] + ", " + this.ranges[target + 1] + "]");
                    }
                    this.ranges[base + 1] = this.ranges[target + 1];
                    baseend = this.ranges[base + 1];
                    target += 2;
                } else if (baseend >= this.ranges[target + 1]) {
                    if (0 != 0) {
                        System.err.println("Token#compactRanges(): Compaction: [" + this.ranges[base] + ", " + this.ranges[base + 1] + "], [" + this.ranges[target] + ", " + this.ranges[target + 1] + "] -> [" + this.ranges[base] + ", " + this.ranges[base + 1] + "]");
                    }
                    target += 2;
                } else if (baseend < this.ranges[target + 1]) {
                    if (0 != 0) {
                        System.err.println("Token#compactRanges(): Compaction: [" + this.ranges[base] + ", " + this.ranges[base + 1] + "], [" + this.ranges[target] + ", " + this.ranges[target + 1] + "] -> [" + this.ranges[base] + ", " + this.ranges[target + 1] + "]");
                    }
                    this.ranges[base + 1] = this.ranges[target + 1];
                    baseend = this.ranges[base + 1];
                    target += 2;
                } else {
                    throw new RuntimeException("Token#compactRanges(): Internel Error: [" + this.ranges[base] + "," + this.ranges[base + 1] + "] [" + this.ranges[target] + "," + this.ranges[target + 1] + "]");
                }
            }
            base += 2;
        }
        if (base != this.ranges.length) {
            int[] result = new int[base];
            System.arraycopy(this.ranges, 0, result, 0, base);
            this.ranges = result;
        }
        setCompacted();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
    protected void mergeRanges(Token token) {
        RangeToken tok = (RangeToken) token;
        sortRanges();
        tok.sortRanges();
        if (tok.ranges == null) {
            return;
        }
        this.icaseCache = null;
        setSorted(true);
        if (this.ranges == null) {
            this.ranges = new int[tok.ranges.length];
            System.arraycopy(tok.ranges, 0, this.ranges, 0, tok.ranges.length);
            return;
        }
        int[] result = new int[this.ranges.length + tok.ranges.length];
        int i = 0;
        int j = 0;
        int k = 0;
        while (true) {
            if (i < this.ranges.length || j < tok.ranges.length) {
                if (i >= this.ranges.length) {
                    int i2 = k;
                    int k2 = k + 1;
                    int i3 = j;
                    int j2 = j + 1;
                    result[i2] = tok.ranges[i3];
                    k = k2 + 1;
                    j = j2 + 1;
                    result[k2] = tok.ranges[j2];
                } else if (j >= tok.ranges.length) {
                    int i4 = k;
                    int k3 = k + 1;
                    int i5 = i;
                    int i6 = i + 1;
                    result[i4] = this.ranges[i5];
                    k = k3 + 1;
                    i = i6 + 1;
                    result[k3] = this.ranges[i6];
                } else if (tok.ranges[j] < this.ranges[i] || (tok.ranges[j] == this.ranges[i] && tok.ranges[j + 1] < this.ranges[i + 1])) {
                    int i7 = k;
                    int k4 = k + 1;
                    int i8 = j;
                    int j3 = j + 1;
                    result[i7] = tok.ranges[i8];
                    k = k4 + 1;
                    j = j3 + 1;
                    result[k4] = tok.ranges[j3];
                } else {
                    int i9 = k;
                    int k5 = k + 1;
                    int i10 = i;
                    int i11 = i + 1;
                    result[i9] = this.ranges[i10];
                    k = k5 + 1;
                    i = i11 + 1;
                    result[k5] = this.ranges[i11];
                }
            } else {
                this.ranges = result;
                return;
            }
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
    protected void subtractRanges(Token token) {
        if (token.type == 5) {
            intersectRanges(token);
            return;
        }
        RangeToken tok = (RangeToken) token;
        if (tok.ranges == null || this.ranges == null) {
            return;
        }
        this.icaseCache = null;
        sortRanges();
        compactRanges();
        tok.sortRanges();
        tok.compactRanges();
        int[] result = new int[this.ranges.length + tok.ranges.length];
        int wp = 0;
        int src = 0;
        int sub = 0;
        while (src < this.ranges.length && sub < tok.ranges.length) {
            int srcbegin = this.ranges[src];
            int srcend = this.ranges[src + 1];
            int subbegin = tok.ranges[sub];
            int subend = tok.ranges[sub + 1];
            if (srcend < subbegin) {
                int i = wp;
                int wp2 = wp + 1;
                int i2 = src;
                int src2 = src + 1;
                result[i] = this.ranges[i2];
                wp = wp2 + 1;
                src = src2 + 1;
                result[wp2] = this.ranges[src2];
            } else if (srcend >= subbegin && srcbegin <= subend) {
                if (subbegin <= srcbegin && srcend <= subend) {
                    src += 2;
                } else if (subbegin <= srcbegin) {
                    this.ranges[src] = subend + 1;
                    sub += 2;
                } else if (srcend <= subend) {
                    int i3 = wp;
                    int wp3 = wp + 1;
                    result[i3] = srcbegin;
                    wp = wp3 + 1;
                    result[wp3] = subbegin - 1;
                    src += 2;
                } else {
                    int i4 = wp;
                    int wp4 = wp + 1;
                    result[i4] = srcbegin;
                    wp = wp4 + 1;
                    result[wp4] = subbegin - 1;
                    this.ranges[src] = subend + 1;
                    sub += 2;
                }
            } else if (subend < srcbegin) {
                sub += 2;
            } else {
                throw new RuntimeException("Token#subtractRanges(): Internal Error: [" + this.ranges[src] + "," + this.ranges[src + 1] + "] - [" + tok.ranges[sub] + "," + tok.ranges[sub + 1] + "]");
            }
        }
        while (src < this.ranges.length) {
            int i5 = wp;
            int wp5 = wp + 1;
            int i6 = src;
            int src3 = src + 1;
            result[i5] = this.ranges[i6];
            wp = wp5 + 1;
            src = src3 + 1;
            result[wp5] = this.ranges[src3];
        }
        this.ranges = new int[wp];
        System.arraycopy(result, 0, this.ranges, 0, wp);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
    protected void intersectRanges(Token token) {
        RangeToken tok = (RangeToken) token;
        if (tok.ranges == null || this.ranges == null) {
            return;
        }
        this.icaseCache = null;
        sortRanges();
        compactRanges();
        tok.sortRanges();
        tok.compactRanges();
        int[] result = new int[this.ranges.length + tok.ranges.length];
        int wp = 0;
        int src1 = 0;
        int src2 = 0;
        while (src1 < this.ranges.length && src2 < tok.ranges.length) {
            int src1begin = this.ranges[src1];
            int src1end = this.ranges[src1 + 1];
            int src2begin = tok.ranges[src2];
            int src2end = tok.ranges[src2 + 1];
            if (src1end < src2begin) {
                src1 += 2;
            } else if (src1end >= src2begin && src1begin <= src2end) {
                if (src2begin <= src1begin && src1end <= src2end) {
                    int i = wp;
                    int wp2 = wp + 1;
                    result[i] = src1begin;
                    wp = wp2 + 1;
                    result[wp2] = src1end;
                    src1 += 2;
                } else if (src2begin <= src1begin) {
                    int i2 = wp;
                    int wp3 = wp + 1;
                    result[i2] = src1begin;
                    wp = wp3 + 1;
                    result[wp3] = src2end;
                    this.ranges[src1] = src2end + 1;
                    src2 += 2;
                } else if (src1end <= src2end) {
                    int i3 = wp;
                    int wp4 = wp + 1;
                    result[i3] = src2begin;
                    wp = wp4 + 1;
                    result[wp4] = src1end;
                    src1 += 2;
                } else {
                    int i4 = wp;
                    int wp5 = wp + 1;
                    result[i4] = src2begin;
                    wp = wp5 + 1;
                    result[wp5] = src2end;
                    this.ranges[src1] = src2end + 1;
                }
            } else if (src2end < src1begin) {
                src2 += 2;
            } else {
                throw new RuntimeException("Token#intersectRanges(): Internal Error: [" + this.ranges[src1] + "," + this.ranges[src1 + 1] + "] & [" + tok.ranges[src2] + "," + tok.ranges[src2 + 1] + "]");
            }
        }
        while (src1 < this.ranges.length) {
            int i5 = wp;
            int wp6 = wp + 1;
            int i6 = src1;
            int src3 = src1 + 1;
            result[i5] = this.ranges[i6];
            wp = wp6 + 1;
            src1 = src3 + 1;
            result[wp6] = this.ranges[src3];
        }
        this.ranges = new int[wp];
        System.arraycopy(result, 0, this.ranges, 0, wp);
    }

    static Token complementRanges(Token token) {
        if (token.type != 4 && token.type != 5) {
            throw new IllegalArgumentException("Token#complementRanges(): must be RANGE: " + token.type);
        }
        RangeToken tok = (RangeToken) token;
        tok.sortRanges();
        tok.compactRanges();
        int len = tok.ranges.length + 2;
        if (tok.ranges[0] == 0) {
            len -= 2;
        }
        int last = tok.ranges[tok.ranges.length - 1];
        if (last == 1114111) {
            len -= 2;
        }
        RangeToken ret = Token.createRange();
        ret.ranges = new int[len];
        int wp = 0;
        if (tok.ranges[0] > 0) {
            int wp2 = 0 + 1;
            ret.ranges[0] = 0;
            wp = wp2 + 1;
            ret.ranges[wp2] = tok.ranges[0] - 1;
        }
        for (int i = 1; i < tok.ranges.length - 2; i += 2) {
            int i2 = wp;
            int wp3 = wp + 1;
            ret.ranges[i2] = tok.ranges[i] + 1;
            wp = wp3 + 1;
            ret.ranges[wp3] = tok.ranges[i + 1] - 1;
        }
        if (last != 1114111) {
            ret.ranges[wp] = last + 1;
            ret.ranges[wp + 1] = 1114111;
        }
        ret.setCompacted();
        return ret;
    }

    synchronized RangeToken getCaseInsensitiveToken() {
        if (this.icaseCache != null) {
            return this.icaseCache;
        }
        RangeToken uppers = this.type == 4 ? Token.createRange() : Token.createNRange();
        for (int i = 0; i < this.ranges.length; i += 2) {
            for (int ch = this.ranges[i]; ch <= this.ranges[i + 1]; ch++) {
                if (ch > 65535) {
                    uppers.addRange(ch, ch);
                } else {
                    char uch = Character.toUpperCase((char) ch);
                    uppers.addRange(uch, uch);
                }
            }
        }
        RangeToken lowers = this.type == 4 ? Token.createRange() : Token.createNRange();
        for (int i2 = 0; i2 < uppers.ranges.length; i2 += 2) {
            for (int ch2 = uppers.ranges[i2]; ch2 <= uppers.ranges[i2 + 1]; ch2++) {
                if (ch2 > 65535) {
                    lowers.addRange(ch2, ch2);
                } else {
                    char uch2 = Character.toUpperCase((char) ch2);
                    lowers.addRange(uch2, uch2);
                }
            }
        }
        lowers.mergeRanges(uppers);
        lowers.mergeRanges(this);
        lowers.compactRanges();
        this.icaseCache = lowers;
        return lowers;
    }

    void dumpRanges() {
        System.err.print("RANGE: ");
        if (this.ranges != null) {
            for (int i = 0; i < this.ranges.length; i += 2) {
                System.err.print("[" + this.ranges[i] + "," + this.ranges[i + 1] + "] ");
            }
        } else {
            System.err.println(" NULL");
        }
        System.err.println("");
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
    boolean match(int ch) {
        boolean ret;
        if (this.map == null) {
            createMap();
        }
        if (this.type == 4) {
            if (ch < 256) {
                return (this.map[ch / 32] & (1 << (ch & 31))) != 0;
            }
            ret = false;
            for (int i = this.nonMapIndex; i < this.ranges.length; i += 2) {
                if (this.ranges[i] <= ch && ch <= this.ranges[i + 1]) {
                    return true;
                }
            }
        } else {
            if (ch < 256) {
                return (this.map[ch / 32] & (1 << (ch & 31))) == 0;
            }
            ret = true;
            for (int i2 = this.nonMapIndex; i2 < this.ranges.length; i2 += 2) {
                if (this.ranges[i2] <= ch && ch <= this.ranges[i2 + 1]) {
                    return false;
                }
            }
        }
        return ret;
    }

    private void createMap() {
        this.map = new int[8];
        this.nonMapIndex = this.ranges.length;
        for (int i = 0; i < 8; i++) {
            this.map[i] = 0;
        }
        for (int i2 = 0; i2 < this.ranges.length; i2 += 2) {
            int s = this.ranges[i2];
            int e = this.ranges[i2 + 1];
            if (s < 256) {
                for (int j = s; j <= e && j < 256; j++) {
                    int[] iArr = this.map;
                    int i3 = j / 32;
                    iArr[i3] = iArr[i3] | (1 << (j & 31));
                }
                if (e >= 256) {
                    this.nonMapIndex = i2;
                    return;
                }
            } else {
                this.nonMapIndex = i2;
                return;
            }
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
    public String toString(int options) {
        String ret;
        if (this.type == 4) {
            if (this == Token.token_dot) {
                ret = ".";
            } else if (this == Token.token_0to9) {
                ret = "\\d";
            } else if (this == Token.token_wordchars) {
                ret = "\\w";
            } else if (this == Token.token_spaces) {
                ret = "\\s";
            } else {
                StringBuffer sb = new StringBuffer();
                sb.append("[");
                for (int i = 0; i < this.ranges.length; i += 2) {
                    if ((options & 1024) != 0 && i > 0) {
                        sb.append(",");
                    }
                    if (this.ranges[i] == this.ranges[i + 1]) {
                        sb.append(escapeCharInCharClass(this.ranges[i]));
                    } else {
                        sb.append(escapeCharInCharClass(this.ranges[i]));
                        sb.append('-');
                        sb.append(escapeCharInCharClass(this.ranges[i + 1]));
                    }
                }
                sb.append("]");
                ret = sb.toString();
            }
        } else if (this == Token.token_not_0to9) {
            ret = "\\D";
        } else if (this == Token.token_not_wordchars) {
            ret = "\\W";
        } else if (this == Token.token_not_spaces) {
            ret = "\\S";
        } else {
            StringBuffer sb2 = new StringBuffer();
            sb2.append("[^");
            for (int i2 = 0; i2 < this.ranges.length; i2 += 2) {
                if ((options & 1024) != 0 && i2 > 0) {
                    sb2.append(",");
                }
                if (this.ranges[i2] == this.ranges[i2 + 1]) {
                    sb2.append(escapeCharInCharClass(this.ranges[i2]));
                } else {
                    sb2.append(escapeCharInCharClass(this.ranges[i2]));
                    sb2.append('-');
                    sb2.append(escapeCharInCharClass(this.ranges[i2 + 1]));
                }
            }
            sb2.append("]");
            ret = sb2.toString();
        }
        return ret;
    }

    private static String escapeCharInCharClass(int ch) {
        String ret;
        switch (ch) {
            case 9:
                ret = "\\t";
                break;
            case 10:
                ret = "\\n";
                break;
            case 12:
                ret = "\\f";
                break;
            case 13:
                ret = "\\r";
                break;
            case 27:
                ret = "\\e";
                break;
            case 44:
            case 45:
            case 91:
            case 92:
            case 93:
            case 94:
                ret = "\\" + ((char) ch);
                break;
            default:
                if (ch < 32) {
                    String pre = "0" + Integer.toHexString(ch);
                    ret = "\\x" + pre.substring(pre.length() - 2, pre.length());
                } else if (ch >= 65536) {
                    String pre2 = "0" + Integer.toHexString(ch);
                    ret = "\\v" + pre2.substring(pre2.length() - 6, pre2.length());
                } else {
                    ret = "" + ((char) ch);
                }
                break;
        }
        return ret;
    }
}
