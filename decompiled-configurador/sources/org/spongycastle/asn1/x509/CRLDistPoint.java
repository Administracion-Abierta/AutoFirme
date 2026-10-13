package org.spongycastle.asn1.x509;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/x509/CRLDistPoint.class */
public class CRLDistPoint extends ASN1Object {
    ASN1Sequence seq;

    public static CRLDistPoint getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static CRLDistPoint getInstance(Object obj) {
        if (obj instanceof CRLDistPoint) {
            return (CRLDistPoint) obj;
        }
        if (obj != null) {
            return new CRLDistPoint(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private CRLDistPoint(ASN1Sequence seq) {
        this.seq = null;
        this.seq = seq;
    }

    public CRLDistPoint(DistributionPoint[] points) {
        this.seq = null;
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (int i = 0; i != points.length; i++) {
            v.add(points[i]);
        }
        this.seq = new DERSequence(v);
    }

    public DistributionPoint[] getDistributionPoints() {
        DistributionPoint[] dp = new DistributionPoint[this.seq.size()];
        for (int i = 0; i != this.seq.size(); i++) {
            dp[i] = DistributionPoint.getInstance(this.seq.getObjectAt(i));
        }
        return dp;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String sep = Strings.lineSeparator();
        buf.append("CRLDistPoint:");
        buf.append(sep);
        DistributionPoint[] dp = getDistributionPoints();
        for (int i = 0; i != dp.length; i++) {
            buf.append("    ");
            buf.append(dp[i]);
            buf.append(sep);
        }
        return buf.toString();
    }
}
