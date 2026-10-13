package org.spongycastle.est;

import org.spongycastle.cert.X509CRLHolder;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.util.Store;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/est/CACertsResponse.class */
public class CACertsResponse {
    private final Store<X509CertificateHolder> store;
    private Store<X509CRLHolder> crlHolderStore;
    private final ESTRequest requestToRetry;
    private final Source session;
    private final boolean trusted;

    public CACertsResponse(Store<X509CertificateHolder> store, Store<X509CRLHolder> crlHolderStore, ESTRequest requestToRetry, Source session, boolean trusted) {
        this.store = store;
        this.requestToRetry = requestToRetry;
        this.session = session;
        this.trusted = trusted;
        this.crlHolderStore = crlHolderStore;
    }

    public boolean hasCertificates() {
        return this.store != null;
    }

    public Store<X509CertificateHolder> getCertificateStore() {
        if (this.store == null) {
            throw new IllegalStateException("Response has no certificates.");
        }
        return this.store;
    }

    public boolean hasCRLs() {
        return this.crlHolderStore != null;
    }

    public Store<X509CRLHolder> getCrlStore() {
        if (this.crlHolderStore == null) {
            throw new IllegalStateException("Response has no CRLs.");
        }
        return this.crlHolderStore;
    }

    public ESTRequest getRequestToRetry() {
        return this.requestToRetry;
    }

    public Object getSession() {
        return this.session.getSession();
    }

    public boolean isTrusted() {
        return this.trusted;
    }
}
