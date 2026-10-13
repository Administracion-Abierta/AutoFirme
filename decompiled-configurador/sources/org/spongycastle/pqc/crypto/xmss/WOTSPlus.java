package org.spongycastle.pqc.crypto.xmss;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/WOTSPlus.class */
final class WOTSPlus {
    private final WOTSPlusParameters params;
    private final KeyedHashFunctions khf;
    private byte[] secretKeySeed;
    private byte[] publicSeed;

    protected WOTSPlus(WOTSPlusParameters params) {
        if (params == null) {
            throw new NullPointerException("params == null");
        }
        this.params = params;
        int n = params.getDigestSize();
        this.khf = new KeyedHashFunctions(params.getDigest(), n);
        this.secretKeySeed = new byte[n];
        this.publicSeed = new byte[n];
    }

    void importKeys(byte[] secretKeySeed, byte[] publicSeed) {
        if (secretKeySeed == null) {
            throw new NullPointerException("secretKeySeed == null");
        }
        if (secretKeySeed.length != this.params.getDigestSize()) {
            throw new IllegalArgumentException("size of secretKeySeed needs to be equal to size of digest");
        }
        if (publicSeed == null) {
            throw new NullPointerException("publicSeed == null");
        }
        if (publicSeed.length != this.params.getDigestSize()) {
            throw new IllegalArgumentException("size of publicSeed needs to be equal to size of digest");
        }
        this.secretKeySeed = secretKeySeed;
        this.publicSeed = publicSeed;
    }

    protected WOTSPlusSignature sign(byte[] messageDigest, OTSHashAddress otsHashAddress) {
        if (messageDigest == null) {
            throw new NullPointerException("messageDigest == null");
        }
        if (messageDigest.length != this.params.getDigestSize()) {
            throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
        }
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        List<Integer> baseWMessage = convertToBaseW(messageDigest, this.params.getWinternitzParameter(), this.params.getLen1());
        int checksum = 0;
        for (int i = 0; i < this.params.getLen1(); i++) {
            checksum += (this.params.getWinternitzParameter() - 1) - baseWMessage.get(i).intValue();
        }
        int checksum2 = checksum << (8 - ((this.params.getLen2() * XMSSUtil.log2(this.params.getWinternitzParameter())) % 8));
        int len2Bytes = (int) Math.ceil(((double) (this.params.getLen2() * XMSSUtil.log2(this.params.getWinternitzParameter()))) / 8.0d);
        List<Integer> baseWChecksum = convertToBaseW(XMSSUtil.toBytesBigEndian(checksum2, len2Bytes), this.params.getWinternitzParameter(), this.params.getLen2());
        baseWMessage.addAll(baseWChecksum);
        byte[][] signature = new byte[this.params.getLen()][];
        for (int i2 = 0; i2 < this.params.getLen(); i2++) {
            otsHashAddress = (OTSHashAddress) new OTSHashAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withOTSAddress(otsHashAddress.getOTSAddress()).withChainAddress(i2).withHashAddress(otsHashAddress.getHashAddress()).withKeyAndMask(otsHashAddress.getKeyAndMask()).build();
            signature[i2] = chain(expandSecretKeySeed(i2), 0, baseWMessage.get(i2).intValue(), otsHashAddress);
        }
        return new WOTSPlusSignature(this.params, signature);
    }

    protected boolean verifySignature(byte[] messageDigest, WOTSPlusSignature signature, OTSHashAddress otsHashAddress) {
        if (messageDigest == null) {
            throw new NullPointerException("messageDigest == null");
        }
        if (messageDigest.length != this.params.getDigestSize()) {
            throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
        }
        if (signature == null) {
            throw new NullPointerException("signature == null");
        }
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        byte[][] tmpPublicKey = getPublicKeyFromSignature(messageDigest, signature, otsHashAddress).toByteArray();
        return XMSSUtil.areEqual(tmpPublicKey, getPublicKey(otsHashAddress).toByteArray());
    }

    protected WOTSPlusPublicKeyParameters getPublicKeyFromSignature(byte[] messageDigest, WOTSPlusSignature signature, OTSHashAddress otsHashAddress) {
        if (messageDigest == null) {
            throw new NullPointerException("messageDigest == null");
        }
        if (messageDigest.length != this.params.getDigestSize()) {
            throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
        }
        if (signature == null) {
            throw new NullPointerException("signature == null");
        }
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        List<Integer> baseWMessage = convertToBaseW(messageDigest, this.params.getWinternitzParameter(), this.params.getLen1());
        int checksum = 0;
        for (int i = 0; i < this.params.getLen1(); i++) {
            checksum += (this.params.getWinternitzParameter() - 1) - baseWMessage.get(i).intValue();
        }
        int checksum2 = checksum << (8 - ((this.params.getLen2() * XMSSUtil.log2(this.params.getWinternitzParameter())) % 8));
        int len2Bytes = (int) Math.ceil(((double) (this.params.getLen2() * XMSSUtil.log2(this.params.getWinternitzParameter()))) / 8.0d);
        List<Integer> baseWChecksum = convertToBaseW(XMSSUtil.toBytesBigEndian(checksum2, len2Bytes), this.params.getWinternitzParameter(), this.params.getLen2());
        baseWMessage.addAll(baseWChecksum);
        byte[][] publicKey = new byte[this.params.getLen()][];
        for (int i2 = 0; i2 < this.params.getLen(); i2++) {
            otsHashAddress = (OTSHashAddress) new OTSHashAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withOTSAddress(otsHashAddress.getOTSAddress()).withChainAddress(i2).withHashAddress(otsHashAddress.getHashAddress()).withKeyAndMask(otsHashAddress.getKeyAndMask()).build();
            publicKey[i2] = chain(signature.toByteArray()[i2], baseWMessage.get(i2).intValue(), (this.params.getWinternitzParameter() - 1) - baseWMessage.get(i2).intValue(), otsHashAddress);
        }
        return new WOTSPlusPublicKeyParameters(this.params, publicKey);
    }

    private byte[] chain(byte[] startHash, int startIndex, int steps, OTSHashAddress otsHashAddress) {
        int n = this.params.getDigestSize();
        if (startHash == null) {
            throw new NullPointerException("startHash == null");
        }
        if (startHash.length != n) {
            throw new IllegalArgumentException("startHash needs to be " + n + "bytes");
        }
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        if (otsHashAddress.toByteArray() == null) {
            throw new NullPointerException("otsHashAddress byte array == null");
        }
        if (startIndex + steps > this.params.getWinternitzParameter() - 1) {
            throw new IllegalArgumentException("max chain length must not be greater than w");
        }
        if (steps == 0) {
            return startHash;
        }
        byte[] tmp = chain(startHash, startIndex, steps - 1, otsHashAddress);
        OTSHashAddress otsHashAddress2 = (OTSHashAddress) new OTSHashAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withOTSAddress(otsHashAddress.getOTSAddress()).withChainAddress(otsHashAddress.getChainAddress()).withHashAddress((startIndex + steps) - 1).withKeyAndMask(0).build();
        byte[] key = this.khf.PRF(this.publicSeed, otsHashAddress2.toByteArray());
        byte[] bitmask = this.khf.PRF(this.publicSeed, ((OTSHashAddress) new OTSHashAddress.Builder().withLayerAddress(otsHashAddress2.getLayerAddress()).withTreeAddress(otsHashAddress2.getTreeAddress()).withOTSAddress(otsHashAddress2.getOTSAddress()).withChainAddress(otsHashAddress2.getChainAddress()).withHashAddress(otsHashAddress2.getHashAddress()).withKeyAndMask(1).build()).toByteArray());
        byte[] tmpMasked = new byte[n];
        for (int i = 0; i < n; i++) {
            tmpMasked[i] = (byte) (tmp[i] ^ bitmask[i]);
        }
        byte[] tmp2 = this.khf.F(key, tmpMasked);
        return tmp2;
    }

    private List<Integer> convertToBaseW(byte[] messageDigest, int w, int outLength) {
        if (messageDigest == null) {
            throw new NullPointerException("msg == null");
        }
        if (w != 4 && w != 16) {
            throw new IllegalArgumentException("w needs to be 4 or 16");
        }
        int logW = XMSSUtil.log2(w);
        if (outLength > (8 * messageDigest.length) / logW) {
            throw new IllegalArgumentException("outLength too big");
        }
        ArrayList<Integer> res = new ArrayList<>();
        for (byte b : messageDigest) {
            int i = 8;
            while (true) {
                int j = i - logW;
                if (j >= 0) {
                    res.add(Integer.valueOf((b >> j) & (w - 1)));
                    if (res.size() != outLength) {
                        i = j;
                    } else {
                        return res;
                    }
                }
            }
        }
        return res;
    }

    protected byte[] getWOTSPlusSecretKey(byte[] secretKeySeed, OTSHashAddress otsHashAddress) {
        return this.khf.PRF(secretKeySeed, ((OTSHashAddress) new OTSHashAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withOTSAddress(otsHashAddress.getOTSAddress()).build()).toByteArray());
    }

    private byte[] expandSecretKeySeed(int index) {
        if (index < 0 || index >= this.params.getLen()) {
            throw new IllegalArgumentException("index out of bounds");
        }
        return this.khf.PRF(this.secretKeySeed, XMSSUtil.toBytesBigEndian(index, 32));
    }

    protected WOTSPlusParameters getParams() {
        return this.params;
    }

    protected KeyedHashFunctions getKhf() {
        return this.khf;
    }

    protected byte[] getSecretKeySeed() {
        return XMSSUtil.cloneArray(getSecretKeySeed());
    }

    protected byte[] getPublicSeed() {
        return XMSSUtil.cloneArray(this.publicSeed);
    }

    protected WOTSPlusPrivateKeyParameters getPrivateKey() {
        byte[][] privateKey = new byte[this.params.getLen()][];
        for (int i = 0; i < privateKey.length; i++) {
            privateKey[i] = expandSecretKeySeed(i);
        }
        return new WOTSPlusPrivateKeyParameters(this.params, privateKey);
    }

    protected WOTSPlusPublicKeyParameters getPublicKey(OTSHashAddress otsHashAddress) {
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        byte[][] publicKey = new byte[this.params.getLen()][];
        for (int i = 0; i < this.params.getLen(); i++) {
            otsHashAddress = (OTSHashAddress) new OTSHashAddress.Builder().withLayerAddress(otsHashAddress.getLayerAddress()).withTreeAddress(otsHashAddress.getTreeAddress()).withOTSAddress(otsHashAddress.getOTSAddress()).withChainAddress(i).withHashAddress(otsHashAddress.getHashAddress()).withKeyAndMask(otsHashAddress.getKeyAndMask()).build();
            publicKey[i] = chain(expandSecretKeySeed(i), 0, this.params.getWinternitzParameter() - 1, otsHashAddress);
        }
        return new WOTSPlusPublicKeyParameters(this.params, publicKey);
    }
}
