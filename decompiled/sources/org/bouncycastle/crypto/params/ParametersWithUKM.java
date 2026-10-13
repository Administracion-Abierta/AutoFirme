package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/ParametersWithUKM.class */
public class ParametersWithUKM implements CipherParameters {
    private byte[] ukm;
    private CipherParameters parameters;

    public ParametersWithUKM(CipherParameters cipherParameters, byte[] bArr) {
        this(cipherParameters, bArr, 0, bArr.length);
    }

    public ParametersWithUKM(CipherParameters cipherParameters, byte[] bArr, int i, int i2) {
        this.ukm = new byte[i2];
        this.parameters = cipherParameters;
        System.arraycopy(bArr, i, this.ukm, 0, i2);
    }

    public byte[] getUKM() {
        return this.ukm;
    }

    public CipherParameters getParameters() {
        return this.parameters;
    }
}
