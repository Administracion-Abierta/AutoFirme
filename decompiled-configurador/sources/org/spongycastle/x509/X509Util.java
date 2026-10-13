package org.spongycastle.x509;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.pkcs.RSASSAPSSparams;
import org.spongycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x9.X9ObjectIdentifiers;
import org.spongycastle.jce.X509Principal;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/x509/X509Util.class */
class X509Util {
    private static Hashtable algorithms = new Hashtable();
    private static Hashtable params = new Hashtable();
    private static Set noParams = new HashSet();

    X509Util() {
    }

    static {
        algorithms.put("MD2WITHRSAENCRYPTION", PKCSObjectIdentifiers.md2WithRSAEncryption);
        algorithms.put("MD2WITHRSA", PKCSObjectIdentifiers.md2WithRSAEncryption);
        algorithms.put("MD5WITHRSAENCRYPTION", PKCSObjectIdentifiers.md5WithRSAEncryption);
        algorithms.put("MD5WITHRSA", PKCSObjectIdentifiers.md5WithRSAEncryption);
        algorithms.put("SHA1WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha1WithRSAEncryption);
        algorithms.put("SHA1WITHRSA", PKCSObjectIdentifiers.sha1WithRSAEncryption);
        algorithms.put("SHA224WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha224WithRSAEncryption);
        algorithms.put("SHA224WITHRSA", PKCSObjectIdentifiers.sha224WithRSAEncryption);
        algorithms.put("SHA256WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha256WithRSAEncryption);
        algorithms.put("SHA256WITHRSA", PKCSObjectIdentifiers.sha256WithRSAEncryption);
        algorithms.put("SHA384WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha384WithRSAEncryption);
        algorithms.put("SHA384WITHRSA", PKCSObjectIdentifiers.sha384WithRSAEncryption);
        algorithms.put("SHA512WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha512WithRSAEncryption);
        algorithms.put("SHA512WITHRSA", PKCSObjectIdentifiers.sha512WithRSAEncryption);
        algorithms.put("SHA1WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        algorithms.put("SHA224WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        algorithms.put("SHA256WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        algorithms.put("SHA384WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        algorithms.put("SHA512WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        algorithms.put("RIPEMD160WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
        algorithms.put("RIPEMD160WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
        algorithms.put("RIPEMD128WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
        algorithms.put("RIPEMD128WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
        algorithms.put("RIPEMD256WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
        algorithms.put("RIPEMD256WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
        algorithms.put("SHA1WITHDSA", X9ObjectIdentifiers.id_dsa_with_sha1);
        algorithms.put("DSAWITHSHA1", X9ObjectIdentifiers.id_dsa_with_sha1);
        algorithms.put("SHA224WITHDSA", NISTObjectIdentifiers.dsa_with_sha224);
        algorithms.put("SHA256WITHDSA", NISTObjectIdentifiers.dsa_with_sha256);
        algorithms.put("SHA384WITHDSA", NISTObjectIdentifiers.dsa_with_sha384);
        algorithms.put("SHA512WITHDSA", NISTObjectIdentifiers.dsa_with_sha512);
        algorithms.put("SHA1WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA1);
        algorithms.put("ECDSAWITHSHA1", X9ObjectIdentifiers.ecdsa_with_SHA1);
        algorithms.put("SHA224WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA224);
        algorithms.put("SHA256WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA256);
        algorithms.put("SHA384WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA384);
        algorithms.put("SHA512WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA512);
        algorithms.put("GOST3411WITHGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
        algorithms.put("GOST3411WITHGOST3410-94", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
        algorithms.put("GOST3411WITHECGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        algorithms.put("GOST3411WITHECGOST3410-2001", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        algorithms.put("GOST3411WITHGOST3410-2001", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA1);
        noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA224);
        noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA256);
        noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA384);
        noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA512);
        noParams.add(X9ObjectIdentifiers.id_dsa_with_sha1);
        noParams.add(NISTObjectIdentifiers.dsa_with_sha224);
        noParams.add(NISTObjectIdentifiers.dsa_with_sha256);
        noParams.add(NISTObjectIdentifiers.dsa_with_sha384);
        noParams.add(NISTObjectIdentifiers.dsa_with_sha512);
        noParams.add(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
        noParams.add(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        AlgorithmIdentifier sha1AlgId = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
        params.put("SHA1WITHRSAANDMGF1", creatPSSParams(sha1AlgId, 20));
        AlgorithmIdentifier sha224AlgId = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, DERNull.INSTANCE);
        params.put("SHA224WITHRSAANDMGF1", creatPSSParams(sha224AlgId, 28));
        AlgorithmIdentifier sha256AlgId = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE);
        params.put("SHA256WITHRSAANDMGF1", creatPSSParams(sha256AlgId, 32));
        AlgorithmIdentifier sha384AlgId = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, DERNull.INSTANCE);
        params.put("SHA384WITHRSAANDMGF1", creatPSSParams(sha384AlgId, 48));
        AlgorithmIdentifier sha512AlgId = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, DERNull.INSTANCE);
        params.put("SHA512WITHRSAANDMGF1", creatPSSParams(sha512AlgId, 64));
    }

    private static RSASSAPSSparams creatPSSParams(AlgorithmIdentifier hashAlgId, int saltSize) {
        return new RSASSAPSSparams(hashAlgId, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, hashAlgId), new ASN1Integer(saltSize), new ASN1Integer(1L));
    }

    static ASN1ObjectIdentifier getAlgorithmOID(String algorithmName) {
        String algorithmName2 = Strings.toUpperCase(algorithmName);
        if (algorithms.containsKey(algorithmName2)) {
            return (ASN1ObjectIdentifier) algorithms.get(algorithmName2);
        }
        return new ASN1ObjectIdentifier(algorithmName2);
    }

    static AlgorithmIdentifier getSigAlgID(ASN1ObjectIdentifier sigOid, String algorithmName) {
        if (noParams.contains(sigOid)) {
            return new AlgorithmIdentifier(sigOid);
        }
        String algorithmName2 = Strings.toUpperCase(algorithmName);
        if (params.containsKey(algorithmName2)) {
            return new AlgorithmIdentifier(sigOid, (ASN1Encodable) params.get(algorithmName2));
        }
        return new AlgorithmIdentifier(sigOid, DERNull.INSTANCE);
    }

    static Iterator getAlgNames() {
        Enumeration e = algorithms.keys();
        List l = new ArrayList();
        while (e.hasMoreElements()) {
            l.add(e.nextElement());
        }
        return l.iterator();
    }

    static Signature getSignatureInstance(String algorithm) throws NoSuchAlgorithmException {
        return Signature.getInstance(algorithm);
    }

    static Signature getSignatureInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (provider != null) {
            return Signature.getInstance(algorithm, provider);
        }
        return Signature.getInstance(algorithm);
    }

    static byte[] calculateSignature(ASN1ObjectIdentifier sigOid, String sigName, PrivateKey key, SecureRandom random, ASN1Encodable object) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, IOException {
        if (sigOid == null) {
            throw new IllegalStateException("no signature algorithm specified");
        }
        Signature sig = getSignatureInstance(sigName);
        if (random != null) {
            sig.initSign(key, random);
        } else {
            sig.initSign(key);
        }
        sig.update(object.toASN1Primitive().getEncoded(ASN1Encoding.DER));
        return sig.sign();
    }

    static byte[] calculateSignature(ASN1ObjectIdentifier sigOid, String sigName, String provider, PrivateKey key, SecureRandom random, ASN1Encodable object) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, IOException, NoSuchProviderException {
        if (sigOid == null) {
            throw new IllegalStateException("no signature algorithm specified");
        }
        Signature sig = getSignatureInstance(sigName, provider);
        if (random != null) {
            sig.initSign(key, random);
        } else {
            sig.initSign(key);
        }
        sig.update(object.toASN1Primitive().getEncoded(ASN1Encoding.DER));
        return sig.sign();
    }

    static X509Principal convertPrincipal(X500Principal principal) {
        try {
            return new X509Principal(principal.getEncoded());
        } catch (IOException e) {
            throw new IllegalArgumentException("cannot convert principal");
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/x509/X509Util$Implementation.class */
    static class Implementation {
        Object engine;
        Provider provider;

        Implementation(Object engine, Provider provider) {
            this.engine = engine;
            this.provider = provider;
        }

        Object getEngine() {
            return this.engine;
        }

        Provider getProvider() {
            return this.provider;
        }
    }

    static Implementation getImplementation(String baseName, String algorithm, Provider prov) throws NoSuchAlgorithmException {
        String algorithm2;
        Class<?> cls;
        String upperCase = Strings.toUpperCase(algorithm);
        while (true) {
            algorithm2 = upperCase;
            String alias = prov.getProperty("Alg.Alias." + baseName + "." + algorithm2);
            if (alias == null) {
                break;
            }
            upperCase = alias;
        }
        String className = prov.getProperty(baseName + "." + algorithm2);
        if (className != null) {
            try {
                ClassLoader clsLoader = prov.getClass().getClassLoader();
                if (clsLoader != null) {
                    cls = clsLoader.loadClass(className);
                } else {
                    cls = Class.forName(className);
                }
                return new Implementation(cls.newInstance(), prov);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("algorithm " + algorithm2 + " in provider " + prov.getName() + " but no class \"" + className + "\" found!");
            } catch (Exception e2) {
                throw new IllegalStateException("algorithm " + algorithm2 + " in provider " + prov.getName() + " but class \"" + className + "\" inaccessible!");
            }
        }
        throw new NoSuchAlgorithmException("cannot find implementation " + algorithm2 + " for provider " + prov.getName());
    }

    static Implementation getImplementation(String baseName, String algorithm) throws NoSuchAlgorithmException {
        Provider[] prov = Security.getProviders();
        for (int i = 0; i != prov.length; i++) {
            Implementation imp = getImplementation(baseName, Strings.toUpperCase(algorithm), prov[i]);
            if (imp != null) {
                return imp;
            }
            try {
                getImplementation(baseName, algorithm, prov[i]);
            } catch (NoSuchAlgorithmException e) {
            }
        }
        throw new NoSuchAlgorithmException("cannot find implementation " + algorithm);
    }

    static Provider getProvider(String provider) throws NoSuchProviderException {
        Provider prov = Security.getProvider(provider);
        if (prov == null) {
            throw new NoSuchProviderException("Provider " + provider + " not found");
        }
        return prov;
    }
}
