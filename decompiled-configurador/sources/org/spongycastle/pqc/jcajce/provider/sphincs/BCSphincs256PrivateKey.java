package org.spongycastle.pqc.jcajce.provider.sphincs;

import java.io.IOException;
import java.security.PrivateKey;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.pqc.asn1.PQCObjectIdentifiers;
import org.spongycastle.pqc.asn1.SPHINCS256KeyParams;
import org.spongycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.spongycastle.pqc.jcajce.interfaces.SPHINCSKey;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/sphincs/BCSphincs256PrivateKey.class */
public class BCSphincs256PrivateKey implements PrivateKey, SPHINCSKey {
    private static final long serialVersionUID = 1;
    private final ASN1ObjectIdentifier treeDigest;
    private final SPHINCSPrivateKeyParameters params;

    public BCSphincs256PrivateKey(ASN1ObjectIdentifier treeDigest, SPHINCSPrivateKeyParameters params) {
        this.treeDigest = treeDigest;
        this.params = params;
    }

    public BCSphincs256PrivateKey(PrivateKeyInfo keyInfo) throws IOException {
        this.treeDigest = SPHINCS256KeyParams.getInstance(keyInfo.getPrivateKeyAlgorithm().getParameters()).getTreeDigest().getAlgorithm();
        this.params = new SPHINCSPrivateKeyParameters(ASN1OctetString.getInstance(keyInfo.parsePrivateKey()).getOctets());
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BCSphincs256PrivateKey) {
            BCSphincs256PrivateKey otherKey = (BCSphincs256PrivateKey) o;
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
            PrivateKeyInfo pki = new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(this.params.getKeyData()));
            return pki.getEncoded();
        } catch (IOException e) {
            return null;
        }
    }

    @Override // java.security.Key
    public String getFormat() {
        return "PKCS#8";
    }

    @Override // org.spongycastle.pqc.jcajce.interfaces.SPHINCSKey
    public byte[] getKeyData() {
        return this.params.getKeyData();
    }

    CipherParameters getKeyParams() {
        return this.params;
    }
}
