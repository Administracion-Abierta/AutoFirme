package org.spongycastle.crypto.macs;

import com.sun.jna.platform.linux.Fcntl;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.ISO7816d4Padding;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/macs/CMac.class */
public class CMac implements Mac {
    private byte[] poly;
    private byte[] ZEROES;
    private byte[] mac;
    private byte[] buf;
    private int bufOff;
    private BlockCipher cipher;
    private int macSize;
    private byte[] Lu;
    private byte[] Lu2;

    public CMac(BlockCipher cipher) {
        this(cipher, cipher.getBlockSize() * 8);
    }

    public CMac(BlockCipher cipher, int macSizeInBits) {
        if (macSizeInBits % 8 != 0) {
            throw new IllegalArgumentException("MAC size must be multiple of 8");
        }
        if (macSizeInBits > cipher.getBlockSize() * 8) {
            throw new IllegalArgumentException("MAC size must be less or equal to " + (cipher.getBlockSize() * 8));
        }
        this.cipher = new CBCBlockCipher(cipher);
        this.macSize = macSizeInBits / 8;
        this.poly = lookupPoly(cipher.getBlockSize());
        this.mac = new byte[cipher.getBlockSize()];
        this.buf = new byte[cipher.getBlockSize()];
        this.ZEROES = new byte[cipher.getBlockSize()];
        this.bufOff = 0;
    }

    @Override // org.spongycastle.crypto.Mac
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName();
    }

    private static int shiftLeft(byte[] block, byte[] output) {
        int i = block.length;
        int i2 = 0;
        while (true) {
            int bit = i2;
            i--;
            if (i >= 0) {
                int b = block[i] & 255;
                output[i] = (byte) ((b << 1) | bit);
                i2 = (b >>> 7) & 1;
            } else {
                return bit;
            }
        }
    }

    private byte[] doubleLu(byte[] in) {
        byte[] ret = new byte[in.length];
        int carry = shiftLeft(in, ret);
        int mask = (-carry) & 255;
        int length = in.length - 3;
        ret[length] = (byte) (ret[length] ^ (this.poly[1] & mask));
        int length2 = in.length - 2;
        ret[length2] = (byte) (ret[length2] ^ (this.poly[2] & mask));
        int length3 = in.length - 1;
        ret[length3] = (byte) (ret[length3] ^ (this.poly[3] & mask));
        return ret;
    }

    private static byte[] lookupPoly(int blockSizeLength) {
        int xor;
        switch (blockSizeLength * 8) {
            case 64:
                xor = 27;
                break;
            case 128:
                xor = 135;
                break;
            case 160:
                xor = 45;
                break;
            case 192:
                xor = 135;
                break;
            case 224:
                xor = 777;
                break;
            case 256:
                xor = 1061;
                break;
            case 320:
                xor = 27;
                break;
            case 384:
                xor = 4109;
                break;
            case Fcntl.S_IRWXU /* 448 */:
                xor = 2129;
                break;
            case 512:
                xor = 293;
                break;
            case 768:
                xor = 655377;
                break;
            case 1024:
                xor = 524355;
                break;
            case 2048:
                xor = 548865;
                break;
            default:
                throw new IllegalArgumentException("Unknown block size for CMAC: " + (blockSizeLength * 8));
        }
        return Pack.intToBigEndian(xor);
    }

    @Override // org.spongycastle.crypto.Mac
    public void init(CipherParameters params) {
        validate(params);
        this.cipher.init(true, params);
        byte[] L = new byte[this.ZEROES.length];
        this.cipher.processBlock(this.ZEROES, 0, L, 0);
        this.Lu = doubleLu(L);
        this.Lu2 = doubleLu(this.Lu);
        reset();
    }

    void validate(CipherParameters params) {
        if (params != null && !(params instanceof KeyParameter)) {
            throw new IllegalArgumentException("CMac mode only permits key to be set.");
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
            this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
            len -= gapLen;
            int i = inOff;
            int i2 = gapLen;
            while (true) {
                inOff = i + i2;
                if (len <= blockSize) {
                    break;
                }
                this.cipher.processBlock(in, inOff, this.mac, 0);
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
        byte[] lu;
        int blockSize = this.cipher.getBlockSize();
        if (this.bufOff == blockSize) {
            lu = this.Lu;
        } else {
            new ISO7816d4Padding().addPadding(this.buf, this.bufOff);
            lu = this.Lu2;
        }
        for (int i = 0; i < this.mac.length; i++) {
            byte[] bArr = this.buf;
            int i2 = i;
            bArr[i2] = (byte) (bArr[i2] ^ lu[i]);
        }
        this.cipher.processBlock(this.buf, 0, this.mac, 0);
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
