package com.aowagie.text.pdf.hyphenation;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Stack;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/hyphenation/TernaryTree.class */
class TernaryTree implements Cloneable, Serializable {
    private static final long serialVersionUID = 5313366505322983510L;
    protected char[] lo;
    protected char[] hi;
    protected char[] eq;
    protected char[] sc;
    protected CharVector kv;
    protected char root;
    private char freenode;
    private int length;
    private static final int BLOCK_SIZE = 2048;

    TernaryTree() {
        init();
    }

    private void init() {
        this.root = (char) 0;
        this.freenode = (char) 1;
        this.length = 0;
        this.lo = new char[2048];
        this.hi = new char[2048];
        this.eq = new char[2048];
        this.sc = new char[2048];
        this.kv = new CharVector();
    }

    void insert(String key, char val) {
        int len = key.length() + 1;
        if (this.freenode + len > this.eq.length) {
            redimNodeArrays(this.eq.length + 2048);
        }
        int len2 = len - 1;
        char[] strkey = new char[len];
        key.getChars(0, len2, strkey, 0);
        strkey[len2] = 0;
        this.root = insert(this.root, strkey, 0, val);
    }

    void insert(char[] key, int start, char val) {
        int len = strlen(key) + 1;
        if (this.freenode + len > this.eq.length) {
            redimNodeArrays(this.eq.length + 2048);
        }
        this.root = insert(this.root, key, start, val);
    }

    private char insert(char p, char[] key, int start, char val) {
        int len = strlen(key, start);
        if (p == 0) {
            char p2 = this.freenode;
            this.freenode = (char) (p2 + 1);
            this.eq[p2] = val;
            this.length++;
            this.hi[p2] = 0;
            if (len > 0) {
                this.sc[p2] = 65535;
                this.lo[p2] = (char) this.kv.alloc(len + 1);
                strcpy(this.kv.getArray(), this.lo[p2], key, start);
            } else {
                this.sc[p2] = 0;
                this.lo[p2] = 0;
            }
            return p2;
        }
        if (this.sc[p] == 65535) {
            char pp = this.freenode;
            this.freenode = (char) (pp + 1);
            this.lo[pp] = this.lo[p];
            this.eq[pp] = this.eq[p];
            this.lo[p] = 0;
            if (len > 0) {
                this.sc[p] = this.kv.get(this.lo[pp]);
                this.eq[p] = pp;
                char[] cArr = this.lo;
                cArr[pp] = (char) (cArr[pp] + 1);
                if (this.kv.get(this.lo[pp]) == 0) {
                    this.lo[pp] = 0;
                    this.sc[pp] = 0;
                    this.hi[pp] = 0;
                } else {
                    this.sc[pp] = 65535;
                }
            } else {
                this.sc[pp] = 65535;
                this.hi[p] = pp;
                this.sc[p] = 0;
                this.eq[p] = val;
                this.length++;
                return p;
            }
        }
        char s = key[start];
        if (s < this.sc[p]) {
            this.lo[p] = insert(this.lo[p], key, start, val);
        } else if (s == this.sc[p]) {
            if (s != 0) {
                this.eq[p] = insert(this.eq[p], key, start + 1, val);
            } else {
                this.eq[p] = val;
            }
        } else {
            this.hi[p] = insert(this.hi[p], key, start, val);
        }
        return p;
    }

    private static int strcmp(char[] a, int startA, char[] b, int startB) {
        while (a[startA] == b[startB]) {
            if (a[startA] != 0) {
                startA++;
                startB++;
            } else {
                return 0;
            }
        }
        return a[startA] - b[startB];
    }

    private static void strcpy(char[] dst, int di, char[] src, int si) {
        while (src[si] != 0) {
            int i = di;
            di++;
            int i2 = si;
            si++;
            dst[i] = src[i2];
        }
        dst[di] = 0;
    }

    private static int strlen(char[] a, int start) {
        int len = 0;
        for (int i = start; i < a.length && a[i] != 0; i++) {
            len++;
        }
        return len;
    }

    private static int strlen(char[] a) {
        return strlen(a, 0);
    }

    int find(String key) {
        int len = key.length();
        char[] strkey = new char[len + 1];
        key.getChars(0, len, strkey, 0);
        strkey[len] = 0;
        return find(strkey, 0);
    }

    int find(char[] key, int start) {
        char p = this.root;
        int i = start;
        while (p != 0) {
            if (this.sc[p] == 65535) {
                if (strcmp(key, i, this.kv.getArray(), this.lo[p]) == 0) {
                    return this.eq[p];
                }
                return -1;
            }
            char c = key[i];
            int d = c - this.sc[p];
            if (d == 0) {
                if (c == 0) {
                    return this.eq[p];
                }
                i++;
                p = this.eq[p];
            } else if (d < 0) {
                p = this.lo[p];
            } else {
                p = this.hi[p];
            }
        }
        return -1;
    }

    private void redimNodeArrays(int newsize) {
        int len = newsize < this.lo.length ? newsize : this.lo.length;
        char[] na = new char[newsize];
        System.arraycopy(this.lo, 0, na, 0, len);
        this.lo = na;
        char[] na2 = new char[newsize];
        System.arraycopy(this.hi, 0, na2, 0, len);
        this.hi = na2;
        char[] na3 = new char[newsize];
        System.arraycopy(this.eq, 0, na3, 0, len);
        this.eq = na3;
        char[] na4 = new char[newsize];
        System.arraycopy(this.sc, 0, na4, 0, len);
        this.sc = na4;
    }

    public Object clone() {
        TernaryTree t = new TernaryTree();
        t.lo = (char[]) this.lo.clone();
        t.hi = (char[]) this.hi.clone();
        t.eq = (char[]) this.eq.clone();
        t.sc = (char[]) this.sc.clone();
        t.kv = (CharVector) this.kv.clone();
        t.root = this.root;
        t.freenode = this.freenode;
        t.length = this.length;
        return t;
    }

    private void insertBalanced(String[] k, char[] v, int offset, int n) {
        if (n < 1) {
            return;
        }
        int m = n >> 1;
        insert(k[m + offset], v[m + offset]);
        insertBalanced(k, v, offset, m);
        insertBalanced(k, v, offset + m + 1, (n - m) - 1);
    }

    private void balance() {
        int i = 0;
        int n = this.length;
        String[] k = new String[n];
        char[] v = new char[n];
        Iterator iter = new Iterator();
        while (iter.hasMoreElements()) {
            v[i] = iter.getValue();
            int i2 = i;
            i++;
            k[i2] = (String) iter.nextElement();
        }
        init();
        insertBalanced(k, v, 0, n);
    }

    void trimToSize() {
        balance();
        redimNodeArrays(this.freenode);
        CharVector kx = new CharVector();
        kx.alloc(1);
        TernaryTree map = new TernaryTree();
        compact(kx, map, this.root);
        this.kv = kx;
        this.kv.trimToSize();
    }

    private void compact(CharVector kx, TernaryTree map, char p) {
        if (p == 0) {
            return;
        }
        if (this.sc[p] == 65535) {
            int k = map.find(this.kv.getArray(), this.lo[p]);
            if (k < 0) {
                k = kx.alloc(strlen(this.kv.getArray(), this.lo[p]) + 1);
                strcpy(kx.getArray(), k, this.kv.getArray(), this.lo[p]);
                map.insert(kx.getArray(), k, (char) k);
            }
            this.lo[p] = (char) k;
            return;
        }
        compact(kx, map, this.lo[p]);
        if (this.sc[p] != 0) {
            compact(kx, map, this.eq[p]);
        }
        compact(kx, map, this.hi[p]);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/hyphenation/TernaryTree$Iterator.class */
    private class Iterator implements Enumeration {
        private String curkey;
        private int cur = -1;
        private final Stack ns = new Stack();
        private final StringBuffer ks = new StringBuffer();

        /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/hyphenation/TernaryTree$Iterator$Item.class */
        private class Item implements Cloneable {
            char parent;
            char child;

            public Item() {
                this.parent = (char) 0;
                this.child = (char) 0;
            }

            public Item(char p, char c) {
                this.parent = p;
                this.child = c;
            }

            public Object clone() {
                return Iterator.this.new Item(this.parent, this.child);
            }
        }

        public Iterator() {
            rewind();
        }

        private void rewind() {
            this.ns.removeAllElements();
            this.ks.setLength(0);
            this.cur = TernaryTree.this.root;
            run();
        }

        @Override // java.util.Enumeration
        public Object nextElement() {
            String res = this.curkey;
            this.cur = up();
            run();
            return res;
        }

        public char getValue() {
            if (this.cur >= 0) {
                return TernaryTree.this.eq[this.cur];
            }
            return (char) 0;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return this.cur != -1;
        }

        private int up() {
            new Item();
            int res = 0;
            if (this.ns.empty()) {
                return -1;
            }
            if (this.cur != 0 && TernaryTree.this.sc[this.cur] == 0) {
                return TernaryTree.this.lo[this.cur];
            }
            boolean z = true;
            while (climb) {
                Item i = (Item) this.ns.pop();
                i.child = (char) (i.child + 1);
                switch (i.child) {
                    case 1:
                        if (TernaryTree.this.sc[i.parent] != 0) {
                            res = TernaryTree.this.eq[i.parent];
                            this.ns.push(i.clone());
                            this.ks.append(TernaryTree.this.sc[i.parent]);
                        } else {
                            i.child = (char) (i.child + 1);
                            this.ns.push(i.clone());
                            res = TernaryTree.this.hi[i.parent];
                        }
                        z = false;
                        break;
                    case 2:
                        res = TernaryTree.this.hi[i.parent];
                        this.ns.push(i.clone());
                        if (this.ks.length() > 0) {
                            this.ks.setLength(this.ks.length() - 1);
                        }
                        z = false;
                        break;
                    default:
                        if (this.ns.empty()) {
                            return -1;
                        }
                        z = true;
                        break;
                        break;
                }
            }
            return res;
        }

        private int run() {
            if (this.cur == -1) {
                return -1;
            }
            boolean leaf = false;
            while (true) {
                if (this.cur != 0) {
                    if (TernaryTree.this.sc[this.cur] == 65535) {
                        leaf = true;
                    } else {
                        this.ns.push(new Item((char) this.cur, (char) 0));
                        if (TernaryTree.this.sc[this.cur] == 0) {
                            leaf = true;
                        } else {
                            this.cur = TernaryTree.this.lo[this.cur];
                        }
                    }
                }
                if (!leaf) {
                    this.cur = up();
                    if (this.cur == -1) {
                        return -1;
                    }
                } else {
                    StringBuffer buf = new StringBuffer(this.ks.toString());
                    if (TernaryTree.this.sc[this.cur] == 65535) {
                        int p = TernaryTree.this.lo[this.cur];
                        while (TernaryTree.this.kv.get(p) != 0) {
                            int i = p;
                            p++;
                            buf.append(TernaryTree.this.kv.get(i));
                        }
                    }
                    this.curkey = buf.toString();
                    return 0;
                }
            }
        }
    }

    public void printStats() {
        System.out.println("Number of keys = " + Integer.toString(this.length));
        System.out.println("Node count = " + Integer.toString(this.freenode));
        System.out.println("Key Array length = " + Integer.toString(this.kv.length()));
    }
}
