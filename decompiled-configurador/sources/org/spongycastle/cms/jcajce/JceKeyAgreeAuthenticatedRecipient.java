package org.spongycastle.cms.jcajce;

import java.io.OutputStream;
import java.security.Key;
import java.security.PrivateKey;
import javax.crypto.Mac;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.RecipientOperator;
import org.spongycastle.jcajce.io.MacOutputStream;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.MacCalculator;
import org.spongycastle.operator.jcajce.JceGenericKey;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/jcajce/JceKeyAgreeAuthenticatedRecipient.class */
public class JceKeyAgreeAuthenticatedRecipient extends JceKeyAgreeRecipient {
    public JceKeyAgreeAuthenticatedRecipient(PrivateKey recipientKey) {
        super(recipientKey);
    }

    @Override // org.spongycastle.cms.KeyAgreeRecipient
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier keyEncryptionAlgorithm, final AlgorithmIdentifier contentMacAlgorithm, SubjectPublicKeyInfo senderPublicKey, ASN1OctetString userKeyingMaterial, byte[] encryptedContentKey) throws CMSException {
        final Key secretKey = extractSecretKey(keyEncryptionAlgorithm, contentMacAlgorithm, senderPublicKey, userKeyingMaterial, encryptedContentKey);
        final Mac dataMac = this.contentHelper.createContentMac(secretKey, contentMacAlgorithm);
        return new RecipientOperator(new MacCalculator() { // from class: org.spongycastle.cms.jcajce.JceKeyAgreeAuthenticatedRecipient.1
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
