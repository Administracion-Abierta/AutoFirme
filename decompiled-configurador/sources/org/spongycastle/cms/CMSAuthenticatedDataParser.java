package org.spongycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1OctetStringParser;
import org.spongycastle.asn1.ASN1SequenceParser;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1SetParser;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.AuthenticatedDataParser;
import org.spongycastle.asn1.cms.CMSAttributes;
import org.spongycastle.asn1.cms.ContentInfoParser;
import org.spongycastle.asn1.cms.OriginatorInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/CMSAuthenticatedDataParser.class */
public class CMSAuthenticatedDataParser extends CMSContentInfoParser {
    RecipientInformationStore recipientInfoStore;
    AuthenticatedDataParser authData;
    private AlgorithmIdentifier macAlg;
    private byte[] mac;
    private AttributeTable authAttrs;
    private ASN1Set authAttrSet;
    private AttributeTable unauthAttrs;
    private boolean authAttrNotRead;
    private boolean unauthAttrNotRead;
    private OriginatorInformation originatorInfo;

    public CMSAuthenticatedDataParser(byte[] envelopedData) throws IOException, CMSException {
        this(new ByteArrayInputStream(envelopedData));
    }

    public CMSAuthenticatedDataParser(byte[] envelopedData, DigestCalculatorProvider digestCalculatorProvider) throws IOException, CMSException {
        this(new ByteArrayInputStream(envelopedData), digestCalculatorProvider);
    }

    public CMSAuthenticatedDataParser(InputStream envelopedData) throws IOException, CMSException {
        this(envelopedData, (DigestCalculatorProvider) null);
    }

    public CMSAuthenticatedDataParser(InputStream envelopedData, DigestCalculatorProvider digestCalculatorProvider) throws IOException, CMSException {
        super(envelopedData);
        this.authAttrNotRead = true;
        this.authData = new AuthenticatedDataParser((ASN1SequenceParser) this._contentInfo.getContent(16));
        OriginatorInfo info = this.authData.getOriginatorInfo();
        if (info != null) {
            this.originatorInfo = new OriginatorInformation(info);
        }
        ASN1Set recipientInfos = ASN1Set.getInstance(this.authData.getRecipientInfos().toASN1Primitive());
        this.macAlg = this.authData.getMacAlgorithm();
        AlgorithmIdentifier digestAlgorithm = this.authData.getDigestAlgorithm();
        if (digestAlgorithm != null) {
            if (digestCalculatorProvider == null) {
                throw new CMSException("a digest calculator provider is required if authenticated attributes are present");
            }
            ContentInfoParser data = this.authData.getEncapsulatedContentInfo();
            CMSReadable readable = new CMSProcessableInputStream(((ASN1OctetStringParser) data.getContent(4)).getOctetStream());
            try {
                CMSSecureReadable secureReadable = new CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable(digestCalculatorProvider.get(digestAlgorithm), readable);
                this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.macAlg, secureReadable, new AuthAttributesProvider() { // from class: org.spongycastle.cms.CMSAuthenticatedDataParser.1
                    @Override // org.spongycastle.cms.AuthAttributesProvider
                    public ASN1Set getAuthAttributes() {
                        try {
                            return CMSAuthenticatedDataParser.this.getAuthAttrSet();
                        } catch (IOException e) {
                            throw new IllegalStateException("can't parse authenticated attributes!");
                        }
                    }
                });
                return;
            } catch (OperatorCreationException e) {
                throw new CMSException("unable to create digest calculator: " + e.getMessage(), e);
            }
        }
        ContentInfoParser data2 = this.authData.getEncapsulatedContentInfo();
        CMSReadable readable2 = new CMSProcessableInputStream(((ASN1OctetStringParser) data2.getContent(4)).getOctetStream());
        CMSSecureReadable secureReadable2 = new CMSEnvelopedHelper.CMSAuthenticatedSecureReadable(this.macAlg, readable2);
        this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.macAlg, secureReadable2);
    }

    public OriginatorInformation getOriginatorInfo() {
        return this.originatorInfo;
    }

    public AlgorithmIdentifier getMacAlgorithm() {
        return this.macAlg;
    }

    public String getMacAlgOID() {
        return this.macAlg.getAlgorithm().toString();
    }

    public byte[] getMacAlgParams() {
        try {
            return encodeObj(this.macAlg.getParameters());
        } catch (Exception e) {
            throw new RuntimeException("exception getting encryption parameters " + e);
        }
    }

    public RecipientInformationStore getRecipientInfos() {
        return this.recipientInfoStore;
    }

    public byte[] getMac() throws IOException {
        if (this.mac == null) {
            getAuthAttrs();
            this.mac = this.authData.getMac().getOctets();
        }
        return Arrays.clone(this.mac);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ASN1Set getAuthAttrSet() throws IOException {
        if (this.authAttrs == null && this.authAttrNotRead) {
            ASN1SetParser set = this.authData.getAuthAttrs();
            if (set != null) {
                this.authAttrSet = (ASN1Set) set.toASN1Primitive();
            }
            this.authAttrNotRead = false;
        }
        return this.authAttrSet;
    }

    public AttributeTable getAuthAttrs() throws IOException {
        ASN1Set set;
        if (this.authAttrs == null && this.authAttrNotRead && (set = getAuthAttrSet()) != null) {
            this.authAttrs = new AttributeTable(set);
        }
        return this.authAttrs;
    }

    public AttributeTable getUnauthAttrs() throws IOException {
        if (this.unauthAttrs == null && this.unauthAttrNotRead) {
            ASN1SetParser set = this.authData.getUnauthAttrs();
            this.unauthAttrNotRead = false;
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
                this.unauthAttrs = new AttributeTable(new DERSet(v));
            }
        }
        return this.unauthAttrs;
    }

    private byte[] encodeObj(ASN1Encodable obj) throws IOException {
        if (obj != null) {
            return obj.toASN1Primitive().getEncoded();
        }
        return null;
    }

    public byte[] getContentDigest() {
        if (this.authAttrs != null) {
            return ASN1OctetString.getInstance(this.authAttrs.get(CMSAttributes.messageDigest).getAttrValues().getObjectAt(0)).getOctets();
        }
        return null;
    }
}
