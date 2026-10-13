package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntruprime/SNTRUPrimeKEMExtractor.class */
public class SNTRUPrimeKEMExtractor implements EncapsulatedSecretExtractor {
    private final SNTRUPrimePrivateKeyParameters privateKey;

    public SNTRUPrimeKEMExtractor(SNTRUPrimePrivateKeyParameters sNTRUPrimePrivateKeyParameters) {
        this.privateKey = sNTRUPrimePrivateKeyParameters;
    }

    @Override // org.bouncycastle.crypto.EncapsulatedSecretExtractor
    public byte[] extractSecret(byte[] bArr) {
        SNTRUPrimeParameters parameters = this.privateKey.getParameters();
        int p = parameters.getP();
        int q = parameters.getQ();
        int w = parameters.getW();
        int roundedPolynomialBytes = parameters.getRoundedPolynomialBytes();
        byte[] bArr2 = new byte[p];
        Utils.getDecodedSmallPolynomial(bArr2, this.privateKey.getF(), p);
        byte[] bArr3 = new byte[p];
        Utils.getDecodedSmallPolynomial(bArr3, this.privateKey.getGinv(), p);
        short[] sArr = new short[p];
        Utils.getRoundedDecodedPolynomial(sArr, bArr, p, q);
        short[] sArr2 = new short[p];
        Utils.multiplicationInRQ(sArr2, sArr, bArr2, p, q);
        short[] sArr3 = new short[p];
        Utils.scalarMultiplicationInRQ(sArr3, sArr2, 3, q);
        byte[] bArr4 = new byte[p];
        Utils.transformRQToR3(bArr4, sArr3);
        byte[] bArr5 = new byte[p];
        Utils.multiplicationInR3(bArr5, bArr4, bArr3, p);
        byte[] bArr6 = new byte[p];
        Utils.checkForSmallPolynomial(bArr6, bArr5, p, w);
        byte[] bArr7 = new byte[(p + 3) / 4];
        Utils.getEncodedSmallPolynomial(bArr7, bArr6, p);
        short[] sArr4 = new short[p];
        Utils.getDecodedPolynomial(sArr4, this.privateKey.getPk(), p, q);
        short[] sArr5 = new short[p];
        Utils.multiplicationInRQ(sArr5, sArr4, bArr6, p, q);
        short[] sArr6 = new short[p];
        Utils.roundPolynomial(sArr6, sArr5);
        byte[] bArr8 = new byte[roundedPolynomialBytes];
        Utils.getRoundedEncodedPolynomial(bArr8, sArr6, p, q);
        byte[] hashWithPrefix = Utils.getHashWithPrefix(new byte[]{3}, bArr7);
        byte[] bArr9 = new byte[(hashWithPrefix.length / 2) + this.privateKey.getHash().length];
        System.arraycopy(hashWithPrefix, 0, bArr9, 0, hashWithPrefix.length / 2);
        System.arraycopy(this.privateKey.getHash(), 0, bArr9, hashWithPrefix.length / 2, this.privateKey.getHash().length);
        byte[] hashWithPrefix2 = Utils.getHashWithPrefix(new byte[]{2}, bArr9);
        byte[] bArr10 = new byte[bArr8.length + (hashWithPrefix2.length / 2)];
        System.arraycopy(bArr8, 0, bArr10, 0, bArr8.length);
        System.arraycopy(hashWithPrefix2, 0, bArr10, bArr8.length, hashWithPrefix2.length / 2);
        int i = Arrays.areEqual(bArr, bArr10) ? 0 : -1;
        Utils.updateDiffMask(bArr7, this.privateKey.getRho(), i);
        byte[] hashWithPrefix3 = Utils.getHashWithPrefix(new byte[]{3}, bArr7);
        byte[] bArr11 = new byte[(hashWithPrefix3.length / 2) + bArr10.length];
        System.arraycopy(hashWithPrefix3, 0, bArr11, 0, hashWithPrefix3.length / 2);
        System.arraycopy(bArr10, 0, bArr11, hashWithPrefix3.length / 2, bArr10.length);
        return Arrays.copyOfRange(Utils.getHashWithPrefix(new byte[]{(byte) (i + 1)}, bArr11), 0, parameters.getSessionKeySize() / 8);
    }

    @Override // org.bouncycastle.crypto.EncapsulatedSecretExtractor
    public int getEncapsulationLength() {
        return this.privateKey.getParameters().getRoundedPolynomialBytes() + 32;
    }
}
