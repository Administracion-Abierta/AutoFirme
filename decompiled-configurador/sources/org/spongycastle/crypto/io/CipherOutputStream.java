package org.spongycastle.crypto.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.StreamCipher;
import org.spongycastle.crypto.modes.AEADBlockCipher;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/io/CipherOutputStream.class */
public class CipherOutputStream extends FilterOutputStream {
    private BufferedBlockCipher bufferedBlockCipher;
    private StreamCipher streamCipher;
    private AEADBlockCipher aeadBlockCipher;
    private final byte[] oneByte;
    private byte[] buf;

    public CipherOutputStream(OutputStream os, BufferedBlockCipher cipher) {
        super(os);
        this.oneByte = new byte[1];
        this.bufferedBlockCipher = cipher;
    }

    public CipherOutputStream(OutputStream os, StreamCipher cipher) {
        super(os);
        this.oneByte = new byte[1];
        this.streamCipher = cipher;
    }

    public CipherOutputStream(OutputStream os, AEADBlockCipher cipher) {
        super(os);
        this.oneByte = new byte[1];
        this.aeadBlockCipher = cipher;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) throws IOException {
        this.oneByte[0] = (byte) b;
        if (this.streamCipher != null) {
            this.out.write(this.streamCipher.returnByte((byte) b));
        } else {
            write(this.oneByte, 0, 1);
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        ensureCapacity(len, false);
        if (this.bufferedBlockCipher != null) {
            int outLen = this.bufferedBlockCipher.processBytes(b, off, len, this.buf, 0);
            if (outLen != 0) {
                this.out.write(this.buf, 0, outLen);
                return;
            }
            return;
        }
        if (this.aeadBlockCipher != null) {
            int outLen2 = this.aeadBlockCipher.processBytes(b, off, len, this.buf, 0);
            if (outLen2 != 0) {
                this.out.write(this.buf, 0, outLen2);
                return;
            }
            return;
        }
        this.streamCipher.processBytes(b, off, len, this.buf, 0);
        this.out.write(this.buf, 0, len);
    }

    private void ensureCapacity(int updateSize, boolean finalOutput) {
        int bufLen = updateSize;
        if (finalOutput) {
            if (this.bufferedBlockCipher != null) {
                bufLen = this.bufferedBlockCipher.getOutputSize(updateSize);
            } else if (this.aeadBlockCipher != null) {
                bufLen = this.aeadBlockCipher.getOutputSize(updateSize);
            }
        } else if (this.bufferedBlockCipher != null) {
            bufLen = this.bufferedBlockCipher.getUpdateOutputSize(updateSize);
        } else if (this.aeadBlockCipher != null) {
            bufLen = this.aeadBlockCipher.getUpdateOutputSize(updateSize);
        }
        if (this.buf == null || this.buf.length < bufLen) {
            this.buf = new byte[bufLen];
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        ensureCapacity(0, true);
        IOException error = null;
        try {
            if (this.bufferedBlockCipher != null) {
                int outLen = this.bufferedBlockCipher.doFinal(this.buf, 0);
                if (outLen != 0) {
                    this.out.write(this.buf, 0, outLen);
                }
            } else if (this.aeadBlockCipher != null) {
                int outLen2 = this.aeadBlockCipher.doFinal(this.buf, 0);
                if (outLen2 != 0) {
                    this.out.write(this.buf, 0, outLen2);
                }
            } else if (this.streamCipher != null) {
                this.streamCipher.reset();
            }
        } catch (InvalidCipherTextException e) {
            error = new InvalidCipherTextIOException("Error finalising cipher data", e);
        } catch (Exception e2) {
            error = new CipherIOException("Error closing stream: ", e2);
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
