package org.bouncycastle.crypto.prng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/prng/FixedSecureRandom.class */
public class FixedSecureRandom extends SecureRandom {
    private byte[] _data;
    private int _index;
    private int _intPad;

    public FixedSecureRandom(byte[] bArr) {
        this(false, new byte[][]{bArr});
    }

    public FixedSecureRandom(byte[][] bArr) {
        this(false, bArr);
    }

    public FixedSecureRandom(boolean z, byte[] bArr) {
        this(z, new byte[][]{bArr});
    }

    public FixedSecureRandom(boolean z, byte[][] bArr) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i != bArr.length; i++) {
            try {
                byteArrayOutputStream.write(bArr[i]);
            } catch (IOException e) {
                throw new IllegalArgumentException("can't save value array.");
            }
        }
        this._data = byteArrayOutputStream.toByteArray();
        if (z) {
            this._intPad = this._data.length % 4;
        }
    }

    @Override // java.security.SecureRandom, java.util.Random
    public void nextBytes(byte[] bArr) {
        System.arraycopy(this._data, this._index, bArr, 0, bArr.length);
        this._index += bArr.length;
    }

    @Override // java.security.SecureRandom
    public byte[] generateSeed(int i) {
        byte[] bArr = new byte[i];
        nextBytes(bArr);
        return bArr;
    }

    @Override // java.util.Random
    public int nextInt() {
        int iNextValue = 0 | (nextValue() << 24) | (nextValue() << 16);
        if (this._intPad == 2) {
            this._intPad--;
        } else {
            iNextValue |= nextValue() << 8;
        }
        if (this._intPad == 1) {
            this._intPad--;
        } else {
            iNextValue |= nextValue();
        }
        return iNextValue;
    }

    @Override // java.util.Random
    public long nextLong() {
        return 0 | (((long) nextValue()) << 56) | (((long) nextValue()) << 48) | (((long) nextValue()) << 40) | (((long) nextValue()) << 32) | (((long) nextValue()) << 24) | (((long) nextValue()) << 16) | (((long) nextValue()) << 8) | ((long) nextValue());
    }

    public boolean isExhausted() {
        return this._index == this._data.length;
    }

    private int nextValue() {
        byte[] bArr = this._data;
        int i = this._index;
        this._index = i + 1;
        return bArr[i] & 255;
    }
}
