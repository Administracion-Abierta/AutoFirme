package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/tsp/Accuracy.class */
public class Accuracy extends ASN1Object {
    ASN1Integer seconds;
    ASN1Integer millis;
    ASN1Integer micros;
    protected static final int MIN_MILLIS = 1;
    protected static final int MAX_MILLIS = 999;
    protected static final int MIN_MICROS = 1;
    protected static final int MAX_MICROS = 999;

    protected Accuracy() {
    }

    public Accuracy(ASN1Integer aSN1Integer, ASN1Integer aSN1Integer2, ASN1Integer aSN1Integer3) {
        int iIntValueExact;
        int iIntValueExact2;
        if (null != aSN1Integer2 && ((iIntValueExact2 = aSN1Integer2.intValueExact()) < 1 || iIntValueExact2 > 999)) {
            throw new IllegalArgumentException("Invalid millis field : not in (1..999)");
        }
        if (null != aSN1Integer3 && ((iIntValueExact = aSN1Integer3.intValueExact()) < 1 || iIntValueExact > 999)) {
            throw new IllegalArgumentException("Invalid micros field : not in (1..999)");
        }
        this.seconds = aSN1Integer;
        this.millis = aSN1Integer2;
        this.micros = aSN1Integer3;
    }

    private Accuracy(ASN1Sequence aSN1Sequence) {
        this.seconds = null;
        this.millis = null;
        this.micros = null;
        for (int i = 0; i < aSN1Sequence.size(); i++) {
            if (aSN1Sequence.getObjectAt(i) instanceof ASN1Integer) {
                this.seconds = (ASN1Integer) aSN1Sequence.getObjectAt(i);
            } else if (aSN1Sequence.getObjectAt(i) instanceof ASN1TaggedObject) {
                ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) aSN1Sequence.getObjectAt(i);
                switch (aSN1TaggedObject.getTagNo()) {
                    case 0:
                        this.millis = ASN1Integer.getInstance(aSN1TaggedObject, false);
                        int iIntValueExact = this.millis.intValueExact();
                        if (iIntValueExact < 1 || iIntValueExact > 999) {
                            throw new IllegalArgumentException("Invalid millis field : not in (1..999)");
                        }
                        break;
                        break;
                    case 1:
                        this.micros = ASN1Integer.getInstance(aSN1TaggedObject, false);
                        int iIntValueExact2 = this.micros.intValueExact();
                        if (iIntValueExact2 < 1 || iIntValueExact2 > 999) {
                            throw new IllegalArgumentException("Invalid micros field : not in (1..999)");
                        }
                        break;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid tag number");
                }
            } else {
                continue;
            }
        }
    }

    public static Accuracy getInstance(Object obj) {
        if (obj instanceof Accuracy) {
            return (Accuracy) obj;
        }
        if (obj != null) {
            return new Accuracy(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1Integer getSeconds() {
        return this.seconds;
    }

    public ASN1Integer getMillis() {
        return this.millis;
    }

    public ASN1Integer getMicros() {
        return this.micros;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        if (this.seconds != null) {
            aSN1EncodableVector.add(this.seconds);
        }
        if (this.millis != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 0, (ASN1Encodable) this.millis));
        }
        if (this.micros != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 1, (ASN1Encodable) this.micros));
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
