package org.spongycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.pqc.crypto.rainbow.util.ComputeInField;
import org.spongycastle.pqc.crypto.rainbow.util.GF2Field;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/rainbow/RainbowKeyPairGenerator.class */
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
        RainbowPrivateKeyParameters privKey = new RainbowPrivateKeyParameters(this.A1inv, this.b1, this.A2inv, this.b2, this.vi, this.layers);
        RainbowPublicKeyParameters pubKey = new RainbowPublicKeyParameters(this.vi[this.vi.length - 1] - this.vi[0], this.pub_quadratic, this.pub_singular, this.pub_scalar);
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) pubKey, (AsymmetricKeyParameter) privKey);
    }

    public void initialize(KeyGenerationParameters param) {
        this.rainbowParams = (RainbowKeyGenerationParameters) param;
        this.sr = this.rainbowParams.getRandom();
        this.vi = this.rainbowParams.getParameters().getVi();
        this.numOfLayers = this.rainbowParams.getParameters().getNumOfLayers();
        this.initialized = true;
    }

    private void initializeDefault() {
        RainbowKeyGenerationParameters rbKGParams = new RainbowKeyGenerationParameters(new SecureRandom(), new RainbowParameters());
        initialize(rbKGParams);
    }

    private void keygen() {
        generateL1();
        generateL2();
        generateF();
        computePublicKey();
    }

    private void generateL1() {
        int dim = this.vi[this.vi.length - 1] - this.vi[0];
        this.A1 = new short[dim][dim];
        this.A1inv = (short[][]) null;
        ComputeInField c = new ComputeInField();
        while (this.A1inv == null) {
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    this.A1[i][j] = (short) (this.sr.nextInt() & 255);
                }
            }
            this.A1inv = c.inverse(this.A1);
        }
        this.b1 = new short[dim];
        for (int i2 = 0; i2 < dim; i2++) {
            this.b1[i2] = (short) (this.sr.nextInt() & 255);
        }
    }

    private void generateL2() {
        int dim = this.vi[this.vi.length - 1];
        this.A2 = new short[dim][dim];
        this.A2inv = (short[][]) null;
        ComputeInField c = new ComputeInField();
        while (this.A2inv == null) {
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    this.A2[i][j] = (short) (this.sr.nextInt() & 255);
                }
            }
            this.A2inv = c.inverse(this.A2);
        }
        this.b2 = new short[dim];
        for (int i2 = 0; i2 < dim; i2++) {
            this.b2[i2] = (short) (this.sr.nextInt() & 255);
        }
    }

    private void generateF() {
        this.layers = new Layer[this.numOfLayers];
        for (int i = 0; i < this.numOfLayers; i++) {
            this.layers[i] = new Layer(this.vi[i], this.vi[i + 1], this.sr);
        }
    }

    private void computePublicKey() {
        ComputeInField c = new ComputeInField();
        int rows = this.vi[this.vi.length - 1] - this.vi[0];
        int vars = this.vi[this.vi.length - 1];
        short[][][] coeff_quadratic_3dim = new short[rows][vars][vars];
        this.pub_singular = new short[rows][vars];
        this.pub_scalar = new short[rows];
        int crnt_row = 0;
        short[] sArr = new short[vars];
        for (int l = 0; l < this.layers.length; l++) {
            short[][][] coeff_alpha = this.layers[l].getCoeffAlpha();
            short[][][] coeff_beta = this.layers[l].getCoeffBeta();
            short[][] coeff_gamma = this.layers[l].getCoeffGamma();
            short[] coeff_eta = this.layers[l].getCoeffEta();
            int oils = coeff_alpha[0].length;
            int vins = coeff_beta[0].length;
            for (int p = 0; p < oils; p++) {
                for (int x1 = 0; x1 < oils; x1++) {
                    for (int x2 = 0; x2 < vins; x2++) {
                        short[] vect_tmp = c.multVect(coeff_alpha[p][x1][x2], this.A2[x1 + vins]);
                        coeff_quadratic_3dim[crnt_row + p] = c.addSquareMatrix(coeff_quadratic_3dim[crnt_row + p], c.multVects(vect_tmp, this.A2[x2]));
                        this.pub_singular[crnt_row + p] = c.addVect(c.multVect(this.b2[x2], vect_tmp), this.pub_singular[crnt_row + p]);
                        this.pub_singular[crnt_row + p] = c.addVect(c.multVect(this.b2[x1 + vins], c.multVect(coeff_alpha[p][x1][x2], this.A2[x2])), this.pub_singular[crnt_row + p]);
                        short sclr_tmp = GF2Field.multElem(coeff_alpha[p][x1][x2], this.b2[x1 + vins]);
                        this.pub_scalar[crnt_row + p] = GF2Field.addElem(this.pub_scalar[crnt_row + p], GF2Field.multElem(sclr_tmp, this.b2[x2]));
                    }
                }
                for (int x3 = 0; x3 < vins; x3++) {
                    for (int x4 = 0; x4 < vins; x4++) {
                        short[] vect_tmp2 = c.multVect(coeff_beta[p][x3][x4], this.A2[x3]);
                        coeff_quadratic_3dim[crnt_row + p] = c.addSquareMatrix(coeff_quadratic_3dim[crnt_row + p], c.multVects(vect_tmp2, this.A2[x4]));
                        this.pub_singular[crnt_row + p] = c.addVect(c.multVect(this.b2[x4], vect_tmp2), this.pub_singular[crnt_row + p]);
                        this.pub_singular[crnt_row + p] = c.addVect(c.multVect(this.b2[x3], c.multVect(coeff_beta[p][x3][x4], this.A2[x4])), this.pub_singular[crnt_row + p]);
                        short sclr_tmp2 = GF2Field.multElem(coeff_beta[p][x3][x4], this.b2[x3]);
                        this.pub_scalar[crnt_row + p] = GF2Field.addElem(this.pub_scalar[crnt_row + p], GF2Field.multElem(sclr_tmp2, this.b2[x4]));
                    }
                }
                for (int n = 0; n < vins + oils; n++) {
                    this.pub_singular[crnt_row + p] = c.addVect(c.multVect(coeff_gamma[p][n], this.A2[n]), this.pub_singular[crnt_row + p]);
                    this.pub_scalar[crnt_row + p] = GF2Field.addElem(this.pub_scalar[crnt_row + p], GF2Field.multElem(coeff_gamma[p][n], this.b2[n]));
                }
                this.pub_scalar[crnt_row + p] = GF2Field.addElem(this.pub_scalar[crnt_row + p], coeff_eta[p]);
            }
            crnt_row += oils;
        }
        short[][][] tmp_c_quad = new short[rows][vars][vars];
        short[][] tmp_c_sing = new short[rows][vars];
        short[] tmp_c_scal = new short[rows];
        for (int r = 0; r < rows; r++) {
            for (int q = 0; q < this.A1.length; q++) {
                tmp_c_quad[r] = c.addSquareMatrix(tmp_c_quad[r], c.multMatrix(this.A1[r][q], coeff_quadratic_3dim[q]));
                tmp_c_sing[r] = c.addVect(tmp_c_sing[r], c.multVect(this.A1[r][q], this.pub_singular[q]));
                tmp_c_scal[r] = GF2Field.addElem(tmp_c_scal[r], GF2Field.multElem(this.A1[r][q], this.pub_scalar[q]));
            }
            tmp_c_scal[r] = GF2Field.addElem(tmp_c_scal[r], this.b1[r]);
        }
        this.pub_singular = tmp_c_sing;
        this.pub_scalar = tmp_c_scal;
        compactPublicKey(tmp_c_quad);
    }

    private void compactPublicKey(short[][][] coeff_quadratic_to_compact) {
        int polynomials = coeff_quadratic_to_compact.length;
        int n = coeff_quadratic_to_compact[0].length;
        int entries = (n * (n + 1)) / 2;
        this.pub_quadratic = new short[polynomials][entries];
        for (int p = 0; p < polynomials; p++) {
            int offset = 0;
            for (int x = 0; x < n; x++) {
                for (int y = x; y < n; y++) {
                    if (y == x) {
                        this.pub_quadratic[p][offset] = coeff_quadratic_to_compact[p][x][y];
                    } else {
                        this.pub_quadratic[p][offset] = GF2Field.addElem(coeff_quadratic_to_compact[p][x][y], coeff_quadratic_to_compact[p][y][x]);
                    }
                    offset++;
                }
            }
        }
    }

    @Override // org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters param) {
        initialize(param);
    }

    @Override // org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        return genKeyPair();
    }
}
