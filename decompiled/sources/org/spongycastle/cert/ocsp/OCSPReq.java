package org.spongycastle.cert.ocsp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import org.spongycastle.asn1.ASN1Exception;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OutputStream;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ocsp.OCSPRequest;
import org.spongycastle.asn1.ocsp.Request;
import org.spongycastle.asn1.x509.Certificate;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.Extensions;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.cert.CertIOException;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.operator.ContentVerifier;
import org.spongycastle.operator.ContentVerifierProvider;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/ocsp/OCSPReq.class */
public class OCSPReq {
    private static final X509CertificateHolder[] EMPTY_CERTS = new X509CertificateHolder[0];
    private OCSPRequest req;
    private Extensions extensions;

    public OCSPReq(OCSPRequest req) {
        this.req = req;
        this.extensions = req.getTbsRequest().getRequestExtensions();
    }

    public OCSPReq(byte[] req) throws IOException {
        this(new ASN1InputStream(req));
    }

    private OCSPReq(ASN1InputStream aIn) throws IOException {
        try {
            this.req = OCSPRequest.getInstance(aIn.readObject());
            if (this.req == null) {
                throw new CertIOException("malformed request: no request data found");
            }
            this.extensions = this.req.getTbsRequest().getRequestExtensions();
        } catch (ClassCastException e) {
            throw new CertIOException("malformed request: " + e.getMessage(), e);
        } catch (IllegalArgumentException e2) {
            throw new CertIOException("malformed request: " + e2.getMessage(), e2);
        } catch (ASN1Exception e3) {
            throw new CertIOException("malformed request: " + e3.getMessage(), e3);
        }
    }

    public int getVersionNumber() {
        return this.req.getTbsRequest().getVersion().getValue().intValue() + 1;
    }

    public GeneralName getRequestorName() {
        return GeneralName.getInstance(this.req.getTbsRequest().getRequestorName());
    }

    public Req[] getRequestList() {
        ASN1Sequence seq = this.req.getTbsRequest().getRequestList();
        Req[] requests = new Req[seq.size()];
        for (int i = 0; i != requests.length; i++) {
            requests[i] = new Req(Request.getInstance(seq.getObjectAt(i)));
        }
        return requests;
    }

    public boolean hasExtensions() {
        return this.extensions != null;
    }

    public Extension getExtension(ASN1ObjectIdentifier oid) {
        if (this.extensions != null) {
            return this.extensions.getExtension(oid);
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
        if (!isSigned()) {
            return null;
        }
        return this.req.getOptionalSignature().getSignatureAlgorithm().getAlgorithm();
    }

    public byte[] getSignature() {
        if (!isSigned()) {
            return null;
        }
        return this.req.getOptionalSignature().getSignature().getOctets();
    }

    public X509CertificateHolder[] getCerts() {
        if (this.req.getOptionalSignature() != null) {
            ASN1Sequence s = this.req.getOptionalSignature().getCerts();
            if (s != null) {
                X509CertificateHolder[] certs = new X509CertificateHolder[s.size()];
                for (int i = 0; i != certs.length; i++) {
                    certs[i] = new X509CertificateHolder(Certificate.getInstance(s.getObjectAt(i)));
                }
                return certs;
            }
            return EMPTY_CERTS;
        }
        return EMPTY_CERTS;
    }

    public boolean isSigned() {
        return this.req.getOptionalSignature() != null;
    }

    public boolean isSignatureValid(ContentVerifierProvider verifierProvider) throws OCSPException {
        if (!isSigned()) {
            throw new OCSPException("attempt to verify signature on unsigned object");
        }
        try {
            ContentVerifier verifier = verifierProvider.get(this.req.getOptionalSignature().getSignatureAlgorithm());
            OutputStream sOut = verifier.getOutputStream();
            sOut.write(this.req.getTbsRequest().getEncoded("DER"));
            return verifier.verify(getSignature());
        } catch (Exception e) {
            throw new OCSPException("exception processing signature: " + e, e);
        }
    }

    public byte[] getEncoded() throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ASN1OutputStream aOut = new ASN1OutputStream(bOut);
        aOut.writeObject(this.req);
        return bOut.toByteArray();
    }
}
