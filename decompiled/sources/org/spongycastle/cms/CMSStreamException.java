package org.spongycastle.cms;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSStreamException.class */
public class CMSStreamException extends IOException {
    private final Throwable underlying;

    CMSStreamException(String msg) {
        super(msg);
        this.underlying = null;
    }

    CMSStreamException(String msg, Throwable underlying) {
        super(msg);
        this.underlying = underlying;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.underlying;
    }
}
