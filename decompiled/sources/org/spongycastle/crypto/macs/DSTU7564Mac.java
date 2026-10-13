package org.spongycastle.crypto.macs;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.digests.DSTU7564Digest;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/macs/DSTU7564Mac.class */
public class DSTU7564Mac implements Mac {
    private static final int BITS_IN_BYTE = 8;
    private DSTU7564Digest engine;
    private int macSize;
    private byte[] paddedKey = null;
    private byte[] invertedKey = null;
    private long inputLength;

    public DSTU7564Mac(int macBitSize) {
        this.engine = new DSTU7564Digest(macBitSize);
        this.macSize = macBitSize / 8;
    }

    @Override // org.spongycastle.crypto.Mac
    public void init(CipherParameters params) throws IllegalArgumentException {
        if (params instanceof KeyParameter) {
            byte[] key = ((KeyParameter) params).getKey();
            this.invertedKey = new byte[key.length];
            this.paddedKey = padKey(key);
            for (int byteIndex = 0; byteIndex < this.invertedKey.length; byteIndex++) {
                this.invertedKey[byteIndex] = (byte) (key[byteIndex] ^ (-1));
            }
            this.engine.update(this.paddedKey, 0, this.paddedKey.length);
            return;
        }
        throw new IllegalArgumentException("Bad parameter passed");
    }

    @Override // org.spongycastle.crypto.Mac
    public String getAlgorithmName() {
        return "DSTU7564Mac";
    }

    @Override // org.spongycastle.crypto.Mac
    public int getMacSize() {
        return this.macSize;
    }

    @Override // org.spongycastle.crypto.Mac
    public void update(byte in) throws IllegalStateException {
        this.engine.update(in);
        this.inputLength++;
    }

    @Override // org.spongycastle.crypto.Mac
    public void update(byte[] in, int inOff, int len) throws IllegalStateException, DataLengthException {
        if (in.length - inOff < len) {
            throw new DataLengthException("Input buffer too short");
        }
        if (this.paddedKey == null) {
            throw new IllegalStateException(getAlgorithmName() + " not initialised");
        }
        this.engine.update(in, inOff, len);
        this.inputLength += (long) len;
    }

    @Override // org.spongycastle.crypto.Mac
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, DataLengthException {
        if (this.paddedKey == null) {
            throw new IllegalStateException(getAlgorithmName() + " not initialised");
        }
        if (out.length - outOff < this.macSize) {
            throw new OutputLengthException("Output buffer too short");
        }
        pad();
        this.engine.update(this.invertedKey, 0, this.invertedKey.length);
        this.inputLength = 0L;
        return this.engine.doFinal(out, outOff);
    }

    @Override // org.spongycastle.crypto.Mac
    public void reset() {
        this.inputLength = 0L;
        this.engine.reset();
        if (this.paddedKey != null) {
            this.engine.update(this.paddedKey, 0, this.paddedKey.length);
        }
    }

    private void pad() {
        int extra = this.engine.getByteLength() - ((int) (this.inputLength % ((long) this.engine.getByteLength())));
        if (extra < 13) {
            extra += this.engine.getByteLength();
        }
        byte[] padded = new byte[extra];
        padded[0] = -128;
        Pack.longToLittleEndian(this.inputLength * 8, padded, padded.length - 12);
        this.engine.update(padded, 0, padded.length);
    }

    private byte[] padKey(byte[] in) {
        int paddedLen = (((in.length + this.engine.getByteLength()) - 1) / this.engine.getByteLength()) * this.engine.getByteLength();
        int extra = this.engine.getByteLength() - (in.length % this.engine.getByteLength());
        if (extra < 13) {
            paddedLen += this.engine.getByteLength();
        }
        byte[] padded = new byte[paddedLen];
        System.arraycopy(in, 0, padded, 0, in.length);
        padded[in.length] = -128;
        Pack.intToLittleEndian(in.length * 8, padded, padded.length - 12);
        return padded;
    }
}
