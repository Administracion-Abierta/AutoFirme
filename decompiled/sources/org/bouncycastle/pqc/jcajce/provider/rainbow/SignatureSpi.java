package org.bouncycastle.pqc.jcajce.provider.rainbow;

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
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowSigner;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/rainbow/SignatureSpi.class */
public class SignatureSpi extends Signature {
    private ByteArrayOutputStream bOut;
    private RainbowSigner signer;
    private SecureRandom random;
    private RainbowParameters parameters;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/rainbow/SignatureSpi$Base.class */
    public static class Base extends SignatureSpi {
        public Base() {
            super(new RainbowSigner());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/rainbow/SignatureSpi$RainbowIIIcircum.class */
    public static class RainbowIIIcircum extends SignatureSpi {
        public RainbowIIIcircum() {
            super(new RainbowSigner(), RainbowParameters.rainbowIIIcircumzenithal);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/rainbow/SignatureSpi$RainbowIIIclassic.class */
    public static class RainbowIIIclassic extends SignatureSpi {
        public RainbowIIIclassic() {
            super(new RainbowSigner(), RainbowParameters.rainbowIIIclassic);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/rainbow/SignatureSpi$RainbowIIIcomp.class */
    public static class RainbowIIIcomp extends SignatureSpi {
        public RainbowIIIcomp() {
            super(new RainbowSigner(), RainbowParameters.rainbowIIIcompressed);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/rainbow/SignatureSpi$RainbowVcircum.class */
    public static class RainbowVcircum extends SignatureSpi {
        public RainbowVcircum() {
            super(new RainbowSigner(), RainbowParameters.rainbowVcircumzenithal);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/rainbow/SignatureSpi$RainbowVclassic.class */
    public static class RainbowVclassic extends SignatureSpi {
        public RainbowVclassic() {
            super(new RainbowSigner(), RainbowParameters.rainbowVclassic);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/rainbow/SignatureSpi$RainbowVcomp.class */
    public static class RainbowVcomp extends SignatureSpi {
        public RainbowVcomp() {
            super(new RainbowSigner(), RainbowParameters.rainbowVcompressed);
        }
    }

    protected SignatureSpi(RainbowSigner rainbowSigner) {
        super("RAINBOW");
        this.bOut = new ByteArrayOutputStream();
        this.signer = rainbowSigner;
        this.parameters = null;
    }

    protected SignatureSpi(RainbowSigner rainbowSigner, RainbowParameters rainbowParameters) {
        super(Strings.toUpperCase(rainbowParameters.getName()));
        this.parameters = rainbowParameters;
        this.bOut = new ByteArrayOutputStream();
        this.signer = rainbowSigner;
    }

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        if (!(publicKey instanceof BCRainbowPublicKey)) {
            try {
                publicKey = new BCRainbowPublicKey(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
            } catch (Exception e) {
                throw new InvalidKeyException("unknown public key passed to Rainbow: " + e.getMessage(), e);
            }
        }
        BCRainbowPublicKey bCRainbowPublicKey = (BCRainbowPublicKey) publicKey;
        if (this.parameters != null) {
            String upperCase = Strings.toUpperCase(this.parameters.getName());
            if (!upperCase.equals(bCRainbowPublicKey.getAlgorithm())) {
                throw new InvalidKeyException("signature configured for " + upperCase);
            }
        }
        this.signer.init(false, bCRainbowPublicKey.getKeyParams());
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException {
        this.random = secureRandom;
        engineInitSign(privateKey);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (!(privateKey instanceof BCRainbowPrivateKey)) {
            throw new InvalidKeyException("unknown private key passed to Rainbow");
        }
        BCRainbowPrivateKey bCRainbowPrivateKey = (BCRainbowPrivateKey) privateKey;
        RainbowPrivateKeyParameters keyParams = bCRainbowPrivateKey.getKeyParams();
        if (this.parameters != null) {
            String upperCase = Strings.toUpperCase(this.parameters.getName());
            if (!upperCase.equals(bCRainbowPrivateKey.getAlgorithm())) {
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
