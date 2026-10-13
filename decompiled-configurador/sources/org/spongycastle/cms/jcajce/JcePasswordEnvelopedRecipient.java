package org.spongycastle.cms.jcajce;

import java.io.InputStream;
import java.security.Key;
import javax.crypto.Cipher;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.RecipientOperator;
import org.spongycastle.jcajce.io.CipherInputStream;
import org.spongycastle.operator.InputDecryptor;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/jcajce/JcePasswordEnvelopedRecipient.class */
public class JcePasswordEnvelopedRecipient extends JcePasswordRecipient {
    public JcePasswordEnvelopedRecipient(char[] password) {
        super(password);
    }

    @Override // org.spongycastle.cms.PasswordRecipient
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier keyEncryptionAlgorithm, final AlgorithmIdentifier contentEncryptionAlgorithm, byte[] derivedKey, byte[] encryptedContentEncryptionKey) throws CMSException {
        Key secretKey = extractSecretKey(keyEncryptionAlgorithm, contentEncryptionAlgorithm, derivedKey, encryptedContentEncryptionKey);
        final Cipher dataCipher = this.helper.createContentCipher(secretKey, contentEncryptionAlgorithm);
        return new RecipientOperator(new InputDecryptor() { // from class: org.spongycastle.cms.jcajce.JcePasswordEnvelopedRecipient.1
            @Override // org.spongycastle.operator.InputDecryptor
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return contentEncryptionAlgorithm;
            }

            @Override // org.spongycastle.operator.InputDecryptor
            public InputStream getInputStream(InputStream dataOut) {
                return new CipherInputStream(dataOut, dataCipher);
            }
        });
    }
}
