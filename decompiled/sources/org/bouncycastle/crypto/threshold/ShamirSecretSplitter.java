package org.bouncycastle.crypto.threshold;

import java.io.IOException;
import java.security.SecureRandom;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/threshold/ShamirSecretSplitter.class */
public class ShamirSecretSplitter implements SecretSplitter {
    private final Polynomial poly;
    protected int l;
    protected SecureRandom random;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/threshold/ShamirSecretSplitter$Algorithm.class */
    public enum Algorithm {
        AES,
        RSA
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/threshold/ShamirSecretSplitter$Mode.class */
    public enum Mode {
        Native,
        Table
    }

    public ShamirSecretSplitter(Algorithm algorithm, Mode mode, int i, SecureRandom secureRandom) {
        if (i < 0 || i > 65534) {
            throw new IllegalArgumentException("Invalid input: l ranges from 0 to 65534 (2^16-2) bytes.");
        }
        this.poly = Polynomial.newInstance(algorithm, mode);
        this.l = i;
        this.random = secureRandom;
    }

    @Override // org.bouncycastle.crypto.threshold.SecretSplitter
    public ShamirSplitSecret split(int i, int i2) {
        byte[][] bArrInitP = initP(i, i2);
        byte[][] bArr = new byte[i][this.l];
        ShamirSplitSecretShare[] shamirSplitSecretShareArr = new ShamirSplitSecretShare[this.l];
        for (int i3 = 0; i3 < i; i3++) {
            this.random.nextBytes(bArr[i3]);
        }
        for (int i4 = 0; i4 < bArrInitP.length; i4++) {
            shamirSplitSecretShareArr[i4] = new ShamirSplitSecretShare(this.poly.gfVecMul(bArrInitP[i4], bArr), i4 + 1);
        }
        return new ShamirSplitSecret(this.poly, shamirSplitSecretShareArr);
    }

    @Override // org.bouncycastle.crypto.threshold.SecretSplitter
    public ShamirSplitSecret splitAround(SecretShare secretShare, int i, int i2) throws IOException {
        byte[][] bArrInitP = initP(i, i2);
        byte[][] bArr = new byte[i][this.l];
        ShamirSplitSecretShare[] shamirSplitSecretShareArr = new ShamirSplitSecretShare[this.l];
        byte[] encoded = secretShare.getEncoded();
        shamirSplitSecretShareArr[0] = new ShamirSplitSecretShare(encoded, 1);
        for (int i3 = 0; i3 < i; i3++) {
            this.random.nextBytes(bArr[i3]);
        }
        for (int i4 = 0; i4 < this.l; i4++) {
            byte b = bArr[1][i4];
            for (int i5 = 2; i5 < i; i5++) {
                b = (byte) (b ^ bArr[i5][i4]);
            }
            bArr[0][i4] = (byte) (b ^ encoded[i4]);
        }
        for (int i6 = 1; i6 < bArrInitP.length; i6++) {
            shamirSplitSecretShareArr[i6] = new ShamirSplitSecretShare(this.poly.gfVecMul(bArrInitP[i6], bArr), i6 + 1);
        }
        return new ShamirSplitSecret(this.poly, shamirSplitSecretShareArr);
    }

    @Override // org.bouncycastle.crypto.threshold.SecretSplitter
    public ShamirSplitSecret resplit(byte[] bArr, int i, int i2) {
        byte[][] bArrInitP = initP(i, i2);
        byte[][] bArr2 = new byte[i][this.l];
        ShamirSplitSecretShare[] shamirSplitSecretShareArr = new ShamirSplitSecretShare[this.l];
        bArr2[0] = Arrays.clone(bArr);
        for (int i3 = 1; i3 < i; i3++) {
            this.random.nextBytes(bArr2[i3]);
        }
        for (int i4 = 0; i4 < bArrInitP.length; i4++) {
            shamirSplitSecretShareArr[i4] = new ShamirSplitSecretShare(this.poly.gfVecMul(bArrInitP[i4], bArr2), i4 + 1);
        }
        return new ShamirSplitSecret(this.poly, shamirSplitSecretShareArr);
    }

    private byte[][] initP(int i, int i2) {
        if (i < 1 || i > 255) {
            throw new IllegalArgumentException("Invalid input: m must be less than 256 and positive.");
        }
        if (i2 < i || i2 > 255) {
            throw new IllegalArgumentException("Invalid input: n must be less than 256 and greater than or equal to n.");
        }
        byte[][] bArr = new byte[i2][i];
        for (int i3 = 0; i3 < i2; i3++) {
            for (int i4 = 0; i4 < i; i4++) {
                bArr[i3][i4] = this.poly.gfPow((byte) (i3 + 1), (byte) i4);
            }
        }
        return bArr;
    }
}
