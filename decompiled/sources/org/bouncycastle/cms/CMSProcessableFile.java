package org.bouncycastle.cms;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSProcessableFile.class */
public class CMSProcessableFile implements CMSTypedData, CMSReadable {
    private static final int DEFAULT_BUF_SIZE = 32768;
    private final ASN1ObjectIdentifier type;
    private final File file;
    private final int bufSize;

    public CMSProcessableFile(File file) {
        this(file, 32768);
    }

    public CMSProcessableFile(File file, int i) {
        this(CMSObjectIdentifiers.data, file, i);
    }

    public CMSProcessableFile(ASN1ObjectIdentifier aSN1ObjectIdentifier, File file, int i) {
        this.type = aSN1ObjectIdentifier;
        this.file = file;
        this.bufSize = i;
    }

    @Override // org.bouncycastle.cms.CMSReadable
    public InputStream getInputStream() throws CMSException, IOException {
        return new BufferedInputStream(new FileInputStream(this.file), this.bufSize);
    }

    @Override // org.bouncycastle.cms.CMSProcessable
    public void write(OutputStream outputStream) throws CMSException, IOException {
        FileInputStream fileInputStream = new FileInputStream(this.file);
        Streams.pipeAll(fileInputStream, outputStream, this.bufSize);
        fileInputStream.close();
    }

    @Override // org.bouncycastle.cms.CMSProcessable
    public Object getContent() {
        return this.file;
    }

    @Override // org.bouncycastle.cms.CMSTypedData
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }
}
