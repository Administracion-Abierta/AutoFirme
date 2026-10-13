package org.bouncycastle.est;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Store;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/EnrollmentResponse.class */
public class EnrollmentResponse {
    private final Store<X509CertificateHolder> store;
    private final long notBefore;
    private final ESTRequest requestToRetry;
    private final Source source;
    private final PrivateKeyInfo privateKeyInfo;

    public EnrollmentResponse(Store<X509CertificateHolder> store, long j, ESTRequest eSTRequest, Source source) {
        this.store = store;
        this.notBefore = j;
        this.requestToRetry = eSTRequest;
        this.source = source;
        this.privateKeyInfo = null;
    }

    public EnrollmentResponse(Store<X509CertificateHolder> store, long j, ESTRequest eSTRequest, Source source, PrivateKeyInfo privateKeyInfo) {
        this.store = store;
        this.notBefore = j;
        this.requestToRetry = eSTRequest;
        this.source = source;
        this.privateKeyInfo = privateKeyInfo;
    }

    public boolean canRetry() {
        return this.notBefore < System.currentTimeMillis();
    }

    public Store<X509CertificateHolder> getStore() {
        return this.store;
    }

    public long getNotBefore() {
        return this.notBefore;
    }

    public ESTRequest getRequestToRetry() {
        return this.requestToRetry;
    }

    public Object getSession() {
        return this.source.getSession();
    }

    public Source getSource() {
        return this.source;
    }

    public boolean isCompleted() {
        return this.requestToRetry == null;
    }

    public PrivateKeyInfo getPrivateKeyInfo() {
        return this.privateKeyInfo;
    }
}
