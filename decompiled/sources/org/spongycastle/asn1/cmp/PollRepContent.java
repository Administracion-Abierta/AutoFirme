package org.spongycastle.asn1.cmp;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmp/PollRepContent.class */
public class PollRepContent extends ASN1Object {
    private ASN1Integer[] certReqId;
    private ASN1Integer[] checkAfter;
    private PKIFreeText[] reason;

    private PollRepContent(ASN1Sequence seq) {
        this.certReqId = new ASN1Integer[seq.size()];
        this.checkAfter = new ASN1Integer[seq.size()];
        this.reason = new PKIFreeText[seq.size()];
        for (int i = 0; i != seq.size(); i++) {
            ASN1Sequence s = ASN1Sequence.getInstance(seq.getObjectAt(i));
            this.certReqId[i] = ASN1Integer.getInstance(s.getObjectAt(0));
            this.checkAfter[i] = ASN1Integer.getInstance(s.getObjectAt(1));
            if (s.size() > 2) {
                this.reason[i] = PKIFreeText.getInstance(s.getObjectAt(2));
            }
        }
    }

    public static PollRepContent getInstance(Object o) {
        if (o instanceof PollRepContent) {
            return (PollRepContent) o;
        }
        if (o != null) {
            return new PollRepContent(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public PollRepContent(ASN1Integer certReqId, ASN1Integer checkAfter) {
        this(certReqId, checkAfter, null);
    }

    public PollRepContent(ASN1Integer certReqId, ASN1Integer checkAfter, PKIFreeText reason) {
        this.certReqId = new ASN1Integer[1];
        this.checkAfter = new ASN1Integer[1];
        this.reason = new PKIFreeText[1];
        this.certReqId[0] = certReqId;
        this.checkAfter[0] = checkAfter;
        this.reason[0] = reason;
    }

    public int size() {
        return this.certReqId.length;
    }

    public ASN1Integer getCertReqId(int index) {
        return this.certReqId[index];
    }

    public ASN1Integer getCheckAfter(int index) {
        return this.checkAfter[index];
    }

    public PKIFreeText getReason(int index) {
        return this.reason[index];
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector outer = new ASN1EncodableVector();
        for (int i = 0; i != this.certReqId.length; i++) {
            ASN1EncodableVector v = new ASN1EncodableVector();
            v.add(this.certReqId[i]);
            v.add(this.checkAfter[i]);
            if (this.reason[i] != null) {
                v.add(this.reason[i]);
            }
            outer.add(new DERSequence(v));
        }
        return new DERSequence(outer);
    }
}
