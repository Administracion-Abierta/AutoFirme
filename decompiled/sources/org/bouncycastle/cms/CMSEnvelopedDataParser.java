package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.EncryptedContentInfoParser;
import org.bouncycastle.asn1.cms.EnvelopedDataParser;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSEnvelopedDataParser.class */
public class CMSEnvelopedDataParser extends CMSContentInfoParser {
    RecipientInformationStore recipientInfoStore;
    EnvelopedDataParser envelopedData;
    private AlgorithmIdentifier encAlg;
    private AttributeTable unprotectedAttributes;
    private boolean attrNotRead;
    private OriginatorInformation originatorInfo;

    public CMSEnvelopedDataParser(byte[] bArr) throws CMSException, IOException {
        this(new ByteArrayInputStream(bArr));
    }

    public CMSEnvelopedDataParser(InputStream inputStream) throws CMSException, IOException {
        super(inputStream);
        this.attrNotRead = true;
        this.envelopedData = new EnvelopedDataParser((ASN1SequenceParser) this._contentInfo.getContent(16));
        OriginatorInfo originatorInfo = this.envelopedData.getOriginatorInfo();
        if (originatorInfo != null) {
            this.originatorInfo = new OriginatorInformation(originatorInfo);
        }
        ASN1Set aSN1Set = ASN1Set.getInstance(this.envelopedData.getRecipientInfos().toASN1Primitive());
        EncryptedContentInfoParser encryptedContentInfo = this.envelopedData.getEncryptedContentInfo();
        this.encAlg = encryptedContentInfo.getContentEncryptionAlgorithm();
        this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(aSN1Set, this.encAlg, new CMSEnvelopedHelper.CMSAuthEnveSecureReadable(this.encAlg, encryptedContentInfo.getContentType(), new CMSProcessableInputStream(((ASN1OctetStringParser) encryptedContentInfo.getEncryptedContent(4)).getOctetStream())));
    }

    public String getEncryptionAlgOID() {
        return this.encAlg.getAlgorithm().toString();
    }

    public byte[] getEncryptionAlgParams() {
        try {
            return CMSUtils.encodeObj(this.encAlg.getParameters());
        } catch (Exception e) {
            throw new RuntimeException("exception getting encryption parameters " + e);
        }
    }

    public AlgorithmIdentifier getContentEncryptionAlgorithm() {
        return this.encAlg;
    }

    public OriginatorInformation getOriginatorInfo() {
        return this.originatorInfo;
    }

    public RecipientInformationStore getRecipientInfos() {
        return this.recipientInfoStore;
    }

    public AttributeTable getUnprotectedAttributes() throws IOException {
        if (this.unprotectedAttributes == null && this.attrNotRead) {
            this.attrNotRead = false;
            this.unprotectedAttributes = CMSUtils.getAttributesTable(this.envelopedData.getUnprotectedAttrs());
        }
        return this.unprotectedAttributes;
    }
}
