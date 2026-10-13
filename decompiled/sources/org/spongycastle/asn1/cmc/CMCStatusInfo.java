package org.spongycastle.asn1.cmc;

import org.spongycastle.asn1.ASN1Choice;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERUTF8String;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmc/CMCStatusInfo.class */
public class CMCStatusInfo extends ASN1Object {
    private final CMCStatus cMCStatus;
    private final ASN1Sequence bodyList;
    private final DERUTF8String statusString;
    private final OtherInfo otherInfo;

    CMCStatusInfo(CMCStatus cMCStatus, ASN1Sequence bodyList, DERUTF8String statusString, OtherInfo otherInfo) {
        this.cMCStatus = cMCStatus;
        this.bodyList = bodyList;
        this.statusString = statusString;
        this.otherInfo = otherInfo;
    }

    private CMCStatusInfo(ASN1Sequence seq) {
        if (seq.size() < 2 || seq.size() > 4) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.cMCStatus = CMCStatus.getInstance(seq.getObjectAt(0));
        this.bodyList = ASN1Sequence.getInstance(seq.getObjectAt(1));
        if (seq.size() > 3) {
            this.statusString = DERUTF8String.getInstance(seq.getObjectAt(2));
            this.otherInfo = OtherInfo.getInstance(seq.getObjectAt(3));
        } else {
            if (seq.size() > 2) {
                if (seq.getObjectAt(2) instanceof DERUTF8String) {
                    this.statusString = DERUTF8String.getInstance(seq.getObjectAt(2));
                    this.otherInfo = null;
                    return;
                } else {
                    this.statusString = null;
                    this.otherInfo = OtherInfo.getInstance(seq.getObjectAt(2));
                    return;
                }
            }
            this.statusString = null;
            this.otherInfo = null;
        }
    }

    public static CMCStatusInfo getInstance(Object o) {
        if (o instanceof CMCStatusInfo) {
            return (CMCStatusInfo) o;
        }
        if (o != null) {
            return new CMCStatusInfo(ASN1Sequence.getInstance(o));
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
        if (this.otherInfo != null) {
            v.add(this.otherInfo);
        }
        return new DERSequence(v);
    }

    public CMCStatus getCMCStatus() {
        return this.cMCStatus;
    }

    public BodyPartID[] getBodyList() {
        return Utils.toBodyPartIDArray(this.bodyList);
    }

    public DERUTF8String getStatusString() {
        return this.statusString;
    }

    public boolean hasOtherInfo() {
        return this.otherInfo != null;
    }

    public OtherInfo getOtherInfo() {
        return this.otherInfo;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmc/CMCStatusInfo$OtherInfo.class */
    public static class OtherInfo extends ASN1Object implements ASN1Choice {
        private final CMCFailInfo failInfo;
        private final PendInfo pendInfo;

        /* JADX INFO: Access modifiers changed from: private */
        public static OtherInfo getInstance(Object obj) {
            if (obj instanceof OtherInfo) {
                return (OtherInfo) obj;
            }
            if (obj instanceof ASN1Encodable) {
                ASN1Encodable asn1Value = ((ASN1Encodable) obj).toASN1Primitive();
                if (asn1Value instanceof ASN1Integer) {
                    return new OtherInfo(CMCFailInfo.getInstance(asn1Value));
                }
                if (asn1Value instanceof ASN1Sequence) {
                    return new OtherInfo(PendInfo.getInstance(asn1Value));
                }
            }
            throw new IllegalArgumentException("unknown object in getInstance(): " + obj.getClass().getName());
        }

        OtherInfo(CMCFailInfo failInfo) {
            this(failInfo, null);
        }

        OtherInfo(PendInfo pendInfo) {
            this(null, pendInfo);
        }

        private OtherInfo(CMCFailInfo failInfo, PendInfo pendInfo) {
            this.failInfo = failInfo;
            this.pendInfo = pendInfo;
        }

        public boolean isFailInfo() {
            return this.failInfo != null;
        }

        @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
        public ASN1Primitive toASN1Primitive() {
            if (this.pendInfo != null) {
                return this.pendInfo.toASN1Primitive();
            }
            return this.failInfo.toASN1Primitive();
        }
    }
}
