package org.bouncycastle.openssl.jcajce;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/jcajce/JcaPEMKeyConverter.class */
public class JcaPEMKeyConverter {
    private JcaJceHelper helper = new DefaultJcaJceHelper();
    private final Map algorithms = new HashMap();
    private static final Map baseMappings = new HashMap();

    public JcaPEMKeyConverter() {
        this.algorithms.putAll(baseMappings);
    }

    public JcaPEMKeyConverter setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public JcaPEMKeyConverter setProvider(String str) {
        this.helper = new NamedJcaJceHelper(str);
        return this;
    }

    public JcaPEMKeyConverter setAlgorithmMapping(ASN1ObjectIdentifier aSN1ObjectIdentifier, String str) {
        this.algorithms.put(aSN1ObjectIdentifier, str);
        return this;
    }

    public KeyPair getKeyPair(PEMKeyPair pEMKeyPair) throws PEMException {
        try {
            KeyFactory keyFactory = getKeyFactory(pEMKeyPair.getPrivateKeyInfo().getPrivateKeyAlgorithm());
            return new KeyPair(keyFactory.generatePublic(new X509EncodedKeySpec(pEMKeyPair.getPublicKeyInfo().getEncoded())), keyFactory.generatePrivate(new PKCS8EncodedKeySpec(pEMKeyPair.getPrivateKeyInfo().getEncoded())));
        } catch (Exception e) {
            throw new PEMException("unable to convert key pair: " + e.getMessage(), e);
        }
    }

    public PublicKey getPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws PEMException {
        try {
            return getKeyFactory(subjectPublicKeyInfo.getAlgorithm()).generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));
        } catch (Exception e) {
            throw new PEMException("unable to convert key pair: " + e.getMessage(), e);
        }
    }

    public PrivateKey getPrivateKey(PrivateKeyInfo privateKeyInfo) throws PEMException {
        try {
            return getKeyFactory(privateKeyInfo.getPrivateKeyAlgorithm()).generatePrivate(new PKCS8EncodedKeySpec(privateKeyInfo.getEncoded()));
        } catch (Exception e) {
            throw new PEMException("unable to convert key pair: " + e.getMessage(), e);
        }
    }

    private KeyFactory getKeyFactory(AlgorithmIdentifier algorithmIdentifier) throws NoSuchAlgorithmException, NoSuchProviderException {
        ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
        String id = (String) this.algorithms.get(algorithm);
        if (id == null) {
            id = algorithm.getId();
        }
        try {
            return this.helper.createKeyFactory(id);
        } catch (NoSuchAlgorithmException e) {
            if (id.equals("ECDSA")) {
                return this.helper.createKeyFactory("EC");
            }
            throw e;
        }
    }

    static {
        baseMappings.put(X9ObjectIdentifiers.id_ecPublicKey, "ECDSA");
        baseMappings.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
        baseMappings.put(X9ObjectIdentifiers.id_dsa, "DSA");
    }
}
