package org.spongycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import java.text.ParseException;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/XMSS.class */
public class XMSS {
    private final XMSSParameters params;
    private WOTSPlus wotsPlus;
    private SecureRandom prng;
    private XMSSPrivateKeyParameters privateKey;
    private XMSSPublicKeyParameters publicKey;

    public XMSS(XMSSParameters params, SecureRandom prng) {
        if (params == null) {
            throw new NullPointerException("params == null");
        }
        this.params = params;
        this.wotsPlus = params.getWOTSPlus();
        this.prng = prng;
    }

    public void generateKeys() {
        XMSSKeyPairGenerator kpGen = new XMSSKeyPairGenerator();
        kpGen.init(new XMSSKeyGenerationParameters(getParams(), this.prng));
        AsymmetricCipherKeyPair kp = kpGen.generateKeyPair();
        this.privateKey = (XMSSPrivateKeyParameters) kp.getPrivate();
        this.publicKey = (XMSSPublicKeyParameters) kp.getPublic();
        this.wotsPlus.importKeys(new byte[this.params.getDigestSize()], this.privateKey.getPublicSeed());
    }

    void importState(XMSSPrivateKeyParameters privateKey, XMSSPublicKeyParameters publicKey) {
        if (!Arrays.areEqual(privateKey.getRoot(), publicKey.getRoot())) {
            throw new IllegalStateException("root of private key and public key do not match");
        }
        if (!Arrays.areEqual(privateKey.getPublicSeed(), publicKey.getPublicSeed())) {
            throw new IllegalStateException("public seed of private key and public key do not match");
        }
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.wotsPlus.importKeys(new byte[this.params.getDigestSize()], this.privateKey.getPublicSeed());
    }

    public void importState(byte[] privateKey, byte[] publicKey) {
        if (privateKey == null) {
            throw new NullPointerException("privateKey == null");
        }
        if (publicKey == null) {
            throw new NullPointerException("publicKey == null");
        }
        XMSSPrivateKeyParameters tmpPrivateKey = new XMSSPrivateKeyParameters.Builder(this.params).withPrivateKey(privateKey, getParams()).build();
        XMSSPublicKeyParameters tmpPublicKey = new XMSSPublicKeyParameters.Builder(this.params).withPublicKey(publicKey).build();
        if (!Arrays.areEqual(tmpPrivateKey.getRoot(), tmpPublicKey.getRoot())) {
            throw new IllegalStateException("root of private key and public key do not match");
        }
        if (!Arrays.areEqual(tmpPrivateKey.getPublicSeed(), tmpPublicKey.getPublicSeed())) {
            throw new IllegalStateException("public seed of private key and public key do not match");
        }
        this.privateKey = tmpPrivateKey;
        this.publicKey = tmpPublicKey;
        this.wotsPlus.importKeys(new byte[this.params.getDigestSize()], this.privateKey.getPublicSeed());
    }

    public byte[] sign(byte[] message) {
        if (message == null) {
            throw new NullPointerException("message == null");
        }
        XMSSSigner signer = new XMSSSigner();
        signer.init(true, this.privateKey);
        byte[] signature = signer.generateSignature(message);
        this.privateKey = (XMSSPrivateKeyParameters) signer.getUpdatedPrivateKey();
        importState(this.privateKey, this.publicKey);
        return signature;
    }

    public boolean verifySignature(byte[] message, byte[] signature, byte[] publicKey) throws ParseException {
        if (message == null) {
            throw new NullPointerException("message == null");
        }
        if (signature == null) {
            throw new NullPointerException("signature == null");
        }
        if (publicKey == null) {
            throw new NullPointerException("publicKey == null");
        }
        XMSSSigner signer = new XMSSSigner();
        signer.init(false, new XMSSPublicKeyParameters.Builder(getParams()).withPublicKey(publicKey).build());
        return signer.verifySignature(message, signature);
    }

    public byte[] exportPrivateKey() {
        return this.privateKey.toByteArray();
    }

    public byte[] exportPublicKey() {
        return this.publicKey.toByteArray();
    }

    protected WOTSPlusSignature wotsSign(byte[] messageDigest, OTSHashAddress otsHashAddress) {
        if (messageDigest.length != this.params.getDigestSize()) {
            throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
        }
        if (otsHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        this.wotsPlus.importKeys(this.wotsPlus.getWOTSPlusSecretKey(this.privateKey.getSecretKeySeed(), otsHashAddress), getPublicSeed());
        return this.wotsPlus.sign(messageDigest, otsHashAddress);
    }

    public XMSSParameters getParams() {
        return this.params;
    }

    protected WOTSPlus getWOTSPlus() {
        return this.wotsPlus;
    }

    public byte[] getRoot() {
        return this.privateKey.getRoot();
    }

    protected void setRoot(byte[] root) {
        this.privateKey = new XMSSPrivateKeyParameters.Builder(this.params).withSecretKeySeed(this.privateKey.getSecretKeySeed()).withSecretKeyPRF(this.privateKey.getSecretKeyPRF()).withPublicSeed(getPublicSeed()).withRoot(root).withBDSState(this.privateKey.getBDSState()).build();
        this.publicKey = new XMSSPublicKeyParameters.Builder(this.params).withRoot(root).withPublicSeed(getPublicSeed()).build();
    }

    public int getIndex() {
        return this.privateKey.getIndex();
    }

    protected void setIndex(int index) {
        this.privateKey = new XMSSPrivateKeyParameters.Builder(this.params).withSecretKeySeed(this.privateKey.getSecretKeySeed()).withSecretKeyPRF(this.privateKey.getSecretKeyPRF()).withPublicSeed(this.privateKey.getPublicSeed()).withRoot(this.privateKey.getRoot()).withBDSState(this.privateKey.getBDSState()).build();
    }

    public byte[] getPublicSeed() {
        return this.privateKey.getPublicSeed();
    }

    protected void setPublicSeed(byte[] publicSeed) {
        this.privateKey = new XMSSPrivateKeyParameters.Builder(this.params).withSecretKeySeed(this.privateKey.getSecretKeySeed()).withSecretKeyPRF(this.privateKey.getSecretKeyPRF()).withPublicSeed(publicSeed).withRoot(getRoot()).withBDSState(this.privateKey.getBDSState()).build();
        this.publicKey = new XMSSPublicKeyParameters.Builder(this.params).withRoot(getRoot()).withPublicSeed(publicSeed).build();
        this.wotsPlus.importKeys(new byte[this.params.getDigestSize()], publicSeed);
    }

    public XMSSPrivateKeyParameters getPrivateKey() {
        return this.privateKey;
    }
}
