package org.spongycastle.openssl;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.pkcs.RSAPrivateKey;
import org.spongycastle.asn1.pkcs.RSAPublicKey;
import org.spongycastle.asn1.sec.ECPrivateKey;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.DSAParameter;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.asn1.x9.X9ObjectIdentifiers;
import org.spongycastle.cert.X509AttributeCertificateHolder;
import org.spongycastle.cert.X509CRLHolder;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.pkcs.PKCS10CertificationRequest;
import org.spongycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.spongycastle.util.encoders.Hex;
import org.spongycastle.util.io.pem.PemHeader;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemObjectParser;
import org.spongycastle.util.io.pem.PemReader;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMParser.class */
public class PEMParser extends PemReader {
    private final Map parsers;

    public PEMParser(Reader reader) {
        super(reader);
        this.parsers = new HashMap();
        this.parsers.put(org.bouncycastle.openssl.PEMParser.TYPE_CERTIFICATE_REQUEST, new PKCS10CertificationRequestParser());
        this.parsers.put(org.bouncycastle.openssl.PEMParser.TYPE_NEW_CERTIFICATE_REQUEST, new PKCS10CertificationRequestParser());
        this.parsers.put(org.bouncycastle.openssl.PEMParser.TYPE_CERTIFICATE, new X509CertificateParser());
        this.parsers.put(org.bouncycastle.openssl.PEMParser.TYPE_TRUSTED_CERTIFICATE, new X509TrustedCertificateParser());
        this.parsers.put(org.bouncycastle.openssl.PEMParser.TYPE_X509_CERTIFICATE, new X509CertificateParser());
        this.parsers.put(org.bouncycastle.openssl.PEMParser.TYPE_X509_CRL, new X509CRLParser());
        this.parsers.put("PKCS7", new PKCS7Parser());
        this.parsers.put("CMS", new PKCS7Parser());
        this.parsers.put(org.bouncycastle.openssl.PEMParser.TYPE_ATTRIBUTE_CERTIFICATE, new X509AttributeCertificateParser());
        this.parsers.put(org.bouncycastle.openssl.PEMParser.TYPE_EC_PARAMETERS, new ECCurveParamsParser());
        this.parsers.put(org.bouncycastle.openssl.PEMParser.TYPE_PUBLIC_KEY, new PublicKeyParser());
        this.parsers.put(org.bouncycastle.openssl.PEMParser.TYPE_RSA_PUBLIC_KEY, new RSAPublicKeyParser());
        this.parsers.put(org.bouncycastle.openssl.PEMParser.TYPE_RSA_PRIVATE_KEY, new KeyPairParser(new RSAKeyPairParser()));
        this.parsers.put(org.bouncycastle.openssl.PEMParser.TYPE_DSA_PRIVATE_KEY, new KeyPairParser(new DSAKeyPairParser()));
        this.parsers.put(org.bouncycastle.openssl.PEMParser.TYPE_EC_PRIVATE_KEY, new KeyPairParser(new ECDSAKeyPairParser()));
        this.parsers.put(org.bouncycastle.openssl.PEMParser.TYPE_ENCRYPTED_PRIVATE_KEY, new EncryptedPrivateKeyParser());
        this.parsers.put(org.bouncycastle.openssl.PEMParser.TYPE_PRIVATE_KEY, new PrivateKeyParser());
    }

    public Object readObject() throws IOException {
        PemObject obj = readPemObject();
        if (obj != null) {
            String type = obj.getType();
            if (this.parsers.containsKey(type)) {
                return ((PemObjectParser) this.parsers.get(type)).parseObject(obj);
            }
            throw new IOException("unrecognised object: " + type);
        }
        return null;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMParser$KeyPairParser.class */
    private class KeyPairParser implements PemObjectParser {
        private final PEMKeyPairParser pemKeyPairParser;

        public KeyPairParser(PEMKeyPairParser pemKeyPairParser) {
            this.pemKeyPairParser = pemKeyPairParser;
        }

        @Override // org.spongycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject obj) throws IOException {
            boolean isEncrypted = false;
            String dekInfo = null;
            List<PemHeader> headers = obj.getHeaders();
            for (PemHeader hdr : headers) {
                if (hdr.getName().equals("Proc-Type") && hdr.getValue().equals("4,ENCRYPTED")) {
                    isEncrypted = true;
                } else if (hdr.getName().equals("DEK-Info")) {
                    dekInfo = hdr.getValue();
                }
            }
            byte[] keyBytes = obj.getContent();
            try {
                if (isEncrypted) {
                    StringTokenizer tknz = new StringTokenizer(dekInfo, ",");
                    String dekAlgName = tknz.nextToken();
                    byte[] iv = Hex.decode(tknz.nextToken());
                    return new PEMEncryptedKeyPair(dekAlgName, iv, keyBytes, this.pemKeyPairParser);
                }
                return this.pemKeyPairParser.parse(keyBytes);
            } catch (IOException e) {
                if (isEncrypted) {
                    throw new PEMException("exception decoding - please check password and data.", e);
                }
                throw new PEMException(e.getMessage(), e);
            } catch (IllegalArgumentException e2) {
                if (isEncrypted) {
                    throw new PEMException("exception decoding - please check password and data.", e2);
                }
                throw new PEMException(e2.getMessage(), e2);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMParser$DSAKeyPairParser.class */
    private class DSAKeyPairParser implements PEMKeyPairParser {
        private DSAKeyPairParser() {
        }

        @Override // org.spongycastle.openssl.PEMKeyPairParser
        public PEMKeyPair parse(byte[] encoding) throws IOException {
            try {
                ASN1Sequence seq = ASN1Sequence.getInstance(encoding);
                if (seq.size() != 6) {
                    throw new PEMException("malformed sequence in DSA private key");
                }
                ASN1Integer p = ASN1Integer.getInstance(seq.getObjectAt(1));
                ASN1Integer q = ASN1Integer.getInstance(seq.getObjectAt(2));
                ASN1Integer g = ASN1Integer.getInstance(seq.getObjectAt(3));
                ASN1Integer y = ASN1Integer.getInstance(seq.getObjectAt(4));
                ASN1Integer x = ASN1Integer.getInstance(seq.getObjectAt(5));
                return new PEMKeyPair(new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(p.getValue(), q.getValue(), g.getValue())), y), new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(p.getValue(), q.getValue(), g.getValue())), x));
            } catch (IOException e) {
                throw e;
            } catch (Exception e2) {
                throw new PEMException("problem creating DSA private key: " + e2.toString(), e2);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMParser$ECDSAKeyPairParser.class */
    private class ECDSAKeyPairParser implements PEMKeyPairParser {
        private ECDSAKeyPairParser() {
        }

        @Override // org.spongycastle.openssl.PEMKeyPairParser
        public PEMKeyPair parse(byte[] encoding) throws IOException {
            try {
                ASN1Sequence seq = ASN1Sequence.getInstance(encoding);
                ECPrivateKey pKey = ECPrivateKey.getInstance(seq);
                AlgorithmIdentifier algId = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, pKey.getParameters());
                PrivateKeyInfo privInfo = new PrivateKeyInfo(algId, pKey);
                SubjectPublicKeyInfo pubInfo = new SubjectPublicKeyInfo(algId, pKey.getPublicKey().getBytes());
                return new PEMKeyPair(pubInfo, privInfo);
            } catch (IOException e) {
                throw e;
            } catch (Exception e2) {
                throw new PEMException("problem creating EC private key: " + e2.toString(), e2);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMParser$RSAKeyPairParser.class */
    private class RSAKeyPairParser implements PEMKeyPairParser {
        private RSAKeyPairParser() {
        }

        @Override // org.spongycastle.openssl.PEMKeyPairParser
        public PEMKeyPair parse(byte[] encoding) throws IOException {
            try {
                ASN1Sequence seq = ASN1Sequence.getInstance(encoding);
                if (seq.size() != 9) {
                    throw new PEMException("malformed sequence in RSA private key");
                }
                RSAPrivateKey keyStruct = RSAPrivateKey.getInstance(seq);
                RSAPublicKey pubSpec = new RSAPublicKey(keyStruct.getModulus(), keyStruct.getPublicExponent());
                AlgorithmIdentifier algId = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
                return new PEMKeyPair(new SubjectPublicKeyInfo(algId, pubSpec), new PrivateKeyInfo(algId, keyStruct));
            } catch (IOException e) {
                throw e;
            } catch (Exception e2) {
                throw new PEMException("problem creating RSA private key: " + e2.toString(), e2);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMParser$PublicKeyParser.class */
    private class PublicKeyParser implements PemObjectParser {
        public PublicKeyParser() {
        }

        @Override // org.spongycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject obj) throws IOException {
            return SubjectPublicKeyInfo.getInstance(obj.getContent());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMParser$RSAPublicKeyParser.class */
    private class RSAPublicKeyParser implements PemObjectParser {
        public RSAPublicKeyParser() {
        }

        @Override // org.spongycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject obj) throws IOException {
            try {
                RSAPublicKey rsaPubStructure = RSAPublicKey.getInstance(obj.getContent());
                return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE), rsaPubStructure);
            } catch (IOException e) {
                throw e;
            } catch (Exception e2) {
                throw new PEMException("problem extracting key: " + e2.toString(), e2);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMParser$X509CertificateParser.class */
    private class X509CertificateParser implements PemObjectParser {
        private X509CertificateParser() {
        }

        @Override // org.spongycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject obj) throws IOException {
            try {
                return new X509CertificateHolder(obj.getContent());
            } catch (Exception e) {
                throw new PEMException("problem parsing cert: " + e.toString(), e);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMParser$X509TrustedCertificateParser.class */
    private class X509TrustedCertificateParser implements PemObjectParser {
        private X509TrustedCertificateParser() {
        }

        @Override // org.spongycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject obj) throws IOException {
            try {
                return new X509TrustedCertificateBlock(obj.getContent());
            } catch (Exception e) {
                throw new PEMException("problem parsing cert: " + e.toString(), e);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMParser$X509CRLParser.class */
    private class X509CRLParser implements PemObjectParser {
        private X509CRLParser() {
        }

        @Override // org.spongycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject obj) throws IOException {
            try {
                return new X509CRLHolder(obj.getContent());
            } catch (Exception e) {
                throw new PEMException("problem parsing cert: " + e.toString(), e);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMParser$PKCS10CertificationRequestParser.class */
    private class PKCS10CertificationRequestParser implements PemObjectParser {
        private PKCS10CertificationRequestParser() {
        }

        @Override // org.spongycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject obj) throws IOException {
            try {
                return new PKCS10CertificationRequest(obj.getContent());
            } catch (Exception e) {
                throw new PEMException("problem parsing certrequest: " + e.toString(), e);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMParser$PKCS7Parser.class */
    private class PKCS7Parser implements PemObjectParser {
        private PKCS7Parser() {
        }

        @Override // org.spongycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject obj) throws IOException {
            try {
                ASN1InputStream aIn = new ASN1InputStream(obj.getContent());
                return ContentInfo.getInstance(aIn.readObject());
            } catch (Exception e) {
                throw new PEMException("problem parsing PKCS7 object: " + e.toString(), e);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMParser$X509AttributeCertificateParser.class */
    private class X509AttributeCertificateParser implements PemObjectParser {
        private X509AttributeCertificateParser() {
        }

        @Override // org.spongycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject obj) throws IOException {
            return new X509AttributeCertificateHolder(obj.getContent());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMParser$ECCurveParamsParser.class */
    private class ECCurveParamsParser implements PemObjectParser {
        private ECCurveParamsParser() {
        }

        @Override // org.spongycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject obj) throws IOException {
            try {
                Object param = ASN1Primitive.fromByteArray(obj.getContent());
                if (param instanceof ASN1ObjectIdentifier) {
                    return ASN1Primitive.fromByteArray(obj.getContent());
                }
                if (param instanceof ASN1Sequence) {
                    return X9ECParameters.getInstance(param);
                }
                return null;
            } catch (IOException e) {
                throw e;
            } catch (Exception e2) {
                throw new PEMException("exception extracting EC named curve: " + e2.toString());
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMParser$EncryptedPrivateKeyParser.class */
    private class EncryptedPrivateKeyParser implements PemObjectParser {
        public EncryptedPrivateKeyParser() {
        }

        @Override // org.spongycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject obj) throws IOException {
            try {
                return new PKCS8EncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo.getInstance(obj.getContent()));
            } catch (Exception e) {
                throw new PEMException("problem parsing ENCRYPTED PRIVATE KEY: " + e.toString(), e);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/openssl/PEMParser$PrivateKeyParser.class */
    private class PrivateKeyParser implements PemObjectParser {
        public PrivateKeyParser() {
        }

        @Override // org.spongycastle.util.io.pem.PemObjectParser
        public Object parseObject(PemObject obj) throws IOException {
            try {
                return PrivateKeyInfo.getInstance(obj.getContent());
            } catch (Exception e) {
                throw new PEMException("problem parsing PRIVATE KEY: " + e.toString(), e);
            }
        }
    }
}
