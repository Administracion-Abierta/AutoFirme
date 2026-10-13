package org.spongycastle.cms;

import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.cms.KEKIdentifier;
import org.spongycastle.asn1.cms.KEKRecipientInfo;
import org.spongycastle.asn1.cms.RecipientInfo;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.OperatorException;
import org.spongycastle.operator.SymmetricKeyWrapper;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/KEKRecipientInfoGenerator.class */
public abstract class KEKRecipientInfoGenerator implements RecipientInfoGenerator {
    private final KEKIdentifier kekIdentifier;
    protected final SymmetricKeyWrapper wrapper;

    protected KEKRecipientInfoGenerator(KEKIdentifier kekIdentifier, SymmetricKeyWrapper wrapper) {
        this.kekIdentifier = kekIdentifier;
        this.wrapper = wrapper;
    }

    @Override // org.spongycastle.cms.RecipientInfoGenerator
    public final RecipientInfo generate(GenericKey contentEncryptionKey) throws CMSException {
        try {
            ASN1OctetString encryptedKey = new DEROctetString(this.wrapper.generateWrappedKey(contentEncryptionKey));
            return new RecipientInfo(new KEKRecipientInfo(this.kekIdentifier, this.wrapper.getAlgorithmIdentifier(), encryptedKey));
        } catch (OperatorException e) {
            throw new CMSException("exception wrapping content key: " + e.getMessage(), e);
        }
    }
}
