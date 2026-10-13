package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/agreement/ECVKOAgreement.class */
public class ECVKOAgreement {
    private final Digest digest;
    private ECPrivateKeyParameters key;
    private BigInteger ukm;

    public ECVKOAgreement(Digest digest) {
        this.digest = digest;
    }

    public void init(CipherParameters cipherParameters) {
        ParametersWithUKM parametersWithUKM = (ParametersWithUKM) cipherParameters;
        this.key = (ECPrivateKeyParameters) parametersWithUKM.getParameters();
        this.ukm = new BigInteger(1, Arrays.reverse(parametersWithUKM.getUKM()));
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECVKO", this.key));
    }

    public int getAgreementSize() {
        return this.digest.getDigestSize();
    }

    public int getFieldSize() {
        return this.key.getParameters().getCurve().getFieldElementEncodingLength();
    }

    public byte[] calculateAgreement(CipherParameters cipherParameters) {
        ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters) cipherParameters;
        ECDomainParameters parameters = this.key.getParameters();
        if (!parameters.equals(eCPublicKeyParameters.getParameters())) {
            throw new IllegalStateException("ECVKO public key has wrong domain parameters");
        }
        BigInteger bigIntegerMod = parameters.getH().multiply(this.ukm).multiply(this.key.getD()).mod(parameters.getN());
        ECPoint eCPointCleanPoint = ECAlgorithms.cleanPoint(parameters.getCurve(), eCPublicKeyParameters.getQ());
        if (eCPointCleanPoint.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid public key for ECVKO");
        }
        ECPoint eCPointNormalize = eCPointCleanPoint.multiply(bigIntegerMod).normalize();
        if (eCPointNormalize.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid agreement value for ECVKO");
        }
        byte[] encoded = eCPointNormalize.getEncoded(false);
        int length = encoded.length;
        int i = length / 2;
        Arrays.reverseInPlace(encoded, length - (i * 2), i);
        Arrays.reverseInPlace(encoded, length - i, i);
        byte[] bArr = new byte[this.digest.getDigestSize()];
        this.digest.update(encoded, length - (i * 2), i * 2);
        this.digest.doFinal(bArr, 0);
        return bArr;
    }
}
