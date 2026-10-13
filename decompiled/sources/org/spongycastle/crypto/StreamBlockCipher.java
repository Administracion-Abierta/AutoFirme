package org.spongycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/StreamBlockCipher.class */
public abstract class StreamBlockCipher implements BlockCipher, StreamCipher {
    private final BlockCipher cipher;

    protected abstract byte calculateByte(byte b);

    protected StreamBlockCipher(BlockCipher cipher) {
        this.cipher = cipher;
    }

    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public final byte returnByte(byte in) {
        return calculateByte(in);
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
        if (inOff + len > in.length) {
            throw new DataLengthException("input buffer too small");
        }
        if (outOff + len > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        int inStart = inOff;
        int inEnd = inOff + len;
        int outStart = outOff;
        while (inStart < inEnd) {
            int i = outStart;
            outStart++;
            int i2 = inStart;
            inStart++;
            out[i] = calculateByte(in[i2]);
        }
        return len;
    }
}
