package org.spongycastle.pqc.jcajce.provider.mceliece;

import java.io.IOException;
import java.security.PrivateKey;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.pqc.asn1.McEliecePrivateKey;
import org.spongycastle.pqc.asn1.PQCObjectIdentifiers;
import org.spongycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;
import org.spongycastle.pqc.math.linearalgebra.GF2Matrix;
import org.spongycastle.pqc.math.linearalgebra.GF2mField;
import org.spongycastle.pqc.math.linearalgebra.Permutation;
import org.spongycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/mceliece/BCMcEliecePrivateKey.class */
public class BCMcEliecePrivateKey implements CipherParameters, PrivateKey {
    private static final long serialVersionUID = 1;
    private McEliecePrivateKeyParameters params;

    public BCMcEliecePrivateKey(McEliecePrivateKeyParameters params) {
        this.params = params;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "McEliece";
    }

    public int getN() {
        return this.params.getN();
    }

    public int getK() {
        return this.params.getK();
    }

    public GF2mField getField() {
        return this.params.getField();
    }

    public PolynomialGF2mSmallM getGoppaPoly() {
        return this.params.getGoppaPoly();
    }

    public GF2Matrix getSInv() {
        return this.params.getSInv();
    }

    public Permutation getP1() {
        return this.params.getP1();
    }

    public Permutation getP2() {
        return this.params.getP2();
    }

    public GF2Matrix getH() {
        return this.params.getH();
    }

    public PolynomialGF2mSmallM[] getQInv() {
        return this.params.getQInv();
    }

    public boolean equals(Object other) {
        if (!(other instanceof BCMcEliecePrivateKey)) {
            return false;
        }
        BCMcEliecePrivateKey otherKey = (BCMcEliecePrivateKey) other;
        return getN() == otherKey.getN() && getK() == otherKey.getK() && getField().equals(otherKey.getField()) && getGoppaPoly().equals(otherKey.getGoppaPoly()) && getSInv().equals(otherKey.getSInv()) && getP1().equals(otherKey.getP1()) && getP2().equals(otherKey.getP2());
    }

    public int hashCode() {
        int code = this.params.getK();
        return (((((((((((code * 37) + this.params.getN()) * 37) + this.params.getField().hashCode()) * 37) + this.params.getGoppaPoly().hashCode()) * 37) + this.params.getP1().hashCode()) * 37) + this.params.getP2().hashCode()) * 37) + this.params.getSInv().hashCode();
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        McEliecePrivateKey privateKey = new McEliecePrivateKey(this.params.getN(), this.params.getK(), this.params.getField(), this.params.getGoppaPoly(), this.params.getP1(), this.params.getP2(), this.params.getSInv());
        try {
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.mcEliece);
            PrivateKeyInfo pki = new PrivateKeyInfo(algorithmIdentifier, privateKey);
            try {
                byte[] encoded = pki.getEncoded();
                return encoded;
            } catch (IOException e) {
                return null;
            }
        } catch (IOException e2) {
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
