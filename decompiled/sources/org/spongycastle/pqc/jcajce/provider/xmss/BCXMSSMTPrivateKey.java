package org.spongycastle.pqc.jcajce.provider.xmss;

import java.io.IOException;
import java.security.PrivateKey;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.pqc.asn1.PQCObjectIdentifiers;
import org.spongycastle.pqc.asn1.XMSSMTKeyParams;
import org.spongycastle.pqc.asn1.XMSSMTPrivateKey;
import org.spongycastle.pqc.asn1.XMSSPrivateKey;
import org.spongycastle.pqc.crypto.xmss.BDSStateMap;
import org.spongycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.spongycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.spongycastle.pqc.crypto.xmss.XMSSUtil;
import org.spongycastle.pqc.jcajce.interfaces.XMSSMTKey;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/xmss/BCXMSSMTPrivateKey.class */
public class BCXMSSMTPrivateKey implements PrivateKey, XMSSMTKey {
    private final ASN1ObjectIdentifier treeDigest;
    private final XMSSMTPrivateKeyParameters keyParams;

    public BCXMSSMTPrivateKey(ASN1ObjectIdentifier treeDigest, XMSSMTPrivateKeyParameters keyParams) {
        this.treeDigest = treeDigest;
        this.keyParams = keyParams;
    }

    public BCXMSSMTPrivateKey(PrivateKeyInfo keyInfo) throws IOException {
        XMSSMTKeyParams keyParams = XMSSMTKeyParams.getInstance(keyInfo.getPrivateKeyAlgorithm().getParameters());
        this.treeDigest = keyParams.getTreeDigest().getAlgorithm();
        XMSSPrivateKey xmssMtPrivateKey = XMSSPrivateKey.getInstance(keyInfo.parsePrivateKey());
        try {
            XMSSMTPrivateKeyParameters.Builder keyBuilder = new XMSSMTPrivateKeyParameters.Builder(new XMSSMTParameters(keyParams.getHeight(), keyParams.getLayers(), DigestUtil.getDigest(this.treeDigest))).withIndex(xmssMtPrivateKey.getIndex()).withSecretKeySeed(xmssMtPrivateKey.getSecretKeySeed()).withSecretKeyPRF(xmssMtPrivateKey.getSecretKeyPRF()).withPublicSeed(xmssMtPrivateKey.getPublicSeed()).withRoot(xmssMtPrivateKey.getRoot());
            if (xmssMtPrivateKey.getBdsState() != null) {
                keyBuilder.withBDSState((BDSStateMap) XMSSUtil.deserialize(xmssMtPrivateKey.getBdsState()));
            }
            this.keyParams = keyBuilder.build();
        } catch (ClassNotFoundException e) {
            throw new IOException("ClassNotFoundException processing BDS state: " + e.getMessage());
        }
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "XMSSMT";
    }

    @Override // java.security.Key
    public String getFormat() {
        return "PKCS#8";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        try {
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt, new XMSSMTKeyParams(this.keyParams.getParameters().getHeight(), this.keyParams.getParameters().getLayers(), new AlgorithmIdentifier(this.treeDigest)));
            PrivateKeyInfo pki = new PrivateKeyInfo(algorithmIdentifier, createKeyStructure());
            return pki.getEncoded();
        } catch (IOException e) {
            return null;
        }
    }

    CipherParameters getKeyParams() {
        return this.keyParams;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BCXMSSMTPrivateKey) {
            BCXMSSMTPrivateKey otherKey = (BCXMSSMTPrivateKey) o;
            return this.treeDigest.equals(otherKey.treeDigest) && Arrays.areEqual(this.keyParams.toByteArray(), otherKey.keyParams.toByteArray());
        }
        return false;
    }

    public int hashCode() {
        return this.treeDigest.hashCode() + (37 * Arrays.hashCode(this.keyParams.toByteArray()));
    }

    private XMSSMTPrivateKey createKeyStructure() {
        byte[] keyData = this.keyParams.toByteArray();
        int n = this.keyParams.getParameters().getDigestSize();
        int totalHeight = this.keyParams.getParameters().getHeight();
        int indexSize = (totalHeight + 7) / 8;
        int index = (int) XMSSUtil.bytesToXBigEndian(keyData, 0, indexSize);
        if (!XMSSUtil.isIndexValid(totalHeight, index)) {
            throw new IllegalArgumentException("index out of bounds");
        }
        int position = 0 + indexSize;
        byte[] secretKeySeed = XMSSUtil.extractBytesAtOffset(keyData, position, n);
        int position2 = position + n;
        byte[] secretKeyPRF = XMSSUtil.extractBytesAtOffset(keyData, position2, n);
        int position3 = position2 + n;
        byte[] publicSeed = XMSSUtil.extractBytesAtOffset(keyData, position3, n);
        int position4 = position3 + n;
        byte[] root = XMSSUtil.extractBytesAtOffset(keyData, position4, n);
        int position5 = position4 + n;
        byte[] bdsStateBinary = XMSSUtil.extractBytesAtOffset(keyData, position5, keyData.length - position5);
        return new XMSSMTPrivateKey(index, secretKeySeed, secretKeyPRF, publicSeed, root, bdsStateBinary);
    }

    ASN1ObjectIdentifier getTreeDigestOID() {
        return this.treeDigest;
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
