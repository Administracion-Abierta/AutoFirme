package org.bouncycastle.jcajce.provider.keystore.pkcs12;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
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
import org.bouncycastle.asn1.ASN1BMPString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST28147Parameters;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.AuthenticatedSafe;
import org.bouncycastle.asn1.pkcs.CertBag;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.EncryptedData;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.internal.asn1.cms.GCMParameters;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.internal.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.BCLoadStoreParameter;
import org.bouncycastle.jcajce.PKCS12Key;
import org.bouncycastle.jcajce.PKCS12StoreParameter;
import org.bouncycastle.jcajce.provider.keystore.util.AdaptingKeyStoreSpi;
import org.bouncycastle.jcajce.provider.keystore.util.ParameterUtil;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;
import org.bouncycastle.jcajce.spec.PBKDF2KeySpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.BCKeyStore;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.JDKPKCS12StoreParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;
import org.spongycastle.cms.CMSEnvelopedGenerator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi.class */
public class PKCS12KeyStoreSpi extends KeyStoreSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers, BCKeyStore {
    static final String PKCS12_MAX_IT_COUNT_PROPERTY = "org.bouncycastle.pkcs12.max_it_count";
    private static final int SALT_SIZE = 20;
    private static final int MIN_ITERATIONS = 51200;
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
    private IgnoresCaseHashtable localIds = new IgnoresCaseHashtable();
    private IgnoresCaseHashtable certs = new IgnoresCaseHashtable();
    private Hashtable chainCerts = new Hashtable();
    private Hashtable keyCerts = new Hashtable();
    protected SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    private AlgorithmIdentifier macAlgorithm = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
    private int itCount = 102400;
    private int saltLength = 20;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi$BCPKCS12KeyStore.class */
    public static class BCPKCS12KeyStore extends AdaptingKeyStoreSpi {
        public BCPKCS12KeyStore() {
            super(new BCJcaJceHelper(), new PKCS12KeyStoreSpi(new BCJcaJceHelper(), PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi$BCPKCS12KeyStore3DES.class */
    public static class BCPKCS12KeyStore3DES extends AdaptingKeyStoreSpi {
        public BCPKCS12KeyStore3DES() {
            super(new BCJcaJceHelper(), new PKCS12KeyStoreSpi(new BCJcaJceHelper(), PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi$BCPKCS12KeyStoreAES256.class */
    public static class BCPKCS12KeyStoreAES256 extends AdaptingKeyStoreSpi {
        public BCPKCS12KeyStoreAES256() {
            super(new BCJcaJceHelper(), new PKCS12KeyStoreSpi(new BCJcaJceHelper(), NISTObjectIdentifiers.id_aes256_CBC, NISTObjectIdentifiers.id_aes128_CBC));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi$BCPKCS12KeyStoreAES256GCM.class */
    public static class BCPKCS12KeyStoreAES256GCM extends AdaptingKeyStoreSpi {
        public BCPKCS12KeyStoreAES256GCM() {
            super(new BCJcaJceHelper(), new PKCS12KeyStoreSpi(new BCJcaJceHelper(), NISTObjectIdentifiers.id_aes256_GCM, NISTObjectIdentifiers.id_aes128_GCM));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi$CertId.class */
    private class CertId {
        byte[] id;

        CertId(PublicKey publicKey) {
            this.id = PKCS12KeyStoreSpi.this.createSubjectKeyId(publicKey).getKeyIdentifier();
        }

        CertId(byte[] bArr) {
            this.id = bArr;
        }

        public int hashCode() {
            return Arrays.hashCode(this.id);
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof CertId) {
                return Arrays.areEqual(this.id, ((CertId) obj).id);
            }
            return false;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi$DefPKCS12KeyStore.class */
    public static class DefPKCS12KeyStore extends AdaptingKeyStoreSpi {
        public DefPKCS12KeyStore() {
            super(new DefaultJcaJceHelper(), new PKCS12KeyStoreSpi(new DefaultJcaJceHelper(), PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi$DefPKCS12KeyStore3DES.class */
    public static class DefPKCS12KeyStore3DES extends AdaptingKeyStoreSpi {
        public DefPKCS12KeyStore3DES() {
            super(new DefaultJcaJceHelper(), new PKCS12KeyStoreSpi(new DefaultJcaJceHelper(), PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi$DefPKCS12KeyStoreAES256.class */
    public static class DefPKCS12KeyStoreAES256 extends AdaptingKeyStoreSpi {
        public DefPKCS12KeyStoreAES256() {
            super(new BCJcaJceHelper(), new PKCS12KeyStoreSpi(new BCJcaJceHelper(), NISTObjectIdentifiers.id_aes256_CBC, NISTObjectIdentifiers.id_aes128_CBC));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi$DefPKCS12KeyStoreAES256GCM.class */
    public static class DefPKCS12KeyStoreAES256GCM extends AdaptingKeyStoreSpi {
        public DefPKCS12KeyStoreAES256GCM() {
            super(new BCJcaJceHelper(), new PKCS12KeyStoreSpi(new BCJcaJceHelper(), NISTObjectIdentifiers.id_aes256_GCM, NISTObjectIdentifiers.id_aes128_GCM));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi$DefaultSecretKeyProvider.class */
    private static class DefaultSecretKeyProvider {
        private final Map KEY_SIZES;

        DefaultSecretKeyProvider() {
            HashMap map = new HashMap();
            map.put(new ASN1ObjectIdentifier(CMSEnvelopedGenerator.CAST5_CBC), Integers.valueOf(128));
            map.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf(192));
            map.put(NISTObjectIdentifiers.id_aes128_CBC, Integers.valueOf(128));
            map.put(NISTObjectIdentifiers.id_aes192_CBC, Integers.valueOf(192));
            map.put(NISTObjectIdentifiers.id_aes256_CBC, Integers.valueOf(256));
            map.put(NISTObjectIdentifiers.id_aes128_GCM, Integers.valueOf(128));
            map.put(NISTObjectIdentifiers.id_aes256_GCM, Integers.valueOf(256));
            map.put(NTTObjectIdentifiers.id_camellia128_cbc, Integers.valueOf(128));
            map.put(NTTObjectIdentifiers.id_camellia192_cbc, Integers.valueOf(192));
            map.put(NTTObjectIdentifiers.id_camellia256_cbc, Integers.valueOf(256));
            map.put(CryptoProObjectIdentifiers.gostR28147_gcfb, Integers.valueOf(256));
            this.KEY_SIZES = Collections.unmodifiableMap(map);
        }

        public int getKeySize(AlgorithmIdentifier algorithmIdentifier) {
            Integer num = (Integer) this.KEY_SIZES.get(algorithmIdentifier.getAlgorithm());
            if (num != null) {
                return num.intValue();
            }
            return -1;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/pkcs12/PKCS12KeyStoreSpi$IgnoresCaseHashtable.class */
    private static class IgnoresCaseHashtable {
        private Hashtable orig;
        private Hashtable keys;

        private IgnoresCaseHashtable() {
            this.orig = new Hashtable();
            this.keys = new Hashtable();
        }

        public void put(String str, Object obj) {
            String lowerCase = str == null ? null : Strings.toLowerCase(str);
            String str2 = (String) this.keys.get(lowerCase);
            if (str2 != null) {
                this.orig.remove(str2);
            }
            this.keys.put(lowerCase, str);
            this.orig.put(str, obj);
        }

        public Enumeration keys() {
            return new Hashtable(this.orig).keys();
        }

        public Object remove(String str) {
            String str2 = (String) this.keys.remove(str == null ? null : Strings.toLowerCase(str));
            if (str2 == null) {
                return null;
            }
            return this.orig.remove(str2);
        }

        public Object get(String str) {
            String str2 = (String) this.keys.get(str == null ? null : Strings.toLowerCase(str));
            if (str2 == null) {
                return null;
            }
            return this.orig.get(str2);
        }

        public Enumeration elements() {
            return this.orig.elements();
        }

        public int size() {
            return this.orig.size();
        }
    }

    private static boolean isPBKDF2(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return aSN1ObjectIdentifier.equals((ASN1Primitive) NISTObjectIdentifiers.id_aes256_CBC) || aSN1ObjectIdentifier.equals((ASN1Primitive) NISTObjectIdentifiers.id_aes256_GCM) || aSN1ObjectIdentifier.equals((ASN1Primitive) NISTObjectIdentifiers.id_aes128_CBC) || aSN1ObjectIdentifier.equals((ASN1Primitive) NISTObjectIdentifiers.id_aes128_GCM);
    }

    private static int getKeyLength(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return (aSN1ObjectIdentifier.equals((ASN1Primitive) NISTObjectIdentifiers.id_aes256_CBC) || aSN1ObjectIdentifier.equals((ASN1Primitive) NISTObjectIdentifiers.id_aes256_GCM)) ? 32 : 16;
    }

    public PKCS12KeyStoreSpi(JcaJceHelper jcaJceHelper, ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1ObjectIdentifier aSN1ObjectIdentifier2) {
        this.keyAlgorithm = aSN1ObjectIdentifier;
        this.certAlgorithm = aSN1ObjectIdentifier2;
        try {
            this.certFact = jcaJceHelper.createCertificateFactory(XMLX509Certificate.JCA_CERT_ID);
        } catch (Exception e) {
            throw new IllegalArgumentException("can't create cert factory - " + e.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SubjectKeyIdentifier createSubjectKeyId(PublicKey publicKey) {
        try {
            return new SubjectKeyIdentifier(getDigest(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded())));
        } catch (Exception e) {
            throw new RuntimeException("error creating key");
        }
    }

    private static byte[] getDigest(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        Digest digestCreateSHA1 = DigestFactory.createSHA1();
        byte[] bArr = new byte[digestCreateSHA1.getDigestSize()];
        byte[] bytes = subjectPublicKeyInfo.getPublicKeyData().getBytes();
        digestCreateSHA1.update(bytes, 0, bytes.length);
        digestCreateSHA1.doFinal(bArr, 0);
        return bArr;
    }

    @Override // org.bouncycastle.jce.interfaces.BCKeyStore
    public void setRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineProbe(InputStream inputStream) throws IOException {
        return false;
    }

    @Override // java.security.KeyStoreSpi
    public Enumeration engineAliases() {
        Hashtable hashtable = new Hashtable();
        Enumeration enumerationKeys = this.certs.keys();
        while (enumerationKeys.hasMoreElements()) {
            hashtable.put(enumerationKeys.nextElement(), "cert");
        }
        Enumeration enumerationKeys2 = this.keys.keys();
        while (enumerationKeys2.hasMoreElements()) {
            String str = (String) enumerationKeys2.nextElement();
            if (hashtable.get(str) == null) {
                hashtable.put(str, "key");
            }
        }
        return hashtable.keys();
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineContainsAlias(String str) {
        return (this.certs.get(str) == null && this.keys.get(str) == null) ? false : true;
    }

    @Override // java.security.KeyStoreSpi
    public void engineDeleteEntry(String str) throws KeyStoreException {
        String str2;
        Certificate certificate;
        Certificate certificate2 = (Certificate) this.certs.remove(str);
        if (certificate2 != null) {
            this.chainCerts.remove(new CertId(certificate2.getPublicKey()));
        }
        if (((Key) this.keys.remove(str)) == null || (str2 = (String) this.localIds.remove(str)) == null || (certificate = (Certificate) this.keyCerts.remove(str2)) == null) {
            return;
        }
        this.chainCerts.remove(new CertId(certificate.getPublicKey()));
    }

    @Override // java.security.KeyStoreSpi
    public Certificate engineGetCertificate(String str) {
        if (str == null) {
            throw new IllegalArgumentException("null alias passed to getCertificate.");
        }
        Certificate certificate = (Certificate) this.certs.get(str);
        if (certificate == null) {
            String str2 = (String) this.localIds.get(str);
            certificate = str2 != null ? (Certificate) this.keyCerts.get(str2) : (Certificate) this.keyCerts.get(str);
        }
        return certificate;
    }

    @Override // java.security.KeyStoreSpi
    public String engineGetCertificateAlias(Certificate certificate) {
        Enumeration enumerationElements = this.certs.elements();
        Enumeration enumerationKeys = this.certs.keys();
        while (enumerationElements.hasMoreElements()) {
            Certificate certificate2 = (Certificate) enumerationElements.nextElement();
            String str = (String) enumerationKeys.nextElement();
            if (certificate2.equals(certificate)) {
                return str;
            }
        }
        Enumeration enumerationElements2 = this.keyCerts.elements();
        Enumeration enumerationKeys2 = this.keyCerts.keys();
        while (enumerationElements2.hasMoreElements()) {
            Certificate certificate3 = (Certificate) enumerationElements2.nextElement();
            String str2 = (String) enumerationKeys2.nextElement();
            if (certificate3.equals(certificate)) {
                return str2;
            }
        }
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public Certificate[] engineGetCertificateChain(String str) {
        byte[] keyIdentifier;
        if (str == null) {
            throw new IllegalArgumentException("null alias passed to getCertificateChain.");
        }
        if (!engineIsKeyEntry(str)) {
            return null;
        }
        Certificate certificateEngineGetCertificate = engineGetCertificate(str);
        if (certificateEngineGetCertificate == null) {
            return null;
        }
        Vector vector = new Vector();
        while (certificateEngineGetCertificate != null) {
            X509Certificate x509Certificate = (X509Certificate) certificateEngineGetCertificate;
            Certificate certificate = null;
            byte[] extensionValue = x509Certificate.getExtensionValue(Extension.authorityKeyIdentifier.getId());
            if (extensionValue != null && null != (keyIdentifier = AuthorityKeyIdentifier.getInstance(ASN1OctetString.getInstance(extensionValue).getOctets()).getKeyIdentifier())) {
                certificate = (Certificate) this.chainCerts.get(new CertId(keyIdentifier));
            }
            if (certificate == null) {
                Principal issuerDN = x509Certificate.getIssuerDN();
                if (!issuerDN.equals(x509Certificate.getSubjectDN())) {
                    Enumeration enumerationKeys = this.chainCerts.keys();
                    while (enumerationKeys.hasMoreElements()) {
                        X509Certificate x509Certificate2 = (X509Certificate) this.chainCerts.get(enumerationKeys.nextElement());
                        if (x509Certificate2.getSubjectDN().equals(issuerDN)) {
                            try {
                                x509Certificate.verify(x509Certificate2.getPublicKey());
                                certificate = x509Certificate2;
                                break;
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
            if (vector.contains(certificateEngineGetCertificate)) {
                certificateEngineGetCertificate = null;
            } else {
                vector.addElement(certificateEngineGetCertificate);
                certificateEngineGetCertificate = certificate != certificateEngineGetCertificate ? certificate : null;
            }
        }
        Certificate[] certificateArr = new Certificate[vector.size()];
        for (int i = 0; i != certificateArr.length; i++) {
            certificateArr[i] = (Certificate) vector.elementAt(i);
        }
        return certificateArr;
    }

    @Override // java.security.KeyStoreSpi
    public Date engineGetCreationDate(String str) {
        if (str == null) {
            throw new NullPointerException("alias == null");
        }
        if (this.keys.get(str) == null && this.certs.get(str) == null) {
            return null;
        }
        return new Date();
    }

    @Override // java.security.KeyStoreSpi
    public Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        if (str == null) {
            throw new IllegalArgumentException("null alias passed to getKey.");
        }
        return (Key) this.keys.get(str);
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsCertificateEntry(String str) {
        return this.certs.get(str) != null && this.keys.get(str) == null;
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsKeyEntry(String str) {
        return this.keys.get(str) != null;
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
        if (this.keys.get(str) != null) {
            throw new KeyStoreException("There is a key entry with the name " + str + ".");
        }
        this.certs.put(str, certificate);
        this.chainCerts.put(new CertId(certificate.getPublicKey()), certificate);
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
        throw new RuntimeException("operation not supported");
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
        if (!(key instanceof PrivateKey)) {
            throw new KeyStoreException("PKCS12 does not support non-PrivateKeys");
        }
        if ((key instanceof PrivateKey) && certificateArr == null) {
            throw new KeyStoreException("no certificate chain for private key");
        }
        if (this.keys.get(str) != null) {
            engineDeleteEntry(str);
        }
        this.keys.put(str, key);
        if (certificateArr != null) {
            this.certs.put(str, certificateArr[0]);
            for (int i = 0; i != certificateArr.length; i++) {
                this.chainCerts.put(new CertId(certificateArr[i].getPublicKey()), certificateArr[i]);
            }
        }
    }

    @Override // java.security.KeyStoreSpi
    public int engineSize() {
        Hashtable hashtable = new Hashtable();
        Enumeration enumerationKeys = this.certs.keys();
        while (enumerationKeys.hasMoreElements()) {
            hashtable.put(enumerationKeys.nextElement(), "cert");
        }
        Enumeration enumerationKeys2 = this.keys.keys();
        while (enumerationKeys2.hasMoreElements()) {
            String str = (String) enumerationKeys2.nextElement();
            if (hashtable.get(str) == null) {
                hashtable.put(str, "key");
            }
        }
        return hashtable.size();
    }

    protected PrivateKey unwrapKey(AlgorithmIdentifier algorithmIdentifier, byte[] bArr, char[] cArr, boolean z) throws IOException {
        ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
        try {
            if (!algorithm.on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
                if (algorithm.equals((ASN1Primitive) PKCSObjectIdentifiers.id_PBES2)) {
                    return (PrivateKey) createCipher(4, cArr, algorithmIdentifier).unwrap(bArr, "", 2);
                }
                throw new IOException("exception unwrapping private key - cannot recognise: " + algorithm);
            }
            PKCS12PBEParams pKCS12PBEParams = PKCS12PBEParams.getInstance(algorithmIdentifier.getParameters());
            PBEParameterSpec pBEParameterSpec = new PBEParameterSpec(pKCS12PBEParams.getIV(), validateIterationCount(pKCS12PBEParams.getIterations()));
            Cipher cipherCreateCipher = this.helper.createCipher(algorithm.getId());
            cipherCreateCipher.init(4, new PKCS12Key(cArr, z), pBEParameterSpec);
            return (PrivateKey) cipherCreateCipher.unwrap(bArr, "", 2);
        } catch (Exception e) {
            throw new IOException("exception unwrapping private key - " + e.toString());
        }
    }

    protected byte[] wrapKey(String str, Key key, PKCS12PBEParams pKCS12PBEParams, char[] cArr) throws IOException {
        PBEKeySpec pBEKeySpec = new PBEKeySpec(cArr);
        try {
            SecretKeyFactory secretKeyFactoryCreateSecretKeyFactory = this.helper.createSecretKeyFactory(str);
            PBEParameterSpec pBEParameterSpec = new PBEParameterSpec(pKCS12PBEParams.getIV(), BigIntegers.intValueExact(pKCS12PBEParams.getIterations()));
            Cipher cipherCreateCipher = this.helper.createCipher(str);
            cipherCreateCipher.init(3, secretKeyFactoryCreateSecretKeyFactory.generateSecret(pBEKeySpec), pBEParameterSpec);
            return cipherCreateCipher.wrap(key);
        } catch (Exception e) {
            throw new IOException("exception encrypting data - " + e.toString());
        }
    }

    protected byte[] wrapKey(EncryptionScheme encryptionScheme, Key key, PBKDF2Params pBKDF2Params, char[] cArr) throws IOException {
        PBEKeySpec pBEKeySpec = new PBEKeySpec(cArr, pBKDF2Params.getSalt(), BigIntegers.intValueExact(pBKDF2Params.getIterationCount()), BigIntegers.intValueExact(pBKDF2Params.getKeyLength()) * 8);
        try {
            SecretKeyFactory secretKeyFactoryCreateSecretKeyFactory = this.helper.createSecretKeyFactory("PBKDF2withHMacSHA256");
            Cipher cipherCreateCipher = this.helper.createCipher(encryptionScheme.getAlgorithm().getId());
            AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(encryptionScheme.getAlgorithm().getId());
            algorithmParameters.init(encryptionScheme.getParameters().toASN1Primitive().getEncoded());
            cipherCreateCipher.init(3, secretKeyFactoryCreateSecretKeyFactory.generateSecret(pBEKeySpec), algorithmParameters);
            return cipherCreateCipher.wrap(key);
        } catch (Exception e) {
            throw new IOException("exception encrypting data - " + e.toString());
        }
    }

    protected byte[] cryptData(boolean z, AlgorithmIdentifier algorithmIdentifier, char[] cArr, boolean z2, byte[] bArr) throws IOException {
        ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
        int i = z ? 1 : 2;
        if (!algorithm.on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
            if (!algorithm.equals((ASN1Primitive) PKCSObjectIdentifiers.id_PBES2)) {
                throw new IOException("unknown PBE algorithm: " + algorithm);
            }
            try {
                return createCipher(i, cArr, algorithmIdentifier).doFinal(bArr);
            } catch (Exception e) {
                throw new IOException("exception decrypting data - " + e.toString());
            }
        }
        PKCS12PBEParams pKCS12PBEParams = PKCS12PBEParams.getInstance(algorithmIdentifier.getParameters());
        try {
            PBEParameterSpec pBEParameterSpec = new PBEParameterSpec(pKCS12PBEParams.getIV(), BigIntegers.intValueExact(pKCS12PBEParams.getIterations()));
            PKCS12Key pKCS12Key = new PKCS12Key(cArr, z2);
            Cipher cipherCreateCipher = this.helper.createCipher(algorithm.getId());
            cipherCreateCipher.init(i, pKCS12Key, pBEParameterSpec);
            return cipherCreateCipher.doFinal(bArr);
        } catch (Exception e2) {
            throw new IOException("exception decrypting data - " + e2.toString());
        }
    }

    private Cipher createCipher(int i, char[] cArr, AlgorithmIdentifier algorithmIdentifier) throws NoSuchPaddingException, InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, InvalidAlgorithmParameterException {
        PBES2Parameters pBES2Parameters = PBES2Parameters.getInstance(algorithmIdentifier.getParameters());
        PBKDF2Params pBKDF2Params = PBKDF2Params.getInstance(pBES2Parameters.getKeyDerivationFunc().getParameters());
        AlgorithmIdentifier algorithmIdentifier2 = AlgorithmIdentifier.getInstance(pBES2Parameters.getEncryptionScheme());
        SecretKeyFactory secretKeyFactoryCreateSecretKeyFactory = this.helper.createSecretKeyFactory(pBES2Parameters.getKeyDerivationFunc().getAlgorithm().getId());
        SecretKey secretKeyGenerateSecret = pBKDF2Params.isDefaultPrf() ? secretKeyFactoryCreateSecretKeyFactory.generateSecret(new PBEKeySpec(cArr, pBKDF2Params.getSalt(), validateIterationCount(pBKDF2Params.getIterationCount()), keySizeProvider.getKeySize(algorithmIdentifier2))) : secretKeyFactoryCreateSecretKeyFactory.generateSecret(new PBKDF2KeySpec(cArr, pBKDF2Params.getSalt(), validateIterationCount(pBKDF2Params.getIterationCount()), keySizeProvider.getKeySize(algorithmIdentifier2), pBKDF2Params.getPrf()));
        Cipher cipherCreateCipher = this.helper.createCipher(pBES2Parameters.getEncryptionScheme().getAlgorithm().getId());
        ASN1Encodable parameters = pBES2Parameters.getEncryptionScheme().getParameters();
        if (parameters instanceof ASN1OctetString) {
            cipherCreateCipher.init(i, secretKeyGenerateSecret, new IvParameterSpec(ASN1OctetString.getInstance(parameters).getOctets()));
        } else {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(parameters);
            if (aSN1Sequence.getObjectAt(1) instanceof ASN1ObjectIdentifier) {
                GOST28147Parameters gOST28147Parameters = GOST28147Parameters.getInstance(parameters);
                cipherCreateCipher.init(i, secretKeyGenerateSecret, new GOST28147ParameterSpec(gOST28147Parameters.getEncryptionParamSet(), gOST28147Parameters.getIV()));
            } else {
                AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(algorithmIdentifier2.getAlgorithm().getId(), BouncyCastleProvider.PROVIDER_NAME);
                try {
                    algorithmParameters.init(aSN1Sequence.getEncoded());
                    cipherCreateCipher.init(i, secretKeyGenerateSecret, algorithmParameters);
                } catch (IOException e) {
                    throw new InvalidKeySpecException(e.getMessage());
                }
            }
        }
        return cipherCreateCipher;
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(KeyStore.LoadStoreParameter loadStoreParameter) throws NoSuchAlgorithmException, IOException, CertificateException {
        if (loadStoreParameter == null) {
            engineLoad(null, null);
        } else {
            if (!(loadStoreParameter instanceof BCLoadStoreParameter)) {
                throw new IllegalArgumentException("no support for 'param' of type " + loadStoreParameter.getClass().getName());
            }
            engineLoad(((BCLoadStoreParameter) loadStoreParameter).getInputStream(), ParameterUtil.extractPassword(loadStoreParameter));
        }
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
    public void engineLoad(InputStream inputStream, char[] cArr) throws IOException {
        if (inputStream == null) {
            return;
        }
        boolean z = true;
        boolean z2 = true;
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        bufferedInputStream.mark(10);
        int i = bufferedInputStream.read();
        if (i < 0) {
            throw new EOFException("no data in keystore stream");
        }
        if (i != 48) {
            throw new IOException("stream does not represent a PKCS12 key store");
        }
        bufferedInputStream.reset();
        try {
            Pfx pfx = Pfx.getInstance(new ASN1InputStream(bufferedInputStream).readObject());
            ContentInfo authSafe = pfx.getAuthSafe();
            Vector vector = new Vector();
            boolean zProcessShroudedKeyBag = false;
            boolean z3 = false;
            if (pfx.getMacData() != null) {
                if (cArr == null) {
                    throw new NullPointerException("no password supplied when one expected");
                }
                z = false;
                MacData macData = pfx.getMacData();
                DigestInfo mac = macData.getMac();
                this.macAlgorithm = mac.getAlgorithmId();
                byte[] salt = macData.getSalt();
                this.itCount = validateIterationCount(macData.getIterationCount());
                this.saltLength = salt.length;
                byte[] octets = ((ASN1OctetString) authSafe.getContent()).getOctets();
                try {
                    byte[] bArrCalculatePbeMac = calculatePbeMac(this.macAlgorithm.getAlgorithm(), salt, this.itCount, cArr, false, octets);
                    byte[] digest = mac.getDigest();
                    if (!Arrays.constantTimeAreEqual(bArrCalculatePbeMac, digest)) {
                        if (cArr.length > 0) {
                            throw new IOException("PKCS12 key store mac invalid - wrong password or corrupted file.");
                        }
                        if (!Arrays.constantTimeAreEqual(calculatePbeMac(this.macAlgorithm.getAlgorithm(), salt, this.itCount, cArr, true, octets), digest)) {
                            throw new IOException("PKCS12 key store mac invalid - wrong password or corrupted file.");
                        }
                        z3 = true;
                    }
                } catch (IOException e) {
                    throw e;
                } catch (Exception e2) {
                    throw new IOException("error constructing MAC: " + e2.toString());
                }
            }
            this.keys = new IgnoresCaseHashtable();
            this.localIds = new IgnoresCaseHashtable();
            if (authSafe.getContentType().equals((ASN1Primitive) data)) {
                ContentInfo[] contentInfo = AuthenticatedSafe.getInstance(ASN1OctetString.getInstance(authSafe.getContent()).getOctets()).getContentInfo();
                for (int i2 = 0; i2 != contentInfo.length; i2++) {
                    if (contentInfo[i2].getContentType().equals((ASN1Primitive) data)) {
                        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(ASN1OctetString.getInstance(contentInfo[i2].getContent()).getOctets());
                        for (int i3 = 0; i3 != aSN1Sequence.size(); i3++) {
                            SafeBag safeBag = SafeBag.getInstance(aSN1Sequence.getObjectAt(i3));
                            if (safeBag.getBagId().equals((ASN1Primitive) pkcs8ShroudedKeyBag)) {
                                zProcessShroudedKeyBag = processShroudedKeyBag(safeBag, cArr, z3);
                                z2 = false;
                            } else if (safeBag.getBagId().equals((ASN1Primitive) certBag)) {
                                vector.addElement(safeBag);
                            } else if (safeBag.getBagId().equals((ASN1Primitive) keyBag)) {
                                processKeyBag(safeBag);
                            } else {
                                System.out.println("extra in data " + safeBag.getBagId());
                                System.out.println(ASN1Dump.dumpAsString(safeBag));
                            }
                        }
                    } else if (contentInfo[i2].getContentType().equals((ASN1Primitive) encryptedData)) {
                        EncryptedData encryptedData = EncryptedData.getInstance(contentInfo[i2].getContent());
                        ASN1Sequence aSN1Sequence2 = ASN1Sequence.getInstance(cryptData(false, encryptedData.getEncryptionAlgorithm(), cArr, z3, encryptedData.getContent().getOctets()));
                        z2 = false;
                        for (int i4 = 0; i4 != aSN1Sequence2.size(); i4++) {
                            SafeBag safeBag2 = SafeBag.getInstance(aSN1Sequence2.getObjectAt(i4));
                            if (safeBag2.getBagId().equals((ASN1Primitive) certBag)) {
                                vector.addElement(safeBag2);
                            } else if (safeBag2.getBagId().equals((ASN1Primitive) pkcs8ShroudedKeyBag)) {
                                zProcessShroudedKeyBag = processShroudedKeyBag(safeBag2, cArr, z3);
                            } else if (safeBag2.getBagId().equals((ASN1Primitive) keyBag)) {
                                processKeyBag(safeBag2);
                            } else {
                                System.out.println("extra in encryptedData " + safeBag2.getBagId());
                                System.out.println(ASN1Dump.dumpAsString(safeBag2));
                            }
                        }
                    } else {
                        System.out.println("extra " + contentInfo[i2].getContentType().getId());
                        System.out.println("extra " + ASN1Dump.dumpAsString(contentInfo[i2].getContent()));
                    }
                }
            }
            this.certs = new IgnoresCaseHashtable();
            this.chainCerts = new Hashtable();
            this.keyCerts = new Hashtable();
            for (int i5 = 0; i5 != vector.size(); i5++) {
                SafeBag safeBag3 = (SafeBag) vector.elementAt(i5);
                CertBag certBag = CertBag.getInstance(safeBag3.getBagValue());
                if (!certBag.getCertId().equals((ASN1Primitive) x509Certificate)) {
                    throw new RuntimeException("Unsupported certificate type: " + certBag.getCertId());
                }
                try {
                    Certificate certificateGenerateCertificate = this.certFact.generateCertificate(new ByteArrayInputStream(((ASN1OctetString) certBag.getCertValue()).getOctets()));
                    ASN1OctetString aSN1OctetString = null;
                    String string = null;
                    if (safeBag3.getBagAttributes() != null) {
                        Enumeration objects = safeBag3.getBagAttributes().getObjects();
                        while (objects.hasMoreElements()) {
                            ASN1Sequence aSN1Sequence3 = ASN1Sequence.getInstance(objects.nextElement());
                            ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(aSN1Sequence3.getObjectAt(0));
                            ASN1Set aSN1Set = ASN1Set.getInstance(aSN1Sequence3.getObjectAt(1));
                            if (aSN1Set.size() > 0) {
                                ASN1Primitive aSN1Primitive = (ASN1Primitive) aSN1Set.getObjectAt(0);
                                if (certificateGenerateCertificate instanceof PKCS12BagAttributeCarrier) {
                                    PKCS12BagAttributeCarrier pKCS12BagAttributeCarrier = (PKCS12BagAttributeCarrier) certificateGenerateCertificate;
                                    ASN1Encodable bagAttribute = pKCS12BagAttributeCarrier.getBagAttribute(aSN1ObjectIdentifier);
                                    if (bagAttribute != null) {
                                        if (aSN1ObjectIdentifier.equals((ASN1Primitive) pkcs_9_at_localKeyId)) {
                                            String hexString = Hex.toHexString(((ASN1OctetString) aSN1Primitive).getOctets());
                                            if (this.keys.keys.containsKey(hexString) || this.localIds.keys.containsKey(hexString)) {
                                            }
                                        }
                                        if (!bagAttribute.toASN1Primitive().equals(aSN1Primitive)) {
                                            throw new IOException("attempt to add existing attribute with different value");
                                        }
                                    } else if (aSN1Set.size() > 1) {
                                        pKCS12BagAttributeCarrier.setBagAttribute(aSN1ObjectIdentifier, aSN1Set);
                                    } else {
                                        pKCS12BagAttributeCarrier.setBagAttribute(aSN1ObjectIdentifier, aSN1Primitive);
                                    }
                                }
                                if (aSN1ObjectIdentifier.equals((ASN1Primitive) pkcs_9_at_friendlyName)) {
                                    string = ((ASN1BMPString) aSN1Primitive).getString();
                                } else if (aSN1ObjectIdentifier.equals((ASN1Primitive) pkcs_9_at_localKeyId)) {
                                    aSN1OctetString = (ASN1OctetString) aSN1Primitive;
                                }
                            }
                        }
                    }
                    this.chainCerts.put(new CertId(certificateGenerateCertificate.getPublicKey()), certificateGenerateCertificate);
                    if (!zProcessShroudedKeyBag) {
                        if (aSN1OctetString != null) {
                            this.keyCerts.put(new String(Hex.encode(aSN1OctetString.getOctets())), certificateGenerateCertificate);
                        }
                        if (string != null) {
                            this.certs.put(string, certificateGenerateCertificate);
                        }
                    } else if (this.keyCerts.isEmpty()) {
                        String str = new String(Hex.encode(createSubjectKeyId(certificateGenerateCertificate.getPublicKey()).getKeyIdentifier()));
                        this.keyCerts.put(str, certificateGenerateCertificate);
                        this.keys.put(str, this.keys.remove("unmarked"));
                    }
                } catch (Exception e3) {
                    throw new RuntimeException(e3.toString());
                }
            }
            if (z && z2 && cArr != null && cArr.length != 0 && !Properties.isOverrideSet("org.bouncycastle.pkcs12.ignore_useless_passwd")) {
                throw new IOException("password supplied for keystore that does not require one");
            }
        } catch (Exception e4) {
            throw new IOException(e4.getMessage());
        }
    }

    private boolean processShroudedKeyBag(SafeBag safeBag, char[] cArr, boolean z) throws IOException {
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = EncryptedPrivateKeyInfo.getInstance(safeBag.getBagValue());
        PrivateKey privateKeyUnwrapKey = unwrapKey(encryptedPrivateKeyInfo.getEncryptionAlgorithm(), encryptedPrivateKeyInfo.getEncryptedData(), cArr, z);
        String string = null;
        ASN1OctetString aSN1OctetString = null;
        if (safeBag.getBagAttributes() != null) {
            Enumeration objects = safeBag.getBagAttributes().getObjects();
            while (objects.hasMoreElements()) {
                ASN1Sequence aSN1Sequence = (ASN1Sequence) objects.nextElement();
                ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier) aSN1Sequence.getObjectAt(0);
                ASN1Set aSN1Set = (ASN1Set) aSN1Sequence.getObjectAt(1);
                ASN1Primitive aSN1Primitive = null;
                if (aSN1Set.size() > 0) {
                    aSN1Primitive = (ASN1Primitive) aSN1Set.getObjectAt(0);
                    if (privateKeyUnwrapKey instanceof PKCS12BagAttributeCarrier) {
                        PKCS12BagAttributeCarrier pKCS12BagAttributeCarrier = (PKCS12BagAttributeCarrier) privateKeyUnwrapKey;
                        ASN1Encodable bagAttribute = pKCS12BagAttributeCarrier.getBagAttribute(aSN1ObjectIdentifier);
                        if (bagAttribute == null) {
                            pKCS12BagAttributeCarrier.setBagAttribute(aSN1ObjectIdentifier, aSN1Primitive);
                        } else if (!bagAttribute.toASN1Primitive().equals(aSN1Primitive)) {
                            throw new IOException("attempt to add existing attribute with different value");
                        }
                    }
                }
                if (aSN1ObjectIdentifier.equals((ASN1Primitive) pkcs_9_at_friendlyName)) {
                    string = ((ASN1BMPString) aSN1Primitive).getString();
                    this.keys.put(string, privateKeyUnwrapKey);
                } else if (aSN1ObjectIdentifier.equals((ASN1Primitive) pkcs_9_at_localKeyId)) {
                    aSN1OctetString = (ASN1OctetString) aSN1Primitive;
                }
            }
        }
        if (aSN1OctetString == null) {
            this.keys.put("unmarked", privateKeyUnwrapKey);
            return true;
        }
        String str = new String(Hex.encode(aSN1OctetString.getOctets()));
        if (string == null) {
            this.keys.put(str, privateKeyUnwrapKey);
            return false;
        }
        this.localIds.put(string, str);
        return false;
    }

    private void processKeyBag(SafeBag safeBag) throws IOException {
        PrivateKey privateKey = BouncyCastleProvider.getPrivateKey(PrivateKeyInfo.getInstance(safeBag.getBagValue()));
        String string = null;
        ASN1OctetString aSN1OctetString = null;
        if (privateKey instanceof PKCS12BagAttributeCarrier) {
            PKCS12BagAttributeCarrier pKCS12BagAttributeCarrier = (PKCS12BagAttributeCarrier) privateKey;
            Enumeration objects = safeBag.getBagAttributes().getObjects();
            while (objects.hasMoreElements()) {
                ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(objects.nextElement());
                ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
                ASN1Set aSN1Set = ASN1Set.getInstance(aSN1Sequence.getObjectAt(1));
                if (aSN1Set.size() > 0) {
                    ASN1Primitive aSN1Primitive = (ASN1Primitive) aSN1Set.getObjectAt(0);
                    ASN1Encodable bagAttribute = pKCS12BagAttributeCarrier.getBagAttribute(aSN1ObjectIdentifier);
                    if (bagAttribute == null) {
                        pKCS12BagAttributeCarrier.setBagAttribute(aSN1ObjectIdentifier, aSN1Primitive);
                    } else if (!bagAttribute.toASN1Primitive().equals(aSN1Primitive)) {
                        throw new IOException("attempt to add existing attribute with different value");
                    }
                    if (aSN1ObjectIdentifier.equals((ASN1Primitive) pkcs_9_at_friendlyName)) {
                        string = ((ASN1BMPString) aSN1Primitive).getString();
                        this.keys.put(string, privateKey);
                    } else if (aSN1ObjectIdentifier.equals((ASN1Primitive) pkcs_9_at_localKeyId)) {
                        aSN1OctetString = (ASN1OctetString) aSN1Primitive;
                    }
                }
            }
        }
        String str = new String(Hex.encode(aSN1OctetString.getOctets()));
        if (string == null) {
            this.keys.put(str, privateKey);
        } else {
            this.localIds.put(string, str);
        }
    }

    private int validateIterationCount(BigInteger bigInteger) {
        int iIntValueExact = BigIntegers.intValueExact(bigInteger);
        if (iIntValueExact < 0) {
            throw new IllegalStateException("negative iteration count found");
        }
        BigInteger bigIntegerAsBigInteger = Properties.asBigInteger(PKCS12_MAX_IT_COUNT_PROPERTY);
        if (bigIntegerAsBigInteger == null || BigIntegers.intValueExact(bigIntegerAsBigInteger) >= iIntValueExact) {
            return iIntValueExact;
        }
        throw new IllegalStateException("iteration count " + iIntValueExact + " greater than " + BigIntegers.intValueExact(bigIntegerAsBigInteger));
    }

    private ASN1Primitive getAlgParams(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (aSN1ObjectIdentifier.equals((ASN1Primitive) NISTObjectIdentifiers.id_aes128_CBC) || aSN1ObjectIdentifier.equals((ASN1Primitive) NISTObjectIdentifiers.id_aes256_CBC)) {
            byte[] bArr = new byte[16];
            this.random.nextBytes(bArr);
            return new DEROctetString(bArr);
        }
        if (!aSN1ObjectIdentifier.equals((ASN1Primitive) NISTObjectIdentifiers.id_aes128_GCM) && !aSN1ObjectIdentifier.equals((ASN1Primitive) NISTObjectIdentifiers.id_aes256_GCM)) {
            throw new IllegalStateException("unknown encryption OID in getAlgParams()");
        }
        byte[] bArr2 = new byte[12];
        this.random.nextBytes(bArr2);
        return new GCMParameters(bArr2, 16).toASN1Primitive();
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(KeyStore.LoadStoreParameter loadStoreParameter) throws NoSuchAlgorithmException, IOException, CertificateException {
        char[] password;
        if (loadStoreParameter == null) {
            throw new IllegalArgumentException("'param' arg cannot be null");
        }
        if (!(loadStoreParameter instanceof PKCS12StoreParameter) && !(loadStoreParameter instanceof JDKPKCS12StoreParameter)) {
            throw new IllegalArgumentException("No support for 'param' of type " + loadStoreParameter.getClass().getName());
        }
        PKCS12StoreParameter pKCS12StoreParameter = loadStoreParameter instanceof PKCS12StoreParameter ? (PKCS12StoreParameter) loadStoreParameter : new PKCS12StoreParameter(((JDKPKCS12StoreParameter) loadStoreParameter).getOutputStream(), loadStoreParameter.getProtectionParameter(), ((JDKPKCS12StoreParameter) loadStoreParameter).isUseDEREncoding(), ((JDKPKCS12StoreParameter) loadStoreParameter).isOverwriteFriendlyName());
        KeyStore.ProtectionParameter protectionParameter = loadStoreParameter.getProtectionParameter();
        if (protectionParameter == null) {
            password = null;
        } else {
            if (!(protectionParameter instanceof KeyStore.PasswordProtection)) {
                throw new IllegalArgumentException("No support for protection parameter of type " + protectionParameter.getClass().getName());
            }
            password = ((KeyStore.PasswordProtection) protectionParameter).getPassword();
        }
        doStore(pKCS12StoreParameter.getOutputStream(), password, pKCS12StoreParameter.isForDEREncoding(), pKCS12StoreParameter.isOverwriteFriendlyName());
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(OutputStream outputStream, char[] cArr) throws IOException {
        doStore(outputStream, cArr, false, true);
    }

    private void syncFriendlyName() {
        ASN1Encodable bagAttribute;
        ASN1Encodable bagAttribute2;
        ASN1Encodable bagAttribute3;
        Enumeration enumerationKeys = this.keys.keys();
        while (enumerationKeys.hasMoreElements()) {
            String str = (String) enumerationKeys.nextElement();
            PrivateKey privateKey = (PrivateKey) this.keys.get(str);
            if ((privateKey instanceof PKCS12BagAttributeCarrier) && (bagAttribute3 = ((PKCS12BagAttributeCarrier) privateKey).getBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName)) != null && !str.equals(bagAttribute3.toString())) {
                this.keys.put(bagAttribute3.toString(), privateKey);
                this.keys.remove(str);
            }
        }
        Enumeration enumerationKeys2 = this.certs.keys();
        while (enumerationKeys2.hasMoreElements()) {
            String str2 = (String) enumerationKeys2.nextElement();
            Object obj = (Certificate) this.certs.get(str2);
            if ((obj instanceof PKCS12BagAttributeCarrier) && (bagAttribute2 = ((PKCS12BagAttributeCarrier) obj).getBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName)) != null && !str2.equals(bagAttribute2.toString())) {
                this.certs.put(bagAttribute2.toString(), obj);
                this.certs.remove(str2);
            }
        }
        Enumeration enumerationKeys3 = this.keyCerts.keys();
        while (enumerationKeys3.hasMoreElements()) {
            String str3 = (String) enumerationKeys3.nextElement();
            Object obj2 = (Certificate) this.keyCerts.get(str3);
            if ((obj2 instanceof PKCS12BagAttributeCarrier) && (bagAttribute = ((PKCS12BagAttributeCarrier) obj2).getBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName)) != null && !str3.equals(bagAttribute.toString())) {
                this.keyCerts.put(bagAttribute.toString(), obj2);
                this.keyCerts.remove(str3);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void doStore(OutputStream outputStream, char[] cArr, boolean z, boolean z2) throws IOException {
        MacData macData;
        byte[] bArrWrapKey;
        AlgorithmIdentifier algorithmIdentifier;
        if (!z2) {
            syncFriendlyName();
        }
        if (this.keys.size() == 0) {
            if (cArr == null) {
                Enumeration enumerationKeys = this.certs.keys();
                ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
                while (enumerationKeys.hasMoreElements()) {
                    try {
                        String str = (String) enumerationKeys.nextElement();
                        aSN1EncodableVector.add(createSafeBag(str, (Certificate) this.certs.get(str), z2));
                    } catch (CertificateEncodingException e) {
                        throw new IOException("Error encoding certificate: " + e.toString());
                    }
                }
                if (z) {
                    new Pfx(new ContentInfo(PKCSObjectIdentifiers.data, new DEROctetString(new DERSequence(new ContentInfo(PKCSObjectIdentifiers.data, new DEROctetString(new DERSequence(aSN1EncodableVector).getEncoded()))).getEncoded())), null).encodeTo(outputStream, "DER");
                    return;
                } else {
                    new Pfx(new ContentInfo(PKCSObjectIdentifiers.data, new BEROctetString(new BERSequence(new ContentInfo(PKCSObjectIdentifiers.data, new BEROctetString(new BERSequence(aSN1EncodableVector).getEncoded()))).getEncoded())), null).encodeTo(outputStream, "BER");
                    return;
                }
            }
        } else if (cArr == null) {
            throw new NullPointerException("no password supplied for PKCS#12 KeyStore");
        }
        ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
        Enumeration enumerationKeys2 = this.keys.keys();
        while (enumerationKeys2.hasMoreElements()) {
            byte[] bArr = new byte[20];
            this.random.nextBytes(bArr);
            String str2 = (String) enumerationKeys2.nextElement();
            PrivateKey privateKey = (PrivateKey) this.keys.get(str2);
            if (isPBKDF2(this.keyAlgorithm)) {
                PBKDF2Params pBKDF2Params = new PBKDF2Params(bArr, MIN_ITERATIONS, getKeyLength(this.keyAlgorithm), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, DERNull.INSTANCE));
                EncryptionScheme encryptionScheme = new EncryptionScheme(this.keyAlgorithm, getAlgParams(this.keyAlgorithm));
                algorithmIdentifier = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, pBKDF2Params), encryptionScheme));
                bArrWrapKey = wrapKey(encryptionScheme, privateKey, pBKDF2Params, cArr);
            } else {
                PKCS12PBEParams pKCS12PBEParams = new PKCS12PBEParams(bArr, MIN_ITERATIONS);
                bArrWrapKey = wrapKey(this.keyAlgorithm.getId(), privateKey, pKCS12PBEParams, cArr);
                algorithmIdentifier = new AlgorithmIdentifier(this.keyAlgorithm, pKCS12PBEParams.toASN1Primitive());
            }
            EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(algorithmIdentifier, bArrWrapKey);
            boolean z3 = false;
            ASN1EncodableVector aSN1EncodableVector3 = new ASN1EncodableVector();
            if (privateKey instanceof PKCS12BagAttributeCarrier) {
                PKCS12BagAttributeCarrier pKCS12BagAttributeCarrier = (PKCS12BagAttributeCarrier) privateKey;
                ASN1BMPString aSN1BMPString = (ASN1BMPString) pKCS12BagAttributeCarrier.getBagAttribute(pkcs_9_at_friendlyName);
                if (z2 && (aSN1BMPString == null || !aSN1BMPString.getString().equals(str2))) {
                    pKCS12BagAttributeCarrier.setBagAttribute(pkcs_9_at_friendlyName, new DERBMPString(str2));
                }
                if (pKCS12BagAttributeCarrier.getBagAttribute(pkcs_9_at_localKeyId) == null) {
                    pKCS12BagAttributeCarrier.setBagAttribute(pkcs_9_at_localKeyId, createSubjectKeyId(engineGetCertificate(str2).getPublicKey()));
                }
                Enumeration bagAttributeKeys = pKCS12BagAttributeCarrier.getBagAttributeKeys();
                while (bagAttributeKeys.hasMoreElements()) {
                    ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier) bagAttributeKeys.nextElement();
                    ASN1EncodableVector aSN1EncodableVector4 = new ASN1EncodableVector();
                    aSN1EncodableVector4.add(aSN1ObjectIdentifier);
                    aSN1EncodableVector4.add(new DERSet(pKCS12BagAttributeCarrier.getBagAttribute(aSN1ObjectIdentifier)));
                    z3 = true;
                    aSN1EncodableVector3.add(new DERSequence(aSN1EncodableVector4));
                }
            }
            if (!z3) {
                ASN1EncodableVector aSN1EncodableVector5 = new ASN1EncodableVector();
                Certificate certificateEngineGetCertificate = engineGetCertificate(str2);
                aSN1EncodableVector5.add(pkcs_9_at_localKeyId);
                aSN1EncodableVector5.add(new DERSet(createSubjectKeyId(certificateEngineGetCertificate.getPublicKey())));
                aSN1EncodableVector3.add(new DERSequence(aSN1EncodableVector5));
                ASN1EncodableVector aSN1EncodableVector6 = new ASN1EncodableVector();
                aSN1EncodableVector6.add(pkcs_9_at_friendlyName);
                aSN1EncodableVector6.add(new DERSet(new DERBMPString(str2)));
                aSN1EncodableVector3.add(new DERSequence(aSN1EncodableVector6));
            }
            aSN1EncodableVector2.add(new SafeBag(pkcs8ShroudedKeyBag, encryptedPrivateKeyInfo.toASN1Primitive(), new DERSet(aSN1EncodableVector3)));
        }
        BEROctetString bEROctetString = new BEROctetString(new DERSequence(aSN1EncodableVector2).getEncoded("DER"));
        byte[] bArr2 = new byte[20];
        this.random.nextBytes(bArr2);
        ASN1EncodableVector aSN1EncodableVector7 = new ASN1EncodableVector();
        AlgorithmIdentifier algorithmIdentifier2 = isPBKDF2(this.certAlgorithm) ? new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(bArr2, MIN_ITERATIONS, getKeyLength(this.certAlgorithm), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, DERNull.INSTANCE))), new EncryptionScheme(this.certAlgorithm, getAlgParams(this.certAlgorithm)))) : new AlgorithmIdentifier(this.certAlgorithm, new PKCS12PBEParams(bArr2, MIN_ITERATIONS).toASN1Primitive());
        Hashtable hashtable = new Hashtable();
        Enumeration enumerationKeys3 = this.keys.keys();
        while (enumerationKeys3.hasMoreElements()) {
            try {
                String str3 = (String) enumerationKeys3.nextElement();
                Certificate certificateEngineGetCertificate2 = engineGetCertificate(str3);
                boolean z4 = false;
                CertBag certBag = new CertBag(x509Certificate, new DEROctetString(certificateEngineGetCertificate2.getEncoded()));
                ASN1EncodableVector aSN1EncodableVector8 = new ASN1EncodableVector();
                if (certificateEngineGetCertificate2 instanceof PKCS12BagAttributeCarrier) {
                    PKCS12BagAttributeCarrier pKCS12BagAttributeCarrier2 = (PKCS12BagAttributeCarrier) certificateEngineGetCertificate2;
                    ASN1BMPString aSN1BMPString2 = (ASN1BMPString) pKCS12BagAttributeCarrier2.getBagAttribute(pkcs_9_at_friendlyName);
                    if (z2 && (aSN1BMPString2 == null || !aSN1BMPString2.getString().equals(str3))) {
                        pKCS12BagAttributeCarrier2.setBagAttribute(pkcs_9_at_friendlyName, new DERBMPString(str3));
                    }
                    if (pKCS12BagAttributeCarrier2.getBagAttribute(pkcs_9_at_localKeyId) == null) {
                        pKCS12BagAttributeCarrier2.setBagAttribute(pkcs_9_at_localKeyId, createSubjectKeyId(certificateEngineGetCertificate2.getPublicKey()));
                    }
                    Enumeration bagAttributeKeys2 = pKCS12BagAttributeCarrier2.getBagAttributeKeys();
                    while (bagAttributeKeys2.hasMoreElements()) {
                        ASN1ObjectIdentifier aSN1ObjectIdentifier2 = (ASN1ObjectIdentifier) bagAttributeKeys2.nextElement();
                        ASN1EncodableVector aSN1EncodableVector9 = new ASN1EncodableVector();
                        aSN1EncodableVector9.add(aSN1ObjectIdentifier2);
                        aSN1EncodableVector9.add(new DERSet(pKCS12BagAttributeCarrier2.getBagAttribute(aSN1ObjectIdentifier2)));
                        aSN1EncodableVector8.add(new DERSequence(aSN1EncodableVector9));
                        z4 = true;
                    }
                }
                if (!z4) {
                    ASN1EncodableVector aSN1EncodableVector10 = new ASN1EncodableVector();
                    aSN1EncodableVector10.add(pkcs_9_at_localKeyId);
                    aSN1EncodableVector10.add(new DERSet(createSubjectKeyId(certificateEngineGetCertificate2.getPublicKey())));
                    aSN1EncodableVector8.add(new DERSequence(aSN1EncodableVector10));
                    ASN1EncodableVector aSN1EncodableVector11 = new ASN1EncodableVector();
                    aSN1EncodableVector11.add(pkcs_9_at_friendlyName);
                    aSN1EncodableVector11.add(new DERSet(new DERBMPString(str3)));
                    aSN1EncodableVector8.add(new DERSequence(aSN1EncodableVector11));
                }
                aSN1EncodableVector7.add(new SafeBag(certBag, certBag.toASN1Primitive(), new DERSet(aSN1EncodableVector8)));
                hashtable.put(certificateEngineGetCertificate2, certificateEngineGetCertificate2);
            } catch (CertificateEncodingException e2) {
                throw new IOException("Error encoding certificate: " + e2.toString());
            }
        }
        Enumeration enumerationKeys4 = this.certs.keys();
        while (enumerationKeys4.hasMoreElements()) {
            try {
                String str4 = (String) enumerationKeys4.nextElement();
                Certificate certificate = (Certificate) this.certs.get(str4);
                if (this.keys.get(str4) == null) {
                    aSN1EncodableVector7.add(createSafeBag(str4, certificate, z2));
                    hashtable.put(certificate, certificate);
                }
            } catch (CertificateEncodingException e3) {
                throw new IOException("Error encoding certificate: " + e3.toString());
            }
        }
        Set usedCertificateSet = getUsedCertificateSet();
        Enumeration enumerationKeys5 = this.chainCerts.keys();
        while (enumerationKeys5.hasMoreElements()) {
            try {
                Certificate certificate2 = (Certificate) this.chainCerts.get((CertId) enumerationKeys5.nextElement());
                if (usedCertificateSet.contains(certificate2) && hashtable.get(certificate2) == null) {
                    CertBag certBag2 = new CertBag(x509Certificate, new DEROctetString(certificate2.getEncoded()));
                    ASN1EncodableVector aSN1EncodableVector12 = new ASN1EncodableVector();
                    if (certificate2 instanceof PKCS12BagAttributeCarrier) {
                        PKCS12BagAttributeCarrier pKCS12BagAttributeCarrier3 = (PKCS12BagAttributeCarrier) certificate2;
                        Enumeration bagAttributeKeys3 = pKCS12BagAttributeCarrier3.getBagAttributeKeys();
                        while (bagAttributeKeys3.hasMoreElements()) {
                            ASN1ObjectIdentifier aSN1ObjectIdentifier3 = (ASN1ObjectIdentifier) bagAttributeKeys3.nextElement();
                            if (!aSN1ObjectIdentifier3.equals((ASN1Primitive) PKCSObjectIdentifiers.pkcs_9_at_localKeyId)) {
                                ASN1EncodableVector aSN1EncodableVector13 = new ASN1EncodableVector();
                                aSN1EncodableVector13.add(aSN1ObjectIdentifier3);
                                aSN1EncodableVector13.add(new DERSet(pKCS12BagAttributeCarrier3.getBagAttribute(aSN1ObjectIdentifier3)));
                                aSN1EncodableVector12.add(new DERSequence(aSN1EncodableVector13));
                            }
                        }
                    }
                    aSN1EncodableVector7.add(new SafeBag(certBag, certBag2.toASN1Primitive(), new DERSet(aSN1EncodableVector12)));
                }
            } catch (CertificateEncodingException e4) {
                throw new IOException("Error encoding certificate: " + e4.toString());
            }
        }
        ContentInfo contentInfo = new ContentInfo(data, new BEROctetString(new AuthenticatedSafe(new ContentInfo[]{new ContentInfo(data, bEROctetString), new ContentInfo(encryptedData, new EncryptedData(data, algorithmIdentifier2, new BEROctetString(cryptData(true, algorithmIdentifier2, cArr, false, new DERSequence(aSN1EncodableVector7).getEncoded("DER")))).toASN1Primitive())}).getEncoded(z ? "DER" : "BER")));
        byte[] bArr3 = new byte[this.saltLength];
        this.random.nextBytes(bArr3);
        byte[] octets = ((ASN1OctetString) contentInfo.getContent()).getOctets();
        if (this.keyAlgorithm.equals((ASN1Primitive) NISTObjectIdentifiers.id_aes256_GCM)) {
            macData = null;
        } else {
            try {
                macData = new MacData(new DigestInfo(this.macAlgorithm, calculatePbeMac(this.macAlgorithm.getAlgorithm(), bArr3, this.itCount, cArr, false, octets)), bArr3, this.itCount);
            } catch (Exception e5) {
                throw new IOException("error constructing MAC: " + e5.toString());
            }
        }
        new Pfx(contentInfo, macData).encodeTo(outputStream, z ? "DER" : "BER");
    }

    /* JADX WARN: Multi-variable type inference failed */
    private SafeBag createSafeBag(String str, Certificate certificate, boolean z) throws CertificateEncodingException {
        CertBag certBag = new CertBag(x509Certificate, new DEROctetString(certificate.getEncoded()));
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        boolean z2 = false;
        if (certificate instanceof PKCS12BagAttributeCarrier) {
            PKCS12BagAttributeCarrier pKCS12BagAttributeCarrier = (PKCS12BagAttributeCarrier) certificate;
            ASN1BMPString aSN1BMPString = (ASN1BMPString) pKCS12BagAttributeCarrier.getBagAttribute(pkcs_9_at_friendlyName);
            if (z && ((aSN1BMPString == null || !aSN1BMPString.getString().equals(str)) && str != null)) {
                pKCS12BagAttributeCarrier.setBagAttribute(pkcs_9_at_friendlyName, new DERBMPString(str));
            }
            Enumeration bagAttributeKeys = pKCS12BagAttributeCarrier.getBagAttributeKeys();
            while (bagAttributeKeys.hasMoreElements()) {
                ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier) bagAttributeKeys.nextElement();
                if (!aSN1ObjectIdentifier.equals((ASN1Primitive) PKCSObjectIdentifiers.pkcs_9_at_localKeyId) && !aSN1ObjectIdentifier.equals((ASN1Primitive) MiscObjectIdentifiers.id_oracle_pkcs12_trusted_key_usage)) {
                    ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
                    aSN1EncodableVector2.add(aSN1ObjectIdentifier);
                    aSN1EncodableVector2.add(new DERSet(pKCS12BagAttributeCarrier.getBagAttribute(aSN1ObjectIdentifier)));
                    aSN1EncodableVector.add(new DERSequence(aSN1EncodableVector2));
                    z2 = true;
                }
            }
        }
        if (!z2) {
            ASN1EncodableVector aSN1EncodableVector3 = new ASN1EncodableVector();
            aSN1EncodableVector3.add(pkcs_9_at_friendlyName);
            aSN1EncodableVector3.add(new DERSet(new DERBMPString(str)));
            aSN1EncodableVector.add(new DERSequence(aSN1EncodableVector3));
        }
        if (certificate instanceof X509Certificate) {
            Extensions extensions = TBSCertificate.getInstance(((X509Certificate) certificate).getTBSCertificate()).getExtensions();
            if (extensions != null) {
                Extension extension = extensions.getExtension(Extension.extendedKeyUsage);
                if (extension != null) {
                    ASN1EncodableVector aSN1EncodableVector4 = new ASN1EncodableVector();
                    aSN1EncodableVector4.add(MiscObjectIdentifiers.id_oracle_pkcs12_trusted_key_usage);
                    aSN1EncodableVector4.add(new DERSet(ExtendedKeyUsage.getInstance(extension.getParsedValue()).getUsages()));
                    aSN1EncodableVector.add(new DERSequence(aSN1EncodableVector4));
                } else {
                    ASN1EncodableVector aSN1EncodableVector5 = new ASN1EncodableVector();
                    aSN1EncodableVector5.add(MiscObjectIdentifiers.id_oracle_pkcs12_trusted_key_usage);
                    aSN1EncodableVector5.add(new DERSet(KeyPurposeId.anyExtendedKeyUsage));
                    aSN1EncodableVector.add(new DERSequence(aSN1EncodableVector5));
                }
            } else {
                ASN1EncodableVector aSN1EncodableVector6 = new ASN1EncodableVector();
                aSN1EncodableVector6.add(MiscObjectIdentifiers.id_oracle_pkcs12_trusted_key_usage);
                aSN1EncodableVector6.add(new DERSet(KeyPurposeId.anyExtendedKeyUsage));
                aSN1EncodableVector.add(new DERSequence(aSN1EncodableVector6));
            }
        }
        return new SafeBag(certBag, certBag.toASN1Primitive(), new DERSet(aSN1EncodableVector));
    }

    private Set getUsedCertificateSet() {
        HashSet hashSet = new HashSet();
        Enumeration enumerationKeys = this.keys.keys();
        while (enumerationKeys.hasMoreElements()) {
            Certificate[] certificateArrEngineGetCertificateChain = engineGetCertificateChain((String) enumerationKeys.nextElement());
            for (int i = 0; i != certificateArrEngineGetCertificateChain.length; i++) {
                hashSet.add(certificateArrEngineGetCertificateChain[i]);
            }
        }
        Enumeration enumerationKeys2 = this.certs.keys();
        while (enumerationKeys2.hasMoreElements()) {
            hashSet.add(engineGetCertificate((String) enumerationKeys2.nextElement()));
        }
        return hashSet;
    }

    private byte[] calculatePbeMac(ASN1ObjectIdentifier aSN1ObjectIdentifier, byte[] bArr, int i, char[] cArr, boolean z, byte[] bArr2) throws Exception {
        PBEParameterSpec pBEParameterSpec = new PBEParameterSpec(bArr, i);
        Mac macCreateMac = this.helper.createMac(aSN1ObjectIdentifier.getId());
        macCreateMac.init(new PKCS12Key(cArr, z), pBEParameterSpec);
        macCreateMac.update(bArr2);
        return macCreateMac.doFinal();
    }
}
