package org.spongycastle.pqc.jcajce.provider.newhope;

import java.io.IOException;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.pqc.asn1.PQCObjectIdentifiers;
import org.spongycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.spongycastle.pqc.jcajce.interfaces.NHPublicKey;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/newhope/BCNHPublicKey.class */
public class BCNHPublicKey implements NHPublicKey {
    private static final long serialVersionUID = 1;
    private final NHPublicKeyParameters params;

    public BCNHPublicKey(NHPublicKeyParameters params) {
        this.params = params;
    }

    public BCNHPublicKey(SubjectPublicKeyInfo keyInfo) {
        this.params = new NHPublicKeyParameters(keyInfo.getPublicKeyData().getBytes());
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof BCNHPublicKey)) {
            return false;
        }
        BCNHPublicKey otherKey = (BCNHPublicKey) o;
        return Arrays.areEqual(this.params.getPubData(), otherKey.params.getPubData());
    }

    public int hashCode() {
        return Arrays.hashCode(this.params.getPubData());
    }

    @Override // java.security.Key
    public final String getAlgorithm() {
        return "NH";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        try {
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.newHope);
            SubjectPublicKeyInfo pki = new SubjectPublicKeyInfo(algorithmIdentifier, this.params.getPubData());
            return pki.getEncoded();
        } catch (IOException e) {
            return null;
        }
    }

    @Override // java.security.Key
    public String getFormat() {
        return XMLX509Certificate.JCA_CERT_ID;
    }

    @Override // org.spongycastle.pqc.jcajce.interfaces.NHPublicKey
    public byte[] getPublicData() {
        return this.params.getPubData();
    }

    CipherParameters getKeyParams() {
        return this.params;
    }
}
