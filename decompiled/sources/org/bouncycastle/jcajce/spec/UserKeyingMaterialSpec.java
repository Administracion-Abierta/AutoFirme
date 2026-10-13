package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/UserKeyingMaterialSpec.class */
public class UserKeyingMaterialSpec implements AlgorithmParameterSpec {
    private final byte[] userKeyingMaterial;
    private final byte[] salt;

    public UserKeyingMaterialSpec(byte[] bArr) {
        this(bArr, null);
    }

    public UserKeyingMaterialSpec(byte[] bArr, byte[] bArr2) {
        this.userKeyingMaterial = Arrays.clone(bArr);
        this.salt = Arrays.clone(bArr2);
    }

    public byte[] getUserKeyingMaterial() {
        return Arrays.clone(this.userKeyingMaterial);
    }

    public byte[] getSalt() {
        return Arrays.clone(this.salt);
    }
}
