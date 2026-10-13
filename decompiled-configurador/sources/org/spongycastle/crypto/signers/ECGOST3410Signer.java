package org.spongycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DSA;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECKeyParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.math.ec.ECAlgorithms;
import org.spongycastle.math.ec.ECConstants;
import org.spongycastle.math.ec.ECMultiplier;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.ec.FixedPointCombMultiplier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/signers/ECGOST3410Signer.class */
public class ECGOST3410Signer implements DSA {
    ECKeyParameters key;
    SecureRandom random;

    @Override // org.spongycastle.crypto.DSA
    public void init(boolean forSigning, CipherParameters param) {
        if (forSigning) {
            if (param instanceof ParametersWithRandom) {
                ParametersWithRandom rParam = (ParametersWithRandom) param;
                this.random = rParam.getRandom();
                this.key = (ECPrivateKeyParameters) rParam.getParameters();
                return;
            } else {
                this.random = new SecureRandom();
                this.key = (ECPrivateKeyParameters) param;
                return;
            }
        }
        this.key = (ECPublicKeyParameters) param;
    }

    @Override // org.spongycastle.crypto.DSA
    public BigInteger[] generateSignature(byte[] message) {
        byte[] mRev = new byte[message.length];
        for (int i = 0; i != mRev.length; i++) {
            mRev[i] = message[(mRev.length - 1) - i];
        }
        BigInteger e = new BigInteger(1, mRev);
        ECDomainParameters ec = this.key.getParameters();
        BigInteger n = ec.getN();
        BigInteger d = ((ECPrivateKeyParameters) this.key).getD();
        ECMultiplier basePointMultiplier = createBasePointMultiplier();
        while (true) {
            BigInteger k = new BigInteger(n.bitLength(), this.random);
            if (!k.equals(ECConstants.ZERO)) {
                ECPoint p = basePointMultiplier.multiply(ec.getG(), k).normalize();
                BigInteger r = p.getAffineXCoord().toBigInteger().mod(n);
                if (r.equals(ECConstants.ZERO)) {
                    continue;
                } else {
                    BigInteger s = k.multiply(e).add(d.multiply(r)).mod(n);
                    if (!s.equals(ECConstants.ZERO)) {
                        return new BigInteger[]{r, s};
                    }
                }
            }
        }
    }

    @Override // org.spongycastle.crypto.DSA
    public boolean verifySignature(byte[] message, BigInteger r, BigInteger s) {
        byte[] mRev = new byte[message.length];
        for (int i = 0; i != mRev.length; i++) {
            mRev[i] = message[(mRev.length - 1) - i];
        }
        BigInteger e = new BigInteger(1, mRev);
        BigInteger n = this.key.getParameters().getN();
        if (r.compareTo(ECConstants.ONE) < 0 || r.compareTo(n) >= 0 || s.compareTo(ECConstants.ONE) < 0 || s.compareTo(n) >= 0) {
            return false;
        }
        BigInteger v = e.modInverse(n);
        BigInteger z1 = s.multiply(v).mod(n);
        BigInteger z2 = n.subtract(r).multiply(v).mod(n);
        ECPoint G = this.key.getParameters().getG();
        ECPoint Q = ((ECPublicKeyParameters) this.key).getQ();
        ECPoint point = ECAlgorithms.sumOfTwoMultiplies(G, z1, Q, z2).normalize();
        if (point.isInfinity()) {
            return false;
        }
        BigInteger R = point.getAffineXCoord().toBigInteger().mod(n);
        return R.equals(r);
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
}
