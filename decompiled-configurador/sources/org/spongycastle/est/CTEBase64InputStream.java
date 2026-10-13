package org.spongycastle.est;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.spongycastle.util.encoders.Base64;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/est/CTEBase64InputStream.class */
class CTEBase64InputStream extends InputStream {
    protected final InputStream src;
    protected final byte[] rawBuf = new byte[1024];
    protected final byte[] data = new byte[768];
    protected final OutputStream dataOutputStream = new OutputStream() { // from class: org.spongycastle.est.CTEBase64InputStream.1
        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            byte[] bArr = CTEBase64InputStream.this.data;
            CTEBase64InputStream cTEBase64InputStream = CTEBase64InputStream.this;
            int i = cTEBase64InputStream.wp;
            cTEBase64InputStream.wp = i + 1;
            bArr[i] = (byte) b;
        }
    };
    protected final Long max;
    protected int rp;
    protected int wp;
    protected boolean end;
    protected long read;

    public CTEBase64InputStream(InputStream src, Long limit) {
        this.src = src;
        this.max = limit;
    }

    protected int pullFromSrc() throws IOException {
        int j;
        if (this.read >= this.max.longValue()) {
            return -1;
        }
        int c = 0;
        do {
            j = this.src.read();
            if (j >= 33 || j == 13 || j == 10) {
                if (c >= this.rawBuf.length) {
                    throw new IOException("Content Transfer Encoding, base64 line length > 1024");
                }
                int i = c;
                c++;
                this.rawBuf[i] = (byte) j;
                this.read++;
            } else if (j >= 0) {
                this.read++;
            }
            if (j <= -1 || c >= this.rawBuf.length || j == 10) {
                break;
            }
        } while (this.read < this.max.longValue());
        if (c > 0) {
            try {
                Base64.decode(this.rawBuf, 0, c, this.dataOutputStream);
            } catch (Exception ex) {
                throw new IOException("Decode Base64 Content-Transfer-Encoding: " + ex);
            }
        } else if (j == -1) {
            return -1;
        }
        return this.wp;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (this.rp == this.wp) {
            this.rp = 0;
            this.wp = 0;
            int i = pullFromSrc();
            if (i == -1) {
                return i;
            }
        }
        byte[] bArr = this.data;
        int i2 = this.rp;
        this.rp = i2 + 1;
        return bArr[i2] & 255;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.src.close();
    }
}
