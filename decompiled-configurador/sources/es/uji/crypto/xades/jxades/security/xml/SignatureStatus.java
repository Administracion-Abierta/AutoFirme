package es.uji.crypto.xades.jxades.security.xml;

import es.uji.crypto.xades.jxades.util.ComparableBean;
import java.util.ArrayList;
import java.util.List;
import javax.xml.crypto.MarshalException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/SignatureStatus.class */
public class SignatureStatus implements ComparableBean {
    private String signatureId;
    private ValidateResult validateResult;
    private ArrayList<InvalidSignatureReason> invalidSignatureReasons;

    public SignatureStatus() {
        this.invalidSignatureReasons = new ArrayList<>();
    }

    public SignatureStatus(String signatureId, MarshalException ex) {
        this(signatureId, ValidateResult.INVALID, new InvalidSignatureReason(ex));
    }

    public SignatureStatus(String signatureId, NullPointerException ex) {
        this(signatureId, ValidateResult.INVALID, new InvalidSignatureReason("XML", ex));
    }

    public SignatureStatus(String signatureId, ClassCastException ex) {
        this(signatureId, ValidateResult.INVALID, new InvalidSignatureReason(InvalidSignature.INAPPROPRIATE_XML_CONTEXT, ex));
    }

    public SignatureStatus(String signatureId, ValidateResult validateResult, InvalidSignatureReason reason) {
        this(signatureId, validateResult);
        addInvalidSignatureReason(reason);
    }

    public SignatureStatus(String signatureId, ValidateResult validateResult) {
        this.invalidSignatureReasons = new ArrayList<>();
        this.signatureId = signatureId;
        this.validateResult = validateResult;
    }

    public ValidateResult getValidateResult() {
        return this.validateResult;
    }

    public String getSignatureId() {
        return this.signatureId;
    }

    public void addInvalidSignatureReason(InvalidSignatureReason reason) {
        this.invalidSignatureReasons.add(reason);
    }

    public List<InvalidSignatureReason> getInvalidSignatureReasons() {
        return this.invalidSignatureReasons;
    }

    public String getReasonsAsText() {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        List<InvalidSignatureReason> reasons = getInvalidSignatureReasons();
        for (InvalidSignatureReason reason : reasons) {
            if (isFirst) {
                isFirst = false;
                sb.append(reason.getReason());
            } else {
                sb.append(", ").append(reason.getReason());
            }
        }
        return sb.toString();
    }

    public String toString() {
        return this.validateResult.toString();
    }

    public static boolean isValid(List<SignatureStatus> validateResults) {
        for (SignatureStatus signStatus : validateResults) {
            if (!ValidateResult.VALID.equals(signStatus.getValidateResult())) {
                return false;
            }
        }
        return true;
    }

    @Override // es.uji.crypto.xades.jxades.util.ComparableBean
    public Comparable getIndexKey() {
        return getSignatureId();
    }
}
