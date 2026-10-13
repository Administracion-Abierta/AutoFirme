package org.spongycastle.pqc.jcajce.provider.rainbow;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.SHA224Digest;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.digests.SHA384Digest;
import org.spongycastle.crypto.digests.SHA512Digest;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.pqc.crypto.rainbow.RainbowSigner;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/rainbow/SignatureSpi.class */
public class SignatureSpi extends java.security.SignatureSpi {
    private Digest digest;
    private RainbowSigner signer;
    private SecureRandom random;

    protected SignatureSpi(Digest digest, RainbowSigner signer) {
        this.digest = digest;
        this.signer = signer;
    }

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        CipherParameters param = RainbowKeysToParams.generatePublicKeyParameter(publicKey);
        this.digest.reset();
        this.signer.init(false, param);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey, SecureRandom random) throws InvalidKeyException {
        this.random = random;
        engineInitSign(privateKey);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        CipherParameters param = RainbowKeysToParams.generatePrivateKeyParameter(privateKey);
        if (this.random != null) {
            param = new ParametersWithRandom(param, this.random);
        }
        this.digest.reset();
        this.signer.init(true, param);
    }

    @Override // java.security.SignatureSpi
    protected void engineUpdate(byte b) throws SignatureException {
        this.digest.update(b);
    }

    @Override // java.security.SignatureSpi
    protected void engineUpdate(byte[] b, int off, int len) throws SignatureException {
        this.digest.update(b, off, len);
    }

    @Override // java.security.SignatureSpi
    protected byte[] engineSign() throws SignatureException {
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        try {
            byte[] sig = this.signer.generateSignature(hash);
            return sig;
        } catch (Exception e) {
            throw new SignatureException(e.toString());
        }
    }

    @Override // java.security.SignatureSpi
    protected boolean engineVerify(byte[] sigBytes) throws SignatureException {
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        return this.signer.verifySignature(hash, sigBytes);
    }

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(AlgorithmParameterSpec params) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(String param, Object value) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override // java.security.SignatureSpi
    protected Object engineGetParameter(String param) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/rainbow/SignatureSpi$withSha224.class */
    public static class withSha224 extends SignatureSpi {
        public withSha224() {
            super(new SHA224Digest(), new RainbowSigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/rainbow/SignatureSpi$withSha256.class */
    public static class withSha256 extends SignatureSpi {
        public withSha256() {
            super(new SHA256Digest(), new RainbowSigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/rainbow/SignatureSpi$withSha384.class */
    public static class withSha384 extends SignatureSpi {
        public withSha384() {
            super(new SHA384Digest(), new RainbowSigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/rainbow/SignatureSpi$withSha512.class */
    public static class withSha512 extends SignatureSpi {
        public withSha512() {
            super(new SHA512Digest(), new RainbowSigner());
        }
    }
}
