package org.bouncycastle.mozilla.jcajce;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.mozilla.SignedPublicKeyAndChallenge;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mozilla/jcajce/JcaSignedPublicKeyAndChallenge.class */
public class JcaSignedPublicKeyAndChallenge extends SignedPublicKeyAndChallenge {
    JcaJceHelper helper;

    private JcaSignedPublicKeyAndChallenge(org.bouncycastle.asn1.mozilla.SignedPublicKeyAndChallenge signedPublicKeyAndChallenge, JcaJceHelper jcaJceHelper) {
        super(signedPublicKeyAndChallenge);
        this.helper = new DefaultJcaJceHelper();
        this.helper = jcaJceHelper;
    }

    public JcaSignedPublicKeyAndChallenge(byte[] bArr) {
        super(bArr);
        this.helper = new DefaultJcaJceHelper();
    }

    public JcaSignedPublicKeyAndChallenge setProvider(String str) {
        return new JcaSignedPublicKeyAndChallenge(this.spkacSeq, new NamedJcaJceHelper(str));
    }

    public JcaSignedPublicKeyAndChallenge setProvider(Provider provider) {
        return new JcaSignedPublicKeyAndChallenge(this.spkacSeq, new ProviderJcaJceHelper(provider));
    }

    public PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        try {
            SubjectPublicKeyInfo subjectPublicKeyInfo = this.spkacSeq.getPublicKeyAndChallenge().getSubjectPublicKeyInfo();
            return this.helper.createKeyFactory(subjectPublicKeyInfo.getAlgorithm().getAlgorithm().getId()).generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));
        } catch (Exception e) {
            throw new InvalidKeyException("error encoding public key");
        }
    }
}
