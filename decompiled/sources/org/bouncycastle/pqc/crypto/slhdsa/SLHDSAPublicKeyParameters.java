package org.bouncycastle.pqc.crypto.slhdsa;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/slhdsa/SLHDSAPublicKeyParameters.class */
public class SLHDSAPublicKeyParameters extends SLHDSAKeyParameters {
    private final PK pk;

    public SLHDSAPublicKeyParameters(SLHDSAParameters sLHDSAParameters, byte[] bArr) {
        super(false, sLHDSAParameters);
        int n = sLHDSAParameters.getN();
        if (bArr.length != 2 * n) {
            throw new IllegalArgumentException("public key encoding does not match parameters");
        }
        this.pk = new PK(Arrays.copyOfRange(bArr, 0, n), Arrays.copyOfRange(bArr, n, 2 * n));
    }

    SLHDSAPublicKeyParameters(SLHDSAParameters sLHDSAParameters, PK pk) {
        super(false, sLHDSAParameters);
        this.pk = pk;
    }

    public byte[] getSeed() {
        return Arrays.clone(this.pk.seed);
    }

    public byte[] getRoot() {
        return Arrays.clone(this.pk.root);
    }

    public byte[] getEncoded() {
        return Arrays.concatenate(this.pk.seed, this.pk.root);
    }
}
