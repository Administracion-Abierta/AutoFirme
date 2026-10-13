package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/PKCS7TypedStream.class */
public class PKCS7TypedStream extends CMSTypedStream {
    private final ASN1Encodable content;

    public PKCS7TypedStream(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Encodable aSN1Encodable) throws IOException {
        super(aSN1ObjectIdentifier);
        this.content = aSN1Encodable;
    }

    public ASN1Encodable getContent() {
        return this.content;
    }

    @Override // org.bouncycastle.cms.CMSTypedStream
    public InputStream getContentStream() {
        try {
            return getContentStream(this.content);
        } catch (IOException e) {
            throw new CMSRuntimeException("unable to convert content to stream: " + e.getMessage(), e);
        }
    }

    @Override // org.bouncycastle.cms.CMSTypedStream
    public void drain() throws IOException {
        this.content.toASN1Primitive();
    }

    private InputStream getContentStream(ASN1Encodable aSN1Encodable) throws IOException {
        int i;
        byte[] encoded = aSN1Encodable.toASN1Primitive().getEncoded("DER");
        int i2 = 0 + 1;
        if ((encoded[0] & 31) == 31) {
            do {
                i = i2;
                i2++;
            } while ((encoded[i] & 128) != 0);
        }
        int i3 = i2;
        int i4 = i2 + 1;
        byte b = encoded[i3];
        if ((b & 128) != 0) {
            i4 += b & 127;
        }
        return new ByteArrayInputStream(encoded, i4, encoded.length - i4);
    }
}
