package org.bouncycastle.pqc.legacy.crypto.rainbow;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.legacy.crypto.rainbow.util.ComputeInField;
import org.bouncycastle.pqc.legacy.crypto.rainbow.util.GF2Field;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/rainbow/RainbowKeyPairGenerator.class */
public class RainbowKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private boolean initialized = false;
    private SecureRandom sr;
    private RainbowKeyGenerationParameters rainbowParams;
    private short[][] A1;
    private short[][] A1inv;
    private short[] b1;
    private short[][] A2;
    private short[][] A2inv;
    private short[] b2;
    private int numOfLayers;
    private Layer[] layers;
    private int[] vi;
    private short[][] pub_quadratic;
    private short[][] pub_singular;
    private short[] pub_scalar;

    public AsymmetricCipherKeyPair genKeyPair() {
        if (!this.initialized) {
            initializeDefault();
        }
        keygen();
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) new RainbowPublicKeyParameters(this.vi[this.vi.length - 1] - this.vi[0], this.pub_quadratic, this.pub_singular, this.pub_scalar), (AsymmetricKeyParameter) new RainbowPrivateKeyParameters(this.A1inv, this.b1, this.A2inv, this.b2, this.vi, this.layers));
    }

    public void initialize(KeyGenerationParameters keyGenerationParameters) {
        this.rainbowParams = (RainbowKeyGenerationParameters) keyGenerationParameters;
        this.sr = this.rainbowParams.getRandom();
        this.vi = this.rainbowParams.getParameters().getVi();
        this.numOfLayers = this.rainbowParams.getParameters().getNumOfLayers();
        this.initialized = true;
    }

    private void initializeDefault() {
        initialize(new RainbowKeyGenerationParameters(CryptoServicesRegistrar.getSecureRandom(), new RainbowParameters()));
    }

    private void keygen() {
        generateL1();
        generateL2();
        generateF();
        computePublicKey();
    }

    private void generateL1() {
        int i = this.vi[this.vi.length - 1] - this.vi[0];
        this.A1 = new short[i][i];
        this.A1inv = null;
        ComputeInField computeInField = new ComputeInField();
        while (this.A1inv == null) {
            for (int i2 = 0; i2 < i; i2++) {
                for (int i3 = 0; i3 < i; i3++) {
                    this.A1[i2][i3] = (short) (this.sr.nextInt() & 255);
                }
            }
            this.A1inv = computeInField.inverse(this.A1);
        }
        this.b1 = new short[i];
        for (int i4 = 0; i4 < i; i4++) {
            this.b1[i4] = (short) (this.sr.nextInt() & 255);
        }
    }

    private void generateL2() {
        int i = this.vi[this.vi.length - 1];
        this.A2 = new short[i][i];
        this.A2inv = null;
        ComputeInField computeInField = new ComputeInField();
        while (this.A2inv == null) {
            for (int i2 = 0; i2 < i; i2++) {
                for (int i3 = 0; i3 < i; i3++) {
                    this.A2[i2][i3] = (short) (this.sr.nextInt() & 255);
                }
            }
            this.A2inv = computeInField.inverse(this.A2);
        }
        this.b2 = new short[i];
        for (int i4 = 0; i4 < i; i4++) {
            this.b2[i4] = (short) (this.sr.nextInt() & 255);
        }
    }

    private void generateF() {
        this.layers = new Layer[this.numOfLayers];
        for (int i = 0; i < this.numOfLayers; i++) {
            this.layers[i] = new Layer(this.vi[i], this.vi[i + 1], this.sr);
        }
    }

    private void computePublicKey() {
        ComputeInField computeInField = new ComputeInField();
        int i = this.vi[this.vi.length - 1] - this.vi[0];
        int i2 = this.vi[this.vi.length - 1];
        short[][][] sArr = new short[i][i2][i2];
        this.pub_singular = new short[i][i2];
        this.pub_scalar = new short[i];
        int i3 = 0;
        short[] sArr2 = new short[i2];
        for (int i4 = 0; i4 < this.layers.length; i4++) {
            short[][][] coeffAlpha = this.layers[i4].getCoeffAlpha();
            short[][][] coeffBeta = this.layers[i4].getCoeffBeta();
            short[][] coeffGamma = this.layers[i4].getCoeffGamma();
            short[] coeffEta = this.layers[i4].getCoeffEta();
            int length = coeffAlpha[0].length;
            int length2 = coeffBeta[0].length;
            for (int i5 = 0; i5 < length; i5++) {
                for (int i6 = 0; i6 < length; i6++) {
                    for (int i7 = 0; i7 < length2; i7++) {
                        short[] sArrMultVect = computeInField.multVect(coeffAlpha[i5][i6][i7], this.A2[i6 + length2]);
                        sArr[i3 + i5] = computeInField.addSquareMatrix(sArr[i3 + i5], computeInField.multVects(sArrMultVect, this.A2[i7]));
                        this.pub_singular[i3 + i5] = computeInField.addVect(computeInField.multVect(this.b2[i7], sArrMultVect), this.pub_singular[i3 + i5]);
                        this.pub_singular[i3 + i5] = computeInField.addVect(computeInField.multVect(this.b2[i6 + length2], computeInField.multVect(coeffAlpha[i5][i6][i7], this.A2[i7])), this.pub_singular[i3 + i5]);
                        this.pub_scalar[i3 + i5] = GF2Field.addElem(this.pub_scalar[i3 + i5], GF2Field.multElem(GF2Field.multElem(coeffAlpha[i5][i6][i7], this.b2[i6 + length2]), this.b2[i7]));
                    }
                }
                for (int i8 = 0; i8 < length2; i8++) {
                    for (int i9 = 0; i9 < length2; i9++) {
                        short[] sArrMultVect2 = computeInField.multVect(coeffBeta[i5][i8][i9], this.A2[i8]);
                        sArr[i3 + i5] = computeInField.addSquareMatrix(sArr[i3 + i5], computeInField.multVects(sArrMultVect2, this.A2[i9]));
                        this.pub_singular[i3 + i5] = computeInField.addVect(computeInField.multVect(this.b2[i9], sArrMultVect2), this.pub_singular[i3 + i5]);
                        this.pub_singular[i3 + i5] = computeInField.addVect(computeInField.multVect(this.b2[i8], computeInField.multVect(coeffBeta[i5][i8][i9], this.A2[i9])), this.pub_singular[i3 + i5]);
                        this.pub_scalar[i3 + i5] = GF2Field.addElem(this.pub_scalar[i3 + i5], GF2Field.multElem(GF2Field.multElem(coeffBeta[i5][i8][i9], this.b2[i8]), this.b2[i9]));
                    }
                }
                for (int i10 = 0; i10 < length2 + length; i10++) {
                    this.pub_singular[i3 + i5] = computeInField.addVect(computeInField.multVect(coeffGamma[i5][i10], this.A2[i10]), this.pub_singular[i3 + i5]);
                    this.pub_scalar[i3 + i5] = GF2Field.addElem(this.pub_scalar[i3 + i5], GF2Field.multElem(coeffGamma[i5][i10], this.b2[i10]));
                }
                this.pub_scalar[i3 + i5] = GF2Field.addElem(this.pub_scalar[i3 + i5], coeffEta[i5]);
            }
            i3 += length;
        }
        short[][][] sArr3 = new short[i][i2][i2];
        short[][] sArr4 = new short[i][i2];
        short[] sArr5 = new short[i];
        for (int i11 = 0; i11 < i; i11++) {
            for (int i12 = 0; i12 < this.A1.length; i12++) {
                sArr3[i11] = computeInField.addSquareMatrix(sArr3[i11], computeInField.multMatrix(this.A1[i11][i12], sArr[i12]));
                sArr4[i11] = computeInField.addVect(sArr4[i11], computeInField.multVect(this.A1[i11][i12], this.pub_singular[i12]));
                sArr5[i11] = GF2Field.addElem(sArr5[i11], GF2Field.multElem(this.A1[i11][i12], this.pub_scalar[i12]));
            }
            sArr5[i11] = GF2Field.addElem(sArr5[i11], this.b1[i11]);
        }
        this.pub_singular = sArr4;
        this.pub_scalar = sArr5;
        compactPublicKey(sArr3);
    }

    private void compactPublicKey(short[][][] sArr) {
        int length = sArr.length;
        int length2 = sArr[0].length;
        this.pub_quadratic = new short[length][(length2 * (length2 + 1)) / 2];
        for (int i = 0; i < length; i++) {
            int i2 = 0;
            for (int i3 = 0; i3 < length2; i3++) {
                for (int i4 = i3; i4 < length2; i4++) {
                    if (i4 == i3) {
                        this.pub_quadratic[i][i2] = sArr[i][i3][i4];
                    } else {
                        this.pub_quadratic[i][i2] = GF2Field.addElem(sArr[i][i3][i4], sArr[i][i4][i3]);
                    }
                    i2++;
                }
            }
        }
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        initialize(keyGenerationParameters);
    }

    @Override // org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        return genKeyPair();
    }
}
