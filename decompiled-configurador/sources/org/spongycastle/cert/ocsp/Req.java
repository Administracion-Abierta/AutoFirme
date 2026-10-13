package org.spongycastle.cert.ocsp;

import org.spongycastle.asn1.ocsp.Request;
import org.spongycastle.asn1.x509.Extensions;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/ocsp/Req.class */
public class Req {
    private Request req;

    public Req(Request req) {
        this.req = req;
    }

    public CertificateID getCertID() {
        return new CertificateID(this.req.getReqCert());
    }

    public Extensions getSingleRequestExtensions() {
        return this.req.getSingleRequestExtensions();
    }
}
