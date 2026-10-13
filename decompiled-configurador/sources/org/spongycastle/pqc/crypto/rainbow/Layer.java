package org.spongycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.spongycastle.pqc.crypto.rainbow.util.GF2Field;
import org.spongycastle.pqc.crypto.rainbow.util.RainbowUtil;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/rainbow/Layer.class */
public class Layer {
    private int vi;
    private int viNext;
    private int oi;
    private short[][][] coeff_alpha;
    private short[][][] coeff_beta;
    private short[][] coeff_gamma;
    private short[] coeff_eta;

    public Layer(byte vi, byte viNext, short[][][] coeffAlpha, short[][][] coeffBeta, short[][] coeffGamma, short[] coeffEta) {
        this.vi = vi & 255;
        this.viNext = viNext & 255;
        this.oi = this.viNext - this.vi;
        this.coeff_alpha = coeffAlpha;
        this.coeff_beta = coeffBeta;
        this.coeff_gamma = coeffGamma;
        this.coeff_eta = coeffEta;
    }

    public Layer(int vi, int viNext, SecureRandom sr) {
        this.vi = vi;
        this.viNext = viNext;
        this.oi = viNext - vi;
        this.coeff_alpha = new short[this.oi][this.oi][this.vi];
        this.coeff_beta = new short[this.oi][this.vi][this.vi];
        this.coeff_gamma = new short[this.oi][this.viNext];
        this.coeff_eta = new short[this.oi];
        int numOfPoly = this.oi;
        for (int k = 0; k < numOfPoly; k++) {
            for (int i = 0; i < this.oi; i++) {
                for (int j = 0; j < this.vi; j++) {
                    this.coeff_alpha[k][i][j] = (short) (sr.nextInt() & 255);
                }
            }
        }
        for (int k2 = 0; k2 < numOfPoly; k2++) {
            for (int i2 = 0; i2 < this.vi; i2++) {
                for (int j2 = 0; j2 < this.vi; j2++) {
                    this.coeff_beta[k2][i2][j2] = (short) (sr.nextInt() & 255);
                }
            }
        }
        for (int k3 = 0; k3 < numOfPoly; k3++) {
            for (int i3 = 0; i3 < this.viNext; i3++) {
                this.coeff_gamma[k3][i3] = (short) (sr.nextInt() & 255);
            }
        }
        for (int k4 = 0; k4 < numOfPoly; k4++) {
            this.coeff_eta[k4] = (short) (sr.nextInt() & 255);
        }
    }

    public short[][] plugInVinegars(short[] x) {
        short[][] coeff = new short[this.oi][this.oi + 1];
        short[] sum = new short[this.oi];
        for (int k = 0; k < this.oi; k++) {
            for (int i = 0; i < this.vi; i++) {
                for (int j = 0; j < this.vi; j++) {
                    short tmpMult = GF2Field.multElem(this.coeff_beta[k][i][j], x[i]);
                    sum[k] = GF2Field.addElem(sum[k], GF2Field.multElem(tmpMult, x[j]));
                }
            }
        }
        for (int k2 = 0; k2 < this.oi; k2++) {
            for (int i2 = 0; i2 < this.oi; i2++) {
                for (int j2 = 0; j2 < this.vi; j2++) {
                    short tmpMult2 = GF2Field.multElem(this.coeff_alpha[k2][i2][j2], x[j2]);
                    coeff[k2][i2] = GF2Field.addElem(coeff[k2][i2], tmpMult2);
                }
            }
        }
        for (int k3 = 0; k3 < this.oi; k3++) {
            for (int i3 = 0; i3 < this.vi; i3++) {
                short tmpMult3 = GF2Field.multElem(this.coeff_gamma[k3][i3], x[i3]);
                sum[k3] = GF2Field.addElem(sum[k3], tmpMult3);
            }
        }
        for (int k4 = 0; k4 < this.oi; k4++) {
            for (int i4 = this.vi; i4 < this.viNext; i4++) {
                coeff[k4][i4 - this.vi] = GF2Field.addElem(this.coeff_gamma[k4][i4], coeff[k4][i4 - this.vi]);
            }
        }
        for (int k5 = 0; k5 < this.oi; k5++) {
            sum[k5] = GF2Field.addElem(sum[k5], this.coeff_eta[k5]);
        }
        for (int k6 = 0; k6 < this.oi; k6++) {
            coeff[k6][this.oi] = sum[k6];
        }
        return coeff;
    }

    public int getVi() {
        return this.vi;
    }

    public int getViNext() {
        return this.viNext;
    }

    public int getOi() {
        return this.oi;
    }

    public short[][][] getCoeffAlpha() {
        return this.coeff_alpha;
    }

    public short[][][] getCoeffBeta() {
        return this.coeff_beta;
    }

    public short[][] getCoeffGamma() {
        return this.coeff_gamma;
    }

    public short[] getCoeffEta() {
        return this.coeff_eta;
    }

    public boolean equals(Object other) {
        if (other == null || !(other instanceof Layer)) {
            return false;
        }
        Layer otherLayer = (Layer) other;
        return this.vi == otherLayer.getVi() && this.viNext == otherLayer.getViNext() && this.oi == otherLayer.getOi() && RainbowUtil.equals(this.coeff_alpha, otherLayer.getCoeffAlpha()) && RainbowUtil.equals(this.coeff_beta, otherLayer.getCoeffBeta()) && RainbowUtil.equals(this.coeff_gamma, otherLayer.getCoeffGamma()) && RainbowUtil.equals(this.coeff_eta, otherLayer.getCoeffEta());
    }

    public int hashCode() {
        int hash = this.vi;
        return (((((((((((hash * 37) + this.viNext) * 37) + this.oi) * 37) + Arrays.hashCode(this.coeff_alpha)) * 37) + Arrays.hashCode(this.coeff_beta)) * 37) + Arrays.hashCode(this.coeff_gamma)) * 37) + Arrays.hashCode(this.coeff_eta);
    }
}
