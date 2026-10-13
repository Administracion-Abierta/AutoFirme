package org.bouncycastle.pqc.crypto.xmss;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/XMSSVerifierUtil.class */
class XMSSVerifierUtil {
    XMSSVerifierUtil() {
    }

    static XMSSNode getRootNodeFromSignature(WOTSPlus wOTSPlus, int i, byte[] bArr, XMSSReducedSignature xMSSReducedSignature, OTSHashAddress oTSHashAddress, int i2) {
        if (bArr.length != wOTSPlus.getParams().getTreeDigestSize()) {
            throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
        }
        if (xMSSReducedSignature == null) {
            throw new NullPointerException("signature == null");
        }
        if (oTSHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        LTreeAddress lTreeAddress = (LTreeAddress) new LTreeAddress.Builder().withLayerAddress(oTSHashAddress.getLayerAddress()).withTreeAddress(oTSHashAddress.getTreeAddress()).withLTreeAddress(oTSHashAddress.getOTSAddress()).build();
        HashTreeAddress hashTreeAddress = (HashTreeAddress) new HashTreeAddress.Builder().withLayerAddress(oTSHashAddress.getLayerAddress()).withTreeAddress(oTSHashAddress.getTreeAddress()).withTreeIndex(oTSHashAddress.getOTSAddress()).build();
        XMSSNode[] xMSSNodeArr = new XMSSNode[2];
        xMSSNodeArr[0] = XMSSNodeUtil.lTree(wOTSPlus, wOTSPlus.getPublicKeyFromSignature(bArr, xMSSReducedSignature.getWOTSPlusSignature(), oTSHashAddress), lTreeAddress);
        for (int i3 = 0; i3 < i; i3++) {
            HashTreeAddress hashTreeAddress2 = (HashTreeAddress) new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress.getLayerAddress()).withTreeAddress(hashTreeAddress.getTreeAddress()).withTreeHeight(i3).withTreeIndex(hashTreeAddress.getTreeIndex()).withKeyAndMask(hashTreeAddress.getKeyAndMask()).build();
            if (Math.floor(i2 / (1 << i3)) % 2.0d == 0.0d) {
                hashTreeAddress = (HashTreeAddress) new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress2.getLayerAddress()).withTreeAddress(hashTreeAddress2.getTreeAddress()).withTreeHeight(hashTreeAddress2.getTreeHeight()).withTreeIndex(hashTreeAddress2.getTreeIndex() / 2).withKeyAndMask(hashTreeAddress2.getKeyAndMask()).build();
                xMSSNodeArr[1] = XMSSNodeUtil.randomizeHash(wOTSPlus, xMSSNodeArr[0], xMSSReducedSignature.getAuthPath().get(i3), hashTreeAddress);
                xMSSNodeArr[1] = new XMSSNode(xMSSNodeArr[1].getHeight() + 1, xMSSNodeArr[1].getValue());
            } else {
                hashTreeAddress = (HashTreeAddress) new HashTreeAddress.Builder().withLayerAddress(hashTreeAddress2.getLayerAddress()).withTreeAddress(hashTreeAddress2.getTreeAddress()).withTreeHeight(hashTreeAddress2.getTreeHeight()).withTreeIndex((hashTreeAddress2.getTreeIndex() - 1) / 2).withKeyAndMask(hashTreeAddress2.getKeyAndMask()).build();
                xMSSNodeArr[1] = XMSSNodeUtil.randomizeHash(wOTSPlus, xMSSReducedSignature.getAuthPath().get(i3), xMSSNodeArr[0], hashTreeAddress);
                xMSSNodeArr[1] = new XMSSNode(xMSSNodeArr[1].getHeight() + 1, xMSSNodeArr[1].getValue());
            }
            xMSSNodeArr[0] = xMSSNodeArr[1];
        }
        return xMSSNodeArr[0];
    }
}
