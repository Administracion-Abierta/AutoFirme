package org.bouncycastle.cms.jcajce;

import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/JceKEKAuthEnvelopedRecipient.class */
public class JceKEKAuthEnvelopedRecipient extends JceKEKRecipient {
    public JceKEKAuthEnvelopedRecipient(SecretKey secretKey) {
        super(secretKey);
    }

    @Override // org.bouncycastle.cms.KEKRecipient
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, byte[] bArr) throws CMSException {
        return new RecipientOperator(new CMSInputAEADDecryptor(algorithmIdentifier2, this.contentHelper.createContentCipher(extractSecretKey(algorithmIdentifier, algorithmIdentifier2, bArr), algorithmIdentifier2)));
    }
}
