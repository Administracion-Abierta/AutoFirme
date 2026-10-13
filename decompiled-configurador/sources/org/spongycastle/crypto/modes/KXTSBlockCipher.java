package org.spongycastle.crypto.modes;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/modes/KXTSBlockCipher.class */
public class KXTSBlockCipher extends BufferedBlockCipher {
    private static final long RED_POLY_128 = 135;
    private static final long RED_POLY_256 = 1061;
    private static final long RED_POLY_512 = 293;
    private final int blockSize;
    private final long reductionPolynomial;
    private final long[] tw_init;
    private final long[] tw_current;
    private int counter;

    protected static long getReductionPolynomial(int blockSize) {
        switch (blockSize) {
            case 16:
                return RED_POLY_128;
            case 32:
                return RED_POLY_256;
            case 64:
                return RED_POLY_512;
            default:
                throw new IllegalArgumentException("Only 128, 256, and 512 -bit block sizes supported");
        }
    }

    public KXTSBlockCipher(BlockCipher cipher) {
        this.cipher = cipher;
        this.blockSize = cipher.getBlockSize();
        this.reductionPolynomial = getReductionPolynomial(this.blockSize);
        this.tw_init = new long[this.blockSize >>> 3];
        this.tw_current = new long[this.blockSize >>> 3];
        this.counter = -1;
    }

    @Override // org.spongycastle.crypto.BufferedBlockCipher
    public int getOutputSize(int length) {
        return length;
    }

    @Override // org.spongycastle.crypto.BufferedBlockCipher
    public int getUpdateOutputSize(int len) {
        return len;
    }

    @Override // org.spongycastle.crypto.BufferedBlockCipher
    public void init(boolean forEncryption, CipherParameters parameters) {
        if (!(parameters instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("Invalid parameters passed");
        }
        ParametersWithIV ivParam = (ParametersWithIV) parameters;
        CipherParameters parameters2 = ivParam.getParameters();
        byte[] iv = ivParam.getIV();
        if (iv.length != this.blockSize) {
            throw new IllegalArgumentException("Currently only support IVs of exactly one block");
        }
        byte[] tweak = new byte[this.blockSize];
        System.arraycopy(iv, 0, tweak, 0, this.blockSize);
        this.cipher.init(true, parameters2);
        this.cipher.processBlock(tweak, 0, tweak, 0);
        this.cipher.init(forEncryption, parameters2);
        Pack.littleEndianToLong(tweak, 0, this.tw_init);
        System.arraycopy(this.tw_init, 0, this.tw_current, 0, this.tw_init.length);
        this.counter = 0;
    }

    @Override // org.spongycastle.crypto.BufferedBlockCipher
    public int processByte(byte in, byte[] out, int outOff) {
        throw new IllegalStateException("unsupported operation");
    }

    @Override // org.spongycastle.crypto.BufferedBlockCipher
    public int processBytes(byte[] input, int inOff, int len, byte[] output, int outOff) {
        if (input.length - inOff < len) {
            throw new DataLengthException("Input buffer too short");
        }
        if (output.length - inOff < len) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (len % this.blockSize != 0) {
            throw new IllegalArgumentException("Partial blocks not supported");
        }
        int i = 0;
        while (true) {
            int pos = i;
            if (pos < len) {
                processBlock(input, inOff + pos, output, outOff + pos);
                i = pos + this.blockSize;
            } else {
                return len;
            }
        }
    }

    private void processBlock(byte[] input, int inOff, byte[] output, int outOff) {
        if (this.counter == -1) {
            throw new IllegalStateException("Attempt to process too many blocks");
        }
        this.counter++;
        GF_double(this.reductionPolynomial, this.tw_current);
        byte[] tweak = new byte[this.blockSize];
        Pack.longToLittleEndian(this.tw_current, tweak, 0);
        byte[] buffer = new byte[this.blockSize];
        System.arraycopy(tweak, 0, buffer, 0, this.blockSize);
        for (int i = 0; i < this.blockSize; i++) {
            int i2 = i;
            buffer[i2] = (byte) (buffer[i2] ^ input[inOff + i]);
        }
        this.cipher.processBlock(buffer, 0, buffer, 0);
        for (int i3 = 0; i3 < this.blockSize; i3++) {
            output[outOff + i3] = (byte) (buffer[i3] ^ tweak[i3]);
        }
    }

    @Override // org.spongycastle.crypto.BufferedBlockCipher
    public int doFinal(byte[] output, int outOff) {
        reset();
        return 0;
    }

    @Override // org.spongycastle.crypto.BufferedBlockCipher
    public void reset() {
        this.cipher.reset();
        System.arraycopy(this.tw_init, 0, this.tw_current, 0, this.tw_init.length);
        this.counter = 0;
    }

    private static void GF_double(long redPoly, long[] z) {
        long c = 0;
        for (int i = 0; i < z.length; i++) {
            long zVal = z[i];
            long bit = zVal >>> 63;
            z[i] = (zVal << 1) ^ c;
            c = bit;
        }
        z[0] = z[0] ^ (redPoly & (-c));
    }
}
