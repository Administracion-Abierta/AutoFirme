package org.bouncycastle.est;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/CTEChunkedInputStream.class */
public class CTEChunkedInputStream extends InputStream {
    private InputStream src;
    int chunkLen = 0;

    public CTEChunkedInputStream(InputStream inputStream) {
        this.src = inputStream;
    }

    private String readEOL() throws IOException {
        int i;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        do {
            i = this.src.read();
            if (i == -1) {
                if (byteArrayOutputStream.size() == 0) {
                    return null;
                }
                return byteArrayOutputStream.toString().trim();
            }
            byteArrayOutputStream.write(i & 255);
        } while (i != 10);
        return byteArrayOutputStream.toString().trim();
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        String eol;
        if (this.chunkLen == Integer.MIN_VALUE) {
            return -1;
        }
        if (this.chunkLen == 0) {
            do {
                eol = readEOL();
                if (eol == null) {
                    break;
                }
            } while (eol.length() == 0);
            if (eol == null) {
                return -1;
            }
            this.chunkLen = Integer.parseInt(eol.trim(), 16);
            if (this.chunkLen == 0) {
                readEOL();
                this.chunkLen = Integer.MIN_VALUE;
                return -1;
            }
        }
        int i = this.src.read();
        this.chunkLen--;
        return i;
    }
}
