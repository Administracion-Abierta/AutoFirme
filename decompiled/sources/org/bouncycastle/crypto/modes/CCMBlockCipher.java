package org.bouncycastle.crypto.modes;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/CCMBlockCipher.class */
public class CCMBlockCipher implements CCMModeCipher {
    private BlockCipher cipher;
    private int blockSize;
    private boolean forEncryption;
    private byte[] nonce;
    private byte[] initialAssociatedText;
    private int macSize;
    private CipherParameters keyParam;
    private byte[] macBlock;
    private ExposedByteArrayOutputStream associatedText = new ExposedByteArrayOutputStream();
    private ExposedByteArrayOutputStream data = new ExposedByteArrayOutputStream();

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/CCMBlockCipher$ExposedByteArrayOutputStream.class */
    private static class ExposedByteArrayOutputStream extends ByteArrayOutputStream {
        public byte[] getBuffer() {
            return this.buf;
        }
    }

    public static CCMModeCipher newInstance(BlockCipher blockCipher) {
        return new CCMBlockCipher(blockCipher);
    }

    public CCMBlockCipher(BlockCipher blockCipher) {
        this.cipher = blockCipher;
        this.blockSize = blockCipher.getBlockSize();
        this.macBlock = new byte[this.blockSize];
        if (this.blockSize != 16) {
            throw new IllegalArgumentException("cipher required with a block size of 16.");
        }
    }

    @Override // org.bouncycastle.crypto.modes.AEADBlockCipher
    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException {
        CipherParameters parameters;
        this.forEncryption = z;
        if (cipherParameters instanceof AEADParameters) {
            AEADParameters aEADParameters = (AEADParameters) cipherParameters;
            this.nonce = aEADParameters.getNonce();
            this.initialAssociatedText = aEADParameters.getAssociatedText();
            this.macSize = getMacSize(z, aEADParameters.getMacSize());
            parameters = aEADParameters.getKey();
        } else {
            if (!(cipherParameters instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("invalid parameters passed to CCM: " + cipherParameters.getClass().getName());
            }
            ParametersWithIV parametersWithIV = (ParametersWithIV) cipherParameters;
            this.nonce = parametersWithIV.getIV();
            this.initialAssociatedText = null;
            this.macSize = getMacSize(z, 64);
            parameters = parametersWithIV.getParameters();
        }
        if (parameters != null) {
            this.keyParam = parameters;
        }
        if (this.nonce == null || this.nonce.length < 7 || this.nonce.length > 13) {
            throw new IllegalArgumentException("nonce must have length from 7 to 13 octets");
        }
        reset();
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/CCM";
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void processAADByte(byte b) {
        this.associatedText.write(b);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void processAADBytes(byte[] bArr, int i, int i2) {
        this.associatedText.write(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int processByte(byte b, byte[] bArr, int i) throws IllegalStateException, DataLengthException {
        this.data.write(b);
        return 0;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws IllegalStateException, DataLengthException {
        if (bArr.length < i + i2) {
            throw new DataLengthException("Input buffer too short");
        }
        this.data.write(bArr, i, i2);
        return 0;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int doFinal(byte[] bArr, int i) throws InvalidCipherTextException, IllegalStateException {
        int iProcessPacket = processPacket(this.data.getBuffer(), 0, this.data.size(), bArr, i);
        reset();
        return iProcessPacket;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void reset() {
        this.cipher.reset();
        this.associatedText.reset();
        this.data.reset();
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public byte[] getMac() {
        byte[] bArr = new byte[this.macSize];
        System.arraycopy(this.macBlock, 0, bArr, 0, bArr.length);
        return bArr;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int getUpdateOutputSize(int i) {
        return 0;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int getOutputSize(int i) {
        int size = i + this.data.size();
        if (this.forEncryption) {
            return size + this.macSize;
        }
        if (size < this.macSize) {
            return 0;
        }
        return size - this.macSize;
    }

    public byte[] processPacket(byte[] bArr, int i, int i2) throws InvalidCipherTextException, IllegalStateException {
        byte[] bArr2;
        if (this.forEncryption) {
            bArr2 = new byte[i2 + this.macSize];
        } else {
            if (i2 < this.macSize) {
                throw new InvalidCipherTextException("data too short");
            }
            bArr2 = new byte[i2 - this.macSize];
        }
        processPacket(bArr, i, i2, bArr2, 0);
        return bArr2;
    }

    public int processPacket(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws InvalidCipherTextException, IllegalStateException, DataLengthException {
        int i4;
        if (this.keyParam == null) {
            throw new IllegalStateException("CCM cipher unitialized.");
        }
        int length = 15 - this.nonce.length;
        if (length < 4) {
            if (i2 - (this.forEncryption ? 0 : 16) >= (1 << (8 * length))) {
                throw new IllegalStateException("CCM packet too large for choice of q");
            }
        }
        byte[] bArr3 = new byte[this.blockSize];
        bArr3[0] = (byte) ((length - 1) & 7);
        System.arraycopy(this.nonce, 0, bArr3, 1, this.nonce.length);
        CTRModeCipher cTRModeCipherNewInstance = SICBlockCipher.newInstance(this.cipher);
        cTRModeCipherNewInstance.init(this.forEncryption, new ParametersWithIV(this.keyParam, bArr3));
        int i5 = i;
        int i6 = i3;
        if (this.forEncryption) {
            i4 = i2 + this.macSize;
            if (bArr2.length < i4 + i3) {
                throw new OutputLengthException("Output buffer too short.");
            }
            calculateMac(bArr, i, i2, this.macBlock);
            byte[] bArr4 = new byte[this.blockSize];
            cTRModeCipherNewInstance.processBlock(this.macBlock, 0, bArr4, 0);
            while (i5 < (i + i2) - this.blockSize) {
                cTRModeCipherNewInstance.processBlock(bArr, i5, bArr2, i6);
                i6 += this.blockSize;
                i5 += this.blockSize;
            }
            byte[] bArr5 = new byte[this.blockSize];
            System.arraycopy(bArr, i5, bArr5, 0, (i2 + i) - i5);
            cTRModeCipherNewInstance.processBlock(bArr5, 0, bArr5, 0);
            System.arraycopy(bArr5, 0, bArr2, i6, (i2 + i) - i5);
            System.arraycopy(bArr4, 0, bArr2, i3 + i2, this.macSize);
        } else {
            if (i2 < this.macSize) {
                throw new InvalidCipherTextException("data too short");
            }
            i4 = i2 - this.macSize;
            if (bArr2.length < i4 + i3) {
                throw new OutputLengthException("Output buffer too short.");
            }
            System.arraycopy(bArr, i + i4, this.macBlock, 0, this.macSize);
            cTRModeCipherNewInstance.processBlock(this.macBlock, 0, this.macBlock, 0);
            for (int i7 = this.macSize; i7 != this.macBlock.length; i7++) {
                this.macBlock[i7] = 0;
            }
            while (i5 < (i + i4) - this.blockSize) {
                cTRModeCipherNewInstance.processBlock(bArr, i5, bArr2, i6);
                i6 += this.blockSize;
                i5 += this.blockSize;
            }
            byte[] bArr6 = new byte[this.blockSize];
            System.arraycopy(bArr, i5, bArr6, 0, i4 - (i5 - i));
            cTRModeCipherNewInstance.processBlock(bArr6, 0, bArr6, 0);
            System.arraycopy(bArr6, 0, bArr2, i6, i4 - (i5 - i));
            byte[] bArr7 = new byte[this.blockSize];
            calculateMac(bArr2, i3, i4, bArr7);
            if (!Arrays.constantTimeAreEqual(this.macBlock, bArr7)) {
                throw new InvalidCipherTextException("mac check in CCM failed");
            }
        }
        return i4;
    }

    private int calculateMac(byte[] bArr, int i, int i2, byte[] bArr2) {
        int i3;
        CBCBlockCipherMac cBCBlockCipherMac = new CBCBlockCipherMac(this.cipher, this.macSize * 8);
        cBCBlockCipherMac.init(this.keyParam);
        byte[] bArr3 = new byte[16];
        if (hasAssociatedText()) {
            bArr3[0] = (byte) (bArr3[0] | 64);
        }
        bArr3[0] = (byte) (bArr3[0] | ((((cBCBlockCipherMac.getMacSize() - 2) / 2) & 7) << 3));
        bArr3[0] = (byte) (bArr3[0] | (((15 - this.nonce.length) - 1) & 7));
        System.arraycopy(this.nonce, 0, bArr3, 1, this.nonce.length);
        int i4 = i2;
        int i5 = 1;
        while (i4 > 0) {
            bArr3[bArr3.length - i5] = (byte) (i4 & 255);
            i4 >>>= 8;
            i5++;
        }
        cBCBlockCipherMac.update(bArr3, 0, bArr3.length);
        if (hasAssociatedText()) {
            int associatedTextLength = getAssociatedTextLength();
            if (associatedTextLength < 65280) {
                cBCBlockCipherMac.update((byte) (associatedTextLength >> 8));
                cBCBlockCipherMac.update((byte) associatedTextLength);
                i3 = 2;
            } else {
                cBCBlockCipherMac.update((byte) -1);
                cBCBlockCipherMac.update((byte) -2);
                cBCBlockCipherMac.update((byte) (associatedTextLength >> 24));
                cBCBlockCipherMac.update((byte) (associatedTextLength >> 16));
                cBCBlockCipherMac.update((byte) (associatedTextLength >> 8));
                cBCBlockCipherMac.update((byte) associatedTextLength);
                i3 = 6;
            }
            if (this.initialAssociatedText != null) {
                cBCBlockCipherMac.update(this.initialAssociatedText, 0, this.initialAssociatedText.length);
            }
            if (this.associatedText.size() > 0) {
                cBCBlockCipherMac.update(this.associatedText.getBuffer(), 0, this.associatedText.size());
            }
            int i6 = (i3 + associatedTextLength) % 16;
            if (i6 != 0) {
                for (int i7 = i6; i7 != 16; i7++) {
                    cBCBlockCipherMac.update((byte) 0);
                }
            }
        }
        cBCBlockCipherMac.update(bArr, i, i2);
        return cBCBlockCipherMac.doFinal(bArr2, 0);
    }

    private int getMacSize(boolean z, int i) {
        if (!z || (i >= 32 && i <= 128 && 0 == (i & 15))) {
            return i >>> 3;
        }
        throw new IllegalArgumentException("tag length in octets must be one of {4,6,8,10,12,14,16}");
    }

    private int getAssociatedTextLength() {
        return this.associatedText.size() + (this.initialAssociatedText == null ? 0 : this.initialAssociatedText.length);
    }

    private boolean hasAssociatedText() {
        return getAssociatedTextLength() > 0;
    }
}
