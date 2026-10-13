package org.spongycastle.crypto.prng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/prng/FixedSecureRandom.class */
public class FixedSecureRandom extends SecureRandom {
    private byte[] _data;
    private int _index;
    private int _intPad;

    public FixedSecureRandom(byte[] value) {
        this(false, new byte[][]{value});
    }

    public FixedSecureRandom(byte[][] values) {
        this(false, values);
    }

    public FixedSecureRandom(boolean intPad, byte[] value) {
        this(intPad, new byte[][]{value});
    }

    public FixedSecureRandom(boolean intPad, byte[][] values) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        for (int i = 0; i != values.length; i++) {
            try {
                bOut.write(values[i]);
            } catch (IOException e) {
                throw new IllegalArgumentException("can't save value array.");
            }
        }
        this._data = bOut.toByteArray();
        if (intPad) {
            this._intPad = this._data.length % 4;
        }
    }

    @Override // java.security.SecureRandom, java.util.Random
    public void nextBytes(byte[] bytes) {
        System.arraycopy(this._data, this._index, bytes, 0, bytes.length);
        this._index += bytes.length;
    }

    @Override // java.security.SecureRandom
    public byte[] generateSeed(int numBytes) {
        byte[] bytes = new byte[numBytes];
        nextBytes(bytes);
        return bytes;
    }

    @Override // java.util.Random
    public int nextInt() {
        int val = 0 | (nextValue() << 24) | (nextValue() << 16);
        if (this._intPad == 2) {
            this._intPad--;
        } else {
            val |= nextValue() << 8;
        }
        if (this._intPad == 1) {
            this._intPad--;
        } else {
            val |= nextValue();
        }
        return val;
    }

    @Override // java.util.Random
    public long nextLong() {
        long val = 0 | (((long) nextValue()) << 56);
        return val | (((long) nextValue()) << 48) | (((long) nextValue()) << 40) | (((long) nextValue()) << 32) | (((long) nextValue()) << 24) | (((long) nextValue()) << 16) | (((long) nextValue()) << 8) | ((long) nextValue());
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
