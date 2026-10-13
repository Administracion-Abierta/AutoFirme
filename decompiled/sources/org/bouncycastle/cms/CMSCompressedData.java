package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.CompressedData;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.operator.InputExpanderProvider;
import org.bouncycastle.util.Encodable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSCompressedData.class */
public class CMSCompressedData implements Encodable {
    ContentInfo contentInfo;
    CompressedData comData;

    public CMSCompressedData(byte[] bArr) throws CMSException {
        this(CMSUtils.readContentInfo(bArr));
    }

    public CMSCompressedData(InputStream inputStream) throws CMSException {
        this(CMSUtils.readContentInfo(inputStream));
    }

    public CMSCompressedData(ContentInfo contentInfo) throws CMSException {
        this.contentInfo = contentInfo;
        try {
            this.comData = CompressedData.getInstance(contentInfo.getContent());
        } catch (ClassCastException e) {
            throw new CMSException("Malformed content.", e);
        } catch (IllegalArgumentException e2) {
            throw new CMSException("Malformed content.", e2);
        }
    }

    public ASN1ObjectIdentifier getContentType() {
        return this.contentInfo.getContentType();
    }

    public ASN1ObjectIdentifier getCompressedContentType() {
        return this.comData.getEncapContentInfo().getContentType();
    }

    public CMSTypedStream getContentStream(InputExpanderProvider inputExpanderProvider) {
        ContentInfo encapContentInfo = this.comData.getEncapContentInfo();
        return new CMSTypedStream(encapContentInfo.getContentType(), inputExpanderProvider.get(this.comData.getCompressionAlgorithmIdentifier()).getInputStream(((ASN1OctetString) encapContentInfo.getContent()).getOctetStream()));
    }

    public byte[] getContent(InputExpanderProvider inputExpanderProvider) throws CMSException {
        try {
            return CMSUtils.streamToByteArray(inputExpanderProvider.get(this.comData.getCompressionAlgorithmIdentifier()).getInputStream(((ASN1OctetString) this.comData.getEncapContentInfo().getContent()).getOctetStream()));
        } catch (IOException e) {
            throw new CMSException("exception reading compressed stream.", e);
        }
    }

    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }

    @Override // org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }
}
