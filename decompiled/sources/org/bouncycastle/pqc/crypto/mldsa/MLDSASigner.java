package org.bouncycastle.pqc.crypto.mldsa;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.ParametersWithContext;
import org.bouncycastle.crypto.params.ParametersWithRandom;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mldsa/MLDSASigner.class */
public class MLDSASigner implements Signer {
    private static final byte[] EMPTY_CONTEXT = new byte[0];
    private MLDSAPublicKeyParameters pubKey;
    private MLDSAPrivateKeyParameters privKey;
    private SecureRandom random;
    private MLDSAEngine engine;
    private SHAKEDigest msgDigest;

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
            this.engine.initSign(this.privKey.tr, false, context);
        } else {
            this.pubKey = (MLDSAPublicKeyParameters) cipherParameters;
            this.privKey = null;
            this.random = null;
            parameters = this.pubKey.getParameters();
            this.engine = parameters.getEngine(null);
            this.engine.initVerify(this.pubKey.rho, this.pubKey.t1, false, context);
        }
        if (parameters.isPreHash()) {
            throw new IllegalArgumentException("\"pure\" ml-dsa must use non pre-hash parameters");
        }
        reset();
    }

    @Override // org.bouncycastle.crypto.Signer
    public void update(byte b) {
        this.msgDigest.update(b);
    }

    @Override // org.bouncycastle.crypto.Signer
    public void update(byte[] bArr, int i, int i2) {
        this.msgDigest.update(bArr, i, i2);
    }

    @Override // org.bouncycastle.crypto.Signer
    public byte[] generateSignature() throws DataLengthException, CryptoException {
        byte[] bArr = new byte[32];
        if (this.random != null) {
            this.random.nextBytes(bArr);
        }
        byte[] bArrGenerateSignature = this.engine.generateSignature(this.msgDigest, this.privKey.rho, this.privKey.k, this.privKey.t0, this.privKey.s1, this.privKey.s2, bArr);
        reset();
        return bArrGenerateSignature;
    }

    @Override // org.bouncycastle.crypto.Signer
    public boolean verifySignature(byte[] bArr) {
        boolean zVerifyInternal = this.engine.verifyInternal(bArr, bArr.length, this.msgDigest, this.pubKey.rho, this.pubKey.t1);
        reset();
        return zVerifyInternal;
    }

    @Override // org.bouncycastle.crypto.Signer
    public void reset() {
        this.msgDigest = this.engine.getShake256Digest();
    }

    protected byte[] internalGenerateSignature(byte[] bArr, byte[] bArr2) {
        MLDSAEngine engine = this.privKey.getParameters().getEngine(this.random);
        engine.initSign(this.privKey.tr, false, null);
        return engine.signInternal(bArr, bArr.length, this.privKey.rho, this.privKey.k, this.privKey.t0, this.privKey.s1, this.privKey.s2, bArr2);
    }

    protected boolean internalVerifySignature(byte[] bArr, byte[] bArr2) {
        MLDSAEngine engine = this.pubKey.getParameters().getEngine(this.random);
        engine.initVerify(this.pubKey.rho, this.pubKey.t1, false, null);
        SHAKEDigest shake256Digest = engine.getShake256Digest();
        shake256Digest.update(bArr, 0, bArr.length);
        return engine.verifyInternal(bArr2, bArr2.length, shake256Digest, this.pubKey.rho, this.pubKey.t1);
    }
}
