package org.bouncycastle.jcajce.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import org.bouncycastle.crypto.io.InvalidCipherTextIOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/io/CipherOutputStream.class */
public class CipherOutputStream extends FilterOutputStream {
    private final Cipher cipher;
    private final byte[] oneByte;

    public CipherOutputStream(OutputStream outputStream, Cipher cipher) {
        super(outputStream);
        this.oneByte = new byte[1];
        this.cipher = cipher;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int i) throws IOException {
        this.oneByte[0] = (byte) i;
        write(this.oneByte, 0, 1);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) throws IOException {
        byte[] bArrUpdate = this.cipher.update(bArr, i, i2);
        if (bArrUpdate != null) {
            this.out.write(bArrUpdate);
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        IOException iOException = null;
        try {
            byte[] bArrDoFinal = this.cipher.doFinal();
            if (bArrDoFinal != null) {
                this.out.write(bArrDoFinal);
            }
        } catch (GeneralSecurityException e) {
            iOException = new InvalidCipherTextIOException("Error during cipher finalisation", e);
        } catch (Exception e2) {
            iOException = new IOException("Error closing stream: " + e2);
        }
        try {
            flush();
            this.out.close();
        } catch (IOException e3) {
            if (iOException == null) {
                iOException = e3;
            }
        }
        if (iOException != null) {
            throw iOException;
        }
    }
}
