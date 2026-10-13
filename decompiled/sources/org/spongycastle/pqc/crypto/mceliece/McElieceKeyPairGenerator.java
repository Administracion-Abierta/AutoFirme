package org.spongycastle.pqc.crypto.mceliece;

import java.security.SecureRandom;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.pqc.math.linearalgebra.GF2Matrix;
import org.spongycastle.pqc.math.linearalgebra.GF2mField;
import org.spongycastle.pqc.math.linearalgebra.GoppaCode;
import org.spongycastle.pqc.math.linearalgebra.Permutation;
import org.spongycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;
import org.spongycastle.pqc.math.linearalgebra.PolynomialRingGF2m;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/mceliece/McElieceKeyPairGenerator.class */
public class McElieceKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private static final String OID = "1.3.6.1.4.1.8301.3.1.3.4.1";
    private McElieceKeyGenerationParameters mcElieceParams;
    private int m;
    private int n;
    private int t;
    private int fieldPoly;
    private SecureRandom random;
    private boolean initialized = false;

    private void initializeDefault() {
        McElieceKeyGenerationParameters mcParams = new McElieceKeyGenerationParameters(new SecureRandom(), new McElieceParameters());
        initialize(mcParams);
    }

    private void initialize(KeyGenerationParameters param) {
        this.mcElieceParams = (McElieceKeyGenerationParameters) param;
        this.random = new SecureRandom();
        this.m = this.mcElieceParams.getParameters().getM();
        this.n = this.mcElieceParams.getParameters().getN();
        this.t = this.mcElieceParams.getParameters().getT();
        this.fieldPoly = this.mcElieceParams.getParameters().getFieldPoly();
        this.initialized = true;
    }

    private AsymmetricCipherKeyPair genKeyPair() {
        if (!this.initialized) {
            initializeDefault();
        }
        GF2mField field = new GF2mField(this.m, this.fieldPoly);
        PolynomialGF2mSmallM gp = new PolynomialGF2mSmallM(field, this.t, 'I', this.random);
        PolynomialRingGF2m ring = new PolynomialRingGF2m(field, gp);
        ring.getSquareRootMatrix();
        GF2Matrix h = GoppaCode.createCanonicalCheckMatrix(field, gp);
        GoppaCode.MaMaPe mmp = GoppaCode.computeSystematicForm(h, this.random);
        GF2Matrix shortH = mmp.getSecondMatrix();
        Permutation p1 = mmp.getPermutation();
        GF2Matrix shortG = (GF2Matrix) shortH.computeTranspose();
        GF2Matrix gPrime = shortG.extendLeftCompactForm();
        int k = shortG.getNumRows();
        GF2Matrix[] matrixSandInverse = GF2Matrix.createRandomRegularMatrixAndItsInverse(k, this.random);
        Permutation p2 = new Permutation(this.n, this.random);
        GF2Matrix g = (GF2Matrix) matrixSandInverse[0].rightMultiply(gPrime);
        McEliecePublicKeyParameters pubKey = new McEliecePublicKeyParameters(this.n, this.t, (GF2Matrix) g.rightMultiply(p2));
        McEliecePrivateKeyParameters privKey = new McEliecePrivateKeyParameters(this.n, k, field, gp, p1, p2, matrixSandInverse[1]);
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) pubKey, (AsymmetricKeyParameter) privKey);
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
