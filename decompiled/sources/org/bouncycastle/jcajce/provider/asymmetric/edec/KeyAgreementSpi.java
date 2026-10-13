package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.RawAgreement;
import org.bouncycastle.crypto.agreement.X25519Agreement;
import org.bouncycastle.crypto.agreement.X448Agreement;
import org.bouncycastle.crypto.agreement.XDHUnifiedAgreement;
import org.bouncycastle.crypto.agreement.kdf.ConcatenationKDFGenerator;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.crypto.params.XDHUPrivateParameters;
import org.bouncycastle.crypto.params.XDHUPublicParameters;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;
import org.bouncycastle.jcajce.spec.DHUParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.jcajce.spec.XDHParameterSpec;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/KeyAgreementSpi.class */
public class KeyAgreementSpi extends BaseAgreementSpi {
    private RawAgreement agreement;
    private DHUParameterSpec dhuSpec;
    private byte[] result;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/KeyAgreementSpi$X25519.class */
    public static final class X25519 extends KeyAgreementSpi {
        public X25519() {
            super(XDHParameterSpec.X25519);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/KeyAgreementSpi$X25519UwithSHA256CKDF.class */
    public static class X25519UwithSHA256CKDF extends KeyAgreementSpi {
        public X25519UwithSHA256CKDF() {
            super("X25519UwithSHA256CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/KeyAgreementSpi$X25519UwithSHA256KDF.class */
    public static class X25519UwithSHA256KDF extends KeyAgreementSpi {
        public X25519UwithSHA256KDF() {
            super("X25519UwithSHA256KDF", new KDF2BytesGenerator(DigestFactory.createSHA256()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/KeyAgreementSpi$X25519withSHA256CKDF.class */
    public static final class X25519withSHA256CKDF extends KeyAgreementSpi {
        public X25519withSHA256CKDF() {
            super("X25519withSHA256CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/KeyAgreementSpi$X25519withSHA256HKDF.class */
    public static final class X25519withSHA256HKDF extends KeyAgreementSpi {
        public X25519withSHA256HKDF() {
            super("X25519withSHA256HKDF", new HKDFBytesGenerator(DigestFactory.createSHA256()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/KeyAgreementSpi$X25519withSHA256KDF.class */
    public static final class X25519withSHA256KDF extends KeyAgreementSpi {
        public X25519withSHA256KDF() {
            super("X25519withSHA256KDF", new KDF2BytesGenerator(DigestFactory.createSHA256()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/KeyAgreementSpi$X25519withSHA384CKDF.class */
    public static class X25519withSHA384CKDF extends KeyAgreementSpi {
        public X25519withSHA384CKDF() {
            super("X25519withSHA384CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA384()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/KeyAgreementSpi$X25519withSHA512CKDF.class */
    public static class X25519withSHA512CKDF extends KeyAgreementSpi {
        public X25519withSHA512CKDF() {
            super("X25519withSHA512CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/KeyAgreementSpi$X448.class */
    public static final class X448 extends KeyAgreementSpi {
        public X448() {
            super(XDHParameterSpec.X448);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/KeyAgreementSpi$X448UwithSHA512CKDF.class */
    public static class X448UwithSHA512CKDF extends KeyAgreementSpi {
        public X448UwithSHA512CKDF() {
            super("X448UwithSHA512CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/KeyAgreementSpi$X448UwithSHA512KDF.class */
    public static class X448UwithSHA512KDF extends KeyAgreementSpi {
        public X448UwithSHA512KDF() {
            super("X448UwithSHA512KDF", new KDF2BytesGenerator(DigestFactory.createSHA512()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/KeyAgreementSpi$X448withSHA256CKDF.class */
    public static final class X448withSHA256CKDF extends KeyAgreementSpi {
        public X448withSHA256CKDF() {
            super("X448withSHA256CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/KeyAgreementSpi$X448withSHA384CKDF.class */
    public static class X448withSHA384CKDF extends KeyAgreementSpi {
        public X448withSHA384CKDF() {
            super("X448withSHA384CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA384()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/KeyAgreementSpi$X448withSHA512CKDF.class */
    public static final class X448withSHA512CKDF extends KeyAgreementSpi {
        public X448withSHA512CKDF() {
            super("X448withSHA512CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/KeyAgreementSpi$X448withSHA512HKDF.class */
    public static final class X448withSHA512HKDF extends KeyAgreementSpi {
        public X448withSHA512HKDF() {
            super("X448withSHA512HKDF", new HKDFBytesGenerator(DigestFactory.createSHA512()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/KeyAgreementSpi$X448withSHA512KDF.class */
    public static final class X448withSHA512KDF extends KeyAgreementSpi {
        public X448withSHA512KDF() {
            super("X448withSHA512KDF", new KDF2BytesGenerator(DigestFactory.createSHA512()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/KeyAgreementSpi$XDH.class */
    public static final class XDH extends KeyAgreementSpi {
        public XDH() {
            super("XDH");
        }
    }

    KeyAgreementSpi(String str) {
        super(Properties.isOverrideSet(Properties.EMULATE_ORACLE) ? "XDH" : str, null);
    }

    KeyAgreementSpi(String str, DerivationFunction derivationFunction) {
        super(Properties.isOverrideSet(Properties.EMULATE_ORACLE) ? "XDH" : str, derivationFunction);
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi
    protected byte[] doCalcSecret() {
        return this.result;
    }

    @Override // org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi
    protected void doInitFromKey(Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (!(key instanceof PrivateKey)) {
            throw new InvalidKeyException("private XDH key required");
        }
        AsymmetricKeyParameter asymmetricKeyParameterGeneratePrivateKeyParameter = EdECUtil.generatePrivateKeyParameter((PrivateKey) key);
        if (asymmetricKeyParameterGeneratePrivateKeyParameter instanceof X25519PrivateKeyParameters) {
            this.agreement = getAgreement(XDHParameterSpec.X25519);
        } else {
            if (!(asymmetricKeyParameterGeneratePrivateKeyParameter instanceof X448PrivateKeyParameters)) {
                throw new InvalidKeyException("unsupported private key type");
            }
            this.agreement = getAgreement(XDHParameterSpec.X448);
        }
        this.ukmParameters = null;
        this.ukmParametersSalt = null;
        if (algorithmParameterSpec instanceof DHUParameterSpec) {
            if (this.kaAlgorithm.indexOf(85) < 0) {
                throw new InvalidAlgorithmParameterException("agreement algorithm not DHU based");
            }
            this.dhuSpec = (DHUParameterSpec) algorithmParameterSpec;
            this.ukmParameters = this.dhuSpec.getUserKeyingMaterial();
            this.agreement.init(new XDHUPrivateParameters(asymmetricKeyParameterGeneratePrivateKeyParameter, ((BCXDHPrivateKey) this.dhuSpec.getEphemeralPrivateKey()).engineGetKeyParameters(), ((BCXDHPublicKey) this.dhuSpec.getEphemeralPublicKey()).engineGetKeyParameters()));
        } else if (algorithmParameterSpec != null) {
            this.agreement.init(asymmetricKeyParameterGeneratePrivateKeyParameter);
            if (!(algorithmParameterSpec instanceof UserKeyingMaterialSpec)) {
                throw new InvalidAlgorithmParameterException("unknown ParameterSpec");
            }
            if (this.kdf == null) {
                throw new InvalidAlgorithmParameterException("no KDF specified for UserKeyingMaterialSpec");
            }
            this.ukmParameters = ((UserKeyingMaterialSpec) algorithmParameterSpec).getUserKeyingMaterial();
            this.ukmParametersSalt = ((UserKeyingMaterialSpec) algorithmParameterSpec).getSalt();
        } else {
            this.agreement.init(asymmetricKeyParameterGeneratePrivateKeyParameter);
        }
        if (this.kdf == null || this.ukmParameters != null) {
            return;
        }
        this.ukmParameters = new byte[0];
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected Key engineDoPhase(Key key, boolean z) throws IllegalStateException, InvalidKeyException {
        if (!(key instanceof PublicKey)) {
            throw new InvalidKeyException("public XDH key required");
        }
        if (this.agreement == null) {
            throw new IllegalStateException(this.kaAlgorithm + " not initialised.");
        }
        if (!z) {
            throw new IllegalStateException(this.kaAlgorithm + " can only be between two parties.");
        }
        AsymmetricKeyParameter asymmetricKeyParameterGeneratePublicKeyParameter = EdECUtil.generatePublicKeyParameter((PublicKey) key);
        this.result = new byte[this.agreement.getAgreementSize()];
        if (this.dhuSpec != null) {
            this.agreement.calculateAgreement(new XDHUPublicParameters(asymmetricKeyParameterGeneratePublicKeyParameter, ((BCXDHPublicKey) this.dhuSpec.getOtherPartyEphemeralKey()).engineGetKeyParameters()), this.result, 0);
            return null;
        }
        this.agreement.calculateAgreement(asymmetricKeyParameterGeneratePublicKeyParameter, this.result, 0);
        return null;
    }

    private RawAgreement getAgreement(String str) throws InvalidKeyException {
        if (!this.kaAlgorithm.equals("XDH") && !this.kaAlgorithm.startsWith(str)) {
            throw new InvalidKeyException("inappropriate key for " + this.kaAlgorithm);
        }
        if (this.kaAlgorithm.indexOf(85) > 0) {
            return str.startsWith(XDHParameterSpec.X448) ? new XDHUnifiedAgreement(new X448Agreement()) : new XDHUnifiedAgreement(new X25519Agreement());
        }
        return str.startsWith(XDHParameterSpec.X448) ? new X448Agreement() : new X25519Agreement();
    }
}
