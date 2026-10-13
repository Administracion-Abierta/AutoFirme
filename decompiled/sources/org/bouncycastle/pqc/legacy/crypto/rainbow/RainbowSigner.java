package org.bouncycastle.pqc.legacy.crypto.rainbow;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.pqc.legacy.crypto.rainbow.util.ComputeInField;
import org.bouncycastle.pqc.legacy.crypto.rainbow.util.GF2Field;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/rainbow/RainbowSigner.class */
public class RainbowSigner implements MessageSigner {
    private static final int MAXITS = 65536;
    private SecureRandom random;
    int signableDocumentLength;
    private short[] x;
    private ComputeInField cf = new ComputeInField();
    RainbowKeyParameters key;

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public void init(boolean z, CipherParameters cipherParameters) {
        if (!z) {
            this.key = (RainbowPublicKeyParameters) cipherParameters;
        } else if (cipherParameters instanceof ParametersWithRandom) {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom) cipherParameters;
            this.random = parametersWithRandom.getRandom();
            this.key = (RainbowPrivateKeyParameters) parametersWithRandom.getParameters();
        } else {
            this.random = CryptoServicesRegistrar.getSecureRandom();
            this.key = (RainbowPrivateKeyParameters) cipherParameters;
        }
        this.signableDocumentLength = this.key.getDocLength();
    }

    private short[] initSign(Layer[] layerArr, short[] sArr) {
        short[] sArr2 = new short[sArr.length];
        short[] sArrMultiplyMatrix = this.cf.multiplyMatrix(((RainbowPrivateKeyParameters) this.key).getInvA1(), this.cf.addVect(((RainbowPrivateKeyParameters) this.key).getB1(), sArr));
        for (int i = 0; i < layerArr[0].getVi(); i++) {
            this.x[i] = (short) this.random.nextInt();
            this.x[i] = (short) (this.x[i] & 255);
        }
        return sArrMultiplyMatrix;
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public byte[] generateSignature(byte[] bArr) {
        Layer[] layers = ((RainbowPrivateKeyParameters) this.key).getLayers();
        int length = layers.length;
        this.x = new short[((RainbowPrivateKeyParameters) this.key).getInvA2().length];
        byte[] bArr2 = new byte[layers[length - 1].getViNext()];
        short[] sArrMakeMessageRepresentative = makeMessageRepresentative(bArr);
        int i = 0;
        do {
            boolean z = true;
            int i2 = 0;
            try {
                short[] sArrInitSign = initSign(layers, sArrMakeMessageRepresentative);
                for (int i3 = 0; i3 < length; i3++) {
                    short[] sArr = new short[layers[i3].getOi()];
                    short[] sArr2 = new short[layers[i3].getOi()];
                    for (int i4 = 0; i4 < layers[i3].getOi(); i4++) {
                        sArr[i4] = sArrInitSign[i2];
                        i2++;
                    }
                    short[] sArrSolveEquation = this.cf.solveEquation(layers[i3].plugInVinegars(this.x), sArr);
                    if (sArrSolveEquation == null) {
                        throw new Exception("LES is not solveable!");
                    }
                    for (int i5 = 0; i5 < sArrSolveEquation.length; i5++) {
                        this.x[layers[i3].getVi() + i5] = sArrSolveEquation[i5];
                    }
                }
                short[] sArrMultiplyMatrix = this.cf.multiplyMatrix(((RainbowPrivateKeyParameters) this.key).getInvA2(), this.cf.addVect(((RainbowPrivateKeyParameters) this.key).getB2(), this.x));
                for (int i6 = 0; i6 < bArr2.length; i6++) {
                    bArr2[i6] = (byte) sArrMultiplyMatrix[i6];
                }
            } catch (Exception e) {
                z = false;
            }
            if (z) {
                break;
            }
            i++;
        } while (i < 65536);
        if (i == 65536) {
            throw new IllegalStateException("unable to generate signature - LES not solvable");
        }
        return bArr2;
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public boolean verifySignature(byte[] bArr, byte[] bArr2) {
        short[] sArr = new short[bArr2.length];
        for (int i = 0; i < bArr2.length; i++) {
            sArr[i] = (short) (bArr2[i] & 255);
        }
        short[] sArrMakeMessageRepresentative = makeMessageRepresentative(bArr);
        short[] sArrVerifySignatureIntern = verifySignatureIntern(sArr);
        boolean z = true;
        if (sArrMakeMessageRepresentative.length != sArrVerifySignatureIntern.length) {
            return false;
        }
        for (int i2 = 0; i2 < sArrMakeMessageRepresentative.length; i2++) {
            z = z && sArrMakeMessageRepresentative[i2] == sArrVerifySignatureIntern[i2];
        }
        return z;
    }

    private short[] verifySignatureIntern(short[] sArr) {
        short[][] coeffQuadratic = ((RainbowPublicKeyParameters) this.key).getCoeffQuadratic();
        short[][] coeffSingular = ((RainbowPublicKeyParameters) this.key).getCoeffSingular();
        short[] coeffScalar = ((RainbowPublicKeyParameters) this.key).getCoeffScalar();
        short[] sArr2 = new short[coeffQuadratic.length];
        int length = coeffSingular[0].length;
        for (int i = 0; i < coeffQuadratic.length; i++) {
            int i2 = 0;
            for (int i3 = 0; i3 < length; i3++) {
                for (int i4 = i3; i4 < length; i4++) {
                    sArr2[i] = GF2Field.addElem(sArr2[i], GF2Field.multElem(coeffQuadratic[i][i2], GF2Field.multElem(sArr[i3], sArr[i4])));
                    i2++;
                }
                sArr2[i] = GF2Field.addElem(sArr2[i], GF2Field.multElem(coeffSingular[i][i3], sArr[i3]));
            }
            sArr2[i] = GF2Field.addElem(sArr2[i], coeffScalar[i]);
        }
        return sArr2;
    }

    private short[] makeMessageRepresentative(byte[] bArr) {
        short[] sArr = new short[this.signableDocumentLength];
        int i = 0;
        int i2 = 0;
        while (i2 < bArr.length) {
            sArr[i2] = bArr[i];
            int i3 = i2;
            sArr[i3] = (short) (sArr[i3] & 255);
            i++;
            i2++;
            if (i2 >= sArr.length) {
                break;
            }
        }
        return sArr;
    }
}
