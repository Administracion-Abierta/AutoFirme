package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmp/ErrorMsgContent.class */
public class ErrorMsgContent extends ASN1Object {
    private final PKIStatusInfo pkiStatusInfo;
    private ASN1Integer errorCode;
    private PKIFreeText errorDetails;

    private ErrorMsgContent(ASN1Sequence aSN1Sequence) {
        Enumeration objects = aSN1Sequence.getObjects();
        this.pkiStatusInfo = PKIStatusInfo.getInstance(objects.nextElement());
        while (objects.hasMoreElements()) {
            Object objNextElement = objects.nextElement();
            if (objNextElement instanceof ASN1Integer) {
                this.errorCode = ASN1Integer.getInstance(objNextElement);
            } else {
                this.errorDetails = PKIFreeText.getInstance(objNextElement);
            }
        }
    }

    public ErrorMsgContent(PKIStatusInfo pKIStatusInfo) {
        this(pKIStatusInfo, null, null);
    }

    public ErrorMsgContent(PKIStatusInfo pKIStatusInfo, ASN1Integer aSN1Integer, PKIFreeText pKIFreeText) {
        if (pKIStatusInfo == null) {
            throw new IllegalArgumentException("'pkiStatusInfo' cannot be null");
        }
        this.pkiStatusInfo = pKIStatusInfo;
        this.errorCode = aSN1Integer;
        this.errorDetails = pKIFreeText;
    }

    public static ErrorMsgContent getInstance(Object obj) {
        if (obj instanceof ErrorMsgContent) {
            return (ErrorMsgContent) obj;
        }
        if (obj != null) {
            return new ErrorMsgContent(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public PKIStatusInfo getPKIStatusInfo() {
        return this.pkiStatusInfo;
    }

    public ASN1Integer getErrorCode() {
        return this.errorCode;
    }

    public PKIFreeText getErrorDetails() {
        return this.errorDetails;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.add(this.pkiStatusInfo);
        addOptional(aSN1EncodableVector, this.errorCode);
        addOptional(aSN1EncodableVector, this.errorDetails);
        return new DERSequence(aSN1EncodableVector);
    }

    private void addOptional(ASN1EncodableVector aSN1EncodableVector, ASN1Encodable aSN1Encodable) {
        if (aSN1Encodable != null) {
            aSN1EncodableVector.add(aSN1Encodable);
        }
    }
}
