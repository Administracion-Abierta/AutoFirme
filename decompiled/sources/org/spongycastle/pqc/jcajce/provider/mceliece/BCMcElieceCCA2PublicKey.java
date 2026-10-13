package org.spongycastle.pqc.jcajce.provider.mceliece;

import java.io.IOException;
import java.security.PublicKey;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.pqc.asn1.McElieceCCA2PublicKey;
import org.spongycastle.pqc.asn1.PQCObjectIdentifiers;
import org.spongycastle.pqc.crypto.mceliece.McElieceCCA2PublicKeyParameters;
import org.spongycastle.pqc.math.linearalgebra.GF2Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/mceliece/BCMcElieceCCA2PublicKey.class */
public class BCMcElieceCCA2PublicKey implements CipherParameters, PublicKey {
    private static final long serialVersionUID = 1;
    private McElieceCCA2PublicKeyParameters params;

    public BCMcElieceCCA2PublicKey(McElieceCCA2PublicKeyParameters params) {
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
        return this.params.getT();
    }

    public GF2Matrix getG() {
        return this.params.getG();
    }

    public String toString() {
        String result = "McEliecePublicKey:\n length of the code         : " + this.params.getN() + "\n";
        return (result + " error correction capability: " + this.params.getT() + "\n") + " generator matrix           : " + this.params.getG().toString();
    }

    public boolean equals(Object other) {
        if (other == null || !(other instanceof BCMcElieceCCA2PublicKey)) {
            return false;
        }
        BCMcElieceCCA2PublicKey otherKey = (BCMcElieceCCA2PublicKey) other;
        return this.params.getN() == otherKey.getN() && this.params.getT() == otherKey.getT() && this.params.getG().equals(otherKey.getG());
    }

    public int hashCode() {
        return (37 * (this.params.getN() + (37 * this.params.getT()))) + this.params.getG().hashCode();
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        McElieceCCA2PublicKey key = new McElieceCCA2PublicKey(this.params.getN(), this.params.getT(), this.params.getG(), Utils.getDigAlgId(this.params.getDigest()));
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.mcElieceCca2);
        try {
            SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(algorithmIdentifier, key);
            return subjectPublicKeyInfo.getEncoded();
        } catch (IOException e) {
            return null;
        }
    }

    @Override // java.security.Key
    public String getFormat() {
        return XMLX509Certificate.JCA_CERT_ID;
    }

    AsymmetricKeyParameter getKeyParams() {
        return this.params;
    }
}
