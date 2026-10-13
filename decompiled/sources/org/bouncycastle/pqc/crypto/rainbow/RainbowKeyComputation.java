package org.bouncycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/rainbow/RainbowKeyComputation.class */
class RainbowKeyComputation {
    private SecureRandom random;
    private Version version;
    private RainbowParameters rainbowParams;
    ComputeInField cf;
    private int v1;
    private int o1;
    private int o2;
    private byte[] sk_seed;
    private byte[] pk_seed;
    private short[][] s1;
    private short[][] t1;
    private short[][] t2;
    private short[][] t3;
    private short[][] t4;
    private short[][][] l1_F1;
    private short[][][] l1_F2;
    private short[][][] l2_F1;
    private short[][][] l2_F2;
    private short[][][] l2_F3;
    private short[][][] l2_F5;
    private short[][][] l2_F6;
    private short[][][] l1_Q1;
    private short[][][] l1_Q2;
    private short[][][] l1_Q3;
    private short[][][] l1_Q5;
    private short[][][] l1_Q6;
    private short[][][] l1_Q9;
    private short[][][] l2_Q1;
    private short[][][] l2_Q2;
    private short[][][] l2_Q3;
    private short[][][] l2_Q5;
    private short[][][] l2_Q6;
    private short[][][] l2_Q9;

    public RainbowKeyComputation(RainbowParameters rainbowParameters, SecureRandom secureRandom) {
        this.cf = new ComputeInField();
        this.rainbowParams = rainbowParameters;
        this.random = secureRandom;
        this.version = this.rainbowParams.getVersion();
        this.v1 = this.rainbowParams.getV1();
        this.o1 = this.rainbowParams.getO1();
        this.o2 = this.rainbowParams.getO2();
    }

    public RainbowKeyComputation(RainbowParameters rainbowParameters, byte[] bArr, byte[] bArr2) {
        this.cf = new ComputeInField();
        this.rainbowParams = rainbowParameters;
        this.random = null;
        this.version = this.rainbowParams.getVersion();
        this.pk_seed = bArr;
        this.sk_seed = bArr2;
        this.v1 = this.rainbowParams.getV1();
        this.o1 = this.rainbowParams.getO1();
        this.o2 = this.rainbowParams.getO2();
    }

    private void generate_S_and_T(SecureRandom secureRandom) {
        this.s1 = RainbowUtil.generate_random_2d(secureRandom, this.o1, this.o2);
        this.t1 = RainbowUtil.generate_random_2d(secureRandom, this.v1, this.o1);
        this.t2 = RainbowUtil.generate_random_2d(secureRandom, this.v1, this.o2);
        this.t3 = RainbowUtil.generate_random_2d(secureRandom, this.o1, this.o2);
    }

    private void generate_B1_and_B2(SecureRandom secureRandom) {
        this.l1_Q1 = RainbowUtil.generate_random(secureRandom, this.o1, this.v1, this.v1, true);
        this.l1_Q2 = RainbowUtil.generate_random(secureRandom, this.o1, this.v1, this.o1, false);
        this.l2_Q1 = RainbowUtil.generate_random(secureRandom, this.o2, this.v1, this.v1, true);
        this.l2_Q2 = RainbowUtil.generate_random(secureRandom, this.o2, this.v1, this.o1, false);
        this.l2_Q3 = RainbowUtil.generate_random(secureRandom, this.o2, this.v1, this.o2, false);
        this.l2_Q5 = RainbowUtil.generate_random(secureRandom, this.o2, this.o1, this.o1, true);
        this.l2_Q6 = RainbowUtil.generate_random(secureRandom, this.o2, this.o1, this.o2, false);
    }

    private void calculate_t4() {
        this.t4 = this.cf.addMatrix(this.cf.multiplyMatrix(this.t1, this.t3), this.t2);
    }

    private void calculate_F_from_Q() {
        this.l1_F1 = RainbowUtil.cloneArray(this.l1_Q1);
        this.l1_F2 = new short[this.o1][][];
        for (int i = 0; i < this.o1; i++) {
            this.l1_F2[i] = this.cf.addMatrixTranspose(this.l1_Q1[i]);
            this.l1_F2[i] = this.cf.multiplyMatrix(this.l1_F2[i], this.t1);
            this.l1_F2[i] = this.cf.addMatrix(this.l1_F2[i], this.l1_Q2[i]);
        }
        this.l2_F2 = new short[this.o2][][];
        this.l2_F3 = new short[this.o2][][];
        this.l2_F5 = new short[this.o2][][];
        this.l2_F6 = new short[this.o2][][];
        this.l2_F1 = RainbowUtil.cloneArray(this.l2_Q1);
        for (int i2 = 0; i2 < this.o2; i2++) {
            short[][] sArrAddMatrixTranspose = this.cf.addMatrixTranspose(this.l2_Q1[i2]);
            this.l2_F2[i2] = this.cf.multiplyMatrix(sArrAddMatrixTranspose, this.t1);
            this.l2_F2[i2] = this.cf.addMatrix(this.l2_F2[i2], this.l2_Q2[i2]);
            this.l2_F3[i2] = this.cf.multiplyMatrix(sArrAddMatrixTranspose, this.t4);
            this.l2_F3[i2] = this.cf.addMatrix(this.l2_F3[i2], this.cf.multiplyMatrix(this.l2_Q2[i2], this.t3));
            this.l2_F3[i2] = this.cf.addMatrix(this.l2_F3[i2], this.l2_Q3[i2]);
            short[][] sArrAddMatrix = this.cf.addMatrix(this.cf.multiplyMatrix(this.l2_Q1[i2], this.t1), this.l2_Q2[i2]);
            short[][] sArrTranspose = this.cf.transpose(this.t1);
            this.l2_F5[i2] = this.cf.multiplyMatrix(sArrTranspose, sArrAddMatrix);
            this.l2_F5[i2] = this.cf.addMatrix(this.l2_F5[i2], this.l2_Q5[i2]);
            this.l2_F5[i2] = this.cf.to_UT(this.l2_F5[i2]);
            this.l2_F6[i2] = this.cf.multiplyMatrix(sArrTranspose, this.l2_F3[i2]);
            this.l2_F6[i2] = this.cf.addMatrix(this.l2_F6[i2], this.cf.multiplyMatrix(this.cf.transpose(this.l2_Q2[i2]), this.t4));
            this.l2_F6[i2] = this.cf.addMatrix(this.l2_F6[i2], this.cf.multiplyMatrix(this.cf.addMatrixTranspose(this.l2_Q5[i2]), this.t3));
            this.l2_F6[i2] = this.cf.addMatrix(this.l2_F6[i2], this.l2_Q6[i2]);
        }
    }

    private void calculate_Q_from_F() {
        short[][] sArrTranspose = this.cf.transpose(this.t1);
        short[][] sArrTranspose2 = this.cf.transpose(this.t2);
        this.l1_Q1 = RainbowUtil.cloneArray(this.l1_F1);
        this.l1_Q2 = new short[this.o1][][];
        for (int i = 0; i < this.o1; i++) {
            this.l1_Q2[i] = this.cf.addMatrixTranspose(this.l1_F1[i]);
            this.l1_Q2[i] = this.cf.multiplyMatrix(this.l1_Q2[i], this.t1);
            this.l1_Q2[i] = this.cf.addMatrix(this.l1_Q2[i], this.l1_F2[i]);
        }
        calculate_l1_Q3569(sArrTranspose, sArrTranspose2);
        this.l2_Q2 = new short[this.o2][][];
        this.l2_Q3 = new short[this.o2][][];
        this.l2_Q5 = new short[this.o2][][];
        this.l2_Q6 = new short[this.o2][][];
        this.l2_Q1 = RainbowUtil.cloneArray(this.l2_F1);
        for (int i2 = 0; i2 < this.o2; i2++) {
            short[][] sArrAddMatrixTranspose = this.cf.addMatrixTranspose(this.l2_F1[i2]);
            this.l2_Q2[i2] = this.cf.multiplyMatrix(sArrAddMatrixTranspose, this.t1);
            this.l2_Q2[i2] = this.cf.addMatrix(this.l2_Q2[i2], this.l2_F2[i2]);
            this.l2_Q3[i2] = this.cf.multiplyMatrix(sArrAddMatrixTranspose, this.t2);
            this.l2_Q3[i2] = this.cf.addMatrix(this.l2_Q3[i2], this.cf.multiplyMatrix(this.l2_F2[i2], this.t3));
            this.l2_Q3[i2] = this.cf.addMatrix(this.l2_Q3[i2], this.l2_F3[i2]);
            this.l2_Q5[i2] = this.cf.multiplyMatrix(sArrTranspose, this.cf.addMatrix(this.cf.multiplyMatrix(this.l2_F1[i2], this.t1), this.l2_F2[i2]));
            this.l2_Q5[i2] = this.cf.addMatrix(this.l2_Q5[i2], this.l2_F5[i2]);
            this.l2_Q5[i2] = this.cf.to_UT(this.l2_Q5[i2]);
            this.l2_Q6[i2] = this.cf.multiplyMatrix(sArrTranspose, this.l2_Q3[i2]);
            this.l2_Q6[i2] = this.cf.addMatrix(this.l2_Q6[i2], this.cf.multiplyMatrix(this.cf.transpose(this.l2_F2[i2]), this.t2));
            this.l2_Q6[i2] = this.cf.addMatrix(this.l2_Q6[i2], this.cf.multiplyMatrix(this.cf.addMatrixTranspose(this.l2_F5[i2]), this.t3));
            this.l2_Q6[i2] = this.cf.addMatrix(this.l2_Q6[i2], this.l2_F6[i2]);
        }
        calculate_l2_Q9(sArrTranspose2);
    }

    private void calculate_Q_from_F_cyclic() {
        short[][] sArrTranspose = this.cf.transpose(this.t1);
        short[][] sArrTranspose2 = this.cf.transpose(this.t2);
        calculate_l1_Q3569(sArrTranspose, sArrTranspose2);
        calculate_l2_Q9(sArrTranspose2);
    }

    private void calculate_l1_Q3569(short[][] sArr, short[][] sArr2) {
        this.l1_Q3 = new short[this.o1][][];
        this.l1_Q5 = new short[this.o1][][];
        this.l1_Q6 = new short[this.o1][][];
        this.l1_Q9 = new short[this.o1][][];
        for (int i = 0; i < this.o1; i++) {
            short[][] sArrMultiplyMatrix = this.cf.multiplyMatrix(this.l1_F2[i], this.t3);
            this.l1_Q3[i] = this.cf.addMatrixTranspose(this.l1_F1[i]);
            this.l1_Q3[i] = this.cf.multiplyMatrix(this.l1_Q3[i], this.t2);
            this.l1_Q3[i] = this.cf.addMatrix(this.l1_Q3[i], sArrMultiplyMatrix);
            this.l1_Q5[i] = this.cf.multiplyMatrix(this.l1_F1[i], this.t1);
            this.l1_Q5[i] = this.cf.addMatrix(this.l1_Q5[i], this.l1_F2[i]);
            this.l1_Q5[i] = this.cf.multiplyMatrix(sArr, this.l1_Q5[i]);
            this.l1_Q5[i] = this.cf.to_UT(this.l1_Q5[i]);
            short[][] sArrMultiplyMatrix2 = this.cf.multiplyMatrix(this.cf.transpose(this.l1_F2[i]), this.t2);
            this.l1_Q6[i] = this.cf.multiplyMatrix(sArr, this.l1_Q3[i]);
            this.l1_Q6[i] = this.cf.addMatrix(this.l1_Q6[i], sArrMultiplyMatrix2);
            this.l1_Q9[i] = this.cf.addMatrix(this.cf.multiplyMatrix(this.l1_F1[i], this.t2), sArrMultiplyMatrix);
            this.l1_Q9[i] = this.cf.multiplyMatrix(sArr2, this.l1_Q9[i]);
            this.l1_Q9[i] = this.cf.to_UT(this.l1_Q9[i]);
        }
    }

    private void calculate_l2_Q9(short[][] sArr) {
        this.l2_Q9 = new short[this.o2][][];
        for (int i = 0; i < this.o2; i++) {
            this.l2_Q9[i] = this.cf.multiplyMatrix(this.l2_F1[i], this.t2);
            this.l2_Q9[i] = this.cf.addMatrix(this.l2_Q9[i], this.cf.multiplyMatrix(this.l2_F2[i], this.t3));
            this.l2_Q9[i] = this.cf.addMatrix(this.l2_Q9[i], this.l2_F3[i]);
            this.l2_Q9[i] = this.cf.multiplyMatrix(sArr, this.l2_Q9[i]);
            this.l2_Q9[i] = this.cf.addMatrix(this.l2_Q9[i], this.cf.multiplyMatrix(this.cf.transpose(this.t3), this.cf.addMatrix(this.cf.multiplyMatrix(this.l2_F5[i], this.t3), this.l2_F6[i])));
            this.l2_Q9[i] = this.cf.to_UT(this.l2_Q9[i]);
        }
    }

    private void genKeyMaterial() {
        this.sk_seed = new byte[this.rainbowParams.getLen_skseed()];
        this.random.nextBytes(this.sk_seed);
        RainbowDRBG rainbowDRBG = new RainbowDRBG(this.sk_seed, this.rainbowParams.getHash_algo());
        generate_S_and_T(rainbowDRBG);
        this.l1_F1 = RainbowUtil.generate_random(rainbowDRBG, this.o1, this.v1, this.v1, true);
        this.l1_F2 = RainbowUtil.generate_random(rainbowDRBG, this.o1, this.v1, this.o1, false);
        this.l2_F1 = RainbowUtil.generate_random(rainbowDRBG, this.o2, this.v1, this.v1, true);
        this.l2_F2 = RainbowUtil.generate_random(rainbowDRBG, this.o2, this.v1, this.o1, false);
        this.l2_F3 = RainbowUtil.generate_random(rainbowDRBG, this.o2, this.v1, this.o2, false);
        this.l2_F5 = RainbowUtil.generate_random(rainbowDRBG, this.o2, this.o1, this.o1, true);
        this.l2_F6 = RainbowUtil.generate_random(rainbowDRBG, this.o2, this.o1, this.o2, false);
        calculate_Q_from_F();
        calculate_t4();
        this.l1_Q1 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q1, this.l1_Q1);
        this.l1_Q2 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q2, this.l1_Q2);
        this.l1_Q3 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q3, this.l1_Q3);
        this.l1_Q5 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q5, this.l1_Q5);
        this.l1_Q6 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q6, this.l1_Q6);
        this.l1_Q9 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q9, this.l1_Q9);
    }

    private void genPrivateKeyMaterial_cyclic() {
        RainbowDRBG rainbowDRBG = new RainbowDRBG(this.sk_seed, this.rainbowParams.getHash_algo());
        RainbowDRBG rainbowDRBG2 = new RainbowDRBG(this.pk_seed, this.rainbowParams.getHash_algo());
        generate_S_and_T(rainbowDRBG);
        calculate_t4();
        generate_B1_and_B2(rainbowDRBG2);
        this.l1_Q1 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q1, this.l1_Q1);
        this.l1_Q2 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q2, this.l1_Q2);
        calculate_F_from_Q();
    }

    private void genKeyMaterial_cyclic() {
        this.sk_seed = new byte[this.rainbowParams.getLen_skseed()];
        this.random.nextBytes(this.sk_seed);
        this.pk_seed = new byte[this.rainbowParams.getLen_pkseed()];
        this.random.nextBytes(this.pk_seed);
        genPrivateKeyMaterial_cyclic();
        calculate_Q_from_F_cyclic();
        this.l1_Q3 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q3, this.l1_Q3);
        this.l1_Q5 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q5, this.l1_Q5);
        this.l1_Q6 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q6, this.l1_Q6);
        this.l1_Q9 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q9, this.l1_Q9);
    }

    public AsymmetricCipherKeyPair genKeyPairClassical() {
        genKeyMaterial();
        RainbowPublicKeyParameters rainbowPublicKeyParameters = new RainbowPublicKeyParameters(this.rainbowParams, this.l1_Q1, this.l1_Q2, this.l1_Q3, this.l1_Q5, this.l1_Q6, this.l1_Q9, this.l2_Q1, this.l2_Q2, this.l2_Q3, this.l2_Q5, this.l2_Q6, this.l2_Q9);
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) rainbowPublicKeyParameters, (AsymmetricKeyParameter) new RainbowPrivateKeyParameters(this.rainbowParams, this.sk_seed, this.s1, this.t1, this.t3, this.t4, this.l1_F1, this.l1_F2, this.l2_F1, this.l2_F2, this.l2_F3, this.l2_F5, this.l2_F6, rainbowPublicKeyParameters.getEncoded()));
    }

    public AsymmetricCipherKeyPair genKeyPairCircumzenithal() {
        genKeyMaterial_cyclic();
        RainbowPublicKeyParameters rainbowPublicKeyParameters = new RainbowPublicKeyParameters(this.rainbowParams, this.pk_seed, this.l1_Q3, this.l1_Q5, this.l1_Q6, this.l1_Q9, this.l2_Q9);
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) rainbowPublicKeyParameters, (AsymmetricKeyParameter) new RainbowPrivateKeyParameters(this.rainbowParams, this.sk_seed, this.s1, this.t1, this.t3, this.t4, this.l1_F1, this.l1_F2, this.l2_F1, this.l2_F2, this.l2_F3, this.l2_F5, this.l2_F6, rainbowPublicKeyParameters.getEncoded()));
    }

    public AsymmetricCipherKeyPair genKeyPairCompressed() {
        genKeyMaterial_cyclic();
        RainbowPublicKeyParameters rainbowPublicKeyParameters = new RainbowPublicKeyParameters(this.rainbowParams, this.pk_seed, this.l1_Q3, this.l1_Q5, this.l1_Q6, this.l1_Q9, this.l2_Q9);
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) rainbowPublicKeyParameters, (AsymmetricKeyParameter) new RainbowPrivateKeyParameters(this.rainbowParams, this.pk_seed, this.sk_seed, rainbowPublicKeyParameters.getEncoded()));
    }

    RainbowPrivateKeyParameters generatePrivateKey() {
        this.sk_seed = Arrays.clone(this.sk_seed);
        this.pk_seed = Arrays.clone(this.pk_seed);
        genPrivateKeyMaterial_cyclic();
        return new RainbowPrivateKeyParameters(this.rainbowParams, this.sk_seed, this.s1, this.t1, this.t3, this.t4, this.l1_F1, this.l1_F2, this.l2_F1, this.l2_F2, this.l2_F3, this.l2_F5, this.l2_F6, null);
    }
}
