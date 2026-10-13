package org.bouncycastle.jcajce.spec;

import java.security.spec.KeySpec;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/MLKEMPrivateKeySpec.class */
public class MLKEMPrivateKeySpec implements KeySpec {
    private final byte[] data;
    private final byte[] publicData;
    private final MLKEMParameterSpec params;
    private final boolean isSeed;

    public MLKEMPrivateKeySpec(MLKEMParameterSpec mLKEMParameterSpec, byte[] bArr) {
        if (bArr.length != 64) {
            throw new IllegalArgumentException("incorrect length for seed");
        }
        this.isSeed = true;
        this.params = mLKEMParameterSpec;
        this.data = Arrays.clone(bArr);
        this.publicData = null;
    }

    public MLKEMPrivateKeySpec(MLKEMParameterSpec mLKEMParameterSpec, byte[] bArr, byte[] bArr2) {
        this.isSeed = false;
        this.params = mLKEMParameterSpec;
        this.data = Arrays.clone(bArr);
        this.publicData = Arrays.clone(bArr2);
    }

    public boolean isSeed() {
        return this.isSeed;
    }

    public MLKEMParameterSpec getParameterSpec() {
        return this.params;
    }

    public byte[] getSeed() {
        if (isSeed()) {
            return Arrays.clone(this.data);
        }
        throw new IllegalStateException("KeySpec represents long form");
    }

    public byte[] getPrivateData() {
        if (isSeed()) {
            throw new IllegalStateException("KeySpec represents seed");
        }
        return Arrays.clone(this.data);
    }

    public byte[] getPublicData() {
        if (isSeed()) {
            throw new IllegalStateException("KeySpec represents long form");
        }
        return Arrays.clone(this.publicData);
    }
}
