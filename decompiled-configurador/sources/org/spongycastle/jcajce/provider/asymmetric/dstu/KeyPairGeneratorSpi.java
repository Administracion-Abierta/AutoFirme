package org.spongycastle.jcajce.provider.asymmetric.dstu;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ua.DSTU4145NamedCurves;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.generators.DSTU4145KeyPairGenerator;
import org.spongycastle.crypto.generators.ECKeyPairGenerator;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECKeyGenerationParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.spongycastle.jce.spec.ECNamedCurveSpec;
import org.spongycastle.jce.spec.ECParameterSpec;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/dstu/KeyPairGeneratorSpi.class */
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
    public void initialize(int strength, SecureRandom random) {
        this.random = random;
        if (this.ecParams != null) {
            try {
                initialize((ECGenParameterSpec) this.ecParams, random);
                return;
            } catch (InvalidAlgorithmParameterException e) {
                throw new InvalidParameterException("key size not configurable.");
            }
        }
        throw new InvalidParameterException("unknown key size.");
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
        String curveName;
        if (params instanceof ECParameterSpec) {
            ECParameterSpec p = (ECParameterSpec) params;
            this.ecParams = params;
            this.param = new ECKeyGenerationParameters(new ECDomainParameters(p.getCurve(), p.getG(), p.getN(), p.getH()), random);
            this.engine.init(this.param);
            this.initialised = true;
            return;
        }
        if (params instanceof java.security.spec.ECParameterSpec) {
            java.security.spec.ECParameterSpec p2 = (java.security.spec.ECParameterSpec) params;
            this.ecParams = params;
            ECCurve curve = EC5Util.convertCurve(p2.getCurve());
            ECPoint g = EC5Util.convertPoint(curve, p2.getGenerator(), false);
            this.param = new ECKeyGenerationParameters(new ECDomainParameters(curve, g, p2.getOrder(), BigInteger.valueOf(p2.getCofactor())), random);
            this.engine.init(this.param);
            this.initialised = true;
            return;
        }
        if ((params instanceof ECGenParameterSpec) || (params instanceof ECNamedCurveGenParameterSpec)) {
            if (params instanceof ECGenParameterSpec) {
                curveName = ((ECGenParameterSpec) params).getName();
            } else {
                curveName = ((ECNamedCurveGenParameterSpec) params).getName();
            }
            ECDomainParameters ecP = DSTU4145NamedCurves.getByOID(new ASN1ObjectIdentifier(curveName));
            if (ecP == null) {
                throw new InvalidAlgorithmParameterException("unknown curve name: " + curveName);
            }
            this.ecParams = new ECNamedCurveSpec(curveName, ecP.getCurve(), ecP.getG(), ecP.getN(), ecP.getH(), ecP.getSeed());
            java.security.spec.ECParameterSpec p3 = (java.security.spec.ECParameterSpec) this.ecParams;
            ECCurve curve2 = EC5Util.convertCurve(p3.getCurve());
            ECPoint g2 = EC5Util.convertPoint(curve2, p3.getGenerator(), false);
            this.param = new ECKeyGenerationParameters(new ECDomainParameters(curve2, g2, p3.getOrder(), BigInteger.valueOf(p3.getCofactor())), random);
            this.engine.init(this.param);
            this.initialised = true;
            return;
        }
        if (params == null && BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa() != null) {
            ECParameterSpec p4 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            this.ecParams = params;
            this.param = new ECKeyGenerationParameters(new ECDomainParameters(p4.getCurve(), p4.getG(), p4.getN(), p4.getH()), random);
            this.engine.init(this.param);
            this.initialised = true;
            return;
        }
        if (params == null && BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa() == null) {
            throw new InvalidAlgorithmParameterException("null parameter passed but no implicitCA set");
        }
        throw new InvalidAlgorithmParameterException("parameter object not a ECParameterSpec: " + params.getClass().getName());
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            throw new IllegalStateException("DSTU Key Pair Generator not initialised");
        }
        AsymmetricCipherKeyPair pair = this.engine.generateKeyPair();
        ECPublicKeyParameters pub = (ECPublicKeyParameters) pair.getPublic();
        ECPrivateKeyParameters priv = (ECPrivateKeyParameters) pair.getPrivate();
        if (this.ecParams instanceof ECParameterSpec) {
            ECParameterSpec p = (ECParameterSpec) this.ecParams;
            BCDSTU4145PublicKey pubKey = new BCDSTU4145PublicKey(this.algorithm, pub, p);
            return new KeyPair(pubKey, new BCDSTU4145PrivateKey(this.algorithm, priv, pubKey, p));
        }
        if (this.ecParams == null) {
            return new KeyPair(new BCDSTU4145PublicKey(this.algorithm, pub), new BCDSTU4145PrivateKey(this.algorithm, priv));
        }
        java.security.spec.ECParameterSpec p2 = (java.security.spec.ECParameterSpec) this.ecParams;
        BCDSTU4145PublicKey pubKey2 = new BCDSTU4145PublicKey(this.algorithm, pub, p2);
        return new KeyPair(pubKey2, new BCDSTU4145PrivateKey(this.algorithm, priv, pubKey2, p2));
    }
}
