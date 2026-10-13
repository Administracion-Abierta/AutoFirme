package org.bouncycastle.jcajce.provider.asymmetric.dstu;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.DSTU4145KeyPairGenerator;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.DSTU4145Parameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.spec.DSTU4145ParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/dstu/KeyPairGeneratorSpi.class */
public class KeyPairGeneratorSpi extends KeyPairGenerator {
    Object ecParams;
    ECKeyPairGenerator engine;
    String algorithm;
    ECKeyGenerationParameters param;
    SecureRandom random;
    boolean initialised;

    public KeyPairGeneratorSpi() {
        super("DSTU4145");
        this.ecParams = null;
        this.engine = new DSTU4145KeyPairGenerator();
        this.algorithm = "DSTU4145";
        this.random = null;
        this.initialised = false;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(int i, SecureRandom secureRandom) {
        this.random = secureRandom;
        if (this.ecParams == null) {
            throw new InvalidParameterException("unknown key size.");
        }
        try {
            initialize((ECGenParameterSpec) this.ecParams, secureRandom);
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidParameterException("key size not configurable.");
        }
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec instanceof ECParameterSpec) {
            ECParameterSpec eCParameterSpec = (ECParameterSpec) algorithmParameterSpec;
            this.ecParams = algorithmParameterSpec;
            this.param = new ECKeyGenerationParameters(new ECDomainParameters(eCParameterSpec.getCurve(), eCParameterSpec.getG(), eCParameterSpec.getN(), eCParameterSpec.getH()), secureRandom);
            this.engine.init(this.param);
            this.initialised = true;
            return;
        }
        if (algorithmParameterSpec instanceof java.security.spec.ECParameterSpec) {
            java.security.spec.ECParameterSpec eCParameterSpec2 = (java.security.spec.ECParameterSpec) algorithmParameterSpec;
            this.ecParams = algorithmParameterSpec;
            ECCurve eCCurveConvertCurve = EC5Util.convertCurve(eCParameterSpec2.getCurve());
            ECPoint eCPointConvertPoint = EC5Util.convertPoint(eCCurveConvertCurve, eCParameterSpec2.getGenerator());
            if (eCParameterSpec2 instanceof DSTU4145ParameterSpec) {
                this.param = new ECKeyGenerationParameters(new DSTU4145Parameters(new ECDomainParameters(eCCurveConvertCurve, eCPointConvertPoint, eCParameterSpec2.getOrder(), BigInteger.valueOf(eCParameterSpec2.getCofactor())), ((DSTU4145ParameterSpec) eCParameterSpec2).getDKE()), secureRandom);
            } else {
                this.param = new ECKeyGenerationParameters(new ECDomainParameters(eCCurveConvertCurve, eCPointConvertPoint, eCParameterSpec2.getOrder(), BigInteger.valueOf(eCParameterSpec2.getCofactor())), secureRandom);
            }
            this.engine.init(this.param);
            this.initialised = true;
            return;
        }
        if ((algorithmParameterSpec instanceof ECGenParameterSpec) || (algorithmParameterSpec instanceof ECNamedCurveGenParameterSpec)) {
            String name = algorithmParameterSpec instanceof ECGenParameterSpec ? ((ECGenParameterSpec) algorithmParameterSpec).getName() : ((ECNamedCurveGenParameterSpec) algorithmParameterSpec).getName();
            ECDomainParameters byOID = DSTU4145NamedCurves.getByOID(new ASN1ObjectIdentifier(name));
            if (byOID == null) {
                throw new InvalidAlgorithmParameterException("unknown curve name: " + name);
            }
            this.ecParams = new ECNamedCurveSpec(name, byOID.getCurve(), byOID.getG(), byOID.getN(), byOID.getH(), byOID.getSeed());
            java.security.spec.ECParameterSpec eCParameterSpec3 = (java.security.spec.ECParameterSpec) this.ecParams;
            ECCurve eCCurveConvertCurve2 = EC5Util.convertCurve(eCParameterSpec3.getCurve());
            this.param = new ECKeyGenerationParameters(new ECDomainParameters(eCCurveConvertCurve2, EC5Util.convertPoint(eCCurveConvertCurve2, eCParameterSpec3.getGenerator()), eCParameterSpec3.getOrder(), BigInteger.valueOf(eCParameterSpec3.getCofactor())), secureRandom);
            this.engine.init(this.param);
            this.initialised = true;
            return;
        }
        if (algorithmParameterSpec != null || BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa() == null) {
            if (algorithmParameterSpec != null || BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa() != null) {
                throw new InvalidAlgorithmParameterException("parameter object not a ECParameterSpec: " + algorithmParameterSpec.getClass().getName());
            }
            throw new InvalidAlgorithmParameterException("null parameter passed but no implicitCA set");
        }
        ECParameterSpec ecImplicitlyCa = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
        this.ecParams = algorithmParameterSpec;
        this.param = new ECKeyGenerationParameters(new ECDomainParameters(ecImplicitlyCa.getCurve(), ecImplicitlyCa.getG(), ecImplicitlyCa.getN(), ecImplicitlyCa.getH()), secureRandom);
        this.engine.init(this.param);
        this.initialised = true;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            throw new IllegalStateException("DSTU Key Pair Generator not initialised");
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPairGenerateKeyPair = this.engine.generateKeyPair();
        ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPublic();
        ECPrivateKeyParameters eCPrivateKeyParameters = (ECPrivateKeyParameters) asymmetricCipherKeyPairGenerateKeyPair.getPrivate();
        if (this.ecParams instanceof ECParameterSpec) {
            ECParameterSpec eCParameterSpec = (ECParameterSpec) this.ecParams;
            BCDSTU4145PublicKey bCDSTU4145PublicKey = new BCDSTU4145PublicKey(this.algorithm, eCPublicKeyParameters, eCParameterSpec);
            return new KeyPair(bCDSTU4145PublicKey, new BCDSTU4145PrivateKey(this.algorithm, eCPrivateKeyParameters, bCDSTU4145PublicKey, eCParameterSpec));
        }
        if (this.ecParams == null) {
            return new KeyPair(new BCDSTU4145PublicKey(this.algorithm, eCPublicKeyParameters), new BCDSTU4145PrivateKey(this.algorithm, eCPrivateKeyParameters));
        }
        java.security.spec.ECParameterSpec eCParameterSpec2 = (java.security.spec.ECParameterSpec) this.ecParams;
        BCDSTU4145PublicKey bCDSTU4145PublicKey2 = new BCDSTU4145PublicKey(this.algorithm, eCPublicKeyParameters, eCParameterSpec2);
        return new KeyPair(bCDSTU4145PublicKey2, new BCDSTU4145PrivateKey(this.algorithm, eCPrivateKeyParameters, bCDSTU4145PublicKey2, eCParameterSpec2));
    }
}
