package com.ctc.wstx.util;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/util/BijectiveNsMap.class */
public final class BijectiveNsMap {
    static final int DEFAULT_ARRAY_SIZE = 32;
    private static final int MAX_LOOP_FOR_NEW_PREFIX = 999999;
    final int mScopeStart;
    String[] mNsStrings;
    int mScopeEnd;

    private BijectiveNsMap(int scopeStart, String[] strs) {
        this.mScopeEnd = scopeStart;
        this.mScopeStart = scopeStart;
        this.mNsStrings = strs;
    }

    public static BijectiveNsMap createEmpty() {
        String[] strs = new String[32];
        strs[0] = CanonicalizerBase.XML;
        strs[1] = "http://www.w3.org/XML/1998/namespace";
        strs[2] = CanonicalizerBase.XMLNS;
        strs[3] = "http://www.w3.org/2000/xmlns/";
        return new BijectiveNsMap(4, strs);
    }

    public BijectiveNsMap createChild() {
        return new BijectiveNsMap(this.mScopeEnd, this.mNsStrings);
    }

    public String findUriByPrefix(String prefix) {
        String[] strs = this.mNsStrings;
        int phash = prefix.hashCode();
        for (int ix = this.mScopeEnd - 2; ix >= 0; ix -= 2) {
            String thisP = strs[ix];
            if (thisP == prefix || (thisP.hashCode() == phash && thisP.equals(prefix))) {
                return strs[ix + 1];
            }
        }
        return null;
    }

    public String findPrefixByUri(String uri) {
        String[] strs = this.mNsStrings;
        int uhash = uri.hashCode();
        for (int ix = this.mScopeEnd - 1; ix > 0; ix -= 2) {
            String thisU = strs[ix];
            if (thisU == uri || (thisU.hashCode() == uhash && thisU.equals(uri))) {
                String prefix = strs[ix - 1];
                if (ix < this.mScopeStart) {
                    int phash = prefix.hashCode();
                    int j = ix + 1;
                    int end = this.mScopeEnd;
                    while (true) {
                        if (j < end) {
                            String thisP = strs[j];
                            if (thisP == prefix || (thisP.hashCode() == phash && thisP.equals(prefix))) {
                                break;
                            }
                            j += 2;
                        }
                    }
                }
                return prefix;
            }
        }
        return null;
    }

    /* JADX WARN: Code duplicated, block: B:26:0x0090  */
    public List<String> getPrefixesBoundToUri(String uri, List<String> l) {
        String[] strs = this.mNsStrings;
        int uhash = uri.hashCode();
        for (int ix = this.mScopeEnd - 1; ix > 0; ix -= 2) {
            String thisU = strs[ix];
            if (thisU == uri || (thisU.hashCode() == uhash && thisU.equals(uri))) {
                String prefix = strs[ix - 1];
                if (ix < this.mScopeStart) {
                    int phash = prefix.hashCode();
                    int j = ix + 1;
                    int end = this.mScopeEnd;
                    while (true) {
                        if (j < end) {
                            String thisP = strs[j];
                            if (thisP != prefix && (thisP.hashCode() != phash || !thisP.equals(prefix))) {
                                j += 2;
                            }
                        } else {
                            if (l == null) {
                                l = new ArrayList();
                            }
                            l.add(prefix);
                        }
                    }
                } else {
                    if (l == null) {
                        l = new ArrayList();
                    }
                    l.add(prefix);
                }
            }
        }
        return l;
    }

    public int size() {
        return this.mScopeEnd >> 1;
    }

    public int localSize() {
        return (this.mScopeEnd - this.mScopeStart) >> 1;
    }

    public String addMapping(String prefix, String uri) {
        String[] strs = this.mNsStrings;
        int phash = prefix.hashCode();
        int end = this.mScopeEnd;
        for (int ix = this.mScopeStart; ix < end; ix += 2) {
            String thisP = strs[ix];
            if (thisP == prefix || (thisP.hashCode() == phash && thisP.equals(prefix))) {
                String old = strs[ix + 1];
                strs[ix + 1] = uri;
                return old;
            }
        }
        if (this.mScopeEnd >= strs.length) {
            strs = DataUtil.growArrayBy(strs, strs.length);
            this.mNsStrings = strs;
        }
        int i = this.mScopeEnd;
        this.mScopeEnd = i + 1;
        strs[i] = prefix;
        int i2 = this.mScopeEnd;
        this.mScopeEnd = i2 + 1;
        strs[i2] = uri;
        return null;
    }

    public String addGeneratedMapping(String prefixBase, NamespaceContext ctxt, String uri, int[] seqArr) {
        String prefix;
        String existing;
        String[] strs = this.mNsStrings;
        int seqNr = seqArr[0];
        int attempts = 0;
        while (true) {
            prefix = (prefixBase + seqNr).intern();
            seqNr++;
            int phash = prefix.hashCode();
            int ix = this.mScopeEnd - 2;
            while (true) {
                if (ix >= 0) {
                    String thisP = strs[ix];
                    if (thisP == prefix || (thisP.hashCode() == phash && thisP.equals(prefix))) {
                        break;
                    }
                    ix -= 2;
                } else {
                    if (ctxt != null && (existing = ctxt.getNamespaceURI(prefix)) != null && !existing.isEmpty()) {
                        attempts++;
                        if (attempts <= MAX_LOOP_FOR_NEW_PREFIX) {
                            break;
                        }
                        throw new IllegalStateException("Internal error: failed to find a mapping prefix for URI '" + uri + " in " + MAX_LOOP_FOR_NEW_PREFIX + " attempts");
                    }
                    break;
                }
            }
        }
        seqArr[0] = seqNr;
        if (this.mScopeEnd >= strs.length) {
            strs = DataUtil.growArrayBy(strs, strs.length);
            this.mNsStrings = strs;
        }
        int i = this.mScopeEnd;
        this.mScopeEnd = i + 1;
        strs[i] = prefix;
        int i2 = this.mScopeEnd;
        this.mScopeEnd = i2 + 1;
        strs[i2] = uri;
        return prefix;
    }

    public String toString() {
        return "[" + getClass().toString() + "; " + size() + " entries; of which " + localSize() + " local]";
    }
}
