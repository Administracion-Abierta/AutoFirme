package org.spongycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DSA;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.generators.ECKeyPairGenerator;
import org.spongycastle.crypto.params.ECKeyGenerationParameters;
import org.spongycastle.crypto.params.ECKeyParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.math.ec.ECAlgorithms;
import org.spongycastle.math.ec.ECConstants;
import org.spongycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/signers/ECNRSigner.class */
public class ECNRSigner implements DSA {
    private boolean forSigning;
    private ECKeyParameters key;
    private SecureRandom random;

    @Override // org.spongycastle.crypto.DSA
    public void init(boolean forSigning, CipherParameters param) {
        this.forSigning = forSigning;
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
    public BigInteger[] generateSignature(byte[] digest) {
        AsymmetricCipherKeyPair tempPair;
        BigInteger r;
        if (!this.forSigning) {
            throw new IllegalStateException("not initialised for signing");
        }
        BigInteger n = ((ECPrivateKeyParameters) this.key).getParameters().getN();
        int nBitLength = n.bitLength();
        BigInteger e = new BigInteger(1, digest);
        int eBitLength = e.bitLength();
        ECPrivateKeyParameters privKey = (ECPrivateKeyParameters) this.key;
        if (eBitLength > nBitLength) {
            throw new DataLengthException("input too large for ECNR key.");
        }
        do {
            ECKeyPairGenerator keyGen = new ECKeyPairGenerator();
            keyGen.init(new ECKeyGenerationParameters(privKey.getParameters(), this.random));
            tempPair = keyGen.generateKeyPair();
            ECPublicKeyParameters V = (ECPublicKeyParameters) tempPair.getPublic();
            BigInteger Vx = V.getQ().getAffineXCoord().toBigInteger();
            r = Vx.add(e).mod(n);
        } while (r.equals(ECConstants.ZERO));
        BigInteger x = privKey.getD();
        BigInteger u = ((ECPrivateKeyParameters) tempPair.getPrivate()).getD();
        BigInteger s = u.subtract(r.multiply(x)).mod(n);
        BigInteger[] res = {r, s};
        return res;
    }

    @Override // org.spongycastle.crypto.DSA
    public boolean verifySignature(byte[] digest, BigInteger r, BigInteger s) {
        if (this.forSigning) {
            throw new IllegalStateException("not initialised for verifying");
        }
        ECPublicKeyParameters pubKey = (ECPublicKeyParameters) this.key;
        BigInteger n = pubKey.getParameters().getN();
        int nBitLength = n.bitLength();
        BigInteger e = new BigInteger(1, digest);
        int eBitLength = e.bitLength();
        if (eBitLength > nBitLength) {
            throw new DataLengthException("input too large for ECNR key.");
        }
        if (r.compareTo(ECConstants.ONE) < 0 || r.compareTo(n) >= 0 || s.compareTo(ECConstants.ZERO) < 0 || s.compareTo(n) >= 0) {
            return false;
        }
        ECPoint G = pubKey.getParameters().getG();
        ECPoint W = pubKey.getQ();
        ECPoint P = ECAlgorithms.sumOfTwoMultiplies(G, s, W, r).normalize();
        if (P.isInfinity()) {
            return false;
        }
        BigInteger x = P.getAffineXCoord().toBigInteger();
        BigInteger t = r.subtract(x).mod(n);
        return t.equals(e);
    }
}
