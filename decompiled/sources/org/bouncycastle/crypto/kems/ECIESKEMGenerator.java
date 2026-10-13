package org.bouncycastle.crypto.kems;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/kems/ECIESKEMGenerator.class */
public class ECIESKEMGenerator implements EncapsulatedSecretGenerator {
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private DerivationFunction kdf;
    private SecureRandom rnd;
    private final int keySize;
    private boolean CofactorMode;
    private boolean OldCofactorMode;
    private boolean SingleHashMode;

    public ECIESKEMGenerator(int i, DerivationFunction derivationFunction, SecureRandom secureRandom) {
        this.keySize = i;
        this.kdf = derivationFunction;
        this.rnd = secureRandom;
        this.CofactorMode = false;
        this.OldCofactorMode = false;
        this.SingleHashMode = false;
    }

    public ECIESKEMGenerator(int i, DerivationFunction derivationFunction, SecureRandom secureRandom, boolean z, boolean z2, boolean z3) {
        this.kdf = derivationFunction;
        this.rnd = secureRandom;
        this.keySize = i;
        this.CofactorMode = z;
        if (z) {
            this.OldCofactorMode = false;
        } else {
            this.OldCofactorMode = z2;
        }
        this.SingleHashMode = z3;
    }

    private ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }

    @Override // org.bouncycastle.crypto.EncapsulatedSecretGenerator
    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter asymmetricKeyParameter) {
        if (!(asymmetricKeyParameter instanceof ECPublicKeyParameters)) {
            throw new IllegalArgumentException("EC public key required");
        }
        ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters) asymmetricKeyParameter;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("ECIESKem", ConstraintUtils.bitsOfSecurityFor(eCPublicKeyParameters.getParameters().getCurve()), asymmetricKeyParameter, CryptoServicePurpose.ENCRYPTION));
        ECDomainParameters parameters = eCPublicKeyParameters.getParameters();
        ECCurve curve = parameters.getCurve();
        BigInteger n = parameters.getN();
        BigInteger h = parameters.getH();
        BigInteger bigIntegerCreateRandomInRange = BigIntegers.createRandomInRange(ONE, n, this.rnd);
        ECPoint[] eCPointArr = {createBasePointMultiplier().multiply(parameters.getG(), bigIntegerCreateRandomInRange), eCPublicKeyParameters.getQ().multiply(this.OldCofactorMode ? bigIntegerCreateRandomInRange.multiply(h).mod(n) : bigIntegerCreateRandomInRange)};
        curve.normalizeAll(eCPointArr);
        ECPoint eCPoint = eCPointArr[0];
        ECPoint eCPoint2 = eCPointArr[1];
        byte[] encoded = eCPoint.getEncoded(false);
        byte[] bArr = new byte[encoded.length];
        System.arraycopy(encoded, 0, bArr, 0, encoded.length);
        return new SecretWithEncapsulationImpl(deriveKey(this.SingleHashMode, this.kdf, this.keySize, encoded, eCPoint2.getAffineXCoord().getEncoded()), bArr);
    }

    static byte[] deriveKey(boolean z, DerivationFunction derivationFunction, int i, byte[] bArr, byte[] bArr2) {
        byte[] bArrConcatenate = bArr2;
        if (!z) {
            bArrConcatenate = Arrays.concatenate(bArr, bArr2);
            Arrays.fill(bArr2, (byte) 0);
        }
        try {
            derivationFunction.init(new KDFParameters(bArrConcatenate, null));
            byte[] bArr3 = new byte[i];
            derivationFunction.generateBytes(bArr3, 0, bArr3.length);
            return bArr3;
        } finally {
            Arrays.fill(bArrConcatenate, (byte) 0);
        }
    }
}
