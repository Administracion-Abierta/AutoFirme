package org.bouncycastle.openssl;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.pem.PemGenerationException;
import org.bouncycastle.util.io.pem.PemHeader;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectGenerator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/MiscPEMGenerator.class */
public class MiscPEMGenerator implements PemObjectGenerator {
    private static final ASN1ObjectIdentifier[] dsaOids = {X9ObjectIdentifiers.id_dsa, OIWObjectIdentifiers.dsaWithSHA1};
    private static final byte[] hexEncodingTable = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};
    private final Object obj;
    private final PEMEncryptor encryptor;

    public MiscPEMGenerator(Object obj) {
        this.obj = obj;
        this.encryptor = null;
    }

    public MiscPEMGenerator(Object obj, PEMEncryptor pEMEncryptor) {
        this.obj = obj;
        this.encryptor = pEMEncryptor;
    }

    private PemObject createPemObject(Object obj) throws IOException {
        String str;
        byte[] encoded;
        if (obj instanceof PemObject) {
            return (PemObject) obj;
        }
        if (obj instanceof PemObjectGenerator) {
            return ((PemObjectGenerator) obj).generate();
        }
        if (obj instanceof X509CertificateHolder) {
            str = PEMParser.TYPE_CERTIFICATE;
            encoded = ((X509CertificateHolder) obj).getEncoded();
        } else if (obj instanceof X509CRLHolder) {
            str = PEMParser.TYPE_X509_CRL;
            encoded = ((X509CRLHolder) obj).getEncoded();
        } else if (obj instanceof X509TrustedCertificateBlock) {
            str = PEMParser.TYPE_TRUSTED_CERTIFICATE;
            encoded = ((X509TrustedCertificateBlock) obj).getEncoded();
        } else if (obj instanceof PrivateKeyInfo) {
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) obj;
            ASN1ObjectIdentifier algorithm = privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
            if (algorithm.equals((ASN1Primitive) PKCSObjectIdentifiers.rsaEncryption)) {
                str = PEMParser.TYPE_RSA_PRIVATE_KEY;
                encoded = privateKeyInfo.parsePrivateKey().toASN1Primitive().getEncoded();
            } else if (algorithm.equals((ASN1Primitive) dsaOids[0]) || algorithm.equals((ASN1Primitive) dsaOids[1])) {
                str = PEMParser.TYPE_DSA_PRIVATE_KEY;
                DSAParameter dSAParameter = DSAParameter.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getParameters());
                ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
                aSN1EncodableVector.add(new ASN1Integer(0L));
                aSN1EncodableVector.add(new ASN1Integer(dSAParameter.getP()));
                aSN1EncodableVector.add(new ASN1Integer(dSAParameter.getQ()));
                aSN1EncodableVector.add(new ASN1Integer(dSAParameter.getG()));
                BigInteger value = ASN1Integer.getInstance(privateKeyInfo.parsePrivateKey()).getValue();
                aSN1EncodableVector.add(new ASN1Integer(dSAParameter.getG().modPow(value, dSAParameter.getP())));
                aSN1EncodableVector.add(new ASN1Integer(value));
                encoded = new DERSequence(aSN1EncodableVector).getEncoded();
            } else if (algorithm.equals((ASN1Primitive) X9ObjectIdentifiers.id_ecPublicKey)) {
                str = PEMParser.TYPE_EC_PRIVATE_KEY;
                encoded = privateKeyInfo.parsePrivateKey().toASN1Primitive().getEncoded();
            } else {
                str = PEMParser.TYPE_PRIVATE_KEY;
                encoded = privateKeyInfo.getEncoded();
            }
        } else if (obj instanceof SubjectPublicKeyInfo) {
            str = PEMParser.TYPE_PUBLIC_KEY;
            encoded = ((SubjectPublicKeyInfo) obj).getEncoded();
        } else if (obj instanceof X509AttributeCertificateHolder) {
            str = PEMParser.TYPE_ATTRIBUTE_CERTIFICATE;
            encoded = ((X509AttributeCertificateHolder) obj).getEncoded();
        } else if (obj instanceof PKCS10CertificationRequest) {
            str = PEMParser.TYPE_CERTIFICATE_REQUEST;
            encoded = ((PKCS10CertificationRequest) obj).getEncoded();
        } else if (obj instanceof PKCS8EncryptedPrivateKeyInfo) {
            str = PEMParser.TYPE_ENCRYPTED_PRIVATE_KEY;
            encoded = ((PKCS8EncryptedPrivateKeyInfo) obj).getEncoded();
        } else {
            if (!(obj instanceof ContentInfo)) {
                throw new PemGenerationException("unknown object passed - can't encode.");
            }
            str = "PKCS7";
            encoded = ((ContentInfo) obj).getEncoded();
        }
        if (this.encryptor == null) {
            return new PemObject(str, encoded);
        }
        String upperCase = Strings.toUpperCase(this.encryptor.getAlgorithm());
        if (upperCase.equals("DESEDE")) {
            upperCase = "DES-EDE3-CBC";
        }
        byte[] iv = this.encryptor.getIV();
        byte[] bArrEncrypt = this.encryptor.encrypt(encoded);
        ArrayList arrayList = new ArrayList(2);
        arrayList.add(new PemHeader("Proc-Type", "4,ENCRYPTED"));
        arrayList.add(new PemHeader("DEK-Info", upperCase + "," + getHexEncoded(iv)));
        return new PemObject(str, arrayList, bArrEncrypt);
    }

    private String getHexEncoded(byte[] bArr) throws IOException {
        char[] cArr = new char[bArr.length * 2];
        for (int i = 0; i != bArr.length; i++) {
            int i2 = bArr[i] & 255;
            cArr[2 * i] = (char) hexEncodingTable[i2 >>> 4];
            cArr[(2 * i) + 1] = (char) hexEncodingTable[i2 & 15];
        }
        return new String(cArr);
    }

    @Override // org.bouncycastle.util.io.pem.PemObjectGenerator
    public PemObject generate() throws PemGenerationException {
        try {
            return createPemObject(this.obj);
        } catch (IOException e) {
            throw new PemGenerationException("encoding exception: " + e.getMessage(), e);
        }
    }
}
