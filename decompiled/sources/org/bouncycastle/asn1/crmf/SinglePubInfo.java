package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.GeneralName;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/crmf/SinglePubInfo.class */
public class SinglePubInfo extends ASN1Object {
    public static final ASN1Integer dontCare = new ASN1Integer(0);
    public static final ASN1Integer x500 = new ASN1Integer(1);
    public static final ASN1Integer web = new ASN1Integer(2);
    public static final ASN1Integer ldap = new ASN1Integer(3);
    private ASN1Integer pubMethod;
    private GeneralName pubLocation;

    private SinglePubInfo(ASN1Sequence aSN1Sequence) {
        this.pubMethod = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0));
        if (aSN1Sequence.size() == 2) {
            this.pubLocation = GeneralName.getInstance(aSN1Sequence.getObjectAt(1));
        }
    }

    public static SinglePubInfo getInstance(Object obj) {
        if (obj instanceof SinglePubInfo) {
            return (SinglePubInfo) obj;
        }
        if (obj != null) {
            return new SinglePubInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public SinglePubInfo(ASN1Integer aSN1Integer, GeneralName generalName) {
        this.pubMethod = aSN1Integer;
        this.pubLocation = generalName;
    }

    public ASN1Integer getPubMethod() {
        return this.pubMethod;
    }

    public GeneralName getPubLocation() {
        return this.pubLocation;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(2);
        aSN1EncodableVector.add(this.pubMethod);
        if (this.pubLocation != null) {
            aSN1EncodableVector.add(this.pubLocation);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
