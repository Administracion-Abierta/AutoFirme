package org.spongycastle.openssl;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x509.DSAParameter;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x9.X9ObjectIdentifiers;
import org.spongycastle.cert.X509AttributeCertificateHolder;
import org.spongycastle.cert.X509CRLHolder;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.pkcs.PKCS10CertificationRequest;
import org.spongycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.spongycastle.util.Strings;
import org.spongycastle.util.io.pem.PemGenerationException;
import org.spongycastle.util.io.pem.PemHeader;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemObjectGenerator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/openssl/MiscPEMGenerator.class */
public class MiscPEMGenerator implements PemObjectGenerator {
    private static final ASN1ObjectIdentifier[] dsaOids = {X9ObjectIdentifiers.id_dsa, OIWObjectIdentifiers.dsaWithSHA1};
    private static final byte[] hexEncodingTable = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};
    private final Object obj;
    private final PEMEncryptor encryptor;

    public MiscPEMGenerator(Object o) {
        this.obj = o;
        this.encryptor = null;
    }

    public MiscPEMGenerator(Object o, PEMEncryptor encryptor) {
        this.obj = o;
        this.encryptor = encryptor;
    }

    private PemObject createPemObject(Object o) throws IOException {
        String type;
        byte[] encoding;
        if (o instanceof PemObject) {
            return (PemObject) o;
        }
        if (o instanceof PemObjectGenerator) {
            return ((PemObjectGenerator) o).generate();
        }
        if (o instanceof X509CertificateHolder) {
            type = "CERTIFICATE";
            encoding = ((X509CertificateHolder) o).getEncoded();
        } else if (o instanceof X509CRLHolder) {
            type = "X509 CRL";
            encoding = ((X509CRLHolder) o).getEncoded();
        } else if (o instanceof X509TrustedCertificateBlock) {
            type = "TRUSTED CERTIFICATE";
            encoding = ((X509TrustedCertificateBlock) o).getEncoded();
        } else if (o instanceof PrivateKeyInfo) {
            PrivateKeyInfo info = (PrivateKeyInfo) o;
            ASN1ObjectIdentifier algOID = info.getPrivateKeyAlgorithm().getAlgorithm();
            if (algOID.equals(PKCSObjectIdentifiers.rsaEncryption)) {
                type = "RSA PRIVATE KEY";
                encoding = info.parsePrivateKey().toASN1Primitive().getEncoded();
            } else if (algOID.equals(dsaOids[0]) || algOID.equals(dsaOids[1])) {
                type = "DSA PRIVATE KEY";
                DSAParameter p = DSAParameter.getInstance(info.getPrivateKeyAlgorithm().getParameters());
                ASN1EncodableVector v = new ASN1EncodableVector();
                v.add(new ASN1Integer(0L));
                v.add(new ASN1Integer(p.getP()));
                v.add(new ASN1Integer(p.getQ()));
                v.add(new ASN1Integer(p.getG()));
                BigInteger x = ASN1Integer.getInstance(info.parsePrivateKey()).getValue();
                BigInteger y = p.getG().modPow(x, p.getP());
                v.add(new ASN1Integer(y));
                v.add(new ASN1Integer(x));
                encoding = new DERSequence(v).getEncoded();
            } else if (algOID.equals(X9ObjectIdentifiers.id_ecPublicKey)) {
                type = "EC PRIVATE KEY";
                encoding = info.parsePrivateKey().toASN1Primitive().getEncoded();
            } else {
                throw new IOException("Cannot identify private key");
            }
        } else if (o instanceof SubjectPublicKeyInfo) {
            type = "PUBLIC KEY";
            encoding = ((SubjectPublicKeyInfo) o).getEncoded();
        } else if (o instanceof X509AttributeCertificateHolder) {
            type = "ATTRIBUTE CERTIFICATE";
            encoding = ((X509AttributeCertificateHolder) o).getEncoded();
        } else if (o instanceof PKCS10CertificationRequest) {
            type = "CERTIFICATE REQUEST";
            encoding = ((PKCS10CertificationRequest) o).getEncoded();
        } else if (o instanceof PKCS8EncryptedPrivateKeyInfo) {
            type = "ENCRYPTED PRIVATE KEY";
            encoding = ((PKCS8EncryptedPrivateKeyInfo) o).getEncoded();
        } else if (o instanceof ContentInfo) {
            type = "PKCS7";
            encoding = ((ContentInfo) o).getEncoded();
        } else {
            throw new PemGenerationException("unknown object passed - can't encode.");
        }
        if (this.encryptor != null) {
            String dekAlgName = Strings.toUpperCase(this.encryptor.getAlgorithm());
            if (dekAlgName.equals("DESEDE")) {
                dekAlgName = "DES-EDE3-CBC";
            }
            byte[] iv = this.encryptor.getIV();
            byte[] encData = this.encryptor.encrypt(encoding);
            List headers = new ArrayList(2);
            headers.add(new PemHeader("Proc-Type", "4,ENCRYPTED"));
            headers.add(new PemHeader("DEK-Info", dekAlgName + "," + getHexEncoded(iv)));
            return new PemObject(type, headers, encData);
        }
        return new PemObject(type, encoding);
    }

    private String getHexEncoded(byte[] bytes) throws IOException {
        char[] chars = new char[bytes.length * 2];
        for (int i = 0; i != bytes.length; i++) {
            int v = bytes[i] & 255;
            chars[2 * i] = (char) hexEncodingTable[v >>> 4];
            chars[(2 * i) + 1] = (char) hexEncodingTable[v & 15];
        }
        return new String(chars);
    }

    @Override // org.spongycastle.util.io.pem.PemObjectGenerator
    public PemObject generate() throws PemGenerationException {
        try {
            return createPemObject(this.obj);
        } catch (IOException e) {
            throw new PemGenerationException("encoding exception: " + e.getMessage(), e);
        }
    }
}
