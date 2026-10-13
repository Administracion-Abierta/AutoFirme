package org.bouncycastle.est;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/CSRRequestResponse.class */
public class CSRRequestResponse {
    private final CSRAttributesResponse attributesResponse;
    private final Source source;

    public CSRRequestResponse(CSRAttributesResponse cSRAttributesResponse, Source source) {
        this.attributesResponse = cSRAttributesResponse;
        this.source = source;
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
