package org.spongycastle.pqc.jcajce.provider.mceliece;

import java.io.IOException;
import java.security.PublicKey;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.pqc.asn1.McEliecePublicKey;
import org.spongycastle.pqc.asn1.PQCObjectIdentifiers;
import org.spongycastle.pqc.crypto.mceliece.McEliecePublicKeyParameters;
import org.spongycastle.pqc.math.linearalgebra.GF2Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/mceliece/BCMcEliecePublicKey.class */
public class BCMcEliecePublicKey implements PublicKey {
    private static final long serialVersionUID = 1;
    private McEliecePublicKeyParameters params;

    public BCMcEliecePublicKey(McEliecePublicKeyParameters params) {
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

    public int getT() {
        return this.params.getT();
    }

    public GF2Matrix getG() {
        return this.params.getG();
    }

    public String toString() {
        String result = "McEliecePublicKey:\n length of the code         : " + this.params.getN() + "\n";
        return (result + " error correction capability: " + this.params.getT() + "\n") + " generator matrix           : " + this.params.getG();
    }

    public boolean equals(Object other) {
        if (other instanceof BCMcEliecePublicKey) {
            BCMcEliecePublicKey otherKey = (BCMcEliecePublicKey) other;
            return this.params.getN() == otherKey.getN() && this.params.getT() == otherKey.getT() && this.params.getG().equals(otherKey.getG());
        }
        return false;
    }

    public int hashCode() {
        return (37 * (this.params.getN() + (37 * this.params.getT()))) + this.params.getG().hashCode();
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        McEliecePublicKey key = new McEliecePublicKey(this.params.getN(), this.params.getT(), this.params.getG());
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.mcEliece);
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
