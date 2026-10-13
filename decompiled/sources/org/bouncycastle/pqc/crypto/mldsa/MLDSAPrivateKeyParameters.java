package org.bouncycastle.pqc.crypto.mldsa;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mldsa/MLDSAPrivateKeyParameters.class */
public class MLDSAPrivateKeyParameters extends MLDSAKeyParameters {
    final byte[] rho;
    final byte[] k;
    final byte[] tr;
    final byte[] s1;
    final byte[] s2;
    final byte[] t0;
    private final byte[] t1;
    private final byte[] seed;

    public MLDSAPrivateKeyParameters(MLDSAParameters mLDSAParameters, byte[] bArr) {
        this(mLDSAParameters, bArr, null);
    }

    public MLDSAPrivateKeyParameters(MLDSAParameters mLDSAParameters, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, byte[] bArr6, byte[] bArr7) {
        this(mLDSAParameters, bArr, bArr2, bArr3, bArr4, bArr5, bArr6, bArr7, null);
    }

    public MLDSAPrivateKeyParameters(MLDSAParameters mLDSAParameters, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, byte[] bArr6, byte[] bArr7, byte[] bArr8) {
        super(true, mLDSAParameters);
        this.rho = Arrays.clone(bArr);
        this.k = Arrays.clone(bArr2);
        this.tr = Arrays.clone(bArr3);
        this.s1 = Arrays.clone(bArr4);
        this.s2 = Arrays.clone(bArr5);
        this.t0 = Arrays.clone(bArr6);
        this.t1 = Arrays.clone(bArr7);
        this.seed = Arrays.clone(bArr8);
    }

    public MLDSAPrivateKeyParameters(MLDSAParameters mLDSAParameters, byte[] bArr, MLDSAPublicKeyParameters mLDSAPublicKeyParameters) {
        super(true, mLDSAParameters);
        MLDSAEngine engine = mLDSAParameters.getEngine(null);
        if (bArr.length == 32) {
            byte[][] bArrGenerateKeyPairInternal = engine.generateKeyPairInternal(bArr);
            this.rho = bArrGenerateKeyPairInternal[0];
            this.k = bArrGenerateKeyPairInternal[1];
            this.tr = bArrGenerateKeyPairInternal[2];
            this.s1 = bArrGenerateKeyPairInternal[3];
            this.s2 = bArrGenerateKeyPairInternal[4];
            this.t0 = bArrGenerateKeyPairInternal[5];
            this.t1 = bArrGenerateKeyPairInternal[6];
            this.seed = bArrGenerateKeyPairInternal[7];
            return;
        }
        this.rho = Arrays.copyOfRange(bArr, 0, 32);
        int i = 0 + 32;
        this.k = Arrays.copyOfRange(bArr, i, i + 32);
        int i2 = i + 32;
        this.tr = Arrays.copyOfRange(bArr, i2, i2 + 64);
        int i3 = i2 + 64;
        int dilithiumL = engine.getDilithiumL() * engine.getDilithiumPolyEtaPackedBytes();
        this.s1 = Arrays.copyOfRange(bArr, i3, i3 + dilithiumL);
        int i4 = i3 + dilithiumL;
        int dilithiumK = engine.getDilithiumK() * engine.getDilithiumPolyEtaPackedBytes();
        this.s2 = Arrays.copyOfRange(bArr, i4, i4 + dilithiumK);
        int i5 = i4 + dilithiumK;
        int dilithiumK2 = engine.getDilithiumK() * 416;
        this.t0 = Arrays.copyOfRange(bArr, i5, i5 + dilithiumK2);
        int i6 = i5 + dilithiumK2;
        this.t1 = engine.deriveT1(this.rho, this.k, this.tr, this.s1, this.s2, this.t0);
        if (mLDSAPublicKeyParameters != null && !Arrays.constantTimeAreEqual(this.t1, mLDSAPublicKeyParameters.getT1())) {
            throw new IllegalArgumentException("passed in public key does not match private values");
        }
        this.seed = null;
    }

    public byte[] getEncoded() {
        return Arrays.concatenate(new byte[][]{this.rho, this.k, this.tr, this.s1, this.s2, this.t0});
    }

    public byte[] getK() {
        return Arrays.clone(this.k);
    }

    public byte[] getPrivateKey() {
        return getEncoded();
    }

    public byte[] getPublicKey() {
        return MLDSAPublicKeyParameters.getEncoded(this.rho, this.t1);
    }

    public byte[] getSeed() {
        return Arrays.clone(this.seed);
    }

    public MLDSAPublicKeyParameters getPublicKeyParameters() {
        if (this.t1 == null) {
            return null;
        }
        return new MLDSAPublicKeyParameters(getParameters(), this.rho, this.t1);
    }

    public byte[] getRho() {
        return Arrays.clone(this.rho);
    }

    public byte[] getS1() {
        return Arrays.clone(this.s1);
    }

    public byte[] getS2() {
        return Arrays.clone(this.s2);
    }

    public byte[] getT0() {
        return Arrays.clone(this.t0);
    }

    public byte[] getT1() {
        return Arrays.clone(this.t1);
    }

    public byte[] getTr() {
        return Arrays.clone(this.tr);
    }
}
