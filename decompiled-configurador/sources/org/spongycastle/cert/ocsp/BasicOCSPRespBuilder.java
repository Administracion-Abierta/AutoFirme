package org.spongycastle.cert.ocsp;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERGeneralizedTime;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.ocsp.BasicOCSPResponse;
import org.spongycastle.asn1.ocsp.CertStatus;
import org.spongycastle.asn1.ocsp.ResponseData;
import org.spongycastle.asn1.ocsp.RevokedInfo;
import org.spongycastle.asn1.ocsp.SingleResponse;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.CRLReason;
import org.spongycastle.asn1.x509.Extensions;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/ocsp/BasicOCSPRespBuilder.class */
public class BasicOCSPRespBuilder {
    private List list = new ArrayList();
    private Extensions responseExtensions = null;
    private RespID responderID;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/ocsp/BasicOCSPRespBuilder$ResponseObject.class */
    private class ResponseObject {
        CertificateID certId;
        CertStatus certStatus;
        ASN1GeneralizedTime thisUpdate;
        ASN1GeneralizedTime nextUpdate;
        Extensions extensions;

        public ResponseObject(CertificateID certId, CertificateStatus certStatus, Date thisUpdate, Date nextUpdate, Extensions extensions) {
            this.certId = certId;
            if (certStatus == null) {
                this.certStatus = new CertStatus();
            } else if (certStatus instanceof UnknownStatus) {
                this.certStatus = new CertStatus(2, DERNull.INSTANCE);
            } else {
                RevokedStatus rs = (RevokedStatus) certStatus;
                if (rs.hasRevocationReason()) {
                    this.certStatus = new CertStatus(new RevokedInfo(new ASN1GeneralizedTime(rs.getRevocationTime()), CRLReason.lookup(rs.getRevocationReason())));
                } else {
                    this.certStatus = new CertStatus(new RevokedInfo(new ASN1GeneralizedTime(rs.getRevocationTime()), null));
                }
            }
            this.thisUpdate = new DERGeneralizedTime(thisUpdate);
            if (nextUpdate != null) {
                this.nextUpdate = new DERGeneralizedTime(nextUpdate);
            } else {
                this.nextUpdate = null;
            }
            this.extensions = extensions;
        }

        public SingleResponse toResponse() throws Exception {
            return new SingleResponse(this.certId.toASN1Primitive(), this.certStatus, this.thisUpdate, this.nextUpdate, this.extensions);
        }
    }

    public BasicOCSPRespBuilder(RespID responderID) {
        this.responderID = responderID;
    }

    public BasicOCSPRespBuilder(SubjectPublicKeyInfo key, DigestCalculator digCalc) throws OCSPException {
        this.responderID = new RespID(key, digCalc);
    }

    public BasicOCSPRespBuilder addResponse(CertificateID certID, CertificateStatus certStatus) {
        addResponse(certID, certStatus, new Date(), null, null);
        return this;
    }

    public BasicOCSPRespBuilder addResponse(CertificateID certID, CertificateStatus certStatus, Extensions singleExtensions) {
        addResponse(certID, certStatus, new Date(), null, singleExtensions);
        return this;
    }

    public BasicOCSPRespBuilder addResponse(CertificateID certID, CertificateStatus certStatus, Date nextUpdate, Extensions singleExtensions) {
        addResponse(certID, certStatus, new Date(), nextUpdate, singleExtensions);
        return this;
    }

    public BasicOCSPRespBuilder addResponse(CertificateID certID, CertificateStatus certStatus, Date thisUpdate, Date nextUpdate) {
        addResponse(certID, certStatus, thisUpdate, nextUpdate, null);
        return this;
    }

    public BasicOCSPRespBuilder addResponse(CertificateID certID, CertificateStatus certStatus, Date thisUpdate, Date nextUpdate, Extensions singleExtensions) {
        this.list.add(new ResponseObject(certID, certStatus, thisUpdate, nextUpdate, singleExtensions));
        return this;
    }

    public BasicOCSPRespBuilder setResponseExtensions(Extensions responseExtensions) {
        this.responseExtensions = responseExtensions;
        return this;
    }

    public BasicOCSPResp build(ContentSigner signer, X509CertificateHolder[] chain, Date producedAt) throws OCSPException {
        Iterator it = this.list.iterator();
        ASN1EncodableVector responses = new ASN1EncodableVector();
        while (it.hasNext()) {
            try {
                responses.add(((ResponseObject) it.next()).toResponse());
            } catch (Exception e) {
                throw new OCSPException("exception creating Request", e);
            }
        }
        ResponseData tbsResp = new ResponseData(this.responderID.toASN1Primitive(), new ASN1GeneralizedTime(producedAt), new DERSequence(responses), this.responseExtensions);
        try {
            OutputStream sigOut = signer.getOutputStream();
            sigOut.write(tbsResp.getEncoded(ASN1Encoding.DER));
            sigOut.close();
            DERBitString bitSig = new DERBitString(signer.getSignature());
            AlgorithmIdentifier sigAlgId = signer.getAlgorithmIdentifier();
            DERSequence chainSeq = null;
            if (chain != null && chain.length > 0) {
                ASN1EncodableVector v = new ASN1EncodableVector();
                for (int i = 0; i != chain.length; i++) {
                    v.add(chain[i].toASN1Structure());
                }
                chainSeq = new DERSequence(v);
            }
            return new BasicOCSPResp(new BasicOCSPResponse(tbsResp, sigAlgId, bitSig, chainSeq));
        } catch (Exception e2) {
            throw new OCSPException("exception processing TBSRequest: " + e2.getMessage(), e2);
        }
    }
}
