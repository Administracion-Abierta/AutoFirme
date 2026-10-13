package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfPsidSspRange;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/SubjectPermissions.class */
public class SubjectPermissions extends ASN1Object implements ASN1Choice {
    public static final int explicit = 0;
    public static final int all = 1;
    private final ASN1Encodable subjectPermissions;
    private final int choice;

    SubjectPermissions(int i, ASN1Encodable aSN1Encodable) {
        this.subjectPermissions = aSN1Encodable;
        this.choice = i;
    }

    public static SubjectPermissions explicit(SequenceOfPsidSspRange sequenceOfPsidSspRange) {
        return new SubjectPermissions(0, sequenceOfPsidSspRange);
    }

    public static SubjectPermissions all() {
        return new SubjectPermissions(1, DERNull.INSTANCE);
    }

    private SubjectPermissions(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (this.choice) {
            case 0:
                this.subjectPermissions = SequenceOfPsidSspRange.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 1:
                this.subjectPermissions = ASN1Null.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            default:
                throw new IllegalArgumentException("invalid choice value " + this.choice);
        }
    }

    public static SubjectPermissions getInstance(Object obj) {
        if (obj instanceof SubjectPermissions) {
            return (SubjectPermissions) obj;
        }
        if (obj != null) {
            return new SubjectPermissions(ASN1TaggedObject.getInstance(obj, 128));
        }
        return null;
    }

    public ASN1Encodable getSubjectPermissions() {
        return this.subjectPermissions;
    }

    public int getChoice() {
        return this.choice;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.subjectPermissions);
    }
}
