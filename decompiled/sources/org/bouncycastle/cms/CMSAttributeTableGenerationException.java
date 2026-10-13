package org.bouncycastle.cms;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSAttributeTableGenerationException.class */
public class CMSAttributeTableGenerationException extends CMSRuntimeException {
    Exception e;

    public CMSAttributeTableGenerationException(String str) {
        super(str);
    }

    public CMSAttributeTableGenerationException(String str, Exception exc) {
        super(str);
        this.e = exc;
    }

    @Override // org.bouncycastle.cms.CMSRuntimeException
    public Exception getUnderlyingException() {
        return this.e;
    }

    @Override // org.bouncycastle.cms.CMSRuntimeException, java.lang.Throwable
    public Throwable getCause() {
        return this.e;
    }
}
