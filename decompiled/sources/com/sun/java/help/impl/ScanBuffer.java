package com.sun.java.help.impl;

/* JADX INFO: compiled from: DocumentParser.java */
/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/ScanBuffer.class */
class ScanBuffer {
    char[] buf;
    int buflen;
    int scale;

    ScanBuffer() {
        this.scale = 2;
        this.buf = new char[256];
    }

    ScanBuffer(int i, int i2) {
        this.scale = 2;
        this.buf = new char[i];
        this.scale = i2;
    }

    protected void clear() {
        this.buflen = 0;
    }

    protected void reset(int i) {
        this.buflen = i;
    }

    protected void flush(DocumentParser documentParser) {
        if (this.buflen > 0) {
            documentParser.callFlush(this.buf, 0, this.buflen);
            this.buflen = 0;
        }
    }

    protected void add(char c) {
        if (this.buflen >= this.buf.length) {
            char[] cArr = new char[this.buf.length * this.scale];
            System.arraycopy(this.buf, 0, cArr, 0, this.buf.length);
            this.buf = cArr;
        }
        char[] cArr2 = this.buf;
        int i = this.buflen;
        this.buflen = i + 1;
        cArr2[i] = c;
    }

    protected int length() {
        return this.buflen;
    }

    public String toString() {
        return new StringBuffer().append("ScanBuffer, buf = ").append((Object) this.buf).append(", buflen = ").append(this.buflen).toString();
    }

    protected String extract(int i) {
        return new String(this.buf, i, this.buflen - i);
    }
}
