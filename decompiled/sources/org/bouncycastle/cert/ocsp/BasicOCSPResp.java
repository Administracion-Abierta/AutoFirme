package org.bouncycastle.cert.ocsp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.util.Encodable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/ocsp/BasicOCSPResp.class */
public class BasicOCSPResp implements Encodable {
    private BasicOCSPResponse resp;
    private ResponseData data;
    private Extensions extensions;

    public BasicOCSPResp(BasicOCSPResponse basicOCSPResponse) {
        this.resp = basicOCSPResponse;
        this.data = basicOCSPResponse.getTbsResponseData();
        this.extensions = Extensions.getInstance(basicOCSPResponse.getTbsResponseData().getResponseExtensions());
    }

    public byte[] getTBSResponseData() {
        try {
            return this.resp.getTbsResponseData().getEncoded("DER");
        } catch (IOException e) {
            return null;
        }
    }

    public AlgorithmIdentifier getSignatureAlgorithmID() {
        return this.resp.getSignatureAlgorithm();
    }

    public int getVersion() {
        return this.data.getVersion().intValueExact() + 1;
    }

    public RespID getResponderId() {
        return new RespID(this.data.getResponderID());
    }

    public Date getProducedAt() {
        return OCSPUtils.extractDate(this.data.getProducedAt());
    }

    public SingleResp[] getResponses() {
        ASN1Sequence responses = this.data.getResponses();
        SingleResp[] singleRespArr = new SingleResp[responses.size()];
        for (int i = 0; i != singleRespArr.length; i++) {
            singleRespArr[i] = new SingleResp(SingleResponse.getInstance(responses.getObjectAt(i)));
        }
        return singleRespArr;
    }

    public boolean hasExtensions() {
        return this.extensions != null;
    }

    public Extension getExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (this.extensions != null) {
            return this.extensions.getExtension(aSN1ObjectIdentifier);
        }
        return null;
    }

    public List getExtensionOIDs() {
        return OCSPUtils.getExtensionOIDs(this.extensions);
    }

    public Set getCriticalExtensionOIDs() {
        return OCSPUtils.getCriticalExtensionOIDs(this.extensions);
    }

    public Set getNonCriticalExtensionOIDs() {
        return OCSPUtils.getNonCriticalExtensionOIDs(this.extensions);
    }

    public ASN1ObjectIdentifier getSignatureAlgOID() {
        return this.resp.getSignatureAlgorithm().getAlgorithm();
    }

    public byte[] getSignature() {
        return this.resp.getSignature().getOctets();
    }

    public X509CertificateHolder[] getCerts() {
        ASN1Sequence certs;
        if (this.resp.getCerts() != null && (certs = this.resp.getCerts()) != null) {
            X509CertificateHolder[] x509CertificateHolderArr = new X509CertificateHolder[certs.size()];
            for (int i = 0; i != x509CertificateHolderArr.length; i++) {
                x509CertificateHolderArr[i] = new X509CertificateHolder(Certificate.getInstance(certs.getObjectAt(i)));
            }
            return x509CertificateHolderArr;
        }
        return OCSPUtils.EMPTY_CERTS;
    }

    public boolean isSignatureValid(ContentVerifierProvider contentVerifierProvider) throws OCSPException {
        try {
            ContentVerifier contentVerifier = contentVerifierProvider.get(this.resp.getSignatureAlgorithm());
            OutputStream outputStream = contentVerifier.getOutputStream();
            outputStream.write(this.resp.getTbsResponseData().getEncoded("DER"));
            outputStream.close();
            return contentVerifier.verify(getSignature());
        } catch (Exception e) {
            throw new OCSPException("exception processing sig: " + e, e);
        }
    }

    @Override // org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return this.resp.getEncoded();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BasicOCSPResp) {
            return this.resp.equals(((BasicOCSPResp) obj).resp);
        }
        return false;
    }

    public int hashCode() {
        return this.resp.hashCode();
    }
}
