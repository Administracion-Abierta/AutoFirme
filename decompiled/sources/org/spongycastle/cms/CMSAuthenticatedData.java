package org.spongycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.AuthenticatedData;
import org.spongycastle.asn1.cms.CMSAlgorithmProtection;
import org.spongycastle.asn1.cms.CMSAttributes;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Encodable;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSAuthenticatedData.class */
public class CMSAuthenticatedData implements Encodable {
    RecipientInformationStore recipientInfoStore;
    ContentInfo contentInfo;
    private AlgorithmIdentifier macAlg;
    private ASN1Set authAttrs;
    private ASN1Set unauthAttrs;
    private byte[] mac;
    private OriginatorInformation originatorInfo;

    public CMSAuthenticatedData(byte[] authData) throws CMSException {
        this(CMSUtils.readContentInfo(authData));
    }

    public CMSAuthenticatedData(byte[] authData, DigestCalculatorProvider digestCalculatorProvider) throws CMSException {
        this(CMSUtils.readContentInfo(authData), digestCalculatorProvider);
    }

    public CMSAuthenticatedData(InputStream authData) throws CMSException {
        this(CMSUtils.readContentInfo(authData));
    }

    public CMSAuthenticatedData(InputStream authData, DigestCalculatorProvider digestCalculatorProvider) throws CMSException {
        this(CMSUtils.readContentInfo(authData), digestCalculatorProvider);
    }

    public CMSAuthenticatedData(ContentInfo contentInfo) throws CMSException {
        this(contentInfo, (DigestCalculatorProvider) null);
    }

    public CMSAuthenticatedData(ContentInfo contentInfo, DigestCalculatorProvider digestCalculatorProvider) throws CMSException {
        this.contentInfo = contentInfo;
        AuthenticatedData authData = AuthenticatedData.getInstance(contentInfo.getContent());
        if (authData.getOriginatorInfo() != null) {
            this.originatorInfo = new OriginatorInformation(authData.getOriginatorInfo());
        }
        ASN1Set recipientInfos = authData.getRecipientInfos();
        this.macAlg = authData.getMacAlgorithm();
        this.authAttrs = authData.getAuthAttrs();
        this.mac = authData.getMac().getOctets();
        this.unauthAttrs = authData.getUnauthAttrs();
        ContentInfo encInfo = authData.getEncapsulatedContentInfo();
        CMSReadable readable = new CMSProcessableByteArray(ASN1OctetString.getInstance(encInfo.getContent()).getOctets());
        if (this.authAttrs != null) {
            if (digestCalculatorProvider == null) {
                throw new CMSException("a digest calculator provider is required if authenticated attributes are present");
            }
            AttributeTable table = new AttributeTable(this.authAttrs);
            ASN1EncodableVector protectionAttributes = table.getAll(CMSAttributes.cmsAlgorithmProtect);
            if (protectionAttributes.size() > 1) {
                throw new CMSException("Only one instance of a cmsAlgorithmProtect attribute can be present");
            }
            if (protectionAttributes.size() > 0) {
                Attribute attr = Attribute.getInstance(protectionAttributes.get(0));
                if (attr.getAttrValues().size() != 1) {
                    throw new CMSException("A cmsAlgorithmProtect attribute MUST contain exactly one value");
                }
                CMSAlgorithmProtection algorithmProtection = CMSAlgorithmProtection.getInstance(attr.getAttributeValues()[0]);
                if (!CMSUtils.isEquivalent(algorithmProtection.getDigestAlgorithm(), authData.getDigestAlgorithm())) {
                    throw new CMSException("CMS Algorithm Identifier Protection check failed for digestAlgorithm");
                }
                if (!CMSUtils.isEquivalent(algorithmProtection.getMacAlgorithm(), this.macAlg)) {
                    throw new CMSException("CMS Algorithm Identifier Protection check failed for macAlgorithm");
                }
            }
            try {
                CMSSecureReadable secureReadable = new CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable(digestCalculatorProvider.get(authData.getDigestAlgorithm()), readable);
                this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.macAlg, secureReadable, new AuthAttributesProvider() { // from class: org.spongycastle.cms.CMSAuthenticatedData.1
                    @Override // org.spongycastle.cms.AuthAttributesProvider
                    public ASN1Set getAuthAttributes() {
                        return CMSAuthenticatedData.this.authAttrs;
                    }
                });
                return;
            } catch (OperatorCreationException e) {
                throw new CMSException("unable to create digest calculator: " + e.getMessage(), e);
            }
        }
        CMSSecureReadable secureReadable2 = new CMSEnvelopedHelper.CMSAuthenticatedSecureReadable(this.macAlg, readable);
        this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.macAlg, secureReadable2);
    }

    public OriginatorInformation getOriginatorInfo() {
        return this.originatorInfo;
    }

    public byte[] getMac() {
        return Arrays.clone(this.mac);
    }

    private byte[] encodeObj(ASN1Encodable obj) throws IOException {
        if (obj != null) {
            return obj.toASN1Primitive().getEncoded();
        }
        return null;
    }

    public AlgorithmIdentifier getMacAlgorithm() {
        return this.macAlg;
    }

    public String getMacAlgOID() {
        return this.macAlg.getAlgorithm().getId();
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

    public ContentInfo getContentInfo() {
        return this.contentInfo;
    }

    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }

    public AttributeTable getAuthAttrs() {
        if (this.authAttrs == null) {
            return null;
        }
        return new AttributeTable(this.authAttrs);
    }

    public AttributeTable getUnauthAttrs() {
        if (this.unauthAttrs == null) {
            return null;
        }
        return new AttributeTable(this.unauthAttrs);
    }

    @Override // org.spongycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }

    public byte[] getContentDigest() {
        if (this.authAttrs != null) {
            return ASN1OctetString.getInstance(getAuthAttrs().get(CMSAttributes.messageDigest).getAttrValues().getObjectAt(0)).getOctets();
        }
        return null;
    }
}
