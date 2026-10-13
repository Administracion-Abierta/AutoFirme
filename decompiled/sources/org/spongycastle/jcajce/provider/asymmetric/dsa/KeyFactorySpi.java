package org.spongycastle.jcajce.provider.asymmetric.dsa;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/dsa/KeyFactorySpi.class */
public class KeyFactorySpi extends BaseKeyFactorySpi {
    @Override // org.spongycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi, java.security.KeyFactorySpi
    protected KeySpec engineGetKeySpec(Key key, Class spec) throws InvalidKeySpecException {
        if (spec.isAssignableFrom(DSAPublicKeySpec.class) && (key instanceof DSAPublicKey)) {
            DSAPublicKey k = (DSAPublicKey) key;
            return new DSAPublicKeySpec(k.getY(), k.getParams().getP(), k.getParams().getQ(), k.getParams().getG());
        }
        if (spec.isAssignableFrom(DSAPrivateKeySpec.class) && (key instanceof DSAPrivateKey)) {
            DSAPrivateKey k2 = (DSAPrivateKey) key;
            return new DSAPrivateKeySpec(k2.getX(), k2.getParams().getP(), k2.getParams().getQ(), k2.getParams().getG());
        }
        return super.engineGetKeySpec(key, spec);
    }

    @Override // java.security.KeyFactorySpi
    protected Key engineTranslateKey(Key key) throws InvalidKeyException {
        if (key instanceof DSAPublicKey) {
            return new BCDSAPublicKey((DSAPublicKey) key);
        }
        if (key instanceof DSAPrivateKey) {
            return new BCDSAPrivateKey((DSAPrivateKey) key);
        }
        throw new InvalidKeyException("key type unknown");
    }

    @Override // org.spongycastle.jcajce.provider.util.AsymmetricKeyInfoConverter
    public PrivateKey generatePrivate(PrivateKeyInfo keyInfo) throws IOException {
        ASN1ObjectIdentifier algOid = keyInfo.getPrivateKeyAlgorithm().getAlgorithm();
        if (DSAUtil.isDsaOid(algOid)) {
            return new BCDSAPrivateKey(keyInfo);
        }
        throw new IOException("algorithm identifier " + algOid + " in key not recognised");
    }

    @Override // org.spongycastle.jcajce.provider.util.AsymmetricKeyInfoConverter
    public PublicKey generatePublic(SubjectPublicKeyInfo keyInfo) throws IOException {
        ASN1ObjectIdentifier algOid = keyInfo.getAlgorithm().getAlgorithm();
        if (DSAUtil.isDsaOid(algOid)) {
            return new BCDSAPublicKey(keyInfo);
        }
        throw new IOException("algorithm identifier " + algOid + " in key not recognised");
    }

    @Override // org.spongycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi, java.security.KeyFactorySpi
    protected PrivateKey engineGeneratePrivate(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof DSAPrivateKeySpec) {
            return new BCDSAPrivateKey((DSAPrivateKeySpec) keySpec);
        }
        return super.engineGeneratePrivate(keySpec);
    }

    @Override // org.spongycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi, java.security.KeyFactorySpi
    protected PublicKey engineGeneratePublic(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof DSAPublicKeySpec) {
            try {
                return new BCDSAPublicKey((DSAPublicKeySpec) keySpec);
            } catch (Exception e) {
                throw new InvalidKeySpecException("invalid KeySpec: " + e.getMessage()) { // from class: org.spongycastle.jcajce.provider.asymmetric.dsa.KeyFactorySpi.1
                    @Override // java.lang.Throwable
                    public Throwable getCause() {
                        return e;
                    }
                };
            }
        }
        return super.engineGeneratePublic(keySpec);
    }
}
