package org.spongycastle.cert.ocsp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.asn1.ASN1Exception;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ocsp.BasicOCSPResponse;
import org.spongycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.spongycastle.asn1.ocsp.OCSPResponse;
import org.spongycastle.asn1.ocsp.ResponseBytes;
import org.spongycastle.cert.CertIOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/ocsp/OCSPResp.class */
public class OCSPResp {
    public static final int SUCCESSFUL = 0;
    public static final int MALFORMED_REQUEST = 1;
    public static final int INTERNAL_ERROR = 2;
    public static final int TRY_LATER = 3;
    public static final int SIG_REQUIRED = 5;
    public static final int UNAUTHORIZED = 6;
    private OCSPResponse resp;

    public OCSPResp(OCSPResponse resp) {
        this.resp = resp;
    }

    public OCSPResp(byte[] resp) throws IOException {
        this(new ByteArrayInputStream(resp));
    }

    public OCSPResp(InputStream resp) throws IOException {
        this(new ASN1InputStream(resp));
    }

    private OCSPResp(ASN1InputStream aIn) throws IOException {
        try {
            this.resp = OCSPResponse.getInstance(aIn.readObject());
            if (this.resp == null) {
                throw new CertIOException("malformed response: no response data found");
            }
        } catch (ClassCastException e) {
            throw new CertIOException("malformed response: " + e.getMessage(), e);
        } catch (IllegalArgumentException e2) {
            throw new CertIOException("malformed response: " + e2.getMessage(), e2);
        } catch (ASN1Exception e3) {
            throw new CertIOException("malformed response: " + e3.getMessage(), e3);
        }
    }

    public int getStatus() {
        return this.resp.getResponseStatus().getValue().intValue();
    }

    public Object getResponseObject() throws OCSPException {
        ResponseBytes rb = this.resp.getResponseBytes();
        if (rb == null) {
            return null;
        }
        if (rb.getResponseType().equals(OCSPObjectIdentifiers.id_pkix_ocsp_basic)) {
            try {
                ASN1Primitive obj = ASN1Primitive.fromByteArray(rb.getResponse().getOctets());
                return new BasicOCSPResp(BasicOCSPResponse.getInstance(obj));
            } catch (Exception e) {
                throw new OCSPException("problem decoding object: " + e, e);
            }
        }
        return rb.getResponse();
    }

    public byte[] getEncoded() throws IOException {
        return this.resp.getEncoded();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OCSPResp)) {
            return false;
        }
        OCSPResp r = (OCSPResp) o;
        return this.resp.equals(r.resp);
    }

    public int hashCode() {
        return this.resp.hashCode();
    }

    public OCSPResponse toASN1Structure() {
        return this.resp;
    }
}
