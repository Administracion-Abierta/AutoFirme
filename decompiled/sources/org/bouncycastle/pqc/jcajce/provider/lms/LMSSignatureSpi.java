package org.bouncycastle.pqc.jcajce.provider.lms;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.pqc.crypto.ExhaustedPrivateKeyException;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.pqc.crypto.lms.LMSContext;
import org.bouncycastle.pqc.crypto.lms.LMSContextBasedSigner;
import org.bouncycastle.pqc.crypto.lms.LMSContextBasedVerifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/lms/LMSSignatureSpi.class */
public class LMSSignatureSpi extends Signature {
    private Digest digest;
    private MessageSigner signer;
    private SecureRandom random;
    private LMSContextBasedSigner lmOtsSigner;
    private LMSContextBasedVerifier lmOtsVerifier;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/lms/LMSSignatureSpi$generic.class */
    public static class generic extends LMSSignatureSpi {
        public generic() {
            super("LMS", new NullDigest());
        }
    }

    protected LMSSignatureSpi(String str) {
        super(str);
    }

    protected LMSSignatureSpi(String str, Digest digest) {
        super(str);
        this.digest = digest;
    }

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        if (!(publicKey instanceof BCLMSPublicKey)) {
            throw new InvalidKeyException("unknown public key passed to LMS");
        }
        this.digest = new NullDigest();
        this.digest.reset();
        this.lmOtsVerifier = (LMSContextBasedVerifier) ((BCLMSPublicKey) publicKey).getKeyParams();
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException {
        this.random = secureRandom;
        engineInitSign(privateKey);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (!(privateKey instanceof BCLMSPrivateKey)) {
            throw new InvalidKeyException("unknown private key passed to LMS");
        }
        this.lmOtsSigner = (LMSContextBasedSigner) ((BCLMSPrivateKey) privateKey).getKeyParams();
        if (this.lmOtsSigner.getUsagesRemaining() == 0) {
            throw new InvalidKeyException("private key exhausted");
        }
        this.digest = null;
    }

    @Override // java.security.SignatureSpi
    protected void engineUpdate(byte b) throws SignatureException {
        if (this.digest == null) {
            this.digest = getSigner();
        }
        this.digest.update(b);
    }

    @Override // java.security.SignatureSpi
    protected void engineUpdate(byte[] bArr, int i, int i2) throws SignatureException {
        if (this.digest == null) {
            this.digest = getSigner();
        }
        this.digest.update(bArr, i, i2);
    }

    private Digest getSigner() throws SignatureException {
        try {
            return this.lmOtsSigner.generateLMSContext();
        } catch (ExhaustedPrivateKeyException e) {
            throw new SignatureException(e.getMessage(), e);
        }
    }

    @Override // java.security.SignatureSpi
    protected byte[] engineSign() throws SignatureException {
        if (this.digest == null) {
            this.digest = getSigner();
        }
        try {
            byte[] bArrGenerateSignature = this.lmOtsSigner.generateSignature((LMSContext) this.digest);
            this.digest = null;
            return bArrGenerateSignature;
        } catch (Exception e) {
            if (e instanceof IllegalStateException) {
                throw new SignatureException(e.getMessage(), e);
            }
            throw new SignatureException(e.toString(), e);
        }
    }

    @Override // java.security.SignatureSpi
    protected boolean engineVerify(byte[] bArr) throws SignatureException {
        LMSContext lMSContextGenerateLMSContext = this.lmOtsVerifier.generateLMSContext(bArr);
        byte[] digestResult = DigestUtil.getDigestResult(this.digest);
        lMSContextGenerateLMSContext.update(digestResult, 0, digestResult.length);
        return this.lmOtsVerifier.verify(lMSContextGenerateLMSContext);
    }

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(AlgorithmParameterSpec algorithmParameterSpec) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(String str, Object obj) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override // java.security.SignatureSpi
    protected Object engineGetParameter(String str) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }
}
