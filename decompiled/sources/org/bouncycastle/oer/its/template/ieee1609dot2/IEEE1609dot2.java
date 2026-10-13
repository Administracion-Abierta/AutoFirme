package org.bouncycastle.oer.its.template.ieee1609dot2;

import com.aowagie.text.ElementTags;
import com.ctc.wstx.cfg.XmlConsts;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.ElementSupplier;
import org.bouncycastle.oer.OERDefinition;
import org.bouncycastle.oer.Switch;
import org.bouncycastle.oer.SwitchIndexer;
import org.bouncycastle.oer.its.template.etsi103097.extension.EtsiTs103097ExtensionModule;
import org.bouncycastle.oer.its.template.ieee1609dot2.basetypes.Ieee1609Dot2BaseTypes;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/template/ieee1609dot2/IEEE1609dot2.class */
public class IEEE1609dot2 {
    public static final OERDefinition.Builder Opaque = OERDefinition.octets().typeName("Opaque");
    public static final OERDefinition.Builder PduFunctionalType = OERDefinition.integer(0, 255).typeName("PduFunctionalType");
    public static final OERDefinition.Builder HashedData = OERDefinition.choice(OERDefinition.octets(32).label("sha256HashedData"), OERDefinition.extension(OERDefinition.octets(48).label("sha384HashedData"), OERDefinition.octets(32).label("reserved"))).typeName("HashedData");
    public static final OERDefinition.Builder MissingCrlIdentifier = OERDefinition.seq(Ieee1609Dot2BaseTypes.HashedId3.label("cracaId"), Ieee1609Dot2BaseTypes.CrlSeries.label("crlSeries"), OERDefinition.extension(new Object[0])).typeName("MissingCrlIdentifier");
    private static final ASN1Integer etsiHeaderInfoContributorId = new ASN1Integer(BigIntegers.TWO);
    private static final ASN1Encodable[] extensionBlockSwitchKeys = {etsiHeaderInfoContributorId};
    public static final OERDefinition.Builder HeaderInfoContributorId = OERDefinition.integer(0, 255).typeName("HeaderInfoContributorId").validSwitchValue(etsiHeaderInfoContributorId);
    public static final Switch ContributedExtensionBlockSwitch = new Switch() { // from class: org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2.1
        @Override // org.bouncycastle.oer.Switch
        public ASN1Encodable[] keys() {
            return IEEE1609dot2.extensionBlockSwitchKeys;
        }

        @Override // org.bouncycastle.oer.Switch
        public Element result(SwitchIndexer switchIndexer) {
            ASN1Integer aSN1Integer = ASN1Integer.getInstance(switchIndexer.get(0).toASN1Primitive());
            if (aSN1Integer.equals((ASN1Primitive) IEEE1609dot2.etsiHeaderInfoContributorId)) {
                return OERDefinition.seqof(EtsiTs103097ExtensionModule.EtsiOriginatingHeaderInfoExtension).rangeToMAXFrom(1L).label("extns").build();
            }
            throw new IllegalArgumentException("No forward definition for type id " + aSN1Integer);
        }
    };
    public static final OERDefinition.Builder ContributedExtensionBlock = OERDefinition.seq(HeaderInfoContributorId.label("contributorId"), OERDefinition.aSwitch(ContributedExtensionBlockSwitch).label("Extn")).typeName("ContributedExtensionBlock");
    public static final OERDefinition.Builder ContributedExtensionBlocks = OERDefinition.seqof(ContributedExtensionBlock).rangeToMAXFrom(1).typeName("ContributedExtensionBlocks");
    public static final OERDefinition.Builder PreSharedKeyRecipientInfo = Ieee1609Dot2BaseTypes.HashedId8.typeName("PreSharedKeyRecipientInfo");
    public static final OERDefinition.Builder EncryptedDataEncryptionKey = OERDefinition.choice(Ieee1609Dot2BaseTypes.EciesP256EncryptedKey.label("eciesNistP256"), Ieee1609Dot2BaseTypes.EciesP256EncryptedKey.label("eciesBrainpoolP256r1"), OERDefinition.extension(new Object[0])).typeName("EncryptedDataEncryptionKey");
    public static final OERDefinition.Builder PKRecipientInfo = OERDefinition.seq(Ieee1609Dot2BaseTypes.HashedId8.label("recipientId"), EncryptedDataEncryptionKey.label("encKey")).typeName("PKRecipientInfo");
    public static final OERDefinition.Builder AesCcmCiphertext = OERDefinition.seq(OERDefinition.octets(12).label("nonce"), Opaque.label("ccmCiphertext")).typeName("AesCcmCiphertext");
    public static final OERDefinition.Builder SymmetricCiphertext = OERDefinition.choice(AesCcmCiphertext.label("aes128ccm"), OERDefinition.extension(new Object[0])).typeName("SymmetricCiphertext");
    public static final OERDefinition.Builder SymmRecipientInfo = OERDefinition.seq(Ieee1609Dot2BaseTypes.HashedId8.label("recipientId"), SymmetricCiphertext.label("encKey")).typeName("SymmRecipientInfo");
    public static final OERDefinition.Builder RecipientInfo = OERDefinition.choice(PreSharedKeyRecipientInfo.label("pskRecipInfo"), SymmRecipientInfo.label("symmRecipInfo"), PKRecipientInfo.label("certRecipInfo"), PKRecipientInfo.label("signedDataRecipInfo"), PKRecipientInfo.label("rekRecipInfo")).typeName("RecipientInfo");
    public static final OERDefinition.Builder SequenceOfRecipientInfo = OERDefinition.seqof(RecipientInfo).typeName("SequenceOfRecipientInfo");
    public static final OERDefinition.Builder EncryptedData = OERDefinition.seq(SequenceOfRecipientInfo.label("recipients"), SymmetricCiphertext.label("ciphertext")).typeName("EncryptedData");
    public static final OERDefinition.Builder EndEntityType = OERDefinition.bitString(8).defaultValue(new DERBitString(new byte[]{0}, 0)).typeName("EndEntityType");
    public static final OERDefinition.Builder SubjectPermissions = OERDefinition.choice(Ieee1609Dot2BaseTypes.SequenceOfPsidSspRange.label("explicit"), OERDefinition.nullValue().label(PdfExtraParams.PAGES_TO_CHECK_PSA_VALUE_ALL), OERDefinition.extension(new Object[0])).typeName("SubjectPermissions");
    public static final OERDefinition.Builder VerificationKeyIndicator = OERDefinition.choice(Ieee1609Dot2BaseTypes.PublicVerificationKey.label("verificationKey"), Ieee1609Dot2BaseTypes.EccP256CurvePoint.label("reconstructionValue"), OERDefinition.extension(new Object[0])).typeName("VerificationKeyIndicator");
    public static final OERDefinition.Builder PsidGroupPermissions = OERDefinition.seq(SubjectPermissions.label("subjectPermissions"), OERDefinition.integer(1).label("minChainLength"), OERDefinition.integer(0).label("chainLengthRange"), EndEntityType.label("eeType")).typeName("PsidGroupPermissions");
    public static final OERDefinition.Builder SequenceOfPsidGroupPermissions = OERDefinition.seqof(PsidGroupPermissions).typeName("SequenceOfPsidGroupPermissions");
    public static final OERDefinition.Builder LinkageData = OERDefinition.seq(Ieee1609Dot2BaseTypes.IValue.label("iCert"), Ieee1609Dot2BaseTypes.LinkageValue.label("linkageValue"), OERDefinition.optional(Ieee1609Dot2BaseTypes.GroupLinkageValue.label("groupLinkageValue")), OERDefinition.extension(new Object[0])).typeName("LinkageData");
    public static final OERDefinition.Builder CertificateId = OERDefinition.choice(LinkageData.label("linkageData"), Ieee1609Dot2BaseTypes.Hostname.label("name"), OERDefinition.octets(1, 64).label("binaryId"), OERDefinition.nullValue().label("none"), OERDefinition.extension(new Object[0])).typeName("CertificateId");
    public static final OERDefinition.Builder ToBeSignedCertificate = OERDefinition.seq(CertificateId.label(ElementTags.ID), Ieee1609Dot2BaseTypes.HashedId3.label("cracaId"), Ieee1609Dot2BaseTypes.CrlSeries.label("crlSeries"), Ieee1609Dot2BaseTypes.ValidityPeriod.label("validityPeriod"), OERDefinition.optional(Ieee1609Dot2BaseTypes.GeographicRegion.label("region"), Ieee1609Dot2BaseTypes.SubjectAssurance.label("assuranceLevel"), Ieee1609Dot2BaseTypes.SequenceOfPsidSsp.label("appPermissions"), SequenceOfPsidGroupPermissions.label("certIssuePermissions"), SequenceOfPsidGroupPermissions.label("certRequestPermissions"), OERDefinition.nullValue().label("canRequestRollover"), Ieee1609Dot2BaseTypes.PublicEncryptionKey.label("encryptionKey")), VerificationKeyIndicator.label("verifyKeyIndicator"), OERDefinition.extension(new Object[0])).typeName("ToBeSignedCertificate");
    public static final OERDefinition.Builder IssuerIdentifier = OERDefinition.choice(Ieee1609Dot2BaseTypes.HashedId8.label("sha256AndDigest"), Ieee1609Dot2BaseTypes.HashAlgorithm.label("self"), OERDefinition.extension(Ieee1609Dot2BaseTypes.HashedId8.label("sha384AndDigest"))).typeName("IssuerIdentifier");
    public static final OERDefinition.Builder CertificateType = OERDefinition.enumeration(OERDefinition.enumItem("explicit"), OERDefinition.enumItem(AOSignConstants.SIGN_MODE_IMPLICIT), OERDefinition.extension(new Object[0])).typeName("CertificateType");
    private static ASN1Integer explicitOrdinal = new ASN1Integer(BigInteger.ZERO);
    private static ASN1Integer implicitOrdinal = new ASN1Integer(BigInteger.ONE);
    public static final OERDefinition.Builder CertificateBase = OERDefinition.seq(Ieee1609Dot2BaseTypes.UINT8.label(XmlConsts.XML_DECL_KW_VERSION), CertificateType.label("type"), IssuerIdentifier.label("issuer"), ToBeSignedCertificate.label("toBeSigned"), OERDefinition.optional(Ieee1609Dot2BaseTypes.Signature.label("signature"))).label("signature").typeName("CertificateBase");
    public static final OERDefinition.Builder Certificate = CertificateBase.copy().typeName("Certificate");
    public static final OERDefinition.Builder ExplicitCertificate = CertificateBase.typeName("ExplicitCertificate").replaceChild(1, CertificateType.validSwitchValue(explicitOrdinal).label("type"));
    public static final OERDefinition.Builder ImplicitCertificate = CertificateBase.typeName("ImplicitCertificate").replaceChild(1, CertificateType.validSwitchValue(implicitOrdinal).label("type"));
    public static final OERDefinition.Builder SequenceOfCertificate = OERDefinition.seqof(Certificate).typeName("SequenceOfCertificate");
    public static final OERDefinition.Builder SignerIdentifier = OERDefinition.choice(Ieee1609Dot2BaseTypes.HashedId8.label("digest"), SequenceOfCertificate.label("certificate"), OERDefinition.nullValue().label("self"), OERDefinition.extension(new Object[0])).typeName("SignerIdentifier");
    public static final OERDefinition.Builder HeaderInfo = OERDefinition.seq(Ieee1609Dot2BaseTypes.Psid.label("psid"), OERDefinition.optional(Ieee1609Dot2BaseTypes.Time64.label("generationTime"), Ieee1609Dot2BaseTypes.Time64.label("expiryTime"), Ieee1609Dot2BaseTypes.ThreeDLocation.label("generationLocation"), Ieee1609Dot2BaseTypes.HashedId3.label("p2pcdLearningRequest"), MissingCrlIdentifier.label("missingCrlIdentifier"), Ieee1609Dot2BaseTypes.EncryptionKey.label("encryptionKey")), OERDefinition.extension(OERDefinition.optional(Ieee1609Dot2BaseTypes.SequenceOfHashedId3.label("inlineP2pcdRequest"), Certificate.label("requestedCertificate"), PduFunctionalType.label("pduFunctionalType"), ContributedExtensionBlocks.label("contributedExtensions")))).typeName("HeaderInfo");
    public static final OERDefinition.Builder SignedData = OERDefinition.seq(Ieee1609Dot2BaseTypes.HashAlgorithm.label("hashId"), OERDefinition.deferred(new ElementSupplier() { // from class: org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2.2
        private Element built;

        @Override // org.bouncycastle.oer.ElementSupplier
        public Element build() {
            Element element;
            synchronized (this) {
                if (this.built == null) {
                    this.built = IEEE1609dot2.ToBeSignedData.label("tbsData").build();
                }
                element = this.built;
            }
            return element;
        }
    }).label("tbsData"), SignerIdentifier.label("signer"), Ieee1609Dot2BaseTypes.Signature.label("signature")).typeName(AOSignConstants.CMS_CONTENTTYPE_SIGNEDDATA);
    public static final OERDefinition.Builder Ieee1609Dot2Content = OERDefinition.choice(Opaque.label("unsecuredData"), OERDefinition.deferred(new ElementSupplier() { // from class: org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2.3
        private Element built;

        @Override // org.bouncycastle.oer.ElementSupplier
        public Element build() {
            Element element;
            synchronized (this) {
                if (this.built == null) {
                    this.built = IEEE1609dot2.SignedData.label("signedData").mayRecurse(true).build();
                }
                element = this.built;
            }
            return element;
        }
    }).label("signedData").mayRecurse(true), EncryptedData.label("encryptedData"), Opaque.label("signedCertificateRequest"), OERDefinition.extension(new Object[0])).typeName("Ieee1609Dot2Content");
    public static final OERDefinition.Builder CounterSignature = OERDefinition.seq(Ieee1609Dot2BaseTypes.UINT8.label("protocolVersion"), Ieee1609Dot2Content.label("content")).typeName("CounterSignature");
    public static final OERDefinition.Builder Ieee1609Dot2Data = OERDefinition.seq(Ieee1609Dot2BaseTypes.UINT8.validSwitchValue(new ASN1Integer(3)).label("protocolVersion"), Ieee1609Dot2Content.label("content")).typeName("Ieee1609Dot2Data");
    public static final OERDefinition.Builder SignedDataPayload = OERDefinition.seq(OERDefinition.optional(Ieee1609Dot2Data.label("data"), HashedData.label("extDataHash")), OERDefinition.extension(new Object[0])).typeName("SignedDataPayload");
    public static final OERDefinition.Builder ToBeSignedData = OERDefinition.seq(SignedDataPayload.label("payload"), HeaderInfo.label("headerInfo")).typeName("ToBeSignedData");
}
