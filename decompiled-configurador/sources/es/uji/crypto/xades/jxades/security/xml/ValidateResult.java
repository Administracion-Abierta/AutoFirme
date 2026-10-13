package es.uji.crypto.xades.jxades.security.xml;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/ValidateResult.class */
public enum ValidateResult {
    VALID("Valid"),
    INCOMPLETE("Incomplete validation"),
    INVALID("Invalid");

    private String resultName;

    ValidateResult(String resultName) {
        this.resultName = resultName;
    }

    public String getResultName() {
        return this.resultName;
    }

    @Override // java.lang.Enum
    public String toString() {
        return getResultName();
    }
}
