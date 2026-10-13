package org.bouncycastle.asn1.x509;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/X509NameTokenizer.class */
public class X509NameTokenizer {
    private String value;
    private int index;
    private char separator;
    private StringBuffer buf;

    public X509NameTokenizer(String str) {
        this(str, ',');
    }

    public X509NameTokenizer(String str, char c) {
        this.buf = new StringBuffer();
        this.value = str;
        this.index = -1;
        this.separator = c;
    }

    public boolean hasMoreTokens() {
        return this.index != this.value.length();
    }

    public String nextToken() {
        if (this.index == this.value.length()) {
            return null;
        }
        int i = this.index + 1;
        boolean z = false;
        boolean z2 = false;
        this.buf.setLength(0);
        while (i != this.value.length()) {
            char cCharAt = this.value.charAt(i);
            if (cCharAt == '\"') {
                if (!z2) {
                    z = !z;
                }
                this.buf.append(cCharAt);
                z2 = false;
            } else if (z2 || z) {
                this.buf.append(cCharAt);
                z2 = false;
            } else if (cCharAt == '\\') {
                this.buf.append(cCharAt);
                z2 = true;
            } else {
                if (cCharAt == this.separator) {
                    break;
                }
                this.buf.append(cCharAt);
            }
            i++;
        }
        this.index = i;
        return this.buf.toString();
    }
}
