package org.mozilla.javascript;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ContinuationPending.class */
public class ContinuationPending extends RuntimeException {
    private static final long serialVersionUID = 4956008116771118856L;
    private NativeContinuation continuationState;
    private Object applicationState;

    protected ContinuationPending(NativeContinuation continuationState) {
        this.continuationState = continuationState;
    }

    public Object getContinuation() {
        return this.continuationState;
    }

    public void setContinuation(NativeContinuation continuation) {
        this.continuationState = continuation;
    }

    NativeContinuation getContinuationState() {
        return this.continuationState;
    }

    public void setApplicationState(Object applicationState) {
        this.applicationState = applicationState;
    }

    public Object getApplicationState() {
        return this.applicationState;
    }
}
