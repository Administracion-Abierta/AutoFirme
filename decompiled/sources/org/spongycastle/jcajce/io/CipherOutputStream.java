package org.spongycastle.jcajce.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import org.spongycastle.crypto.io.InvalidCipherTextIOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/io/CipherOutputStream.class */
public class CipherOutputStream extends FilterOutputStream {
    private final Cipher cipher;
    private final byte[] oneByte;

    public CipherOutputStream(OutputStream output, Cipher cipher) {
        super(output);
        this.oneByte = new byte[1];
        this.cipher = cipher;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) throws IOException {
        this.oneByte[0] = (byte) b;
        write(this.oneByte, 0, 1);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        byte[] outData = this.cipher.update(b, off, len);
        if (outData != null) {
            this.out.write(outData);
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        IOException error = null;
        try {
            byte[] outData = this.cipher.doFinal();
            if (outData != null) {
                this.out.write(outData);
            }
        } catch (GeneralSecurityException e) {
            error = new InvalidCipherTextIOException("Error during cipher finalisation", e);
        } catch (Exception e2) {
            error = new IOException("Error closing stream: " + e2);
        }
        try {
            flush();
            this.out.close();
        } catch (IOException e3) {
            if (error == null) {
                error = e3;
            }
        }
        if (error != null) {
            throw error;
        }
    }
}
