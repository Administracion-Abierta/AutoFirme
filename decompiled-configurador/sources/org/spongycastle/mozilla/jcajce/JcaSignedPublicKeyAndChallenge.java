package org.spongycastle.mozilla.jcajce;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.jcajce.util.DefaultJcaJceHelper;
import org.spongycastle.jcajce.util.JcaJceHelper;
import org.spongycastle.jcajce.util.NamedJcaJceHelper;
import org.spongycastle.jcajce.util.ProviderJcaJceHelper;
import org.spongycastle.mozilla.SignedPublicKeyAndChallenge;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/mozilla/jcajce/JcaSignedPublicKeyAndChallenge.class */
public class JcaSignedPublicKeyAndChallenge extends SignedPublicKeyAndChallenge {
    JcaJceHelper helper;

    private JcaSignedPublicKeyAndChallenge(org.spongycastle.asn1.mozilla.SignedPublicKeyAndChallenge struct, JcaJceHelper helper) {
        super(struct);
        this.helper = new DefaultJcaJceHelper();
        this.helper = helper;
    }

    public JcaSignedPublicKeyAndChallenge(byte[] bytes) {
        super(bytes);
        this.helper = new DefaultJcaJceHelper();
    }

    public JcaSignedPublicKeyAndChallenge setProvider(String providerName) {
        return new JcaSignedPublicKeyAndChallenge(this.spkacSeq, new NamedJcaJceHelper(providerName));
    }

    public JcaSignedPublicKeyAndChallenge setProvider(Provider provider) {
        return new JcaSignedPublicKeyAndChallenge(this.spkacSeq, new ProviderJcaJceHelper(provider));
    }

    public PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        try {
            SubjectPublicKeyInfo subjectPublicKeyInfo = this.spkacSeq.getPublicKeyAndChallenge().getSubjectPublicKeyInfo();
            X509EncodedKeySpec xspec = new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded());
            AlgorithmIdentifier keyAlg = subjectPublicKeyInfo.getAlgorithm();
            KeyFactory factory = this.helper.createKeyFactory(keyAlg.getAlgorithm().getId());
            return factory.generatePublic(xspec);
        } catch (Exception e) {
            throw new InvalidKeyException("error encoding public key");
        }
    }
}
