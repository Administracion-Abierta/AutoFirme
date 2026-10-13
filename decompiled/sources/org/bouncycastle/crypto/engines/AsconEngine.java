package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/AsconEngine.class */
public class AsconEngine extends AsconBaseEngine {
    private final AsconParameters asconParameters;
    private long K2;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/AsconEngine$AsconParameters.class */
    public enum AsconParameters {
        ascon80pq,
        ascon128a,
        ascon128
    }

    public AsconEngine(AsconParameters asconParameters) {
        this.asconParameters = asconParameters;
        this.IV_SIZE = 16;
        this.MAC_SIZE = 16;
        switch (asconParameters) {
            case ascon80pq:
                this.KEY_SIZE = 20;
                this.ASCON_AEAD_RATE = 8;
                this.ASCON_IV = -6899501409222262784L;
                this.algorithmName = "Ascon-80pq AEAD";
                break;
            case ascon128a:
                this.KEY_SIZE = 16;
                this.ASCON_AEAD_RATE = 16;
                this.ASCON_IV = -9187330011336540160L;
                this.algorithmName = "Ascon-128a AEAD";
                break;
            case ascon128:
                this.KEY_SIZE = 16;
                this.ASCON_AEAD_RATE = 8;
                this.ASCON_IV = -9205344418435956736L;
                this.algorithmName = "Ascon-128 AEAD";
                break;
            default:
                throw new IllegalArgumentException("invalid parameter setting for ASCON AEAD");
        }
        this.nr = this.ASCON_AEAD_RATE == 8 ? 6 : 8;
        this.m_bufferSizeDecrypt = this.ASCON_AEAD_RATE + this.MAC_SIZE;
        this.m_buf = new byte[this.m_bufferSizeDecrypt];
        this.dsep = 1L;
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine
    protected long pad(int i) {
        return 128 << (56 - (i << 3));
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine
    protected long loadBytes(byte[] bArr, int i) {
        return Pack.bigEndianToLong(bArr, i);
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine
    protected void setBytes(long j, byte[] bArr, int i) {
        Pack.longToBigEndian(j, bArr, i);
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine
    protected void ascon_aeadinit() {
        this.x0 = this.ASCON_IV;
        if (this.KEY_SIZE == 20) {
            this.x0 ^= this.K0;
        }
        this.x1 = this.K1;
        this.x2 = this.K2;
        this.x3 = this.N0;
        this.x4 = this.N1;
        p(12);
        if (this.KEY_SIZE == 20) {
            this.x2 ^= this.K0;
        }
        this.x3 ^= this.K1;
        this.x4 ^= this.K2;
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine
    protected void processFinalAadBlock() {
        this.m_buf[this.m_bufPos] = -128;
        if (this.m_bufPos < 8) {
            this.x0 ^= Pack.bigEndianToLong(this.m_buf, 0) & ((-1) << (56 - (this.m_bufPos << 3)));
        } else {
            this.x0 ^= Pack.bigEndianToLong(this.m_buf, 0);
            this.x1 ^= Pack.bigEndianToLong(this.m_buf, 8) & ((-1) << (56 - ((this.m_bufPos - 8) << 3)));
        }
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine
    protected void processFinalDecrypt(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (i >= 8) {
            long jBigEndianToLong = Pack.bigEndianToLong(bArr, 0);
            this.x0 ^= jBigEndianToLong;
            Pack.longToBigEndian(this.x0, bArr2, i2);
            this.x0 = jBigEndianToLong;
            int i3 = i2 + 8;
            int i4 = i - 8;
            this.x1 ^= pad(i4);
            if (i4 != 0) {
                long jLittleEndianToLong_High = Pack.littleEndianToLong_High(bArr, 8, i4);
                this.x1 ^= jLittleEndianToLong_High;
                Pack.longToLittleEndian_High(this.x1, bArr2, i3, i4);
                this.x1 &= (-1) >>> (i4 << 3);
                this.x1 ^= jLittleEndianToLong_High;
            }
        } else {
            this.x0 ^= pad(i);
            if (i != 0) {
                long jLittleEndianToLong_High2 = Pack.littleEndianToLong_High(bArr, 0, i);
                this.x0 ^= jLittleEndianToLong_High2;
                Pack.longToLittleEndian_High(this.x0, bArr2, i2, i);
                this.x0 &= (-1) >>> (i << 3);
                this.x0 ^= jLittleEndianToLong_High2;
            }
        }
        finishData(AsconBaseEngine.State.DecFinal);
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine
    protected void processFinalEncrypt(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (i >= 8) {
            this.x0 ^= Pack.bigEndianToLong(bArr, 0);
            Pack.longToBigEndian(this.x0, bArr2, i2);
            int i3 = i2 + 8;
            int i4 = i - 8;
            this.x1 ^= pad(i4);
            if (i4 != 0) {
                this.x1 ^= Pack.littleEndianToLong_High(bArr, 8, i4);
                Pack.longToLittleEndian_High(this.x1, bArr2, i3, i4);
            }
        } else {
            this.x0 ^= pad(i);
            if (i != 0) {
                this.x0 ^= Pack.littleEndianToLong_High(bArr, 0, i);
                Pack.longToLittleEndian_High(this.x0, bArr2, i2, i);
            }
        }
        finishData(AsconBaseEngine.State.EncFinal);
    }

    private void finishData(AsconBaseEngine.State state) {
        switch (this.asconParameters) {
            case ascon80pq:
                this.x1 ^= (this.K0 << 32) | (this.K1 >> 32);
                this.x2 ^= (this.K1 << 32) | (this.K2 >> 32);
                this.x3 ^= this.K2 << 32;
                break;
            case ascon128a:
                this.x2 ^= this.K1;
                this.x3 ^= this.K2;
                break;
            case ascon128:
                this.x1 ^= this.K1;
                this.x2 ^= this.K2;
                break;
            default:
                throw new IllegalStateException();
        }
        p(12);
        this.x3 ^= this.K1;
        this.x4 ^= this.K2;
        this.m_state = state;
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    protected void init(byte[] bArr, byte[] bArr2) throws IllegalArgumentException {
        this.N0 = Pack.bigEndianToLong(bArr2, 0);
        this.N1 = Pack.bigEndianToLong(bArr2, 8);
        if (this.KEY_SIZE == 16) {
            this.K1 = Pack.bigEndianToLong(bArr, 0);
            this.K2 = Pack.bigEndianToLong(bArr, 8);
        } else {
            if (this.KEY_SIZE != 20) {
                throw new IllegalStateException();
            }
            this.K0 = Pack.bigEndianToInt(bArr, 0);
            this.K1 = Pack.bigEndianToLong(bArr, 4);
            this.K2 = Pack.bigEndianToLong(bArr, 12);
        }
        this.m_state = this.forEncryption ? AsconBaseEngine.State.EncInit : AsconBaseEngine.State.DecInit;
        reset(true);
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine
    public String getAlgorithmVersion() {
        return "v1.2";
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int getOutputSize(int i) {
        return super.getOutputSize(i);
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int getUpdateOutputSize(int i) {
        return super.getUpdateOutputSize(i);
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int doFinal(byte[] bArr, int i) throws InvalidCipherTextException, IllegalStateException, DataLengthException {
        return super.doFinal(bArr, i);
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException {
        return super.processBytes(bArr, i, i2, bArr2, i3);
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
    public /* bridge */ /* synthetic */ void processAADBytes(byte[] bArr, int i, int i2) {
        super.processAADBytes(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine, org.bouncycastle.crypto.modes.AEADCipher
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
