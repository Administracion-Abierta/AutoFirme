package org.bouncycastle.crypto.modes;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/KCCMBlockCipher.class */
public class KCCMBlockCipher implements AEADBlockCipher {
    private static final int BYTES_IN_INT = 4;
    private static final int BITS_IN_BYTE = 8;
    private static final int MAX_MAC_BIT_LENGTH = 512;
    private static final int MIN_MAC_BIT_LENGTH = 64;
    private BlockCipher engine;
    private int macSize;
    private boolean forEncryption;
    private byte[] initialAssociatedText;
    private byte[] mac;
    private byte[] macBlock;
    private byte[] nonce;
    private byte[] G1;
    private byte[] buffer;
    private byte[] s;
    private byte[] counter;
    private ExposedByteArrayOutputStream associatedText;
    private ExposedByteArrayOutputStream data;
    private int Nb_;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/KCCMBlockCipher$ExposedByteArrayOutputStream.class */
    private static class ExposedByteArrayOutputStream extends ByteArrayOutputStream {
        public byte[] getBuffer() {
            return this.buf;
        }
    }

    private void setNb(int i) {
        if (i != 4 && i != 6 && i != 8) {
            throw new IllegalArgumentException("Nb = 4 is recommended by DSTU7624 but can be changed to only 6 or 8 in this implementation");
        }
        this.Nb_ = i;
    }

    public KCCMBlockCipher(BlockCipher blockCipher) {
        this(blockCipher, 4);
    }

    public KCCMBlockCipher(BlockCipher blockCipher, int i) {
        this.associatedText = new ExposedByteArrayOutputStream();
        this.data = new ExposedByteArrayOutputStream();
        this.Nb_ = 4;
        this.engine = blockCipher;
        this.macSize = blockCipher.getBlockSize();
        this.nonce = new byte[blockCipher.getBlockSize()];
        this.initialAssociatedText = new byte[blockCipher.getBlockSize()];
        this.mac = new byte[blockCipher.getBlockSize()];
        this.macBlock = new byte[blockCipher.getBlockSize()];
        this.G1 = new byte[blockCipher.getBlockSize()];
        this.buffer = new byte[blockCipher.getBlockSize()];
        this.s = new byte[blockCipher.getBlockSize()];
        this.counter = new byte[blockCipher.getBlockSize()];
        setNb(i);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException {
        CipherParameters parameters;
        if (cipherParameters instanceof AEADParameters) {
            AEADParameters aEADParameters = (AEADParameters) cipherParameters;
            if (aEADParameters.getMacSize() > 512 || aEADParameters.getMacSize() < 64 || aEADParameters.getMacSize() % 8 != 0) {
                throw new IllegalArgumentException("Invalid mac size specified");
            }
            this.nonce = aEADParameters.getNonce();
            this.macSize = aEADParameters.getMacSize() / 8;
            this.initialAssociatedText = aEADParameters.getAssociatedText();
            parameters = aEADParameters.getKey();
        } else {
            if (!(cipherParameters instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("Invalid parameters specified");
            }
            this.nonce = ((ParametersWithIV) cipherParameters).getIV();
            this.macSize = this.engine.getBlockSize();
            this.initialAssociatedText = null;
            parameters = ((ParametersWithIV) cipherParameters).getParameters();
        }
        this.mac = new byte[this.macSize];
        this.forEncryption = z;
        this.engine.init(true, parameters);
        this.counter[0] = 1;
        if (this.initialAssociatedText != null) {
            processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public String getAlgorithmName() {
        return this.engine.getAlgorithmName() + "/KCCM";
    }

    @Override // org.bouncycastle.crypto.modes.AEADBlockCipher
    public BlockCipher getUnderlyingCipher() {
        return this.engine;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void processAADByte(byte b) {
        this.associatedText.write(b);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void processAADBytes(byte[] bArr, int i, int i2) {
        this.associatedText.write(bArr, i, i2);
    }

    private void processAAD(byte[] bArr, int i, int i2, int i3) {
        if (i2 - i < this.engine.getBlockSize()) {
            throw new IllegalArgumentException("authText buffer too short");
        }
        if (i2 % this.engine.getBlockSize() != 0) {
            throw new IllegalArgumentException("padding not supported");
        }
        System.arraycopy(this.nonce, 0, this.G1, 0, (this.nonce.length - this.Nb_) - 1);
        intToBytes(i3, this.buffer, 0);
        System.arraycopy(this.buffer, 0, this.G1, (this.nonce.length - this.Nb_) - 1, 4);
        this.G1[this.G1.length - 1] = getFlag(true, this.macSize);
        this.engine.processBlock(this.G1, 0, this.macBlock, 0);
        intToBytes(i2, this.buffer, 0);
        if (i2 <= this.engine.getBlockSize() - this.Nb_) {
            for (int i4 = 0; i4 < i2; i4++) {
                byte[] bArr2 = this.buffer;
                int i5 = i4 + this.Nb_;
                bArr2[i5] = (byte) (bArr2[i5] ^ bArr[i + i4]);
            }
            for (int i6 = 0; i6 < this.engine.getBlockSize(); i6++) {
                byte[] bArr3 = this.macBlock;
                int i7 = i6;
                bArr3[i7] = (byte) (bArr3[i7] ^ this.buffer[i6]);
            }
            this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
            return;
        }
        for (int i8 = 0; i8 < this.engine.getBlockSize(); i8++) {
            byte[] bArr4 = this.macBlock;
            int i9 = i8;
            bArr4[i9] = (byte) (bArr4[i9] ^ this.buffer[i8]);
        }
        this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
        int blockSize = i2;
        while (true) {
            int i10 = blockSize;
            if (i10 == 0) {
                return;
            }
            for (int i11 = 0; i11 < this.engine.getBlockSize(); i11++) {
                byte[] bArr5 = this.macBlock;
                int i12 = i11;
                bArr5[i12] = (byte) (bArr5[i12] ^ bArr[i11 + i]);
            }
            this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
            i += this.engine.getBlockSize();
            blockSize = i10 - this.engine.getBlockSize();
        }
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int processByte(byte b, byte[] bArr, int i) throws IllegalStateException, DataLengthException {
        this.data.write(b);
        return 0;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws IllegalStateException, DataLengthException {
        if (bArr.length < i + i2) {
            throw new DataLengthException("input buffer too short");
        }
        this.data.write(bArr, i, i2);
        return 0;
    }

    public int processPacket(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws InvalidCipherTextException, IllegalStateException {
        if (bArr.length - i < i2) {
            throw new DataLengthException("input buffer too short");
        }
        if (bArr2.length - i3 < i2) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.associatedText.size() > 0) {
            if (this.forEncryption) {
                processAAD(this.associatedText.getBuffer(), 0, this.associatedText.size(), this.data.size());
            } else {
                processAAD(this.associatedText.getBuffer(), 0, this.associatedText.size(), this.data.size() - this.macSize);
            }
        }
        if (this.forEncryption) {
            if (i2 % this.engine.getBlockSize() != 0) {
                throw new DataLengthException("partial blocks not supported");
            }
            CalculateMac(bArr, i, i2);
            this.engine.processBlock(this.nonce, 0, this.s, 0);
            int blockSize = i2;
            while (blockSize > 0) {
                ProcessBlock(bArr, i, i2, bArr2, i3);
                blockSize -= this.engine.getBlockSize();
                i += this.engine.getBlockSize();
                i3 += this.engine.getBlockSize();
            }
            for (int i4 = 0; i4 < this.counter.length; i4++) {
                byte[] bArr3 = this.s;
                int i5 = i4;
                bArr3[i5] = (byte) (bArr3[i5] + this.counter[i4]);
            }
            this.engine.processBlock(this.s, 0, this.buffer, 0);
            for (int i6 = 0; i6 < this.macSize; i6++) {
                bArr2[i3 + i6] = (byte) (this.buffer[i6] ^ this.macBlock[i6]);
            }
            System.arraycopy(this.macBlock, 0, this.mac, 0, this.macSize);
            reset();
            return i2 + this.macSize;
        }
        if ((i2 - this.macSize) % this.engine.getBlockSize() != 0) {
            throw new DataLengthException("partial blocks not supported");
        }
        this.engine.processBlock(this.nonce, 0, this.s, 0);
        int blockSize2 = i2 / this.engine.getBlockSize();
        for (int i7 = 0; i7 < blockSize2; i7++) {
            ProcessBlock(bArr, i, i2, bArr2, i3);
            i += this.engine.getBlockSize();
            i3 += this.engine.getBlockSize();
        }
        if (i2 > i) {
            for (int i8 = 0; i8 < this.counter.length; i8++) {
                byte[] bArr4 = this.s;
                int i9 = i8;
                bArr4[i9] = (byte) (bArr4[i9] + this.counter[i8]);
            }
            this.engine.processBlock(this.s, 0, this.buffer, 0);
            for (int i10 = 0; i10 < this.macSize; i10++) {
                bArr2[i3 + i10] = (byte) (this.buffer[i10] ^ bArr[i + i10]);
            }
            i3 += this.macSize;
        }
        for (int i11 = 0; i11 < this.counter.length; i11++) {
            byte[] bArr5 = this.s;
            int i12 = i11;
            bArr5[i12] = (byte) (bArr5[i12] + this.counter[i11]);
        }
        this.engine.processBlock(this.s, 0, this.buffer, 0);
        System.arraycopy(bArr2, i3 - this.macSize, this.buffer, 0, this.macSize);
        CalculateMac(bArr2, 0, i3 - this.macSize);
        System.arraycopy(this.macBlock, 0, this.mac, 0, this.macSize);
        byte[] bArr6 = new byte[this.macSize];
        System.arraycopy(this.buffer, 0, bArr6, 0, this.macSize);
        if (!Arrays.constantTimeAreEqual(this.mac, bArr6)) {
            throw new InvalidCipherTextException("mac check failed");
        }
        reset();
        return i2 - this.macSize;
    }

    private void ProcessBlock(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        for (int i4 = 0; i4 < this.counter.length; i4++) {
            byte[] bArr3 = this.s;
            int i5 = i4;
            bArr3[i5] = (byte) (bArr3[i5] + this.counter[i4]);
        }
        this.engine.processBlock(this.s, 0, this.buffer, 0);
        for (int i6 = 0; i6 < this.engine.getBlockSize(); i6++) {
            bArr2[i3 + i6] = (byte) (this.buffer[i6] ^ bArr[i + i6]);
        }
    }

    private void CalculateMac(byte[] bArr, int i, int i2) {
        int blockSize = i2;
        while (blockSize > 0) {
            for (int i3 = 0; i3 < this.engine.getBlockSize(); i3++) {
                byte[] bArr2 = this.macBlock;
                int i4 = i3;
                bArr2[i4] = (byte) (bArr2[i4] ^ bArr[i + i3]);
            }
            this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
            blockSize -= this.engine.getBlockSize();
            i += this.engine.getBlockSize();
        }
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int doFinal(byte[] bArr, int i) throws InvalidCipherTextException, IllegalStateException {
        int iProcessPacket = processPacket(this.data.getBuffer(), 0, this.data.size(), bArr, i);
        reset();
        return iProcessPacket;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public byte[] getMac() {
        return Arrays.clone(this.mac);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int getUpdateOutputSize(int i) {
        return i;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int getOutputSize(int i) {
        return i + this.macSize;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void reset() {
        Arrays.fill(this.G1, (byte) 0);
        Arrays.fill(this.buffer, (byte) 0);
        Arrays.fill(this.counter, (byte) 0);
        Arrays.fill(this.macBlock, (byte) 0);
        this.counter[0] = 1;
        this.data.reset();
        this.associatedText.reset();
        if (this.initialAssociatedText != null) {
            processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    private void intToBytes(int i, byte[] bArr, int i2) {
        bArr[i2 + 3] = (byte) (i >> 24);
        bArr[i2 + 2] = (byte) (i >> 16);
        bArr[i2 + 1] = (byte) (i >> 8);
        bArr[i2] = (byte) i;
    }

    private byte getFlag(boolean z, int i) {
        StringBuffer stringBuffer = new StringBuffer();
        if (z) {
            stringBuffer.append("1");
        } else {
            stringBuffer.append("0");
        }
        switch (i) {
            case 8:
                stringBuffer.append("010");
                break;
            case 16:
                stringBuffer.append("011");
                break;
            case 32:
                stringBuffer.append("100");
                break;
            case 48:
                stringBuffer.append("101");
                break;
            case 64:
                stringBuffer.append("110");
                break;
        }
        String binaryString = Integer.toBinaryString(this.Nb_ - 1);
        while (true) {
            String str = binaryString;
            if (str.length() >= 4) {
                stringBuffer.append(str);
                return (byte) Integer.parseInt(stringBuffer.toString(), 2);
            }
            binaryString = new StringBuffer(str).insert(0, "0").toString();
        }
    }
}
