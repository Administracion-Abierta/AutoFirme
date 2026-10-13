package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/ParametersWithContext.class */
public class ParametersWithContext implements CipherParameters {
    private CipherParameters parameters;
    private byte[] context;

    public ParametersWithContext(CipherParameters cipherParameters, byte[] bArr) {
        if (bArr == null) {
            throw new NullPointerException("'context' cannot be null");
        }
        this.parameters = cipherParameters;
        this.context = Arrays.clone(bArr);
    }

    public void copyContextTo(byte[] bArr, int i, int i2) {
        if (this.context.length != i2) {
            throw new IllegalArgumentException("len");
        }
        System.arraycopy(this.context, 0, bArr, i, i2);
    }

    public byte[] getContext() {
        return Arrays.clone(this.context);
    }

    public int getContextLength() {
        return this.context.length;
    }

    public CipherParameters getParameters() {
        return this.parameters;
    }
}
