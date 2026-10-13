package org.bouncycastle.asn1;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/OIDTokenizer.class */
public class OIDTokenizer {
    private String oid;
    private int index = 0;

    public OIDTokenizer(String str) {
        this.oid = str;
    }

    public boolean hasMoreTokens() {
        return this.index != -1;
    }

    public String nextToken() {
        if (this.index == -1) {
            return null;
        }
        int iIndexOf = this.oid.indexOf(46, this.index);
        if (iIndexOf == -1) {
            String strSubstring = this.oid.substring(this.index);
            this.index = -1;
            return strSubstring;
        }
        String strSubstring2 = this.oid.substring(this.index, iIndexOf);
        this.index = iIndexOf + 1;
        return strSubstring2;
    }
}
