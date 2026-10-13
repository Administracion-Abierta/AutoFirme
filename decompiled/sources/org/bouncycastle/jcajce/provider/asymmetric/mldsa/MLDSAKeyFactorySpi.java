package org.bouncycastle.jcajce.provider.asymmetric.mldsa;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.spec.MLDSAPrivateKeySpec;
import org.bouncycastle.jcajce.spec.MLDSAPublicKeySpec;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.util.BaseKeyFactorySpi;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/MLDSAKeyFactorySpi.class */
public class MLDSAKeyFactorySpi extends BaseKeyFactorySpi {
    private static final Set<ASN1ObjectIdentifier> pureKeyOids = new HashSet();
    private static final Set<ASN1ObjectIdentifier> hashKeyOids = new HashSet();

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/MLDSAKeyFactorySpi$Hash.class */
    public static class Hash extends MLDSAKeyFactorySpi {
        public Hash() {
            super((Set<ASN1ObjectIdentifier>) MLDSAKeyFactorySpi.hashKeyOids);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/MLDSAKeyFactorySpi$HashMLDSA44.class */
    public static class HashMLDSA44 extends MLDSAKeyFactorySpi {
        public HashMLDSA44() {
            super(NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/MLDSAKeyFactorySpi$HashMLDSA65.class */
    public static class HashMLDSA65 extends MLDSAKeyFactorySpi {
        public HashMLDSA65() {
            super(NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/MLDSAKeyFactorySpi$HashMLDSA87.class */
    public static class HashMLDSA87 extends MLDSAKeyFactorySpi {
        public HashMLDSA87() {
            super(NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/MLDSAKeyFactorySpi$MLDSA44.class */
    public static class MLDSA44 extends MLDSAKeyFactorySpi {
        public MLDSA44() {
            super(NISTObjectIdentifiers.id_ml_dsa_44);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/MLDSAKeyFactorySpi$MLDSA65.class */
    public static class MLDSA65 extends MLDSAKeyFactorySpi {
        public MLDSA65() {
            super(NISTObjectIdentifiers.id_ml_dsa_65);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/MLDSAKeyFactorySpi$MLDSA87.class */
    public static class MLDSA87 extends MLDSAKeyFactorySpi {
        public MLDSA87() {
            super(NISTObjectIdentifiers.id_ml_dsa_87);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/MLDSAKeyFactorySpi$Pure.class */
    public static class Pure extends MLDSAKeyFactorySpi {
        public Pure() {
            super((Set<ASN1ObjectIdentifier>) MLDSAKeyFactorySpi.pureKeyOids);
        }
    }

    public MLDSAKeyFactorySpi(Set<ASN1ObjectIdentifier> set) {
        super(set);
    }

    public MLDSAKeyFactorySpi(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        super(aSN1ObjectIdentifier);
    }

    @Override // java.security.KeyFactorySpi
    public final KeySpec engineGetKeySpec(Key key, Class cls) throws InvalidKeySpecException {
        if (key instanceof BCMLDSAPrivateKey) {
            if (PKCS8EncodedKeySpec.class.isAssignableFrom(cls)) {
                return new PKCS8EncodedKeySpec(key.getEncoded());
            }
            if (MLDSAPrivateKeySpec.class.isAssignableFrom(cls)) {
                BCMLDSAPrivateKey bCMLDSAPrivateKey = (BCMLDSAPrivateKey) key;
                byte[] seed = bCMLDSAPrivateKey.getSeed();
                return seed != null ? new MLDSAPrivateKeySpec(bCMLDSAPrivateKey.getParameterSpec(), seed) : new MLDSAPrivateKeySpec(bCMLDSAPrivateKey.getParameterSpec(), bCMLDSAPrivateKey.getPrivateData(), bCMLDSAPrivateKey.getPublicKey().getPublicData());
            }
            if (MLDSAPublicKeySpec.class.isAssignableFrom(cls)) {
                BCMLDSAPrivateKey bCMLDSAPrivateKey2 = (BCMLDSAPrivateKey) key;
                return new MLDSAPublicKeySpec(bCMLDSAPrivateKey2.getParameterSpec(), bCMLDSAPrivateKey2.getPublicKey().getPublicData());
            }
        } else {
            if (!(key instanceof BCMLDSAPublicKey)) {
                throw new InvalidKeySpecException("unsupported key type: " + key.getClass() + ".");
            }
            if (X509EncodedKeySpec.class.isAssignableFrom(cls)) {
                return new X509EncodedKeySpec(key.getEncoded());
            }
            if (MLDSAPublicKeySpec.class.isAssignableFrom(cls)) {
                BCMLDSAPublicKey bCMLDSAPublicKey = (BCMLDSAPublicKey) key;
                return new MLDSAPublicKeySpec(bCMLDSAPublicKey.getParameterSpec(), bCMLDSAPublicKey.getPublicData());
            }
        }
        throw new InvalidKeySpecException("unknown key specification: " + cls + ".");
    }

    @Override // java.security.KeyFactorySpi
    public final Key engineTranslateKey(Key key) throws InvalidKeyException {
        if ((key instanceof BCMLDSAPrivateKey) || (key instanceof BCMLDSAPublicKey)) {
            return key;
        }
        throw new InvalidKeyException("unsupported key type");
    }

    @Override // org.bouncycastle.pqc.jcajce.provider.util.BaseKeyFactorySpi, java.security.KeyFactorySpi
    public PrivateKey engineGeneratePrivate(KeySpec keySpec) throws InvalidKeySpecException {
        MLDSAPrivateKeyParameters mLDSAPrivateKeyParameters;
        if (!(keySpec instanceof MLDSAPrivateKeySpec)) {
            return super.engineGeneratePrivate(keySpec);
        }
        MLDSAPrivateKeySpec mLDSAPrivateKeySpec = (MLDSAPrivateKeySpec) keySpec;
        MLDSAParameters parameters = Utils.getParameters(mLDSAPrivateKeySpec.getParameterSpec().getName());
        if (mLDSAPrivateKeySpec.isSeed()) {
            mLDSAPrivateKeyParameters = new MLDSAPrivateKeyParameters(parameters, mLDSAPrivateKeySpec.getSeed());
        } else {
            mLDSAPrivateKeyParameters = new MLDSAPrivateKeyParameters(parameters, mLDSAPrivateKeySpec.getPrivateData(), null);
            byte[] publicData = mLDSAPrivateKeySpec.getPublicData();
            if (publicData != null && !Arrays.constantTimeAreEqual(publicData, mLDSAPrivateKeyParameters.getPublicKey())) {
                throw new InvalidKeySpecException("public key data does not match private key data");
            }
        }
        return new BCMLDSAPrivateKey(mLDSAPrivateKeyParameters);
    }

    @Override // org.bouncycastle.pqc.jcajce.provider.util.BaseKeyFactorySpi, java.security.KeyFactorySpi
    public PublicKey engineGeneratePublic(KeySpec keySpec) throws InvalidKeySpecException {
        if (!(keySpec instanceof MLDSAPublicKeySpec)) {
            return super.engineGeneratePublic(keySpec);
        }
        MLDSAPublicKeySpec mLDSAPublicKeySpec = (MLDSAPublicKeySpec) keySpec;
        return new BCMLDSAPublicKey(new MLDSAPublicKeyParameters(Utils.getParameters(mLDSAPublicKeySpec.getParameterSpec().getName()), mLDSAPublicKeySpec.getPublicData()));
    }

    @Override // org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter
    public PrivateKey generatePrivate(PrivateKeyInfo privateKeyInfo) throws IOException {
        return new BCMLDSAPrivateKey(privateKeyInfo);
    }

    @Override // org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter
    public PublicKey generatePublic(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        return new BCMLDSAPublicKey(subjectPublicKeyInfo);
    }

    static {
        pureKeyOids.add(NISTObjectIdentifiers.id_ml_dsa_44);
        pureKeyOids.add(NISTObjectIdentifiers.id_ml_dsa_65);
        pureKeyOids.add(NISTObjectIdentifiers.id_ml_dsa_87);
        hashKeyOids.add(NISTObjectIdentifiers.id_ml_dsa_44);
        hashKeyOids.add(NISTObjectIdentifiers.id_ml_dsa_65);
        hashKeyOids.add(NISTObjectIdentifiers.id_ml_dsa_87);
        hashKeyOids.add(NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512);
        hashKeyOids.add(NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512);
        hashKeyOids.add(NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512);
    }
}
