package org.bouncycastle.crypto.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/util/JournalingSecureRandom.class */
public class JournalingSecureRandom extends SecureRandom {
    private static byte[] EMPTY_TRANSCRIPT = new byte[0];
    private final SecureRandom base;
    private TranscriptStream tOut;
    private byte[] transcript;
    private int index;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/util/JournalingSecureRandom$TranscriptStream.class */
    private static class TranscriptStream extends ByteArrayOutputStream {
        private TranscriptStream() {
        }

        public void clear() {
            Arrays.fill(this.buf, (byte) 0);
        }
    }

    public JournalingSecureRandom() {
        this(CryptoServicesRegistrar.getSecureRandom());
    }

    public JournalingSecureRandom(SecureRandom secureRandom) {
        this.tOut = new TranscriptStream();
        this.index = 0;
        this.base = secureRandom;
        this.transcript = EMPTY_TRANSCRIPT;
    }

    public JournalingSecureRandom(byte[] bArr, SecureRandom secureRandom) {
        this.tOut = new TranscriptStream();
        this.index = 0;
        this.base = secureRandom;
        this.transcript = Arrays.clone(bArr);
    }

    @Override // java.security.SecureRandom, java.util.Random
    public final void nextBytes(byte[] bArr) {
        if (this.index >= this.transcript.length) {
            this.base.nextBytes(bArr);
        } else {
            int i = 0;
            while (i != bArr.length && this.index < this.transcript.length) {
                byte[] bArr2 = this.transcript;
                int i2 = this.index;
                this.index = i2 + 1;
                bArr[i] = bArr2[i2];
                i++;
            }
            if (i != bArr.length) {
                byte[] bArr3 = new byte[bArr.length - i];
                this.base.nextBytes(bArr3);
                System.arraycopy(bArr3, 0, bArr, i, bArr3.length);
            }
        }
        try {
            this.tOut.write(bArr);
        } catch (IOException e) {
            throw new IllegalStateException("unable to record transcript: " + e.getMessage());
        }
    }

    public void clear() {
        Arrays.fill(this.transcript, (byte) 0);
        this.tOut.clear();
    }

    public void reset() {
        this.index = 0;
        if (this.index == this.transcript.length) {
            this.transcript = this.tOut.toByteArray();
        }
        this.tOut.reset();
    }

    public byte[] getTranscript() {
        return this.tOut.toByteArray();
    }

    public byte[] getFullTranscript() {
        return this.index == this.transcript.length ? this.tOut.toByteArray() : Arrays.clone(this.transcript);
    }
}
