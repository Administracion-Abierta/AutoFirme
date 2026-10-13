package org.spongycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.pqc.crypto.MessageSigner;
import org.spongycastle.pqc.crypto.rainbow.util.ComputeInField;
import org.spongycastle.pqc.crypto.rainbow.util.GF2Field;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/rainbow/RainbowSigner.class */
public class RainbowSigner implements MessageSigner {
    private static final int MAXITS = 65536;
    private SecureRandom random;
    int signableDocumentLength;
    private short[] x;
    private ComputeInField cf = new ComputeInField();
    RainbowKeyParameters key;

    @Override // org.spongycastle.pqc.crypto.MessageSigner
    public void init(boolean forSigning, CipherParameters param) {
        if (forSigning) {
            if (param instanceof ParametersWithRandom) {
                ParametersWithRandom rParam = (ParametersWithRandom) param;
                this.random = rParam.getRandom();
                this.key = (RainbowPrivateKeyParameters) rParam.getParameters();
            } else {
                this.random = new SecureRandom();
                this.key = (RainbowPrivateKeyParameters) param;
            }
        } else {
            this.key = (RainbowPublicKeyParameters) param;
        }
        this.signableDocumentLength = this.key.getDocLength();
    }

    private short[] initSign(Layer[] layer, short[] msg) {
        short[] sArr = new short[msg.length];
        short[] tmpVec = this.cf.addVect(((RainbowPrivateKeyParameters) this.key).getB1(), msg);
        short[] Y_ = this.cf.multiplyMatrix(((RainbowPrivateKeyParameters) this.key).getInvA1(), tmpVec);
        for (int i = 0; i < layer[0].getVi(); i++) {
            this.x[i] = (short) this.random.nextInt();
            this.x[i] = (short) (this.x[i] & 255);
        }
        return Y_;
    }

    @Override // org.spongycastle.pqc.crypto.MessageSigner
    public byte[] generateSignature(byte[] message) {
        Layer[] layer = ((RainbowPrivateKeyParameters) this.key).getLayers();
        int numberOfLayers = layer.length;
        this.x = new short[((RainbowPrivateKeyParameters) this.key).getInvA2().length];
        byte[] S = new byte[layer[numberOfLayers - 1].getViNext()];
        short[] msgHashVals = makeMessageRepresentative(message);
        int itCount = 0;
        do {
            boolean ok = true;
            int counter = 0;
            try {
                short[] Y_ = initSign(layer, msgHashVals);
                for (int i = 0; i < numberOfLayers; i++) {
                    short[] y_i = new short[layer[i].getOi()];
                    short[] sArr = new short[layer[i].getOi()];
                    for (int k = 0; k < layer[i].getOi(); k++) {
                        y_i[k] = Y_[counter];
                        counter++;
                    }
                    short[] solVec = this.cf.solveEquation(layer[i].plugInVinegars(this.x), y_i);
                    if (solVec == null) {
                        throw new Exception("LES is not solveable!");
                    }
                    for (int j = 0; j < solVec.length; j++) {
                        this.x[layer[i].getVi() + j] = solVec[j];
                    }
                }
                short[] tmpVec = this.cf.addVect(((RainbowPrivateKeyParameters) this.key).getB2(), this.x);
                short[] signature = this.cf.multiplyMatrix(((RainbowPrivateKeyParameters) this.key).getInvA2(), tmpVec);
                for (int i2 = 0; i2 < S.length; i2++) {
                    S[i2] = (byte) signature[i2];
                }
            } catch (Exception e) {
                ok = false;
            }
            if (ok) {
                break;
            }
            itCount++;
        } while (itCount < 65536);
        if (itCount == 65536) {
            throw new IllegalStateException("unable to generate signature - LES not solvable");
        }
        return S;
    }

    @Override // org.spongycastle.pqc.crypto.MessageSigner
    public boolean verifySignature(byte[] message, byte[] signature) {
        short[] sigInt = new short[signature.length];
        for (int i = 0; i < signature.length; i++) {
            short tmp = signature[i];
            sigInt[i] = (short) (tmp & 255);
        }
        short[] msgHashVal = makeMessageRepresentative(message);
        short[] verificationResult = verifySignatureIntern(sigInt);
        boolean verified = true;
        if (msgHashVal.length != verificationResult.length) {
            return false;
        }
        for (int i2 = 0; i2 < msgHashVal.length; i2++) {
            verified = verified && msgHashVal[i2] == verificationResult[i2];
        }
        return verified;
    }

    private short[] verifySignatureIntern(short[] signature) {
        short[][] coeff_quadratic = ((RainbowPublicKeyParameters) this.key).getCoeffQuadratic();
        short[][] coeff_singular = ((RainbowPublicKeyParameters) this.key).getCoeffSingular();
        short[] coeff_scalar = ((RainbowPublicKeyParameters) this.key).getCoeffScalar();
        short[] rslt = new short[coeff_quadratic.length];
        int n = coeff_singular[0].length;
        for (int p = 0; p < coeff_quadratic.length; p++) {
            int offset = 0;
            for (int x = 0; x < n; x++) {
                for (int y = x; y < n; y++) {
                    short tmp = GF2Field.multElem(coeff_quadratic[p][offset], GF2Field.multElem(signature[x], signature[y]));
                    rslt[p] = GF2Field.addElem(rslt[p], tmp);
                    offset++;
                }
                short tmp2 = GF2Field.multElem(coeff_singular[p][x], signature[x]);
                rslt[p] = GF2Field.addElem(rslt[p], tmp2);
            }
            rslt[p] = GF2Field.addElem(rslt[p], coeff_scalar[p]);
        }
        return rslt;
    }

    private short[] makeMessageRepresentative(byte[] message) {
        short[] output = new short[this.signableDocumentLength];
        int h = 0;
        int i = 0;
        while (i < message.length) {
            output[i] = message[h];
            int i2 = i;
            output[i2] = (short) (output[i2] & 255);
            h++;
            i++;
            if (i >= output.length) {
                break;
            }
        }
        return output;
    }
}
