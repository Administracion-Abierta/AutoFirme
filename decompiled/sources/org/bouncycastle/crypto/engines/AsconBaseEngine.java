package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Longs;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/AsconBaseEngine.class */
abstract class AsconBaseEngine extends AEADBaseEngine {
    protected int nr;
    protected int ASCON_AEAD_RATE;
    protected long K0;
    protected long K1;
    protected long N0;
    protected long N1;
    protected long ASCON_IV;
    protected long x0;
    protected long x1;
    protected long x2;
    protected long x3;
    protected long x4;
    protected int m_bufferSizeDecrypt;
    protected byte[] m_buf;
    protected long dsep;
    protected State m_state = State.Uninitialized;
    protected int m_bufPos = 0;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/AsconBaseEngine$State.class */
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

    AsconBaseEngine() {
    }

    protected abstract long pad(int i);

    protected abstract long loadBytes(byte[] bArr, int i);

    protected abstract void setBytes(long j, byte[] bArr, int i);

    private void round(long j) {
        long j2 = ((((this.x0 ^ this.x1) ^ this.x2) ^ this.x3) ^ j) ^ (this.x1 & (((this.x0 ^ this.x2) ^ this.x4) ^ j));
        long j3 = ((((this.x0 ^ this.x2) ^ this.x3) ^ this.x4) ^ j) ^ (((this.x1 ^ this.x2) ^ j) & (this.x1 ^ this.x3));
        long j4 = (((this.x1 ^ this.x2) ^ this.x4) ^ j) ^ (this.x3 & this.x4);
        long j5 = (((this.x0 ^ this.x1) ^ this.x2) ^ j) ^ ((this.x0 ^ (-1)) & (this.x3 ^ this.x4));
        long j6 = ((this.x1 ^ this.x3) ^ this.x4) ^ ((this.x0 ^ this.x4) & this.x1);
        this.x0 = (j2 ^ Longs.rotateRight(j2, 19)) ^ Longs.rotateRight(j2, 28);
        this.x1 = (j3 ^ Longs.rotateRight(j3, 39)) ^ Longs.rotateRight(j3, 61);
        this.x2 = ((j4 ^ Longs.rotateRight(j4, 1)) ^ Longs.rotateRight(j4, 6)) ^ (-1);
        this.x3 = (j5 ^ Longs.rotateRight(j5, 10)) ^ Longs.rotateRight(j5, 17);
        this.x4 = (j6 ^ Longs.rotateRight(j6, 7)) ^ Longs.rotateRight(j6, 41);
    }

    protected void p(int i) {
        if (i == 12) {
            round(240L);
            round(225L);
            round(210L);
            round(195L);
        }
        if (i >= 8) {
            round(180L);
            round(165L);
        }
        round(150L);
        round(135L);
        round(120L);
        round(105L);
        round(90L);
        round(75L);
    }

    protected abstract void ascon_aeadinit();

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
                processFinalAadBlock();
                p(this.nr);
                break;
        }
        this.x4 ^= this.dsep;
        this.m_bufPos = 0;
        this.m_state = state;
    }

    protected abstract void processFinalAadBlock();

    protected abstract void processFinalDecrypt(byte[] bArr, int i, byte[] bArr2, int i2);

    protected abstract void processFinalEncrypt(byte[] bArr, int i, byte[] bArr2, int i2);

    protected void processBufferAAD(byte[] bArr, int i) {
        this.x0 ^= loadBytes(bArr, i);
        if (this.ASCON_AEAD_RATE == 16) {
            this.x1 ^= loadBytes(bArr, 8 + i);
        }
        p(this.nr);
    }

    protected void processBufferDecrypt(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (i2 + this.ASCON_AEAD_RATE > bArr2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        long jLoadBytes = loadBytes(bArr, i);
        setBytes(this.x0 ^ jLoadBytes, bArr2, i2);
        this.x0 = jLoadBytes;
        if (this.ASCON_AEAD_RATE == 16) {
            long jLoadBytes2 = loadBytes(bArr, i + 8);
            setBytes(this.x1 ^ jLoadBytes2, bArr2, i2 + 8);
            this.x1 = jLoadBytes2;
        }
        p(this.nr);
    }

    protected void processBufferEncrypt(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (i2 + this.ASCON_AEAD_RATE > bArr2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        this.x0 ^= loadBytes(bArr, i);
        setBytes(this.x0, bArr2, i2);
        if (this.ASCON_AEAD_RATE == 16) {
            this.x1 ^= loadBytes(bArr, i + 8);
            setBytes(this.x1, bArr2, i2 + 8);
        }
        p(this.nr);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void processAADByte(byte b) {
        checkAAD();
        this.m_buf[this.m_bufPos] = b;
        int i = this.m_bufPos + 1;
        this.m_bufPos = i;
        if (i == this.ASCON_AEAD_RATE) {
            processBufferAAD(this.m_buf, 0);
            this.m_bufPos = 0;
        }
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
        if (this.m_bufPos > 0) {
            int i3 = this.ASCON_AEAD_RATE - this.m_bufPos;
            if (i2 < i3) {
                System.arraycopy(bArr, i, this.m_buf, this.m_bufPos, i2);
                this.m_bufPos += i2;
                return;
            } else {
                System.arraycopy(bArr, i, this.m_buf, this.m_bufPos, i3);
                i += i3;
                i2 -= i3;
                processBufferAAD(this.m_buf, 0);
            }
        }
        while (i2 >= this.ASCON_AEAD_RATE) {
            processBufferAAD(bArr, i);
            i += this.ASCON_AEAD_RATE;
            i2 -= this.ASCON_AEAD_RATE;
        }
        System.arraycopy(bArr, i, this.m_buf, 0, i2);
        this.m_bufPos = i2;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException {
        if (i + i2 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        int i4 = 0;
        if (!checkData()) {
            int i5 = this.m_bufferSizeDecrypt - this.m_bufPos;
            if (i2 >= i5) {
                while (this.m_bufPos >= this.ASCON_AEAD_RATE) {
                    processBufferDecrypt(this.m_buf, 0, bArr2, i3 + i4);
                    this.m_bufPos -= this.ASCON_AEAD_RATE;
                    System.arraycopy(this.m_buf, this.ASCON_AEAD_RATE, this.m_buf, 0, this.m_bufPos);
                    i4 += this.ASCON_AEAD_RATE;
                    i5 += this.ASCON_AEAD_RATE;
                    if (i2 < i5) {
                        System.arraycopy(bArr, i, this.m_buf, this.m_bufPos, i2);
                        this.m_bufPos += i2;
                        return i4;
                    }
                }
                int i6 = this.ASCON_AEAD_RATE - this.m_bufPos;
                System.arraycopy(bArr, i, this.m_buf, this.m_bufPos, i6);
                i += i6;
                i2 -= i6;
                processBufferDecrypt(this.m_buf, 0, bArr2, i3 + i4);
                int i7 = i4;
                int i8 = this.ASCON_AEAD_RATE;
                while (true) {
                    i4 = i7 + i8;
                    if (i2 < this.m_bufferSizeDecrypt) {
                        break;
                    }
                    processBufferDecrypt(bArr, i, bArr2, i3 + i4);
                    i += this.ASCON_AEAD_RATE;
                    i2 -= this.ASCON_AEAD_RATE;
                    i7 = i4;
                    i8 = this.ASCON_AEAD_RATE;
                }
            } else {
                System.arraycopy(bArr, i, this.m_buf, this.m_bufPos, i2);
                this.m_bufPos += i2;
                return 0;
            }
        } else {
            if (this.m_bufPos > 0) {
                int i9 = this.ASCON_AEAD_RATE - this.m_bufPos;
                if (i2 < i9) {
                    System.arraycopy(bArr, i, this.m_buf, this.m_bufPos, i2);
                    this.m_bufPos += i2;
                    return 0;
                }
                System.arraycopy(bArr, i, this.m_buf, this.m_bufPos, i9);
                i += i9;
                i2 -= i9;
                processBufferEncrypt(this.m_buf, 0, bArr2, i3);
                i4 = this.ASCON_AEAD_RATE;
            }
            while (i2 >= this.ASCON_AEAD_RATE) {
                processBufferEncrypt(bArr, i, bArr2, i3 + i4);
                i += this.ASCON_AEAD_RATE;
                i2 -= this.ASCON_AEAD_RATE;
                i4 += this.ASCON_AEAD_RATE;
            }
        }
        System.arraycopy(bArr, i, this.m_buf, 0, i2);
        this.m_bufPos = i2;
        return i4;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int doFinal(byte[] bArr, int i) throws InvalidCipherTextException, IllegalStateException, DataLengthException {
        int i2;
        if (checkData()) {
            i2 = this.m_bufPos + this.MAC_SIZE;
            if (i + i2 > bArr.length) {
                throw new OutputLengthException("output buffer too short");
            }
            processFinalEncrypt(this.m_buf, this.m_bufPos, bArr, i);
            this.mac = new byte[this.MAC_SIZE];
            setBytes(this.x3, this.mac, 0);
            setBytes(this.x4, this.mac, 8);
            System.arraycopy(this.mac, 0, bArr, i + this.m_bufPos, this.MAC_SIZE);
            reset(false);
        } else {
            if (this.m_bufPos < this.MAC_SIZE) {
                throw new InvalidCipherTextException("data too short");
            }
            this.m_bufPos -= this.MAC_SIZE;
            i2 = this.m_bufPos;
            if (i + i2 > bArr.length) {
                throw new OutputLengthException("output buffer too short");
            }
            processFinalDecrypt(this.m_buf, this.m_bufPos, bArr, i);
            this.x3 ^= loadBytes(this.m_buf, this.m_bufPos);
            this.x4 ^= loadBytes(this.m_buf, this.m_bufPos + 8);
            if ((this.x3 | this.x4) != 0) {
                throw new InvalidCipherTextException("mac check in " + getAlgorithmName() + " failed");
            }
            reset(true);
        }
        return i2;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int getUpdateOutputSize(int i) {
        int iMax = Math.max(0, i);
        switch (this.m_state.ordinal()) {
            case 3:
            case 4:
                iMax += this.m_bufPos;
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
        return iMax - (iMax % this.ASCON_AEAD_RATE);
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

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    protected void reset(boolean z) {
        Arrays.clear(this.m_buf);
        this.m_bufPos = 0;
        switch (this.m_state.ordinal()) {
            case 1:
            case 5:
                break;
            case 2:
            case 3:
            case 4:
                this.m_state = State.EncFinal;
                return;
            case 6:
            case 7:
            case 8:
                this.m_state = State.DecInit;
                break;
            default:
                throw new IllegalStateException(getAlgorithmName() + " needs to be initialized");
        }
        ascon_aeadinit();
        super.reset(z);
    }

    public abstract String getAlgorithmVersion();
}
