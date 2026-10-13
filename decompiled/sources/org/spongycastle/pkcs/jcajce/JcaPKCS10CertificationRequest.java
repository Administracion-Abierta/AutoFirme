package org.spongycastle.pkcs.jcajce;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Hashtable;
import org.spongycastle.asn1.pkcs.CertificationRequest;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x9.X9ObjectIdentifiers;
import org.spongycastle.jcajce.util.DefaultJcaJceHelper;
import org.spongycastle.jcajce.util.JcaJceHelper;
import org.spongycastle.jcajce.util.NamedJcaJceHelper;
import org.spongycastle.jcajce.util.ProviderJcaJceHelper;
import org.spongycastle.pkcs.PKCS10CertificationRequest;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pkcs/jcajce/JcaPKCS10CertificationRequest.class */
public class JcaPKCS10CertificationRequest extends PKCS10CertificationRequest {
    private static Hashtable keyAlgorithms = new Hashtable();
    private JcaJceHelper helper;

    static {
        keyAlgorithms.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
        keyAlgorithms.put(X9ObjectIdentifiers.id_dsa, "DSA");
    }

    public JcaPKCS10CertificationRequest(CertificationRequest certificationRequest) {
        super(certificationRequest);
        this.helper = new DefaultJcaJceHelper();
    }

    public JcaPKCS10CertificationRequest(byte[] encoding) throws IOException {
        super(encoding);
        this.helper = new DefaultJcaJceHelper();
    }

    public JcaPKCS10CertificationRequest(PKCS10CertificationRequest requestHolder) {
        super(requestHolder.toASN1Structure());
        this.helper = new DefaultJcaJceHelper();
    }

    public JcaPKCS10CertificationRequest setProvider(String providerName) {
        this.helper = new NamedJcaJceHelper(providerName);
        return this;
    }

    public JcaPKCS10CertificationRequest setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeyException {
        KeyFactory kFact;
        try {
            SubjectPublicKeyInfo keyInfo = getSubjectPublicKeyInfo();
            X509EncodedKeySpec xspec = new X509EncodedKeySpec(keyInfo.getEncoded());
            try {
                kFact = this.helper.createKeyFactory(keyInfo.getAlgorithm().getAlgorithm().getId());
            } catch (NoSuchAlgorithmException e) {
                if (keyAlgorithms.get(keyInfo.getAlgorithm().getAlgorithm()) != null) {
                    String keyAlgorithm = (String) keyAlgorithms.get(keyInfo.getAlgorithm().getAlgorithm());
                    kFact = this.helper.createKeyFactory(keyAlgorithm);
                } else {
                    throw e;
                }
            }
            return kFact.generatePublic(xspec);
        } catch (IOException e2) {
            throw new InvalidKeyException("error extracting key encoding");
        } catch (NoSuchProviderException e3) {
            throw new NoSuchAlgorithmException("cannot find provider: " + e3.getMessage());
        } catch (InvalidKeySpecException e4) {
            throw new InvalidKeyException("error decoding public key");
        }
    }
}
