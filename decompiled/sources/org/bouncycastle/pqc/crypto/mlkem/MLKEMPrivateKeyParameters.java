package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mlkem/MLKEMPrivateKeyParameters.class */
public class MLKEMPrivateKeyParameters extends MLKEMKeyParameters {
    final byte[] s;
    final byte[] hpk;
    final byte[] nonce;
    final byte[] t;
    final byte[] rho;
    final byte[] seed;

    public MLKEMPrivateKeyParameters(MLKEMParameters mLKEMParameters, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5) {
        this(mLKEMParameters, bArr, bArr2, bArr3, bArr4, bArr5, null);
    }

    public MLKEMPrivateKeyParameters(MLKEMParameters mLKEMParameters, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, byte[] bArr6) {
        super(true, mLKEMParameters);
        this.s = Arrays.clone(bArr);
        this.hpk = Arrays.clone(bArr2);
        this.nonce = Arrays.clone(bArr3);
        this.t = Arrays.clone(bArr4);
        this.rho = Arrays.clone(bArr5);
        this.seed = Arrays.clone(bArr6);
    }

    public MLKEMPrivateKeyParameters(MLKEMParameters mLKEMParameters, byte[] bArr) {
        super(true, mLKEMParameters);
        MLKEMEngine engine = mLKEMParameters.getEngine();
        if (bArr.length == 64) {
            byte[][] bArrGenerateKemKeyPairInternal = engine.generateKemKeyPairInternal(Arrays.copyOfRange(bArr, 0, 32), Arrays.copyOfRange(bArr, 32, bArr.length));
            this.s = bArrGenerateKemKeyPairInternal[2];
            this.hpk = bArrGenerateKemKeyPairInternal[3];
            this.nonce = bArrGenerateKemKeyPairInternal[4];
            this.t = bArrGenerateKemKeyPairInternal[0];
            this.rho = bArrGenerateKemKeyPairInternal[1];
            this.seed = bArrGenerateKemKeyPairInternal[5];
            return;
        }
        this.s = Arrays.copyOfRange(bArr, 0, engine.getKyberIndCpaSecretKeyBytes());
        int kyberIndCpaSecretKeyBytes = 0 + engine.getKyberIndCpaSecretKeyBytes();
        this.t = Arrays.copyOfRange(bArr, kyberIndCpaSecretKeyBytes, (kyberIndCpaSecretKeyBytes + engine.getKyberIndCpaPublicKeyBytes()) - 32);
        int kyberIndCpaPublicKeyBytes = kyberIndCpaSecretKeyBytes + (engine.getKyberIndCpaPublicKeyBytes() - 32);
        this.rho = Arrays.copyOfRange(bArr, kyberIndCpaPublicKeyBytes, kyberIndCpaPublicKeyBytes + 32);
        int i = kyberIndCpaPublicKeyBytes + 32;
        this.hpk = Arrays.copyOfRange(bArr, i, i + 32);
        int i2 = i + 32;
        this.nonce = Arrays.copyOfRange(bArr, i2, i2 + 32);
        this.seed = null;
    }

    public byte[] getEncoded() {
        return Arrays.concatenate(new byte[][]{this.s, this.t, this.rho, this.hpk, this.nonce});
    }

    public byte[] getHPK() {
        return Arrays.clone(this.hpk);
    }

    public byte[] getNonce() {
        return Arrays.clone(this.nonce);
    }

    public byte[] getPublicKey() {
        return MLKEMPublicKeyParameters.getEncoded(this.t, this.rho);
    }

    public MLKEMPublicKeyParameters getPublicKeyParameters() {
        return new MLKEMPublicKeyParameters(getParameters(), this.t, this.rho);
    }

    public byte[] getRho() {
        return Arrays.clone(this.rho);
    }

    public byte[] getS() {
        return Arrays.clone(this.s);
    }

    public byte[] getT() {
        return Arrays.clone(this.t);
    }

    public byte[] getSeed() {
        return Arrays.clone(this.seed);
    }
}
