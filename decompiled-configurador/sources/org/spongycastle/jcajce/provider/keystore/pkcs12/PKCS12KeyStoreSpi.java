package org.spongycastle.jcajce.provider.keystore.pkcs12;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.BEROctetString;
import org.spongycastle.asn1.BEROutputStream;
import org.spongycastle.asn1.DERBMPString;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DEROutputStream;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.asn1.cryptopro.GOST28147Parameters;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.ntt.NTTObjectIdentifiers;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.pkcs.AuthenticatedSafe;
import org.spongycastle.asn1.pkcs.CertBag;
import org.spongycastle.asn1.pkcs.ContentInfo;
import org.spongycastle.asn1.pkcs.EncryptedData;
import org.spongycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.spongycastle.asn1.pkcs.MacData;
import org.spongycastle.asn1.pkcs.PBES2Parameters;
import org.spongycastle.asn1.pkcs.PBKDF2Params;
import org.spongycastle.asn1.pkcs.PKCS12PBEParams;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.pkcs.Pfx;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.pkcs.SafeBag;
import org.spongycastle.asn1.util.ASN1Dump;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.AuthorityKeyIdentifier;
import org.spongycastle.asn1.x509.DigestInfo;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.SubjectKeyIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x509.X509ObjectIdentifiers;
import org.spongycastle.cms.CMSEnvelopedGenerator;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.util.DigestFactory;
import org.spongycastle.jcajce.PKCS12Key;
import org.spongycastle.jcajce.PKCS12StoreParameter;
import org.spongycastle.jcajce.spec.GOST28147ParameterSpec;
import org.spongycastle.jcajce.spec.PBKDF2KeySpec;
import org.spongycastle.jcajce.util.BCJcaJceHelper;
import org.spongycastle.jcajce.util.JcaJceHelper;
import org.spongycastle.jce.interfaces.BCKeyStore;
import org.spongycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.provider.JDKPKCS12StoreParameter;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Integers;
import org.spongycastle.util.Properties;
import org.spongycastle.util.Strings;
import org.spongycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi.class */
public class PKCS12KeyStoreSpi extends KeyStoreSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers, BCKeyStore {
    static final String PKCS12_MAX_IT_COUNT_PROPERTY = "org.spongycastle.pkcs12.max_it_count";
    private static final int SALT_SIZE = 20;
    private static final int MIN_ITERATIONS = 1024;
    private static final DefaultSecretKeyProvider keySizeProvider = new DefaultSecretKeyProvider();
    static final int NULL = 0;
    static final int CERTIFICATE = 1;
    static final int KEY = 2;
    static final int SECRET = 3;
    static final int SEALED = 4;
    static final int KEY_PRIVATE = 0;
    static final int KEY_PUBLIC = 1;
    static final int KEY_SECRET = 2;
    private CertificateFactory certFact;
    private ASN1ObjectIdentifier keyAlgorithm;
    private ASN1ObjectIdentifier certAlgorithm;
    private final JcaJceHelper helper = new BCJcaJceHelper();
    private IgnoresCaseHashtable keys = new IgnoresCaseHashtable();
    private Hashtable localIds = new Hashtable();
    private IgnoresCaseHashtable certs = new IgnoresCaseHashtable();
    private Hashtable chainCerts = new Hashtable();
    private Hashtable keyCerts = new Hashtable();
    protected SecureRandom random = new SecureRandom();
    private AlgorithmIdentifier macAlgorithm = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
    private int itCount = 1024;
    private int saltLength = 20;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi$CertId.class */
    private class CertId {
        byte[] id;

        CertId(PublicKey key) {
            this.id = PKCS12KeyStoreSpi.this.createSubjectKeyId(key).getKeyIdentifier();
        }

        CertId(byte[] id) {
            this.id = id;
        }

        public int hashCode() {
            return Arrays.hashCode(this.id);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof CertId)) {
                return false;
            }
            CertId cId = (CertId) o;
            return Arrays.areEqual(this.id, cId.id);
        }
    }

    public PKCS12KeyStoreSpi(Provider provider, ASN1ObjectIdentifier keyAlgorithm, ASN1ObjectIdentifier certAlgorithm) {
        this.keyAlgorithm = keyAlgorithm;
        this.certAlgorithm = certAlgorithm;
        try {
            if (provider != null) {
                this.certFact = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID, provider);
            } else {
                this.certFact = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("can't create cert factory - " + e.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SubjectKeyIdentifier createSubjectKeyId(PublicKey pubKey) {
        try {
            SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(pubKey.getEncoded());
            return new SubjectKeyIdentifier(getDigest(info));
        } catch (Exception e) {
            throw new RuntimeException("error creating key");
        }
    }

    private static byte[] getDigest(SubjectPublicKeyInfo spki) {
        Digest digest = DigestFactory.createSHA1();
        byte[] resBuf = new byte[digest.getDigestSize()];
        byte[] bytes = spki.getPublicKeyData().getBytes();
        digest.update(bytes, 0, bytes.length);
        digest.doFinal(resBuf, 0);
        return resBuf;
    }

    @Override // org.spongycastle.jce.interfaces.BCKeyStore
    public void setRandom(SecureRandom rand) {
        this.random = rand;
    }

    @Override // java.security.KeyStoreSpi
    public Enumeration engineAliases() {
        Hashtable tab = new Hashtable();
        Enumeration e = this.certs.keys();
        while (e.hasMoreElements()) {
            tab.put(e.nextElement(), "cert");
        }
        Enumeration e2 = this.keys.keys();
        while (e2.hasMoreElements()) {
            String a = (String) e2.nextElement();
            if (tab.get(a) == null) {
                tab.put(a, "key");
            }
        }
        return tab.keys();
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineContainsAlias(String alias) {
        return (this.certs.get(alias) == null && this.keys.get(alias) == null) ? false : true;
    }

    @Override // java.security.KeyStoreSpi
    public void engineDeleteEntry(String alias) throws KeyStoreException {
        Key k = (Key) this.keys.remove(alias);
        Certificate c = (Certificate) this.certs.remove(alias);
        if (c != null) {
            this.chainCerts.remove(new CertId(c.getPublicKey()));
        }
        if (k != null) {
            String id = (String) this.localIds.remove(alias);
            if (id != null) {
                c = (Certificate) this.keyCerts.remove(id);
            }
            if (c != null) {
                this.chainCerts.remove(new CertId(c.getPublicKey()));
            }
        }
    }

    @Override // java.security.KeyStoreSpi
    public Certificate engineGetCertificate(String alias) {
        if (alias == null) {
            throw new IllegalArgumentException("null alias passed to getCertificate.");
        }
        Certificate c = (Certificate) this.certs.get(alias);
        if (c == null) {
            String id = (String) this.localIds.get(alias);
            if (id != null) {
                c = (Certificate) this.keyCerts.get(id);
            } else {
                c = (Certificate) this.keyCerts.get(alias);
            }
        }
        return c;
    }

    @Override // java.security.KeyStoreSpi
    public String engineGetCertificateAlias(Certificate cert) {
        Enumeration c = this.certs.elements();
        Enumeration k = this.certs.keys();
        while (c.hasMoreElements()) {
            Certificate tc = (Certificate) c.nextElement();
            String ta = (String) k.nextElement();
            if (tc.equals(cert)) {
                return ta;
            }
        }
        Enumeration c2 = this.keyCerts.elements();
        Enumeration k2 = this.keyCerts.keys();
        while (c2.hasMoreElements()) {
            Certificate tc2 = (Certificate) c2.nextElement();
            String ta2 = (String) k2.nextElement();
            if (tc2.equals(cert)) {
                return ta2;
            }
        }
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public Certificate[] engineGetCertificateChain(String alias) {
        if (alias == null) {
            throw new IllegalArgumentException("null alias passed to getCertificateChain.");
        }
        if (!engineIsKeyEntry(alias)) {
            return null;
        }
        Certificate c = engineGetCertificate(alias);
        if (c != null) {
            Vector cs = new Vector();
            while (c != null) {
                X509Certificate x509c = (X509Certificate) c;
                Certificate nextC = null;
                byte[] bytes = x509c.getExtensionValue(Extension.authorityKeyIdentifier.getId());
                if (bytes != null) {
                    try {
                        ASN1InputStream aIn = new ASN1InputStream(bytes);
                        byte[] authBytes = ((ASN1OctetString) aIn.readObject()).getOctets();
                        ASN1InputStream aIn2 = new ASN1InputStream(authBytes);
                        AuthorityKeyIdentifier id = AuthorityKeyIdentifier.getInstance(aIn2.readObject());
                        if (id.getKeyIdentifier() != null) {
                            nextC = (Certificate) this.chainCerts.get(new CertId(id.getKeyIdentifier()));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e.toString());
                    }
                }
                if (nextC == null) {
                    Principal i = x509c.getIssuerDN();
                    Principal s = x509c.getSubjectDN();
                    if (!i.equals(s)) {
                        Enumeration e2 = this.chainCerts.keys();
                        while (e2.hasMoreElements()) {
                            X509Certificate crt = (X509Certificate) this.chainCerts.get(e2.nextElement());
                            Principal sub = crt.getSubjectDN();
                            if (sub.equals(i)) {
                                try {
                                    x509c.verify(crt.getPublicKey());
                                    nextC = crt;
                                    break;
                                } catch (Exception e3) {
                                }
                            }
                        }
                    }
                }
                if (cs.contains(c)) {
                    c = null;
                } else {
                    cs.addElement(c);
                    if (nextC != c) {
                        c = nextC;
                    } else {
                        c = null;
                    }
                }
            }
            Certificate[] certChain = new Certificate[cs.size()];
            for (int i2 = 0; i2 != certChain.length; i2++) {
                certChain[i2] = (Certificate) cs.elementAt(i2);
            }
            return certChain;
        }
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public Date engineGetCreationDate(String alias) {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        if (this.keys.get(alias) == null && this.certs.get(alias) == null) {
            return null;
        }
        return new Date();
    }

    @Override // java.security.KeyStoreSpi
    public Key engineGetKey(String alias, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        if (alias == null) {
            throw new IllegalArgumentException("null alias passed to getKey.");
        }
        return (Key) this.keys.get(alias);
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsCertificateEntry(String alias) {
        return this.certs.get(alias) != null && this.keys.get(alias) == null;
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsKeyEntry(String alias) {
        return this.keys.get(alias) != null;
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetCertificateEntry(String alias, Certificate cert) throws KeyStoreException {
        if (this.keys.get(alias) != null) {
            throw new KeyStoreException("There is a key entry with the name " + alias + ".");
        }
        this.certs.put(alias, cert);
        this.chainCerts.put(new CertId(cert.getPublicKey()), cert);
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) throws KeyStoreException {
        throw new RuntimeException("operation not supported");
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
        if (!(key instanceof PrivateKey)) {
            throw new KeyStoreException("PKCS12 does not support non-PrivateKeys");
        }
        if ((key instanceof PrivateKey) && chain == null) {
            throw new KeyStoreException("no certificate chain for private key");
        }
        if (this.keys.get(alias) != null) {
            engineDeleteEntry(alias);
        }
        this.keys.put(alias, key);
        if (chain != null) {
            this.certs.put(alias, chain[0]);
            for (int i = 0; i != chain.length; i++) {
                this.chainCerts.put(new CertId(chain[i].getPublicKey()), chain[i]);
            }
        }
    }

    @Override // java.security.KeyStoreSpi
    public int engineSize() {
        Hashtable tab = new Hashtable();
        Enumeration e = this.certs.keys();
        while (e.hasMoreElements()) {
            tab.put(e.nextElement(), "cert");
        }
        Enumeration e2 = this.keys.keys();
        while (e2.hasMoreElements()) {
            String a = (String) e2.nextElement();
            if (tab.get(a) == null) {
                tab.put(a, "key");
            }
        }
        return tab.size();
    }

    protected PrivateKey unwrapKey(AlgorithmIdentifier algId, byte[] data, char[] password, boolean wrongPKCS12Zero) throws IOException {
        ASN1ObjectIdentifier algorithm = algId.getAlgorithm();
        try {
            if (algorithm.on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
                PKCS12PBEParams pbeParams = PKCS12PBEParams.getInstance(algId.getParameters());
                PBEParameterSpec defParams = new PBEParameterSpec(pbeParams.getIV(), validateIterationCount(pbeParams.getIterations()));
                Cipher cipher = this.helper.createCipher(algorithm.getId());
                PKCS12Key key = new PKCS12Key(password, wrongPKCS12Zero);
                cipher.init(4, key, defParams);
                return (PrivateKey) cipher.unwrap(data, "", 2);
            }
            if (algorithm.equals(PKCSObjectIdentifiers.id_PBES2)) {
                return (PrivateKey) createCipher(4, password, algId).unwrap(data, "", 2);
            }
            throw new IOException("exception unwrapping private key - cannot recognise: " + algorithm);
        } catch (Exception e) {
            throw new IOException("exception unwrapping private key - " + e.toString());
        }
    }

    protected byte[] wrapKey(String algorithm, Key key, PKCS12PBEParams pbeParams, char[] password) throws IOException {
        PBEKeySpec pbeSpec = new PBEKeySpec(password);
        try {
            SecretKeyFactory keyFact = this.helper.createSecretKeyFactory(algorithm);
            PBEParameterSpec defParams = new PBEParameterSpec(pbeParams.getIV(), pbeParams.getIterations().intValue());
            Cipher cipher = this.helper.createCipher(algorithm);
            cipher.init(3, keyFact.generateSecret(pbeSpec), defParams);
            byte[] out = cipher.wrap(key);
            return out;
        } catch (Exception e) {
            throw new IOException("exception encrypting data - " + e.toString());
        }
    }

    protected byte[] cryptData(boolean forEncryption, AlgorithmIdentifier algId, char[] password, boolean wrongPKCS12Zero, byte[] data) throws IOException {
        ASN1ObjectIdentifier algorithm = algId.getAlgorithm();
        int mode = forEncryption ? 1 : 2;
        if (algorithm.on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
            PKCS12PBEParams pbeParams = PKCS12PBEParams.getInstance(algId.getParameters());
            try {
                PBEParameterSpec defParams = new PBEParameterSpec(pbeParams.getIV(), pbeParams.getIterations().intValue());
                PKCS12Key key = new PKCS12Key(password, wrongPKCS12Zero);
                Cipher cipher = this.helper.createCipher(algorithm.getId());
                cipher.init(mode, key, defParams);
                return cipher.doFinal(data);
            } catch (Exception e) {
                throw new IOException("exception decrypting data - " + e.toString());
            }
        }
        if (algorithm.equals(PKCSObjectIdentifiers.id_PBES2)) {
            try {
                return createCipher(mode, password, algId).doFinal(data);
            } catch (Exception e2) {
                throw new IOException("exception decrypting data - " + e2.toString());
            }
        }
        throw new IOException("unknown PBE algorithm: " + algorithm);
    }

    private Cipher createCipher(int mode, char[] password, AlgorithmIdentifier algId) throws InvalidKeySpecException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, InvalidAlgorithmParameterException {
        SecretKey key;
        PBES2Parameters alg = PBES2Parameters.getInstance(algId.getParameters());
        PBKDF2Params func = PBKDF2Params.getInstance(alg.getKeyDerivationFunc().getParameters());
        AlgorithmIdentifier encScheme = AlgorithmIdentifier.getInstance(alg.getEncryptionScheme());
        SecretKeyFactory keyFact = this.helper.createSecretKeyFactory(alg.getKeyDerivationFunc().getAlgorithm().getId());
        if (func.isDefaultPrf()) {
            key = keyFact.generateSecret(new PBEKeySpec(password, func.getSalt(), validateIterationCount(func.getIterationCount()), keySizeProvider.getKeySize(encScheme)));
        } else {
            key = keyFact.generateSecret(new PBKDF2KeySpec(password, func.getSalt(), validateIterationCount(func.getIterationCount()), keySizeProvider.getKeySize(encScheme), func.getPrf()));
        }
        Cipher cipher = Cipher.getInstance(alg.getEncryptionScheme().getAlgorithm().getId());
        ASN1Encodable encParams = alg.getEncryptionScheme().getParameters();
        if (encParams instanceof ASN1OctetString) {
            cipher.init(mode, key, new IvParameterSpec(ASN1OctetString.getInstance(encParams).getOctets()));
        } else {
            GOST28147Parameters gParams = GOST28147Parameters.getInstance(encParams);
            cipher.init(mode, key, new GOST28147ParameterSpec(gParams.getEncryptionParamSet(), gParams.getIV()));
        }
        return cipher;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    @Override // java.security.KeyStoreSpi
    public void engineLoad(InputStream stream, char[] password) throws IOException {
        if (stream == null) {
            return;
        }
        if (password == null) {
            throw new NullPointerException("No password supplied for PKCS#12 KeyStore.");
        }
        BufferedInputStream bufIn = new BufferedInputStream(stream);
        bufIn.mark(10);
        int head = bufIn.read();
        if (head != 48) {
            throw new IOException("stream does not represent a PKCS12 key store");
        }
        bufIn.reset();
        ASN1InputStream bIn = new ASN1InputStream(bufIn);
        ASN1Sequence obj = (ASN1Sequence) bIn.readObject();
        Pfx bag = Pfx.getInstance(obj);
        ContentInfo info = bag.getAuthSafe();
        Vector chain = new Vector();
        boolean unmarkedKey = false;
        boolean wrongPKCS12Zero = false;
        if (bag.getMacData() != null) {
            MacData mData = bag.getMacData();
            DigestInfo dInfo = mData.getMac();
            this.macAlgorithm = dInfo.getAlgorithmId();
            byte[] salt = mData.getSalt();
            this.itCount = validateIterationCount(mData.getIterationCount());
            this.saltLength = salt.length;
            byte[] data = ((ASN1OctetString) info.getContent()).getOctets();
            try {
                byte[] res = calculatePbeMac(this.macAlgorithm.getAlgorithm(), salt, this.itCount, password, false, data);
                byte[] dig = dInfo.getDigest();
                if (!Arrays.constantTimeAreEqual(res, dig)) {
                    if (password.length > 0) {
                        throw new IOException("PKCS12 key store mac invalid - wrong password or corrupted file.");
                    }
                    byte[] res2 = calculatePbeMac(this.macAlgorithm.getAlgorithm(), salt, this.itCount, password, true, data);
                    if (!Arrays.constantTimeAreEqual(res2, dig)) {
                        throw new IOException("PKCS12 key store mac invalid - wrong password or corrupted file.");
                    }
                    wrongPKCS12Zero = true;
                }
            } catch (IOException e) {
                throw e;
            } catch (Exception e2) {
                throw new IOException("error constructing MAC: " + e2.toString());
            }
        }
        this.keys = new IgnoresCaseHashtable();
        this.localIds = new Hashtable();
        if (info.getContentType().equals(data)) {
            ASN1InputStream bIn2 = new ASN1InputStream(((ASN1OctetString) info.getContent()).getOctets());
            AuthenticatedSafe authSafe = AuthenticatedSafe.getInstance(bIn2.readObject());
            ContentInfo[] c = authSafe.getContentInfo();
            for (int i = 0; i != c.length; i++) {
                if (c[i].getContentType().equals(data)) {
                    ASN1InputStream dIn = new ASN1InputStream(((ASN1OctetString) c[i].getContent()).getOctets());
                    ASN1Sequence seq = (ASN1Sequence) dIn.readObject();
                    for (int j = 0; j != seq.size(); j++) {
                        SafeBag b = SafeBag.getInstance(seq.getObjectAt(j));
                        if (b.getBagId().equals(pkcs8ShroudedKeyBag)) {
                            EncryptedPrivateKeyInfo eIn = EncryptedPrivateKeyInfo.getInstance(b.getBagValue());
                            PrivateKey privKey = unwrapKey(eIn.getEncryptionAlgorithm(), eIn.getEncryptedData(), password, wrongPKCS12Zero);
                            PKCS12BagAttributeCarrier bagAttr = (PKCS12BagAttributeCarrier) privKey;
                            String alias = null;
                            ASN1OctetString localId = null;
                            if (b.getBagAttributes() != null) {
                                Enumeration e3 = b.getBagAttributes().getObjects();
                                while (e3.hasMoreElements()) {
                                    ASN1Sequence sq = (ASN1Sequence) e3.nextElement();
                                    ASN1ObjectIdentifier aOid = (ASN1ObjectIdentifier) sq.getObjectAt(0);
                                    ASN1Set attrSet = (ASN1Set) sq.getObjectAt(1);
                                    ASN1Primitive attr = null;
                                    if (attrSet.size() > 0) {
                                        attr = (ASN1Primitive) attrSet.getObjectAt(0);
                                        ASN1Encodable existing = bagAttr.getBagAttribute(aOid);
                                        if (existing != null) {
                                            if (!existing.toASN1Primitive().equals(attr)) {
                                                throw new IOException("attempt to add existing attribute with different value");
                                            }
                                        } else {
                                            bagAttr.setBagAttribute(aOid, attr);
                                        }
                                    }
                                    if (aOid.equals(pkcs_9_at_friendlyName)) {
                                        alias = ((DERBMPString) attr).getString();
                                        this.keys.put(alias, privKey);
                                    } else if (aOid.equals(pkcs_9_at_localKeyId)) {
                                        localId = (ASN1OctetString) attr;
                                    }
                                }
                            }
                            if (localId != null) {
                                String name = new String(Hex.encode(localId.getOctets()));
                                if (alias == null) {
                                    this.keys.put(name, privKey);
                                } else {
                                    this.localIds.put(alias, name);
                                }
                            } else {
                                unmarkedKey = true;
                                this.keys.put("unmarked", privKey);
                            }
                        } else if (b.getBagId().equals(certBag)) {
                            chain.addElement(b);
                        } else {
                            System.out.println("extra in data " + b.getBagId());
                            System.out.println(ASN1Dump.dumpAsString(b));
                        }
                    }
                } else if (c[i].getContentType().equals(encryptedData)) {
                    EncryptedData d = EncryptedData.getInstance(c[i].getContent());
                    byte[] octets = cryptData(false, d.getEncryptionAlgorithm(), password, wrongPKCS12Zero, d.getContent().getOctets());
                    ASN1Sequence seq2 = (ASN1Sequence) ASN1Primitive.fromByteArray(octets);
                    for (int j2 = 0; j2 != seq2.size(); j2++) {
                        SafeBag b2 = SafeBag.getInstance(seq2.getObjectAt(j2));
                        if (b2.getBagId().equals(certBag)) {
                            chain.addElement(b2);
                        } else if (b2.getBagId().equals(pkcs8ShroudedKeyBag)) {
                            EncryptedPrivateKeyInfo eIn2 = EncryptedPrivateKeyInfo.getInstance(b2.getBagValue());
                            PrivateKey privKey2 = unwrapKey(eIn2.getEncryptionAlgorithm(), eIn2.getEncryptedData(), password, wrongPKCS12Zero);
                            PKCS12BagAttributeCarrier bagAttr2 = (PKCS12BagAttributeCarrier) privKey2;
                            String alias2 = null;
                            ASN1OctetString localId2 = null;
                            Enumeration e4 = b2.getBagAttributes().getObjects();
                            while (e4.hasMoreElements()) {
                                ASN1Sequence sq2 = (ASN1Sequence) e4.nextElement();
                                ASN1ObjectIdentifier aOid2 = (ASN1ObjectIdentifier) sq2.getObjectAt(0);
                                ASN1Set attrSet2 = (ASN1Set) sq2.getObjectAt(1);
                                ASN1Primitive attr2 = null;
                                if (attrSet2.size() > 0) {
                                    attr2 = (ASN1Primitive) attrSet2.getObjectAt(0);
                                    ASN1Encodable existing2 = bagAttr2.getBagAttribute(aOid2);
                                    if (existing2 != null) {
                                        if (!existing2.toASN1Primitive().equals(attr2)) {
                                            throw new IOException("attempt to add existing attribute with different value");
                                        }
                                    } else {
                                        bagAttr2.setBagAttribute(aOid2, attr2);
                                    }
                                }
                                if (aOid2.equals(pkcs_9_at_friendlyName)) {
                                    alias2 = ((DERBMPString) attr2).getString();
                                    this.keys.put(alias2, privKey2);
                                } else if (aOid2.equals(pkcs_9_at_localKeyId)) {
                                    localId2 = (ASN1OctetString) attr2;
                                }
                            }
                            String name2 = new String(Hex.encode(localId2.getOctets()));
                            if (alias2 == null) {
                                this.keys.put(name2, privKey2);
                            } else {
                                this.localIds.put(alias2, name2);
                            }
                        } else if (b2.getBagId().equals(keyBag)) {
                            PrivateKeyInfo kInfo = PrivateKeyInfo.getInstance(b2.getBagValue());
                            PrivateKey privKey3 = BouncyCastleProvider.getPrivateKey(kInfo);
                            PKCS12BagAttributeCarrier bagAttr3 = (PKCS12BagAttributeCarrier) privKey3;
                            String alias3 = null;
                            ASN1OctetString localId3 = null;
                            Enumeration e5 = b2.getBagAttributes().getObjects();
                            while (e5.hasMoreElements()) {
                                ASN1Sequence sq3 = ASN1Sequence.getInstance(e5.nextElement());
                                ASN1ObjectIdentifier aOid3 = ASN1ObjectIdentifier.getInstance(sq3.getObjectAt(0));
                                ASN1Set attrSet3 = ASN1Set.getInstance(sq3.getObjectAt(1));
                                if (attrSet3.size() > 0) {
                                    ASN1Primitive attr3 = (ASN1Primitive) attrSet3.getObjectAt(0);
                                    ASN1Encodable existing3 = bagAttr3.getBagAttribute(aOid3);
                                    if (existing3 != null) {
                                        if (!existing3.toASN1Primitive().equals(attr3)) {
                                            throw new IOException("attempt to add existing attribute with different value");
                                        }
                                    } else {
                                        bagAttr3.setBagAttribute(aOid3, attr3);
                                    }
                                    if (aOid3.equals(pkcs_9_at_friendlyName)) {
                                        alias3 = ((DERBMPString) attr3).getString();
                                        this.keys.put(alias3, privKey3);
                                    } else if (aOid3.equals(pkcs_9_at_localKeyId)) {
                                        localId3 = (ASN1OctetString) attr3;
                                    }
                                }
                            }
                            String name3 = new String(Hex.encode(localId3.getOctets()));
                            if (alias3 == null) {
                                this.keys.put(name3, privKey3);
                            } else {
                                this.localIds.put(alias3, name3);
                            }
                        } else {
                            System.out.println("extra in encryptedData " + b2.getBagId());
                            System.out.println(ASN1Dump.dumpAsString(b2));
                        }
                    }
                } else {
                    System.out.println("extra " + c[i].getContentType().getId());
                    System.out.println("extra " + ASN1Dump.dumpAsString(c[i].getContent()));
                }
            }
        }
        this.certs = new IgnoresCaseHashtable();
        this.chainCerts = new Hashtable();
        this.keyCerts = new Hashtable();
        for (int i2 = 0; i2 != chain.size(); i2++) {
            SafeBag b3 = (SafeBag) chain.elementAt(i2);
            CertBag cb = CertBag.getInstance(b3.getBagValue());
            if (!cb.getCertId().equals(x509Certificate)) {
                throw new RuntimeException("Unsupported certificate type: " + cb.getCertId());
            }
            try {
                ByteArrayInputStream cIn = new ByteArrayInputStream(((ASN1OctetString) cb.getCertValue()).getOctets());
                Certificate certificateGenerateCertificate = this.certFact.generateCertificate(cIn);
                ASN1OctetString localId4 = null;
                String alias4 = null;
                if (b3.getBagAttributes() != null) {
                    Enumeration e6 = b3.getBagAttributes().getObjects();
                    while (e6.hasMoreElements()) {
                        ASN1Sequence sq4 = ASN1Sequence.getInstance(e6.nextElement());
                        ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(sq4.getObjectAt(0));
                        ASN1Set attrSet4 = ASN1Set.getInstance(sq4.getObjectAt(1));
                        if (attrSet4.size() > 0) {
                            ASN1Primitive attr4 = (ASN1Primitive) attrSet4.getObjectAt(0);
                            if (certificateGenerateCertificate instanceof PKCS12BagAttributeCarrier) {
                                PKCS12BagAttributeCarrier bagAttr4 = (PKCS12BagAttributeCarrier) certificateGenerateCertificate;
                                ASN1Encodable existing4 = bagAttr4.getBagAttribute(oid);
                                if (existing4 != null) {
                                    if (!existing4.toASN1Primitive().equals(attr4)) {
                                        throw new IOException("attempt to add existing attribute with different value");
                                    }
                                } else {
                                    bagAttr4.setBagAttribute(oid, attr4);
                                }
                            }
                            if (oid.equals(pkcs_9_at_friendlyName)) {
                                alias4 = ((DERBMPString) attr4).getString();
                            } else if (oid.equals(pkcs_9_at_localKeyId)) {
                                localId4 = (ASN1OctetString) attr4;
                            }
                        }
                    }
                }
                this.chainCerts.put(new CertId(certificateGenerateCertificate.getPublicKey()), certificateGenerateCertificate);
                if (unmarkedKey) {
                    if (this.keyCerts.isEmpty()) {
                        String name4 = new String(Hex.encode(createSubjectKeyId(certificateGenerateCertificate.getPublicKey()).getKeyIdentifier()));
                        this.keyCerts.put(name4, certificateGenerateCertificate);
                        this.keys.put(name4, this.keys.remove("unmarked"));
                    }
                } else {
                    if (localId4 != null) {
                        this.keyCerts.put(new String(Hex.encode(localId4.getOctets())), certificateGenerateCertificate);
                    }
                    if (alias4 != null) {
                        this.certs.put(alias4, certificateGenerateCertificate);
                    }
                }
            } catch (Exception e7) {
                throw new RuntimeException(e7.toString());
            }
        }
    }

    private int validateIterationCount(BigInteger i) {
        int count = i.intValue();
        if (count < 0) {
            throw new IllegalStateException("negative iteration count found");
        }
        BigInteger maxValue = Properties.asBigInteger(PKCS12_MAX_IT_COUNT_PROPERTY);
        if (maxValue != null && maxValue.intValue() < count) {
            throw new IllegalStateException("iteration count " + count + " greater than " + maxValue.intValue());
        }
        return count;
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(KeyStore.LoadStoreParameter param) throws NoSuchAlgorithmException, IOException, CertificateException {
        PKCS12StoreParameter bcParam;
        char[] password;
        if (param == null) {
            throw new IllegalArgumentException("'param' arg cannot be null");
        }
        if (!(param instanceof PKCS12StoreParameter) && !(param instanceof JDKPKCS12StoreParameter)) {
            throw new IllegalArgumentException("No support for 'param' of type " + param.getClass().getName());
        }
        if (param instanceof PKCS12StoreParameter) {
            bcParam = (PKCS12StoreParameter) param;
        } else {
            bcParam = new PKCS12StoreParameter(((JDKPKCS12StoreParameter) param).getOutputStream(), param.getProtectionParameter(), ((JDKPKCS12StoreParameter) param).isUseDEREncoding());
        }
        KeyStore.ProtectionParameter protParam = param.getProtectionParameter();
        if (protParam == null) {
            password = null;
        } else if (protParam instanceof KeyStore.PasswordProtection) {
            password = ((KeyStore.PasswordProtection) protParam).getPassword();
        } else {
            throw new IllegalArgumentException("No support for protection parameter of type " + protParam.getClass().getName());
        }
        doStore(bcParam.getOutputStream(), password, bcParam.isForDEREncoding());
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(OutputStream stream, char[] password) throws IOException {
        doStore(stream, password, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void doStore(OutputStream stream, char[] password, boolean useDEREncoding) throws IOException {
        DEROutputStream asn1Out;
        DEROutputStream asn1Out2;
        if (password == null) {
            throw new NullPointerException("No password supplied for PKCS#12 KeyStore.");
        }
        ASN1EncodableVector keyS = new ASN1EncodableVector();
        Enumeration ks = this.keys.keys();
        while (ks.hasMoreElements()) {
            byte[] kSalt = new byte[20];
            this.random.nextBytes(kSalt);
            String name = (String) ks.nextElement();
            PrivateKey privKey = (PrivateKey) this.keys.get(name);
            PKCS12PBEParams kParams = new PKCS12PBEParams(kSalt, 1024);
            byte[] kBytes = wrapKey(this.keyAlgorithm.getId(), privKey, kParams, password);
            AlgorithmIdentifier kAlgId = new AlgorithmIdentifier(this.keyAlgorithm, kParams.toASN1Primitive());
            EncryptedPrivateKeyInfo kInfo = new EncryptedPrivateKeyInfo(kAlgId, kBytes);
            boolean attrSet = false;
            ASN1EncodableVector kName = new ASN1EncodableVector();
            if (privKey instanceof PKCS12BagAttributeCarrier) {
                PKCS12BagAttributeCarrier bagAttrs = (PKCS12BagAttributeCarrier) privKey;
                DERBMPString nm = (DERBMPString) bagAttrs.getBagAttribute(pkcs_9_at_friendlyName);
                if (nm == null || !nm.getString().equals(name)) {
                    bagAttrs.setBagAttribute(pkcs_9_at_friendlyName, new DERBMPString(name));
                }
                if (bagAttrs.getBagAttribute(pkcs_9_at_localKeyId) == null) {
                    Certificate ct = engineGetCertificate(name);
                    bagAttrs.setBagAttribute(pkcs_9_at_localKeyId, createSubjectKeyId(ct.getPublicKey()));
                }
                Enumeration e = bagAttrs.getBagAttributeKeys();
                while (e.hasMoreElements()) {
                    ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) e.nextElement();
                    ASN1EncodableVector kSeq = new ASN1EncodableVector();
                    kSeq.add(oid);
                    kSeq.add(new DERSet(bagAttrs.getBagAttribute(oid)));
                    attrSet = true;
                    kName.add(new DERSequence(kSeq));
                }
            }
            if (!attrSet) {
                ASN1EncodableVector kSeq2 = new ASN1EncodableVector();
                Certificate ct2 = engineGetCertificate(name);
                kSeq2.add(pkcs_9_at_localKeyId);
                kSeq2.add(new DERSet(createSubjectKeyId(ct2.getPublicKey())));
                kName.add(new DERSequence(kSeq2));
                ASN1EncodableVector kSeq3 = new ASN1EncodableVector();
                kSeq3.add(pkcs_9_at_friendlyName);
                kSeq3.add(new DERSet(new DERBMPString(name)));
                kName.add(new DERSequence(kSeq3));
            }
            SafeBag kBag = new SafeBag(pkcs8ShroudedKeyBag, kInfo.toASN1Primitive(), new DERSet(kName));
            keyS.add(kBag);
        }
        byte[] keySEncoded = new DERSequence(keyS).getEncoded(ASN1Encoding.DER);
        BEROctetString keyString = new BEROctetString(keySEncoded);
        byte[] cSalt = new byte[20];
        this.random.nextBytes(cSalt);
        ASN1EncodableVector certSeq = new ASN1EncodableVector();
        PKCS12PBEParams cParams = new PKCS12PBEParams(cSalt, 1024);
        AlgorithmIdentifier cAlgId = new AlgorithmIdentifier(this.certAlgorithm, cParams.toASN1Primitive());
        Hashtable hashtable = new Hashtable();
        Enumeration cs = this.keys.keys();
        while (cs.hasMoreElements()) {
            try {
                String name2 = (String) cs.nextElement();
                Certificate certificateEngineGetCertificate = engineGetCertificate(name2);
                boolean cAttrSet = false;
                CertBag cBag = new CertBag(x509Certificate, new DEROctetString(certificateEngineGetCertificate.getEncoded()));
                ASN1EncodableVector fName = new ASN1EncodableVector();
                if (certificateEngineGetCertificate instanceof PKCS12BagAttributeCarrier) {
                    PKCS12BagAttributeCarrier bagAttrs2 = (PKCS12BagAttributeCarrier) certificateEngineGetCertificate;
                    DERBMPString nm2 = (DERBMPString) bagAttrs2.getBagAttribute(pkcs_9_at_friendlyName);
                    if (nm2 == null || !nm2.getString().equals(name2)) {
                        bagAttrs2.setBagAttribute(pkcs_9_at_friendlyName, new DERBMPString(name2));
                    }
                    if (bagAttrs2.getBagAttribute(pkcs_9_at_localKeyId) == null) {
                        bagAttrs2.setBagAttribute(pkcs_9_at_localKeyId, createSubjectKeyId(certificateEngineGetCertificate.getPublicKey()));
                    }
                    Enumeration e2 = bagAttrs2.getBagAttributeKeys();
                    while (e2.hasMoreElements()) {
                        ASN1ObjectIdentifier oid2 = (ASN1ObjectIdentifier) e2.nextElement();
                        ASN1EncodableVector fSeq = new ASN1EncodableVector();
                        fSeq.add(oid2);
                        fSeq.add(new DERSet(bagAttrs2.getBagAttribute(oid2)));
                        fName.add(new DERSequence(fSeq));
                        cAttrSet = true;
                    }
                }
                if (!cAttrSet) {
                    ASN1EncodableVector fSeq2 = new ASN1EncodableVector();
                    fSeq2.add(pkcs_9_at_localKeyId);
                    fSeq2.add(new DERSet(createSubjectKeyId(certificateEngineGetCertificate.getPublicKey())));
                    fName.add(new DERSequence(fSeq2));
                    ASN1EncodableVector fSeq3 = new ASN1EncodableVector();
                    fSeq3.add(pkcs_9_at_friendlyName);
                    fSeq3.add(new DERSet(new DERBMPString(name2)));
                    fName.add(new DERSequence(fSeq3));
                }
                SafeBag sBag = new SafeBag(certBag, cBag.toASN1Primitive(), new DERSet(fName));
                certSeq.add(sBag);
                hashtable.put(certificateEngineGetCertificate, certificateEngineGetCertificate);
            } catch (CertificateEncodingException e3) {
                throw new IOException("Error encoding certificate: " + e3.toString());
            }
        }
        Enumeration cs2 = this.certs.keys();
        while (cs2.hasMoreElements()) {
            try {
                String certId = (String) cs2.nextElement();
                Certificate certificate = (Certificate) this.certs.get(certId);
                boolean cAttrSet2 = false;
                if (this.keys.get(certId) == null) {
                    CertBag cBag2 = new CertBag(x509Certificate, new DEROctetString(certificate.getEncoded()));
                    ASN1EncodableVector fName2 = new ASN1EncodableVector();
                    if (certificate instanceof PKCS12BagAttributeCarrier) {
                        PKCS12BagAttributeCarrier bagAttrs3 = (PKCS12BagAttributeCarrier) certificate;
                        DERBMPString nm3 = (DERBMPString) bagAttrs3.getBagAttribute(pkcs_9_at_friendlyName);
                        if (nm3 == null || !nm3.getString().equals(certId)) {
                            bagAttrs3.setBagAttribute(pkcs_9_at_friendlyName, new DERBMPString(certId));
                        }
                        Enumeration e4 = bagAttrs3.getBagAttributeKeys();
                        while (e4.hasMoreElements()) {
                            ASN1ObjectIdentifier oid3 = (ASN1ObjectIdentifier) e4.nextElement();
                            if (!oid3.equals(PKCSObjectIdentifiers.pkcs_9_at_localKeyId)) {
                                ASN1EncodableVector fSeq4 = new ASN1EncodableVector();
                                fSeq4.add(oid3);
                                fSeq4.add(new DERSet(bagAttrs3.getBagAttribute(oid3)));
                                fName2.add(new DERSequence(fSeq4));
                                cAttrSet2 = true;
                            }
                        }
                    }
                    if (!cAttrSet2) {
                        ASN1EncodableVector fSeq5 = new ASN1EncodableVector();
                        fSeq5.add(pkcs_9_at_friendlyName);
                        fSeq5.add(new DERSet(new DERBMPString(certId)));
                        fName2.add(new DERSequence(fSeq5));
                    }
                    SafeBag sBag2 = new SafeBag(certBag, cBag2.toASN1Primitive(), new DERSet(fName2));
                    certSeq.add(sBag2);
                    hashtable.put(certificate, certificate);
                }
            } catch (CertificateEncodingException e5) {
                throw new IOException("Error encoding certificate: " + e5.toString());
            }
        }
        Set usedCertificateSet = getUsedCertificateSet();
        Enumeration cs3 = this.chainCerts.keys();
        while (cs3.hasMoreElements()) {
            try {
                Certificate certificate2 = (Certificate) this.chainCerts.get((CertId) cs3.nextElement());
                if (usedCertificateSet.contains(certificate2) && hashtable.get(certificate2) == null) {
                    CertBag cBag3 = new CertBag(x509Certificate, new DEROctetString(certificate2.getEncoded()));
                    ASN1EncodableVector fName3 = new ASN1EncodableVector();
                    if (certificate2 instanceof PKCS12BagAttributeCarrier) {
                        PKCS12BagAttributeCarrier bagAttrs4 = (PKCS12BagAttributeCarrier) certificate2;
                        Enumeration e6 = bagAttrs4.getBagAttributeKeys();
                        while (e6.hasMoreElements()) {
                            ASN1ObjectIdentifier oid4 = (ASN1ObjectIdentifier) e6.nextElement();
                            if (!oid4.equals(PKCSObjectIdentifiers.pkcs_9_at_localKeyId)) {
                                ASN1EncodableVector fSeq6 = new ASN1EncodableVector();
                                fSeq6.add(oid4);
                                fSeq6.add(new DERSet(bagAttrs4.getBagAttribute(oid4)));
                                fName3.add(new DERSequence(fSeq6));
                            }
                        }
                    }
                    SafeBag sBag3 = new SafeBag(certBag, cBag3.toASN1Primitive(), new DERSet(fName3));
                    certSeq.add(sBag3);
                }
            } catch (CertificateEncodingException e7) {
                throw new IOException("Error encoding certificate: " + e7.toString());
            }
        }
        byte[] certSeqEncoded = new DERSequence(certSeq).getEncoded(ASN1Encoding.DER);
        byte[] certBytes = cryptData(true, cAlgId, password, false, certSeqEncoded);
        EncryptedData cInfo = new EncryptedData(data, cAlgId, new BEROctetString(certBytes));
        ContentInfo[] info = {new ContentInfo(data, keyString), new ContentInfo(encryptedData, cInfo.toASN1Primitive())};
        AuthenticatedSafe auth = new AuthenticatedSafe(info);
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        if (useDEREncoding) {
            asn1Out = new DEROutputStream(bOut);
        } else {
            asn1Out = new BEROutputStream(bOut);
        }
        asn1Out.writeObject(auth);
        byte[] pkg = bOut.toByteArray();
        ContentInfo mainInfo = new ContentInfo(data, new BEROctetString(pkg));
        byte[] mSalt = new byte[this.saltLength];
        this.random.nextBytes(mSalt);
        byte[] data = ((ASN1OctetString) mainInfo.getContent()).getOctets();
        try {
            byte[] res = calculatePbeMac(this.macAlgorithm.getAlgorithm(), mSalt, this.itCount, password, false, data);
            DigestInfo dInfo = new DigestInfo(this.macAlgorithm, res);
            MacData mData = new MacData(dInfo, mSalt, this.itCount);
            Pfx pfx = new Pfx(mainInfo, mData);
            if (useDEREncoding) {
                asn1Out2 = new DEROutputStream(stream);
            } else {
                asn1Out2 = new BEROutputStream(stream);
            }
            asn1Out2.writeObject(pfx);
        } catch (Exception e8) {
            throw new IOException("error constructing MAC: " + e8.toString());
        }
    }

    private Set getUsedCertificateSet() {
        Set usedSet = new HashSet();
        Enumeration en = this.keys.keys();
        while (en.hasMoreElements()) {
            String alias = (String) en.nextElement();
            Certificate[] certs = engineGetCertificateChain(alias);
            for (int i = 0; i != certs.length; i++) {
                usedSet.add(certs[i]);
            }
        }
        Enumeration en2 = this.certs.keys();
        while (en2.hasMoreElements()) {
            String alias2 = (String) en2.nextElement();
            Certificate cert = engineGetCertificate(alias2);
            usedSet.add(cert);
        }
        return usedSet;
    }

    private byte[] calculatePbeMac(ASN1ObjectIdentifier oid, byte[] salt, int itCount, char[] password, boolean wrongPkcs12Zero, byte[] data) throws Exception {
        PBEParameterSpec defParams = new PBEParameterSpec(salt, itCount);
        Mac mac = this.helper.createMac(oid.getId());
        mac.init(new PKCS12Key(password, wrongPkcs12Zero), defParams);
        mac.update(data);
        return mac.doFinal();
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi$BCPKCS12KeyStore.class */
    public static class BCPKCS12KeyStore extends PKCS12KeyStoreSpi {
        public BCPKCS12KeyStore() {
            super(new BouncyCastleProvider(), pbeWithSHAAnd3_KeyTripleDES_CBC, pbeWithSHAAnd40BitRC2_CBC);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi$BCPKCS12KeyStore3DES.class */
    public static class BCPKCS12KeyStore3DES extends PKCS12KeyStoreSpi {
        public BCPKCS12KeyStore3DES() {
            super(new BouncyCastleProvider(), pbeWithSHAAnd3_KeyTripleDES_CBC, pbeWithSHAAnd3_KeyTripleDES_CBC);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi$DefPKCS12KeyStore.class */
    public static class DefPKCS12KeyStore extends PKCS12KeyStoreSpi {
        public DefPKCS12KeyStore() {
            super(null, pbeWithSHAAnd3_KeyTripleDES_CBC, pbeWithSHAAnd40BitRC2_CBC);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi$DefPKCS12KeyStore3DES.class */
    public static class DefPKCS12KeyStore3DES extends PKCS12KeyStoreSpi {
        public DefPKCS12KeyStore3DES() {
            super(null, pbeWithSHAAnd3_KeyTripleDES_CBC, pbeWithSHAAnd3_KeyTripleDES_CBC);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi$IgnoresCaseHashtable.class */
    private static class IgnoresCaseHashtable {
        private Hashtable orig;
        private Hashtable keys;

        private IgnoresCaseHashtable() {
            this.orig = new Hashtable();
            this.keys = new Hashtable();
        }

        public void put(String key, Object value) {
            String lower = key == null ? null : Strings.toLowerCase(key);
            String k = (String) this.keys.get(lower);
            if (k != null) {
                this.orig.remove(k);
            }
            this.keys.put(lower, key);
            this.orig.put(key, value);
        }

        public Enumeration keys() {
            return this.orig.keys();
        }

        public Object remove(String alias) {
            String k = (String) this.keys.remove(alias == null ? null : Strings.toLowerCase(alias));
            if (k == null) {
                return null;
            }
            return this.orig.remove(k);
        }

        public Object get(String alias) {
            String k = (String) this.keys.get(alias == null ? null : Strings.toLowerCase(alias));
            if (k == null) {
                return null;
            }
            return this.orig.get(k);
        }

        public Enumeration elements() {
            return this.orig.elements();
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi$DefaultSecretKeyProvider.class */
    private static class DefaultSecretKeyProvider {
        private final Map KEY_SIZES;

        DefaultSecretKeyProvider() {
            Map keySizes = new HashMap();
            keySizes.put(new ASN1ObjectIdentifier(CMSEnvelopedGenerator.CAST5_CBC), Integers.valueOf(128));
            keySizes.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf(192));
            keySizes.put(NISTObjectIdentifiers.id_aes128_CBC, Integers.valueOf(128));
            keySizes.put(NISTObjectIdentifiers.id_aes192_CBC, Integers.valueOf(192));
            keySizes.put(NISTObjectIdentifiers.id_aes256_CBC, Integers.valueOf(256));
            keySizes.put(NTTObjectIdentifiers.id_camellia128_cbc, Integers.valueOf(128));
            keySizes.put(NTTObjectIdentifiers.id_camellia192_cbc, Integers.valueOf(192));
            keySizes.put(NTTObjectIdentifiers.id_camellia256_cbc, Integers.valueOf(256));
            keySizes.put(CryptoProObjectIdentifiers.gostR28147_gcfb, Integers.valueOf(256));
            this.KEY_SIZES = Collections.unmodifiableMap(keySizes);
        }

        public int getKeySize(AlgorithmIdentifier algorithmIdentifier) {
            Integer keySize = (Integer) this.KEY_SIZES.get(algorithmIdentifier.getAlgorithm());
            if (keySize != null) {
                return keySize.intValue();
            }
            return -1;
        }
    }
}
