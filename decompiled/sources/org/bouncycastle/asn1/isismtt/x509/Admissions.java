package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.GeneralName;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/isismtt/x509/Admissions.class */
public class Admissions extends ASN1Object {
    private GeneralName admissionAuthority;
    private NamingAuthority namingAuthority;
    private ASN1Sequence professionInfos;

    public static Admissions getInstance(Object obj) {
        if (obj == null || (obj instanceof Admissions)) {
            return (Admissions) obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new Admissions((ASN1Sequence) obj);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    private Admissions(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + aSN1Sequence.size());
        }
        Enumeration objects = aSN1Sequence.getObjects();
        ASN1Encodable aSN1Encodable = (ASN1Encodable) objects.nextElement();
        if (aSN1Encodable instanceof ASN1TaggedObject) {
            switch (((ASN1TaggedObject) aSN1Encodable).getTagNo()) {
                case 0:
                    this.admissionAuthority = GeneralName.getInstance((ASN1TaggedObject) aSN1Encodable, true);
                    break;
                case 1:
                    this.namingAuthority = NamingAuthority.getInstance((ASN1TaggedObject) aSN1Encodable, true);
                    break;
                default:
                    throw new IllegalArgumentException("Bad tag number: " + ((ASN1TaggedObject) aSN1Encodable).getTagNo());
            }
            aSN1Encodable = (ASN1Encodable) objects.nextElement();
        }
        if (aSN1Encodable instanceof ASN1TaggedObject) {
            switch (((ASN1TaggedObject) aSN1Encodable).getTagNo()) {
                case 1:
                    this.namingAuthority = NamingAuthority.getInstance((ASN1TaggedObject) aSN1Encodable, true);
                    aSN1Encodable = (ASN1Encodable) objects.nextElement();
                    break;
                default:
                    throw new IllegalArgumentException("Bad tag number: " + ((ASN1TaggedObject) aSN1Encodable).getTagNo());
            }
        }
        this.professionInfos = ASN1Sequence.getInstance(aSN1Encodable);
        if (objects.hasMoreElements()) {
            throw new IllegalArgumentException("Bad object encountered: " + objects.nextElement().getClass());
        }
    }

    public Admissions(GeneralName generalName, NamingAuthority namingAuthority, ProfessionInfo[] professionInfoArr) {
        this.admissionAuthority = generalName;
        this.namingAuthority = namingAuthority;
        this.professionInfos = new DERSequence(professionInfoArr);
    }

    public GeneralName getAdmissionAuthority() {
        return this.admissionAuthority;
    }

    public NamingAuthority getNamingAuthority() {
        return this.namingAuthority;
    }

    public ProfessionInfo[] getProfessionInfos() {
        ProfessionInfo[] professionInfoArr = new ProfessionInfo[this.professionInfos.size()];
        int i = 0;
        Enumeration objects = this.professionInfos.getObjects();
        while (objects.hasMoreElements()) {
            int i2 = i;
            i++;
            professionInfoArr[i2] = ProfessionInfo.getInstance(objects.nextElement());
        }
        return professionInfoArr;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        if (this.admissionAuthority != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 0, (ASN1Encodable) this.admissionAuthority));
        }
        if (this.namingAuthority != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 1, (ASN1Encodable) this.namingAuthority));
        }
        aSN1EncodableVector.add(this.professionInfos);
        return new DERSequence(aSN1EncodableVector);
    }
}
