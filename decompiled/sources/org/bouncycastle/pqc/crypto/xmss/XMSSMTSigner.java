package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.StateAwareMessageSigner;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/XMSSMTSigner.class */
public class XMSSMTSigner implements StateAwareMessageSigner {
    private XMSSMTPrivateKeyParameters privateKey;
    private XMSSMTPublicKeyParameters publicKey;
    private XMSSMTParameters params;
    private XMSSParameters xmssParams;
    private WOTSPlus wotsPlus;
    private boolean hasGenerated;
    private boolean initSign;

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public void init(boolean z, CipherParameters cipherParameters) {
        if (z) {
            this.initSign = true;
            this.hasGenerated = false;
            this.privateKey = (XMSSMTPrivateKeyParameters) cipherParameters;
            this.params = this.privateKey.getParameters();
            this.xmssParams = this.params.getXMSSParameters();
        } else {
            this.initSign = false;
            this.publicKey = (XMSSMTPublicKeyParameters) cipherParameters;
            this.params = this.publicKey.getParameters();
            this.xmssParams = this.params.getXMSSParameters();
        }
        this.wotsPlus = this.params.getWOTSPlus();
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public byte[] generateSignature(byte[] bArr) {
        byte[] byteArray;
        if (bArr == null) {
            throw new NullPointerException("message == null");
        }
        if (!this.initSign) {
            throw new IllegalStateException("signer not initialized for signature generation");
        }
        if (this.privateKey == null) {
            throw new IllegalStateException("signing key no longer usable");
        }
        synchronized (this.privateKey) {
            if (this.privateKey.getUsagesRemaining() <= 0) {
                throw new IllegalStateException("no usages of private key remaining");
            }
            if (this.privateKey.getBDSState().isEmpty()) {
                throw new IllegalStateException("not initialized");
            }
            try {
                BDSStateMap bDSState = this.privateKey.getBDSState();
                long index = this.privateKey.getIndex();
                this.params.getHeight();
                int height = this.xmssParams.getHeight();
                if (this.privateKey.getUsagesRemaining() <= 0) {
                    throw new IllegalStateException("index out of bounds");
                }
                byte[] bArrPRF = this.wotsPlus.getKhf().PRF(this.privateKey.getSecretKeyPRF(), XMSSUtil.toBytesBigEndian(index, 32));
                byte[] bArrHMsg = this.wotsPlus.getKhf().HMsg(Arrays.concatenate(bArrPRF, this.privateKey.getRoot(), XMSSUtil.toBytesBigEndian(index, this.params.getTreeDigestSize())), bArr);
                this.hasGenerated = true;
                XMSSMTSignature xMSSMTSignatureBuild = new XMSSMTSignature.Builder(this.params).withIndex(index).withRandom(bArrPRF).build();
                long treeIndex = XMSSUtil.getTreeIndex(index, height);
                int leafIndex = XMSSUtil.getLeafIndex(index, height);
                this.wotsPlus.importKeys(new byte[this.params.getTreeDigestSize()], this.privateKey.getPublicSeed());
                OTSHashAddress oTSHashAddress = (OTSHashAddress) new OTSHashAddress.Builder().withTreeAddress(treeIndex).withOTSAddress(leafIndex).build();
                if (bDSState.get(0) == null || leafIndex == 0) {
                    bDSState.put(0, new BDS(this.xmssParams, this.privateKey.getPublicSeed(), this.privateKey.getSecretKeySeed(), oTSHashAddress));
                }
                xMSSMTSignatureBuild.getReducedSignatures().add(new XMSSReducedSignature.Builder(this.xmssParams).withWOTSPlusSignature(wotsSign(bArrHMsg, oTSHashAddress)).withAuthPath(bDSState.get(0).getAuthenticationPath()).build());
                for (int i = 1; i < this.params.getLayers(); i++) {
                    XMSSNode root = bDSState.get(i - 1).getRoot();
                    int leafIndex2 = XMSSUtil.getLeafIndex(treeIndex, height);
                    treeIndex = XMSSUtil.getTreeIndex(treeIndex, height);
                    OTSHashAddress oTSHashAddress2 = (OTSHashAddress) new OTSHashAddress.Builder().withLayerAddress(i).withTreeAddress(treeIndex).withOTSAddress(leafIndex2).build();
                    WOTSPlusSignature wOTSPlusSignatureWotsSign = wotsSign(root.getValue(), oTSHashAddress2);
                    if (bDSState.get(i) == null || XMSSUtil.isNewBDSInitNeeded(index, height, i)) {
                        bDSState.put(i, new BDS(this.xmssParams, this.privateKey.getPublicSeed(), this.privateKey.getSecretKeySeed(), oTSHashAddress2));
                    }
                    xMSSMTSignatureBuild.getReducedSignatures().add(new XMSSReducedSignature.Builder(this.xmssParams).withWOTSPlusSignature(wOTSPlusSignatureWotsSign).withAuthPath(bDSState.get(i).getAuthenticationPath()).build());
                }
                byteArray = xMSSMTSignatureBuild.toByteArray();
                this.privateKey.rollKey();
            } catch (Throwable th) {
                this.privateKey.rollKey();
                throw th;
            }
        }
        return byteArray;
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public boolean verifySignature(byte[] bArr, byte[] bArr2) {
        if (bArr == null) {
            throw new NullPointerException("message == null");
        }
        if (bArr2 == null) {
            throw new NullPointerException("signature == null");
        }
        if (this.publicKey == null) {
            throw new NullPointerException("publicKey == null");
        }
        XMSSMTSignature xMSSMTSignatureBuild = new XMSSMTSignature.Builder(this.params).withSignature(bArr2).build();
        byte[] bArrHMsg = this.wotsPlus.getKhf().HMsg(Arrays.concatenate(xMSSMTSignatureBuild.getRandom(), this.publicKey.getRoot(), XMSSUtil.toBytesBigEndian(xMSSMTSignatureBuild.getIndex(), this.params.getTreeDigestSize())), bArr);
        long index = xMSSMTSignatureBuild.getIndex();
        int height = this.xmssParams.getHeight();
        long treeIndex = XMSSUtil.getTreeIndex(index, height);
        int leafIndex = XMSSUtil.getLeafIndex(index, height);
        this.wotsPlus.importKeys(new byte[this.params.getTreeDigestSize()], this.publicKey.getPublicSeed());
        XMSSNode rootNodeFromSignature = XMSSVerifierUtil.getRootNodeFromSignature(this.wotsPlus, height, bArrHMsg, xMSSMTSignatureBuild.getReducedSignatures().get(0), (OTSHashAddress) new OTSHashAddress.Builder().withTreeAddress(treeIndex).withOTSAddress(leafIndex).build(), leafIndex);
        for (int i = 1; i < this.params.getLayers(); i++) {
            XMSSReducedSignature xMSSReducedSignature = xMSSMTSignatureBuild.getReducedSignatures().get(i);
            int leafIndex2 = XMSSUtil.getLeafIndex(treeIndex, height);
            treeIndex = XMSSUtil.getTreeIndex(treeIndex, height);
            rootNodeFromSignature = XMSSVerifierUtil.getRootNodeFromSignature(this.wotsPlus, height, rootNodeFromSignature.getValue(), xMSSReducedSignature, (OTSHashAddress) new OTSHashAddress.Builder().withLayerAddress(i).withTreeAddress(treeIndex).withOTSAddress(leafIndex2).build(), leafIndex2);
        }
        return Arrays.constantTimeAreEqual(rootNodeFromSignature.getValue(), this.publicKey.getRoot());
    }

    private WOTSPlusSignature wotsSign(byte[] bArr, OTSHashAddress oTSHashAddress) {
        if (bArr.length != this.params.getTreeDigestSize()) {
            throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
        }
        if (oTSHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        this.wotsPlus.importKeys(this.wotsPlus.getWOTSPlusSecretKey(this.privateKey.getSecretKeySeed(), oTSHashAddress), this.privateKey.getPublicSeed());
        return this.wotsPlus.sign(bArr, oTSHashAddress);
    }

    public long getUsagesRemaining() {
        return this.privateKey.getUsagesRemaining();
    }

    @Override // org.bouncycastle.pqc.crypto.StateAwareMessageSigner
    public AsymmetricKeyParameter getUpdatedPrivateKey() {
        if (this.hasGenerated) {
            XMSSMTPrivateKeyParameters xMSSMTPrivateKeyParameters = this.privateKey;
            this.privateKey = null;
            return xMSSMTPrivateKeyParameters;
        }
        XMSSMTPrivateKeyParameters xMSSMTPrivateKeyParameters2 = this.privateKey;
        if (xMSSMTPrivateKeyParameters2 != null) {
            this.privateKey = this.privateKey.getNextKey();
        }
        return xMSSMTPrivateKeyParameters2;
    }
}
