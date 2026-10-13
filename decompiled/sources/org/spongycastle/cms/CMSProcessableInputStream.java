package org.spongycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.spongycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSProcessableInputStream.class */
class CMSProcessableInputStream implements CMSProcessable, CMSReadable {
    private InputStream input;
    private boolean used = false;

    public CMSProcessableInputStream(InputStream input) {
        this.input = input;
    }

    @Override // org.spongycastle.cms.CMSReadable
    public InputStream getInputStream() {
        checkSingleUsage();
        return this.input;
    }

    @Override // org.spongycastle.cms.CMSProcessable
    public void write(OutputStream zOut) throws IOException, CMSException {
        checkSingleUsage();
        Streams.pipeAll(this.input, zOut);
        this.input.close();
    }

    @Override // org.spongycastle.cms.CMSProcessable
    public Object getContent() {
        return getInputStream();
    }

    private synchronized void checkSingleUsage() {
        if (this.used) {
            throw new IllegalStateException("CMSProcessableInputStream can only be used once");
        }
        this.used = true;
    }
}
