package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.ExhaustedPrivateKeyException;
import org.bouncycastle.pqc.crypto.StateAwareMessageSigner;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/XMSSSigner.class */
public class XMSSSigner implements StateAwareMessageSigner {
    private XMSSPrivateKeyParameters privateKey;
    private XMSSPublicKeyParameters publicKey;
    private XMSSParameters params;
    private WOTSPlus wotsPlus;
    private KeyedHashFunctions khf;
    private boolean initSign;
    private boolean hasGenerated;

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public void init(boolean z, CipherParameters cipherParameters) {
        if (z) {
            this.initSign = true;
            this.hasGenerated = false;
            this.privateKey = (XMSSPrivateKeyParameters) cipherParameters;
            this.params = this.privateKey.getParameters();
        } else {
            this.initSign = false;
            this.publicKey = (XMSSPublicKeyParameters) cipherParameters;
            this.params = this.publicKey.getParameters();
        }
        this.wotsPlus = this.params.getWOTSPlus();
        this.khf = this.wotsPlus.getKhf();
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
                throw new ExhaustedPrivateKeyException("no usages of private key remaining");
            }
            if (this.privateKey.getBDSState().getAuthenticationPath().isEmpty()) {
                throw new IllegalStateException("not initialized");
            }
            try {
                int index = this.privateKey.getIndex();
                this.hasGenerated = true;
                byte[] bArrPRF = this.khf.PRF(this.privateKey.getSecretKeyPRF(), XMSSUtil.toBytesBigEndian(index, 32));
                byteArray = new XMSSSignature.Builder(this.params).withIndex(index).withRandom(bArrPRF).withWOTSPlusSignature(wotsSign(this.khf.HMsg(Arrays.concatenate(bArrPRF, this.privateKey.getRoot(), XMSSUtil.toBytesBigEndian(index, this.params.getTreeDigestSize())), bArr), (OTSHashAddress) new OTSHashAddress.Builder().withOTSAddress(index).build())).withAuthPath(this.privateKey.getBDSState().getAuthenticationPath()).build().toByteArray();
                this.privateKey.getBDSState().markUsed();
                this.privateKey.rollKey();
            } catch (Throwable th) {
                this.privateKey.getBDSState().markUsed();
                this.privateKey.rollKey();
                throw th;
            }
        }
        return byteArray;
    }

    public long getUsagesRemaining() {
        return this.privateKey.getUsagesRemaining();
    }

    @Override // org.bouncycastle.pqc.crypto.MessageSigner
    public boolean verifySignature(byte[] bArr, byte[] bArr2) {
        XMSSSignature xMSSSignatureBuild = new XMSSSignature.Builder(this.params).withSignature(bArr2).build();
        int index = xMSSSignatureBuild.getIndex();
        this.wotsPlus.importKeys(new byte[this.params.getTreeDigestSize()], this.publicKey.getPublicSeed());
        byte[] bArrHMsg = this.khf.HMsg(Arrays.concatenate(xMSSSignatureBuild.getRandom(), this.publicKey.getRoot(), XMSSUtil.toBytesBigEndian(index, this.params.getTreeDigestSize())), bArr);
        int height = this.params.getHeight();
        return Arrays.constantTimeAreEqual(XMSSVerifierUtil.getRootNodeFromSignature(this.wotsPlus, height, bArrHMsg, xMSSSignatureBuild, (OTSHashAddress) new OTSHashAddress.Builder().withOTSAddress(index).build(), XMSSUtil.getLeafIndex(index, height)).getValue(), this.publicKey.getRoot());
    }

    @Override // org.bouncycastle.pqc.crypto.StateAwareMessageSigner
    public AsymmetricKeyParameter getUpdatedPrivateKey() {
        synchronized (this.privateKey) {
            if (this.hasGenerated) {
                XMSSPrivateKeyParameters xMSSPrivateKeyParameters = this.privateKey;
                this.privateKey = null;
                return xMSSPrivateKeyParameters;
            }
            XMSSPrivateKeyParameters xMSSPrivateKeyParameters2 = this.privateKey;
            if (xMSSPrivateKeyParameters2 != null) {
                this.privateKey = this.privateKey.getNextKey();
            }
            return xMSSPrivateKeyParameters2;
        }
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
}
