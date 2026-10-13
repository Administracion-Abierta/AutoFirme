package org.bouncycastle.pqc.crypto.ntruprime;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntruprime/SNTRUPrimeKEMGenerator.class */
public class SNTRUPrimeKEMGenerator implements EncapsulatedSecretGenerator {
    private final SecureRandom random;

    public SNTRUPrimeKEMGenerator(SecureRandom secureRandom) {
        this.random = secureRandom;
    }

    @Override // org.bouncycastle.crypto.EncapsulatedSecretGenerator
    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter asymmetricKeyParameter) {
        SNTRUPrimePublicKeyParameters sNTRUPrimePublicKeyParameters = (SNTRUPrimePublicKeyParameters) asymmetricKeyParameter;
        SNTRUPrimeParameters parameters = sNTRUPrimePublicKeyParameters.getParameters();
        int p = parameters.getP();
        int q = parameters.getQ();
        int w = parameters.getW();
        int roundedPolynomialBytes = parameters.getRoundedPolynomialBytes();
        byte[] hashWithPrefix = Utils.getHashWithPrefix(new byte[]{4}, sNTRUPrimePublicKeyParameters.getEncoded());
        byte[] bArr = new byte[p];
        Utils.getRandomShortPolynomial(this.random, bArr, p, w);
        byte[] bArr2 = new byte[(p + 3) / 4];
        Utils.getEncodedSmallPolynomial(bArr2, bArr, p);
        short[] sArr = new short[p];
        Utils.getDecodedPolynomial(sArr, sNTRUPrimePublicKeyParameters.getEncH(), p, q);
        short[] sArr2 = new short[p];
        Utils.multiplicationInRQ(sArr2, sArr, bArr, p, q);
        short[] sArr3 = new short[p];
        Utils.roundPolynomial(sArr3, sArr2);
        byte[] bArr3 = new byte[roundedPolynomialBytes];
        Utils.getRoundedEncodedPolynomial(bArr3, sArr3, p, q);
        byte[] hashWithPrefix2 = Utils.getHashWithPrefix(new byte[]{3}, bArr2);
        byte[] bArr4 = new byte[(hashWithPrefix2.length / 2) + (hashWithPrefix.length / 2)];
        System.arraycopy(hashWithPrefix2, 0, bArr4, 0, hashWithPrefix2.length / 2);
        System.arraycopy(hashWithPrefix, 0, bArr4, hashWithPrefix2.length / 2, hashWithPrefix.length / 2);
        byte[] hashWithPrefix3 = Utils.getHashWithPrefix(new byte[]{2}, bArr4);
        byte[] bArr5 = new byte[bArr3.length + (hashWithPrefix3.length / 2)];
        System.arraycopy(bArr3, 0, bArr5, 0, bArr3.length);
        System.arraycopy(hashWithPrefix3, 0, bArr5, bArr3.length, hashWithPrefix3.length / 2);
        byte[] hashWithPrefix4 = Utils.getHashWithPrefix(new byte[]{3}, bArr2);
        byte[] bArr6 = new byte[(hashWithPrefix4.length / 2) + bArr5.length];
        System.arraycopy(hashWithPrefix4, 0, bArr6, 0, hashWithPrefix4.length / 2);
        System.arraycopy(bArr5, 0, bArr6, hashWithPrefix4.length / 2, bArr5.length);
        return new SecretWithEncapsulationImpl(Arrays.copyOfRange(Utils.getHashWithPrefix(new byte[]{1}, bArr6), 0, parameters.getSessionKeySize() / 8), bArr5);
    }
}
