package es.gob.afirma.standalone.ui.restoreconfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
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
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
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
import org.spongycastle.cert.CertIOException;
import org.spongycastle.cert.X509ExtensionUtils;
import org.spongycastle.cert.jcajce.JcaX509CertificateConverter;
import org.spongycastle.cert.jcajce.JcaX509CertificateHolder;
import org.spongycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.spongycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.bc.BcDigestCalculatorProvider;
import org.spongycastle.operator.jcajce.JcaContentSignerBuilder;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/restoreconfig/CertUtil.class */
final class CertUtil {
    private static final String CA_CERT_COMMONNAME = "Autofirma ROOT";
    private static final int KEY_SIZE = 2048;
    private static final String PROVIDER = "SC";
    private static final String DEFAULT_LOCALHOST = "127.0.0.1";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    CertUtil() {
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/restoreconfig/CertUtil$CertPack.class */
    static class CertPack {
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
            this.password = cArr != null ? (char[]) cArr.clone() : null;
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
            if (this.p12 != null) {
                return (byte[]) this.p12.clone();
            }
            return null;
        }
    }

    static CertPack getCertPackForLocalhostSsl(String str, String str2) throws GeneralSecurityException, IOException {
        Security.addProvider(new BouncyCastleProvider());
        KeyStore.PrivateKeyEntry privateKeyEntryGenerateCaCertificate = generateCaCertificate(CA_CERT_COMMONNAME);
        return new CertPack(privateKeyEntryGenerateCaCertificate.getCertificate(), generateSslCertificate(DEFAULT_LOCALHOST, privateKeyEntryGenerateCaCertificate), str, str2.toCharArray());
    }

    private static KeyStore.PrivateKeyEntry generateCaCertificate(String str) throws NoSuchAlgorithmException, IOException, CertificateException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, new SecureRandom());
        KeyPair keyPairGenerateKeyPair = keyPairGenerator.generateKeyPair();
        Date date = new Date();
        date.setTime(new Date().getTime() + 315360000000L);
        JcaX509v3CertificateBuilder jcaX509v3CertificateBuilder = new JcaX509v3CertificateBuilder(new X500Name("CN=" + str), BigInteger.valueOf(new Random().nextInt()), new Date(), date, new X500Name("CN=" + str), keyPairGenerateKeyPair.getPublic());
        try {
            jcaX509v3CertificateBuilder.addExtension(Extension.subjectKeyIdentifier, false, (ASN1Encodable) new X509ExtensionUtils(new BcDigestCalculatorProvider().get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1))).createSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance(ASN1Sequence.getInstance(keyPairGenerateKeyPair.getPublic().getEncoded()))));
            jcaX509v3CertificateBuilder.addExtension(Extension.basicConstraints, true, (ASN1Encodable) new BasicConstraints(true));
            jcaX509v3CertificateBuilder.addExtension(Extension.keyUsage, false, (ASN1Encodable) new KeyUsage(182));
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

    private static KeyStore.PrivateKeyEntry generateSslCertificate(String str, KeyStore.PrivateKeyEntry privateKeyEntry) throws GeneralSecurityException, CertIOException {
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
        arrayList.add(new GeneralName(7, str));
        if (arrayList.size() > 0) {
            jcaX509v3CertificateBuilder.addExtension(Extension.subjectAlternativeName, false, (ASN1Encodable) new GeneralNames((GeneralName[]) arrayList.toArray(new GeneralName[arrayList.size()])));
        }
        try {
            return new KeyStore.PrivateKeyEntry(keyPairGenerateKeyPair.getPrivate(), new Certificate[]{new JcaX509CertificateConverter().setProvider("SC").getCertificate(jcaX509v3CertificateBuilder.build(new JcaContentSignerBuilder("SHA256withRSA").setProvider("SC").build(privateKeyEntry.getPrivateKey())))});
        } catch (OperatorCreationException e) {
            throw new GeneralSecurityException("No ha sido posible firmar el certificado SSL", e);
        }
    }

    static X509Certificate loadCertificate(File file) throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            Throwable th = null;
            try {
                X509Certificate x509Certificate = (X509Certificate) CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID).generateCertificate(fileInputStream);
                if (fileInputStream != null) {
                    if (0 != 0) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
                return x509Certificate;
            } catch (Throwable th3) {
                if (fileInputStream != null) {
                    if (0 != 0) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
                throw th3;
            }
        } catch (Exception e) {
            throw new IOException("No se pudo cargar el certificado", e);
        }
    }
}
