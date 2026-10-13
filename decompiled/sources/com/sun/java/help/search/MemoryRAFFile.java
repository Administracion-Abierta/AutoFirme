package com.sun.java.help.search;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/MemoryRAFFile.class */
public class MemoryRAFFile extends RAFFile {
    private URL url;
    private byte[] data;
    private int size;
    private int filePointer;
    private static boolean debugFlag = false;

    public MemoryRAFFile(URLConnection uRLConnection) throws IOException {
        this.url = uRLConnection.getURL();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(uRLConnection.getInputStream());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[512];
        while (true) {
            int i = bufferedInputStream.read(bArr);
            if (i != -1) {
                byteArrayOutputStream.write(bArr, 0, i);
            } else {
                this.data = byteArrayOutputStream.toByteArray();
                this.size = byteArrayOutputStream.size();
                this.filePointer = 0;
                return;
            }
        }
    }

    @Override // com.sun.java.help.search.RAFFile
    public long length() {
        return this.size;
    }

    @Override // com.sun.java.help.search.RAFFile
    public void close() throws IOException {
        this.filePointer = 0;
        this.data = null;
        this.size = 0;
    }

    @Override // com.sun.java.help.search.RAFFile
    public long getFilePointer() throws IOException {
        return this.filePointer;
    }

    @Override // com.sun.java.help.search.RAFFile
    public void seek(long j) throws IOException {
        if (j > this.size) {
            throw new IOException();
        }
        this.filePointer = (int) j;
    }

    @Override // com.sun.java.help.search.RAFFile
    public int read() throws IOException {
        if (this.filePointer >= this.size) {
            return -1;
        }
        this.filePointer++;
        return this.data[this.filePointer - 1] & 255;
    }

    private int readBytes(byte[] bArr, int i, int i2) throws IOException {
        debug("readBytes");
        if (this.filePointer + i + i2 > this.size) {
            throw new IOException();
        }
        this.filePointer += i;
        System.arraycopy(this.data, this.filePointer, bArr, 0, i2);
        this.filePointer += i2;
        return i2;
    }

    @Override // com.sun.java.help.search.RAFFile
    public int read(byte[] bArr, int i, int i2) throws IOException {
        return readBytes(bArr, i, i2);
    }

    @Override // com.sun.java.help.search.RAFFile
    public int readInt() throws IOException {
        debug("readInt");
        int i = read();
        int i2 = read();
        int i3 = read();
        int i4 = read();
        if ((i | i2 | i3 | i4) < 0) {
            throw new EOFException();
        }
        return (i << 24) + (i2 << 16) + (i3 << 8) + (i4 << 0);
    }

    @Override // com.sun.java.help.search.RAFFile
    public final void readFully(byte[] bArr) throws IOException {
        readFully(bArr, 0, bArr.length);
    }

    private void readFully(byte[] bArr, int i, int i2) throws IOException {
        int i3 = 0;
        do {
            int i4 = read(bArr, i + i3, i2 - i3);
            if (i4 < 0) {
                throw new EOFException();
            }
            i3 += i4;
        } while (i3 < i2);
    }

    @Override // com.sun.java.help.search.RAFFile
    public void writeInt(int i) throws IOException {
        throw new IOException("Unsupported Operation");
    }

    @Override // com.sun.java.help.search.RAFFile
    public void write(byte[] bArr) throws IOException {
        throw new IOException("Unsupported Operation");
    }

    private static void debug(String str) {
        if (debugFlag) {
            System.out.println(new StringBuffer().append("MemoryRAFFile: ").append(str).toString());
        }
    }
}
