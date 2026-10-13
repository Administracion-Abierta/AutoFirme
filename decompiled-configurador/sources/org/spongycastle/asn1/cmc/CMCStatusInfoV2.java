package org.spongycastle.asn1.cmc;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERUTF8String;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cmc/CMCStatusInfoV2.class */
public class CMCStatusInfoV2 extends ASN1Object {
    private final CMCStatus cMCStatus;
    private final ASN1Sequence bodyList;
    private final DERUTF8String statusString;
    private final OtherStatusInfo otherStatusInfo;

    CMCStatusInfoV2(CMCStatus cMCStatus, ASN1Sequence bodyList, DERUTF8String statusString, OtherStatusInfo otherStatusInfo) {
        this.cMCStatus = cMCStatus;
        this.bodyList = bodyList;
        this.statusString = statusString;
        this.otherStatusInfo = otherStatusInfo;
    }

    private CMCStatusInfoV2(ASN1Sequence seq) {
        if (seq.size() < 2 || seq.size() > 4) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.cMCStatus = CMCStatus.getInstance(seq.getObjectAt(0));
        this.bodyList = ASN1Sequence.getInstance(seq.getObjectAt(1));
        if (seq.size() > 2) {
            if (seq.size() == 4) {
                this.statusString = DERUTF8String.getInstance(seq.getObjectAt(2));
                this.otherStatusInfo = OtherStatusInfo.getInstance(seq.getObjectAt(3));
                return;
            } else if (seq.getObjectAt(2) instanceof DERUTF8String) {
                this.statusString = DERUTF8String.getInstance(seq.getObjectAt(2));
                this.otherStatusInfo = null;
                return;
            } else {
                this.statusString = null;
                this.otherStatusInfo = OtherStatusInfo.getInstance(seq.getObjectAt(2));
                return;
            }
        }
        this.statusString = null;
        this.otherStatusInfo = null;
    }

    public CMCStatus getcMCStatus() {
        return this.cMCStatus;
    }

    public BodyPartID[] getBodyList() {
        return Utils.toBodyPartIDArray(this.bodyList);
    }

    public DERUTF8String getStatusString() {
        return this.statusString;
    }

    public OtherStatusInfo getOtherStatusInfo() {
        return this.otherStatusInfo;
    }

    public boolean hasOtherInfo() {
        return this.otherStatusInfo != null;
    }

    public static CMCStatusInfoV2 getInstance(Object o) {
        if (o instanceof CMCStatusInfoV2) {
            return (CMCStatusInfoV2) o;
        }
        if (o != null) {
            return new CMCStatusInfoV2(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.cMCStatus);
        v.add(this.bodyList);
        if (this.statusString != null) {
            v.add(this.statusString);
        }
        if (this.otherStatusInfo != null) {
            v.add(this.otherStatusInfo);
        }
        return new DERSequence(v);
    }
}
