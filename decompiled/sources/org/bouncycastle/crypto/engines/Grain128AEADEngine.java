package org.bouncycastle.crypto.engines;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/Grain128AEADEngine.class */
public class Grain128AEADEngine extends AEADBaseEngine {
    private static final int STATE_SIZE = 4;
    private byte[] workingKey;
    private byte[] workingIV;
    private int[] lfsr;
    private int[] nfsr;
    private int[] authAcc;
    private int[] authSr;
    private boolean initialised = false;
    private boolean aadFinished = false;
    private final ErasableOutputStream aadData = new ErasableOutputStream();

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/Grain128AEADEngine$ErasableOutputStream.class */
    private static final class ErasableOutputStream extends ByteArrayOutputStream {
        public byte[] getBuf() {
            return this.buf;
        }
    }

    public Grain128AEADEngine() {
        this.algorithmName = "Grain-128AEAD";
        this.KEY_SIZE = 16;
        this.IV_SIZE = 12;
        this.MAC_SIZE = 8;
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    protected void init(byte[] bArr, byte[] bArr2) throws IllegalArgumentException {
        this.workingIV = new byte[16];
        this.workingKey = bArr;
        this.lfsr = new int[4];
        this.nfsr = new int[4];
        this.authAcc = new int[2];
        this.authSr = new int[2];
        System.arraycopy(bArr2, 0, this.workingIV, 0, this.IV_SIZE);
        reset();
    }

    private void initGrain() {
        for (int i = 0; i < 320; i++) {
            int output = getOutput();
            this.nfsr = shift(this.nfsr, ((getOutputNFSR() ^ this.lfsr[0]) ^ output) & 1);
            this.lfsr = shift(this.lfsr, (getOutputLFSR() ^ output) & 1);
        }
        for (int i2 = 0; i2 < 8; i2++) {
            for (int i3 = 0; i3 < 8; i3++) {
                int output2 = getOutput();
                this.nfsr = shift(this.nfsr, (((getOutputNFSR() ^ this.lfsr[0]) ^ output2) ^ (this.workingKey[i2] >> i3)) & 1);
                this.lfsr = shift(this.lfsr, ((getOutputLFSR() ^ output2) ^ (this.workingKey[i2 + 8] >> i3)) & 1);
            }
        }
        initGrain(this.authAcc);
        initGrain(this.authSr);
        this.initialised = true;
    }

    private void initGrain(int[] iArr) {
        for (int i = 0; i < 2; i++) {
            for (int i2 = 0; i2 < 32; i2++) {
                int output = getOutput();
                this.nfsr = shift(this.nfsr, (getOutputNFSR() ^ this.lfsr[0]) & 1);
                this.lfsr = shift(this.lfsr, getOutputLFSR() & 1);
                int i3 = i;
                iArr[i3] = iArr[i3] | (output << i2);
            }
        }
    }

    private int getOutputNFSR() {
        int i = this.nfsr[0];
        int i2 = this.nfsr[0] >>> 3;
        int i3 = this.nfsr[0] >>> 11;
        int i4 = this.nfsr[0] >>> 13;
        int i5 = this.nfsr[0] >>> 17;
        int i6 = this.nfsr[0] >>> 18;
        int i7 = this.nfsr[0] >>> 22;
        int i8 = this.nfsr[0] >>> 24;
        int i9 = this.nfsr[0] >>> 25;
        int i10 = this.nfsr[0] >>> 26;
        int i11 = this.nfsr[0] >>> 27;
        int i12 = this.nfsr[1] >>> 8;
        int i13 = this.nfsr[1] >>> 16;
        int i14 = this.nfsr[1] >>> 24;
        int i15 = this.nfsr[1] >>> 27;
        int i16 = this.nfsr[1] >>> 29;
        int i17 = this.nfsr[2] >>> 1;
        int i18 = this.nfsr[2] >>> 3;
        int i19 = this.nfsr[2] >>> 4;
        int i20 = this.nfsr[2] >>> 6;
        int i21 = this.nfsr[2] >>> 14;
        int i22 = this.nfsr[2] >>> 18;
        int i23 = this.nfsr[2] >>> 20;
        int i24 = this.nfsr[2] >>> 24;
        int i25 = this.nfsr[2] >>> 27;
        int i26 = this.nfsr[2] >>> 28;
        int i27 = this.nfsr[2] >>> 29;
        int i28 = this.nfsr[2] >>> 31;
        return ((((((((((((((i ^ i10) ^ i14) ^ i25) ^ this.nfsr[3]) ^ (i2 & i18)) ^ (i3 & i4)) ^ (i5 & i6)) ^ (i11 & i15)) ^ (i12 & i13)) ^ (i16 & i17)) ^ (i19 & i23)) ^ ((i7 & i8) & i9)) ^ ((i20 & i21) & i22)) ^ (((i24 & i26) & i27) & i28)) & 1;
    }

    private int getOutputLFSR() {
        int i = this.lfsr[0];
        int i2 = this.lfsr[0] >>> 7;
        int i3 = this.lfsr[1] >>> 6;
        int i4 = this.lfsr[2] >>> 6;
        int i5 = this.lfsr[2] >>> 17;
        return (((((i ^ i2) ^ i3) ^ i4) ^ i5) ^ this.lfsr[3]) & 1;
    }

    private int getOutput() {
        int i = this.nfsr[0] >>> 2;
        int i2 = this.nfsr[0] >>> 12;
        int i3 = this.nfsr[0] >>> 15;
        int i4 = this.nfsr[1] >>> 4;
        int i5 = this.nfsr[1] >>> 13;
        int i6 = this.nfsr[2];
        int i7 = this.nfsr[2] >>> 9;
        int i8 = this.nfsr[2] >>> 25;
        int i9 = this.nfsr[2] >>> 31;
        int i10 = this.lfsr[0] >>> 8;
        int i11 = this.lfsr[0] >>> 13;
        int i12 = this.lfsr[0] >>> 20;
        int i13 = this.lfsr[1] >>> 10;
        int i14 = this.lfsr[1] >>> 28;
        int i15 = this.lfsr[2] >>> 15;
        return (((((((((((((i2 & i10) ^ (i11 & i12)) ^ (i9 & i13)) ^ (i14 & i15)) ^ ((i2 & i9) & (this.lfsr[2] >>> 30))) ^ (this.lfsr[2] >>> 29)) ^ i) ^ i3) ^ i4) ^ i5) ^ i6) ^ i7) ^ i8) & 1;
    }

    private int[] shift(int[] iArr, int i) {
        iArr[0] = (iArr[0] >>> 1) | (iArr[1] << 31);
        iArr[1] = (iArr[1] >>> 1) | (iArr[2] << 31);
        iArr[2] = (iArr[2] >>> 1) | (iArr[3] << 31);
        iArr[3] = (iArr[3] >>> 1) | (i << 31);
        return iArr;
    }

    private void setKey(byte[] bArr, byte[] bArr2) {
        bArr2[12] = -1;
        bArr2[13] = -1;
        bArr2[14] = -1;
        bArr2[15] = 127;
        this.workingKey = bArr;
        this.workingIV = bArr2;
        Pack.littleEndianToInt(this.workingKey, 0, this.nfsr);
        Pack.littleEndianToInt(this.workingIV, 0, this.lfsr);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException {
        if (!this.initialised) {
            throw new IllegalStateException(getAlgorithmName() + " not initialised");
        }
        if (!this.aadFinished) {
            doProcessAADBytes(this.aadData.getBuf(), this.aadData.size());
            this.aadFinished = true;
        }
        if (i + i2 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (i3 + i2 > bArr2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        getKeyStream(bArr, i, i2, bArr2, i3);
        return i2;
    }

    @Override // org.bouncycastle.crypto.engines.AEADBaseEngine
    protected void reset(boolean z) {
        this.aadData.reset();
        this.aadFinished = false;
        setKey(this.workingKey, this.workingIV);
        initGrain();
        super.reset(z);
    }

    private void getKeyStream(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        for (int i4 = 0; i4 < i2; i4++) {
            byte b = 0;
            byte b2 = bArr[i + i4];
            for (int i5 = 0; i5 < 8; i5++) {
                int output = getOutput();
                this.nfsr = shift(this.nfsr, (getOutputNFSR() ^ this.lfsr[0]) & 1);
                this.lfsr = shift(this.lfsr, getOutputLFSR() & 1);
                int i6 = (b2 >> i5) & 1;
                b = (byte) (b | ((i6 ^ output) << i5));
                updateInternalState(i6);
            }
            bArr2[i3 + i4] = b;
        }
    }

    private void updateInternalState(int i) {
        int i2 = -i;
        int[] iArr = this.authAcc;
        iArr[0] = iArr[0] ^ (this.authSr[0] & i2);
        int[] iArr2 = this.authAcc;
        iArr2[1] = iArr2[1] ^ (this.authSr[1] & i2);
        authShift(getOutput());
        this.nfsr = shift(this.nfsr, (getOutputNFSR() ^ this.lfsr[0]) & 1);
        this.lfsr = shift(this.lfsr, getOutputLFSR() & 1);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void processAADByte(byte b) {
        if (this.aadFinished) {
            throw new IllegalStateException("associated data must be added before plaintext/ciphertext");
        }
        this.aadData.write(b);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public void processAADBytes(byte[] bArr, int i, int i2) {
        if (this.aadFinished) {
            throw new IllegalStateException("associated data must be added before plaintext/ciphertext");
        }
        this.aadData.write(bArr, i, i2);
    }

    private void doProcessAADBytes(byte[] bArr, int i) {
        int iLen_length;
        byte[] bArr2;
        if (i < 128) {
            bArr2 = new byte[1 + i];
            bArr2[0] = (byte) i;
            iLen_length = 0;
        } else {
            iLen_length = len_length(i);
            bArr2 = new byte[1 + iLen_length + i];
            bArr2[0] = (byte) (128 | iLen_length);
            int i2 = i;
            for (int i3 = 0; i3 < iLen_length; i3++) {
                bArr2[1 + i3] = (byte) i2;
                i2 >>>= 8;
            }
        }
        for (int i4 = 0; i4 < i; i4++) {
            bArr2[1 + iLen_length + i4] = bArr[i4];
        }
        for (byte b : bArr2) {
            for (int i5 = 0; i5 < 8; i5++) {
                this.nfsr = shift(this.nfsr, (getOutputNFSR() ^ this.lfsr[0]) & 1);
                this.lfsr = shift(this.lfsr, getOutputLFSR() & 1);
                updateInternalState((b >> i5) & 1);
            }
        }
    }

    private void accumulate() {
        int[] iArr = this.authAcc;
        iArr[0] = iArr[0] ^ this.authSr[0];
        int[] iArr2 = this.authAcc;
        iArr2[1] = iArr2[1] ^ this.authSr[1];
    }

    private void authShift(int i) {
        this.authSr[0] = (this.authSr[0] >>> 1) | (this.authSr[1] << 31);
        this.authSr[1] = (this.authSr[1] >>> 1) | (i << 31);
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int doFinal(byte[] bArr, int i) throws InvalidCipherTextException, IllegalStateException {
        if (!this.aadFinished) {
            doProcessAADBytes(this.aadData.getBuf(), this.aadData.size());
            this.aadFinished = true;
        }
        accumulate();
        this.mac = Pack.intToLittleEndian(this.authAcc);
        System.arraycopy(this.mac, 0, bArr, i, this.mac.length);
        reset(false);
        return this.mac.length;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int getUpdateOutputSize(int i) {
        return i;
    }

    @Override // org.bouncycastle.crypto.modes.AEADCipher
    public int getOutputSize(int i) {
        return i + 8;
    }

    private static int len_length(int i) {
        if ((i & 255) == i) {
            return 1;
        }
        if ((i & 65535) == i) {
            return 2;
        }
        return (i & 16777215) == i ? 3 : 4;
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
