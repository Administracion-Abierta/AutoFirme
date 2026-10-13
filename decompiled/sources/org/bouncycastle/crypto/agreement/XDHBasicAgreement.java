package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.RawAgreement;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/agreement/XDHBasicAgreement.class */
public class XDHBasicAgreement implements BasicAgreement {
    private AsymmetricKeyParameter key;
    private RawAgreement agreement;
    private int fieldSize = 0;

    @Override // org.bouncycastle.crypto.BasicAgreement
    public void init(CipherParameters cipherParameters) {
        if (cipherParameters instanceof X25519PrivateKeyParameters) {
            this.fieldSize = 32;
            this.agreement = new X25519Agreement();
        } else {
            if (!(cipherParameters instanceof X448PrivateKeyParameters)) {
                throw new IllegalArgumentException("key is neither X25519 nor X448");
            }
            this.fieldSize = 56;
            this.agreement = new X448Agreement();
        }
        this.key = (AsymmetricKeyParameter) cipherParameters;
        this.agreement.init(cipherParameters);
    }

    @Override // org.bouncycastle.crypto.BasicAgreement
    public int getFieldSize() {
        return this.fieldSize;
    }

    @Override // org.bouncycastle.crypto.BasicAgreement
    public BigInteger calculateAgreement(CipherParameters cipherParameters) {
        byte[] bArr = new byte[this.fieldSize];
        this.agreement.calculateAgreement(cipherParameters, bArr, 0);
        return new BigInteger(1, bArr);
    }
}
