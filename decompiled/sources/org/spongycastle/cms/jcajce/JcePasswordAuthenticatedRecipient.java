package org.spongycastle.cms.jcajce;

import java.io.OutputStream;
import java.security.Key;
import javax.crypto.Mac;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.RecipientOperator;
import org.spongycastle.jcajce.io.MacOutputStream;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.MacCalculator;
import org.spongycastle.operator.jcajce.JceGenericKey;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/JcePasswordAuthenticatedRecipient.class */
public class JcePasswordAuthenticatedRecipient extends JcePasswordRecipient {
    public JcePasswordAuthenticatedRecipient(char[] password) {
        super(password);
    }

    @Override // org.spongycastle.cms.PasswordRecipient
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier keyEncryptionAlgorithm, final AlgorithmIdentifier contentMacAlgorithm, byte[] derivedKey, byte[] encryptedContentEncryptionKey) throws CMSException {
        final Key secretKey = extractSecretKey(keyEncryptionAlgorithm, contentMacAlgorithm, derivedKey, encryptedContentEncryptionKey);
        final Mac dataMac = this.helper.createContentMac(secretKey, contentMacAlgorithm);
        return new RecipientOperator(new MacCalculator() { // from class: org.spongycastle.cms.jcajce.JcePasswordAuthenticatedRecipient.1
            @Override // org.spongycastle.operator.MacCalculator
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return contentMacAlgorithm;
            }

            @Override // org.spongycastle.operator.MacCalculator
            public GenericKey getKey() {
                return new JceGenericKey(contentMacAlgorithm, secretKey);
            }

            @Override // org.spongycastle.operator.MacCalculator
            public OutputStream getOutputStream() {
                return new MacOutputStream(dataMac);
            }

            @Override // org.spongycastle.operator.MacCalculator
            public byte[] getMac() {
                return dataMac.doFinal();
            }
        });
    }
}
