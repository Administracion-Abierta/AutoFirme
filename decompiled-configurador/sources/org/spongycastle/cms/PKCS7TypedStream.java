package org.spongycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1ObjectIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/PKCS7TypedStream.class */
public class PKCS7TypedStream extends CMSTypedStream {
    private final ASN1Encodable content;

    public PKCS7TypedStream(ASN1ObjectIdentifier oid, ASN1Encodable encodable) throws IOException {
        super(oid);
        this.content = encodable;
    }

    public ASN1Encodable getContent() {
        return this.content;
    }

    @Override // org.spongycastle.cms.CMSTypedStream
    public InputStream getContentStream() {
        try {
            return getContentStream(this.content);
        } catch (IOException e) {
            throw new CMSRuntimeException("unable to convert content to stream: " + e.getMessage(), e);
        }
    }

    @Override // org.spongycastle.cms.CMSTypedStream
    public void drain() throws IOException {
        getContentStream(this.content);
    }

    private InputStream getContentStream(ASN1Encodable encodable) throws IOException {
        byte[] encoded = encodable.toASN1Primitive().getEncoded(ASN1Encoding.DER);
        int index = 1;
        while ((encoded[index] & 255) > 127) {
            index++;
        }
        int index2 = index + 1;
        return new ByteArrayInputStream(encoded, index2, encoded.length - index2);
    }
}
