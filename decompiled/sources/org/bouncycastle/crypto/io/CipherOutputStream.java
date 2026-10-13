package org.bouncycastle.crypto.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.modes.AEADBlockCipher;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/io/CipherOutputStream.class */
public class CipherOutputStream extends FilterOutputStream {
    private BufferedBlockCipher bufferedBlockCipher;
    private StreamCipher streamCipher;
    private AEADBlockCipher aeadBlockCipher;
    private final byte[] oneByte;
    private byte[] buf;

    public CipherOutputStream(OutputStream outputStream, BufferedBlockCipher bufferedBlockCipher) {
        super(outputStream);
        this.oneByte = new byte[1];
        this.bufferedBlockCipher = bufferedBlockCipher;
    }

    public CipherOutputStream(OutputStream outputStream, StreamCipher streamCipher) {
        super(outputStream);
        this.oneByte = new byte[1];
        this.streamCipher = streamCipher;
    }

    public CipherOutputStream(OutputStream outputStream, AEADBlockCipher aEADBlockCipher) {
        super(outputStream);
        this.oneByte = new byte[1];
        this.aeadBlockCipher = aEADBlockCipher;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int i) throws IOException {
        this.oneByte[0] = (byte) i;
        if (this.streamCipher != null) {
            this.out.write(this.streamCipher.returnByte((byte) i));
        } else {
            write(this.oneByte, 0, 1);
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] bArr) throws IOException {
        write(bArr, 0, bArr.length);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) throws IOException {
        ensureCapacity(i2, false);
        if (this.bufferedBlockCipher != null) {
            int iProcessBytes = this.bufferedBlockCipher.processBytes(bArr, i, i2, this.buf, 0);
            if (iProcessBytes != 0) {
                this.out.write(this.buf, 0, iProcessBytes);
                return;
            }
            return;
        }
        if (this.aeadBlockCipher == null) {
            this.streamCipher.processBytes(bArr, i, i2, this.buf, 0);
            this.out.write(this.buf, 0, i2);
        } else {
            int iProcessBytes2 = this.aeadBlockCipher.processBytes(bArr, i, i2, this.buf, 0);
            if (iProcessBytes2 != 0) {
                this.out.write(this.buf, 0, iProcessBytes2);
            }
        }
    }

    private void ensureCapacity(int i, boolean z) {
        int updateOutputSize = i;
        if (z) {
            if (this.bufferedBlockCipher != null) {
                updateOutputSize = this.bufferedBlockCipher.getOutputSize(i);
            } else if (this.aeadBlockCipher != null) {
                updateOutputSize = this.aeadBlockCipher.getOutputSize(i);
            }
        } else if (this.bufferedBlockCipher != null) {
            updateOutputSize = this.bufferedBlockCipher.getUpdateOutputSize(i);
        } else if (this.aeadBlockCipher != null) {
            updateOutputSize = this.aeadBlockCipher.getUpdateOutputSize(i);
        }
        if (this.buf == null || this.buf.length < updateOutputSize) {
            this.buf = new byte[updateOutputSize];
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        ensureCapacity(0, true);
        IOException cipherIOException = null;
        try {
            if (this.bufferedBlockCipher != null) {
                int iDoFinal = this.bufferedBlockCipher.doFinal(this.buf, 0);
                if (iDoFinal != 0) {
                    this.out.write(this.buf, 0, iDoFinal);
                }
            } else if (this.aeadBlockCipher != null) {
                int iDoFinal2 = this.aeadBlockCipher.doFinal(this.buf, 0);
                if (iDoFinal2 != 0) {
                    this.out.write(this.buf, 0, iDoFinal2);
                }
            } else if (this.streamCipher != null) {
                this.streamCipher.reset();
            }
        } catch (InvalidCipherTextException e) {
            cipherIOException = new InvalidCipherTextIOException("Error finalising cipher data", e);
        } catch (Exception e2) {
            cipherIOException = new CipherIOException("Error closing stream: ", e2);
        }
        try {
            flush();
            this.out.close();
        } catch (IOException e3) {
            if (cipherIOException == null) {
                cipherIOException = e3;
            }
        }
        if (cipherIOException != null) {
            throw cipherIOException;
        }
    }
}
