package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.x509.CertificateList;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/esf/RevocationValues.class */
public class RevocationValues extends ASN1Object {
    private ASN1Sequence crlVals;
    private ASN1Sequence ocspVals;
    private OtherRevVals otherRevVals;

    public static RevocationValues getInstance(Object obj) {
        if (obj instanceof RevocationValues) {
            return (RevocationValues) obj;
        }
        if (obj != null) {
            return new RevocationValues(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private RevocationValues(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + aSN1Sequence.size());
        }
        Enumeration objects = aSN1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(objects.nextElement(), 128);
            switch (aSN1TaggedObject.getTagNo()) {
                case 0:
                    ASN1Sequence aSN1Sequence2 = (ASN1Sequence) aSN1TaggedObject.getExplicitBaseObject();
                    Enumeration objects2 = aSN1Sequence2.getObjects();
                    while (objects2.hasMoreElements()) {
                        CertificateList.getInstance(objects2.nextElement());
                    }
                    this.crlVals = aSN1Sequence2;
                    break;
                case 1:
                    ASN1Sequence aSN1Sequence3 = (ASN1Sequence) aSN1TaggedObject.getExplicitBaseObject();
                    Enumeration objects3 = aSN1Sequence3.getObjects();
                    while (objects3.hasMoreElements()) {
                        BasicOCSPResponse.getInstance(objects3.nextElement());
                    }
                    this.ocspVals = aSN1Sequence3;
                    break;
                case 2:
                    this.otherRevVals = OtherRevVals.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                    break;
                default:
                    throw new IllegalArgumentException("invalid tag: " + aSN1TaggedObject.getTagNo());
            }
        }
    }

    public RevocationValues(CertificateList[] certificateListArr, BasicOCSPResponse[] basicOCSPResponseArr, OtherRevVals otherRevVals) {
        if (null != certificateListArr) {
            this.crlVals = new DERSequence(certificateListArr);
        }
        if (null != basicOCSPResponseArr) {
            this.ocspVals = new DERSequence(basicOCSPResponseArr);
        }
        this.otherRevVals = otherRevVals;
    }

    public CertificateList[] getCrlVals() {
        if (null == this.crlVals) {
            return new CertificateList[0];
        }
        CertificateList[] certificateListArr = new CertificateList[this.crlVals.size()];
        for (int i = 0; i < certificateListArr.length; i++) {
            certificateListArr[i] = CertificateList.getInstance(this.crlVals.getObjectAt(i));
        }
        return certificateListArr;
    }

    public BasicOCSPResponse[] getOcspVals() {
        if (null == this.ocspVals) {
            return new BasicOCSPResponse[0];
        }
        BasicOCSPResponse[] basicOCSPResponseArr = new BasicOCSPResponse[this.ocspVals.size()];
        for (int i = 0; i < basicOCSPResponseArr.length; i++) {
            basicOCSPResponseArr[i] = BasicOCSPResponse.getInstance(this.ocspVals.getObjectAt(i));
        }
        return basicOCSPResponseArr;
    }

    public OtherRevVals getOtherRevVals() {
        return this.otherRevVals;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        if (null != this.crlVals) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 0, (ASN1Encodable) this.crlVals));
        }
        if (null != this.ocspVals) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 1, (ASN1Encodable) this.ocspVals));
        }
        if (null != this.otherRevVals) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 2, (ASN1Encodable) this.otherRevVals.toASN1Primitive()));
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
