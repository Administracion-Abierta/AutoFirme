package org.spongycastle.asn1.smime;

import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cms.Attribute;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/smime/SMIMECapabilitiesAttribute.class */
public class SMIMECapabilitiesAttribute extends Attribute {
    public SMIMECapabilitiesAttribute(SMIMECapabilityVector capabilities) {
        super(SMIMEAttributes.smimeCapabilities, new DERSet(new DERSequence(capabilities.toASN1EncodableVector())));
    }
}
