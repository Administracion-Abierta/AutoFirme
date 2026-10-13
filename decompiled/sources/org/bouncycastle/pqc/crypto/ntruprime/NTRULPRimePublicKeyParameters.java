package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntruprime/NTRULPRimePublicKeyParameters.class */
public class NTRULPRimePublicKeyParameters extends NTRULPRimeKeyParameters {
    private final byte[] seed;
    private final byte[] roundEncA;

    public NTRULPRimePublicKeyParameters(NTRULPRimeParameters nTRULPRimeParameters, byte[] bArr) {
        super(false, nTRULPRimeParameters);
        this.seed = Arrays.copyOfRange(bArr, 0, 32);
        this.roundEncA = Arrays.copyOfRange(bArr, this.seed.length, bArr.length);
    }

    NTRULPRimePublicKeyParameters(NTRULPRimeParameters nTRULPRimeParameters, byte[] bArr, byte[] bArr2) {
        super(false, nTRULPRimeParameters);
        this.seed = Arrays.clone(bArr);
        this.roundEncA = Arrays.clone(bArr2);
    }

    byte[] getSeed() {
        return this.seed;
    }

    byte[] getRoundEncA() {
        return this.roundEncA;
    }

    public byte[] getEncoded() {
        byte[] bArr = new byte[getParameters().getPublicKeyBytes()];
        System.arraycopy(this.seed, 0, bArr, 0, this.seed.length);
        System.arraycopy(this.roundEncA, 0, bArr, this.seed.length, this.roundEncA.length);
        return bArr;
    }
}
