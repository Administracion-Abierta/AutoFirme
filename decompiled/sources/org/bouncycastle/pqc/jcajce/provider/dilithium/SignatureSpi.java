package org.bouncycastle.pqc.jcajce.provider.dilithium;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumSigner;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/dilithium/SignatureSpi.class */
public class SignatureSpi extends Signature {
    private ByteArrayOutputStream bOut;
    private DilithiumSigner signer;
    private SecureRandom random;
    private DilithiumParameters parameters;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/dilithium/SignatureSpi$Base.class */
    public static class Base extends SignatureSpi {
        public Base() {
            super(new DilithiumSigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/dilithium/SignatureSpi$Base2.class */
    public static class Base2 extends SignatureSpi {
        public Base2() {
            super(new DilithiumSigner(), DilithiumParameters.dilithium2);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/dilithium/SignatureSpi$Base3.class */
    public static class Base3 extends SignatureSpi {
        public Base3() {
            super(new DilithiumSigner(), DilithiumParameters.dilithium3);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/dilithium/SignatureSpi$Base5.class */
    public static class Base5 extends SignatureSpi {
        public Base5() throws NoSuchAlgorithmException {
            super(new DilithiumSigner(), DilithiumParameters.dilithium5);
        }
    }

    protected SignatureSpi(DilithiumSigner dilithiumSigner) {
        super("Dilithium");
        this.bOut = new ByteArrayOutputStream();
        this.signer = dilithiumSigner;
        this.parameters = null;
    }

    protected SignatureSpi(DilithiumSigner dilithiumSigner, DilithiumParameters dilithiumParameters) {
        super(Strings.toUpperCase(dilithiumParameters.getName()));
        this.bOut = new ByteArrayOutputStream();
        this.signer = dilithiumSigner;
        this.parameters = dilithiumParameters;
    }

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        if (!(publicKey instanceof BCDilithiumPublicKey)) {
            try {
                publicKey = new BCDilithiumPublicKey(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
            } catch (Exception e) {
                throw new InvalidKeyException("unknown public key passed to Dilithium: " + e.getMessage(), e);
            }
        }
        BCDilithiumPublicKey bCDilithiumPublicKey = (BCDilithiumPublicKey) publicKey;
        if (this.parameters != null) {
            String upperCase = Strings.toUpperCase(this.parameters.getName());
            if (!upperCase.equals(bCDilithiumPublicKey.getAlgorithm())) {
                throw new InvalidKeyException("signature configured for " + upperCase);
            }
        }
        this.signer.init(false, bCDilithiumPublicKey.getKeyParams());
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException {
        this.random = secureRandom;
        engineInitSign(privateKey);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (!(privateKey instanceof BCDilithiumPrivateKey)) {
            throw new InvalidKeyException("unknown private key passed to Dilithium");
        }
        BCDilithiumPrivateKey bCDilithiumPrivateKey = (BCDilithiumPrivateKey) privateKey;
        DilithiumPrivateKeyParameters keyParams = bCDilithiumPrivateKey.getKeyParams();
        if (this.parameters != null) {
            String upperCase = Strings.toUpperCase(this.parameters.getName());
            if (!upperCase.equals(bCDilithiumPrivateKey.getAlgorithm())) {
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
