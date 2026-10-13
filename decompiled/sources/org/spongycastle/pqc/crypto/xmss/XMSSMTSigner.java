package org.spongycastle.pqc.crypto.xmss;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.pqc.crypto.StateAwareMessageSigner;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/XMSSMTSigner.class */
public class XMSSMTSigner implements StateAwareMessageSigner {
    private XMSSMTPrivateKeyParameters privateKey;
    private XMSSMTPrivateKeyParameters nextKeyGenerator;
    private XMSSMTPublicKeyParameters publicKey;
    private XMSSMTParameters params;
    private XMSSParameters xmssParams;
    private WOTSPlus wotsPlus;
    private boolean hasGenerated;
    private boolean initSign;

    @Override // org.spongycastle.pqc.crypto.MessageSigner
    public void init(boolean forSigning, CipherParameters param) {
        if (forSigning) {
            this.initSign = true;
            this.hasGenerated = false;
            this.privateKey = (XMSSMTPrivateKeyParameters) param;
            this.nextKeyGenerator = this.privateKey;
            this.params = this.privateKey.getParameters();
            this.xmssParams = this.params.getXMSSParameters();
        } else {
            this.initSign = false;
            this.publicKey = (XMSSMTPublicKeyParameters) param;
            this.params = this.publicKey.getParameters();
            this.xmssParams = this.params.getXMSSParameters();
        }
        this.wotsPlus = new WOTSPlus(new WOTSPlusParameters(this.params.getDigest()));
    }

    @Override // org.spongycastle.pqc.crypto.MessageSigner
    public byte[] generateSignature(byte[] message) {
        if (message == null) {
            throw new NullPointerException("message == null");
        }
        if (this.initSign) {
            if (this.privateKey == null) {
                throw new IllegalStateException("signing key no longer usable");
            }
            if (this.privateKey.getBDSState().isEmpty()) {
                throw new IllegalStateException("not initialized");
            }
            BDSStateMap bdsState = this.privateKey.getBDSState();
            long globalIndex = this.privateKey.getIndex();
            int totalHeight = this.params.getHeight();
            int xmssHeight = this.xmssParams.getHeight();
            if (!XMSSUtil.isIndexValid(totalHeight, globalIndex)) {
                throw new IllegalStateException("index out of bounds");
            }
            byte[] random = this.wotsPlus.getKhf().PRF(this.privateKey.getSecretKeyPRF(), XMSSUtil.toBytesBigEndian(globalIndex, 32));
            byte[] concatenated = Arrays.concatenate(random, this.privateKey.getRoot(), XMSSUtil.toBytesBigEndian(globalIndex, this.params.getDigestSize()));
            byte[] messageDigest = this.wotsPlus.getKhf().HMsg(concatenated, message);
            XMSSMTSignature signature = new XMSSMTSignature.Builder(this.params).withIndex(globalIndex).withRandom(random).build();
            long indexTree = XMSSUtil.getTreeIndex(globalIndex, xmssHeight);
            int indexLeaf = XMSSUtil.getLeafIndex(globalIndex, xmssHeight);
            this.wotsPlus.importKeys(new byte[this.params.getDigestSize()], this.privateKey.getPublicSeed());
            OTSHashAddress otsHashAddress = (OTSHashAddress) new OTSHashAddress.Builder().withTreeAddress(indexTree).withOTSAddress(indexLeaf).build();
            if (bdsState.get(0) == null || indexLeaf == 0) {
                bdsState.put(0, new BDS(this.xmssParams, this.privateKey.getPublicSeed(), this.privateKey.getSecretKeySeed(), otsHashAddress));
            }
            WOTSPlusSignature wotsPlusSignature = wotsSign(messageDigest, otsHashAddress);
            XMSSReducedSignature reducedSignature = new XMSSReducedSignature.Builder(this.xmssParams).withWOTSPlusSignature(wotsPlusSignature).withAuthPath(bdsState.get(0).getAuthenticationPath()).build();
            signature.getReducedSignatures().add(reducedSignature);
            for (int layer = 1; layer < this.params.getLayers(); layer++) {
                XMSSNode root = bdsState.get(layer - 1).getRoot();
                int indexLeaf2 = XMSSUtil.getLeafIndex(indexTree, xmssHeight);
                indexTree = XMSSUtil.getTreeIndex(indexTree, xmssHeight);
                OTSHashAddress otsHashAddress2 = (OTSHashAddress) new OTSHashAddress.Builder().withLayerAddress(layer).withTreeAddress(indexTree).withOTSAddress(indexLeaf2).build();
                WOTSPlusSignature wotsPlusSignature2 = wotsSign(root.getValue(), otsHashAddress2);
                if (bdsState.get(layer) == null || XMSSUtil.isNewBDSInitNeeded(globalIndex, xmssHeight, layer)) {
                    bdsState.put(layer, new BDS(this.xmssParams, this.privateKey.getPublicSeed(), this.privateKey.getSecretKeySeed(), otsHashAddress2));
                }
                XMSSReducedSignature reducedSignature2 = new XMSSReducedSignature.Builder(this.xmssParams).withWOTSPlusSignature(wotsPlusSignature2).withAuthPath(bdsState.get(layer).getAuthenticationPath()).build();
                signature.getReducedSignatures().add(reducedSignature2);
            }
            this.hasGenerated = true;
            if (this.nextKeyGenerator != null) {
                this.privateKey = this.nextKeyGenerator.getNextKey();
                this.nextKeyGenerator = this.privateKey;
            } else {
                this.privateKey = null;
            }
            return signature.toByteArray();
        }
        throw new IllegalStateException("signer not initialized for signature generation");
    }

    @Override // org.spongycastle.pqc.crypto.MessageSigner
    public boolean verifySignature(byte[] message, byte[] signature) {
        if (message == null) {
            throw new NullPointerException("message == null");
        }
        if (signature == null) {
            throw new NullPointerException("signature == null");
        }
        if (this.publicKey == null) {
            throw new NullPointerException("publicKey == null");
        }
        XMSSMTSignature sig = new XMSSMTSignature.Builder(this.params).withSignature(signature).build();
        byte[] concatenated = Arrays.concatenate(sig.getRandom(), this.publicKey.getRoot(), XMSSUtil.toBytesBigEndian(sig.getIndex(), this.params.getDigestSize()));
        byte[] messageDigest = this.wotsPlus.getKhf().HMsg(concatenated, message);
        long globalIndex = sig.getIndex();
        int xmssHeight = this.xmssParams.getHeight();
        long indexTree = XMSSUtil.getTreeIndex(globalIndex, xmssHeight);
        int indexLeaf = XMSSUtil.getLeafIndex(globalIndex, xmssHeight);
        this.wotsPlus.importKeys(new byte[this.params.getDigestSize()], this.publicKey.getPublicSeed());
        OTSHashAddress otsHashAddress = (OTSHashAddress) new OTSHashAddress.Builder().withTreeAddress(indexTree).withOTSAddress(indexLeaf).build();
        XMSSReducedSignature xmssMTSignature = sig.getReducedSignatures().get(0);
        XMSSNode rootNode = XMSSVerifierUtil.getRootNodeFromSignature(this.wotsPlus, xmssHeight, messageDigest, xmssMTSignature, otsHashAddress, indexLeaf);
        for (int layer = 1; layer < this.params.getLayers(); layer++) {
            XMSSReducedSignature xmssMTSignature2 = sig.getReducedSignatures().get(layer);
            int indexLeaf2 = XMSSUtil.getLeafIndex(indexTree, xmssHeight);
            indexTree = XMSSUtil.getTreeIndex(indexTree, xmssHeight);
            OTSHashAddress otsHashAddress2 = (OTSHashAddress) new OTSHashAddress.Builder().withLayerAddress(layer).withTreeAddress(indexTree).withOTSAddress(indexLeaf2).build();
            rootNode = XMSSVerifierUtil.getRootNodeFromSignature(this.wotsPlus, xmssHeight, rootNode.getValue(), xmssMTSignature2, otsHashAddress2, indexLeaf2);
        }
        return Arrays.constantTimeAreEqual(rootNode.getValue(), this.publicKey.getRoot());
    }

    private WOTSPlusSignature wotsSign(byte[] messageDigest, OTSHashAddress otsHashAddress) {
        if (messageDigest.length != this.params.getDigestSize()) {
            throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
        }
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        this.wotsPlus.importKeys(this.wotsPlus.getWOTSPlusSecretKey(this.privateKey.getSecretKeySeed(), otsHashAddress), this.privateKey.getPublicSeed());
        return this.wotsPlus.sign(messageDigest, otsHashAddress);
    }

    @Override // org.spongycastle.pqc.crypto.StateAwareMessageSigner
    public AsymmetricKeyParameter getUpdatedPrivateKey() {
        if (this.hasGenerated) {
            XMSSMTPrivateKeyParameters privKey = this.privateKey;
            this.privateKey = null;
            this.nextKeyGenerator = null;
            return privKey;
        }
        XMSSMTPrivateKeyParameters privKey2 = this.nextKeyGenerator.getNextKey();
        this.nextKeyGenerator = null;
        return privKey2;
    }
}
