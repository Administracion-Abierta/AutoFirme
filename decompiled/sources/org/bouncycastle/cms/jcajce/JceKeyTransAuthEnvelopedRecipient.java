package org.bouncycastle.cms.jcajce;

import java.security.PrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/JceKeyTransAuthEnvelopedRecipient.class */
public class JceKeyTransAuthEnvelopedRecipient extends JceKeyTransRecipient {
    public JceKeyTransAuthEnvelopedRecipient(PrivateKey privateKey) {
        super(privateKey);
    }

    @Override // org.bouncycastle.cms.KeyTransRecipient
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, byte[] bArr) throws CMSException {
        return new RecipientOperator(new CMSInputAEADDecryptor(algorithmIdentifier2, this.contentHelper.createContentCipher(extractSecretKey(algorithmIdentifier, algorithmIdentifier2, bArr), algorithmIdentifier2)));
    }
}
