package org.spongycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import java.text.ParseException;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/XMSSMT.class */
public final class XMSSMT {
    private XMSSMTParameters params;
    private XMSSParameters xmssParams;
    private SecureRandom prng;
    private XMSSMTPrivateKeyParameters privateKey;
    private XMSSMTPublicKeyParameters publicKey;

    public XMSSMT(XMSSMTParameters params, SecureRandom prng) {
        if (params == null) {
            throw new NullPointerException("params == null");
        }
        this.params = params;
        this.xmssParams = params.getXMSSParameters();
        this.prng = prng;
        this.privateKey = new XMSSMTPrivateKeyParameters.Builder(params).build();
        this.publicKey = new XMSSMTPublicKeyParameters.Builder(params).build();
    }

    public void generateKeys() {
        XMSSMTKeyPairGenerator kpGen = new XMSSMTKeyPairGenerator();
        kpGen.init(new XMSSMTKeyGenerationParameters(getParams(), this.prng));
        AsymmetricCipherKeyPair kp = kpGen.generateKeyPair();
        this.privateKey = (XMSSMTPrivateKeyParameters) kp.getPrivate();
        this.publicKey = (XMSSMTPublicKeyParameters) kp.getPublic();
        importState(this.privateKey, this.publicKey);
    }

    private void importState(XMSSMTPrivateKeyParameters privateKey, XMSSMTPublicKeyParameters publicKey) {
        this.xmssParams.getWOTSPlus().importKeys(new byte[this.params.getDigestSize()], this.privateKey.getPublicSeed());
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public void importState(byte[] privateKey, byte[] publicKey) {
        if (privateKey == null) {
            throw new NullPointerException("privateKey == null");
        }
        if (publicKey == null) {
            throw new NullPointerException("publicKey == null");
        }
        XMSSMTPrivateKeyParameters xmssMTPrivateKey = new XMSSMTPrivateKeyParameters.Builder(this.params).withPrivateKey(privateKey, this.xmssParams).build();
        XMSSMTPublicKeyParameters xmssMTPublicKey = new XMSSMTPublicKeyParameters.Builder(this.params).withPublicKey(publicKey).build();
        if (!Arrays.areEqual(xmssMTPrivateKey.getRoot(), xmssMTPublicKey.getRoot())) {
            throw new IllegalStateException("root of private key and public key do not match");
        }
        if (!Arrays.areEqual(xmssMTPrivateKey.getPublicSeed(), xmssMTPublicKey.getPublicSeed())) {
            throw new IllegalStateException("public seed of private key and public key do not match");
        }
        this.xmssParams.getWOTSPlus().importKeys(new byte[this.params.getDigestSize()], xmssMTPrivateKey.getPublicSeed());
        this.privateKey = xmssMTPrivateKey;
        this.publicKey = xmssMTPublicKey;
    }

    public byte[] sign(byte[] message) {
        if (message == null) {
            throw new NullPointerException("message == null");
        }
        XMSSMTSigner signer = new XMSSMTSigner();
        signer.init(true, this.privateKey);
        byte[] signature = signer.generateSignature(message);
        this.privateKey = (XMSSMTPrivateKeyParameters) signer.getUpdatedPrivateKey();
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
        XMSSMTSigner signer = new XMSSMTSigner();
        signer.init(false, new XMSSMTPublicKeyParameters.Builder(getParams()).withPublicKey(publicKey).build());
        return signer.verifySignature(message, signature);
    }

    public byte[] exportPrivateKey() {
        return this.privateKey.toByteArray();
    }

    public byte[] exportPublicKey() {
        return this.publicKey.toByteArray();
    }

    public XMSSMTParameters getParams() {
        return this.params;
    }

    public byte[] getPublicSeed() {
        return this.privateKey.getPublicSeed();
    }

    protected XMSSParameters getXMSS() {
        return this.xmssParams;
    }
}
