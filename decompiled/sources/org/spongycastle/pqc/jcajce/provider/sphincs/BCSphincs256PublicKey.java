package org.spongycastle.pqc.jcajce.provider.sphincs;

import java.io.IOException;
import java.security.PublicKey;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.pqc.asn1.PQCObjectIdentifiers;
import org.spongycastle.pqc.asn1.SPHINCS256KeyParams;
import org.spongycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;
import org.spongycastle.pqc.jcajce.interfaces.SPHINCSKey;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/sphincs/BCSphincs256PublicKey.class */
public class BCSphincs256PublicKey implements PublicKey, SPHINCSKey {
    private static final long serialVersionUID = 1;
    private final ASN1ObjectIdentifier treeDigest;
    private final SPHINCSPublicKeyParameters params;

    public BCSphincs256PublicKey(ASN1ObjectIdentifier treeDigest, SPHINCSPublicKeyParameters params) {
        this.treeDigest = treeDigest;
        this.params = params;
    }

    public BCSphincs256PublicKey(SubjectPublicKeyInfo keyInfo) {
        this.treeDigest = SPHINCS256KeyParams.getInstance(keyInfo.getAlgorithm().getParameters()).getTreeDigest().getAlgorithm();
        this.params = new SPHINCSPublicKeyParameters(keyInfo.getPublicKeyData().getBytes());
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BCSphincs256PublicKey) {
            BCSphincs256PublicKey otherKey = (BCSphincs256PublicKey) o;
            return this.treeDigest.equals(otherKey.treeDigest) && Arrays.areEqual(this.params.getKeyData(), otherKey.params.getKeyData());
        }
        return false;
    }

    public int hashCode() {
        return this.treeDigest.hashCode() + (37 * Arrays.hashCode(this.params.getKeyData()));
    }

    @Override // java.security.Key
    public final String getAlgorithm() {
        return "SPHINCS-256";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        try {
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.sphincs256, new SPHINCS256KeyParams(new AlgorithmIdentifier(this.treeDigest)));
            SubjectPublicKeyInfo pki = new SubjectPublicKeyInfo(algorithmIdentifier, this.params.getKeyData());
            return pki.getEncoded();
        } catch (IOException e) {
            return null;
        }
    }

    @Override // java.security.Key
    public String getFormat() {
        return XMLX509Certificate.JCA_CERT_ID;
    }

    @Override // org.spongycastle.pqc.jcajce.interfaces.SPHINCSKey
    public byte[] getKeyData() {
        return this.params.getKeyData();
    }

    CipherParameters getKeyParams() {
        return this.params;
    }
}
