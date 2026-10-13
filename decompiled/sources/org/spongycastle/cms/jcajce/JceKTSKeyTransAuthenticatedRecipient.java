package org.spongycastle.cms.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.security.PrivateKey;
import javax.crypto.Mac;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.KeyTransRecipientId;
import org.spongycastle.cms.RecipientOperator;
import org.spongycastle.jcajce.io.MacOutputStream;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.MacCalculator;
import org.spongycastle.operator.jcajce.JceGenericKey;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/JceKTSKeyTransAuthenticatedRecipient.class */
public class JceKTSKeyTransAuthenticatedRecipient extends JceKTSKeyTransRecipient {
    public JceKTSKeyTransAuthenticatedRecipient(PrivateKey recipientKey, KeyTransRecipientId recipientId) throws IOException {
        super(recipientKey, getPartyVInfoFromRID(recipientId));
    }

    @Override // org.spongycastle.cms.KeyTransRecipient
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier keyEncryptionAlgorithm, final AlgorithmIdentifier contentMacAlgorithm, byte[] encryptedContentEncryptionKey) throws CMSException {
        final Key secretKey = extractSecretKey(keyEncryptionAlgorithm, contentMacAlgorithm, encryptedContentEncryptionKey);
        final Mac dataMac = this.contentHelper.createContentMac(secretKey, contentMacAlgorithm);
        return new RecipientOperator(new MacCalculator() { // from class: org.spongycastle.cms.jcajce.JceKTSKeyTransAuthenticatedRecipient.1
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
