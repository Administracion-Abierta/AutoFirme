package com.sun.java.help.search;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.Permission;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/TemporaryRAFFile.class */
public final class TemporaryRAFFile extends RAFFile {
    private RandomAccessFile raf;
    private Permission permission;
    private static final boolean debug = false;

    public TemporaryRAFFile(File file, Permission permission) throws IOException {
        debug(new StringBuffer().append("TemporaryRAFFile ").append(file).toString());
        this.raf = new RandomAccessFile(file, PDPageLabelRange.STYLE_ROMAN_LOWER);
        this.permission = permission;
    }

    @Override // com.sun.java.help.search.RAFFile
    public long length() throws IOException {
        return this.raf.length();
    }

    @Override // com.sun.java.help.search.RAFFile
    public long getFilePointer() throws IOException {
        return this.raf.getFilePointer();
    }

    @Override // com.sun.java.help.search.RAFFile
    public void close() throws IOException {
        this.raf.close();
    }

    @Override // com.sun.java.help.search.RAFFile
    public void seek(long j) throws IOException {
        this.raf.seek(j);
    }

    @Override // com.sun.java.help.search.RAFFile
    public int readInt() throws IOException {
        return this.raf.readInt();
    }

    @Override // com.sun.java.help.search.RAFFile
    public int read() throws IOException {
        return this.raf.read();
    }

    @Override // com.sun.java.help.search.RAFFile
    public void readFully(byte[] bArr) throws IOException {
        this.raf.readFully(bArr);
    }

    @Override // com.sun.java.help.search.RAFFile
    public int read(byte[] bArr, int i, int i2) throws IOException {
        return this.raf.read(bArr, i, i2);
    }

    @Override // com.sun.java.help.search.RAFFile
    public void writeInt(int i) throws IOException {
        this.raf.writeInt(i);
    }

    @Override // com.sun.java.help.search.RAFFile
    public void write(byte[] bArr) throws IOException {
        this.raf.write(bArr);
    }

    private static void debug(String str) {
    }
}
