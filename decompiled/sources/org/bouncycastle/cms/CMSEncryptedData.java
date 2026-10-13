package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EncryptedData;
import org.bouncycastle.operator.InputDecryptorProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSEncryptedData.class */
public class CMSEncryptedData {
    private ContentInfo contentInfo;
    private EncryptedData encryptedData;

    public CMSEncryptedData(ContentInfo contentInfo) {
        this.contentInfo = contentInfo;
        this.encryptedData = EncryptedData.getInstance(contentInfo.getContent());
    }

    public byte[] getContent(InputDecryptorProvider inputDecryptorProvider) throws CMSException {
        try {
            return CMSUtils.streamToByteArray(getContentStream(inputDecryptorProvider).getContentStream());
        } catch (IOException e) {
            throw new CMSException("unable to parse internal stream: " + e.getMessage(), e);
        }
    }

    public CMSTypedStream getContentStream(InputDecryptorProvider inputDecryptorProvider) throws CMSException {
        try {
            EncryptedContentInfo encryptedContentInfo = this.encryptedData.getEncryptedContentInfo();
            return new CMSTypedStream(encryptedContentInfo.getContentType(), inputDecryptorProvider.get(encryptedContentInfo.getContentEncryptionAlgorithm()).getInputStream(new ByteArrayInputStream(encryptedContentInfo.getEncryptedContent().getOctets())));
        } catch (Exception e) {
            throw new CMSException("unable to create stream: " + e.getMessage(), e);
        }
    }

    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }
}
