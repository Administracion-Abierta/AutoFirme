package org.spongycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.cms.CMSObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSAbsentContent.class */
public class CMSAbsentContent implements CMSTypedData, CMSReadable {
    private final ASN1ObjectIdentifier type;

    public CMSAbsentContent() {
        this(CMSObjectIdentifiers.data);
    }

    public CMSAbsentContent(ASN1ObjectIdentifier type) {
        this.type = type;
    }

    @Override // org.spongycastle.cms.CMSReadable
    public InputStream getInputStream() {
        return null;
    }

    @Override // org.spongycastle.cms.CMSProcessable
    public void write(OutputStream zOut) throws IOException, CMSException {
    }

    @Override // org.spongycastle.cms.CMSProcessable
    public Object getContent() {
        return null;
    }

    @Override // org.spongycastle.cms.CMSTypedData
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }
}
