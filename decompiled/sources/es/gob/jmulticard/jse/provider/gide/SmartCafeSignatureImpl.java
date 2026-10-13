package es.gob.jmulticard.jse.provider.gide;

import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.jmulticard.card.CryptoCardException;
import es.gob.jmulticard.card.PinException;
import es.gob.jmulticard.card.gide.smartcafe.SmartCafePkcs15Applet;
import es.gob.jmulticard.card.gide.smartcafe.SmartCafePrivateKeyReference;
import es.gob.jmulticard.jse.provider.ProviderUtil;
import es.gob.jmulticard.jse.provider.SignatureAuthException;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignatureSpi;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/gide/SmartCafeSignatureImpl.class */
abstract class SmartCafeSignatureImpl extends SignatureSpi {
    private final ByteArrayOutputStream data = new ByteArrayOutputStream();
    private Signature signatureVerifier = null;
    private SmartCafePrivateKey privateKey = null;
    private final String signatureAlgo;

    SmartCafeSignatureImpl(String signatureAlgorithm) {
        this.signatureAlgo = signatureAlgorithm;
    }

    @Override // java.security.SignatureSpi
    protected Object engineGetParameter(String param) {
        throw new InvalidParameterException("Parametro no soportado");
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey prKey) throws InvalidKeyException {
        if (prKey == null) {
            throw new InvalidKeyException("La clave proporcionada es nula");
        }
        if (!(prKey instanceof SmartCafePrivateKey)) {
            throw new InvalidKeyException("La clave proporcionada no es de G&D Smartcafe PKCS#15: " + prKey.getClass().getName());
        }
        this.privateKey = (SmartCafePrivateKey) prKey;
        this.data.reset();
    }

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        this.data.reset();
        try {
            this.signatureVerifier = Signature.getInstance(this.signatureAlgo);
            try {
                if (this.signatureVerifier.getProvider() instanceof SmartCafeProvider) {
                    this.signatureVerifier = Signature.getInstance(this.signatureAlgo, ProviderUtil.getDefaultOtherProvider("Signature", this.signatureAlgo));
                }
                this.signatureVerifier.initVerify(publicKey);
            } catch (NoSuchProviderException e) {
                throw new IllegalStateException("No esta instalado el proveedor por defecto de firma", e);
            }
        } catch (NoSuchAlgorithmException e2) {
            throw new IllegalStateException("No existe un proveedor para validar firmas con el algoritmo " + this.signatureAlgo, e2);
        }
    }

    @Override // java.security.SignatureSpi
    protected void engineSetParameter(String param, Object value) {
        throw new InvalidParameterException("Parametro no soportado");
    }

    @Override // java.security.SignatureSpi
    protected byte[] engineSign() throws SignatureException {
        if (!(this.privateKey.getCryptoCard() instanceof SmartCafePkcs15Applet)) {
            throw new ProviderException("La clave proporcionada no es de G&D Smartcafe PKCS#15: " + this.privateKey.getCryptoCard().getClass().getName());
        }
        SmartCafePrivateKeyReference prkRef = new SmartCafePrivateKeyReference(Integer.valueOf(this.privateKey.getId()));
        try {
            return this.privateKey.getCryptoCard().sign(this.data.toByteArray(), this.signatureAlgo, prkRef);
        } catch (CryptoCardException e) {
            throw new SignatureException(e);
        } catch (PinException e2) {
            throw new SignatureAuthException(e2);
        }
    }

    @Override // java.security.SignatureSpi
    protected void engineUpdate(byte b) {
        this.data.write(b);
    }

    @Override // java.security.SignatureSpi
    protected void engineUpdate(byte[] b, int off, int len) {
        this.data.write(b, off, len);
    }

    @Override // java.security.SignatureSpi
    protected boolean engineVerify(byte[] sigBytes) throws SignatureException {
        if (this.signatureVerifier == null) {
            throw new SignatureException("La verificacion no esta inicializada");
        }
        this.signatureVerifier.update(this.data.toByteArray());
        this.data.reset();
        return this.signatureVerifier.verify(sigBytes);
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/gide/SmartCafeSignatureImpl$Sha1.class */
    public static final class Sha1 extends SmartCafeSignatureImpl {
        public Sha1() {
            super(AOSignConstants.SIGN_ALGORITHM_SHA1WITHRSA);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/gide/SmartCafeSignatureImpl$Sha256.class */
    public static final class Sha256 extends SmartCafeSignatureImpl {
        public Sha256() {
            super(AOSignConstants.SIGN_ALGORITHM_SHA256WITHRSA);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/gide/SmartCafeSignatureImpl$Sha384.class */
    public static final class Sha384 extends SmartCafeSignatureImpl {
        public Sha384() {
            super(AOSignConstants.SIGN_ALGORITHM_SHA384WITHRSA);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/gide/SmartCafeSignatureImpl$Sha512.class */
    public static final class Sha512 extends SmartCafeSignatureImpl {
        public Sha512() {
            super("SHA512withRSA");
        }
    }
}
