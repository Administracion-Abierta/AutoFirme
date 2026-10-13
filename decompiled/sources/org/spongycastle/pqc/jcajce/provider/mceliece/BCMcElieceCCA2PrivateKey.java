package org.spongycastle.pqc.jcajce.provider.mceliece;

import java.io.IOException;
import java.security.PrivateKey;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.pqc.asn1.McElieceCCA2PrivateKey;
import org.spongycastle.pqc.asn1.PQCObjectIdentifiers;
import org.spongycastle.pqc.crypto.mceliece.McElieceCCA2PrivateKeyParameters;
import org.spongycastle.pqc.math.linearalgebra.GF2Matrix;
import org.spongycastle.pqc.math.linearalgebra.GF2mField;
import org.spongycastle.pqc.math.linearalgebra.Permutation;
import org.spongycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/mceliece/BCMcElieceCCA2PrivateKey.class */
public class BCMcElieceCCA2PrivateKey implements PrivateKey {
    private static final long serialVersionUID = 1;
    private McElieceCCA2PrivateKeyParameters params;

    public BCMcElieceCCA2PrivateKey(McElieceCCA2PrivateKeyParameters params) {
        this.params = params;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "McEliece-CCA2";
    }

    public int getN() {
        return this.params.getN();
    }

    public int getK() {
        return this.params.getK();
    }

    public int getT() {
        return this.params.getGoppaPoly().getDegree();
    }

    public GF2mField getField() {
        return this.params.getField();
    }

    public PolynomialGF2mSmallM getGoppaPoly() {
        return this.params.getGoppaPoly();
    }

    public Permutation getP() {
        return this.params.getP();
    }

    public GF2Matrix getH() {
        return this.params.getH();
    }

    public PolynomialGF2mSmallM[] getQInv() {
        return this.params.getQInv();
    }

    public boolean equals(Object other) {
        if (other == null || !(other instanceof BCMcElieceCCA2PrivateKey)) {
            return false;
        }
        BCMcElieceCCA2PrivateKey otherKey = (BCMcElieceCCA2PrivateKey) other;
        return getN() == otherKey.getN() && getK() == otherKey.getK() && getField().equals(otherKey.getField()) && getGoppaPoly().equals(otherKey.getGoppaPoly()) && getP().equals(otherKey.getP()) && getH().equals(otherKey.getH());
    }

    public int hashCode() {
        int code = this.params.getK();
        return (((((((((code * 37) + this.params.getN()) * 37) + this.params.getField().hashCode()) * 37) + this.params.getGoppaPoly().hashCode()) * 37) + this.params.getP().hashCode()) * 37) + this.params.getH().hashCode();
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        try {
            McElieceCCA2PrivateKey privateKey = new McElieceCCA2PrivateKey(getN(), getK(), getField(), getGoppaPoly(), getP(), Utils.getDigAlgId(this.params.getDigest()));
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.mcElieceCca2);
            PrivateKeyInfo pki = new PrivateKeyInfo(algorithmIdentifier, privateKey);
            return pki.getEncoded();
        } catch (IOException e) {
            return null;
        }
    }

    @Override // java.security.Key
    public String getFormat() {
        return "PKCS#8";
    }

    AsymmetricKeyParameter getKeyParams() {
        return this.params;
    }
}
