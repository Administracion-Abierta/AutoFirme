package org.bouncycastle.cms;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSRuntimeException.class */
public class CMSRuntimeException extends RuntimeException {
    Exception e;

    public CMSRuntimeException(String str) {
        super(str);
    }

    public CMSRuntimeException(String str, Exception exc) {
        super(str);
        this.e = exc;
    }

    public Exception getUnderlyingException() {
        return this.e;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.e;
    }
}
