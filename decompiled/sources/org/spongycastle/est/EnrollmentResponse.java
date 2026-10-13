package org.spongycastle.est;

import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.util.Store;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/est/EnrollmentResponse.class */
public class EnrollmentResponse {
    private final Store<X509CertificateHolder> store;
    private final long notBefore;
    private final ESTRequest requestToRetry;
    private final Source source;

    public EnrollmentResponse(Store<X509CertificateHolder> store, long notBefore, ESTRequest requestToRetry, Source session) {
        this.store = store;
        this.notBefore = notBefore;
        this.requestToRetry = requestToRetry;
        this.source = session;
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
}
