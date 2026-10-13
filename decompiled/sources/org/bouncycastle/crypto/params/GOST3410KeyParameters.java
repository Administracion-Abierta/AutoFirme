package org.bouncycastle.crypto.params;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/GOST3410KeyParameters.class */
public class GOST3410KeyParameters extends AsymmetricKeyParameter {
    private GOST3410Parameters params;

    public GOST3410KeyParameters(boolean z, GOST3410Parameters gOST3410Parameters) {
        super(z);
        this.params = gOST3410Parameters;
    }

    public GOST3410Parameters getParameters() {
        return this.params;
    }
}
