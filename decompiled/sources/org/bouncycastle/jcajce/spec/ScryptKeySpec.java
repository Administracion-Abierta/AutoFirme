package org.bouncycastle.jcajce.spec;

import java.security.spec.KeySpec;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/ScryptKeySpec.class */
public class ScryptKeySpec implements KeySpec {
    private final char[] password;
    private final byte[] salt;
    private final int costParameter;
    private final int blockSize;
    private final int parallelizationParameter;
    private final int keySize;

    public ScryptKeySpec(char[] cArr, byte[] bArr, int i, int i2, int i3, int i4) {
        this.password = cArr;
        this.salt = Arrays.clone(bArr);
        this.costParameter = i;
        this.blockSize = i2;
        this.parallelizationParameter = i3;
        this.keySize = i4;
    }

    public char[] getPassword() {
        return this.password;
    }

    public byte[] getSalt() {
        return Arrays.clone(this.salt);
    }

    public int getCostParameter() {
        return this.costParameter;
    }

    public int getBlockSize() {
        return this.blockSize;
    }

    public int getParallelizationParameter() {
        return this.parallelizationParameter;
    }

    public int getKeyLength() {
        return this.keySize;
    }
}
