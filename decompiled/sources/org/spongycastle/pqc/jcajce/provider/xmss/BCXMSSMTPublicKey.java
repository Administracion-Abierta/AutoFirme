package org.spongycastle.pqc.jcajce.provider.xmss;

import java.io.IOException;
import java.security.PublicKey;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.pqc.asn1.PQCObjectIdentifiers;
import org.spongycastle.pqc.asn1.XMSSMTKeyParams;
import org.spongycastle.pqc.asn1.XMSSPublicKey;
import org.spongycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.spongycastle.pqc.crypto.xmss.XMSSMTPublicKeyParameters;
import org.spongycastle.pqc.jcajce.interfaces.XMSSMTKey;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/xmss/BCXMSSMTPublicKey.class */
public class BCXMSSMTPublicKey implements PublicKey, XMSSMTKey {
    private final ASN1ObjectIdentifier treeDigest;
    private final XMSSMTPublicKeyParameters keyParams;

    public BCXMSSMTPublicKey(ASN1ObjectIdentifier treeDigest, XMSSMTPublicKeyParameters keyParams) {
        this.treeDigest = treeDigest;
        this.keyParams = keyParams;
    }

    public BCXMSSMTPublicKey(SubjectPublicKeyInfo keyInfo) throws IOException {
        XMSSMTKeyParams keyParams = XMSSMTKeyParams.getInstance(keyInfo.getAlgorithm().getParameters());
        this.treeDigest = keyParams.getTreeDigest().getAlgorithm();
        XMSSPublicKey xmssMtPublicKey = XMSSPublicKey.getInstance(keyInfo.parsePublicKey());
        this.keyParams = new XMSSMTPublicKeyParameters.Builder(new XMSSMTParameters(keyParams.getHeight(), keyParams.getLayers(), DigestUtil.getDigest(this.treeDigest))).withPublicSeed(xmssMtPublicKey.getPublicSeed()).withRoot(xmssMtPublicKey.getRoot()).build();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BCXMSSMTPublicKey) {
            BCXMSSMTPublicKey otherKey = (BCXMSSMTPublicKey) o;
            return this.treeDigest.equals(otherKey.treeDigest) && Arrays.areEqual(this.keyParams.toByteArray(), otherKey.keyParams.toByteArray());
        }
        return false;
    }

    public int hashCode() {
        return this.treeDigest.hashCode() + (37 * Arrays.hashCode(this.keyParams.toByteArray()));
    }

    @Override // java.security.Key
    public final String getAlgorithm() {
        return "XMSSMT";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        try {
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt, new XMSSMTKeyParams(this.keyParams.getParameters().getHeight(), this.keyParams.getParameters().getLayers(), new AlgorithmIdentifier(this.treeDigest)));
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

    @Override // org.spongycastle.pqc.jcajce.interfaces.XMSSMTKey
    public int getHeight() {
        return this.keyParams.getParameters().getHeight();
    }

    @Override // org.spongycastle.pqc.jcajce.interfaces.XMSSMTKey
    public int getLayers() {
        return this.keyParams.getParameters().getLayers();
    }

    @Override // org.spongycastle.pqc.jcajce.interfaces.XMSSMTKey
    public String getTreeDigest() {
        return DigestUtil.getXMSSDigestName(this.treeDigest);
    }
}
