package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.spec.SM2ParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/GMSignatureSpi.class */
public class GMSignatureSpi extends java.security.SignatureSpi {
    private final JcaJceHelper helper = new BCJcaJceHelper();
    private AlgorithmParameters engineParams;
    private SM2ParameterSpec paramSpec;
    private final SM2Signer signer;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/GMSignatureSpi$sha256WithSM2.class */
    public static class sha256WithSM2 extends GMSignatureSpi {
        public sha256WithSM2() {
            super(new SM2Signer(SHA256Digest.newInstance()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/GMSignatureSpi$sm3WithSM2.class */
    public static class sm3WithSM2 extends GMSignatureSpi {
        public sm3WithSM2() {
            super(new SM2Signer());
        }
    }

    protected GMSignatureSpi(SM2Signer sM2Signer) {
        this.signer = sM2Signer;
    }

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        CipherParameters cipherParametersGeneratePublicKeyParameter = ECUtils.generatePublicKeyParameter(publicKey);
        if (this.paramSpec != null) {
            cipherParametersGeneratePublicKeyParameter = new ParametersWithID(cipherParametersGeneratePublicKeyParameter, this.paramSpec.getID());
        }
        this.signer.init(false, cipherParametersGeneratePublicKeyParameter);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        CipherParameters cipherParametersGeneratePrivateKeyParameter = ECUtil.generatePrivateKeyParameter(privateKey);
        if (this.appRandom != null) {
            cipherParametersGeneratePrivateKeyParameter = new ParametersWithRandom(cipherParametersGeneratePrivateKeyParameter, this.appRandom);
        }
        if (this.paramSpec != null) {
            this.signer.init(true, new ParametersWithID(cipherParametersGeneratePrivateKeyParameter, this.paramSpec.getID()));
        } else {
            this.signer.init(true, cipherParametersGeneratePrivateKeyParameter);
        }
    }

    @Override // java.security.SignatureSpi
    protected void engineUpdate(byte b) throws SignatureException {
        this.signer.update(b);
    }

    @Override // java.security.SignatureSpi
    protected void engineUpdate(byte[] bArr, int i, int i2) throws SignatureException {
        this.signer.update(bArr, i, i2);
    }

    @Override // java.security.SignatureSpi
    protected byte[] engineSign() throws SignatureException {
        try {
            return this.signer.generateSignature();
        } catch (CryptoException e) {
            throw new SignatureException("unable to create signature: " + e.getMessage());
        }
    }

    @Override // java.security.SignatureSpi
    protected boolean engineVerify(byte[] bArr) throws SignatureException {
        return this.signer.verifySignature(bArr);
    }

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof SM2ParameterSpec)) {
            throw new InvalidAlgorithmParameterException("only SM2ParameterSpec supported");
        }
        this.paramSpec = (SM2ParameterSpec) algorithmParameterSpec;
    }

    @Override // java.security.SignatureSpi
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null && this.paramSpec != null) {
            try {
                this.engineParams = this.helper.createAlgorithmParameters("PSS");
                this.engineParams.init(this.paramSpec);
            } catch (Exception e) {
                throw new RuntimeException(e.toString());
            }
        }
        return this.engineParams;
    }

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(String str, Object obj) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override // java.security.SignatureSpi
    protected Object engineGetParameter(String str) {
        throw new UnsupportedOperationException("engineGetParameter unsupported");
    }
}
