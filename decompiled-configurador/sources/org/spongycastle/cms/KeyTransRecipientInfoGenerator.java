package org.spongycastle.cms;

import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.cms.IssuerAndSerialNumber;
import org.spongycastle.asn1.cms.KeyTransRecipientInfo;
import org.spongycastle.asn1.cms.RecipientIdentifier;
import org.spongycastle.asn1.cms.RecipientInfo;
import org.spongycastle.operator.AsymmetricKeyWrapper;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.OperatorException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/KeyTransRecipientInfoGenerator.class */
public abstract class KeyTransRecipientInfoGenerator implements RecipientInfoGenerator {
    protected final AsymmetricKeyWrapper wrapper;
    private IssuerAndSerialNumber issuerAndSerial;
    private byte[] subjectKeyIdentifier;

    protected KeyTransRecipientInfoGenerator(IssuerAndSerialNumber issuerAndSerial, AsymmetricKeyWrapper wrapper) {
        this.issuerAndSerial = issuerAndSerial;
        this.wrapper = wrapper;
    }

    protected KeyTransRecipientInfoGenerator(byte[] subjectKeyIdentifier, AsymmetricKeyWrapper wrapper) {
        this.subjectKeyIdentifier = subjectKeyIdentifier;
        this.wrapper = wrapper;
    }

    @Override // org.spongycastle.cms.RecipientInfoGenerator
    public final RecipientInfo generate(GenericKey contentEncryptionKey) throws CMSException {
        RecipientIdentifier recipId;
        try {
            byte[] encryptedKeyBytes = this.wrapper.generateWrappedKey(contentEncryptionKey);
            if (this.issuerAndSerial != null) {
                recipId = new RecipientIdentifier(this.issuerAndSerial);
            } else {
                recipId = new RecipientIdentifier((ASN1OctetString) new DEROctetString(this.subjectKeyIdentifier));
            }
            return new RecipientInfo(new KeyTransRecipientInfo(recipId, this.wrapper.getAlgorithmIdentifier(), new DEROctetString(encryptedKeyBytes)));
        } catch (OperatorException e) {
            throw new CMSException("exception wrapping content key: " + e.getMessage(), e);
        }
    }
}
