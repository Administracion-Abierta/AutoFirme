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

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/mceliece/McElieceCCA2KeyPairGenerator.class */
public class McElieceCCA2KeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    public static final String OID = "1.3.6.1.4.1.8301.3.1.3.4.2";
    private McElieceCCA2KeyGenerationParameters mcElieceCCA2Params;
    private int m;
    private int n;
    private int t;
    private int fieldPoly;
    private SecureRandom random;
    private boolean initialized = false;

    private void initializeDefault() {
        McElieceCCA2KeyGenerationParameters mcCCA2Params = new McElieceCCA2KeyGenerationParameters(new SecureRandom(), new McElieceCCA2Parameters());
        init(mcCCA2Params);
    }

    @Override // org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters param) {
        this.mcElieceCCA2Params = (McElieceCCA2KeyGenerationParameters) param;
        this.random = new SecureRandom();
        this.m = this.mcElieceCCA2Params.getParameters().getM();
        this.n = this.mcElieceCCA2Params.getParameters().getN();
        this.t = this.mcElieceCCA2Params.getParameters().getT();
        this.fieldPoly = this.mcElieceCCA2Params.getParameters().getFieldPoly();
        this.initialized = true;
    }

    @Override // org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        if (!this.initialized) {
            initializeDefault();
        }
        GF2mField field = new GF2mField(this.m, this.fieldPoly);
        PolynomialGF2mSmallM gp = new PolynomialGF2mSmallM(field, this.t, 'I', this.random);
        GF2Matrix h = GoppaCode.createCanonicalCheckMatrix(field, gp);
        GoppaCode.MaMaPe mmp = GoppaCode.computeSystematicForm(h, this.random);
        GF2Matrix shortH = mmp.getSecondMatrix();
        Permutation p = mmp.getPermutation();
        GF2Matrix shortG = (GF2Matrix) shortH.computeTranspose();
        int k = shortG.getNumRows();
        McElieceCCA2PublicKeyParameters pubKey = new McElieceCCA2PublicKeyParameters(this.n, this.t, shortG, this.mcElieceCCA2Params.getParameters().getDigest());
        McElieceCCA2PrivateKeyParameters privKey = new McElieceCCA2PrivateKeyParameters(this.n, k, field, gp, p, this.mcElieceCCA2Params.getParameters().getDigest());
        return new AsymmetricCipherKeyPair((AsymmetricKeyParameter) pubKey, (AsymmetricKeyParameter) privKey);
    }
}
