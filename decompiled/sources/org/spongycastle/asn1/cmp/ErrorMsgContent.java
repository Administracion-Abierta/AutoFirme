package org.spongycastle.asn1.cmp;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmp/ErrorMsgContent.class */
public class ErrorMsgContent extends ASN1Object {
    private PKIStatusInfo pkiStatusInfo;
    private ASN1Integer errorCode;
    private PKIFreeText errorDetails;

    private ErrorMsgContent(ASN1Sequence seq) {
        Enumeration en = seq.getObjects();
        this.pkiStatusInfo = PKIStatusInfo.getInstance(en.nextElement());
        while (en.hasMoreElements()) {
            Object o = en.nextElement();
            if (o instanceof ASN1Integer) {
                this.errorCode = ASN1Integer.getInstance(o);
            } else {
                this.errorDetails = PKIFreeText.getInstance(o);
            }
        }
    }

    public static ErrorMsgContent getInstance(Object o) {
        if (o instanceof ErrorMsgContent) {
            return (ErrorMsgContent) o;
        }
        if (o != null) {
            return new ErrorMsgContent(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public ErrorMsgContent(PKIStatusInfo pkiStatusInfo) {
        this(pkiStatusInfo, null, null);
    }

    public ErrorMsgContent(PKIStatusInfo pkiStatusInfo, ASN1Integer errorCode, PKIFreeText errorDetails) {
        if (pkiStatusInfo == null) {
            throw new IllegalArgumentException("'pkiStatusInfo' cannot be null");
        }
        this.pkiStatusInfo = pkiStatusInfo;
        this.errorCode = errorCode;
        this.errorDetails = errorDetails;
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

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.pkiStatusInfo);
        addOptional(v, this.errorCode);
        addOptional(v, this.errorDetails);
        return new DERSequence(v);
    }

    private void addOptional(ASN1EncodableVector v, ASN1Encodable obj) {
        if (obj != null) {
            v.add(obj);
        }
    }
}
