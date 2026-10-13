package org.spongycastle.asn1.dvcs;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.cmp.PKIStatusInfo;
import org.spongycastle.asn1.x509.DigestInfo;
import org.spongycastle.asn1.x509.Extensions;
import org.spongycastle.asn1.x509.PolicyInformation;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/dvcs/DVCSCertInfo.class */
public class DVCSCertInfo extends ASN1Object {
    private int version;
    private DVCSRequestInformation dvReqInfo;
    private DigestInfo messageImprint;
    private ASN1Integer serialNumber;
    private DVCSTime responseTime;
    private PKIStatusInfo dvStatus;
    private PolicyInformation policy;
    private ASN1Set reqSignature;
    private ASN1Sequence certs;
    private Extensions extensions;
    private static final int DEFAULT_VERSION = 1;
    private static final int TAG_DV_STATUS = 0;
    private static final int TAG_POLICY = 1;
    private static final int TAG_REQ_SIGNATURE = 2;
    private static final int TAG_CERTS = 3;

    public DVCSCertInfo(DVCSRequestInformation dvReqInfo, DigestInfo messageImprint, ASN1Integer serialNumber, DVCSTime responseTime) {
        this.version = 1;
        this.dvReqInfo = dvReqInfo;
        this.messageImprint = messageImprint;
        this.serialNumber = serialNumber;
        this.responseTime = responseTime;
    }

    private DVCSCertInfo(ASN1Sequence seq) {
        this.version = 1;
        int i = 0 + 1;
        ASN1Encodable x = seq.getObjectAt(0);
        try {
            ASN1Integer encVersion = ASN1Integer.getInstance(x);
            this.version = encVersion.getValue().intValue();
            i++;
            x = seq.getObjectAt(i);
        } catch (IllegalArgumentException e) {
        }
        this.dvReqInfo = DVCSRequestInformation.getInstance(x);
        int i2 = i;
        int i3 = i + 1;
        this.messageImprint = DigestInfo.getInstance(seq.getObjectAt(i2));
        int i4 = i3 + 1;
        this.serialNumber = ASN1Integer.getInstance(seq.getObjectAt(i3));
        int i5 = i4 + 1;
        this.responseTime = DVCSTime.getInstance(seq.getObjectAt(i4));
        while (i5 < seq.size()) {
            int i6 = i5;
            i5++;
            ASN1Encodable x2 = seq.getObjectAt(i6);
            if (x2 instanceof ASN1TaggedObject) {
                ASN1TaggedObject t = ASN1TaggedObject.getInstance(x2);
                int tagNo = t.getTagNo();
                switch (tagNo) {
                    case 0:
                        this.dvStatus = PKIStatusInfo.getInstance(t, false);
                        break;
                    case 1:
                        this.policy = PolicyInformation.getInstance(ASN1Sequence.getInstance(t, false));
                        break;
                    case 2:
                        this.reqSignature = ASN1Set.getInstance(t, false);
                        break;
                    case 3:
                        this.certs = ASN1Sequence.getInstance(t, false);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown tag encountered: " + tagNo);
                }
            } else {
                try {
                    this.extensions = Extensions.getInstance(x2);
                } catch (IllegalArgumentException e2) {
                }
            }
        }
    }

    public static DVCSCertInfo getInstance(Object obj) {
        if (obj instanceof DVCSCertInfo) {
            return (DVCSCertInfo) obj;
        }
        if (obj != null) {
            return new DVCSCertInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static DVCSCertInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (this.version != 1) {
            v.add(new ASN1Integer(this.version));
        }
        v.add(this.dvReqInfo);
        v.add(this.messageImprint);
        v.add(this.serialNumber);
        v.add(this.responseTime);
        if (this.dvStatus != null) {
            v.add(new DERTaggedObject(false, 0, this.dvStatus));
        }
        if (this.policy != null) {
            v.add(new DERTaggedObject(false, 1, this.policy));
        }
        if (this.reqSignature != null) {
            v.add(new DERTaggedObject(false, 2, this.reqSignature));
        }
        if (this.certs != null) {
            v.add(new DERTaggedObject(false, 3, this.certs));
        }
        if (this.extensions != null) {
            v.add(this.extensions);
        }
        return new DERSequence(v);
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("DVCSCertInfo {\n");
        if (this.version != 1) {
            s.append("version: " + this.version + "\n");
        }
        s.append("dvReqInfo: " + this.dvReqInfo + "\n");
        s.append("messageImprint: " + this.messageImprint + "\n");
        s.append("serialNumber: " + this.serialNumber + "\n");
        s.append("responseTime: " + this.responseTime + "\n");
        if (this.dvStatus != null) {
            s.append("dvStatus: " + this.dvStatus + "\n");
        }
        if (this.policy != null) {
            s.append("policy: " + this.policy + "\n");
        }
        if (this.reqSignature != null) {
            s.append("reqSignature: " + this.reqSignature + "\n");
        }
        if (this.certs != null) {
            s.append("certs: " + this.certs + "\n");
        }
        if (this.extensions != null) {
            s.append("extensions: " + this.extensions + "\n");
        }
        s.append("}\n");
        return s.toString();
    }

    public int getVersion() {
        return this.version;
    }

    private void setVersion(int version) {
        this.version = version;
    }

    public DVCSRequestInformation getDvReqInfo() {
        return this.dvReqInfo;
    }

    private void setDvReqInfo(DVCSRequestInformation dvReqInfo) {
        this.dvReqInfo = dvReqInfo;
    }

    public DigestInfo getMessageImprint() {
        return this.messageImprint;
    }

    private void setMessageImprint(DigestInfo messageImprint) {
        this.messageImprint = messageImprint;
    }

    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }

    public DVCSTime getResponseTime() {
        return this.responseTime;
    }

    public PKIStatusInfo getDvStatus() {
        return this.dvStatus;
    }

    public PolicyInformation getPolicy() {
        return this.policy;
    }

    public ASN1Set getReqSignature() {
        return this.reqSignature;
    }

    public TargetEtcChain[] getCerts() {
        if (this.certs != null) {
            return TargetEtcChain.arrayFromSequence(this.certs);
        }
        return null;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }
}
