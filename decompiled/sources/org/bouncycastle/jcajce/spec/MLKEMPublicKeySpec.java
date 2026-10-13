package org.bouncycastle.jcajce.spec;

import java.security.spec.KeySpec;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/MLKEMPublicKeySpec.class */
public class MLKEMPublicKeySpec implements KeySpec {
    private final MLKEMParameterSpec params;
    private final byte[] publicData;

    public MLKEMPublicKeySpec(MLKEMParameterSpec mLKEMParameterSpec, byte[] bArr) {
        this.params = mLKEMParameterSpec;
        this.publicData = Arrays.clone(bArr);
    }

    public MLKEMParameterSpec getParameterSpec() {
        return this.params;
    }

    public byte[] getPublicData() {
        return Arrays.clone(this.publicData);
    }
}
