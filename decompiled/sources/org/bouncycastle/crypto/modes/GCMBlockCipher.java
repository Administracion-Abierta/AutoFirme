package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.modes.gcm.BasicGCMExponentiator;
import org.bouncycastle.crypto.modes.gcm.GCMExponentiator;
import org.bouncycastle.crypto.modes.gcm.GCMMultiplier;
import org.bouncycastle.crypto.modes.gcm.GCMUtil;
import org.bouncycastle.crypto.modes.gcm.Tables4kGCMMultiplier;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/GCMBlockCipher.class */
public class GCMBlockCipher implements GCMModeCipher {
    private static final int BLOCK_SIZE = 16;
    private BlockCipher cipher;
    private GCMMultiplier multiplier;
    private GCMExponentiator exp;
    private boolean forEncryption;
    private boolean initialised;
    private int macSize;
    private byte[] lastKey;
    private byte[] nonce;
    private byte[] initialAssociatedText;
    private byte[] H;
    private byte[] J0;
    private byte[] bufBlock;
    private byte[] macBlock;
    private byte[] S;
    private byte[] S_at;
    private byte[] S_atPre;
    private byte[] counter;
    private int blocksRemaining;
    private int bufOff;
    private long totalLength;
    private byte[] atBlock;
    private int atBlockPos;
    private long atLength;
    private long atLengthPre;

    public static GCMModeCipher newInstance(BlockCipher blockCipher) {
        return new GCMBlockCipher(blockCipher);
    }

    public static GCMModeCipher newInstance(BlockCipher blockCipher, GCMMultiplier gCMMultiplier) {
        return new GCMBlockCipher(blockCipher, gCMMultiplier);
    }

    public GCMBlockCipher(BlockCipher blockCipher) {
        this(blockCipher, null);
    }

    public GCMBlockCipher(BlockCipher blockCipher, GCMMultiplier gCMMultiplier) {
        if (blockCipher.getBlockSize() != 16) {
            throw new IllegalArgumentException("cipher required with a block size of 16.");
        }
        gCMMultiplier = gCMMultiplier == null ? new Tables4kGCMMultiplier() : gCMMultiplier;
        this.cipher = blockCipher;
        this.multiplier = gCMMultiplier;
    }

    @Override // org.bouncycastle.crypto.modes.AEADBlockCipher
    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/GCM";
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException {
        byte[] iv;
        KeyParameter key;
        this.forEncryption = z;
        this.macBlock = null;
        this.initialised = true;
        if (cipherParameters instanceof AEADParameters) {
            AEADParameters aEADParameters = (AEADParameters) cipherParameters;
            iv = aEADParameters.getNonce();
            this.initialAssociatedText = aEADParameters.getAssociatedText();
            int macSize = aEADParameters.getMacSize();
            if (macSize < 32 || macSize > 128 || macSize % 8 != 0) {
                throw new IllegalArgumentException("Invalid value for MAC size: " + macSize);
            }
            this.macSize = macSize / 8;
            key = aEADParameters.getKey();
        } else {
            if (!(cipherParameters instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("invalid parameters passed to GCM");
            }
            ParametersWithIV parametersWithIV = (ParametersWithIV) cipherParameters;
            iv = parametersWithIV.getIV();
            this.initialAssociatedText = null;
            this.macSize = 16;
            key = (KeyParameter) parametersWithIV.getParameters();
        }
        this.bufBlock = new byte[z ? 16 : 16 + this.macSize];
        if (iv == null || iv.length < 1) {
            throw new IllegalArgumentException("IV must be at least 1 byte");
        }
        if (z && this.nonce != null && Arrays.areEqual(this.nonce, iv)) {
            if (key == null) {
                throw new IllegalArgumentException("cannot reuse nonce for GCM encryption");
            }
            if (this.lastKey != null && Arrays.areEqual(this.lastKey, key.getKey())) {
                throw new IllegalArgumentException("cannot reuse nonce for GCM encryption");
            }
        }
        this.nonce = iv;
        if (key != null) {
            this.lastKey = key.getKey();
        }
        if (key != null) {
            this.cipher.init(true, key);
            this.H = new byte[16];
            this.cipher.processBlock(this.H, 0, this.H, 0);
            this.multiplier.init(this.H);
            this.exp = null;
        } else if (this.H == null) {
            throw new IllegalArgumentException("Key must be specified in initial init");
        }
        this.J0 = new byte[16];
        if (this.nonce.length == 12) {
            System.arraycopy(this.nonce, 0, this.J0, 0, this.nonce.length);
            this.J0[15] = 1;
        } else {
            gHASH(this.J0, this.nonce, this.nonce.length);
            byte[] bArr = new byte[16];
            Pack.longToBigEndian(((long) this.nonce.length) * 8, bArr, 8);
            gHASHBlock(this.J0, bArr);
        }
        this.S = new byte[16];
        this.S_at = new byte[16];
        this.S_atPre = new byte[16];
        this.atBlock = new byte[16];
        this.atBlockPos = 0;
        this.atLength = 0L;
        this.atLengthPre = 0L;
        this.counter = Arrays.clone(this.J0);
        this.blocksRemaining = -2;
        this.bufOff = 0;
        this.totalLength = 0L;
        if (this.initialAssociatedText != null) {
            processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public byte[] getMac() {
        return this.macBlock == null ? new byte[this.macSize] : Arrays.clone(this.macBlock);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int getOutputSize(int i) {
        int i2 = i + this.bufOff;
        if (this.forEncryption) {
            return i2 + this.macSize;
        }
        if (i2 < this.macSize) {
            return 0;
        }
        return i2 - this.macSize;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int getUpdateOutputSize(int i) {
        int i2 = i + this.bufOff;
        if (!this.forEncryption) {
            if (i2 < this.macSize) {
                return 0;
            }
            i2 -= this.macSize;
        }
        return i2 - (i2 % 16);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void processAADByte(byte b) {
        checkStatus();
        this.atBlock[this.atBlockPos] = b;
        int i = this.atBlockPos + 1;
        this.atBlockPos = i;
        if (i == 16) {
            gHASHBlock(this.S_at, this.atBlock);
            this.atBlockPos = 0;
            this.atLength += 16;
        }
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void processAADBytes(byte[] bArr, int i, int i2) {
        checkStatus();
        if (this.atBlockPos > 0) {
            int i3 = 16 - this.atBlockPos;
            if (i2 < i3) {
                System.arraycopy(bArr, i, this.atBlock, this.atBlockPos, i2);
                this.atBlockPos += i2;
                return;
            } else {
                System.arraycopy(bArr, i, this.atBlock, this.atBlockPos, i3);
                gHASHBlock(this.S_at, this.atBlock);
                this.atLength += 16;
                i += i3;
                i2 -= i3;
            }
        }
        int i4 = (i + i2) - 16;
        while (i <= i4) {
            gHASHBlock(this.S_at, bArr, i);
            this.atLength += 16;
            i += 16;
        }
        this.atBlockPos = (16 + i4) - i;
        System.arraycopy(bArr, i, this.atBlock, 0, this.atBlockPos);
    }

    private void initCipher() {
        if (this.atLength > 0) {
            System.arraycopy(this.S_at, 0, this.S_atPre, 0, 16);
            this.atLengthPre = this.atLength;
        }
        if (this.atBlockPos > 0) {
            gHASHPartial(this.S_atPre, this.atBlock, 0, this.atBlockPos);
            this.atLengthPre += (long) this.atBlockPos;
        }
        if (this.atLengthPre > 0) {
            System.arraycopy(this.S_atPre, 0, this.S, 0, 16);
        }
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int processByte(byte b, byte[] bArr, int i) throws DataLengthException {
        checkStatus();
        this.bufBlock[this.bufOff] = b;
        int i2 = this.bufOff + 1;
        this.bufOff = i2;
        if (i2 != this.bufBlock.length) {
            return 0;
        }
        if (this.forEncryption) {
            encryptBlock(this.bufBlock, 0, bArr, i);
            this.bufOff = 0;
            return 16;
        }
        decryptBlock(this.bufBlock, 0, bArr, i);
        System.arraycopy(this.bufBlock, 16, this.bufBlock, 0, this.macSize);
        this.bufOff = this.macSize;
        return 16;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException {
        checkStatus();
        if (bArr.length - i < i2) {
            throw new DataLengthException("Input buffer too short");
        }
        int i4 = 0;
        if (this.forEncryption) {
            if (this.bufOff > 0) {
                int i5 = 16 - this.bufOff;
                if (i2 < i5) {
                    System.arraycopy(bArr, i, this.bufBlock, this.bufOff, i2);
                    this.bufOff += i2;
                    return 0;
                }
                System.arraycopy(bArr, i, this.bufBlock, this.bufOff, i5);
                encryptBlock(this.bufBlock, 0, bArr2, i3);
                i += i5;
                i2 -= i5;
                i4 = 16;
            }
            int i6 = (i + i2) - 16;
            while (i <= i6) {
                encryptBlock(bArr, i, bArr2, i3 + i4);
                i += 16;
                i4 += 16;
            }
            this.bufOff = (16 + i6) - i;
            System.arraycopy(bArr, i, this.bufBlock, 0, this.bufOff);
        } else {
            int length = this.bufBlock.length - this.bufOff;
            if (i2 < length) {
                System.arraycopy(bArr, i, this.bufBlock, this.bufOff, i2);
                this.bufOff += i2;
                return 0;
            }
            if (this.bufOff >= 16) {
                decryptBlock(this.bufBlock, 0, bArr2, i3);
                byte[] bArr3 = this.bufBlock;
                byte[] bArr4 = this.bufBlock;
                int i7 = this.bufOff - 16;
                this.bufOff = i7;
                System.arraycopy(bArr3, 16, bArr4, 0, i7);
                i4 = 16;
                if (i2 < length + 16) {
                    System.arraycopy(bArr, i, this.bufBlock, this.bufOff, i2);
                    this.bufOff += i2;
                    return 16;
                }
            }
            int length2 = (i + i2) - this.bufBlock.length;
            int i8 = 16 - this.bufOff;
            System.arraycopy(bArr, i, this.bufBlock, this.bufOff, i8);
            decryptBlock(this.bufBlock, 0, bArr2, i3 + i4);
            int i9 = i + i8;
            while (true) {
                i4 += 16;
                if (i9 > length2) {
                    break;
                }
                decryptBlock(bArr, i9, bArr2, i3 + i4);
                i9 += 16;
            }
            this.bufOff = (this.bufBlock.length + length2) - i9;
            System.arraycopy(bArr, i9, this.bufBlock, 0, this.bufOff);
        }
        return i4;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int doFinal(byte[] bArr, int i) throws InvalidCipherTextException, IllegalStateException {
        checkStatus();
        if (this.totalLength == 0) {
            initCipher();
        }
        int i2 = this.bufOff;
        if (this.forEncryption) {
            if (bArr.length - i < i2 + this.macSize) {
                throw new OutputLengthException("Output buffer too short");
            }
        } else {
            if (i2 < this.macSize) {
                throw new InvalidCipherTextException("data too short");
            }
            i2 -= this.macSize;
            if (bArr.length - i < i2) {
                throw new OutputLengthException("Output buffer too short");
            }
        }
        if (i2 > 0) {
            processPartial(this.bufBlock, 0, i2, bArr, i);
        }
        this.atLength += (long) this.atBlockPos;
        if (this.atLength > this.atLengthPre) {
            if (this.atBlockPos > 0) {
                gHASHPartial(this.S_at, this.atBlock, 0, this.atBlockPos);
            }
            if (this.atLengthPre > 0) {
                GCMUtil.xor(this.S_at, this.S_atPre);
            }
            long j = ((this.totalLength * 8) + 127) >>> 7;
            byte[] bArr2 = new byte[16];
            if (this.exp == null) {
                this.exp = new BasicGCMExponentiator();
                this.exp.init(this.H);
            }
            this.exp.exponentiateX(j, bArr2);
            GCMUtil.multiply(this.S_at, bArr2);
            GCMUtil.xor(this.S, this.S_at);
        }
        byte[] bArr3 = new byte[16];
        Pack.longToBigEndian(this.atLength * 8, bArr3, 0);
        Pack.longToBigEndian(this.totalLength * 8, bArr3, 8);
        gHASHBlock(this.S, bArr3);
        byte[] bArr4 = new byte[16];
        this.cipher.processBlock(this.J0, 0, bArr4, 0);
        GCMUtil.xor(bArr4, this.S);
        int i3 = i2;
        this.macBlock = new byte[this.macSize];
        System.arraycopy(bArr4, 0, this.macBlock, 0, this.macSize);
        if (this.forEncryption) {
            System.arraycopy(this.macBlock, 0, bArr, i + this.bufOff, this.macSize);
            i3 += this.macSize;
        } else {
            byte[] bArr5 = new byte[this.macSize];
            System.arraycopy(this.bufBlock, i2, bArr5, 0, this.macSize);
            if (!Arrays.constantTimeAreEqual(this.macBlock, bArr5)) {
                throw new InvalidCipherTextException("mac check in GCM failed");
            }
        }
        reset(false);
        return i3;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void reset() {
        reset(true);
    }

    private void reset(boolean z) {
        this.cipher.reset();
        this.S = new byte[16];
        this.S_at = new byte[16];
        this.S_atPre = new byte[16];
        this.atBlock = new byte[16];
        this.atBlockPos = 0;
        this.atLength = 0L;
        this.atLengthPre = 0L;
        this.counter = Arrays.clone(this.J0);
        this.blocksRemaining = -2;
        this.bufOff = 0;
        this.totalLength = 0L;
        if (this.bufBlock != null) {
            Arrays.fill(this.bufBlock, (byte) 0);
        }
        if (z) {
            this.macBlock = null;
        }
        if (this.forEncryption) {
            this.initialised = false;
        } else if (this.initialAssociatedText != null) {
            processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    private void decryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (bArr2.length - i2 < 16) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (this.totalLength == 0) {
            initCipher();
        }
        byte[] bArr3 = new byte[16];
        getNextCTRBlock(bArr3);
        gHASHBlock(this.S, bArr, i);
        GCMUtil.xor(bArr3, 0, bArr, i, bArr2, i2);
        this.totalLength += 16;
    }

    private void encryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (bArr2.length - i2 < 16) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (this.totalLength == 0) {
            initCipher();
        }
        byte[] bArr3 = new byte[16];
        getNextCTRBlock(bArr3);
        GCMUtil.xor(bArr3, bArr, i);
        gHASHBlock(this.S, bArr3);
        System.arraycopy(bArr3, 0, bArr2, i2, 16);
        this.totalLength += 16;
    }

    private void processPartial(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        byte[] bArr3 = new byte[16];
        getNextCTRBlock(bArr3);
        if (this.forEncryption) {
            GCMUtil.xor(bArr, i, bArr3, 0, i2);
            gHASHPartial(this.S, bArr, i, i2);
        } else {
            gHASHPartial(this.S, bArr, i, i2);
            GCMUtil.xor(bArr, i, bArr3, 0, i2);
        }
        System.arraycopy(bArr, i, bArr2, i3, i2);
        this.totalLength += (long) i2;
    }

    private void gHASH(byte[] bArr, byte[] bArr2, int i) {
        for (int i2 = 0; i2 < i; i2 += 16) {
            gHASHPartial(bArr, bArr2, i2, Math.min(i - i2, 16));
        }
    }

    private void gHASHBlock(byte[] bArr, byte[] bArr2) {
        GCMUtil.xor(bArr, bArr2);
        this.multiplier.multiplyH(bArr);
    }

    private void gHASHBlock(byte[] bArr, byte[] bArr2, int i) {
        GCMUtil.xor(bArr, bArr2, i);
        this.multiplier.multiplyH(bArr);
    }

    private void gHASHPartial(byte[] bArr, byte[] bArr2, int i, int i2) {
        GCMUtil.xor(bArr, bArr2, i, i2);
        this.multiplier.multiplyH(bArr);
    }

    private void getNextCTRBlock(byte[] bArr) {
        if (this.blocksRemaining == 0) {
            throw new IllegalStateException("Attempt to process too many blocks");
        }
        this.blocksRemaining--;
        int i = 1 + (this.counter[15] & 255);
        this.counter[15] = (byte) i;
        int i2 = (i >>> 8) + (this.counter[14] & 255);
        this.counter[14] = (byte) i2;
        int i3 = (i2 >>> 8) + (this.counter[13] & 255);
        this.counter[13] = (byte) i3;
        this.counter[12] = (byte) ((i3 >>> 8) + (this.counter[12] & 255));
        this.cipher.processBlock(this.counter, 0, bArr, 0);
    }

    private void checkStatus() {
        if (this.initialised) {
            return;
        }
        if (!this.forEncryption) {
            throw new IllegalStateException("GCM cipher needs to be initialised");
        }
        throw new IllegalStateException("GCM cipher cannot be reused for encryption");
    }
}
