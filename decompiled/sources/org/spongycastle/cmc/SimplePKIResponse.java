package org.spongycastle.cmc;

import java.io.IOException;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.cert.X509CRLHolder;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.CMSSignedData;
import org.spongycastle.util.Encodable;
import org.spongycastle.util.Store;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cmc/SimplePKIResponse.class */
public class SimplePKIResponse implements Encodable {
    private final CMSSignedData certificateResponse;

    private static ContentInfo parseBytes(byte[] responseEncoding) throws CMCException {
        try {
            return ContentInfo.getInstance(ASN1Primitive.fromByteArray(responseEncoding));
        } catch (Exception e) {
            throw new CMCException("malformed data: " + e.getMessage(), e);
        }
    }

    public SimplePKIResponse(byte[] responseEncoding) throws CMCException {
        this(parseBytes(responseEncoding));
    }

    public SimplePKIResponse(ContentInfo signedData) throws CMCException {
        try {
            this.certificateResponse = new CMSSignedData(signedData);
            if (this.certificateResponse.getSignerInfos().size() != 0) {
                throw new CMCException("malformed response: SignerInfo structures found");
            }
            if (this.certificateResponse.getSignedContent() != null) {
                throw new CMCException("malformed response: Signed Content found");
            }
        } catch (CMSException e) {
            throw new CMCException("malformed response: " + e.getMessage(), e);
        }
    }

    public Store<X509CertificateHolder> getCertificates() {
        return this.certificateResponse.getCertificates();
    }

    public Store<X509CRLHolder> getCRLs() {
        return this.certificateResponse.getCRLs();
    }

    @Override // org.spongycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return this.certificateResponse.getEncoded();
    }
}
