package org.spongycastle.crypto.generators;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/generators/PKCS5S1ParametersGenerator.class */
public class PKCS5S1ParametersGenerator extends PBEParametersGenerator {
    private Digest digest;

    public PKCS5S1ParametersGenerator(Digest digest) {
        this.digest = digest;
    }

    private byte[] generateDerivedKey() {
        byte[] digestBytes = new byte[this.digest.getDigestSize()];
        this.digest.update(this.password, 0, this.password.length);
        this.digest.update(this.salt, 0, this.salt.length);
        this.digest.doFinal(digestBytes, 0);
        for (int i = 1; i < this.iterationCount; i++) {
            this.digest.update(digestBytes, 0, digestBytes.length);
            this.digest.doFinal(digestBytes, 0);
        }
        return digestBytes;
    }

    @Override // org.spongycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedParameters(int keySize) {
        int keySize2 = keySize / 8;
        if (keySize2 > this.digest.getDigestSize()) {
            throw new IllegalArgumentException("Can't generate a derived key " + keySize2 + " bytes long.");
        }
        byte[] dKey = generateDerivedKey();
        return new KeyParameter(dKey, 0, keySize2);
    }

    @Override // org.spongycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedParameters(int keySize, int ivSize) {
        int keySize2 = keySize / 8;
        int ivSize2 = ivSize / 8;
        if (keySize2 + ivSize2 > this.digest.getDigestSize()) {
            throw new IllegalArgumentException("Can't generate a derived key " + (keySize2 + ivSize2) + " bytes long.");
        }
        byte[] dKey = generateDerivedKey();
        return new ParametersWithIV(new KeyParameter(dKey, 0, keySize2), dKey, keySize2, ivSize2);
    }

    @Override // org.spongycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedMacParameters(int keySize) {
        return generateDerivedParameters(keySize);
    }
}
