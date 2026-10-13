package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.MQVPrivateParameters;
import org.bouncycastle.crypto.params.MQVPublicParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/agreement/ECMQVBasicAgreement.class */
public class ECMQVBasicAgreement implements BasicAgreement {
    MQVPrivateParameters privParams;

    @Override // org.bouncycastle.crypto.BasicAgreement
    public void init(CipherParameters cipherParameters) {
        this.privParams = (MQVPrivateParameters) cipherParameters;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECMQV", this.privParams.getStaticPrivateKey()));
    }

    @Override // org.bouncycastle.crypto.BasicAgreement
    public int getFieldSize() {
        return this.privParams.getStaticPrivateKey().getParameters().getCurve().getFieldElementEncodingLength();
    }

    @Override // org.bouncycastle.crypto.BasicAgreement
    public BigInteger calculateAgreement(CipherParameters cipherParameters) {
        if (Properties.isOverrideSet("org.bouncycastle.ec.disable_mqv")) {
            throw new IllegalStateException("ECMQV explicitly disabled");
        }
        MQVPublicParameters mQVPublicParameters = (MQVPublicParameters) cipherParameters;
        ECPrivateKeyParameters staticPrivateKey = this.privParams.getStaticPrivateKey();
        ECDomainParameters parameters = staticPrivateKey.getParameters();
        if (!parameters.equals(mQVPublicParameters.getStaticPublicKey().getParameters())) {
            throw new IllegalStateException("ECMQV public key components have wrong domain parameters");
        }
        ECPoint eCPointNormalize = calculateMqvAgreement(parameters, staticPrivateKey, this.privParams.getEphemeralPrivateKey(), this.privParams.getEphemeralPublicKey(), mQVPublicParameters.getStaticPublicKey(), mQVPublicParameters.getEphemeralPublicKey()).normalize();
        if (eCPointNormalize.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid agreement value for MQV");
        }
        return eCPointNormalize.getAffineXCoord().toBigInteger();
    }

    private ECPoint calculateMqvAgreement(ECDomainParameters eCDomainParameters, ECPrivateKeyParameters eCPrivateKeyParameters, ECPrivateKeyParameters eCPrivateKeyParameters2, ECPublicKeyParameters eCPublicKeyParameters, ECPublicKeyParameters eCPublicKeyParameters2, ECPublicKeyParameters eCPublicKeyParameters3) {
        BigInteger n = eCDomainParameters.getN();
        int iBitLength = (n.bitLength() + 1) / 2;
        BigInteger bigIntegerShiftLeft = ECConstants.ONE.shiftLeft(iBitLength);
        ECCurve curve = eCDomainParameters.getCurve();
        ECPoint eCPointCleanPoint = ECAlgorithms.cleanPoint(curve, eCPublicKeyParameters.getQ());
        ECPoint eCPointCleanPoint2 = ECAlgorithms.cleanPoint(curve, eCPublicKeyParameters2.getQ());
        ECPoint eCPointCleanPoint3 = ECAlgorithms.cleanPoint(curve, eCPublicKeyParameters3.getQ());
        BigInteger bigIntegerMod = eCPrivateKeyParameters.getD().multiply(eCPointCleanPoint.getAffineXCoord().toBigInteger().mod(bigIntegerShiftLeft).setBit(iBitLength)).add(eCPrivateKeyParameters2.getD()).mod(n);
        BigInteger bit = eCPointCleanPoint3.getAffineXCoord().toBigInteger().mod(bigIntegerShiftLeft).setBit(iBitLength);
        BigInteger bigIntegerMod2 = eCDomainParameters.getH().multiply(bigIntegerMod).mod(n);
        return ECAlgorithms.sumOfTwoMultiplies(eCPointCleanPoint2, bit.multiply(bigIntegerMod2).mod(n), eCPointCleanPoint3, bigIntegerMod2);
    }
}
