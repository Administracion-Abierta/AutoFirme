package org.spongycastle.pqc.crypto.xmss;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.pqc.crypto.StateAwareMessageSigner;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/XMSSSigner.class */
public class XMSSSigner implements StateAwareMessageSigner {
    private XMSSPrivateKeyParameters privateKey;
    private XMSSPrivateKeyParameters nextKeyGenerator;
    private XMSSPublicKeyParameters publicKey;
    private XMSSParameters params;
    private KeyedHashFunctions khf;
    private boolean initSign;
    private boolean hasGenerated;

    @Override // org.spongycastle.pqc.crypto.MessageSigner
    public void init(boolean forSigning, CipherParameters param) {
        if (forSigning) {
            this.initSign = true;
            this.hasGenerated = false;
            this.privateKey = (XMSSPrivateKeyParameters) param;
            this.nextKeyGenerator = this.privateKey;
            this.params = this.privateKey.getParameters();
            this.khf = this.params.getWOTSPlus().getKhf();
            return;
        }
        this.initSign = false;
        this.publicKey = (XMSSPublicKeyParameters) param;
        this.params = this.publicKey.getParameters();
        this.khf = this.params.getWOTSPlus().getKhf();
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
            if (this.privateKey.getBDSState().getAuthenticationPath().isEmpty()) {
                throw new IllegalStateException("not initialized");
            }
            int index = this.privateKey.getIndex();
            if (!XMSSUtil.isIndexValid(this.params.getHeight(), index)) {
                throw new IllegalStateException("index out of bounds");
            }
            byte[] random = this.khf.PRF(this.privateKey.getSecretKeyPRF(), XMSSUtil.toBytesBigEndian(index, 32));
            byte[] concatenated = Arrays.concatenate(random, this.privateKey.getRoot(), XMSSUtil.toBytesBigEndian(index, this.params.getDigestSize()));
            byte[] messageDigest = this.khf.HMsg(concatenated, message);
            OTSHashAddress otsHashAddress = (OTSHashAddress) new OTSHashAddress.Builder().withOTSAddress(index).build();
            WOTSPlusSignature wotsPlusSignature = wotsSign(messageDigest, otsHashAddress);
            XMSSSignature signature = (XMSSSignature) new XMSSSignature.Builder(this.params).withIndex(index).withRandom(random).withWOTSPlusSignature(wotsPlusSignature).withAuthPath(this.privateKey.getBDSState().getAuthenticationPath()).build();
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
        XMSSSignature sig = new XMSSSignature.Builder(this.params).withSignature(signature).build();
        int index = sig.getIndex();
        this.params.getWOTSPlus().importKeys(new byte[this.params.getDigestSize()], this.publicKey.getPublicSeed());
        byte[] concatenated = Arrays.concatenate(sig.getRandom(), this.publicKey.getRoot(), XMSSUtil.toBytesBigEndian(index, this.params.getDigestSize()));
        byte[] messageDigest = this.khf.HMsg(concatenated, message);
        int xmssHeight = this.params.getHeight();
        int indexLeaf = XMSSUtil.getLeafIndex(index, xmssHeight);
        OTSHashAddress otsHashAddress = (OTSHashAddress) new OTSHashAddress.Builder().withOTSAddress(index).build();
        XMSSNode rootNodeFromSignature = XMSSVerifierUtil.getRootNodeFromSignature(this.params.getWOTSPlus(), xmssHeight, messageDigest, sig, otsHashAddress, indexLeaf);
        return Arrays.constantTimeAreEqual(rootNodeFromSignature.getValue(), this.publicKey.getRoot());
    }

    @Override // org.spongycastle.pqc.crypto.StateAwareMessageSigner
    public AsymmetricKeyParameter getUpdatedPrivateKey() {
        if (this.hasGenerated) {
            XMSSPrivateKeyParameters privKey = this.privateKey;
            this.privateKey = null;
            this.nextKeyGenerator = null;
            return privKey;
        }
        XMSSPrivateKeyParameters privKey2 = this.nextKeyGenerator.getNextKey();
        this.nextKeyGenerator = null;
        return privKey2;
    }

    private WOTSPlusSignature wotsSign(byte[] messageDigest, OTSHashAddress otsHashAddress) {
        if (messageDigest.length != this.params.getDigestSize()) {
            throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
        }
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        this.params.getWOTSPlus().importKeys(this.params.getWOTSPlus().getWOTSPlusSecretKey(this.privateKey.getSecretKeySeed(), otsHashAddress), this.privateKey.getPublicSeed());
        return this.params.getWOTSPlus().sign(messageDigest, otsHashAddress);
    }
}
