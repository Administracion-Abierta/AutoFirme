package org.spongycastle.asn1.x509;

import org.spongycastle.asn1.ASN1Boolean;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/x509/IssuingDistributionPoint.class */
public class IssuingDistributionPoint extends ASN1Object {
    private DistributionPointName distributionPoint;
    private boolean onlyContainsUserCerts;
    private boolean onlyContainsCACerts;
    private ReasonFlags onlySomeReasons;
    private boolean indirectCRL;
    private boolean onlyContainsAttributeCerts;
    private ASN1Sequence seq;

    public static IssuingDistributionPoint getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static IssuingDistributionPoint getInstance(Object obj) {
        if (obj instanceof IssuingDistributionPoint) {
            return (IssuingDistributionPoint) obj;
        }
        if (obj != null) {
            return new IssuingDistributionPoint(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public IssuingDistributionPoint(DistributionPointName distributionPoint, boolean onlyContainsUserCerts, boolean onlyContainsCACerts, ReasonFlags onlySomeReasons, boolean indirectCRL, boolean onlyContainsAttributeCerts) {
        this.distributionPoint = distributionPoint;
        this.indirectCRL = indirectCRL;
        this.onlyContainsAttributeCerts = onlyContainsAttributeCerts;
        this.onlyContainsCACerts = onlyContainsCACerts;
        this.onlyContainsUserCerts = onlyContainsUserCerts;
        this.onlySomeReasons = onlySomeReasons;
        ASN1EncodableVector vec = new ASN1EncodableVector();
        if (distributionPoint != null) {
            vec.add(new DERTaggedObject(true, 0, distributionPoint));
        }
        if (onlyContainsUserCerts) {
            vec.add(new DERTaggedObject(false, 1, ASN1Boolean.getInstance(true)));
        }
        if (onlyContainsCACerts) {
            vec.add(new DERTaggedObject(false, 2, ASN1Boolean.getInstance(true)));
        }
        if (onlySomeReasons != null) {
            vec.add(new DERTaggedObject(false, 3, onlySomeReasons));
        }
        if (indirectCRL) {
            vec.add(new DERTaggedObject(false, 4, ASN1Boolean.getInstance(true)));
        }
        if (onlyContainsAttributeCerts) {
            vec.add(new DERTaggedObject(false, 5, ASN1Boolean.getInstance(true)));
        }
        this.seq = new DERSequence(vec);
    }

    public IssuingDistributionPoint(DistributionPointName distributionPoint, boolean indirectCRL, boolean onlyContainsAttributeCerts) {
        this(distributionPoint, false, false, null, indirectCRL, onlyContainsAttributeCerts);
    }

    private IssuingDistributionPoint(ASN1Sequence seq) {
        this.seq = seq;
        for (int i = 0; i != seq.size(); i++) {
            ASN1TaggedObject o = ASN1TaggedObject.getInstance(seq.getObjectAt(i));
            switch (o.getTagNo()) {
                case 0:
                    this.distributionPoint = DistributionPointName.getInstance(o, true);
                    break;
                case 1:
                    this.onlyContainsUserCerts = ASN1Boolean.getInstance(o, false).isTrue();
                    break;
                case 2:
                    this.onlyContainsCACerts = ASN1Boolean.getInstance(o, false).isTrue();
                    break;
                case 3:
                    this.onlySomeReasons = new ReasonFlags(ReasonFlags.getInstance(o, false));
                    break;
                case 4:
                    this.indirectCRL = ASN1Boolean.getInstance(o, false).isTrue();
                    break;
                case 5:
                    this.onlyContainsAttributeCerts = ASN1Boolean.getInstance(o, false).isTrue();
                    break;
                default:
                    throw new IllegalArgumentException("unknown tag in IssuingDistributionPoint");
            }
        }
    }

    public boolean onlyContainsUserCerts() {
        return this.onlyContainsUserCerts;
    }

    public boolean onlyContainsCACerts() {
        return this.onlyContainsCACerts;
    }

    public boolean isIndirectCRL() {
        return this.indirectCRL;
    }

    public boolean onlyContainsAttributeCerts() {
        return this.onlyContainsAttributeCerts;
    }

    public DistributionPointName getDistributionPoint() {
        return this.distributionPoint;
    }

    public ReasonFlags getOnlySomeReasons() {
        return this.onlySomeReasons;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }

    public String toString() {
        String sep = Strings.lineSeparator();
        StringBuffer buf = new StringBuffer();
        buf.append("IssuingDistributionPoint: [");
        buf.append(sep);
        if (this.distributionPoint != null) {
            appendObject(buf, sep, "distributionPoint", this.distributionPoint.toString());
        }
        if (this.onlyContainsUserCerts) {
            appendObject(buf, sep, "onlyContainsUserCerts", booleanToString(this.onlyContainsUserCerts));
        }
        if (this.onlyContainsCACerts) {
            appendObject(buf, sep, "onlyContainsCACerts", booleanToString(this.onlyContainsCACerts));
        }
        if (this.onlySomeReasons != null) {
            appendObject(buf, sep, "onlySomeReasons", this.onlySomeReasons.toString());
        }
        if (this.onlyContainsAttributeCerts) {
            appendObject(buf, sep, "onlyContainsAttributeCerts", booleanToString(this.onlyContainsAttributeCerts));
        }
        if (this.indirectCRL) {
            appendObject(buf, sep, "indirectCRL", booleanToString(this.indirectCRL));
        }
        buf.append("]");
        buf.append(sep);
        return buf.toString();
    }

    private void appendObject(StringBuffer buf, String sep, String name, String value) {
        buf.append("    ");
        buf.append(name);
        buf.append(":");
        buf.append(sep);
        buf.append("    ");
        buf.append("    ");
        buf.append(value);
        buf.append(sep);
    }

    private String booleanToString(boolean value) {
        return value ? "true" : "false";
    }
}
