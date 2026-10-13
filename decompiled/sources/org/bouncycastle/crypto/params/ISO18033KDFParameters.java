package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.DerivationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/ISO18033KDFParameters.class */
public class ISO18033KDFParameters implements DerivationParameters {
    byte[] seed;

    public ISO18033KDFParameters(byte[] bArr) {
        this.seed = bArr;
    }

    public byte[] getSeed() {
        return this.seed;
    }
}
