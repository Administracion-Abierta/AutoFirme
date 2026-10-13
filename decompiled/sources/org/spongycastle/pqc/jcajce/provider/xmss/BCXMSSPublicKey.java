package org.spongycastle.pqc.jcajce.provider.xmss;

import java.io.IOException;
import java.security.PublicKey;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.pqc.asn1.PQCObjectIdentifiers;
import org.spongycastle.pqc.asn1.XMSSKeyParams;
import org.spongycastle.pqc.asn1.XMSSPublicKey;
import org.spongycastle.pqc.crypto.xmss.XMSSParameters;
import org.spongycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;
import org.spongycastle.pqc.jcajce.interfaces.XMSSKey;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/xmss/BCXMSSPublicKey.class */
public class BCXMSSPublicKey implements PublicKey, XMSSKey {
    private final XMSSPublicKeyParameters keyParams;
    private final ASN1ObjectIdentifier treeDigest;

    public BCXMSSPublicKey(ASN1ObjectIdentifier treeDigest, XMSSPublicKeyParameters keyParams) {
        this.treeDigest = treeDigest;
        this.keyParams = keyParams;
    }

    public BCXMSSPublicKey(SubjectPublicKeyInfo keyInfo) throws IOException {
        XMSSKeyParams keyParams = XMSSKeyParams.getInstance(keyInfo.getAlgorithm().getParameters());
        this.treeDigest = keyParams.getTreeDigest().getAlgorithm();
        XMSSPublicKey xmssPublicKey = XMSSPublicKey.getInstance(keyInfo.parsePublicKey());
        this.keyParams = new XMSSPublicKeyParameters.Builder(new XMSSParameters(keyParams.getHeight(), DigestUtil.getDigest(this.treeDigest))).withPublicSeed(xmssPublicKey.getPublicSeed()).withRoot(xmssPublicKey.getRoot()).build();
    }

    @Override // java.security.Key
    public final String getAlgorithm() {
        return "XMSS";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        try {
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.xmss, new XMSSKeyParams(this.keyParams.getParameters().getHeight(), new AlgorithmIdentifier(this.treeDigest)));
            SubjectPublicKeyInfo pki = new SubjectPublicKeyInfo(algorithmIdentifier, new XMSSPublicKey(this.keyParams.getPublicSeed(), this.keyParams.getRoot()));
            return pki.getEncoded();
        } catch (IOException e) {
            return null;
        }
    }

    @Override // java.security.Key
    public String getFormat() {
        return XMLX509Certificate.JCA_CERT_ID;
    }

    CipherParameters getKeyParams() {
        return this.keyParams;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BCXMSSPublicKey) {
            BCXMSSPublicKey otherKey = (BCXMSSPublicKey) o;
            return this.treeDigest.equals(otherKey.treeDigest) && Arrays.areEqual(this.keyParams.toByteArray(), otherKey.keyParams.toByteArray());
        }
        return false;
    }

    public int hashCode() {
        return this.treeDigest.hashCode() + (37 * Arrays.hashCode(this.keyParams.toByteArray()));
    }

    @Override // org.spongycastle.pqc.jcajce.interfaces.XMSSKey
    public int getHeight() {
        return this.keyParams.getParameters().getHeight();
    }

    @Override // org.spongycastle.pqc.jcajce.interfaces.XMSSKey
    public String getTreeDigest() {
        return DigestUtil.getXMSSDigestName(this.treeDigest);
    }
}
