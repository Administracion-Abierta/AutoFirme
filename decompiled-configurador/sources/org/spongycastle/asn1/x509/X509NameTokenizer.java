package org.spongycastle.asn1.x509;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/x509/X509NameTokenizer.class */
public class X509NameTokenizer {
    private String value;
    private int index;
    private char separator;
    private StringBuffer buf;

    public X509NameTokenizer(String oid) {
        this(oid, ',');
    }

    public X509NameTokenizer(String oid, char separator) {
        this.buf = new StringBuffer();
        this.value = oid;
        this.index = -1;
        this.separator = separator;
    }

    public boolean hasMoreTokens() {
        return this.index != this.value.length();
    }

    public String nextToken() {
        if (this.index == this.value.length()) {
            return null;
        }
        int end = this.index + 1;
        boolean quoted = false;
        boolean escaped = false;
        this.buf.setLength(0);
        while (end != this.value.length()) {
            char c = this.value.charAt(end);
            if (c == '\"') {
                if (!escaped) {
                    quoted = !quoted;
                }
                this.buf.append(c);
                escaped = false;
            } else if (escaped || quoted) {
                this.buf.append(c);
                escaped = false;
            } else if (c == '\\') {
                this.buf.append(c);
                escaped = true;
            } else {
                if (c == this.separator) {
                    break;
                }
                this.buf.append(c);
            }
            end++;
        }
        this.index = end;
        return this.buf.toString();
    }
}
