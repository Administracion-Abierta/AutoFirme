package org.spongycastle.cms;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSException.class */
public class CMSException extends Exception {
    Exception e;

    public CMSException(String msg) {
        super(msg);
    }

    public CMSException(String msg, Exception e) {
        super(msg);
        this.e = e;
    }

    public Exception getUnderlyingException() {
        return this.e;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.e;
    }
}
