package org.spongycastle.asn1.crmf;

import java.math.BigInteger;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.GeneralName;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/crmf/CertId.class */
public class CertId extends ASN1Object {
    private GeneralName issuer;
    private ASN1Integer serialNumber;

    private CertId(ASN1Sequence seq) {
        this.issuer = GeneralName.getInstance(seq.getObjectAt(0));
        this.serialNumber = ASN1Integer.getInstance(seq.getObjectAt(1));
    }

    public static CertId getInstance(Object o) {
        if (o instanceof CertId) {
            return (CertId) o;
        }
        if (o != null) {
            return new CertId(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public static CertId getInstance(ASN1TaggedObject obj, boolean isExplicit) {
        return getInstance(ASN1Sequence.getInstance(obj, isExplicit));
    }

    public CertId(GeneralName issuer, BigInteger serialNumber) {
        this(issuer, new ASN1Integer(serialNumber));
    }

    public CertId(GeneralName issuer, ASN1Integer serialNumber) {
        this.issuer = issuer;
        this.serialNumber = serialNumber;
    }

    public GeneralName getIssuer() {
        return this.issuer;
    }

    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.issuer);
        v.add(this.serialNumber);
        return new DERSequence(v);
    }
}
