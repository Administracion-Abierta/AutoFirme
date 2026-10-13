package org.spongycastle.cert.ocsp;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.ocsp.OCSPRequest;
import org.spongycastle.asn1.ocsp.Request;
import org.spongycastle.asn1.ocsp.Signature;
import org.spongycastle.asn1.ocsp.TBSRequest;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.Extensions;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.operator.ContentSigner;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/ocsp/OCSPReqBuilder.class */
public class OCSPReqBuilder {
    private List list = new ArrayList();
    private GeneralName requestorName = null;
    private Extensions requestExtensions = null;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/ocsp/OCSPReqBuilder$RequestObject.class */
    private class RequestObject {
        CertificateID certId;
        Extensions extensions;

        public RequestObject(CertificateID certId, Extensions extensions) {
            this.certId = certId;
            this.extensions = extensions;
        }

        public Request toRequest() throws Exception {
            return new Request(this.certId.toASN1Primitive(), this.extensions);
        }
    }

    public OCSPReqBuilder addRequest(CertificateID certId) {
        this.list.add(new RequestObject(certId, null));
        return this;
    }

    public OCSPReqBuilder addRequest(CertificateID certId, Extensions singleRequestExtensions) {
        this.list.add(new RequestObject(certId, singleRequestExtensions));
        return this;
    }

    public OCSPReqBuilder setRequestorName(X500Name requestorName) {
        this.requestorName = new GeneralName(4, requestorName);
        return this;
    }

    public OCSPReqBuilder setRequestorName(GeneralName requestorName) {
        this.requestorName = requestorName;
        return this;
    }

    public OCSPReqBuilder setRequestExtensions(Extensions requestExtensions) {
        this.requestExtensions = requestExtensions;
        return this;
    }

    private OCSPReq generateRequest(ContentSigner contentSigner, X509CertificateHolder[] chain) throws OCSPException {
        Iterator it = this.list.iterator();
        ASN1EncodableVector requests = new ASN1EncodableVector();
        while (it.hasNext()) {
            try {
                requests.add(((RequestObject) it.next()).toRequest());
            } catch (Exception e) {
                throw new OCSPException("exception creating Request", e);
            }
        }
        TBSRequest tbsReq = new TBSRequest(this.requestorName, new DERSequence(requests), this.requestExtensions);
        Signature signature = null;
        if (contentSigner != null) {
            if (this.requestorName == null) {
                throw new OCSPException("requestorName must be specified if request is signed.");
            }
            try {
                OutputStream sOut = contentSigner.getOutputStream();
                sOut.write(tbsReq.getEncoded(ASN1Encoding.DER));
                sOut.close();
                DERBitString bitSig = new DERBitString(contentSigner.getSignature());
                AlgorithmIdentifier sigAlgId = contentSigner.getAlgorithmIdentifier();
                if (chain != null && chain.length > 0) {
                    ASN1EncodableVector v = new ASN1EncodableVector();
                    for (int i = 0; i != chain.length; i++) {
                        v.add(chain[i].toASN1Structure());
                    }
                    signature = new Signature(sigAlgId, bitSig, new DERSequence(v));
                } else {
                    signature = new Signature(sigAlgId, bitSig);
                }
            } catch (Exception e2) {
                throw new OCSPException("exception processing TBSRequest: " + e2, e2);
            }
        }
        return new OCSPReq(new OCSPRequest(tbsReq, signature));
    }

    public OCSPReq build() throws OCSPException {
        return generateRequest(null, null);
    }

    public OCSPReq build(ContentSigner signer, X509CertificateHolder[] chain) throws OCSPException, IllegalArgumentException {
        if (signer == null) {
            throw new IllegalArgumentException("no signer specified");
        }
        return generateRequest(signer, chain);
    }
}
