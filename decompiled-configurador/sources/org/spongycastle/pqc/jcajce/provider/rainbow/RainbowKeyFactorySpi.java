package org.spongycastle.pqc.jcajce.provider.rainbow;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactorySpi;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.spongycastle.pqc.asn1.RainbowPrivateKey;
import org.spongycastle.pqc.asn1.RainbowPublicKey;
import org.spongycastle.pqc.jcajce.spec.RainbowPrivateKeySpec;
import org.spongycastle.pqc.jcajce.spec.RainbowPublicKeySpec;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/rainbow/RainbowKeyFactorySpi.class */
public class RainbowKeyFactorySpi extends KeyFactorySpi implements AsymmetricKeyInfoConverter {
    @Override // java.security.KeyFactorySpi
    public PrivateKey engineGeneratePrivate(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof RainbowPrivateKeySpec) {
            return new BCRainbowPrivateKey((RainbowPrivateKeySpec) keySpec);
        }
        if (keySpec instanceof PKCS8EncodedKeySpec) {
            byte[] encKey = ((PKCS8EncodedKeySpec) keySpec).getEncoded();
            try {
                return generatePrivate(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(encKey)));
            } catch (Exception e) {
                throw new InvalidKeySpecException(e.toString());
            }
        }
        throw new InvalidKeySpecException("Unsupported key specification: " + keySpec.getClass() + ".");
    }

    @Override // java.security.KeyFactorySpi
    public PublicKey engineGeneratePublic(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof RainbowPublicKeySpec) {
            return new BCRainbowPublicKey((RainbowPublicKeySpec) keySpec);
        }
        if (keySpec instanceof X509EncodedKeySpec) {
            byte[] encKey = ((X509EncodedKeySpec) keySpec).getEncoded();
            try {
                return generatePublic(SubjectPublicKeyInfo.getInstance(encKey));
            } catch (Exception e) {
                throw new InvalidKeySpecException(e.toString());
            }
        }
        throw new InvalidKeySpecException("Unknown key specification: " + keySpec + ".");
    }

    @Override // java.security.KeyFactorySpi
    public final KeySpec engineGetKeySpec(Key key, Class keySpec) throws InvalidKeySpecException {
        if (key instanceof BCRainbowPrivateKey) {
            if (PKCS8EncodedKeySpec.class.isAssignableFrom(keySpec)) {
                return new PKCS8EncodedKeySpec(key.getEncoded());
            }
            if (RainbowPrivateKeySpec.class.isAssignableFrom(keySpec)) {
                BCRainbowPrivateKey privKey = (BCRainbowPrivateKey) key;
                return new RainbowPrivateKeySpec(privKey.getInvA1(), privKey.getB1(), privKey.getInvA2(), privKey.getB2(), privKey.getVi(), privKey.getLayers());
            }
        } else if (key instanceof BCRainbowPublicKey) {
            if (X509EncodedKeySpec.class.isAssignableFrom(keySpec)) {
                return new X509EncodedKeySpec(key.getEncoded());
            }
            if (RainbowPublicKeySpec.class.isAssignableFrom(keySpec)) {
                BCRainbowPublicKey pubKey = (BCRainbowPublicKey) key;
                return new RainbowPublicKeySpec(pubKey.getDocLength(), pubKey.getCoeffQuadratic(), pubKey.getCoeffSingular(), pubKey.getCoeffScalar());
            }
        } else {
            throw new InvalidKeySpecException("Unsupported key type: " + key.getClass() + ".");
        }
        throw new InvalidKeySpecException("Unknown key specification: " + keySpec + ".");
    }

    @Override // java.security.KeyFactorySpi
    public final Key engineTranslateKey(Key key) throws InvalidKeyException {
        if ((key instanceof BCRainbowPrivateKey) || (key instanceof BCRainbowPublicKey)) {
            return key;
        }
        throw new InvalidKeyException("Unsupported key type");
    }

    @Override // org.spongycastle.jcajce.provider.util.AsymmetricKeyInfoConverter
    public PrivateKey generatePrivate(PrivateKeyInfo keyInfo) throws IOException {
        RainbowPrivateKey pKey = RainbowPrivateKey.getInstance(keyInfo.parsePrivateKey());
        return new BCRainbowPrivateKey(pKey.getInvA1(), pKey.getB1(), pKey.getInvA2(), pKey.getB2(), pKey.getVi(), pKey.getLayers());
    }

    @Override // org.spongycastle.jcajce.provider.util.AsymmetricKeyInfoConverter
    public PublicKey generatePublic(SubjectPublicKeyInfo keyInfo) throws IOException {
        RainbowPublicKey pKey = RainbowPublicKey.getInstance(keyInfo.parsePublicKey());
        return new BCRainbowPublicKey(pKey.getDocLength(), pKey.getCoeffQuadratic(), pKey.getCoeffSingular(), pKey.getCoeffScalar());
    }
}
