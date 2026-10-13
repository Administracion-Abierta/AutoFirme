package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.DHUPrivateParameters;
import org.bouncycastle.crypto.params.DHUPublicParameters;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/agreement/DHUnifiedAgreement.class */
public class DHUnifiedAgreement {
    private DHUPrivateParameters privParams;

    public void init(CipherParameters cipherParameters) {
        this.privParams = (DHUPrivateParameters) cipherParameters;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("DHU", this.privParams.getStaticPrivateKey()));
    }

    public int getFieldSize() {
        return (this.privParams.getStaticPrivateKey().getParameters().getP().bitLength() + 7) / 8;
    }

    public byte[] calculateAgreement(CipherParameters cipherParameters) {
        DHUPublicParameters dHUPublicParameters = (DHUPublicParameters) cipherParameters;
        DHBasicAgreement dHBasicAgreement = new DHBasicAgreement();
        DHBasicAgreement dHBasicAgreement2 = new DHBasicAgreement();
        dHBasicAgreement.init(this.privParams.getStaticPrivateKey());
        BigInteger bigIntegerCalculateAgreement = dHBasicAgreement.calculateAgreement(dHUPublicParameters.getStaticPublicKey());
        dHBasicAgreement2.init(this.privParams.getEphemeralPrivateKey());
        BigInteger bigIntegerCalculateAgreement2 = dHBasicAgreement2.calculateAgreement(dHUPublicParameters.getEphemeralPublicKey());
        int fieldSize = getFieldSize();
        byte[] bArr = new byte[fieldSize * 2];
        BigIntegers.asUnsignedByteArray(bigIntegerCalculateAgreement2, bArr, 0, fieldSize);
        BigIntegers.asUnsignedByteArray(bigIntegerCalculateAgreement, bArr, fieldSize, fieldSize);
        return bArr;
    }
}
