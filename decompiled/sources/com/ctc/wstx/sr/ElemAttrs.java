package com.ctc.wstx.sr;

import javax.xml.namespace.QName;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sr/ElemAttrs.class */
public final class ElemAttrs {
    private static final int OFFSET_NS_URI = 1;
    private final String[] mRawAttrs;
    private final int mDefaultOffset;
    private final int[] mAttrMap;
    private final int mAttrHashSize;
    private final int mAttrSpillEnd;

    public ElemAttrs(String[] rawAttrs, int defOffset) {
        this.mRawAttrs = rawAttrs;
        this.mAttrMap = null;
        this.mAttrHashSize = 0;
        this.mAttrSpillEnd = 0;
        this.mDefaultOffset = defOffset << 2;
    }

    public ElemAttrs(String[] rawAttrs, int defOffset, int[] attrMap, int hashSize, int spillEnd) {
        this.mRawAttrs = rawAttrs;
        this.mDefaultOffset = defOffset << 2;
        this.mAttrMap = attrMap;
        this.mAttrHashSize = hashSize;
        this.mAttrSpillEnd = spillEnd;
    }

    public String[] getRawAttrs() {
        return this.mRawAttrs;
    }

    public int findIndex(QName name) {
        if (this.mAttrMap != null) {
            return findMapIndex(name.getNamespaceURI(), name.getLocalPart());
        }
        String ln = name.getLocalPart();
        String uri = name.getNamespaceURI();
        boolean defaultNs = uri == null || uri.length() == 0;
        String[] raw = this.mRawAttrs;
        int len = raw.length;
        for (int i = 0; i < len; i += 4) {
            if (ln.equals(raw[i])) {
                String thisUri = raw[i + 1];
                if (defaultNs) {
                    if (thisUri == null || thisUri.length() == 0) {
                        return i;
                    }
                } else if (thisUri != null && (thisUri == uri || thisUri.equals(uri))) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int getFirstDefaultOffset() {
        return this.mDefaultOffset;
    }

    public boolean isDefault(int ix) {
        return ix >= this.mDefaultOffset;
    }

    private final int findMapIndex(String nsURI, String localName) {
        int ix;
        String thisName;
        int hash = localName.hashCode();
        if (nsURI == null) {
            nsURI = "";
        } else if (nsURI.length() > 0) {
            hash ^= nsURI.hashCode();
        }
        int ix2 = this.mAttrMap[hash & (this.mAttrHashSize - 1)];
        if (ix2 == 0) {
            return -1;
        }
        int ix3 = (ix2 - 1) << 2;
        String[] raw = this.mRawAttrs;
        String thisName2 = raw[ix3];
        if (thisName2 == localName || thisName2.equals(localName)) {
            String thisURI = raw[ix3 + 1];
            if (thisURI == nsURI) {
                return ix3;
            }
            if (thisURI == null) {
                if (nsURI.length() == 0) {
                    return ix3;
                }
            } else if (thisURI.equals(nsURI)) {
                return ix3;
            }
        }
        int len = this.mAttrSpillEnd;
        for (int i = this.mAttrHashSize; i < len; i += 2) {
            if (this.mAttrMap[i] == hash && ((thisName = raw[(ix = this.mAttrMap[i + 1] << 2)]) == localName || thisName.equals(localName))) {
                String thisURI2 = raw[ix + 1];
                if (thisURI2 == nsURI) {
                    return ix;
                }
                if (thisURI2 == null) {
                    if (nsURI.length() == 0) {
                        return ix;
                    }
                } else if (thisURI2.equals(nsURI)) {
                    return ix;
                }
            }
        }
        return -1;
    }
}
