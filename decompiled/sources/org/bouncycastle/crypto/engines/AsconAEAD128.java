package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/AsconAEAD128.class */
public class AsconAEAD128 extends AsconBaseEngine {
    public AsconAEAD128() {
        this.KEY_SIZE = 16;
        this.IV_SIZE = 16;
        this.MAC_SIZE = 16;
        this.ASCON_AEAD_RATE = 16;
        this.ASCON_IV = 17594342703105L;
        this.algorithmName = "Ascon-AEAD128";
        this.nr = 8;
        this.m_bufferSizeDecrypt = this.ASCON_AEAD_RATE + this.MAC_SIZE;
        this.m_buf = new byte[this.m_bufferSizeDecrypt];
        this.dsep = Long.MIN_VALUE;
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine
    protected long pad(int i) {
        return 1 << (i << 3);
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine
    protected long loadBytes(byte[] bArr, int i) {
        return Pack.littleEndianToLong(bArr, i);
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine
    protected void setBytes(long j, byte[] bArr, int i) {
        Pack.longToLittleEndian(j, bArr, i);
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine
    protected void ascon_aeadinit() {
        this.x0 = this.ASCON_IV;
        this.x1 = this.K0;
        this.x2 = this.K1;
        this.x3 = this.N0;
        this.x4 = this.N1;
        p(12);
        this.x3 ^= this.K0;
        this.x4 ^= this.K1;
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine
    protected void processFinalAadBlock() {
        Arrays.fill(this.m_buf, this.m_bufPos, this.m_buf.length, (byte) 0);
        if (this.m_bufPos < 8) {
            this.x0 ^= Pack.littleEndianToLong(this.m_buf, 0) ^ pad(this.m_bufPos);
        } else {
            this.x0 ^= Pack.littleEndianToLong(this.m_buf, 0);
            this.x1 ^= Pack.littleEndianToLong(this.m_buf, 8) ^ pad(this.m_bufPos);
        }
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine
    protected void processFinalDecrypt(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (i >= 8) {
            long jLittleEndianToLong = Pack.littleEndianToLong(bArr, 0);
            int i3 = i - 8;
            long jLittleEndianToLong2 = Pack.littleEndianToLong(bArr, 8, i3);
            Pack.longToLittleEndian(this.x0 ^ jLittleEndianToLong, bArr2, i2);
            Pack.longToLittleEndian(this.x1 ^ jLittleEndianToLong2, bArr2, i2 + 8, i3);
            this.x0 = jLittleEndianToLong;
            this.x1 &= -(1 << (i3 << 3));
            this.x1 |= jLittleEndianToLong2;
            this.x1 ^= pad(i3);
        } else {
            if (i != 0) {
                long jLittleEndianToLong3 = Pack.littleEndianToLong(bArr, 0, i);
                Pack.longToLittleEndian(this.x0 ^ jLittleEndianToLong3, bArr2, i2, i);
                this.x0 &= -(1 << (i << 3));
                this.x0 |= jLittleEndianToLong3;
            }
            this.x0 ^= pad(i);
        }
        finishData(AsconBaseEngine.State.DecFinal);
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine
    protected void processFinalEncrypt(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (i >= 8) {
            this.x0 ^= Pack.littleEndianToLong(bArr, 0);
            int i3 = i - 8;
            this.x1 ^= Pack.littleEndianToLong(bArr, 8, i3);
            Pack.longToLittleEndian(this.x0, bArr2, i2);
            Pack.longToLittleEndian(this.x1, bArr2, i2 + 8);
            this.x1 ^= pad(i3);
        } else {
            if (i != 0) {
                this.x0 ^= Pack.littleEndianToLong(bArr, 0, i);
                Pack.longToLittleEndian(this.x0, bArr2, i2, i);
            }
            this.x0 ^= pad(i);
        }
        finishData(AsconBaseEngine.State.EncFinal);
    }

    private void finishData(AsconBaseEngine.State state) {
        this.x2 ^= this.K0;
        this.x3 ^= this.K1;
        p(12);
        this.x3 ^= this.K0;
        this.x4 ^= this.K1;
        this.m_state = state;
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    protected void init(byte[] bArr, byte[] bArr2) throws IllegalArgumentException {
        this.K0 = Pack.littleEndianToLong(bArr, 0);
        this.K1 = Pack.littleEndianToLong(bArr, 8);
        this.N0 = Pack.littleEndianToLong(bArr2, 0);
        this.N1 = Pack.littleEndianToLong(bArr2, 8);
        this.m_state = this.forEncryption ? AsconBaseEngine.State.EncInit : AsconBaseEngine.State.DecInit;
        reset(true);
    }

    @Override // org.bouncycastle.crypto.engines.AsconBaseEngine
    public String getAlgorithmVersion() {
        return "v1.3";
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
