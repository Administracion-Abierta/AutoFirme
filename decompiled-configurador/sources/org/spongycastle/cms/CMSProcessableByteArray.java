package org.spongycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.cms.CMSObjectIdentifiers;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/CMSProcessableByteArray.class */
public class CMSProcessableByteArray implements CMSTypedData, CMSReadable {
    private final ASN1ObjectIdentifier type;
    private final byte[] bytes;

    public CMSProcessableByteArray(byte[] bytes) {
        this(new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId()), bytes);
    }

    public CMSProcessableByteArray(ASN1ObjectIdentifier type, byte[] bytes) {
        this.type = type;
        this.bytes = bytes;
    }

    @Override // org.spongycastle.cms.CMSReadable
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.bytes);
    }

    @Override // org.spongycastle.cms.CMSProcessable
    public void write(OutputStream zOut) throws IOException, CMSException {
        zOut.write(this.bytes);
    }

    @Override // org.spongycastle.cms.CMSProcessable
    public Object getContent() {
        return Arrays.clone(this.bytes);
    }

    @Override // org.spongycastle.cms.CMSTypedData
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }
}
