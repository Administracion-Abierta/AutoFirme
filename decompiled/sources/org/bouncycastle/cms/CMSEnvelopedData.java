package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Encodable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSEnvelopedData.class */
public class CMSEnvelopedData implements Encodable {
    RecipientInformationStore recipientInfoStore;
    ContentInfo contentInfo;
    private AlgorithmIdentifier encAlg;
    private ASN1Set unprotectedAttributes;
    private OriginatorInformation originatorInfo;

    public CMSEnvelopedData(byte[] bArr) throws CMSException {
        this(CMSUtils.readContentInfo(bArr));
    }

    public CMSEnvelopedData(InputStream inputStream) throws CMSException {
        this(CMSUtils.readContentInfo(inputStream));
    }

    public CMSEnvelopedData(ContentInfo contentInfo) throws CMSException {
        this.contentInfo = contentInfo;
        try {
            EnvelopedData envelopedData = EnvelopedData.getInstance(contentInfo.getContent());
            if (envelopedData.getOriginatorInfo() != null) {
                this.originatorInfo = new OriginatorInformation(envelopedData.getOriginatorInfo());
            }
            ASN1Set recipientInfos = envelopedData.getRecipientInfos();
            EncryptedContentInfo encryptedContentInfo = envelopedData.getEncryptedContentInfo();
            this.encAlg = encryptedContentInfo.getContentEncryptionAlgorithm();
            this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.encAlg, new CMSEnvelopedHelper.CMSAuthEnveSecureReadable(this.encAlg, encryptedContentInfo.getContentType(), new CMSProcessableByteArray(encryptedContentInfo.getEncryptedContent().getOctets())));
            this.unprotectedAttributes = envelopedData.getUnprotectedAttrs();
        } catch (ClassCastException e) {
            throw new CMSException("Malformed content.", e);
        } catch (IllegalArgumentException e2) {
            throw new CMSException("Malformed content.", e2);
        }
    }

    public OriginatorInformation getOriginatorInfo() {
        return this.originatorInfo;
    }

    public AlgorithmIdentifier getContentEncryptionAlgorithm() {
        return this.encAlg;
    }

    public String getEncryptionAlgOID() {
        return this.encAlg.getAlgorithm().getId();
    }

    public byte[] getEncryptionAlgParams() {
        try {
            return CMSUtils.encodeObj(this.encAlg.getParameters());
        } catch (Exception e) {
            throw new RuntimeException("exception getting encryption parameters " + e);
        }
    }

    public RecipientInformationStore getRecipientInfos() {
        return this.recipientInfoStore;
    }

    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }

    public AttributeTable getUnprotectedAttributes() {
        if (this.unprotectedAttributes == null) {
            return null;
        }
        return new AttributeTable(this.unprotectedAttributes);
    }

    @Override // org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }
}
