package org.spongycastle.pqc.jcajce.provider.sphincs;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.SHA3Digest;
import org.spongycastle.crypto.digests.SHA512Digest;
import org.spongycastle.crypto.digests.SHA512tDigest;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.pqc.crypto.sphincs.SPHINCS256Signer;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/sphincs/SignatureSpi.class */
public class SignatureSpi extends java.security.SignatureSpi {
    private Digest digest;
    private SPHINCS256Signer signer;
    private SecureRandom random;

    protected SignatureSpi(Digest digest, SPHINCS256Signer signer) {
        this.digest = digest;
        this.signer = signer;
    }

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        if (publicKey instanceof BCSphincs256PublicKey) {
            CipherParameters param = ((BCSphincs256PublicKey) publicKey).getKeyParams();
            this.digest.reset();
            this.signer.init(false, param);
            return;
        }
        throw new InvalidKeyException("unknown public key passed to SPHINCS-256");
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey, SecureRandom random) throws InvalidKeyException {
        this.random = random;
        engineInitSign(privateKey);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof BCSphincs256PrivateKey) {
            CipherParameters param = ((BCSphincs256PrivateKey) privateKey).getKeyParams();
            if (this.random != null) {
                param = new ParametersWithRandom(param, this.random);
            }
            this.digest.reset();
            this.signer.init(true, param);
            return;
        }
        throw new InvalidKeyException("unknown private key passed to SPHINCS-256");
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

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/sphincs/SignatureSpi$withSha512.class */
    public static class withSha512 extends SignatureSpi {
        public withSha512() {
            super(new SHA512Digest(), new SPHINCS256Signer(new SHA512tDigest(256), new SHA512Digest()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/sphincs/SignatureSpi$withSha3_512.class */
    public static class withSha3_512 extends SignatureSpi {
        public withSha3_512() {
            super(new SHA3Digest(512), new SPHINCS256Signer(new SHA3Digest(256), new SHA3Digest(512)));
        }
    }
}
