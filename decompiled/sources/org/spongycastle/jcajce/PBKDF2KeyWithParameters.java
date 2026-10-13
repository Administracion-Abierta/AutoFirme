package org.spongycastle.jcajce;

import javax.crypto.interfaces.PBEKey;
import org.spongycastle.crypto.CharToByteConverter;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/PBKDF2KeyWithParameters.class */
public class PBKDF2KeyWithParameters extends PBKDF2Key implements PBEKey {
    private final byte[] salt;
    private final int iterationCount;

    public PBKDF2KeyWithParameters(char[] password, CharToByteConverter converter, byte[] salt, int iterationCount) {
        super(password, converter);
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
