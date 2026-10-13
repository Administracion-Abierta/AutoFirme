package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KEMRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/KEMRecipientInformation.class */
public class KEMRecipientInformation extends RecipientInformation {
    private KEMRecipientInfo info;

    KEMRecipientInformation(KEMRecipientInfo kEMRecipientInfo, AlgorithmIdentifier algorithmIdentifier, CMSSecureReadable cMSSecureReadable) {
        super(kEMRecipientInfo.getKem(), algorithmIdentifier, cMSSecureReadable);
        this.info = kEMRecipientInfo;
        RecipientIdentifier recipientIdentifier = kEMRecipientInfo.getRecipientIdentifier();
        if (recipientIdentifier.isTagged()) {
            this.rid = new KEMRecipientId(ASN1OctetString.getInstance(recipientIdentifier.getId()).getOctets());
        } else {
            IssuerAndSerialNumber issuerAndSerialNumber = IssuerAndSerialNumber.getInstance(recipientIdentifier.getId());
            this.rid = new KEMRecipientId(issuerAndSerialNumber.getName(), issuerAndSerialNumber.getSerialNumber().getValue());
        }
    }

    public AlgorithmIdentifier getKdfAlgorithm() {
        return this.info.getKdf();
    }

    public byte[] getUkm() {
        return Arrays.clone(this.info.getUkm());
    }

    public byte[] getEncapsulation() {
        return Arrays.clone(this.info.getKemct().getOctets());
    }

    @Override // org.bouncycastle.cms.RecipientInformation
    protected RecipientOperator getRecipientOperator(Recipient recipient) throws CMSException {
        return ((KEMRecipient) recipient).getRecipientOperator(new AlgorithmIdentifier(this.keyEncAlg.getAlgorithm(), this.info), this.messageAlgorithm, this.info.getEncryptedKey().getOctets());
    }
}
