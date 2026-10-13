package com.sun.java.help.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/* JADX INFO: compiled from: DocumentParser.java */
/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/impl/MyBufferedReader.class */
class MyBufferedReader extends BufferedReader {
    public MyBufferedReader(Reader reader, int i) {
        super(reader, i);
    }

    public MyBufferedReader(Reader reader) {
        super(reader);
    }

    @Override // java.io.BufferedReader, java.io.Reader
    public int read(char[] cArr, int i, int i2) throws IOException {
        if (this.lock == null) {
            return -1;
        }
        return super.read(cArr, i, i2);
    }
}
