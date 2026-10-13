package org.bouncycastle.pqc.crypto.slhdsa;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/slhdsa/SLHDSAPrivateKeyParameters.class */
public class SLHDSAPrivateKeyParameters extends SLHDSAKeyParameters {
    final SK sk;
    final PK pk;

    public SLHDSAPrivateKeyParameters(SLHDSAParameters sLHDSAParameters, byte[] bArr) {
        super(true, sLHDSAParameters);
        int n = sLHDSAParameters.getN();
        if (bArr.length != 4 * n) {
            throw new IllegalArgumentException("private key encoding does not match parameters");
        }
        this.sk = new SK(Arrays.copyOfRange(bArr, 0, n), Arrays.copyOfRange(bArr, n, 2 * n));
        this.pk = new PK(Arrays.copyOfRange(bArr, 2 * n, 3 * n), Arrays.copyOfRange(bArr, 3 * n, 4 * n));
    }

    public SLHDSAPrivateKeyParameters(SLHDSAParameters sLHDSAParameters, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        super(true, sLHDSAParameters);
        this.sk = new SK(bArr, bArr2);
        this.pk = new PK(bArr3, bArr4);
    }

    SLHDSAPrivateKeyParameters(SLHDSAParameters sLHDSAParameters, SK sk, PK pk) {
        super(true, sLHDSAParameters);
        this.sk = sk;
        this.pk = pk;
    }

    public byte[] getSeed() {
        return Arrays.clone(this.sk.seed);
    }

    public byte[] getPrf() {
        return Arrays.clone(this.sk.prf);
    }

    public byte[] getPublicSeed() {
        return Arrays.clone(this.pk.seed);
    }

    public byte[] getRoot() {
        return Arrays.clone(this.pk.root);
    }

    public byte[] getPublicKey() {
        return Arrays.concatenate(this.pk.seed, this.pk.root);
    }

    public byte[] getEncoded() {
        return Arrays.concatenate(new byte[][]{this.sk.seed, this.sk.prf, this.pk.seed, this.pk.root});
    }

    public byte[] getEncodedPublicKey() {
        return Arrays.concatenate(this.pk.seed, this.pk.root);
    }
}
