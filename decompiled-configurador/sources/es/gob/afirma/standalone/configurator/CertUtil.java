package es.gob.afirma.standalone.configurator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.BasicConstraints;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.asn1.x509.GeneralNames;
import org.spongycastle.asn1.x509.KeyPurposeId;
import org.spongycastle.asn1.x509.KeyUsage;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cert.X509ExtensionUtils;
import org.spongycastle.cert.jcajce.JcaX509CertificateConverter;
import org.spongycastle.cert.jcajce.JcaX509CertificateHolder;
import org.spongycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.spongycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.bc.BcDigestCalculatorProvider;
import org.spongycastle.operator.jcajce.JcaContentSignerBuilder;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/CertUtil.class */
final class CertUtil {
    private static final int KEY_SIZE = 2048;
    private static final String PROVIDER = "SC";
    private static final String DEFAULT_LOCALHOST_IP = "127.0.0.1";
    private static final String DEFAULT_LOCALHOST_HOSTNAME = "localhost";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    static final String ROOT_CERTIFICATE_PRINCIPAL = "CN=Autofirma ROOT";

    CertUtil() {
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/CertUtil$CertPack.class */
    static final class CertPack {
        private final Certificate sslCert;
        private final PrivateKey prK;
        private final String alias;
        private final char[] password;
        private byte[] p12 = null;
        private final Certificate caCert;

        CertPack(Certificate certificate, KeyStore.PrivateKeyEntry privateKeyEntry, String str, char[] cArr) {
            this.sslCert = privateKeyEntry.getCertificate();
            this.caCert = certificate;
            this.prK = privateKeyEntry.getPrivateKey();
            this.alias = str;
            this.password = cArr;
        }

        Certificate getSslCertificate() {
            return this.sslCert;
        }

        Certificate getCaCertificate() {
            return this.caCert;
        }

        byte[] getPkcs12() throws NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException {
            if (this.p12 == null) {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(null, null);
                keyStore.setKeyEntry(this.alias, this.prK, this.password, new Certificate[]{this.sslCert, this.caCert});
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                keyStore.store(byteArrayOutputStream, this.password);
                this.p12 = byteArrayOutputStream.toByteArray();
            }
            return this.p12;
        }
    }

    static CertPack getCertPackForLocalhostSsl(String str, String str2) throws NoSuchAlgorithmException, IOException, CertificateException {
        return getCertPackForHostSsl(str, str2, DEFAULT_LOCALHOST_IP, DEFAULT_LOCALHOST_IP, new String[]{DEFAULT_LOCALHOST_IP, DEFAULT_LOCALHOST_HOSTNAME}, ROOT_CERTIFICATE_PRINCIPAL);
    }

    static CertPack getCertPackForHostSsl(String str, String str2, String str3, String str4, String[] strArr, String str5) throws NoSuchAlgorithmException, IOException, CertificateException {
        String str6;
        Security.addProvider(new BouncyCastleProvider());
        KeyStore.PrivateKeyEntry privateKeyEntryGenerateCaCertificate = generateCaCertificate(str5);
        if (str3 != null) {
            str6 = str3;
        } else {
            str6 = (strArr == null || strArr.length <= 0 || strArr[0] == null) ? str4 : strArr[0];
        }
        return new CertPack(privateKeyEntryGenerateCaCertificate.getCertificate(), generateSslCertificate(str6, str4, strArr, privateKeyEntryGenerateCaCertificate), str, str2.toCharArray());
    }

    private static KeyStore.PrivateKeyEntry generateCaCertificate(String str) throws NoSuchAlgorithmException, IOException, CertificateException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, new SecureRandom());
        KeyPair keyPairGenerateKeyPair = keyPairGenerator.generateKeyPair();
        Date date = new Date();
        date.setTime(new Date().getTime() + 315360000000L);
        JcaX509v3CertificateBuilder jcaX509v3CertificateBuilder = new JcaX509v3CertificateBuilder(new X500Name(str), BigInteger.valueOf(new Random().nextInt()), new Date(), date, new X500Name(str), keyPairGenerateKeyPair.getPublic());
        try {
            jcaX509v3CertificateBuilder.addExtension(Extension.subjectKeyIdentifier, false, (ASN1Encodable) new X509ExtensionUtils(new BcDigestCalculatorProvider().get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1))).createSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance(ASN1Sequence.getInstance(keyPairGenerateKeyPair.getPublic().getEncoded()))));
            jcaX509v3CertificateBuilder.addExtension(Extension.basicConstraints, true, (ASN1Encodable) new BasicConstraints(true));
            jcaX509v3CertificateBuilder.addExtension(Extension.keyUsage, false, (ASN1Encodable) new KeyUsage(CipherSuite.TLS_RSA_PSK_WITH_AES_128_CBC_SHA256));
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
            aSN1EncodableVector.add(KeyPurposeId.id_kp_serverAuth);
            aSN1EncodableVector.add(KeyPurposeId.id_kp_clientAuth);
            aSN1EncodableVector.add(KeyPurposeId.anyExtendedKeyUsage);
            jcaX509v3CertificateBuilder.addExtension(Extension.extendedKeyUsage, false, (ASN1Encodable) new DERSequence(aSN1EncodableVector));
            try {
                return new KeyStore.PrivateKeyEntry(keyPairGenerateKeyPair.getPrivate(), new Certificate[]{new JcaX509CertificateConverter().setProvider("SC").getCertificate(jcaX509v3CertificateBuilder.build(new JcaContentSignerBuilder("SHA256withRSA").setProvider("SC").build(keyPairGenerateKeyPair.getPrivate())))});
            } catch (OperatorCreationException e) {
                throw new CertificateException("Error durante la construccion del certificado CA: " + e, e);
            }
        } catch (OperatorCreationException e2) {
            throw new IOException("No se ha podido inicializar el operador de cifrado: " + e2, e2);
        }
    }

    private static KeyStore.PrivateKeyEntry generateSslCertificate(String str, String str2, String[] strArr, KeyStore.PrivateKeyEntry privateKeyEntry) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048, new SecureRandom());
            KeyPair keyPairGenerateKeyPair = keyPairGenerator.generateKeyPair();
            Date date = new Date();
            date.setTime(new Date().getTime() + 315360000000L);
            JcaX509v3CertificateBuilder jcaX509v3CertificateBuilder = new JcaX509v3CertificateBuilder(new JcaX509CertificateHolder((X509Certificate) privateKeyEntry.getCertificate()).getSubject(), BigInteger.valueOf(new Random().nextInt()), new Date(), date, new X500Name("CN=" + str), keyPairGenerateKeyPair.getPublic());
            JcaX509ExtensionUtils jcaX509ExtensionUtils = new JcaX509ExtensionUtils();
            jcaX509v3CertificateBuilder.addExtension(Extension.subjectKeyIdentifier, false, (ASN1Encodable) jcaX509ExtensionUtils.createSubjectKeyIdentifier(keyPairGenerateKeyPair.getPublic()));
            jcaX509v3CertificateBuilder.addExtension(Extension.basicConstraints, false, (ASN1Encodable) new BasicConstraints(false));
            jcaX509v3CertificateBuilder.addExtension(Extension.authorityKeyIdentifier, false, (ASN1Encodable) jcaX509ExtensionUtils.createAuthorityKeyIdentifier(privateKeyEntry.getCertificate().getPublicKey()));
            ArrayList arrayList = new ArrayList();
            arrayList.add(new GeneralName(7, str2));
            if (strArr != null) {
                for (String str3 : strArr) {
                    if (str3 != null) {
                        arrayList.add(new GeneralName(2, str3));
                    }
                }
            }
            jcaX509v3CertificateBuilder.addExtension(Extension.subjectAlternativeName, false, (ASN1Encodable) new GeneralNames((GeneralName[]) arrayList.toArray(new GeneralName[arrayList.size()])));
            return new KeyStore.PrivateKeyEntry(keyPairGenerateKeyPair.getPrivate(), new Certificate[]{new JcaX509CertificateConverter().setProvider("SC").getCertificate(jcaX509v3CertificateBuilder.build(new JcaContentSignerBuilder("SHA256withRSA").setProvider("SC").build(privateKeyEntry.getPrivateKey())))});
        } catch (Error | Exception e) {
            throw new RuntimeException("Error al generar el certificado SSL: " + e, e);
        }
    }
}
