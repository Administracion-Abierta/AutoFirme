package org.spongycastle.crypto.generators;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.crypto.util.DigestFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/generators/OpenSSLPBEParametersGenerator.class */
public class OpenSSLPBEParametersGenerator extends PBEParametersGenerator {
    private Digest digest = DigestFactory.createMD5();

    public void init(byte[] password, byte[] salt) {
        super.init(password, salt, 1);
    }

    private byte[] generateDerivedKey(int bytesNeeded) {
        byte[] buf = new byte[this.digest.getDigestSize()];
        byte[] key = new byte[bytesNeeded];
        int offset = 0;
        while (true) {
            this.digest.update(this.password, 0, this.password.length);
            this.digest.update(this.salt, 0, this.salt.length);
            this.digest.doFinal(buf, 0);
            int len = bytesNeeded > buf.length ? buf.length : bytesNeeded;
            System.arraycopy(buf, 0, key, offset, len);
            offset += len;
            bytesNeeded -= len;
            if (bytesNeeded != 0) {
                this.digest.reset();
                this.digest.update(buf, 0, buf.length);
            } else {
                return key;
            }
        }
    }

    @Override // org.spongycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedParameters(int keySize) {
        int keySize2 = keySize / 8;
        byte[] dKey = generateDerivedKey(keySize2);
        return new KeyParameter(dKey, 0, keySize2);
    }

    @Override // org.spongycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedParameters(int keySize, int ivSize) {
        int keySize2 = keySize / 8;
        int ivSize2 = ivSize / 8;
        byte[] dKey = generateDerivedKey(keySize2 + ivSize2);
        return new ParametersWithIV(new KeyParameter(dKey, 0, keySize2), dKey, keySize2, ivSize2);
    }

    @Override // org.spongycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedMacParameters(int keySize) {
        return generateDerivedParameters(keySize);
    }
}
