package org.spongycastle.tsp;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/tsp/TSPValidationException.class */
public class TSPValidationException extends TSPException {
    private int failureCode;

    public TSPValidationException(String message) {
        super(message);
        this.failureCode = -1;
    }

    public TSPValidationException(String message, int failureCode) {
        super(message);
        this.failureCode = -1;
        this.failureCode = failureCode;
    }

    public int getFailureCode() {
        return this.failureCode;
    }
}
