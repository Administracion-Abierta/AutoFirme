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
import org.spongycastle.operator.bc.BcSymmetricKeyUnwrapper;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/bc/BcKEKEnvelopedRecipient.class */
public class BcKEKEnvelopedRecipient extends BcKEKRecipient {
    public BcKEKEnvelopedRecipient(BcSymmetricKeyUnwrapper unwrapper) {
        super(unwrapper);
    }

    @Override // org.spongycastle.cms.KEKRecipient
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier keyEncryptionAlgorithm, final AlgorithmIdentifier contentEncryptionAlgorithm, byte[] encryptedContentEncryptionKey) throws CMSException {
        KeyParameter secretKey = (KeyParameter) extractSecretKey(keyEncryptionAlgorithm, contentEncryptionAlgorithm, encryptedContentEncryptionKey);
        final Object dataCipher = EnvelopedDataHelper.createContentCipher(false, secretKey, contentEncryptionAlgorithm);
        return new RecipientOperator(new InputDecryptor() { // from class: org.spongycastle.cms.bc.BcKEKEnvelopedRecipient.1
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
