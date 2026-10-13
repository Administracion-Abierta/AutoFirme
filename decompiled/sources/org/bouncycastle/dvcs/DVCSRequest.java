package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.dvcs.DVCSObjectIdentifiers;
import org.bouncycastle.asn1.dvcs.ServiceType;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cms.CMSSignedData;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/dvcs/DVCSRequest.class */
public class DVCSRequest extends DVCSMessage {
    private org.bouncycastle.asn1.dvcs.DVCSRequest asn1;
    private DVCSRequestInfo reqInfo;
    private DVCSRequestData data;

    public DVCSRequest(CMSSignedData cMSSignedData) throws DVCSConstructionException {
        this(SignedData.getInstance(cMSSignedData.toASN1Structure().getContent()).getEncapContentInfo());
    }

    public DVCSRequest(ContentInfo contentInfo) throws DVCSConstructionException {
        super(contentInfo);
        if (!DVCSObjectIdentifiers.id_ct_DVCSRequestData.equals((ASN1Primitive) contentInfo.getContentType())) {
            throw new DVCSConstructionException("ContentInfo not a DVCS Request");
        }
        try {
            if (contentInfo.getContent().toASN1Primitive() instanceof ASN1Sequence) {
                this.asn1 = org.bouncycastle.asn1.dvcs.DVCSRequest.getInstance(contentInfo.getContent());
            } else {
                this.asn1 = org.bouncycastle.asn1.dvcs.DVCSRequest.getInstance(ASN1OctetString.getInstance(contentInfo.getContent()).getOctets());
            }
            this.reqInfo = new DVCSRequestInfo(this.asn1.getRequestInformation());
            int serviceType = this.reqInfo.getServiceType();
            if (serviceType == ServiceType.CPD.getValue().intValue()) {
                this.data = new CPDRequestData(this.asn1.getData());
                return;
            }
            if (serviceType == ServiceType.VSD.getValue().intValue()) {
                this.data = new VSDRequestData(this.asn1.getData());
            } else if (serviceType == ServiceType.VPKC.getValue().intValue()) {
                this.data = new VPKCRequestData(this.asn1.getData());
            } else {
                if (serviceType != ServiceType.CCPD.getValue().intValue()) {
                    throw new DVCSConstructionException("Unknown service type: " + serviceType);
                }
                this.data = new CCPDRequestData(this.asn1.getData());
            }
        } catch (Exception e) {
            throw new DVCSConstructionException("Unable to parse content: " + e.getMessage(), e);
        }
    }

    @Override // org.bouncycastle.dvcs.DVCSMessage
    public ASN1Encodable getContent() {
        return this.asn1;
    }

    public DVCSRequestInfo getRequestInfo() {
        return this.reqInfo;
    }

    public DVCSRequestData getData() {
        return this.data;
    }

    public GeneralName getTransactionIdentifier() {
        return this.asn1.getTransactionIdentifier();
    }
}
