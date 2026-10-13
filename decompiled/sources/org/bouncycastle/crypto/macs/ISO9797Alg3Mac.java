package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/macs/ISO9797Alg3Mac.class */
public class ISO9797Alg3Mac implements Mac {
    private byte[] mac;
    private byte[] buf;
    private int bufOff;
    private BlockCipher cipher;
    private BlockCipherPadding padding;
    private int macSize;
    private KeyParameter lastKey2;
    private KeyParameter lastKey3;

    public ISO9797Alg3Mac(BlockCipher blockCipher) {
        this(blockCipher, blockCipher.getBlockSize() * 8, null);
    }

    public ISO9797Alg3Mac(BlockCipher blockCipher, BlockCipherPadding blockCipherPadding) {
        this(blockCipher, blockCipher.getBlockSize() * 8, blockCipherPadding);
    }

    public ISO9797Alg3Mac(BlockCipher blockCipher, int i) {
        this(blockCipher, i, null);
    }

    public ISO9797Alg3Mac(BlockCipher blockCipher, int i, BlockCipherPadding blockCipherPadding) {
        if (i % 8 != 0) {
            throw new IllegalArgumentException("MAC size must be multiple of 8");
        }
        if (!(blockCipher instanceof DESEngine)) {
            throw new IllegalArgumentException("cipher must be instance of DESEngine");
        }
        this.cipher = CBCBlockCipher.newInstance(blockCipher);
        this.padding = blockCipherPadding;
        this.macSize = i / 8;
        this.mac = new byte[blockCipher.getBlockSize()];
        this.buf = new byte[blockCipher.getBlockSize()];
        this.bufOff = 0;
    }

    @Override // org.bouncycastle.crypto.Mac
    public String getAlgorithmName() {
        return "ISO9797Alg3";
    }

    @Override // org.bouncycastle.crypto.Mac
    public void init(CipherParameters cipherParameters) {
        KeyParameter keyParameter;
        reset();
        if (!(cipherParameters instanceof KeyParameter) && !(cipherParameters instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("params must be an instance of KeyParameter or ParametersWithIV");
        }
        byte[] key = (cipherParameters instanceof KeyParameter ? (KeyParameter) cipherParameters : (KeyParameter) ((ParametersWithIV) cipherParameters).getParameters()).getKey();
        if (key.length == 16) {
            keyParameter = new KeyParameter(key, 0, 8);
            this.lastKey2 = new KeyParameter(key, 8, 8);
            this.lastKey3 = keyParameter;
        } else {
            if (key.length != 24) {
                throw new IllegalArgumentException("Key must be either 112 or 168 bit long");
            }
            keyParameter = new KeyParameter(key, 0, 8);
            this.lastKey2 = new KeyParameter(key, 8, 8);
            this.lastKey3 = new KeyParameter(key, 16, 8);
        }
        if (cipherParameters instanceof ParametersWithIV) {
            this.cipher.init(true, new ParametersWithIV(keyParameter, ((ParametersWithIV) cipherParameters).getIV()));
        } else {
            this.cipher.init(true, keyParameter);
        }
    }

    @Override // org.bouncycastle.crypto.Mac
    public int getMacSize() {
        return this.macSize;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte b) {
        if (this.bufOff == this.buf.length) {
            this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
        }
        byte[] bArr = this.buf;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = b;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte[] bArr, int i, int i2) {
        if (i2 < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int blockSize = this.cipher.getBlockSize();
        int i3 = blockSize - this.bufOff;
        if (i2 > i3) {
            System.arraycopy(bArr, i, this.buf, this.bufOff, i3);
            int iProcessBlock = 0 + this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
            i2 -= i3;
            int i4 = i;
            int i5 = i3;
            while (true) {
                i = i4 + i5;
                if (i2 <= blockSize) {
                    break;
                }
                iProcessBlock += this.cipher.processBlock(bArr, i, this.mac, 0);
                i2 -= blockSize;
                i4 = i;
                i5 = blockSize;
            }
        }
        System.arraycopy(bArr, i, this.buf, this.bufOff, i2);
        this.bufOff += i2;
    }

    @Override // org.bouncycastle.crypto.Mac
    public int doFinal(byte[] bArr, int i) {
        int blockSize = this.cipher.getBlockSize();
        if (this.padding == null) {
            while (this.bufOff < blockSize) {
                this.buf[this.bufOff] = 0;
                this.bufOff++;
            }
        } else {
            if (this.bufOff == blockSize) {
                this.cipher.processBlock(this.buf, 0, this.mac, 0);
                this.bufOff = 0;
            }
            this.padding.addPadding(this.buf, this.bufOff);
        }
        this.cipher.processBlock(this.buf, 0, this.mac, 0);
        DESEngine dESEngine = new DESEngine();
        dESEngine.init(false, this.lastKey2);
        dESEngine.processBlock(this.mac, 0, this.mac, 0);
        dESEngine.init(true, this.lastKey3);
        dESEngine.processBlock(this.mac, 0, this.mac, 0);
        System.arraycopy(this.mac, 0, bArr, i, this.macSize);
        reset();
        return this.macSize;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void reset() {
        for (int i = 0; i < this.buf.length; i++) {
            this.buf[i] = 0;
        }
        this.bufOff = 0;
        this.cipher.reset();
    }
}
