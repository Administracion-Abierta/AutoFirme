package org.bouncycastle.openssl;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemHeader;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectParser;
import org.bouncycastle.util.io.pem.PemReader;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMParser.class */
public class PEMParser extends PemReader {
    public static final String TYPE_CERTIFICATE_REQUEST = "CERTIFICATE REQUEST";
    public static final String TYPE_NEW_CERTIFICATE_REQUEST = "NEW CERTIFICATE REQUEST";
    public static final String TYPE_CERTIFICATE = "CERTIFICATE";
    public static final String TYPE_TRUSTED_CERTIFICATE = "TRUSTED CERTIFICATE";
    public static final String TYPE_X509_CERTIFICATE = "X509 CERTIFICATE";
    public static final String TYPE_X509_CRL = "X509 CRL";
    public static final String TYPE_PKCS7 = "PKCS7";
    public static final String TYPE_CMS = "CMS";
    public static final String TYPE_ATTRIBUTE_CERTIFICATE = "ATTRIBUTE CERTIFICATE";
    public static final String TYPE_EC_PARAMETERS = "EC PARAMETERS";
    public static final String TYPE_PUBLIC_KEY = "PUBLIC KEY";
    public static final String TYPE_RSA_PUBLIC_KEY = "RSA PUBLIC KEY";
    public static final String TYPE_RSA_PRIVATE_KEY = "RSA PRIVATE KEY";
    public static final String TYPE_DSA_PRIVATE_KEY = "DSA PRIVATE KEY";
    public static final String TYPE_EC_PRIVATE_KEY = "EC PRIVATE KEY";
    public static final String TYPE_ENCRYPTED_PRIVATE_KEY = "ENCRYPTED PRIVATE KEY";
    public static final String TYPE_PRIVATE_KEY = "PRIVATE KEY";
    protected final Map parsers;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMParser$DSAKeyPairParser.class */
    private static class DSAKeyPairParser implements PEMKeyPairParser {
        private DSAKeyPairParser() {
        }

        @Override // org.bouncycastle.openssl.PEMKeyPairParser
        public PEMKeyPair parse(byte[] bArr) throws IOException {
            try {
                ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(bArr);
                if (aSN1Sequence.size() != 6) {
                    throw new PEMException("malformed sequence in DSA private key");
                }
                ASN1Integer aSN1Integer = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(1));
                ASN1Integer aSN1Integer2 = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(2));
                ASN1Integer aSN1Integer3 = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(3));
                return new PEMKeyPair(new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(aSN1Integer.getValue(), aSN1Integer2.getValue(), aSN1Integer3.getValue())), ASN1Integer.getInstance(aSN1Sequence.getObjectAt(4))), new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(aSN1Integer.getValue(), aSN1Integer2.getValue(), aSN1Integer3.getValue())), ASN1Integer.getInstance(aSN1Sequence.getObjectAt(5))));
            } catch (IOException e) {
                throw e;
            } catch (Exception e2) {
                throw new PEMException("problem creating DSA private key: " + e2.toString(), e2);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMParser$ECCurveParamsParser.class */
    private static class ECCurveParamsParser implements PemObjectParser {
        private ECCurveParamsParser() {
        }

        @Override // org.bouncycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject pemObject) throws IOException {
            try {
                ASN1Primitive aSN1PrimitiveFromByteArray = ASN1Primitive.fromByteArray(pemObject.getContent());
                if (aSN1PrimitiveFromByteArray instanceof ASN1ObjectIdentifier) {
                    return aSN1PrimitiveFromByteArray;
                }
                if (aSN1PrimitiveFromByteArray instanceof ASN1Sequence) {
                    return X9ECParameters.getInstance(aSN1PrimitiveFromByteArray);
                }
                return null;
            } catch (IOException e) {
                throw e;
            } catch (Exception e2) {
                throw new PEMException("exception extracting EC named curve: " + e2.toString());
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMParser$ECDSAKeyPairParser.class */
    private static class ECDSAKeyPairParser implements PEMKeyPairParser {
        private ECDSAKeyPairParser() {
        }

        @Override // org.bouncycastle.openssl.PEMKeyPairParser
        public PEMKeyPair parse(byte[] bArr) throws IOException {
            try {
                ECPrivateKey eCPrivateKey = ECPrivateKey.getInstance(ASN1Sequence.getInstance(bArr));
                AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, eCPrivateKey.getParametersObject());
                PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(algorithmIdentifier, eCPrivateKey);
                ASN1BitString publicKey = eCPrivateKey.getPublicKey();
                SubjectPublicKeyInfo subjectPublicKeyInfo = null;
                if (publicKey != null) {
                    subjectPublicKeyInfo = new SubjectPublicKeyInfo(algorithmIdentifier, publicKey.getBytes());
                }
                return new PEMKeyPair(subjectPublicKeyInfo, privateKeyInfo);
            } catch (IOException e) {
                throw e;
            } catch (Exception e2) {
                throw new PEMException("problem creating EC private key: " + e2.toString(), e2);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMParser$EncryptedPrivateKeyParser.class */
    private static class EncryptedPrivateKeyParser implements PemObjectParser {
        @Override // org.bouncycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject pemObject) throws IOException {
            try {
                return new PKCS8EncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo.getInstance(pemObject.getContent()));
            } catch (Exception e) {
                throw new PEMException("problem parsing ENCRYPTED PRIVATE KEY: " + e.toString(), e);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMParser$KeyPairParser.class */
    private static class KeyPairParser implements PemObjectParser {
        private final PEMKeyPairParser pemKeyPairParser;

        public KeyPairParser(PEMKeyPairParser pEMKeyPairParser) {
            this.pemKeyPairParser = pEMKeyPairParser;
        }

        @Override // org.bouncycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject pemObject) throws IOException {
            boolean z = false;
            String value = null;
            for (PemHeader pemHeader : pemObject.getHeaders()) {
                if (pemHeader.getName().equals("Proc-Type") && pemHeader.getValue().equals("4,ENCRYPTED")) {
                    z = true;
                } else if (pemHeader.getName().equals("DEK-Info")) {
                    value = pemHeader.getValue();
                }
            }
            byte[] content = pemObject.getContent();
            try {
                if (!z) {
                    return this.pemKeyPairParser.parse(content);
                }
                StringTokenizer stringTokenizer = new StringTokenizer(value, ",");
                return new PEMEncryptedKeyPair(stringTokenizer.nextToken(), Hex.decode(stringTokenizer.nextToken()), content, this.pemKeyPairParser);
            } catch (IOException e) {
                if (z) {
                    throw new PEMException("exception decoding - please check password and data.", e);
                }
                throw new PEMException(e.getMessage(), e);
            } catch (IllegalArgumentException e2) {
                if (z) {
                    throw new PEMException("exception decoding - please check password and data.", e2);
                }
                throw new PEMException(e2.getMessage(), e2);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMParser$PKCS10CertificationRequestParser.class */
    private static class PKCS10CertificationRequestParser implements PemObjectParser {
        private PKCS10CertificationRequestParser() {
        }

        @Override // org.bouncycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject pemObject) throws IOException {
            try {
                return new PKCS10CertificationRequest(pemObject.getContent());
            } catch (Exception e) {
                throw new PEMException("problem parsing certrequest: " + e.toString(), e);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMParser$PKCS7Parser.class */
    private static class PKCS7Parser implements PemObjectParser {
        private PKCS7Parser() {
        }

        @Override // org.bouncycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject pemObject) throws IOException {
            try {
                return ContentInfo.getInstance(pemObject.getContent());
            } catch (Exception e) {
                throw new PEMException("problem parsing PKCS7 object: " + e.toString(), e);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMParser$PrivateKeyParser.class */
    private static class PrivateKeyParser implements PemObjectParser {
        @Override // org.bouncycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject pemObject) throws IOException {
            try {
                return PrivateKeyInfo.getInstance(pemObject.getContent());
            } catch (Exception e) {
                throw new PEMException("problem parsing PRIVATE KEY: " + e.toString(), e);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMParser$PublicKeyParser.class */
    private static class PublicKeyParser implements PemObjectParser {
        @Override // org.bouncycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject pemObject) throws IOException {
            return SubjectPublicKeyInfo.getInstance(pemObject.getContent());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMParser$RSAKeyPairParser.class */
    private static class RSAKeyPairParser implements PEMKeyPairParser {
        private RSAKeyPairParser() {
        }

        @Override // org.bouncycastle.openssl.PEMKeyPairParser
        public PEMKeyPair parse(byte[] bArr) throws IOException {
            try {
                ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(bArr);
                if (aSN1Sequence.size() != 9) {
                    throw new PEMException("malformed sequence in RSA private key");
                }
                RSAPrivateKey rSAPrivateKey = RSAPrivateKey.getInstance(aSN1Sequence);
                RSAPublicKey rSAPublicKey = new RSAPublicKey(rSAPrivateKey.getModulus(), rSAPrivateKey.getPublicExponent());
                AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
                return new PEMKeyPair(new SubjectPublicKeyInfo(algorithmIdentifier, rSAPublicKey), new PrivateKeyInfo(algorithmIdentifier, rSAPrivateKey));
            } catch (IOException e) {
                throw e;
            } catch (Exception e2) {
                throw new PEMException("problem creating RSA private key: " + e2.toString(), e2);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMParser$RSAPublicKeyParser.class */
    private static class RSAPublicKeyParser implements PemObjectParser {
        @Override // org.bouncycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject pemObject) throws IOException {
            try {
                return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE), RSAPublicKey.getInstance(pemObject.getContent()));
            } catch (IOException e) {
                throw e;
            } catch (Exception e2) {
                throw new PEMException("problem extracting key: " + e2.toString(), e2);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMParser$X509AttributeCertificateParser.class */
    private static class X509AttributeCertificateParser implements PemObjectParser {
        private X509AttributeCertificateParser() {
        }

        @Override // org.bouncycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject pemObject) throws IOException {
            return new X509AttributeCertificateHolder(pemObject.getContent());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMParser$X509CRLParser.class */
    private static class X509CRLParser implements PemObjectParser {
        private X509CRLParser() {
        }

        @Override // org.bouncycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject pemObject) throws IOException {
            try {
                return new X509CRLHolder(pemObject.getContent());
            } catch (Exception e) {
                throw new PEMException("problem parsing cert: " + e.toString(), e);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMParser$X509CertificateParser.class */
    private static class X509CertificateParser implements PemObjectParser {
        private X509CertificateParser() {
        }

        @Override // org.bouncycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject pemObject) throws IOException {
            try {
                return new X509CertificateHolder(pemObject.getContent());
            } catch (Exception e) {
                throw new PEMException("problem parsing cert: " + e.toString(), e);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/PEMParser$X509TrustedCertificateParser.class */
    private static class X509TrustedCertificateParser implements PemObjectParser {
        private X509TrustedCertificateParser() {
        }

        @Override // org.bouncycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject pemObject) throws IOException {
            try {
                return new X509TrustedCertificateBlock(pemObject.getContent());
            } catch (Exception e) {
                throw new PEMException("problem parsing cert: " + e.toString(), e);
            }
        }
    }

    public PEMParser(Reader reader) {
        super(reader);
        this.parsers = new HashMap();
        this.parsers.put(TYPE_CERTIFICATE_REQUEST, new PKCS10CertificationRequestParser());
        this.parsers.put(TYPE_NEW_CERTIFICATE_REQUEST, new PKCS10CertificationRequestParser());
        this.parsers.put(TYPE_CERTIFICATE, new X509CertificateParser());
        this.parsers.put(TYPE_TRUSTED_CERTIFICATE, new X509TrustedCertificateParser());
        this.parsers.put(TYPE_X509_CERTIFICATE, new X509CertificateParser());
        this.parsers.put(TYPE_X509_CRL, new X509CRLParser());
        this.parsers.put("PKCS7", new PKCS7Parser());
        this.parsers.put("CMS", new PKCS7Parser());
        this.parsers.put(TYPE_ATTRIBUTE_CERTIFICATE, new X509AttributeCertificateParser());
        this.parsers.put(TYPE_EC_PARAMETERS, new ECCurveParamsParser());
        this.parsers.put(TYPE_PUBLIC_KEY, new PublicKeyParser());
        this.parsers.put(TYPE_RSA_PUBLIC_KEY, new RSAPublicKeyParser());
        this.parsers.put(TYPE_RSA_PRIVATE_KEY, new KeyPairParser(new RSAKeyPairParser()));
        this.parsers.put(TYPE_DSA_PRIVATE_KEY, new KeyPairParser(new DSAKeyPairParser()));
        this.parsers.put(TYPE_EC_PRIVATE_KEY, new KeyPairParser(new ECDSAKeyPairParser()));
        this.parsers.put(TYPE_ENCRYPTED_PRIVATE_KEY, new EncryptedPrivateKeyParser());
        this.parsers.put(TYPE_PRIVATE_KEY, new PrivateKeyParser());
    }

    public Object readObject() throws IOException {
        PemObject pemObject = readPemObject();
        if (pemObject == null) {
            return null;
        }
        String type = pemObject.getType();
        Object obj = this.parsers.get(type);
        if (obj == null) {
            throw new IOException("unrecognised object: " + type);
        }
        return ((PemObjectParser) obj).parseObject(pemObject);
    }

    public Set<String> getSupportedTypes() {
        return Collections.unmodifiableSet(this.parsers.keySet());
    }
}
