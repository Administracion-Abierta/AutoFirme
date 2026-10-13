package org.bouncycastle.cms.jcajce;

import java.io.OutputStream;
import java.security.Key;
import javax.crypto.Mac;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.jcajce.io.MacOutputStream;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.jcajce.JceGenericKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/JcePasswordAuthenticatedRecipient.class */
public class JcePasswordAuthenticatedRecipient extends JcePasswordRecipient {
    public JcePasswordAuthenticatedRecipient(char[] cArr) {
        super(cArr);
    }

    @Override // org.bouncycastle.cms.PasswordRecipient
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, byte[] bArr, byte[] bArr2) throws CMSException {
        final Key keyExtractSecretKey = extractSecretKey(algorithmIdentifier, algorithmIdentifier2, bArr, bArr2);
        final Mac macCreateContentMac = this.helper.createContentMac(keyExtractSecretKey, algorithmIdentifier2);
        return new RecipientOperator(new MacCalculator() { // from class: org.bouncycastle.cms.jcajce.JcePasswordAuthenticatedRecipient.1
            @Override // org.bouncycastle.operator.MacCalculator
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithmIdentifier2;
            }

            @Override // org.bouncycastle.operator.MacCalculator
            public GenericKey getKey() {
                return new JceGenericKey(algorithmIdentifier2, keyExtractSecretKey);
            }

            @Override // org.bouncycastle.operator.MacCalculator
            public OutputStream getOutputStream() {
                return new MacOutputStream(macCreateContentMac);
            }

            @Override // org.bouncycastle.operator.MacCalculator
            public byte[] getMac() {
                return macCreateContentMac.doFinal();
            }
        });
    }
}
