package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/signers/SM2Signer.class */
public class SM2Signer implements Signer, ECConstants {
    private final DSAKCalculator kCalculator;
    private final Digest digest;
    private final DSAEncoding encoding;
    private int state;
    private ECDomainParameters ecParams;
    private ECPoint pubPoint;
    private ECKeyParameters ecKey;
    private byte[] z;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/signers/SM2Signer$State.class */
    private static final class State {
        static final int UNINITIALIZED = 0;
        static final int INIT = 1;
        static final int DATA = 2;

        private State() {
        }
    }

    public SM2Signer() {
        this(StandardDSAEncoding.INSTANCE, new SM3Digest());
    }

    public SM2Signer(Digest digest) {
        this(StandardDSAEncoding.INSTANCE, digest);
    }

    public SM2Signer(DSAEncoding dSAEncoding) {
        this.kCalculator = new RandomDSAKCalculator();
        this.state = 0;
        this.encoding = dSAEncoding;
        this.digest = new SM3Digest();
    }

    public SM2Signer(DSAEncoding dSAEncoding, Digest digest) {
        this.kCalculator = new RandomDSAKCalculator();
        this.state = 0;
        this.encoding = dSAEncoding;
        this.digest = digest;
    }

    @Override // org.bouncycastle.crypto.Signer
    public void init(boolean z, CipherParameters cipherParameters) {
        CipherParameters parameters;
        byte[] bArrDecodeStrict;
        if (cipherParameters instanceof ParametersWithID) {
            parameters = ((ParametersWithID) cipherParameters).getParameters();
            bArrDecodeStrict = ((ParametersWithID) cipherParameters).getID();
            if (bArrDecodeStrict.length >= 8192) {
                throw new IllegalArgumentException("SM2 user ID must be less than 2^13 bits long");
            }
        } else {
            parameters = cipherParameters;
            bArrDecodeStrict = Hex.decodeStrict("31323334353637383132333435363738");
        }
        if (z) {
            if (parameters instanceof ParametersWithRandom) {
                ParametersWithRandom parametersWithRandom = (ParametersWithRandom) parameters;
                this.ecKey = (ECKeyParameters) parametersWithRandom.getParameters();
                this.ecParams = this.ecKey.getParameters();
                this.kCalculator.init(this.ecParams.getN(), parametersWithRandom.getRandom());
            } else {
                this.ecKey = (ECKeyParameters) parameters;
                this.ecParams = this.ecKey.getParameters();
                this.kCalculator.init(this.ecParams.getN(), CryptoServicesRegistrar.getSecureRandom());
            }
            BigInteger d = ((ECPrivateKeyParameters) this.ecKey).getD();
            BigInteger bigIntegerSubtract = this.ecParams.getN().subtract(BigIntegers.ONE);
            if (d.compareTo(ONE) < 0 || d.compareTo(bigIntegerSubtract) >= 0) {
                throw new IllegalArgumentException("SM2 private key out of range");
            }
            this.pubPoint = createBasePointMultiplier().multiply(this.ecParams.getG(), d).normalize();
        } else {
            this.ecKey = (ECKeyParameters) parameters;
            this.ecParams = this.ecKey.getParameters();
            this.pubPoint = ((ECPublicKeyParameters) this.ecKey).getQ();
        }
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECNR", this.ecKey, z));
        this.digest.reset();
        this.z = getZ(bArrDecodeStrict);
        this.state = 1;
    }

    @Override // org.bouncycastle.crypto.Signer
    public void update(byte b) {
        checkData();
        this.digest.update(b);
    }

    @Override // org.bouncycastle.crypto.Signer
    public void update(byte[] bArr, int i, int i2) {
        checkData();
        this.digest.update(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Signer
    public boolean verifySignature(byte[] bArr) {
        checkData();
        try {
            BigInteger[] bigIntegerArrDecode = this.encoding.decode(this.ecParams.getN(), bArr);
            return verifySignature(bigIntegerArrDecode[0], bigIntegerArrDecode[1]);
        } catch (Exception e) {
            return false;
        } finally {
            reset();
        }
    }

    @Override // org.bouncycastle.crypto.Signer
    public void reset() {
        switch (this.state) {
            case 1:
                return;
            case 2:
                this.digest.reset();
                this.state = 1;
                return;
            default:
                throw new IllegalStateException("SM2Signer needs to be initialized");
        }
    }

    @Override // org.bouncycastle.crypto.Signer
    public byte[] generateSignature() throws CryptoException {
        checkData();
        byte[] bArrDigestDoFinal = digestDoFinal();
        BigInteger n = this.ecParams.getN();
        BigInteger bigIntegerCalculateE = calculateE(n, bArrDigestDoFinal);
        BigInteger d = ((ECPrivateKeyParameters) this.ecKey).getD();
        ECMultiplier eCMultiplierCreateBasePointMultiplier = createBasePointMultiplier();
        while (true) {
            BigInteger bigIntegerNextK = this.kCalculator.nextK();
            BigInteger bigIntegerMod = bigIntegerCalculateE.add(eCMultiplierCreateBasePointMultiplier.multiply(this.ecParams.getG(), bigIntegerNextK).normalize().getAffineXCoord().toBigInteger()).mod(n);
            if (!bigIntegerMod.equals(ZERO) && !bigIntegerMod.add(bigIntegerNextK).equals(n)) {
                BigInteger bigIntegerMod2 = BigIntegers.modOddInverse(n, d.add(ONE)).multiply(bigIntegerNextK.subtract(bigIntegerMod.multiply(d)).mod(n)).mod(n);
                if (!bigIntegerMod2.equals(ZERO)) {
                    try {
                        try {
                            byte[] bArrEncode = this.encoding.encode(this.ecParams.getN(), bigIntegerMod, bigIntegerMod2);
                            reset();
                            return bArrEncode;
                        } catch (Exception e) {
                            throw new CryptoException("unable to encode signature: " + e.getMessage(), e);
                        }
                    } catch (Throwable th) {
                        reset();
                        throw th;
                    }
                }
            }
        }
    }

    private boolean verifySignature(BigInteger bigInteger, BigInteger bigInteger2) {
        BigInteger n = this.ecParams.getN();
        if (bigInteger.compareTo(ONE) < 0 || bigInteger.compareTo(n) >= 0 || bigInteger2.compareTo(ONE) < 0 || bigInteger2.compareTo(n) >= 0) {
            return false;
        }
        BigInteger bigIntegerCalculateE = calculateE(n, digestDoFinal());
        BigInteger bigIntegerMod = bigInteger.add(bigInteger2).mod(n);
        if (bigIntegerMod.equals(ZERO)) {
            return false;
        }
        ECPoint eCPointNormalize = ECAlgorithms.sumOfTwoMultiplies(this.ecParams.getG(), bigInteger2, ((ECPublicKeyParameters) this.ecKey).getQ(), bigIntegerMod).normalize();
        if (eCPointNormalize.isInfinity()) {
            return false;
        }
        return bigIntegerCalculateE.add(eCPointNormalize.getAffineXCoord().toBigInteger()).mod(n).equals(bigInteger);
    }

    private void checkData() {
        switch (this.state) {
            case 1:
                this.digest.update(this.z, 0, this.z.length);
                this.state = 2;
                return;
            case 2:
                return;
            default:
                throw new IllegalStateException("SM2Signer needs to be initialized");
        }
    }

    private byte[] digestDoFinal() {
        byte[] bArr = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(bArr, 0);
        return bArr;
    }

    private byte[] getZ(byte[] bArr) {
        addUserID(this.digest, bArr);
        addFieldElement(this.digest, this.ecParams.getCurve().getA());
        addFieldElement(this.digest, this.ecParams.getCurve().getB());
        addFieldElement(this.digest, this.ecParams.getG().getAffineXCoord());
        addFieldElement(this.digest, this.ecParams.getG().getAffineYCoord());
        addFieldElement(this.digest, this.pubPoint.getAffineXCoord());
        addFieldElement(this.digest, this.pubPoint.getAffineYCoord());
        return digestDoFinal();
    }

    private void addUserID(Digest digest, byte[] bArr) {
        int length = bArr.length * 8;
        digest.update((byte) (length >>> 8));
        digest.update((byte) length);
        digest.update(bArr, 0, bArr.length);
    }

    private void addFieldElement(Digest digest, ECFieldElement eCFieldElement) {
        byte[] encoded = eCFieldElement.getEncoded();
        digest.update(encoded, 0, encoded.length);
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }

    protected BigInteger calculateE(BigInteger bigInteger, byte[] bArr) {
        return new BigInteger(1, bArr);
    }
}
