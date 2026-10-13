package org.bouncycastle.crypto.engines;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ElephantEngine.class */
public class ElephantEngine extends AEADBaseEngine {
    private final ElephantParameters parameters;
    private final int BLOCK_SIZE;
    private int nBits;
    private int nSBox;
    private final int nRounds;
    private byte lfsrIV;
    private byte[] npub;
    private byte[] expanded_key;
    private boolean initialised;
    private int nb_its;
    private byte[] ad;
    private int adOff;
    private int adlen;
    private final byte[] tag_buffer;
    private byte[] previous_mask;
    private byte[] current_mask;
    private byte[] next_mask;
    private final byte[] buffer;
    private final byte[] previous_outputMessage;
    private int inputOff;
    private byte[] inputMessage;
    private int messageLen;
    private State m_state = State.Uninitialized;
    private final ByteArrayOutputStream aadData = new ByteArrayOutputStream();
    private final byte[] sBoxLayer = {-18, -19, -21, -32, -30, -31, -28, -17, -25, -22, -24, -27, -23, -20, -29, -26, -34, -35, -37, -48, -46, -47, -44, -33, -41, -38, -40, -43, -39, -36, -45, -42, -66, -67, -69, -80, -78, -79, -76, -65, -73, -70, -72, -75, -71, -68, -77, -74, 14, 13, 11, 0, 2, 1, 4, 15, 7, 10, 8, 5, 9, 12, 3, 6, 46, 45, 43, 32, 34, 33, 36, 47, 39, 42, 40, 37, 41, 44, 35, 38, 30, 29, 27, 16, 18, 17, 20, 31, 23, 26, 24, 21, 25, 28, 19, 22, 78, 77, 75, 64, 66, 65, 68, 79, 71, 74, 72, 69, 73, 76, 67, 70, -2, -3, -5, -16, -14, -15, -12, -1, -9, -6, -8, -11, -7, -4, -13, -10, 126, 125, 123, 112, 114, 113, 116, 127, 119, 122, 120, 117, 121, 124, 115, 118, -82, -83, -85, -96, -94, -95, -92, -81, -89, -86, -88, -91, -87, -84, -93, -90, -114, -115, -117, -128, -126, -127, -124, -113, -121, -118, -120, -123, -119, -116, -125, -122, 94, 93, 91, 80, 82, 81, 84, 95, 87, 90, 88, 85, 89, 92, 83, 86, -98, -99, -101, -112, -110, -111, -108, -97, -105, -102, -104, -107, -103, -100, -109, -106, -50, -51, -53, -64, -62, -63, -60, -49, -57, -54, -56, -59, -55, -52, -61, -58, 62, 61, 59, 48, 50, 49, 52, 63, 55, 58, 56, 53, 57, 60, 51, 54, 110, 109, 107, 96, 98, 97, 100, 111, 103, 106, 104, 101, 105, 108, 99, 102};
    private final byte[] KeccakRoundConstants = {1, -126, -118, 0, -117, 1, -127, 9, -118, -120, 9, 10, -117, -117, -119, 3, 2, -128};
    private final int[] KeccakRhoOffsets = {0, 1, 6, 4, 3, 4, 4, 6, 7, 4, 3, 2, 3, 1, 7, 1, 5, 7, 5, 0, 2, 2, 5, 0, 6};

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ElephantEngine$ElephantParameters.class */
    public enum ElephantParameters {
        elephant160,
        elephant176,
        elephant200
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ElephantEngine$State.class */
    private enum State {
        Uninitialized,
        EncInit,
        EncAad,
        EncData,
        EncFinal,
        DecInit,
        DecAad,
        DecData,
        DecFinal
    }

    public ElephantEngine(ElephantParameters elephantParameters) {
        this.KEY_SIZE = 16;
        this.IV_SIZE = 12;
        switch (elephantParameters) {
            case elephant160:
                this.BLOCK_SIZE = 20;
                this.nBits = 160;
                this.nSBox = 20;
                this.nRounds = 80;
                this.lfsrIV = (byte) 117;
                this.MAC_SIZE = 8;
                this.algorithmName = "Elephant 160 AEAD";
                break;
            case elephant176:
                this.BLOCK_SIZE = 22;
                this.nBits = 176;
                this.nSBox = 22;
                this.nRounds = 90;
                this.lfsrIV = (byte) 69;
                this.algorithmName = "Elephant 176 AEAD";
                this.MAC_SIZE = 8;
                break;
            case elephant200:
                this.BLOCK_SIZE = 25;
                this.nRounds = 18;
                this.algorithmName = "Elephant 200 AEAD";
                this.MAC_SIZE = 16;
                break;
            default:
                throw new IllegalArgumentException("Invalid parameter settings for Elephant");
        }
        this.parameters = elephantParameters;
        this.tag_buffer = new byte[this.BLOCK_SIZE];
        this.previous_mask = new byte[this.BLOCK_SIZE];
        this.current_mask = new byte[this.BLOCK_SIZE];
        this.next_mask = new byte[this.BLOCK_SIZE];
        this.buffer = new byte[this.BLOCK_SIZE];
        this.previous_outputMessage = new byte[this.BLOCK_SIZE];
        this.initialised = false;
        reset(false);
    }

    private void permutation(byte[] bArr) {
        switch (this.parameters) {
            case elephant160:
            case elephant176:
                byte b = this.lfsrIV;
                byte[] bArr2 = new byte[this.nSBox];
                for (int i = 0; i < this.nRounds; i++) {
                    bArr[0] = (byte) (bArr[0] ^ b);
                    int i2 = this.nSBox - 1;
                    bArr[i2] = (byte) (bArr[i2] ^ ((byte) (((((((((b & 1) << 7) | ((b & 2) << 5)) | ((b & 4) << 3)) | ((b & 8) << 1)) | ((b & 16) >>> 1)) | ((b & 32) >>> 3)) | ((b & 64) >>> 5)) | ((b & 128) >>> 7))));
                    b = (byte) (((b << 1) | (((64 & b) >>> 6) ^ ((32 & b) >>> 5))) & 127);
                    for (int i3 = 0; i3 < this.nSBox; i3++) {
                        bArr[i3] = this.sBoxLayer[bArr[i3] & 255];
                    }
                    Arrays.fill(bArr2, (byte) 0);
                    for (int i4 = 0; i4 < this.nSBox; i4++) {
                        for (int i5 = 0; i5 < 8; i5++) {
                            int i6 = (i4 << 3) + i5;
                            if (i6 != this.nBits - 1) {
                                i6 = ((i6 * this.nBits) >> 2) % (this.nBits - 1);
                            }
                            int i7 = i6 >>> 3;
                            bArr2[i7] = (byte) (bArr2[i7] ^ ((((bArr[i4] & 255) >>> i5) & 1) << (i6 & 7)));
                        }
                    }
                    System.arraycopy(bArr2, 0, bArr, 0, this.nSBox);
                }
                break;
            case elephant200:
                for (int i8 = 0; i8 < this.nRounds; i8++) {
                    KeccakP200Round(bArr, i8);
                }
                break;
        }
    }

    private byte rotl(byte b) {
        return (byte) (((b & 255) << 1) | ((b & 255) >>> 7));
    }

    private byte ROL8(byte b, int i) {
        return (byte) (i != 0 ? ((b & 255) << i) ^ ((b & 255) >>> (8 - i)) : b);
    }

    private int index(int i, int i2) {
        return i + (i2 * 5);
    }

    private void KeccakP200Round(byte[] bArr, int i) {
        byte[] bArr2 = new byte[25];
        for (int i2 = 0; i2 < 5; i2++) {
            for (int i3 = 0; i3 < 5; i3++) {
                int i4 = i2;
                bArr2[i4] = (byte) (bArr2[i4] ^ bArr[index(i2, i3)]);
            }
        }
        for (int i5 = 0; i5 < 5; i5++) {
            bArr2[i5 + 5] = (byte) (ROL8(bArr2[(i5 + 1) % 5], 1) ^ bArr2[(i5 + 4) % 5]);
        }
        for (int i6 = 0; i6 < 5; i6++) {
            for (int i7 = 0; i7 < 5; i7++) {
                int iIndex = index(i6, i7);
                bArr[iIndex] = (byte) (bArr[iIndex] ^ bArr2[i6 + 5]);
            }
        }
        for (int i8 = 0; i8 < 5; i8++) {
            for (int i9 = 0; i9 < 5; i9++) {
                bArr2[index(i8, i9)] = ROL8(bArr[index(i8, i9)], this.KeccakRhoOffsets[index(i8, i9)]);
            }
        }
        for (int i10 = 0; i10 < 5; i10++) {
            for (int i11 = 0; i11 < 5; i11++) {
                bArr[index(i11, ((2 * i10) + (3 * i11)) % 5)] = bArr2[index(i10, i11)];
            }
        }
        for (int i12 = 0; i12 < 5; i12++) {
            for (int i13 = 0; i13 < 5; i13++) {
                bArr2[i13] = (byte) (bArr[index(i13, i12)] ^ ((bArr[index((i13 + 1) % 5, i12)] ^ (-1)) & bArr[index((i13 + 2) % 5, i12)]));
            }
            for (int i14 = 0; i14 < 5; i14++) {
                bArr[index(i14, i12)] = bArr2[i14];
            }
        }
        bArr[0] = (byte) (bArr[0] ^ this.KeccakRoundConstants[i]);
    }

    private void lfsr_step(byte[] bArr, byte[] bArr2) {
        switch (this.parameters) {
            case elephant160:
                bArr[this.BLOCK_SIZE - 1] = (byte) (((((bArr2[0] & 255) << 3) | ((bArr2[0] & 255) >>> 5)) ^ ((bArr2[3] & 255) << 7)) ^ ((bArr2[13] & 255) >>> 7));
                break;
            case elephant176:
                bArr[this.BLOCK_SIZE - 1] = (byte) ((rotl(bArr2[0]) ^ ((bArr2[3] & 255) << 7)) ^ ((bArr2[19] & 255) >>> 7));
                break;
            case elephant200:
                bArr[this.BLOCK_SIZE - 1] = (byte) ((rotl(bArr2[0]) ^ rotl(bArr2[2])) ^ (bArr2[13] << 1));
                break;
        }
        System.arraycopy(bArr2, 1, bArr, 0, this.BLOCK_SIZE - 1);
    }

    private void xor_block(byte[] bArr, byte[] bArr2, int i, int i2) {
        for (int i3 = 0; i3 < i2; i3++) {
            int i4 = i3;
            bArr[i4] = (byte) (bArr[i4] ^ bArr2[i3 + i]);
        }
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    protected void init(byte[] bArr, byte[] bArr2) throws IllegalArgumentException {
        this.npub = bArr2;
        this.expanded_key = new byte[this.BLOCK_SIZE];
        System.arraycopy(bArr, 0, this.expanded_key, 0, this.KEY_SIZE);
        permutation(this.expanded_key);
        this.initialised = true;
        this.m_state = this.forEncryption ? State.EncInit : State.DecInit;
        this.inputMessage = new byte[(this.BLOCK_SIZE * 2) + (this.forEncryption ? 0 : this.MAC_SIZE)];
        reset(false);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void processAADByte(byte b) {
        this.aadData.write(b);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void processAADBytes(byte[] bArr, int i, int i2) {
        if (i + i2 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        this.aadData.write(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException {
        if (i + i2 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        if ((this.inputOff + i2) - (this.forEncryption ? 0 : this.MAC_SIZE) < this.BLOCK_SIZE) {
            System.arraycopy(bArr, i, this.inputMessage, this.inputOff, i2);
            this.inputOff += i2;
            return 0;
        }
        int i4 = ((this.inputOff + this.messageLen) + i2) - (this.forEncryption ? 0 : this.MAC_SIZE);
        int iProcessAADBytes = processAADBytes();
        int i5 = 1 + (i4 / this.BLOCK_SIZE);
        int i6 = i4 % this.BLOCK_SIZE != 0 ? i5 : i5 - 1;
        int i7 = 1 + ((this.IV_SIZE + iProcessAADBytes) / this.BLOCK_SIZE);
        int iMax = Math.max(i5 + 1, i7 - 1);
        byte[] bArr3 = new byte[Math.max(i5, 1) * this.BLOCK_SIZE];
        System.arraycopy(this.inputMessage, 0, bArr3, 0, this.inputOff);
        System.arraycopy(bArr, i, bArr3, this.inputOff, Math.min(i2, bArr3.length - this.inputOff));
        int iProcessBytes = processBytes(bArr3, bArr2, i3, iMax, i6, i5, i4, i7, false);
        int i8 = iProcessBytes - this.inputOff;
        if (i8 >= 0) {
            this.inputOff = (this.inputOff + i2) - iProcessBytes;
            System.arraycopy(bArr, i + i8, this.inputMessage, 0, this.inputOff);
        } else {
            System.arraycopy(this.inputMessage, this.inputOff + i8, this.inputMessage, 0, -i8);
            System.arraycopy(bArr, i, this.inputMessage, -i8, i2);
            this.inputOff = i2 - i8;
        }
        this.messageLen += iProcessBytes;
        return iProcessBytes;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int doFinal(byte[] bArr, int i) throws InvalidCipherTextException, IllegalStateException {
        if (!this.initialised) {
            throw new IllegalArgumentException(this.algorithmName + " needs call init function before doFinal");
        }
        int i2 = this.inputOff;
        if ((this.forEncryption && i2 + i + this.MAC_SIZE > bArr.length) || (!this.forEncryption && (i2 + i) - this.MAC_SIZE > bArr.length)) {
            throw new OutputLengthException("output buffer is too short");
        }
        int i3 = (i2 + this.messageLen) - (this.forEncryption ? 0 : this.MAC_SIZE);
        int i4 = i3 - this.messageLen;
        int iProcessAADBytes = processAADBytes();
        int i5 = 1 + (i3 / this.BLOCK_SIZE);
        int i6 = i3 % this.BLOCK_SIZE != 0 ? i5 : i5 - 1;
        int i7 = 1 + ((this.IV_SIZE + iProcessAADBytes) / this.BLOCK_SIZE);
        int iProcessBytes = i + processBytes(this.inputMessage, bArr, i, Math.max(i5 + 1, i7 - 1), i6, i5, i3, i7, true);
        this.mac = new byte[this.MAC_SIZE];
        xor_block(this.tag_buffer, this.expanded_key, 0, this.BLOCK_SIZE);
        permutation(this.tag_buffer);
        xor_block(this.tag_buffer, this.expanded_key, 0, this.BLOCK_SIZE);
        if (this.forEncryption) {
            System.arraycopy(this.tag_buffer, 0, this.mac, 0, this.MAC_SIZE);
            System.arraycopy(this.mac, 0, bArr, iProcessBytes, this.mac.length);
            i4 += this.MAC_SIZE;
        } else {
            this.inputOff -= this.MAC_SIZE;
            for (int i8 = 0; i8 < this.MAC_SIZE; i8++) {
                if (this.tag_buffer[i8] != this.inputMessage[this.inputOff + i8]) {
                    throw new IllegalArgumentException("Mac does not match");
                }
            }
        }
        reset(false);
        return i4;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int getUpdateOutputSize(int i) {
        switch (this.m_state) {
            case Uninitialized:
                throw new IllegalArgumentException(this.algorithmName + " needs call init function before getUpdateOutputSize");
            case EncInit:
            case EncAad:
            case EncData:
                int i2 = this.inputOff + i;
                return i2 - (i2 % this.BLOCK_SIZE);
            case EncFinal:
            case DecFinal:
                return 0;
            case DecInit:
            case DecAad:
            case DecData:
                int iMax = Math.max(0, (this.inputOff + i) - this.MAC_SIZE);
                return iMax - (iMax % this.BLOCK_SIZE);
            default:
                return Math.max(0, (i + this.inputOff) - this.MAC_SIZE);
        }
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int getOutputSize(int i) {
        switch (this.m_state) {
            case Uninitialized:
                throw new IllegalArgumentException(this.algorithmName + " needs call init function before getUpdateOutputSize");
            case EncInit:
            case EncAad:
            case EncData:
                return i + this.inputOff + this.MAC_SIZE;
            case EncFinal:
            case DecFinal:
                return 0;
            case DecInit:
            case DecAad:
            case DecData:
            default:
                return Math.max(0, (i + this.inputOff) - this.MAC_SIZE);
        }
    }

    private int processAADBytes() {
        byte[] byteArray = this.aadData.toByteArray();
        switch (this.m_state.ordinal()) {
            case 1:
            case 5:
                processAADBytes(this.tag_buffer);
                break;
        }
        return byteArray.length;
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    protected void reset(boolean z) {
        this.aadData.reset();
        Arrays.fill(this.tag_buffer, (byte) 0);
        Arrays.fill(this.previous_outputMessage, (byte) 0);
        this.inputOff = 0;
        this.nb_its = 0;
        this.adOff = -1;
        this.messageLen = 0;
        super.reset(z);
    }

    public int getBlockSize() {
        return this.BLOCK_SIZE;
    }

    private void checkAad() {
        switch (this.m_state.ordinal()) {
            case 3:
                throw new IllegalArgumentException(this.algorithmName + " cannot process AAD when the length of the ciphertext to be processed exceeds the a block size");
            case 4:
                throw new IllegalArgumentException(this.algorithmName + " cannot be reused for encryption");
            case 5:
            case 6:
            default:
                return;
            case 7:
                throw new IllegalArgumentException(this.algorithmName + " cannot process AAD when the length of the plaintext to be processed exceeds the a block size");
        }
    }

    private void processAADBytes(byte[] bArr) {
        checkAad();
        if (this.adOff == -1) {
            this.adlen = this.aadData.size();
            this.ad = this.aadData.toByteArray();
            this.adOff = 0;
        }
        int i = 0;
        switch (this.m_state.ordinal()) {
            case 1:
                System.arraycopy(this.expanded_key, 0, this.current_mask, 0, this.BLOCK_SIZE);
                System.arraycopy(this.npub, 0, bArr, 0, this.IV_SIZE);
                i = 0 + this.IV_SIZE;
                this.m_state = State.EncAad;
                break;
            case 2:
            case 6:
                if (this.adOff == this.adlen) {
                    Arrays.fill(bArr, 0, this.BLOCK_SIZE, (byte) 0);
                    bArr[0] = 1;
                    return;
                }
                break;
            case 3:
                throw new IllegalArgumentException(this.algorithmName + " cannot process AAD when the length of the ciphertext to be processed exceeds the a block size");
            case 4:
                throw new IllegalArgumentException(this.algorithmName + " cannot be reused for encryption");
            case 5:
                System.arraycopy(this.expanded_key, 0, this.current_mask, 0, this.BLOCK_SIZE);
                System.arraycopy(this.npub, 0, bArr, 0, this.IV_SIZE);
                i = 0 + this.IV_SIZE;
                this.m_state = State.DecAad;
                break;
            case 7:
                throw new IllegalArgumentException(this.algorithmName + " cannot process AAD when the length of the plaintext to be processed exceeds the a block size");
        }
        int i2 = this.BLOCK_SIZE - i;
        int i3 = this.adlen - this.adOff;
        if (i2 <= i3) {
            System.arraycopy(this.ad, this.adOff, bArr, i, i2);
            this.adOff += i2;
            return;
        }
        if (i3 > 0) {
            System.arraycopy(this.ad, this.adOff, bArr, i, i3);
            this.adOff += i3;
        }
        Arrays.fill(bArr, i + i3, i + i2, (byte) 0);
        bArr[i + i3] = 1;
        switch (this.m_state.ordinal()) {
            case 2:
                this.m_state = State.EncData;
                return;
            case 6:
                this.m_state = State.DecData;
                return;
            default:
                return;
        }
    }

    private int processBytes(byte[] bArr, byte[] bArr2, int i, int i2, int i3, int i4, int i5, int i6, boolean z) {
        int i7 = 0;
        byte[] bArr3 = new byte[this.BLOCK_SIZE];
        int i8 = this.nb_its;
        while (i8 < i2) {
            int i9 = i8 == i3 - 1 ? i5 - (i8 * this.BLOCK_SIZE) : this.BLOCK_SIZE;
            if (!z && (i5 <= i8 * this.BLOCK_SIZE || i9 % this.BLOCK_SIZE != 0)) {
                break;
            }
            lfsr_step(this.next_mask, this.current_mask);
            if (i8 < i3) {
                System.arraycopy(this.npub, 0, this.buffer, 0, this.IV_SIZE);
                Arrays.fill(this.buffer, this.IV_SIZE, this.BLOCK_SIZE, (byte) 0);
                xor_block(this.buffer, this.current_mask, 0, this.BLOCK_SIZE);
                xor_block(this.buffer, this.next_mask, 0, this.BLOCK_SIZE);
                permutation(this.buffer);
                xor_block(this.buffer, this.current_mask, 0, this.BLOCK_SIZE);
                xor_block(this.buffer, this.next_mask, 0, this.BLOCK_SIZE);
                xor_block(this.buffer, bArr, i7, i9);
                System.arraycopy(this.buffer, 0, bArr2, i, i9);
                if (this.forEncryption) {
                    System.arraycopy(this.buffer, 0, bArr3, 0, i9);
                } else {
                    System.arraycopy(bArr, i7, bArr3, 0, i9);
                }
                i += i9;
                i7 += i9;
            }
            if (i8 > 0 && i8 <= i4) {
                int i10 = (i8 - 1) * this.BLOCK_SIZE;
                if (i10 == i5) {
                    Arrays.fill(this.buffer, 0, this.BLOCK_SIZE, (byte) 0);
                    this.buffer[0] = 1;
                } else {
                    int i11 = i5 - i10;
                    if (this.BLOCK_SIZE <= i11) {
                        System.arraycopy(this.previous_outputMessage, 0, this.buffer, 0, this.BLOCK_SIZE);
                    } else if (i11 > 0) {
                        System.arraycopy(this.previous_outputMessage, 0, this.buffer, 0, i11);
                        Arrays.fill(this.buffer, i11, this.BLOCK_SIZE, (byte) 0);
                        this.buffer[i11] = 1;
                    }
                }
                xor_block(this.buffer, this.previous_mask, 0, this.BLOCK_SIZE);
                xor_block(this.buffer, this.next_mask, 0, this.BLOCK_SIZE);
                permutation(this.buffer);
                xor_block(this.buffer, this.previous_mask, 0, this.BLOCK_SIZE);
                xor_block(this.buffer, this.next_mask, 0, this.BLOCK_SIZE);
                xor_block(this.tag_buffer, this.buffer, 0, this.BLOCK_SIZE);
            }
            if (i8 + 1 < i6) {
                processAADBytes(this.buffer);
                xor_block(this.buffer, this.next_mask, 0, this.BLOCK_SIZE);
                permutation(this.buffer);
                xor_block(this.buffer, this.next_mask, 0, this.BLOCK_SIZE);
                xor_block(this.tag_buffer, this.buffer, 0, this.BLOCK_SIZE);
            }
            byte[] bArr4 = this.previous_mask;
            this.previous_mask = this.current_mask;
            this.current_mask = this.next_mask;
            this.next_mask = bArr4;
            System.arraycopy(bArr3, 0, this.previous_outputMessage, 0, this.BLOCK_SIZE);
            i8++;
        }
        this.nb_its = i8;
        return i7;
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
