package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.pqc.crypto.KEMParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mlkem/MLKEMParameters.class */
public class MLKEMParameters implements KEMParameters {
    public static final MLKEMParameters ml_kem_512 = new MLKEMParameters("ML-KEM-512", 2, 256);
    public static final MLKEMParameters ml_kem_768 = new MLKEMParameters("ML-KEM-768", 3, 256);
    public static final MLKEMParameters ml_kem_1024 = new MLKEMParameters("ML-KEM-1024", 4, 256);
    private final String name;
    private final int k;
    private final int sessionKeySize;

    private MLKEMParameters(String str, int i, int i2) {
        this.name = str;
        this.k = i;
        this.sessionKeySize = i2;
    }

    public String getName() {
        return this.name;
    }

    public MLKEMEngine getEngine() {
        return new MLKEMEngine(this.k);
    }

    public int getSessionKeySize() {
        return this.sessionKeySize;
    }
}
