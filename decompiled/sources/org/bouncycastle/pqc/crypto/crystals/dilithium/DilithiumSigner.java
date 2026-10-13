package org.bouncycastle.pqc.crypto.crystals.dilithium;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/crystals/dilithium/DilithiumSigner.class */
public class DilithiumSigner implements MessageSigner {
    private DilithiumPrivateKeyParameters privKey;
    private DilithiumPublicKeyParameters pubKey;
    private SecureRandom random;

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public void init(boolean z, CipherParameters cipherParameters) {
        if (!z) {
            this.pubKey = (DilithiumPublicKeyParameters) cipherParameters;
        } else if (cipherParameters instanceof ParametersWithRandom) {
            this.privKey = (DilithiumPrivateKeyParameters) ((ParametersWithRandom) cipherParameters).getParameters();
            this.random = ((ParametersWithRandom) cipherParameters).getRandom();
        } else {
            this.privKey = (DilithiumPrivateKeyParameters) cipherParameters;
            this.random = null;
        }
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public byte[] generateSignature(byte[] bArr) {
        return this.privKey.getParameters().getEngine(this.random).sign(bArr, bArr.length, this.privKey.rho, this.privKey.k, this.privKey.tr, this.privKey.t0, this.privKey.s1, this.privKey.s2);
    }

    public byte[] internalGenerateSignature(byte[] bArr, byte[] bArr2) {
        return this.privKey.getParameters().getEngine(this.random).signSignatureInternal(bArr, bArr.length, this.privKey.rho, this.privKey.k, this.privKey.tr, this.privKey.t0, this.privKey.s1, this.privKey.s2, bArr2);
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public boolean verifySignature(byte[] bArr, byte[] bArr2) {
        return this.pubKey.getParameters().getEngine(this.random).signOpen(bArr, bArr2, bArr2.length, this.pubKey.rho, this.pubKey.t1);
    }
}
