package org.spongycastle.asn1.esf;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1String;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.DisplayText;
import org.spongycastle.asn1.x509.NoticeReference;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/esf/SPUserNotice.class */
public class SPUserNotice extends ASN1Object {
    private NoticeReference noticeRef;
    private DisplayText explicitText;

    public static SPUserNotice getInstance(Object obj) {
        if (obj instanceof SPUserNotice) {
            return (SPUserNotice) obj;
        }
        if (obj != null) {
            return new SPUserNotice(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private SPUserNotice(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements()) {
            ASN1Encodable object = (ASN1Encodable) e.nextElement();
            if ((object instanceof DisplayText) || (object instanceof ASN1String)) {
                this.explicitText = DisplayText.getInstance(object);
            } else if ((object instanceof NoticeReference) || (object instanceof ASN1Sequence)) {
                this.noticeRef = NoticeReference.getInstance(object);
            } else {
                throw new IllegalArgumentException("Invalid element in 'SPUserNotice': " + object.getClass().getName());
            }
        }
    }

    public SPUserNotice(NoticeReference noticeRef, DisplayText explicitText) {
        this.noticeRef = noticeRef;
        this.explicitText = explicitText;
    }

    public NoticeReference getNoticeRef() {
        return this.noticeRef;
    }

    public DisplayText getExplicitText() {
        return this.explicitText;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (this.noticeRef != null) {
            v.add(this.noticeRef);
        }
        if (this.explicitText != null) {
            v.add(this.explicitText);
        }
        return new DERSequence(v);
    }
}
