package org.spongycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.BEROctetString;
import org.spongycastle.asn1.cms.CMSObjectIdentifiers;
import org.spongycastle.asn1.cms.CompressedData;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.OutputCompressor;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSCompressedDataGenerator.class */
public class CMSCompressedDataGenerator {
    public static final String ZLIB = "1.2.840.113549.1.9.16.3.8";

    public CMSCompressedData generate(CMSTypedData content, OutputCompressor compressor) throws CMSException {
        try {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            OutputStream zOut = compressor.getOutputStream(bOut);
            content.write(zOut);
            zOut.close();
            AlgorithmIdentifier comAlgId = compressor.getAlgorithmIdentifier();
            ASN1OctetString comOcts = new BEROctetString(bOut.toByteArray());
            ContentInfo comContent = new ContentInfo(content.getContentType(), comOcts);
            ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.compressedData, new CompressedData(comAlgId, comContent));
            return new CMSCompressedData(contentInfo);
        } catch (IOException e) {
            throw new CMSException("exception encoding data.", e);
        }
    }
}
