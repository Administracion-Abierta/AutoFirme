package org.bouncycastle.cert.ocsp;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.CertStatus;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.asn1.ocsp.RevokedInfo;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/ocsp/BasicOCSPRespBuilder.class */
public class BasicOCSPRespBuilder {
    private List list = new ArrayList();
    private Extensions responseExtensions = null;
    private RespID responderID;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/ocsp/BasicOCSPRespBuilder$ResponseObject.class */
    private static class ResponseObject {
        CertificateID certId;
        CertStatus certStatus;
        ASN1GeneralizedTime thisUpdate;
        ASN1GeneralizedTime nextUpdate;
        Extensions extensions;

        public ResponseObject(CertificateID certificateID, CertificateStatus certificateStatus, Date date, Date date2, Extensions extensions) {
            this.certId = certificateID;
            if (certificateStatus == null) {
                this.certStatus = new CertStatus();
            } else if (certificateStatus instanceof UnknownStatus) {
                this.certStatus = new CertStatus(2, DERNull.INSTANCE);
            } else {
                RevokedStatus revokedStatus = (RevokedStatus) certificateStatus;
                if (revokedStatus.hasRevocationReason()) {
                    this.certStatus = new CertStatus(new RevokedInfo(new ASN1GeneralizedTime(revokedStatus.getRevocationTime()), CRLReason.lookup(revokedStatus.getRevocationReason())));
                } else {
                    this.certStatus = new CertStatus(new RevokedInfo(new ASN1GeneralizedTime(revokedStatus.getRevocationTime()), null));
                }
            }
            this.thisUpdate = new DERGeneralizedTime(date);
            if (date2 != null) {
                this.nextUpdate = new DERGeneralizedTime(date2);
            } else {
                this.nextUpdate = null;
            }
            this.extensions = extensions;
        }

        public SingleResponse toResponse() throws Exception {
            return new SingleResponse(this.certId.toASN1Primitive(), this.certStatus, this.thisUpdate, this.nextUpdate, this.extensions);
        }
    }

    public BasicOCSPRespBuilder(RespID respID) {
        this.responderID = respID;
    }

    public BasicOCSPRespBuilder(SubjectPublicKeyInfo subjectPublicKeyInfo, DigestCalculator digestCalculator) throws OCSPException {
        this.responderID = new RespID(subjectPublicKeyInfo, digestCalculator);
    }

    public BasicOCSPRespBuilder addResponse(CertificateID certificateID, CertificateStatus certificateStatus) {
        addResponse(certificateID, certificateStatus, new Date(), null, null);
        return this;
    }

    public BasicOCSPRespBuilder addResponse(CertificateID certificateID, CertificateStatus certificateStatus, Extensions extensions) {
        addResponse(certificateID, certificateStatus, new Date(), null, extensions);
        return this;
    }

    public BasicOCSPRespBuilder addResponse(CertificateID certificateID, CertificateStatus certificateStatus, Date date, Extensions extensions) {
        addResponse(certificateID, certificateStatus, new Date(), date, extensions);
        return this;
    }

    public BasicOCSPRespBuilder addResponse(CertificateID certificateID, CertificateStatus certificateStatus, Date date, Date date2) {
        addResponse(certificateID, certificateStatus, date, date2, null);
        return this;
    }

    public BasicOCSPRespBuilder addResponse(CertificateID certificateID, CertificateStatus certificateStatus, Date date, Date date2, Extensions extensions) {
        this.list.add(new ResponseObject(certificateID, certificateStatus, date, date2, extensions));
        return this;
    }

    public BasicOCSPRespBuilder setResponseExtensions(Extensions extensions) {
        this.responseExtensions = extensions;
        return this;
    }

    public BasicOCSPResp build(ContentSigner contentSigner, X509CertificateHolder[] x509CertificateHolderArr, Date date) throws OCSPException {
        Iterator it = this.list.iterator();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        while (it.hasNext()) {
            try {
                aSN1EncodableVector.add(((ResponseObject) it.next()).toResponse());
            } catch (Exception e) {
                throw new OCSPException("exception creating Request", e);
            }
        }
        ResponseData responseData = new ResponseData(this.responderID.toASN1Primitive(), new ASN1GeneralizedTime(date), new DERSequence(aSN1EncodableVector), this.responseExtensions);
        try {
            OutputStream outputStream = contentSigner.getOutputStream();
            outputStream.write(responseData.getEncoded("DER"));
            outputStream.close();
            DERBitString dERBitString = new DERBitString(contentSigner.getSignature());
            AlgorithmIdentifier algorithmIdentifier = contentSigner.getAlgorithmIdentifier();
            DERSequence dERSequence = null;
            if (x509CertificateHolderArr != null && x509CertificateHolderArr.length > 0) {
                ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
                for (int i = 0; i != x509CertificateHolderArr.length; i++) {
                    aSN1EncodableVector2.add(x509CertificateHolderArr[i].toASN1Structure());
                }
                dERSequence = new DERSequence(aSN1EncodableVector2);
            }
            return new BasicOCSPResp(new BasicOCSPResponse(responseData, algorithmIdentifier, dERBitString, dERSequence));
        } catch (Exception e2) {
            throw new OCSPException("exception processing TBSRequest: " + e2.getMessage(), e2);
        }
    }
}
