package org.spongycastle.crypto.macs;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.engines.DESEngine;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.BlockCipherPadding;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/macs/ISO9797Alg3Mac.class */
public class ISO9797Alg3Mac implements Mac {
    private byte[] mac;
    private byte[] buf;
    private int bufOff;
    private BlockCipher cipher;
    private BlockCipherPadding padding;
    private int macSize;
    private KeyParameter lastKey2;
    private KeyParameter lastKey3;

    public ISO9797Alg3Mac(BlockCipher cipher) {
        this(cipher, cipher.getBlockSize() * 8, null);
    }

    public ISO9797Alg3Mac(BlockCipher cipher, BlockCipherPadding padding) {
        this(cipher, cipher.getBlockSize() * 8, padding);
    }

    public ISO9797Alg3Mac(BlockCipher cipher, int macSizeInBits) {
        this(cipher, macSizeInBits, null);
    }

    public ISO9797Alg3Mac(BlockCipher cipher, int macSizeInBits, BlockCipherPadding padding) {
        if (macSizeInBits % 8 != 0) {
            throw new IllegalArgumentException("MAC size must be multiple of 8");
        }
        if (!(cipher instanceof DESEngine)) {
            throw new IllegalArgumentException("cipher must be instance of DESEngine");
        }
        this.cipher = new CBCBlockCipher(cipher);
        this.padding = padding;
        this.macSize = macSizeInBits / 8;
        this.mac = new byte[cipher.getBlockSize()];
        this.buf = new byte[cipher.getBlockSize()];
        this.bufOff = 0;
    }

    @Override // org.spongycastle.crypto.Mac
    public String getAlgorithmName() {
        return "ISO9797Alg3";
    }

    @Override // org.spongycastle.crypto.Mac
    public void init(CipherParameters params) {
        KeyParameter kp;
        KeyParameter key1;
        reset();
        if (!(params instanceof KeyParameter) && !(params instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("params must be an instance of KeyParameter or ParametersWithIV");
        }
        if (params instanceof KeyParameter) {
            kp = (KeyParameter) params;
        } else {
            kp = (KeyParameter) ((ParametersWithIV) params).getParameters();
        }
        byte[] keyvalue = kp.getKey();
        if (keyvalue.length == 16) {
            key1 = new KeyParameter(keyvalue, 0, 8);
            this.lastKey2 = new KeyParameter(keyvalue, 8, 8);
            this.lastKey3 = key1;
        } else if (keyvalue.length == 24) {
            key1 = new KeyParameter(keyvalue, 0, 8);
            this.lastKey2 = new KeyParameter(keyvalue, 8, 8);
            this.lastKey3 = new KeyParameter(keyvalue, 16, 8);
        } else {
            throw new IllegalArgumentException("Key must be either 112 or 168 bit long");
        }
        if (params instanceof ParametersWithIV) {
            this.cipher.init(true, new ParametersWithIV(key1, ((ParametersWithIV) params).getIV()));
        } else {
            this.cipher.init(true, key1);
        }
    }

    @Override // org.spongycastle.crypto.Mac
    public int getMacSize() {
        return this.macSize;
    }

    @Override // org.spongycastle.crypto.Mac
    public void update(byte in) {
        if (this.bufOff == this.buf.length) {
            this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
        }
        byte[] bArr = this.buf;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = in;
    }

    @Override // org.spongycastle.crypto.Mac
    public void update(byte[] in, int inOff, int len) {
        if (len < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int blockSize = this.cipher.getBlockSize();
        int gapLen = blockSize - this.bufOff;
        if (len > gapLen) {
            System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
            int resultLen = 0 + this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
            len -= gapLen;
            int i = inOff;
            int i2 = gapLen;
            while (true) {
                inOff = i + i2;
                if (len <= blockSize) {
                    break;
                }
                resultLen += this.cipher.processBlock(in, inOff, this.mac, 0);
                len -= blockSize;
                i = inOff;
                i2 = blockSize;
            }
        }
        System.arraycopy(in, inOff, this.buf, this.bufOff, len);
        this.bufOff += len;
    }

    @Override // org.spongycastle.crypto.Mac
    public int doFinal(byte[] out, int outOff) {
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
        DESEngine deseng = new DESEngine();
        deseng.init(false, this.lastKey2);
        deseng.processBlock(this.mac, 0, this.mac, 0);
        deseng.init(true, this.lastKey3);
        deseng.processBlock(this.mac, 0, this.mac, 0);
        System.arraycopy(this.mac, 0, out, outOff, this.macSize);
        reset();
        return this.macSize;
    }

    @Override // org.spongycastle.crypto.Mac
    public void reset() {
        for (int i = 0; i < this.buf.length; i++) {
            this.buf[i] = 0;
        }
        this.bufOff = 0;
        this.cipher.reset();
    }
}
