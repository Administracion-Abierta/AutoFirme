package org.spongycastle.jcajce.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import org.spongycastle.crypto.io.InvalidCipherTextIOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/io/CipherInputStream.class */
public class CipherInputStream extends FilterInputStream {
    private final Cipher cipher;
    private final byte[] inputBuffer;
    private boolean finalized;
    private byte[] buf;
    private int maxBuf;
    private int bufOff;

    public CipherInputStream(InputStream input, Cipher cipher) {
        super(input);
        this.inputBuffer = new byte[512];
        this.finalized = false;
        this.cipher = cipher;
    }

    private int nextChunk() throws IOException {
        if (this.finalized) {
            return -1;
        }
        this.bufOff = 0;
        this.maxBuf = 0;
        while (this.maxBuf == 0) {
            int read = this.in.read(this.inputBuffer);
            if (read == -1) {
                this.buf = finaliseCipher();
                if (this.buf == null || this.buf.length == 0) {
                    return -1;
                }
                this.maxBuf = this.buf.length;
                return this.maxBuf;
            }
            this.buf = this.cipher.update(this.inputBuffer, 0, read);
            if (this.buf != null) {
                this.maxBuf = this.buf.length;
            }
        }
        return this.maxBuf;
    }

    private byte[] finaliseCipher() throws InvalidCipherTextIOException {
        try {
            this.finalized = true;
            return this.cipher.doFinal();
        } catch (GeneralSecurityException e) {
            throw new InvalidCipherTextIOException("Error finalising cipher", e);
        }
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        if (this.bufOff >= this.maxBuf && nextChunk() < 0) {
            return -1;
        }
        byte[] bArr = this.buf;
        int i = this.bufOff;
        this.bufOff = i + 1;
        return bArr[i] & 255;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.bufOff >= this.maxBuf && nextChunk() < 0) {
            return -1;
        }
        int toSupply = Math.min(len, available());
        System.arraycopy(this.buf, this.bufOff, b, off, toSupply);
        this.bufOff += toSupply;
        return toSupply;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long n) throws IOException {
        if (n <= 0) {
            return 0L;
        }
        int skip = (int) Math.min(n, available());
        this.bufOff += skip;
        return skip;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int available() throws IOException {
        return this.maxBuf - this.bufOff;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        try {
            this.in.close();
            if (!this.finalized) {
                finaliseCipher();
            }
            this.bufOff = 0;
            this.maxBuf = 0;
        } catch (Throwable th) {
            if (!this.finalized) {
                finaliseCipher();
            }
            throw th;
        }
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public void mark(int readlimit) {
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public void reset() throws IOException {
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public boolean markSupported() {
        return false;
    }
}
