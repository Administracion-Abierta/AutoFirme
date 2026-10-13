package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/AEADBufferBaseEngine.class */
abstract class AEADBufferBaseEngine extends AEADBaseEngine {
    protected byte[] m_buf;
    protected byte[] m_aad;
    protected int m_bufPos;
    protected int m_aadPos;
    protected boolean aadFinished;
    protected int AADBufferSize;
    protected int BlockSize;
    protected boolean initialised = false;
    protected State m_state = State.Uninitialized;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/AEADBufferBaseEngine$State.class */
    protected enum State {
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

    AEADBufferBaseEngine() {
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void processAADByte(byte b) {
        checkAAD();
        if (this.m_aadPos == this.AADBufferSize) {
            processBufferAAD(this.m_aad, 0);
            this.m_aadPos = 0;
        }
        byte[] bArr = this.m_aad;
        int i = this.m_aadPos;
        this.m_aadPos = i + 1;
        bArr[i] = b;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void processAADBytes(byte[] bArr, int i, int i2) {
        if (i + i2 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (i2 <= 0) {
            return;
        }
        checkAAD();
        if (this.m_aadPos > 0) {
            int i3 = this.AADBufferSize - this.m_aadPos;
            if (i2 <= i3) {
                System.arraycopy(bArr, i, this.m_aad, this.m_aadPos, i2);
                this.m_aadPos += i2;
                return;
            } else {
                System.arraycopy(bArr, i, this.m_aad, this.m_aadPos, i3);
                i += i3;
                i2 -= i3;
                processBufferAAD(this.m_aad, 0);
                this.m_aadPos = 0;
            }
        }
        while (i2 > this.AADBufferSize) {
            processBufferAAD(bArr, i);
            i += this.AADBufferSize;
            i2 -= this.AADBufferSize;
        }
        System.arraycopy(bArr, i, this.m_aad, this.m_aadPos, i2);
        this.m_aadPos += i2;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException {
        if (i + i2 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        int i4 = 0;
        if (checkData()) {
            if (this.m_bufPos > 0) {
                int i5 = this.BlockSize - this.m_bufPos;
                if (i2 <= i5) {
                    System.arraycopy(bArr, i, this.m_buf, this.m_bufPos, i2);
                    this.m_bufPos += i2;
                    return 0;
                }
                System.arraycopy(bArr, i, this.m_buf, this.m_bufPos, i5);
                i += i5;
                i2 -= i5;
                validateAndProcessBuffer(this.m_buf, 0, bArr2, i3);
                i4 = this.BlockSize;
            }
            while (i2 > this.BlockSize) {
                validateAndProcessBuffer(bArr, i, bArr2, i3 + i4);
                i += this.BlockSize;
                i2 -= this.BlockSize;
                i4 += this.BlockSize;
            }
        } else {
            int i6 = (this.BlockSize + this.MAC_SIZE) - this.m_bufPos;
            if (i2 <= i6) {
                System.arraycopy(bArr, i, this.m_buf, this.m_bufPos, i2);
                this.m_bufPos += i2;
                return 0;
            }
            if (this.BlockSize >= this.MAC_SIZE) {
                if (this.m_bufPos > this.BlockSize) {
                    validateAndProcessBuffer(this.m_buf, 0, bArr2, i3);
                    this.m_bufPos -= this.BlockSize;
                    System.arraycopy(this.m_buf, this.BlockSize, this.m_buf, 0, this.m_bufPos);
                    i4 = this.BlockSize;
                    if (i2 <= i6 + this.BlockSize) {
                        System.arraycopy(bArr, i, this.m_buf, this.m_bufPos, i2);
                        this.m_bufPos += i2;
                        return i4;
                    }
                }
                int i7 = this.BlockSize - this.m_bufPos;
                System.arraycopy(bArr, i, this.m_buf, this.m_bufPos, i7);
                i += i7;
                i2 -= i7;
                validateAndProcessBuffer(this.m_buf, 0, bArr2, i3 + i4);
                i4 += this.BlockSize;
            } else {
                while (this.m_bufPos > this.BlockSize && i2 + this.m_bufPos > this.BlockSize + this.MAC_SIZE) {
                    validateAndProcessBuffer(this.m_buf, i4, bArr2, i3 + i4);
                    this.m_bufPos -= this.BlockSize;
                    i4 += this.BlockSize;
                }
                if (this.m_bufPos != 0) {
                    System.arraycopy(this.m_buf, i4, this.m_buf, 0, this.m_bufPos);
                    if (this.m_bufPos + i2 <= this.BlockSize + this.MAC_SIZE) {
                        System.arraycopy(bArr, i, this.m_buf, this.m_bufPos, i2);
                        this.m_bufPos += i2;
                        return i4;
                    }
                    int iMax = Math.max(this.BlockSize - this.m_bufPos, 0);
                    System.arraycopy(bArr, i, this.m_buf, this.m_bufPos, iMax);
                    i += iMax;
                    validateAndProcessBuffer(this.m_buf, 0, bArr2, i3 + i4);
                    i4 += this.BlockSize;
                    i2 -= iMax;
                }
            }
            while (i2 > this.BlockSize + this.MAC_SIZE) {
                validateAndProcessBuffer(bArr, i, bArr2, i3 + i4);
                i += this.BlockSize;
                i2 -= this.BlockSize;
                i4 += this.BlockSize;
            }
        }
        System.arraycopy(bArr, i, this.m_buf, 0, i2);
        this.m_bufPos = i2;
        return i4;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int doFinal(byte[] bArr, int i) throws InvalidCipherTextException, IllegalStateException {
        int i2;
        boolean zCheckData = checkData();
        if (zCheckData) {
            i2 = this.m_bufPos + this.MAC_SIZE;
        } else {
            if (this.m_bufPos < this.MAC_SIZE) {
                throw new InvalidCipherTextException("data too short");
            }
            this.m_bufPos -= this.MAC_SIZE;
            i2 = this.m_bufPos;
        }
        if (i > bArr.length - i2) {
            throw new OutputLengthException("output buffer too short");
        }
        processFinalBlock(bArr, i);
        if (zCheckData) {
            System.arraycopy(this.mac, 0, bArr, (i + i2) - this.MAC_SIZE, this.MAC_SIZE);
        } else if (!Arrays.constantTimeAreEqual(this.MAC_SIZE, this.mac, 0, this.m_buf, this.m_bufPos)) {
            throw new InvalidCipherTextException(this.algorithmName + " mac does not match");
        }
        reset(!zCheckData);
        return i2;
    }

    public int getBlockSize() {
        return this.BlockSize;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int getUpdateOutputSize(int i) {
        int iMax = Math.max(0, i) - 1;
        switch (this.m_state.ordinal()) {
            case 3:
            case 4:
                iMax = Math.max(0, iMax + this.m_bufPos);
                break;
            case 5:
            case 6:
                iMax = Math.max(0, iMax - this.MAC_SIZE);
                break;
            case 7:
            case 8:
                iMax = Math.max(0, (iMax + this.m_bufPos) - this.MAC_SIZE);
                break;
        }
        return iMax - (iMax % this.BlockSize);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int getOutputSize(int i) {
        int iMax = Math.max(0, i);
        switch (this.m_state.ordinal()) {
            case 3:
            case 4:
                return iMax + this.m_bufPos + this.MAC_SIZE;
            case 5:
            case 6:
                return Math.max(0, iMax - this.MAC_SIZE);
            case 7:
            case 8:
                return Math.max(0, (iMax + this.m_bufPos) - this.MAC_SIZE);
            default:
                return iMax + this.MAC_SIZE;
        }
    }

    protected void checkAAD() {
        switch (this.m_state.ordinal()) {
            case 1:
                this.m_state = State.EncAad;
                return;
            case 2:
            case 6:
                return;
            case 3:
            default:
                throw new IllegalStateException(getAlgorithmName() + " needs to be initialized");
            case 4:
                throw new IllegalStateException(getAlgorithmName() + " cannot be reused for encryption");
            case 5:
                this.m_state = State.DecAad;
                return;
        }
    }

    protected boolean checkData() {
        switch (this.m_state.ordinal()) {
            case 1:
            case 2:
                finishAAD(State.EncData);
                return true;
            case 3:
                return true;
            case 4:
                throw new IllegalStateException(getAlgorithmName() + " cannot be reused for encryption");
            case 5:
            case 6:
                finishAAD(State.DecData);
                return false;
            case 7:
                return false;
            default:
                throw new IllegalStateException(getAlgorithmName() + " needs to be initialized");
        }
    }

    private void finishAAD(State state) {
        switch (this.m_state.ordinal()) {
            case 2:
            case 6:
                processFinalAAD();
                break;
        }
        this.m_aadPos = 0;
        this.m_state = state;
    }

    protected void bufferReset() {
        Arrays.fill(this.m_buf, (byte) 0);
        Arrays.fill(this.m_aad, (byte) 0);
        this.m_bufPos = 0;
        this.m_aadPos = 0;
        switch (this.m_state.ordinal()) {
            case 1:
            case 5:
                return;
            case 2:
            case 3:
            case 4:
                this.m_state = State.EncFinal;
                return;
            case 6:
            case 7:
            case 8:
                this.m_state = State.DecInit;
                return;
            default:
                throw new IllegalStateException(getAlgorithmName() + " needs to be initialized");
        }
    }

    protected void validateAndProcessBuffer(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (i2 > bArr2.length - this.BlockSize) {
            throw new OutputLengthException("output buffer too short");
        }
        processBuffer(bArr, i, bArr2, i2);
    }

    protected abstract void processFinalBlock(byte[] bArr, int i);

    protected abstract void processBufferAAD(byte[] bArr, int i);

    protected abstract void processFinalAAD();

    protected abstract void processBuffer(byte[] bArr, int i, byte[] bArr2, int i2);
}
