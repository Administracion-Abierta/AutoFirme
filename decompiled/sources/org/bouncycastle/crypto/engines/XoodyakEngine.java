package org.bouncycastle.crypto.engines;

import com.sun.jna.platform.win32.WinError;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/XoodyakEngine.class */
public class XoodyakEngine extends AEADBufferBaseEngine {
    private byte[] state;
    private int phase;
    private MODE mode;
    private byte[] K;
    private byte[] iv;
    private boolean encrypted;
    private byte aadcd;
    private final int f_bPrime_1 = 47;
    private final int PhaseUp = 2;
    private final int[] RC = {88, 56, 960, WinError.ERROR_META_EXPANSION_TOO_LONG, 288, 20, 96, 44, 896, 240, 416, 18};

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/XoodyakEngine$MODE.class */
    enum MODE {
        ModeHash,
        ModeKeyed
    }

    public XoodyakEngine() {
        this.algorithmName = "Xoodyak AEAD";
        this.KEY_SIZE = 16;
        this.IV_SIZE = 16;
        this.MAC_SIZE = 16;
        this.BlockSize = 24;
        this.AADBufferSize = 44;
        this.m_aad = new byte[this.AADBufferSize];
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    public void init(byte[] bArr, byte[] bArr2) throws IllegalArgumentException {
        this.K = bArr;
        this.iv = bArr2;
        this.state = new byte[48];
        this.mac = new byte[this.MAC_SIZE];
        this.m_buf = new byte[this.BlockSize + (this.forEncryption ? 0 : this.MAC_SIZE)];
        this.initialised = true;
        this.m_state = this.forEncryption ? AEADBufferBaseEngine.State.EncInit : AEADBufferBaseEngine.State.DecInit;
        reset();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    protected void processBufferAAD(byte[] bArr, int i) {
        AbsorbAny(bArr, i, this.AADBufferSize, this.aadcd);
        this.aadcd = (byte) 0;
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    protected void processFinalAAD() {
        if (this.mode != MODE.ModeKeyed) {
            throw new IllegalArgumentException("Xoodyak has not been initialised");
        }
        if (this.aadFinished) {
            return;
        }
        AbsorbAny(this.m_aad, 0, this.m_aadPos, this.aadcd);
        this.aadFinished = true;
        this.m_aadPos = 0;
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    protected void processBuffer(byte[] bArr, int i, byte[] bArr2, int i2) {
        processFinalAAD();
        encrypt(bArr, i, this.BlockSize, bArr2, i2);
    }

    private void encrypt(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        int i4 = i2;
        byte[] bArr3 = new byte[this.BlockSize];
        int i5 = this.encrypted ? 0 : 128;
        while (true) {
            if (i4 == 0 && this.encrypted) {
                return;
            }
            int iMin = Math.min(i4, this.BlockSize);
            if (this.forEncryption) {
                System.arraycopy(bArr, i, bArr3, 0, iMin);
            }
            Up(null, 0, i5);
            for (int i6 = 0; i6 < iMin; i6++) {
                int i7 = i;
                i++;
                bArr2[i3 + i6] = (byte) (bArr[i7] ^ this.state[i6]);
            }
            if (this.forEncryption) {
                Down(bArr3, 0, iMin, 0);
            } else {
                Down(bArr2, i3, iMin, 0);
            }
            i5 = 0;
            i3 += iMin;
            i4 -= iMin;
            this.encrypted = true;
        }
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    protected void processFinalBlock(byte[] bArr, int i) {
        processFinalAAD();
        if (this.forEncryption) {
            Arrays.fill(this.m_buf, this.m_bufPos, this.BlockSize, (byte) 0);
        }
        encrypt(this.m_buf, 0, this.m_bufPos, bArr, i);
        this.mac = new byte[this.MAC_SIZE];
        Up(this.mac, this.MAC_SIZE, 64);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    protected void reset(boolean z) {
        if (!this.initialised) {
            throw new IllegalArgumentException("Need call init function before encryption/decryption");
        }
        Arrays.fill(this.state, (byte) 0);
        this.aadFinished = false;
        this.encrypted = false;
        this.phase = 2;
        Arrays.fill(this.m_buf, (byte) 0);
        Arrays.fill(this.m_aad, (byte) 0);
        this.m_bufPos = 0;
        this.m_aadPos = 0;
        this.aadcd = (byte) 3;
        int length = this.K.length;
        int length2 = this.iv.length;
        byte[] bArr = new byte[this.AADBufferSize];
        this.mode = MODE.ModeKeyed;
        System.arraycopy(this.K, 0, bArr, 0, length);
        System.arraycopy(this.iv, 0, bArr, length, length2);
        bArr[length + length2] = (byte) length2;
        AbsorbAny(bArr, 0, length + length2 + 1, 2);
        super.reset(z);
    }

    private void AbsorbAny(byte[] bArr, int i, int i2, int i3) {
        do {
            if (this.phase != 2) {
                Up(null, 0, 0);
            }
            int iMin = Math.min(i2, this.AADBufferSize);
            Down(bArr, i, iMin, i3);
            i3 = 0;
            i += iMin;
            i2 -= iMin;
        } while (i2 != 0);
    }

    private void Up(byte[] bArr, int i, int i2) {
        if (this.mode != MODE.ModeHash) {
            byte[] bArr2 = this.state;
            bArr2[47] = (byte) (bArr2[47] ^ i2);
        }
        int iLittleEndianToInt = Pack.littleEndianToInt(this.state, 0);
        int iLittleEndianToInt2 = Pack.littleEndianToInt(this.state, 4);
        int iLittleEndianToInt3 = Pack.littleEndianToInt(this.state, 8);
        int iLittleEndianToInt4 = Pack.littleEndianToInt(this.state, 12);
        int iLittleEndianToInt5 = Pack.littleEndianToInt(this.state, 16);
        int iLittleEndianToInt6 = Pack.littleEndianToInt(this.state, 20);
        int iLittleEndianToInt7 = Pack.littleEndianToInt(this.state, 24);
        int iLittleEndianToInt8 = Pack.littleEndianToInt(this.state, 28);
        int iLittleEndianToInt9 = Pack.littleEndianToInt(this.state, 32);
        int iLittleEndianToInt10 = Pack.littleEndianToInt(this.state, 36);
        int iLittleEndianToInt11 = Pack.littleEndianToInt(this.state, 40);
        int iLittleEndianToInt12 = Pack.littleEndianToInt(this.state, 44);
        for (int i3 = 0; i3 < 12; i3++) {
            int i4 = (iLittleEndianToInt ^ iLittleEndianToInt5) ^ iLittleEndianToInt9;
            int i5 = (iLittleEndianToInt2 ^ iLittleEndianToInt6) ^ iLittleEndianToInt10;
            int i6 = (iLittleEndianToInt3 ^ iLittleEndianToInt7) ^ iLittleEndianToInt11;
            int i7 = (iLittleEndianToInt4 ^ iLittleEndianToInt8) ^ iLittleEndianToInt12;
            int iRotateLeft = Integers.rotateLeft(i7, 5) ^ Integers.rotateLeft(i7, 14);
            int iRotateLeft2 = Integers.rotateLeft(i4, 5) ^ Integers.rotateLeft(i4, 14);
            int iRotateLeft3 = Integers.rotateLeft(i5, 5) ^ Integers.rotateLeft(i5, 14);
            int iRotateLeft4 = Integers.rotateLeft(i6, 5) ^ Integers.rotateLeft(i6, 14);
            int i8 = iLittleEndianToInt ^ iRotateLeft;
            int i9 = iLittleEndianToInt5 ^ iRotateLeft;
            int i10 = iLittleEndianToInt9 ^ iRotateLeft;
            int i11 = iLittleEndianToInt2 ^ iRotateLeft2;
            int i12 = iLittleEndianToInt6 ^ iRotateLeft2;
            int i13 = iLittleEndianToInt10 ^ iRotateLeft2;
            int i14 = iLittleEndianToInt3 ^ iRotateLeft3;
            int i15 = iLittleEndianToInt7 ^ iRotateLeft3;
            int i16 = iLittleEndianToInt11 ^ iRotateLeft3;
            int i17 = iLittleEndianToInt4 ^ iRotateLeft4;
            int i18 = iLittleEndianToInt8 ^ iRotateLeft4;
            int i19 = iLittleEndianToInt12 ^ iRotateLeft4;
            int iRotateLeft5 = Integers.rotateLeft(i10, 11);
            int iRotateLeft6 = Integers.rotateLeft(i13, 11);
            int iRotateLeft7 = Integers.rotateLeft(i16, 11);
            int iRotateLeft8 = Integers.rotateLeft(i19, 11);
            int i20 = i8 ^ this.RC[i3];
            iLittleEndianToInt = i20 ^ ((i18 ^ (-1)) & iRotateLeft5);
            iLittleEndianToInt2 = i11 ^ ((i9 ^ (-1)) & iRotateLeft6);
            iLittleEndianToInt3 = i14 ^ ((i12 ^ (-1)) & iRotateLeft7);
            iLittleEndianToInt4 = i17 ^ ((i15 ^ (-1)) & iRotateLeft8);
            int i21 = i18 ^ ((iRotateLeft5 ^ (-1)) & i20);
            int i22 = i9 ^ ((iRotateLeft6 ^ (-1)) & i11);
            int i23 = i12 ^ ((iRotateLeft7 ^ (-1)) & i14);
            int i24 = i15 ^ ((iRotateLeft8 ^ (-1)) & i17);
            int i25 = iRotateLeft5 ^ ((i20 ^ (-1)) & i18);
            int i26 = iRotateLeft6 ^ ((i11 ^ (-1)) & i9);
            iLittleEndianToInt5 = Integers.rotateLeft(i21, 1);
            iLittleEndianToInt6 = Integers.rotateLeft(i22, 1);
            iLittleEndianToInt7 = Integers.rotateLeft(i23, 1);
            iLittleEndianToInt8 = Integers.rotateLeft(i24, 1);
            iLittleEndianToInt9 = Integers.rotateLeft(iRotateLeft7 ^ ((i14 ^ (-1)) & i12), 8);
            iLittleEndianToInt10 = Integers.rotateLeft(iRotateLeft8 ^ ((i17 ^ (-1)) & i15), 8);
            iLittleEndianToInt11 = Integers.rotateLeft(i25, 8);
            iLittleEndianToInt12 = Integers.rotateLeft(i26, 8);
        }
        Pack.intToLittleEndian(iLittleEndianToInt, this.state, 0);
        Pack.intToLittleEndian(iLittleEndianToInt2, this.state, 4);
        Pack.intToLittleEndian(iLittleEndianToInt3, this.state, 8);
        Pack.intToLittleEndian(iLittleEndianToInt4, this.state, 12);
        Pack.intToLittleEndian(iLittleEndianToInt5, this.state, 16);
        Pack.intToLittleEndian(iLittleEndianToInt6, this.state, 20);
        Pack.intToLittleEndian(iLittleEndianToInt7, this.state, 24);
        Pack.intToLittleEndian(iLittleEndianToInt8, this.state, 28);
        Pack.intToLittleEndian(iLittleEndianToInt9, this.state, 32);
        Pack.intToLittleEndian(iLittleEndianToInt10, this.state, 36);
        Pack.intToLittleEndian(iLittleEndianToInt11, this.state, 40);
        Pack.intToLittleEndian(iLittleEndianToInt12, this.state, 44);
        this.phase = 2;
        if (bArr != null) {
            System.arraycopy(this.state, 0, bArr, 0, i);
        }
    }

    void Down(byte[] bArr, int i, int i2, int i3) {
        for (int i4 = 0; i4 < i2; i4++) {
            byte[] bArr2 = this.state;
            int i5 = i4;
            int i6 = i;
            i++;
            bArr2[i5] = (byte) (bArr2[i5] ^ bArr[i6]);
        }
        byte[] bArr3 = this.state;
        bArr3[i2] = (byte) (bArr3[i2] ^ 1);
        byte[] bArr4 = this.state;
        bArr4[47] = (byte) (bArr4[47] ^ (this.mode == MODE.ModeHash ? i3 & 1 : i3));
        this.phase = 1;
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int getOutputSize(int i) {
        return super.getOutputSize(i);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int getUpdateOutputSize(int i) {
        return super.getUpdateOutputSize(i);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    public /* bridge */ /* synthetic */ int getBlockSize() {
        return super.getBlockSize();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int doFinal(byte[] bArr, int i) throws InvalidCipherTextException, IllegalStateException {
        return super.doFinal(bArr, i);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException {
        return super.processBytes(bArr, i, i2, bArr2, i3);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ void processAADBytes(byte[] bArr, int i, int i2) {
        super.processAADBytes(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ void processAADByte(byte b) {
        super.processAADByte(b);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ void init(boolean z, CipherParameters cipherParameters) {
        super.init(z, cipherParameters);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int processByte(byte b, byte[] bArr, int i) throws DataLengthException {
        return super.processByte(b, bArr, i);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ void reset() {
        super.reset();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ byte[] getMac() {
        return super.getMac();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    public /* bridge */ /* synthetic */ int getIVBytesSize() {
        return super.getIVBytesSize();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    public /* bridge */ /* synthetic */ int getKeyBytesSize() {
        return super.getKeyBytesSize();
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ String getAlgorithmName() {
        return super.getAlgorithmName();
    }
}
