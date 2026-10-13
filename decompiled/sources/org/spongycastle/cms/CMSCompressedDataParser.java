package org.spongycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.asn1.ASN1OctetStringParser;
import org.spongycastle.asn1.ASN1SequenceParser;
import org.spongycastle.asn1.cms.CompressedDataParser;
import org.spongycastle.asn1.cms.ContentInfoParser;
import org.spongycastle.operator.InputExpander;
import org.spongycastle.operator.InputExpanderProvider;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSCompressedDataParser.class */
public class CMSCompressedDataParser extends CMSContentInfoParser {
    public CMSCompressedDataParser(byte[] compressedData) throws CMSException {
        this(new ByteArrayInputStream(compressedData));
    }

    public CMSCompressedDataParser(InputStream compressedData) throws CMSException {
        super(compressedData);
    }

    public CMSTypedStream getContent(InputExpanderProvider expanderProvider) throws CMSException {
        try {
            CompressedDataParser comData = new CompressedDataParser((ASN1SequenceParser) this._contentInfo.getContent(16));
            ContentInfoParser content = comData.getEncapContentInfo();
            InputExpander expander = expanderProvider.get(comData.getCompressionAlgorithmIdentifier());
            ASN1OctetStringParser bytes = (ASN1OctetStringParser) content.getContent(4);
            return new CMSTypedStream(content.getContentType().getId(), expander.getInputStream(bytes.getOctetStream()));
        } catch (IOException e) {
            throw new CMSException("IOException reading compressed content.", e);
        }
    }
}
