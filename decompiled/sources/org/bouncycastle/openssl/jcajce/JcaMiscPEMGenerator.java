package org.bouncycastle.openssl.jcajce;

import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.jcajce.JcaX509CRLHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.openssl.MiscPEMGenerator;
import org.bouncycastle.openssl.PEMEncryptor;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/jcajce/JcaMiscPEMGenerator.class */
public class JcaMiscPEMGenerator extends MiscPEMGenerator {
    private Object obj;
    private String algorithm;
    private char[] password;
    private SecureRandom random;
    private Provider provider;

    public JcaMiscPEMGenerator(Object obj) throws IOException {
        super(convertObject(obj));
    }

    public JcaMiscPEMGenerator(Object obj, PEMEncryptor pEMEncryptor) throws IOException {
        super(convertObject(obj), pEMEncryptor);
    }

    private static Object convertObject(Object obj) throws IOException {
        if (obj instanceof X509Certificate) {
            try {
                return new JcaX509CertificateHolder((X509Certificate) obj);
            } catch (CertificateEncodingException e) {
                throw new IllegalArgumentException("Cannot encode object: " + e.toString());
            }
        }
        if (obj instanceof X509CRL) {
            try {
                return new JcaX509CRLHolder((X509CRL) obj);
            } catch (CRLException e2) {
                throw new IllegalArgumentException("Cannot encode object: " + e2.toString());
            }
        }
        if (obj instanceof KeyPair) {
            return convertObject(((KeyPair) obj).getPrivate());
        }
        if (obj instanceof PrivateKey) {
            return PrivateKeyInfo.getInstance(((Key) obj).getEncoded());
        }
        return obj instanceof PublicKey ? SubjectPublicKeyInfo.getInstance(((PublicKey) obj).getEncoded()) : obj;
    }
}
