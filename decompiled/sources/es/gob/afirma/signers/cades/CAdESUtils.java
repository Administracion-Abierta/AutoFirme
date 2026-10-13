package es.gob.afirma.signers.cades;

import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AdESPolicy;
import es.gob.afirma.signers.pkcs7.AOAlgorithmID;
import es.gob.afirma.signers.pkcs7.SigUtils;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Locale;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.DERUTCTime;
import org.spongycastle.asn1.DERUTF8String;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.cms.CMSAttributes;
import org.spongycastle.asn1.esf.SignerLocation;
import org.spongycastle.asn1.ess.ContentHints;
import org.spongycastle.asn1.ess.ESSCertID;
import org.spongycastle.asn1.ess.ESSCertIDv2;
import org.spongycastle.asn1.ess.SigningCertificate;
import org.spongycastle.asn1.ess.SigningCertificateV2;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.CertificatePolicies;
import org.spongycastle.asn1.x509.DigestInfo;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.asn1.x509.GeneralNames;
import org.spongycastle.asn1.x509.IssuerSerial;
import org.spongycastle.asn1.x509.PolicyInformation;
import org.spongycastle.asn1.x509.X509AttributeIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cades/CAdESUtils.class */
public final class CAdESUtils {
    private CAdESUtils() {
    }

    private static Attribute getSigningCertificateV2(X509Certificate x509Certificate, String str, boolean z, boolean z2) throws NoSuchAlgorithmException, CertificateEncodingException {
        SigningCertificateV2 signingCertificateV2;
        String oid = AOAlgorithmID.getOID(str);
        AlgorithmIdentifier algorithmIdentifierMakeAlgId = AOAlgorithmID.OID_SHA256.equals(oid) ? null : SigUtils.makeAlgId(oid);
        GeneralNames generalNames = new GeneralNames(new GeneralName(X500Name.getInstance(x509Certificate.getIssuerX500Principal().getEncoded())));
        IssuerSerial issuerSerial = null;
        if (z2) {
            issuerSerial = new IssuerSerial(generalNames, x509Certificate.getSerialNumber());
        }
        ESSCertIDv2[] eSSCertIDv2Arr = {new ESSCertIDv2(algorithmIdentifierMakeAlgId, MessageDigest.getInstance(str).digest(x509Certificate.getEncoded()), issuerSerial)};
        PolicyInformation[] policyInformation = z ? getPolicyInformation(x509Certificate) : null;
        if (policyInformation != null) {
            signingCertificateV2 = new SigningCertificateV2(eSSCertIDv2Arr, policyInformation);
        } else {
            signingCertificateV2 = new SigningCertificateV2(eSSCertIDv2Arr);
        }
        return new Attribute(PKCSObjectIdentifiers.id_aa_signingCertificateV2, new DERSet(signingCertificateV2));
    }

    private static Attribute getSigningCertificateV1(X509Certificate x509Certificate, String str, boolean z, boolean z2) throws NoSuchAlgorithmException, CertificateEncodingException {
        SigningCertificate signingCertificate;
        GeneralNames generalNames = new GeneralNames(new GeneralName(X500Name.getInstance(x509Certificate.getIssuerX500Principal().getEncoded())));
        IssuerSerial issuerSerial = null;
        if (z2) {
            issuerSerial = new IssuerSerial(generalNames, x509Certificate.getSerialNumber());
        }
        ESSCertID eSSCertID = new ESSCertID(MessageDigest.getInstance(str).digest(x509Certificate.getEncoded()), issuerSerial);
        PolicyInformation[] policyInformation = z ? getPolicyInformation(x509Certificate) : null;
        if (policyInformation != null) {
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
            aSN1EncodableVector.add(new DERSequence(eSSCertID));
            aSN1EncodableVector.add(new DERSequence(policyInformation));
            signingCertificate = SigningCertificate.getInstance(new DERSequence(aSN1EncodableVector));
        } else {
            signingCertificate = new SigningCertificate(eSSCertID);
        }
        return new Attribute(PKCSObjectIdentifiers.id_aa_signingCertificate, new DERSet(signingCertificate));
    }

    private static DERSet getSigPolicyAttribute(String str, AdESPolicy adESPolicy) throws IOException {
        AlgorithmIdentifier algorithmIdentifierMakeAlgId;
        byte[] bArrDecode;
        ASN1ObjectIdentifier aSN1ObjectIdentifier = new ASN1ObjectIdentifier(adESPolicy.getPolicyIdentifier().toLowerCase(Locale.US).replace("urn:oid:", ""));
        if (adESPolicy.getPolicyIdentifierHashAlgorithm() != null) {
            algorithmIdentifierMakeAlgId = SigUtils.makeAlgId(AOAlgorithmID.getOID(AOSignConstants.getDigestAlgorithmName(adESPolicy.getPolicyIdentifierHashAlgorithm())));
        } else {
            algorithmIdentifierMakeAlgId = SigUtils.makeAlgId(AOAlgorithmID.getOID(str));
        }
        if (adESPolicy.getPolicyIdentifierHash() != null) {
            bArrDecode = Base64.decode(adESPolicy.getPolicyIdentifierHash());
        } else {
            bArrDecode = new byte[]{0};
        }
        DigestInfo digestInfo = new DigestInfo(algorithmIdentifierMakeAlgId, bArrDecode);
        AOSigPolicyQualifierInfo aOSigPolicyQualifierInfo = null;
        if (adESPolicy.getPolicyQualifier() != null) {
            aOSigPolicyQualifierInfo = new AOSigPolicyQualifierInfo(adESPolicy.getPolicyQualifier().toString());
        }
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(aSN1ObjectIdentifier);
        aSN1EncodableVector.add(digestInfo.toASN1Primitive());
        if (aOSigPolicyQualifierInfo != null) {
            aSN1EncodableVector.add(new DERSequence(aOSigPolicyQualifierInfo.toASN1Primitive()));
        }
        return new DERSet(new DERSequence(aSN1EncodableVector).toASN1Primitive());
    }

    public static ASN1EncodableVector generateSignedAttributes(Certificate certificate, CAdESParameters cAdESParameters, boolean z) throws NoSuchAlgorithmException, IOException, CertificateEncodingException {
        byte[] bArrDigest;
        ContentHints contentHints;
        ASN1EncodableVector signerClaimedRoles;
        SignerLocation signerLocation;
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        if (!z) {
            aSN1EncodableVector.add(new Attribute(CMSAttributes.contentType, new DERSet(PKCSObjectIdentifiers.data)));
        }
        if (cAdESParameters.getDataDigest() != null || cAdESParameters.getContentData() != null) {
            ASN1ObjectIdentifier aSN1ObjectIdentifier = CMSAttributes.messageDigest;
            if (cAdESParameters.getDataDigest() != null) {
                bArrDigest = cAdESParameters.getDataDigest();
            } else {
                bArrDigest = MessageDigest.getInstance(cAdESParameters.getDigestAlgorithm()).digest(cAdESParameters.getContentData());
            }
            aSN1EncodableVector.add(new Attribute(aSN1ObjectIdentifier, new DERSet(new DEROctetString(bArrDigest))));
            if (cAdESParameters.isSigningCertificateV2()) {
                aSN1EncodableVector.add(getSigningCertificateV2((X509Certificate) certificate, cAdESParameters.getDigestAlgorithm(), cAdESParameters.isIncludedPolicyOnSigningCertificate(), cAdESParameters.isIncludedIssuerSerial()));
            } else {
                aSN1EncodableVector.add(getSigningCertificateV1((X509Certificate) certificate, cAdESParameters.getDigestAlgorithm(), cAdESParameters.isIncludedPolicyOnSigningCertificate(), cAdESParameters.isIncludedIssuerSerial()));
            }
            if (cAdESParameters.getExternalPolicy() != null && cAdESParameters.getExternalPolicy().getPolicyIdentifier() != null) {
                aSN1EncodableVector.add(new Attribute(PKCSObjectIdentifiers.id_aa_ets_sigPolicyId, getSigPolicyAttribute(cAdESParameters.getDigestAlgorithm(), cAdESParameters.getExternalPolicy())));
            }
            if ((!z || !AOSignConstants.SIGN_PROFILE_BASELINE.equals(cAdESParameters.getProfileSet())) && cAdESParameters.getContentTypeOid() != null) {
                if (cAdESParameters.getContentDescription() != null) {
                    contentHints = new ContentHints(new ASN1ObjectIdentifier(cAdESParameters.getContentTypeOid()), new DERUTF8String(cAdESParameters.getContentDescription()));
                } else {
                    contentHints = new ContentHints(new ASN1ObjectIdentifier(cAdESParameters.getContentTypeOid()));
                }
                aSN1EncodableVector.add(new Attribute(PKCSObjectIdentifiers.id_aa_contentHint, new DERSet(contentHints.toASN1Primitive())));
            }
            if (cAdESParameters.getCommitmentTypeIndications() != null && cAdESParameters.getCommitmentTypeIndications().size() > 0) {
                Iterator<CommitmentTypeIndicationBean> it = cAdESParameters.getCommitmentTypeIndications().iterator();
                while (it.hasNext()) {
                    aSN1EncodableVector.add(new Attribute(PKCSObjectIdentifiers.id_aa_ets_commitmentType, new DERSet(CommitmentTypeIndicationsHelper.generateCommitmentTypeIndication(it.next()).toASN1Primitive())));
                }
            }
            if (cAdESParameters.getMetadata() != null && (signerLocation = CAdESSignerMetadataHelper.getSignerLocation(cAdESParameters.getMetadata().getSignerLocation())) != null) {
                aSN1EncodableVector.add(new Attribute(PKCSObjectIdentifiers.id_aa_ets_signerLocation, new DERSet(signerLocation)));
            }
            if (cAdESParameters.getClaimedRoles() != null && cAdESParameters.getClaimedRoles().length > 0 && (signerClaimedRoles = getSignerClaimedRoles(cAdESParameters.getClaimedRoles())) != null) {
                ASN1ObjectIdentifier aSN1ObjectIdentifier2 = null;
                if ("advanced".equals(cAdESParameters.getProfileSet())) {
                    aSN1ObjectIdentifier2 = PKCSObjectIdentifiers.id_aa_ets_signerAttr;
                } else if (AOSignConstants.SIGN_PROFILE_BASELINE.equals(cAdESParameters.getProfileSet())) {
                    aSN1ObjectIdentifier2 = new ASN1ObjectIdentifier(CAdESAttributes.OID_id_aa_ets_signerAttrV2);
                }
                if (aSN1ObjectIdentifier2 != null) {
                    aSN1EncodableVector.add(new Attribute(aSN1ObjectIdentifier2, new DERSet(new DERSequence(new DERTaggedObject(0, new DERSequence(signerClaimedRoles))))));
                }
            }
            if (cAdESParameters.getSigningTime() != null) {
                aSN1EncodableVector.add(new Attribute(CMSAttributes.signingTime, new DERSet(new DERUTCTime(cAdESParameters.getSigningTime()))));
            }
            if (!z && cAdESParameters.getMimeType() != null) {
                aSN1EncodableVector.add(new Attribute(new ASN1ObjectIdentifier(CAdESAttributes.OID_id_aa_ets_mimeType), new DERSet(new DERUTF8String(cAdESParameters.getMimeType()))));
            }
            return aSN1EncodableVector;
        }
        throw new IllegalArgumentException("Ni los datos a firmar ni la huella de los mismos se han configurado o encontrado ");
    }

    private static ASN1EncodableVector getSignerClaimedRoles(String[] strArr) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        for (String str : strArr) {
            if (str != null && !str.isEmpty()) {
                aSN1EncodableVector.add(new Attribute(X509AttributeIdentifiers.id_at_role, new DERSet(new DERUTF8String(str))));
            }
        }
        if (aSN1EncodableVector.size() > 0) {
            return aSN1EncodableVector;
        }
        return null;
    }

    private static PolicyInformation[] getPolicyInformation(X509Certificate x509Certificate) {
        if (x509Certificate == null) {
            throw new IllegalArgumentException("El certificado no puede ser nulo");
        }
        byte[] extensionValue = x509Certificate.getExtensionValue("2.5.29.32");
        if (extensionValue == null || extensionValue.length < 1) {
            return null;
        }
        return CertificatePolicies.getInstance(ASN1Sequence.getInstance(ASN1OctetString.getInstance(extensionValue).getOctets())).getPolicyInformation();
    }
}
