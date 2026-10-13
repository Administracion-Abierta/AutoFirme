package org.spongycastle.crypto.ec;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/ec/ECElGamalDecryptor.class */
public class ECElGamalDecryptor implements ECDecryptor {
    private ECPrivateKeyParameters key;

    @Override // org.spongycastle.crypto.ec.ECDecryptor
    public void init(CipherParameters param) {
        if (!(param instanceof ECPrivateKeyParameters)) {
            throw new IllegalArgumentException("ECPrivateKeyParameters are required for decryption.");
        }
        this.key = (ECPrivateKeyParameters) param;
    }

    @Override // org.spongycastle.crypto.ec.ECDecryptor
    public ECPoint decrypt(ECPair pair) {
        if (this.key == null) {
            throw new IllegalStateException("ECElGamalDecryptor not initialised");
        }
        ECPoint tmp = pair.getX().multiply(this.key.getD());
        return pair.getY().subtract(tmp).normalize();
    }
}
