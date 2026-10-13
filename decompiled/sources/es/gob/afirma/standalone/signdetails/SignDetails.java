package es.gob.afirma.standalone.signdetails;

import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AdESPolicy;
import es.gob.afirma.signvalidation.SignValidity;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/signdetails/SignDetails.class */
public class SignDetails {
    private static final String POLICY_BUNDLE_NAME = "policy";
    public static final AdESPolicy POLICY_CADES_AGE_1_9;
    public static final AdESPolicy POLICY_CADES_AGE_1_8;
    public static final AdESPolicy POLICY_XADES_AGE_1_8;
    public static final AdESPolicy POLICY_XADES_AGE_1_9;
    public static final AdESPolicy POLICY_PADES_AGE_1_9;
    protected Date signingTime;
    protected String signProfile;
    protected String algorithm;
    protected SignaturePolicy policy;
    protected Boolean dataIncluded;
    protected String dataLocation;
    protected CertificateDetails signer;
    protected int certificationLevel;
    protected Boolean certificationSign;
    protected Boolean lastRevisionSign;
    protected Map<String, String> metadata = new HashMap();
    protected List<DataObjectFormat> dataObjectFormats = new ArrayList();
    protected List<SignValidity> validityResult = new ArrayList();

    static {
        ResourceBundle bundle = ResourceBundle.getBundle(POLICY_BUNDLE_NAME, Locale.getDefault());
        POLICY_XADES_AGE_1_9 = new AdESPolicy(bundle.getString("FirmaAGE19.policyIdentifier"), bundle.getString("FirmaAGE19.policyIdentifierHash.XAdES"), AOSignConstants.DIGEST_ALGORITHM_SHA1, bundle.getString("FirmaAGE19.policyQualifier"));
        POLICY_XADES_AGE_1_8 = new AdESPolicy(bundle.getString("FirmaAGE18.policyIdentifier"), bundle.getString("FirmaAGE18.policyIdentifierHash.XAdES"), AOSignConstants.DIGEST_ALGORITHM_SHA1, bundle.getString("FirmaAGE18.policyQualifier"));
        POLICY_PADES_AGE_1_9 = new AdESPolicy(bundle.getString("FirmaAGE19.policyIdentifier"), bundle.getString("FirmaAGE19.policyIdentifierHash.PAdES"), AOSignConstants.DIGEST_ALGORITHM_SHA1, bundle.getString("FirmaAGE19.policyQualifier"));
        POLICY_CADES_AGE_1_9 = new AdESPolicy(bundle.getString("FirmaAGE19.policyIdentifier"), bundle.getString("FirmaAGE19.policyIdentifierHash.CAdES"), AOSignConstants.DIGEST_ALGORITHM_SHA1, bundle.getString("FirmaAGE19.policyQualifier"));
        POLICY_CADES_AGE_1_8 = new AdESPolicy(bundle.getString("FirmaAGE18.policyIdentifier"), bundle.getString("FirmaAGE18.policyIdentifierHash.CAdES"), AOSignConstants.DIGEST_ALGORITHM_SHA1, bundle.getString("FirmaAGE18.policyQualifier"));
    }

    public String getSignProfile() {
        return this.signProfile;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public SignaturePolicy getPolicy() {
        return this.policy;
    }

    public Map<String, String> getMetadata() {
        return this.metadata;
    }

    public Boolean getDataIncluded() {
        return this.dataIncluded;
    }

    public String getDataLocation() {
        return this.dataLocation;
    }

    public List<SignValidity> getValidityResult() {
        return this.validityResult;
    }

    public List<DataObjectFormat> getDataObjectFormats() {
        return this.dataObjectFormats;
    }

    public void setSignProfile(String str) {
        this.signProfile = str;
    }

    public void setAlgorithm(String str) {
        this.algorithm = str;
    }

    public void setPolicy(SignaturePolicy signaturePolicy) {
        this.policy = signaturePolicy;
    }

    public void setMetadata(Map<String, String> map) {
        this.metadata = map;
    }

    public void setDataIncluded(Boolean bool) {
        this.dataIncluded = bool;
    }

    public void setDataLocation(String str) {
        this.dataLocation = str;
    }

    public void setValidityResult(List<SignValidity> list) {
        this.validityResult = list;
    }

    public void setDataObjectFormats(List<DataObjectFormat> list) {
        this.dataObjectFormats = list;
    }

    public CertificateDetails getSigner() {
        return this.signer;
    }

    public void setSigner(CertificateDetails certificateDetails) {
        this.signer = certificateDetails;
    }

    public Date getSigningTime() {
        return this.signingTime;
    }

    public void setSigningTime(Date date) {
        this.signingTime = date;
    }

    public int getCertificationLevel() {
        return this.certificationLevel;
    }

    public void setCertificationLevel(int i) {
        this.certificationLevel = i;
    }

    public Boolean getCertificationSign() {
        return this.certificationSign;
    }

    public void setCertificationSign(Boolean bool) {
        this.certificationSign = bool;
    }

    public Boolean getLastRevisionSign() {
        return this.lastRevisionSign;
    }

    public void setLastRevisionSign(Boolean bool) {
        this.lastRevisionSign = bool;
    }
}
