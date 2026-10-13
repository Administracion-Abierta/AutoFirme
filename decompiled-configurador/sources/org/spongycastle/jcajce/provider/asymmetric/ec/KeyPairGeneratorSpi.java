package org.spongycastle.jcajce.provider.asymmetric.ec;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.util.Hashtable;
import java.util.Map;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.x9.ECNamedCurveTable;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.generators.ECKeyPairGenerator;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECKeyGenerationParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.spongycastle.jcajce.provider.config.ProviderConfiguration;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.spongycastle.jce.spec.ECNamedCurveSpec;
import org.spongycastle.jce.spec.ECParameterSpec;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.util.Integers;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/ec/KeyPairGeneratorSpi.class */
public abstract class KeyPairGeneratorSpi extends KeyPairGenerator {
    public KeyPairGeneratorSpi(String algorithmName) {
        super(algorithmName);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/ec/KeyPairGeneratorSpi$EC.class */
    public static class EC extends KeyPairGeneratorSpi {
        ECKeyGenerationParameters param;
        ECKeyPairGenerator engine;
        Object ecParams;
        int strength;
        int certainty;
        SecureRandom random;
        boolean initialised;
        String algorithm;
        ProviderConfiguration configuration;
        private static Hashtable ecParameters = new Hashtable();

        static {
            ecParameters.put(Integers.valueOf(192), new ECGenParameterSpec("prime192v1"));
            ecParameters.put(Integers.valueOf(239), new ECGenParameterSpec("prime239v1"));
            ecParameters.put(Integers.valueOf(256), new ECGenParameterSpec("prime256v1"));
            ecParameters.put(Integers.valueOf(224), new ECGenParameterSpec("P-224"));
            ecParameters.put(Integers.valueOf(384), new ECGenParameterSpec("P-384"));
            ecParameters.put(Integers.valueOf(521), new ECGenParameterSpec("P-521"));
        }

        public EC() {
            super("EC");
            this.engine = new ECKeyPairGenerator();
            this.ecParams = null;
            this.strength = 239;
            this.certainty = 50;
            this.random = new SecureRandom();
            this.initialised = false;
            this.algorithm = "EC";
            this.configuration = BouncyCastleProvider.CONFIGURATION;
        }

        public EC(String algorithm, ProviderConfiguration configuration) {
            super(algorithm);
            this.engine = new ECKeyPairGenerator();
            this.ecParams = null;
            this.strength = 239;
            this.certainty = 50;
            this.random = new SecureRandom();
            this.initialised = false;
            this.algorithm = algorithm;
            this.configuration = configuration;
        }

        @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
        public void initialize(int strength, SecureRandom random) {
            this.strength = strength;
            this.random = random;
            ECGenParameterSpec ecParams = (ECGenParameterSpec) ecParameters.get(Integers.valueOf(strength));
            if (ecParams == null) {
                throw new InvalidParameterException("unknown key size.");
            }
            try {
                initialize(ecParams, random);
            } catch (InvalidAlgorithmParameterException e) {
                throw new InvalidParameterException("key size not configurable.");
            }
        }

        @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
        public void initialize(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
            if (params == null) {
                ECParameterSpec implicitCA = this.configuration.getEcImplicitlyCa();
                if (implicitCA == null) {
                    throw new InvalidAlgorithmParameterException("null parameter passed but no implicitCA set");
                }
                this.ecParams = null;
                this.param = createKeyGenParamsBC(implicitCA, random);
            } else if (params instanceof ECParameterSpec) {
                this.ecParams = params;
                this.param = createKeyGenParamsBC((ECParameterSpec) params, random);
            } else if (params instanceof java.security.spec.ECParameterSpec) {
                this.ecParams = params;
                this.param = createKeyGenParamsJCE((java.security.spec.ECParameterSpec) params, random);
            } else if (params instanceof ECGenParameterSpec) {
                initializeNamedCurve(((ECGenParameterSpec) params).getName(), random);
            } else if (params instanceof ECNamedCurveGenParameterSpec) {
                initializeNamedCurve(((ECNamedCurveGenParameterSpec) params).getName(), random);
            } else {
                throw new InvalidAlgorithmParameterException("parameter object not a ECParameterSpec");
            }
            this.engine.init(this.param);
            this.initialised = true;
        }

        @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
        public KeyPair generateKeyPair() {
            if (!this.initialised) {
                initialize(this.strength, new SecureRandom());
            }
            AsymmetricCipherKeyPair pair = this.engine.generateKeyPair();
            ECPublicKeyParameters pub = (ECPublicKeyParameters) pair.getPublic();
            ECPrivateKeyParameters priv = (ECPrivateKeyParameters) pair.getPrivate();
            if (this.ecParams instanceof ECParameterSpec) {
                ECParameterSpec p = (ECParameterSpec) this.ecParams;
                BCECPublicKey pubKey = new BCECPublicKey(this.algorithm, pub, p, this.configuration);
                return new KeyPair(pubKey, new BCECPrivateKey(this.algorithm, priv, pubKey, p, this.configuration));
            }
            if (this.ecParams == null) {
                return new KeyPair(new BCECPublicKey(this.algorithm, pub, this.configuration), new BCECPrivateKey(this.algorithm, priv, this.configuration));
            }
            java.security.spec.ECParameterSpec p2 = (java.security.spec.ECParameterSpec) this.ecParams;
            BCECPublicKey pubKey2 = new BCECPublicKey(this.algorithm, pub, p2, this.configuration);
            return new KeyPair(pubKey2, new BCECPrivateKey(this.algorithm, priv, pubKey2, p2, this.configuration));
        }

        protected ECKeyGenerationParameters createKeyGenParamsBC(ECParameterSpec p, SecureRandom r) {
            return new ECKeyGenerationParameters(new ECDomainParameters(p.getCurve(), p.getG(), p.getN(), p.getH()), r);
        }

        protected ECKeyGenerationParameters createKeyGenParamsJCE(java.security.spec.ECParameterSpec p, SecureRandom r) {
            ECCurve curve = EC5Util.convertCurve(p.getCurve());
            ECPoint g = EC5Util.convertPoint(curve, p.getGenerator(), false);
            BigInteger n = p.getOrder();
            BigInteger h = BigInteger.valueOf(p.getCofactor());
            ECDomainParameters dp = new ECDomainParameters(curve, g, n, h);
            return new ECKeyGenerationParameters(dp, r);
        }

        protected ECNamedCurveSpec createNamedCurveSpec(String curveName) throws InvalidAlgorithmParameterException {
            X9ECParameters p = ECUtils.getDomainParametersFromName(curveName);
            if (p == null) {
                try {
                    p = ECNamedCurveTable.getByOID(new ASN1ObjectIdentifier(curveName));
                    if (p == null) {
                        Map extraCurves = this.configuration.getAdditionalECParameters();
                        p = (X9ECParameters) extraCurves.get(new ASN1ObjectIdentifier(curveName));
                        if (p == null) {
                            throw new InvalidAlgorithmParameterException("unknown curve OID: " + curveName);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    throw new InvalidAlgorithmParameterException("unknown curve name: " + curveName);
                }
            }
            return new ECNamedCurveSpec(curveName, p.getCurve(), p.getG(), p.getN(), p.getH(), null);
        }

        protected void initializeNamedCurve(String curveName, SecureRandom random) throws InvalidAlgorithmParameterException {
            ECNamedCurveSpec namedCurve = createNamedCurveSpec(curveName);
            this.ecParams = namedCurve;
            this.param = createKeyGenParamsJCE(namedCurve, random);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/ec/KeyPairGeneratorSpi$ECDSA.class */
    public static class ECDSA extends EC {
        public ECDSA() {
            super("ECDSA", BouncyCastleProvider.CONFIGURATION);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/ec/KeyPairGeneratorSpi$ECDH.class */
    public static class ECDH extends EC {
        public ECDH() {
            super("ECDH", BouncyCastleProvider.CONFIGURATION);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/ec/KeyPairGeneratorSpi$ECDHC.class */
    public static class ECDHC extends EC {
        public ECDHC() {
            super("ECDHC", BouncyCastleProvider.CONFIGURATION);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/ec/KeyPairGeneratorSpi$ECMQV.class */
    public static class ECMQV extends EC {
        public ECMQV() {
            super("ECMQV", BouncyCastleProvider.CONFIGURATION);
        }
    }
}
