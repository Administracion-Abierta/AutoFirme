package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/EtsiTs102941DataContent.class */
public class EtsiTs102941DataContent extends ASN1Object implements ASN1Choice {
    public static final int enrolmentRequest = 0;
    public static final int enrolmentResponse = 1;
    public static final int authorizationRequest = 2;
    public static final int authorizationResponse = 3;
    public static final int certificateRevocationList = 4;
    public static final int certificateTrustListTlm = 5;
    public static final int certificateTrustListRca = 6;
    public static final int authorizationValidationRequest = 7;
    public static final int authorizationValidationResponse = 8;
    public static final int caCertificateRequest = 9;
    public static final int linkCertificateTlm = 10;
    public static final int singleSignedLinkCertificateRca = 11;
    public static final int doubleSignedlinkCertificateRca = 12;
    private final int choice;
    private final ASN1Encodable etsiTs102941DataContent;

    public EtsiTs102941DataContent(int i, ASN1Encodable aSN1Encodable) {
        this.choice = i;
        this.etsiTs102941DataContent = aSN1Encodable;
    }

    private EtsiTs102941DataContent(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (this.choice) {
            case 0:
                this.etsiTs102941DataContent = InnerEcRequestSignedForPop.getInstance((Object) aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 1:
                this.etsiTs102941DataContent = InnerEcResponse.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 2:
                this.etsiTs102941DataContent = InnerAtRequest.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 3:
                this.etsiTs102941DataContent = InnerAtResponse.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 4:
            default:
                throw new IllegalArgumentException("choice not implemented " + this.choice);
            case 5:
                this.etsiTs102941DataContent = ToBeSignedTlmCtl.getInstance((Object) aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 6:
                this.etsiTs102941DataContent = ToBeSignedRcaCtl.getInstance((Object) aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 7:
                this.etsiTs102941DataContent = AuthorizationValidationRequest.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 8:
                this.etsiTs102941DataContent = AuthorizationValidationResponse.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
            case 9:
                this.etsiTs102941DataContent = CaCertificateRequest.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                return;
        }
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getEtsiTs102941DataContent() {
        return this.etsiTs102941DataContent;
    }

    public static EtsiTs102941DataContent getInstance(Object obj) {
        if (obj instanceof EtsiTs102941DataContent) {
            return (EtsiTs102941DataContent) obj;
        }
        if (obj != null) {
            return new EtsiTs102941DataContent(ASN1TaggedObject.getInstance(obj, 128));
        }
        return null;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.etsiTs102941DataContent);
    }
}
