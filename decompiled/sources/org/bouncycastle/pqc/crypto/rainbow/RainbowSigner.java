package org.bouncycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/rainbow/RainbowSigner.class */
public class RainbowSigner implements MessageSigner {
    private static final int MAXITS = 65536;
    private SecureRandom random;
    int signableDocumentLength;
    private ComputeInField cf = new ComputeInField();
    private RainbowKeyParameters key;
    private Digest hashAlgo;
    private Version version;

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public void init(boolean z, CipherParameters cipherParameters) {
        RainbowKeyParameters rainbowKeyParameters;
        if (z) {
            if (cipherParameters instanceof ParametersWithRandom) {
                ParametersWithRandom parametersWithRandom = (ParametersWithRandom) cipherParameters;
                this.random = parametersWithRandom.getRandom();
                rainbowKeyParameters = (RainbowKeyParameters) parametersWithRandom.getParameters();
            } else {
                rainbowKeyParameters = (RainbowKeyParameters) cipherParameters;
                SecureRandom secureRandom = CryptoServicesRegistrar.getSecureRandom();
                byte[] bArr = new byte[rainbowKeyParameters.getParameters().getLen_skseed()];
                secureRandom.nextBytes(bArr);
                this.random = new RainbowDRBG(bArr, rainbowKeyParameters.getParameters().getHash_algo());
            }
            this.version = rainbowKeyParameters.getParameters().getVersion();
            this.key = rainbowKeyParameters;
        } else {
            this.key = (RainbowKeyParameters) cipherParameters;
            this.version = this.key.getParameters().getVersion();
        }
        this.signableDocumentLength = this.key.getDocLength();
        this.hashAlgo = this.key.getParameters().getHash_algo();
    }

    private byte[] genSignature(byte[] bArr) {
        byte[] bArr2 = new byte[this.hashAlgo.getDigestSize()];
        this.hashAlgo.update(bArr, 0, bArr.length);
        this.hashAlgo.doFinal(bArr2, 0);
        int v1 = this.key.getParameters().getV1();
        int o1 = this.key.getParameters().getO1();
        int o2 = this.key.getParameters().getO2();
        int m = this.key.getParameters().getM();
        int n = this.key.getParameters().getN();
        RainbowPrivateKeyParameters rainbowPrivateKeyParameters = (RainbowPrivateKeyParameters) this.key;
        this.random = new RainbowDRBG(RainbowUtil.hash(this.hashAlgo, rainbowPrivateKeyParameters.sk_seed, bArr2, new byte[this.hashAlgo.getDigestSize()]), rainbowPrivateKeyParameters.getParameters().getHash_algo());
        short[] sArr = new short[v1];
        short[][] sArrInverse = null;
        short[] sArr2 = new short[o1];
        short[] sArr3 = new short[o2];
        short[] sArr4 = new short[o2];
        short[][] sArr5 = new short[o2][o1];
        short[][] sArr6 = new short[o2][o2];
        byte[] bArr3 = new byte[rainbowPrivateKeyParameters.getParameters().getLen_salt()];
        short[] sArr7 = new short[m];
        short[] sArrMultiplyMatrix = new short[o1];
        short[] sArrSolveEquation = null;
        int i = 0;
        while (sArrInverse == null && i < 65536) {
            byte[] bArr4 = new byte[v1];
            this.random.nextBytes(bArr4);
            for (int i2 = 0; i2 < v1; i2++) {
                sArr[i2] = (short) (bArr4[i2] & 255);
            }
            short[][] sArr8 = new short[o1][o1];
            for (int i3 = 0; i3 < v1; i3++) {
                for (int i4 = 0; i4 < o1; i4++) {
                    for (int i5 = 0; i5 < o1; i5++) {
                        sArr8[i4][i5] = GF2Field.addElem(sArr8[i4][i5], GF2Field.multElem(rainbowPrivateKeyParameters.l1_F2[i4][i3][i5], sArr[i3]));
                    }
                }
            }
            sArrInverse = this.cf.inverse(sArr8);
            i++;
        }
        for (int i6 = 0; i6 < o1; i6++) {
            sArr2[i6] = this.cf.multiplyMatrix_quad(rainbowPrivateKeyParameters.l1_F1[i6], sArr);
        }
        for (int i7 = 0; i7 < v1; i7++) {
            for (int i8 = 0; i8 < o2; i8++) {
                sArr3[i8] = this.cf.multiplyMatrix_quad(rainbowPrivateKeyParameters.l2_F1[i8], sArr);
                for (int i9 = 0; i9 < o1; i9++) {
                    sArr5[i8][i9] = GF2Field.addElem(sArr5[i8][i9], GF2Field.multElem(rainbowPrivateKeyParameters.l2_F2[i8][i7][i9], sArr[i7]));
                }
                for (int i10 = 0; i10 < o2; i10++) {
                    sArr6[i8][i10] = GF2Field.addElem(sArr6[i8][i10], GF2Field.multElem(rainbowPrivateKeyParameters.l2_F3[i8][i7][i10], sArr[i7]));
                }
            }
        }
        byte[] bArr5 = new byte[m];
        while (sArrSolveEquation == null && i < 65536) {
            short[][] sArr9 = new short[o2][o2];
            this.random.nextBytes(bArr3);
            short[] sArrMakeMessageRepresentative = makeMessageRepresentative(RainbowUtil.hash(this.hashAlgo, bArr2, bArr3, bArr5));
            System.arraycopy(this.cf.addVect(Arrays.copyOf(sArrMakeMessageRepresentative, o1), this.cf.multiplyMatrix(rainbowPrivateKeyParameters.s1, Arrays.copyOfRange(sArrMakeMessageRepresentative, o1, m))), 0, sArr7, 0, o1);
            System.arraycopy(sArrMakeMessageRepresentative, o1, sArr7, o1, o2);
            sArrMultiplyMatrix = this.cf.multiplyMatrix(sArrInverse, this.cf.addVect(sArr2, Arrays.copyOf(sArr7, o1)));
            short[] sArrMultiplyMatrix2 = this.cf.multiplyMatrix(sArr5, sArrMultiplyMatrix);
            for (int i11 = 0; i11 < o2; i11++) {
                sArr4[i11] = this.cf.multiplyMatrix_quad(rainbowPrivateKeyParameters.l2_F5[i11], sArrMultiplyMatrix);
            }
            short[] sArrAddVect = this.cf.addVect(this.cf.addVect(this.cf.addVect(sArrMultiplyMatrix2, sArr4), sArr3), Arrays.copyOfRange(sArr7, o1, m));
            for (int i12 = 0; i12 < o1; i12++) {
                for (int i13 = 0; i13 < o2; i13++) {
                    for (int i14 = 0; i14 < o2; i14++) {
                        sArr9[i13][i14] = GF2Field.addElem(sArr9[i13][i14], GF2Field.multElem(rainbowPrivateKeyParameters.l2_F6[i13][i12][i14], sArrMultiplyMatrix[i12]));
                    }
                }
            }
            sArrSolveEquation = this.cf.solveEquation(this.cf.addMatrix(sArr9, sArr6), sArrAddVect);
            i++;
        }
        short[] sArr10 = sArrSolveEquation == null ? new short[o2] : sArrSolveEquation;
        short[] sArrAddVect2 = this.cf.addVect(this.cf.addVect(sArr, this.cf.multiplyMatrix(rainbowPrivateKeyParameters.t1, sArrMultiplyMatrix)), this.cf.multiplyMatrix(rainbowPrivateKeyParameters.t4, sArr10));
        short[] sArrAddVect3 = this.cf.addVect(sArrMultiplyMatrix, this.cf.multiplyMatrix(rainbowPrivateKeyParameters.t3, sArr10));
        short[] sArrCopyOf = Arrays.copyOf(sArrAddVect2, n);
        System.arraycopy(sArrAddVect3, 0, sArrCopyOf, v1, o1);
        System.arraycopy(sArr10, 0, sArrCopyOf, o1 + v1, o2);
        if (i == 65536) {
            throw new IllegalStateException("unable to generate signature - LES not solvable");
        }
        return Arrays.concatenate(RainbowUtil.convertArray(sArrCopyOf), bArr3);
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public byte[] generateSignature(byte[] bArr) {
        return genSignature(bArr);
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public boolean verifySignature(byte[] bArr, byte[] bArr2) {
        short[] sArrPublicMap_cyclic;
        byte[] bArr3 = new byte[this.hashAlgo.getDigestSize()];
        this.hashAlgo.update(bArr, 0, bArr.length);
        this.hashAlgo.doFinal(bArr3, 0);
        int m = this.key.getParameters().getM();
        int n = this.key.getParameters().getN();
        RainbowPublicMap rainbowPublicMap = new RainbowPublicMap(this.key.getParameters());
        short[] sArrMakeMessageRepresentative = makeMessageRepresentative(RainbowUtil.hash(this.hashAlgo, bArr3, Arrays.copyOfRange(bArr2, n, bArr2.length), new byte[m]));
        short[] sArrConvertArray = RainbowUtil.convertArray(Arrays.copyOfRange(bArr2, 0, n));
        switch (this.version) {
            case CLASSIC:
                sArrPublicMap_cyclic = rainbowPublicMap.publicMap((RainbowPublicKeyParameters) this.key, sArrConvertArray);
                break;
            case CIRCUMZENITHAL:
            case COMPRESSED:
                sArrPublicMap_cyclic = rainbowPublicMap.publicMap_cyclic((RainbowPublicKeyParameters) this.key, sArrConvertArray);
                break;
            default:
                throw new IllegalArgumentException("No valid version. Please choose one of the following: classic, circumzenithal, compressed");
        }
        return RainbowUtil.equals(sArrMakeMessageRepresentative, sArrPublicMap_cyclic);
    }

    private short[] makeMessageRepresentative(byte[] bArr) {
        short[] sArr = new short[this.signableDocumentLength];
        int i = 0;
        int i2 = 0;
        while (i2 < bArr.length) {
            sArr[i2] = (short) (bArr[i] & 255);
            i++;
            i2++;
            if (i2 >= sArr.length) {
                break;
            }
        }
        return sArr;
    }
}
