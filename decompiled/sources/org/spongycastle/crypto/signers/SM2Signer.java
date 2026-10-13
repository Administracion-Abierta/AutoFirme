package org.spongycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DSA;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.SM3Digest;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECKeyParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.params.ParametersWithID;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.math.ec.ECConstants;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.ec.ECMultiplier;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.ec.FixedPointCombMultiplier;
import org.spongycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/signers/SM2Signer.class */
public class SM2Signer implements DSA, ECConstants {
    private final DSAKCalculator kCalculator = new RandomDSAKCalculator();
    private byte[] userID;
    private int curveLength;
    private ECDomainParameters ecParams;
    private ECPoint pubPoint;
    private ECKeyParameters ecKey;
    private SecureRandom random;

    @Override // org.spongycastle.crypto.DSA
    public void init(boolean forSigning, CipherParameters param) {
        CipherParameters baseParam;
        if (param instanceof ParametersWithID) {
            baseParam = ((ParametersWithID) param).getParameters();
            this.userID = ((ParametersWithID) param).getID();
        } else {
            baseParam = param;
            this.userID = new byte[0];
        }
        if (forSigning) {
            if (baseParam instanceof ParametersWithRandom) {
                ParametersWithRandom rParam = (ParametersWithRandom) baseParam;
                this.ecKey = (ECKeyParameters) rParam.getParameters();
                this.ecParams = this.ecKey.getParameters();
                this.kCalculator.init(this.ecParams.getN(), rParam.getRandom());
            } else {
                this.ecKey = (ECKeyParameters) baseParam;
                this.ecParams = this.ecKey.getParameters();
                this.kCalculator.init(this.ecParams.getN(), new SecureRandom());
            }
            this.pubPoint = this.ecParams.getG().multiply(((ECPrivateKeyParameters) this.ecKey).getD()).normalize();
        } else {
            this.ecKey = (ECKeyParameters) baseParam;
            this.ecParams = this.ecKey.getParameters();
            this.pubPoint = ((ECPublicKeyParameters) this.ecKey).getQ();
        }
        this.curveLength = (this.ecParams.getCurve().getFieldSize() + 7) / 8;
    }

    @Override // org.spongycastle.crypto.DSA
    public BigInteger[] generateSignature(byte[] message) {
        SM3Digest digest = new SM3Digest();
        byte[] z = getZ(digest);
        digest.update(z, 0, z.length);
        digest.update(message, 0, message.length);
        byte[] eHash = new byte[digest.getDigestSize()];
        digest.doFinal(eHash, 0);
        BigInteger n = this.ecParams.getN();
        BigInteger e = calculateE(eHash);
        BigInteger d = ((ECPrivateKeyParameters) this.ecKey).getD();
        ECMultiplier basePointMultiplier = createBasePointMultiplier();
        while (true) {
            BigInteger k = this.kCalculator.nextK();
            ECPoint p = basePointMultiplier.multiply(this.ecParams.getG(), k).normalize();
            BigInteger r = e.add(p.getAffineXCoord().toBigInteger()).mod(n);
            if (!r.equals(ZERO) && !r.add(k).equals(n)) {
                BigInteger dPlus1ModN = d.add(ONE).modInverse(n);
                BigInteger s = dPlus1ModN.multiply(k.subtract(r.multiply(d)).mod(n)).mod(n);
                if (!s.equals(ZERO)) {
                    return new BigInteger[]{r, s};
                }
            }
        }
    }

    @Override // org.spongycastle.crypto.DSA
    public boolean verifySignature(byte[] message, BigInteger r, BigInteger s) {
        BigInteger n = this.ecParams.getN();
        if (r.compareTo(ONE) < 0 || r.compareTo(n) >= 0 || s.compareTo(ONE) < 0 || s.compareTo(n) >= 0) {
            return false;
        }
        ECPoint q = ((ECPublicKeyParameters) this.ecKey).getQ();
        SM3Digest digest = new SM3Digest();
        byte[] z = getZ(digest);
        digest.update(z, 0, z.length);
        digest.update(message, 0, message.length);
        byte[] eHash = new byte[digest.getDigestSize()];
        digest.doFinal(eHash, 0);
        BigInteger e = calculateE(eHash);
        BigInteger t = r.add(s).mod(n);
        if (t.equals(ZERO)) {
            return false;
        }
        ECPoint x1y1 = this.ecParams.getG().multiply(s);
        return r.equals(e.add(x1y1.add(q.multiply(t)).normalize().getAffineXCoord().toBigInteger()).mod(n));
    }

    private byte[] getZ(Digest digest) {
        addUserID(digest, this.userID);
        addFieldElement(digest, this.ecParams.getCurve().getA());
        addFieldElement(digest, this.ecParams.getCurve().getB());
        addFieldElement(digest, this.ecParams.getG().getAffineXCoord());
        addFieldElement(digest, this.ecParams.getG().getAffineYCoord());
        addFieldElement(digest, this.pubPoint.getAffineXCoord());
        addFieldElement(digest, this.pubPoint.getAffineYCoord());
        byte[] rv = new byte[digest.getDigestSize()];
        digest.doFinal(rv, 0);
        return rv;
    }

    private void addUserID(Digest digest, byte[] userID) {
        int len = userID.length * 8;
        digest.update((byte) ((len >> 8) & 255));
        digest.update((byte) (len & 255));
        digest.update(userID, 0, userID.length);
    }

    private void addFieldElement(Digest digest, ECFieldElement v) {
        byte[] p = BigIntegers.asUnsignedByteArray(this.curveLength, v.toBigInteger());
        digest.update(p, 0, p.length);
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }

    protected BigInteger calculateE(byte[] message) {
        return new BigInteger(1, message);
    }
}
