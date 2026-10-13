package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.Wrapper;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/RFC3394WrapEngine.class */
public class RFC3394WrapEngine implements Wrapper {
    private BlockCipher engine;
    private boolean wrapCipherMode;
    private KeyParameter param;
    private boolean forWrapping;
    private byte[] iv;

    public RFC3394WrapEngine(BlockCipher engine) {
        this(engine, false);
    }

    public RFC3394WrapEngine(BlockCipher engine, boolean useReverseDirection) {
        this.iv = new byte[]{-90, -90, -90, -90, -90, -90, -90, -90};
        this.engine = engine;
        this.wrapCipherMode = !useReverseDirection;
    }

    @Override // org.spongycastle.crypto.Wrapper
    public void init(boolean forWrapping, CipherParameters param) {
        this.forWrapping = forWrapping;
        if (param instanceof ParametersWithRandom) {
            param = ((ParametersWithRandom) param).getParameters();
        }
        if (param instanceof KeyParameter) {
            this.param = (KeyParameter) param;
        } else if (param instanceof ParametersWithIV) {
            this.iv = ((ParametersWithIV) param).getIV();
            this.param = (KeyParameter) ((ParametersWithIV) param).getParameters();
            if (this.iv.length != 8) {
                throw new IllegalArgumentException("IV not equal to 8");
            }
        }
    }

    @Override // org.spongycastle.crypto.Wrapper
    public String getAlgorithmName() {
        return this.engine.getAlgorithmName();
    }

    @Override // org.spongycastle.crypto.Wrapper
    public byte[] wrap(byte[] in, int inOff, int inLen) {
        if (!this.forWrapping) {
            throw new IllegalStateException("not set for wrapping");
        }
        int n = inLen / 8;
        if (n * 8 != inLen) {
            throw new DataLengthException("wrap data must be a multiple of 8 bytes");
        }
        byte[] block = new byte[inLen + this.iv.length];
        byte[] buf = new byte[8 + this.iv.length];
        System.arraycopy(this.iv, 0, block, 0, this.iv.length);
        System.arraycopy(in, inOff, block, this.iv.length, inLen);
        this.engine.init(this.wrapCipherMode, this.param);
        for (int j = 0; j != 6; j++) {
            for (int i = 1; i <= n; i++) {
                System.arraycopy(block, 0, buf, 0, this.iv.length);
                System.arraycopy(block, 8 * i, buf, this.iv.length, 8);
                this.engine.processBlock(buf, 0, buf, 0);
                int t = (n * j) + i;
                int k = 1;
                while (t != 0) {
                    byte v = (byte) t;
                    int length = this.iv.length - k;
                    buf[length] = (byte) (buf[length] ^ v);
                    t >>>= 8;
                    k++;
                }
                System.arraycopy(buf, 0, block, 0, 8);
                System.arraycopy(buf, 8, block, 8 * i, 8);
            }
        }
        return block;
    }

    @Override // org.spongycastle.crypto.Wrapper
    public byte[] unwrap(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("not set for unwrapping");
        }
        int n = inLen / 8;
        if (n * 8 != inLen) {
            throw new InvalidCipherTextException("unwrap data must be a multiple of 8 bytes");
        }
        byte[] block = new byte[inLen - this.iv.length];
        byte[] a = new byte[this.iv.length];
        byte[] buf = new byte[8 + this.iv.length];
        System.arraycopy(in, inOff, a, 0, this.iv.length);
        System.arraycopy(in, inOff + this.iv.length, block, 0, inLen - this.iv.length);
        this.engine.init(!this.wrapCipherMode, this.param);
        int n2 = n - 1;
        for (int j = 5; j >= 0; j--) {
            for (int i = n2; i >= 1; i--) {
                System.arraycopy(a, 0, buf, 0, this.iv.length);
                System.arraycopy(block, 8 * (i - 1), buf, this.iv.length, 8);
                int t = (n2 * j) + i;
                int k = 1;
                while (t != 0) {
                    byte v = (byte) t;
                    int length = this.iv.length - k;
                    buf[length] = (byte) (buf[length] ^ v);
                    t >>>= 8;
                    k++;
                }
                this.engine.processBlock(buf, 0, buf, 0);
                System.arraycopy(buf, 0, a, 0, 8);
                System.arraycopy(buf, 8, block, 8 * (i - 1), 8);
            }
        }
        if (!Arrays.constantTimeAreEqual(a, this.iv)) {
            throw new InvalidCipherTextException("checksum failed");
        }
        return block;
    }
}
