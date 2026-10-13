package org.spongycastle.eac;

import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.asn1.ASN1ParsingException;
import org.spongycastle.asn1.eac.CVCertificate;
import org.spongycastle.asn1.eac.PublicKeyDataObject;
import org.spongycastle.eac.operator.EACSignatureVerifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/eac/EACCertificateHolder.class */
public class EACCertificateHolder {
    private CVCertificate cvCertificate;

    private static CVCertificate parseBytes(byte[] certEncoding) throws IOException {
        try {
            return CVCertificate.getInstance(certEncoding);
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

    public EACCertificateHolder(byte[] certEncoding) throws IOException {
        this(parseBytes(certEncoding));
    }

    public EACCertificateHolder(CVCertificate cvCertificate) {
        this.cvCertificate = cvCertificate;
    }

    public CVCertificate toASN1Structure() {
        return this.cvCertificate;
    }

    public PublicKeyDataObject getPublicKeyDataObject() {
        return this.cvCertificate.getBody().getPublicKey();
    }

    public boolean isSignatureValid(EACSignatureVerifier verifier) throws EACException {
        try {
            OutputStream vOut = verifier.getOutputStream();
            vOut.write(this.cvCertificate.getBody().getEncoded("DER"));
            vOut.close();
            return verifier.verify(this.cvCertificate.getSignature());
        } catch (Exception e) {
            throw new EACException("unable to process signature: " + e.getMessage(), e);
        }
    }
}
