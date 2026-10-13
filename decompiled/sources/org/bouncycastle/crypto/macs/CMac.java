package org.bouncycastle.crypto.macs;

import com.sun.jna.platform.linux.Fcntl;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/macs/CMac.class */
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

    public CMac(BlockCipher blockCipher) {
        this(blockCipher, blockCipher.getBlockSize() * 8);
    }

    public CMac(BlockCipher blockCipher, int i) {
        if (i % 8 != 0) {
            throw new IllegalArgumentException("MAC size must be multiple of 8");
        }
        if (i > blockCipher.getBlockSize() * 8) {
            throw new IllegalArgumentException("MAC size must be less or equal to " + (blockCipher.getBlockSize() * 8));
        }
        this.cipher = CBCBlockCipher.newInstance(blockCipher);
        this.macSize = i / 8;
        this.poly = lookupPoly(blockCipher.getBlockSize());
        this.mac = new byte[blockCipher.getBlockSize()];
        this.buf = new byte[blockCipher.getBlockSize()];
        this.ZEROES = new byte[blockCipher.getBlockSize()];
        this.bufOff = 0;
    }

    @Override // org.bouncycastle.crypto.Mac
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName();
    }

    private static int shiftLeft(byte[] bArr, byte[] bArr2) {
        int length = bArr.length;
        int i = 0;
        while (true) {
            int i2 = i;
            length--;
            if (length < 0) {
                return i2;
            }
            int i3 = bArr[length] & 255;
            bArr2[length] = (byte) ((i3 << 1) | i2);
            i = (i3 >>> 7) & 1;
        }
    }

    private byte[] doubleLu(byte[] bArr) {
        byte[] bArr2 = new byte[bArr.length];
        int i = (-shiftLeft(bArr, bArr2)) & 255;
        int length = bArr.length - 3;
        bArr2[length] = (byte) (bArr2[length] ^ (this.poly[1] & i));
        int length2 = bArr.length - 2;
        bArr2[length2] = (byte) (bArr2[length2] ^ (this.poly[2] & i));
        int length3 = bArr.length - 1;
        bArr2[length3] = (byte) (bArr2[length3] ^ (this.poly[3] & i));
        return bArr2;
    }

    private static byte[] lookupPoly(int i) {
        int i2;
        switch (i * 8) {
            case 64:
                i2 = 27;
                break;
            case 128:
                i2 = 135;
                break;
            case 160:
                i2 = 45;
                break;
            case 192:
                i2 = 135;
                break;
            case 224:
                i2 = 777;
                break;
            case 256:
                i2 = 1061;
                break;
            case 320:
                i2 = 27;
                break;
            case 384:
                i2 = 4109;
                break;
            case Fcntl.S_IRWXU /* 448 */:
                i2 = 2129;
                break;
            case 512:
                i2 = 293;
                break;
            case 768:
                i2 = 655377;
                break;
            case 1024:
                i2 = 524355;
                break;
            case 2048:
                i2 = 548865;
                break;
            default:
                throw new IllegalArgumentException("Unknown block size for CMAC: " + (i * 8));
        }
        return Pack.intToBigEndian(i2);
    }

    @Override // org.bouncycastle.crypto.Mac
    public void init(CipherParameters cipherParameters) {
        validate(cipherParameters);
        this.cipher.init(true, cipherParameters);
        byte[] bArr = new byte[this.ZEROES.length];
        this.cipher.processBlock(this.ZEROES, 0, bArr, 0);
        this.Lu = doubleLu(bArr);
        this.Lu2 = doubleLu(this.Lu);
        reset();
    }

    void validate(CipherParameters cipherParameters) {
        if (cipherParameters != null && !(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("CMac mode only permits key to be set.");
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
            this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
            i2 -= i3;
            int i4 = i;
            int i5 = i3;
            while (true) {
                i = i4 + i5;
                if (i2 <= blockSize) {
                    break;
                }
                this.cipher.processBlock(bArr, i, this.mac, 0);
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
        byte[] bArr2;
        if (this.bufOff == this.cipher.getBlockSize()) {
            bArr2 = this.Lu;
        } else {
            new ISO7816d4Padding().addPadding(this.buf, this.bufOff);
            bArr2 = this.Lu2;
        }
        for (int i2 = 0; i2 < this.mac.length; i2++) {
            byte[] bArr3 = this.buf;
            int i3 = i2;
            bArr3[i3] = (byte) (bArr3[i3] ^ bArr2[i2]);
        }
        this.cipher.processBlock(this.buf, 0, this.mac, 0);
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
