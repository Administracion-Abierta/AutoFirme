package org.spongycastle.cms.jcajce;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.PrivateKey;
import javax.crypto.Cipher;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.KeyTransRecipientId;
import org.spongycastle.cms.RecipientOperator;
import org.spongycastle.jcajce.io.CipherInputStream;
import org.spongycastle.operator.InputDecryptor;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/jcajce/JceKTSKeyTransEnvelopedRecipient.class */
public class JceKTSKeyTransEnvelopedRecipient extends JceKTSKeyTransRecipient {
    public JceKTSKeyTransEnvelopedRecipient(PrivateKey recipientKey, KeyTransRecipientId recipientId) throws IOException {
        super(recipientKey, getPartyVInfoFromRID(recipientId));
    }

    @Override // org.spongycastle.cms.KeyTransRecipient
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier keyEncryptionAlgorithm, final AlgorithmIdentifier contentEncryptionAlgorithm, byte[] encryptedContentEncryptionKey) throws CMSException {
        Key secretKey = extractSecretKey(keyEncryptionAlgorithm, contentEncryptionAlgorithm, encryptedContentEncryptionKey);
        final Cipher dataCipher = this.contentHelper.createContentCipher(secretKey, contentEncryptionAlgorithm);
        return new RecipientOperator(new InputDecryptor() { // from class: org.spongycastle.cms.jcajce.JceKTSKeyTransEnvelopedRecipient.1
            @Override // org.spongycastle.operator.InputDecryptor
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return contentEncryptionAlgorithm;
            }

            @Override // org.spongycastle.operator.InputDecryptor
            public InputStream getInputStream(InputStream dataIn) {
                return new CipherInputStream(dataIn, dataCipher);
            }
        });
    }
}
