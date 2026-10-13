package org.spongycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.cms.EncryptedContentInfo;
import org.spongycastle.asn1.cms.EnvelopedData;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.util.Encodable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/CMSEnvelopedData.class */
public class CMSEnvelopedData implements Encodable {
    RecipientInformationStore recipientInfoStore;
    ContentInfo contentInfo;
    private AlgorithmIdentifier encAlg;
    private ASN1Set unprotectedAttributes;
    private OriginatorInformation originatorInfo;

    public CMSEnvelopedData(byte[] envelopedData) throws CMSException {
        this(CMSUtils.readContentInfo(envelopedData));
    }

    public CMSEnvelopedData(InputStream envelopedData) throws CMSException {
        this(CMSUtils.readContentInfo(envelopedData));
    }

    public CMSEnvelopedData(ContentInfo contentInfo) throws CMSException {
        this.contentInfo = contentInfo;
        try {
            EnvelopedData envData = EnvelopedData.getInstance(contentInfo.getContent());
            if (envData.getOriginatorInfo() != null) {
                this.originatorInfo = new OriginatorInformation(envData.getOriginatorInfo());
            }
            ASN1Set recipientInfos = envData.getRecipientInfos();
            EncryptedContentInfo encInfo = envData.getEncryptedContentInfo();
            this.encAlg = encInfo.getContentEncryptionAlgorithm();
            CMSReadable readable = new CMSProcessableByteArray(encInfo.getEncryptedContent().getOctets());
            CMSSecureReadable secureReadable = new CMSEnvelopedHelper.CMSEnvelopedSecureReadable(this.encAlg, readable);
            this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.encAlg, secureReadable);
            this.unprotectedAttributes = envData.getUnprotectedAttrs();
        } catch (ClassCastException e) {
            throw new CMSException("Malformed content.", e);
        } catch (IllegalArgumentException e2) {
            throw new CMSException("Malformed content.", e2);
        }
    }

    private byte[] encodeObj(ASN1Encodable obj) throws IOException {
        if (obj != null) {
            return obj.toASN1Primitive().getEncoded();
        }
        return null;
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
            return encodeObj(this.encAlg.getParameters());
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

    @Override // org.spongycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }
}
