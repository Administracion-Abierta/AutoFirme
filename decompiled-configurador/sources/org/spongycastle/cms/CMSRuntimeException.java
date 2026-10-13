package org.spongycastle.cms;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/CMSRuntimeException.class */
public class CMSRuntimeException extends RuntimeException {
    Exception e;

    public CMSRuntimeException(String name) {
        super(name);
    }

    public CMSRuntimeException(String name, Exception e) {
        super(name);
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
