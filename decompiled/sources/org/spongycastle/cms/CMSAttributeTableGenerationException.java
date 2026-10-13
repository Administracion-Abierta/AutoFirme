package org.spongycastle.cms;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSAttributeTableGenerationException.class */
public class CMSAttributeTableGenerationException extends CMSRuntimeException {
    Exception e;

    public CMSAttributeTableGenerationException(String name) {
        super(name);
    }

    public CMSAttributeTableGenerationException(String name, Exception e) {
        super(name);
        this.e = e;
    }

    @Override // org.spongycastle.cms.CMSRuntimeException
    public Exception getUnderlyingException() {
        return this.e;
    }

    @Override // org.spongycastle.cms.CMSRuntimeException, java.lang.Throwable
    public Throwable getCause() {
        return this.e;
    }
}
