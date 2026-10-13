package org.bouncycastle.pqc.crypto.mldsa;

import java.io.IOException;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.ParametersWithContext;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.DigestUtils;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mldsa/HashMLDSASigner.class */
public class HashMLDSASigner implements Signer {
    private static final byte[] EMPTY_CONTEXT = new byte[0];
    private MLDSAPublicKeyParameters pubKey;
    private MLDSAPrivateKeyParameters privKey;
    private SecureRandom random;
    private MLDSAEngine engine;
    private Digest digest;
    private byte[] digestOIDEncoding;

    @Override // org.bouncycastle.crypto.Signer
    public void init(boolean z, CipherParameters cipherParameters) {
        MLDSAParameters parameters;
        byte[] context = EMPTY_CONTEXT;
        if (cipherParameters instanceof ParametersWithContext) {
            ParametersWithContext parametersWithContext = (ParametersWithContext) cipherParameters;
            context = parametersWithContext.getContext();
            cipherParameters = parametersWithContext.getParameters();
            if (context.length > 255) {
                throw new IllegalArgumentException("context too long");
            }
        }
        if (z) {
            this.pubKey = null;
            if (cipherParameters instanceof ParametersWithRandom) {
                ParametersWithRandom parametersWithRandom = (ParametersWithRandom) cipherParameters;
                this.privKey = (MLDSAPrivateKeyParameters) parametersWithRandom.getParameters();
                this.random = parametersWithRandom.getRandom();
            } else {
                this.privKey = (MLDSAPrivateKeyParameters) cipherParameters;
                this.random = null;
            }
            parameters = this.privKey.getParameters();
            this.engine = parameters.getEngine(this.random);
            this.engine.initSign(this.privKey.tr, true, context);
        } else {
            this.pubKey = (MLDSAPublicKeyParameters) cipherParameters;
            this.privKey = null;
            this.random = null;
            parameters = this.pubKey.getParameters();
            this.engine = parameters.getEngine(null);
            this.engine.initVerify(this.pubKey.rho, this.pubKey.t1, true, context);
        }
        initDigest(parameters);
    }

    private void initDigest(MLDSAParameters mLDSAParameters) {
        this.digest = createDigest(mLDSAParameters);
        try {
            this.digestOIDEncoding = DigestUtils.getDigestOid(this.digest.getAlgorithmName()).getEncoded("DER");
        } catch (IOException e) {
            throw new IllegalStateException("oid encoding failed: " + e.getMessage());
        }
    }

    @Override // org.bouncycastle.crypto.Signer
    public void update(byte b) {
        this.digest.update(b);
    }

    @Override // org.bouncycastle.crypto.Signer
    public void update(byte[] bArr, int i, int i2) {
        this.digest.update(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Signer
    public byte[] generateSignature() throws DataLengthException, CryptoException {
        SHAKEDigest sHAKEDigestFinishPreHash = finishPreHash();
        byte[] bArr = new byte[32];
        if (this.random != null) {
            this.random.nextBytes(bArr);
        }
        return this.engine.generateSignature(sHAKEDigestFinishPreHash, this.privKey.rho, this.privKey.k, this.privKey.t0, this.privKey.s1, this.privKey.s2, bArr);
    }

    @Override // org.bouncycastle.crypto.Signer
    public boolean verifySignature(byte[] bArr) {
        return this.engine.verifyInternal(bArr, bArr.length, finishPreHash(), this.pubKey.rho, this.pubKey.t1);
    }

    @Override // org.bouncycastle.crypto.Signer
    public void reset() {
        this.digest.reset();
    }

    private SHAKEDigest finishPreHash() {
        byte[] bArr = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(bArr, 0);
        SHAKEDigest shake256Digest = this.engine.getShake256Digest();
        shake256Digest.update(this.digestOIDEncoding, 0, this.digestOIDEncoding.length);
        shake256Digest.update(bArr, 0, bArr.length);
        return shake256Digest;
    }

    private static Digest createDigest(MLDSAParameters mLDSAParameters) {
        switch (mLDSAParameters.getType()) {
            case 0:
            case 1:
                return new SHA512Digest();
            default:
                throw new IllegalArgumentException("unknown parameters type");
        }
    }
}
