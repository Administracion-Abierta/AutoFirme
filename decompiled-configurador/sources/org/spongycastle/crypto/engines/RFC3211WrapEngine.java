package org.spongycastle.crypto.engines;

import java.security.SecureRandom;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.Wrapper;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.crypto.params.ParametersWithRandom;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/engines/RFC3211WrapEngine.class */
public class RFC3211WrapEngine implements Wrapper {
    private CBCBlockCipher engine;
    private ParametersWithIV param;
    private boolean forWrapping;
    private SecureRandom rand;

    public RFC3211WrapEngine(BlockCipher engine) {
        this.engine = new CBCBlockCipher(engine);
    }

    @Override // org.spongycastle.crypto.Wrapper
    public void init(boolean forWrapping, CipherParameters param) {
        this.forWrapping = forWrapping;
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom p = (ParametersWithRandom) param;
            this.rand = p.getRandom();
            this.param = (ParametersWithIV) p.getParameters();
        } else {
            if (forWrapping) {
                this.rand = new SecureRandom();
            }
            this.param = (ParametersWithIV) param;
        }
    }

    @Override // org.spongycastle.crypto.Wrapper
    public String getAlgorithmName() {
        return this.engine.getUnderlyingCipher().getAlgorithmName() + "/RFC3211Wrap";
    }

    @Override // org.spongycastle.crypto.Wrapper
    public byte[] wrap(byte[] in, int inOff, int inLen) {
        byte[] cekBlock;
        if (!this.forWrapping) {
            throw new IllegalStateException("not set for wrapping");
        }
        this.engine.init(true, this.param);
        int blockSize = this.engine.getBlockSize();
        if (inLen + 4 < blockSize * 2) {
            cekBlock = new byte[blockSize * 2];
        } else {
            cekBlock = new byte[(inLen + 4) % blockSize == 0 ? inLen + 4 : (((inLen + 4) / blockSize) + 1) * blockSize];
        }
        cekBlock[0] = (byte) inLen;
        cekBlock[1] = (byte) (in[inOff] ^ (-1));
        cekBlock[2] = (byte) (in[inOff + 1] ^ (-1));
        cekBlock[3] = (byte) (in[inOff + 2] ^ (-1));
        System.arraycopy(in, inOff, cekBlock, 4, inLen);
        byte[] pad = new byte[cekBlock.length - (inLen + 4)];
        this.rand.nextBytes(pad);
        System.arraycopy(pad, 0, cekBlock, inLen + 4, pad.length);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= cekBlock.length) {
                break;
            }
            this.engine.processBlock(cekBlock, i2, cekBlock, i2);
            i = i2 + blockSize;
        }
        int i3 = 0;
        while (true) {
            int i4 = i3;
            if (i4 < cekBlock.length) {
                this.engine.processBlock(cekBlock, i4, cekBlock, i4);
                i3 = i4 + blockSize;
            } else {
                return cekBlock;
            }
        }
    }

    @Override // org.spongycastle.crypto.Wrapper
    public byte[] unwrap(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("not set for unwrapping");
        }
        int blockSize = this.engine.getBlockSize();
        if (inLen < 2 * blockSize) {
            throw new InvalidCipherTextException("input too short");
        }
        byte[] cekBlock = new byte[inLen];
        byte[] iv = new byte[blockSize];
        System.arraycopy(in, inOff, cekBlock, 0, inLen);
        System.arraycopy(in, inOff, iv, 0, iv.length);
        this.engine.init(false, new ParametersWithIV(this.param.getParameters(), iv));
        int i = blockSize;
        while (true) {
            int i2 = i;
            if (i2 >= cekBlock.length) {
                break;
            }
            this.engine.processBlock(cekBlock, i2, cekBlock, i2);
            i = i2 + blockSize;
        }
        System.arraycopy(cekBlock, cekBlock.length - iv.length, iv, 0, iv.length);
        this.engine.init(false, new ParametersWithIV(this.param.getParameters(), iv));
        this.engine.processBlock(cekBlock, 0, cekBlock, 0);
        this.engine.init(false, this.param);
        int i3 = 0;
        while (true) {
            int i4 = i3;
            if (i4 >= cekBlock.length) {
                break;
            }
            this.engine.processBlock(cekBlock, i4, cekBlock, i4);
            i3 = i4 + blockSize;
        }
        if ((cekBlock[0] & 255) > cekBlock.length - 4) {
            throw new InvalidCipherTextException("wrapped key corrupted");
        }
        byte[] key = new byte[cekBlock[0] & 255];
        System.arraycopy(cekBlock, 4, key, 0, cekBlock[0]);
        int nonEqual = 0;
        for (int i5 = 0; i5 != 3; i5++) {
            byte check = (byte) (cekBlock[1 + i5] ^ (-1));
            nonEqual |= check ^ key[i5];
        }
        if (nonEqual != 0) {
            throw new InvalidCipherTextException("wrapped key fails checksum");
        }
        return key;
    }
}
