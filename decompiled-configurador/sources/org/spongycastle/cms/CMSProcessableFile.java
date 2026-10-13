package org.spongycastle.cms;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.cms.CMSObjectIdentifiers;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/CMSProcessableFile.class */
public class CMSProcessableFile implements CMSTypedData, CMSReadable {
    private static final int DEFAULT_BUF_SIZE = 32768;
    private final ASN1ObjectIdentifier type;
    private final File file;
    private final byte[] buf;

    public CMSProcessableFile(File file) {
        this(file, 32768);
    }

    public CMSProcessableFile(File file, int bufSize) {
        this(new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId()), file, bufSize);
    }

    public CMSProcessableFile(ASN1ObjectIdentifier type, File file, int bufSize) {
        this.type = type;
        this.file = file;
        this.buf = new byte[bufSize];
    }

    @Override // org.spongycastle.cms.CMSReadable
    public InputStream getInputStream() throws IOException, CMSException {
        return new BufferedInputStream(new FileInputStream(this.file), 32768);
    }

    @Override // org.spongycastle.cms.CMSProcessable
    public void write(OutputStream zOut) throws IOException, CMSException {
        FileInputStream fIn = new FileInputStream(this.file);
        while (true) {
            int len = fIn.read(this.buf, 0, this.buf.length);
            if (len > 0) {
                zOut.write(this.buf, 0, len);
            } else {
                fIn.close();
                return;
            }
        }
    }

    @Override // org.spongycastle.cms.CMSProcessable
    public Object getContent() {
        return this.file;
    }

    @Override // org.spongycastle.cms.CMSTypedData
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }
}
