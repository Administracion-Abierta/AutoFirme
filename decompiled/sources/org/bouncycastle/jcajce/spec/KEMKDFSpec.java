package org.bouncycastle.jcajce.spec;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/KEMKDFSpec.class */
public class KEMKDFSpec {
    private final String keyAlgorithmName;
    private final int keySizeInBits;
    private final AlgorithmIdentifier kdfAlgorithm;
    private final byte[] otherInfo;

    protected KEMKDFSpec(AlgorithmIdentifier algorithmIdentifier, byte[] bArr, String str, int i) {
        this.keyAlgorithmName = str;
        this.keySizeInBits = i;
        this.kdfAlgorithm = algorithmIdentifier;
        this.otherInfo = bArr;
    }

    public String getKeyAlgorithmName() {
        return this.keyAlgorithmName;
    }

    public int getKeySize() {
        return this.keySizeInBits;
    }

    public AlgorithmIdentifier getKdfAlgorithm() {
        return this.kdfAlgorithm;
    }

    public byte[] getOtherInfo() {
        return Arrays.clone(this.otherInfo);
    }
}
