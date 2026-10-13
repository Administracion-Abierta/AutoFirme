package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/PhotonBeetleEngine.class */
public class PhotonBeetleEngine extends AEADBufferBaseEngine {
    private boolean input_empty;
    private byte[] K;
    private byte[] N;
    private byte[] state;
    private byte[][] state_2d;
    private int aadLen;
    private int messageLen;
    private final int RATE_INBYTES_HALF;
    private final int STATE_INBYTES;
    private final int LAST_THREE_BITS_OFFSET;
    private final int D = 8;
    private final byte[][] RC = {new byte[]{1, 3, 7, 14, 13, 11, 6, 12, 9, 2, 5, 10}, new byte[]{0, 2, 6, 15, 12, 10, 7, 13, 8, 3, 4, 11}, new byte[]{2, 0, 4, 13, 14, 8, 5, 15, 10, 1, 6, 9}, new byte[]{6, 4, 0, 9, 10, 12, 1, 11, 14, 5, 2, 13}, new byte[]{14, 12, 8, 1, 2, 4, 9, 3, 6, 13, 10, 5}, new byte[]{15, 13, 9, 0, 3, 5, 8, 2, 7, 12, 11, 4}, new byte[]{13, 15, 11, 2, 1, 7, 10, 0, 5, 14, 9, 6}, new byte[]{9, 11, 15, 6, 5, 3, 14, 4, 1, 10, 13, 2}};
    private final byte[][] MixColMatrix = {new byte[]{2, 4, 2, 11, 2, 8, 5, 6}, new byte[]{12, 9, 8, 13, 7, 7, 5, 2}, new byte[]{4, 4, 13, 13, 9, 4, 13, 9}, new byte[]{1, 6, 5, 1, 12, 13, 15, 14}, new byte[]{15, 12, 9, 13, 14, 5, 14, 13}, new byte[]{9, 14, 5, 15, 4, 12, 9, 6}, new byte[]{12, 2, 2, 10, 3, 1, 1, 14}, new byte[]{15, 1, 13, 10, 5, 10, 2, 3}};
    private final byte[] sbox = {12, 5, 6, 11, 9, 0, 10, 13, 3, 14, 15, 8, 4, 7, 1, 2};

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/PhotonBeetleEngine$PhotonBeetleParameters.class */
    public enum PhotonBeetleParameters {
        pb32,
        pb128
    }

    public PhotonBeetleEngine(PhotonBeetleParameters photonBeetleParameters) {
        this.KEY_SIZE = 16;
        this.IV_SIZE = 16;
        this.MAC_SIZE = 16;
        int i = 0;
        int i2 = 0;
        switch (photonBeetleParameters) {
            case pb32:
                i2 = 32;
                i = 224;
                break;
            case pb128:
                i2 = 128;
                i = 128;
                break;
        }
        int i3 = (i2 + 7) >>> 3;
        this.BlockSize = i3;
        this.AADBufferSize = i3;
        this.RATE_INBYTES_HALF = this.BlockSize >>> 1;
        int i4 = i2 + i;
        this.STATE_INBYTES = (i4 + 7) >>> 3;
        this.LAST_THREE_BITS_OFFSET = (i4 - ((this.STATE_INBYTES - 1) << 3)) - 3;
        this.initialised = false;
        this.algorithmName = "Photon-Beetle AEAD";
        this.m_aad = new byte[this.AADBufferSize];
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    protected void init(byte[] bArr, byte[] bArr2) throws IllegalArgumentException {
        this.K = bArr;
        this.N = bArr2;
        this.state = new byte[this.STATE_INBYTES];
        this.state_2d = new byte[8][8];
        this.mac = new byte[this.MAC_SIZE];
        this.initialised = true;
        this.m_buf = new byte[this.BlockSize + (this.forEncryption ? 0 : this.MAC_SIZE)];
        this.m_state = this.forEncryption ? AEADBufferBaseEngine.State.EncInit : AEADBufferBaseEngine.State.DecInit;
        reset(false);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    protected void processBufferAAD(byte[] bArr, int i) {
        PHOTON_Permutation();
        XOR(bArr, i, this.BlockSize);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    protected void processBuffer(byte[] bArr, int i, byte[] bArr2, int i2) {
        PHOTON_Permutation();
        rhoohr(bArr2, i2, bArr, i, this.BlockSize);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public void processAADByte(byte b) {
        this.aadLen++;
        super.processAADByte(b);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public void processAADBytes(byte[] bArr, int i, int i2) {
        this.aadLen += i2;
        super.processAADBytes(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException {
        this.messageLen += i2;
        return super.processBytes(bArr, i, i2, bArr2, i3);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    protected void processFinalBlock(byte[] bArr, int i) {
        int i2 = this.messageLen - (this.forEncryption ? 0 : this.MAC_SIZE);
        int i3 = this.m_bufPos;
        if (this.aadLen != 0 || i2 != 0) {
            this.input_empty = false;
        }
        byte bSelect = select(this.aadLen != 0, i2 % this.BlockSize == 0, (byte) 5, (byte) 6);
        if (i2 != 0) {
            if (i3 != 0) {
                PHOTON_Permutation();
                rhoohr(bArr, i, this.m_buf, 0, i3);
                if (i3 < this.BlockSize) {
                    byte[] bArr2 = this.state;
                    bArr2[i3] = (byte) (bArr2[i3] ^ 1);
                }
            }
            byte[] bArr3 = this.state;
            int i4 = this.STATE_INBYTES - 1;
            bArr3[i4] = (byte) (bArr3[i4] ^ (bSelect << this.LAST_THREE_BITS_OFFSET));
        }
        if (this.input_empty) {
            byte[] bArr4 = this.state;
            int i5 = this.STATE_INBYTES - 1;
            bArr4[i5] = (byte) (bArr4[i5] ^ (1 << this.LAST_THREE_BITS_OFFSET));
        }
        PHOTON_Permutation();
        this.mac = new byte[this.MAC_SIZE];
        System.arraycopy(this.state, 0, this.mac, 0, this.MAC_SIZE);
    }

    @Override // org.bouncycastle.crypto.engines.AEADBufferBaseEngine
    protected void processFinalAAD() {
        if (this.aadFinished) {
            return;
        }
        if (this.aadLen != 0) {
            if (this.m_aadPos != 0) {
                PHOTON_Permutation();
                XOR(this.m_aad, 0, this.m_aadPos);
                if (this.m_aadPos < this.BlockSize) {
                    byte[] bArr = this.state;
                    int i = this.m_aadPos;
                    bArr[i] = (byte) (bArr[i] ^ 1);
                }
            }
            byte[] bArr2 = this.state;
            int i2 = this.STATE_INBYTES - 1;
            bArr2[i2] = (byte) (bArr2[i2] ^ (select(this.messageLen - (this.forEncryption ? 0 : this.MAC_SIZE) > 0, this.aadLen % this.BlockSize == 0, (byte) 3, (byte) 4) << this.LAST_THREE_BITS_OFFSET));
        }
        this.m_aadPos = 0;
        this.aadFinished = true;
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    protected void reset(boolean z) {
        if (!this.initialised) {
            throw new IllegalArgumentException("Need call init function before encryption/decryption");
        }
        bufferReset();
        this.input_empty = true;
        this.aadLen = 0;
        this.aadFinished = false;
        this.messageLen = 0;
        System.arraycopy(this.K, 0, this.state, 0, this.K.length);
        System.arraycopy(this.N, 0, this.state, this.K.length, this.N.length);
        super.reset(z);
    }

    private void PHOTON_Permutation() {
        for (int i = 0; i < 64; i++) {
            this.state_2d[i >>> 3][i & 7] = (byte) (((this.state[i >> 1] & 255) >>> (4 * (i & 1))) & 15);
        }
        for (int i2 = 0; i2 < 12; i2++) {
            for (int i3 = 0; i3 < 8; i3++) {
                byte[] bArr = this.state_2d[i3];
                bArr[0] = (byte) (bArr[0] ^ this.RC[i3][i2]);
            }
            for (int i4 = 0; i4 < 8; i4++) {
                for (int i5 = 0; i5 < 8; i5++) {
                    this.state_2d[i4][i5] = this.sbox[this.state_2d[i4][i5]];
                }
            }
            for (int i6 = 1; i6 < 8; i6++) {
                System.arraycopy(this.state_2d[i6], 0, this.state, 0, 8);
                System.arraycopy(this.state, i6, this.state_2d[i6], 0, 8 - i6);
                System.arraycopy(this.state, 0, this.state_2d[i6], 8 - i6, i6);
            }
            for (int i7 = 0; i7 < 8; i7++) {
                for (int i8 = 0; i8 < 8; i8++) {
                    int i9 = 0;
                    for (int i10 = 0; i10 < 8; i10++) {
                        byte b = this.MixColMatrix[i8][i10];
                        byte b2 = this.state_2d[i10][i7];
                        i9 = (((i9 ^ (b * (b2 & 1))) ^ (b * (b2 & 2))) ^ (b * (b2 & 4))) ^ (b * (b2 & 8));
                    }
                    int i11 = i9 >>> 4;
                    int i12 = ((i9 & 15) ^ i11) ^ (i11 << 1);
                    int i13 = i12 >>> 4;
                    this.state[i8] = (byte) (((i12 & 15) ^ i13) ^ (i13 << 1));
                }
                for (int i14 = 0; i14 < 8; i14++) {
                    this.state_2d[i14][i7] = this.state[i14];
                }
            }
        }
        for (int i15 = 0; i15 < 64; i15 += 2) {
            this.state[i15 >>> 1] = (byte) ((this.state_2d[i15 >>> 3][i15 & 7] & 15) | ((this.state_2d[i15 >>> 3][(i15 + 1) & 7] & 15) << 4));
        }
    }

    private byte select(boolean z, boolean z2, byte b, byte b2) {
        if (z && z2) {
            return (byte) 1;
        }
        if (z) {
            return (byte) 2;
        }
        return z2 ? b : b2;
    }

    private void rhoohr(byte[] bArr, int i, byte[] bArr2, int i2, int i3) {
        byte[] bArr3 = this.state_2d[0];
        int iMin = Math.min(i3, this.RATE_INBYTES_HALF);
        int i4 = 0;
        while (i4 < this.RATE_INBYTES_HALF - 1) {
            bArr3[i4] = (byte) (((this.state[i4] & 255) >>> 1) | ((this.state[i4 + 1] & 1) << 7));
            i4++;
        }
        bArr3[this.RATE_INBYTES_HALF - 1] = (byte) (((this.state[i4] & 255) >>> 1) | ((this.state[0] & 1) << 7));
        int i5 = 0;
        while (i5 < iMin) {
            int i6 = i5 + i;
            byte b = this.state[i5 + this.RATE_INBYTES_HALF];
            int i7 = i5;
            i5++;
            bArr[i6] = (byte) (b ^ bArr2[i7 + i2]);
        }
        while (i5 < i3) {
            int i8 = i5 + i;
            byte b2 = bArr3[i5 - this.RATE_INBYTES_HALF];
            int i9 = i5;
            i5++;
            bArr[i8] = (byte) (b2 ^ bArr2[i9 + i2]);
        }
        if (this.forEncryption) {
            XOR(bArr2, i2, i3);
        } else {
            XOR(bArr, i, i3);
        }
    }

    private void XOR(byte[] bArr, int i, int i2) {
        for (int i3 = 0; i3 < i2; i3++) {
            byte[] bArr2 = this.state;
            int i4 = i3;
            int i5 = i;
            i++;
            bArr2[i4] = (byte) (bArr2[i4] ^ bArr[i5]);
        }
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
