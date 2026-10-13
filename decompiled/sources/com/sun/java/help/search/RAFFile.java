package com.sun.java.help.search;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/RAFFile.class */
public class RAFFile {
    private RandomAccessFile raf;
    private static final boolean debug = false;

    protected RAFFile() {
    }

    public RAFFile(String str, boolean z) throws IOException {
        debug(new StringBuffer().append("RAFFile ").append(str).toString());
        this.raf = new RandomAccessFile(str, z ? "rw" : PDPageLabelRange.STYLE_ROMAN_LOWER);
    }

    public long length() throws IOException {
        return this.raf.length();
    }

    public long getFilePointer() throws IOException {
        return this.raf.getFilePointer();
    }

    public void close() throws IOException {
        this.raf.close();
    }

    public void seek(long j) throws IOException {
        this.raf.seek(j);
    }

    public int readInt() throws IOException {
        return this.raf.readInt();
    }

    public int read() throws IOException {
        return this.raf.read();
    }

    public void readFully(byte[] bArr) throws IOException {
        this.raf.readFully(bArr);
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        return this.raf.read(bArr, i, i2);
    }

    public void writeInt(int i) throws IOException {
        this.raf.writeInt(i);
    }

    public void write(byte[] bArr) throws IOException {
        this.raf.write(bArr);
    }

    private static void debug(String str) {
    }
}
