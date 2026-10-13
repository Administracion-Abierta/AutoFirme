package org.spongycastle.crypto.agreement;

import java.math.BigInteger;
import org.spongycastle.crypto.BasicAgreement;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/agreement/ECDHCBasicAgreement.class */
public class ECDHCBasicAgreement implements BasicAgreement {
    ECPrivateKeyParameters key;

    @Override // org.spongycastle.crypto.BasicAgreement
    public void init(CipherParameters key) {
        this.key = (ECPrivateKeyParameters) key;
    }

    @Override // org.spongycastle.crypto.BasicAgreement
    public int getFieldSize() {
        return (this.key.getParameters().getCurve().getFieldSize() + 7) / 8;
    }

    @Override // org.spongycastle.crypto.BasicAgreement
    public BigInteger calculateAgreement(CipherParameters pubKey) {
        ECPublicKeyParameters pub = (ECPublicKeyParameters) pubKey;
        ECDomainParameters params = pub.getParameters();
        if (!params.equals(this.key.getParameters())) {
            throw new IllegalStateException("ECDHC public key has wrong domain parameters");
        }
        BigInteger hd = params.getH().multiply(this.key.getD()).mod(params.getN());
        ECPoint P = pub.getQ().multiply(hd).normalize();
        if (P.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid agreement value for ECDHC");
        }
        return P.getAffineXCoord().toBigInteger();
    }
}
