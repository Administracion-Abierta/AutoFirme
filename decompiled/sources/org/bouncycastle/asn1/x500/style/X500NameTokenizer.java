package org.bouncycastle.asn1.x500.style;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x500/style/X500NameTokenizer.class */
public class X500NameTokenizer {
    private final String value;
    private final char separator;
    private int index;

    public X500NameTokenizer(String str) {
        this(str, ',');
    }

    public X500NameTokenizer(String str, char c) {
        if (str == null) {
            throw new NullPointerException();
        }
        if (c == '\"' || c == '\\') {
            throw new IllegalArgumentException("reserved separator character");
        }
        this.value = str;
        this.separator = c;
        this.index = str.length() < 1 ? 0 : -1;
    }

    public boolean hasMoreTokens() {
        return this.index < this.value.length();
    }

    public String nextToken() {
        if (this.index >= this.value.length()) {
            return null;
        }
        boolean z = false;
        boolean z2 = false;
        int i = this.index + 1;
        while (true) {
            int i2 = this.index + 1;
            this.index = i2;
            if (i2 >= this.value.length()) {
                if (z2 || z) {
                    throw new IllegalArgumentException("badly formatted directory string");
                }
                return this.value.substring(i, this.index);
            }
            char cCharAt = this.value.charAt(this.index);
            if (z2) {
                z2 = false;
            } else if (cCharAt == '\"') {
                z = !z;
            } else if (z) {
                continue;
            } else if (cCharAt == '\\') {
                z2 = true;
            } else if (cCharAt == this.separator) {
                return this.value.substring(i, this.index);
            }
        }
    }
}
