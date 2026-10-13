package org.bouncycastle.jcajce.provider.asymmetric.mldsa;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseDeterministicOrRandomSignature;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.pqc.crypto.mldsa.HashMLDSASigner;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/HashSignatureSpi.class */
public class HashSignatureSpi extends BaseDeterministicOrRandomSignature {
    private HashMLDSASigner signer;
    private MLDSAParameters parameters;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/HashSignatureSpi$MLDSA.class */
    public static class MLDSA extends HashSignatureSpi {
        public MLDSA() {
            super(new HashMLDSASigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/HashSignatureSpi$MLDSA44.class */
    public static class MLDSA44 extends HashSignatureSpi {
        public MLDSA44() {
            super(new HashMLDSASigner(), MLDSAParameters.ml_dsa_44_with_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/HashSignatureSpi$MLDSA65.class */
    public static class MLDSA65 extends HashSignatureSpi {
        public MLDSA65() {
            super(new HashMLDSASigner(), MLDSAParameters.ml_dsa_65_with_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/HashSignatureSpi$MLDSA87.class */
    public static class MLDSA87 extends HashSignatureSpi {
        public MLDSA87() throws NoSuchAlgorithmException {
            super(new HashMLDSASigner(), MLDSAParameters.ml_dsa_87_with_sha512);
        }
    }

    protected HashSignatureSpi(HashMLDSASigner hashMLDSASigner) {
        super("HashMLDSA");
        this.signer = hashMLDSASigner;
        this.parameters = null;
    }

    protected HashSignatureSpi(HashMLDSASigner hashMLDSASigner, MLDSAParameters mLDSAParameters) {
        super(MLDSAParameterSpec.fromName(mLDSAParameters.getName()).getName());
        this.signer = hashMLDSASigner;
        this.parameters = mLDSAParameters;
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseDeterministicOrRandomSignature
    protected void verifyInit(PublicKey publicKey) throws InvalidKeyException {
        if (!(publicKey instanceof BCMLDSAPublicKey)) {
            throw new InvalidKeyException("unknown public key passed to ML-DSA");
        }
        BCMLDSAPublicKey bCMLDSAPublicKey = (BCMLDSAPublicKey) publicKey;
        this.keyParams = bCMLDSAPublicKey.getKeyParams();
        if (this.parameters != null) {
            String name = MLDSAParameterSpec.fromName(this.parameters.getName()).getName();
            if (!name.equals(bCMLDSAPublicKey.getAlgorithm())) {
                throw new InvalidKeyException("signature configured for " + name);
            }
        }
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseDeterministicOrRandomSignature
    protected void signInit(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException {
        this.appRandom = secureRandom;
        if (!(privateKey instanceof BCMLDSAPrivateKey)) {
            throw new InvalidKeyException("unknown private key passed to ML-DSA");
        }
        BCMLDSAPrivateKey bCMLDSAPrivateKey = (BCMLDSAPrivateKey) privateKey;
        this.keyParams = bCMLDSAPrivateKey.getKeyParams();
        if (this.parameters != null) {
            String name = MLDSAParameterSpec.fromName(this.parameters.getName()).getName();
            if (!name.equals(bCMLDSAPrivateKey.getAlgorithm())) {
                throw new InvalidKeyException("signature configured for " + name);
            }
        }
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseDeterministicOrRandomSignature
    protected void updateEngine(byte b) throws SignatureException {
        this.signer.update(b);
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseDeterministicOrRandomSignature
    protected void updateEngine(byte[] bArr, int i, int i2) throws SignatureException {
        this.signer.update(bArr, i, i2);
    }

    @Override // java.security.SignatureSpi
    protected byte[] engineSign() throws SignatureException {
        try {
            return this.signer.generateSignature();
        } catch (Exception e) {
            throw new SignatureException(e.toString());
        }
    }

    @Override // java.security.SignatureSpi
    protected boolean engineVerify(byte[] bArr) throws SignatureException {
        return this.signer.verifySignature(bArr);
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseDeterministicOrRandomSignature
    protected void reInitialize(boolean z, CipherParameters cipherParameters) {
        this.signer.init(z, cipherParameters);
    }
}
