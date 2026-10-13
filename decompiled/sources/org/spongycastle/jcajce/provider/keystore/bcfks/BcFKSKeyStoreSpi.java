package org.spongycastle.jcajce.provider.keystore.bcfks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.bc.EncryptedObjectStoreData;
import org.spongycastle.asn1.bc.EncryptedPrivateKeyData;
import org.spongycastle.asn1.bc.EncryptedSecretKeyData;
import org.spongycastle.asn1.bc.ObjectData;
import org.spongycastle.asn1.bc.ObjectDataSequence;
import org.spongycastle.asn1.bc.ObjectStore;
import org.spongycastle.asn1.bc.ObjectStoreData;
import org.spongycastle.asn1.bc.ObjectStoreIntegrityCheck;
import org.spongycastle.asn1.bc.PbkdMacIntegrityCheck;
import org.spongycastle.asn1.bc.SecretKeyData;
import org.spongycastle.asn1.cms.CCMParameters;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.spongycastle.asn1.pkcs.EncryptionScheme;
import org.spongycastle.asn1.pkcs.KeyDerivationFunc;
import org.spongycastle.asn1.pkcs.PBES2Parameters;
import org.spongycastle.asn1.pkcs.PBKDF2Params;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x9.X9ObjectIdentifiers;
import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.SHA512Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/keystore/bcfks/BcFKSKeyStoreSpi.class */
class BcFKSKeyStoreSpi extends KeyStoreSpi {
    private static final Map<String, ASN1ObjectIdentifier> oidMap = new HashMap();
    private static final Map<ASN1ObjectIdentifier, String> publicAlgMap = new HashMap();
    private static final BigInteger CERTIFICATE;
    private static final BigInteger PRIVATE_KEY;
    private static final BigInteger SECRET_KEY;
    private static final BigInteger PROTECTED_PRIVATE_KEY;
    private static final BigInteger PROTECTED_SECRET_KEY;
    private final BouncyCastleProvider provider;
    private final Map<String, ObjectData> entries = new HashMap();
    private final Map<String, PrivateKey> privateKeyCache = new HashMap();
    private AlgorithmIdentifier hmacAlgorithm;
    private KeyDerivationFunc hmacPkbdAlgorithm;
    private Date creationDate;
    private Date lastModifiedDate;

    static {
        oidMap.put("DESEDE", OIWObjectIdentifiers.desEDE);
        oidMap.put("TRIPLEDES", OIWObjectIdentifiers.desEDE);
        oidMap.put("TDEA", OIWObjectIdentifiers.desEDE);
        oidMap.put("HMACSHA1", PKCSObjectIdentifiers.id_hmacWithSHA1);
        oidMap.put("HMACSHA224", PKCSObjectIdentifiers.id_hmacWithSHA224);
        oidMap.put("HMACSHA256", PKCSObjectIdentifiers.id_hmacWithSHA256);
        oidMap.put("HMACSHA384", PKCSObjectIdentifiers.id_hmacWithSHA384);
        oidMap.put("HMACSHA512", PKCSObjectIdentifiers.id_hmacWithSHA512);
        publicAlgMap.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
        publicAlgMap.put(X9ObjectIdentifiers.id_ecPublicKey, "EC");
        publicAlgMap.put(OIWObjectIdentifiers.elGamalAlgorithm, "DH");
        publicAlgMap.put(PKCSObjectIdentifiers.dhKeyAgreement, "DH");
        publicAlgMap.put(X9ObjectIdentifiers.id_dsa, "DSA");
        CERTIFICATE = BigInteger.valueOf(0L);
        PRIVATE_KEY = BigInteger.valueOf(1L);
        SECRET_KEY = BigInteger.valueOf(2L);
        PROTECTED_PRIVATE_KEY = BigInteger.valueOf(3L);
        PROTECTED_SECRET_KEY = BigInteger.valueOf(4L);
    }

    private static String getPublicKeyAlg(ASN1ObjectIdentifier oid) {
        String algName = publicAlgMap.get(oid);
        if (algName != null) {
            return algName;
        }
        return oid.getId();
    }

    BcFKSKeyStoreSpi(BouncyCastleProvider provider) {
        this.provider = provider;
    }

    @Override // java.security.KeyStoreSpi
    public Key engineGetKey(String alias, char[] password) throws UnrecoverableKeyException, NoSuchAlgorithmException {
        KeyFactory kFact;
        SecretKeyFactory kFact2;
        ObjectData ent = this.entries.get(alias);
        if (ent != null) {
            if (ent.getType().equals(PRIVATE_KEY) || ent.getType().equals(PROTECTED_PRIVATE_KEY)) {
                PrivateKey cachedKey = this.privateKeyCache.get(alias);
                if (cachedKey != null) {
                    return cachedKey;
                }
                EncryptedPrivateKeyData encPrivData = EncryptedPrivateKeyData.getInstance(ent.getData());
                EncryptedPrivateKeyInfo encInfo = EncryptedPrivateKeyInfo.getInstance(encPrivData.getEncryptedPrivateKeyInfo());
                try {
                    PrivateKeyInfo pInfo = PrivateKeyInfo.getInstance(decryptData("PRIVATE_KEY_ENCRYPTION", encInfo.getEncryptionAlgorithm(), password, encInfo.getEncryptedData()));
                    if (this.provider != null) {
                        kFact = KeyFactory.getInstance(pInfo.getPrivateKeyAlgorithm().getAlgorithm().getId(), this.provider);
                    } else {
                        kFact = KeyFactory.getInstance(getPublicKeyAlg(pInfo.getPrivateKeyAlgorithm().getAlgorithm()));
                    }
                    PrivateKey privateKey = kFact.generatePrivate(new PKCS8EncodedKeySpec(pInfo.getEncoded()));
                    this.privateKeyCache.put(alias, privateKey);
                    return privateKey;
                } catch (Exception e) {
                    throw new UnrecoverableKeyException("BCFKS KeyStore unable to recover private key (" + alias + "): " + e.getMessage());
                }
            }
            if (ent.getType().equals(SECRET_KEY) || ent.getType().equals(PROTECTED_SECRET_KEY)) {
                EncryptedSecretKeyData encKeyData = EncryptedSecretKeyData.getInstance(ent.getData());
                try {
                    SecretKeyData keyData = SecretKeyData.getInstance(decryptData("SECRET_KEY_ENCRYPTION", encKeyData.getKeyEncryptionAlgorithm(), password, encKeyData.getEncryptedKeyData()));
                    if (this.provider != null) {
                        kFact2 = SecretKeyFactory.getInstance(keyData.getKeyAlgorithm().getId(), this.provider);
                    } else {
                        kFact2 = SecretKeyFactory.getInstance(keyData.getKeyAlgorithm().getId());
                    }
                    return kFact2.generateSecret(new SecretKeySpec(keyData.getKeyBytes(), keyData.getKeyAlgorithm().getId()));
                } catch (Exception e2) {
                    throw new UnrecoverableKeyException("BCFKS KeyStore unable to recover secret key (" + alias + "): " + e2.getMessage());
                }
            }
            throw new UnrecoverableKeyException("BCFKS KeyStore unable to recover secret key (" + alias + "): type not recognized");
        }
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public Certificate[] engineGetCertificateChain(String alias) {
        ObjectData ent = this.entries.get(alias);
        if (ent != null) {
            if (ent.getType().equals(PRIVATE_KEY) || ent.getType().equals(PROTECTED_PRIVATE_KEY)) {
                EncryptedPrivateKeyData encPrivData = EncryptedPrivateKeyData.getInstance(ent.getData());
                org.spongycastle.asn1.x509.Certificate[] certificates = encPrivData.getCertificateChain();
                Certificate[] chain = new X509Certificate[certificates.length];
                for (int i = 0; i != chain.length; i++) {
                    chain[i] = decodeCertificate(certificates[i]);
                }
                return chain;
            }
            return null;
        }
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public Certificate engineGetCertificate(String s) {
        ObjectData ent = this.entries.get(s);
        if (ent != null) {
            if (ent.getType().equals(PRIVATE_KEY) || ent.getType().equals(PROTECTED_PRIVATE_KEY)) {
                EncryptedPrivateKeyData encPrivData = EncryptedPrivateKeyData.getInstance(ent.getData());
                org.spongycastle.asn1.x509.Certificate[] certificates = encPrivData.getCertificateChain();
                return decodeCertificate(certificates[0]);
            }
            if (ent.getType().equals(CERTIFICATE)) {
                return decodeCertificate(ent.getData());
            }
            return null;
        }
        return null;
    }

    private Certificate decodeCertificate(Object cert) {
        if (this.provider != null) {
            try {
                CertificateFactory certFact = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID, this.provider);
                return certFact.generateCertificate(new ByteArrayInputStream(org.spongycastle.asn1.x509.Certificate.getInstance(cert).getEncoded()));
            } catch (Exception e) {
                return null;
            }
        }
        try {
            CertificateFactory certFact2 = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID);
            return certFact2.generateCertificate(new ByteArrayInputStream(org.spongycastle.asn1.x509.Certificate.getInstance(cert).getEncoded()));
        } catch (Exception e2) {
            return null;
        }
    }

    @Override // java.security.KeyStoreSpi
    public Date engineGetCreationDate(String s) {
        ObjectData ent = this.entries.get(s);
        if (ent != null) {
            try {
                return ent.getLastModifiedDate().getDate();
            } catch (ParseException e) {
                return new Date();
            }
        }
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
        Cipher c;
        byte[] encryptedKey;
        Cipher c2;
        Date creationDate = new Date();
        ObjectData entry = this.entries.get(alias);
        if (entry != null) {
            creationDate = extractCreationDate(entry, creationDate);
        }
        this.privateKeyCache.remove(alias);
        if (key instanceof PrivateKey) {
            if (chain == null) {
                throw new KeyStoreException("BCFKS KeyStore requires a certificate chain for private key storage.");
            }
            try {
                byte[] encodedKey = key.getEncoded();
                KeyDerivationFunc pbkdAlgId = generatePkbdAlgorithmIdentifier(32);
                byte[] keyBytes = generateKey(pbkdAlgId, "PRIVATE_KEY_ENCRYPTION", password != null ? password : new char[0]);
                if (this.provider == null) {
                    c2 = Cipher.getInstance("AES/CCM/NoPadding");
                } else {
                    c2 = Cipher.getInstance("AES/CCM/NoPadding", this.provider);
                }
                c2.init(1, new SecretKeySpec(keyBytes, "AES"));
                byte[] encryptedKey2 = c2.doFinal(encodedKey);
                AlgorithmParameters algParams = c2.getParameters();
                PBES2Parameters pbeParams = new PBES2Parameters(pbkdAlgId, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, CCMParameters.getInstance(algParams.getEncoded())));
                EncryptedPrivateKeyInfo keyInfo = new EncryptedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, pbeParams), encryptedKey2);
                EncryptedPrivateKeyData keySeq = createPrivateKeySequence(keyInfo, chain);
                this.entries.put(alias, new ObjectData(PRIVATE_KEY, alias, creationDate, creationDate, keySeq.getEncoded(), null));
            } catch (Exception e) {
                throw new ExtKeyStoreException("BCFKS KeyStore exception storing private key: " + e.toString(), e);
            }
        } else if (key instanceof SecretKey) {
            if (chain != null) {
                throw new KeyStoreException("BCFKS KeyStore cannot store certificate chain with secret key.");
            }
            try {
                byte[] encodedKey2 = key.getEncoded();
                KeyDerivationFunc pbkdAlgId2 = generatePkbdAlgorithmIdentifier(32);
                byte[] keyBytes2 = generateKey(pbkdAlgId2, "SECRET_KEY_ENCRYPTION", password != null ? password : new char[0]);
                if (this.provider == null) {
                    c = Cipher.getInstance("AES/CCM/NoPadding");
                } else {
                    c = Cipher.getInstance("AES/CCM/NoPadding", this.provider);
                }
                c.init(1, new SecretKeySpec(keyBytes2, "AES"));
                String keyAlg = Strings.toUpperCase(key.getAlgorithm());
                if (keyAlg.indexOf("AES") > -1) {
                    encryptedKey = c.doFinal(new SecretKeyData(NISTObjectIdentifiers.aes, encodedKey2).getEncoded());
                } else {
                    ASN1ObjectIdentifier algOid = oidMap.get(keyAlg);
                    if (algOid != null) {
                        encryptedKey = c.doFinal(new SecretKeyData(algOid, encodedKey2).getEncoded());
                    } else {
                        throw new KeyStoreException("BCFKS KeyStore cannot recognize secret key (" + keyAlg + ") for storage.");
                    }
                }
                AlgorithmParameters algParams2 = c.getParameters();
                PBES2Parameters pbeParams2 = new PBES2Parameters(pbkdAlgId2, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, CCMParameters.getInstance(algParams2.getEncoded())));
                EncryptedSecretKeyData keyData = new EncryptedSecretKeyData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, pbeParams2), encryptedKey);
                this.entries.put(alias, new ObjectData(SECRET_KEY, alias, creationDate, creationDate, keyData.getEncoded(), null));
            } catch (Exception e2) {
                throw new ExtKeyStoreException("BCFKS KeyStore exception storing private key: " + e2.toString(), e2);
            }
        } else {
            throw new KeyStoreException("BCFKS KeyStore unable to recognize key.");
        }
        this.lastModifiedDate = creationDate;
    }

    private SecureRandom getDefaultSecureRandom() {
        return new SecureRandom();
    }

    private EncryptedPrivateKeyData createPrivateKeySequence(EncryptedPrivateKeyInfo encryptedPrivateKeyInfo, Certificate[] chain) throws CertificateEncodingException {
        org.spongycastle.asn1.x509.Certificate[] certChain = new org.spongycastle.asn1.x509.Certificate[chain.length];
        for (int i = 0; i != chain.length; i++) {
            certChain[i] = org.spongycastle.asn1.x509.Certificate.getInstance(chain[i].getEncoded());
        }
        return new EncryptedPrivateKeyData(encryptedPrivateKeyInfo, certChain);
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String alias, byte[] keyBytes, Certificate[] chain) throws KeyStoreException {
        Date creationDate = new Date();
        ObjectData entry = this.entries.get(alias);
        if (entry != null) {
            creationDate = extractCreationDate(entry, creationDate);
        }
        if (chain == null) {
            try {
                this.entries.put(alias, new ObjectData(PROTECTED_SECRET_KEY, alias, creationDate, creationDate, keyBytes, null));
            } catch (Exception e) {
                throw new ExtKeyStoreException("BCFKS KeyStore exception storing protected private key: " + e.toString(), e);
            }
        } else {
            try {
                EncryptedPrivateKeyInfo encInfo = EncryptedPrivateKeyInfo.getInstance(keyBytes);
                try {
                    this.privateKeyCache.remove(alias);
                    this.entries.put(alias, new ObjectData(PROTECTED_PRIVATE_KEY, alias, creationDate, creationDate, createPrivateKeySequence(encInfo, chain).getEncoded(), null));
                } catch (Exception e2) {
                    throw new ExtKeyStoreException("BCFKS KeyStore exception storing protected private key: " + e2.toString(), e2);
                }
            } catch (Exception e3) {
                throw new ExtKeyStoreException("BCFKS KeyStore private key encoding must be an EncryptedPrivateKeyInfo.", e3);
            }
        }
        this.lastModifiedDate = creationDate;
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetCertificateEntry(String alias, Certificate certificate) throws KeyStoreException {
        ObjectData entry = this.entries.get(alias);
        Date creationDate = new Date();
        if (entry != null) {
            if (!entry.getType().equals(CERTIFICATE)) {
                throw new KeyStoreException("BCFKS KeyStore already has a key entry with alias " + alias);
            }
            creationDate = extractCreationDate(entry, creationDate);
        }
        try {
            this.entries.put(alias, new ObjectData(CERTIFICATE, alias, creationDate, creationDate, certificate.getEncoded(), null));
            this.lastModifiedDate = creationDate;
        } catch (CertificateEncodingException e) {
            throw new ExtKeyStoreException("BCFKS KeyStore unable to handle certificate: " + e.getMessage(), e);
        }
    }

    private Date extractCreationDate(ObjectData entry, Date creationDate) {
        try {
            creationDate = entry.getCreationDate().getDate();
        } catch (ParseException e) {
        }
        return creationDate;
    }

    @Override // java.security.KeyStoreSpi
    public void engineDeleteEntry(String alias) throws KeyStoreException {
        ObjectData entry = this.entries.get(alias);
        if (entry == null) {
            return;
        }
        this.privateKeyCache.remove(alias);
        this.entries.remove(alias);
        this.lastModifiedDate = new Date();
    }

    @Override // java.security.KeyStoreSpi
    public Enumeration<String> engineAliases() {
        final Iterator<String> it = new HashSet(this.entries.keySet()).iterator();
        return new Enumeration() { // from class: org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi.1
            @Override // java.util.Enumeration
            public boolean hasMoreElements() {
                return it.hasNext();
            }

            @Override // java.util.Enumeration
            public Object nextElement() {
                return it.next();
            }
        };
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineContainsAlias(String alias) {
        if (alias == null) {
            throw new NullPointerException("alias value is null");
        }
        return this.entries.containsKey(alias);
    }

    @Override // java.security.KeyStoreSpi
    public int engineSize() {
        return this.entries.size();
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsKeyEntry(String alias) {
        ObjectData ent = this.entries.get(alias);
        if (ent != null) {
            BigInteger entryType = ent.getType();
            return entryType.equals(PRIVATE_KEY) || entryType.equals(SECRET_KEY) || entryType.equals(PROTECTED_PRIVATE_KEY) || entryType.equals(PROTECTED_SECRET_KEY);
        }
        return false;
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsCertificateEntry(String alias) {
        ObjectData ent = this.entries.get(alias);
        if (ent != null) {
            return ent.getType().equals(CERTIFICATE);
        }
        return false;
    }

    @Override // java.security.KeyStoreSpi
    public String engineGetCertificateAlias(Certificate certificate) {
        if (certificate == null) {
            return null;
        }
        try {
            byte[] encodedCert = certificate.getEncoded();
            for (String alias : this.entries.keySet()) {
                ObjectData ent = this.entries.get(alias);
                if (ent.getType().equals(CERTIFICATE)) {
                    if (Arrays.areEqual(ent.getData(), encodedCert)) {
                        return alias;
                    }
                } else if (ent.getType().equals(PRIVATE_KEY) || ent.getType().equals(PROTECTED_PRIVATE_KEY)) {
                    try {
                        EncryptedPrivateKeyData encPrivData = EncryptedPrivateKeyData.getInstance(ent.getData());
                        if (Arrays.areEqual(encPrivData.getCertificateChain()[0].toASN1Primitive().getEncoded(), encodedCert)) {
                            return alias;
                        }
                    } catch (IOException e) {
                    }
                }
            }
            return null;
        } catch (CertificateEncodingException e2) {
            return null;
        }
    }

    private byte[] generateKey(KeyDerivationFunc pbkdAlgorithm, String purpose, char[] password) throws IOException {
        byte[] encPassword = PBEParametersGenerator.PKCS12PasswordToBytes(password);
        byte[] differentiator = PBEParametersGenerator.PKCS12PasswordToBytes(purpose.toCharArray());
        PKCS5S2ParametersGenerator pGen = new PKCS5S2ParametersGenerator(new SHA512Digest());
        if (pbkdAlgorithm.getAlgorithm().equals(PKCSObjectIdentifiers.id_PBKDF2)) {
            PBKDF2Params pbkdf2Params = PBKDF2Params.getInstance(pbkdAlgorithm.getParameters());
            if (pbkdf2Params.getPrf().getAlgorithm().equals(PKCSObjectIdentifiers.id_hmacWithSHA512)) {
                pGen.init(Arrays.concatenate(encPassword, differentiator), pbkdf2Params.getSalt(), pbkdf2Params.getIterationCount().intValue());
                int keySizeInBytes = pbkdf2Params.getKeyLength().intValue();
                return ((KeyParameter) pGen.generateDerivedParameters(keySizeInBytes * 8)).getKey();
            }
            throw new IOException("BCFKS KeyStore: unrecognized MAC PBKD PRF.");
        }
        throw new IOException("BCFKS KeyStore: unrecognized MAC PBKD.");
    }

    private void verifyMac(byte[] content, PbkdMacIntegrityCheck integrityCheck, char[] password) throws NoSuchAlgorithmException, IOException {
        byte[] check = calculateMac(content, integrityCheck.getMacAlgorithm(), integrityCheck.getPbkdAlgorithm(), password);
        if (!Arrays.constantTimeAreEqual(check, integrityCheck.getMac())) {
            throw new IOException("BCFKS KeyStore corrupted: MAC calculation failed.");
        }
    }

    private byte[] calculateMac(byte[] content, AlgorithmIdentifier algorithm, KeyDerivationFunc pbkdAlgorithm, char[] password) throws NoSuchAlgorithmException, IOException {
        Mac mac;
        String algorithmId = algorithm.getAlgorithm().getId();
        if (this.provider != null) {
            mac = Mac.getInstance(algorithmId, this.provider);
        } else {
            mac = Mac.getInstance(algorithmId);
        }
        try {
            mac.init(new SecretKeySpec(generateKey(pbkdAlgorithm, "INTEGRITY_CHECK", password != null ? password : new char[0]), algorithmId));
            return mac.doFinal(content);
        } catch (InvalidKeyException e) {
            throw new IOException("Cannot set up MAC calculation: " + e.getMessage());
        }
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(OutputStream outputStream, char[] password) throws NoSuchAlgorithmException, IOException, CertificateException {
        Cipher c;
        ObjectData[] dataArray = (ObjectData[]) this.entries.values().toArray(new ObjectData[this.entries.size()]);
        KeyDerivationFunc pbkdAlgId = generatePkbdAlgorithmIdentifier(32);
        byte[] keyBytes = generateKey(pbkdAlgId, "STORE_ENCRYPTION", password != null ? password : new char[0]);
        ObjectStoreData storeData = new ObjectStoreData(this.hmacAlgorithm, this.creationDate, this.lastModifiedDate, new ObjectDataSequence(dataArray), null);
        try {
            if (this.provider == null) {
                c = Cipher.getInstance("AES/CCM/NoPadding");
            } else {
                c = Cipher.getInstance("AES/CCM/NoPadding", this.provider);
            }
            c.init(1, new SecretKeySpec(keyBytes, "AES"));
            byte[] encOut = c.doFinal(storeData.getEncoded());
            AlgorithmParameters algorithmParameters = c.getParameters();
            PBES2Parameters pbeParams = new PBES2Parameters(pbkdAlgId, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, CCMParameters.getInstance(algorithmParameters.getEncoded())));
            EncryptedObjectStoreData encStoreData = new EncryptedObjectStoreData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, pbeParams), encOut);
            PBKDF2Params pbkdf2Params = PBKDF2Params.getInstance(this.hmacPkbdAlgorithm.getParameters());
            byte[] pbkdSalt = new byte[pbkdf2Params.getSalt().length];
            getDefaultSecureRandom().nextBytes(pbkdSalt);
            this.hmacPkbdAlgorithm = new KeyDerivationFunc(this.hmacPkbdAlgorithm.getAlgorithm(), new PBKDF2Params(pbkdSalt, pbkdf2Params.getIterationCount().intValue(), pbkdf2Params.getKeyLength().intValue(), pbkdf2Params.getPrf()));
            byte[] mac = calculateMac(encStoreData.getEncoded(), this.hmacAlgorithm, this.hmacPkbdAlgorithm, password);
            ObjectStore store = new ObjectStore(encStoreData, new ObjectStoreIntegrityCheck(new PbkdMacIntegrityCheck(this.hmacAlgorithm, this.hmacPkbdAlgorithm, mac)));
            outputStream.write(store.getEncoded());
            outputStream.flush();
        } catch (InvalidKeyException e) {
            throw new IOException(e.toString());
        } catch (BadPaddingException e2) {
            throw new IOException(e2.toString());
        } catch (IllegalBlockSizeException e3) {
            throw new IOException(e3.toString());
        } catch (NoSuchPaddingException e4) {
            throw new NoSuchAlgorithmException(e4.toString());
        }
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(InputStream inputStream, char[] password) throws NoSuchAlgorithmException, IOException, CertificateException {
        ObjectStoreData storeData;
        this.entries.clear();
        this.privateKeyCache.clear();
        this.creationDate = null;
        this.lastModifiedDate = null;
        this.hmacAlgorithm = null;
        if (inputStream == null) {
            Date date = new Date();
            this.creationDate = date;
            this.lastModifiedDate = date;
            this.hmacAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE);
            this.hmacPkbdAlgorithm = generatePkbdAlgorithmIdentifier(64);
            return;
        }
        ASN1InputStream aIn = new ASN1InputStream(inputStream);
        ObjectStore store = ObjectStore.getInstance(aIn.readObject());
        ObjectStoreIntegrityCheck integrityCheck = store.getIntegrityCheck();
        if (integrityCheck.getType() == 0) {
            PbkdMacIntegrityCheck pbkdMacIntegrityCheck = PbkdMacIntegrityCheck.getInstance(integrityCheck.getIntegrityCheck());
            this.hmacAlgorithm = pbkdMacIntegrityCheck.getMacAlgorithm();
            this.hmacPkbdAlgorithm = pbkdMacIntegrityCheck.getPbkdAlgorithm();
            verifyMac(store.getStoreData().toASN1Primitive().getEncoded(), pbkdMacIntegrityCheck, password);
            ASN1Encodable sData = store.getStoreData();
            if (sData instanceof EncryptedObjectStoreData) {
                EncryptedObjectStoreData encryptedStoreData = (EncryptedObjectStoreData) sData;
                AlgorithmIdentifier protectAlgId = encryptedStoreData.getEncryptionAlgorithm();
                storeData = ObjectStoreData.getInstance(decryptData("STORE_ENCRYPTION", protectAlgId, password, encryptedStoreData.getEncryptedContent().getOctets()));
            } else {
                storeData = ObjectStoreData.getInstance(sData);
            }
            try {
                this.creationDate = storeData.getCreationDate().getDate();
                this.lastModifiedDate = storeData.getLastModifiedDate().getDate();
                if (!storeData.getIntegrityAlgorithm().equals(this.hmacAlgorithm)) {
                    throw new IOException("BCFKS KeyStore storeData integrity algorithm does not match store integrity algorithm.");
                }
                Iterator<ASN1Encodable> it = storeData.getObjectDataSequence().iterator();
                while (it.hasNext()) {
                    ObjectData objData = ObjectData.getInstance(it.next());
                    this.entries.put(objData.getIdentifier(), objData);
                }
                return;
            } catch (ParseException e) {
                throw new IOException("BCFKS KeyStore unable to parse store data information.");
            }
        }
        throw new IOException("BCFKS KeyStore unable to recognize integrity check.");
    }

    private byte[] decryptData(String purpose, AlgorithmIdentifier protectAlgId, char[] password, byte[] encryptedData) throws IOException {
        Cipher c;
        AlgorithmParameters algParams;
        if (!protectAlgId.getAlgorithm().equals(PKCSObjectIdentifiers.id_PBES2)) {
            throw new IOException("BCFKS KeyStore cannot recognize protection algorithm.");
        }
        PBES2Parameters pbes2Parameters = PBES2Parameters.getInstance(protectAlgId.getParameters());
        EncryptionScheme algId = pbes2Parameters.getEncryptionScheme();
        if (!algId.getAlgorithm().equals(NISTObjectIdentifiers.id_aes256_CCM)) {
            throw new IOException("BCFKS KeyStore cannot recognize protection encryption algorithm.");
        }
        try {
            CCMParameters ccmParameters = CCMParameters.getInstance(algId.getParameters());
            if (this.provider == null) {
                c = Cipher.getInstance("AES/CCM/NoPadding");
                algParams = AlgorithmParameters.getInstance("CCM");
            } else {
                c = Cipher.getInstance("AES/CCM/NoPadding", this.provider);
                algParams = AlgorithmParameters.getInstance("CCM", this.provider);
            }
            algParams.init(ccmParameters.getEncoded());
            byte[] keyBytes = generateKey(pbes2Parameters.getKeyDerivationFunc(), purpose, password != null ? password : new char[0]);
            c.init(2, new SecretKeySpec(keyBytes, "AES"), algParams);
            byte[] rv = c.doFinal(encryptedData);
            return rv;
        } catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    private KeyDerivationFunc generatePkbdAlgorithmIdentifier(int keySizeInBytes) {
        byte[] pbkdSalt = new byte[64];
        getDefaultSecureRandom().nextBytes(pbkdSalt);
        return new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(pbkdSalt, 1024, keySizeInBytes, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE)));
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/keystore/bcfks/BcFKSKeyStoreSpi$Std.class */
    public static class Std extends BcFKSKeyStoreSpi {
        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineLoad(InputStream inputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineLoad(inputStream, cArr);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineStore(OutputStream outputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineStore(outputStream, cArr);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ String engineGetCertificateAlias(Certificate certificate) {
            return super.engineGetCertificateAlias(certificate);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ boolean engineIsCertificateEntry(String str) {
            return super.engineIsCertificateEntry(str);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ boolean engineIsKeyEntry(String str) {
            return super.engineIsKeyEntry(str);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ int engineSize() {
            return super.engineSize();
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ boolean engineContainsAlias(String str) {
            return super.engineContainsAlias(str);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Enumeration engineAliases() {
            return super.engineAliases();
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineDeleteEntry(String str) throws KeyStoreException {
            super.engineDeleteEntry(str);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
            super.engineSetCertificateEntry(str, certificate);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
            super.engineSetKeyEntry(str, bArr, certificateArr);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
            super.engineSetKeyEntry(str, key, cArr, certificateArr);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Date engineGetCreationDate(String str) {
            return super.engineGetCreationDate(str);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Certificate engineGetCertificate(String str) {
            return super.engineGetCertificate(str);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Certificate[] engineGetCertificateChain(String str) {
            return super.engineGetCertificateChain(str);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException {
            return super.engineGetKey(str, cArr);
        }

        public Std() {
            super(new BouncyCastleProvider());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/keystore/bcfks/BcFKSKeyStoreSpi$Def.class */
    public static class Def extends BcFKSKeyStoreSpi {
        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineLoad(InputStream inputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineLoad(inputStream, cArr);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineStore(OutputStream outputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineStore(outputStream, cArr);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ String engineGetCertificateAlias(Certificate certificate) {
            return super.engineGetCertificateAlias(certificate);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ boolean engineIsCertificateEntry(String str) {
            return super.engineIsCertificateEntry(str);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ boolean engineIsKeyEntry(String str) {
            return super.engineIsKeyEntry(str);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ int engineSize() {
            return super.engineSize();
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ boolean engineContainsAlias(String str) {
            return super.engineContainsAlias(str);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Enumeration engineAliases() {
            return super.engineAliases();
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineDeleteEntry(String str) throws KeyStoreException {
            super.engineDeleteEntry(str);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
            super.engineSetCertificateEntry(str, certificate);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
            super.engineSetKeyEntry(str, bArr, certificateArr);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
            super.engineSetKeyEntry(str, key, cArr, certificateArr);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Date engineGetCreationDate(String str) {
            return super.engineGetCreationDate(str);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Certificate engineGetCertificate(String str) {
            return super.engineGetCertificate(str);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Certificate[] engineGetCertificateChain(String str) {
            return super.engineGetCertificateChain(str);
        }

        @Override // org.spongycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException {
            return super.engineGetKey(str, cArr);
        }

        public Def() {
            super(null);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/keystore/bcfks/BcFKSKeyStoreSpi$ExtKeyStoreException.class */
    private static class ExtKeyStoreException extends KeyStoreException {
        private final Throwable cause;

        ExtKeyStoreException(String msg, Throwable cause) {
            super(msg);
            this.cause = cause;
        }

        @Override // java.lang.Throwable
        public Throwable getCause() {
            return this.cause;
        }
    }
}
