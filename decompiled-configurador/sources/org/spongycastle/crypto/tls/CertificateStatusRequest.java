package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/CertificateStatusRequest.class */
public class CertificateStatusRequest {
    protected short statusType;
    protected Object request;

    public CertificateStatusRequest(short statusType, Object request) {
        if (!isCorrectType(statusType, request)) {
            throw new IllegalArgumentException("'request' is not an instance of the correct type");
        }
        this.statusType = statusType;
        this.request = request;
    }

    public short getStatusType() {
        return this.statusType;
    }

    public Object getRequest() {
        return this.request;
    }

    public OCSPStatusRequest getOCSPStatusRequest() {
        if (!isCorrectType((short) 1, this.request)) {
            throw new IllegalStateException("'request' is not an OCSPStatusRequest");
        }
        return (OCSPStatusRequest) this.request;
    }

    public void encode(OutputStream output) throws IOException {
        TlsUtils.writeUint8(this.statusType, output);
        switch (this.statusType) {
            case 1:
                ((OCSPStatusRequest) this.request).encode(output);
                return;
            default:
                throw new TlsFatalAlert((short) 80);
        }
    }

    public static CertificateStatusRequest parse(InputStream input) throws IOException {
        short status_type = TlsUtils.readUint8(input);
        switch (status_type) {
            case 1:
                Object result = OCSPStatusRequest.parse(input);
                return new CertificateStatusRequest(status_type, result);
            default:
                throw new TlsFatalAlert((short) 50);
        }
    }

    protected static boolean isCorrectType(short statusType, Object request) {
        switch (statusType) {
            case 1:
                return request instanceof OCSPStatusRequest;
            default:
                throw new IllegalArgumentException("'statusType' is an unsupported CertificateStatusType");
        }
    }
}
