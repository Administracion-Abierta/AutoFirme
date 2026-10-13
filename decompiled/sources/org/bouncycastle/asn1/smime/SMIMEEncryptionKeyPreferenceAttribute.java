package org.bouncycastle.asn1.smime;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.RecipientKeyIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/smime/SMIMEEncryptionKeyPreferenceAttribute.class */
public class SMIMEEncryptionKeyPreferenceAttribute extends Attribute {
    public SMIMEEncryptionKeyPreferenceAttribute(IssuerAndSerialNumber issuerAndSerialNumber) {
        super(SMIMEAttributes.encrypKeyPref, new DERSet(new DERTaggedObject(false, 0, (ASN1Encodable) issuerAndSerialNumber)));
    }

    public SMIMEEncryptionKeyPreferenceAttribute(RecipientKeyIdentifier recipientKeyIdentifier) {
        super(SMIMEAttributes.encrypKeyPref, new DERSet(new DERTaggedObject(false, 1, (ASN1Encodable) recipientKeyIdentifier)));
    }

    public SMIMEEncryptionKeyPreferenceAttribute(ASN1OctetString aSN1OctetString) {
        super(SMIMEAttributes.encrypKeyPref, new DERSet(new DERTaggedObject(false, 2, (ASN1Encodable) aSN1OctetString)));
    }
}
