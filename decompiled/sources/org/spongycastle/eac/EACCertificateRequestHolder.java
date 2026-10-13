package org.spongycastle.eac;

import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.asn1.ASN1ParsingException;
import org.spongycastle.asn1.eac.CVCertificateRequest;
import org.spongycastle.asn1.eac.PublicKeyDataObject;
import org.spongycastle.eac.operator.EACSignatureVerifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/eac/EACCertificateRequestHolder.class */
public class EACCertificateRequestHolder {
    private CVCertificateRequest request;

    private static CVCertificateRequest parseBytes(byte[] requestEncoding) throws IOException {
        try {
            return CVCertificateRequest.getInstance(requestEncoding);
        } catch (ClassCastException e) {
            throw new EACIOException("malformed data: " + e.getMessage(), e);
        } catch (IllegalArgumentException e2) {
            throw new EACIOException("malformed data: " + e2.getMessage(), e2);
        } catch (ASN1ParsingException e3) {
            if (e3.getCause() instanceof IOException) {
                throw ((IOException) e3.getCause());
            }
            throw new EACIOException("malformed data: " + e3.getMessage(), e3);
        }
    }

    public EACCertificateRequestHolder(byte[] certEncoding) throws IOException {
        this(parseBytes(certEncoding));
    }

    public EACCertificateRequestHolder(CVCertificateRequest request) {
        this.request = request;
    }

    public CVCertificateRequest toASN1Structure() {
        return this.request;
    }

    public PublicKeyDataObject getPublicKeyDataObject() {
        return this.request.getPublicKey();
    }

    public boolean isInnerSignatureValid(EACSignatureVerifier verifier) throws EACException {
        try {
            OutputStream vOut = verifier.getOutputStream();
            vOut.write(this.request.getCertificateBody().getEncoded("DER"));
            vOut.close();
            return verifier.verify(this.request.getInnerSignature());
        } catch (Exception e) {
            throw new EACException("unable to process signature: " + e.getMessage(), e);
        }
    }
}
