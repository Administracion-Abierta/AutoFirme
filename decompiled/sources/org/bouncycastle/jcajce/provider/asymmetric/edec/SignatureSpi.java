package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.crypto.signers.Ed448Signer;
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/SignatureSpi.class */
public class SignatureSpi extends java.security.SignatureSpi {
    private static final byte[] EMPTY_CONTEXT = new byte[0];
    private final String algorithm;
    private Signer signer;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/SignatureSpi$Ed25519.class */
    public static final class Ed25519 extends SignatureSpi {
        public Ed25519() {
            super(EdDSAParameterSpec.Ed25519);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/SignatureSpi$Ed448.class */
    public static final class Ed448 extends SignatureSpi {
        public Ed448() {
            super(EdDSAParameterSpec.Ed448);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/SignatureSpi$EdDSA.class */
    public static final class EdDSA extends SignatureSpi {
        public EdDSA() {
            super(null);
        }
    }

    SignatureSpi(String str) {
        this.algorithm = str;
    }

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        AsymmetricKeyParameter lwEdDSAKeyPublic = getLwEdDSAKeyPublic(publicKey);
        if (lwEdDSAKeyPublic instanceof Ed25519PublicKeyParameters) {
            this.signer = getSigner(EdDSAParameterSpec.Ed25519);
        } else {
            if (!(lwEdDSAKeyPublic instanceof Ed448PublicKeyParameters)) {
                throw new InvalidKeyException("unsupported public key type");
            }
            this.signer = getSigner(EdDSAParameterSpec.Ed448);
        }
        this.signer.init(false, lwEdDSAKeyPublic);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        AsymmetricKeyParameter lwEdDSAKeyPrivate = getLwEdDSAKeyPrivate(privateKey);
        if (lwEdDSAKeyPrivate instanceof Ed25519PrivateKeyParameters) {
            this.signer = getSigner(EdDSAParameterSpec.Ed25519);
        } else {
            if (!(lwEdDSAKeyPrivate instanceof Ed448PrivateKeyParameters)) {
                throw new InvalidKeyException("unsupported private key type");
            }
            this.signer = getSigner(EdDSAParameterSpec.Ed448);
        }
        this.signer.init(true, lwEdDSAKeyPrivate);
    }

    private static AsymmetricKeyParameter getLwEdDSAKeyPrivate(PrivateKey privateKey) throws InvalidKeyException {
        return EdECUtil.generatePrivateKeyParameter(privateKey);
    }

    private static AsymmetricKeyParameter getLwEdDSAKeyPublic(PublicKey publicKey) throws InvalidKeyException {
        return EdECUtil.generatePublicKeyParameter(publicKey);
    }

    private Signer getSigner(String str) throws InvalidKeyException {
        if (this.algorithm == null || str.equals(this.algorithm)) {
            return str.equals(EdDSAParameterSpec.Ed448) ? new Ed448Signer(EMPTY_CONTEXT) : new Ed25519Signer();
        }
        throw new InvalidKeyException("inappropriate key for " + this.algorithm);
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
            throw new SignatureException(e.getMessage());
        }
    }

    @Override // java.security.SignatureSpi
    protected boolean engineVerify(byte[] bArr) throws SignatureException {
        return this.signer.verifySignature(bArr);
    }

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(String str, Object obj) throws InvalidParameterException {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override // java.security.SignatureSpi
    protected Object engineGetParameter(String str) throws InvalidParameterException {
        throw new UnsupportedOperationException("engineGetParameter unsupported");
    }

    @Override // java.security.SignatureSpi
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }
}
