package org.spongycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1OctetStringParser;
import org.spongycastle.asn1.ASN1SequenceParser;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1SetParser;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.EncryptedContentInfoParser;
import org.spongycastle.asn1.cms.EnvelopedDataParser;
import org.spongycastle.asn1.cms.OriginatorInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSEnvelopedDataParser.class */
public class CMSEnvelopedDataParser extends CMSContentInfoParser {
    RecipientInformationStore recipientInfoStore;
    EnvelopedDataParser envelopedData;
    private AlgorithmIdentifier encAlg;
    private AttributeTable unprotectedAttributes;
    private boolean attrNotRead;
    private OriginatorInformation originatorInfo;

    public CMSEnvelopedDataParser(byte[] envelopedData) throws IOException, CMSException {
        this(new ByteArrayInputStream(envelopedData));
    }

    public CMSEnvelopedDataParser(InputStream envelopedData) throws IOException, CMSException {
        super(envelopedData);
        this.attrNotRead = true;
        this.envelopedData = new EnvelopedDataParser((ASN1SequenceParser) this._contentInfo.getContent(16));
        OriginatorInfo info = this.envelopedData.getOriginatorInfo();
        if (info != null) {
            this.originatorInfo = new OriginatorInformation(info);
        }
        ASN1Set recipientInfos = ASN1Set.getInstance(this.envelopedData.getRecipientInfos().toASN1Primitive());
        EncryptedContentInfoParser encInfo = this.envelopedData.getEncryptedContentInfo();
        this.encAlg = encInfo.getContentEncryptionAlgorithm();
        CMSReadable readable = new CMSProcessableInputStream(((ASN1OctetStringParser) encInfo.getEncryptedContent(4)).getOctetStream());
        CMSSecureReadable secureReadable = new CMSEnvelopedHelper.CMSEnvelopedSecureReadable(this.encAlg, readable);
        this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.encAlg, secureReadable);
    }

    public String getEncryptionAlgOID() {
        return this.encAlg.getAlgorithm().toString();
    }

    public byte[] getEncryptionAlgParams() {
        try {
            return encodeObj(this.encAlg.getParameters());
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
            ASN1SetParser set = this.envelopedData.getUnprotectedAttrs();
            this.attrNotRead = false;
            if (set != null) {
                ASN1EncodableVector v = new ASN1EncodableVector();
                while (true) {
                    ASN1Encodable o = set.readObject();
                    if (o == null) {
                        break;
                    }
                    ASN1SequenceParser seq = (ASN1SequenceParser) o;
                    v.add(seq.toASN1Primitive());
                }
                this.unprotectedAttributes = new AttributeTable(new DERSet(v));
            }
        }
        return this.unprotectedAttributes;
    }

    private byte[] encodeObj(ASN1Encodable obj) throws IOException {
        if (obj != null) {
            return obj.toASN1Primitive().getEncoded();
        }
        return null;
    }
}
