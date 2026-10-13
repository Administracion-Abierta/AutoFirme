package org.bouncycastle.pqc.jcajce.provider.falcon;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.falcon.FalconParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconSigner;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/falcon/SignatureSpi.class */
public class SignatureSpi extends Signature {
    private ByteArrayOutputStream bOut;
    private FalconSigner signer;
    private SecureRandom random;
    private FalconParameters parameters;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/falcon/SignatureSpi$Base.class */
    public static class Base extends SignatureSpi {
        public Base() {
            super(new FalconSigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/falcon/SignatureSpi$Falcon1024.class */
    public static class Falcon1024 extends SignatureSpi {
        public Falcon1024() {
            super(new FalconSigner(), FalconParameters.falcon_1024);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/falcon/SignatureSpi$Falcon512.class */
    public static class Falcon512 extends SignatureSpi {
        public Falcon512() {
            super(new FalconSigner(), FalconParameters.falcon_512);
        }
    }

    protected SignatureSpi(FalconSigner falconSigner) {
        super("FALCON");
        this.bOut = new ByteArrayOutputStream();
        this.signer = falconSigner;
        this.parameters = null;
    }

    protected SignatureSpi(FalconSigner falconSigner, FalconParameters falconParameters) {
        super(Strings.toUpperCase(falconParameters.getName()));
        this.parameters = falconParameters;
        this.bOut = new ByteArrayOutputStream();
        this.signer = falconSigner;
    }

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        if (!(publicKey instanceof BCFalconPublicKey)) {
            try {
                publicKey = new BCFalconPublicKey(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
            } catch (Exception e) {
                throw new InvalidKeyException("unknown public key passed to Falcon: " + e.getMessage(), e);
            }
        }
        BCFalconPublicKey bCFalconPublicKey = (BCFalconPublicKey) publicKey;
        if (this.parameters != null) {
            String upperCase = Strings.toUpperCase(this.parameters.getName());
            if (!upperCase.equals(bCFalconPublicKey.getAlgorithm())) {
                throw new InvalidKeyException("signature configured for " + upperCase);
            }
        }
        this.signer.init(false, bCFalconPublicKey.getKeyParams());
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException {
        this.random = secureRandom;
        engineInitSign(privateKey);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (!(privateKey instanceof BCFalconPrivateKey)) {
            throw new InvalidKeyException("unknown private key passed to Falcon");
        }
        BCFalconPrivateKey bCFalconPrivateKey = (BCFalconPrivateKey) privateKey;
        FalconPrivateKeyParameters keyParams = bCFalconPrivateKey.getKeyParams();
        if (this.parameters != null) {
            String upperCase = Strings.toUpperCase(this.parameters.getName());
            if (!upperCase.equals(bCFalconPrivateKey.getAlgorithm())) {
                throw new InvalidKeyException("signature configured for " + upperCase);
            }
        }
        if (this.random != null) {
            this.signer.init(true, new ParametersWithRandom(keyParams, this.random));
        } else {
            this.signer.init(true, keyParams);
        }
    }

    @Override // java.security.SignatureSpi
    protected void engineUpdate(byte b) throws SignatureException {
        this.bOut.write(b);
    }

    @Override // java.security.SignatureSpi
    protected void engineUpdate(byte[] bArr, int i, int i2) throws SignatureException {
        this.bOut.write(bArr, i, i2);
    }

    @Override // java.security.SignatureSpi
    protected byte[] engineSign() throws SignatureException {
        try {
            byte[] byteArray = this.bOut.toByteArray();
            this.bOut.reset();
            return this.signer.generateSignature(byteArray);
        } catch (Exception e) {
            throw new SignatureException(e.toString());
        }
    }

    @Override // java.security.SignatureSpi
    protected boolean engineVerify(byte[] bArr) throws SignatureException {
        byte[] byteArray = this.bOut.toByteArray();
        this.bOut.reset();
        return this.signer.verifySignature(byteArray, bArr);
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
