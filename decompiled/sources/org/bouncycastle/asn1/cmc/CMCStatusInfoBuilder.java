package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmc/CMCStatusInfoBuilder.class */
public class CMCStatusInfoBuilder {
    private final CMCStatus cMCStatus;
    private final ASN1Sequence bodyList;
    private ASN1UTF8String statusString;
    private CMCStatusInfo.OtherInfo otherInfo;

    public CMCStatusInfoBuilder(CMCStatus cMCStatus, BodyPartID bodyPartID) {
        this.cMCStatus = cMCStatus;
        this.bodyList = new DERSequence(bodyPartID);
    }

    public CMCStatusInfoBuilder(CMCStatus cMCStatus, BodyPartID[] bodyPartIDArr) {
        this.cMCStatus = cMCStatus;
        this.bodyList = new DERSequence(bodyPartIDArr);
    }

    public CMCStatusInfoBuilder setStatusString(String str) {
        this.statusString = new DERUTF8String(str);
        return this;
    }

    public CMCStatusInfoBuilder setOtherInfo(CMCFailInfo cMCFailInfo) {
        this.otherInfo = new CMCStatusInfo.OtherInfo(cMCFailInfo);
        return this;
    }

    public CMCStatusInfoBuilder setOtherInfo(PendInfo pendInfo) {
        this.otherInfo = new CMCStatusInfo.OtherInfo(pendInfo);
        return this;
    }

    public CMCStatusInfo build() {
        return new CMCStatusInfo(this.cMCStatus, this.bodyList, this.statusString, this.otherInfo);
    }
}
