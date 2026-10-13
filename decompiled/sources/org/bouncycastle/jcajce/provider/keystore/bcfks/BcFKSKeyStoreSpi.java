package org.bouncycastle.jcajce.provider.keystore.bcfks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAKey;
import java.security.interfaces.RSAKey;
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
import javax.crypto.spec.SecretKeySpec;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.bc.EncryptedObjectStoreData;
import org.bouncycastle.asn1.bc.EncryptedPrivateKeyData;
import org.bouncycastle.asn1.bc.EncryptedSecretKeyData;
import org.bouncycastle.asn1.bc.ObjectData;
import org.bouncycastle.asn1.bc.ObjectDataSequence;
import org.bouncycastle.asn1.bc.ObjectStore;
import org.bouncycastle.asn1.bc.ObjectStoreData;
import org.bouncycastle.asn1.bc.ObjectStoreIntegrityCheck;
import org.bouncycastle.asn1.bc.PbkdMacIntegrityCheck;
import org.bouncycastle.asn1.bc.SecretKeyData;
import org.bouncycastle.asn1.bc.SignatureCheck;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.PBKDF2Config;
import org.bouncycastle.crypto.util.PBKDFConfig;
import org.bouncycastle.crypto.util.ScryptConfig;
import org.bouncycastle.internal.asn1.cms.CCMParameters;
import org.bouncycastle.internal.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.ScryptParams;
import org.bouncycastle.internal.asn1.nsri.NSRIObjectIdentifiers;
import org.bouncycastle.internal.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.BCFKSLoadStoreParameter;
import org.bouncycastle.jcajce.BCFKSStoreParameter;
import org.bouncycastle.jcajce.BCLoadStoreParameter;
import org.bouncycastle.jcajce.provider.keystore.util.AdaptingKeyStoreSpi;
import org.bouncycastle.jcajce.provider.keystore.util.ParameterUtil;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/bcfks/BcFKSKeyStoreSpi.class */
class BcFKSKeyStoreSpi extends KeyStoreSpi {
    private static final Map<String, ASN1ObjectIdentifier> oidMap = new HashMap();
    private static final Map<ASN1ObjectIdentifier, String> publicAlgMap = new HashMap();
    private PublicKey verificationKey;
    private BCFKSLoadStoreParameter.CertChainValidator validator;
    private static final BigInteger CERTIFICATE;
    private static final BigInteger PRIVATE_KEY;
    private static final BigInteger SECRET_KEY;
    private static final BigInteger PROTECTED_PRIVATE_KEY;
    private static final BigInteger PROTECTED_SECRET_KEY;
    private final JcaJceHelper helper;
    private AlgorithmIdentifier hmacAlgorithm;
    private KeyDerivationFunc hmacPkbdAlgorithm;
    private AlgorithmIdentifier signatureAlgorithm;
    private Date creationDate;
    private Date lastModifiedDate;
    private final Map<String, ObjectData> entries = new HashMap();
    private final Map<String, PrivateKey> privateKeyCache = new HashMap();
    private ASN1ObjectIdentifier storeEncryptionAlgorithm = NISTObjectIdentifiers.id_aes256_CCM;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/bcfks/BcFKSKeyStoreSpi$Def.class */
    public static class Def extends BcFKSKeyStoreSpi {
        public Def() {
            super(new DefaultJcaJceHelper());
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineLoad(InputStream inputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineLoad(inputStream, cArr);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineLoad(KeyStore.LoadStoreParameter loadStoreParameter) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineLoad(loadStoreParameter);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineStore(OutputStream outputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineStore(outputStream, cArr);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineStore(KeyStore.LoadStoreParameter loadStoreParameter) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineStore(loadStoreParameter);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ String engineGetCertificateAlias(Certificate certificate) {
            return super.engineGetCertificateAlias(certificate);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ boolean engineIsCertificateEntry(String str) {
            return super.engineIsCertificateEntry(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ boolean engineIsKeyEntry(String str) {
            return super.engineIsKeyEntry(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ int engineSize() {
            return super.engineSize();
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ boolean engineContainsAlias(String str) {
            return super.engineContainsAlias(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Enumeration engineAliases() {
            return super.engineAliases();
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineDeleteEntry(String str) throws KeyStoreException {
            super.engineDeleteEntry(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
            super.engineSetCertificateEntry(str, certificate);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
            super.engineSetKeyEntry(str, bArr, certificateArr);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
            super.engineSetKeyEntry(str, key, cArr, certificateArr);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Date engineGetCreationDate(String str) {
            return super.engineGetCreationDate(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Certificate engineGetCertificate(String str) {
            return super.engineGetCertificate(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Certificate[] engineGetCertificateChain(String str) {
            return super.engineGetCertificateChain(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException {
            return super.engineGetKey(str, cArr);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/bcfks/BcFKSKeyStoreSpi$DefCompat.class */
    public static class DefCompat extends AdaptingKeyStoreSpi {
        public DefCompat() {
            super(new DefaultJcaJceHelper(), new BcFKSKeyStoreSpi(new DefaultJcaJceHelper()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/bcfks/BcFKSKeyStoreSpi$DefShared.class */
    public static class DefShared extends SharedKeyStoreSpi {
        public DefShared() {
            super(new DefaultJcaJceHelper());
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi.SharedKeyStoreSpi, org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException {
            return super.engineGetKey(str, cArr);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi.SharedKeyStoreSpi, org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
            super.engineSetCertificateEntry(str, certificate);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi.SharedKeyStoreSpi, org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
            super.engineSetKeyEntry(str, bArr, certificateArr);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi.SharedKeyStoreSpi, org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
            super.engineSetKeyEntry(str, key, cArr, certificateArr);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi.SharedKeyStoreSpi, org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineDeleteEntry(String str) throws KeyStoreException {
            super.engineDeleteEntry(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineLoad(InputStream inputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineLoad(inputStream, cArr);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineLoad(KeyStore.LoadStoreParameter loadStoreParameter) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineLoad(loadStoreParameter);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineStore(OutputStream outputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineStore(outputStream, cArr);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineStore(KeyStore.LoadStoreParameter loadStoreParameter) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineStore(loadStoreParameter);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ String engineGetCertificateAlias(Certificate certificate) {
            return super.engineGetCertificateAlias(certificate);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ boolean engineIsCertificateEntry(String str) {
            return super.engineIsCertificateEntry(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ boolean engineIsKeyEntry(String str) {
            return super.engineIsKeyEntry(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ int engineSize() {
            return super.engineSize();
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ boolean engineContainsAlias(String str) {
            return super.engineContainsAlias(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Enumeration engineAliases() {
            return super.engineAliases();
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Date engineGetCreationDate(String str) {
            return super.engineGetCreationDate(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Certificate engineGetCertificate(String str) {
            return super.engineGetCertificate(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Certificate[] engineGetCertificateChain(String str) {
            return super.engineGetCertificateChain(str);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/bcfks/BcFKSKeyStoreSpi$DefSharedCompat.class */
    public static class DefSharedCompat extends AdaptingKeyStoreSpi {
        public DefSharedCompat() {
            super(new DefaultJcaJceHelper(), new BcFKSKeyStoreSpi(new DefaultJcaJceHelper()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/bcfks/BcFKSKeyStoreSpi$ExtKeyStoreException.class */
    private static class ExtKeyStoreException extends KeyStoreException {
        private final Throwable cause;

        ExtKeyStoreException(String str, Throwable th) {
            super(str);
            this.cause = th;
        }

        @Override // java.lang.Throwable
        public Throwable getCause() {
            return this.cause;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/bcfks/BcFKSKeyStoreSpi$SharedKeyStoreSpi.class */
    private static class SharedKeyStoreSpi extends BcFKSKeyStoreSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
        private final Map<String, byte[]> cache;
        private final byte[] seedKey;

        public SharedKeyStoreSpi(JcaJceHelper jcaJceHelper) {
            super(jcaJceHelper);
            try {
                this.seedKey = new byte[32];
                jcaJceHelper.createSecureRandom("DEFAULT").nextBytes(this.seedKey);
                this.cache = new HashMap();
            } catch (GeneralSecurityException e) {
                throw new IllegalArgumentException("can't create random - " + e.toString());
            }
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public void engineDeleteEntry(String str) throws KeyStoreException {
            throw new KeyStoreException("delete operation not supported in shared mode");
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
            throw new KeyStoreException("set operation not supported in shared mode");
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
            throw new KeyStoreException("set operation not supported in shared mode");
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
            throw new KeyStoreException("set operation not supported in shared mode");
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException {
            try {
                byte[] bArrCalculateMac = calculateMac(str, cArr);
                if (this.cache.containsKey(str) && !Arrays.constantTimeAreEqual(this.cache.get(str), bArrCalculateMac)) {
                    throw new UnrecoverableKeyException("unable to recover key (" + str + ")");
                }
                Key keyEngineGetKey = super.engineGetKey(str, cArr);
                if (keyEngineGetKey != null && !this.cache.containsKey(str)) {
                    this.cache.put(str, bArrCalculateMac);
                }
                return keyEngineGetKey;
            } catch (InvalidKeyException e) {
                throw new UnrecoverableKeyException("unable to recover key (" + str + "): " + e.getMessage());
            }
        }

        private byte[] calculateMac(String str, char[] cArr) throws NoSuchAlgorithmException, InvalidKeyException {
            return SCrypt.generate(cArr != null ? Arrays.concatenate(Strings.toUTF8ByteArray(cArr), Strings.toUTF8ByteArray(str)) : Arrays.concatenate(this.seedKey, Strings.toUTF8ByteArray(str)), this.seedKey, 16384, 8, 1, 32);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/bcfks/BcFKSKeyStoreSpi$Std.class */
    public static class Std extends BcFKSKeyStoreSpi {
        public Std() {
            super(new BCJcaJceHelper());
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineLoad(InputStream inputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineLoad(inputStream, cArr);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineLoad(KeyStore.LoadStoreParameter loadStoreParameter) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineLoad(loadStoreParameter);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineStore(OutputStream outputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineStore(outputStream, cArr);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineStore(KeyStore.LoadStoreParameter loadStoreParameter) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineStore(loadStoreParameter);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ String engineGetCertificateAlias(Certificate certificate) {
            return super.engineGetCertificateAlias(certificate);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ boolean engineIsCertificateEntry(String str) {
            return super.engineIsCertificateEntry(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ boolean engineIsKeyEntry(String str) {
            return super.engineIsKeyEntry(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ int engineSize() {
            return super.engineSize();
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ boolean engineContainsAlias(String str) {
            return super.engineContainsAlias(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Enumeration engineAliases() {
            return super.engineAliases();
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineDeleteEntry(String str) throws KeyStoreException {
            super.engineDeleteEntry(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
            super.engineSetCertificateEntry(str, certificate);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
            super.engineSetKeyEntry(str, bArr, certificateArr);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
            super.engineSetKeyEntry(str, key, cArr, certificateArr);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Date engineGetCreationDate(String str) {
            return super.engineGetCreationDate(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Certificate engineGetCertificate(String str) {
            return super.engineGetCertificate(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Certificate[] engineGetCertificateChain(String str) {
            return super.engineGetCertificateChain(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException {
            return super.engineGetKey(str, cArr);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/bcfks/BcFKSKeyStoreSpi$StdCompat.class */
    public static class StdCompat extends AdaptingKeyStoreSpi {
        public StdCompat() {
            super(new DefaultJcaJceHelper(), new BcFKSKeyStoreSpi(new BCJcaJceHelper()));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/bcfks/BcFKSKeyStoreSpi$StdShared.class */
    public static class StdShared extends SharedKeyStoreSpi {
        public StdShared() {
            super(new BCJcaJceHelper());
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi.SharedKeyStoreSpi, org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException {
            return super.engineGetKey(str, cArr);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi.SharedKeyStoreSpi, org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
            super.engineSetCertificateEntry(str, certificate);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi.SharedKeyStoreSpi, org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
            super.engineSetKeyEntry(str, bArr, certificateArr);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi.SharedKeyStoreSpi, org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
            super.engineSetKeyEntry(str, key, cArr, certificateArr);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi.SharedKeyStoreSpi, org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineDeleteEntry(String str) throws KeyStoreException {
            super.engineDeleteEntry(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineLoad(InputStream inputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineLoad(inputStream, cArr);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineLoad(KeyStore.LoadStoreParameter loadStoreParameter) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineLoad(loadStoreParameter);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineStore(OutputStream outputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineStore(outputStream, cArr);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ void engineStore(KeyStore.LoadStoreParameter loadStoreParameter) throws NoSuchAlgorithmException, IOException, CertificateException {
            super.engineStore(loadStoreParameter);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ String engineGetCertificateAlias(Certificate certificate) {
            return super.engineGetCertificateAlias(certificate);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ boolean engineIsCertificateEntry(String str) {
            return super.engineIsCertificateEntry(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ boolean engineIsKeyEntry(String str) {
            return super.engineIsKeyEntry(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ int engineSize() {
            return super.engineSize();
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ boolean engineContainsAlias(String str) {
            return super.engineContainsAlias(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Enumeration engineAliases() {
            return super.engineAliases();
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Date engineGetCreationDate(String str) {
            return super.engineGetCreationDate(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Certificate engineGetCertificate(String str) {
            return super.engineGetCertificate(str);
        }

        @Override // org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi, java.security.KeyStoreSpi
        public /* bridge */ /* synthetic */ Certificate[] engineGetCertificateChain(String str) {
            return super.engineGetCertificateChain(str);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/bcfks/BcFKSKeyStoreSpi$StdSharedCompat.class */
    public static class StdSharedCompat extends AdaptingKeyStoreSpi {
        public StdSharedCompat() {
            super(new BCJcaJceHelper(), new BcFKSKeyStoreSpi(new BCJcaJceHelper()));
        }
    }

    private static String getPublicKeyAlg(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String str = publicAlgMap.get(aSN1ObjectIdentifier);
        return str != null ? str : aSN1ObjectIdentifier.getId();
    }

    BcFKSKeyStoreSpi(JcaJceHelper jcaJceHelper) {
        this.helper = jcaJceHelper;
    }

    @Override // java.security.KeyStoreSpi
    public Key engineGetKey(String str, char[] cArr) throws UnrecoverableKeyException, NoSuchAlgorithmException {
        ObjectData objectData = this.entries.get(str);
        if (objectData == null) {
            return null;
        }
        if (!objectData.getType().equals(PRIVATE_KEY) && !objectData.getType().equals(PROTECTED_PRIVATE_KEY)) {
            if (!objectData.getType().equals(SECRET_KEY) && !objectData.getType().equals(PROTECTED_SECRET_KEY)) {
                throw new UnrecoverableKeyException("BCFKS KeyStore unable to recover secret key (" + str + "): type not recognized");
            }
            EncryptedSecretKeyData encryptedSecretKeyData = EncryptedSecretKeyData.getInstance(objectData.getData());
            try {
                SecretKeyData secretKeyData = SecretKeyData.getInstance(decryptData("SECRET_KEY_ENCRYPTION", encryptedSecretKeyData.getKeyEncryptionAlgorithm(), cArr, encryptedSecretKeyData.getEncryptedKeyData()));
                return this.helper.createSecretKeyFactory(secretKeyData.getKeyAlgorithm().getId()).generateSecret(new SecretKeySpec(secretKeyData.getKeyBytes(), secretKeyData.getKeyAlgorithm().getId()));
            } catch (Exception e) {
                throw new UnrecoverableKeyException("BCFKS KeyStore unable to recover secret key (" + str + "): " + e.getMessage());
            }
        }
        PrivateKey privateKey = this.privateKeyCache.get(str);
        if (privateKey != null) {
            return privateKey;
        }
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = EncryptedPrivateKeyInfo.getInstance(EncryptedPrivateKeyData.getInstance(objectData.getData()).getEncryptedPrivateKeyInfo());
        try {
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(decryptData("PRIVATE_KEY_ENCRYPTION", encryptedPrivateKeyInfo.getEncryptionAlgorithm(), cArr, encryptedPrivateKeyInfo.getEncryptedData()));
            PrivateKey privateKeyGeneratePrivate = this.helper.createKeyFactory(getPublicKeyAlg(privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm())).generatePrivate(new PKCS8EncodedKeySpec(privateKeyInfo.getEncoded()));
            this.privateKeyCache.put(str, privateKeyGeneratePrivate);
            return privateKeyGeneratePrivate;
        } catch (Exception e2) {
            throw new UnrecoverableKeyException("BCFKS KeyStore unable to recover private key (" + str + "): " + e2.getMessage());
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.security.KeyStoreSpi
    public Certificate[] engineGetCertificateChain(String str) {
        ObjectData objectData = this.entries.get(str);
        if (objectData == null) {
            return null;
        }
        if (!objectData.getType().equals(PRIVATE_KEY) && !objectData.getType().equals(PROTECTED_PRIVATE_KEY)) {
            return null;
        }
        org.bouncycastle.asn1.x509.Certificate[] certificateChain = EncryptedPrivateKeyData.getInstance(objectData.getData()).getCertificateChain();
        X509Certificate[] x509CertificateArr = new X509Certificate[certificateChain.length];
        for (int i = 0; i != x509CertificateArr.length; i++) {
            x509CertificateArr[i] = decodeCertificate(certificateChain[i]);
        }
        return x509CertificateArr;
    }

    @Override // java.security.KeyStoreSpi
    public Certificate engineGetCertificate(String str) {
        ObjectData objectData = this.entries.get(str);
        if (objectData == null) {
            return null;
        }
        if (objectData.getType().equals(PRIVATE_KEY) || objectData.getType().equals(PROTECTED_PRIVATE_KEY)) {
            return decodeCertificate(EncryptedPrivateKeyData.getInstance(objectData.getData()).getCertificateChain()[0]);
        }
        if (objectData.getType().equals(CERTIFICATE)) {
            return decodeCertificate(objectData.getData());
        }
        return null;
    }

    private Certificate decodeCertificate(Object obj) {
        if (this.helper != null) {
            try {
                return this.helper.createCertificateFactory(XMLX509Certificate.JCA_CERT_ID).generateCertificate(new ByteArrayInputStream(org.bouncycastle.asn1.x509.Certificate.getInstance(obj).getEncoded()));
            } catch (Exception e) {
                return null;
            }
        }
        try {
            return CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID).generateCertificate(new ByteArrayInputStream(org.bouncycastle.asn1.x509.Certificate.getInstance(obj).getEncoded()));
        } catch (Exception e2) {
            return null;
        }
    }

    @Override // java.security.KeyStoreSpi
    public Date engineGetCreationDate(String str) {
        ObjectData objectData = this.entries.get(str);
        if (objectData == null) {
            return null;
        }
        try {
            return objectData.getLastModifiedDate().getDate();
        } catch (ParseException e) {
            return new Date();
        }
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
        SecretKeyData secretKeyData;
        EncryptedSecretKeyData encryptedSecretKeyData;
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo;
        Date date = new Date();
        ObjectData objectData = this.entries.get(str);
        if (objectData != null) {
            date = extractCreationDate(objectData, date);
        }
        this.privateKeyCache.remove(str);
        if (key instanceof PrivateKey) {
            if (certificateArr == null) {
                throw new KeyStoreException("BCFKS KeyStore requires a certificate chain for private key storage.");
            }
            try {
                byte[] encoded = key.getEncoded();
                KeyDerivationFunc keyDerivationFuncGeneratePkbdAlgorithmIdentifier = generatePkbdAlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, 32);
                byte[] bArrGenerateKey = generateKey(keyDerivationFuncGeneratePkbdAlgorithmIdentifier, "PRIVATE_KEY_ENCRYPTION", cArr != null ? cArr : new char[0], 32);
                if (this.storeEncryptionAlgorithm.equals((ASN1Primitive) NISTObjectIdentifiers.id_aes256_CCM)) {
                    Cipher cipherCreateCipher = createCipher("AES/CCM/NoPadding", bArrGenerateKey);
                    encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(keyDerivationFuncGeneratePkbdAlgorithmIdentifier, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, CCMParameters.getInstance(cipherCreateCipher.getParameters().getEncoded())))), cipherCreateCipher.doFinal(encoded));
                } else {
                    encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(keyDerivationFuncGeneratePkbdAlgorithmIdentifier, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_wrap_pad))), createCipher("AESKWP", bArrGenerateKey).doFinal(encoded));
                }
                this.entries.put(str, new ObjectData(PRIVATE_KEY, str, date, date, createPrivateKeySequence(encryptedPrivateKeyInfo, certificateArr).getEncoded(), null));
            } catch (Exception e) {
                throw new ExtKeyStoreException("BCFKS KeyStore exception storing private key: " + e.toString(), e);
            }
        } else {
            if (!(key instanceof SecretKey)) {
                throw new KeyStoreException("BCFKS KeyStore unable to recognize key.");
            }
            if (certificateArr != null) {
                throw new KeyStoreException("BCFKS KeyStore cannot store certificate chain with secret key.");
            }
            try {
                byte[] encoded2 = key.getEncoded();
                KeyDerivationFunc keyDerivationFuncGeneratePkbdAlgorithmIdentifier2 = generatePkbdAlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, 32);
                byte[] bArrGenerateKey2 = generateKey(keyDerivationFuncGeneratePkbdAlgorithmIdentifier2, "SECRET_KEY_ENCRYPTION", cArr != null ? cArr : new char[0], 32);
                String upperCase = Strings.toUpperCase(key.getAlgorithm());
                if (upperCase.indexOf("AES") > -1) {
                    secretKeyData = new SecretKeyData(NISTObjectIdentifiers.aes, encoded2);
                } else {
                    ASN1ObjectIdentifier aSN1ObjectIdentifier = oidMap.get(upperCase);
                    if (aSN1ObjectIdentifier != null) {
                        secretKeyData = new SecretKeyData(aSN1ObjectIdentifier, encoded2);
                    } else {
                        ASN1ObjectIdentifier aSN1ObjectIdentifier2 = oidMap.get(upperCase + "." + (encoded2.length * 8));
                        if (aSN1ObjectIdentifier2 == null) {
                            throw new KeyStoreException("BCFKS KeyStore cannot recognize secret key (" + upperCase + ") for storage.");
                        }
                        secretKeyData = new SecretKeyData(aSN1ObjectIdentifier2, encoded2);
                    }
                }
                if (this.storeEncryptionAlgorithm.equals((ASN1Primitive) NISTObjectIdentifiers.id_aes256_CCM)) {
                    Cipher cipherCreateCipher2 = createCipher("AES/CCM/NoPadding", bArrGenerateKey2);
                    encryptedSecretKeyData = new EncryptedSecretKeyData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(keyDerivationFuncGeneratePkbdAlgorithmIdentifier2, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, CCMParameters.getInstance(cipherCreateCipher2.getParameters().getEncoded())))), cipherCreateCipher2.doFinal(secretKeyData.getEncoded()));
                } else {
                    encryptedSecretKeyData = new EncryptedSecretKeyData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(keyDerivationFuncGeneratePkbdAlgorithmIdentifier2, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_wrap_pad))), createCipher("AESKWP", bArrGenerateKey2).doFinal(secretKeyData.getEncoded()));
                }
                this.entries.put(str, new ObjectData(SECRET_KEY, str, date, date, encryptedSecretKeyData.getEncoded(), null));
            } catch (Exception e2) {
                throw new ExtKeyStoreException("BCFKS KeyStore exception storing private key: " + e2.toString(), e2);
            }
        }
        this.lastModifiedDate = date;
    }

    private Cipher createCipher(String str, byte[] bArr) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        Cipher cipherCreateCipher = this.helper.createCipher(str);
        cipherCreateCipher.init(1, new SecretKeySpec(bArr, "AES"));
        return cipherCreateCipher;
    }

    private SecureRandom getDefaultSecureRandom() {
        return CryptoServicesRegistrar.getSecureRandom();
    }

    private EncryptedPrivateKeyData createPrivateKeySequence(EncryptedPrivateKeyInfo encryptedPrivateKeyInfo, Certificate[] certificateArr) throws CertificateEncodingException {
        org.bouncycastle.asn1.x509.Certificate[] certificateArr2 = new org.bouncycastle.asn1.x509.Certificate[certificateArr.length];
        for (int i = 0; i != certificateArr.length; i++) {
            certificateArr2[i] = org.bouncycastle.asn1.x509.Certificate.getInstance(certificateArr[i].getEncoded());
        }
        return new EncryptedPrivateKeyData(encryptedPrivateKeyInfo, certificateArr2);
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
        Date date = new Date();
        ObjectData objectData = this.entries.get(str);
        if (objectData != null) {
            date = extractCreationDate(objectData, date);
        }
        if (certificateArr != null) {
            try {
                EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = EncryptedPrivateKeyInfo.getInstance(bArr);
                try {
                    this.privateKeyCache.remove(str);
                    this.entries.put(str, new ObjectData(PROTECTED_PRIVATE_KEY, str, date, date, createPrivateKeySequence(encryptedPrivateKeyInfo, certificateArr).getEncoded(), null));
                } catch (Exception e) {
                    throw new ExtKeyStoreException("BCFKS KeyStore exception storing protected private key: " + e.toString(), e);
                }
            } catch (Exception e2) {
                throw new ExtKeyStoreException("BCFKS KeyStore private key encoding must be an EncryptedPrivateKeyInfo.", e2);
            }
        } else {
            try {
                this.entries.put(str, new ObjectData(PROTECTED_SECRET_KEY, str, date, date, bArr, null));
            } catch (Exception e3) {
                throw new ExtKeyStoreException("BCFKS KeyStore exception storing protected private key: " + e3.toString(), e3);
            }
        }
        this.lastModifiedDate = date;
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
        ObjectData objectData = this.entries.get(str);
        Date date = new Date();
        if (objectData != null) {
            if (!objectData.getType().equals(CERTIFICATE)) {
                throw new KeyStoreException("BCFKS KeyStore already has a key entry with alias " + str);
            }
            date = extractCreationDate(objectData, date);
        }
        try {
            this.entries.put(str, new ObjectData(CERTIFICATE, str, date, date, certificate.getEncoded(), null));
            this.lastModifiedDate = date;
        } catch (CertificateEncodingException e) {
            throw new ExtKeyStoreException("BCFKS KeyStore unable to handle certificate: " + e.getMessage(), e);
        }
    }

    private Date extractCreationDate(ObjectData objectData, Date date) {
        try {
            date = objectData.getCreationDate().getDate();
        } catch (ParseException e) {
        }
        return date;
    }

    @Override // java.security.KeyStoreSpi
    public void engineDeleteEntry(String str) throws KeyStoreException {
        if (this.entries.get(str) == null) {
            return;
        }
        this.privateKeyCache.remove(str);
        this.entries.remove(str);
        this.lastModifiedDate = new Date();
    }

    @Override // java.security.KeyStoreSpi
    public Enumeration<String> engineAliases() {
        final Iterator it = new HashSet(this.entries.keySet()).iterator();
        return new Enumeration() { // from class: org.bouncycastle.jcajce.provider.keystore.bcfks.BcFKSKeyStoreSpi.1
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
    public boolean engineContainsAlias(String str) {
        if (str == null) {
            throw new NullPointerException("alias value is null");
        }
        return this.entries.containsKey(str);
    }

    @Override // java.security.KeyStoreSpi
    public int engineSize() {
        return this.entries.size();
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsKeyEntry(String str) {
        ObjectData objectData = this.entries.get(str);
        if (objectData == null) {
            return false;
        }
        BigInteger type = objectData.getType();
        return type.equals(PRIVATE_KEY) || type.equals(SECRET_KEY) || type.equals(PROTECTED_PRIVATE_KEY) || type.equals(PROTECTED_SECRET_KEY);
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsCertificateEntry(String str) {
        ObjectData objectData = this.entries.get(str);
        if (objectData != null) {
            return objectData.getType().equals(CERTIFICATE);
        }
        return false;
    }

    @Override // java.security.KeyStoreSpi
    public String engineGetCertificateAlias(Certificate certificate) {
        if (certificate == null) {
            return null;
        }
        try {
            byte[] encoded = certificate.getEncoded();
            for (String str : this.entries.keySet()) {
                ObjectData objectData = this.entries.get(str);
                if (objectData.getType().equals(CERTIFICATE)) {
                    if (Arrays.areEqual(objectData.getData(), encoded)) {
                        return str;
                    }
                } else if (objectData.getType().equals(PRIVATE_KEY) || objectData.getType().equals(PROTECTED_PRIVATE_KEY)) {
                    try {
                        if (Arrays.areEqual(EncryptedPrivateKeyData.getInstance(objectData.getData()).getCertificateChain()[0].toASN1Primitive().getEncoded(), encoded)) {
                            return str;
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

    private byte[] generateKey(KeyDerivationFunc keyDerivationFunc, String str, char[] cArr, int i) throws IOException {
        byte[] bArrPKCS12PasswordToBytes = PBEParametersGenerator.PKCS12PasswordToBytes(cArr);
        byte[] bArrPKCS12PasswordToBytes2 = PBEParametersGenerator.PKCS12PasswordToBytes(str.toCharArray());
        int iIntValue = i;
        if (MiscObjectIdentifiers.id_scrypt.equals((ASN1Primitive) keyDerivationFunc.getAlgorithm())) {
            ScryptParams scryptParams = ScryptParams.getInstance(keyDerivationFunc.getParameters());
            if (scryptParams.getKeyLength() != null) {
                iIntValue = scryptParams.getKeyLength().intValue();
            } else if (iIntValue == -1) {
                throw new IOException("no keyLength found in ScryptParams");
            }
            return SCrypt.generate(Arrays.concatenate(bArrPKCS12PasswordToBytes, bArrPKCS12PasswordToBytes2), scryptParams.getSalt(), scryptParams.getCostParameter().intValue(), scryptParams.getBlockSize().intValue(), scryptParams.getBlockSize().intValue(), iIntValue);
        }
        if (!keyDerivationFunc.getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.id_PBKDF2)) {
            throw new IOException("BCFKS KeyStore: unrecognized MAC PBKD.");
        }
        PBKDF2Params pBKDF2Params = PBKDF2Params.getInstance(keyDerivationFunc.getParameters());
        if (pBKDF2Params.getKeyLength() != null) {
            iIntValue = pBKDF2Params.getKeyLength().intValue();
        } else if (iIntValue == -1) {
            throw new IOException("no keyLength found in PBKDF2Params");
        }
        if (pBKDF2Params.getPrf().getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.id_hmacWithSHA512)) {
            PKCS5S2ParametersGenerator pKCS5S2ParametersGenerator = new PKCS5S2ParametersGenerator(new SHA512Digest());
            pKCS5S2ParametersGenerator.init(Arrays.concatenate(bArrPKCS12PasswordToBytes, bArrPKCS12PasswordToBytes2), pBKDF2Params.getSalt(), pBKDF2Params.getIterationCount().intValue());
            return ((KeyParameter) pKCS5S2ParametersGenerator.generateDerivedParameters(iIntValue * 8)).getKey();
        }
        if (!pBKDF2Params.getPrf().getAlgorithm().equals((ASN1Primitive) NISTObjectIdentifiers.id_hmacWithSHA3_512)) {
            throw new IOException("BCFKS KeyStore: unrecognized MAC PBKD PRF: " + pBKDF2Params.getPrf().getAlgorithm());
        }
        PKCS5S2ParametersGenerator pKCS5S2ParametersGenerator2 = new PKCS5S2ParametersGenerator(new SHA3Digest(512));
        pKCS5S2ParametersGenerator2.init(Arrays.concatenate(bArrPKCS12PasswordToBytes, bArrPKCS12PasswordToBytes2), pBKDF2Params.getSalt(), pBKDF2Params.getIterationCount().intValue());
        return ((KeyParameter) pKCS5S2ParametersGenerator2.generateDerivedParameters(iIntValue * 8)).getKey();
    }

    private void verifySig(ASN1Encodable aSN1Encodable, SignatureCheck signatureCheck, PublicKey publicKey) throws GeneralSecurityException, IOException {
        Signature signatureCreateSignature = this.helper.createSignature(signatureCheck.getSignatureAlgorithm().getAlgorithm().getId());
        signatureCreateSignature.initVerify(publicKey);
        signatureCreateSignature.update(aSN1Encodable.toASN1Primitive().getEncoded("DER"));
        if (!signatureCreateSignature.verify(signatureCheck.getSignature().getOctets())) {
            throw new IOException("BCFKS KeyStore corrupted: signature calculation failed");
        }
    }

    private void verifyMac(byte[] bArr, PbkdMacIntegrityCheck pbkdMacIntegrityCheck, char[] cArr) throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        if (!Arrays.constantTimeAreEqual(calculateMac(bArr, pbkdMacIntegrityCheck.getMacAlgorithm(), pbkdMacIntegrityCheck.getPbkdAlgorithm(), cArr), pbkdMacIntegrityCheck.getMac())) {
            throw new IOException("BCFKS KeyStore corrupted: MAC calculation failed");
        }
    }

    private byte[] calculateMac(byte[] bArr, AlgorithmIdentifier algorithmIdentifier, KeyDerivationFunc keyDerivationFunc, char[] cArr) throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        String id = algorithmIdentifier.getAlgorithm().getId();
        Mac macCreateMac = this.helper.createMac(id);
        try {
            macCreateMac.init(new SecretKeySpec(generateKey(keyDerivationFunc, "INTEGRITY_CHECK", cArr != null ? cArr : new char[0], -1), id));
            return macCreateMac.doFinal(bArr);
        } catch (InvalidKeyException e) {
            throw new IOException("Cannot set up MAC calculation: " + e.getMessage());
        }
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(KeyStore.LoadStoreParameter loadStoreParameter) throws NoSuchAlgorithmException, IOException, CertificateException {
        SignatureCheck signatureCheck;
        if (loadStoreParameter == null) {
            throw new IllegalArgumentException("'parameter' arg cannot be null");
        }
        if (loadStoreParameter instanceof BCFKSStoreParameter) {
            BCFKSStoreParameter bCFKSStoreParameter = (BCFKSStoreParameter) loadStoreParameter;
            char[] cArrExtractPassword = ParameterUtil.extractPassword(loadStoreParameter);
            this.hmacPkbdAlgorithm = generatePkbdAlgorithmIdentifier(bCFKSStoreParameter.getStorePBKDFConfig(), 64);
            engineStore(bCFKSStoreParameter.getOutputStream(), cArrExtractPassword);
            return;
        }
        if (!(loadStoreParameter instanceof BCFKSLoadStoreParameter)) {
            if (!(loadStoreParameter instanceof BCLoadStoreParameter)) {
                throw new IllegalArgumentException("no support for 'parameter' of type " + loadStoreParameter.getClass().getName());
            }
            engineStore(((BCLoadStoreParameter) loadStoreParameter).getOutputStream(), ParameterUtil.extractPassword(loadStoreParameter));
            return;
        }
        BCFKSLoadStoreParameter bCFKSLoadStoreParameter = (BCFKSLoadStoreParameter) loadStoreParameter;
        if (bCFKSLoadStoreParameter.getStoreSignatureKey() == null) {
            char[] cArrExtractPassword2 = ParameterUtil.extractPassword(bCFKSLoadStoreParameter);
            this.hmacPkbdAlgorithm = generatePkbdAlgorithmIdentifier(bCFKSLoadStoreParameter.getStorePBKDFConfig(), 64);
            if (bCFKSLoadStoreParameter.getStoreEncryptionAlgorithm() == BCFKSLoadStoreParameter.EncryptionAlgorithm.AES256_CCM) {
                this.storeEncryptionAlgorithm = NISTObjectIdentifiers.id_aes256_CCM;
            } else {
                this.storeEncryptionAlgorithm = NISTObjectIdentifiers.id_aes256_wrap_pad;
            }
            if (bCFKSLoadStoreParameter.getStoreMacAlgorithm() == BCFKSLoadStoreParameter.MacAlgorithm.HmacSHA512) {
                this.hmacAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE);
            } else {
                this.hmacAlgorithm = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512, DERNull.INSTANCE);
            }
            engineStore(bCFKSLoadStoreParameter.getOutputStream(), cArrExtractPassword2);
            return;
        }
        this.signatureAlgorithm = generateSignatureAlgId(bCFKSLoadStoreParameter.getStoreSignatureKey(), bCFKSLoadStoreParameter.getStoreSignatureAlgorithm());
        this.hmacPkbdAlgorithm = generatePkbdAlgorithmIdentifier(bCFKSLoadStoreParameter.getStorePBKDFConfig(), 64);
        if (bCFKSLoadStoreParameter.getStoreEncryptionAlgorithm() == BCFKSLoadStoreParameter.EncryptionAlgorithm.AES256_CCM) {
            this.storeEncryptionAlgorithm = NISTObjectIdentifiers.id_aes256_CCM;
        } else {
            this.storeEncryptionAlgorithm = NISTObjectIdentifiers.id_aes256_wrap_pad;
        }
        if (bCFKSLoadStoreParameter.getStoreMacAlgorithm() == BCFKSLoadStoreParameter.MacAlgorithm.HmacSHA512) {
            this.hmacAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE);
        } else {
            this.hmacAlgorithm = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512, DERNull.INSTANCE);
        }
        EncryptedObjectStoreData encryptedObjectStoreData = getEncryptedObjectStoreData(this.signatureAlgorithm, ParameterUtil.extractPassword(bCFKSLoadStoreParameter));
        try {
            Signature signatureCreateSignature = this.helper.createSignature(this.signatureAlgorithm.getAlgorithm().getId());
            signatureCreateSignature.initSign((PrivateKey) bCFKSLoadStoreParameter.getStoreSignatureKey());
            signatureCreateSignature.update(encryptedObjectStoreData.getEncoded());
            X509Certificate[] storeCertificates = bCFKSLoadStoreParameter.getStoreCertificates();
            if (storeCertificates != null) {
                org.bouncycastle.asn1.x509.Certificate[] certificateArr = new org.bouncycastle.asn1.x509.Certificate[storeCertificates.length];
                for (int i = 0; i != certificateArr.length; i++) {
                    certificateArr[i] = org.bouncycastle.asn1.x509.Certificate.getInstance(storeCertificates[i].getEncoded());
                }
                signatureCheck = new SignatureCheck(this.signatureAlgorithm, certificateArr, signatureCreateSignature.sign());
            } else {
                signatureCheck = new SignatureCheck(this.signatureAlgorithm, signatureCreateSignature.sign());
            }
            bCFKSLoadStoreParameter.getOutputStream().write(new ObjectStore(encryptedObjectStoreData, new ObjectStoreIntegrityCheck(signatureCheck)).getEncoded());
            bCFKSLoadStoreParameter.getOutputStream().flush();
        } catch (GeneralSecurityException e) {
            throw new IOException("error creating signature: " + e.getMessage(), e);
        }
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(OutputStream outputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
        if (this.creationDate == null) {
            throw new IOException("KeyStore not initialized");
        }
        EncryptedObjectStoreData encryptedObjectStoreData = getEncryptedObjectStoreData(this.hmacAlgorithm, cArr);
        if (MiscObjectIdentifiers.id_scrypt.equals((ASN1Primitive) this.hmacPkbdAlgorithm.getAlgorithm())) {
            this.hmacPkbdAlgorithm = generatePkbdAlgorithmIdentifier(this.hmacPkbdAlgorithm, ScryptParams.getInstance(this.hmacPkbdAlgorithm.getParameters()).getKeyLength().intValue());
        } else {
            this.hmacPkbdAlgorithm = generatePkbdAlgorithmIdentifier(this.hmacPkbdAlgorithm, PBKDF2Params.getInstance(this.hmacPkbdAlgorithm.getParameters()).getKeyLength().intValue());
        }
        try {
            outputStream.write(new ObjectStore(encryptedObjectStoreData, new ObjectStoreIntegrityCheck(new PbkdMacIntegrityCheck(this.hmacAlgorithm, this.hmacPkbdAlgorithm, calculateMac(encryptedObjectStoreData.getEncoded(), this.hmacAlgorithm, this.hmacPkbdAlgorithm, cArr)))).getEncoded());
            outputStream.flush();
        } catch (NoSuchProviderException e) {
            throw new IOException("cannot calculate mac: " + e.getMessage());
        }
    }

    private EncryptedObjectStoreData getEncryptedObjectStoreData(AlgorithmIdentifier algorithmIdentifier, char[] cArr) throws NoSuchAlgorithmException, IOException {
        EncryptedObjectStoreData encryptedObjectStoreData;
        ObjectData[] objectDataArr = (ObjectData[]) this.entries.values().toArray(new ObjectData[this.entries.size()]);
        KeyDerivationFunc keyDerivationFuncGeneratePkbdAlgorithmIdentifier = generatePkbdAlgorithmIdentifier(this.hmacPkbdAlgorithm, 32);
        byte[] bArrGenerateKey = generateKey(keyDerivationFuncGeneratePkbdAlgorithmIdentifier, "STORE_ENCRYPTION", cArr != null ? cArr : new char[0], 32);
        ObjectStoreData objectStoreData = new ObjectStoreData(algorithmIdentifier, this.creationDate, this.lastModifiedDate, new ObjectDataSequence(objectDataArr), null);
        try {
            if (this.storeEncryptionAlgorithm.equals((ASN1Primitive) NISTObjectIdentifiers.id_aes256_CCM)) {
                Cipher cipherCreateCipher = createCipher("AES/CCM/NoPadding", bArrGenerateKey);
                encryptedObjectStoreData = new EncryptedObjectStoreData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(keyDerivationFuncGeneratePkbdAlgorithmIdentifier, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, CCMParameters.getInstance(cipherCreateCipher.getParameters().getEncoded())))), cipherCreateCipher.doFinal(objectStoreData.getEncoded()));
            } else {
                encryptedObjectStoreData = new EncryptedObjectStoreData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(keyDerivationFuncGeneratePkbdAlgorithmIdentifier, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_wrap_pad))), createCipher("AESKWP", bArrGenerateKey).doFinal(objectStoreData.getEncoded()));
            }
            return encryptedObjectStoreData;
        } catch (InvalidKeyException e) {
            throw new IOException(e.toString());
        } catch (NoSuchProviderException e2) {
            throw new IOException(e2.toString());
        } catch (BadPaddingException e3) {
            throw new IOException(e3.toString());
        } catch (IllegalBlockSizeException e4) {
            throw new IOException(e4.toString());
        } catch (NoSuchPaddingException e5) {
            throw new NoSuchAlgorithmException(e5.toString());
        }
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(KeyStore.LoadStoreParameter loadStoreParameter) throws NoSuchAlgorithmException, IOException, CertificateException {
        if (loadStoreParameter == null) {
            engineLoad(null, null);
            return;
        }
        if (!(loadStoreParameter instanceof BCFKSLoadStoreParameter)) {
            if (!(loadStoreParameter instanceof BCLoadStoreParameter)) {
                throw new IllegalArgumentException("no support for 'parameter' of type " + loadStoreParameter.getClass().getName());
            }
            engineLoad(((BCLoadStoreParameter) loadStoreParameter).getInputStream(), ParameterUtil.extractPassword(loadStoreParameter));
            return;
        }
        BCFKSLoadStoreParameter bCFKSLoadStoreParameter = (BCFKSLoadStoreParameter) loadStoreParameter;
        char[] cArrExtractPassword = ParameterUtil.extractPassword(bCFKSLoadStoreParameter);
        this.hmacPkbdAlgorithm = generatePkbdAlgorithmIdentifier(bCFKSLoadStoreParameter.getStorePBKDFConfig(), 64);
        if (bCFKSLoadStoreParameter.getStoreEncryptionAlgorithm() == BCFKSLoadStoreParameter.EncryptionAlgorithm.AES256_CCM) {
            this.storeEncryptionAlgorithm = NISTObjectIdentifiers.id_aes256_CCM;
        } else {
            this.storeEncryptionAlgorithm = NISTObjectIdentifiers.id_aes256_wrap_pad;
        }
        if (bCFKSLoadStoreParameter.getStoreMacAlgorithm() == BCFKSLoadStoreParameter.MacAlgorithm.HmacSHA512) {
            this.hmacAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE);
        } else {
            this.hmacAlgorithm = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512, DERNull.INSTANCE);
        }
        this.verificationKey = (PublicKey) bCFKSLoadStoreParameter.getStoreSignatureKey();
        this.validator = bCFKSLoadStoreParameter.getCertChainValidator();
        this.signatureAlgorithm = generateSignatureAlgId(this.verificationKey, bCFKSLoadStoreParameter.getStoreSignatureAlgorithm());
        AlgorithmIdentifier algorithmIdentifier = this.hmacAlgorithm;
        ASN1ObjectIdentifier aSN1ObjectIdentifier = this.storeEncryptionAlgorithm;
        InputStream inputStream = bCFKSLoadStoreParameter.getInputStream();
        engineLoad(inputStream, cArrExtractPassword);
        if (inputStream != null) {
            if (!isSimilarHmacPbkd(bCFKSLoadStoreParameter.getStorePBKDFConfig(), this.hmacPkbdAlgorithm) || !aSN1ObjectIdentifier.equals((ASN1Primitive) this.storeEncryptionAlgorithm)) {
                throw new IOException("configuration parameters do not match existing store");
            }
        }
    }

    private boolean isSimilarHmacPbkd(PBKDFConfig pBKDFConfig, KeyDerivationFunc keyDerivationFunc) {
        if (!pBKDFConfig.getAlgorithm().equals((ASN1Primitive) keyDerivationFunc.getAlgorithm())) {
            return false;
        }
        if (MiscObjectIdentifiers.id_scrypt.equals((ASN1Primitive) keyDerivationFunc.getAlgorithm())) {
            if (!(pBKDFConfig instanceof ScryptConfig)) {
                return false;
            }
            ScryptConfig scryptConfig = (ScryptConfig) pBKDFConfig;
            ScryptParams scryptParams = ScryptParams.getInstance(keyDerivationFunc.getParameters());
            return scryptConfig.getSaltLength() == scryptParams.getSalt().length && scryptConfig.getBlockSize() == scryptParams.getBlockSize().intValue() && scryptConfig.getCostParameter() == scryptParams.getCostParameter().intValue() && scryptConfig.getParallelizationParameter() == scryptParams.getParallelizationParameter().intValue();
        }
        if (!(pBKDFConfig instanceof PBKDF2Config)) {
            return false;
        }
        PBKDF2Config pBKDF2Config = (PBKDF2Config) pBKDFConfig;
        PBKDF2Params pBKDF2Params = PBKDF2Params.getInstance(keyDerivationFunc.getParameters());
        return pBKDF2Config.getSaltLength() == pBKDF2Params.getSalt().length && pBKDF2Config.getIterationCount() == pBKDF2Params.getIterationCount().intValue();
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(InputStream inputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
        AlgorithmIdentifier signatureAlgorithm;
        ObjectStoreData objectStoreData;
        this.entries.clear();
        this.privateKeyCache.clear();
        this.creationDate = null;
        this.lastModifiedDate = null;
        this.hmacAlgorithm = null;
        if (inputStream == null) {
            Date date = new Date();
            this.creationDate = date;
            this.lastModifiedDate = date;
            this.verificationKey = null;
            this.validator = null;
            this.hmacAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE);
            this.hmacPkbdAlgorithm = generatePkbdAlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, 64);
            return;
        }
        try {
            ObjectStore objectStore = ObjectStore.getInstance(new ASN1InputStream(inputStream).readObject());
            ObjectStoreIntegrityCheck integrityCheck = objectStore.getIntegrityCheck();
            if (integrityCheck.getType() == 0) {
                PbkdMacIntegrityCheck pbkdMacIntegrityCheck = PbkdMacIntegrityCheck.getInstance(integrityCheck.getIntegrityCheck());
                this.hmacAlgorithm = pbkdMacIntegrityCheck.getMacAlgorithm();
                this.hmacPkbdAlgorithm = pbkdMacIntegrityCheck.getPbkdAlgorithm();
                signatureAlgorithm = this.hmacAlgorithm;
                try {
                    verifyMac(objectStore.getStoreData().toASN1Primitive().getEncoded(), pbkdMacIntegrityCheck, cArr);
                } catch (NoSuchProviderException e) {
                    throw new IOException(e.getMessage());
                }
            } else {
                if (integrityCheck.getType() != 1) {
                    throw new IOException("BCFKS KeyStore unable to recognize integrity check.");
                }
                SignatureCheck signatureCheck = SignatureCheck.getInstance(integrityCheck.getIntegrityCheck());
                signatureAlgorithm = signatureCheck.getSignatureAlgorithm();
                try {
                    org.bouncycastle.asn1.x509.Certificate[] certificates = signatureCheck.getCertificates();
                    if (this.validator == null) {
                        verifySig(objectStore.getStoreData(), signatureCheck, this.verificationKey);
                    } else {
                        if (certificates == null) {
                            throw new IOException("validator specified but no certifcates in store");
                        }
                        CertificateFactory certificateFactoryCreateCertificateFactory = this.helper.createCertificateFactory(XMLX509Certificate.JCA_CERT_ID);
                        X509Certificate[] x509CertificateArr = new X509Certificate[certificates.length];
                        for (int i = 0; i != x509CertificateArr.length; i++) {
                            x509CertificateArr[i] = (X509Certificate) certificateFactoryCreateCertificateFactory.generateCertificate(new ByteArrayInputStream(certificates[i].getEncoded()));
                        }
                        if (!this.validator.isValid(x509CertificateArr)) {
                            throw new IOException("certificate chain in key store signature not valid");
                        }
                        verifySig(objectStore.getStoreData(), signatureCheck, x509CertificateArr[0].getPublicKey());
                    }
                } catch (GeneralSecurityException e2) {
                    throw new IOException("error verifying signature: " + e2.getMessage(), e2);
                }
            }
            ASN1Encodable storeData = objectStore.getStoreData();
            if (storeData instanceof EncryptedObjectStoreData) {
                EncryptedObjectStoreData encryptedObjectStoreData = (EncryptedObjectStoreData) storeData;
                objectStoreData = ObjectStoreData.getInstance(decryptData("STORE_ENCRYPTION", encryptedObjectStoreData.getEncryptionAlgorithm(), cArr, encryptedObjectStoreData.getEncryptedContent().getOctets()));
            } else {
                objectStoreData = ObjectStoreData.getInstance(storeData);
            }
            try {
                this.creationDate = objectStoreData.getCreationDate().getDate();
                this.lastModifiedDate = objectStoreData.getLastModifiedDate().getDate();
                if (!objectStoreData.getIntegrityAlgorithm().equals(signatureAlgorithm)) {
                    throw new IOException("BCFKS KeyStore storeData integrity algorithm does not match store integrity algorithm.");
                }
                Iterator<ASN1Encodable> it = objectStoreData.getObjectDataSequence().iterator();
                while (it.hasNext()) {
                    ObjectData objectData = ObjectData.getInstance(it.next());
                    this.entries.put(objectData.getIdentifier(), objectData);
                }
            } catch (ParseException e3) {
                throw new IOException("BCFKS KeyStore unable to parse store data information.");
            }
        } catch (Exception e4) {
            throw new IOException(e4.getMessage());
        }
    }

    private byte[] decryptData(String str, AlgorithmIdentifier algorithmIdentifier, char[] cArr, byte[] bArr) throws IOException {
        Cipher cipherCreateCipher;
        AlgorithmParameters algorithmParametersCreateAlgorithmParameters;
        if (!algorithmIdentifier.getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.id_PBES2)) {
            throw new IOException("BCFKS KeyStore cannot recognize protection algorithm.");
        }
        PBES2Parameters pBES2Parameters = PBES2Parameters.getInstance(algorithmIdentifier.getParameters());
        EncryptionScheme encryptionScheme = pBES2Parameters.getEncryptionScheme();
        try {
            if (encryptionScheme.getAlgorithm().equals((ASN1Primitive) NISTObjectIdentifiers.id_aes256_CCM)) {
                cipherCreateCipher = this.helper.createCipher("AES/CCM/NoPadding");
                algorithmParametersCreateAlgorithmParameters = this.helper.createAlgorithmParameters("CCM");
                algorithmParametersCreateAlgorithmParameters.init(CCMParameters.getInstance(encryptionScheme.getParameters()).getEncoded());
            } else {
                if (!encryptionScheme.getAlgorithm().equals((ASN1Primitive) NISTObjectIdentifiers.id_aes256_wrap_pad)) {
                    throw new IOException("BCFKS KeyStore cannot recognize protection encryption algorithm.");
                }
                cipherCreateCipher = this.helper.createCipher("AESKWP");
                algorithmParametersCreateAlgorithmParameters = null;
            }
            cipherCreateCipher.init(2, new SecretKeySpec(generateKey(pBES2Parameters.getKeyDerivationFunc(), str, cArr != null ? cArr : new char[0], 32), "AES"), algorithmParametersCreateAlgorithmParameters);
            return cipherCreateCipher.doFinal(bArr);
        } catch (IOException e) {
            throw e;
        } catch (Exception e2) {
            throw new IOException(e2.toString());
        }
    }

    private AlgorithmIdentifier generateSignatureAlgId(Key key, BCFKSLoadStoreParameter.SignatureAlgorithm signatureAlgorithm) throws IOException {
        if (key == null) {
            return null;
        }
        if (key instanceof ECKey) {
            if (signatureAlgorithm == BCFKSLoadStoreParameter.SignatureAlgorithm.SHA512withECDSA) {
                return new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA512);
            }
            if (signatureAlgorithm == BCFKSLoadStoreParameter.SignatureAlgorithm.SHA3_512withECDSA) {
                return new AlgorithmIdentifier(NISTObjectIdentifiers.id_ecdsa_with_sha3_512);
            }
        }
        if (key instanceof DSAKey) {
            if (signatureAlgorithm == BCFKSLoadStoreParameter.SignatureAlgorithm.SHA512withDSA) {
                return new AlgorithmIdentifier(NISTObjectIdentifiers.dsa_with_sha512);
            }
            if (signatureAlgorithm == BCFKSLoadStoreParameter.SignatureAlgorithm.SHA3_512withDSA) {
                return new AlgorithmIdentifier(NISTObjectIdentifiers.id_dsa_with_sha3_512);
            }
        }
        if (key instanceof RSAKey) {
            if (signatureAlgorithm == BCFKSLoadStoreParameter.SignatureAlgorithm.SHA512withRSA) {
                return new AlgorithmIdentifier(PKCSObjectIdentifiers.sha512WithRSAEncryption, DERNull.INSTANCE);
            }
            if (signatureAlgorithm == BCFKSLoadStoreParameter.SignatureAlgorithm.SHA3_512withRSA) {
                return new AlgorithmIdentifier(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512, DERNull.INSTANCE);
            }
        }
        throw new IOException("unknown signature algorithm");
    }

    private KeyDerivationFunc generatePkbdAlgorithmIdentifier(PBKDFConfig pBKDFConfig, int i) {
        if (!MiscObjectIdentifiers.id_scrypt.equals((ASN1Primitive) pBKDFConfig.getAlgorithm())) {
            PBKDF2Config pBKDF2Config = (PBKDF2Config) pBKDFConfig;
            byte[] bArr = new byte[pBKDF2Config.getSaltLength()];
            getDefaultSecureRandom().nextBytes(bArr);
            return new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(bArr, pBKDF2Config.getIterationCount(), i, pBKDF2Config.getPRF()));
        }
        ScryptConfig scryptConfig = (ScryptConfig) pBKDFConfig;
        byte[] bArr2 = new byte[scryptConfig.getSaltLength()];
        getDefaultSecureRandom().nextBytes(bArr2);
        return new KeyDerivationFunc(MiscObjectIdentifiers.id_scrypt, new ScryptParams(bArr2, scryptConfig.getCostParameter(), scryptConfig.getBlockSize(), scryptConfig.getParallelizationParameter(), i));
    }

    private KeyDerivationFunc generatePkbdAlgorithmIdentifier(KeyDerivationFunc keyDerivationFunc, int i) {
        if (MiscObjectIdentifiers.id_scrypt.equals((ASN1Primitive) keyDerivationFunc.getAlgorithm())) {
            ScryptParams scryptParams = ScryptParams.getInstance(keyDerivationFunc.getParameters());
            byte[] bArr = new byte[scryptParams.getSalt().length];
            getDefaultSecureRandom().nextBytes(bArr);
            return new KeyDerivationFunc(MiscObjectIdentifiers.id_scrypt, new ScryptParams(bArr, scryptParams.getCostParameter(), scryptParams.getBlockSize(), scryptParams.getParallelizationParameter(), BigInteger.valueOf(i)));
        }
        PBKDF2Params pBKDF2Params = PBKDF2Params.getInstance(keyDerivationFunc.getParameters());
        byte[] bArr2 = new byte[pBKDF2Params.getSalt().length];
        getDefaultSecureRandom().nextBytes(bArr2);
        return new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(bArr2, pBKDF2Params.getIterationCount().intValue(), i, pBKDF2Params.getPrf()));
    }

    private KeyDerivationFunc generatePkbdAlgorithmIdentifier(ASN1ObjectIdentifier aSN1ObjectIdentifier, int i) {
        byte[] bArr = new byte[64];
        getDefaultSecureRandom().nextBytes(bArr);
        if (PKCSObjectIdentifiers.id_PBKDF2.equals((ASN1Primitive) aSN1ObjectIdentifier)) {
            return new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(bArr, 51200, i, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE)));
        }
        throw new IllegalStateException("unknown derivation algorithm: " + aSN1ObjectIdentifier);
    }

    static {
        oidMap.put("DESEDE", OIWObjectIdentifiers.desEDE);
        oidMap.put("TRIPLEDES", OIWObjectIdentifiers.desEDE);
        oidMap.put("TDEA", OIWObjectIdentifiers.desEDE);
        oidMap.put("HMACSHA1", PKCSObjectIdentifiers.id_hmacWithSHA1);
        oidMap.put("HMACSHA224", PKCSObjectIdentifiers.id_hmacWithSHA224);
        oidMap.put("HMACSHA256", PKCSObjectIdentifiers.id_hmacWithSHA256);
        oidMap.put("HMACSHA384", PKCSObjectIdentifiers.id_hmacWithSHA384);
        oidMap.put("HMACSHA512", PKCSObjectIdentifiers.id_hmacWithSHA512);
        oidMap.put("HMACSHA512/224", PKCSObjectIdentifiers.id_hmacWithSHA512_224);
        oidMap.put("HMACSHA512/256", PKCSObjectIdentifiers.id_hmacWithSHA512_256);
        oidMap.put("HMACSHA512(224)", PKCSObjectIdentifiers.id_hmacWithSHA512_224);
        oidMap.put("HMACSHA512(256)", PKCSObjectIdentifiers.id_hmacWithSHA512_256);
        oidMap.put("HMACSHA3-224", NISTObjectIdentifiers.id_hmacWithSHA3_224);
        oidMap.put("HMACSHA3-256", NISTObjectIdentifiers.id_hmacWithSHA3_256);
        oidMap.put("HMACSHA3-384", NISTObjectIdentifiers.id_hmacWithSHA3_384);
        oidMap.put("HMACSHA3-512", NISTObjectIdentifiers.id_hmacWithSHA3_512);
        oidMap.put("KMAC128", NISTObjectIdentifiers.id_Kmac128);
        oidMap.put("KMAC256", NISTObjectIdentifiers.id_Kmac256);
        oidMap.put("SEED", KISAObjectIdentifiers.id_seedCBC);
        oidMap.put("CAMELLIA.128", NTTObjectIdentifiers.id_camellia128_cbc);
        oidMap.put("CAMELLIA.192", NTTObjectIdentifiers.id_camellia192_cbc);
        oidMap.put("CAMELLIA.256", NTTObjectIdentifiers.id_camellia256_cbc);
        oidMap.put("ARIA.128", NSRIObjectIdentifiers.id_aria128_cbc);
        oidMap.put("ARIA.192", NSRIObjectIdentifiers.id_aria192_cbc);
        oidMap.put("ARIA.256", NSRIObjectIdentifiers.id_aria256_cbc);
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
}
