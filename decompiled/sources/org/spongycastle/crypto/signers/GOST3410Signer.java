package org.spongycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DSA;
import org.spongycastle.crypto.params.GOST3410KeyParameters;
import org.spongycastle.crypto.params.GOST3410Parameters;
import org.spongycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.spongycastle.crypto.params.GOST3410PublicKeyParameters;
import org.spongycastle.crypto.params.ParametersWithRandom;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/signers/GOST3410Signer.class */
public class GOST3410Signer implements DSA {
    GOST3410KeyParameters key;
    SecureRandom random;

    @Override // org.spongycastle.crypto.DSA
    public void init(boolean forSigning, CipherParameters param) {
        if (forSigning) {
            if (param instanceof ParametersWithRandom) {
                ParametersWithRandom rParam = (ParametersWithRandom) param;
                this.random = rParam.getRandom();
                this.key = (GOST3410PrivateKeyParameters) rParam.getParameters();
                return;
            } else {
                this.random = new SecureRandom();
                this.key = (GOST3410PrivateKeyParameters) param;
                return;
            }
        }
        this.key = (GOST3410PublicKeyParameters) param;
    }

    @Override // org.spongycastle.crypto.DSA
    public BigInteger[] generateSignature(byte[] message) {
        BigInteger k;
        byte[] mRev = new byte[message.length];
        for (int i = 0; i != mRev.length; i++) {
            mRev[i] = message[(mRev.length - 1) - i];
        }
        BigInteger m = new BigInteger(1, mRev);
        GOST3410Parameters params = this.key.getParameters();
        do {
            k = new BigInteger(params.getQ().bitLength(), this.random);
        } while (k.compareTo(params.getQ()) >= 0);
        BigInteger r = params.getA().modPow(k, params.getP()).mod(params.getQ());
        BigInteger s = k.multiply(m).add(((GOST3410PrivateKeyParameters) this.key).getX().multiply(r)).mod(params.getQ());
        BigInteger[] res = {r, s};
        return res;
    }

    @Override // org.spongycastle.crypto.DSA
    public boolean verifySignature(byte[] message, BigInteger r, BigInteger s) {
        byte[] mRev = new byte[message.length];
        for (int i = 0; i != mRev.length; i++) {
            mRev[i] = message[(mRev.length - 1) - i];
        }
        BigInteger m = new BigInteger(1, mRev);
        GOST3410Parameters params = this.key.getParameters();
        BigInteger zero = BigInteger.valueOf(0L);
        if (zero.compareTo(r) >= 0 || params.getQ().compareTo(r) <= 0 || zero.compareTo(s) >= 0 || params.getQ().compareTo(s) <= 0) {
            return false;
        }
        BigInteger v = m.modPow(params.getQ().subtract(new BigInteger("2")), params.getQ());
        BigInteger z1 = s.multiply(v).mod(params.getQ());
        BigInteger z2 = params.getQ().subtract(r).multiply(v).mod(params.getQ());
        BigInteger u = params.getA().modPow(z1, params.getP()).multiply(((GOST3410PublicKeyParameters) this.key).getY().modPow(z2, params.getP())).mod(params.getP()).mod(params.getQ());
        return u.equals(r);
    }
}
