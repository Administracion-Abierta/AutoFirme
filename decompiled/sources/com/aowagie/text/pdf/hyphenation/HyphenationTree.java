package com.aowagie.text.pdf.hyphenation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/hyphenation/HyphenationTree.class */
class HyphenationTree extends TernaryTree implements PatternConsumer {
    private static final long serialVersionUID = -7763254239309429432L;
    private transient TernaryTree ivalues;
    private final HashMap stoplist = new LinkedHashMap(23);
    private final TernaryTree classmap = new TernaryTree();
    private final ByteVector vspace = new ByteVector();

    public HyphenationTree() {
        this.vspace.alloc(1);
    }

    private int packValues(String values) {
        int n = values.length();
        int m = (n & 1) == 1 ? (n >> 1) + 2 : (n >> 1) + 1;
        int offset = this.vspace.alloc(m);
        byte[] va = this.vspace.getArray();
        for (int i = 0; i < n; i++) {
            int j = i >> 1;
            byte v = (byte) (((values.charAt(i) - '0') + 1) & 15);
            if ((i & 1) == 1) {
                va[j + offset] = (byte) (va[j + offset] | v);
            } else {
                va[j + offset] = (byte) (v << 4);
            }
        }
        va[(m - 1) + offset] = 0;
        return offset;
    }

    void loadSimplePatterns(InputStream stream) {
        SimplePatternParser pp = new SimplePatternParser();
        this.ivalues = new TernaryTree();
        pp.parse(stream, this);
        trimToSize();
        this.vspace.trimToSize();
        this.classmap.trimToSize();
        this.ivalues = null;
    }

    private int hstrcmp(char[] s, int si, char[] t, int ti) {
        while (s[si] == t[ti]) {
            if (s[si] != 0) {
                si++;
                ti++;
            } else {
                return 0;
            }
        }
        if (t[ti] == 0) {
            return 0;
        }
        return s[si] - t[ti];
    }

    private byte[] getValues(int k) {
        StringBuffer buf = new StringBuffer();
        int k2 = k + 1;
        byte b = this.vspace.get(k);
        while (true) {
            byte v = b;
            if (v == 0) {
                break;
            }
            buf.append((char) ((v >>> 4) - 1));
            char c = (char) (v & 15);
            if (c == 0) {
                break;
            }
            buf.append((char) (c - 1));
            int i = k2;
            k2++;
            b = this.vspace.get(i);
        }
        byte[] res = new byte[buf.length()];
        for (int i2 = 0; i2 < res.length; i2++) {
            res[i2] = (byte) buf.charAt(i2);
        }
        return res;
    }

    private void searchPatterns(char[] word, int index, byte[] il) {
        int i = index;
        char sp = word[i];
        char p = this.root;
        while (p > 0 && p < this.sc.length) {
            if (this.sc[p] == 65535) {
                if (hstrcmp(word, i, this.kv.getArray(), this.lo[p]) == 0) {
                    byte[] values = getValues(this.eq[p]);
                    int j = index;
                    for (byte value : values) {
                        if (j < il.length && value > il[j]) {
                            il[j] = value;
                        }
                        j++;
                    }
                    return;
                }
                return;
            }
            int d = sp - this.sc[p];
            if (d == 0) {
                if (sp != 0) {
                    i++;
                    sp = word[i];
                    p = this.eq[p];
                    char c = p;
                    while (true) {
                        char q = c;
                        if (q <= 0 || q >= this.sc.length || this.sc[q] == 65535) {
                            break;
                        }
                        if (this.sc[q] == 0) {
                            byte[] values2 = getValues(this.eq[q]);
                            int j2 = index;
                            for (byte value2 : values2) {
                                if (j2 < il.length && value2 > il[j2]) {
                                    il[j2] = value2;
                                }
                                j2++;
                            }
                            break;
                        }
                        c = this.lo[q];
                    }
                } else {
                    return;
                }
            } else {
                p = d < 0 ? this.lo[p] : this.hi[p];
            }
        }
    }

    Hyphenation hyphenate(String word, int remainCharCount, int pushCharCount) {
        char[] w = word.toCharArray();
        return hyphenate(w, 0, w.length, remainCharCount, pushCharCount);
    }

    private Hyphenation hyphenate(char[] w, int offset, int len, int remainCharCount, int pushCharCount) {
        char[] word = new char[len + 3];
        char[] c = new char[2];
        int iIgnoreAtBeginning = 0;
        int iLength = len;
        boolean bEndOfLetters = false;
        for (int i = 1; i <= len; i++) {
            c[0] = w[(offset + i) - 1];
            int nc = this.classmap.find(c, 0);
            if (nc < 0) {
                if (i == 1 + iIgnoreAtBeginning) {
                    iIgnoreAtBeginning++;
                } else {
                    bEndOfLetters = true;
                }
                iLength--;
            } else if (!bEndOfLetters) {
                word[i - iIgnoreAtBeginning] = (char) nc;
            } else {
                return null;
            }
        }
        int len2 = iLength;
        if (len2 < remainCharCount + pushCharCount) {
            return null;
        }
        int[] result = new int[len2 + 1];
        int k = 0;
        String sw = new String(word, 1, len2);
        if (this.stoplist.containsKey(sw)) {
            ArrayList hw = (ArrayList) this.stoplist.get(sw);
            int j = 0;
            for (int i2 = 0; i2 < hw.size(); i2++) {
                Object o = hw.get(i2);
                if (o instanceof String) {
                    j += ((String) o).length();
                    if (j >= remainCharCount && j < len2 - pushCharCount) {
                        int i3 = k;
                        k++;
                        result[i3] = j + iIgnoreAtBeginning;
                    }
                }
            }
        } else {
            word[0] = '.';
            word[len2 + 1] = '.';
            word[len2 + 2] = 0;
            byte[] il = new byte[len2 + 3];
            for (int i4 = 0; i4 < len2 + 1; i4++) {
                searchPatterns(word, i4, il);
            }
            for (int i5 = 0; i5 < len2; i5++) {
                if ((il[i5 + 1] & 1) == 1 && i5 >= remainCharCount && i5 <= len2 - pushCharCount) {
                    int i6 = k;
                    k++;
                    result[i6] = i5 + iIgnoreAtBeginning;
                }
            }
        }
        if (k > 0) {
            int[] res = new int[k];
            System.arraycopy(result, 0, res, 0, k);
            return new Hyphenation(new String(w, offset, len2), res);
        }
        return null;
    }

    @Override // com.aowagie.text.pdf.hyphenation.PatternConsumer
    public void addClass(String chargroup) {
        if (chargroup.length() > 0) {
            char equivChar = chargroup.charAt(0);
            char[] key = new char[2];
            key[1] = 0;
            for (int i = 0; i < chargroup.length(); i++) {
                key[0] = chargroup.charAt(i);
                this.classmap.insert(key, 0, equivChar);
            }
        }
    }

    @Override // com.aowagie.text.pdf.hyphenation.PatternConsumer
    public void addException(String word, ArrayList hyphenatedword) {
        this.stoplist.put(word, hyphenatedword);
    }

    @Override // com.aowagie.text.pdf.hyphenation.PatternConsumer
    public void addPattern(String pattern, String ivalue) {
        int k = this.ivalues.find(ivalue);
        if (k <= 0) {
            k = packValues(ivalue);
            this.ivalues.insert(ivalue, (char) k);
        }
        insert(pattern, (char) k);
    }

    @Override // com.aowagie.text.pdf.hyphenation.TernaryTree
    public void printStats() {
        System.out.println("Value space size = " + Integer.toString(this.vspace.length()));
        super.printStats();
    }
}
