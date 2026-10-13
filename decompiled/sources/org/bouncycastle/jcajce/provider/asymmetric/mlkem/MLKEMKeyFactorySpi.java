package org.bouncycastle.jcajce.provider.asymmetric.mlkem;

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
import org.bouncycastle.jcajce.spec.MLKEMPrivateKeySpec;
import org.bouncycastle.jcajce.spec.MLKEMPublicKeySpec;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.util.BaseKeyFactorySpi;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mlkem/MLKEMKeyFactorySpi.class */
public class MLKEMKeyFactorySpi extends BaseKeyFactorySpi {
    private static final Set<ASN1ObjectIdentifier> keyOids = new HashSet();

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mlkem/MLKEMKeyFactorySpi$MLKEM1024.class */
    public static class MLKEM1024 extends MLKEMKeyFactorySpi {
        public MLKEM1024() {
            super(NISTObjectIdentifiers.id_alg_ml_kem_1024);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mlkem/MLKEMKeyFactorySpi$MLKEM512.class */
    public static class MLKEM512 extends MLKEMKeyFactorySpi {
        public MLKEM512() {
            super(NISTObjectIdentifiers.id_alg_ml_kem_512);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mlkem/MLKEMKeyFactorySpi$MLKEM768.class */
    public static class MLKEM768 extends MLKEMKeyFactorySpi {
        public MLKEM768() {
            super(NISTObjectIdentifiers.id_alg_ml_kem_768);
        }
    }

    public MLKEMKeyFactorySpi() {
        super(keyOids);
    }

    public MLKEMKeyFactorySpi(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        super(aSN1ObjectIdentifier);
    }

    @Override // java.security.KeyFactorySpi
    public final KeySpec engineGetKeySpec(Key key, Class cls) throws InvalidKeySpecException {
        if (key instanceof BCMLKEMPrivateKey) {
            if (PKCS8EncodedKeySpec.class.isAssignableFrom(cls)) {
                return new PKCS8EncodedKeySpec(key.getEncoded());
            }
            if (MLKEMPrivateKeySpec.class.isAssignableFrom(cls)) {
                BCMLKEMPrivateKey bCMLKEMPrivateKey = (BCMLKEMPrivateKey) key;
                byte[] seed = bCMLKEMPrivateKey.getSeed();
                return seed != null ? new MLKEMPrivateKeySpec(bCMLKEMPrivateKey.getParameterSpec(), seed) : new MLKEMPrivateKeySpec(bCMLKEMPrivateKey.getParameterSpec(), bCMLKEMPrivateKey.getPrivateData(), bCMLKEMPrivateKey.getPublicKey().getPublicData());
            }
            if (MLKEMPublicKeySpec.class.isAssignableFrom(cls)) {
                BCMLKEMPrivateKey bCMLKEMPrivateKey2 = (BCMLKEMPrivateKey) key;
                return new MLKEMPublicKeySpec(bCMLKEMPrivateKey2.getParameterSpec(), bCMLKEMPrivateKey2.getPublicKey().getPublicData());
            }
        } else {
            if (!(key instanceof BCMLKEMPublicKey)) {
                throw new InvalidKeySpecException("Unsupported key type: " + key.getClass() + ".");
            }
            if (X509EncodedKeySpec.class.isAssignableFrom(cls)) {
                return new X509EncodedKeySpec(key.getEncoded());
            }
            if (MLKEMPublicKeySpec.class.isAssignableFrom(cls)) {
                BCMLKEMPublicKey bCMLKEMPublicKey = (BCMLKEMPublicKey) key;
                return new MLKEMPublicKeySpec(bCMLKEMPublicKey.getParameterSpec(), bCMLKEMPublicKey.getPublicData());
            }
        }
        throw new InvalidKeySpecException("unknown key specification: " + cls + ".");
    }

    @Override // java.security.KeyFactorySpi
    public final Key engineTranslateKey(Key key) throws InvalidKeyException {
        if ((key instanceof BCMLKEMPrivateKey) || (key instanceof BCMLKEMPublicKey)) {
            return key;
        }
        throw new InvalidKeyException("unsupported key type");
    }

    @Override // org.bouncycastle.pqc.jcajce.provider.util.BaseKeyFactorySpi, java.security.KeyFactorySpi
    public PrivateKey engineGeneratePrivate(KeySpec keySpec) throws InvalidKeySpecException {
        MLKEMPrivateKeyParameters mLKEMPrivateKeyParameters;
        if (!(keySpec instanceof MLKEMPrivateKeySpec)) {
            return super.engineGeneratePrivate(keySpec);
        }
        MLKEMPrivateKeySpec mLKEMPrivateKeySpec = (MLKEMPrivateKeySpec) keySpec;
        MLKEMParameters parameters = Utils.getParameters(mLKEMPrivateKeySpec.getParameterSpec().getName());
        if (mLKEMPrivateKeySpec.isSeed()) {
            mLKEMPrivateKeyParameters = new MLKEMPrivateKeyParameters(parameters, mLKEMPrivateKeySpec.getSeed());
        } else {
            mLKEMPrivateKeyParameters = new MLKEMPrivateKeyParameters(parameters, mLKEMPrivateKeySpec.getPrivateData());
            byte[] publicData = mLKEMPrivateKeySpec.getPublicData();
            if (publicData != null && !Arrays.constantTimeAreEqual(publicData, mLKEMPrivateKeyParameters.getPublicKey())) {
                throw new InvalidKeySpecException("public key data does not match private key data");
            }
        }
        return new BCMLKEMPrivateKey(mLKEMPrivateKeyParameters);
    }

    @Override // org.bouncycastle.pqc.jcajce.provider.util.BaseKeyFactorySpi, java.security.KeyFactorySpi
    public PublicKey engineGeneratePublic(KeySpec keySpec) throws InvalidKeySpecException {
        if (!(keySpec instanceof MLKEMPublicKeySpec)) {
            return super.engineGeneratePublic(keySpec);
        }
        MLKEMPublicKeySpec mLKEMPublicKeySpec = (MLKEMPublicKeySpec) keySpec;
        return new BCMLKEMPublicKey(new MLKEMPublicKeyParameters(Utils.getParameters(mLKEMPublicKeySpec.getParameterSpec().getName()), mLKEMPublicKeySpec.getPublicData()));
    }

    @Override // org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter
    public PrivateKey generatePrivate(PrivateKeyInfo privateKeyInfo) throws IOException {
        return new BCMLKEMPrivateKey(privateKeyInfo);
    }

    @Override // org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter
    public PublicKey generatePublic(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        return new BCMLKEMPublicKey(subjectPublicKeyInfo);
    }

    static {
        keyOids.add(NISTObjectIdentifiers.id_alg_ml_kem_512);
        keyOids.add(NISTObjectIdentifiers.id_alg_ml_kem_768);
        keyOids.add(NISTObjectIdentifiers.id_alg_ml_kem_1024);
    }
}
