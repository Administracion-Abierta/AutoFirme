package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/macs/GOST28147Mac.class */
public class GOST28147Mac implements Mac {
    private final CryptoServicePurpose purpose;
    private static final int BLOCK_SIZE = 8;
    private static final int MAC_SIZE = 4;
    private int bufOff;
    private byte[] buf;
    private byte[] mac;
    private boolean firstStep;
    private int[] workingKey;
    private byte[] macIV;
    private byte[] S;

    public GOST28147Mac() {
        this(CryptoServicePurpose.AUTHENTICATION);
    }

    public GOST28147Mac(CryptoServicePurpose cryptoServicePurpose) {
        this.firstStep = true;
        this.workingKey = null;
        this.macIV = null;
        this.S = new byte[]{9, 6, 3, 2, 8, 11, 1, 7, 10, 4, 14, 15, 12, 0, 13, 5, 3, 7, 14, 9, 8, 10, 15, 0, 5, 2, 6, 12, 11, 4, 13, 1, 14, 4, 6, 2, 11, 3, 13, 8, 12, 15, 5, 10, 0, 7, 1, 9, 14, 7, 10, 12, 13, 1, 3, 9, 0, 2, 11, 4, 15, 8, 5, 6, 11, 5, 1, 9, 8, 13, 15, 0, 14, 4, 2, 3, 12, 7, 10, 6, 3, 10, 13, 12, 1, 2, 0, 11, 7, 5, 9, 4, 8, 15, 14, 6, 1, 13, 2, 9, 7, 10, 6, 0, 8, 12, 4, 5, 15, 3, 11, 14, 11, 10, 15, 5, 0, 12, 14, 8, 6, 2, 3, 9, 1, 7, 13, 4};
        this.purpose = cryptoServicePurpose;
        this.mac = new byte[8];
        this.buf = new byte[8];
        this.bufOff = 0;
    }

    private int[] generateWorkingKey(byte[] bArr) {
        if (bArr.length != 32) {
            throw new IllegalArgumentException("Key length invalid. Key needs to be 32 byte - 256 bit!!!");
        }
        int[] iArr = new int[8];
        for (int i = 0; i != 8; i++) {
            iArr[i] = Pack.littleEndianToInt(bArr, i * 4);
        }
        return iArr;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void init(CipherParameters cipherParameters) throws IllegalArgumentException {
        reset();
        this.buf = new byte[8];
        this.macIV = null;
        recursiveInit(cipherParameters);
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), 178, cipherParameters, this.purpose));
    }

    private void recursiveInit(CipherParameters cipherParameters) throws IllegalArgumentException {
        if (cipherParameters == null) {
            return;
        }
        CipherParameters parameters = null;
        if (cipherParameters instanceof ParametersWithSBox) {
            ParametersWithSBox parametersWithSBox = (ParametersWithSBox) cipherParameters;
            System.arraycopy(parametersWithSBox.getSBox(), 0, this.S, 0, parametersWithSBox.getSBox().length);
            parameters = parametersWithSBox.getParameters();
        } else if (cipherParameters instanceof KeyParameter) {
            this.workingKey = generateWorkingKey(((KeyParameter) cipherParameters).getKey());
        } else {
            if (!(cipherParameters instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("invalid parameter passed to GOST28147 init - " + cipherParameters.getClass().getName());
            }
            ParametersWithIV parametersWithIV = (ParametersWithIV) cipherParameters;
            System.arraycopy(parametersWithIV.getIV(), 0, this.mac, 0, this.mac.length);
            this.macIV = parametersWithIV.getIV();
            parameters = parametersWithIV.getParameters();
        }
        recursiveInit(parameters);
    }

    @Override // org.bouncycastle.crypto.Mac
    public String getAlgorithmName() {
        return "GOST28147Mac";
    }

    @Override // org.bouncycastle.crypto.Mac
    public int getMacSize() {
        return 4;
    }

    private int gost28147_mainStep(int i, int i2) {
        int i3 = i2 + i;
        int i4 = (this.S[0 + ((i3 >> 0) & 15)] << 0) + (this.S[16 + ((i3 >> 4) & 15)] << 4) + (this.S[32 + ((i3 >> 8) & 15)] << 8) + (this.S[48 + ((i3 >> 12) & 15)] << 12) + (this.S[64 + ((i3 >> 16) & 15)] << 16) + (this.S[80 + ((i3 >> 20) & 15)] << 20) + (this.S[96 + ((i3 >> 24) & 15)] << 24) + (this.S[112 + ((i3 >> 28) & 15)] << 28);
        return (i4 << 11) | (i4 >>> 21);
    }

    private void gost28147MacFunc(int[] iArr, byte[] bArr, int i, byte[] bArr2, int i2) {
        int iLittleEndianToInt = Pack.littleEndianToInt(bArr, i);
        int iLittleEndianToInt2 = Pack.littleEndianToInt(bArr, i + 4);
        for (int i3 = 0; i3 < 2; i3++) {
            for (int i4 = 0; i4 < 8; i4++) {
                int i5 = iLittleEndianToInt;
                iLittleEndianToInt = iLittleEndianToInt2 ^ gost28147_mainStep(iLittleEndianToInt, iArr[i4]);
                iLittleEndianToInt2 = i5;
            }
        }
        Pack.intToLittleEndian(iLittleEndianToInt, bArr2, i2);
        Pack.intToLittleEndian(iLittleEndianToInt2, bArr2, i2 + 4);
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte b) throws IllegalStateException {
        if (this.bufOff == this.buf.length) {
            byte[] bArr = new byte[this.buf.length];
            if (this.firstStep) {
                this.firstStep = false;
                if (this.macIV != null) {
                    CM5func(this.buf, 0, this.macIV, bArr);
                } else {
                    System.arraycopy(this.buf, 0, bArr, 0, this.mac.length);
                }
            } else {
                CM5func(this.buf, 0, this.mac, bArr);
            }
            gost28147MacFunc(this.workingKey, bArr, 0, this.mac, 0);
            this.bufOff = 0;
        }
        byte[] bArr2 = this.buf;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr2[i] = b;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte[] bArr, int i, int i2) throws IllegalStateException, DataLengthException {
        if (i2 < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int i3 = 8 - this.bufOff;
        if (i2 > i3) {
            System.arraycopy(bArr, i, this.buf, this.bufOff, i3);
            byte[] bArr2 = new byte[this.buf.length];
            if (this.firstStep) {
                this.firstStep = false;
                if (this.macIV != null) {
                    CM5func(this.buf, 0, this.macIV, bArr2);
                } else {
                    System.arraycopy(this.buf, 0, bArr2, 0, this.mac.length);
                }
            } else {
                CM5func(this.buf, 0, this.mac, bArr2);
            }
            gost28147MacFunc(this.workingKey, bArr2, 0, this.mac, 0);
            this.bufOff = 0;
            i2 -= i3;
            i += i3;
            while (i2 > 8) {
                CM5func(bArr, i, this.mac, bArr2);
                gost28147MacFunc(this.workingKey, bArr2, 0, this.mac, 0);
                i2 -= 8;
                i += 8;
            }
        }
        System.arraycopy(bArr, i, this.buf, this.bufOff, i2);
        this.bufOff += i2;
    }

    @Override // org.bouncycastle.crypto.Mac
    public int doFinal(byte[] bArr, int i) throws IllegalStateException, DataLengthException {
        while (this.bufOff < 8) {
            this.buf[this.bufOff] = 0;
            this.bufOff++;
        }
        byte[] bArr2 = new byte[this.buf.length];
        if (this.firstStep) {
            this.firstStep = false;
            System.arraycopy(this.buf, 0, bArr2, 0, this.mac.length);
        } else {
            CM5func(this.buf, 0, this.mac, bArr2);
        }
        gost28147MacFunc(this.workingKey, bArr2, 0, this.mac, 0);
        System.arraycopy(this.mac, (this.mac.length / 2) - 4, bArr, i, 4);
        reset();
        return 4;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void reset() {
        for (int i = 0; i < this.buf.length; i++) {
            this.buf[i] = 0;
        }
        this.bufOff = 0;
        this.firstStep = true;
    }

    private static void CM5func(byte[] bArr, int i, byte[] bArr2, byte[] bArr3) {
        for (int i2 = 0; i2 < 8; i2++) {
            bArr3[i2] = (byte) (bArr[i + i2] ^ bArr2[i2]);
        }
    }
}
