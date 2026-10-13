package org.spongycastle.pqc.crypto.xmss;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/XMSSNodeUtil.class */
class XMSSNodeUtil {
    XMSSNodeUtil() {
    }

    static XMSSNode lTree(WOTSPlus wotsPlus, WOTSPlusPublicKeyParameters publicKey, LTreeAddress address) {
        if (publicKey == null) {
            throw new NullPointerException("publicKey == null");
        }
        if (address == null) {
            throw new NullPointerException("address == null");
        }
        int len = wotsPlus.getParams().getLen();
        byte[][] publicKeyBytes = publicKey.toByteArray();
        XMSSNode[] publicKeyNodes = new XMSSNode[publicKeyBytes.length];
        for (int i = 0; i < publicKeyBytes.length; i++) {
            publicKeyNodes[i] = new XMSSNode(0, publicKeyBytes[i]);
        }
        XMSSAddress xMSSAddressBuild = new LTreeAddress.Builder().withLayerAddress(address.getLayerAddress()).withTreeAddress(address.getTreeAddress()).withLTreeAddress(address.getLTreeAddress()).withTreeHeight(0).withTreeIndex(address.getTreeIndex()).withKeyAndMask(address.getKeyAndMask()).build();
        while (true) {
            LTreeAddress address2 = (LTreeAddress) xMSSAddressBuild;
            if (len > 1) {
                for (int i2 = 0; i2 < ((int) Math.floor(len / 2)); i2++) {
                    address2 = (LTreeAddress) new LTreeAddress.Builder().withLayerAddress(address2.getLayerAddress()).withTreeAddress(address2.getTreeAddress()).withLTreeAddress(address2.getLTreeAddress()).withTreeHeight(address2.getTreeHeight()).withTreeIndex(i2).withKeyAndMask(address2.getKeyAndMask()).build();
                    publicKeyNodes[i2] = randomizeHash(wotsPlus, publicKeyNodes[2 * i2], publicKeyNodes[(2 * i2) + 1], address2);
                }
                if (len % 2 == 1) {
                    publicKeyNodes[(int) Math.floor(len / 2)] = publicKeyNodes[len - 1];
                }
                len = (int) Math.ceil(((double) len) / 2.0d);
                xMSSAddressBuild = new LTreeAddress.Builder().withLayerAddress(address2.getLayerAddress()).withTreeAddress(address2.getTreeAddress()).withLTreeAddress(address2.getLTreeAddress()).withTreeHeight(address2.getTreeHeight() + 1).withTreeIndex(address2.getTreeIndex()).withKeyAndMask(address2.getKeyAndMask()).build();
            } else {
                return publicKeyNodes[0];
            }
        }
    }

    static XMSSNode randomizeHash(WOTSPlus wotsPlus, XMSSNode left, XMSSNode right, XMSSAddress address) {
        if (left == null) {
            throw new NullPointerException("left == null");
        }
        if (right == null) {
            throw new NullPointerException("right == null");
        }
        if (left.getHeight() != right.getHeight()) {
            throw new IllegalStateException("height of both nodes must be equal");
        }
        if (address == null) {
            throw new NullPointerException("address == null");
        }
        byte[] publicSeed = wotsPlus.getPublicSeed();
        if (address instanceof LTreeAddress) {
            LTreeAddress tmpAddress = (LTreeAddress) address;
            address = (LTreeAddress) new LTreeAddress.Builder().withLayerAddress(tmpAddress.getLayerAddress()).withTreeAddress(tmpAddress.getTreeAddress()).withLTreeAddress(tmpAddress.getLTreeAddress()).withTreeHeight(tmpAddress.getTreeHeight()).withTreeIndex(tmpAddress.getTreeIndex()).withKeyAndMask(0).build();
        } else if (address instanceof HashTreeAddress) {
            HashTreeAddress tmpAddress2 = (HashTreeAddress) address;
            address = (HashTreeAddress) new HashTreeAddress.Builder().withLayerAddress(tmpAddress2.getLayerAddress()).withTreeAddress(tmpAddress2.getTreeAddress()).withTreeHeight(tmpAddress2.getTreeHeight()).withTreeIndex(tmpAddress2.getTreeIndex()).withKeyAndMask(0).build();
        }
        byte[] key = wotsPlus.getKhf().PRF(publicSeed, address.toByteArray());
        if (address instanceof LTreeAddress) {
            LTreeAddress tmpAddress3 = (LTreeAddress) address;
            address = (LTreeAddress) new LTreeAddress.Builder().withLayerAddress(tmpAddress3.getLayerAddress()).withTreeAddress(tmpAddress3.getTreeAddress()).withLTreeAddress(tmpAddress3.getLTreeAddress()).withTreeHeight(tmpAddress3.getTreeHeight()).withTreeIndex(tmpAddress3.getTreeIndex()).withKeyAndMask(1).build();
        } else if (address instanceof HashTreeAddress) {
            HashTreeAddress tmpAddress4 = (HashTreeAddress) address;
            address = (HashTreeAddress) new HashTreeAddress.Builder().withLayerAddress(tmpAddress4.getLayerAddress()).withTreeAddress(tmpAddress4.getTreeAddress()).withTreeHeight(tmpAddress4.getTreeHeight()).withTreeIndex(tmpAddress4.getTreeIndex()).withKeyAndMask(1).build();
        }
        byte[] bitmask0 = wotsPlus.getKhf().PRF(publicSeed, address.toByteArray());
        if (address instanceof LTreeAddress) {
            LTreeAddress tmpAddress5 = (LTreeAddress) address;
            address = (LTreeAddress) new LTreeAddress.Builder().withLayerAddress(tmpAddress5.getLayerAddress()).withTreeAddress(tmpAddress5.getTreeAddress()).withLTreeAddress(tmpAddress5.getLTreeAddress()).withTreeHeight(tmpAddress5.getTreeHeight()).withTreeIndex(tmpAddress5.getTreeIndex()).withKeyAndMask(2).build();
        } else if (address instanceof HashTreeAddress) {
            HashTreeAddress tmpAddress6 = (HashTreeAddress) address;
            address = (HashTreeAddress) new HashTreeAddress.Builder().withLayerAddress(tmpAddress6.getLayerAddress()).withTreeAddress(tmpAddress6.getTreeAddress()).withTreeHeight(tmpAddress6.getTreeHeight()).withTreeIndex(tmpAddress6.getTreeIndex()).withKeyAndMask(2).build();
        }
        byte[] bitmask1 = wotsPlus.getKhf().PRF(publicSeed, address.toByteArray());
        int n = wotsPlus.getParams().getDigestSize();
        byte[] tmpMask = new byte[2 * n];
        for (int i = 0; i < n; i++) {
            tmpMask[i] = (byte) (left.getValue()[i] ^ bitmask0[i]);
        }
        for (int i2 = 0; i2 < n; i2++) {
            tmpMask[i2 + n] = (byte) (right.getValue()[i2] ^ bitmask1[i2]);
        }
        byte[] out = wotsPlus.getKhf().H(key, tmpMask);
        return new XMSSNode(left.getHeight(), out);
    }
}
