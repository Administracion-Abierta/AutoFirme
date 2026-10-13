package org.bouncycastle.asn1.smime;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/smime/SMIMECapabilityVector.class */
public class SMIMECapabilityVector {
    private ASN1EncodableVector capabilities = new ASN1EncodableVector();

    public void addCapability(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.capabilities.add(new DERSequence(aSN1ObjectIdentifier));
    }

    public void addCapability(ASN1ObjectIdentifier aSN1ObjectIdentifier, int i) {
        this.capabilities.add(new DERSequence(aSN1ObjectIdentifier, new ASN1Integer(i)));
    }

    public void addCapability(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Encodable aSN1Encodable) {
        this.capabilities.add(new DERSequence(aSN1ObjectIdentifier, aSN1Encodable));
    }

    public ASN1EncodableVector toASN1EncodableVector() {
        return this.capabilities;
    }
}
