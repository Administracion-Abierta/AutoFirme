package org.spongycastle.jcajce;

import javax.crypto.interfaces.PBEKey;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/PKCS12KeyWithParameters.class */
public class PKCS12KeyWithParameters extends PKCS12Key implements PBEKey {
    private final byte[] salt;
    private final int iterationCount;

    public PKCS12KeyWithParameters(char[] password, byte[] salt, int iterationCount) {
        super(password);
        this.salt = Arrays.clone(salt);
        this.iterationCount = iterationCount;
    }

    public PKCS12KeyWithParameters(char[] password, boolean useWrongZeroLengthConversion, byte[] salt, int iterationCount) {
        super(password, useWrongZeroLengthConversion);
        this.salt = Arrays.clone(salt);
        this.iterationCount = iterationCount;
    }

    @Override // javax.crypto.interfaces.PBEKey
    public byte[] getSalt() {
        return this.salt;
    }

    @Override // javax.crypto.interfaces.PBEKey
    public int getIterationCount() {
        return this.iterationCount;
    }
}
