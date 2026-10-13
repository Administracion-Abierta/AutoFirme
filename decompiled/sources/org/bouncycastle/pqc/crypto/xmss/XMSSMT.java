package org.bouncycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import java.text.ParseException;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/XMSSMT.class */
public final class XMSSMT {
    private XMSSMTParameters params;
    private XMSSParameters xmssParams;
    private SecureRandom prng;
    private XMSSMTPrivateKeyParameters privateKey;
    private XMSSMTPublicKeyParameters publicKey;

    public XMSSMT(XMSSMTParameters xMSSMTParameters, SecureRandom secureRandom) {
        if (xMSSMTParameters == null) {
            throw new NullPointerException("params == null");
        }
        this.params = xMSSMTParameters;
        this.xmssParams = xMSSMTParameters.getXMSSParameters();
        this.prng = secureRandom;
        this.privateKey = new XMSSMTPrivateKeyParameters.Builder(xMSSMTParameters).build();
        this.publicKey = new XMSSMTPublicKeyParameters.Builder(xMSSMTParameters).build();
    }

    public void generateKeys() {
        XMSSMTKeyPairGenerator xMSSMTKeyPairGenerator = new XMSSMTKeyPairGenerator();
        xMSSMTKeyPairGenerator.init(new XMSSMTKeyGenerationParameters(getParams(), this.prng));
        AsymmetricCipherKeyPair asymmetricCipherKeyPairGenerateKeyPair = xMSSMTKeyPairGenerator.generateKeyPair();
        this.privateKey = (XMSSMTPrivateKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPrivate();
        this.publicKey = (XMSSMTPublicKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPublic();
        importState(this.privateKey, this.publicKey);
    }

    private void importState(XMSSMTPrivateKeyParameters xMSSMTPrivateKeyParameters, XMSSMTPublicKeyParameters xMSSMTPublicKeyParameters) {
        this.xmssParams.getWOTSPlus().importKeys(new byte[this.params.getTreeDigestSize()], this.privateKey.getPublicSeed());
        this.privateKey = xMSSMTPrivateKeyParameters;
        this.publicKey = xMSSMTPublicKeyParameters;
    }

    public void importState(byte[] bArr, byte[] bArr2) {
        if (bArr == null) {
            throw new NullPointerException("privateKey == null");
        }
        if (bArr2 == null) {
            throw new NullPointerException("publicKey == null");
        }
        XMSSMTPrivateKeyParameters xMSSMTPrivateKeyParametersBuild = new XMSSMTPrivateKeyParameters.Builder(this.params).withPrivateKey(bArr).build();
        XMSSMTPublicKeyParameters xMSSMTPublicKeyParametersBuild = new XMSSMTPublicKeyParameters.Builder(this.params).withPublicKey(bArr2).build();
        if (!Arrays.areEqual(xMSSMTPrivateKeyParametersBuild.getRoot(), xMSSMTPublicKeyParametersBuild.getRoot())) {
            throw new IllegalStateException("root of private key and public key do not match");
        }
        if (!Arrays.areEqual(xMSSMTPrivateKeyParametersBuild.getPublicSeed(), xMSSMTPublicKeyParametersBuild.getPublicSeed())) {
            throw new IllegalStateException("public seed of private key and public key do not match");
        }
        this.xmssParams.getWOTSPlus().importKeys(new byte[this.params.getTreeDigestSize()], xMSSMTPrivateKeyParametersBuild.getPublicSeed());
        this.privateKey = xMSSMTPrivateKeyParametersBuild;
        this.publicKey = xMSSMTPublicKeyParametersBuild;
    }

    public byte[] sign(byte[] bArr) {
        if (bArr == null) {
            throw new NullPointerException("message == null");
        }
        XMSSMTSigner xMSSMTSigner = new XMSSMTSigner();
        xMSSMTSigner.init(true, this.privateKey);
        byte[] bArrGenerateSignature = xMSSMTSigner.generateSignature(bArr);
        this.privateKey = (XMSSMTPrivateKeyParameters) xMSSMTSigner.getUpdatedPrivateKey();
        importState(this.privateKey, this.publicKey);
        return bArrGenerateSignature;
    }

    public boolean verifySignature(byte[] bArr, byte[] bArr2, byte[] bArr3) throws ParseException {
        if (bArr == null) {
            throw new NullPointerException("message == null");
        }
        if (bArr2 == null) {
            throw new NullPointerException("signature == null");
        }
        if (bArr3 == null) {
            throw new NullPointerException("publicKey == null");
        }
        XMSSMTSigner xMSSMTSigner = new XMSSMTSigner();
        xMSSMTSigner.init(false, new XMSSMTPublicKeyParameters.Builder(getParams()).withPublicKey(bArr3).build());
        return xMSSMTSigner.verifySignature(bArr, bArr2);
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
