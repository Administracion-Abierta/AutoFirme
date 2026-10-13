package org.spongycastle.est;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/est/CSRRequestResponse.class */
public class CSRRequestResponse {
    private final CSRAttributesResponse attributesResponse;
    private final Source source;

    public CSRRequestResponse(CSRAttributesResponse attributesResponse, Source session) {
        this.attributesResponse = attributesResponse;
        this.source = session;
    }

    public boolean hasAttributesResponse() {
        return this.attributesResponse != null;
    }

    public CSRAttributesResponse getAttributesResponse() {
        if (this.attributesResponse == null) {
            throw new IllegalStateException("Response has no CSRAttributesResponse.");
        }
        return this.attributesResponse;
    }

    public Object getSession() {
        return this.source.getSession();
    }

    public Source getSource() {
        return this.source;
    }
}
