package org.spongycastle.cms.jcajce;

import java.io.InputStream;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.RecipientOperator;
import org.spongycastle.jcajce.io.CipherInputStream;
import org.spongycastle.operator.InputDecryptor;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/JceKEKEnvelopedRecipient.class */
public class JceKEKEnvelopedRecipient extends JceKEKRecipient {
    public JceKEKEnvelopedRecipient(SecretKey recipientKey) {
        super(recipientKey);
    }

    @Override // org.spongycastle.cms.KEKRecipient
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier keyEncryptionAlgorithm, final AlgorithmIdentifier contentEncryptionAlgorithm, byte[] encryptedContentEncryptionKey) throws CMSException {
        Key secretKey = extractSecretKey(keyEncryptionAlgorithm, contentEncryptionAlgorithm, encryptedContentEncryptionKey);
        final Cipher dataCipher = this.contentHelper.createContentCipher(secretKey, contentEncryptionAlgorithm);
        return new RecipientOperator(new InputDecryptor() { // from class: org.spongycastle.cms.jcajce.JceKEKEnvelopedRecipient.1
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
