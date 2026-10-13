package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/rainbow/RainbowPublicMap.class */
class RainbowPublicMap {
    private RainbowParameters params;
    private final int num_gf_elements = 256;
    private ComputeInField cf = new ComputeInField();

    public RainbowPublicMap(RainbowParameters rainbowParameters) {
        this.params = rainbowParameters;
    }

    private short[][] compute_accumulator(short[] sArr, short[] sArr2, short[][][] sArr3, int i) {
        short[][] sArr4 = new short[256][i];
        if (sArr2.length != sArr3[0].length || sArr.length != sArr3[0][0].length || sArr3.length != i) {
            throw new RuntimeException("Accumulator calculation not possible!");
        }
        for (int i2 = 0; i2 < sArr2.length; i2++) {
            short[] sArrMultVect = this.cf.multVect(sArr2[i2], sArr);
            for (int i3 = 0; i3 < sArr.length; i3++) {
                for (int i4 = 0; i4 < sArr3.length; i4++) {
                    short s = sArrMultVect[i3];
                    if (s != 0) {
                        sArr4[s][i4] = GF2Field.addElem(sArr4[s][i4], sArr3[i4][i2][i3]);
                    }
                }
            }
        }
        return sArr4;
    }

    private short[] add_and_reduce(short[][] sArr) {
        int m = this.params.getM();
        short[] sArrAddVect = new short[m];
        for (int i = 0; i < 8; i++) {
            int iPow = (int) Math.pow(2.0d, i);
            short[] sArrAddVect2 = new short[m];
            int i2 = iPow;
            while (true) {
                int i3 = i2;
                if (i3 < 256) {
                    for (int i4 = 0; i4 < iPow; i4++) {
                        sArrAddVect2 = this.cf.addVect(sArrAddVect2, sArr[i3 + i4]);
                    }
                    i2 = i3 + (iPow * 2);
                }
            }
            sArrAddVect = this.cf.addVect(sArrAddVect, this.cf.multVect((short) iPow, sArrAddVect2));
        }
        return sArrAddVect;
    }

    public short[] publicMap(RainbowPublicKeyParameters rainbowPublicKeyParameters, short[] sArr) {
        return add_and_reduce(compute_accumulator(sArr, sArr, rainbowPublicKeyParameters.pk, this.params.getM()));
    }

    public short[] publicMap_cyclic(RainbowPublicKeyParameters rainbowPublicKeyParameters, short[] sArr) {
        int v1 = this.params.getV1();
        int o1 = this.params.getO1();
        int o2 = this.params.getO2();
        short[][] sArr2 = new short[256][o1 + o2];
        short[] sArrCopyOfRange = Arrays.copyOfRange(sArr, 0, v1);
        short[] sArrCopyOfRange2 = Arrays.copyOfRange(sArr, v1, v1 + o1);
        short[] sArrCopyOfRange3 = Arrays.copyOfRange(sArr, v1 + o1, sArr.length);
        RainbowDRBG rainbowDRBG = new RainbowDRBG(rainbowPublicKeyParameters.pk_seed, rainbowPublicKeyParameters.getParameters().getHash_algo());
        short[][] sArrAddMatrix = this.cf.addMatrix(this.cf.addMatrix(this.cf.addMatrix(this.cf.addMatrix(this.cf.addMatrix(compute_accumulator(sArrCopyOfRange, sArrCopyOfRange, RainbowUtil.generate_random(rainbowDRBG, o1, v1, v1, true), o1), compute_accumulator(sArrCopyOfRange2, sArrCopyOfRange, RainbowUtil.generate_random(rainbowDRBG, o1, v1, o1, false), o1)), compute_accumulator(sArrCopyOfRange3, sArrCopyOfRange, rainbowPublicKeyParameters.l1_Q3, o1)), compute_accumulator(sArrCopyOfRange2, sArrCopyOfRange2, rainbowPublicKeyParameters.l1_Q5, o1)), compute_accumulator(sArrCopyOfRange3, sArrCopyOfRange2, rainbowPublicKeyParameters.l1_Q6, o1)), compute_accumulator(sArrCopyOfRange3, sArrCopyOfRange3, rainbowPublicKeyParameters.l1_Q9, o1));
        short[][] sArrAddMatrix2 = this.cf.addMatrix(this.cf.addMatrix(this.cf.addMatrix(this.cf.addMatrix(this.cf.addMatrix(compute_accumulator(sArrCopyOfRange, sArrCopyOfRange, RainbowUtil.generate_random(rainbowDRBG, o2, v1, v1, true), o2), compute_accumulator(sArrCopyOfRange2, sArrCopyOfRange, RainbowUtil.generate_random(rainbowDRBG, o2, v1, o1, false), o2)), compute_accumulator(sArrCopyOfRange3, sArrCopyOfRange, RainbowUtil.generate_random(rainbowDRBG, o2, v1, o2, false), o2)), compute_accumulator(sArrCopyOfRange2, sArrCopyOfRange2, RainbowUtil.generate_random(rainbowDRBG, o2, o1, o1, true), o2)), compute_accumulator(sArrCopyOfRange3, sArrCopyOfRange2, RainbowUtil.generate_random(rainbowDRBG, o2, o1, o2, false), o2)), compute_accumulator(sArrCopyOfRange3, sArrCopyOfRange3, rainbowPublicKeyParameters.l2_Q9, o2));
        for (int i = 0; i < 256; i++) {
            sArr2[i] = Arrays.concatenate(sArrAddMatrix[i], sArrAddMatrix2[i]);
        }
        return add_and_reduce(sArr2);
    }
}
