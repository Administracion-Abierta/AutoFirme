package org.spongycastle.pqc.jcajce.provider.xmss;

import java.io.IOException;
import java.security.PrivateKey;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.pqc.asn1.PQCObjectIdentifiers;
import org.spongycastle.pqc.asn1.XMSSKeyParams;
import org.spongycastle.pqc.asn1.XMSSPrivateKey;
import org.spongycastle.pqc.crypto.xmss.BDS;
import org.spongycastle.pqc.crypto.xmss.XMSSParameters;
import org.spongycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.spongycastle.pqc.crypto.xmss.XMSSUtil;
import org.spongycastle.pqc.jcajce.interfaces.XMSSKey;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/xmss/BCXMSSPrivateKey.class */
public class BCXMSSPrivateKey implements PrivateKey, XMSSKey {
    private final XMSSPrivateKeyParameters keyParams;
    private final ASN1ObjectIdentifier treeDigest;

    public BCXMSSPrivateKey(ASN1ObjectIdentifier treeDigest, XMSSPrivateKeyParameters keyParams) {
        this.treeDigest = treeDigest;
        this.keyParams = keyParams;
    }

    public BCXMSSPrivateKey(PrivateKeyInfo keyInfo) throws IOException {
        XMSSKeyParams keyParams = XMSSKeyParams.getInstance(keyInfo.getPrivateKeyAlgorithm().getParameters());
        this.treeDigest = keyParams.getTreeDigest().getAlgorithm();
        XMSSPrivateKey xmssPrivateKey = XMSSPrivateKey.getInstance(keyInfo.parsePrivateKey());
        try {
            XMSSPrivateKeyParameters.Builder keyBuilder = new XMSSPrivateKeyParameters.Builder(new XMSSParameters(keyParams.getHeight(), DigestUtil.getDigest(this.treeDigest))).withIndex(xmssPrivateKey.getIndex()).withSecretKeySeed(xmssPrivateKey.getSecretKeySeed()).withSecretKeyPRF(xmssPrivateKey.getSecretKeyPRF()).withPublicSeed(xmssPrivateKey.getPublicSeed()).withRoot(xmssPrivateKey.getRoot());
            if (xmssPrivateKey.getBdsState() != null) {
                keyBuilder.withBDSState((BDS) XMSSUtil.deserialize(xmssPrivateKey.getBdsState()));
            }
            this.keyParams = keyBuilder.build();
        } catch (ClassNotFoundException e) {
            throw new IOException("ClassNotFoundException processing BDS state: " + e.getMessage());
        }
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "XMSS";
    }

    @Override // java.security.Key
    public String getFormat() {
        return "PKCS#8";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        try {
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.xmss, new XMSSKeyParams(this.keyParams.getParameters().getHeight(), new AlgorithmIdentifier(this.treeDigest)));
            PrivateKeyInfo pki = new PrivateKeyInfo(algorithmIdentifier, createKeyStructure());
            return pki.getEncoded();
        } catch (IOException e) {
            return null;
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BCXMSSPrivateKey) {
            BCXMSSPrivateKey otherKey = (BCXMSSPrivateKey) o;
            return this.treeDigest.equals(otherKey.treeDigest) && Arrays.areEqual(this.keyParams.toByteArray(), otherKey.keyParams.toByteArray());
        }
        return false;
    }

    public int hashCode() {
        return this.treeDigest.hashCode() + (37 * Arrays.hashCode(this.keyParams.toByteArray()));
    }

    CipherParameters getKeyParams() {
        return this.keyParams;
    }

    private XMSSPrivateKey createKeyStructure() {
        byte[] keyData = this.keyParams.toByteArray();
        int n = this.keyParams.getParameters().getDigestSize();
        int totalHeight = this.keyParams.getParameters().getHeight();
        int index = (int) XMSSUtil.bytesToXBigEndian(keyData, 0, 4);
        if (!XMSSUtil.isIndexValid(totalHeight, index)) {
            throw new IllegalArgumentException("index out of bounds");
        }
        int position = 0 + 4;
        byte[] secretKeySeed = XMSSUtil.extractBytesAtOffset(keyData, position, n);
        int position2 = position + n;
        byte[] secretKeyPRF = XMSSUtil.extractBytesAtOffset(keyData, position2, n);
        int position3 = position2 + n;
        byte[] publicSeed = XMSSUtil.extractBytesAtOffset(keyData, position3, n);
        int position4 = position3 + n;
        byte[] root = XMSSUtil.extractBytesAtOffset(keyData, position4, n);
        int position5 = position4 + n;
        byte[] bdsStateBinary = XMSSUtil.extractBytesAtOffset(keyData, position5, keyData.length - position5);
        return new XMSSPrivateKey(index, secretKeySeed, secretKeyPRF, publicSeed, root, bdsStateBinary);
    }

    ASN1ObjectIdentifier getTreeDigestOID() {
        return this.treeDigest;
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
