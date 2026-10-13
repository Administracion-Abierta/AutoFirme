package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/rainbow/RainbowPublicKeyParameters.class */
public class RainbowPublicKeyParameters extends RainbowKeyParameters {
    short[][][] pk;
    byte[] pk_seed;
    short[][][] l1_Q3;
    short[][][] l1_Q5;
    short[][][] l1_Q6;
    short[][][] l1_Q9;
    short[][][] l2_Q9;

    RainbowPublicKeyParameters(RainbowParameters rainbowParameters, short[][][] sArr, short[][][] sArr2, short[][][] sArr3, short[][][] sArr4, short[][][] sArr5, short[][][] sArr6, short[][][] sArr7, short[][][] sArr8, short[][][] sArr9, short[][][] sArr10, short[][][] sArr11, short[][][] sArr12) {
        super(false, rainbowParameters);
        int v1 = rainbowParameters.getV1();
        int o1 = rainbowParameters.getO1();
        int o2 = rainbowParameters.getO2();
        this.pk = new short[rainbowParameters.getM()][rainbowParameters.getN()][rainbowParameters.getN()];
        for (int i = 0; i < o1; i++) {
            for (int i2 = 0; i2 < v1; i2++) {
                System.arraycopy(sArr[i][i2], 0, this.pk[i][i2], 0, v1);
                System.arraycopy(sArr2[i][i2], 0, this.pk[i][i2], v1, o1);
                System.arraycopy(sArr3[i][i2], 0, this.pk[i][i2], v1 + o1, o2);
            }
            for (int i3 = 0; i3 < o1; i3++) {
                System.arraycopy(sArr4[i][i3], 0, this.pk[i][i3 + v1], v1, o1);
                System.arraycopy(sArr5[i][i3], 0, this.pk[i][i3 + v1], v1 + o1, o2);
            }
            for (int i4 = 0; i4 < o2; i4++) {
                System.arraycopy(sArr6[i][i4], 0, this.pk[i][i4 + v1 + o1], v1 + o1, o2);
            }
        }
        for (int i5 = 0; i5 < o2; i5++) {
            for (int i6 = 0; i6 < v1; i6++) {
                System.arraycopy(sArr7[i5][i6], 0, this.pk[i5 + o1][i6], 0, v1);
                System.arraycopy(sArr8[i5][i6], 0, this.pk[i5 + o1][i6], v1, o1);
                System.arraycopy(sArr9[i5][i6], 0, this.pk[i5 + o1][i6], v1 + o1, o2);
            }
            for (int i7 = 0; i7 < o1; i7++) {
                System.arraycopy(sArr10[i5][i7], 0, this.pk[i5 + o1][i7 + v1], v1, o1);
                System.arraycopy(sArr11[i5][i7], 0, this.pk[i5 + o1][i7 + v1], v1 + o1, o2);
            }
            for (int i8 = 0; i8 < o2; i8++) {
                System.arraycopy(sArr12[i5][i8], 0, this.pk[i5 + o1][i8 + v1 + o1], v1 + o1, o2);
            }
        }
    }

    RainbowPublicKeyParameters(RainbowParameters rainbowParameters, byte[] bArr, short[][][] sArr, short[][][] sArr2, short[][][] sArr3, short[][][] sArr4, short[][][] sArr5) {
        super(false, rainbowParameters);
        this.pk_seed = (byte[]) bArr.clone();
        this.l1_Q3 = RainbowUtil.cloneArray(sArr);
        this.l1_Q5 = RainbowUtil.cloneArray(sArr2);
        this.l1_Q6 = RainbowUtil.cloneArray(sArr3);
        this.l1_Q9 = RainbowUtil.cloneArray(sArr4);
        this.l2_Q9 = RainbowUtil.cloneArray(sArr5);
    }

    public RainbowPublicKeyParameters(RainbowParameters rainbowParameters, byte[] bArr) {
        super(false, rainbowParameters);
        int m = rainbowParameters.getM();
        int n = rainbowParameters.getN();
        if (getParameters().getVersion() != Version.CLASSIC) {
            this.pk_seed = Arrays.copyOfRange(bArr, 0, rainbowParameters.getLen_pkseed());
            this.l1_Q3 = new short[rainbowParameters.getO1()][rainbowParameters.getV1()][rainbowParameters.getO2()];
            this.l1_Q5 = new short[rainbowParameters.getO1()][rainbowParameters.getO1()][rainbowParameters.getO1()];
            this.l1_Q6 = new short[rainbowParameters.getO1()][rainbowParameters.getO1()][rainbowParameters.getO2()];
            this.l1_Q9 = new short[rainbowParameters.getO1()][rainbowParameters.getO2()][rainbowParameters.getO2()];
            this.l2_Q9 = new short[rainbowParameters.getO2()][rainbowParameters.getO2()][rainbowParameters.getO2()];
            int len_pkseed = rainbowParameters.getLen_pkseed();
            int iLoadEncoded = len_pkseed + RainbowUtil.loadEncoded(this.l1_Q3, bArr, len_pkseed, false);
            int iLoadEncoded2 = iLoadEncoded + RainbowUtil.loadEncoded(this.l1_Q5, bArr, iLoadEncoded, true);
            int iLoadEncoded3 = iLoadEncoded2 + RainbowUtil.loadEncoded(this.l1_Q6, bArr, iLoadEncoded2, false);
            int iLoadEncoded4 = iLoadEncoded3 + RainbowUtil.loadEncoded(this.l1_Q9, bArr, iLoadEncoded3, true);
            if (iLoadEncoded4 + RainbowUtil.loadEncoded(this.l2_Q9, bArr, iLoadEncoded4, true) != bArr.length) {
                throw new IllegalArgumentException("unparsed data in key encoding");
            }
            return;
        }
        this.pk = new short[m][n][n];
        int i = 0;
        for (int i2 = 0; i2 < n; i2++) {
            for (int i3 = 0; i3 < n; i3++) {
                for (int i4 = 0; i4 < m; i4++) {
                    if (i2 > i3) {
                        this.pk[i4][i2][i3] = 0;
                    } else {
                        this.pk[i4][i2][i3] = (short) (bArr[i] & 255);
                        i++;
                    }
                }
            }
        }
    }

    public short[][][] getPk() {
        return RainbowUtil.cloneArray(this.pk);
    }

    public byte[] getEncoded() {
        return getParameters().getVersion() != Version.CLASSIC ? Arrays.concatenate(Arrays.concatenate(Arrays.concatenate(Arrays.concatenate(Arrays.concatenate(this.pk_seed, RainbowUtil.getEncoded(this.l1_Q3, false)), RainbowUtil.getEncoded(this.l1_Q5, true)), RainbowUtil.getEncoded(this.l1_Q6, false)), RainbowUtil.getEncoded(this.l1_Q9, true)), RainbowUtil.getEncoded(this.l2_Q9, true)) : RainbowUtil.getEncoded(this.pk, true);
    }
}
