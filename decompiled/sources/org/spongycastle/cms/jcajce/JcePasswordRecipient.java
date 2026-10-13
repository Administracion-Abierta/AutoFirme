package org.spongycastle.cms.jcajce;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.PasswordRecipient;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/JcePasswordRecipient.class */
public abstract class JcePasswordRecipient implements PasswordRecipient {
    private int schemeID = 1;
    protected EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    private char[] password;

    JcePasswordRecipient(char[] password) {
        this.password = password;
    }

    public JcePasswordRecipient setPasswordConversionScheme(int schemeID) {
        this.schemeID = schemeID;
        return this;
    }

    public JcePasswordRecipient setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JcePasswordRecipient setProvider(String providerName) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(providerName));
        return this;
    }

    protected Key extractSecretKey(AlgorithmIdentifier keyEncryptionAlgorithm, AlgorithmIdentifier contentEncryptionAlgorithm, byte[] derivedKey, byte[] encryptedContentEncryptionKey) throws CMSException {
        Cipher keyEncryptionCipher = this.helper.createRFC3211Wrapper(keyEncryptionAlgorithm.getAlgorithm());
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(ASN1OctetString.getInstance(keyEncryptionAlgorithm.getParameters()).getOctets());
            keyEncryptionCipher.init(4, new SecretKeySpec(derivedKey, keyEncryptionCipher.getAlgorithm()), ivSpec);
            return keyEncryptionCipher.unwrap(encryptedContentEncryptionKey, contentEncryptionAlgorithm.getAlgorithm().getId(), 3);
        } catch (GeneralSecurityException e) {
            throw new CMSException("cannot process content encryption key: " + e.getMessage(), e);
        }
    }

    @Override // org.spongycastle.cms.PasswordRecipient
    public byte[] calculateDerivedKey(int schemeID, AlgorithmIdentifier derivationAlgorithm, int keySize) throws CMSException {
        return this.helper.calculateDerivedKey(schemeID, this.password, derivationAlgorithm, keySize);
    }

    @Override // org.spongycastle.cms.PasswordRecipient
    public int getPasswordConversionScheme() {
        return this.schemeID;
    }

    @Override // org.spongycastle.cms.PasswordRecipient
    public char[] getPassword() {
        return this.password;
    }
}
