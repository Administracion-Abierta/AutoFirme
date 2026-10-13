package com.aowagie.text.pdf;

import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.pdf.crypto.AESCipher;
import com.aowagie.text.pdf.crypto.ARCFOUREncryption;
import com.aowagie.text.pdf.crypto.IVGenerator;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/OutputStreamEncryption.class */
class OutputStreamEncryption extends OutputStream {
    private OutputStream out;
    private ARCFOUREncryption arcfour;
    private AESCipher cipher;
    private final byte[] sb = new byte[1];
    private static final int AES_128 = 4;
    private static final int AES_256_V3 = 6;
    private boolean aes;
    private boolean finished;

    OutputStreamEncryption(OutputStream out, byte[] key, int off, int len, int revision) {
        try {
            this.out = out;
            this.aes = revision == 4 || revision == 6;
            if (this.aes) {
                byte[] iv = IVGenerator.getIV();
                byte[] nkey = new byte[len];
                System.arraycopy(key, off, nkey, 0, len);
                this.cipher = new AESCipher(true, nkey, iv);
                write(iv);
            } else {
                this.arcfour = new ARCFOUREncryption();
                this.arcfour.prepareARCFOURKey(key, off, len);
            }
        } catch (Exception ex) {
            throw new ExceptionConverter(ex);
        }
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        finish();
        this.out.close();
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override // java.io.OutputStream
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        this.sb[0] = (byte) b;
        write(this.sb, 0, 1);
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        if (this.aes) {
            byte[] b2 = this.cipher.update(b, off, len);
            if (b2 == null || b2.length == 0) {
                return;
            }
            this.out.write(b2, 0, b2.length);
            return;
        }
        byte[] b3 = new byte[Math.min(len, 4192)];
        while (len > 0) {
            int sz = Math.min(len, b3.length);
            this.arcfour.encryptARCFOUR(b, off, sz, b3, 0);
            this.out.write(b3, 0, sz);
            len -= sz;
            off += sz;
        }
    }

    void finish() throws IOException {
        if (!this.finished) {
            this.finished = true;
            if (this.aes) {
                try {
                    byte[] b = this.cipher.doFinal();
                    this.out.write(b, 0, b.length);
                } catch (Exception ex) {
                    throw new ExceptionConverter(ex);
                }
            }
        }
    }
}
