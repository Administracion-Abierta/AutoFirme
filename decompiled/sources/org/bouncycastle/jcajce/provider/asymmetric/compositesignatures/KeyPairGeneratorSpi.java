package org.bouncycastle.jcajce.provider.asymmetric.compositesignatures;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.CompositePrivateKey;
import org.bouncycastle.jcajce.CompositePublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi.class */
public class KeyPairGeneratorSpi extends java.security.KeyPairGeneratorSpi {
    private final ASN1ObjectIdentifier algorithm;
    private final KeyPairGenerator[] generators;
    private SecureRandom secureRandom;
    private boolean parametersInitialized = false;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$HashMLDSA44_ECDSA_P256_SHA256.class */
    public static final class HashMLDSA44_ECDSA_P256_SHA256 extends KeyPairGeneratorSpi {
        public HashMLDSA44_ECDSA_P256_SHA256() {
            super(MiscObjectIdentifiers.id_HashMLDSA44_ECDSA_P256_SHA256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$HashMLDSA44_Ed25519_SHA512.class */
    public static final class HashMLDSA44_Ed25519_SHA512 extends KeyPairGeneratorSpi {
        public HashMLDSA44_Ed25519_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA44_Ed25519_SHA512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$HashMLDSA44_RSA2048_PKCS15_SHA256.class */
    public static final class HashMLDSA44_RSA2048_PKCS15_SHA256 extends KeyPairGeneratorSpi {
        public HashMLDSA44_RSA2048_PKCS15_SHA256() {
            super(MiscObjectIdentifiers.id_HashMLDSA44_RSA2048_PKCS15_SHA256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$HashMLDSA44_RSA2048_PSS_SHA256.class */
    public static final class HashMLDSA44_RSA2048_PSS_SHA256 extends KeyPairGeneratorSpi {
        public HashMLDSA44_RSA2048_PSS_SHA256() {
            super(MiscObjectIdentifiers.id_HashMLDSA44_RSA2048_PSS_SHA256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$HashMLDSA65_ECDSA_P384_SHA512.class */
    public static final class HashMLDSA65_ECDSA_P384_SHA512 extends KeyPairGeneratorSpi {
        public HashMLDSA65_ECDSA_P384_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA65_ECDSA_P384_SHA512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$HashMLDSA65_ECDSA_brainpoolP256r1_SHA512.class */
    public static final class HashMLDSA65_ECDSA_brainpoolP256r1_SHA512 extends KeyPairGeneratorSpi {
        public HashMLDSA65_ECDSA_brainpoolP256r1_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA65_ECDSA_brainpoolP256r1_SHA512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$HashMLDSA65_Ed25519_SHA512.class */
    public static final class HashMLDSA65_Ed25519_SHA512 extends KeyPairGeneratorSpi {
        public HashMLDSA65_Ed25519_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA65_Ed25519_SHA512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$HashMLDSA65_RSA3072_PKCS15_SHA512.class */
    public static final class HashMLDSA65_RSA3072_PKCS15_SHA512 extends KeyPairGeneratorSpi {
        public HashMLDSA65_RSA3072_PKCS15_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA65_RSA3072_PKCS15_SHA512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$HashMLDSA65_RSA3072_PSS_SHA512.class */
    public static final class HashMLDSA65_RSA3072_PSS_SHA512 extends KeyPairGeneratorSpi {
        public HashMLDSA65_RSA3072_PSS_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA65_RSA3072_PSS_SHA512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$HashMLDSA65_RSA4096_PKCS15_SHA512.class */
    public static final class HashMLDSA65_RSA4096_PKCS15_SHA512 extends KeyPairGeneratorSpi {
        public HashMLDSA65_RSA4096_PKCS15_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA65_RSA4096_PKCS15_SHA512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$HashMLDSA65_RSA4096_PSS_SHA512.class */
    public static final class HashMLDSA65_RSA4096_PSS_SHA512 extends KeyPairGeneratorSpi {
        public HashMLDSA65_RSA4096_PSS_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA65_RSA4096_PSS_SHA512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$HashMLDSA87_ECDSA_P384_SHA512.class */
    public static final class HashMLDSA87_ECDSA_P384_SHA512 extends KeyPairGeneratorSpi {
        public HashMLDSA87_ECDSA_P384_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA87_ECDSA_P384_SHA512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$HashMLDSA87_ECDSA_brainpoolP384r1_SHA512.class */
    public static final class HashMLDSA87_ECDSA_brainpoolP384r1_SHA512 extends KeyPairGeneratorSpi {
        public HashMLDSA87_ECDSA_brainpoolP384r1_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA87_ECDSA_brainpoolP384r1_SHA512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$HashMLDSA87_Ed448_SHA512.class */
    public static final class HashMLDSA87_Ed448_SHA512 extends KeyPairGeneratorSpi {
        public HashMLDSA87_Ed448_SHA512() {
            super(MiscObjectIdentifiers.id_HashMLDSA87_Ed448_SHA512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$MLDSA44_ECDSA_P256_SHA256.class */
    public static final class MLDSA44_ECDSA_P256_SHA256 extends KeyPairGeneratorSpi {
        public MLDSA44_ECDSA_P256_SHA256() {
            super(MiscObjectIdentifiers.id_MLDSA44_ECDSA_P256_SHA256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$MLDSA44_Ed25519_SHA512.class */
    public static final class MLDSA44_Ed25519_SHA512 extends KeyPairGeneratorSpi {
        public MLDSA44_Ed25519_SHA512() {
            super(MiscObjectIdentifiers.id_MLDSA44_Ed25519_SHA512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$MLDSA44_RSA2048_PKCS15_SHA256.class */
    public static final class MLDSA44_RSA2048_PKCS15_SHA256 extends KeyPairGeneratorSpi {
        public MLDSA44_RSA2048_PKCS15_SHA256() {
            super(MiscObjectIdentifiers.id_MLDSA44_RSA2048_PKCS15_SHA256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$MLDSA44_RSA2048_PSS_SHA256.class */
    public static final class MLDSA44_RSA2048_PSS_SHA256 extends KeyPairGeneratorSpi {
        public MLDSA44_RSA2048_PSS_SHA256() {
            super(MiscObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$MLDSA65_ECDSA_P384_SHA384.class */
    public static final class MLDSA65_ECDSA_P384_SHA384 extends KeyPairGeneratorSpi {
        public MLDSA65_ECDSA_P384_SHA384() {
            super(MiscObjectIdentifiers.id_MLDSA65_ECDSA_P384_SHA384);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$MLDSA65_ECDSA_brainpoolP256r1_SHA256.class */
    public static final class MLDSA65_ECDSA_brainpoolP256r1_SHA256 extends KeyPairGeneratorSpi {
        public MLDSA65_ECDSA_brainpoolP256r1_SHA256() {
            super(MiscObjectIdentifiers.id_MLDSA65_ECDSA_brainpoolP256r1_SHA256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$MLDSA65_Ed25519_SHA512.class */
    public static final class MLDSA65_Ed25519_SHA512 extends KeyPairGeneratorSpi {
        public MLDSA65_Ed25519_SHA512() {
            super(MiscObjectIdentifiers.id_MLDSA65_Ed25519_SHA512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$MLDSA65_RSA3072_PKCS15_SHA256.class */
    public static final class MLDSA65_RSA3072_PKCS15_SHA256 extends KeyPairGeneratorSpi {
        public MLDSA65_RSA3072_PKCS15_SHA256() {
            super(MiscObjectIdentifiers.id_MLDSA65_RSA3072_PKCS15_SHA256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$MLDSA65_RSA3072_PSS_SHA256.class */
    public static final class MLDSA65_RSA3072_PSS_SHA256 extends KeyPairGeneratorSpi {
        public MLDSA65_RSA3072_PSS_SHA256() {
            super(MiscObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA256);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$MLDSA65_RSA4096_PKCS15_SHA384.class */
    public static final class MLDSA65_RSA4096_PKCS15_SHA384 extends KeyPairGeneratorSpi {
        public MLDSA65_RSA4096_PKCS15_SHA384() {
            super(MiscObjectIdentifiers.id_MLDSA65_RSA4096_PKCS15_SHA384);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$MLDSA65_RSA4096_PSS_SHA384.class */
    public static final class MLDSA65_RSA4096_PSS_SHA384 extends KeyPairGeneratorSpi {
        public MLDSA65_RSA4096_PSS_SHA384() {
            super(MiscObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA384);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$MLDSA87_ECDSA_P384_SHA384.class */
    public static final class MLDSA87_ECDSA_P384_SHA384 extends KeyPairGeneratorSpi {
        public MLDSA87_ECDSA_P384_SHA384() {
            super(MiscObjectIdentifiers.id_MLDSA87_ECDSA_P384_SHA384);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$MLDSA87_ECDSA_brainpoolP384r1_SHA384.class */
    public static final class MLDSA87_ECDSA_brainpoolP384r1_SHA384 extends KeyPairGeneratorSpi {
        public MLDSA87_ECDSA_brainpoolP384r1_SHA384() {
            super(MiscObjectIdentifiers.id_MLDSA87_ECDSA_brainpoolP384r1_SHA384);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/compositesignatures/KeyPairGeneratorSpi$MLDSA87_Ed448_SHA512.class */
    public static final class MLDSA87_Ed448_SHA512 extends KeyPairGeneratorSpi {
        public MLDSA87_Ed448_SHA512() {
            super(MiscObjectIdentifiers.id_MLDSA87_Ed448_SHA512);
        }
    }

    KeyPairGeneratorSpi(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.algorithm = aSN1ObjectIdentifier;
        String[] pairing = CompositeIndex.getPairing(aSN1ObjectIdentifier);
        AlgorithmParameterSpec[] keyPairSpecs = CompositeIndex.getKeyPairSpecs(aSN1ObjectIdentifier);
        this.generators = new KeyPairGenerator[pairing.length];
        for (int i = 0; i != pairing.length; i++) {
            try {
                this.generators[i] = KeyPairGenerator.getInstance(CompositeIndex.getBaseName(pairing[i]), BouncyCastleProvider.PROVIDER_NAME);
                AlgorithmParameterSpec algorithmParameterSpec = keyPairSpecs[i];
                if (algorithmParameterSpec != null) {
                    this.generators[i].initialize(algorithmParameterSpec);
                }
            } catch (Exception e) {
                throw new IllegalStateException("unable to create base generator: " + e.getMessage());
            }
        }
    }

    @Override // java.security.KeyPairGeneratorSpi
    public void initialize(int i, SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    @Override // java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null) {
            throw new IllegalArgumentException("Use initialize only for custom SecureRandom. AlgorithmParameterSpec must be null because it is determined by algorithm name.");
        }
        AlgorithmParameterSpec[] keyPairSpecs = CompositeIndex.getKeyPairSpecs(this.algorithm);
        for (int i = 0; i != keyPairSpecs.length; i++) {
            AlgorithmParameterSpec algorithmParameterSpec2 = keyPairSpecs[i];
            if (algorithmParameterSpec2 != null) {
                this.generators[i].initialize(algorithmParameterSpec2, secureRandom);
            }
        }
    }

    @Override // java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        return getCompositeKeyPair();
    }

    private KeyPair getCompositeKeyPair() {
        PublicKey[] publicKeyArr = new PublicKey[this.generators.length];
        PrivateKey[] privateKeyArr = new PrivateKey[this.generators.length];
        for (int i = 0; i < this.generators.length; i++) {
            KeyPair keyPairGenerateKeyPair = this.generators[i].generateKeyPair();
            publicKeyArr[i] = keyPairGenerateKeyPair.getPublic();
            privateKeyArr[i] = keyPairGenerateKeyPair.getPrivate();
        }
        return new KeyPair(new CompositePublicKey(this.algorithm, publicKeyArr), new CompositePrivateKey(this.algorithm, privateKeyArr));
    }
}
