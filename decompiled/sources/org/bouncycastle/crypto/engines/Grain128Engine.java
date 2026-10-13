package org.bouncycastle.crypto.engines;

import com.sun.jna.platform.win32.Winspool;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/Grain128Engine.class */
public class Grain128Engine implements StreamCipher {
    private static final int STATE_SIZE = 4;
    private byte[] workingKey;
    private byte[] workingIV;
    private byte[] out;
    private int[] lfsr;
    private int[] nfsr;
    private int output;
    private int index = 4;
    private boolean initialised = false;

    @Override // org.bouncycastle.crypto.StreamCipher
    public String getAlgorithmName() {
        return "Grain-128";
    }

    @Override // org.bouncycastle.crypto.StreamCipher
    public void init(boolean z, CipherParameters cipherParameters) throws IllegalArgumentException {
        if (!(cipherParameters instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("Grain-128 Init parameters must include an IV");
        }
        ParametersWithIV parametersWithIV = (ParametersWithIV) cipherParameters;
        byte[] iv = parametersWithIV.getIV();
        if (iv == null || iv.length != 12) {
            throw new IllegalArgumentException("Grain-128 requires exactly 12 bytes of IV");
        }
        if (!(parametersWithIV.getParameters() instanceof KeyParameter)) {
            throw new IllegalArgumentException("Grain-128 init parameters must include a key");
        }
        byte[] key = ((KeyParameter) parametersWithIV.getParameters()).getKey();
        if (key.length != 16) {
            throw new IllegalArgumentException("Grain-128 key must be 128 bits long");
        }
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), 128, cipherParameters, Utils.getPurpose(z)));
        this.workingIV = new byte[key.length];
        this.workingKey = new byte[key.length];
        this.lfsr = new int[4];
        this.nfsr = new int[4];
        this.out = new byte[4];
        System.arraycopy(iv, 0, this.workingIV, 0, iv.length);
        System.arraycopy(key, 0, this.workingKey, 0, key.length);
        reset();
    }

    private void initGrain() {
        for (int i = 0; i < 8; i++) {
            this.output = getOutput();
            this.nfsr = shift(this.nfsr, (getOutputNFSR() ^ this.lfsr[0]) ^ this.output);
            this.lfsr = shift(this.lfsr, getOutputLFSR() ^ this.output);
        }
        this.initialised = true;
    }

    private int getOutputNFSR() {
        int i = this.nfsr[0];
        int i2 = (this.nfsr[0] >>> 3) | (this.nfsr[1] << 29);
        int i3 = (this.nfsr[0] >>> 11) | (this.nfsr[1] << 21);
        int i4 = (this.nfsr[0] >>> 13) | (this.nfsr[1] << 19);
        int i5 = (this.nfsr[0] >>> 17) | (this.nfsr[1] << 15);
        int i6 = (this.nfsr[0] >>> 18) | (this.nfsr[1] << 14);
        int i7 = (this.nfsr[0] >>> 26) | (this.nfsr[1] << 6);
        int i8 = (this.nfsr[0] >>> 27) | (this.nfsr[1] << 5);
        int i9 = (this.nfsr[1] >>> 8) | (this.nfsr[2] << 24);
        int i10 = (this.nfsr[1] >>> 16) | (this.nfsr[2] << 16);
        int i11 = (this.nfsr[1] >>> 24) | (this.nfsr[2] << 8);
        int i12 = (this.nfsr[1] >>> 27) | (this.nfsr[2] << 5);
        int i13 = (this.nfsr[1] >>> 29) | (this.nfsr[2] << 3);
        int i14 = (this.nfsr[2] >>> 1) | (this.nfsr[3] << 31);
        int i15 = (this.nfsr[2] >>> 3) | (this.nfsr[3] << 29);
        int i16 = (this.nfsr[2] >>> 4) | (this.nfsr[3] << 28);
        int i17 = (this.nfsr[2] >>> 20) | (this.nfsr[3] << 12);
        int i18 = (this.nfsr[2] >>> 27) | (this.nfsr[3] << 5);
        return ((((((((((i ^ i7) ^ i11) ^ i18) ^ this.nfsr[3]) ^ (i2 & i15)) ^ (i3 & i4)) ^ (i5 & i6)) ^ (i8 & i12)) ^ (i9 & i10)) ^ (i13 & i14)) ^ (i16 & i17);
    }

    private int getOutputLFSR() {
        int i = this.lfsr[0];
        int i2 = (this.lfsr[0] >>> 7) | (this.lfsr[1] << 25);
        int i3 = (this.lfsr[1] >>> 6) | (this.lfsr[2] << 26);
        int i4 = (this.lfsr[2] >>> 6) | (this.lfsr[3] << 26);
        int i5 = (this.lfsr[2] >>> 17) | (this.lfsr[3] << 15);
        return ((((i ^ i2) ^ i3) ^ i4) ^ i5) ^ this.lfsr[3];
    }

    private int getOutput() {
        int i = (this.nfsr[0] >>> 2) | (this.nfsr[1] << 30);
        int i2 = (this.nfsr[0] >>> 12) | (this.nfsr[1] << 20);
        int i3 = (this.nfsr[0] >>> 15) | (this.nfsr[1] << 17);
        int i4 = (this.nfsr[1] >>> 4) | (this.nfsr[2] << 28);
        int i5 = (this.nfsr[1] >>> 13) | (this.nfsr[2] << 19);
        int i6 = this.nfsr[2];
        int i7 = (this.nfsr[2] >>> 9) | (this.nfsr[3] << 23);
        int i8 = (this.nfsr[2] >>> 25) | (this.nfsr[3] << 7);
        int i9 = (this.nfsr[2] >>> 31) | (this.nfsr[3] << 1);
        int i10 = (this.lfsr[0] >>> 8) | (this.lfsr[1] << 24);
        int i11 = (this.lfsr[0] >>> 13) | (this.lfsr[1] << 19);
        int i12 = (this.lfsr[0] >>> 20) | (this.lfsr[1] << 12);
        int i13 = (this.lfsr[1] >>> 10) | (this.lfsr[2] << 22);
        int i14 = (this.lfsr[1] >>> 28) | (this.lfsr[2] << 4);
        int i15 = (this.lfsr[2] >>> 15) | (this.lfsr[3] << 17);
        return ((((((((((((i2 & i10) ^ (i11 & i12)) ^ (i9 & i13)) ^ (i14 & i15)) ^ ((i2 & i9) & ((this.lfsr[2] >>> 31) | (this.lfsr[3] << 1)))) ^ ((this.lfsr[2] >>> 29) | (this.lfsr[3] << 3))) ^ i) ^ i3) ^ i4) ^ i5) ^ i6) ^ i7) ^ i8;
    }

    private int[] shift(int[] iArr, int i) {
        iArr[0] = iArr[1];
        iArr[1] = iArr[2];
        iArr[2] = iArr[3];
        iArr[3] = i;
        return iArr;
    }

    private void setKey(byte[] bArr, byte[] bArr2) {
        bArr2[12] = -1;
        bArr2[13] = -1;
        bArr2[14] = -1;
        bArr2[15] = -1;
        this.workingKey = bArr;
        this.workingIV = bArr2;
        int i = 0;
        for (int i2 = 0; i2 < this.nfsr.length; i2++) {
            this.nfsr[i2] = (this.workingKey[i + 3] << 24) | ((this.workingKey[i + 2] << 16) & Winspool.PRINTER_ENUM_ICONMASK) | ((this.workingKey[i + 1] << 8) & 65280) | (this.workingKey[i] & 255);
            this.lfsr[i2] = (this.workingIV[i + 3] << 24) | ((this.workingIV[i + 2] << 16) & Winspool.PRINTER_ENUM_ICONMASK) | ((this.workingIV[i + 1] << 8) & 65280) | (this.workingIV[i] & 255);
            i += 4;
        }
    }

    @Override // org.bouncycastle.crypto.StreamCipher
    public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws DataLengthException {
        if (!this.initialised) {
            throw new IllegalStateException(getAlgorithmName() + " not initialised");
        }
        if (i + i2 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (i3 + i2 > bArr2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i4 = 0; i4 < i2; i4++) {
            bArr2[i3 + i4] = (byte) (bArr[i + i4] ^ getKeyStream());
        }
        return i2;
    }

    @Override // org.bouncycastle.crypto.StreamCipher
    public void reset() {
        this.index = 4;
        setKey(this.workingKey, this.workingIV);
        initGrain();
    }

    private void oneRound() {
        this.output = getOutput();
        this.out[0] = (byte) this.output;
        this.out[1] = (byte) (this.output >> 8);
        this.out[2] = (byte) (this.output >> 16);
        this.out[3] = (byte) (this.output >> 24);
        this.nfsr = shift(this.nfsr, getOutputNFSR() ^ this.lfsr[0]);
        this.lfsr = shift(this.lfsr, getOutputLFSR());
    }

    @Override // org.bouncycastle.crypto.StreamCipher
    public byte returnByte(byte b) {
        if (this.initialised) {
            return (byte) (b ^ getKeyStream());
        }
        throw new IllegalStateException(getAlgorithmName() + " not initialised");
    }

    private byte getKeyStream() {
        if (this.index > 3) {
            oneRound();
            this.index = 0;
        }
        byte[] bArr = this.out;
        int i = this.index;
        this.index = i + 1;
        return bArr[i];
    }
}
