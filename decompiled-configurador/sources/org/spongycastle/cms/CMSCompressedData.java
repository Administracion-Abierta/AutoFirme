package org.spongycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.cms.CompressedData;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.operator.InputExpander;
import org.spongycastle.operator.InputExpanderProvider;
import org.spongycastle.util.Encodable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/CMSCompressedData.class */
public class CMSCompressedData implements Encodable {
    ContentInfo contentInfo;
    CompressedData comData;

    public CMSCompressedData(byte[] compressedData) throws CMSException {
        this(CMSUtils.readContentInfo(compressedData));
    }

    public CMSCompressedData(InputStream compressedData) throws CMSException {
        this(CMSUtils.readContentInfo(compressedData));
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

    public byte[] getContent(InputExpanderProvider expanderProvider) throws CMSException {
        ContentInfo content = this.comData.getEncapContentInfo();
        ASN1OctetString bytes = (ASN1OctetString) content.getContent();
        InputExpander expander = expanderProvider.get(this.comData.getCompressionAlgorithmIdentifier());
        InputStream zIn = expander.getInputStream(bytes.getOctetStream());
        try {
            return CMSUtils.streamToByteArray(zIn);
        } catch (IOException e) {
            throw new CMSException("exception reading compressed stream.", e);
        }
    }

    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }

    @Override // org.spongycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }
}
