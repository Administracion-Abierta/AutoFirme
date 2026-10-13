package org.spongycastle.cms.bc;

import java.io.InputStream;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.RecipientOperator;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.StreamCipher;
import org.spongycastle.crypto.io.CipherInputStream;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.operator.InputDecryptor;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/bc/BcPasswordEnvelopedRecipient.class */
public class BcPasswordEnvelopedRecipient extends BcPasswordRecipient {
    public BcPasswordEnvelopedRecipient(char[] password) {
        super(password);
    }

    @Override // org.spongycastle.cms.PasswordRecipient
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier keyEncryptionAlgorithm, final AlgorithmIdentifier contentEncryptionAlgorithm, byte[] derivedKey, byte[] encryptedContentEncryptionKey) throws CMSException {
        KeyParameter secretKey = extractSecretKey(keyEncryptionAlgorithm, contentEncryptionAlgorithm, derivedKey, encryptedContentEncryptionKey);
        final Object dataCipher = EnvelopedDataHelper.createContentCipher(false, secretKey, contentEncryptionAlgorithm);
        return new RecipientOperator(new InputDecryptor() { // from class: org.spongycastle.cms.bc.BcPasswordEnvelopedRecipient.1
            @Override // org.spongycastle.operator.InputDecryptor
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return contentEncryptionAlgorithm;
            }

            @Override // org.spongycastle.operator.InputDecryptor
            public InputStream getInputStream(InputStream dataOut) {
                if (dataCipher instanceof BufferedBlockCipher) {
                    return new CipherInputStream(dataOut, (BufferedBlockCipher) dataCipher);
                }
                return new CipherInputStream(dataOut, (StreamCipher) dataCipher);
            }
        });
    }
}
