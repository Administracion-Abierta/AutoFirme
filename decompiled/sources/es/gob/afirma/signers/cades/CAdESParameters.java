package es.gob.afirma.signers.cades;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.MimeHelper;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AdESPolicy;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERUTF8String;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.CMSAttributes;
import org.spongycastle.asn1.ess.ContentHints;
import org.spongycastle.cms.CMSSignedData;
import org.spongycastle.cms.SignerInformation;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cades/CAdESParameters.class */
public class CAdESParameters {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String SHA1_ALGORITHM = "SHA1";
    private AdESPolicy externalPolicy;
    private String digestAlgorithm;
    private byte[] dataDigest;
    private byte[] contentData;
    private Date signingTime;
    private String contentTypeOid;
    private String contentDescription;
    private String mimetype;
    private List<CommitmentTypeIndicationBean> commitmentTypeIndications;
    private String[] claimedRoles;
    private CAdESSignerMetadata metadata;
    private Properties extraParams;
    private boolean signingCertificateV2 = true;
    private boolean includedOnlySigningCertificate = false;
    private boolean contentNeeded = true;
    private boolean includedPolicyOnSigningCertificate = true;
    private boolean includedIssuerSerial = true;
    private String profileSet = "advanced";

    public static CAdESParameters load(byte[] bArr, String str, Properties properties) throws AOException {
        return load(bArr, null, str, properties);
    }

    public static CAdESParameters load(byte[] bArr, CMSSignedData cMSSignedData, String str, Properties properties) throws AOException {
        boolean z;
        String digestAlgorithmName;
        byte[] bArr2;
        byte[] bArrDigest;
        Attribute attribute;
        DERUTF8String contentDescription;
        Attribute attribute2;
        CAdESParameters cAdESParameters = new CAdESParameters();
        if (AOSignConstants.isSHA2SignatureAlgorithm(str)) {
            z = true;
        } else if (properties.containsKey(CAdESExtraParams.SIGNING_CERTIFICATE_V2)) {
            z = Boolean.parseBoolean(properties.getProperty(CAdESExtraParams.SIGNING_CERTIFICATE_V2));
        } else {
            z = !"SHA1".equals(AOSignConstants.getDigestAlgorithmName(str));
        }
        cAdESParameters.setSigningCertificateV2(z);
        String property = properties.getProperty("precalculatedHashAlgorithm");
        boolean z2 = false;
        String property2 = properties.getProperty(CAdESExtraParams.MODE, "explicit");
        if (property != null || "explicit".equalsIgnoreCase(property2)) {
            z2 = true;
        }
        cAdESParameters.setContentNeeded(!z2);
        if (property != null) {
            digestAlgorithmName = AOSignConstants.getDigestAlgorithmName(property);
            bArrDigest = bArr;
            bArr2 = null;
        } else {
            digestAlgorithmName = AOSignConstants.getDigestAlgorithmName(str);
            bArr2 = z2 ? null : bArr;
            if (bArr != null) {
                try {
                    bArrDigest = MessageDigest.getInstance(digestAlgorithmName).digest(bArr);
                } catch (NoSuchAlgorithmException e) {
                    throw new AOException("Algoritmo no soportado: " + e, e);
                }
            } else {
                bArrDigest = null;
            }
        }
        cAdESParameters.setContentData(bArr2);
        cAdESParameters.setDigestAlgorithm(digestAlgorithmName);
        cAdESParameters.setDataDigest(bArrDigest);
        String[] strArrSplit = null;
        String property3 = properties.getProperty("signerClaimedRoles");
        if (property3 != null && !property3.isEmpty()) {
            strArrSplit = property3.split(Pattern.quote("|"));
        }
        cAdESParameters.setClaimedRoles(strArrSplit);
        cAdESParameters.setIncludedPolicyOnSigningCertificate(!Boolean.parseBoolean(properties.getProperty("doNotIncludePolicyOnSigningCertificate", Boolean.FALSE.toString())));
        cAdESParameters.setExternalPolicy(AdESPolicy.buildAdESPolicy(properties));
        cAdESParameters.setIncludedOnlySigningCertificate(Boolean.parseBoolean(properties.getProperty("includeOnlySignningCertificate", Boolean.FALSE.toString())));
        cAdESParameters.setCommitmentTypeIndications(CommitmentTypeIndicationsHelper.getCommitmentTypeIndications(properties));
        boolean z3 = true;
        if (properties.containsKey(CAdESExtraParams.INCLUDE_SIGNING_TIME_ATTRIBUTE)) {
            z3 = Boolean.parseBoolean(properties.getProperty(CAdESExtraParams.INCLUDE_SIGNING_TIME_ATTRIBUTE));
        }
        if (z3) {
            cAdESParameters.setSigningTime(new Date());
        }
        boolean z4 = (properties.containsKey(CAdESExtraParams.INCLUDE_CONTENT_HINT_ATTRIBUTE) && Boolean.FALSE.toString().equalsIgnoreCase(properties.getProperty(CAdESExtraParams.INCLUDE_CONTENT_HINT_ATTRIBUTE))) ? false : true;
        boolean z5 = Boolean.parseBoolean(properties.getProperty(CAdESExtraParams.INCLUDE_MIMETYPE_ATTRIBUTE));
        if (z4 || z5) {
            String property4 = properties.getProperty("contentTypeOid");
            String property5 = properties.getProperty(CAdESExtraParams.CONTENT_DESCRIPTION);
            String property6 = properties.getProperty("mimeType");
            if (z5 && property6 == null && property4 != null) {
                try {
                    property6 = MimeHelper.transformOidToMimeType(property4);
                } catch (IOException e2) {
                    LOGGER.warning("No se pudo cargar la tabla de correspondencias entre OIDs y MimeTypes: " + e2);
                }
                if ("application/octet-stream".equals(property6)) {
                    property6 = null;
                }
            }
            if (((z4 && property4 == null) || (z5 && property6 == null)) && cMSSignedData != null) {
                boolean z6 = false;
                Iterator<SignerInformation> it = cMSSignedData.getSignerInfos().iterator();
                while (it.hasNext() && !z6) {
                    AttributeTable signedAttributes = it.next().getSignedAttributes();
                    if (z5 && property6 == null && (attribute2 = signedAttributes.get(new ASN1ObjectIdentifier(CAdESAttributes.OID_id_aa_ets_mimeType))) != null) {
                        property6 = attribute2.getAttributeValues()[0].toString();
                        z6 = true;
                    }
                    if (z4 && property4 == null && (attribute = signedAttributes.get(CMSAttributes.contentHint)) != null && attribute.getAttributeValues() != null && attribute.getAttributeValues().length >= 1) {
                        ContentHints contentHints = ContentHints.getInstance(attribute.getAttributeValues()[0]);
                        property4 = contentHints.getContentType().getId();
                        if (property5 == null && (contentDescription = contentHints.getContentDescription()) != null) {
                            property5 = contentDescription.getString();
                        }
                        z6 = true;
                    }
                    if (property6 != null && property4 == null) {
                        try {
                            property4 = MimeHelper.transformMimeTypeToOid(property6);
                            if (MimeHelper.DEFAULT_CONTENT_OID_DATA.equals(property4)) {
                                property4 = null;
                            }
                        } catch (Exception e3) {
                            LOGGER.warning("No se han podido las tablas de relacion entre MimeTypes y OIDs: " + e3);
                        }
                    } else if (z5 && property6 == null && property4 != null && !MimeHelper.DEFAULT_CONTENT_OID_DATA.equals(property4)) {
                        property6 = MimeHelper.transformOidToMimeType(property4);
                        if ("application/octet-stream".equals(property6)) {
                            property6 = null;
                        }
                    }
                }
            }
            if (((z4 && property4 == null) || (z5 && property6 == null)) && bArr != null) {
                try {
                    MimeHelper mimeHelper = new MimeHelper(bArr);
                    if (z4 && property4 == null) {
                        property4 = MimeHelper.transformMimeTypeToOid(mimeHelper.getMimeType());
                        if (MimeHelper.DEFAULT_CONTENT_OID_DATA.equals(property4)) {
                            property4 = null;
                        }
                    }
                    if (z4 && property5 == null) {
                        property5 = mimeHelper.getDescription();
                    }
                    if (z5 && property6 == null) {
                        property6 = mimeHelper.getMimeType();
                    }
                } catch (Exception e4) {
                    LOGGER.warning("No se han podido cargar las librerias para identificar el tipo de dato firmado: " + e4);
                }
            }
            if (z4) {
                cAdESParameters.setContentTypeOid(property4 != null ? property4 : MimeHelper.DEFAULT_CONTENT_OID_DATA);
                cAdESParameters.setContentDescription(property5 != null ? property5 : MimeHelper.DEFAULT_CONTENT_DESCRIPTION);
            }
            if (z5) {
                cAdESParameters.setMimeType(property6 != null ? property6 : "application/octet-stream");
            }
        }
        cAdESParameters.setMetadata(CAdESSignerMetadataHelper.getCAdESSignerMetadata(properties));
        if (AOSignConstants.SIGN_PROFILE_BASELINE.equals(properties.getProperty("profile"))) {
            cAdESParameters.setProfileSet(AOSignConstants.SIGN_PROFILE_BASELINE);
        } else {
            cAdESParameters.setProfileSet("advanced");
        }
        cAdESParameters.setExtraParams(properties);
        return cAdESParameters;
    }

    public AdESPolicy getExternalPolicy() {
        return this.externalPolicy;
    }

    public void setExternalPolicy(AdESPolicy adESPolicy) {
        this.externalPolicy = adESPolicy;
    }

    public boolean isSigningCertificateV2() {
        return this.signingCertificateV2;
    }

    public void setSigningCertificateV2(boolean z) {
        this.signingCertificateV2 = z;
    }

    public boolean isIncludedOnlySigningCertificate() {
        return this.includedOnlySigningCertificate;
    }

    public void setIncludedOnlySigningCertificate(boolean z) {
        this.includedOnlySigningCertificate = z;
    }

    public String getDigestAlgorithm() {
        return this.digestAlgorithm;
    }

    public void setDigestAlgorithm(String str) {
        this.digestAlgorithm = str;
    }

    public byte[] getDataDigest() {
        if (this.dataDigest != null) {
            return (byte[]) this.dataDigest.clone();
        }
        return null;
    }

    public void setDataDigest(byte[] bArr) {
        this.dataDigest = bArr != null ? (byte[]) bArr.clone() : null;
    }

    public void setContentNeeded(boolean z) {
        this.contentNeeded = z;
    }

    public boolean isContentNeeded() {
        return this.contentNeeded;
    }

    public byte[] getContentData() {
        return this.contentData;
    }

    public void setContentData(byte[] bArr) {
        this.contentData = bArr;
    }

    public Date getSigningTime() {
        return this.signingTime;
    }

    public void setSigningTime(Date date) {
        this.signingTime = date;
    }

    public String getContentTypeOid() {
        return this.contentTypeOid;
    }

    public void setContentTypeOid(String str) {
        this.contentTypeOid = str;
    }

    public String getContentDescription() {
        return this.contentDescription;
    }

    public void setContentDescription(String str) {
        this.contentDescription = str;
    }

    public String getMimeType() {
        return this.mimetype;
    }

    private void setMimeType(String str) {
        this.mimetype = str;
    }

    public List<CommitmentTypeIndicationBean> getCommitmentTypeIndications() {
        if (this.commitmentTypeIndications != null) {
            return new ArrayList(this.commitmentTypeIndications);
        }
        return null;
    }

    public void setCommitmentTypeIndications(List<CommitmentTypeIndicationBean> list) {
        this.commitmentTypeIndications = list != null ? new ArrayList(list) : null;
    }

    public String[] getClaimedRoles() {
        if (this.claimedRoles != null) {
            return (String[]) this.claimedRoles.clone();
        }
        return null;
    }

    public void setClaimedRoles(String[] strArr) {
        this.claimedRoles = strArr != null ? (String[]) strArr.clone() : null;
    }

    public CAdESSignerMetadata getMetadata() {
        return this.metadata;
    }

    public void setMetadata(CAdESSignerMetadata cAdESSignerMetadata) {
        this.metadata = cAdESSignerMetadata;
    }

    public boolean isIncludedPolicyOnSigningCertificate() {
        return this.includedPolicyOnSigningCertificate;
    }

    public void setIncludedPolicyOnSigningCertificate(boolean z) {
        this.includedPolicyOnSigningCertificate = z;
    }

    public boolean isIncludedIssuerSerial() {
        return this.includedIssuerSerial;
    }

    public void setIncludedIssuerSerial(boolean z) {
        this.includedIssuerSerial = z;
    }

    public Properties getExtraParams() {
        if (this.extraParams != null) {
            return (Properties) this.extraParams.clone();
        }
        return null;
    }

    public void setExtraParams(Properties properties) {
        this.extraParams = properties != null ? (Properties) properties.clone() : null;
    }

    public String getProfileSet() {
        return this.profileSet;
    }

    public void setProfileSet(String str) {
        this.profileSet = str;
    }
}
