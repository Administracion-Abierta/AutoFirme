package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ocsp.OCSPResponse;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/CertificateStatus.class */
public class CertificateStatus {
    protected short statusType;
    protected Object response;

    public CertificateStatus(short statusType, Object response) {
        if (!isCorrectType(statusType, response)) {
            throw new IllegalArgumentException("'response' is not an instance of the correct type");
        }
        this.statusType = statusType;
        this.response = response;
    }

    public short getStatusType() {
        return this.statusType;
    }

    public Object getResponse() {
        return this.response;
    }

    public OCSPResponse getOCSPResponse() {
        if (!isCorrectType((short) 1, this.response)) {
            throw new IllegalStateException("'response' is not an OCSPResponse");
        }
        return (OCSPResponse) this.response;
    }

    public void encode(OutputStream output) throws IOException {
        TlsUtils.writeUint8(this.statusType, output);
        switch (this.statusType) {
            case 1:
                byte[] derEncoding = ((OCSPResponse) this.response).getEncoded(ASN1Encoding.DER);
                TlsUtils.writeOpaque24(derEncoding, output);
                return;
            default:
                throw new TlsFatalAlert((short) 80);
        }
    }

    public static CertificateStatus parse(InputStream input) throws IOException {
        short status_type = TlsUtils.readUint8(input);
        switch (status_type) {
            case 1:
                byte[] derEncoding = TlsUtils.readOpaque24(input);
                Object response = OCSPResponse.getInstance(TlsUtils.readDERObject(derEncoding));
                return new CertificateStatus(status_type, response);
            default:
                throw new TlsFatalAlert((short) 50);
        }
    }

    protected static boolean isCorrectType(short statusType, Object response) {
        switch (statusType) {
            case 1:
                return response instanceof OCSPResponse;
            default:
                throw new IllegalArgumentException("'statusType' is an unsupported CertificateStatusType");
        }
    }
}
